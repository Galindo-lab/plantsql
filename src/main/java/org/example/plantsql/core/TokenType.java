package org.example.plantsql.core;

public enum TokenType {
    STARTUML,
    ENDUML,
    COMMENT,
    ENTITY,
    STRING,
    AS,
    IDENTIFIER,
    LBRACKET,
    RBRACKET,
    BOLD, // ** asteriscos
    MANDATORY, // asterisco solo
    COLON,
    TYPE,
    LGUILLEMET, // <<
    RGUILLEMET, // >>
    MODIFIER,
    COMMA,
    HLINE, // -- --- o ----
    RELATION,
    ERROR,
    SPACE,
    LPAREN,
    RPAREN,
}
