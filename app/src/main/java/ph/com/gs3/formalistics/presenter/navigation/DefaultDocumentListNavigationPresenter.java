package ph.com.gs3.formalistics.presenter.navigation;

import android.content.Context;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;
import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.OutgoingActionsDAO;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;

/**
 * Created by Ervinne on 4/24/2015.
 */
public class DefaultDocumentListNavigationPresenter implements DocumentListNavigationPresenter {

    public static final NavigationDrawerItem openInboxNavItem = new NavigationDrawerItem(1, R.drawable.inbox, "Inbox");
    public static final NavigationDrawerItem openOutboxNavItem = new NavigationDrawerItem(2, R.drawable.outbox, "Outbox");
    public static final NavigationDrawerItem openProcessedNavItem = new NavigationDrawerItem(3, R.drawable.processed, "Processed");
    public static final NavigationDrawerItem openStarredNavItem = new NavigationDrawerItem(4, R.drawable.starred, "Starred");

    public static final NavigationDrawerItem openDeveloperOptionsCommandNavItem = new NavigationDrawerItem(6, R.drawable.ic_launcher, "Developer Options");

    public static final NavigationDrawerItem resetDataCommandNavItem = new NavigationDrawerItem(7, R.drawable.processed, "Reset Data");

    public static final NavigationDrawerItem fullSynchronizeCommandNavItem = new NavigationDrawerItem(8, R.drawable.processed, "Full Synchronize");
    public static final NavigationDrawerItem navigateToUserViewCommandNavItem = new NavigationDrawerItem(9, R.drawable.user, "User");
    public static final NavigationDrawerItem logoutNavCommandItem = new NavigationDrawerItem(10, R.drawable.logout, "Logout");

    private Context context;

    private User activeUser;
    private DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener;

    private FormsDAO formsDAO;
    private DocumentsDAO documentsDAO;
    private OutgoingActionsDAO outgoingActionsDAO;

    private int currentlySelectedNavigationDrawerPosition;

    public DefaultDocumentListNavigationPresenter(
            Context context,
            User activeUser,
            DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener) {
        this.context = context;

        this.activeUser = activeUser;
        this.documentListNavigationPresenterEventsListener = documentListNavigationPresenterEventsListener;

        this.formsDAO = new FormsDAO(context);
        this.documentsDAO = new DocumentsDAO(context);
        this.outgoingActionsDAO = new OutgoingActionsDAO(context);

        this.currentlySelectedNavigationDrawerPosition = 0;

    }

    @Override
    public List<NavigationDrawerItem> getNavigationDrawerItems() {
        List<NavigationDrawerItem> navItems = new ArrayList<>();

        navItems.add(openInboxNavItem);
        navItems.add(openOutboxNavItem);
//        navItems.add(openProcessedNavItem);
        navItems.add(openStarredNavItem);
        if (FormalisticsApplication.APPLICATION_MODE == ApplicationMode.DEVELOPMENT) {
            navItems.add(openDeveloperOptionsCommandNavItem);
            navItems.add(resetDataCommandNavItem);
        }

        if (FormalisticsApplication.versionSettings.partiallySynchronize) {
            navItems.add(DefaultDocumentListNavigationPresenter.fullSynchronizeCommandNavItem);
        }

//        navItems.add(new NavigationDrawerItem(5, R.drawable.archive, "Archive"));
        navItems.add(navigateToUserViewCommandNavItem);
        navItems.add(logoutNavCommandItem);

        return navItems;
    }

    @Override
    public List<DocumentSummary> getDisplayableDocumentSummaries() {
        return null;
    }

    @Override
    public List<DisplayReadyAction> getDisplayableOutgoingActions() {
        return null;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        NavigationDrawerItem navigationDrawerItem = getNavigationDrawerItems().get(position);

        if (navigationDrawerItem == navigateToUserViewCommandNavItem && currentlySelectedNavigationDrawerPosition != position) {
            documentListNavigationPresenterEventsListener.onOpenUserProfileCommand();
        } else if (navigationDrawerItem == logoutNavCommandItem && currentlySelectedNavigationDrawerPosition != position) {
            documentListNavigationPresenterEventsListener.onLogoutCommand();
        } else if (navigationDrawerItem == openDeveloperOptionsCommandNavItem && currentlySelectedNavigationDrawerPosition != position) {
            documentListNavigationPresenterEventsListener.onOpenDeveloperOptionsCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.resetDataCommandNavItem) {
//            documentListNavigationPresenterEventsListener.onResetDataCommand();
        } else if (navigationDrawerItem == fullSynchronizeCommandNavItem) {
            documentListNavigationPresenterEventsListener.onFullSynchronizeCommand();
        } else if (navigationDrawerItem == openInboxNavItem) {
            documentListNavigationPresenterEventsListener.onDisplayDocumentSummaries(
                    navigationDrawerItem, EnumSet.of(DocumentSearchType.DEFAULT_INBOX)
            );
        } else if (navigationDrawerItem == openStarredNavItem) {
            documentListNavigationPresenterEventsListener.onDisplayDocumentSummaries(
                    navigationDrawerItem, EnumSet.of(DocumentSearchType.DEFAULT_INBOX, DocumentSearchType.DEFAULT_STARRED)
            );
        } else if (navigationDrawerItem == openOutboxNavItem) {
            List<DisplayReadyAction> displayReadyActions = outgoingActionsDAO.getAllDisplayReadyOutgoingActions(activeUser.getId());
            documentListNavigationPresenterEventsListener.onDisplayOutgoingActions(navigationDrawerItem, displayReadyActions);
        }

        currentlySelectedNavigationDrawerPosition = position;

    }

    @Override
    public void refreshCurrentView() {
        onNavigationDrawerItemSelected(currentlySelectedNavigationDrawerPosition);
    }
}
