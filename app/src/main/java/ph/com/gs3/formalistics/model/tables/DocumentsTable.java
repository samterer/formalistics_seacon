package ph.com.gs3.formalistics.model.tables;

public class DocumentsTable {

    public static final String TAG = DocumentsTable.class.getSimpleName();

    // Table name
    public static final String NAME = "Documents";

    public static final String JOIN_PREFIX = "documents_";

    // Table columns
    public static final String COL_ID = "_id";
    public static final String COL_WEB_ID = "web_id";
    public static final String COL_TRACKING_NUMBER = "tracking_number";

    public static final String COL_FORM_ID = "form_id";

    public static final String COL_WORKFLOW_NODE_ID = "workflow_node_id";
    public static final String COL_WORKFLOW_ID = "workflow_id";

    public static final String COL_STATUS = "status";

    public static final String COL_PROCESSOR = "processor";
    public static final String COL_PROCESSOR_TYPE = "processor_type";
    public static final String COL_PROCESSOR_DEPARTMENT_LEVEL = "processor_department_level";
    public static final String COL_AUTHOR = "author_id";

    public static final String COL_DATE_CREATED = "date_created";
    public static final String COL_DATE_UPDATED = "date_updated";

    // Denormalized field values
    public static final String COL_FIELD_VALUES = "field_values";

    public static final String COL_COMMENTS_LAST_UPDATE_DATE = "comments_last_update_date";

    public static final String[] COLUMN_COLLECTION = {
            COL_ID, COL_WEB_ID, COL_TRACKING_NUMBER, COL_FORM_ID, COL_WORKFLOW_NODE_ID, COL_WORKFLOW_ID,
            COL_STATUS, COL_PROCESSOR, COL_PROCESSOR_TYPE, COL_PROCESSOR_DEPARTMENT_LEVEL,
            COL_AUTHOR, COL_DATE_CREATED, COL_DATE_UPDATED, COL_FIELD_VALUES, COL_COMMENTS_LAST_UPDATE_DATE};

    // @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
			+ COL_ID				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_WEB_ID 			+ " TEXT NOT NULL, "
			+ COL_TRACKING_NUMBER 	+ " TEXT NOT NULL, "

			+ COL_FORM_ID 			+ " INTEGER NOT NULL, "
			+ COL_WORKFLOW_NODE_ID 	+ " INTEGER NOT NULL, "
			+ COL_WORKFLOW_ID 		+ " INTEGER NOT NULL, "

			+ COL_STATUS			            + " TEXT NOT NULL, "

			+ COL_PROCESSOR 	                + " TEXT, "
			+ COL_PROCESSOR_TYPE 	            + " INTEGER DEFAULT 0, "
			+ COL_PROCESSOR_DEPARTMENT_LEVEL 	+ " INTEGER DEFAULT 0, "
			+ COL_AUTHOR 			            + " INTEGER NOT NULL, "

			+ COL_DATE_CREATED 		+ " TEXT NOT NULL, "
			+ COL_DATE_UPDATED 		+ " TEXT NOT NULL, "

			+ COL_FIELD_VALUES 		+ " TEXT, "
			
			+ COL_COMMENTS_LAST_UPDATE_DATE 	+ " TEXT "
		+ "); ";
	// @formatter:on

}
