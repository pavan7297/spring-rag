package com.example.rag.dto;

import java.util.*;

public record ChatResponse(UUID sessionId, String answer, List<SourceExcerpt> sources) {
}
