package ph.com.gs3.formalistics.view.document.contents.fields;

import android.content.Context;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FRadioButtonGroup extends FField {

    private final RadioGroup radioGroup;
    private List<String> options;
    private boolean enabled;
    private boolean changeListenerInitialized = false;

    public FRadioButtonGroup(Context context, FormFieldData formFieldData, List<String> options) {
        super(context, R.layout.field_radiobutton_group, formFieldData);

        radioGroup = (RadioGroup) findViewById(R.id.FField_rgRadioButtonContainer);

        for (int i = 0; i < options.size(); i++) {
            String optionString = options.get(i).trim();
            String optionLabel = optionString;

            RadioButton radioButton = (RadioButton) inflate(context, R.layout.field_radiobutton, null);
            radioButton.setId(i);
            radioButton.setLayoutParams(new LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
            // Check if the option is value label pair, if it is, divide the option string
            // with | and assign the label and value properly
            if (optionString.contains("|")) {
                String[] splittedOptionString = optionString.split("\\|");
                optionLabel = splittedOptionString[1];
            }

            radioButton.setText(optionLabel);
            radioGroup.addView(radioButton);
        }

    }

    @Override
    public void showError(String errorMessage) {
        int radioButtonCount = radioGroup.getChildCount();

        if (radioButtonCount > 0) {
            // set error on the first radio button found
            final RadioButton rbFirstField = (RadioButton) radioGroup.getChildAt(0);
            rbFirstField.setError(PROMPT_FIELD_REQUIRED);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    rbFirstField.setError(null);
                }
            });

        }
    }

    @Override
    public void setValue(String value) {
        int collectionChildCount = radioGroup.getChildCount();
        for (int index = 0; index < collectionChildCount; index++) {
            if (radioGroup.getChildAt(index) instanceof RadioButton) {
                RadioButton rbField = (RadioButton) radioGroup.getChildAt(index);
                if (rbField.getText().toString().equals(value)) {
                    rbField.setChecked(true);
                }
            }
        }
    }

    @Override
    public String getValue() {
        int collectionChildCount = radioGroup.getChildCount();

        String value = "";

        // Search which value is selected and return it
        for (int index = 0; index < collectionChildCount; index++) {
            if (radioGroup.getChildAt(index) instanceof RadioButton) {
                RadioButton rbField = (RadioButton) radioGroup.getChildAt(index);
                if (rbField.isChecked()) {
                    value = rbField.getText().toString();
                }
            }
        }

        return value;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        int collectionChildCount = radioGroup.getChildCount();
        for (int index = 0; index < collectionChildCount; index++) {
            if (radioGroup.getChildAt(index) instanceof RadioButton) {
                radioGroup.getChildAt(index).setEnabled(enabled);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void addOnChangeListener(AbstractFieldChangeListener abstractFieldChangeListener) {
        super.addOnChangeListener(abstractFieldChangeListener);

        if (!changeListenerInitialized) {
            // Lazy load change listener
            initializeChangeListener();
            changeListenerInitialized = true;
        }

    }

    private void initializeChangeListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                notifyValueChanged();
            }
        });
    }
}
