package ph.com.gs3.formalistics.view.document.contents.fields;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FCodeScannerField extends FField {

    private final EditText etScannedCode;
    private final Button bScanCode;

    private Timer timer = new Timer();
    private final long ON_CHANGE_DELAY = 1000; // Milliseconds
    private boolean changeListenerInitialized = false;

    private final CodeType codeType;

    private String oldValue;



    public enum CodeType {
        BAR_CODE, QR_CODE
    }

    public FCodeScannerField(Context context, FormFieldData formFieldData, CodeType codeType, final CodeScannerListener listener) {
        super(context, R.layout.field_code_scanner, formFieldData);

        this.codeType = codeType;
        etScannedCode = (EditText) findViewById(R.id.Barcode_etScannedCode);
        bScanCode = (Button) findViewById(R.id.Barcode_bScanCode);
        bScanCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onScanCodeCommand(FCodeScannerField.this);
                }
            }
        });

    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    @Override
    public void showError(String errorMessage) {
        etScannedCode.setError(errorMessage);
    }

    @Override
    public void setValue(String value) {
        oldValue = getValue();
        etScannedCode.setText(value);
    }


    @Override
    public String getValue() {
        return etScannedCode.getText().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        etScannedCode.setEnabled(enabled);
        bScanCode.setVisibility(enabled ? VISIBLE : GONE);
    }

    @Override
    public boolean isEnabled() {
        return etScannedCode.isEnabled();
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

    public CodeType getCodeType() {
        return codeType;
    }

    private void initializeChangeListener() {
        final Context contextReference = getContext();
        etScannedCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldValue = getValue();
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

    public interface CodeScannerListener {

        void onScanCodeCommand(FCodeScannerField source);

    }

}
