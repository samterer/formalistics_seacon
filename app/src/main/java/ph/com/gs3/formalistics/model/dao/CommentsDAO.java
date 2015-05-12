package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.tables.CommentsTable;
import ph.com.gs3.formalistics.model.values.business.Comment;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class CommentsDAO extends DataAccessObject {

    public static final String TAG = CommentsDAO.class.getSimpleName();

    public CommentsDAO(Context context) {
        super(context);
    }

    //<editor-fold desc="Utility Query Methods">
    public String getLatestCommentDateCreated(int userId) {

        // TODO: change this later with a different last update date
        String query = "SELECT c." + CommentsTable.COL_DATE_CREATED + " FROM Comments c "
                + "LEFT JOIN User_Documents ud ON ud.document_id=c.document_id "
                + "WHERE ud.user_id = ? " + "ORDER BY c.date_created DESC LIMIT 1";

        String lastUpdateDate = null;

        try {
            open();
            Cursor cursor = database.rawQuery(query, new String[]{Integer.toString(userId)});
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int dateCreatedIndex = cursor.getColumnIndexOrThrow(CommentsTable.COL_DATE_CREATED);
                lastUpdateDate = cursor.getString(dateCreatedIndex);
            }
            cursor.close();
        } finally {
            close();
        }

        return lastUpdateDate;

    }

    public String getLatestFormDocumentsCommentDateCreated(int userId, int formId) {

        String query = "SELECT c." + CommentsTable.COL_DATE_CREATED + " FROM Comments c  "
                + "LEFT JOIN User_Documents ud ON ud.document_id=c.document_id "
                + "LEFT JOIN Documents d ON d._id = c.document_id "
                + "LEFT JOIN Forms f ON f._id = d.form_id " + "WHERE ud.user_id = ? AND f._id = ? "
                + "ORDER BY c.date_created DESC LIMIT 1";

        String lastUpdateDate = null;

        try {
            open();
            Cursor cursor = database.rawQuery(query, new String[]{Integer.toString(userId), Integer.toString(formId)});

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int dateCreatedIndex = cursor.getColumnIndexOrThrow(CommentsTable.COL_DATE_CREATED);
                lastUpdateDate = cursor.getString(dateCreatedIndex);
            }
            cursor.close();
        } finally {
            close();
        }

        return lastUpdateDate;

    }
    //</editor-fold>

    //<editor-fold desc="Get Multiple Comments Variants">
    public List<Comment> getDocumentComments(int documentId) {

        // @formatter:off
		String whereClause 	= "c." + CommentsTable.COL_DOCUMENT_ID + "=? AND "
			        		+ "c." + CommentsTable.COL_MARKED_FOR_DELETION	+ "=?";
		String[] whereArgs 	= {Integer.toString(documentId), "0"};
		// @formatter:on

        return queryComments(whereClause, whereArgs);

    }

    public List<Comment> getAllOutgoingComments(int authorId) {

        // @formatter:off
		String whereClause 	= "c." + CommentsTable.COL_AUTHOR_ID + "=? AND "
		        			+ "c." + CommentsTable.COL_IS_OUTGOING 		+ "=?";
		String[] whereArgs = {Integer.toString(authorId), "1"};
		// @formatter:on

        return queryComments(whereClause, whereArgs);
    }

    public List<Comment> getAllCommentsMarkedForDeletion(int authorId) {

        // @formatter:off
		String whereClause 	= "c." + CommentsTable.COL_AUTHOR_ID + "=? AND "
							+ "c." + CommentsTable.COL_MARKED_FOR_DELETION + "=?";
		String[] whereArgs = {Integer.toString(authorId), "1"};
		// @formatter:on

        return queryComments(whereClause, whereArgs);

    }

    public List<Comment> queryComments(String whereClause, String[] whereArgs) {
        return queryComments(whereClause, whereArgs, "ASC");
    }

    public List<Comment> queryComments(String whereClause, String[] whereArgs, String order) {

        // @formatter:off
		String query = "SELECT c.*, "
				+ "u._id AS author_id, u.web_id AS author_web_id, "
				+ "u.email AS author_email, "
				+ "u.display_name AS author_display_name, "
				+ "u.image_url AS author_image_url "
				+ "FROM Comments c "
				+ "LEFT JOIN Users u ON c.author_id=u._id "
				+ "WHERE " + whereClause + " ORDER BY c.date_created " + order;
		// @formatter:on

        List<Comment> comments = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                comments.add(cursorToComment(cursor));
                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return comments;

    }

    //</editor-fold>

    //<editor-fold desc="Get Comment Variants">
    public Comment getCommentByDbId(int id) {

        String whereClause = "c._id = ?";
        String[] whereArgs = {Integer.toString(id)};

        return queryComment(whereClause, whereArgs);

    }

    public Comment getComment(int webId, int documentId) {

        // @formatter:off
		String whereClause 	= "c." + CommentsTable.COL_WEB_ID + "=? AND "
							+ "c." + CommentsTable.COL_DOCUMENT_ID + "=?";
		String[] whereArgs 	= { Integer.toString(webId), Integer.toString(documentId) };
		// @formatter:on

        return queryComment(whereClause, whereArgs);
    }

    public Comment getCommentById(int id) {

        String whereClause = "c." + CommentsTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(id)};

        return queryComment(whereClause, whereArgs);

    }

    public Comment queryComment(String whereClause, String[] whereArgs) {

        // @formatter:off
		String query = "SELECT c.*, "
				+ "u._id AS author_id, u.web_id AS author_web_id, "
				+ "u.email AS author_email, "
				+ "u.display_name AS author_display_name, "
				+ "u.image_url AS author_image_url "
				+ "FROM Comments c "
				+ "LEFT JOIN Users u ON c.author_id=u._id "
				+ "WHERE " + whereClause;
		// @formatter:on

        Comment comment = null;

        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                comment = cursorToComment(cursor);
            }
            cursor.close();
        } finally {
            close();
        }

        return comment;
    }

    public Comment getCommentByWebId(int webId, int companyId) {

        // @formatter:off
		String query = "SELECT c.*, " + "u._id AS author_id, u.web_id AS author_web_id, "
		        + "u.email AS author_email, "
				+ "u.display_name AS author_display_name, "
		        + "u.image_url AS author_image_url "
				+ "FROM Comments c "
		        + "LEFT JOIN Users u ON c.author_id = u._id "
		        + "LEFT JOIN Companies cp ON u.company_id = cp._id "
		        + "WHERE c.web_id = ? AND cp._id = ?";
		// @formatter:on

        Comment comment = null;

        try {
            open();
            Cursor cursor = database.rawQuery(query, new String[]{
                    Integer.toString(webId),
                    Integer.toString(companyId)
            });
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                comment = cursorToComment(cursor);
            }

            cursor.close();
        } finally {
            close();
        }

        return comment;

    }
    //</editor-fold>

    //<editor-fold desc="Insert & Update Methods">
    public Comment saveComment(Comment comment) {

        ContentValues cv = createCVFromComment(comment);

        try {
            open();
            int insertId = (int) database.insertOrThrow(CommentsTable.NAME, null, cv);
            return getCommentByDbId(insertId);
        } finally {
            close();
        }

    }

    public Comment updateOutgoingDocument(int id, int resultingWebId, String dateCreated) throws SQLiteException {

        ContentValues cv = new ContentValues();
        cv.put(CommentsTable.COL_WEB_ID, resultingWebId);
        cv.put(CommentsTable.COL_DATE_CREATED, dateCreated);
        cv.put(CommentsTable.COL_IS_OUTGOING, 0);

        String whereClause = CommentsTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(id)};

        try {
            open();
            int affectedRows = database.update(CommentsTable.NAME, cv, whereClause, whereArgs);
            if (affectedRows <= 0) {
                throw new SQLiteException("Comment to update not found.");
            } else {
                return getCommentById(id);
            }
        } finally {
            close();
        }

    }

    public Comment updateComment(int webId, int documentId, Comment comment) throws SQLiteException {

        ContentValues cv = createCVFromComment(comment);

        // The web id and the document id cannot be updated
        cv.remove(CommentsTable.COL_WEB_ID);
        cv.remove(CommentsTable.COL_DOCUMENT_ID);

        String whereClause = CommentsTable.COL_WEB_ID + "=? AND " + CommentsTable.COL_DOCUMENT_ID
                + "=?";
        String[] whereArgs = {Integer.toString(webId), Integer.toString(documentId)};

        try {
            open();
            int affectedRows = database.update(CommentsTable.NAME, cv, whereClause, whereArgs);
            if (affectedRows <= 0) {
                throw new SQLiteException("Comment to update not found.");
            } else {
                return getComment(webId, documentId);
            }
        } finally {
            close();
        }

    }

    public Comment markCommentForDeletion(int id) throws SQLiteException {

        ContentValues cv = new ContentValues();

        cv.put(CommentsTable.COL_MARKED_FOR_DELETION, "1");

        String whereClause = CommentsTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(id)};

        try {
            open();
            int affectedRows = database.update(CommentsTable.NAME, cv, whereClause, whereArgs);
            if (affectedRows <= 0) {
                throw new SQLiteException("Comment to update not found.");
            } else {
                return getCommentById(id);
            }
        } finally {
            close();
        }

    }

    public void deleteComment(int webId, int authorCompanyId) throws SQLiteException {

        Comment commentToDelete = getCommentByWebId(webId, authorCompanyId);
        if (commentToDelete == null) {
            throw new SQLiteException("Comment to delete not found in the database");
        }
        deleteComment(commentToDelete.getId());

    }

    public void deleteComment(int id) throws SQLiteException {

        String whereClause = CommentsTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(id)};

        try {
            open();
            int affectedRows = database.delete(CommentsTable.NAME, whereClause, whereArgs);
            if (affectedRows <= 0) {
                throw new SQLiteException("No comments with id " + id + " found to be deleted");
            } else if (affectedRows > 1) {
                FLLogger.w(TAG, "There are more than one comments deleted when deleting comment with id " + id);
            }
        } finally {
            close();
        }

    }
    //</editor-fold>


    //<editor-fold desc="Parser Methods">
    private User cursorToUser(Cursor cursor) {

        // @formatter:off
		int idIndex 			= cursor.getColumnIndexOrThrow("author_id");
		int webIdIndex 			= cursor.getColumnIndexOrThrow("author_web_id");
		int emailIndex 			= cursor.getColumnIndexOrThrow("author_email");
		int displayNameIndex 	= cursor.getColumnIndexOrThrow("author_display_name");
		int imageURLIndex 		= cursor.getColumnIndexOrThrow("author_image_url");

		int id 				= cursor.getInt(idIndex);
		int webId 		    = cursor.getInt(webIdIndex);
		String email 		= cursor.getString(emailIndex);
		String displayName 	= cursor.getString(displayNameIndex);
		String imageURL 	= cursor.getString(imageURLIndex);
		// @formatter:on

        User user = new User();

        user.setId(id);
        user.setWebId(webId);

        user.setEmail(email);
        user.setDisplayName(displayName);

        user.setImageURL(imageURL);

        return user;

    }

    private Comment cursorToComment(Cursor cursor) {
        User author = cursorToUser(cursor);

        // @formatter:off
		int idIndex 				= cursor.getColumnIndexOrThrow(CommentsTable.COL_ID);
		int webIdIndex 				= cursor.getColumnIndexOrThrow(CommentsTable.COL_WEB_ID);

		int documentIdIndex			= cursor.getColumnIndexOrThrow(CommentsTable.COL_DOCUMENT_ID);
        int documentWebIdIndex      = cursor.getColumnIndexOrThrow(CommentsTable.COL_DOCUMENT_WEB_ID);
        int formWebIdIndex          = cursor.getColumnIndexOrThrow(CommentsTable.COL_FORM_ID);

		int textIndex 				= cursor.getColumnIndexOrThrow(CommentsTable.COL_TEXT);
		int dateCreatedIndex 		= cursor.getColumnIndexOrThrow(CommentsTable.COL_DATE_CREATED);
		int isOutgoingIndex			= cursor.getColumnIndexOrThrow(CommentsTable.COL_IS_OUTGOING);
		int markedForDeletionIndex	= cursor.getColumnIndexOrThrow(CommentsTable.COL_MARKED_FOR_DELETION);

		int id 						= cursor.getInt(idIndex);
		int webId 				    = cursor.getInt(webIdIndex);

		int documentId				= cursor.getInt(documentIdIndex);
        int documentWebId           = cursor.getInt(documentWebIdIndex);
        int formWebId               = cursor.getInt(formWebIdIndex);

		String text 				= cursor.getString(textIndex);
		String dateCreated 			= cursor.getString(dateCreatedIndex);
		boolean isOutgoing			= cursor.getInt(isOutgoingIndex) == 1;
		boolean markedForDeletion	= cursor.getInt(markedForDeletionIndex) == 1;
		// @formatter:on

        Comment comment = new Comment();

        comment.setId(id);
        comment.setWebId(webId);

        comment.setDocumentId(documentId);
        comment.setDocumentWebId(documentWebId);
        comment.setFormWebId(formWebId);

        comment.setText(text);
        comment.setDateCreated(dateCreated);
        comment.setOutgoing(isOutgoing);
        comment.setMarkedForDeletion(markedForDeletion);

        comment.setAuthor(author);

        return comment;

    }

    private ContentValues createCVFromComment(Comment comment) {

        ContentValues cv = new ContentValues();

        cv.put(CommentsTable.COL_WEB_ID, comment.getWebId());

        cv.put(CommentsTable.COL_DOCUMENT_ID, comment.getDocumentId());
        cv.put(CommentsTable.COL_DOCUMENT_WEB_ID, comment.getDocumentWebId());
        cv.put(CommentsTable.COL_FORM_ID, comment.getFormWebId());

        cv.put(CommentsTable.COL_TEXT, comment.getText());
        cv.put(CommentsTable.COL_DATE_CREATED, comment.getDateCreated());

        cv.put(CommentsTable.COL_AUTHOR_ID, comment.getAuthor().getId());
        cv.put(CommentsTable.COL_IS_OUTGOING, comment.isOutgoing() ? 1 : 0);
        cv.put(CommentsTable.COL_MARKED_FOR_DELETION, comment.isMarkedForDeletion() ? 1 : 0);

        return cv;

    }
    //</editor-fold>

}
