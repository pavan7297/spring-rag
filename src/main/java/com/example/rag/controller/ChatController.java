package com.example.rag.controller;

import com.example.rag.dto.*;
import com.example.rag.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final ChatService service;

    public ChatController(ChatService service) {
        this.service = service;
    }

    @PostMapping
    public ChatResponse ask(@Valid @RequestBody ChatRequest request) {
        return service.ask(request);
    }
}
