package com.example.rag.service;

import java.nio.file.Path;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

@Service
public class DocumentTextExtractor {
    private final Tika tika = new Tika();

    public String extract(Path path) throws Exception {
        return tika.parseToString(path);
    }
}
