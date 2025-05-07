package org.example.plantsql;

import org.example.plantsql.core.Token;
import org.example.plantsql.core.TokenType;
import org.example.plantsql.exeptions.LexicalException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.plantsql.core.TokenType.*;

public class Lexer {
    private final ArrayList<Token> tokens = new ArrayList<>();
    private final Pattern pattern;

    public Lexer() {
        LinkedHashMap<TokenType, String> types = new LinkedHashMap<>();

        types.put(STARTUML, "@startuml");
        types.put(ENDUML, "@enduml");
        types.put(ENTITY, "entity");
        types.put(STRING, "\".*\"");
        types.put(AS, "as");
        types.put(MODIFIER, "FK|unique|generated");
        types.put(TYPE, "integer|float|string|datetime|timestamp|date");
        types.put(RELATION, "[|}](?:[o|]*-[o|-]*)+[{|][|}]?");
        types.put(LBRACKET, "\\{");
        types.put(RBRACKET, "\\}");
        types.put(BOLD, "\\*\\*");
        types.put(MANDATORY, "\\*");
        types.put(COLON, ":");
        types.put(LGUILLEMET, "<<");
        types.put(RGUILLEMET, ">>");
        types.put(COMMA, ",");
        types.put(HLINE, "-{2,4}");
        types.put(COMMENT, "\\'(.*?)(?=\\n)");
        types.put(IDENTIFIER, "\\S+?(?=\\s|\\*\\*)"); // ignora ** al final de los identificadores
        types.put(SPACE, "[ \\t\\r\\n]+");
        types.put(ERROR, "[^ \\t\\r\\n]+");

        pattern = buildPattern(types);
    }

    /**
     * Prints all tokens from a Lexer instance.
     *
     * @param lexer The Lexer instance containing tokens to print
     */
    public static void printTokens(Lexer lexer) {
        System.out.println("*** Lexer Tokens ***");
        for (Token token : lexer.getTokens())
            System.out.println(token);
        System.out.println("********************");
    }

    /**
     * Builds combined regex pattern from all token types.
     *
     * @param types Map of token types and their patterns
     * @return Compiled Pattern object
     */
    private static Pattern buildPattern(LinkedHashMap<TokenType, String> types) {
        StringBuilder combinedPattern = new StringBuilder();

        types.forEach((key, pattern) ->
                combinedPattern.append(String.format("|(?<%s>%s)", key, pattern))
        );

        // delete '|' at the beginning of the pattern
        return Pattern.compile(combinedPattern.substring(1));
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    /**
     * Analyzes input text and generates token list.
     *
     * @param input Text to analyze
     * @throws LexicalException When encountering unrecognized text
     */
    public void analyze(String input) throws LexicalException {
        Matcher matcher = this.pattern.matcher(input);

        while (matcher.find()) {
            for (TokenType type : values()) {
                String groupName = type.name();
                String matchedValue = matcher.group(groupName);

                // Skips match if doesn't find token type
                if (matchedValue == null)
                    continue;

                // skips
                if (groupName.equals(SPACE.name())
                        || groupName.equals(BOLD.name())
                        || groupName.equals(COMMENT.name()))
                    continue;

                // throw exception if ERROR is found
                if (groupName.equals(ERROR.name()))
                    throw new LexicalException(matchedValue);

                // Remove double quotes from the string
                if (groupName.equals(STRING.name()))
                    matchedValue = matchedValue.substring(1, matchedValue.length() - 1);

                tokens.add(new Token(type, matchedValue));
            }
        }
    }
}