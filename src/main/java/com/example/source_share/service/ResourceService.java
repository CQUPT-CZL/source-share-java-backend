package com.example.source_share.service;

import com.example.source_share.dto.AddResourceRequest;
import com.example.source_share.model.CategoryCode;
import com.example.source_share.model.NodeType;
import com.example.source_share.model.ResourceNode;
import com.example.source_share.model.User;
import com.example.source_share.repository.ResourceRepository;
import com.example.source_share.repository.UserRepository;
import com.example.source_share.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private LogService logService;

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
     * 删除资源
     * @param resourceId 资源ID
     * @param userId 当前操作用户ID
     * @param isAdmin 是否为管理员 (管理员拥有最高权限)
     */
    @Transactional
    public void deleteResource(Long resourceId, Long userId, boolean isAdmin) {
        // 1. 获取资源
        ResourceNode node = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("资源不存在"));

        // 2. 权限校验
        // 如果不是管理员，必须检查所有权
        if (!isAdmin) {
            if (!node.getOwnerId().equals(userId)) {
                throw new IllegalArgumentException("无权删除他人的资源");
            }
        }

        // 3. 文件夹校验
        if (node.getResourceType() == NodeType.DIRECTORY) {
            if (resourceRepository.existsChildren(node.getTreePath())) {
                throw new IllegalArgumentException("无法删除非空文件夹");
            }
        }

        // 4. 物理文件清理 (如果是文件)
        if (node.getResourceType() == NodeType.FILE) {
            String filePath = (String) node.getProperties().get("filePath");
            if (filePath != null) {
                // 这里的删除异常不应该阻断数据库删除，可以记录日志
                try {
                    fileStorageService.deleteFile(filePath);
                } catch (Exception e) {
                    System.err.println("物理文件删除失败: " + filePath);
                }
            }
        }

        // 5. 数据库删除
        resourceRepository.delete(node);

        // 6. 记录日志 (异步)
        logService.log(
            userId, 
            "DELETE", 
            node.getNodeName(), 
            node.getId(), 
            "Deleted resource type: " + node.getResourceType()
        );
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
     * @param folderId 搜索范围的文件夹ID (如果为 null，则搜索所有)
     * @return 匹配的资源列表
     */
    public List<ResourceNode> searchResources(String keyword, Long folderId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        List<ResourceNode> results;
        
        // 如果没有指定文件夹，默认全库搜索
        if (folderId == null) {
            results = resourceRepository.findByNodeNameContainingIgnoreCaseOrderByResourceTypeAscNodeNameAsc(keyword.trim());
        } else {
            // 如果指定了文件夹，先找到该文件夹获取其 treePath
            ResourceNode folder = resourceRepository.findById(folderId)
                    .orElseThrow(() -> new IllegalArgumentException("搜索的文件夹不存在"));
            
            if (folder.getResourceType() != NodeType.DIRECTORY) {
                 throw new IllegalArgumentException("搜索范围必须是文件夹");
            }

            results = resourceRepository.searchRecursively(folder.getTreePath(), keyword.trim());
        }
        
        // 填充路径信息，方便前端区分同名文件
        populatePathInfo(results);
        
        return results;
    }

    /**
     * 批量填充资源的路径信息 (displayPath)
     */
    private void populatePathInfo(List<ResourceNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // 1. 收集所有涉及的父节点 ID
        Set<Long> allParentIds = new HashSet<>();
        for (ResourceNode node : nodes) {
            if (node.getTreePath() != null) {
                String[] parts = node.getTreePath().split("\\.");
                // 排除自己 (最后一个 ID)
                for (int i = 0; i < parts.length - 1; i++) {
                    try {
                        allParentIds.add(Long.parseLong(parts[i]));
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        if (allParentIds.isEmpty()) {
            return;
        }

        // 2. 批量查询 ID 对应的名称
        List<ResourceNode> parents = resourceRepository.findAllById(allParentIds);
        Map<Long, String> idNameMap = parents.stream()
                .collect(Collectors.toMap(ResourceNode::getId, ResourceNode::getNodeName));

        // 3. 构建路径字符串并填充
        for (ResourceNode node : nodes) {
            if (node.getTreePath() != null) {
                String[] parts = node.getTreePath().split("\\.");
                StringBuilder pathBuilder = new StringBuilder();
                
                for (int i = 0; i < parts.length - 1; i++) { // 不包含自己
                    try {
                        Long id = Long.parseLong(parts[i]);
                        String name = idNameMap.get(id);
                        if (name != null) {
                            if (pathBuilder.length() > 0) {
                                pathBuilder.append(" / ");
                            }
                            pathBuilder.append(name);
                        }
                    } catch (NumberFormatException ignored) {}
                }
                
                // 存入 properties
                if (node.getProperties() == null) {
                    node.setProperties(new HashMap<>());
                }
                node.getProperties().put("displayPath", pathBuilder.toString());
            }
        }
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
        Map<String, Object> properties = request.getProperties();
        if (properties == null) {
            properties = new HashMap<>();
        }

        // 3.1 自动推断文件后缀 (如果是文件类型)
        if (request.getResourceType() == NodeType.FILE) {
            String fileName = request.getNodeName();
            int lastDotIndex = fileName.lastIndexOf(".");
            if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
                String extension = fileName.substring(lastDotIndex + 1).toLowerCase();
                properties.put("extension", extension);
            }
            
            // 确保 size 属性存在且为数字
            if (properties.get("size") == null) {
                properties.put("size", 0);
            }
        }

        ResourceNode resourceNode = ResourceNode.builder()
                .nodeName(request.getNodeName())
                .resourceType(request.getResourceType())
                .ownerId(userId)
                .ownerName(user.getRealName() != null ? user.getRealName() : user.getUsername())
                .properties(properties)
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
        resourceNode = resourceRepository.save(resourceNode);

        // 7. 记录日志 (异步)
        logService.log(
            userId, 
            "UPLOAD", 
            resourceNode.getNodeName(), 
            resourceNode.getId(), 
            "Created resource type: " + resourceNode.getResourceType()
        );

        return resourceNode;
    }
}
