package ph.com.gs3.formalistics.global.constants;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import android.content.Context;
import android.graphics.Typeface;

public class Constants {

	public static final String TAG = Constants.class.getSimpleName();

	private static Constants applicationInstance;
	private Typeface globalTypeface;

	/**
	 * Returns the application scope instance of this class. Before getting the instance,
	 * the user must create it by calling createApplicationInstance(context) in a place
	 * where it will be called once (Preferably the application class).
	 * 
	 * @return
	 */
	public static Constants getApplicationInstance() {

		if (applicationInstance == null) {
			throw new IllegalStateException(
			        "Tried to get application instance while it's not yet created.");
		}

		return applicationInstance;
	}

	public static Constants createApplicationInstace(Context context) {

		if (applicationInstance == null) {
			applicationInstance = new Constants(context);
		} else {
			FLLogger.w(
			        TAG,
			        "createApplicationInstace called again after an application instance is already created, the call to create the application instance of this class is ignored.");
		}

		return applicationInstance;

	}

	private Constants(Context context) {

		globalTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/opensansregular.ttf");

	}

	public String getGS3Server() {
		return "http://formalistics.com.ph";
	}

	public Typeface getGlobalTypeface() {
		return globalTypeface;
	}

}
