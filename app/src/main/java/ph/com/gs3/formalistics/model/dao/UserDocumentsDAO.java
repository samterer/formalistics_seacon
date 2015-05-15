package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.tables.UserDocumentsTable;

public class UserDocumentsDAO extends DataAccessObject {

    public static final String TAG = UserDocumentsDAO.class.getSimpleName();

    public UserDocumentsDAO(Context context) {
        super(context);
    }

    public UserDocumentsDAO(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        super(context, preOpenedDatabaseWithTransaction);
    }

    public void insertUserDocument(int userId, int documentId, int starMarkInt) {

        try {
            open();
            ContentValues values = createCVFromData(userId, documentId, starMarkInt);
            database.insert(UserDocumentsTable.NAME, null, values);
        } finally {
            close();
        }

    }

    public void changeDocumentStarMark(int userId, int documentId, int starMarkInt) {

        ContentValues cv = new ContentValues();
        cv.put(UserDocumentsTable.COL_IS_STARRED, starMarkInt);

        String whereClause = UserDocumentsTable.COL_USER_ID + "=? AND " + UserDocumentsTable.COL_DOCUMENT_ID + "=?";
        String[] whereArgs = {Integer.toString(userId), Integer.toString(documentId)};

        try {
            open();
            long affectedRows = database.update(UserDocumentsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows <= 0) {
                FLLogger.e(TAG, "Failed to mark document " + documentId + " as starred/unstarred");
            } else if (affectedRows > 1) {
                FLLogger.e(TAG, "More than one documents are starred/unstarred by marking document "
                        + documentId);
            }
        } finally {
            close();
        }

    }

    public void changeDocumentOutgoingMark(int documentId, boolean isOutgoing) {

        ContentValues cv = new ContentValues();
        cv.put(UserDocumentsTable.COL_IS_OUTGOING, isOutgoing ? 1 : 0);

        String whereClause = UserDocumentsTable.COL_DOCUMENT_ID + "=?";
        String[] whereArgs = {Integer.toString(documentId)};

        try {
            open();
            long affectedRows = database.update(UserDocumentsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows <= 0) {
                FLLogger.e(TAG, "Failed to mark document " + documentId + " as outgoing/normal");
            } else if (affectedRows > 1) {
                FLLogger.e(TAG, "More than one documents are marked as outgoing/normal by document "
                        + documentId);
            }
        } finally {
            close();
        }

    }

    public int getUserIdOfDocument(int documentId) {

        String whereClause = UserDocumentsTable.COL_DOCUMENT_ID + "=?";
        String[] whereArgs = {Integer.toString(documentId)};
        String[] columns = {UserDocumentsTable.COL_USER_ID};

        int userId = 0;

        try {
            open();
            Cursor cursor = database.query(UserDocumentsTable.NAME, columns, whereClause, whereArgs,
                    null, null, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                int index = cursor.getColumnIndexOrThrow(UserDocumentsTable.COL_USER_ID);
                userId = cursor.getInt(index);
            }

            cursor.close();
        } finally {
            close();
        }

        return userId;

    }

    public int getUserDocumentsCount(int userId) {

        String whereClause = UserDocumentsTable.COL_USER_ID + "=?";
        String[] whereArgs = {Integer.toString(userId)};
        String[] columns = {UserDocumentsTable.COL_USER_ID};

        int count = 0;

        try {
            open();
            Cursor cursor = database.query(UserDocumentsTable.NAME, columns, whereClause, whereArgs,
                    null, null, null);
            count = cursor.getCount();
            cursor.close();
        } finally {
            close();
        }

        return count;

    }

    public void deleteUserDocumentReference(int userId, int documentId) throws DataAccessObjectException {

        String whereClause = UserDocumentsTable.COL_USER_ID + "=? AND " + UserDocumentsTable.COL_DOCUMENT_ID + "=?";
        String[] whereArgs = {Integer.toString(userId), Integer.toString(documentId)};

        try {
            open();

            int affectedRows = database.delete(UserDocumentsTable.NAME, whereClause, whereArgs);
            if (affectedRows < 1) {
                throw new DataAccessObjectException("Failed to delete document with id " + documentId);
            }
        } finally {
            close();
        }

    }

    public ContentValues createCVFromData(int userId, int documentId, int starMarkInt) {

        ContentValues cv = new ContentValues();

        cv.put(UserDocumentsTable.COL_USER_ID, userId);
        cv.put(UserDocumentsTable.COL_DOCUMENT_ID, documentId);
        cv.put(UserDocumentsTable.COL_IS_STARRED, starMarkInt);

        return cv;

    }

}
