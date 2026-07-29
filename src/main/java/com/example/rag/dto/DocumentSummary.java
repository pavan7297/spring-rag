package com.example.rag.dto;

import com.example.rag.entity.DocumentEntity;

import java.time.Instant;
import java.util.UUID;

public record DocumentSummary(UUID id, String filename, String contentType, long size, String status, int chunks,
                              Instant createdAt, String error) {
    public static DocumentSummary from(DocumentEntity d) {
        return new DocumentSummary(d.getId(), d.getOriginalFilename(), d.getContentType(), d.getFileSize(), d.getStatus().name(), d.getChunkCount(), d.getCreatedAt(), d.getErrorMessage());
    }
}
