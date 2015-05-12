package ph.com.gs3.formalistics.model.tables;

public class UsersTable {

    public static final String TAG = UsersTable.class.getSimpleName();

    // Table name
    public static final String NAME = "Users";

    // Table columns
    public static final String COL_ID = "_id";
    public static final String COL_WEB_ID = "web_id";
    public static final String COL_EMAIL = "email";
    public static final String COL_DISPLAY_NAME = "display_name";
    public static final String COL_COMPANY_ID = "company_id";

    public static final String COL_POSITION_ID = "position_id";
    public static final String COL_POSITION_NAME = "position_name";
    public static final String COL_DEPARTMENT_POSITION_LEVEL_ID = "department_position_level_id";

    public static final String COL_IMAGE_URL = "image_URL";
    public static final String COL_USER_LEVEL_ID = "user_level_id";

    public static final String COL_PASSWORD = "password";

    public static final String COL_FORMS_LAST_UPDATE_DATE = "forms_last_update_date";

    public static final String COL_IS_ACTIVE = "is_active";

    // Table column collection
    public static final String[] COLUMN_COLLECTION = new String[]{COL_ID, COL_WEB_ID, COL_EMAIL,
            COL_DISPLAY_NAME, COL_COMPANY_ID, COL_POSITION_ID,
            COL_DEPARTMENT_POSITION_LEVEL_ID, COL_POSITION_NAME, COL_IMAGE_URL,
            COL_USER_LEVEL_ID, COL_PASSWORD, COL_FORMS_LAST_UPDATE_DATE, COL_IS_ACTIVE

    };

    // Table creation query
    // @formatter:off
	public static final String CREATION_QUERY =
			"CREATE TABLE " 	    + NAME + "("
				+ COL_ID 			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ COL_WEB_ID 		+ " TEXT NOT NULL, "
				+ COL_EMAIL 		+ " TEXT, "
				+ COL_DISPLAY_NAME 	+ " TEXT NOT NULL, "
				+ COL_COMPANY_ID 	+ " TEXT NOT NULL, "

				+ COL_POSITION_ID			+ " INTEGER DEFAULT 0, "
				+ COL_DEPARTMENT_POSITION_LEVEL_ID 	+ " INTEGER DEFAULT 0, "
				+ COL_POSITION_NAME 		+ " TEXT, "

				+ COL_IMAGE_URL 	+ " TEXT, "
				+ COL_USER_LEVEL_ID + " TEXT DEFAULT 3,"

				+ COL_PASSWORD 		+ " TEXT, "

				+ COL_FORMS_LAST_UPDATE_DATE + " TEXT, "

                + COL_IS_ACTIVE 	+ " INTEGER DEFAULT 0"
			+ "); ";
	// @formatter:on

}
