CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE documents (
  id UUID PRIMARY KEY,
  original_filename VARCHAR(512) NOT NULL,
  stored_filename VARCHAR(512) NOT NULL UNIQUE,
  content_type VARCHAR(255),
  file_size BIGINT NOT NULL,
  status VARCHAR(32) NOT NULL,
  error_message TEXT,
  chunk_count INTEGER NOT NULL DEFAULT 0,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE document_chunks (
  id UUID PRIMARY KEY,
  document_id UUID NOT NULL REFERENCES documents(id) ON DELETE CASCADE,
  chunk_index INTEGER NOT NULL,
  content TEXT NOT NULL,
  embedding vector(768) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  UNIQUE(document_id, chunk_index)
);
CREATE INDEX document_chunks_document_id_idx ON document_chunks(document_id);
CREATE INDEX document_chunks_embedding_idx ON document_chunks USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);
CREATE TABLE chat_sessions (
  id UUID PRIMARY KEY,
  title VARCHAR(160) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE chat_messages (
  id UUID PRIMARY KEY,
  session_id UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
  role VARCHAR(16) NOT NULL,
  content TEXT NOT NULL,
  sources_json TEXT,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX chat_messages_session_idx ON chat_messages(session_id, created_at);
