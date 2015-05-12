package ph.com.gs3.formalistics.presenter.navigation;

import android.content.Context;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;
import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
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

    public static final int GATER_POSITION_ID = 4;
    public static final int INSPECTOR_POSITION_ID = 5;
    public static final int CRANE_OPERATOR_ID = 7;

    public static final int CONTAINER_INFORMATION_FORM_WEB_ID = 1;
    public static final int EIR_FORM_WEB_ID = 2;
    public static final int JOB_ORDER_FORM_WEB_ID = 12;
    public static final int SETUP_EQUIPMENT_FORM_CRANE_OPERATORS_FORM_WEB_ID = 46;

    private final Context context;
    private final User activeUser;
    private final DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener;

    private final FormsDAO formsDAO;

    private int currentlySelectedNavigationDrawerItemPosition;

    public SeaconDocumentListNavigationPresenter(
            Context context,
            User activeUser,
            DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener) {
        this.context = context;
        this.activeUser = activeUser;
        this.documentListNavigationPresenterEventsListener = documentListNavigationPresenterEventsListener;

        this.formsDAO = new FormsDAO(context);

    }

    @Override
    public void refreshCurrentView() {
        onNavigationDrawerItemSelected(currentlySelectedNavigationDrawerItemPosition);
    }

    @Override
    public List<NavigationDrawerItem> getNavigationDrawerItems() {
        List<NavigationDrawerItem> navItems = new ArrayList<>();

        if (activeUser.getPositionId() == GATER_POSITION_ID || activeUser.getPositionId() == CRANE_OPERATOR_ID) {
            navItems.add(viewIncomingEIRDocuments);
            navItems.add(viewReturnEIRDocuments);
            navItems.add(viewOutgoingEIRDocuments);
        }

        if (activeUser.getPositionId() == INSPECTOR_POSITION_ID) {
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
