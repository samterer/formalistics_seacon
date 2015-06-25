package ph.com.gs3.formalistics.model.values.business.form;

import java.io.Serializable;
import java.util.List;

public class FormFieldData extends FormViewContentData implements Serializable {

    public static final String TAG = FormFieldData.class.getSimpleName();

    private String label;
    private String placeHolder;
    private String value;
    private List<String> options;
    private boolean isHidden;
    private boolean isRequired;

    private Formula readOnlyFormula;
    private Formula valueFormula;

    // ========================================================================
    //<editor-fold desc="Getters & Setters">
    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(String placeHolder) {
        this.placeHolder = placeHolder;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public Formula getReadOnlyFormula() {
        return readOnlyFormula;
    }

    public void setReadOnlyFormula(Formula readOnlyFormula) {
        this.readOnlyFormula = readOnlyFormula;
    }

    public Formula getValueFormula() {
        return valueFormula;
    }

    public void setValueFormula(Formula valueFormula) {
        this.valueFormula = valueFormula;
    }

    //</editor-fold>

    // ========================================================================
    // Exceptions

    public static class InvalidFormFieldException extends Exception {

        private static final long serialVersionUID = 668103959494161980L;

        public InvalidFormFieldException(String message) {
            super(message);
        }

        public InvalidFormFieldException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
}
