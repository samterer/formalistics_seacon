package ph.com.gs3.formalistics.presenter.navigation;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;

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

    private Context context;
    private User activeUser;
    private DocumentListNavigationPresenterEventsListener documentListNavigationPresenterEventsListener;

    private FormsDAO formsDAO;

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

        if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.navigateToUserViewCommandNavItem && currentlySelectedNavigationDrawerItemPosition != position) {
            documentListNavigationPresenterEventsListener.onOpenUserProfileCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.logoutNavCommandItem && currentlySelectedNavigationDrawerItemPosition != position) {
            documentListNavigationPresenterEventsListener.onLogoutCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.openDeveloperOptionsCommandNavItem && currentlySelectedNavigationDrawerItemPosition != position) {
            documentListNavigationPresenterEventsListener.onOpenDeveloperOptionsCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.resetDataCommandNavItem) {
            documentListNavigationPresenterEventsListener.onResetDataCommand();
        } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.fullSynchronizeCommandNavItem) {
            documentListNavigationPresenterEventsListener.onFullSynchronizeCommand();
        } else {
            processChangeDocumentList(navigationDrawerItem);
//            try {
//                List<Form> formList = new ArrayList<>();
//
//                Form EIRForm = null;
//
//                if (activeUser.getPositionId() == GATER_POSITION_ID || activeUser.getPositionId() == CRANE_OPERATOR_ID) {
//                    EIRForm = formsDAO.getForm(EIR_FORM_WEB_ID, activeUser.getCompany().getId());
//                    if (EIRForm != null) {
//                        formList.add(EIRForm);
//                    }
//
//                } else if (activeUser.getPositionId() == INSPECTOR_POSITION_ID) {
//                    Form jobOrderForm = formsDAO.getForm(JOB_ORDER_FORM_WEB_ID, activeUser.getCompany().getId());
//                    if (jobOrderForm != null) {
//                        formList.add(jobOrderForm);
//                    }
//                }
//
//                List<SearchCondition> searchConditions = new ArrayList<>();
//
//                ViewFilter viewFilter = new ViewFilter();
//                viewFilter.setForms(formList);
//
//                if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.openOutboxNavItem) {
//                    viewFilter.setViewContentType(ViewFilter.ViewContentType.OUTGOING_ACTIONS);
//                } else {
//
//                    if (navigationDrawerItem == viewIncomingEIRDocuments || navigationDrawerItem == viewReturnEIRDocuments || navigationDrawerItem == viewOutgoingEIRDocuments) {
//
//                        if (EIRForm != null) {
//                            if (navigationDrawerItem == viewIncomingEIRDocuments) {
//                                searchConditions.add(new SearchCondition(EIRForm.getGeneratedFormTableName() + ".ContainerStatus", "=", "Incoming"));
//                            } else if (navigationDrawerItem == viewReturnEIRDocuments) {
//                                searchConditions.add(new SearchCondition("ContainerStatus", "=", "Return"));
//                            } else if (navigationDrawerItem == viewOutgoingEIRDocuments) {
//                                searchConditions.add(new SearchCondition("ContainerStatus", "=", "Outgoing"));
//                            }
//
//                            // All documents with outgoing actions will not be displayed anymore
//                            searchConditions.add(new SearchCondition("oa._id", "=", null));
//
//                        } else {
//                            Toast.makeText(context, "EIR Form not found, try doing a full synchronize first", Toast.LENGTH_LONG).show();
//                        }
//
//                        String manualConditions = "((wo.processor_type = 2 AND wo.processor = " + activeUser.getPositionId() + ") " +
//                                "OR (wo.processor_type = 3 AND wo.processor = " + activeUser.getWebId() + ") " +
//                                "OR (wo.processor_type = 4 AND d.author_id = " + activeUser.getWebId() + ")) ";
//
//                        if (activeUser.getPositionId() == CRANE_OPERATOR_ID) {
//
//                            Form setupEquipmentForCraneOperators = formsDAO.getForm(SETUP_EQUIPMENT_FORM_CRANE_OPERATORS_FORM_WEB_ID, activeUser.getCompany().getId());
//                            Form containerInformation = formsDAO.getForm(CONTAINER_INFORMATION_FORM_WEB_ID, activeUser.getCompany().getId());
//
//                            if (setupEquipmentForCraneOperators != null && containerInformation != null) {
//
//                                String SEFCOTableName = setupEquipmentForCraneOperators.getGeneratedFormTableName();
//                                String CITableName = containerInformation.getGeneratedFormTableName();
//                                String EIRTableName = EIRForm.getGeneratedFormTableName();
//
//                                String manualJoins = " LEFT JOIN " + SEFCOTableName + " ON " + CITableName + ".EquipmentUsed = " + SEFCOTableName + ".txt_EqName " +
//                                        " LEFT JOIN " + CITableName + " ON " + CITableName + ".TS = " + EIRTableName + ".TS ";
//
//                                manualConditions += " AND " + SEFCOTableName + ".EmpName = '" + activeUser.getDisplayName() + "'";
//
//                                viewFilter.setManualJoins(manualJoins);
//
//                            } else {
//                                Toast.makeText(
//                                        context,
//                                        "Failed to open view, Setup Equipment For Crane Operators and Container Information forms are not found.",
//                                        Toast.LENGTH_LONG)
//                                        .show();
//                                return;
//                            }
//
//                        }
//
//
//                        viewFilter.setManualConditions(manualConditions);
//
//                        searchConditions.add(new SearchCondition("wo.actions", "!=", "[]"));
//                        viewFilter.setViewContentType(ViewFilter.ViewContentType.EXISTING_DOCUMENTS);
//                    } else if (navigationDrawerItem == DefaultDocumentListNavigationPresenter.openStarredNavItem) {
//                        searchConditions.add(new SearchCondition("ud.is_starred", "=", "1"));
//                    }
//
//                }
//
//                viewFilter.setSearchConditionList(searchConditions);
//
//                documentListNavigationPresenterEventsListener.onChangeViewContentsCommand(navigationDrawerItem, viewFilter);
//            } catch (DataAccessObject.DataAccessObjectException e) {
//                e.printStackTrace();
//            }
        }

        currentlySelectedNavigationDrawerItemPosition = position;
    }

    private void processChangeDocumentList(NavigationDrawerItem selectedNavigationDrawerItem) {

        List<Form> formList = new ArrayList<>();

        Form EIRForm = null;

        try {
            if (activeUser.getPositionId() == GATER_POSITION_ID || activeUser.getPositionId() == CRANE_OPERATOR_ID) {
                // throws DataAccessObjectException
                EIRForm = formsDAO.getForm(EIR_FORM_WEB_ID, activeUser.getCompany().getId());
                if (EIRForm != null) {
                    formList.add(EIRForm);
                }

            } else if (activeUser.getPositionId() == INSPECTOR_POSITION_ID) {
                // throws DataAccessObjectException
                Form jobOrderForm = formsDAO.getForm(JOB_ORDER_FORM_WEB_ID, activeUser.getCompany().getId());
                if (jobOrderForm != null) {
                    formList.add(jobOrderForm);
                }
            }
        } catch (DataAccessObject.DataAccessObjectException e) {
            e.printStackTrace();
        }

    }

}
