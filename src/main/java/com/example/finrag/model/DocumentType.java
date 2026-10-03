package com.example.finrag.model;

public enum DocumentType {
    LOAN("Loan"),
    INSURANCE("Insurance"),
    BOND("Bond"),
    MUTUAL_FUND("Mutual Fund");

    private final String label;

    DocumentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}