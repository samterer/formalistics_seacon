package ph.com.gs3.formalistics.model.tables;

public class CommentsTable {

    // Table name
    public static final String NAME = "Comments";

    // Table columns
    public static final String COL_ID = "_id";
    public static final String COL_WEB_ID = "web_id";

    public static final String COL_DOCUMENT_ID = "document_id";
    public static final String COL_DOCUMENT_WEB_ID = "document_web_id";
    public static final String COL_FORM_ID = "form_id";
    public static final String COL_AUTHOR_ID = "author_id";

    public static final String COL_TEXT = "text";
    public static final String COL_DATE_CREATED = "date_created";

    public static final String COL_IS_OUTGOING = "is_outgoing";
    public static final String COL_MARKED_FOR_DELETION = "marked_for_deletion";

    public static final String[] COLUMN_COLLECTION = {COL_ID, COL_WEB_ID, COL_DOCUMENT_ID, COL_DOCUMENT_WEB_ID, COL_FORM_ID,
            COL_AUTHOR_ID, COL_TEXT, COL_DATE_CREATED, COL_IS_OUTGOING, COL_MARKED_FOR_DELETION};

    // @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "(" 
			+ COL_ID			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
	        + COL_WEB_ID 		+ " TEXT NOT NULL, "
	        
	        + COL_DOCUMENT_ID 	    + " INTEGER DEFAULT 0, "
	        + COL_DOCUMENT_WEB_ID 	+ " INTEGER DEFAULT 0, "
	        + COL_FORM_ID 	        + " INTEGER DEFAULT 0, "
	        + COL_AUTHOR_ID 	    + " TEXT NOT NULL, "
	        
	        + COL_TEXT 			+ " TEXT NOT NULL, "
	        + COL_DATE_CREATED 	+ " TEXT NOT NULL, "
	        
	        + COL_IS_OUTGOING 	+ " INTEGER, "
	        + COL_MARKED_FOR_DELETION 	+ " INTEGER"
	        + ");";
	// @formatter:on
}
