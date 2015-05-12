package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import ph.com.gs3.formalistics.model.dao.CommentsDAO;
import ph.com.gs3.formalistics.model.values.business.Comment;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class CommentsDataWriterFacade {

    private final CommentsDAO commentsDAO;

    public CommentsDataWriterFacade(Context context) {
        commentsDAO = new CommentsDAO(context);
    }

    public Comment saveOrUpdateComment(Comment comment) {

        int webId = comment.getWebId();
        int documentId = comment.getDocumentId();

        Comment savedComment = null;

        Comment existingComment = commentsDAO.getComment(webId, documentId);

        if (existingComment == null) {
            savedComment = commentsDAO.saveComment(comment);
        } else {
            try {
                savedComment = commentsDAO.updateComment(webId, documentId, comment);
            } catch (SQLiteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return savedComment;

    }

}
