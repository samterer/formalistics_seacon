package ph.com.gs3.formalistics.model.values.business.document;

import org.json.JSONObject;

import java.util.List;

import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;

public class DocumentSummary {

    private int documentId;
    private String trackingNumber;
    private String status;
    private int formId;
    private String formName;
    private int authorId;

    private int processorType;
    private String processor;
    private String authorDisplayName;

    private int starMarkInt;

    private List<WorkflowAction> actions;

    private JSONObject fieldValuesJSON;

    private String dateUpdatedString;
    private int commentCount;

    // ======================================================================
    //<editor-fold desc="Getters & Setters">
    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getProcessorType() {
        return processorType;
    }

    public void setProcessorType(int processorType) {
        this.processorType = processorType;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public List<WorkflowAction> getActions() {
        return actions;
    }

    public void setActions(List<WorkflowAction> actions) {
        this.actions = actions;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public void setAuthorDisplayName(String authorDisplayName) {
        this.authorDisplayName = authorDisplayName;
    }

    public int getStarMarkInt() {
        return starMarkInt;
    }

    public void setStarMarkInt(int starMarkInt) {
        this.starMarkInt = starMarkInt;
    }


    public JSONObject getFieldValuesJSON() {
        return fieldValuesJSON;
    }

    public void setFieldValuesJSON(JSONObject fieldValuesJSON) {
        this.fieldValuesJSON = fieldValuesJSON;
    }

    public String getDateUpdatedString() {
        return dateUpdatedString;
    }

    public void setDateUpdatedString(String dateUpdatedString) {
        this.dateUpdatedString = dateUpdatedString;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    //</editor-fold>

}
