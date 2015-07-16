package ph.com.gs3.formalistics.model.api.default_impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.API;
import ph.com.gs3.formalistics.model.api.FormsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.HttpCommunicator.CommunicationException;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.FormJSONParserV2;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.WorkflowObjectJSONParser;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.APIResponse.InvalidResponseException;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class FormsAPIDefaultImpl extends API implements FormsAPI {

    public static final String TAG = FormsAPIDefaultImpl.class.getSimpleName();

    private final List<UnparseableObject> unparseableForms;

    public FormsAPIDefaultImpl(HttpCommunicator communicator, String server) {
        super(communicator, server);
        unparseableForms = new ArrayList<>();
    }

    @Override
    public List<Form> getForms(String fromDate) throws InvalidResponseException, CommunicationException {

        commonValidation();
        unparseableForms.clear();

        String url = getServer() + "/API/form-and-workflow-list";

        // Prepare the parameter for filtering by date updated

        Map<String, String> requestParams = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            JSONObject dateUpdatedFilter = new JSONObject();
            JSONObject dateUpdatedConditions = new JSONObject();
            try {
                dateUpdatedConditions.put("condition", ">");
                dateUpdatedConditions.put("compared_to_date", fromDate);

                dateUpdatedFilter.put("date_updated_comparison", dateUpdatedConditions);

                requestParams = new HashMap<>();

                requestParams.put("search_parameters", dateUpdatedFilter.toString());
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        APIResponse response = request(url, requestParams);

        List<Form> forms = new ArrayList<>();

        if (response.isOperationSuccessful()) {
            String resultsRaw = response.getResults();
            FLLogger.d(TAG, resultsRaw);

            JSONObject results;
            JSONArray formsJSON;
            JSONArray workflowObjectsJSON;

            try {
                results = new JSONObject(resultsRaw);
                formsJSON = results.getJSONArray("forms");
                workflowObjectsJSON = results.getJSONArray("workflow_objects");
            } catch (JSONException e) {
                throw new InvalidResponseException(
                        "Unable to read the JSON object containing the list of forms & workflow objects, it may be corrupted.",
                        e);
            }

            Map<Integer, Form> formMap = new HashMap<>();

            for (int i = 0; i < formsJSON.length(); i++) {
                String formJSONString = null;
                try {
                    formJSONString = formsJSON.getString(i);
                    Form form = FormJSONParserV2.createFromJSON(new JSONObject(formJSONString));
                    // Workflow objects will be added to the list later
                    form.setWorkflowObjects(new ArrayList<WorkflowObject>());
                    formMap.put(form.getWebId(), form);
                } catch (JSONException | FormFieldData.InvalidFormFieldException e) {
                    FLLogger.e(TAG, "Failed to parse form: " + e.getMessage());
                    e.printStackTrace();
                    unparseableForms.add(new UnparseableObject(formJSONString, e));
                }
            }

            for (int i = 0; i < workflowObjectsJSON.length(); i++) {
                String workflowObjectString = null;

                try {
                    workflowObjectString = workflowObjectsJSON.getString(i);
                    WorkflowObject workflowObject = WorkflowObjectJSONParser.createFromJSON(new JSONObject(workflowObjectString));

                    if (formMap.get(workflowObject.getWorkflowFormId()) != null) {
                        formMap.get(workflowObject.getWorkflowFormId())
                                .getWorkflowObjects()
                                .add(workflowObject);
                    } else {
                        FLLogger.w(TAG, "Failed to assign workflow to form " + workflowObject.getWorkflowFormId() + ", it's not found in the forms downloaded");
                    }
                } catch (JSONException e) {
                    FLLogger.e(TAG, "Failed to parse workflow object: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            Iterator iterator = formMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Form> pair = (Map.Entry) iterator.next();
                forms.add(pair.getValue());
                iterator.remove(); // avoids a ConcurrentModificationException
            }

        }

        return forms;

    }

    @Override
    public List<UnparseableObject> getUnparseableForms() {
        return unparseableForms;
    }
}
