package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.global.constants.FormContentType;
import ph.com.gs3.formalistics.global.constants.FormContentTypeGroupings;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData.InvalidFormFieldException;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;
import ph.com.gs3.formalistics.model.values.business.form.Formula;

public class FormViewContentDataJSONParser {

    public static final String TAG = FormViewContentDataJSONParser.class.getSimpleName();

    public static FormViewContentData createFromJSON(JSONObject raw) throws InvalidFormFieldException {

        FormViewContentData formField;
        validateJSONKeys(raw);

        try {

            FormContentType fieldType = getTypeSafeFieldType(raw.getString("type"));

            if (FormContentTypeGroupings.isField(fieldType)) {
                formField = new FormFieldData();
                ((FormFieldData) formField).setLabel(raw.getString("label"));

                // Optional fields
                if (raw.has("value")) {
                    ((FormFieldData) formField).setValue(raw.getString("value"));
                }

                if (raw.has("image_placeholder")) {
                    ((FormFieldData) formField).setPlaceHolder(raw.getString("image_placeholder"));
                }

                if (raw.has("options")) {
                    JSONArray optionsJSON = new JSONArray(raw.getString("options"));
                    List<String> options = JSONParser.createStringListFromJSONArray(optionsJSON);
                    ((FormFieldData) formField).setOptions(options);
                }
            } else {

                if (fieldType == FormContentType.EMBEDDED_VIEW) {
                    formField = EmbeddedViewDataJSONParser.parseJSON(raw);
                } else {
                    formField = new FormFieldData();
                    FLLogger.e(TAG, "Unknown non field type form content: " + raw.getString("type"));
                }

            }

            formField.setName(raw.getString("id"));
            formField.setType(fieldType);
            formField.setRawJSONString(raw.toString());

            // Set formulas
            if (raw.has("formulas")) {
                JSONObject formulas = raw.getJSONObject("formulas");
                Formula visibilityFormula = createFormulaFromJSON(formulas.getJSONObject("visibility"));
                Formula valueFormula = createFormulaFromJSON(formulas.getJSONObject("computed"));
                Formula readOnlyFormula = createFormulaFromJSON(formulas.getJSONObject("readonly"));

                formField.setVisibilityFormula(visibilityFormula);

                if (formField instanceof FormFieldData) {
                    ((FormFieldData) formField).setValueFormula(valueFormula);
                    ((FormFieldData) formField).setReadOnlyFormula(readOnlyFormula);
                }

            }

        } catch (JSONException e) {
            throw new InvalidFormFieldException(raw.toString(), e);
        }

        return formField;

    }

    public static Formula createFormulaFromJSON(JSONObject rawJSON) throws JSONException {

        Formula formula;

        String rule = rawJSON.getString("formula_rule");

        if (rawJSON.has("type")) {
            Formula.FormulaType formulaType;
            String rawFormulaType = rawJSON.getString("type");

            if ("middleware".equalsIgnoreCase(rawFormulaType)) {
                formulaType = Formula.FormulaType.MIDDLEWARE;
            } else if ("static".equalsIgnoreCase(rawFormulaType)) {
                formulaType = Formula.FormulaType.STATIC;
            } else if ("computed".equalsIgnoreCase(rawFormulaType)) {
                formulaType = Formula.FormulaType.COMPUTED;
            } else {
                formulaType = Formula.FormulaType.NOT_APPLICABLE;
            }

            formula = new Formula(rule, formulaType);
        } else {
            formula = new Formula(rule);
        }

        return formula;

    }

    public static void validateJSONKeys(JSONObject raw) throws InvalidFormFieldException {
        // Check for missing required values
        String[] requiredKeys = {"id", "type"};
        String missingKeysString = "";
        boolean hasMissingKeys = false;

        for (String requiredKey : requiredKeys) {
            if (!raw.has(requiredKey)) {
                missingKeysString += requiredKey + ", ";
                hasMissingKeys = true;
            }
        }

        if (hasMissingKeys) {
            missingKeysString = missingKeysString.substring(0, missingKeysString.length() - 2);
            String message = "The following key(s) are missing: (" + missingKeysString
                    + ") from raw JSON " + raw;

            // FLLogger.e(TAG, message);
            throw new InvalidFormFieldException(message);

        }
    }

    public static FormContentType getTypeSafeFieldType(String type) {

        switch (type) {
            case "TEXT_FIELD":
                return FormContentType.TEXT_FIELD;
            case "TEXT_AREA":
                return FormContentType.TEXT_AREA;
            case "DROPDOWN":
                return FormContentType.DROPDOWN;
            case "CHECK_BOX_GROUP":
                return FormContentType.CHECK_BOX_GROUP;
            case "SELECT_MANY":
                return FormContentType.SELECT_MANY;
            case "RADIO_BUTTON_GROUP":
                return FormContentType.RADIO_BUTTON_GROUP;
            case "DATE_PICKER":
                return FormContentType.DATE_PICKER;
            case "DATE_TIME_PICKER":
                return FormContentType.DATE_TIME_PICKER;
            case "TIME_PICKER":
                return FormContentType.TIME_PICKER;
            case "PICK_LIST":
                return FormContentType.PICK_LIST;
            case "BAR_CODE":
            case "BARCODE_SCANNER":
                return FormContentType.BARCODE_SCANNER;
            case "QR_CODE_FIELD":
                return FormContentType.QRCODE_SCANNER;
            case "SINGLE_ATTACHMENT":
                return FormContentType.SINGLE_ATTACHMENT;
            case "REQUEST_IMAGE":
                return FormContentType.DYNAMIC_IMAGE;
            case "CONTAINER_EMBED_VIEW":
                return FormContentType.EMBEDDED_VIEW;
            default:
                FLLogger.d(TAG, "Unsupported field: " + type);
                return FormContentType.UNSUPPORTED;
        }
    }

}
