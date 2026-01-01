package com.example.source_share.service;

import com.example.source_share.dto.LogResponseDTO;
import com.example.source_share.model.OperationLog;
import com.example.source_share.model.User;
import com.example.source_share.repository.OperationLogRepository;
import com.example.source_share.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private OperationLogRepository logRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 记录操作日志
     * 建议使用 @Async 异步记录，避免阻塞主业务流程
     */
    @Async
    public void log(Long userId, String operationType, String resourceName, Long resourceId, String details) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            String username = (user != null) ? user.getUsername() : "Unknown";
            String realName = (user != null) ? user.getRealName() : "Unknown";

            OperationLog log = new OperationLog(userId, username, realName, operationType, resourceName, resourceId, details);
            logRepository.save(log);
        } catch (Exception e) {
            // 日志记录失败不应影响主业务，打印错误即可
            System.err.println("记录日志失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 分页查询日志 (仅管理员)
     */
    public Page<LogResponseDTO> getLogs(int page, int size) {
        Page<OperationLog> logs = logRepository.findAllByOrderByOperationTimeDesc(PageRequest.of(page, size));
        
        // 转换为 DTO
        return logs.map(log -> new LogResponseDTO(
            log.getRealName(),
            log.getOperationType(),
            log.getResourceName(),
            log.getOperationTime()
        ));
    }
}
