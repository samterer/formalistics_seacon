package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class ComparisonExpressionNode implements ExpressionNode {

    private ExpressionNode leftArgument;
    private ExpressionNode rightArgument;
    private String conditionalOperator;

    public ComparisonExpressionNode(ExpressionNode leftArgument, String conditionalOperator) {
        this.leftArgument = leftArgument;
        this.conditionalOperator = conditionalOperator;
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
