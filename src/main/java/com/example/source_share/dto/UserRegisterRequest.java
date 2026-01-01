package com.example.source_share.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    /**
     * 用户名 (拼音简写)
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年级 (例如: 2024)
     */
    private String grade;

    /**
     * 角色 (可选，默认为 user，管理员可指定 admin)
     */
    private String role;
}
