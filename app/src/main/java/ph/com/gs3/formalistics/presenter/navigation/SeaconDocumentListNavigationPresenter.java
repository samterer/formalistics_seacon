package ph.com.gs3.formalistics.presenter.navigation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;
import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.dao.facade.search.SeaconSearchDataProvider;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/24/2015.
 */
public class SeaconDocumentListNavigationPresenter implements DocumentListNavigationPresenter {

    public static final String TAG = SeaconDocumentListNavigationPresenter.class.getSimpleName();

    public static final NavigationDrawerItem viewIncomingEIRDocuments = new NavigationDrawerItem(101, R.drawable.inbox, "Incoming EIR");
    public static final NavigationDrawerItem viewReturnEIRDocuments = new NavigationDrawerItem(102, R.drawable.inbox, "Returned EIR");
    public static final NavigationDrawerItem viewOutgoingEIRDocuments = new NavigationDrawerItem(103, R.drawable.inbox, "Outgoing EIR");

    public static final NavigationDrawerItem viewJobOrderDocuments = new NavigationDrawerItem(104, R.drawable.inbox, "Job Orders");

    private final User activeUser;
    private final DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener;

    private int currentlySelectedNavigationDrawerItemPosition;

    public SeaconDocumentListNavigationPresenter(
            User activeUser,
            DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener) {
        this.activeUser = activeUser;
        this.documentListNavigationPresenterEventsListener = documentListNavigationPresenterEventsListener;
    }

    @Override
    public void refreshCurrentView() {
        onNavigationDrawerItemSelected(currentlySelectedNavigationDrawerItemPosition);
    }

    @Override
    public List<NavigationDrawerItem> getNavigationDrawerItems() {
        List<NavigationDrawerItem> navItems = new ArrayList<>();

        if (activeUser.getPositionId() == SeaconSearchDataProvider.GATER_POSITION_ID ||
                activeUser.getPositionId() == SeaconSearchDataProvider.CRANE_OPERATOR_ID) {
            navItems.add(viewIncomingEIRDocuments);
            navItems.add(viewReturnEIRDocuments);
            navItems.add(viewOutgoingEIRDocuments);
        }

        if (activeUser.getPositionId() == SeaconSearchDataProvider.INSPECTOR_POSITION_ID) {
            navItems.add(viewJobOrderDocuments);
        }

        navItems.add(DefaultDocumentListNavigationPresenter.openOutboxNavItem);

        if (FormalisticsApplication.versionSettings.partiallySynchronize) {
            navItems.add(DefaultDocumentListNavigationPresenter.fullSynchronizeCommandNavItem);
        }

        if (FormalisticsApplication.APPLICATION_MODE == ApplicationMode.DEVELOPMENT) {
            navItems.add(DefaultDocumentListNavigationPresenter.openDeveloperOptionsCommandNavItem);
        }

//        navItems.add(DefaultDocumentListNavigationPresenter.resetDataCommandNavItem);
//        navItems.add(new NavigationDrawerItem(5, R.drawable.archive, "Archive"));
        navItems.add(DefaultDocumentListNavigationPresenter.navigateToUserViewCommandNavItem);
        navItems.add(DefaultDocumentListNavigationPresenter.logoutNavCommandItem);

        return navItems;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        NavigationDrawerItem navigationDrawerItem = getNavigationDrawerItems().get(position);

        if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.navigateToUserViewCommandNavItem && currentlySelectedNavigationDrawerItemPosition != position) {
            documentListNavigationPresenterEventsListener.onOpenUserProfileCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.logoutNavCommandItem && currentlySelectedNavigationDrawerItemPosition != position) {
            documentListNavigationPresenterEventsListener.onLogoutCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.openDeveloperOptionsCommandNavItem && currentlySelectedNavigationDrawerItemPosition != position) {
            documentListNavigationPresenterEventsListener.onOpenDeveloperOptionsCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.resetDataCommandNavItem) {
//            documentListNavigationPresenterEventsListener.onResetDataCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.fullSynchronizeCommandNavItem) {
            documentListNavigationPresenterEventsListener.onFullSynchronizeCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.openOutboxNavItem) {
            documentListNavigationPresenterEventsListener.onDisplayOutgoingActions(navigationDrawerItem);
        } else if (navigationDrawerItem == viewIncomingEIRDocuments) {
            documentListNavigationPresenterEventsListener.onDisplayDocumentSummaries(navigationDrawerItem, EnumSet.of(DocumentSearchType.SEACON_EIR_INCOMING));
        } else if (navigationDrawerItem == viewReturnEIRDocuments) {
            documentListNavigationPresenterEventsListener.onDisplayDocumentSummaries(navigationDrawerItem, EnumSet.of(DocumentSearchType.SEACON_EIR_RETURN));
        } else if (navigationDrawerItem == viewOutgoingEIRDocuments) {
            documentListNavigationPresenterEventsListener.onDisplayDocumentSummaries(navigationDrawerItem, EnumSet.of(DocumentSearchType.SEACON_EIR_OUTGOING));
        } else if (navigationDrawerItem == viewJobOrderDocuments) {
            documentListNavigationPresenterEventsListener.onDisplayDocumentSummaries(navigationDrawerItem, EnumSet.of(DocumentSearchType.SEACON_JOB_ORDERS));
        }

        currentlySelectedNavigationDrawerItemPosition = position;
    }

}
