package ph.com.gs3.formalistics.model.tables;

public class UserDocumentsTable {

	public static final String NAME = "User_Documents";

	public static final String COL_USER_ID = "user_id";
	public static final String COL_DOCUMENT_ID = "document_id";
	public static final String COL_IS_OUTGOING = "is_outgoing";
	public static final String COL_IS_STARRED = "is_starred";	
	
	public static final String[] COLUMN_COLLECTION = { COL_USER_ID, COL_DOCUMENT_ID,
	        COL_IS_OUTGOING, COL_IS_STARRED };

	// @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "(" 
			+ COL_USER_ID		+ " INTEGER NOT NULL, "
			+ COL_DOCUMENT_ID	+ " INTEGER NOT NULL, "
			+ COL_IS_OUTGOING	+ " INTEGER DEFAULT 0, "
			+ COL_IS_STARRED	+ " INTEGER DEFAULT 0"
			+ ");";

}
