package ph.com.gs3.formalistics.service.managers;

import android.content.Context;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.constants.SyncType;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.values.business.Company;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.service.synchronizers.CommentsSycnhronizer;
import ph.com.gs3.formalistics.service.synchronizers.DocumentsSynchronizer;
import ph.com.gs3.formalistics.service.synchronizers.DocumentsSynchronizer.DocumentsSynchronizerEventListener;
import ph.com.gs3.formalistics.service.synchronizers.FilesSynchronizer;
import ph.com.gs3.formalistics.service.synchronizers.FormsSynchronizer;
import ph.com.gs3.formalistics.service.synchronizers.OutgoingActionsSynchronizer;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationFailedException;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationPrematureException;

/**
 * Created by Ervinne on 4/8/2015.
 */
public class DataSynchronizationManager {

    public static final String TAG = DataSynchronizationManager.class.getSimpleName();

    private Context applicationContext;

    private SessionManager sessionManager;
    private DataSynchronizationManagerEventsListener eventsListener;
    private DocumentsSynchronizerEventListener documentsSynchronizerEventListener;

    // <editor-fold desc="Settings">
    public static LoggingType LOGGING_TYPE;
    // </editor-fold>

    // <editor-fold desc="Constants & Enums">

    public static enum UpdateMode {
        NO_REAUTHENTICATION, REAUTHENTICATE_FIRST
    }
    // </editor-fold>

    // <editor-fold desc="Synchronizers">

    private FormsSynchronizer formsSynchronizer;
    private OutgoingActionsSynchronizer outgoingActionsSynchronizer;
    private DocumentsSynchronizer documentsSynchronizer;
    private CommentsSycnhronizer commentsSycnhronizer;
    private FilesSynchronizer filesSynchronizer;

    // </editor-fold>

    // <editor-fold desc="Application Instance Keeper">

    private static DataSynchronizationManager applicationInstance;

    public static DataSynchronizationManager createApplicationInstance(Context applicationContext) {

        if (applicationInstance != null) {
            throw new RuntimeException(TAG + " can only be instantiated once");
        }

        applicationInstance = new DataSynchronizationManager(applicationContext);
        return applicationInstance;

    }

    public static DataSynchronizationManager getApplicationInstance() {
        return applicationInstance;
    }

    private DataSynchronizationManager(Context applicationContext) {
        this.applicationContext = applicationContext;

        sessionManager = SessionManager.getApplicationInstance();
    }

    // </editor-fold>

    /**
     * Full update will first check if the session manager is authenticated. If it's not,
     * then this will try to execute login using the User object passed to this method.
     * <p/>
     * If the session manager is now authenticated, full synchronization will start,
     * otherwise, the events listener will be notified that re-authentication is needed.
     */
    public void startFullUpdate(SyncType syncType, User activeUser, String partialSyncFormIds) throws UsersAPI.LoginException {

        User authenticatedUser = null;

        if (sessionManager == null) {
            throw new RuntimeException("Session manager cannot be null");
        }

        if (activeUser == null) {
            log("Sych suspended, no active user yet");
        } else {
            // Re-authenticate if the session manager is not authenticated yet.
            if (!sessionManager.isAuthenticated()) {
                log("ReAuthenticating @" + activeUser.getCompany().getServer());
                try {
                    authenticatedUser = reAuthenticate(activeUser);
                } catch (UsersAPI.LoginException e) {
                    if (e.getCause() != null) {
                        log("Failed automatic login - " + e.getCause().getMessage());
                    } else {
                        log("Failed automatic login - " + e.getMessage());
                    }
                    if (e.getCause() instanceof HttpCommunicator.CommunicationException) {
                        // Login failed due to connection, stop the synchronization
                        // service, but do not require re-authentication

                        log("Sych suspended due to connection problems");

                        eventsListener.onFullUpdateDone();
                        return;
                    }
                    throw e;
                }
            } else {
                // The active user is already authenticated
                authenticatedUser = activeUser;
            }
        }

        // Execute full update if authenticated, otherwise, notify that the application
        // needs an authenticated user
        if (sessionManager.isAuthenticated()) {
            startSynchronization(authenticatedUser, syncType, partialSyncFormIds);
            eventsListener.onFullUpdateDone();
        } else {
            eventsListener.onReAuthenticationNeeded();
        }

    }

    /**
     * Executes all synchronization tasks in order.
     *
     * @param user The user that owns the records to be synchronized.
     */
    private synchronized void startSynchronization(User user, SyncType syncType, String partialSyncFormIds) {

        long startTime = System.currentTimeMillis();

        // FIXME: Notify the users about the failed synchronization exceptions

        if (documentsSynchronizerEventListener == null) {
            throw new RuntimeException("documentsSynchronizerEventListener must not be null");
        }

        if (syncType == SyncType.FULL_SYNC || syncType == SyncType.PARTIAL_WITH_FORMS) {
            // Synchronize forms
            log("Synchronizing forms");
            try {
                formsSynchronizer = new FormsSynchronizer(applicationContext, user);
                formsSynchronizer.synchronize();
            } catch (SynchronizationFailedException e) {
                e.printStackTrace();
            }
        }

        filesSynchronizer = new FilesSynchronizer(applicationContext, user);
        filesSynchronizer.uploadOutgoingFiles();

        try {
            outgoingActionsSynchronizer = new OutgoingActionsSynchronizer(applicationContext, user);
            outgoingActionsSynchronizer.synchronize();
        } catch (SynchronizationFailedException e) {
            e.printStackTrace();
        } catch (SynchronizationPrematureException e) {
            e.printStackTrace();
        }

        try {
            documentsSynchronizer = new DocumentsSynchronizer(applicationContext, user, documentsSynchronizerEventListener);
            documentsSynchronizer.synchronize(syncType, partialSyncFormIds);
        } catch (SynchronizationFailedException e) {
            e.printStackTrace();
        } catch (SynchronizationPrematureException e) {
            e.printStackTrace();
        }

        try {
            commentsSycnhronizer = new CommentsSycnhronizer(applicationContext, user);
            commentsSycnhronizer.synchronize(syncType, partialSyncFormIds);
        } catch (SynchronizationFailedException e) {
            e.printStackTrace();
        } catch (SynchronizationPrematureException e) {
            e.printStackTrace();
        }

        filesSynchronizer.downloadIncomingFiles();

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        FLLogger.d(TAG, "Finished synchronization in " + elapsedTime + "ms");

    }

    private User reAuthenticate(User user) throws UsersAPI.LoginException {

        Company userCompany = user.getCompany();

        String server = userCompany.getServer();
        String email = user.getEmail();
        String password = user.getPassword();

        return sessionManager.login(server, email, password);

    }

    public DataSynchronizationManagerEventsListener getEventsListener() {
        return eventsListener;
    }

    public void setEventsListener(DataSynchronizationManagerEventsListener eventsListener) {
        this.eventsListener = eventsListener;
    }

    private void log(String log) {
        if (LOGGING_TYPE == LoggingType.ENABLED) {
            FLLogger.d(TAG, log);
        }
    }

    public void setDocumentsSynchronizerEventListener(DocumentsSynchronizerEventListener documentsSynchronizerEventListener) {
        this.documentsSynchronizerEventListener = documentsSynchronizerEventListener;
    }

    public static interface DataSynchronizationManagerEventsListener {

        public void onFormsSynchronized();

        public void onReAuthenticationNeeded();

        public void onFullUpdateDone();

        public void onOutgoingActionsSent();

        public void onDocumentsSynchronized();

    }

}
