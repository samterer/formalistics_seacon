package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.SequenceExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class MultiplicationExpressionNode extends SequenceExpressionNode {

    public MultiplicationExpressionNode() {
    }

    public MultiplicationExpressionNode(ExpressionNode expressionNode) {
        super(expressionNode);
    }

    @Override
    public NodeType getType() {
        return NodeType.MULTIPLICATION;
    }

    @Override
    public Object getValue() throws ParserException {
        double product = 1.0;

        for (ExpressionNode node : expressionNodes) {
            try {
                product *= Double.parseDouble(node.getValue().toString());
            } catch (NumberFormatException e) {
                throw new ParserException("Cannot multiply " + node.getValue());
            }
        }

        return product;
    }
}
