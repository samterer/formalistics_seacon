package ph.com.gs3.formalistics.view.document.contents.fields;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class FTextField extends FField {

    public enum TextFieldType {
        MULTI_LINE, SINGLE_LINE
    }

    private EditText etValue;
    private TextFieldType textFieldType;

    private Timer timer = new Timer();
    private final long ON_CHANGE_DELAY = 1000; // Milliseconds
    private boolean changeListenerInitialized = false;

    public FTextField(Context context, FormFieldData formFieldData) {
        super(context, R.layout.field_text, formFieldData);
        this.textFieldType = TextFieldType.SINGLE_LINE;
        initialize();
    }

    public FTextField(Context context, FormFieldData formFieldData, TextFieldType textFieldType) {
        super(context, R.layout.field_text, formFieldData);
        this.textFieldType = textFieldType;
        initialize();
    }

    private void initialize() {
        etValue = (EditText) findViewById(R.id.FField_etField);
        etValue.setSingleLine(textFieldType == TextFieldType.SINGLE_LINE);
        etValue.setTag(getFieldName());
    }

    @Override
    public void showError(String errorMessage) {
        etValue.setError(errorMessage);
    }

    @Override
    public void setValue(String value) {
        etValue.setText(value);
//        FLLogger.d("FTextField", "Setting " + getLabel() + " = " + getValue());
        if ("null".equals(value)) {
            new Exception().printStackTrace();
        }
    }

    @Override
    public String getValue() {
        return etValue.getText().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        etValue.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return etValue.isEnabled();
    }

    @Override
    public void addOnChangeListener(AbstractFieldChangeListener abstractFieldChangeListener) {
        super.addOnChangeListener(abstractFieldChangeListener);

        if (!changeListenerInitialized) {
            //  Lazy load change listener
            initializeChangeListener();
            changeListenerInitialized = true;
        }
    }

    private void initializeChangeListener() {
        final Context contextReference = getContext();
        etValue.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (isFieldValueChangeNotificationEnabled()) {
                    timer.cancel();
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            ((Activity) contextReference).runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    notifyValueChanged();
                                }
                            });
                        }
                    }, ON_CHANGE_DELAY);
                }
            }
        });
    }
}
