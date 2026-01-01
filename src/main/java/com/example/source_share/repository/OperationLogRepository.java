package com.example.source_share.repository;

import com.example.source_share.model.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    // 按时间倒序查询所有日志
    Page<OperationLog> findAllByOrderByOperationTimeDesc(Pageable pageable);
    
    // 按用户查询日志
    Page<OperationLog> findByUserIdOrderByOperationTimeDesc(Long userId, Pageable pageable);
}
