package ph.com.gs3.formalistics.model.api.custom_impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.default_impl.DocumentsAPIDefaultImpl;
import ph.com.gs3.formalistics.model.dao.facade.search.SeaconSearchDataProvider;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 5/7/2015.
 */
public class DocumentsAPISeaconImpl extends DocumentsAPIDefaultImpl {

    private final User activeUser;

    public DocumentsAPISeaconImpl(HttpCommunicator communicator, User activeUser) {
        super(communicator, activeUser.getCompany().getServer());
        this.activeUser = activeUser;
    }

    @Override
    public APIResponse getFormDocumentUpdates(int formWebId, String lastUpdateDate, int fromIndex, int fetchCount) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException {
        commonValidation();

        String url = getServer() + "/API/request-list-new";

        JSONObject searchParameters = new JSONObject();

        try {
            // Add date updated comparison if available
            if (lastUpdateDate != null && !lastUpdateDate.isEmpty()) {
                JSONObject dateUpdatedConditions = new JSONObject();

                dateUpdatedConditions.put("condition", ">=");
                dateUpdatedConditions.put("compared_to_date", lastUpdateDate);

                searchParameters.put("date_updated_comparison", dateUpdatedConditions);
            }

            JSONObject rangeJSON = new JSONObject();

            rangeJSON.put("from", fromIndex);
            rangeJSON.put("number_of_records", fetchCount);

            searchParameters.put("range", rangeJSON);

//            if (formWebId == SeaconSearchDataProvider.EIR_FORM_WEB_ID) {
//                // only fetch Incoming, Return, and Outgoing EIR documents
//                JSONObject filterJSON = new JSONObject();
//                JSONArray statusList = new JSONArray();
//                statusList.put("Incoming");
//                statusList.put("Return");
//                statusList.put("Outgoing");
//
//                filterJSON.put("ContainerStatus", statusList);
//                searchParameters.put("extra_conditions_by_fields", filterJSON);
//            }

            if (formWebId == SeaconSearchDataProvider.CONTAINER_INFORMATION_FORM_WEB_ID) {
                // only fetch Incoming, Return, and Outgoing EIR documents
                JSONObject filterJSON = new JSONObject();
                JSONArray statusList = new JSONArray();
                statusList.put("For Stocking in Yard");
                statusList.put("For Generating of Location");
                filterJSON.put("Status", statusList);
                searchParameters.put("extra_conditions_by_fields", filterJSON);
            }

            if (formWebId == SeaconSearchDataProvider.EIR_FORM_WEB_ID) {
                // only fetch Incoming, Return, and Outgoing EIR documents
                JSONObject filterJSON = new JSONObject();
                JSONArray statusList = new JSONArray();
                statusList.put("Incoming");
                statusList.put("Returned Container");
                statusList.put("Outgoing");
                statusList.put("For Inspection");
                statusList.put("For Release");

                filterJSON.put("Status", statusList);
                searchParameters.put("extra_conditions_by_fields", filterJSON);
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("form_id", Integer.toString(formWebId));
        requestParams.put("search_parameters", searchParameters.toString());

        return request(url, requestParams);
    }
}
