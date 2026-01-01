package com.example.source_share.controller;

import com.example.source_share.common.Result;
import com.example.source_share.dto.LogResponseDTO;
import com.example.source_share.service.LogService;
import com.example.source_share.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class LogController {

    @Autowired
    private LogService logService;

    @Autowired
    private UserService userService;

    /**
     * 获取系统日志 (仅管理员)
     */
    @GetMapping
    public Result<Page<LogResponseDTO>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        return Result.success(logService.getLogs(page, size));
    }
}
