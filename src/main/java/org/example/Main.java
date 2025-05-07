package org.example;

import org.example.plantsql.Generator;
import org.example.plantsql.Lexer;
import org.example.plantsql.Parser;
import org.example.plantsql.SymbolTable;
import org.example.plantsql.exeptions.LexicalException;
import org.example.plantsql.exeptions.SemanticException;
import org.example.plantsql.exeptions.SyntaxExeption;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws LexicalException, IOException, SyntaxExeption {
        SymbolTable symbolTable = new SymbolTable();

        //  ------------------- d
        Lexer lexer = new Lexer();
        String archivo = readFileFromResources("examples/definition.puml");

        lexer.analyze(archivo);
        Lexer.printTokens(lexer);


        // -----------------
        System.out.println("\n\n\n");
        System.out.println("**** Analysis sintactico ****");
        Parser parser = new Parser(symbolTable);
        parser.analyze(lexer);


        // ----------------
        System.out.println("\n\n\n");
        SymbolTable.printSymbolTable(symbolTable);


        // -----------
        System.out.println("\n\n\n");
        System.out.println("**** Codigo SQl ****");
        Generator generator = new Generator(symbolTable);
        try {
            generator.generate(lexer);
        } catch (SemanticException e) {
            System.out.println("EEEEEEEEEEEEEEE");
        }


    }

    public static String readFileFromResources(String filename) throws IOException {
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File not found in resources: " + filename);
            }
            return new String(inputStream.readAllBytes());
        }
    }

}