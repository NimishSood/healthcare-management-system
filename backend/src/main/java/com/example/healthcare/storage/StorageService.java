package com.example.healthcare.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    String store(MultipartFile file) throws IOException;
    Resource loadAsResource(String storageKey) throws IOException;
    void delete(String storageKey) throws IOException;
}