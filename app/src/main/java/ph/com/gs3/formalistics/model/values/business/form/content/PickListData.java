package ph.com.gs3.formalistics.model.values.business.form.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;

public class PickListData implements Serializable {

    private static final long serialVersionUID = -1498717747428844288L;

    private List<ViewColumn> viewColumns = new ArrayList<>();

    private int formWebId;
    private String condition;
    private String formName;
    private String resultFieldName;

    // ========================================================================================
    // {{ Getters & Setters

    public int getFormWebId() {
        return formWebId;
    }

    public void setFormWebId(int formWebId) {
        this.formWebId = formWebId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getResultFieldName() {
        return resultFieldName;
    }

    public void setResultFieldName(String resultFieldName) {
        this.resultFieldName = resultFieldName;
    }

    public List<ViewColumn> getViewColumns() {
        return viewColumns;
    }

    public void setViewColumns(List<ViewColumn> viewColumns) {
        this.viewColumns = viewColumns;
    }

    public List<String> getViewColumnIdList() {

        List<String> columnIdList = new ArrayList<>();

        for (ViewColumn column : viewColumns) {
            columnIdList.add(column.getName());
        }

        return columnIdList;

    }

    // }}

}
