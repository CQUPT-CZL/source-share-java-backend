package com.example.source_share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceStatisticsDTO {
    // 各种文件类型的数量 (key: extension, value: count)
    private Map<String, Long> fileTypeCounts;
    
    // 总文件大小 (字节)
    private Long totalSize;
    
    // 总文件数量
    private Long totalCount;
}
