package com.example.aiagent.service.impl;

import com.example.aiagent.config.RagProperties;
import com.example.aiagent.service.DocumentStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentStorageServiceImpl implements DocumentStorageService {

    private final RagProperties ragProperties;

    @Override
    public String saveFile(String kbId, String documentId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件为空");
        }

        Path kbDir = Paths.get(ragProperties.getDocumentStorageBasePath(), kbId);
        Path documentDir = kbDir.resolve(documentId);
        Files.createDirectories(documentDir);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        Path targetPath = documentDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        String relativePath = Paths.get(kbId, documentId, uniqueFilename).toString().replace("\\", "/");
        log.info("文件保存成功: kbId={}, documentId={}, path={}", kbId, documentId, relativePath);
        return relativePath;
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        Path fullPath = getFilePath(filePath);
        if (Files.exists(fullPath)) {
            Files.delete(fullPath);
            log.info("文件删除成功: {}", filePath);
            // 尝试删除空的父目录
            Path parentDir = fullPath.getParent();
            if (parentDir != null && Files.exists(parentDir)) {
                try {
                    Files.delete(parentDir);
                } catch (IOException e) {
                    log.debug("目录删除失败（可能不为空）: {}", parentDir);
                }
            }
        } else {
            log.warn("文件不存在，跳过删除: {}", filePath);
        }
    }

    @Override
    public Path getFilePath(String filePath) {
        return Paths.get(ragProperties.getDocumentStorageBasePath(), filePath);
    }

    @Override
    public boolean fileExists(String filePath) {
        return Files.exists(getFilePath(filePath)) && Files.isRegularFile(getFilePath(filePath));
    }
}
