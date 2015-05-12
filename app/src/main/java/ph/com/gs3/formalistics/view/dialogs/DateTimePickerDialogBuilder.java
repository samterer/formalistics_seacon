package ph.com.gs3.formalistics.view.dialogs;

import android.app.Dialog;
import android.content.Context;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.DateTimePickerType;

public class DateTimePickerDialogBuilder {

	public static final String TAG = DateTimePickerDialogBuilder.class.getSimpleName();

	private final Context context;

	private final String title;

	public DateTimePickerDialogBuilder(DateTimePickerType dateTimePickerType, Context context) {

		this.context = context;

		switch (dateTimePickerType) {
			case DATE_ONLY: {
				title = "Pick a date";
			}
				break;
			case DATE_TIME: {
				title = "Pick a date and time";
			}
				break;
			case TIME_ONLY: {
				title = "Pick a time";
			}
				break;
			default: {
				title = "";
			}
		}

	}

	public Dialog createDialog() {

		Dialog dialog = new Dialog(context);

		dialog.setTitle(title);
		dialog.setContentView(R.layout.fragment_date_time_picker);

		return dialog;

	}
}
