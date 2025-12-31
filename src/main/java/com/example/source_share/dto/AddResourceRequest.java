package com.example.source_share.dto;

import com.example.source_share.model.CategoryCode;
import com.example.source_share.model.NodeType;
import lombok.Data;

import java.util.Map;

@Data
public class AddResourceRequest {
    /**
     * 父文件夹 ID (必填，用户只能在现有目录下创建资源)
     */
    private Long parentId;

    /**
     * 资源名称 (文件名或文件夹名)
     */
    private String nodeName;

    /**
     * 资源类型: DIRECTORY 或 FILE
     */
    private NodeType resourceType;

    /**
     * 分类代码。只有根目录(parentId为null)时必填。
     */
    private CategoryCode categoryCode;

    /**
     * 扩展属性 (文件大小, 下载链接等)
     */
    private Map<String, Object> properties;
}
