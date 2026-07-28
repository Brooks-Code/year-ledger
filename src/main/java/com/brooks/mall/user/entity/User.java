package com.brooks.mall.user.entity;

/**
 *  TODO
 *  @Author Brooks Cole
 *  @Date 2026/7/22 10:29
 */
public class User {
    private Long id;
    private String username;
    private String userid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}