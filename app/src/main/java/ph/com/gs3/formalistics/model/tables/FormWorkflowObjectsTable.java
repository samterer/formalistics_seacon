package ph.com.gs3.formalistics.model.tables;

/**
 * Created by Ervinne on 4/17/2015.
 */
public class FormWorkflowObjectsTable {

    public static final String NAME = "Forms_Workflow_Objects";

    public static final String COL_ID = "_id";
    public static final String COL_WEB_ID = "web_id";
    public static final String COL_WORKFLOW_ID = "workflow_id";
    public static final String COL_FORM_ID = "form_id";
    public static final String COL_NODE_TYPE = "node_type";
    public static final String COL_NODE_ID = "node_id";
    public static final String COL_PROCESSOR_TYPE = "processor_type";
    public static final String COL_PROCESSOR = "processor"; //  Can be id of user, company position, department name, etc. depending on processor type
    public static final String COL_STATUS = "status";
    public static final String COL_FIELDS_ENABLED = "fields_enabled";
    public static final String COL_FIELDS_REQUIRED = "fields_required";
    public static final String COL_FIELDS_HIDDEN = "fields_hidden";
    public static final String COL_ACTIONS = "actions";

    public static final String[] COLUMN_COLLECTION = {
            COL_ID,
            COL_WEB_ID,
            COL_WORKFLOW_ID,
            COL_FORM_ID,
            COL_NODE_TYPE,
            COL_NODE_ID,
            COL_PROCESSOR_TYPE,
            COL_PROCESSOR,
            COL_STATUS,
            COL_FIELDS_ENABLED,
            COL_FIELDS_REQUIRED,
            COL_FIELDS_HIDDEN,
            COL_ACTIONS
    };

    // Table Creation Query
    // @formatter:off
	public static final String CREATION_QUERY = "CREATE TABLE " + NAME + "("
			+ COL_ID 			    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_WEB_ID 		    + " INTEGER NOT NULL, "
			+ COL_WORKFLOW_ID 	    + " INTEGER NOT NULL, "
			+ COL_FORM_ID		    + " INTEGER NOT NULL, "

			+ COL_NODE_ID 		    + " TEXT NOT NULL, "
            + COL_NODE_TYPE 		+ " INTEGER NOT NULL, "

			+ COL_PROCESSOR_TYPE 	+ " INTEGER NOT NULL, "
			+ COL_PROCESSOR 		+ " TEXT NOT NULL, "
			+ COL_STATUS 	        + " TEXT NOT NULL, "

			+ COL_FIELDS_ENABLED 	+ " TEXT, "
            + COL_FIELDS_REQUIRED 	+ " TEXT, "
            + COL_FIELDS_HIDDEN 	+ " TEXT, "
            + COL_ACTIONS 	        + " TEXT "
		+ "); ";
	// @formatter:on


}
