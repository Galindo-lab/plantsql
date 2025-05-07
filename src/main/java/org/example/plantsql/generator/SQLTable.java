package org.example.plantsql.generator;

import org.example.plantsql.SymbolTable;
import org.example.plantsql.exeptions.SemanticException;

import java.util.ArrayList;

public class SQLTable {
    private String name;
    private ArrayList<TableAttribute> attributes;
    private SymbolTable symbolTable;

    public SQLTable(String name, SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.name = name;
        this.attributes = new ArrayList<>();
    }

    public String getCode() throws SemanticException {
        StringBuilder code = new StringBuilder("CREATE TABLE " + name + "(\n");

        for(TableAttribute tableAttribute : attributes) {

            if (tableAttribute.isForeignKey()) {
                SymbolTable.PrimaryKey foo = symbolTable.getPrimaryKey(tableAttribute.getDataType());

                String bar = tableAttribute.getDataType();
                tableAttribute.setDataType(foo.type());
                code.append("  ").append(tableAttribute.getCode()).append(", \n");
                tableAttribute.setDataType(bar);
            }
            else {
                code.append("  ").append(tableAttribute.getCode()).append(", \n");
            }
        }

        code.append("\n");

        for(TableAttribute tableAttribute : attributes) {
            if (tableAttribute.isPrimaryKey()) {
                code.append("  PRIMARY KEY (").append(tableAttribute.getName()).append(")");
            }
        }

        for(TableAttribute tableAttribute : attributes) {

            if (tableAttribute.isForeignKey()) {
                code.append(", \n");
                SymbolTable.PrimaryKey foo = symbolTable.getPrimaryKey(tableAttribute.getDataType());

                code.append("  FOREIGN KEY (").append(tableAttribute.getName()).append(") REFERENCES " + tableAttribute.getDataType() + "(" + foo.name() + ")");
            }
        }



        code.append("\n);\n");

        return code.toString();
    }

    public void createAttribute(String name, String dataType, String modifier, boolean primaryKey, boolean mandatory) {
        attributes.add(new TableAttribute(name, dataType, modifier, primaryKey, mandatory));
    }

}