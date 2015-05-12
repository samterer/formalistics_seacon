package ph.com.gs3.formalistics.service.formula.node.function;

/**
 * Created by Ervinne on 5/3/2015.
 */
public interface LookupRequestListener {

    public String onLookupCommand(String formName, String returnFieldName, String compareToOtherFormFieldName, String compareToThisFormFieldValue);

}
