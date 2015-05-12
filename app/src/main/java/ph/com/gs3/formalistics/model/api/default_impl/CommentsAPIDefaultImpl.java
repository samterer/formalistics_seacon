package ph.com.gs3.formalistics.model.api.default_impl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.model.api.API;
import ph.com.gs3.formalistics.model.api.CommentsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.CommentJSONParser;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;
import ph.com.gs3.formalistics.model.values.business.Comment;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class CommentsAPIDefaultImpl extends API implements CommentsAPI {

    private List<UnparseableObject> unparseableComments;

    public CommentsAPIDefaultImpl(HttpCommunicator communicator, String server) {
        super(communicator, server);
        unparseableComments = new ArrayList<>();
    }

    private APIResponse getFormDocumentsCommentUpdatesAPIresponse(
            int formWebId, int[] documentWebIdArray, String lastUpdateDate, int rangeFrom, int rangeTo
    ) throws HttpCommunicator.CommunicationException,
            APIResponse.InvalidResponseException,
            APIResponse.ServerErrorException {

        commonValidation();

        String url = getServer() + "/API/form-requests-comments";

        JSONArray parsedDocumentWebIdList = new JSONArray();

        for (int documentWebId : documentWebIdArray) {
            parsedDocumentWebIdList.put(documentWebId);
        }

        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("form_id", Integer.toString(formWebId));
        requestParams.put("request_id_list", parsedDocumentWebIdList.toString());
        requestParams.put("range_from", Integer.toString(rangeFrom));
        requestParams.put("range_to", Integer.toString(rangeTo));

        if (lastUpdateDate != null) {
            requestParams.put("last_update_date", lastUpdateDate);
        }

        return request(url, requestParams);

    }

    @Override
    public List<Comment> getFormDocumentsCommentUpdates(
            int formWebId, int[] documentWebIdArray, String lastUpdateDate, int rangeFrom, int rangeTo
    ) throws HttpCommunicator.CommunicationException,
            APIResponse.InvalidResponseException,
            APIResponse.ServerErrorException {

        APIResponse response = getFormDocumentsCommentUpdatesAPIresponse(formWebId, documentWebIdArray, lastUpdateDate, rangeFrom, rangeTo);

        List<Comment> comments = new ArrayList<>();

        if (response.isOperationSuccessful()) {

            String commentsJSONString = response.getResults();
            JSONArray commentsJSONArray = null;

            try {
                commentsJSONArray = new JSONArray(commentsJSONString);
            } catch (JSONException e) {
                throw new APIResponse.ServerErrorException("The server response is not a valid JSON array");
            }

            int commentCount = commentsJSONArray.length();
            for (int i = 0; i < commentCount; i++) {

                String commentJSONString = null;

                try {
                    commentJSONString = commentsJSONArray.getString(i);
                    JSONObject commentJSON = new JSONObject(commentJSONString);
                    Comment comment = CommentJSONParser.createFromJSON(commentJSON);
                    comments.add(comment);
                } catch (JSONException e) {
                    unparseableComments.add(new UnparseableObject(commentJSONString, e));
                }
            }
        } else {
            throw new APIResponse.ServerErrorException(response.getErrorMessage());
        }

        return comments;
    }

    @Override
    public Comment submitComment(Comment comment)
            throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException {

        commonValidation();

        String url = getServer() + "/API/write-comment";

        Map<String, String> requestParams = new HashMap<>();

        requestParams.put("comment", comment.getText());
        requestParams.put("form_id", Integer.toString(comment.getFormWebId()));
        requestParams.put("request_id", Integer.toString(comment.getDocumentWebId()));

        APIResponse response = request(url, requestParams);

        if (response.isOperationSuccessful()) {
            String commentJSONString = response.getResults();

            try {
                JSONObject responseJSON = new JSONObject(commentJSONString);
                int commentWebId = responseJSON.getInt("id");
                String dateCreatedString = responseJSON.getString("date_created");

                comment.setWebId(commentWebId);
                comment.setDateCreated(dateCreatedString);
            } catch (JSONException e) {
                e.printStackTrace();
                throw new APIResponse.ServerErrorException(
                        "The server gave an invalid response: " + commentJSONString + ". The error was: " + e.getMessage());
            }
        } else {
            throw new APIResponse.ServerErrorException(response.getErrorMessage());
        }

        return comment;
    }

    @Override
    public void deleteComment(int commentWebId) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException {

        commonValidation();

        String url = getServer() + "/API/delete-comment";

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("comment_id", Integer.toString(commentWebId));

        APIResponse response = request(url, requestParams);

        if (!response.isOperationSuccessful()) {
            throw new APIResponse.ServerErrorException(response.getErrorMessage());
        }

    }

    @Override
    public List<Comment> getDeletedComments(String lastUpdateDate) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException {
        commonValidation();

        String url = getServer() + "/API/all-deleted-comments";

        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("last_update_date", lastUpdateDate);

        APIResponse response = request(url, requestParams);

        List<Comment> deletedComments = new ArrayList<>();

        if (response.isOperationSuccessful()) {
            String rawResultString = response.getResults();
            JSONArray rawResultsJSONArray;

            try {
                rawResultsJSONArray = new JSONArray(rawResultString);
                int commentCount = rawResultsJSONArray.length();
                for (int i = 0; i < commentCount; i++) {
                    Comment comment = CommentJSONParser.createFromJSON(rawResultsJSONArray.getJSONObject(i));
                    deletedComments.add(comment);
                }
            } catch (JSONException e) {
                throw new APIResponse.InvalidResponseException("Server gave an invalid JSON response", e);
            }

        } else {
            throw new APIResponse.ServerErrorException(response.getErrorMessage());
        }

        return deletedComments;

    }

    @Override
    public List<UnparseableObject> getUnparseableComments() {
        return unparseableComments;
    }
}
