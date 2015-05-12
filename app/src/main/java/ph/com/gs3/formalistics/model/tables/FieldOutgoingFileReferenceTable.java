package ph.com.gs3.formalistics.model.tables;

/**
 * Created by Ervinne on 4/23/2015.
 */
public class FieldOutgoingFileReferenceTable {

    public static final String NAME = "Field_Outgoing_File_Reference";

    public static final String COL_OUTGOING_FILE_ID = "outgoing_file_id";
    public static final String COL_FORM_ID = "form_id";
    public static final String COL_OUTGOING_ACTION_ID = "outgoing_action_id";
    public static final String COL_FIELD_NAME = "field_name";

    public static final String[] COLUMN_COLLECTION = {
            COL_OUTGOING_FILE_ID, COL_FORM_ID, COL_OUTGOING_ACTION_ID, COL_FIELD_NAME
    };

    // @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
            + COL_OUTGOING_FILE_ID	    + " INTEGER PRIMARY KEY, "
            + COL_FORM_ID 	            + " INTEGER DEFAULT 0, "
            + COL_OUTGOING_ACTION_ID	+ " INTEGER DEFAULT 0, "
            + COL_FIELD_NAME 	        + " TEXT NOT NULL"
			+ ");";
	// @formatter:on


}
