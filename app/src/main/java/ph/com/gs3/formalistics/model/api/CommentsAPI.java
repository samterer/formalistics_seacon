package ph.com.gs3.formalistics.model.api;

import java.util.List;

import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;
import ph.com.gs3.formalistics.model.values.business.Comment;

/**
 * Created by Ervinne on 4/13/2015.
 */
public interface CommentsAPI {

    List<Comment> getFormDocumentsCommentUpdates(
            int formWebId, int[] documentWebIdArray, String lastUpdateDate, int rangeFrom, int rangeTo) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException;

    Comment submitComment(Comment comment) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException;

    void deleteComment(int commentWebId) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException;

    List<Comment> getDeletedComments(String lastUpdateDate) throws HttpCommunicator.CommunicationException, APIResponse.InvalidResponseException, APIResponse.ServerErrorException;

    List<UnparseableObject> getUnparseableComments();

}
