package com.example.springecom.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AiConfig {

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        // Run SQL commands to enable required extensions before Spring AI initializes the schema
        try {
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS hstore"); // used to store metadata as key-value pairs
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"");
        } catch (Exception e) {
            System.err.println("Warning: Failed to create database extensions: " + e.getMessage());
        }

        PgVectorStore pgVectorStore =  PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(768)
                .initializeSchema(true)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .build();

        try {
            String createHNSWIndexSql =
                    "CREATE INDEX IF NOT EXISTS vector_store_hnsw_cosine_idx " +
                            "ON vector_store USING hnsw (embedding vector_cosine_ops) " +
                            "WITH (m = 16, ef_construction = 64);";

            jdbcTemplate.execute(createHNSWIndexSql);
            System.out.println("Success: HNSW Index created with Cosine Distance.");
        } catch (Exception e) {
            System.err.println("Warning: Failed to create HNSW index: " + e.getMessage());
        }

        return pgVectorStore;
    }
}
