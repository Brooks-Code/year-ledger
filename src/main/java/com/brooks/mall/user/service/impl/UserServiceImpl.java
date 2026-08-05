package com.brooks.mall.user.service.impl;

import com.brooks.mall.user.dto.LoginRequest;
import com.brooks.mall.user.dto.LoginResponse;
import com.brooks.mall.user.dto.UserRegisterRequest;
import com.brooks.mall.user.entity.User;
import com.brooks.mall.user.exception.BusinessException;
import com.brooks.mall.user.mapper.UserMapper;
import com.brooks.mall.user.service.UserService;
import com.brooks.mall.user.util.JwtUtil;
import com.brooks.mall.user.util.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @Author Brooks Cole
 * @Date 2026/7/28 16:45
 */
@Service
@Slf4j
@RequiredArgsConstructor // 自动注入构造函数
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void register(UserRegisterRequest request) {
        // 1. 二次校验密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 2. 检查唯一性 (防止并发问题或绕过前端校验)
        // 注意：这里简单演示，实际生产环境建议在数据库层面利用 UNIQUE KEY 报错来兜底
        if (userMapper.selectByUsername(request.getUserName()) != null) {
            throw new BusinessException("用户ID已存在");
        }

        // 如果邮箱/手机号也是必填且唯一的，也需要在这里检查
         if (StringUtils.hasText(request.getEmail()) && userMapper.selectByEmail(request.getEmail()) != null){
             throw new BusinessException("邮箱已存在");
         }
         if (StringUtils.hasText(request.getMobile()) && userMapper.selectByMobile(request.getMobile()) != null){
             throw new BusinessException("手机号已存在");
         }

        // 3. 构建实体对象
        User user = new User();
        long id = new SnowflakeIdGenerator(1, 1).nextId();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        user.setCreatedBy(request.getUserName());
        user.setUpdatedAt(LocalDateTime.now());
        user.setUpdatedBy(request.getUserName());
        user.setUserid(request.getUserName());
        user.setUsername(request.getRealName());
        user.setEmail(request.getEmail());
        user.setMobile(request.getMobile());
        user.setRealName(request.getRealName());
        user.setIdCard(request.getIdCard());
        user.setAvatar(request.getAvatar());
        user.setAvatar("http://11");

        // 【核心】BCrypt 加密
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 设置默认状态：1-正常
        user.setStatus(1);

        // 设置默认删除标记：0-未删除
        user.setIsDeleted(0);

        // 4. 执行插入
        try {
            userMapper.insertUser(user);
            log.info("用户注册成功: {}", request.getRealName());
        } catch (Exception e) {
            // 捕获数据库唯一索引冲突异常（如 userid 重复）
            log.error("注册失败", e);
            throw new BusinessException("注册失败，该账号可能已被注册");
        }
    }

    /**
     * 登录
     *
     * @param request 登录请求参数
     * @return 登录响应数据
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 查询用户
        User user = userMapper.selectByUsername(request.getUserName());
        if (user == null) {
            // 安全最佳实践：不提示"用户不存在"，统一提示
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 校验账号状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            String msg;
            switch (user.getStatus()) {
                case 0:
                    msg = "账号已被禁用，请联系管理员";
                    break;
                case 2:
                    msg = "账号尚未激活，请检查邮箱完成激活";
                    break;
                default:
                    msg = "账号状态异常";
                    break;
            }
            throw new BusinessException(msg);
        }

        // 3. 校验密码（使用BCrypt等单向加密比对）
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 4. 生成JWT Token（建议将userid和status放入claims）
        Map<String, Object> claims = new HashMap<>(4); // 指定初始容量避免扩容
        claims.put("userid", user.getUserid());
        claims.put("username", user.getUsername());
        claims.put("status", user.getStatus());
        String token = jwtUtil.generateToken(user.getId(), claims);

        // 5. 可选：更新最后登录时间/记录登录日志
        // userMapper.updateLastLogin(user.getId(), LocalDateTime.now());

        log.info("用户登录成功: userid={}", user.getUserid());
        return new LoginResponse(token, user.getUserid(), user.getUsername(), user.getStatus());
    }

    /**
     * 获取用户列表
     */
    @Override
    public List<User> getUsers() {
        // 调用 Mapper 里的查询方法
       return userMapper.findAll();
    }
}