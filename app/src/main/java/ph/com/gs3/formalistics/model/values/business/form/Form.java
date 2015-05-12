package ph.com.gs3.formalistics.model.values.business.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.FormContentType;
import ph.com.gs3.formalistics.model.values.business.Company;

public class Form implements Serializable {

    private int id;
    private int webId;
    private String name;

    private FormCategory category;

    private int workflowId;
    private String webTableName;

    private Company company;
    private List<WorkflowObject> workflowObjects;
    private List<FormViewContentData> activeContents;

    private String documentsLastUpdateDate;

    public String getGeneratedFormTableName() {

        String parsedFormName = this.getName();

        parsedFormName = parsedFormName.replace(' ', '_');

        return "Form_" + this.getCompany().getId() + "_" + parsedFormName + "_Fields";
    }

    public List<FormFieldData> getFieldsWithDownloadableData() {

        List<FormFieldData> formFieldsWithDownloadableData = new ArrayList<>();
        List<FormViewContentData> viewContents = this.getActiveContents();

        for (FormViewContentData viewContent : viewContents) {

            if (viewContent instanceof FormFieldData) {
                // TODO: add other fields here (like single and multiple attachments) once they're available
                if (viewContent.getType() == FormContentType.DYNAMIC_IMAGE) {
                    formFieldsWithDownloadableData.add((FormFieldData) viewContent);
                }
            }

        }

        return formFieldsWithDownloadableData;

    }

    @Override
    public String toString() {
        return name;
    }

    // <editor-fold desc="Getters & Setters">

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormCategory getCategory() {
        return category;
    }

    public void setCategory(FormCategory category) {
        this.category = category;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public String getWebTableName() {
        return webTableName;
    }

    public void setWebTableName(String webTableName) {
        this.webTableName = webTableName;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public FormViewContentData getFormContentById(String id) {

        for (FormViewContentData formContent : activeContents) {
            if (formContent.getName().equals(id)) {
                return formContent;
            }
        }

        return null;

    }

    public List<FormFieldData> getActiveFields() {

        List<FormFieldData> formFields = new ArrayList<>();

        for (FormViewContentData content : activeContents) {
            if (content instanceof FormFieldData) {
                formFields.add((FormFieldData) content);
            }
        }

        return formFields;

    }

    public List<FormViewContentData> getActiveContents() {
        return activeContents;
    }

    public void setActiveContents(List<FormViewContentData> activeContents) {
        this.activeContents = activeContents;
    }

    public List<WorkflowObject> getWorkflowObjects() {
        return workflowObjects;
    }

    public void setWorkflowObjects(List<WorkflowObject> workflowObjects) {
        this.workflowObjects = workflowObjects;
    }

    public String getDocumentsLastUpdateDate() {
        return documentsLastUpdateDate;
    }

    public void setDocumentsLastUpdateDate(String documentsLastUpdateDate) {
        this.documentsLastUpdateDate = documentsLastUpdateDate;
    }

    // </editor-fold>
}
