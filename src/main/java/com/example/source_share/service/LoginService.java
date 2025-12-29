package com.example.source_share.service;

import com.example.source_share.model.User;
import com.example.source_share.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 验证登录逻辑
     * @param account  用户名或邮箱
     * @param password 密码
     * @return 登录成功的用户对象，失败返回 null
     */
    public User login(String account, String password) {
        if (account == null || password == null) {
            return null;
        }

        // 根据用户名或邮箱查找用户
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(account, account);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 简单的明文密码比对（实际项目中建议使用加密密码）
            if (password.equals(user.getPassword())) {
                return user;
            }
        }

        return null;
    }
    
    /**
     * 保留旧方法以兼容测试代码（或者后续修改测试代码）
     */
    public boolean verify(String account, String password) {
        return login(account, password) != null;
    }
}