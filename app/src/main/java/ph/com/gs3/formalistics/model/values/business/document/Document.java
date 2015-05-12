package ph.com.gs3.formalistics.model.values.business.document;

import java.io.Serializable;

public class Document implements Serializable {

    private int id;
    private int webId;
    private int formId;
    private String workflowNodeId;
    private int workflowId;

    private String trackingNumber;
    private String status;

    private int authorId;
    private String processor;
    private int processorType;
    private int processorDepartmentLevel;

    private String dateCreated;
    private String dateUpdated;

    private String fieldValuesJSONString;

    private int starMark;

    private String commentsLastUpdateDate;

    @Override
    public String toString() {
        return String.format("[%s by %d]", trackingNumber, authorId);
    }

    //<editor-fold desc="Getters & Setters">

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getWorkflowNodeId() {
        return workflowNodeId;
    }

    public void setWorkflowNodeId(String workflowNodeId) {
        this.workflowNodeId = workflowNodeId;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
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

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public int getProcessorType() {
        return processorType;
    }

    public void setProcessorType(int processorType) {
        this.processorType = processorType;
    }

    public int getProcessorDepartmentLevel() {
        return processorDepartmentLevel;
    }

    public void setProcessorDepartmentLevel(int processorDepartmentLevel) {
        this.processorDepartmentLevel = processorDepartmentLevel;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getFieldValuesJSONString() {
        return fieldValuesJSONString;
    }

    public void setFieldValuesJSONString(String fieldValuesJSONString) {
        this.fieldValuesJSONString = fieldValuesJSONString;
    }

    public int getStarMark() {
        return starMark;
    }

    public void setStarMark(int starMark) {
        this.starMark = starMark;
    }

    public String getCommentsLastUpdateDate() {
        return commentsLastUpdateDate;
    }

    public void setCommentsLastUpdateDate(String commentsLastUpdateDate) {
        this.commentsLastUpdateDate = commentsLastUpdateDate;
    }

    //</editor-fold>

}
