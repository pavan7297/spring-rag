package com.example.rag.repository;

import com.example.rag.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<DocumentEntity, UUID> {
}
