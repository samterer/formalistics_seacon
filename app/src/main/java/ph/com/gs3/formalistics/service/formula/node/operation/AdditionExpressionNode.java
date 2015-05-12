package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.SequenceExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class AdditionExpressionNode extends SequenceExpressionNode {

    public AdditionExpressionNode() {
    }

    public AdditionExpressionNode(ExpressionNode expressionNode) {
        super(expressionNode);
    }

    @Override
    public NodeType getType() {
        return NodeType.ADDITION;
    }

    @Override
    public Object getValue() throws ParserException {
        double sum = 0.0;

        for (ExpressionNode node : expressionNodes) {
            try {
                sum += Double.parseDouble(node.getValue().toString());
            } catch (NumberFormatException e) {
                throw new ParserException("Cannot add value " + node.getValue().toString());
            }
        }

        return sum;
    }
}
