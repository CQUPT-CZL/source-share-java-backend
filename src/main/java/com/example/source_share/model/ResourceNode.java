package com.example.source_share.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLLTreeType;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resource_nodes") // 数据库表名
public class ResourceNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -----------------------------------------------------------
    // 1. 核心结构：树状路径
    // -----------------------------------------------------------
    // 存 "1.10.55" 这种 ID 路径。
    // 注意：这里必须指定 columnDefinition = "ltree"，这需要你数据库开启了 ltree 插件
    @Type(io.hypersistence.utils.hibernate.type.basic.PostgreSQLLTreeType.class)
    @Column(columnDefinition = "ltree", nullable = false)
    private String treePath;

    private String nodeName; // 显示的名字，如 "计算机网络" 或 "实验1.docx"

    // -----------------------------------------------------------
    // 2. 核心分类
    // -----------------------------------------------------------
    
    @Enumerated(EnumType.STRING)
    private NodeType resourceType; // 是 DIRECTORY 还是 FILE

    @Enumerated(EnumType.STRING)
    private CategoryCode categoryCode; // 只有根文件夹有值，标记它是哪个板块

    // -----------------------------------------------------------
    // 3. 关联你的 User 表 (关键点)
    // -----------------------------------------------------------
    
    // 这里存的是 User 表的 id。
    // 我们不直接用 @ManyToOne 关联 User 对象，是为了性能（不用每次查文件都连表查用户）
    private Long ownerId; 

    private String ownerName; // 冗余存一个真实姓名 (User.realName)，前端显示方便

    // -----------------------------------------------------------
    // 4. 扩展属性 (JSONB)
    // -----------------------------------------------------------
    // 存文件大小、后缀、下载地址、视频时长等乱七八糟的信息
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> properties = new HashMap<>();

    // -----------------------------------------------------------
    // 5. 审计时间
    // -----------------------------------------------------------
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;



    // 为了方便自动填时间
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


}