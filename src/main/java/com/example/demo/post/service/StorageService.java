package com.example.demo.post.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    @Value("${upload.directory}")
    private String uploadDirectory;

    public String saveFile(MultipartFile file) throws IOException {
        // 파일이 비어있다면 예외 발생
        if (file.isEmpty()) {
            throw new IOException("파일이 비어 있습니다.");
        }

        // 저장할 디렉토리 경로 생성
        Path path = Paths.get(uploadDirectory, file.getOriginalFilename());

        // 디렉토리가 없다면 디렉토리 생성
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        // 파일을 지정된 경로에 저장
        file.transferTo(path);

        // 저장된 파일의 경로를 반환
        return path.toString();
    }

    // 저장된 파일 삭제
    public void deleteFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            Files.delete(file.toPath());
        }
    }
}
