package com.example.source_share.controller; // 注意包名增加了 .controller

// 必须 import 另外两个包里的类，否则找不到 User 和 LoginService
import com.example.source_share.model.User;
import com.example.source_share.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") // 建议给接口加个前缀，方便管理
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        boolean success = loginService.verify(user.getUsername(), user.getPassword());
        
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("status", "success");
            result.put("message", "登录成功");
        } else {
            result.put("status", "fail");
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}