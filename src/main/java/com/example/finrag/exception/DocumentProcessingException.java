package com.example.finrag.exception;

public class DocumentProcessingException extends FinancialRagException {

    public DocumentProcessingException(String message) {
        super(message);
    }

    public DocumentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}