package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.document.DocumentAction;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData.InvalidFormFieldException;
import ph.com.gs3.formalistics.model.values.business.form.FormOld;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class FormJSONParserV1 {

    public static FormOld createFromJSON(JSONObject raw) throws JSONException, InvalidFormFieldException {

        FormOld form = new FormOld();

        form.setWebId(raw.getInt("form_id"));
        form.setName(raw.getString("form_name"));

        form.setWorkflowId(raw.getString("workflow_id"));
        form.setWebTableName(raw.getString("form_table_name"));

        // FormOld Fields
        String mobileFieldsStringRaw = raw.getString("mobile_fields");
        JSONObject mobileFieldsRaw = new JSONObject(mobileFieldsStringRaw);
        JSONArray activeFieldsRaw = mobileFieldsRaw.getJSONArray("fields");

        List<FormViewContentData> activeContents = createFormContentsFromJSON(activeFieldsRaw);
        form.setActiveContents(activeContents);

        // Field Configuration
        String fieldsEnabledStringRaw = raw.getString("on_create_fields_enabled");
        String fieldsRequiredStringRaw = raw.getString("on_create_fields_required");
        String fieldsHiddenStringRaw = raw.getString("on_create_fields_hidden");

        JSONArray fieldsEnabledRaw;
        if (fieldsEnabledStringRaw != null && !"null".equals(fieldsEnabledStringRaw)) {
            fieldsEnabledRaw = new JSONArray(fieldsEnabledStringRaw);
        } else {
            fieldsEnabledRaw = new JSONArray();
        }

        JSONArray fieldsRequiredRaw;
        if (fieldsRequiredStringRaw != null && !"null".equals(fieldsRequiredStringRaw)) {
            fieldsRequiredRaw = new JSONArray(fieldsRequiredStringRaw);
        } else {
            fieldsRequiredRaw = new JSONArray();
        }

        JSONArray fieldsHiddenRaw;
        if (fieldsHiddenStringRaw != null && !"null".equals(fieldsHiddenStringRaw)) {
            fieldsHiddenRaw = new JSONArray(fieldsHiddenStringRaw);
        } else {
            fieldsHiddenRaw = new JSONArray();
        }

        List<String> fieldsEnabled = JSONParser.createStringListFromJSONArray(fieldsEnabledRaw);
        List<String> fieldsRequired = JSONParser.createStringListFromJSONArray(fieldsRequiredRaw);
        List<String> fieldsHidden = JSONParser.createStringListFromJSONArray(fieldsHiddenRaw);

        form.setOnCreateFieldsEnabled(fieldsEnabled);
        form.setOnCreateFieldsRequired(fieldsRequired);
        form.setOnCreateFieldsHidden(fieldsHidden);

        // Actions
        JSONArray actionsRaw = raw.getJSONArray("on_create_actions");
        List<DocumentAction> onCreateActions = DocumentActionJSONParser.createFromJSON(actionsRaw);

        form.setOnCreateActions(onCreateActions);

        return form;

    }

    public static JSONArray createJSONArrayFromActions(List<DocumentAction> actions)
            throws JSONException {

        JSONArray jsonArrayActions = new JSONArray();

        for (DocumentAction action : actions) {
            JSONObject json = new JSONObject();

            json.put("label", action.getLabel());
            json.put("action", action.getAction());

            jsonArrayActions.put(json);

        }

        return jsonArrayActions;
    }

    public static List<FormViewContentData> createFormContentsFromJSON(JSONArray raw)
            throws JSONException, InvalidFormFieldException {

        List<FormViewContentData> contents = new ArrayList<>();
        for (int i = 0; i < raw.length(); i++) {
            // Throws InvalidFormOldFieldException
            FormViewContentData formContent = FormViewContentDataJSONParser.createFromJSON(raw.getJSONObject(i));
            if (formContent != null) {
                contents.add(formContent);
            } else {
                throw new InvalidFormFieldException("Failed to create field from: "
                        + raw.getJSONObject(i).toString());
            }
        }

        return contents;

    }

    public static JSONArray createJSONFromFormOldContents(List<FormViewContentData> formContents)
            throws JSONException {

        JSONArray fieldsJSONArray = new JSONArray();

        for (FormViewContentData formContent : formContents) {
            JSONObject rawJSON = new JSONObject(formContent.getRawJSONString());
            fieldsJSONArray.put(rawJSON);
        }

        return fieldsJSONArray;

    }

}
