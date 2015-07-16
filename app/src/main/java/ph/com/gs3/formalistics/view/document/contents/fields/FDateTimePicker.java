package ph.com.gs3.formalistics.view.document.contents.fields;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Date;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.view.document.contents.FField;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class FDateTimePicker extends FField {

    private final PickerType pickerType;
    private final DateTimePickerListener listener;

    private final EditText etDateText;

    private String oldValue;

    public enum PickerType {
        DATE, TIME, DATETIME
    }

    public FDateTimePicker(Context context, FormFieldData formFieldData, PickerType pickerType, DateTimePickerListener listener) {
        super(context, R.layout.field_date_time_picker, formFieldData);

        this.pickerType = pickerType;
        this.listener = listener;

        etDateText = (EditText) findViewById(R.id.FField_etDateText);
        etDateText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPickerView();
            }

        });

        String hint = "";

        switch (pickerType) {
            case DATE:
                hint = "MMM/dd/yyyy";
                break;
            case TIME:
                hint = "HH:mm (AM/PM)";
                break;
            case DATETIME:
                hint = "MMM/dd/yyyy HH:mm (AM/PM)";
                break;

        }

        etDateText.setHint(hint);

    }

    private void showPickerView() {
        FLLogger.d("FDateTimePicker", "show picker view");
        if (listener != null) {
            FLLogger.d("FDateTimePicker", "showing");
            listener.onOpenPickerViewCommand(this, pickerType, getValue());
            FLLogger.d("FDateTimePicker", "shown");
        }

    }

    @Override
    public void showError(String errorMessage) {
        etDateText.setError(errorMessage);
    }

    @Override
    public String getOldValue() {
        return oldValue;
    }

    @Override
    public void setValue(String value) {

        if (value == null || "".equals(value)) {
            oldValue = getValue();
            value = getValue();
        }

        try {
            Date date;
            String formattedDate = "";

            switch (pickerType) {
                case DATE:
                    date = DateUtilities.SERVER_DATE_FORMAT.parse(value);
                    formattedDate = DateUtilities.DEFAULT_DISPLAY_DATE_ONLY_FORMAT.format(date);
                    break;
                case TIME:
                    date = DateUtilities.SERVER_TIME_FORMAT.parse(value);
                    formattedDate = DateUtilities.DEFAULT_DISPLAY_TIME_ONLY_FORMAT.format(date);
                    break;
                case DATETIME:
                    date = DateUtilities.SERVER_DATE_TIME_FORMAT.parse(value);
                    formattedDate = DateUtilities.DEFAULT_DISPLAY_DATE_TIME_FORMAT.format(date);
                    break;
            }

            etDateText.setText(formattedDate);
            notifyValueChanged();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getValue() {
        Date dateValue = getDateValue();
        String formattedValue;

        switch (pickerType) {
            case DATE:
                if (dateValue == null) {
                    formattedValue = "0000-00-00";
                } else {
                    formattedValue = DateUtilities.SERVER_DATE_FORMAT.format(dateValue);
                }
                break;
            case TIME:
                if (dateValue == null) {
                    formattedValue = "00:00:00";
                } else {
                    formattedValue = DateUtilities.SERVER_TIME_FORMAT.format(dateValue);
                }
                break;
            case DATETIME:
            default:
                if (dateValue == null) {
                    formattedValue = "0000-00-00 00:00:00";
                } else {
                    formattedValue = DateUtilities.SERVER_DATE_TIME_FORMAT.format(dateValue);
                }
                break;
        }

        return formattedValue;
    }

    @Override
    public void setEnabled(boolean enabled) {
        etDateText.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return etDateText.isEnabled();
    }

    public Date getDateValue() {

        String rawStringValue = etDateText.getText().toString();
        Date parsedDate = null;

        try {

            if (rawStringValue.trim().isEmpty()) {
                rawStringValue = "0000-00-00 00:00:00";
                parsedDate = DateUtilities.SERVER_DATE_TIME_FORMAT.parse(rawStringValue);
            } else {
                switch (pickerType) {
                    case DATE:
                        parsedDate = DateUtilities.DEFAULT_DISPLAY_DATE_ONLY_FORMAT.parse(rawStringValue);
                        break;
                    case DATETIME:
                        parsedDate = DateUtilities.DEFAULT_DISPLAY_DATE_TIME_FORMAT.parse(rawStringValue);
                        break;
                    case TIME:
                        parsedDate = DateUtilities.DEFAULT_DISPLAY_TIME_ONLY_FORMAT.parse(rawStringValue);
                        break;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return parsedDate;

    }

    public interface DateTimePickerListener {
        void onOpenPickerViewCommand(FDateTimePicker source, PickerType pickerType,
                                     String currentFieldValue);
    }

}
