package com.example.healthcare.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FilesystemStorageService implements StorageService {

    private final Path basePath;

    public FilesystemStorageService(@Value("${storage.filesystem.path}") String basePath) {
        this.basePath = Paths.get(basePath);
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path destination = basePath.resolve(filename);
        Files.createDirectories(destination.getParent());
        try (var input = file.getInputStream()) {
            Files.copy(input, destination, StandardCopyOption.REPLACE_EXISTING);
        }
        return filename;
    }

    @Override
    public Resource loadAsResource(String storageKey) throws IOException {
        Path filePath = basePath.resolve(storageKey);
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new IOException("Invalid file path", e);
        }
        if (!resource.exists()) {
            throw new IOException("File not found: " + storageKey);
        }
        return resource;
    }
    @Override
    public void delete(String storageKey) throws IOException {
        Path filePath = basePath.resolve(storageKey);
        Files.deleteIfExists(filePath);
    }
}