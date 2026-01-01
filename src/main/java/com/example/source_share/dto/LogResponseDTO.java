package com.example.source_share.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogResponseDTO {
    private String realName;      // 操作人真实姓名
    private String operationType; // 操作类型 (UPLOAD/DELETE)
    private String resourceName;  // 资源名称
    private LocalDateTime operationTime; // 操作时间

    public LogResponseDTO(String realName, String operationType, String resourceName, LocalDateTime operationTime) {
        this.realName = realName;
        this.operationType = operationType;
        this.resourceName = resourceName;
        this.operationTime = operationTime;
    }
}
