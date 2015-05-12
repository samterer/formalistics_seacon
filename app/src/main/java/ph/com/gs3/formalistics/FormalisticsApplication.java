package ph.com.gs3.formalistics;

import android.app.Application;

import ph.com.gs3.formalistics.global.constants.ApplicationMode;
import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;
import ph.com.gs3.formalistics.presenter.navigation.SeaconDocumentListNavigationPresenter;
import ph.com.gs3.formalistics.service.managers.DataSynchronizationManager;
import ph.com.gs3.formalistics.service.managers.SessionManager;
import ph.com.gs3.formalistics.service.synchronizers.DocumentsSynchronizer;
import ph.com.gs3.formalistics.service.synchronizers.FilesSynchronizer;
import ph.com.gs3.formalistics.service.synchronizers.OutgoingActionsSynchronizer;

public class FormalisticsApplication extends Application {

    //  Default Version
    public static String FORMALISTICS_API_VERSION = "4.0.01";

    /**
     * Set the application mode here, some behavior of the application will be changed
     * depending on the application mode. These behaviors include logging and availability
     * of developer options.
     */
    public static final ApplicationMode APPLICATION_MODE = ApplicationMode.DEVELOPMENT;
    // public static final ApplicationMode APPLICATION_MODE = ApplicationMode.QA;
//    public static final ApplicationMode APPLICATION_MODE = ApplicationMode.PRODUCTION;

    public static VersionSettings versionSettings = new VersionSettings(VersionSettings.AvailableVersion.SEACON);
//    public static VersionSettings versionSettings = new VersionSettings(VersionSettings.AvailableVersion.DEFAULT);

    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager sessionManager = SessionManager.createApplicationInstance(getApplicationContext());
        sessionManager.enableDebugLogging(true);
        sessionManager.startListeningToNetworkChanges();

        DataSynchronizationManager dataSynchronizationManager = DataSynchronizationManager.createApplicationInstance(getApplicationContext());
        dataSynchronizationManager.LOGGING_TYPE = LoggingType.ENABLED;

//        FormsSynchronizer.LOGGING_TYPE = LoggingType.ENABLED;
        OutgoingActionsSynchronizer.LOGGING_TYPE = LoggingType.ENABLED;
        DocumentsSynchronizer.LOGGING_TYPE = LoggingType.ENABLED;
//        CommentsSycnhronizer.LOGGING_TYPE = LoggingType.ENABLED;
        FilesSynchronizer.LOGGING_TYPE = LoggingType.ENABLED;

        initializeVersion();

    }

    private void initializeVersion() {

        // Custom version settings
        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON) {
            versionSettings.enableDocumentCreationGlobally = false;
            versionSettings.showFilterByQRCodeButton = true;

            versionSettings.partiallySynchronize = true;
            versionSettings.formIdListToSynchronize.add(SeaconDocumentListNavigationPresenter.EIR_FORM_WEB_ID);
            versionSettings.formIdListToSynchronize.add(SeaconDocumentListNavigationPresenter.CONTAINER_INFORMATION_FORM_WEB_ID);
            versionSettings.formIdListToSynchronize.add(SeaconDocumentListNavigationPresenter.JOB_ORDER_FORM_WEB_ID);
        } else {
            // Default
            versionSettings.enableDocumentCreationGlobally = true;
            versionSettings.showFilterByQRCodeButton = false;
            versionSettings.partiallySynchronize = false;
        }

    }

}
