package com.example.finrag.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "finrag")
public record RagProperties(
        @DefaultValue("500") int chunkSize,
        @DefaultValue("5") int topK,
        @DefaultValue("0.5") double similarityThreshold) {
}