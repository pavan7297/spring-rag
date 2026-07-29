package com.example.rag.service;

import com.example.rag.config.RagProperties;
import com.example.rag.dto.DocumentSummary;
import com.example.rag.entity.*;
import com.example.rag.repository.*;
import com.example.rag.util.TextChunker;

import java.nio.file.Path;
import java.util.*;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DocumentService {
    private final DocumentRepository documents;
    private final ChunkRepository chunks;
    private final StorageService storage;
    private final DocumentTextExtractor extractor;
    private final TextChunker chunker;
    private final EmbeddingModel embeddingModel;
    private final RagProperties properties;

    public DocumentService(DocumentRepository documents, ChunkRepository chunks, StorageService storage, DocumentTextExtractor extractor, TextChunker chunker, EmbeddingModel embeddingModel, RagProperties properties) {
        this.documents = documents;
        this.chunks = chunks;
        this.storage = storage;
        this.extractor = extractor;
        this.chunker = chunker;
        this.embeddingModel = embeddingModel;
        this.properties = properties;
    }

    public DocumentSummary upload(MultipartFile file) throws Exception {
        validate(file);
        DocumentEntity d = new DocumentEntity();
        d.setOriginalFilename(Optional.ofNullable(file.getOriginalFilename()).orElse("upload"));
        d.setContentType(file.getContentType());
        d.setFileSize(file.getSize());
        d.setStoredFilename(storage.store(file));
        d.setStatus(DocumentStatus.PROCESSING);
        documents.saveAndFlush(d);
        try {
            ingest(d);
        } catch (Exception e) {
            d.setStatus(DocumentStatus.FAILED);
            d.setErrorMessage(e.getMessage());
            documents.save(d);
        }
        return DocumentSummary.from(d);
    }

    @Transactional
    public void ingest(DocumentEntity d) throws Exception {
        Path path = storage.path(d.getStoredFilename());
        List<String> parts = chunker.split(extractor.extract(path), properties.chunkSize(), properties.chunkOverlap());
        if (parts.isEmpty()) throw new IllegalArgumentException("No readable text found in document");
        for (int i = 0; i < parts.size(); i++)
            chunks.save(d.getId(), i, parts.get(i), embeddingModel.embed(parts.get(i)));
        d.setChunkCount(parts.size());
        d.setStatus(DocumentStatus.READY);
        d.setErrorMessage(null);
        documents.save(d);
    }

    public List<DocumentSummary> list() {
        return documents.findAll().stream().sorted(Comparator.comparing(DocumentEntity::getCreatedAt).reversed()).map(DocumentSummary::from).toList();
    }

    @Transactional
    public void delete(UUID id) {
        DocumentEntity d = documents.findById(id).orElseThrow(() -> new NoSuchElementException("Document not found"));
        chunks.deleteByDocumentId(id);
        documents.delete(d);
        storage.delete(d.getStoredFilename());
    }

    private void validate(MultipartFile f) {
        if (f.isEmpty()) throw new IllegalArgumentException("Choose a non-empty file");
        String n = Optional.ofNullable(f.getOriginalFilename()).orElse("").toLowerCase();
        if (!(n.endsWith(".pdf") || n.endsWith(".docx") || n.endsWith(".txt") || n.endsWith(".md")))
            throw new IllegalArgumentException("Supported files are PDF, DOCX, TXT and Markdown");
    }
}
