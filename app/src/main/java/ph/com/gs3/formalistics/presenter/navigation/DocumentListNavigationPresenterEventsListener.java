package ph.com.gs3.formalistics.presenter.navigation;

import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;

/**
 * Created by Ervinne on 4/24/2015.
 */
public interface DocumentListNavigationPresenterEventsListener {

    void onOpenUserProfileCommand();

    void onLogoutCommand();

    void onOpenDeveloperOptionsCommand();

    void onDisplayDocumentSummaries(NavigationDrawerItem navigationDrawerItem, EnumSet<DocumentSearchType> documentSearchTypes);

    void onDisplayOutgoingActions(NavigationDrawerItem navigationDrawerItem, List<DisplayReadyAction> displayReadyActions);

    void onFullSynchronizeCommand();

//    void onChangeViewDocumentsCommand(NavigationDrawerItem navigationDrawerItem, List<DocumentSummary> documentSummaryList);
//
//    void onChangeViewOutgoingDocumentsCommand(NavigationDrawerItem navigationDrawerItem, List<DisplayReadyAction> documentsForDisplayList);

}
