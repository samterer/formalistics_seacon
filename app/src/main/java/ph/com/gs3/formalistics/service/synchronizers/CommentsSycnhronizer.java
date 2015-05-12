package ph.com.gs3.formalistics.service.synchronizers;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.constants.SyncType;
import ph.com.gs3.formalistics.global.constants.UserUpdateOptions;
import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.CommentsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator.CommunicationException;
import ph.com.gs3.formalistics.model.api.factory.APIFactory;
import ph.com.gs3.formalistics.global.utilities.TextHtmlParser;
import ph.com.gs3.formalistics.model.dao.CommentsDAO;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.facade.CommentsDataWriterFacade;
import ph.com.gs3.formalistics.model.values.application.APIResponse.InvalidResponseException;
import ph.com.gs3.formalistics.model.values.application.APIResponse.ServerErrorException;
import ph.com.gs3.formalistics.model.values.application.UnparseableObject;
import ph.com.gs3.formalistics.model.values.business.Comment;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationFailedException;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationPrematureException;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class CommentsSycnhronizer extends AbstractSynchronizer {

    public static final String TAG = CommentsSycnhronizer.class.getSimpleName();
    public static LoggingType LOGGING_TYPE;

    private UsersSynchronizer usersSynchronizer;
    private CommentsAPI commentsAPI;

    private CommentsDataWriterFacade commentsDataWriterFacade;

    private FormsDAO formsDAO;
    private DocumentsDAO documentsDAO;
    private CommentsDAO commentsDAO;

    private User activeUser;

    public CommentsSycnhronizer(Context context, User activeUser) {
        super(TAG, LOGGING_TYPE == null ? LoggingType.DISABLED : LOGGING_TYPE);

        this.activeUser = activeUser;

        usersSynchronizer = new UsersSynchronizer(context, activeUser);

        APIFactory apiFactory = new APIFactory();
        commentsAPI = apiFactory.createCommentsAPI(activeUser.getCompany().getServer());

        commentsDataWriterFacade = new CommentsDataWriterFacade(context);

        formsDAO = new FormsDAO(context);
        documentsDAO = new DocumentsDAO(context);
        commentsDAO = new CommentsDAO(context);

    }

    public void synchronize(SyncType syncType, String partialSyncFormIds) throws SynchronizationFailedException, SynchronizationPrematureException {

        log("Submitting outgoing comments");
        submitOutgoingComments();
        log("Deleting comments for deletion");
        deleteCommentsForDeletion();
        log("Cleaning up comments deleted from the server");
        deleteCommentsDeletedFromServer();

        log("Updating comments");
        List<Form> forms = null;
        try {
            if (syncType == SyncType.PARTIAL || syncType == SyncType.PARTIAL_WITH_FORMS) {
                List<String> partialSyncFormIdList = Serializer.unserializeList(partialSyncFormIds);
                forms = new ArrayList<>();
                for (String partialSyncFormId : partialSyncFormIdList) {
                    Form form = formsDAO.getForm(Integer.parseInt(partialSyncFormId), activeUser.getCompany().getId());
                    if (form != null) {
                        forms.add(form);
                    }
                }
            } else {
                forms = formsDAO.getCompanyForms(activeUser.getCompany().getId());
            }

        } catch (DataAccessObject.DataAccessObjectException e) {
            throw new SynchronizationFailedException(e);
        }

        for (Form form : forms) {
            try {
                synchronizeCommentsAndCommentAuthors(form);
            } catch (SynchronizationFailedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        log("Comment synchronization done");
    }


    public void submitOutgoingComments() {

        List<Comment> comments = commentsDAO.getAllOutgoingComments(activeUser.getId());

        for (Comment comment : comments) {
            try {
                submitOutgoingComment(comment);
            } catch (SynchronizationFailedException e) {
                Log.e(TAG, "Failed to submit comment. " + e.getMessage());
                e.printStackTrace();
            }
        }

    }

    public Comment submitOutgoingComment(Comment comment) throws SynchronizationFailedException {

        // Parse the comment text to HTML
        comment.setText(TextHtmlParser.stringToHTML(comment.getText()));
        Comment submittedComment = null;
        Comment updatedComment = null;
        try {
            submittedComment = commentsAPI.submitComment(comment);
            updatedComment = commentsDAO.updateOutgoingDocument(comment.getId(), submittedComment.getWebId(), submittedComment.getDateCreated());
        } catch (SQLiteException | CommunicationException | InvalidResponseException | ServerErrorException e) {
            throw new SynchronizationFailedException(e);
        }

        return updatedComment;

    }

    public void deleteCommentsForDeletion() throws SynchronizationFailedException {

        List<Comment> comments = commentsDAO.getAllCommentsMarkedForDeletion(activeUser.getId());
        for (Comment comment : comments) {

            try {
                // delete the comment from the server
                commentsAPI.deleteComment(comment.getWebId());
                commentsDAO.deleteComment(comment.getId());
            } catch (SQLiteException | CommunicationException | InvalidResponseException | ServerErrorException e) {
                throw new SynchronizationFailedException(e);
            }

        }

    }

    public void deleteCommentsDeletedFromServer() throws SynchronizationFailedException {
        String lastUpdateDate = commentsDAO.getLatestCommentDateCreated(activeUser.getId());

        if (lastUpdateDate == null) {
            FLLogger.d(TAG, "Skipping synch of comment deletion, no last update yet.");
            return;
        }

        List<Comment> deletedComments = null;
        try {
            deletedComments = commentsAPI.getDeletedComments(lastUpdateDate);
            int companyId = activeUser.getCompany().getId();

            for (Comment deletedComment : deletedComments) {
                commentsDAO.deleteComment(deletedComment.getWebId(), companyId);
            }
        } catch (CommunicationException | InvalidResponseException | ServerErrorException e) {
            throw new SynchronizationFailedException(e);
        } catch (SQLiteException e) {
            log("Failed deleting comment, it's probably already deleted here");
        }

    }

    private List<Comment> synchronizeCommentsAndCommentAuthors(Form form) throws SynchronizationFailedException, SynchronizationPrematureException {

        List<Comment> comments = new ArrayList<>();

        String lastUpdateDate = commentsDAO.getLatestFormDocumentsCommentDateCreated(activeUser.getId(), form.getId());
        Map<Integer, Integer> documentWebIdAndId = documentsDAO.getFormDocumentWebIdAndIdPairList(form.getId());

        if (documentWebIdAndId.size() <= 0) {
            // No requests found under this form
            return comments;
        }

        int[] documentWebIds = getDocumentWebIdArrayFromIdAndWebIdPairList(documentWebIdAndId);

        List<Comment> savedComments = new ArrayList<>();

        // get only up to 100 comments per form
        try {
            List<Comment> updatedComments = commentsAPI.getFormDocumentsCommentUpdates(
                    form.getWebId(), documentWebIds, lastUpdateDate, 0, 100);
            List<UnparseableObject> failedUpdateComments = commentsAPI.getUnparseableComments();

            for (Comment updatedComment : updatedComments) {
                // set the local document id of each comment using its document web id
                int documentId = documentWebIdAndId.get(updatedComment.getDocumentWebId());
                updatedComment.setDocumentId(documentId);

                // save the author of the comment and set it again as the comment's author
                User commentAuthor = saveOrIgnoreAuthor(updatedComment.getAuthor());
                updatedComment.setAuthor(commentAuthor);

                // save the comment to the database
                Comment savedComment = commentsDataWriterFacade.saveOrUpdateComment(updatedComment);
                savedComments.add(savedComment);

            }

            if (failedUpdateComments.size() > 0) {
                throw new SynchronizationPrematureException("There are " + failedUpdateComments.size() + " comments that failed to be downloaded.");
            }
        } catch (CommunicationException | InvalidResponseException | ServerErrorException e) {
            throw new SynchronizationFailedException(e);
        }

        return savedComments;
    }

    //<editor-fold desc="Utility Methods">
    private User saveOrIgnoreAuthor(User commentAuthor) {

        EnumSet<UserUpdateOptions> userUpdateOptions = EnumSet
                .of(UserUpdateOptions.UPDATE_EXCEPT_IS_ACTIVE, UserUpdateOptions.UPDATE_EXCEPT_PASSWORD);

        commentAuthor.setCompany(activeUser.getCompany());
        commentAuthor = usersSynchronizer.updateUser(commentAuthor, userUpdateOptions);

        return commentAuthor;

    }

    private int[] getDocumentWebIdArrayFromIdAndWebIdPairList(
            Map<Integer, Integer> idAndWebIdPairList) {

        Iterator<Integer> iterator = idAndWebIdPairList.keySet().iterator();
        int[] documentWebIdArray = new int[idAndWebIdPairList.size()];
        int index = 0;

        while (iterator.hasNext()) {
            int key = iterator.next();
            documentWebIdArray[index] = key;
            // documentWebIdArray[index] = idAndWebIdPairList.get(key);
            index++;
        }

        return documentWebIdArray;

    }
    //</editor-fold>
}
