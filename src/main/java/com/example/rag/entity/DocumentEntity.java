package com.example.rag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
public class DocumentEntity {
    @Id
    private UUID id;
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;
    @Column(name = "stored_filename", nullable = false)
    private String storedFilename;
    @Column(name = "content_type")
    private String contentType;
    @Column(name = "file_size", nullable = false)
    private long fileSize;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "chunk_count", nullable = false)
    private int chunkCount;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void created() {
        if (id == null) id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void updated() {
        updatedAt = Instant.now();
    }
}
