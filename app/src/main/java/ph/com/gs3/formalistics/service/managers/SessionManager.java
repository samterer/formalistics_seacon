package ph.com.gs3.formalistics.service.managers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.global.constants.SessionMode;
import ph.com.gs3.formalistics.global.interfaces.CallbackCommand;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.api.factory.APIFactory;
import ph.com.gs3.formalistics.model.dao.CompaniesDAO;
import ph.com.gs3.formalistics.model.dao.UsersDAO;
import ph.com.gs3.formalistics.model.dao.facade.CompaniesDataWriterFacade;
import ph.com.gs3.formalistics.model.dao.facade.UsersDataWriterFacade;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;
import ph.com.gs3.formalistics.model.values.business.Company;
import ph.com.gs3.formalistics.model.values.business.User;

public class SessionManager {

    public static final String TAG = SessionManager.class.getSimpleName();

    private static SessionManager applicationInstance;

    // =====================================================================
    // Server errors/exceptions
    public static final String SERVER_ERROR_USER_NOT_FOUND = "User Not Found";

    // =====================================================================
    // Dependencies

    protected CompaniesDAO companiesDAO;
    protected UsersDAO usersDAO;
    protected UsersAPI usersAPI;

    protected CompaniesDataWriterFacade companiesDataWriterFacade;
    protected UsersDataWriterFacade usersDataWriterFacade;

    // State Fields

    protected Context applicationContext;

    protected User currentUser;

    protected boolean isCurrentlyCheckingServerConnection;
    protected boolean isAuthenticated;
    private SessionMode sessionMode;
    protected int connectivityType;

    private boolean isCurrentlyNotifyingListeners;
    private List<SessionManagerEventListener> listenerAddQueue;
    private List<SessionManagerEventListener> listenerRemoveQueue;

    private List<CallbackCommand<CheckConnectionResult>> checkConnectionMainThreadCallbackList;
    private List<CallbackCommand<CheckConnectionResult>> checkConnectionCallbackList;

    //==================================================
    protected BroadcastReceiver networkChangeBroadcastReceiver;
    protected List<SessionManagerEventListener> listeners;

    public static SessionManager createApplicationInstance(Context applicationContext) {

        if (applicationInstance != null) {
            throw new RuntimeException("You may only create one application instance of SessionManager");
        }

        applicationInstance = new SessionManager(applicationContext);
        applicationInstance.applicationContext = applicationContext;
        return applicationInstance;
    }

    public static SessionManager getApplicationInstance() {
        return applicationInstance;
    }

    private SessionManager(Context context) {

        companiesDAO = new CompaniesDAO(context);
        usersDAO = new UsersDAO(context);

        companiesDataWriterFacade = new CompaniesDataWriterFacade(context);
        usersDataWriterFacade = new UsersDataWriterFacade(context);

        sessionMode = SessionMode.OFFLINE;

        listeners = new ArrayList<SessionManagerEventListener>();
        listenerAddQueue = new ArrayList<SessionManagerEventListener>();
        listenerRemoveQueue = new ArrayList<SessionManagerEventListener>();

        isAuthenticated = false;
        isCurrentlyCheckingServerConnection = false;

        isCurrentlyNotifyingListeners = false;

        checkConnectionMainThreadCallbackList = new ArrayList<>();
        checkConnectionCallbackList = new ArrayList<>();

        connectivityType = -1;// no connection

    }

    public synchronized void startListeningToNetworkChanges() {
        initializeNetworkChangeBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        applicationContext.registerReceiver(networkChangeBroadcastReceiver, intentFilter);
    }

    public synchronized User login(User user) throws UsersAPI.LoginException {

        String server = user.getCompany().getServer();
        String email = user.getEmail();
        String password = user.getPassword();

        return login(server, email, password);

    }

    public synchronized User login(String server, String email, String password) throws UsersAPI.LoginException {

        //  Validate Inputs
        List<UsersAPI.LoginField> requiredFields = new ArrayList<>();

        if (server == null || "".equalsIgnoreCase(server.trim())) {
            requiredFields.add(UsersAPI.LoginField.SERVER);
        }

        if (email == null || "".equalsIgnoreCase(email.trim())) {
            requiredFields.add(UsersAPI.LoginField.EMAIL);
        }

        if (password == null || "".equalsIgnoreCase(password.trim())) {
            requiredFields.add(UsersAPI.LoginField.PASSWORD);
        }

        if (requiredFields.size() > 0) {
            for (UsersAPI.LoginField field : requiredFields) {
                FLLogger.d(TAG, "The field " + field + " is required");
            }

            throw new UsersAPI.LoginException(
                    "This field is required",
                    requiredFields.toArray(new UsersAPI.LoginField[requiredFields.size()])
            );
        }

        APIFactory usersAPIImplFactory = new APIFactory();
        usersAPI = usersAPIImplFactory.createUsersAPI(server);

        // throws UsersAPI.LoginException
        User loggedInUser = usersAPI.login(email, password);

        // if this version is a custom version for a specific company
        VersionSettings versionSettings = FormalisticsApplication.versionSettings;
        if (versionSettings.version != VersionSettings.AvailableVersion.DEFAULT) {
            Company similarCompany = companiesDAO.getSimilarCompanyFromDifferentServer(loggedInUser.getCompany());

            // If there is a similar company found
            if (similarCompany != null) {
                FLLogger.i(TAG, "Updating user company server");
                // update the server of the company
                int companyWebId = loggedInUser.getCompany().getWebId();
                String companyName = loggedInUser.getCompany().getName();
                String newServer = loggedInUser.getCompany().getServer();
                companiesDAO.updateCompanyServer(companyWebId, companyName, newServer);
            }
        }

        // Update the company and the user in the database
        Company registeredCompany = companiesDataWriterFacade.registerCompany(loggedInUser.getCompany());
        loggedInUser.setCompany(registeredCompany);
        loggedInUser.setActive(User.ACTIVE);

        User registeredUser = usersDataWriterFacade.registerOrUpdateUser(loggedInUser);
        usersDAO.activateUser(registeredUser.getId());

        currentUser = registeredUser;
        sessionMode = SessionMode.ONLINE;
        isAuthenticated = true;

        return registeredUser;

    }

    public void logout() {
        usersDAO.deactivateAllUsers();
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public boolean isCurrentlyCheckingServerConnection() {
        return isCurrentlyCheckingServerConnection;
    }

    public User getActiveUser() {
        return usersDAO.getActiveUser();
    }

    public SessionMode getSessionMode() {
        return sessionMode;
    }

    private void setSessionMode(SessionMode sessionMode) {

        SessionMode oldSessionMode = this.sessionMode;
        this.sessionMode = sessionMode;

        if (oldSessionMode != sessionMode) {
            notifyListenersModeChanged();
        }

    }

    /**
     * Checks connection to the server and executes one list of callbacks from the backend
     * thread and another one from the main thread (added through backendThreadCallback,
     * and mainThreadCallback).
     * <p/>
     * If this method is called multiple times while it's still currently checking
     * connection, it will just add the callbacks to the list of callbacks and will be
     * executed by batch later on after the current connection checking. The lists will be
     * cleared after connection checking is done.
     *
     * @param backendThreadCallback
     * @param mainThreadCallback
     */
    public synchronized void checkConnectionToServer(
            CallbackCommand<CheckConnectionResult> backendThreadCallback,
            CallbackCommand<CheckConnectionResult> mainThreadCallback) {

        checkConnectionCallbackList.add(backendThreadCallback);
        checkConnectionMainThreadCallbackList.add(mainThreadCallback);

        if (!isCurrentlyCheckingServerConnection) {
            isCurrentlyCheckingServerConnection = true;
            new CheckConnectionTask().execute();
        }

    }

    // <editor-fold desc="Utility Methods">

    private void log(String message) {
        if (enableDebugLogging) {
            FLLogger.d(TAG, message);
        }
    }

    private void log(String message, FLLogger.LogType logType) {
        if (logType == FLLogger.LogType.DEBUG) {
            log(message);
        } else {
            //   Log anything that's not a debug log
            FLLogger.log(TAG, logType, message);
        }
    }

    private boolean enableDebugLogging = false;

    public void enableDebugLogging(boolean enable) {
        enableDebugLogging = enable;
    }

    // </editor-fold>

    // =============================================================================================
    protected void initializeNetworkChangeBroadcastReceiver() {

        networkChangeBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                // Note: onReceive is called twice on cherry mobile flare 2x
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                onConnectivityChanged(activeNetwork);
            }

        };
    }

    private synchronized void onConnectivityChanged(NetworkInfo activeNetwork) {

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            connectivityType = activeNetwork.getType();
            checkConnectionToServer(null, new CallbackCommand<CheckConnectionResult>() {

                @Override
                public void execute(CheckConnectionResult result) {
                    String modeChangeDebugMessage = "Mode changed: ";

                    if (result.connectionStatus == HttpCommunicator.STATUS_CONNECTED) {
                        setSessionMode(SessionMode.ONLINE);
                        modeChangeDebugMessage += "Online mode";
                    } else if (result.connectionStatus == HttpCommunicator.STATUS_ERROR_ON_CONNECT) {
                        setSessionMode(SessionMode.OFFLINE);
                        modeChangeDebugMessage += "Offline mode, " + result.errorMessage;

                        notifyListenersSessionLost();
                    } else {
                        setSessionMode(SessionMode.OFFLINE);
                        modeChangeDebugMessage += "Offline mode, unable to connect to server";
                    }

                    log(modeChangeDebugMessage);
                }

            });
        } else {
            connectivityType = -1;
            setSessionMode(SessionMode.OFFLINE);
            notifyListenersModeChanged();

            log("Mode changed: Offline mode, no wifi or mobile connection available");
        }
    }

    // <editor-fold desc="Observation Methods">

    protected synchronized void notifyListenersModeChanged() {

        FLLogger.d(TAG, "Notifying listeners");

        isCurrentlyNotifyingListeners = true;

        for (SessionManagerEventListener listener : listeners) {
            listener.onModeChanged(sessionMode);
        }

        isCurrentlyNotifyingListeners = false;

    }

    /**
     * Unsupported
     */
    protected synchronized void notifyListenersSessionLost() {

        isCurrentlyNotifyingListeners = true;

        for (SessionManagerEventListener listener : listeners) {
            listener.onSessionLost();
        }

        isCurrentlyNotifyingListeners = false;

    }

    public synchronized void addListener(SessionManagerEventListener listener) {
        if (isCurrentlyNotifyingListeners) {
            listenerAddQueue.add(listener);
        } else {
            listeners.add(listener);
        }
    }

    public synchronized void removeListener(SessionManagerEventListener listener) {
        if (isCurrentlyNotifyingListeners) {
            listenerRemoveQueue.add(listener);
        } else {
            listeners.remove(listener);
        }
    }

    protected synchronized void applyQueuedListeners() {

        if (listenerAddQueue.size() > 0) {
            listeners.addAll(listenerAddQueue);
            listenerAddQueue.clear();
        }

        if (listenerRemoveQueue.size() > 0) {
            listeners.removeAll(listenerRemoveQueue);
            listenerRemoveQueue.clear();
        }

    }

    // </editor-fold>

    // <editor-fold desc="Classes & Interfaces">

    public static class CheckConnectionResult {

        public int connectionStatus;
        public String errorMessage;

    }

    private class CheckConnectionTask extends AsyncTask<Void, Void, CheckConnectionResult> {

        @Override
        protected CheckConnectionResult doInBackground(Void... params) {
            User currentlyActiveUser = usersDAO.getActiveUser();

            HttpCommunicator httpCommunicator = new HttpCommunicator();
            CheckConnectionResult result = new CheckConnectionResult();

            if (currentlyActiveUser != null) {
                String server = currentlyActiveUser.getCompany().getServer();

                log("Checking connection to: " + server);
                int connectionStatus = httpCommunicator.testConnection(server);

                result.connectionStatus = connectionStatus;
                result.errorMessage = null;

                if (connectionStatus == HttpCommunicator.STATUS_CONNECTED) {
                    log("Connected");
                } else {
                    log("Disconnected");
                }

            } else {
                result.connectionStatus = HttpCommunicator.STATUS_ERROR_ON_CONNECT;
                result.errorMessage = "No active user yet";
            }

            for (CallbackCommand<CheckConnectionResult> callback : checkConnectionCallbackList) {
                if (callback != null) {
                    callback.execute(result);
                }
            }

            checkConnectionCallbackList.clear();

            return result;

        }

        @Override
        protected void onPostExecute(CheckConnectionResult result) {
            super.onPostExecute(result);

            isCurrentlyCheckingServerConnection = false;

            for (CallbackCommand<CheckConnectionResult> callback : checkConnectionMainThreadCallbackList) {
                if (callback != null) {
                    callback.execute(result);
                }
            }

            checkConnectionMainThreadCallbackList.clear();

        }

    }

    public static interface SessionManagerEventListener {
        public void onModeChanged(SessionMode mode);

        public void onSessionLost();
    }

    // </editor-fold>

}
