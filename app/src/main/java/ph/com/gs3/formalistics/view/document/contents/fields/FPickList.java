package ph.com.gs3.formalistics.view.document.contents.fields;


import android.content.Context;
import android.view.View;
import android.widget.EditText;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FPickList extends FField {

    private EditText etPicker;
    private PickListFieldListener listener;
    private FormFieldData formFieldData;

    public FPickList(Context context, FormFieldData formFieldData, PickListFieldListener listener) {
        super(context, R.layout.field_pick_list, formFieldData);

        this.formFieldData = formFieldData;
        this.listener = listener;

        etPicker = (EditText) findViewById(R.id.FField_etPickerResult);
        etPicker.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FLLogger.d("FPickList", "onclick");
                showPickerView();
            }
        });
    }

    private void showPickerView() {
        FLLogger.d("FPickList", "showPickerView");
//        if (listener != null && isEnabled()) {
        if (listener != null) {
            listener.onOpenPicklistCommand(this);
            FLLogger.d("FPickList", "showing picker view");
        }
    }

    public FormFieldData getFormFieldData() {
        return formFieldData;
    }

    @Override
    public void showError(String errorMessage) {
        etPicker.setError(errorMessage);
    }

    @Override
    public void setValue(String value) {
        etPicker.setText(value);
        notifyValueChanged();
    }

    @Override
    public String getValue() {
        return etPicker.getText().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        etPicker.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return etPicker.isEnabled();
    }


    public interface PickListFieldListener {
        void onOpenPicklistCommand(FPickList source);
    }

}
