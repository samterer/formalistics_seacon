package ph.com.gs3.formalistics.service.formula.node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ervinne on 5/3/2015.
 */
public abstract class SequenceExpressionNode implements ExpressionNode {

    protected final LinkedList<ExpressionNode> expressionNodes;

    public SequenceExpressionNode() {
        this.expressionNodes = new LinkedList<>();
    }

    public SequenceExpressionNode(ExpressionNode expressionNode) {
        this.expressionNodes = new LinkedList<>();
        this.expressionNodes.add(expressionNode);
    }

    public void add(ExpressionNode expressionNode) {
        expressionNodes.add(expressionNode);
    }

    public void addAll(List<ExpressionNode> expressionNodeList) {
        expressionNodes.addAll(expressionNodeList);
    }

}
