package com.example.rag.controller;

import com.example.rag.service.*;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {
    private final DocumentService documents;
    private final ChatService chats;

    public PageController(DocumentService documents, ChatService chats) {
        this.documents = documents;
        this.chats = chats;
    }

    @GetMapping("/")
    public String home(Model m) {
        m.addAttribute("documents", documents.list());
        m.addAttribute("sessions", chats.sessions());
        return "index";
    }

    @GetMapping("/documents")
    public String documents(Model m) {
        m.addAttribute("documents", documents.list());
        return "documents";
    }

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @GetMapping("/chat")
    public String chat(@RequestParam(required = false) UUID session, Model m) {
        m.addAttribute("sessions", chats.sessions());
        m.addAttribute("activeSession", session);
        if (session != null) m.addAttribute("messages", chats.messages(session));
        return "chat";
    }
}
