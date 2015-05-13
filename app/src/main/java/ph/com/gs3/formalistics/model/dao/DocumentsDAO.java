package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ph.com.gs3.formalistics.global.constants.ProcessorType;
import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.WorkflowActionJSONParser;
import ph.com.gs3.formalistics.model.tables.DocumentsTable;
import ph.com.gs3.formalistics.model.tables.UserDocumentsTable;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;

public class DocumentsDAO extends DataAccessObject {

    public static final String TAG = DocumentsDAO.class.getSimpleName();

    public DocumentsDAO(Context context) {
        super(context);
    }

    public DocumentsDAO(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        super(context, preOpenedDatabaseWithTransaction);
    }

    //<editor-fold desc=" Insert & Update Methods">
    public Document saveDocument(Document document) {

        // Throws JSONException
        ContentValues cv = createCVFromDocument(document);

        try {
            open();
            long insertId = database.insert(DocumentsTable.NAME, null, cv);

            if (insertId > 0) {
                return getDocument((int) insertId);
            }
        } finally {
            close();
        }

        return null;
    }

    /**
     * Updates a document in the database using the document argument's web id and form id.
     *
     * @param document
     * @param userId
     * @return
     * @throws DataAccessObjectException
     */
    public Document updateDocument(Document document, int userId) {

        // Throws DataAccessObjectException
        ContentValues cv = createCVFromDocument(document);

        // @formatter:off
		String whereClause 	= DocumentsTable.COL_WEB_ID + " = ? AND " 
							+ DocumentsTable.COL_FORM_ID + " = ?";
		String[] whereArgs 	= { Integer.toString(document.getWebId()), Integer.toString(document.getFormId()) };
		// @formatter:on

        try {
            open();
            long affectedRows = database.update(DocumentsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows > 1) {
                FLLogger.w(TAG,
                        "More than one documents where updated with webId = " + document.getWebId()
                                + " and formId = " + document.getFormId());
            }

            return getDocument(document.getWebId(), document.getFormId(), userId);
        } finally {
            close();
        }

    }

    //</editor-fold>

    //<editor-fold desc="Get Document Method Variants">
    public Document getDocument(int webId, int formId, int userId) {

        // @formatter:off
		String whereClause 	= DocumentsTable.COL_WEB_ID + " = ? AND " 
							+ DocumentsTable.COL_FORM_ID + " = ? AND "
							+ "ud.user_id = ?";
		String[] whereArgs 	= { Integer.toString(webId), Integer.toString(formId), Integer.toString(userId) };
		// @formatter:on

        return getDocument(whereClause, whereArgs);
    }

    public Document getDocument(int id) {

        String whereClause = DocumentsTable.COL_ID + " = ?";
        String[] whereArgs = {Integer.toString(id)};

        return getDocument(whereClause, whereArgs);

    }

    public Document getDocument(String whereClause, String[] whereArgs) {

        String query = "SELECT d.*, ud.is_starred FROM Documents d "
                + "LEFT JOIN User_Documents ud ON d._id = ud.document_id " + "WHERE " + whereClause;

        Document document = null;
        try {
            open();
            Cursor cursor = database.rawQuery(query, whereArgs);

            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                document = cursorToDocument(cursor, false);
            }

            cursor.close();
        } finally {
            close();
        }

        return document;

    }
    //</editor-fold>

    // <editor-fold desc="Get Document Summaries Method Variants">

    public List<DocumentSummary> getUserDocumentSummaries(int userId, int rangeFrom, int fetchCount) throws JSONException {

        String whereClause = "ud.user_id = " + userId;
        return getUserDocumentSummaries(null, whereClause, rangeFrom, fetchCount);

    }

    public List<DocumentSummary> getStarredDocumentSummaries(int userId, int rangeFrom, int fetchCount) throws JSONException {

        String whereClause = "ud.user_id = " + userId + " AND ud.is_starred = " + StarMark.STARRED;
        return getUserDocumentSummaries(null, whereClause, rangeFrom, fetchCount);

    }

    public List<DocumentSummary> searchForUserDocumentSummaries(User user, List<Form> forms, String genericStringFilter) throws JSONException {
        return searchForUserDocumentSummaries(user, forms, null, null, null, genericStringFilter);
    }

    public List<DocumentSummary> searchForUserDocumentSummaries(User user, List<Form> forms, List<SearchCondition> searchConditions, String manualJoins, String manualConditions, String genericStringFilter) throws JSONException {

        int userId = user.getId();

        List<SearchCondition> formFilterSearchConditions = new ArrayList<>();
        List<SearchCondition> genericFilterSearchConditions = new ArrayList<>();
        List<SearchCondition> otherSearchConditions = new ArrayList<>();
        otherSearchConditions.add(new SearchCondition("ud.user_id", "=", Integer.toString(userId)));

        for (Form form : forms) {
            formFilterSearchConditions.add(new SearchCondition("d.form_id", "=", Integer.toString(form.getId())));
        }

        if (searchConditions != null) {
            otherSearchConditions.addAll(searchConditions);
        }

        if (genericStringFilter != null && !genericStringFilter.trim().isEmpty()) {
            genericFilterSearchConditions.addAll(generateConditionsFromGenericStringFilter(genericStringFilter, forms));
        }

        String joinClause = generateJoinClauseFromForms(forms, "d._id", "document_id");
        String whereClause = generateWhereClauseFromConditions(otherSearchConditions);
        if (genericFilterSearchConditions.size() > 0) {
            whereClause += " AND " + generateWhereClauseFromConditions(genericFilterSearchConditions, "OR");
        }

        if (formFilterSearchConditions.size() > 0) {
            whereClause += " AND " + generateWhereClauseFromConditions(formFilterSearchConditions, "OR");
        }

        if (manualConditions != null && !"".equals(manualConditions.trim())) {
            whereClause += " AND " + manualConditions;
        }

        if (joinClause != null && !"".equals(joinClause) && manualJoins != null && !"".equals(manualJoins)) {
            joinClause += " " + manualJoins;
        }

        return getUserDocumentSummaries(joinClause, whereClause);

    }

    public List<DocumentSummary> getUserDocumentSummaries(String joinClause, String whereClause) throws JSONException {
        return getUserDocumentSummaries(joinClause, whereClause, 0, 20);
    }

    public List<DocumentSummary> getUserDocumentSummaries(String joinClause, String whereClause, int limitFrom, int fetchCount) throws JSONException {

        if (joinClause == null) {
            joinClause = "";
        }

        if (whereClause == null) {
            whereClause = "";
        }

        List<DocumentSummary> documentSummaries = new ArrayList<>();

        // @formatter:off
		String query = "SELECT 	d._id AS document_id, "
						+ "d.tracking_number, "
        				+ "d.status, "
        				+ "f._id AS form_id, "
        				+ "f.name AS form_name, "
        				+ "d.author_id, "
        				+ "d.processor, "
        				+ "d.processor_type, "
        				+ "author.display_name AS author_display_name, "
        				+ "ud.is_starred, "
        				+ "wo.actions, "
                        + "d.field_values, "
        				+ "d.date_updated, "
                        + "oa._id AS outgoing_action_id, "
        				+ "(SELECT COUNT(_id) FROM comments c WHERE c.document_id = d._id) AS comment_count "
    				+ "FROM Documents d	"
        				+ "LEFT JOIN User_Documents ud ON d._id = ud.document_id "
        				+ "LEFT JOIN Forms f ON d.form_id = f._id "
        				+ "LEFT JOIN Users author ON d.author_id = author._id "
                        + "LEFT JOIN Outgoing_Actions oa ON d._id = oa.document_id "
                        + "LEFT JOIN Forms_Workflow_Objects wo ON "
                            + "(d.workflow_node_id = wo.node_id AND d.workflow_id = wo.workflow_id) "
                        + joinClause + " "
                    + "WHERE " + whereClause + " "
                    + "GROUP BY d._id ORDER BY date_updated, date_created DESC LIMIT " + limitFrom + ", " + fetchCount;
		// @formatter:on

        FLLogger.d(TAG, "Generated query: " + query);

        try {
            open();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                documentSummaries.add(cursorToDocumentSummary(cursor));
                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return documentSummaries;

    }
    // </editor-fold>

    //<editor-fold desc="Other Query Methods">

    public List<String> getFormDocumentIdList(int formId) {

        String whereClause = DocumentsTable.COL_FORM_ID + " = ?";
        String[] whereArgs = {Integer.toString(formId)};
        String[] columns = {DocumentsTable.COL_WEB_ID};

        List<String> idList = new ArrayList<>();

        try {
            open();
            Cursor cursor = database.query(DocumentsTable.NAME, columns, whereClause, whereArgs, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                int index = cursor.getColumnIndexOrThrow(DocumentsTable.COL_WEB_ID);
                idList.add(cursor.getString(index));

                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return idList;

    }

    public Map<Integer, Integer> getFormDocumentWebIdAndIdPairList(int formId) {

        String whereClause = DocumentsTable.COL_FORM_ID + " = ?";
        String[] whereArgs = {Integer.toString(formId)};
        String[] columns = {DocumentsTable.COL_ID, DocumentsTable.COL_WEB_ID};

        Map<Integer, Integer> documentIdAndWebId = new HashMap<>();

        try {
            open();
            Cursor cursor = database.query(DocumentsTable.NAME, columns, whereClause, whereArgs, null,
                    null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                int idIndex = cursor.getColumnIndexOrThrow(DocumentsTable.COL_ID);
                int webIdIndex = cursor.getColumnIndexOrThrow(DocumentsTable.COL_WEB_ID);

                int id = cursor.getInt(idIndex);
                int webId = cursor.getInt(webIdIndex);

                documentIdAndWebId.put(webId, id);

                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return documentIdAndWebId;

    }
    //</editor-fold>

    /**
     * Generates a where clause for fetching documents that are under the specified user's approval
     *
     * @param user
     * @return
     */
    public static String getForApprovalWhereClause(User user) {

        String whereClause = "((wo.processor_type = %d AND wo.processor = %d) " +
                "OR (wo.processor_type = %d AND wo.processor = %d) " +
                "OR (wo.processor_type = %d AND d.author_id = %d)) ";

        return String.format(whereClause,
                ProcessorType.COMPANY_POSITION, user.getPositionId(),
                ProcessorType.PERSON, user.getWebId(),
                ProcessorType.AUTHOR, user.getWebId()
        );

    }

    //<editor-fold desc="Parsers">

    protected static ContentValues createCVFromDocument(Document document) {

        ContentValues cv = new ContentValues();

        JSONArray rawActions = new JSONArray();

        cv.put(DocumentsTable.COL_WEB_ID, document.getWebId());
        cv.put(DocumentsTable.COL_TRACKING_NUMBER, document.getTrackingNumber());

        cv.put(DocumentsTable.COL_FORM_ID, document.getFormId());

        cv.put(DocumentsTable.COL_WORKFLOW_NODE_ID, document.getWorkflowNodeId());
        cv.put(DocumentsTable.COL_WORKFLOW_ID, document.getWorkflowId());

        cv.put(DocumentsTable.COL_STATUS, document.getStatus());

        cv.put(DocumentsTable.COL_PROCESSOR, document.getProcessor());
        cv.put(DocumentsTable.COL_PROCESSOR_TYPE, document.getProcessorType());
        cv.put(DocumentsTable.COL_PROCESSOR_DEPARTMENT_LEVEL, document.getProcessorType());

        cv.put(DocumentsTable.COL_AUTHOR, document.getAuthorId());

        cv.put(DocumentsTable.COL_DATE_CREATED, document.getDateCreated());
        cv.put(DocumentsTable.COL_DATE_UPDATED, document.getDateUpdated());

        cv.put(DocumentsTable.COL_FIELD_VALUES, document.getFieldValuesJSONString());
        cv.put(DocumentsTable.COL_COMMENTS_LAST_UPDATE_DATE, document.getCommentsLastUpdateDate());

        return cv;

    }

    public static Document cursorToDocument(Cursor cursor, boolean isJoined) {

        // @formatter:off
		String idCol				= DocumentsTable.COL_ID;
		String webIdCol				= DocumentsTable.COL_WEB_ID;
		String trackingNumberCol	= DocumentsTable.COL_TRACKING_NUMBER;
		
		String formIdCol			= DocumentsTable.COL_FORM_ID;
		String workflowNodeIdCol    = DocumentsTable.COL_WORKFLOW_NODE_ID;
		String workflowIdCol        = DocumentsTable.COL_WORKFLOW_ID;

		String statusCol			= DocumentsTable.COL_STATUS;

		String processorCol	                = DocumentsTable.COL_PROCESSOR;
		String processorTypeCol		        = DocumentsTable.COL_PROCESSOR_TYPE;
		String processorDepartmentLevelCol	= DocumentsTable.COL_PROCESSOR_DEPARTMENT_LEVEL;
		String authorIdCol			        = DocumentsTable.COL_AUTHOR;
		
		String dateCreatedCol 		= DocumentsTable.COL_DATE_CREATED;
		String dateUpdatedCol 		= DocumentsTable.COL_DATE_UPDATED;

		String fieldValuesCol		= DocumentsTable.COL_FIELD_VALUES;
		String isStarredCol 		= UserDocumentsTable.COL_IS_STARRED;

		String commentsLastUpdateCol	= DocumentsTable.COL_COMMENTS_LAST_UPDATE_DATE;
		// @formatter:on

        if (isJoined) {
            idCol = DocumentsTable.JOIN_PREFIX + idCol;
            webIdCol = DocumentsTable.JOIN_PREFIX + webIdCol;
            trackingNumberCol = DocumentsTable.JOIN_PREFIX + trackingNumberCol;

            formIdCol = DocumentsTable.JOIN_PREFIX + formIdCol;
            workflowNodeIdCol = DocumentsTable.JOIN_PREFIX + workflowNodeIdCol;
            workflowIdCol = DocumentsTable.JOIN_PREFIX + workflowIdCol;

            statusCol = DocumentsTable.JOIN_PREFIX + statusCol;

            processorCol = DocumentsTable.JOIN_PREFIX + processorCol;
            processorTypeCol = DocumentsTable.JOIN_PREFIX + processorTypeCol;
            processorDepartmentLevelCol = DocumentsTable.JOIN_PREFIX + processorDepartmentLevelCol;
            authorIdCol = DocumentsTable.JOIN_PREFIX + authorIdCol;

            dateCreatedCol = DocumentsTable.JOIN_PREFIX + dateCreatedCol;
            dateUpdatedCol = DocumentsTable.JOIN_PREFIX + dateUpdatedCol;

            fieldValuesCol = DocumentsTable.JOIN_PREFIX + fieldValuesCol;
            commentsLastUpdateCol = DocumentsTable.JOIN_PREFIX + commentsLastUpdateCol;

        }

        // @formatter:off
		// Initialize indices

		int idIndex 			= cursor.getColumnIndexOrThrow(idCol);
		int webIdIndex 			= cursor.getColumnIndexOrThrow(webIdCol);
		int trackingNumberIndex = cursor.getColumnIndexOrThrow(trackingNumberCol);
		
		int formIdIndex 		= cursor.getColumnIndexOrThrow(formIdCol);
        int workflowNodeIdIndex = cursor.getColumnIndexOrThrow(workflowNodeIdCol);
        int workflowIdIndex     = cursor.getColumnIndexOrThrow(workflowIdCol);

		int statusIndex			= cursor.getColumnIndexOrThrow(statusCol);

		int processorTypeIndex	            = cursor.getColumnIndexOrThrow(processorTypeCol);
		int processorDepartmentLevelIndex	= cursor.getColumnIndexOrThrow(processorDepartmentLevelCol);
		int processorIndex                  = cursor.getColumnIndexOrThrow(processorCol);
		int authorIdIndex 		            = cursor.getColumnIndexOrThrow(authorIdCol);
				
		int dateCreatedIndex	= cursor.getColumnIndexOrThrow(dateCreatedCol);
		int dateUpdatedIndex	= cursor.getColumnIndexOrThrow(dateUpdatedCol);

		int fieldsValuesIndex 	= cursor.getColumnIndexOrThrow(fieldValuesCol);				
		int isStarredIndex 		= cursor.getColumnIndexOrThrow(isStarredCol);
		
		int commentsLastUpdateDateIndex = cursor.getColumnIndexOrThrow(commentsLastUpdateCol);
		
		// @formatter:on

        int id = cursor.getInt(idIndex);
        int webId = cursor.getInt(webIdIndex);
        String trackingNumber = cursor.getString(trackingNumberIndex);

        int formId = cursor.getInt(formIdIndex);
        String workflowNodeId = cursor.getString(workflowNodeIdIndex);
        int workflowId = cursor.getInt(workflowIdIndex);

        String status = cursor.getString(statusIndex);

        int processorType = cursor.getInt(processorTypeIndex);
        int processorDepartmentLevel = cursor.getInt(processorDepartmentLevelIndex);
        String processor = cursor.getString(processorIndex);
        int authorId = cursor.getInt(authorIdIndex);

        String dateCreated = cursor.getString(dateCreatedIndex);
        String dateUpdated = cursor.getString(dateUpdatedIndex);

        String fieldValuesJSONString = cursor.getString(fieldsValuesIndex);
        int isStarredInt = cursor.getInt(isStarredIndex);

        String commentsLastUpdateDate = cursor.getString(commentsLastUpdateDateIndex);

        Document document = new Document();

        document.setId(id);
        document.setWebId(webId);
        document.setTrackingNumber(trackingNumber);

        document.setFormId(formId);
        document.setWorkflowNodeId(workflowNodeId);
        document.setWorkflowId(workflowId);

        document.setStatus(status);

        document.setProcessor(processor);
        document.setProcessorType(processorType);
        document.setProcessorDepartmentLevel(processorDepartmentLevel);
        document.setAuthorId(authorId);

        document.setDateCreated(dateCreated);
        document.setDateUpdated(dateUpdated);

        document.setFieldValuesJSONString(fieldValuesJSONString);
        document.setStarMark(isStarredInt);

        document.setCommentsLastUpdateDate(commentsLastUpdateDate);

        return document;

    }

    public static DocumentSummary cursorToDocumentSummary(Cursor cursor) throws JSONException {

        // @formatter:off
		// Initialize Indices
		int documentIdIndex 		= cursor.getColumnIndexOrThrow("document_id");
		int trackingNumberIndex		= cursor.getColumnIndexOrThrow("tracking_number");
		int statusIndex 			= cursor.getColumnIndexOrThrow("status");
		int formIdIndex				= cursor.getColumnIndexOrThrow("form_id");
		int formNameIndex 			= cursor.getColumnIndexOrThrow("form_name");
		int authorIdIndex 			= cursor.getColumnIndexOrThrow("author_id");
        int processorTypeIndex      = cursor.getColumnIndexOrThrow("processor_type");
		int processorIndex 		    = cursor.getColumnIndexOrThrow("processor");
		int authorDisplayNameIndex 	= cursor.getColumnIndexOrThrow("author_display_name");
		int isStarredIndex 			= cursor.getColumnIndexOrThrow("is_starred");
		int actionsIndex 			= cursor.getColumnIndexOrThrow("actions");
		int fieldValuesIndex 		= cursor.getColumnIndexOrThrow("field_values");
		int dateUpdatedIndex 		= cursor.getColumnIndexOrThrow("date_updated");
		int commentCountIndex 		= cursor.getColumnIndexOrThrow("comment_count");		
		
		// Get Values
		int documentId 				= cursor.getInt(documentIdIndex);
		String trackingNumber		= cursor.getString(trackingNumberIndex);
		String status 				= cursor.getString(statusIndex);
		int formId					= cursor.getInt(formIdIndex);
		String formName 			= cursor.getString(formNameIndex);
		int authorId 				= cursor.getInt(authorIdIndex);
        int processorType           = cursor.getInt(processorTypeIndex);
		String processor            = cursor.getString(processorIndex);
		String authorDisplayName 	= cursor.getString(authorDisplayNameIndex);
		String dateUpdatedString 	= cursor.getString(dateUpdatedIndex);
		int commentCount 			= cursor.getInt(commentCountIndex);
		
		int starMarkInt 	= cursor.getInt(isStarredIndex);

		String fieldValuesString 			= cursor.getString(fieldValuesIndex);
		JSONObject fieldValuesJSONObject 	= new JSONObject(fieldValuesString);
		// @formatter:on

        DocumentSummary summary = new DocumentSummary();

        summary.setDocumentId(documentId);
        summary.setTrackingNumber(trackingNumber);
        summary.setStatus(status);
        summary.setFormId(formId);
        summary.setFormName(formName);
        summary.setAuthorId(authorId);

        summary.setProcessorType(processorType);
        summary.setProcessor(processor);
        summary.setAuthorDisplayName(authorDisplayName);

        summary.setStarMarkInt(starMarkInt);
        summary.setFieldValuesJSON(fieldValuesJSONObject);

        summary.setDateUpdatedString(dateUpdatedString);
        summary.setCommentCount(commentCount);

        String actionsString = cursor.getString(actionsIndex);
        if (actionsString != null && !"".equals(actionsString)) {
            JSONArray actionsJSONArray = new JSONArray(actionsString);
            List<WorkflowAction> actions = WorkflowActionJSONParser.createFromJSON(actionsJSONArray);
            summary.setActions(actions);
        } else {
//            FLLogger.d(TAG, "Invalid actions: " + actionsString);
            summary.setActions(new ArrayList<WorkflowAction>());
        }

        return summary;
    }

    //</editor-fold>
}
