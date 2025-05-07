package org.example.plantsql.exeptions;

public class SemanticException extends Exception {
    public SemanticException(String message) {
        super(message);
    }

    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }
}