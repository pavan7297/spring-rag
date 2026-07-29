package com.example.rag.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChatRequest(UUID sessionId, @NotBlank String question) {
}
