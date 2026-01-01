package com.example.source_share.repository;

import com.example.source_share.model.ResourceNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface StatisticsRepository extends JpaRepository<ResourceNode, Long> {

    /**
     * 统计各种文件扩展名的数量
     * 利用 PostgreSQL 的 JSONB 查询能力
     * properties ->> 'extension' 提取后缀名
     */
    @Query(value = """
        SELECT 
            properties ->> 'extension' as extension, 
            COUNT(*) as count
        FROM resource_nodes 
        WHERE resource_type = 'FILE'
        GROUP BY properties ->> 'extension'
    """, nativeQuery = true)
    List<Map<String, Object>> countFileTypes();

    /**
     * 统计所有文件的大小总和
     * properties ->> 'size' 提取大小
     */
    @Query(value = """
        SELECT SUM(CAST(properties ->> 'size' AS BIGINT))
        FROM resource_nodes 
        WHERE resource_type = 'FILE'
    """, nativeQuery = true)
    Long sumTotalSize();
    
    /**
     * 统计文件总数
     */
    @Query(value = "SELECT COUNT(*) FROM resource_nodes WHERE resource_type = 'FILE'", nativeQuery = true)
    Long countTotalFiles();
}
