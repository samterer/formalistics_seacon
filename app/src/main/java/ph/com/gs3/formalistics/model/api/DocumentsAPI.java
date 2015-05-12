package ph.com.gs3.formalistics.model.api;

import ph.com.gs3.formalistics.model.values.application.APIResponse;

/**
 * Created by Ervinne on 4/12/2015.
 */
public interface DocumentsAPI {

    APIResponse getFormDocumentUpdates(int formWebId, String lastUpdateDate, int fromIndex, int fetchCount) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException;

    void submitDocumentAction(int formWebId, int documentWebId, String requestData, String action) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException;

    void markDocumentStar(int formId, int requestId, int starMarkInt) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException;

}
