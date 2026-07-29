package com.example.rag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag")
public record RagProperties(String uploadsDir, int chunkSize, int chunkOverlap, int retrievalLimit) { }
