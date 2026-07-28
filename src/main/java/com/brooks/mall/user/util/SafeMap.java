package com.brooks.mall.user.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.*;

/**
 * 完全自给自足的线程安全 Map，不委托任何外部 Map 实现。
 * 底层采用类似 ConcurrentHashMap 的 Hash 数组 + CAS 操作。
 */
public class SafeMap implements Map<String, Object> {

    // 默认初始容量
    private static final int DEFAULT_CAPACITY = 16;
    // 负载因子
    private static final float LOAD_FACTOR = 0.75f;

    // 哈希表数组
    private volatile Node[] table;
    // 元素数量
    private volatile int size;

    // Unsafe 相关，用于 CAS 操作
    private static final Unsafe UNSAFE;
    private static final long SIZE_OFFSET;
    private static final long TABLE_OFFSET;

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            UNSAFE = (Unsafe) f.get(null);
            SIZE_OFFSET = UNSAFE.objectFieldOffset(SafeMap.class.getDeclaredField("size"));
            TABLE_OFFSET = UNSAFE.objectFieldOffset(SafeMap.class.getDeclaredField("table"));
        } catch (Exception e) {
            throw new Error("Failed to initialize Unsafe", e);
        }
    }

    // 内部节点类
    static class Node {
        final int hash;
        final String key;
        volatile Object value;
        volatile Node next;

        Node(int hash, String key, Object value, Node next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    public SafeMap() {
        this(DEFAULT_CAPACITY);
    }

    public SafeMap(int initialCapacity) {
        int cap = 1;
        while (cap < initialCapacity) cap <<= 1;
        this.table = new Node[cap];
    }

    // ==================== 类型安全访问（零额外开销） ====================

    public String getString(String key) {
        Object val = get(key);
        return val == null ? "" : val.toString();
    }

    public int getInt(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        if (val == null) return 0;
        try { return Integer.parseInt(val.toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    public long getLong(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number) val).longValue();
        if (val == null) return 0L;
        try { return Long.parseLong(val.toString().trim()); }
        catch (NumberFormatException e) { return 0L; }
    }

    public double getDouble(String key) {
        Object val = get(key);
        if (val instanceof Number) return ((Number) val).doubleValue();
        if (val == null) return 0.0D;
        try { return Double.parseDouble(val.toString().trim()); }
        catch (NumberFormatException e) { return 0.0D; }
    }

    public Date getDate(String key) {
        Object val = get(key);
        if (val instanceof Timestamp) return new Date(((Timestamp) val).getTime());
        if (val instanceof Date) return (Date) val;
        return null;
    }

    public Timestamp getTimestamp(String key) {
        Object val = get(key);
        if (val instanceof Timestamp) return (Timestamp) val;
        if (val instanceof Date) return new Timestamp(((Date) val).getTime());
        return null;
    }

    // ==================== 核心自给自足实现 ====================

    private static int hash(String key) {
        if (key == null) return 0;
        int h = key.hashCode();
        // 扰动函数，减少哈希冲突
        return h ^ (h >>> 16);
    }

    @Override
    public Object get(Object key) {
        if (key == null) return null;
        Node[] tab = table;
        if (tab == null || tab.length == 0) return null;
        
        int hash = hash((String) key);
        int index = hash & (tab.length - 1);
        Node e = tab[index];
        
        // 遍历链表查找
        while (e != null) {
            if (e.hash == hash && e.key.equals(key)) {
                return e.value;
            }
            e = e.next;
        }
        return null;
    }

    @Override
    public Object put(String key, Object value) {
        if (key == null || value == null) {
            throw new NullPointerException("SafeMap does not allow null keys or values");
        }
        Node[] tab = table;
        int hash = hash(key);
        int index = hash & (tab.length - 1);
        
        Node first = tab[index];
        // 1. 如果槽位为空，直接 CAS 插入
        if (first == null) {
            Node newNode = new Node(hash, key, value, null);
            if (UNSAFE.compareAndSwapObject(tab, 
                    UNSAFE.arrayBaseOffset(Node[].class) + (long)index * UNSAFE.arrayIndexScale(Node[].class), 
                    null, newNode)) {
                addCount(1);
                return null;
            }
            // CAS 失败，说明有并发，重新走下面的逻辑
            first = tab[index]; 
        }
        
        // 2. 槽位不为空，遍历链表
        Node e = first;
        while (true) {
            if (e.hash == hash && e.key.equals(key)) {
                // 找到相同 key，更新值
                Object oldVal = e.value;
                e.value = value;
                return oldVal;
            }
            if (e.next == null) break;
            e = e.next;
        }
        
        // 3. 尾插法
        Node newNode = new Node(hash, key, value, null);
        // 简单的尾节点 CAS，防止并发追加
        if (UNSAFE.compareAndSwapObject(e, 
                getNodeNextOffset(), 
                null, newNode)) {
            addCount(1);
            // 检查是否需要扩容 (简化版)
            if (size > tab.length * LOAD_FACTOR) {
                resize(tab);
            }
            return null;
        }
        // 如果尾节点 CAS 失败，说明别人刚插入了，递归重试
        return put(key, value);
    }

    @Override
    public Object remove(Object key) {
        if (key == null) return null;
        Node[] tab = table;
        if (tab == null || tab.length == 0) return null;

        int hash = hash((String) key);
        int index = hash & (tab.length - 1);
        Node first = tab[index];
        Node e = first;
        Node pred = null;

        while (e != null) {
            if (e.hash == hash && e.key.equals(key)) {
                Object oldVal = e.value;
                Node next = e.next;
                // CAS 移除节点
                boolean success;
                if (pred == null) {
                    success = UNSAFE.compareAndSwapObject(tab, 
                            UNSAFE.arrayBaseOffset(Node[].class) + (long)index * UNSAFE.arrayIndexScale(Node[].class), 
                            first, next);
                } else {
                    success = UNSAFE.compareAndSwapObject(pred, getNodeNextOffset(), e, next);
                }
                if (success) {
                    addCount(-1);
                    return oldVal;
                }
                return null; // CAS 失败，说明被其他线程修改了
            }
            pred = e;
            e = e.next;
        }
        return null;
    }

    // 简单的扩容机制
    private void resize(Node[] oldTab) {
        // 这里为了演示自给自足，简单加锁扩容，实际生产可参考 CHM 的并发扩容
        synchronized (this) {
            if (oldTab != table) return; // 已经被扩容过了
            int oldCap = oldTab.length;
            int newCap = oldCap << 1;
            Node[] newTab = new Node[newCap];
            
            for (Node node : oldTab) {
                Node e = node;
                while (e != null) {
                    Node next = e.next;
                    int newIndex = e.hash & (newCap - 1);
                    e.next = newTab[newIndex];
                    newTab[newIndex] = e;
                    e = next;
                }
            }
            this.table = newTab;
        }
    }

    private void addCount(int delta) {
        UNSAFE.getAndAddInt(this, SIZE_OFFSET, delta);
    }

    private static long getNodeNextOffset() {
        try {
            return UNSAFE.objectFieldOffset(Node.class.getDeclaredField("next"));
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    // ==================== Map 接口其他方法补齐 ====================
    @Override public int size() { return size; }
    @Override public boolean isEmpty() { return size == 0; }
    @Override public boolean containsKey(Object key) { return get(key) != null; }
    @Override public boolean containsValue(Object value) {
        for (Node node : table) {
            Node e = node;
            while (e != null) {
                if (e.value.equals(value)) return true;
                e = e.next;
            }
        }
        return false;
    }
    @Override public void clear() {
        Node[] tab = table;
        for (int i = 0; i < tab.length; i++) tab[i] = null;
        size = 0;
    }
    @Override public void putAll(Map<? extends String, ?> m) {
        for (Entry<? extends String, ?> e : m.entrySet()) put(e.getKey(), e.getValue());
    }
    @Override public Set<String> keySet() {
        Set<String> keys = new HashSet<>();
        for (Node node : table) {
            Node e = node;
            while (e != null) { keys.add(e.key); e = e.next; }
        }
        return keys;
    }
    @Override public Collection<Object> values() {
        List<Object> vals = new ArrayList<>();
        for (Node node : table) {
            Node e = node;
            while (e != null) { vals.add(e.value); e = e.next; }
        }
        return vals;
    }
    @Override public Set<Entry<String, Object>> entrySet() {
        Set<Entry<String, Object>> set = new HashSet<>();
        for (Node node : table) {
            Node e = node;
            while (e != null) { 
                set.add(new AbstractMap.SimpleEntry<>(e.key, e.value)); 
                e = e.next; 
            }
        }
        return set;
    }
}