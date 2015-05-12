package ph.com.gs3.formalistics.service.formula;

import org.json.JSONObject;

import java.util.LinkedList;

import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;

/**
 * Quick & Dirty implementation of the formula parser
 *
 * @author Ervinne Sodusta
 */
public class FormulaParser {

    public static final String TAG = FormulaParser.class.getSimpleName();

    private final LinkedList<Token> tokens;
    private FormulaVariableParser formulaVariableParser;

    public FormulaParser(LinkedList<Token> tokens, DocumentHeaderData documentHeaderData, JSONObject fieldValues) throws ParserException {
        this.tokens = (LinkedList<Token>) tokens.clone();

        formulaVariableParser = new FormulaVariableParser(documentHeaderData, fieldValues);
    }

    /**
     * Will parse a given formula to create a conditional/where clause that can be used for sqlite queries.
     * constants, variables, and conditional operators are treated as terminals, all other expressions are non terminals
     *
     * @return
     * @throws ParserException
     */
    public String parseForSQLiteWhereClause() throws ParserException {

        String parsedWhereClause = "";
        boolean expectingOperator = false;

        for (Token token : tokens) {
            // values
            if (!expectingOperator && token.type == TokenType.NUMBER) {
                parsedWhereClause += token.data;
            } else if (!expectingOperator && token.type == TokenType.STRING) {
                parsedWhereClause += "'" + formulaVariableParser.getRawStringFromTokenData(token.data) + "'";
            } else if (!expectingOperator && token.type == TokenType.VARIABLE) {
                // throws ParserException
                parsedWhereClause += "'" + formulaVariableParser.getStringVariableValue(token.data) + "'";
            } else if (!expectingOperator && token.type == TokenType.FUNCTION) {
                throw new ParserException("Functions not supported yet");
            }

            // operators
            else if (expectingOperator && token.type == TokenType.COMPARISON) {
                parsedWhereClause += " " + token.data + " ";
            } else if (expectingOperator && token.type == TokenType.BOOLEAN_OPERATOR) {
                if ("&&".equals(token.data)) {
                    parsedWhereClause += " AND ";
                } else if ("||".equals(token.data)) {
                    parsedWhereClause += " OR ";
                }
            } else {
                throw new ParserException("Unexpected token " + token.data);
            }

            expectingOperator = !expectingOperator;
        }

        return parsedWhereClause;

    }

}
