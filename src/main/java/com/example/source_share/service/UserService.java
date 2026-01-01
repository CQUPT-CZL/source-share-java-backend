package com.example.source_share.service;

import com.example.source_share.model.User;
import com.example.source_share.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建成功的用户
     * @throws IllegalArgumentException 如果用户名或邮箱已存在
     */
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 只有当邮箱不为空时，才检查邮箱是否存在
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new IllegalArgumentException("邮箱已存在");
            }
        }
        
        // 确保新用户有默认角色，如果未指定
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }

        return userRepository.save(user);
    }

    /**
     * 检查用户是否为管理员
     * @param userId 用户ID
     * @return true 如果是管理员
     */
    public boolean isAdmin(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return "admin".equalsIgnoreCase(user.getRole());
        }
        return false;
    }
}
