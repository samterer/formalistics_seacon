package ph.com.gs3.formalistics.presenter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ActivityRequestCodes;
import ph.com.gs3.formalistics.global.constants.ActivityResultCodes;
import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.global.constants.DocumentType;
import ph.com.gs3.formalistics.global.constants.SessionMode;
import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.constants.SyncType;
import ph.com.gs3.formalistics.global.interfaces.CallbackCommand;
import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.global.utilities.view.ViewUtils;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.OutgoingActionsDAO;
import ph.com.gs3.formalistics.model.dao.UserDocumentsDAO;
import ph.com.gs3.formalistics.model.dao.facade.OutgoingActionsDataWriterFacade;
import ph.com.gs3.formalistics.model.dao.facade.search.SearchDataProvider;
import ph.com.gs3.formalistics.model.dao.facade.search.SearchDataProviderFactory;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;
import ph.com.gs3.formalistics.model.values.application.ViewFilter;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;
import ph.com.gs3.formalistics.presenter.fragment.view.DocumentListViewFragment;
import ph.com.gs3.formalistics.presenter.fragment.view.DocumentListViewFragment.DocumentListViewFragmentActionListener;
import ph.com.gs3.formalistics.presenter.fragment.view.NavigationDrawerFragment;
import ph.com.gs3.formalistics.presenter.navigation.DocumentListNavigationPresenter;
import ph.com.gs3.formalistics.presenter.navigation.DocumentListNavigationPresenterEventsListener;
import ph.com.gs3.formalistics.presenter.navigation.DocumentListNavigationPresenterFactory;
import ph.com.gs3.formalistics.service.DataSynchronizationService;
import ph.com.gs3.formalistics.service.broadcaster.Broadcaster;
import ph.com.gs3.formalistics.service.managers.SessionManager;
import ph.com.gs3.formalistics.service.managers.SessionManager.SessionManagerEventListener;
import ph.com.gs3.formalistics.view.adapters.DocumentListItemActionListener;
import ph.com.gs3.formalistics.view.dialogs.FormSelectionDialogFragment;
import ph.com.gs3.formalistics.view.dialogs.ListSelectionDialogFragment;

public class DocumentListActivity extends Activity implements
        DocumentListViewFragmentActionListener, DocumentListItemActionListener, DocumentListNavigationPresenterEventsListener {

    public static final String TAG = DocumentListActivity.class.getSimpleName();
    public static final String EXTRA_ACTIVE_USER = "active_user";

    private static final int SEARCH_ITEM_COUNT = 20;
    private static final String PREF_FIRST_FULL_SYNC_EXECUTED = "first_full_sync_executed";

    //<editor-fold desc="Fields: Fragments, Controllers/Managers & Views">
    private DocumentListViewFragment documentListViewFragment;
    private NavigationDrawerFragment navigationDrawerFragment;
    private SearchView searchView;

    private DocumentListNavigationPresenter documentListNavigationManager;

    //</editor-fold>

    //<editor-fold desc="Fields: Dependencies">
    private FormsDAO formsDAO;
    private UserDocumentsDAO userDocumentsDAO;
    private DocumentsDAO documentsDAO;
    private OutgoingActionsDAO outgoingActionsDAO;

    private OutgoingActionsDataWriterFacade outgoingActionsDataWriterFacade;

    private SearchDataProvider searchDataProvider;
    //</editor-fold>

    // <editor-fold desc="Fields: State Fields">

    private boolean pressedClose = false;
    private User activeUser;
    private int currentlySelectedFormId = 0;
    private boolean activityJustChangedOrientation = false;

    private NavigationDrawerItem currentlySelectedNavigationDrawerItem;
    private ViewFilter currentViewFilter;

    private EnumSet<DocumentSearchType> documentSearchTypes;
    private String currentFilter;

    // </editor-fold>

    //<editor-fold desc="Fields: Other Fields">
    private Timer searchDelayTimer;
    //</editor-fold>

    // <editor-fold desc="Activity Life Cycle">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_document_list);

        initializeOtherFields();
        initializeStateTransferredFields();
        initializeDependencies();
        registerBroadcastReceivers();

        SessionManager.getApplicationInstance().addListener(sessionManagerEventListener);

        if (savedInstanceState == null) {
            documentListViewFragment = DocumentListViewFragment.createInstance(activeUser);
            getFragmentManager().beginTransaction()
                    .add(R.id.container, documentListViewFragment, DocumentListViewFragment.TAG)
                    .commit();
            activityJustChangedOrientation = false;
        } else {
            documentListViewFragment = (DocumentListViewFragment) getFragmentManager().findFragmentByTag(DocumentListViewFragment.TAG);
            documentListViewFragment.setActiveUser(activeUser);
            activityJustChangedOrientation = true;
        }

        documentListNavigationManager = DocumentListNavigationPresenterFactory.createNew(
                DocumentListActivity.this,
                activeUser,
                DocumentListActivity.this
        );

        navigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.DocumentList_fNavigationDrawer);
        // Set up the drawer.
        navigationDrawerFragment.setUp(
                R.id.DocumentList_fNavigationDrawer,
                (DrawerLayout) findViewById(R.id.DocumentList_dlDrawerLayout),
                documentListNavigationManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (FormalisticsApplication.versionSettings.enableDocumentCreationGlobally) {
            getMenuInflater().inflate(R.menu.document_list_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.document_list_menu_no_creation, menu);
        }

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                searchDelayTimer.cancel();
                searchDelayTimer = new Timer();
                searchDelayTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        DocumentListActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                filterView(newText);
                            }
                        });
                    }
                }, 1000);// 1 sec delay

                return true;
            }
        });

        // Listen to when the search bar is closed
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    searchView.setIconified(true);
                    searchView.setQuery("", false);

                    searchMenuItem.collapseActionView();
                    filterView("");
                }
            }
        });


        ViewUtils.changeTextViewsColorInsideView(searchView, Color.BLACK);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_compose_document:
                onCreateFormCommand();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                navigationDrawerFragment.openDrawer();
                return true;
        }

        return super.onKeyDown(keyCode, e);
    }

    @Override
    public void onBackPressed() {

        if (!pressedClose) {
            Toast.makeText(this, "Press Back again to quit", Toast.LENGTH_SHORT).show();
            pressedClose = true;

            //  Reset the pressedClose if in case the user does not press close again after 2 seconds
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            pressedClose = false;
                        }
                    },
                    2000);
        } else {
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bundle extras = null;

        if (data != null) {
            extras = data.getExtras();
        }

        switch (requestCode) {
            case IntentIntegrator.REQUEST_CODE: { // Bar code scanner
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanResult == null) {
                    return;
                }
                String result = scanResult.getContents();
                if (result != null) {
                    searchView.setIconified(false);
                    searchView.setQuery(result, true);
                }
            }
            break;
        }

        switch (resultCode) {
            case ActivityResultCodes.DOCUMENT_ACTION_SUBMITTED:
                startSendActionsSyncService();
                // Refresh
                documentListNavigationManager.refreshCurrentView();
                break;
            case ActivityResultCodes.DOCUMENT_STAR_MARKED:
                // Refresh
                documentListNavigationManager.refreshCurrentView();
                break;
            case ActivityResultCodes.LOGOUT_REQUESTED:
                if (extras != null) {
                    if (extras.getInt(UserProfileActivity.EXTRA_LOGOUT_FLAG) == UserProfileActivity.FLAG_DID_LOGOUT) {
                        SessionManager.getApplicationInstance().logout();
                        returnToLoginActivity();
                    }
                }
                break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("active_user", activeUser);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceivers();
        SessionManager.getApplicationInstance().removeListener(sessionManagerEventListener);
    }

    // </editor-fold>

    //<editor-fold desc="Initialization Methods">
    private void initializeStateTransferredFields() {

        Bundle extras = getIntent().getExtras();
        RuntimeException noUserException = new RuntimeException("The current user must be passed when starting " + TAG);

        try {
            activeUser = (User) extras.getSerializable(EXTRA_ACTIVE_USER);
        } catch (Exception e) {
            noUserException.initCause(e);
            throw noUserException;
        }

        if (activeUser == null) {
            throw noUserException;
        }

    }

    private void initializeDependencies() {
        formsDAO = new FormsDAO(this);
        userDocumentsDAO = new UserDocumentsDAO(this);
        documentsDAO = new DocumentsDAO(this);
        outgoingActionsDAO = new OutgoingActionsDAO(this);

        outgoingActionsDataWriterFacade = new OutgoingActionsDataWriterFacade(this);

        searchDataProvider = SearchDataProviderFactory.createSearchDataProvider(this, activeUser);
    }

    private void initializeOtherFields() {

        currentViewFilter = new ViewFilter();
        searchDelayTimer = new Timer();

    }

    //</editor-fold>

    //<editor-fold desc="Service Methods">

    private void startSyncAllService() {

        Intent intent = new Intent(this, DataSynchronizationService.class);
        intent.putExtra(DataSynchronizationService.EXTRA_SYNC_TYPE, SyncType.FULL_SYNC);
        startService(intent);

        documentListViewFragment.startSwipeRefresh();
        if (documentListViewFragment.getViewItemCount() == 0) {
            documentListViewFragment.showCenterMessage("Synch in progress", false);
        }

//        PreferenceManager
//                .getDefaultSharedPreferences(DocumentListActivity.this)
//                .edit()
//                .putBoolean(PREF_FIRST_FULL_SYNC_EXECUTED, true)
//                .apply();

    }

    private void startSynchService() {
        Intent intent = new Intent(this, DataSynchronizationService.class);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(DocumentListActivity.this);
        VersionSettings versionSettings = FormalisticsApplication.versionSettings;

        // execute a partial synchronization if the version settings says so and the application already executed
        // its first full synchronization
        if (versionSettings.partiallySynchronize && sp.getBoolean(PREF_FIRST_FULL_SYNC_EXECUTED, false)) {
            intent.putExtra(DataSynchronizationService.EXTRA_SYNC_TYPE, SyncType.PARTIAL);
            intent.putExtra(DataSynchronizationService.EXTRA_PARTIAL_SYNC_FORM_IDS, Serializer.serializeList(versionSettings.formIdListToSynchronize));
        } else {
            intent.putExtra(DataSynchronizationService.EXTRA_SYNC_TYPE, SyncType.FULL_SYNC);
        }

        startService(intent);

        documentListViewFragment.startSwipeRefresh();
        if (documentListViewFragment.getViewItemCount() == 0) {
            documentListViewFragment.showCenterMessage("Synch in progress", false);
        }

        if (versionSettings.version != VersionSettings.AvailableVersion.DEFAULT) {
            sp.edit().putBoolean(PREF_FIRST_FULL_SYNC_EXECUTED, true).apply();
        }

    }

    private void startSendActionsSyncService() {

        Intent intent = new Intent(this, DataSynchronizationService.class);
        intent.putExtra(DataSynchronizationService.EXTRA_SYNC_TYPE, SyncType.SEND_ACTIONS);
        startService(intent);

        documentListViewFragment.startSwipeRefresh();

    }

    private void registerBroadcastReceivers() {

        IntentFilter fullUpdateIntentFilter = new IntentFilter();

        for (String action : Broadcaster.FULL_UPDATE_ACTIONS) {
            fullUpdateIntentFilter.addAction(action);
        }

        fullUpdateIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(dataSyncBroadcastReceiver, fullUpdateIntentFilter);

        IntentFilter submitOutgoingActionIntentFilter = new IntentFilter();
        submitOutgoingActionIntentFilter.addAction(Broadcaster.OUGTOING_ACTIONS_SUBMITTED);
        submitOutgoingActionIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(outgoingActionsBroadcastReceiver, submitOutgoingActionIntentFilter);

    }

    private void unregisterBroadcastReceivers() {
        unregisterReceiver(dataSyncBroadcastReceiver);
        unregisterReceiver(outgoingActionsBroadcastReceiver);
    }

    //</editor-fold>

    private void showActionsDialogForDocument(final DocumentSummary source) {

        String dialogHeader = "Actions";
        String dialogEmptyListMessage = "No are actions available for this document";

        ListSelectionDialogFragment dialog = ListSelectionDialogFragment.createInstance(dialogHeader, dialogEmptyListMessage);

        List<WorkflowAction> actions = source.getActions();

        List<String> actionStringList = new ArrayList<>();

        for (WorkflowAction action : actions) {
            // remove save
            if (!"Save".equalsIgnoreCase(action.getLabel())) {
                actionStringList.add(action.getLabel());
            }
        }

        dialog.setSelection(actionStringList);
        dialog.show(getFragmentManager(), ListSelectionDialogFragment.TAG);
        dialog.setOnItemSelectedCallback(new CallbackCommand<String>() {

            @Override
            public void execute(String result) {
                submitDocumentAction(source.getDocumentId(), result);
            }
        });

    }

    private void showConfirmResetDataDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Please confirm data reset")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Reset Database", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(DocumentListActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

    }

    //<editor-fold desc="Functional Methods">

    private void filterView(String filterString) {
        currentFilter = filterString;
        currentViewFilter.setGenericStringFilter(filterString);
//        onChangeViewContentsCommand(currentlySelectedNavigationDrawerItem, currentViewFilter);
        onDisplayDocumentSummaries(currentlySelectedNavigationDrawerItem, documentSearchTypes);
    }

    private void submitDocumentAction(int documentId, String action) {
        try {

            Document document;
            Form form;

            try {
                document = documentsDAO.getDocument(documentId);
                form = formsDAO.getForm(document.getFormId());
            } catch (DataAccessObject.DataAccessObjectException e) {
                e.printStackTrace();
                Toast.makeText(DocumentListActivity.this, "Failed to submit action, form not found.", Toast.LENGTH_LONG).show();
                return;
            }

            // throws JSONException
            JSONObject fieldValuesJSON = new JSONObject(document.getFieldValuesJSONString());

            OutgoingActionsDataWriterFacade outgoingActionDataWriter = new OutgoingActionsDataWriterFacade(this);
            outgoingActionDataWriter.saveDocumentAction(
                    document.getId(), form, fieldValuesJSON, action, activeUser
            );

            startSendActionsSyncService();

            Toast.makeText(DocumentListActivity.this,
                    "Action saved, your document will be updated after a while.", Toast.LENGTH_LONG)
                    .show();

            documentListNavigationManager.refreshCurrentView();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(DocumentListActivity.this,
                    "Failed to issue action to document, it's field values may be corrupted", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void onCreateFormCommand() {

        List<Form> forms = new ArrayList<>();

        try {
            forms = formsDAO.getCompanyForms(activeUser.getCompany().getId());
        } catch (DataAccessObject.DataAccessObjectException e) {
            e.printStackTrace();
            // TODO: add feature to reset the forms
            Toast.makeText(this, "Cannot read your forms, one or more of them may be corrupted.", Toast.LENGTH_LONG).show();
        }

        FormSelectionDialogFragment formSelectionDialogFragment = new FormSelectionDialogFragment();

        // Update the form filter selection
        if (forms.size() >= 1) {
            formSelectionDialogFragment.setFormsAvailability(FormSelectionDialogFragment.FormsAvailability.HAS_FORMS);
        } else {
            formSelectionDialogFragment.setFormsAvailability(FormSelectionDialogFragment.FormsAvailability.NO_FORMS_TO_SHOW);
        }
        formSelectionDialogFragment.setForms(forms);
//        }

        formSelectionDialogFragment.show(getFragmentManager(), FormSelectionDialogFragment.TAG);
        formSelectionDialogFragment.setOnFormSelectedCallback(new CallbackCommand<Form>() {

            @Override
            public void execute(Form selectedForm) {
                navigateToDocumentView(DocumentType.NEW_DOCUMENT, selectedForm.getId(), 0);
            }
        });

    }

    private void logout() {
        SessionManager.getApplicationInstance().logout();
        returnToLoginActivity();
    }

    //</editor-fold>

    // <editor-fold desc="Navigational Methods">

    private void returnToLoginActivity() {

        FLLogger.d(TAG, "returnToLoginActivity");

        Intent intent = new Intent(DocumentListActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    private void navigateToUserProfileActivity() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.EXTRA_ACTIVE_USER, activeUser);
        startActivity(intent);
    }

    private void navigateToDeveloperOptionsActivity() {
        Intent intent = new Intent(this, TesterActivity.class);
        startActivity(intent);
    }

    private void navigateToDocumentView(DocumentType documentType, int formId, int documentOrOutgoingActionId) {
        Intent documentActivity = new Intent(DocumentListActivity.this, DocumentActivity.class);

        documentActivity.putExtra(DocumentActivity.EXTRA_ACTIVE_USER, activeUser);
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_TYPE, documentType);
        documentActivity.putExtra(DocumentActivity.EXTRA_FORM_ID, formId);
        documentActivity.putExtra(DocumentActivity.EXTRA_DOCUMENT_OR_OUTGOING_ACTION_ID, documentOrOutgoingActionId);

        startActivityForResult(documentActivity, ActivityRequestCodes.OPEN_DOCUMENT);
    }

    private void navigateToCommentsView(int documentId) {

        try {
            Document document = documentsDAO.getDocument(documentId);
            Form form = formsDAO.getForm(document.getFormId());
            Intent intent = new Intent(DocumentListActivity.this, CommentsActivity.class);

            intent.putExtra(CommentsActivity.EXTRA_ACTIVE_USER, activeUser);
            intent.putExtra(CommentsActivity.EXTRA_DOCUMENT_ID, documentId);
            intent.putExtra(CommentsActivity.EXTRA_DOCUMENT_WEB_ID, document.getWebId());
            intent.putExtra(CommentsActivity.EXTRA_FORM_WEB_ID, form.getWebId());

            startActivity(intent);
        } catch (DataAccessObject.DataAccessObjectException e) {
            e.printStackTrace();
            Toast.makeText(this, "There was an error when trying to open comments. Please try again later.", Toast.LENGTH_LONG).show();
        }
    }

    // </editor-fold>

    // <editor-fold desc="View Fragments Implementation Methods">

    @Override
    public void onViewReady() {
        documentListNavigationManager.refreshCurrentView();

        if (SessionManager.getApplicationInstance().isCurrentlyCheckingServerConnection()) {
            documentListViewFragment.notifyConnectingToServer(activeUser.getCompany().getServer());
        } else if (SessionManager.getApplicationInstance().getSessionMode() == SessionMode.ONLINE) {
            documentListViewFragment.notifyOnlineMode();
        } else {
            documentListViewFragment.notifyOfflineMode();
        }

        if (!activityJustChangedOrientation) {
            startSynchService();
            documentListViewFragment.startSwipeRefresh();
        }
    }

    @Override
    public void onRefreshCommand() {
        startSynchService();
    }

    @Override
    public void onRetryConnectionCommand() {
        SessionManager sessionManager = SessionManager.getApplicationInstance();
        if (!sessionManager.isCurrentlyCheckingServerConnection()) {
            Toast.makeText(DocumentListActivity.this, "Retrying connection", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DocumentListActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
        }

        sessionManager.checkConnectionToServer(null,
                new CallbackCommand<SessionManager.CheckConnectionResult>() {

                    @Override
                    public void execute(SessionManager.CheckConnectionResult result) {
                        if (result.connectionStatus == HttpCommunicator.STATUS_CONNECTED) {
                            startSynchService();
                            documentListViewFragment.notifyOnlineMode();
                        } else {
                            documentListViewFragment.notifyOfflineMode();
                        }
                    }
                });
        documentListViewFragment.notifyConnectingToServer(activeUser.getCompany().getServer());
    }

    @Override
    public void onFilterByQRCodeCommand() {
        IntentIntegrator integrator = new IntentIntegrator(DocumentListActivity.this);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        integrator.initiateScan();
    }

    @Override
    public void onLoadMore(int startIndex, int fetchCount) {
        List<DocumentSummary> documentSummaries = searchDataProvider.searchDocumentSummaries(
                documentSearchTypes, currentFilter, startIndex, fetchCount
        );

        documentListViewFragment.addViewDocuments(documentSummaries);
    }

    @Override
    public void onOpenDocumentCommand(DocumentSummary documentSummary) {
        navigateToDocumentView(DocumentType.EXISTING_DOCUMENT, documentSummary.getFormId(), documentSummary.getDocumentId());
    }

    @Override
    public void onOpenOutgoingDocumentsComment(DisplayReadyAction displayReadyAction) {
        navigateToDocumentView(DocumentType.OUTGOING_DOCUMENT, displayReadyAction.getFormId(), displayReadyAction.getId());
    }

    // </editor-fold>

    // <editor-fold desc="DocumentListViewItem Implementation Methods">

    @Override
    public void onOpenDocumentActionsCommand(DocumentSummary source) {
        showActionsDialogForDocument(source);
    }

    @Override
    public void onToggleDocumentStarMarkCommand(DocumentSummary source) {

        if (source.getStarMarkInt() == StarMark.STARRED) {
            source.setStarMarkInt(StarMark.UNSTARRED);
        } else {
            source.setStarMarkInt(StarMark.STARRED);
        }

        userDocumentsDAO.changeDocumentStarMark(activeUser.getId(), source.getDocumentId(), source.getStarMarkInt());
        outgoingActionsDataWriterFacade.saveStarMark(
                source.getDocumentId(), source.getFormId(), source.getStarMarkInt(), activeUser
        );

    }

    @Override
    public void onOpenDocumentCommentsCommand(DocumentSummary source) {
        navigateToCommentsView(source.getDocumentId());
    }

    // </editor-fold>

    // <editor-fold desc="Event Listeners">

    private final BroadcastReceiver outgoingActionsBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (Broadcaster.OUGTOING_ACTIONS_SUBMITTED.equals(action)) {
                // TODO: refresh outgoing actions here
                startSynchService();
            }
        }

    };

    private final BroadcastReceiver dataSyncBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            FLLogger.d(TAG, "Received broadcasted action: " + action);

            if (Broadcaster.REAUTHENTICATION_REQUIRED.equals(action)) {
                returnToLoginActivity();
            }

            if (Broadcaster.FULL_UPDATE_DONE.equals(action)) {
                FLLogger.d(TAG, "Update done");
                documentListViewFragment.stopSwipeRefresh();
            }

            if (Broadcaster.FULL_UPDATE_NOT_STARTED.equals(action)) {
                String message = intent.getStringExtra(Broadcaster.EXTRA_MESSAGE);
                FLLogger.d(TAG, message);
                documentListViewFragment.stopSwipeRefresh();
            }

            if (Broadcaster.OUGTOING_ACTIONS_SUBMITTED.equals(action) ||
                    Broadcaster.DOCUMENTS_PARTIALLY_SYNCHRONIZED.equals(action)) {
                //   Refresh the view
                documentListNavigationManager.refreshCurrentView();
            }
        }

    };

    // }}

    private final SessionManagerEventListener sessionManagerEventListener = new SessionManagerEventListener() {

        @Override
        public void onSessionLost() {
            documentListViewFragment.notifyOfflineMode();
        }

        @Override
        public void onModeChanged(SessionMode mode) {
            if (mode == SessionMode.ONLINE) {
                documentListViewFragment.notifyOfflineMode();
            } else {
                documentListViewFragment.notifyOfflineMode();
                documentListViewFragment.stopSwipeRefresh();
            }
        }
    };

    // </editor-fold>

    //<editor-fold desc="DocumentListNavigationManagerEventsListener implementation methods">
    @Override
    public void onOpenUserProfileCommand() {
        navigateToUserProfileActivity();
    }

    @Override
    public void onLogoutCommand() {
        logout();
    }

    @Override
    public void onOpenDeveloperOptionsCommand() {
        navigateToDeveloperOptionsActivity();
    }


    @Override
    public void onDisplayDocumentSummaries(NavigationDrawerItem navigationDrawerItem, EnumSet<DocumentSearchType> documentSearchTypes) {
        this.documentSearchTypes = documentSearchTypes;
        List<DocumentSummary> documentSummaries = searchDataProvider.searchDocumentSummaries(
                documentSearchTypes, currentFilter, 0, SEARCH_ITEM_COUNT
        );

        documentListViewFragment.hideCenterMessage();
        
        if (navigationDrawerItem != null) {
            setTitle(navigationDrawerItem.getLabel());
            getActionBar().setIcon(navigationDrawerItem.getImageResourceId());
        }

        if (documentSummaries.size() == 0) {
            if (currentViewFilter.getGenericStringFilter() != null && !currentViewFilter.getGenericStringFilter().trim().isEmpty()) {
                documentListViewFragment.showCenterMessage("No documents found, try synchronizing by swiping the view down and searching again", false);
            } else {
                documentListViewFragment.showCenterMessage("No documents found, try synchronizing by swiping the view down", false);
            }
        }

        if (documentListViewFragment.getViewItemCount() == 0 && !documentListViewFragment.isCenterMessageShowing()) {
            documentListViewFragment.showCenterMessage("No documents to display", false);
        }

        documentListViewFragment.setViewDocuments(documentSummaries);

    }

    @Override
    public void onDisplayOutgoingActions(NavigationDrawerItem navigationDrawerItem, List<DisplayReadyAction> displayReadyActions) {
        documentListViewFragment.setOutgoingViewDocuments(displayReadyActions);

        if (navigationDrawerItem != null) {
            setTitle(navigationDrawerItem.getLabel());
            getActionBar().setIcon(navigationDrawerItem.getImageResourceId());
        }
    }

    @Override
    public void onFullSynchronizeCommand() {
        startSyncAllService();
    }

    //</editor-fold>

}
