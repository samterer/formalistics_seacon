package ph.com.gs3.formalistics.model.tables;

public class FormTableReferenceTable {

	public static final String NAME = "Form_Table_Reference";

	public static final String COL_ID = "_id";
	public static final String COL_TABLE_NAME = "table_name";

	public static final String[] COLUMN_COLLECTION = { COL_ID, COL_TABLE_NAME };

	// @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
			+ COL_ID	+ " INTEGER PRIMARY KEY, "
			+ COL_TABLE_NAME + " TEXT NOT NULL"
		+ ")";
	// @formatter:on

}
