package ph.com.gs3.formalistics.service.formula.node;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class VariableExpressionNode implements ExpressionNode {

    private final String name;
    private String value;
    private boolean isValueSet;

    public VariableExpressionNode(String name) {
        this.name = name;
        isValueSet = false;
    }

    public void setValue(String value) {
        this.value = value;
        isValueSet = true;
    }

    public String getName() {
        return name;
    }

    @Override
    public NodeType getType() {
        return NodeType.VARIABLE;
    }

    @Override
    public Object getValue() {
//        if (isValueSet) {
//            return value;
//        } else {
//            throw new ParserException("Undefined variable " + name);
//        }
        return value;
    }
}
