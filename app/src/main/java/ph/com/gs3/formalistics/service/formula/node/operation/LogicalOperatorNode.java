package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.global.constants.FormulaEvalutationType;
import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;

/**
 * Created by Ervinne Sodusta on 7/15/2015.
 */
public class LogicalOperatorNode implements ExpressionNode {

    private final ExpressionNode leftArgument;
    private ExpressionNode rightArgument;
    private final String operator;

    private FormulaEvalutationType formulaEvalutationType;

    public LogicalOperatorNode(ExpressionNode leftArgument, String operator, FormulaEvalutationType formulaEvalutationType) {
        this.leftArgument = leftArgument;
        this.operator = operator;
        this.formulaEvalutationType = formulaEvalutationType;
    }

    public LogicalOperatorNode(ExpressionNode leftArgument, String operator) {
        this.leftArgument = leftArgument;
        this.operator = operator;
        this.formulaEvalutationType = FormulaEvalutationType.VALUE;
    }

    public void setRightArgument(ExpressionNode rightArgument) {
        this.rightArgument = rightArgument;
    }

    @Override
    public NodeType getType() {
        return NodeType.LOGICAL_OPERATION;
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

        if ("||".equals(operator)) {
            checkedConditionalOperator = " OR ";
        } else if ("&&".equals(operator)) {
            checkedConditionalOperator = " AND ";
        } else {
            checkedConditionalOperator = operator;
        }

        return leftArgument.getValue() + checkedConditionalOperator + rightArgument.getValue();
    }

    private Boolean evaluateForBooleanValue() throws ParserException {
        Boolean result = null;

        if ("||".equals(operator)) {
            result = ((Boolean) leftArgument.getValue()) || ((Boolean) rightArgument.getValue());
        } else if ("&&".equals(operator)) {
            result = ((Boolean) leftArgument.getValue()) && ((Boolean) rightArgument.getValue());
        } else {
            throw new ParserException("Invalid conditional operation " + operator);
        }

        return result;
    }


}
