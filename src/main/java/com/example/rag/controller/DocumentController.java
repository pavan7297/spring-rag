package com.example.rag.controller;

import com.example.rag.dto.DocumentSummary;
import com.example.rag.service.DocumentService;

import java.util.*;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService service;

    public DocumentController(DocumentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentSummary> list() {
        return service.list();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentSummary> upload(@RequestPart("file") MultipartFile file) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.upload(file));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
