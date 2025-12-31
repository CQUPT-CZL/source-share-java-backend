package com.example.source_share;

import com.example.source_share.model.User;
import com.example.source_share.repository.UserRepository;
import com.example.source_share.service.LoginService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SourceShareApplicationTests {

	@Autowired
	UserRepository userRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	com.example.source_share.common.JwtUtils jwtUtils;

	@Test
	void testJwtGeneration() {
		System.out.println("开始测试 JWT 生成...");
		
		Long userId = 1001L;
		String username = "test_user";
		
		// 1. 生成 Token
		String token = jwtUtils.generateToken(userId, username);
		System.out.println("生成的 Token: " + token);
		assert token != null && !token.isEmpty();

		// 2. 验证 Token 有效性
		boolean isValid = jwtUtils.validateToken(token);
		System.out.println("Token 有效性: " + isValid);
		assert isValid;

		// 3. 解析 Token 内容
		String extractedUsername = jwtUtils.getUsernameFromToken(token);
		Long extractedUserId = jwtUtils.getUserIdFromToken(token);
		
		System.out.println("解析出的用户名: " + extractedUsername);
		System.out.println("解析出的用户ID: " + extractedUserId);
		
		assert username.equals(extractedUsername);
		assert userId.equals(extractedUserId);
	}

	@Test
	void testLoginLogic() {
		System.out.println("开始测试登录逻辑...");

		// 确保数据库中有测试用户
		userRepository.deleteAll();
		User user = new User("csr", "陈丝冉", "2023", "login@example.com", "123", "USER");
		userRepository.save(user);

		User user2 = new User("czl", "崔子梁", "2023", "login@example.com", "123", "ADMIN");
		userRepository.save(user2);

		// 场景1: 使用 Username 登录
		boolean loginByUsername = loginService.verify("csr", "123");
		System.out.println("Username 登录结果: " + loginByUsername);
		assert loginByUsername;

		// 场景2: 使用 Email 登录
		boolean loginByEmail = loginService.verify("login@example.com", "123");
		System.out.println("Email 登录结果: " + loginByEmail);
		assert loginByEmail;

		// 场景3: 密码错误
		boolean loginFail = loginService.verify("csr", "wrong_pass");
		System.out.println("错误密码登录结果: " + loginFail);
		assert !loginFail;

		// 场景4: 用户不存在
		boolean userNotExist = loginService.verify("ghost", "123");
		System.out.println("不存在用户登录结果: " + userNotExist);
		assert !userNotExist;
	}

}
