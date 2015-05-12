package ph.com.gs3.formalistics.service.formula.node;

import ph.com.gs3.formalistics.service.formula.ParserException;

/**
 * Created by Ervinne on 5/3/2015.
 */
public interface ExpressionNode {

    enum NodeType {
        VARIABLE, CONSTANT, ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, COMPARISON, FUNCTION
    }

    NodeType getType();

    Object getValue() throws ParserException;

}
