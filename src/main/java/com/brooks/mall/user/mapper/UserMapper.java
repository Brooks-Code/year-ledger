package com.brooks.mall.user.mapper;

import com.brooks.mall.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TODO
 *
 * @Author Brooks Cole
 * @Date 2026/7/22 10:29
 */
@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询未删除的用户
     * 注意：SQL中显式过滤 is_deleted，这是安全底线
     */
    @Select("SELECT * FROM orguser WHERE userid = #{userName} AND is_deleted = 0 LIMIT 1")
    User selectByUsername(@Param("userName") String userName);

    @Select("select * from orguser")
    List<User> findAll();

    /**
     * 新增用户
     */
    void insertUser(User user);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM orguser WHERE email = #{email}")
    Object selectByEmail(String email);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT * FROM orguser WHERE mobile = #{mobile}")
    Object selectByMobile(String mobile);
}