package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class WorkflowObjectJSONParser {

    public static final String TAG = WorkflowObjectJSONParser.class.getSimpleName();

    public static WorkflowObject createFromJSON(JSONObject rawJSON) throws JSONException {

        WorkflowObject workflowObject = new WorkflowObject();

        workflowObject.setWebId(rawJSON.getInt("workflow_object_id"));
        workflowObject.setWorkflowObjectNodeType(rawJSON.getInt("workflow_object_type"));
        workflowObject.setWorkflowId(rawJSON.getInt("workflow_id"));
        workflowObject.setWorkflowFormId(rawJSON.getInt("workflow_form_id"));
        workflowObject.setNodeId(rawJSON.getString("node_id"));

        try {
            workflowObject.setProcessorType(rawJSON.getInt("processor_type"));
        } catch (JSONException e) {
            workflowObject.setProcessorType(0);
        }

        workflowObject.setProcessor(rawJSON.getString("processor"));
        workflowObject.setStatus(rawJSON.getString("status"));

        //<editor-fold desc="Fields Configuration">
        String rawFieldsEnabledString = rawJSON.getString("fields_enabled");
        String rawFieldsRequiredString = rawJSON.getString("fields_required");
        String rawFieldsHiddenString = rawJSON.getString("fields_hidden");

        workflowObject.setFieldsEnabled(new ArrayList<String>());
        workflowObject.setFieldsRequired(new ArrayList<String>());
        workflowObject.setFieldsHidden(new ArrayList<String>());

        try {
            JSONArray rawFieldsEnabled = new JSONArray(rawFieldsEnabledString);
            for (int i = 0; i < rawFieldsEnabled.length(); i++) {
                workflowObject.getFieldsEnabled().add(rawFieldsEnabled.getString(i));
            }
        } catch (JSONException e) {
            FLLogger.w(TAG, "Failed parsing fields enabled: " + e.getMessage());
        }

        try {
            JSONArray rawFieldsRequired = new JSONArray(rawFieldsRequiredString);
            for (int i = 0; i < rawFieldsRequired.length(); i++) {
                workflowObject.getFieldsRequired().add(rawFieldsRequired.getString(i));
            }
        } catch (JSONException e) {
            FLLogger.w(TAG, "Failed parsing fields required: " + e.getMessage());
        }

        try {
            JSONArray rawFieldsHidden = new JSONArray(rawFieldsHiddenString);
            for (int i = 0; i < rawFieldsHidden.length(); i++) {
                workflowObject.getFieldsHidden().add(rawFieldsHidden.getString(i));
            }
        } catch (JSONException e) {
            FLLogger.w(TAG, "Failed parsing fields hidden: " + e.getMessage());
        }
        //</editor-fold>

        String actionsString = rawJSON.getString("actions");
        workflowObject.setWorkflowActions(new ArrayList<WorkflowAction>());

        try {
            workflowObject.setWorkflowActions(WorkflowActionJSONParser.createFromJSON(new JSONArray(actionsString)));
        } catch (JSONException e) {
            FLLogger.e(TAG, "Failed parsing workflow actions: " + e.getMessage());
        }

        return workflowObject;

    }

}
