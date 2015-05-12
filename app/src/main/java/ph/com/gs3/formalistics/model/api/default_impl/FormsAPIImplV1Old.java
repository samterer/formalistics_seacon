package ph.com.gs3.formalistics.model.api.default_impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.model.api.API;
import ph.com.gs3.formalistics.model.api.FormsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.HttpCommunicator.CommunicationException;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.APIResponse.InvalidResponseException;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;
import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class FormsAPIImplV1Old extends API implements FormsAPI {

    public static final String TAG = FormsAPIImplV1Old.class.getSimpleName();

    private List<UnparseableObject> unparseableForms;

    public FormsAPIImplV1Old(HttpCommunicator communicator, String server) {
        super(communicator, server);
        unparseableForms = new ArrayList<>();
    }

    @Override
    public List<Form> getForms(String fromDate) throws InvalidResponseException, CommunicationException {

        commonValidation();
        unparseableForms.clear();

        String url = getServer() + "/API/form-list";

        // Prepare the parameter for filtering by date updated

        Map<String, String> requestParams = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            JSONObject dateUpdatedFilter = new JSONObject();
            JSONObject dateUpdatedConditions = new JSONObject();
            try {
                dateUpdatedConditions.put("condition", ">=");
                dateUpdatedConditions.put("compared_to_date", fromDate);

                dateUpdatedFilter.put("date_updated_comparison", dateUpdatedConditions);

                requestParams = new HashMap<String, String>();

                requestParams.put("search_parameters", dateUpdatedFilter.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        APIResponse response = request(url, requestParams);

        List<Form> forms = new ArrayList<>();

//        if (response.isOperationSuccessful()) {
//            String resultsRaw = response.getResults();
//            FLLogger.d(TAG, resultsRaw);
//
//            JSONArray results;
//            try {
//                results = new JSONArray(resultsRaw);
//            } catch (JSONException e) {
//                throw new InvalidResponseException(
//                        "Unable to read the JSON object (JSON Array) containing the list of forms, it may be corrupted.",
//                        e);
//            }
//
//            for (int i = 0; i < results.length(); i++) {
//                String formJSONString = null;
//                try {
//                    formJSONString = results.getString(i);
//                    Form form = FormJSONParserV1.createFromJSON(new JSONObject(formJSONString));
//                    forms.add(form);
//                } catch (JSONException | FormFieldData.InvalidFormFieldException e) {
//                    e.printStackTrace();
//                    unparseableForms.add(new UnparseableObject(formJSONString, e));
//                }
//            }
//
//        }

        return forms;

    }

    @Override
    public List<UnparseableObject> getUnparseableForms() {
        return unparseableForms;
    }
}
