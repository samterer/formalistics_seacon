package ph.com.gs3.formalistics.model.tables;

public class FormsTableOld {

	// Table name
	public static final String NAME = "Forms";

	// Table columns
	public static final String COL_ID = "_id";
	public static final String COL_WEB_ID = "web_id";
	public static final String COL_NAME = "name";

	public static final String COL_COMPANY_ID = "company_id";

	public static final String COL_ACTIVE_FIELDS = "active_fields";
	public static final String COL_ACTIONS = "actions";

	public static final String COL_ON_CREATE_FIELDS_ENABLED = "on_create_fields_enabled";
	public static final String COL_ON_CREATE_FIELDS_REQUIRED = "on_create_fields_required";
	public static final String COL_ON_CREATE_FIELDS_HIDDEN = "on_create_fields_hidden";

	public static final String COL_WORKFLOW_ID = "workflow_id";
	public static final String COL_WEB_TABLE_NAME = "web_table_name";

	public static final String COL_VISIBLE_TO_USER = "visible_to_user";

	public static final String COL_DOCUMENTS_LAST_UPDATE_DATE = "documents_last_update_date";

	// Table Column Collection
	public static final String[] COLUMN_COLLECTION = new String[] { COL_ID, COL_WEB_ID, COL_NAME,
	        COL_COMPANY_ID, COL_ACTIVE_FIELDS, COL_ON_CREATE_FIELDS_ENABLED,
	        COL_ON_CREATE_FIELDS_REQUIRED, COL_ON_CREATE_FIELDS_HIDDEN, COL_WORKFLOW_ID,
	        COL_WEB_TABLE_NAME, COL_VISIBLE_TO_USER, COL_DOCUMENTS_LAST_UPDATE_DATE };

	// Table Creation Query
	// @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
			+ COL_ID 							+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_WEB_ID 						+ " TEXT NOT NULL, "
			+ COL_NAME 							+ " TEXT NOT NULL, "

			+ COL_COMPANY_ID					+ " INTEGER NOT NULL, "
			
			+ COL_ACTIVE_FIELDS 				+ " TEXT NOT NULL, "
			+ COL_ACTIONS 						+ " TEXT NOT NULL, "

			+ COL_ON_CREATE_FIELDS_ENABLED 		+ " TEXT NOT NULL, "
			+ COL_ON_CREATE_FIELDS_REQUIRED 	+ " TEXT NOT NULL, "
			+ COL_ON_CREATE_FIELDS_HIDDEN 		+ " TEXT NOT NULL, "

			+ COL_WORKFLOW_ID 					+ " TEXT NOT NULL, "
			+ COL_WEB_TABLE_NAME 				+ " TEXT NOT NULL, "
			+ COL_VISIBLE_TO_USER 				+ " INTEGER DEFAULT 1, "
			+ COL_DOCUMENTS_LAST_UPDATE_DATE 	+ " TEXT "
		+ "); ";
	// @formatter:on

}
