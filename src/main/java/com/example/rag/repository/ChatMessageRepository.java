package com.example.rag.repository;

import com.example.rag.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.*;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
}
