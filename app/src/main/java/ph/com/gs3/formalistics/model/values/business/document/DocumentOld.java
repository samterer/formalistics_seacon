package ph.com.gs3.formalistics.model.values.business.document;

import java.io.Serializable;
import java.util.List;

public class DocumentOld implements Serializable {

    private int id;
    private int webId;
    private int formId;

    private String trackingNumber;
    private String status;

    private int authorId;
    private int processorWebId;
    private int processorType;

    private String dateCreated;
    private String dateUpdated;

    private List<String> fieldsRequired;
    private List<String> fieldsEnabled;
    private List<String> fieldsHidden;

    private String fieldValuesJSONString;
    private List<DocumentAction> actions;

    private int starMark;

    private String commentsLastUpdateDate;

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

    public int getProcessorWebId() {
        return processorWebId;
    }

    public void setProcessorWebId(int processorWebId) {
        this.processorWebId = processorWebId;
    }

    public int getProcessorType() {
        return processorType;
    }

    public void setProcessorType(int processorType) {
        this.processorType = processorType;
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

    public List<String> getFieldsRequired() {
        return fieldsRequired;
    }

    public void setFieldsRequired(List<String> fieldsRequired) {
        this.fieldsRequired = fieldsRequired;
    }

    public List<String> getFieldsEnabled() {
        return fieldsEnabled;
    }

    public void setFieldsEnabled(List<String> fieldsEnabled) {
        this.fieldsEnabled = fieldsEnabled;
    }

    public List<String> getFieldsHidden() {
        return fieldsHidden;
    }

    public void setFieldsHidden(List<String> fieldsHidden) {
        this.fieldsHidden = fieldsHidden;
    }

    public String getFieldValuesJSONString() {
        return fieldValuesJSONString;
    }

    public void setFieldValuesJSONString(String fieldValuesJSONString) {
        this.fieldValuesJSONString = fieldValuesJSONString;
    }

    public List<DocumentAction> getActions() {
        return actions;
    }

    public void setActions(List<DocumentAction> actions) {
        this.actions = actions;
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
