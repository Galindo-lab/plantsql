package org.example;

import org.example.plantsql.Generator;
import org.example.plantsql.Lexer;
import org.example.plantsql.Parser;
import org.example.plantsql.SymbolTable;
import org.example.plantsql.exeptions.LexicalException;
import org.example.plantsql.exeptions.SemanticException;
import org.example.plantsql.exeptions.SyntaxExeption;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) throws LexicalException, IOException, SyntaxExeption {
        if (args.length < 1) {
            System.err.println("Uso: java -jar programa.jar <archivo_entrada> [-o <archivo_salida>]");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = null;

        // Buscar flag -o
        for (int i = 1; i < args.length - 1; i++) {
            if (args[i].equals("-o")) {
                outputFile = args[i + 1];
                break;
            }
        }

        String archivo = readFileFromPath(inputFile);
        SymbolTable symbolTable = new SymbolTable();

        Lexer lexer = new Lexer();
        lexer.analyze(archivo);

        Parser parser = new Parser(symbolTable);
        parser.analyze(lexer);

        Generator generator = new Generator(symbolTable);
        try {
            generator.generate(lexer);
            String output = generator.getOutput();

            if (outputFile != null) {
                saveToFile(outputFile, output);
            } else {
                System.out.println(output);
            }

        } catch (SemanticException e) {
            System.err.println("Error semántico: " + e.getMessage());
        }
    }

    public static String readFileFromPath(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static void saveToFile(String filePath, String content) throws IOException {
        Files.writeString(Paths.get(filePath), content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
