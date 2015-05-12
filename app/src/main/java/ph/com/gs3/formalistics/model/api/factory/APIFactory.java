package ph.com.gs3.formalistics.model.api.factory;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.model.api.CommentsAPI;
import ph.com.gs3.formalistics.model.api.DocumentsAPI;
import ph.com.gs3.formalistics.model.api.FormsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.UsersAPI;
import ph.com.gs3.formalistics.model.api.default_impl.CommentsAPIDefaultImpl;
import ph.com.gs3.formalistics.model.api.default_impl.DocumentsAPIDefaultImpl;
import ph.com.gs3.formalistics.model.api.default_impl.FormsAPIDefaultImpl;
import ph.com.gs3.formalistics.model.api.default_impl.UsersAPIDefaultImpl;
import ph.com.gs3.formalistics.model.api.seacon_impl.DocumentsAPISeaconImpl;
import ph.com.gs3.formalistics.model.values.application.VersionSettings;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.presenter.navigation.SeaconDocumentListNavigationPresenter;

/**
 * Created by Ervinne on 4/7/2015.
 */
public class APIFactory {

    public HttpCommunicator getDefaultHttpCommunicator() {
        return new HttpCommunicator();
    }

    public UsersAPI createUsersAPI(String server) {
        // UsersAPIDefaultImpl is currently used in any version
        return new UsersAPIDefaultImpl(getDefaultHttpCommunicator(), server);
    }

    public FormsAPI createFormsAPI(String server) {
        return new FormsAPIDefaultImpl(getDefaultHttpCommunicator(), server);
    }

    public DocumentsAPI createDocumentsAPI(User activeUser) {

        VersionSettings versionSettings = FormalisticsApplication.versionSettings;

        if (versionSettings.version == VersionSettings.AvailableVersion.SEACON && (
                activeUser.getPositionId() == SeaconDocumentListNavigationPresenter.GATER_POSITION_ID ||
                        activeUser.getPositionId() == SeaconDocumentListNavigationPresenter.INSPECTOR_POSITION_ID ||
                        activeUser.getPositionId() == SeaconDocumentListNavigationPresenter.CRANE_OPERATOR_ID
        )
                ) {
            return new DocumentsAPISeaconImpl(getDefaultHttpCommunicator(), activeUser);
        } else {
            return new DocumentsAPIDefaultImpl(getDefaultHttpCommunicator(), activeUser.getCompany().getServer());
        }
    }

    public CommentsAPI createCommentsAPI(String server) {
        // CommentsAPIDefaultImpl is currently used in any version
        return new CommentsAPIDefaultImpl(getDefaultHttpCommunicator(), server);
    }

}
