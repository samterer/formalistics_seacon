package ph.com.gs3.formalistics.presenter.navigation;

import java.util.EnumSet;

import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;

/**
 * Created by Ervinne on 4/24/2015.
 */
public interface DocumentListNavigationPresenterEventsListener {

    void onOpenUserProfileCommand();

    void onLogoutCommand();

    void onOpenDeveloperOptionsCommand();

    void onDisplayDocumentSummaries(NavigationDrawerItem navigationDrawerItem, EnumSet<DocumentSearchType> documentSearchTypes);

    void onDisplayOutgoingActions(NavigationDrawerItem navigationDrawerItem);

    void onFullSynchronizeCommand();

//    void onChangeViewDocumentsCommand(NavigationDrawerItem navigationDrawerItem, List<DocumentSummary> documentSummaryList);
//
//    void onChangeViewOutgoingDocumentsCommand(NavigationDrawerItem navigationDrawerItem, List<DisplayReadyAction> documentsForDisplayList);

}
