package ph.com.gs3.formalistics.model.values.business.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.Company;
import ph.com.gs3.formalistics.model.values.business.document.DocumentAction;

public class FormOld implements Serializable {

    private int id;
    private int webId;
    private String name;

    private int categoryId;
    private String categoryName;

    private String workflowId;
    private String webTableName;

    private Company company;

    private List<FormViewContentData> activeContents;

    private List<String> onCreateFieldsRequired;
    private List<String> onCreateFieldsEnabled;
    private List<String> onCreateFieldsHidden;

    private List<DocumentAction> onCreateActions;

    private String documentsLastUpdateDate;

    public String getGeneratedFormTableName() {

        String parsedFormName = this.getName();

        parsedFormName = parsedFormName.replace(' ', '_');

        return "Form_" + this.getCompany().getId() + "_" + parsedFormName + "_Fields";
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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
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

    public List<String> getOnCreateFieldsRequired() {
        return onCreateFieldsRequired;
    }

    public void setOnCreateFieldsRequired(List<String> onCreateFieldsRequired) {
        this.onCreateFieldsRequired = onCreateFieldsRequired;
    }

    public List<String> getOnCreateFieldsEnabled() {
        return onCreateFieldsEnabled;
    }

    public void setOnCreateFieldsEnabled(List<String> onCreateFieldsEnabled) {
        this.onCreateFieldsEnabled = onCreateFieldsEnabled;
    }

    public List<String> getOnCreateFieldsHidden() {
        return onCreateFieldsHidden;
    }

    public void setOnCreateFieldsHidden(List<String> onCreateFieldsHidden) {
        this.onCreateFieldsHidden = onCreateFieldsHidden;
    }

    public List<DocumentAction> getOnCreateActions() {
        return onCreateActions;
    }

    public void setOnCreateActions(List<DocumentAction> onCreateActions) {
        this.onCreateActions = onCreateActions;
    }

    public String getDocumentsLastUpdateDate() {
        return documentsLastUpdateDate;
    }

    public void setDocumentsLastUpdateDate(String documentsLastUpdateDate) {
        this.documentsLastUpdateDate = documentsLastUpdateDate;
    }

    // </editor-fold>
}
