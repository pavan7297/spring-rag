package com.example.rag.dto;

import java.util.UUID;

public record SourceExcerpt(UUID documentId, String filename, String excerpt, double similarity) {
}
