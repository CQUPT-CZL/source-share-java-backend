package com.example.source_share.controller;

import com.example.source_share.common.Result;
import com.example.source_share.dto.AddResourceRequest;
import com.example.source_share.model.ResourceNode;
import com.example.source_share.service.ResourceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.source_share.model.CategoryCode;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @GetMapping("/root-id")
    public Result<Long> getRootIdByCategory(@RequestParam CategoryCode category) {
        Long rootId = resourceService.getRootIdByCategory(category);
        if (rootId == null) {
            return Result.error(404, "该分类下的根目录不存在");
        }
        return Result.success(rootId);
    }

    @GetMapping("/{parentId}/children")
    public Result<java.util.List<ResourceNode>> getChildren(@PathVariable Long parentId) {
        try {
            return Result.success(resourceService.getChildren(parentId));
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping
    public Result<java.util.List<ResourceNode>> searchResources(@RequestParam String keyword) {
        return Result.success(resourceService.searchResources(keyword));
    }

    @PostMapping
    public Result<ResourceNode> addResource(@RequestBody AddResourceRequest addResourceRequest, 
                                          HttpServletRequest request) {
        // 从拦截器中获取用户信息
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");

        // 双重校验，虽然拦截器已经处理，但防止配置疏漏
        if (userId == null) {
            return Result.error(401, "未登录用户无法添加资源");
        }

        try {
            ResourceNode createdNode = resourceService.addResource(addResourceRequest, userId, username);
            return Result.success("资源添加成功", createdNode);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "服务器内部错误: " + e.getMessage());
        }
    }
}
