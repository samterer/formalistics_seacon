package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.UsersDAO;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;
import ph.com.gs3.formalistics.presenter.fragment.view.TesterViewFragment;
import ph.com.gs3.formalistics.service.formula.FormulaEvaluator;
import ph.com.gs3.formalistics.service.formula.FormulaLexer;
import ph.com.gs3.formalistics.service.formula.FormulaParser;
import ph.com.gs3.formalistics.service.formula.ParserException;
import ph.com.gs3.formalistics.service.formula.Token;
import ph.com.gs3.formalistics.service.formula.node.ExpressionNode;
import ph.com.gs3.formalistics.service.formula.node.function.LookupRequestListener;

public class TesterActivity extends Activity implements TesterViewFragment.TesterViewFragmentActionListener {

    public static final String TAG = TesterActivity.class.getSimpleName();

    private TesterViewFragment testerViewFragment;

    private FormsDAO formsDAO;
    private UsersDAO usersDAO;
    private DynamicFormFieldsDAO dynamicFormFieldsDAO;

    private User activeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        formsDAO = new FormsDAO(this);
        usersDAO = new UsersDAO(this);
        dynamicFormFieldsDAO = new DynamicFormFieldsDAO(this);

        activeUser = usersDAO.getActiveUser();

        if (savedInstanceState == null) {
            testerViewFragment = new TesterViewFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, testerViewFragment, TesterViewFragment.TAG)
                    .commit();
        } else {
            testerViewFragment = (TesterViewFragment) getFragmentManager().findFragmentByTag(TesterViewFragment.TAG);
        }
    }

    @Override
    public void onTestFormulaCommand(String formula) {

        FLLogger.d(TAG, "Testing formula: " + formula);

        DocumentHeaderData dummyHeaderData = generateDummyHeaderData();
        JSONObject dummyFieldValues = generateDummyFieldValues();

        long startTime = System.currentTimeMillis();

        FormulaLexer formulaLexer = new FormulaLexer();
        LinkedList<Token> lexeme = null;
        try {
            lexeme = formulaLexer.lex(formula);
            for (Token token : lexeme) {
                FLLogger.d(TAG, token.toString());
            }
        } catch (ParserException e) {
            e.printStackTrace();
        }

        String message;

        try {
            FormulaParser formulaParser = new FormulaParser(lexeme, dummyHeaderData, dummyFieldValues);
            String parsedWhereClause = formulaParser.parseForSQLiteWhereClause();

            message = "Parsed where clause: " + parsedWhereClause;
            FLLogger.d(TAG, message);
        } catch (ParserException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        long executionTime = System.currentTimeMillis() - startTime;
        FLLogger.d(TAG, "Parsing took " + executionTime + "ms");

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTestFormula2Command(String formula) {

        DocumentHeaderData dummyHeaderData = generateDummyHeaderData();
        JSONObject dummyFieldValues = generateDummyFieldValues();

        long startTime = System.currentTimeMillis();

        String message;

        FormulaLexer formulaLexer = new FormulaLexer();
        LinkedList<Token> lexeme = null;
        try {
            lexeme = formulaLexer.lex(formula);
            for (Token token : lexeme) {
                FLLogger.d(TAG, token.toString());
            }

            FormulaEvaluator formulaEvaluator = new FormulaEvaluator(dummyHeaderData, dummyFieldValues);
            ExpressionNode topExpressionNode = formulaEvaluator.evaluate(lexeme);
            message = formula + " = " + topExpressionNode.getValue().toString();
            FLLogger.d(TAG, message);
        } catch (ParserException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        long executionTime = System.currentTimeMillis() - startTime;
        FLLogger.d(TAG, "Parsing took " + executionTime + "ms");

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTestFormula3Command(String formula) {
        DocumentHeaderData dummyHeaderData = generateDummyHeaderData();
        JSONObject dummyFieldValues = generateDummyFieldValues();

        long startTime = System.currentTimeMillis();

        String message;

        FormulaLexer formulaLexer = new FormulaLexer();
        LinkedList<Token> lexeme = null;
        try {
            lexeme = formulaLexer.lex(formula);
            for (Token token : lexeme) {
                FLLogger.d(TAG, token.toString());
            }

            FormulaEvaluator formulaEvaluator = new FormulaEvaluator(dummyHeaderData, dummyFieldValues, lookupRequestListener);
            ExpressionNode topExpressionNode = formulaEvaluator.evaluate(lexeme);
            message = formula + " = " + topExpressionNode.getValue().toString();
            FLLogger.d(TAG, message);
        } catch (ParserException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        long executionTime = System.currentTimeMillis() - startTime;
        FLLogger.d(TAG, "Parsing took " + executionTime + "ms");

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private DocumentHeaderData generateDummyHeaderData() {

        DocumentHeaderData dummyHeaderData = new DocumentHeaderData();

        dummyHeaderData.setWebId(1);
        dummyHeaderData.setFormWebId(1);
        dummyHeaderData.setTrackingNumber("TN-0001");
        dummyHeaderData.setDateCreated("2011-06-13");
        dummyHeaderData.setDateCreated("2011-06-13");

        WorkflowObject dummyWorkflowObject = new WorkflowObject();
        dummyWorkflowObject.setStatus("Created");

        dummyHeaderData.setWorkflowObject(dummyWorkflowObject);

        return dummyHeaderData;

    }

    private JSONObject generateDummyFieldValues() {

        JSONObject fieldValues = new JSONObject();

        try {
            fieldValues.put("field_1", "1");
            fieldValues.put("field_2", "2");
            fieldValues.put("field_3", "string value");
            fieldValues.put("field_4", "25");
            fieldValues.put("CRFrom", "SLM0003");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return fieldValues;

    }

    private final LookupRequestListener lookupRequestListener = new LookupRequestListener() {

        @Override
        public String onLookupCommand(String formName, String returnFieldName, String compareToOtherFormFieldName, String compareToThisFormFieldValue) {

            List<String> searchResultFieldNames = new ArrayList<>();
            searchResultFieldNames.add(returnFieldName);

            List<SearchCondition> searchConditions = new ArrayList<>();
            searchConditions.add(new SearchCondition(compareToOtherFormFieldName, "=", compareToThisFormFieldValue));

            String resultMessage = "";

            try {
                Form form = formsDAO.getFormByName(formName, activeUser.getCompany().getId());

                if (form != null) {
                    List<JSONObject> searchResults = dynamicFormFieldsDAO.search(form, searchResultFieldNames, activeUser.getId(), searchConditions);

                    if (searchResults.size() > 0) {
                        return searchResults.get(0).getString(returnFieldName);
                    } else {
                        resultMessage = "No value for " + returnFieldName + " found";
                    }
                } else {
                    resultMessage = "Lookup failed, cannot find form " + formName;
                }

            } catch (DataAccessObject.DataAccessObjectException e) {
                resultMessage = "Lookup failed, failed to find form " + formName;
            } catch (JSONException e) {
                resultMessage = "Search failed: " + e.getMessage();
            }

            Toast.makeText(TesterActivity.this, resultMessage, Toast.LENGTH_LONG).show();

            FLLogger.d(TAG, formName + " " + returnFieldName + " " + compareToOtherFormFieldName + " " + compareToThisFormFieldValue);

            return "Some looked up value";
        }
    };

}
