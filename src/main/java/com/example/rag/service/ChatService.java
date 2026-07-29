package com.example.rag.service;

import com.example.rag.config.RagProperties;
import com.example.rag.dto.*;
import com.example.rag.entity.*;
import com.example.rag.repository.*;

import java.util.*;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatService {
    private final ChatClient chat;
    private final EmbeddingModel embeddings;
    private final ChunkRepository chunks;
    private final ChatSessionRepository sessions;
    private final ChatMessageRepository messages;
    private final RagProperties properties;

    public ChatService(ChatClient.Builder builder, EmbeddingModel embeddings, ChunkRepository chunks, ChatSessionRepository sessions, ChatMessageRepository messages, RagProperties properties) {
        this.chat = builder.build();
        this.embeddings = embeddings;
        this.chunks = chunks;
        this.sessions = sessions;
        this.messages = messages;
        this.properties = properties;
    }

    @Transactional
    public ChatResponse ask(ChatRequest request) {
        ChatSession session = request.sessionId() == null ? createSession(request.question()) : sessions.findById(request.sessionId()).orElseThrow(() -> new NoSuchElementException("Chat session not found"));
        List<SourceExcerpt> sources = chunks.search(embeddings.embed(request.question()), properties.retrievalLimit());
        String context = sources.isEmpty() ? "No indexed document passages were found." : formatContext(sources);
        String answer = chat.prompt().system("You are a precise document assistant. Answer only from the supplied context. If the context is insufficient, say so plainly. Cite sources in brackets using their filename.\n\nCONTEXT:\n" + context).user(request.question()).call().content();
        save(session, "USER", request.question(), null);
        save(session, "ASSISTANT", answer, sources.stream().map(SourceExcerpt::filename).distinct().reduce((a, b) -> a + ", " + b).orElse(null));
        return new ChatResponse(session.getId(), answer, sources);
    }

    public List<ChatSession> sessions() {
        return sessions.findAll().stream().sorted(Comparator.comparing(ChatSession::getUpdatedAt).reversed()).toList();
    }

    public List<ChatMessage> messages(UUID id) {
        return messages.findBySessionIdOrderByCreatedAtAsc(id);
    }

    private ChatSession createSession(String question) {
        ChatSession s = new ChatSession();
        s.setTitle(question.length() > 60 ? question.substring(0, 57) + "..." : question);
        return sessions.save(s);
    }

    private void save(ChatSession s, String role, String content, String sources) {
        ChatMessage m = new ChatMessage();
        m.setSession(s);
        m.setRole(role);
        m.setContent(content);
        m.setSourcesJson(sources);
        messages.save(m);
        s.setTitle(s.getTitle());
        sessions.save(s);
    }

    private String formatContext(List<SourceExcerpt> sources) {
        StringBuilder b = new StringBuilder();
        for (SourceExcerpt s : sources)
            b.append("[Source: ").append(s.filename()).append("]\n").append(s.excerpt()).append("\n\n");
        return b.toString();
    }
}
