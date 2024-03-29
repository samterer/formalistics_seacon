package ph.com.gs3.formalistics.presenter.navigation;

import android.content.Context;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.model.dao.facade.search.SeaconSearchDataProvider;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/24/2015.
 */
public class DocumentListNavigationPresenterFactory {

    public static DocumentListNavigationPresenter createNew(
            Context context,
            User activeUser,
            DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener) {

        VersionSettings versionSettings = FormalisticsApplication.versionSettings;

        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON && (
                activeUser.getPositionId() == SeaconSearchDataProvider.GATER_POSITION_ID ||
                        activeUser.getPositionId() == SeaconSearchDataProvider.INSPECTOR_POSITION_ID ||
                        activeUser.getPositionId() == SeaconSearchDataProvider.CRANE_OPERATOR_ID)) {
            return new SeaconDocumentListNavigationPresenter(
                    activeUser, documentListNavigationPresenterEventsListener
            );
        } else {
            // Default navigation
            return new DefaultDocumentListNavigationPresenter(documentListNavigationPresenterEventsListener);
        }

    }

}
