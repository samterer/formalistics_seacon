package ph.com.gs3.formalistics.model.values.business.document;

import org.json.JSONObject;

import ph.com.gs3.formalistics.model.values.business.User;


public class OutgoingAction {

    private int id;
    private int formId;
    private String formName;
    private Document document;
    private User issuedByUser;
    private String dateIssued;
    private JSONObject documentFieldUpdates;
    private String action;
    private int isStarredCode;
    private int outgoingCommentCount;
    private int parentDocumentId;
    private String errorMessage;

    // ===============================================================================
    // {{ Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public User getIssuedByUser() {
        return issuedByUser;
    }

    public void setIssuedByUser(User issuedByUser) {
        this.issuedByUser = issuedByUser;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public JSONObject getDocumentFieldUpdates() {
        return documentFieldUpdates;
    }

    public void setDocumentFieldUpdates(JSONObject documentFieldUpdates) {
        this.documentFieldUpdates = documentFieldUpdates;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    /**
     * -1 = No action, 0 = Unstarred, 1 = Starred
     *
     * @return The code for determining if this action is to be starred, unstarred, or not
     */
    public int getIsStarredCode() {
        return isStarredCode;
    }

    /**
     * -1 = No action, 0 = Unstarred, 1 = Starred
     *
     * @param isStarredCode The code for determining if this action is to be starred, unstarred, or
     *                      not
     */
    public void setIsStarredCode(int isStarredCode) {
        this.isStarredCode = isStarredCode;
    }

    public int getOutgoingCommentCount() {
        return outgoingCommentCount;
    }

    public void setOutgoingCommentCount(int outgoingCommentCount) {
        this.outgoingCommentCount = outgoingCommentCount;
    }

    public int getParentDocumentId() {
        return parentDocumentId;
    }

    public void setParentDocumentId(int parentDocumentId) {
        this.parentDocumentId = parentDocumentId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    // }}

}
