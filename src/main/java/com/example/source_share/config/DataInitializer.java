package com.example.source_share.config;

import com.example.source_share.model.CategoryCode;
import com.example.source_share.model.NodeType;
import com.example.source_share.model.ResourceNode;
import com.example.source_share.model.User;
import com.example.source_share.repository.UserRepository;
import com.example.source_share.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("🚀 正在检查初始数据...");

        // 初始化默认管理员
        initAdminUser();

        // 定义你需要初始化的 5 大板块
        List<InitItem> items = Arrays.asList(
            new InitItem(CategoryCode.COURSEWORK, "课程作业"),
            new InitItem(CategoryCode.PROPOSAL, "开题报告"),
            new InitItem(CategoryCode.MIDTERM, "中期考核"),
            new InitItem(CategoryCode.THESIS, "毕业设计"),
            new InitItem(CategoryCode.OTHERS, "综合资源") // 对应你前端的“留言板”
        );

        for (InitItem item : items) {
            initRootNode(item.code, item.name);
        }
    }

    private void initAdminUser() {
        String adminUsername = "admin";
        if (userRepository.existsByUsername(adminUsername)) {
            return;
        }

        System.out.println("👤 正在初始化默认管理员: admin");
        User admin = new User();
        admin.setUsername(adminUsername);
        admin.setPassword("123"); // 默认密码
        admin.setRole("admin");
        admin.setRealName("管理员");
        admin.setEmail("admin@example.com");
        admin.setGrade("2024");
        
        userRepository.save(admin);
    }

    private void initRootNode(CategoryCode code, String name) {
        // 1. 检查是否存在
        if (resourceRepository.existsByCategoryCode(code)) {
            return; // 存在这就跳过
        }

        System.out.println("🔧 正在初始化根目录: " + name);

        // 2. 创建对象
        ResourceNode node = ResourceNode.builder()
                .nodeName(name)
                .categoryCode(code) // 关键暗号
                .resourceType(NodeType.DIRECTORY)
                
                // ⚠️ 注意：这里需要一个默认的 ownerId
                // 建议：如果你有 ID=1 的管理员用户，就填 1；或者填 0 代表系统创建
                .ownerId(1L)
                .ownerName("System Admin")
                
                .treePath("temp") // 先填个临时的，后面马上改
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 3. 第一次保存：为了获取 ID
        node = resourceRepository.save(node);

        // 4. 更新路径：根节点的路径就是它自己的 ID
        node.setTreePath(String.valueOf(node.getId()));
        
        // 5. 第二次保存：更新正确的路径
        resourceRepository.save(node);
    }

    // 简单的内部类，用来存配置
    static class InitItem {
        CategoryCode code;
        String name;
        public InitItem(CategoryCode code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
