package org.example.plantsql.generator;

public class TableAttribute {
    private String name;
    private String dataType; //  integer|float|string|datetime|timestamp|date
    private String modifier; // FK|unique|generated
    private boolean primaryKey;
    private boolean foreignKey;
    private boolean mandatory;

    public TableAttribute(String name, String dataType, String modifier, boolean primaryKey, boolean mandatory) {
        this.name = name;
        this.dataType = dataType;
        this.modifier = modifier;
        this.primaryKey = primaryKey;
        this.mandatory = mandatory;

        this.foreignKey = modifier.equals("FK");
    }

    public String getCode() {
        StringBuilder code = new StringBuilder();

        // Nombre del campo
        code.append(name).append(" ");

        // Tipo de dato (con mapeo a tipos SQL)
        switch(dataType.toLowerCase()) {
            case "integer":
                code.append("INT");
                break;
            case "float":
                code.append("FLOAT");
                break;
            case "string":
                code.append("VARCHAR(255)"); // Longitud por defecto para strings
                break;
            case "datetime":
                code.append("DATETIME");
                break;
            case "timestamp":
                code.append("TIMESTAMP");
                break;
            case "date":
                code.append("DATE");
                break;
            default:
                code.append(dataType.toUpperCase()); // Por si acaso
        }

        // Modificadores
        if (modifier != null && !modifier.isEmpty()) {
            switch(modifier.toUpperCase()) {
                case "FK":
                    foreignKey = true;
                    break;
                case "UNIQUE":
                    code.append(" UNIQUE");
                    break;
                case "GENERATED":
                    code.append(" AUTO_INCREMENT");
                    break;
            }
        }

        // Restricciones
    //    if (primaryKey) {
      //      code.append(" PRIMARY KEY");
       // }

        if (mandatory) {
            code.append(" NOT NULL");
        }

        return code.toString();
    }

    public String getName() {
        return name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getModifier() {
        return modifier;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isForeignKey() {
        return foreignKey;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    @Override
    public String toString() {
        return "TableAttribute{" +
                "name='" + name + '\'' +
                ", dataType='" + dataType + '\'' +
                ", modifier='" + modifier + '\'' +
                ", primaryKey=" + primaryKey +
                ", foreignKey=" + foreignKey +
                ", mandatory=" + mandatory +
                '}';
    }
}
