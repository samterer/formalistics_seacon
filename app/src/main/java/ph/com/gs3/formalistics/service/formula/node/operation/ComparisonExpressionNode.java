package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.global.constants.FormulaEvalutationType;
import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class ComparisonExpressionNode implements ExpressionNode {

    private final ExpressionNode leftArgument;
    private ExpressionNode rightArgument;
    private final String conditionalOperator;

    private FormulaEvalutationType formulaEvalutationType;

    public ComparisonExpressionNode(ExpressionNode leftArgument, String conditionalOperator, FormulaEvalutationType formulaEvalutationType) {
        this.leftArgument = leftArgument;
        this.conditionalOperator = conditionalOperator;
        this.formulaEvalutationType = formulaEvalutationType;
    }

    public ComparisonExpressionNode(ExpressionNode leftArgument, String conditionalOperator) {
        this.leftArgument = leftArgument;
        this.conditionalOperator = conditionalOperator;
        this.formulaEvalutationType = FormulaEvalutationType.VALUE;
    }

    public void setRightArgument(ExpressionNode rightArgument) {
        this.rightArgument = rightArgument;
    }

    @Override
    public NodeType getType() {
        return NodeType.COMPARISON;
    }

    @Override
    public Object getValue() throws ParserException {

        if (formulaEvalutationType == FormulaEvalutationType.VALUE) {
            return evaluateForBooleanValue();
        } else {
            return evaluateForConditionClause();
        }

    }

    private String evaluateForConditionClause() throws ParserException {
        String checkedConditionalOperator;

        if ("==".equals(conditionalOperator)) {
            checkedConditionalOperator = "=";
        } else {
            checkedConditionalOperator = conditionalOperator;
        }

        return leftArgument.getValue() + checkedConditionalOperator + rightArgument.getValue();
    }

    private Boolean evaluateForBooleanValue() throws ParserException {
        Boolean result = null;

        if ("==".equals(conditionalOperator)) {
            result = leftArgument.getValue().equals(rightArgument.getValue());
        } else if ("!=".equals(conditionalOperator)) {
            result = !leftArgument.getValue().equals(rightArgument.getValue());
        } else if (">=".equals(conditionalOperator) || "<=".equals(conditionalOperator) || ">".equals(conditionalOperator) || "<".equals(conditionalOperator)) {

            try {
                double leftArgumentValue = (double) leftArgument.getValue();
                double rightArgumentValue = (double) rightArgument.getValue();

                if (">=".equals(conditionalOperator)) {
                    result = leftArgumentValue >= rightArgumentValue;
                } else if ("<=".equals(conditionalOperator)) {
                    result = leftArgumentValue <= rightArgumentValue;
                } else if (">".equals(conditionalOperator)) {
                    result = leftArgumentValue > rightArgumentValue;
                } else if ("<".equals(conditionalOperator)) {
                    result = leftArgumentValue < rightArgumentValue;
                }

            } catch (NumberFormatException e) {
                throw new ParserException("Cannot use operators >=, <=, >, and < on non numeric values.");
            }

        } else {
            throw new ParserException("Invalid conditional operation " + conditionalOperator);
        }

        return result;
    }

}
