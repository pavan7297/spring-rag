package com.example.rag.util;

import java.util.*;

import org.springframework.stereotype.Component;

@Component
public class TextChunker {
    public List<String> split(String input, int size, int overlap) {
        String text = input == null ? "" : input.replaceAll("\\s+", " ").trim();
        if (text.isBlank()) return List.of();
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(text.length(), start + size);
            if (end < text.length()) {
                int boundary = Math.max(text.lastIndexOf(". ", end), text.lastIndexOf(" ", end));
                if (boundary > start + size / 2) end = boundary + 1;
            }
            chunks.add(text.substring(start, end).trim());
            if (end == text.length()) break;
            start = Math.max(end - overlap, start + 1);
        }
        return chunks;
    }
}
