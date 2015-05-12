package ph.com.gs3.formalistics.model.values.business.form.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;
import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;

public class EmbeddedViewData extends FormViewContentData implements Serializable {

    private int searchFormWebId;
    private String searchFieldId;
    private String searchConditionalOperator;
    private String searchCompareToThisDocumentFieldId;

    private List<ViewColumn> viewColumns = new ArrayList<>();

    private boolean enableDataSendingToNewDocuments;
    private boolean enableCreateDocumentAction;
    private String createDocumentActionLabel;

    private final List<EmbeddedViewDataSendingItem> dataSendingItems = new ArrayList<>();

    // =========================================================================
    // {{ Getters & Setters

    public int getSearchFormWebId() {
        return searchFormWebId;
    }

    public void setSearchFormWebId(int searchFormWebId) {
        this.searchFormWebId = searchFormWebId;
    }

    public String getSearchFieldId() {
        return searchFieldId;
    }

    public void setSearchFieldId(String searchFieldId) {
        this.searchFieldId = searchFieldId;
    }

    public String getSearchConditionalOperator() {
        return searchConditionalOperator;
    }

    public void setSearchConditionalOperator(String searchConditionalOperator) {
        this.searchConditionalOperator = searchConditionalOperator;
    }

    public String getSearchCompareToThisDocumentFieldId() {
        return searchCompareToThisDocumentFieldId;
    }

    public void setSearchCompareToThisDocumentFieldId(String searchCompareToFieldId) {
        this.searchCompareToThisDocumentFieldId = searchCompareToFieldId;
    }

    public List<ViewColumn> getViewColumns() {
        return viewColumns;
    }

    public void setViewColumns(List<ViewColumn> viewColumns) {
        this.viewColumns = viewColumns;
    }

    public boolean isEnableCreateDocumentAction() {
        return enableCreateDocumentAction;
    }

    public void setEnableCreateDocumentAction(boolean enableCreateDocumentAction) {
        this.enableCreateDocumentAction = enableCreateDocumentAction;
    }

    public String getCreateDocumentActionLabel() {
        return createDocumentActionLabel;
    }

    public void setCreateDocumentActionLabel(String createDocumentActionLabel) {
        this.createDocumentActionLabel = createDocumentActionLabel;
    }

    public void setEnableDataSendingToNewDocuments(boolean enableDataSendingToNewDocuments) {
        this.enableDataSendingToNewDocuments = enableDataSendingToNewDocuments;
    }

    public void addDataSendingDataItem(String sourceField, String destinationField) {
        dataSendingItems.add(new EmbeddedViewDataSendingItem(sourceField, destinationField));
    }

    public List<EmbeddedViewDataSendingItem> getDataSendingItems() {
        return dataSendingItems;
    }

    // }}

    public static class EmbeddedViewDataSendingItem {

        private String destinationField;
        private String sourceField;

        public EmbeddedViewDataSendingItem(String sourceField, String destinationField) {
            this.sourceField = sourceField;
            this.destinationField = destinationField;
        }

        public String getDestinationField() {
            return destinationField;
        }

        public String getSourceField() {
            return sourceField;
        }

    }

}
