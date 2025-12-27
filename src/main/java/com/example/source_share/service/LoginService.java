package com.example.source_share.service;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    /**
     * 验证登录逻辑
     */
    public boolean verify(String username, String password) {
        // 模拟数据库验证
        return "admin".equals(username) && "123456".equals(password);
    }
}