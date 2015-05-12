package ph.com.gs3.formalistics.model.api.default_impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.API;
import ph.com.gs3.formalistics.model.api.DocumentsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;

/**
 * Created by Ervinne on 4/12/2015.
 */
public class DocumentsAPIDefaultImpl extends API implements DocumentsAPI {

    public static final String TAG = DocumentsAPIDefaultImpl.class.getSimpleName();

    private List<UnparseableObject> lastUnparseableDocuments;

    public DocumentsAPIDefaultImpl(HttpCommunicator communicator, String server) {
        super(communicator, server);

        lastUnparseableDocuments = new ArrayList<>();
    }

    @Override
    public APIResponse getFormDocumentUpdates(int formWebId, String lastUpdateDate, int fromIndex, int fetchCount) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException {
        commonValidation();

        lastUnparseableDocuments.clear();

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

        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("form_id", Integer.toString(formWebId));
        requestParams.put("search_parameters", searchParameters.toString());

        return request(url, requestParams);
    }

    @Override
    public void submitDocumentAction(int formWebId, int documentWebId, String requestData, String action) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException {

        commonValidation();

        String url;

        if (documentWebId == 0) {
            url = getServer() + "/API/create-request";
        } else {
            url = getServer() + "/API/update-request";
        }

        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("form_id", Integer.toString(formWebId));
        requestParams.put("request_id", Integer.toString(documentWebId));
        requestParams.put("request_data", requestData);
        requestParams.put("action", action);

        FLLogger.d(TAG, "url: " + url);
        FLLogger.d(TAG, "document data: " + requestData.toString());

        APIResponse response = request(url, requestParams);

        if (!response.isOperationSuccessful()) {
            throw new APIResponse.ServerErrorException(response.getErrorMessage());
        }

    }

    @Override
    public void markDocumentStar(int formId, int requestId, int starMarkInt) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException {

        commonValidation();

        String url;

        if (starMarkInt == StarMark.STARRED) {
            url = getServer() + "/API/star-document";
        } else {
            url = getServer() + "/API/unstar-document";
        }

        Map<String, String> requestParams = new HashMap<String, String>();

        requestParams.put("form_id", Integer.toString(formId));
        requestParams.put("request_id", Integer.toString(requestId));

        APIResponse response = request(url, requestParams);

        if (!response.isOperationSuccessful()) {
            throw new APIResponse.ServerErrorException(response.getErrorMessage());
        }

    }
}
