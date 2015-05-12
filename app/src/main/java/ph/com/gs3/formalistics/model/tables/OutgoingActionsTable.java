package ph.com.gs3.formalistics.model.tables;

public class OutgoingActionsTable {

	public static final String NAME = "Outgoing_Actions";

	public static final String COL_ID = "_id";
	public static final String COL_DOCUMENT_ID = "document_id";
	public static final String COL_FORM_ID = "form_id";
	public static final String COL_ISSUED_BY_USER_ID = "issued_by_user_id";
	public static final String COL_DATE_ISSUED = "date_issued";
	public static final String COL_DOCUMENT_FIELDS_UPDATES_JSON = "document_fields_updates_json";
	public static final String COL_ACTION = "action";
	public static final String COL_IS_STARRED = "is_starred";
	public static final String COL_OUTGOING_COMMENT_COUNT = "outgoing_comment_count";
	public static final String COL_PARENT_DOCUMENT_ID = "parent_document_id";
	public static final String COL_ERROR_MESSAGE = "error_message";

	public static final String[] COLUMN_COLLECTION = { COL_ID, COL_DOCUMENT_ID, COL_FORM_ID,
	        COL_ISSUED_BY_USER_ID, COL_DATE_ISSUED, COL_DOCUMENT_FIELDS_UPDATES_JSON, COL_ACTION,
	        COL_IS_STARRED, COL_OUTGOING_COMMENT_COUNT, COL_PARENT_DOCUMENT_ID, COL_ERROR_MESSAGE };

	// @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "(" 
			+ COL_ID						+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_DOCUMENT_ID				+ " INTEGER DEFAULT 0, "
			+ COL_FORM_ID					+ " INTEGER NOT NULL, "
			+ COL_ISSUED_BY_USER_ID			+ " INTEGER NOT NULL, "
			+ COL_DATE_ISSUED				+ " TEXT NOT NULL, "
			+ COL_DOCUMENT_FIELDS_UPDATES_JSON	+ " TEXT, "
			+ COL_ACTION					+ " TEXT NOT NULL, "
			+ COL_IS_STARRED				+ " INTEGER DEFAULT -1, "	// -1 No action, 0 unstarred, 1 starred 
			+ COL_OUTGOING_COMMENT_COUNT	+ " INTEGER DEFAULT 0, "
			+ COL_PARENT_DOCUMENT_ID		+ " INTEGER DEFAULT -1, "	// -1 No parent, 0 parent is new document, 1 or more parent is an existing document
			+ COL_ERROR_MESSAGE				+ " TEXT"
			+ ");";
	// @formatter:on
}
