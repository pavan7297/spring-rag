package com.example.rag.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
public class ChatMessage {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;
    @Column(nullable = false)
    private String role;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    @Column(name = "sources_json", columnDefinition = "TEXT")
    private String sourcesJson;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    void create() {
        if (id == null) id = UUID.randomUUID();
        createdAt = Instant.now();
    }
}
