package ph.com.gs3.formalistics.model.values.application;

import java.util.List;

import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 4/25/2015.
 */
public class ViewFilter {

    public static enum ViewContentType {
        EXISTING_DOCUMENTS, FOR_APPROVAL_DOCUMENTS, OUTGOING_ACTIONS
    }

    private List<Form> forms;
    private ViewContentType viewContentType;
    private List<SearchCondition> searchConditionList;
    private String manualConditions;
    private String manualJoins;
    private String genericStringFilter;

    //<editor-fold desc="Getters & Setters">
    public List<Form> getForms() {
        return forms;
    }

    public void setForms(List<Form> forms) {
        this.forms = forms;
    }

    public ViewContentType getViewContentType() {
        return viewContentType;
    }

    public void setViewContentType(ViewContentType viewContentType) {
        this.viewContentType = viewContentType;
    }

    public List<SearchCondition> getSearchConditionList() {
        return searchConditionList;
    }

    public void setSearchConditionList(List<SearchCondition> searchConditionList) {
        this.searchConditionList = searchConditionList;
    }

    public String getManualConditions() {
        return manualConditions;
    }

    public void setManualConditions(String manualConditions) {
        this.manualConditions = manualConditions;
    }

    public String getManualJoins() {
        return manualJoins;
    }

    public void setManualJoins(String manualJoins) {
        this.manualJoins = manualJoins;
    }

    public String getGenericStringFilter() {
        return genericStringFilter;
    }

    public void setGenericStringFilter(String genericStringFilter) {
        this.genericStringFilter = genericStringFilter;
    }
    //</editor-fold>
}
