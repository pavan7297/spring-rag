package com.example.rag.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.rag.config.RagProperties;

@Service
public class StorageService {
    private final Path root;

    public StorageService(RagProperties properties) {
        try {
            root = Paths.get(properties.uploadsDir()).toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot prepare upload directory", e);
        }
    }

    public String store(MultipartFile file) throws IOException {
        String ext = extension(file.getOriginalFilename());
        String name = UUID.randomUUID() + ext;
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, root.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return name;
    }

    public Path path(String stored) {
        return root.resolve(stored).normalize();
    }

    public void delete(String stored) {
        try {
            Files.deleteIfExists(path(stored));
        } catch (IOException ignored) {
        }
    }

    private String extension(String name) {
        if (name == null || !name.contains(".")) return "";
        return name.substring(name.lastIndexOf('.')).toLowerCase();
    }
}
