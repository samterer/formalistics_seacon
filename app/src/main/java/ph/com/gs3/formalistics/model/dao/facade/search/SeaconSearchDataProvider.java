package ph.com.gs3.formalistics.model.dao.facade.search;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.DocumentSearchType;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.model.values.business.document.SubmitReadyAction;
import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 5/11/2015.
 */
public class SeaconSearchDataProvider implements SearchDataProvider {

    public static final String TAG = SeaconSearchDataProvider.class.getSimpleName();

    public static final int GATER_POSITION_ID = 4;
    public static final int INSPECTOR_POSITION_ID = 5;
    public static final int CRANE_OPERATOR_ID = 7;

    public static final int CONTAINER_INFORMATION_FORM_WEB_ID = 1;
    public static final int EIR_FORM_WEB_ID = 2;
    public static final int JOB_ORDER_FORM_WEB_ID = 12;
    public static final int SETUP_EQUIPMENT_FORM_CRANE_OPERATORS_FORM_WEB_ID = 46;
    public static final int APPROVED_FOR_REPAIR_CONTAINERS_WEB_ID = 59;
    public static final int VIOLATION_TICKET_FORM_WEB_ID = 35;
    public static final int CONTAINER_RESERVATION_FORM_WEB_ID = 71;

    private Form EIRForm;
    private Form jobOrderForm;
    private Form approvedForRepairContainerForm;
    private Form violationTicketForm;
    private Form jobOrderFromContainerReservationForm;

    private final FormsDAO formsDAO;
    private final DocumentsDAO documentsDAO;

    private final Context context;
    private final User activeUser;

    public SeaconSearchDataProvider(Context context, User activeUser) {
        formsDAO = new FormsDAO(context);
        documentsDAO = new DocumentsDAO(context);

        this.context = context;
        this.activeUser = activeUser;
    }

    @Override
    public List<DocumentSummary> searchDocumentSummaries(EnumSet<DocumentSearchType> searchTypeSet, String searchFilter, int fromIndex, int fetchCount) {

        FLLogger.d(TAG, "search type set: " + searchTypeSet);

        try {
            List<Form> formList = new ArrayList<>();
            lazyLoadForms();

            FLLogger.d(TAG, searchTypeSet.toString());

            if (activeUser.getPositionId() == GATER_POSITION_ID ||
                    activeUser.getPositionId() == CRANE_OPERATOR_ID ||
                    activeUser.getPositionId() == INSPECTOR_POSITION_ID) {

                if (searchTypeSet.contains(DocumentSearchType.SEACON_EIR_INCOMING) || searchTypeSet.contains(DocumentSearchType.SEACON_EIR_OUTGOING) || searchTypeSet.contains(DocumentSearchType.SEACON_EIR_RETURN)) {
                    if (EIRForm != null) {
                        formList.add(EIRForm);
                    }
                } else if (searchTypeSet.contains(DocumentSearchType.SEACON_APPROVED_FOR_REPAIR_CONTAINERS)) {
                    if (approvedForRepairContainerForm != null) {
                        formList.add(approvedForRepairContainerForm);
                    }
                } else if (searchTypeSet.contains(DocumentSearchType.SEACON_JOB_ORDERS)) {
                    if (jobOrderForm != null) {
                        formList.add(jobOrderForm);
                    }
                } else if (searchTypeSet.contains(DocumentSearchType.SEACON_VIOLATION_TICKET)) {
                    if (violationTicketForm != null) {
                        formList.add(violationTicketForm);
                    }
                } else if (searchTypeSet.contains(DocumentSearchType.SEACON_PENDING_JOB_ORDERS)) {
                    if (jobOrderFromContainerReservationForm != null) {
                        formList.add(jobOrderFromContainerReservationForm);
                    }
                }

            }

            List<SearchCondition> searchConditions = new ArrayList<>();

            if (EIRForm != null) {
                if (searchTypeSet.contains(DocumentSearchType.SEACON_EIR_INCOMING)) {
                    searchConditions.add(new SearchCondition(EIRForm.getGeneratedFormTableName() + ".ContainerStatus", "=", "Incoming"));
                } else if (searchTypeSet.contains(DocumentSearchType.SEACON_EIR_RETURN)) {
                    searchConditions.add(new SearchCondition("ContainerStatus", "=", "Return"));
                } else if (searchTypeSet.contains(DocumentSearchType.SEACON_EIR_OUTGOING)) {
                    searchConditions.add(new SearchCondition("ContainerStatus", "=", "Outgoing"));
                }
            } else {
                // TODO: throw exception about missing eir form
            }

            String manualJoins = "";
            String manualConditions = "";

            // all forms will display for approval requests only except seacon violation ticket and approved for repair containers
//            if (!searchTypeSet.contains(DocumentSearchType.SEACON_VIOLATION_TICKET) && !searchTypeSet.contains(DocumentSearchType.SEACON_APPROVED_FOR_REPAIR_CONTAINERS)) {
            if (!searchTypeSet.contains(DocumentSearchType.SEACON_VIOLATION_TICKET)) {
                manualConditions = DocumentsDAO.getForApprovalWhereClause(activeUser);
//                searchConditions.add(new SearchCondition("status", "=", "For Review"));
//                searchConditions.add(new SearchCondition("status", "=", "Updated"));
            }

            if (activeUser.getPositionId() == CRANE_OPERATOR_ID
                    && !searchTypeSet.contains(DocumentSearchType.SEACON_APPROVED_FOR_REPAIR_CONTAINERS)
                    && !searchTypeSet.contains(DocumentSearchType.SEACON_PENDING_JOB_ORDERS)) {

                Form setupEquipmentForCraneOperators = formsDAO.getForm(SETUP_EQUIPMENT_FORM_CRANE_OPERATORS_FORM_WEB_ID, activeUser.getCompany().getId());
                Form containerInformation = formsDAO.getForm(CONTAINER_INFORMATION_FORM_WEB_ID, activeUser.getCompany().getId());

                if (setupEquipmentForCraneOperators != null && containerInformation != null) {

                    String SEFCOTableName = setupEquipmentForCraneOperators.getGeneratedFormTableName();
                    String CITableName = containerInformation.getGeneratedFormTableName();
                    String EIRTableName = EIRForm.getGeneratedFormTableName();

                    manualJoins = " LEFT JOIN " + SEFCOTableName + " ON " + CITableName + ".EquipmentUsed = " + SEFCOTableName + ".txt_EqName " +
                            " LEFT JOIN " + CITableName + " ON " + CITableName + ".TS = " + EIRTableName + ".TS ";

                    manualConditions += " AND " + SEFCOTableName + ".EmpName = '" + activeUser.getDisplayName() + "' ";
                } else {
                    Toast.makeText(
                            context,
                            "Failed to open view, Setup Equipment For Crane Operators and Container Information forms are not found.",
                            Toast.LENGTH_LONG)
                            .show();
                }

            }

            // Do not show documents with outgoing actions
            if (manualConditions != "") {
                manualConditions += " AND ";
            }
            manualConditions += " (oa._id IS NULL OR oa.action = '" + SubmitReadyAction.ACTION_NO_DOCUMENT_SUBMISSION + "') ";
            searchConditions.add(new SearchCondition("wo.actions", "!=", "[]"));

            return documentsDAO.searchForUserDocumentSummaries(
                    activeUser,
                    formList,
                    searchConditions,
                    manualJoins,
                    manualConditions,
                    searchFilter,
                    fromIndex,
                    fetchCount
            );

        } catch (DataAccessObject.DataAccessObjectException | JSONException e) {
            e.printStackTrace();
        } catch (SQLiteDatabaseLockedException e) {
            FLLogger.d(TAG, "Skipped search as the database is still locked");
        }

        return null;
    }

    private void lazyLoadForms() throws DataAccessObject.DataAccessObjectException, SQLiteDatabaseLockedException {
        EIRForm = formsDAO.getForm(EIR_FORM_WEB_ID, activeUser.getCompany().getId());
        jobOrderForm = formsDAO.getForm(JOB_ORDER_FORM_WEB_ID, activeUser.getCompany().getId());
        approvedForRepairContainerForm = formsDAO.getForm(APPROVED_FOR_REPAIR_CONTAINERS_WEB_ID, activeUser.getCompany().getId());
        violationTicketForm = formsDAO.getForm(VIOLATION_TICKET_FORM_WEB_ID, activeUser.getCompany().getId());
        jobOrderFromContainerReservationForm = formsDAO.getForm(CONTAINER_RESERVATION_FORM_WEB_ID, activeUser.getCompany().getId());
    }
}
