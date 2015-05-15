package ph.com.gs3.formalistics.service.formula.node.variables;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;

/**
 * Created by Ervinne on 5/14/2015.
 */
public class CurrentFormVariableExpressionNode implements ExpressionNode {

    private String value;

    private ExpressionNode variableNameArgument;

    public CurrentFormVariableExpressionNode(ExpressionNode variableNameArgument) {
        this.variableNameArgument = variableNameArgument;
    }

    public String getName() throws ParserException {
        return variableNameArgument.getValue().toString();
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public NodeType getType() {
        return NodeType.VARIABLE;
    }

    @Override
    public Object getValue() throws ParserException {
        return value;
    }
}
