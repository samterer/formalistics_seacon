package ph.com.gs3.formalistics.view.document.contents;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;

/**
 * Created by Ervinne on 4/13/2015.
 */
public abstract class FField extends FView {

    public static final String PROMPT_FIELD_REQUIRED = "This field is required";

    public static final String MULTI_VALUE_SEPARATOR_ESCAPED = "\\|\\^\\|";
    public static final String MULTI_VALUE_SEPARATOR = "|^|";

    protected final List<AbstractFieldChangeListener> changeListeners;

    private final TextView tvLabel;

    private final FormFieldData formFieldData;

    private boolean enableFieldValueChangeNotification;

    protected FField(Context context, int resourceId, FormFieldData formFieldData) {
        super(context);
        inflate(context, resourceId, this);

        changeListeners = new ArrayList<>();

        this.formFieldData = formFieldData;
        this.setTag(getFieldName() + "_container");

        tvLabel = (TextView) findViewById(R.id.FField_tvLabel);
        markAsRequired(formFieldData.isRequired());

        enableFieldValueChangeNotification = true;
    }

    public String getLabel() {

        if (tvLabel == null) {
            return null;
        }

        return tvLabel.getText().toString();
    }

    public void setLabel(String label) {
        if (tvLabel != null) {
            tvLabel.setText(label);
        }
    }

    public String getFieldName() {
        return formFieldData.getName();
    }

    public FormFieldData getFormFieldData() {
        return formFieldData;
    }

    public abstract void showError(String errorMessage);

    public void setValue(String value, boolean notifyListeners) {
        enableFieldValueChangeNotification(notifyListeners);
        setValue(value);
        enableFieldValueChangeNotification(true);
    }

    public abstract void setValue(String value);

    public abstract String getOldValue();

    public abstract String getValue();

    public abstract void setEnabled(boolean enabled);

    public abstract boolean isEnabled();

    public void markAsRequired(boolean required) {
        if (required) {
            setLabel(getLabel() + " *");
        }
    }

    public void addOnChangeListener(AbstractFieldChangeListener abstractFieldChangeListener) {
        changeListeners.add(abstractFieldChangeListener);
    }

    public void notifyValueChanged() {
        if (isFieldValueChangeNotificationEnabled()) {
            for (AbstractFieldChangeListener changeListener : changeListeners) {
                changeListener.onChange(this, getValue());
            }
        } else {
            FLLogger.d("FField", "notification not enabled for " + getFieldName());
        }
    }

    public interface AbstractFieldChangeListener {
        void onChange(FField source, String newValue);
    }

    public boolean isFieldValueChangeNotificationEnabled() {
        return enableFieldValueChangeNotification;
    }

    public void enableFieldValueChangeNotification(boolean enableFieldValueChangeNotification) {
        this.enableFieldValueChangeNotification = enableFieldValueChangeNotification;
    }
}
