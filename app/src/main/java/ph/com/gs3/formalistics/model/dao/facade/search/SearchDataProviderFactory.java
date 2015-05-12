package ph.com.gs3.formalistics.model.dao.facade.search;

import android.content.Context;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.presenter.navigation.SeaconDocumentListNavigationPresenter;

/**
 * Created by Ervinne on 5/11/2015.
 */
public class SearchDataProviderFactory {

    public static SearchDataProvider createSearchDataProvider(Context context, User activeUser) {

        VersionSettings versionSettings = FormalisticsApplication.versionSettings;

        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON && (
                activeUser.getPositionId() == SeaconDocumentListNavigationPresenter.GATER_POSITION_ID ||
                        activeUser.getPositionId() == SeaconDocumentListNavigationPresenter.INSPECTOR_POSITION_ID ||
                        activeUser.getPositionId() == SeaconDocumentListNavigationPresenter.CRANE_OPERATOR_ID)) {
            return new SeaconSearchDataProvider(context, activeUser);
        } else {
            return new DefaultSearchDataProvider(context, activeUser);
        }

    }

}
