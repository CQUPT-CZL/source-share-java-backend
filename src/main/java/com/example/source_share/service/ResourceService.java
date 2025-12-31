package com.example.source_share.service;

import com.example.source_share.dto.AddResourceRequest;
import com.example.source_share.model.CategoryCode;
import com.example.source_share.model.NodeType;
import com.example.source_share.model.ResourceNode;
import com.example.source_share.model.User;
import com.example.source_share.repository.ResourceRepository;
import com.example.source_share.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 根据分类代码获取根节点ID
     * @param categoryCode 分类代码
     * @return 根节点ID，如果不存在则返回 null
     */
    public Long getRootIdByCategory(CategoryCode categoryCode) {
        ResourceNode rootNode = resourceRepository.findByCategoryCode(categoryCode);
        return rootNode != null ? rootNode.getId() : null;
    }

    /**
     * 获取指定目录下的所有子资源
     * @param parentId 父目录ID
     * @return 子资源列表
     */
    public List<ResourceNode> getChildren(Long parentId) {
        // 1. 查找父节点
        ResourceNode parentNode = resourceRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("父目录不存在"));

        // 2. 检查父节点是否为文件夹
        if (parentNode.getResourceType() != NodeType.DIRECTORY) {
            throw new IllegalArgumentException("指定ID不是文件夹，无法获取子资源");
        }

        // 3. 根据父节点的 path 查询直接子节点
        return resourceRepository.findDirectChildren(parentNode.getTreePath());
    }

    /**
     * 搜索资源
     * @param keyword 关键词
     * @return 匹配的资源列表
     */
    public List<ResourceNode> searchResources(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return resourceRepository.findByNodeNameContainingIgnoreCase(keyword.trim());
    }

    /**
     * 添加资源 (文件或文件夹)
     *
     * @param request  请求参数
     * @param userId   当前操作用户ID
     * @param username 当前操作用户名
     * @return 创建成功的资源节点
     */
    @Transactional
    public ResourceNode addResource(AddResourceRequest request, Long userId, String username) {
        // 1. 基础校验
        if (request.getNodeName() == null || request.getNodeName().trim().isEmpty()) {
            throw new IllegalArgumentException("资源名称不能为空");
        }
        if (request.getResourceType() == null) {
            throw new IllegalArgumentException("资源类型不能为空");
        }

        // 2. 获取用户信息 (为了填充 ownerName)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 3. 构建新的资源节点对象
        ResourceNode resourceNode = ResourceNode.builder()
                .nodeName(request.getNodeName())
                .resourceType(request.getResourceType())
                .ownerId(userId)
                .ownerName(user.getRealName() != null ? user.getRealName() : user.getUsername())
                .properties(request.getProperties())
                .build();

        // 4. 处理父子关系与路径
        ResourceNode parentNode = null;
        if (request.getParentId() == null) {
            throw new IllegalArgumentException("父目录 ID 不能为空 (用户只能在现有目录下创建资源)");
        }
        
        // 有父节点
        parentNode = resourceRepository.findById(request.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("父目录不存在"));

        if (parentNode.getResourceType() != NodeType.DIRECTORY) {
            throw new IllegalArgumentException("父节点必须是文件夹");
        }
        
        // 子节点不需要 categoryCode，通常跟随根或者为null。
        resourceNode.setCategoryCode(null);

        // 5. 先保存以获取 ID (设置临时 treePath)
        // 注意：PostgreSQL ltree 不能为空且格式有限制。我们暂时用一个 UUID 的一部分或者 "temp" 占位
        // 但为了避免并发下的冲突，最好用 UUID。
        // 不过 ltree 只能包含 A-Za-z0-9_。UUID 有连字符，需要去掉。
        String tempPath = "temp_" + UUID.randomUUID().toString().replace("-", "_");
        resourceNode.setTreePath(tempPath);
        
        resourceNode = resourceRepository.save(resourceNode);

        // 6. 生成正式的 treePath 并更新
        String finalPath;
        if (parentNode != null) {
            finalPath = parentNode.getTreePath() + "." + resourceNode.getId();
        } else {
            finalPath = String.valueOf(resourceNode.getId());
        }

        resourceNode.setTreePath(finalPath);
        return resourceRepository.save(resourceNode);
    }
}
