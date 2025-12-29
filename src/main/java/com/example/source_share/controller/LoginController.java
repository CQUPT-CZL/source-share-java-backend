package com.example.source_share.controller; 

import com.example.source_share.common.JwtUtils;
import com.example.source_share.common.Result;
import com.example.source_share.model.User;
import com.example.source_share.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api") 
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User loginRequest) {
        // 1. 调用 Service 进行登录
        User user = loginService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        // 2. 判断登录结果
        if (user != null) {
            // 3. 生成真实的 JWT Token
            String token = jwtUtils.generateToken(user.getId(), user.getUsername());

            // 4. 封装返回数据 (包含 Token 和 用户基本信息)
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("email", user.getEmail());
            data.put("role", user.getRole());
            data.put("grade", user.getGrade());

            return Result.success("登录成功", data);
        } else {
            return Result.error(401, "用户名或密码错误");
        }
    }
}