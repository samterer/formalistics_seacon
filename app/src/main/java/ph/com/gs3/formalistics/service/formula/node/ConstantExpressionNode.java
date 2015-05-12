package ph.com.gs3.formalistics.service.formula.node;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class ConstantExpressionNode implements ExpressionNode {

    private final String value;

    public ConstantExpressionNode(String value) {
        this.value = value;
    }

    @Override
    public NodeType getType() {
        return NodeType.CONSTANT;
    }

    @Override
    public Object getValue() {
        return value;
    }
}
