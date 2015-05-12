package ph.com.gs3.formalistics.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import ph.com.gs3.formalistics.global.constants.SyncType;
import ph.com.gs3.formalistics.global.interfaces.CallbackCommand;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.service.broadcaster.Broadcaster;
import ph.com.gs3.formalistics.service.managers.DataSynchronizationManager;
import ph.com.gs3.formalistics.service.managers.DataSynchronizationManager.DataSynchronizationManagerEventsListener;
import ph.com.gs3.formalistics.service.managers.SessionManager;
import ph.com.gs3.formalistics.service.managers.SessionManager.CheckConnectionResult;
import ph.com.gs3.formalistics.service.synchronizers.DocumentsSynchronizer.DocumentsSynchronizerEventListener;

/**
 * Created by Ervinne on 4/8/2015.
 */
public class DataSynchronizationService extends IntentService {

    public static final String TAG = DataSynchronizationService.class.getSimpleName();
    public static final String NAME = DataSynchronizationService.class.getName();
    public static final String EXTRA_SYNC_TYPE = "sync_type";
    public static final String EXTRA_PARTIAL_SYNC_FORM_IDS = "partial_sync_form_ids";

    private final DataSynchronizationManager fullUpdateManager;
    private final SessionManager sessionManager;
    private Broadcaster broadcaster;

    private static boolean fullUpdateOngoing;

    public DataSynchronizationService() {
        super(NAME);

        sessionManager = SessionManager.getApplicationInstance();

        fullUpdateManager = DataSynchronizationManager.getApplicationInstance();
        fullUpdateManager.setDocumentsSynchronizerEventListener(documentsSynchronizerEventListener);
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();

        final SyncType syncType;
        final String partialSyncFormIds;

        if (extras != null && extras.containsKey(EXTRA_SYNC_TYPE)) {
            syncType = (SyncType) extras.get(EXTRA_SYNC_TYPE);
        } else {
            syncType = SyncType.FULL_SYNC;
        }

        if (extras != null && extras.containsKey(EXTRA_PARTIAL_SYNC_FORM_IDS)) {
            partialSyncFormIds = extras.getString(EXTRA_PARTIAL_SYNC_FORM_IDS);
        } else {
            partialSyncFormIds = null;
        }

        FLLogger.i(TAG, "Starting synchronization service, mode: " + syncType.name());

        broadcaster = new Broadcaster(this);

        // Check session, then start synchronizing if session is available
        // sessionMan.addListener(sessionEventListener);
        sessionManager.checkConnectionToServer(
                new CallbackCommand<CheckConnectionResult>() {

                    @Override
                    public void execute(CheckConnectionResult result) {

                        if (result.connectionStatus == HttpCommunicator.STATUS_CONNECTED) {

                            User lastActiveUser = SessionManager.getApplicationInstance().getActiveUser();
                            try {
//                                User loggedInUser = SessionManager.getApplicationInstance().login(lastActiveUser);
//                                startUpdateService(syncType, loggedInUser);
                                startUpdateService(syncType, lastActiveUser, partialSyncFormIds);
                            } catch (UsersAPI.LoginException e) {
                                e.printStackTrace();
                                eventsListener.onReAuthenticationNeeded();
                            }

                        } else {
                            broadcaster.broadcastFullUpdateNotStarted();
                        }

                    }
                }, null);
    }

    private void startUpdateService(SyncType syncType, User activeUser, String partialSyncFormIds) throws UsersAPI.LoginException {
        if (!fullUpdateOngoing) {
            fullUpdateOngoing = true;
            fullUpdateManager.setEventsListener(eventsListener);
            fullUpdateManager.startFullUpdate(syncType, activeUser, partialSyncFormIds);
            fullUpdateOngoing = false;
        } else {
            FLLogger.d(TAG, "Full update manager is still busy.");
        }
    }

    // <editor-fold desc="Event Listeners">

    private final DocumentsSynchronizerEventListener documentsSynchronizerEventListener = new DocumentsSynchronizerEventListener() {

        @Override
        public void onNewDocumentsDownloaded() {
            broadcaster.broadcast(Broadcaster.DOCUMENTS_PARTIALLY_SYNCHRONIZED);
        }

        @Override
        public void onAllFormsDownloaded() {
            broadcaster.broadcast(Broadcaster.DOCUMENTS_SYNCHRONIZED);
        }
    };

    private final DataSynchronizationManagerEventsListener eventsListener = new DataSynchronizationManagerEventsListener() {

        @Override
        public void onReAuthenticationNeeded() {
            sessionManager.logout();
            broadcaster.broadcast(Broadcaster.REAUTHENTICATION_REQUIRED);
            broadcaster.broadcast(Broadcaster.FULL_UPDATE_DONE);
        }

        @Override
        public void onFullUpdateDone() {
            broadcaster.broadcast(Broadcaster.FULL_UPDATE_DONE);
        }

        @Override
        public void onFormsSynchronized() {
            broadcaster.broadcast(Broadcaster.FORMS_SYNCHRONIZED);
        }

        @Override
        public void onOutgoingActionsSent() {
            broadcaster.broadcast(Broadcaster.OUGTOING_ACTIONS_SUBMITTED);
        }

        @Override
        public void onDocumentsSynchronized() {
            broadcaster.broadcast(Broadcaster.DOCUMENTS_SYNCHRONIZED);
        }

    };

    // </editor-fold>

}
