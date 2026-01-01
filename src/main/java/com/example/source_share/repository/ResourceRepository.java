package com.example.source_share.repository;

import com.example.source_share.model.CategoryCode;
import com.example.source_share.model.ResourceNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceNode, Long> {
    boolean existsByCategoryCode(CategoryCode categoryCode);

    /**
     * 根据资源名称模糊查询 (忽略大小写)，优先显示文件夹
     * @param nodeName 资源名称关键词
     * @return 匹配的资源列表
     */
    List<ResourceNode> findByNodeNameContainingIgnoreCaseOrderByResourceTypeAscNodeNameAsc(String nodeName);

    /**
     * 根据分类代码查找根节点
     * @param categoryCode 分类代码
     * @return 根节点
     */
    ResourceNode findByCategoryCode(CategoryCode categoryCode);

    /**
     * 查找直接子节点
     * 原理：使用 ltree 操作符 ~ (匹配正则表达式)
     * parentPath.*{1} 表示匹配 parentPath 下的一级子节点
     * 注意：nativeQuery = true 是必须的，因为 JPQL 不支持 ltree 操作符
     */
    // @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM resource_nodes WHERE tree_path ~ (?1 || '.*{1}')::lquery", nativeQuery = true)
    // List<ResourceNode> findDirectChildren(String parentPath);

    @Query(value = """
    SELECT * FROM resource_nodes 
    WHERE tree_path <@ CAST(?1 AS ltree) 
      AND nlevel(tree_path) = nlevel(CAST(?1 AS ltree)) + 1
    """, nativeQuery = true)
    List<ResourceNode> findDirectChildren(String parentPath);

    /**
     * 检查指定路径下是否存在子节点
     * @param parentPath 父节点路径
     * @return 如果存在子节点返回 true
     */
    @Query(value = """
    SELECT EXISTS (
        SELECT 1 FROM resource_nodes 
        WHERE tree_path <@ CAST(?1 AS ltree) 
          AND nlevel(tree_path) = nlevel(CAST(?1 AS ltree)) + 1
    )
    """, nativeQuery = true)
    boolean existsChildren(String parentPath);

    /**
     * 在指定文件夹及其子文件夹下搜索资源 (递归)
     * @param rootPath 搜索的根目录路径
     * @param keyword 搜索关键词 (资源名称)
     * @return 匹配的资源列表
     */
    @Query(value = """
        SELECT * FROM resource_nodes 
        WHERE tree_path <@ CAST(?1 AS ltree) 
          AND node_name ILIKE CONCAT('%', ?2, '%')
        ORDER BY resource_type ASC, node_name ASC
    """, nativeQuery = true)
    List<ResourceNode> searchRecursively(String rootPath, String keyword);
}
