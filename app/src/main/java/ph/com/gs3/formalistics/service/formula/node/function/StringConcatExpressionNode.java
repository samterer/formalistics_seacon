package ph.com.gs3.formalistics.service.formula.node.function;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.SequenceExpressionNode;

/**
 * Created by Ervinne on 5/4/2015.
 */
public class StringConcatExpressionNode extends SequenceExpressionNode {

    @Override
    public NodeType getType() {
        return NodeType.FUNCTION;
    }

    @Override
    public Object getValue() throws ParserException {

        String value = "";

        for (ExpressionNode expressionNode : expressionNodes) {
            value += expressionNode.getValue();
        }

        return value;
    }
}