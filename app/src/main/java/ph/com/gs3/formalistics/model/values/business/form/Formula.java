package ph.com.gs3.formalistics.model.values.business.form;

/**
 * Created by Ervinne on 4/21/2015.
 */
public class Formula {

    public enum FormulaType {
        MIDDLEWARE, STATIC, COMPUTED, NOT_APPLICABLE
    }

    private FormulaType formulaType;
    private String rule;

    public Formula() {
    }

    public Formula(String rule) {
        this.rule = rule;
        this.formulaType = FormulaType.NOT_APPLICABLE;
    }

    public Formula(String rule, FormulaType formulaType) {
        this.rule = rule;
        this.formulaType = formulaType;
    }

    //<editor-fold desc="Getters & Setters">

    public FormulaType getFormulaType() {
        return formulaType;
    }

    public void setFormulaType(FormulaType formulaType) {
        this.formulaType = formulaType;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
    //</editor-fold>
}
