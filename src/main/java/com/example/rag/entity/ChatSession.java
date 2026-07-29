package com.example.rag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
public class ChatSession {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String title;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void create() {
        if (id == null) id = UUID.randomUUID();
        createdAt = updatedAt = Instant.now();
    }

    @PreUpdate
    void update() {
        updatedAt = Instant.now();
    }
}
