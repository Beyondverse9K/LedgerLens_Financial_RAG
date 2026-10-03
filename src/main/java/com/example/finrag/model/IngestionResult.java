package com.example.finrag.model;

public record IngestionResult(String documentId, String fileName, DocumentType docType, int chunkCount) {
}