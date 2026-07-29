package com.example.rag.repository;

import com.example.rag.dto.SourceExcerpt;

import java.time.Instant;
import java.util.*;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ChunkRepository {
    private final JdbcClient jdbc;

    public ChunkRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public void save(UUID documentId, int index, String content, float[] embedding) {
        jdbc.sql("insert into document_chunks (id, document_id, chunk_index, content, embedding, created_at) values (:id,:documentId,:idx,:content,cast(:embedding as vector),:createdAt)")
                .param("id", UUID.randomUUID()).param("documentId", documentId).param("idx", index).param("content", content).param("embedding", vector(embedding)).param("createdAt", Instant.now()).update();
    }

    public List<SourceExcerpt> search(float[] embedding, int limit) {
        return jdbc.sql("select c.document_id, d.original_filename, c.content, 1 - (c.embedding <=> cast(:embedding as vector)) as similarity from document_chunks c join documents d on d.id=c.document_id where d.status='READY' order by c.embedding <=> cast(:embedding as vector) limit :limit")
                .param("embedding", vector(embedding)).param("limit", limit).query((rs, n) -> new SourceExcerpt((UUID) rs.getObject("document_id"), rs.getString("original_filename"), rs.getString("content"), rs.getDouble("similarity"))).list();
    }

    public void deleteByDocumentId(UUID documentId) {
        jdbc.sql("delete from document_chunks where document_id=:id").param("id", documentId).update();
    }

    private String vector(float[] values) {
        StringBuilder b = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) b.append(',');
            b.append(values[i]);
        }
        return b.append(']').toString();
    }
}
