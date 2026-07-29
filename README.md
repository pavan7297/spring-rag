# RAG Studio

A local, document-grounded chat application built with Spring Boot 4, Thymeleaf, Spring AI, Ollama, PostgreSQL, and pgvector.

## What it does

- Accepts PDF, DOCX, TXT, and Markdown files (up to 25 MB).
- Extracts text with Apache Tika, splits it into overlapping chunks, and creates Ollama embeddings.
- Stores chunks and embeddings in PostgreSQL with pgvector cosine search.
- Retrieves the most relevant passages for every question and asks Ollama to answer only from that context.
- Keeps chat-session history and exposes source passages in the chat API response.

## Prerequisites

- Java 21 and Maven 3.6.3+
- Docker Desktop (recommended) or a reachable PostgreSQL server with pgvector
- Ollama with the selected chat and embedding models

## Run locally

```bash
docker compose up -d
ollama pull llama3.2
ollama pull nomic-embed-text
mvn spring-boot:run
```

Open http://localhost:8080. Flyway applies the database schema automatically at startup.

`nomic-embed-text` produces 768-dimensional vectors, matching the schema. If you switch to a model with a different dimension, update `V1__initial_schema.sql` before creating the database.

## Configuration

| Variable | Default | Purpose |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/ragdb` | JDBC connection URL |
| `DATABASE_USER` / `DATABASE_PASSWORD` | `rag` / `rag` | Database credentials |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama endpoint |
| `OLLAMA_CHAT_MODEL` | `llama3.2` | Chat model |
| `OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Embedding model |
| `RAG_UPLOADS_DIR` | `./data/uploads` | Persisted upload location |

## API

- `POST /api/documents` — multipart field named `file`
- `GET /api/documents` — indexed document metadata
- `DELETE /api/documents/{id}` — removes the file and its chunks
- `POST /api/chat` — JSON: `{ "sessionId": null, "question": "..." }`

## Container image

```bash
mvn package
docker build -t rag-studio .
docker run --network host -e DATABASE_URL=jdbc:postgresql://localhost:5432/ragdb rag-studio
```

The included compose file intentionally runs database and Ollama separately so models remain persistent and can be pulled explicitly.
"# spring-rag" 
