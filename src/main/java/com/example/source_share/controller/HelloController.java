package com.example.source_share.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hi")
    public String sayHi() {
        return "Linux 上的 Spring Boot 已经跑起来啦！当前 Java 版本: 24";
    }
}