package com.brooks.mall.user.entity;

/**
 *  TODO
 *  @Author Brooks Cole
 *  @Date 2026/7/22 10:29
 */
public class User {
    private Integer id;
    private String username;
    private Integer age;

    // Getter 和 Setter 方法 (省略，请自行生成或使用 Lombok @Data)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}