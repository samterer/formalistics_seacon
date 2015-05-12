package ph.com.gs3.formalistics.service.broadcaster;

import android.app.IntentService;
import android.content.Intent;

public class Broadcaster {

	public static final String TAG = Broadcaster.class.getSimpleName();

	public static final String FULL_UPDATE_DONE = "full_update_done";
	public static final String FULL_UPDATE_NOT_STARTED = "full_update_not_started";
	public static final String REAUTHENTICATION_REQUIRED = "re_authentication_required";
	public static final String FORMS_SYNCHRONIZED = "forms_synchronized";
	public static final String DOCUMENTS_PARTIALLY_SYNCHRONIZED = "documents_partially_synchronized";
	public static final String OUGTOING_ACTIONS_SUBMITTED = "outgoing_actions_submitted";
	public static final String DOCUMENTS_SYNCHRONIZED = "documents_synchronized";
	public static final String COMMENTS_SYNCHRONIZED = "comments_synchronized";

	public static final String[] FULL_UPDATE_ACTIONS = { FULL_UPDATE_DONE, FULL_UPDATE_NOT_STARTED,
	        REAUTHENTICATION_REQUIRED, FORMS_SYNCHRONIZED, OUGTOING_ACTIONS_SUBMITTED,
	        DOCUMENTS_PARTIALLY_SYNCHRONIZED, DOCUMENTS_SYNCHRONIZED, COMMENTS_SYNCHRONIZED };

	public static final String EXTRA_MESSAGE = "message";

	private IntentService source;

	public Broadcaster(IntentService source) {
		this.source = source;
	}

	public void broadcastFullUpdateNotStarted(String message) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(FULL_UPDATE_NOT_STARTED);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra(EXTRA_MESSAGE, message);
		source.sendBroadcast(broadcastIntent);
	}

	public void broadcast(String action) {
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(action);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		source.sendBroadcast(broadcastIntent);
	}
}
