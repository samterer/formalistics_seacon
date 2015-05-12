package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.SessionMode;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.model.dao.CommentsDAO;
import ph.com.gs3.formalistics.model.dao.facade.CommentsDataWriterFacade;
import ph.com.gs3.formalistics.model.values.business.Comment;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.presenter.fragment.view.CommentsViewFragment;
import ph.com.gs3.formalistics.service.managers.SessionManager;

public class CommentsActivity extends Activity implements CommentsViewFragment.CommentListViewActionListener {


    public static final String EXTRA_ACTIVE_USER = "active_user";
    public static final String EXTRA_DOCUMENT_ID = "document_id";
    public static final String EXTRA_DOCUMENT_WEB_ID = "document_web_id";
    public static final String EXTRA_FORM_WEB_ID = "form_web_id";

    private User activeUser;
    private int documentId;
    private int documentWebId;
    private int formWebId;

    private CommentsDAO commentsDAO;
    private CommentsDataWriterFacade commentsDataWriterFacade;

    private CommentsViewFragment commentsViewFragment;

    //<editor-fold desc="Activity Life Cycle">
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        initializeStateTransferredFields();

        commentsDAO = new CommentsDAO(this);
        commentsDataWriterFacade = new CommentsDataWriterFacade(this);

        commentsViewFragment = (CommentsViewFragment) getFragmentManager().findFragmentByTag(CommentsViewFragment.TAG);

        if (savedInstanceState == null) {
            if (commentsViewFragment == null) {
                commentsViewFragment = new CommentsViewFragment();
            }
            getFragmentManager().beginTransaction().add(R.id.container, commentsViewFragment, CommentsViewFragment.TAG).commit();
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete_comment) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            deleteComment(commentsViewFragment.getCommentsOnDisplay().get(info.position));

            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        int position = info.position;

        Comment selectedComment = commentsViewFragment.getCommentsOnDisplay().get(position);

        // If the author of this comment is the same as the current user
        if (activeUser.getId() == selectedComment.getAuthor().getId()) {
            getMenuInflater().inflate(R.menu.comment_list_item, menu);
        } else {
            getMenuInflater().inflate(R.menu.comment_list_item_no_delete, menu);

        }

    }
    //</editor-fold>

    private void initializeStateTransferredFields() {

        Bundle extras = getIntent().getExtras();

        activeUser = (User) extras.getSerializable(EXTRA_ACTIVE_USER);
        documentId = extras.getInt(EXTRA_DOCUMENT_ID);
        documentWebId = extras.getInt(EXTRA_DOCUMENT_WEB_ID);
        formWebId = extras.getInt(EXTRA_FORM_WEB_ID);

    }

    private void deleteComment(Comment comment) {

        // Validate if the comment is the user's own comment
        if (comment.getAuthor().getId() != activeUser.getId()) {
            Toast.makeText(this, "You cannot delete this comment", Toast.LENGTH_LONG).show();
            return;
        }

        // Validate if the comment is still being processed
        if (comment.isCurrentlyBeingProcessed()) {
            Toast.makeText(this, "This comment is still being processed, try again later.", Toast.LENGTH_LONG).show();
            return;
        }

        if (comment.isOutgoing() && !comment.isCurrentlyBeingProcessed()) {

            // Delete the comment in the database
            try {
                commentsDAO.deleteComment(comment.getId());
            } catch (SQLiteException e) {
                Toast.makeText(CommentsActivity.this, "Failed to delete comment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else if (!comment.isOutgoing()) {
            // Mark the comment for deletion then run the service for deleting comments
            try {
                commentsDAO.markCommentForDeletion(comment.getId());
                if (SessionManager.getApplicationInstance().getSessionMode() == SessionMode.ONLINE) {
                    startDeleteCommentsService();
                }
            } catch (SQLiteException e) {
                Toast.makeText(CommentsActivity.this, "Failed to delete comment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }

        // Update the UI
        commentsViewFragment.removeComment(comment);

    }

    private void startDeleteCommentsService() {

        // Intent deleteCommentsService = new Intent(CommentsActivity.this,
        // DeleteCommentsMarkedForDeletionService.class);
        // deleteCommentsService.putExtra(OutgoingCommentsSubmitService.EXTRA_ACTIVE_USER,
        // activeUser);
        // startService(deleteCommentsService);

    }

    @Override
    public void onViewReady() {
        List<Comment> comments = commentsDAO.getDocumentComments(documentId);
        commentsViewFragment.setComments(comments);
    }

    @Override
    public void onSubmitCommentCommand(String commentText) {

        Comment comment = new Comment();
        comment.setAuthor(activeUser);
        comment.setText(commentText);
        comment.setDocumentId(documentId);
        comment.setDocumentWebId(documentWebId);
        comment.setFormWebId(formWebId);
        comment.setCurrentlyBeingProcessed(true);

        // Give the comment a stub id using the current date and time
        comment.setPendingStubId(DateUtilities.getServerFormattedCurrentDateTime());

        comment.setWebId(0);
        comment.setOutgoing(true);
        comment.setDateCreated(DateUtilities.getServerFormattedCurrentDateTime());

        Comment updatedComment = commentsDAO.saveComment(comment);

        // // Submit the comment
        // Comment updatedComment = startSubmitCommentService(comment);

        // Update the UI
        commentsViewFragment.addComment(updatedComment);

    }


}
