package ph.com.gs3.formalistics.service.formula.node.function;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class GivenIfExpressionNode implements ExpressionNode {

    //@GivenIf((@Status == '') , @StrConcat('New Request') , @StrConcat('Old Request'))

    private ExpressionNode condition;
    private ExpressionNode resultIfTrue;
    private ExpressionNode resultIfFalse;

    public void setCondition(ExpressionNode condition) {
        this.condition = condition;
    }

    public void setResultIfTrue(ExpressionNode resultIfTrue) {
        this.resultIfTrue = resultIfTrue;
    }

    public void setResultIfFalse(ExpressionNode resultIfFalse) {
        this.resultIfFalse = resultIfFalse;
    }

    @Override
    public NodeType getType() {
        return NodeType.FUNCTION;
    }

    @Override
    public Object getValue() throws ParserException {

        // if the condition is a boolean value true or is a non null value, return the result of
        // resultIfTrue expression, otherwise, return the value of resultIfFalse

        Object rawConditionValue = condition.getValue();

        FLLogger.d("GivenIfExpressionNode", "rawConditionValue = " + rawConditionValue.toString());

        if (Boolean.FALSE.equals(rawConditionValue) || rawConditionValue == null) {
            FLLogger.d("GivenIfExpressionNode", "false");
            return resultIfFalse.getValue();
        } else {
            FLLogger.d("GivenIfExpressionNode", "true");
            return resultIfTrue.getValue();
        }

    }
}
