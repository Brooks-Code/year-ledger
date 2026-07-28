package com.brooks.mall.user.mapper;

import com.brooks.mall.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * TODO
 * @Author Brooks Cole
 * @Date 2026/7/22 10:29
 */
@Mapper
public interface UserMapper {

    @Select("select * from users")
    List<User> findAll();
}