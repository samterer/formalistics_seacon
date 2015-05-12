package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentAction;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;

public class DocumentsJSONParser {
    public static final String TAG = DocumentsJSONParser.class.getSimpleName();

    public static final Document createFromJSON(JSONObject raw, List<FormFieldData> formFields) throws JSONException {

        Document document = new Document();

        document.setWebId(raw.getInt("ID"));
        document.setTrackingNumber(raw.getString("TrackNo"));

        document.setWorkflowId(raw.getInt("Workflow_ID"));
        document.setWorkflowNodeId(raw.getString("Node_ID"));

        document.setStatus(raw.getString("Status"));

        document.setDateCreated(raw.getString("DateCreated"));
        document.setDateUpdated(raw.getString("DateUpdated"));

        try {
            document.setProcessor(raw.getString("Processor"));
            document.setProcessorType(raw.getInt("ProcessorType"));
        } catch (Exception e) {
            FLLogger.d(TAG, "Processor and/or processor type not found for document " + document.getWebId() + " - " + document.getTrackingNumber());
            document.setProcessor("");
            document.setProcessorType(0);
        }

        document.setFieldValuesJSONString(createFieldValuesFromJSON(raw, formFields).toString());
        document.setStarMark(raw.getInt("is_starred"));

        return document;

    }

    public static JSONObject createFieldValuesFromJSON(JSONObject raw, List<FormFieldData> formFields) throws JSONException {

        JSONObject filteredJSONFields = new JSONObject();
        for (FormFieldData formField : formFields) {
            String key = formField.getName();
            if (raw.has(key)) {
                filteredJSONFields.put(key, raw.getString(key));
            }
        }

        return filteredJSONFields;

    }

    private static List<DocumentAction> createActionFromDocumentActions(JSONObject raw) {

        List<DocumentAction> actions = new ArrayList<>();

        Iterator<?> iterator = raw.keys();
        int keyCount = 0;

        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            keyCount++;
            try {
                JSONObject actionFromLastAction = new JSONObject(raw.getString(key));

                // Throws JSONException
                DocumentAction action = new DocumentAction();

                action.setLabel(key);
                action.setAction(actionFromLastAction.getString("child_id"));

                actions.add(action);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (keyCount > 0) {
            // Add save and cancel
            DocumentAction save = new DocumentAction();
            DocumentAction cancel = new DocumentAction();

            save.setLabel("Save");
            save.setAction("Save");

            cancel.setLabel("Cancel");
            cancel.setAction("Cancel");

            actions.add(save);
            actions.add(cancel);

        }

        return actions;

    }

}
