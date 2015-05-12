package ph.com.gs3.formalistics.service.formula.node.operation;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.SequenceExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class SubtractionExpressionNode extends SequenceExpressionNode {

    public SubtractionExpressionNode() {
    }

    public SubtractionExpressionNode(ExpressionNode expressionNode) {
        super(expressionNode);
    }

    @Override
    public NodeType getType() {
        return NodeType.SUBTRACTION;
    }

    @Override
    public Object getValue() throws ParserException {
        double difference = 0.0;

        String lastValueBeingEvaluated = null;

        try {
            int startIndex = 0;
            if (expressionNodes.size() > 1) {
                lastValueBeingEvaluated = expressionNodes.getFirst().getValue().toString();
                difference = Double.parseDouble(lastValueBeingEvaluated);
                startIndex = 1;
            }

            for (int i = startIndex; i < expressionNodes.size(); i++) {
                lastValueBeingEvaluated = expressionNodes.get(i).getValue().toString();
                difference -= Double.parseDouble(lastValueBeingEvaluated);
            }
        } catch (NumberFormatException e) {
            throw new ParserException("Cannot subtract with value " + lastValueBeingEvaluated);
        }

        return difference;
    }
}
