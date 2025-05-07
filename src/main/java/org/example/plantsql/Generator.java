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
    private String output;
    private SymbolTable symbolTable;

    public Generator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    private Token currentToken() {
        return (tokenIndex < tokens.size()) ? tokens.get(tokenIndex) : null;
    }

    private void nextToken() {
        tokenIndex++;
    }

    /**
     * Generates SQL table definitions from the tokens provided by the lexer.
     *
     * @param lexer the lexer that provides the tokens to be processed
     * @throws SemanticException if a semantic error is encountered during generation
     */
    public void generate(Lexer lexer) throws SemanticException {
        tokens = lexer.getTokens();

        while (tokenIndex < (tokens.size() - 1)) {

            // keyword ENTITY
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

    /**
     * Generates a many-to-many relationship table between two specified tables.
     *
     * @param tb1 the name of the first table in the relationship
     * @param tb2 the name of the second table in the relationship
     * @throws SemanticException if a semantic error occurs during table generation
     */
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

        System.out.println(table.getCode());

    }

    /**
     * Generates a SQL table definition from the current tokens.
     *
     * @throws SemanticException if a semantic error occurs during table generation
     */
    private void generateTable() throws SemanticException {
        nextToken(); // skip 'entity'

        if (currentToken().getType() == STRING) { // alias
            nextToken(); // skip 'alias'
            nextToken(); // skip 'as'
        }

        // guarda el nombre
        SQLTable table = new SQLTable(currentToken().getValue(), symbolTable);
        nextToken();
        nextToken(); // skip '{'

        // attribs
        while (currentToken().getType() != RBRACKET) {
            addAttribute(table);
        }

        System.out.println(table.getCode());
    }

    /**
     * Adds an attribute to the specified SQL table based on the current tokens.
     *
     * @param table the SQL table to which the attribute will be added
     */
    private void addAttribute(SQLTable table) {
        String name = "";
        String type = "";
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
