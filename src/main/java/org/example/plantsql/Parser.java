package org.example.plantsql;

import org.example.plantsql.core.Token;
import org.example.plantsql.core.TokenType;
import org.example.plantsql.exeptions.SemanticException;
import org.example.plantsql.exeptions.SyntaxExeption;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import static org.example.plantsql.core.TokenType.*;

public class Parser {
    private ArrayList<Token> tokens;
    private int tokenIndex = 0;
    private SyntaxExeption ex;
    private SymbolTable symbolTable;

    public Parser(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private boolean test(TokenType tokenType, Callable<Boolean> funcion) {
        int indiceAux = tokenIndex; // save current token
        Token currentToken = tokens.get(tokenIndex); // getCurrentToken

        // trata de ejecutar la funcion de prueba
        if (currentToken.getType() == tokenType) {
            try {
                if (funcion.call()) {
                    return true;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // restore previus index
        tokenIndex = indiceAux;
        return false;
    }

    /**
     * Attempts to match the current token against the expected token type.
     *
     * @param expected the expected TokenType.
     * @return true if the token matches.
     */
    private boolean match(TokenType expected) {
        Token currentToken = tokens.get(tokenIndex);

        if (expected != currentToken.getType()) {
            String message = String.format("Se esperaba: '%s' y se encontró: '%s'", expected.name(), currentToken.getType().name());
            ex = new SyntaxExeption(message);
            return false;
        }

        String msg = String.format("%s", currentToken.toString());
        tokenIndex++;
        return true;
    }

    /**
     * Initiates the syntax analysis using tokens provided by the lexer.
     *
     * @param lexer that produces the list of tokens.
     * @throws SyntaxExeption if a syntax error is encountered during parsing.
     */
    public void analyze(Lexer lexer) throws SyntaxExeption {
        tokens = lexer.getTokens();

        if (pumlDiagram()) {
            if (tokenIndex == tokens.size()) {
                String message = "\nLa sintaxis del programa es correcta";
                System.out.println(message);
                return;
            }
        }

        throw ex;
    }

    /**
     * Maquina de estado para el analisis semantico
     */
    private boolean pumlDiagram() {
        return match(STARTUML)
                && definitions()
                && match(ENDUML);
    }

    private boolean definitions() {
        int indiceAux = tokenIndex;

        if (definition()) {
            while (definition()) ;
            return true;
        }

        tokenIndex = indiceAux;
        return false;
    }

    private boolean definition() {

        if (test(ENTITY, this::entityDefinition)) {
            return true;
        }

        if (test(IDENTIFIER, this::relationDefinition)) {
            return true;
        }

        return false;
    }

    private boolean relationDefinition() throws SemanticException {

        boolean foo = false;
        if (match(IDENTIFIER) && match(RELATION) && match(IDENTIFIER)) {
            symbolTable.getPrimaryKey(tokens.get(tokenIndex - 3).getValue());
            symbolTable.getPrimaryKey(tokens.get(tokenIndex - 1).getValue());

            foo = true;
        }

        if (match(COLON) && (match(IDENTIFIER) || match(STRING))) {
            return true;
        }

        return foo;
    }


    private Boolean entityDefinition() throws SemanticException {
        if (match(ENTITY)) {
            String tableName = "";

            // entity Customer { <Attribs> }
            if (match(IDENTIFIER)) {
                tableName = tokens.get(tokenIndex - 1).getValue();
                symbolTable.addTable(tableName);
            }

            // entity "Orden" as Order { <Attribs> }
            if (match(STRING)
                    && match(AS)
                    && match(IDENTIFIER)) {
                tableName = tokens.get(tokenIndex - 1).getValue();
                symbolTable.addTable(tableName);
            }

            if (match(LBRACKET)
                    && entityBody(tableName)
                    && match(RBRACKET)) {
                return true;
            }
        }

        return false;
    }


    private Boolean entityBody(String tableName) throws SemanticException {
        String[] pkAttributes = new String[2];
        pkAttributes[0] = ""; // pkName
        pkAttributes[1] = ""; // pkType

        if (attrib(pkAttributes) && match(HLINE)) {

            symbolTable.addPrimaryKey(tableName, pkAttributes[0], pkAttributes[1]);

            while (attrib(pkAttributes)) ;
            return true;
        }

        return false;
    }


    private Boolean attrib(String[] pkAttributes) {


        // astericos al inicio
        boolean mandatory = match(MANDATORY);

        // <IDENTIFIER>:<TYPE>
        if (match(IDENTIFIER) && match(COLON) && (match(TYPE) || match(IDENTIFIER))) {

            pkAttributes[0] = tokens.get(tokenIndex - 3).getValue(); // pkName
            pkAttributes[1] = tokens.get(tokenIndex - 1).getValue(); // pkType

            // << <MODIFIER> >>
            if (match(LGUILLEMET) && match(MODIFIER) && match(RGUILLEMET) && match(LGUILLEMET)) {
                return true;
            }

            return true;
        }

        return false;
    }

}


