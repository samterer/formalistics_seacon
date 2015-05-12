package ph.com.gs3.formalistics.model.values.business.form;

import java.io.Serializable;

import ph.com.gs3.formalistics.global.constants.FormContentType;

public class FormViewContentData implements Serializable {

    public static final String TAG = FormViewContentData.class.getSimpleName();

    private String name;
    private FormContentType type;
    private String rawJSONString;
    private boolean enabled = true;
    private boolean hidden = false;

    private Formula visibilityFormula;

    // ========================================================================
    //<editor-fold desc=" Getters & Setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormContentType getType() {
        return type;
    }

    public void setType(FormContentType type) {
        this.type = type;
    }

    public String getRawJSONString() {
        return rawJSONString;
    }

    public void setRawJSONString(String rawJSONString) {
        this.rawJSONString = rawJSONString;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Formula getVisibilityFormula() {
        return visibilityFormula;
    }

    public void setVisibilityFormula(Formula visibilityFormula) {
        this.visibilityFormula = visibilityFormula;
    }
    //</editor-fold>
}
