package ph.com.gs3.formalistics.service.formula;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.FormulaEvalutationType;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.service.formula.node.ConstantExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.SequenceExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.VariableExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.function.GivenIfExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.function.LookupExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.function.LookupRequestListener;
import ph.com.gs3.formalistics.service.formula.node.function.StringConcatExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.operation.AdditionExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.operation.ComparisonExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.operation.DivisionExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.operation.LogicalOperatorNode;
import ph.com.gs3.formalistics.service.formula.node.operation.MultiplicationExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.operation.SubtractionExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.variables.CurrentFormVariableExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class FormulaEvaluator {

    public static final String TAG = FormulaEvaluator.class.getSimpleName();

    protected LinkedList<Token> tokens;
    protected Token lookAhead;

    protected FormulaVariableParser formulaVariableParser;

    protected final DocumentHeaderData documentHeaderData;
    protected final JSONObject fieldValues;

    protected LookupRequestListener lookupRequestListener;

    protected FormulaEvalutationType formulaEvalutationType = FormulaEvalutationType.VALUE;

    public FormulaEvaluator(DocumentHeaderData documentHeaderData, JSONObject fieldValues) {
        this.documentHeaderData = documentHeaderData;
        this.fieldValues = fieldValues;
    }

    public FormulaEvaluator(DocumentHeaderData documentHeaderData, JSONObject fieldValues, LookupRequestListener lookupRequestListener) {
        this.documentHeaderData = documentHeaderData;
        this.fieldValues = fieldValues;
        this.lookupRequestListener = lookupRequestListener;
    }

    public ExpressionNode evaluate(LinkedList<Token> tokens) throws ParserException {
        this.tokens = (LinkedList<Token>) tokens.clone();
        this.tokens.add(new Token(TokenType.TERMINAL, null));
        this.lookAhead = tokens.getFirst();

        formulaVariableParser = new FormulaVariableParser(documentHeaderData, fieldValues);

        // top level non-terminal is evaluateExpression
        ExpressionNode expr = evaluateExpression();

        // Non Terminal not expected
        if (lookAhead.type != TokenType.TERMINAL) {
            throw new ParserException("Unexpected symbol " + lookAhead.data + " found");
        }

        return expr;
    }

    public ExpressionNode evaluateForCondition(LinkedList<Token> tokens) throws ParserException {
        formulaEvalutationType = FormulaEvalutationType.CONDITION_CLAUSE;
        return evaluate(tokens);
    }

    /**
     * BNF:
     * expression	::=	expression + term
     * |	expression - term
     * |	term
     *
     * @return
     * @throws ParserException
     */
    protected ExpressionNode evaluateExpression() throws ParserException {
        ExpressionNode term = evaluateSignedTerm();
        return evaluateAddSubOperation(term);
    }

    /**
     * BNF:
     * signed_term	::= + term
     * |	- term
     * |	term
     *
     * @return
     * @throws ParserException
     */
    protected ExpressionNode evaluateSignedTerm() throws ParserException {

        // Check if the value is positive or negative
        if (lookAhead.type == TokenType.ADD_SUB) {

            boolean valueIsPositive = "+".equals(lookAhead.data);

            nextToken();
            ExpressionNode term = evaluateTerm();

            if (valueIsPositive) {
                return term;
            } else {
                // return a negative value of the evaluateTerm
                return new SubtractionExpressionNode(term);
            }
        }

        return evaluateTerm();

    }

    /**
     * BNF:
     * term	    ::= + term
     * |	- term
     * |	term
     *
     * @return
     * @throws ParserException
     */
    protected ExpressionNode evaluateTerm() throws ParserException {
        ExpressionNode argumentExp = evaluateArgument();
        ExpressionNode possibleConditionNode = evaluateCondition(argumentExp);
        return evaluateMultDivOperation(possibleConditionNode);
    }

    protected ExpressionNode evaluateAddSubOperation(ExpressionNode expressionNode) throws ParserException {

        if (lookAhead.type == TokenType.ADD_SUB) {

            SequenceExpressionNode operationNode;

            if ("+".equals(lookAhead.data)) {
                operationNode = new AdditionExpressionNode(expressionNode);
            } else {
                operationNode = new SubtractionExpressionNode(expressionNode);
            }

            nextToken();
            ExpressionNode nextExpression = evaluateTerm();
            operationNode.add(nextExpression);

            return evaluateAddSubOperation(operationNode);
        }


        return expressionNode;

    }

    protected ExpressionNode evaluateMultDivOperation(ExpressionNode expressionNode) throws ParserException {

        if (lookAhead.type == TokenType.MULT_DIV) {

            SequenceExpressionNode operationNode;

            if ("*".equals(lookAhead.data)) {
                operationNode = new MultiplicationExpressionNode(expressionNode);
            } else {
                operationNode = new DivisionExpressionNode(expressionNode);
            }

            nextToken();
            ExpressionNode nextExpression = evaluateSignedTerm();
            operationNode.add(nextExpression);

            return evaluateMultDivOperation(operationNode);
        }

        return expressionNode;

    }

    protected ExpressionNode evaluateCondition(ExpressionNode expressionNode) throws ParserException {

        if (lookAhead.type == TokenType.COMPARISON) {

            ComparisonExpressionNode comparison = new ComparisonExpressionNode(expressionNode, lookAhead.data, formulaEvalutationType);

            nextToken();
            ExpressionNode nextExpression = evaluateSignedTerm();
            comparison.setRightArgument(nextExpression);

            return evaluateLogicalOperation(comparison);
        }

        return expressionNode;

    }

    protected ExpressionNode evaluateLogicalOperation(ExpressionNode expressionNode) throws ParserException {

        if (lookAhead.type == TokenType.BOOLEAN_OPERATOR) {

            LogicalOperatorNode logicalOperatorNode = new LogicalOperatorNode(expressionNode, lookAhead.data, formulaEvalutationType);

            nextToken();
            ExpressionNode nextExpression = evaluateSignedTerm();
            logicalOperatorNode.setRightArgument(nextExpression);

            return evaluateLogicalOperation(logicalOperatorNode);

        }

        return expressionNode;

    }

    protected ExpressionNode evaluateLookup() throws ParserException {

        if (lookupRequestListener == null) {
            throw new ParserException("The evaluator does not have a handler for lookups");
        }

        List<ExpressionNode.NodeType> argumentTypes = new ArrayList<>();
        argumentTypes.add(ExpressionNode.NodeType.CONSTANT);
        argumentTypes.add(ExpressionNode.NodeType.CONSTANT);
        argumentTypes.add(ExpressionNode.NodeType.CONSTANT);
        argumentTypes.add(ExpressionNode.NodeType.CONSTANT);

        LookupExpressionNode lookupExpressionNode = new LookupExpressionNode(lookupRequestListener);

        // expected '('
        nextToken();
        List<ExpressionNode> arguments = evaluateArguments(argumentTypes);
        lookupExpressionNode.setFormNameExpression(arguments.get(0));
        lookupExpressionNode.setReturnFieldNameExpression(arguments.get(1));
        lookupExpressionNode.setCompareToOtherFormFieldNameExpression(arguments.get(2));
        lookupExpressionNode.setCompareToThisFormFieldValueExpression(arguments.get(3));

        return lookupExpressionNode;

    }

    protected ExpressionNode evaluateStringConcatination() throws ParserException {

        StringConcatExpressionNode strConcat = new StringConcatExpressionNode();

        // expected '('
        nextToken();
        List<ExpressionNode> arguments = evaluateVariableCountArgument();
        strConcat.addAll(arguments);

        return strConcat;

    }

    protected ExpressionNode evaluateGivenIf() throws ParserException {

        List<ExpressionNode.NodeType> argumentTypes = new ArrayList<>();
        argumentTypes.add(ExpressionNode.NodeType.COMPARISON);
        argumentTypes.add(ExpressionNode.NodeType.EXPRESSION);
        argumentTypes.add(ExpressionNode.NodeType.EXPRESSION);

        GivenIfExpressionNode givenIfExpressionNode = new GivenIfExpressionNode();

        // expected '('
        nextToken();
        List<ExpressionNode> arguments = evaluateArguments(argumentTypes);
        givenIfExpressionNode.setCondition(arguments.get(0));
        givenIfExpressionNode.setResultIfTrue(arguments.get(1));
        givenIfExpressionNode.setResultIfFalse(arguments.get(2));

        return givenIfExpressionNode;
    }

    protected ExpressionNode evaluateCurrentFormVariableContainer() throws ParserException {

        // pop @CurrentForm
        nextToken();

        // expected '['
        nextToken();
        ExpressionNode argumentExpressionNode = evaluateExpression();
        // expected ']'
        nextToken();

        CurrentFormVariableExpressionNode expressionNode = new CurrentFormVariableExpressionNode(argumentExpressionNode);

        return expressionNode;

    }

    protected void popComma() throws ParserException {
        if (lookAhead.type != TokenType.COMMA) {
            throw new ParserException("Expected ',' instead of " + lookAhead.data);
        }

        nextToken();
    }

    /**
     * handles the non-terminal evaluateArgument
     */
    protected ExpressionNode evaluateArgument() throws ParserException {

        if (lookAhead.type == TokenType.FUNCTION) {

            // TODO: move these if statements to a factory class that generates functions

            if ("@GivenIf".equalsIgnoreCase(lookAhead.data)) {
                return evaluateGivenIf();
            } else if ("@Lookup".equalsIgnoreCase(lookAhead.data)) {
                return evaluateLookup();
            } else if ("@StrConcat".equalsIgnoreCase(lookAhead.data)) {
                return evaluateStringConcatination();
            }

            return null;
        } else if (lookAhead.type == TokenType.OPEN_PARENTHESIS) {
            nextToken();
            ExpressionNode exp = evaluateExpression();
            if (lookAhead.type != TokenType.CLOSE_PARENTHESIS) {
                throw new ParserException("Closing brackets expected instead of " + lookAhead.data);
            }
            nextToken();
            return exp;
        }

        return evaluateValue();
    }

    protected List<ExpressionNode> evaluateArguments(List<ExpressionNode.NodeType> argumentTypes) throws ParserException {

        if (lookAhead.type == TokenType.OPEN_PARENTHESIS) {
            // pop '('
            nextToken();

            List<ExpressionNode> arguments = new ArrayList<>();

            int argumentCount = argumentTypes.size();

            for (int i = 0; i < argumentCount; i++) {
                ExpressionNode exp = evaluateExpression();
                if (argumentTypes.get(i) != ExpressionNode.NodeType.EXPRESSION && exp.getType() != argumentTypes.get(i) && exp.getType() != ExpressionNode.NodeType.VARIABLE) {
                    throw new ParserException("Argument type " + exp.getType().name() + " is not applicable for type " + argumentTypes.get(i).name());
                }
                arguments.add(exp);

                if (i < argumentCount - 1) {
                    // expected ','
                    popComma();
                }
            }

            if (lookAhead.type != TokenType.CLOSE_PARENTHESIS) {
                throw new ParserException("Expected ')' instead of " + lookAhead.data);
            }

            // pop ')'
            nextToken();
            return arguments;
        } else {
            throw new ParserException("Expected '(' instead of " + lookAhead.data);
        }

    }

    protected List<ExpressionNode> evaluateVariableCountArgument() throws ParserException {

        if (lookAhead.type == TokenType.OPEN_PARENTHESIS) {
            // pop '('
            nextToken();

            List<ExpressionNode> arguments = new ArrayList<>();

            do {
                ExpressionNode argumentExpression = evaluateExpression();
                arguments.add(argumentExpression);

                if (lookAhead.type == TokenType.COMMA) {
                    popComma();
                } else if (lookAhead.type != TokenType.CLOSE_PARENTHESIS) {
                    // if the token ahead is not a comma and not a ')', this is an unexpected token
                    throw new ParserException("Expected token ')', found" + lookAhead.data);
                }
            } while (lookAhead.type != TokenType.CLOSE_PARENTHESIS);

            // pop ')'
            nextToken();
            return arguments;
        } else {
            throw new ParserException("Expected '(' instead of " + lookAhead.data);
        }

    }

    public ExpressionNode evaluateValue() throws ParserException {
        if (lookAhead.type == TokenType.NUMBER) {
            ExpressionNode exp = new ConstantExpressionNode(lookAhead.data);
            nextToken();
            return exp;
        }

        if (lookAhead.type == TokenType.STRING) {
            String constant = formulaVariableParser.getRawStringFromTokenData(lookAhead.data);
            if (formulaEvalutationType == FormulaEvalutationType.CONDITION_CLAUSE) {
                constant = "'" + constant + "'";
            }
            ExpressionNode exp = new ConstantExpressionNode(constant);
            nextToken();
            return exp;
        }

        if (lookAhead.type == TokenType.VARIABLE) {
            VariableExpressionNode variable = new VariableExpressionNode(lookAhead.data);

            if (formulaEvalutationType == FormulaEvalutationType.CONDITION_CLAUSE) {
                variable.setValue(variable.getName().substring(1)); // remove @
            } else {
                //  throws ParserException
                variable.setValue(formulaVariableParser.getStringVariableValue(variable.getName()));
            }

            nextToken();
            return variable;
        }

        if (lookAhead.type == TokenType.VARIABLE_CONTAINER) {

            // pop @CurrentForm
            nextToken();

            // expected '['
            nextToken();
            ExpressionNode argumentExpressionNode = evaluateExpression();
            // expected ']'
            nextToken();

            CurrentFormVariableExpressionNode variable = new CurrentFormVariableExpressionNode(argumentExpressionNode);
            String variableName = variable.getName();

            if (formulaEvalutationType == FormulaEvalutationType.CONDITION_CLAUSE) {
                variableName = "@" + formulaVariableParser.getRawStringFromTokenData(variableName);
                variable.setValue("'" + formulaVariableParser.getStringVariableValue(variableName) + "'");
            } else {
                variable.setValue(formulaVariableParser.getStringVariableValue(variableName));
            }


            return variable;

        }

        if (lookAhead == null) {
            throw new ParserException("Unexpected end of input");
        } else {
            throw new ParserException("Unexpected symbol " + lookAhead.data + "  found");
        }
    }

    protected boolean nextToken() {
        tokens.pop();
        boolean hasNext = !tokens.isEmpty();

        if (hasNext) {
            lookAhead = tokens.getFirst();
        } else {
            lookAhead = null; // Replace this later with an epsilon token
        }

        return hasNext;
    }
}
