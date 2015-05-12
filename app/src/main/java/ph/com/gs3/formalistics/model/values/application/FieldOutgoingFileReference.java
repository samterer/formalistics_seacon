package ph.com.gs3.formalistics.model.values.application;

/**
 * Created by Ervinne on 4/23/2015.
 */
public class FieldOutgoingFileReference {

    private int outgoingFileId;
    private int formId;
    private int outgoingActionId;
    private String fieldName;

    //<editor-fold desc="Getters & Setters">
    public int getOutgoingFileId() {
        return outgoingFileId;
    }

    public void setOutgoingFileId(int outgoingFileId) {
        this.outgoingFileId = outgoingFileId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public int getOutgoingActionId() {
        return outgoingActionId;
    }

    public void setOutgoingActionId(int outgoingActionId) {
        this.outgoingActionId = outgoingActionId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    //</editor-fold>
}
