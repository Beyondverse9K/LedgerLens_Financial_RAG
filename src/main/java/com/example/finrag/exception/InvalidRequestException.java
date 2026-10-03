package com.example.finrag.exception;

public class InvalidRequestException extends FinancialRagException {

    public InvalidRequestException(String message) {
        super(message);
    }
}