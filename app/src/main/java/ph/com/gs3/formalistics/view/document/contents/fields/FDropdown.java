package ph.com.gs3.formalistics.view.document.contents.fields;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FDropdown extends FField {

    private final Spinner spinner;
    private final List<String> options;

    private boolean changeListenerInitialized = false;

    private String oldValue;

    public FDropdown(Context context, FormFieldData formFieldData, List<String> options) {
        super(context, R.layout.field_dropdown, formFieldData);

        this.options = options;
        spinner = (Spinner) findViewById(R.id.FField_spDropdownField);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, options);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setTag(getFieldName());

    }

    @Override
    public String getLabel() {
        return super.getLabel();
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
    }

    @Override
    public String getFieldName() {
        return super.getFieldName();
    }

    @Override
    public void showError(String errorMessage) {
        // TODO: implement this
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    @Override
    public void setValue(String value) {
        oldValue = getValue();
        // Assign the value if this field is for viewing
        if (value != null && !"null".equals(value)) {
            if (options.indexOf(value) > -1) {
                spinner.setSelection(options.indexOf(value));
            }
        }
    }

    @Override
    public String getValue() {
        return spinner.getSelectedItem().toString();
    }

    @Override
    public void setEnabled(boolean enabled) {
        spinner.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return spinner.isEnabled();
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                notifyValueChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
