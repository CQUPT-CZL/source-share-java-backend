package com.example.source_share.controller;

import com.example.source_share.common.Result;
import com.example.source_share.service.storage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        // 1. 保存文件到本地磁盘
        String storedFilename = fileStorageService.store(file);

        // 2. 生成文件的访问 URL (例如 http://localhost:8080/uploads/uuid.jpg)
        // 注意：这里生成的 URL 是给前端预览或下载用的
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(storedFilename)
                .toUriString();

        // 3. 返回信息给前端
        Map<String, String> response = new HashMap<>();
        response.put("originalName", file.getOriginalFilename());
        response.put("storedName", storedFilename);
        response.put("url", fileDownloadUri);
        response.put("size", String.valueOf(file.getSize()));

        return Result.success("上传成功", response);
    }
}
