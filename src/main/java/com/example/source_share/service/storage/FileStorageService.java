package com.example.source_share.service.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    // 文件存储的根目录 (这里暂时存在项目运行目录下的 uploads 文件夹)
    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() {
        try {
            // 初始化时创建目录
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("无法初始化存储目录", e);
        }
    }

    /**
     * 保存文件
     * @param file 前端上传的文件
     * @return 文件的存储文件名 (UUID + 后缀)
     */
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("无法存储空文件");
            }

            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            // 获取后缀名 (如 .jpg, .docx)
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成唯一文件名，防止重名覆盖
            String storedFilename = UUID.randomUUID().toString() + extension;

            // 保存文件到本地
            Files.copy(file.getInputStream(), this.rootLocation.resolve(storedFilename));

            return storedFilename;
        } catch (IOException e) {
            throw new RuntimeException("存储文件失败", e);
        }
    }

    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    /**
     * 删除文件
     * @param filename 存储的文件名
     */
    public void deleteFile(String filename) {
        try {
            Path file = rootLocation.resolve(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("无法删除文件: " + filename, e);
        }
    }
}
