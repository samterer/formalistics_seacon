package ph.com.gs3.formalistics.service.formula;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;

/**
 * Created by Ervinne on 5/3/2015.
 */
public class FormulaVariableParser {

    private List<String> timeDependentComputationVariables;
    private JSONObject consolidatedDocumentValues;

    public FormulaVariableParser(DocumentHeaderData documentHeaderData, JSONObject fieldValues) throws ParserException {
        initializeTimeDependentComputationVariables();
        buildConsolidatedDocumentValues(documentHeaderData, fieldValues);
    }

    private void initializeTimeDependentComputationVariables() {
        timeDependentComputationVariables = new ArrayList<>();

        timeDependentComputationVariables.add("Now");
        timeDependentComputationVariables.add("Today");
        timeDependentComputationVariables.add("TimeStamp");
    }

    private void buildConsolidatedDocumentValues(DocumentHeaderData documentHeaderData, JSONObject fieldValues) throws ParserException {

        try {
            consolidatedDocumentValues = new JSONObject(fieldValues.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            // This should not happen, ignore
        }

        try {
            consolidatedDocumentValues.put("Status", documentHeaderData.getWorkflowObject().getStatus());
            consolidatedDocumentValues.put("TrackingNumber", documentHeaderData.getTrackingNumber());
            consolidatedDocumentValues.put("TrackNo", documentHeaderData.getTrackingNumber());
            // TODO: put other values here
        } catch (JSONException e) {
            throw new ParserException("Failed adding document header data values to consolidated document values: " + e.getMessage());
        } catch (NullPointerException e) {
            if (documentHeaderData.getWorkflowObject() == null) {
                throw new ParserException("Document header data must have a workflow object value");
            } else {
                throw new ParserException("Unexpected NullPointerException, " + e.getMessage());
            }
        }

    }

    // TODO: move this to a more appropriate class - this class is responsible only for parsing variables, not extrating strings
    public String getRawStringFromTokenData(String tokenData) {
        if (tokenData.length() > 0) {
            return tokenData.substring(1, tokenData.length() - 1);
        } else {
            return "";
        }
    }

    public String getStringVariableValue(String variable) throws ParserException {

        String rawVariable = variable.substring(1); // remove @
        String value = null;

        if (consolidatedDocumentValues.has(rawVariable)) {
            try {
                value = consolidatedDocumentValues.getString(rawVariable);
            } catch (JSONException e) {
                throw new ParserException("Failed to get value for variable " + rawVariable + ": " + e.getMessage());
            }
        } else if (timeDependentComputationVariables.contains(rawVariable)) {
            if ("Now".equals(rawVariable)) {
                value = DateUtilities.getServerFormattedCurrentDateTime();
                FLLogger.d("FormulaVariableParser", "Computed Now: " + value);
            } else if ("Today".equals(rawVariable)) {
                value = DateUtilities.getServerFormattedCurrentDate();
            } else if ("TimeStamp".equals(rawVariable)) {
                value = DateUtilities.getCurrentTimeStamp();
            }
        } else {
            throw new ParserException("Undefined variable " + variable);
        }

        return value;
    }

}
