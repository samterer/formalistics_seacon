package ph.com.gs3.formalistics.model.values.business.view;

import java.util.List;

public class DisplayReadyAction {

	private int id;
	private int documentId;
	private int formId;
	private int issuedByUserId;
	private String issuedByUserDisplayName;
	private String dateIssued;
	
	private int isStarredInt;
	private List<String> issuedActions;

	private String formName;
	private String trackingNumber;

	private String documentFieldUpdatesString;
	
	// =====================================================================
	// {{ Getters & Setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDocumentId() {
		return documentId;
	}

	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}

	public int getFormId() {
		return formId;
	}

	public void setFormId(int formId) {
		this.formId = formId;
	}

	public int getIssuedByUserId() {
		return issuedByUserId;
	}

	public void setIssuedByUserId(int issuedByUserId) {
		this.issuedByUserId = issuedByUserId;
	}

	public String getIssuedByUserDisplayName() {
		return issuedByUserDisplayName;
	}

	public void setIssuedByUserDisplayName(String issuedByUserDisplayName) {
		this.issuedByUserDisplayName = issuedByUserDisplayName;
	}

	public String getDateIssued() {
	    return dateIssued;
    }

	public void setDateIssued(String dateIssued) {
	    this.dateIssued = dateIssued;
    }

	public int getIsStarredInt() {
		return isStarredInt;
	}

	public void setStarredInt(int isStarredInt) {
		this.isStarredInt = isStarredInt;
	}

	public List<String> getIssuedActions() {
		return issuedActions;
	}

	public void setIssuedActions(List<String> issuedActions) {
		this.issuedActions = issuedActions;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getDocumentFieldUpdatesString() {
	    return documentFieldUpdatesString;
    }

	public void setDocumentFieldUpdatesString(String documentFieldUpdatesString) {
	    this.documentFieldUpdatesString = documentFieldUpdatesString;
    }

	// }}

}
