package ph.com.gs3.formalistics.model.api.seacon_impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.default_impl.DocumentsAPIDefaultImpl;
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
