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

    private Form EIRForm;
    private Form jobOrderForm;

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

            if (activeUser.getPositionId() == GATER_POSITION_ID ||
                    activeUser.getPositionId() == CRANE_OPERATOR_ID ||
                    activeUser.getPositionId() == INSPECTOR_POSITION_ID) {
                if (EIRForm != null) {
                    formList.add(EIRForm);
                }

            }

            if (activeUser.getPositionId() == INSPECTOR_POSITION_ID) {
                if (jobOrderForm != null) {
                    formList.add(jobOrderForm);
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
            String manualConditions = DocumentsDAO.getForApprovalWhereClause(activeUser);

            if (activeUser.getPositionId() == CRANE_OPERATOR_ID) {

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
            manualConditions += " AND (oa._id IS NULL OR oa.action = '" + SubmitReadyAction.ACTION_NO_DOCUMENT_SUBMISSION + "') ";
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
    }
}
