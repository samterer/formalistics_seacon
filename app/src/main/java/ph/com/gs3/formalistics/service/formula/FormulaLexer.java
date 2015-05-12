/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ph.com.gs3.formalistics.service.formula;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ph.com.gs3.formalistics.global.utilities.Serializer;

/**
 * @author Ervinne Sodusta
 */
public class FormulaLexer {

    private final List<TokenPattern> tokenPatterns;

    private final List<String> reservedKeywords;
    private final List<String> functions;

    public FormulaLexer() {

        reservedKeywords = new ArrayList<>();
        functions = new ArrayList<>();

        // TODO: add other reserved words here
        reservedKeywords.add("@Status");
        reservedKeywords.add("@TrackingNumber");

        reservedKeywords.add("@Today");
        reservedKeywords.add("@Now");
        reservedKeywords.add("@TimeStamp");

        functions.add("@Lookup");
        functions.add("@GivenIf");
        functions.add("@StrConcat");

        reservedKeywords.addAll(functions);

        tokenPatterns = new ArrayList<>();

        // initialize patterns recognizable
        tokenPatterns.add(new TokenPattern(Serializer.serializeList(functions, "|"), TokenType.FUNCTION));
        tokenPatterns.add(new TokenPattern("@[a-zA-Z0-9_]*", TokenType.VARIABLE));
        tokenPatterns.add(new TokenPattern("(?:\\d+\\.?|\\.\\d)\\d*(?:[Ee][-+]?\\d+)?", TokenType.NUMBER));
        tokenPatterns.add(new TokenPattern("[+-]", TokenType.ADD_SUB));
        tokenPatterns.add(new TokenPattern("[*/]", TokenType.MULT_DIV));
//        tokenPatterns.add(new TokenPattern("[\\\"|\\\'][a-zA-Z0-9,.`\\&: _]*[\\\"|\\\']", TokenType.STRING));
        tokenPatterns.add(new TokenPattern("\\\"[a-zA-Z0-9,'.`\\&: _]*\\\"|\\'[a-zA-Z0-9,.`\\&: _]*\\'", TokenType.STRING));
        tokenPatterns.add(new TokenPattern("\\(", TokenType.OPEN_PARENTHESIS));
        tokenPatterns.add(new TokenPattern("\\)", TokenType.CLOSE_PARENTHESIS));
        tokenPatterns.add(new TokenPattern("(<[=>]?|==|>=?)", TokenType.COMPARISON));
        tokenPatterns.add(new TokenPattern("\\&\\&|\\|\\|", TokenType.BOOLEAN_OPERATOR));
        tokenPatterns.add(new TokenPattern(",", TokenType.COMMA));

    }

    public List<String> lexVariables(String formula) {

        String localFormula = new String(formula);

        List<String> variables = new ArrayList<>();

        Pattern pattern = Pattern.compile("@[a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(localFormula);

        while (matcher.find()) {
            String match = matcher.group().trim();

            if (!reservedKeywords.contains(match)) {
                // substring(1) removes leading @
                variables.add(match.substring(1));
            }
        }

        return variables;

    }

    public LinkedList<Token> lex(String formula) throws ParserException {

        String localFormula = new String(formula);

        LinkedList<Token> tokens = new LinkedList<>();

        // Begin matching tokens
//        localFormula = localFormula.replace(" ", "");
        localFormula = localFormula.trim();
        while (!localFormula.equals("")) {
            boolean match = false;

            for (TokenPattern tokenPattern : tokenPatterns) {
                Matcher matcher = tokenPattern.regex.matcher(localFormula);
                if (matcher.find()) {
                    match = true;

                    String matchedSequence = matcher.group().trim();
                    tokens.add(new Token(tokenPattern.tokenType, matchedSequence));

                    localFormula = matcher.replaceFirst("").trim();
                    break;
                }
            }

            if (!match) {
                throw new ParserException("Unexpected character in input: " + localFormula);
            }

        }

        return tokens;

    }

    private class TokenPattern {

        final Pattern regex;
        final TokenType tokenType;

        TokenPattern(String patternString, TokenType tokenType) {
            this.regex = Pattern.compile("^(" + patternString + ")");
            this.tokenType = tokenType;
        }

    }

}
