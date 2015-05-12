package ph.com.gs3.formalistics.view.document.formula_js;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;

/**
 * Created by Ervinne on 4/21/2015.
 */
public class FormulaEvaluator {

    public static final String TAG = FormulaEvaluator.class.getSimpleName();

    private DocumentHeaderData documentHeaderData;
    private JSONObject fieldValues;

    public FormulaEvaluator(User activeUser, DocumentHeaderData documentHeaderData, JSONObject fieldValues) {
        this.documentHeaderData = documentHeaderData;
        this.fieldValues = fieldValues;
    }

    public Boolean evaluateForBoolean(String formula) throws ParserException {

        Object returnValue = null;

        String builtFormula = assignAllPropertiesToFormula(formula);

        FLLogger.d(TAG, "Executing built formula: " + builtFormula);

        String script = "function formula() {return " + builtFormula + ";}";
        org.mozilla.javascript.Context context = org.mozilla.javascript.Context.enter();
        context.setOptimizationLevel(-1);
        try {
            ScriptableObject scope = context.initStandardObjects();
            Scriptable that = context.newObject(scope);

            Function fct = context.compileFunction(scope, script, "script", 1, null);
            Object result = fct.call(context, scope, that, new Object[]{2, 3});

            returnValue = org.mozilla.javascript.Context.jsToJava(result, boolean.class);
        } finally {
            org.mozilla.javascript.Context.exit();
        }

        if (returnValue instanceof Boolean) {
            return (Boolean) returnValue;
        } else {
            throw new ParserException(returnValue + " is not a boolean value");
        }
    }

    public String assignAllPropertiesToFormula(String formula) {
        String builtFormula = new String(formula);

        builtFormula = assignDocumentPropertiesToFormula(builtFormula);
        builtFormula = assignUserPropertiesToFormula(builtFormula);
        builtFormula = assignDocumentFieldValuesToFormula(builtFormula);

        return builtFormula;
    }

    public String assignDocumentPropertiesToFormula(String formula) {

        formula = formula.replaceAll("@status", "\"" + documentHeaderData.getWorkflowObject().getStatus() + "\"");

        return formula;
    }

    public String assignUserPropertiesToFormula(String formula) {
        // TODO: implement
        return formula;
    }

    public String assignDocumentFieldValuesToFormula(String formula) {

        String variableExpression = "@[a-zA-Z0-9_]*";

        Pattern pattern = Pattern.compile(variableExpression);
        Matcher matcher = pattern.matcher(formula);

        while (matcher.find()) {

            String matchedSequence = matcher.group().trim();
            String key = matchedSequence.substring(1); // remove @

            if (fieldValues.has(key)) {
                try {
                    Object fieldValue = fieldValues.get(key);
                    if (fieldValue instanceof Integer || fieldValue instanceof Boolean) {
                        formula = formula.replaceAll("@" + key, fieldValues.getString(key));
                    } else {
                        formula = formula.replaceAll("@" + key, "\"" + fieldValues.getString(key) + "\"");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return formula;
    }

    public static class ParserException extends Exception {

        public ParserException(String detailMessage) {
            super(detailMessage);
        }

        public ParserException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public ParserException(Throwable throwable) {
            super(throwable);
        }
    }

}
