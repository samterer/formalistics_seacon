package ph.com.gs3.formalistics.model.tables;

public class CompaniesTable {

	public static final String NAME = "Companies";

	public static final String COL_ID = "_id";
	public static final String COL_WEB_ID = "web_id";
	public static final String COL_NAME = "name";
	public static final String COL_SERVER = "server";

	public static final String[] COLUMN_COLLECTION = new String[] { COL_ID, COL_WEB_ID, COL_NAME,
	        COL_SERVER };

	// @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
			+ COL_ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_WEB_ID 	+ " TEXT NOT NULL, "
			+ COL_NAME 		+ " TEXT NOT NULL, "
			+ COL_SERVER 	+ " TEXT NOT NULL"
			+ ");";
	// @formatter:on

}
