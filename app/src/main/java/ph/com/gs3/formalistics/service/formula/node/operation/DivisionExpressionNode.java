package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.SequenceExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class DivisionExpressionNode extends SequenceExpressionNode {

    public DivisionExpressionNode() {
    }

    public DivisionExpressionNode(ExpressionNode expressionNode) {
        super(expressionNode);
    }

    @Override
    public NodeType getType() {
        return NodeType.DIVISION;
    }

    @Override
    public Object getValue() throws ParserException {

        double quotient;

        String lastValueBeingEvalutated = null;
        try {
            lastValueBeingEvalutated = expressionNodes.getFirst().getValue().toString();
            quotient = Double.parseDouble(lastValueBeingEvalutated);

            for (int i = 1; i < expressionNodes.size(); i++) {
                lastValueBeingEvalutated = expressionNodes.get(i).getValue().toString();
                quotient /= Double.parseDouble(lastValueBeingEvalutated);
            }
        } catch (NumberFormatException e) {
            throw new ParserException("Cannot divide with " + lastValueBeingEvalutated);
        } catch (ArithmeticException e) {
            throw new ParserException("Cannot divide with " + lastValueBeingEvalutated + ": " + e.getMessage(), e);
        }

        return quotient;
    }
}
