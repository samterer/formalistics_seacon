package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class FormJSONParserV2 {

    public static Form createFromJSON(JSONObject raw) throws JSONException, FormFieldData.InvalidFormFieldException {

        Form form = new Form();

        form.setWebId(raw.getInt("form_id"));
        form.setName(raw.getString("form_name"));

        form.setWorkflowId(raw.getInt("workflow_id"));
        form.setWebTableName(raw.getString("form_table_name"));

        // Form Fields
        String mobileFieldsStringRaw = raw.getString("mobile_fields");
        JSONObject mobileFieldsRaw = new JSONObject(mobileFieldsStringRaw);
        JSONArray activeFieldsRaw = mobileFieldsRaw.getJSONArray("fields");

        List<FormViewContentData> activeContents = createFormContentsFromJSON(activeFieldsRaw);
        form.setActiveContents(activeContents);

        return form;

    }

    public static List<FormViewContentData> createFormContentsFromJSON(JSONArray raw)
            throws JSONException, FormFieldData.InvalidFormFieldException {

        List<FormViewContentData> contents = new ArrayList<>();
        for (int i = 0; i < raw.length(); i++) {
            // Throws InvalidFormFieldException
            FormViewContentData formContent = FormViewContentDataJSONParser.createFromJSON(raw.getJSONObject(i));
            if (formContent != null) {
                contents.add(formContent);
            } else {
                throw new FormFieldData.InvalidFormFieldException("Failed to create field from: "
                        + raw.getJSONObject(i).toString());
            }
        }

        return contents;

    }

    public static JSONArray createJSONFromFormContents(List<FormViewContentData> formContents)
            throws JSONException {

        JSONArray fieldsJSONArray = new JSONArray();

        for (FormViewContentData formContent : formContents) {
            JSONObject rawJSON = new JSONObject(formContent.getRawJSONString());
            fieldsJSONArray.put(rawJSON);
        }

        return fieldsJSONArray;

    }


}
