package org.example.plantsql;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.example.plantsql.exeptions.SemanticException;

public class SymbolTable {
    private final ArrayList<String> tables = new ArrayList<>();
    LinkedHashMap<String, PrimaryKey> primaryKey = new LinkedHashMap<>();

    public static void printSymbolTable(SymbolTable symbolTable) {
        System.out.println("*** Symbol Table Content ***");

        symbolTable.primaryKey.forEach((table, pk) -> {
            System.out.println("  Table: '" + table + "'");
            System.out.println("  Name: " + pk.name());
            System.out.println("  Type: " + pk.type());
            System.out.println();
        });

        System.out.println("***************************");
    }

    /**
     * @param table
     * @throws SemanticException
     */
    public void addTable(String table) throws SemanticException {

        if (tables.contains(table))
            throw new SemanticException("La tabla '" + table + "' ya existe");

        tables.add(table);
    }

    /**
     * @param table
     * @param name
     * @param type
     * @throws SemanticException
     */
    public void addPrimaryKey(String table, String name, String type) throws SemanticException {

        if (primaryKey.containsKey(table))
            throw new SemanticException("La tabla '" + table + "' ya tiene una llave foranea");

        primaryKey.put(table, new PrimaryKey(name, type));

    }

    /**
     * @param table
     * @return
     * @throws SemanticException
     */
    public PrimaryKey getPrimaryKey(String table) throws SemanticException {

        if (!tables.contains(table))
            throw new SemanticException("La tabla '" + table + "' no existe");

        if (!primaryKey.containsKey(table))
            throw new SemanticException("La tabla '" + table + "' no tiene llave foranea");

        return primaryKey.get(table);
    }

    /**
     * @param name
     * @param type
     */
    public record PrimaryKey(String name, String type) {
    }


}
