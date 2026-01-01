package com.example.source_share;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
public class DatabaseCleanupTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void cleanDatabase() {
        System.out.println(">>> 开始执行数据库清理...");
        
        // 执行清理 SQL
        jdbcTemplate.execute("TRUNCATE TABLE operation_logs RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE resource_nodes RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
        
        System.out.println(">>> 数据库清理完成！所有表已清空。");
    }
}
