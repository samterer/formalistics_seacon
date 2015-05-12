package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class WorkflowActionJSONParser {

    public static List<WorkflowAction> createFromJSON(JSONArray rawJSON) throws JSONException {

        List<WorkflowAction> actions = new ArrayList<>();
        int actionCount = rawJSON.length();

        for (int i = 0; i < actionCount; i++) {
            WorkflowAction action = new WorkflowAction();
            JSONObject rawAction = rawJSON.getJSONObject(i);

            action.setLabel(rawAction.getString("label"));
            action.setNodeId(rawAction.getString("node_id"));

            actions.add(action);

        }

        return actions;
    }

    public static JSONArray createJSONArrayFromList(List<WorkflowAction> workflowActions) throws JSONException {

        JSONArray actionsJSONArray = new JSONArray();

        for (WorkflowAction workflowAction : workflowActions) {
            JSONObject actionJSON = new JSONObject();

            actionJSON.put("label", workflowAction.getLabel());
            actionJSON.put("node_id", workflowAction.getNodeId());

            actionsJSONArray.put(actionJSON);
        }

        return actionsJSONArray;

    }

}
