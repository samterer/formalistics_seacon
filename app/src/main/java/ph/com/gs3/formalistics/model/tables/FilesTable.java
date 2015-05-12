package ph.com.gs3.formalistics.model.tables;

import ph.com.gs3.formalistics.global.constants.FileStatus;

/**
 * Created by Ervinne on 4/16/2015.
 */
public class FilesTable {

    public static final String NAME = "Files";

    public static final String COL_ID = "_id";
    public static final String COL_LOCAL_PATH = "local_path";
    public static final String COL_STATUS = "status";
    public static final String COL_REMOTE_URL = "remote_url";
    public static final String COL_OWNER_ID = "owner_id";

    public static final String[] COLUMN_COLLECTION = {COL_ID, COL_LOCAL_PATH, COL_STATUS, COL_REMOTE_URL, COL_OWNER_ID};

    // @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
            + COL_ID		+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_LOCAL_PATH 	+ " TEXT NOT NULL, "
			+ COL_REMOTE_URL	+ " TEXT NOT NULL, "
            + COL_STATUS 	    + " INTEGER DEFAULT " + FileStatus.NOT_FOUND + ","
			+ COL_OWNER_ID      + " INTEGER NOT NULL"
			+ ");";
	// @formatter:on

}
