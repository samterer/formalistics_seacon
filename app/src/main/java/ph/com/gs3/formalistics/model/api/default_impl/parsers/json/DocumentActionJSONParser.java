package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.document.DocumentAction;


public class DocumentActionJSONParser {

    public static List<DocumentAction> createFromJSON(JSONArray raw) throws JSONException {

        List<DocumentAction> actions = new ArrayList<>();
        int actionCount = raw.length();

        for (int i = 0; i < actionCount; i++) {
            DocumentAction action = new DocumentAction();
            JSONObject rawAction = raw.getJSONObject(i);

            action.setLabel(rawAction.getString("label"));
            action.setAction(rawAction.getString("action"));

            actions.add(action);

        }

        return actions;
    }

    public static JSONArray createJSONArrayFromDocumentActionList(List<DocumentAction> actions)
            throws JSONException {

        JSONArray jsonActions = new JSONArray();

        if (actions == null) {
            // Return an empty JSON array if there are no actions
            return jsonActions;
        }

        for (DocumentAction action : actions) {

            JSONObject jsonAction = new JSONObject();

            jsonAction.put("label", action.getLabel());
            jsonAction.put("action", action.getAction());

            jsonActions.put(jsonAction);

        }

        return jsonActions;

    }

}
