package ph.com.gs3.formalistics.view.document.contents.fields;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FCheckBoxGroup extends FField {

    private final LinearLayout llCheckboxContainer;

    private final List<String> options;
    private final List<CheckBox> groupCheckboxes;

    private boolean enabled;

    public FCheckBoxGroup(Context context, FormFieldData formFieldData, List<String> options) {
        super(context, R.layout.field_checkbox_group, formFieldData);
        this.options = options;
        groupCheckboxes = new ArrayList<>();

        llCheckboxContainer = (LinearLayout) findViewById(R.id.FField_llCheckboxContainer);

        final float scale = this.getResources().getDisplayMetrics().density;

        for (int i = 0; i < options.size(); i++) {

            String optionString = options.get(i);

            CheckBox cbField = (CheckBox) inflate(context, R.layout.field_checkbox, null);
            cbField.setPadding(cbField.getPaddingLeft() + (int) (10.0f * scale + 0.5f),
                    cbField.getPaddingTop(),
                    cbField.getPaddingRight(),
                    cbField.getPaddingBottom());
            cbField.setTag(getFieldName() + "_" + i);

            // Check if the option is a value label pair, if it is, set the label as the
            // text
            if (optionString.contains("|")) {
                cbField.setText(optionString.split("\\|")[1]);
            } else {
                cbField.setText(optionString);
            }

            llCheckboxContainer.addView(cbField);
            groupCheckboxes.add(cbField);

        }

    }

    @Override
    public void showError(String errorMessage) {
        // FIXME: implement this
    }

    @Override
    public void setValue(String value) {
        if (value == null) {
            return;
        }

        String[] selectedOptionsRaw = value.split(MULTI_VALUE_SEPARATOR_ESCAPED);
        List<String> selectedOptions = new ArrayList<>(Arrays.asList(selectedOptionsRaw));

        int optionSize = options.size();

        for (int i = 0; i < optionSize; i++) {

            String currentOptionString = options.get(i);
            CheckBox cbField = groupCheckboxes.get(i);

            for (String selectedOption : selectedOptions) {

                if (currentOptionString.contains("|")) {
                    String optionValue = currentOptionString.split("\\|")[0];
                    if (optionValue.equals(selectedOption.trim())) {
                        cbField.setChecked(true);
                    }
                } else {
                    if (currentOptionString.equals(selectedOption.trim())) {
                        cbField.setChecked(true);
                    }
                }

            }
        }
    }

    @Override
    public String getValue() {
        String value = "";
        for (CheckBox cbField : groupCheckboxes) {
            if (cbField.isChecked()) {
                value += cbField.getText().toString() + MULTI_VALUE_SEPARATOR;
            }
        }

        return value;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        for (int i = 0; i < groupCheckboxes.size(); i++) {
            groupCheckboxes.get(i).setEnabled(enabled);
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
