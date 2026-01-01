package com.example.source_share.controller;

import com.example.source_share.common.Result;
import com.example.source_share.dto.UserRegisterRequest;
import com.example.source_share.model.User;
import com.example.source_share.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Result<User> register(@RequestBody UserRegisterRequest registerRequest, HttpServletRequest request) {
        // 1. 获取当前登录用户的 ID (由 AuthInterceptor 设置)
        Long currentUserId = (Long) request.getAttribute("userId");
        if (currentUserId == null) {
            return Result.error(401, "未登录");
        }

        // 2. 检查权限：只有管理员可以注册新用户
        if (!userService.isAdmin(currentUserId)) {
            return Result.error(403, "权限不足：只有管理员可以创建新用户");
        }

        // 3. 将 DTO 转换为 Entity
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setRealName(registerRequest.getRealName());
        user.setEmail(registerRequest.getEmail());
        user.setGrade(registerRequest.getGrade());
        user.setRole(registerRequest.getRole());

        // 4. 执行注册
        try {
            User createdUser = userService.createUser(user);
            // 隐藏密码返回
            createdUser.setPassword(null);
            return Result.success("用户创建成功", createdUser);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
}
