package com.example.finrag.exception;

public class FinancialRagException extends RuntimeException {

    public FinancialRagException(String message) {
        super(message);
    }

    public FinancialRagException(String message, Throwable cause) {
        super(message, cause);
    }
}