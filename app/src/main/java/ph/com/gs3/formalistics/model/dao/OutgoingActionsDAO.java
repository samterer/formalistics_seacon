package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.tables.DocumentsTable;
import ph.com.gs3.formalistics.model.tables.OutgoingActionsTable;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.OutgoingAction;
import ph.com.gs3.formalistics.model.values.business.document.SubmitReadyAction;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class OutgoingActionsDAO extends DataAccessObject {

    public static final String TAG = OutgoingActionsDAO.class.getSimpleName();

    public OutgoingActionsDAO(Context context) {
        super(context);
    }


    public OutgoingAction saveOutgoingAction(OutgoingAction outgoingAction) throws DataAccessObjectException {
        ContentValues cv = createCVFromOutgoingAction(outgoingAction);

        long insertId = -1;

        try {
            open();
            insertId = database.insert(OutgoingActionsTable.NAME, null, cv);
            if (insertId <= 0) {
                throw new DataAccessObjectException("Saving outgoing action failed");
            }
        } finally {
            close();
        }

        return getOutgoingAction((int) insertId);

    }

    public OutgoingAction updateOutgoingAction(int outgoingActionId, JSONObject documentFieldValues, String action, int parentDocumentId)
            throws DataAccessObjectException {

        ContentValues cv = new ContentValues();

        cv.put(OutgoingActionsTable.COL_DOCUMENT_FIELDS_UPDATES_JSON,
                documentFieldValues.toString());
        cv.put(OutgoingActionsTable.COL_ACTION, action);
        cv.put(OutgoingActionsTable.COL_PARENT_DOCUMENT_ID, parentDocumentId);

        String whereClause = OutgoingActionsTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(outgoingActionId)};


        try {
            open();
            long affectedRows = database.update(OutgoingActionsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows > 0) {
                return getOutgoingAction(outgoingActionId);
            } else {
                return null;
            }
        } finally {
            close();
        }

    }

    public OutgoingAction updateOutgoingActionStar(int outgoingActionId, int isStarred)
            throws DataAccessObjectException {

        if (isStarred != -1 && isStarred != 0 && isStarred != 1) {
            throw new IllegalArgumentException("isStarred must be between -1 and 1");
        }

        ContentValues cv = new ContentValues();

        cv.put(OutgoingActionsTable.COL_IS_STARRED, isStarred);

        String whereClause = OutgoingActionsTable.COL_ID + "=?";
        String[] whereArgs = {Integer.toString(outgoingActionId)};


        try {
            open();
            long affectedRows = database.update(OutgoingActionsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows > 0) {
                return getOutgoingAction(outgoingActionId);
            } else {
                return null;
            }
        } finally {
            close();
        }

    }

    public void removeOutgoingAction(int id) throws DataAccessObjectException {

        String whereClause = OutgoingActionsTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        try {
            open();
            int affectedRows = database.delete(OutgoingActionsTable.NAME, whereClause, whereArgs);

            if (affectedRows > 1) {
                FLLogger.w(TAG,
                        "There are more than one record deleted when the app deleted outgoing action id: "
                                + id);
            }

            if (affectedRows <= 0) {
                throw new DataAccessObjectException("No deleted action with id = " + id);
            }
        } finally {
            close();
        }

    }

    public void clearOrphanedOutgoingActions() {

        FLLogger.d(TAG, "clearing orphaned outgoing actions");

        String whereClause = OutgoingActionsTable.COL_PARENT_DOCUMENT_ID + " = ?";
        String[] whereArgs = {"0"};

        try {
            open();
            int affectedRows = database.delete(OutgoingActionsTable.NAME, whereClause, whereArgs);

            if (affectedRows <= 0) {
                FLLogger.d(TAG, "No orphaned outgoing actions detected upon cleanup.");
            }
        } finally {
            close();
        }

    }

    public void updateChildOutgoingActionsForSubmition(int parentDocumentId) {

        String whereClause = OutgoingActionsTable.COL_PARENT_DOCUMENT_ID + " = ?";
        String[] whereArgs = {Integer.toString(parentDocumentId)};

        ContentValues cv = new ContentValues();
        cv.put(OutgoingActionsTable.COL_PARENT_DOCUMENT_ID, -1);


        try {
            open();
            int affectedRows = database.update(OutgoingActionsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows <= 0) {
                FLLogger.d(TAG, "No child outgoing actions were set for submition for parent id "
                        + parentDocumentId);
            } else {
                FLLogger.d(TAG, affectedRows + " child outgoing action(s) were set for submition");
            }
        } finally {
            close();
        }

    }

    public boolean hasNewChildOutgoingActions() {

        String whereClause = OutgoingActionsTable.COL_PARENT_DOCUMENT_ID + " = ?";
        String[] whereArgs = {"0"};

        String[] columns = {OutgoingActionsTable.COL_ID};

        boolean hasNewChildOutgoingActions = false;

        try {
            open();
            Cursor cursor = database.query(OutgoingActionsTable.NAME, columns, whereClause, whereArgs, null, null, null);
            hasNewChildOutgoingActions = cursor.getCount() > 0;
            cursor.close();
        } finally {
            close();
        }

        return hasNewChildOutgoingActions;

    }

    public OutgoingAction getOutgoingAction(int id) throws DataAccessObjectException {

        String whereClause = "oa." + OutgoingActionsTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        return getOutgoingAction(whereClause, whereArgs);

    }

    public OutgoingAction getOutgoingActionByDocumentId(int documentId) throws DataAccessObjectException {

        // Fetch only a action with COL_PARENT_DOCUMENT_ID = -1, any action with
        // different value is a child
        String whereClause = "oa." + OutgoingActionsTable.COL_DOCUMENT_ID + " = ? AND oa."
                + OutgoingActionsTable.COL_PARENT_DOCUMENT_ID + " = ?";
        String[] whereArgs = {Integer.toString(documentId), "-1"};

        return getOutgoingAction(whereClause, whereArgs);

    }

    public OutgoingAction getOutgoingAction(String whereClause, String[] whereArgs)
            throws DataAccessObjectException {

        OutgoingAction action = null;

        String joinedDocumentSelection = getSelectColumnsFromJoinedDocument();

        // @formatter:off
		String query = "SELECT "
		        + "oa.*, " + joinedDocumentSelection + ",  f.name AS form_name, ud.is_starred "
		        + "FROM Outgoing_Actions oa "
		        + "LEFT JOIN Documents d ON oa.document_id = d._id "
		        + "LEFT JOIN Forms f ON oa.form_id = f._id "
		        + "LEFT JOIN User_Documents ud ON d._id = ud.document_id "
		        + "WHERE " + whereClause;
		// @formatter:on

        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);

            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                // throws DataAccessObjectException
                action = cursorToOutgoingAction(cursor);
                if (action.getDocument().getId() != 0) {
                    // throws DataAccessObjectException
                    action.setDocument(DocumentsDAO.cursorToDocument(cursor, true));
                }
            }
            cursor.close();
        } finally {
            close();
        }

        return action;

    }

    public List<OutgoingAction> getAllOutgoingActions(int userId) {

        String whereClause = OutgoingActionsTable.COL_ISSUED_BY_USER_ID + " = ?";
        String[] whereArgs = {Integer.toString(userId)};

        return getOutgoingActions(whereClause, whereArgs);

    }

    public List<OutgoingAction> getOutgoingActions(String whereClause, String[] whereArgs) {
        return getOutgoingActions("", whereClause, whereArgs);
    }

    public List<OutgoingAction> getOutgoingActions(String joinClause, String whereClause, String[] whereArgs) {

        String joinedDocumentSelection = getSelectColumnsFromJoinedDocument();

        // @formatter:off
		String query = "SELECT "
		        + "oa.*, " + joinedDocumentSelection + ",  f.name AS form_name, ud.is_starred "
		        + "FROM Outgoing_Actions oa "
		        + "LEFT JOIN Documents d ON oa.document_id = d._id "
		        + "LEFT JOIN Forms f ON oa.form_id = f._id "
		        + "LEFT JOIN User_Documents ud ON d._id = ud.document_id "
                + joinClause
		        + "WHERE " + whereClause;
		// @formatter:on

        List<OutgoingAction> actions = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                try {
                    OutgoingAction action = cursorToOutgoingAction(cursor);
                    if (action.getDocument().getId() != 0) {
                        action.setDocument(DocumentsDAO.cursorToDocument(cursor, true));
                    }
                    actions.add(action);
                } catch (DataAccessObjectException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return actions;

    }


    public List<DisplayReadyAction> getAllDisplayReadyOutgoingActions(int userId) {

        List<DisplayReadyAction> actions = new ArrayList<>();

        // @formatter:off
		String query = "SELECT "
    				+ "oa._id, "
    				+ "oa.document_id, "
    				+ "d.tracking_number, "
    				+ "f._id AS form_id, "
    				+ "f.name AS form_name, "
    				+ "oa.issued_by_user_id, "
    				+ "u.display_name AS issued_by_user_display_name, "
    				+ "oa.date_issued, "
    				+ "oa.is_starred, "
    				+ "oa.action, "
    				+ "oa.document_fields_updates_json "
				+ "FROM Outgoing_Actions oa  "
    				+ "LEFT JOIN Documents d ON oa.document_id = d._id "
    				+ "LEFT JOIN Forms f ON oa.form_id = f._id "
    				+ "LEFT JOIN Users u ON oa.issued_by_user_id = u._id "
    			+ "WHERE oa.issued_by_user_id = ? AND action != ? ORDER BY date_updated DESC";
		// @formatter:on

        try {
            open();
            Cursor cursor = database.rawQuery(query, new String[]{
                    Integer.toString(userId), SubmitReadyAction.ACTION_NO_DOCUMENT_SUBMISSION
            });

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                DisplayReadyAction action = cursorToDisplayReadyAction(cursor);
                actions.add(action);
                cursor.moveToNext();
            }
            cursor.close();
        } finally {
            close();
        }

        return actions;

    }

    public List<SubmitReadyAction> getAllSubmitReadyChildOutgoingActions(
            int userId, int parentDocumentId) {

        List<SubmitReadyAction> submitReadyActions = new ArrayList<>();

        // @formatter:off
		String query = "SELECT "
    				+ "oa._id, "
    				+ "d.web_id AS document_web_id, "
    				+ "f.web_id AS form_web_id, "
    				+ "f.name AS form_name, "
    				+ "oa.document_fields_updates_json, "
    				+ "oa.action, "
    				+ "oa.is_starred, "
    				+ "c.server "
		        + "FROM Outgoing_Actions oa "
    		        + "LEFT JOIN Documents d ON oa.document_id = d._id "
    		        + "LEFT JOIN Forms f ON oa.form_id = f._id "
    		        + "LEFT JOIN Users u ON oa.issued_by_user_id = u._id "
    		        + "LEFT JOIN Companies c ON u.company_id = c._id "
		        + "WHERE oa.issued_by_user_id = ? AND oa.parent_document_id = ?";
		// @formatter:on

        String[] whereArgs = {Integer.toString(userId), Integer.toString(parentDocumentId)};

        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                submitReadyActions.add(cursorToSubmitReadyAction(cursor));
                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return submitReadyActions;

    }

    public List<SubmitReadyAction> getAllSubmitReadyOutgoingActions(int userId) {

        List<SubmitReadyAction> submitReadyActions = new ArrayList<>();

        // @formatter:off
		String query = "SELECT "
    				+ "oa._id, "
    				+ "d.web_id AS document_web_id, "
    				+ "f.web_id AS form_web_id, "
    				+ "f.name AS form_name, "
    				+ "oa.document_fields_updates_json, "
    				+ "oa.action, "
    				+ "oa.is_starred, "
    				+ "c.server "
		        + "FROM Outgoing_Actions oa "
    		        + "LEFT JOIN Documents d ON oa.document_id = d._id "
    		        + "LEFT JOIN Forms f ON oa.form_id = f._id "
    		        + "LEFT JOIN Users u ON oa.issued_by_user_id = u._id "
    		        + "LEFT JOIN Companies c ON u.company_id = c._id "
		        + "WHERE oa.issued_by_user_id = ? AND oa.parent_document_id = -1";
		// @formatter:on

        String[] whereArgs = {Integer.toString(userId)};

        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                submitReadyActions.add(cursorToSubmitReadyAction(cursor));
                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return submitReadyActions;

    }

    public String getSelectColumnsFromJoinedDocument() {

        List<String> joinedSelection = new ArrayList<>();

        for (String column : DocumentsTable.COLUMN_COLLECTION) {
            joinedSelection.add("d." + column + " AS " + DocumentsTable.JOIN_PREFIX + column);
        }

        return Serializer.serializeList(joinedSelection);

    }

    //<editor-fold desc="Converter Methods">
    private ContentValues createCVFromOutgoingAction(OutgoingAction outgoingAction) {

        ContentValues cv = new ContentValues();

        cv.put(OutgoingActionsTable.COL_DOCUMENT_ID, outgoingAction.getDocument().getId());
        cv.put(OutgoingActionsTable.COL_FORM_ID, outgoingAction.getFormId());
        cv.put(OutgoingActionsTable.COL_ISSUED_BY_USER_ID, outgoingAction.getIssuedByUser().getId());
        cv.put(OutgoingActionsTable.COL_DATE_ISSUED, outgoingAction.getDateIssued());

        if (outgoingAction.getDocumentFieldUpdates() != null) {
            cv.put(OutgoingActionsTable.COL_DOCUMENT_FIELDS_UPDATES_JSON, outgoingAction
                    .getDocumentFieldUpdates().toString());
        }

        cv.put(OutgoingActionsTable.COL_ACTION, outgoingAction.getAction());
        cv.put(OutgoingActionsTable.COL_ERROR_MESSAGE, outgoingAction.getId());
        cv.put(OutgoingActionsTable.COL_IS_STARRED, outgoingAction.getIsStarredCode());
        cv.put(OutgoingActionsTable.COL_OUTGOING_COMMENT_COUNT,
                outgoingAction.getOutgoingCommentCount());
        cv.put(OutgoingActionsTable.COL_PARENT_DOCUMENT_ID, outgoingAction.getParentDocumentId());
        cv.put(OutgoingActionsTable.COL_ERROR_MESSAGE, outgoingAction.getErrorMessage());

        return cv;

    }

    private OutgoingAction cursorToOutgoingAction(Cursor cursor) throws DataAccessObjectException {

        // @formatter:off
		int idIndex 				= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_ID);
		int documentIdIndex			= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_DOCUMENT_ID);
		int formIdIndex				= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_FORM_ID);
		int formNameIndex			= cursor.getColumnIndexOrThrow("form_name");
		int issuedByUserIdIndex 	= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_ISSUED_BY_USER_ID);
		int dateIssuedIndex 		= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_DATE_ISSUED);
		int documentFieldsUpdatesJSONIndex = cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_DOCUMENT_FIELDS_UPDATES_JSON);
		int actionIndex 			= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_ACTION);
		int starredCodeIndex		= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_IS_STARRED);
		int commentCountIndex		= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_OUTGOING_COMMENT_COUNT);
		int parentDocumentIdIndex 	= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_PARENT_DOCUMENT_ID);
		int errorMessageIndex 		= cursor.getColumnIndexOrThrow(OutgoingActionsTable.COL_ERROR_MESSAGE);

		int id 					= cursor.getInt(idIndex);
		int documentId 			= cursor.getInt(documentIdIndex);
		int formId				= cursor.getInt(formIdIndex);
		String formName 		= cursor.getString(formNameIndex);
		int userId 				= cursor.getInt(issuedByUserIdIndex);
		String dateIssued 		= cursor.getString(dateIssuedIndex);
		String documentFieldUpdatesString = cursor.getString(documentFieldsUpdatesJSONIndex);
		String action 			= cursor.getString(actionIndex);
		int starredCode			= cursor.getInt(starredCodeIndex);
		int commentCount		= cursor.getInt(commentCountIndex);
		int parentDocumentId	= cursor.getInt(parentDocumentIdIndex);
		String errorMessage 	= cursor.getString(errorMessageIndex);
		// @formatter:on

        Document emptyDocument = new Document();
        emptyDocument.setId(documentId);

        User emptyUser = new User();
        emptyUser.setId(userId);

        JSONObject fieldValuesJSON = new JSONObject();

        if (documentFieldUpdatesString != null) {
            try {
                fieldValuesJSON = new JSONObject(documentFieldUpdatesString);
            } catch (JSONException e) {
                throw new DataAccessObjectException("Failed to parse field values: " + e.getMessage(), e);
            }
        }

        OutgoingAction outgoingAction = new OutgoingAction();

        outgoingAction.setId(id);
        outgoingAction.setFormId(formId);
        outgoingAction.setFormName(formName);
        outgoingAction.setDocument(emptyDocument);
        outgoingAction.setIssuedByUser(emptyUser);
        outgoingAction.setDateIssued(dateIssued);
        outgoingAction.setDocumentFieldUpdates(fieldValuesJSON);
        outgoingAction.setAction(action);
        outgoingAction.setIsStarredCode(starredCode);
        outgoingAction.setOutgoingCommentCount(commentCount);
        outgoingAction.setParentDocumentId(parentDocumentId);
        outgoingAction.setErrorMessage(errorMessage);

        return outgoingAction;

    }

    private DisplayReadyAction cursorToDisplayReadyAction(Cursor cursor) {

        // @formatter:off
		int idIndex 						= cursor.getColumnIndexOrThrow("_id");
		int documentIdIndex 				= cursor.getColumnIndexOrThrow("document_id");
		int trackingNumberIndex 			= cursor.getColumnIndexOrThrow("tracking_number");
		int formIdIndex 					= cursor.getColumnIndexOrThrow("form_id");
		int formNameIndex 					= cursor.getColumnIndexOrThrow("form_name");
		int issuedByUserIdIndex 			= cursor.getColumnIndexOrThrow("issued_by_user_id");
		int issuedByUserDisplayNameIndex 	= cursor.getColumnIndexOrThrow("issued_by_user_display_name");
		int dateIssuedIndex					= cursor.getColumnIndexOrThrow("date_issued");
		int isStarredIndex 					= cursor.getColumnIndexOrThrow("is_starred");
		int actionIndex 					= cursor.getColumnIndexOrThrow("action");
		int documentFieldsUpdatesIndex		= cursor.getColumnIndexOrThrow("document_fields_updates_json");

		int id 							= cursor.getInt(idIndex);
		int documentId 					= cursor.getInt(documentIdIndex);
		String trackingNumber			= cursor.getString(trackingNumberIndex);
		int formId 						= cursor.getInt(formIdIndex);
		String formName 				= cursor.getString(formNameIndex);
		int issuedByUserId 				= cursor.getInt(issuedByUserIdIndex);
		String issuedByUserDisplayName 	= cursor.getString(issuedByUserDisplayNameIndex);
		String dateIssued				= cursor.getString(dateIssuedIndex);
		int isStarredInt 				= cursor.getInt(isStarredIndex);
		String action 					= cursor.getString(actionIndex);
		String documentFieldsUpdates	= cursor.getString(documentFieldsUpdatesIndex);
		// @formatter:on

        DisplayReadyAction displayReadyAction = new DisplayReadyAction();

        displayReadyAction.setId(id);
        displayReadyAction.setDocumentId(documentId);
        displayReadyAction.setTrackingNumber(trackingNumber);
        displayReadyAction.setFormId(formId);
        displayReadyAction.setFormName(formName);
        displayReadyAction.setIssuedByUserId(issuedByUserId);
        displayReadyAction.setIssuedByUserDisplayName(issuedByUserDisplayName);
        displayReadyAction.setDateIssued(dateIssued);
        displayReadyAction.setDocumentFieldUpdatesString(documentFieldsUpdates);

        List<String> issuedActions = new ArrayList<>();
        issuedActions.add(action);

        displayReadyAction.setStarredInt(isStarredInt);
        displayReadyAction.setIssuedActions(issuedActions);

        return displayReadyAction;
    }

    private SubmitReadyAction cursorToSubmitReadyAction(Cursor cursor) {

        // @formatter:off
		int idIndex 			= cursor.getColumnIndexOrThrow("_id");
		int documentWebIdIndex 	= cursor.getColumnIndexOrThrow("document_web_id");
		int formWebIdIndex 		= cursor.getColumnIndexOrThrow("form_web_id");
		int fieldUpdatesIndex 	= cursor.getColumnIndexOrThrow("document_fields_updates_json");
		int actionIndex 		= cursor.getColumnIndexOrThrow("action");
		int isStarredCodeIndex	= cursor.getColumnIndexOrThrow("is_starred");
		int serverIndex 		= cursor.getColumnIndexOrThrow("server");

		int id 						= cursor.getInt(idIndex);
		int documentWebId 		    = cursor.getInt(documentWebIdIndex);
		int formWebId 			    = cursor.getInt(formWebIdIndex);
		String fieldUpdatesString 	= cursor.getString(fieldUpdatesIndex);
		String action 				= cursor.getString(actionIndex);
		int isStarredCode			= cursor.getInt(isStarredCodeIndex);
		String server 				= cursor.getString(serverIndex);
		// @formatter:on

        SubmitReadyAction submitReadyAction = new SubmitReadyAction();

        submitReadyAction.setId(id);
        submitReadyAction.setDocumentWebId(documentWebId);
        submitReadyAction.setFormWebId(formWebId);
        submitReadyAction.setFieldUpdates(fieldUpdatesString);
        submitReadyAction.setAction(action);
        submitReadyAction.setIsStarredCode(isStarredCode);
        submitReadyAction.setServer(server);

        return submitReadyAction;

    }
    //</editor-fold>


}
