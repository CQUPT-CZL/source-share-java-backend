package com.example.source_share.controller;

import com.example.source_share.common.Result;
import com.example.source_share.dto.ResourceStatisticsDTO;
import com.example.source_share.service.StatisticsService;
import com.example.source_share.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<ResourceStatisticsDTO> getStatistics(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "请先登录");
        }

        return Result.success(statisticsService.getStatistics());
    }
}
