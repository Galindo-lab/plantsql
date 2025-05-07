package org.example.plantsql.exeptions;

public class SyntaxExeption extends Exception {
    public SyntaxExeption(String message) {
        super(message);
    }

    public SyntaxExeption(String message1, String message2) {
        super("Se esperaba un token '" + message1 + "' y se encontró '" + message2 + "'");
    }
}