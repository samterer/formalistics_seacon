package ph.com.gs3.formalistics.model.values.business.document;

public class SubmitReadyAction {

	public static final String ACTION_NO_DOCUMENT_SUBMISSION = "no_document_submit_action";

	private int id;
	private int documentWebId;
	private int formWebId;
	private String fieldUpdates;
	private String action;
	private int isStarredCode;
	private String server;

	// ===================================================================
	// {{ Getters & Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocumentWebId() {
		return documentWebId;
	}

	public void setDocumentWebId(int documentWebId) {
		this.documentWebId = documentWebId;
	}

	public int getFormWebId() {
		return formWebId;
	}

	public void setFormWebId(int formWebId) {
		this.formWebId = formWebId;
	}

	public String getFieldUpdates() {
		return fieldUpdates;
	}

	public void setFieldUpdates(String fieldUpdates) {
		this.fieldUpdates = fieldUpdates;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getIsStarredCode() {
		return isStarredCode;
	}

	public void setIsStarredCode(int isStarredCode) {
		this.isStarredCode = isStarredCode;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	// }}

}
