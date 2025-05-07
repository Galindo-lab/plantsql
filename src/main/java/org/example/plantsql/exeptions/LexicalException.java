package org.example.plantsql.exeptions;

public class LexicalException extends Exception {
    public LexicalException(String message) {
        super("El token '" + message + "' es invalido");
    }
}