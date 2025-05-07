package org.example.plantsql;

import org.example.plantsql.core.Token;
import org.example.plantsql.exeptions.SemanticException;
import org.example.plantsql.generator.SQLTable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.plantsql.core.TokenType.*;

public class Generator {
    private ArrayList<Token> tokens;
    private int tokenIndex = 0;
    private StringBuilder output = new StringBuilder(); // acumulador de SQL
    private SymbolTable symbolTable;

    public Generator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String getOutput() {
        return output.toString();
    }

    private Token currentToken() {
        return (tokenIndex < tokens.size()) ? tokens.get(tokenIndex) : null;
    }

    private void nextToken() {
        tokenIndex++;
    }

    public void generate(Lexer lexer) throws SemanticException {
        tokens = lexer.getTokens();

        while (tokenIndex < (tokens.size() - 1)) {
            if (currentToken().getType() == ENTITY) {
                generateTable();
            }

            if (currentToken().getType() == IDENTIFIER) {
                String tb1 = currentToken().getValue();
                nextToken();
                String relation = currentToken().getValue();
                nextToken();
                String tb2 = currentToken().getValue();

                Pattern pattern = Pattern.compile("^}.*\\{$");
                Matcher matcher = pattern.matcher(relation);
                boolean coincide = matcher.matches();

                if (coincide) {
                    generateManyToManyTable(tb1, tb2);
                }
            }

            nextToken();
        }
    }

    private void generateManyToManyTable(String tb1, String tb2) throws SemanticException {
        String tbName = tb1 + tb2;
        SQLTable table = new SQLTable(tbName, symbolTable);

        if (currentToken().getType() == COLON) {
            nextToken(); // :
            nextToken(); // <STR>
        }

        SymbolTable.PrimaryKey tb1Pk = symbolTable.getPrimaryKey(tb1);
        SymbolTable.PrimaryKey tb2Pk = symbolTable.getPrimaryKey(tb2);

        table.createAttribute(tbName, "integer", "GENERATED", true, true);
        table.createAttribute(tb1Pk.name(), tb1, "FK", false, true);
        table.createAttribute(tb2Pk.name(), tb2, "FK", false, true);

        output.append(table.getCode()).append("\n"); // almacenar en output
    }

    private void generateTable() throws SemanticException {
        nextToken(); // skip 'entity'

        if (currentToken().getType() == STRING) {
            nextToken(); // alias
            nextToken(); // as
        }

        SQLTable table = new SQLTable(currentToken().getValue(), symbolTable);
        nextToken();
        nextToken(); // skip '{'

        while (currentToken().getType() != RBRACKET) {
            addAttribute(table);
        }

        output.append(table.getCode()).append("\n"); // almacenar en output
    }

    private void addAttribute(SQLTable table) {
        String name;
        String type;
        String modifier = "";
        boolean mandatory = false;
        boolean primary = false;

        if (currentToken().getType() == MANDATORY) {
            mandatory = true;
            nextToken();
        }

        name = currentToken().getValue();
        nextToken();
        nextToken(); // :

        type = currentToken().getValue();
        nextToken();

        if (currentToken().getType() == LGUILLEMET) {
            nextToken(); // <<
            modifier = currentToken().getValue();
            nextToken();
            nextToken(); // >>
        }

        if (currentToken().getType() == HLINE) {
            primary = true;
            nextToken();
        }

        table.createAttribute(name, type, modifier, primary, mandatory);
    }
}
