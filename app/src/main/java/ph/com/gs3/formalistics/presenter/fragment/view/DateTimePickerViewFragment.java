package ph.com.gs3.formalistics.presenter.fragment.view;

import java.util.Calendar;
import java.util.Date;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.DateTimePickerType;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class DateTimePickerViewFragment extends Fragment {

	public static final String TAG = DateTimePickerViewFragment.class.getSimpleName();

	// Views
	private DatePicker dpDate;
	private TimePicker tpTime;

	private Button bOK;
	private Button bCancel;

	// Fields
	private DateTimePickerType dateTimePickerType;
	private Date dateSelected;

	private DateTimePickerViewActionListener listener;

	public static DateTimePickerViewFragment createInstance(DateTimePickerType dateTimePickerType,
	        Date dateSelected) {

		DateTimePickerViewFragment instance = new DateTimePickerViewFragment();

		instance.dateTimePickerType = dateTimePickerType;
		instance.dateSelected = dateSelected;

		return instance;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			listener = (DateTimePickerViewActionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
			        + " must implement DateTimePickerViewActionListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_date_time_picker, container, false);

		initializeDatePickerView(rootView);
		initializeTimePickerView(rootView);
		initializeButtons(rootView);

		// pending set of date
		if (dateSelected != null) {
			setSelectedDate(dateSelected);
		}

		return rootView;
	}

	private void initializeDatePickerView(View rootView) {

		dpDate = (DatePicker) rootView.findViewById(R.id.DTP_dpCalendarDatePicker);

		switch (dateTimePickerType) {
			case DATE_ONLY: {
				// dpDate.setCalendarViewShown(true);
				// dpDate.setSpinnersShown(false);
				// tpTime.setVisibility(View.GONE);
			}
				break;
			case TIME_ONLY: {
				dpDate.setVisibility(View.GONE);
			}
				break;
			case DATE_TIME: {
				dpDate.setCalendarViewShown(false);
				dpDate.setSpinnersShown(true);
			}
				break;
		}

	}

	private void initializeTimePickerView(View rootView) {

		tpTime = (TimePicker) rootView.findViewById(R.id.DTP_tpTime);

		switch (dateTimePickerType) {
			case DATE_ONLY: {
				tpTime.setVisibility(View.GONE);
			}
				break;
			case TIME_ONLY:
			case DATE_TIME: {
				tpTime.setVisibility(View.VISIBLE);
			}
				break;
		}

	}

	private void initializeButtons(View rootView) {

		bOK = (Button) rootView.findViewById(R.id.DTP_bOK);
		bCancel = (Button) rootView.findViewById(R.id.DTP_bCancel);

		bOK.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(0);
				cal.set(dpDate.getYear(), dpDate.getMonth(), dpDate.getDayOfMonth(),
				        tpTime.getCurrentHour(), tpTime.getCurrentMinute());

				listener.onDateTimeSelected(cal.getTime());
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.onCancel();
			}
		});

	}

	public void setSelectedDate(Date date) {

		dateSelected = date;

		if (isAdded()) {

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateSelected);

			dpDate.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
			        calendar.get(Calendar.DAY_OF_MONTH));
		}

	}

	public interface DateTimePickerViewActionListener {
		void onDateTimeSelected(Date dateSelected);

		void onCancel();
	}

}
