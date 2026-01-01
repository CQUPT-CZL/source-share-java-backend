package com.example.source_share.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_logs")
@Data
@NoArgsConstructor
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 操作人信息
    private Long userId;
    private String username;
    private String realName; // 冗余存一下真实姓名，方便显示

    // 操作详情
    private String operationType; // "UPLOAD" 或 "DELETE"
    private String resourceName;  // 被操作的资源名称
    private Long resourceId;      // 被操作的资源ID (注意：如果是删除操作，这个ID对应的记录可能在 resource_nodes 表里没了)
    private String details;       // 额外详情，比如文件路径等

    // 操作时间
    private LocalDateTime operationTime;

    @PrePersist
    protected void onCreate() {
        operationTime = LocalDateTime.now();
    }

    public OperationLog(Long userId, String username, String realName, String operationType, String resourceName, Long resourceId, String details) {
        this.userId = userId;
        this.username = username;
        this.realName = realName;
        this.operationType = operationType;
        this.resourceName = resourceName;
        this.resourceId = resourceId;
        this.details = details;
    }
}
