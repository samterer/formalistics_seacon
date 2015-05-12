package ph.com.gs3.formalistics.service.formula.node.function;

import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class LookupExpressionNode implements ExpressionNode {

    private ExpressionNode formNameExpression;
    private ExpressionNode returnFieldNameExpression;
    private ExpressionNode compareToOtherFormFieldNameExpression;
    private ExpressionNode compareToThisFormFieldValueExpression;

    private LookupRequestListener listener;

    public LookupExpressionNode(LookupRequestListener listener) {
        this.listener = listener;
    }

    public void setFormNameExpression(ExpressionNode formNameExpression) {
        this.formNameExpression = formNameExpression;
    }

    public void setReturnFieldNameExpression(ExpressionNode returnFieldNameExpression) {
        this.returnFieldNameExpression = returnFieldNameExpression;
    }

    public void setCompareToOtherFormFieldNameExpression(ExpressionNode compareToOtherFormFieldNameExpression) {
        this.compareToOtherFormFieldNameExpression = compareToOtherFormFieldNameExpression;
    }

    public void setCompareToThisFormFieldValueExpression(ExpressionNode compareToThisFormFieldValueExpression) {
        this.compareToThisFormFieldValueExpression = compareToThisFormFieldValueExpression;
    }

    @Override
    public NodeType getType() {
        return NodeType.FUNCTION;
    }

    @Override
    public Object getValue() throws ParserException {

        String formName = formNameExpression.getValue().toString();
        String returnFieldName = returnFieldNameExpression.getValue().toString();
        String compareToOtherFormFieldName = compareToOtherFormFieldNameExpression.getValue().toString();
        String compareToThisFormFieldValue = compareToThisFormFieldValueExpression.getValue().toString();

        return listener.onLookupCommand(
                formName,
                returnFieldName,
                compareToOtherFormFieldName,
                compareToThisFormFieldValue
        );
    }

}
