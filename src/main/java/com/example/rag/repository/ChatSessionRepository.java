package com.example.rag.repository;

import com.example.rag.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
}
