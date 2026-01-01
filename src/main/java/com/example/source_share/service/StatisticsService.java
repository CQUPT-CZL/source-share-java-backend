package com.example.source_share.service;

import com.example.source_share.dto.ResourceStatisticsDTO;
import com.example.source_share.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    public ResourceStatisticsDTO getStatistics() {
        // 1. 获取各类型数量
        List<Map<String, Object>> typeCounts = statisticsRepository.countFileTypes();
        Map<String, Long> formattedTypeCounts = new HashMap<>();
        
        for (Map<String, Object> entry : typeCounts) {
            String extension = (String) entry.get("extension");
            Long count = ((Number) entry.get("count")).longValue();
            
            if (extension == null) {
                extension = "unknown";
            }
            formattedTypeCounts.put(extension, count);
        }

        // 2. 获取总大小
        Long totalSize = statisticsRepository.sumTotalSize();
        if (totalSize == null) {
            totalSize = 0L;
        }

        // 3. 获取总数量
        Long totalCount = statisticsRepository.countTotalFiles();

        return new ResourceStatisticsDTO(formattedTypeCounts, totalSize, totalCount);
    }
}
