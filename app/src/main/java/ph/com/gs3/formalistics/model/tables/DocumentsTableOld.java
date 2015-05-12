package ph.com.gs3.formalistics.model.tables;

public class DocumentsTableOld {

	public static final String TAG = DocumentsTableOld.class.getSimpleName();

	// Table name
	public static final String NAME = "Documents";

	// Table columns
	public static final String COL_ID = "_id";
	public static final String COL_WEB_ID = "web_id";
	public static final String COL_TRACKING_NUMBER = "tracking_number";

	public static final String COL_FORM_ID = "form_id";

	public static final String COL_ACTIONS = "actions";

	public static final String COL_STATUS = "status";
	public static final String COL_PROCESSOR_WEB_ID = "processor_web_id";
	public static final String COL_PROCESSOR_TYPE = "processor_type";
	public static final String COL_AUTHOR = "author_id";

	public static final String COL_DATE_CREATED = "date_created";
	public static final String COL_DATE_UPDATED = "date_updated";

	public static final String COL_FIELDS_REQUIRED = "fields_required";
	public static final String COL_FIELDS_ENABLED = "fields_enabled";
	public static final String COL_FIELDS_HIDDEN = "fields_hidden";

	// Denormalized field values
	public static final String COL_FIELD_VALUES = "field_values";

	public static final String COL_COMMENTS_LAST_UPDATE_DATE = "comments_last_update_date";

	public static final String JOIN_PREFIX = "document_";

	public static final String[] COLUMN_COLLECTION = { COL_ID, COL_WEB_ID, COL_TRACKING_NUMBER,
	        COL_FORM_ID, COL_ACTIONS, COL_STATUS, COL_PROCESSOR_WEB_ID, COL_PROCESSOR_TYPE,
	        COL_AUTHOR, COL_DATE_CREATED, COL_DATE_UPDATED, COL_FIELDS_REQUIRED,
	        COL_FIELDS_ENABLED, COL_FIELDS_HIDDEN, COL_FIELD_VALUES, COL_COMMENTS_LAST_UPDATE_DATE };

	public static final String[] JOINED_COLUMN_COLLECTION = { JOIN_PREFIX + COL_ID,
	        JOIN_PREFIX + COL_WEB_ID, JOIN_PREFIX + COL_TRACKING_NUMBER, JOIN_PREFIX + COL_FORM_ID,
	        JOIN_PREFIX + COL_ACTIONS, JOIN_PREFIX + COL_STATUS,
	        JOIN_PREFIX + COL_PROCESSOR_WEB_ID, JOIN_PREFIX + COL_PROCESSOR_TYPE,
	        JOIN_PREFIX + COL_AUTHOR, JOIN_PREFIX + COL_DATE_CREATED,
	        JOIN_PREFIX + COL_DATE_UPDATED, JOIN_PREFIX + COL_FIELDS_REQUIRED,
	        JOIN_PREFIX + COL_FIELDS_ENABLED, JOIN_PREFIX + COL_FIELDS_HIDDEN,
	        JOIN_PREFIX + COL_FIELD_VALUES, JOIN_PREFIX + COL_COMMENTS_LAST_UPDATE_DATE, };

	// @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
			+ COL_ID				+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_WEB_ID 			+ " TEXT NOT NULL, "
			+ COL_TRACKING_NUMBER 	+ " TEXT NOT NULL, "

			+ COL_FORM_ID 			+ " INTEGER NOT NULL, "
			+ COL_ACTIONS 			+ " TEXT NOT NULL, "

			+ COL_STATUS			+ " TEXT NOT NULL, "
			+ COL_PROCESSOR_WEB_ID 	+ " INTEGER NOT NULL, "
			+ COL_PROCESSOR_TYPE 	+ " INTEGER DEFAULT 0, "
			+ COL_AUTHOR 			+ " INTEGER NOT NULL, "

			+ COL_DATE_CREATED 		+ " TEXT NOT NULL, "
			+ COL_DATE_UPDATED 		+ " TEXT NOT NULL, "

			+ COL_FIELDS_REQUIRED 	+ " TEXT, "
			+ COL_FIELDS_ENABLED 	+ " TEXT, "
			+ COL_FIELDS_HIDDEN 	+ " TEXT, "
			
			+ COL_FIELD_VALUES 		+ " TEXT, "
			
			+ COL_COMMENTS_LAST_UPDATE_DATE 	+ " TEXT "
		+ "); ";
	// @formatter:on

}
