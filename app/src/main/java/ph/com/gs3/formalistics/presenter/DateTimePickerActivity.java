package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.text.ParseException;
import java.util.Date;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ActivityRequestCodes;
import ph.com.gs3.formalistics.global.constants.DateTimePickerType;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.presenter.fragment.view.DateTimePickerViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.view.DateTimePickerViewFragment.DateTimePickerViewActionListener;

public class DateTimePickerActivity extends Activity implements DateTimePickerViewActionListener {

	public static final String TAG = DateTimePickerActivity.class.getSimpleName();

	// ========================================================================
	// {{ Extras

	public static final String EXTRA_DATE_TIME_PICKER_FIELD_ID = "date_time_picker_field_id";
	public static final String EXTRA_DATE_TIME_PICKER_TYPE = "date_time_picker_type";
	public static final String EXTRA_PRE_SELECTED_DATE = "pre_selected_date";
	public static final String EXTRA_RESULT_SELECTED_DATE = "result_selected_date";

	// }}

	private DateTimePickerViewFragment dateTimePickerViewFragment;

	private DateTimePickerType dateTimePickerType;
	private Date dateSelected;

	private String fieldId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_date_time_picker);

		initializeFieldsFromExtras();

		if (savedInstanceState == null) {
			dateTimePickerViewFragment = DateTimePickerViewFragment.createInstance(
			        dateTimePickerType, dateSelected);
			getFragmentManager().beginTransaction().add(R.id.container, dateTimePickerViewFragment)
			        .commit();

			initializeViewData();

		}

	}

	private void initializeViewData() {

		dateTimePickerViewFragment.setSelectedDate(dateSelected);

	}

	private void initializeFieldsFromExtras() {

		Bundle extras = getIntent().getExtras();

		fieldId = extras.getString(EXTRA_DATE_TIME_PICKER_FIELD_ID);

		dateTimePickerType = (DateTimePickerType) extras
		        .getSerializable(EXTRA_DATE_TIME_PICKER_TYPE);
		String rawDateString = extras.getString(EXTRA_PRE_SELECTED_DATE);

		if (rawDateString != null && !rawDateString.isEmpty()) {
			try {

				switch (dateTimePickerType) {
					case DATE_ONLY: {
						dateSelected = DateUtilities.DEFAULT_DISPLAY_DATE_ONLY_FORMAT
						        .parse(rawDateString);
					}
						break;
					case TIME_ONLY: {
						dateSelected = DateUtilities.DEFAULT_DISPLAY_TIME_ONLY_FORMAT
						        .parse(rawDateString);
					}
						break;
					case DATE_TIME: {
						dateSelected = DateUtilities.DEFAULT_DISPLAY_DATE_TIME_FORMAT
						        .parse(rawDateString);
					}
						break;
				}

				// dateSelected = DateParser.parseToServerDate(rawDateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onDateTimeSelected(Date dateSelected) {
		this.dateSelected = dateSelected;
		finishWithResult();
	}

	@Override
	public void onCancel() {
		finishWithResult();
	}

	private void finishWithResult() {

		int resultCode = ActivityRequestCodes.PICK_DATE;

		switch (dateTimePickerType) {
			case DATE_ONLY: {
				resultCode = ActivityRequestCodes.PICK_DATE;
			}
				break;
			case TIME_ONLY: {
				resultCode = ActivityRequestCodes.PICK_TIME;
			}
				break;
			case DATE_TIME: {
				resultCode = ActivityRequestCodes.PICK_DATE_TIME;
			}
				break;
		}

		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_DATE_TIME_PICKER_FIELD_ID, fieldId);
		resultIntent.putExtra(EXTRA_RESULT_SELECTED_DATE, dateSelected);
		setResult(resultCode, resultIntent);
		finish();

	}

}
