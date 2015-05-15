package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.DataAccessObject.DataAccessObjectException;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.OutgoingActionsDAO;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.OutgoingAction;
import ph.com.gs3.formalistics.model.values.business.document.SubmitReadyAction;
import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class OutgoingActionsDataWriterFacade {

    public static final String TAG = OutgoingActionsDataWriterFacade.class.getSimpleName();

    private final OutgoingActionsDAO outgoingActionsDAO;
    private final DynamicFormFieldsDAO dynamicFormFieldsDAO;

    private final FormsDAO formsDAO;

    private final FilesDataWriterFacade filesDataWriterFacade;

    public OutgoingActionsDataWriterFacade(Context context) {
        outgoingActionsDAO = new OutgoingActionsDAO(context);
        dynamicFormFieldsDAO = new DynamicFormFieldsDAO(context);

        formsDAO = new FormsDAO(context);

        filesDataWriterFacade = new FilesDataWriterFacade(context);
    }

    public OutgoingAction saveDocumentAction(
            int documentId, Form form, JSONObject documentFieldValues, String action, User issuedByUser) {

        return saveDocumentAction(documentId, form, documentFieldValues, action, issuedByUser, -1);

    }

    public OutgoingAction saveDocumentAction(
            int documentId, Form form, JSONObject documentFieldValues, String action, User issuedByUser, int documentParentId) {

        OutgoingAction updatedAction = null;
        OutgoingAction existingAction = null;

        // Check if there is an already existing outgoing action
        try {
            existingAction = outgoingActionsDAO.getOutgoingActionByDocumentId(documentId);

            if (existingAction == null || documentId == 0) { // always create new action for new documents
                // Create new action
                updatedAction = createAndSaveNewOutgoingAction(
                        documentId, form.getId(), documentFieldValues, action, StarMark.NO_CHANGE, issuedByUser, documentParentId);

                // Insert field values:
                dynamicFormFieldsDAO.insertOutgoingActionFieldValues(
                        updatedAction.getId(), form.getGeneratedFormTableName(), documentFieldValues, form.getActiveFields());
            } else {
                // Update the existing action
                int actionId = existingAction.getId();
                updatedAction = outgoingActionsDAO.updateOutgoingAction(
                        actionId, documentFieldValues, action, documentParentId);

                // Update field values
                dynamicFormFieldsDAO.updateOutgoingActionFieldValues(
                        updatedAction.getId(), form.getGeneratedFormTableName(), documentFieldValues, form.getActiveFields());
            }

        } catch (DataAccessObjectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            FLLogger.e(TAG, "Failed to save outgoing action field values: " + e.getMessage());
            e.printStackTrace();
        }

        return updatedAction;

    }

    public OutgoingAction saveStarMark(
            int documentId, int formId, int starMarkInt, User issuedByUser) {

        OutgoingAction updatedAction = null;
        OutgoingAction existingAction = null;

        // Check if there is an already existing outgoing action
        try {
            // Throws DataAccessObjectException
            existingAction = outgoingActionsDAO.getOutgoingActionByDocumentId(documentId);

            if (existingAction == null) {
                // Create new action
                // Throws InsertFailedException
                updatedAction = createAndSaveNewOutgoingAction(
                        documentId, formId, null, SubmitReadyAction.ACTION_NO_DOCUMENT_SUBMISSION, starMarkInt, issuedByUser, -1);
            } else {
                // Update the existing action
                int actionId = existingAction.getId();
                updatedAction = outgoingActionsDAO.updateOutgoingActionStar(actionId, starMarkInt);
            }

            return updatedAction;

        } catch (DataAccessObjectException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }

    public void removeOutgoingAction(int id, int formWebId, int companyId) throws DataAccessObjectException {

        Form form = formsDAO.getForm(formWebId, companyId);
        removeOutgoingAction(id, form);

    }

    public void removeOutgoingAction(int id, Form form) throws DataAccessObjectException {

        dynamicFormFieldsDAO.deleteOutgoingActionFieldValues(id, form.getGeneratedFormTableName());
        outgoingActionsDAO.removeOutgoingAction(id);

    }

    /**
     * Creates a new outgoing action in the database, this methods assumes a connected
     * database already.
     *
     * @param documentId
     * @param formId
     * @param documentFieldValues
     * @param action
     * @param starMarkInt
     * @param issuedByUser
     * @return
     * @throws DataAccessObjectException
     */
    public OutgoingAction createAndSaveNewOutgoingAction(
            int documentId, int formId, JSONObject documentFieldValues, String action,
            int starMarkInt, User issuedByUser, int parentDocumentId) throws DataAccessObjectException {

        OutgoingAction newAction = new OutgoingAction();
        OutgoingAction savedAction = null;

        Document emptyDocument = new Document();
        emptyDocument.setId(documentId);

        String dateIssued = DateUtilities.getServerFormattedCurrentDateTime();

        newAction.setAction(action);
        newAction.setDocumentFieldUpdates(documentFieldValues);
        newAction.setDocument(emptyDocument);
        newAction.setFormId(formId);
        newAction.setIssuedByUser(issuedByUser);
        newAction.setIsStarredCode(starMarkInt);
        newAction.setDateIssued(dateIssued);
        newAction.setParentDocumentId(parentDocumentId);

        savedAction = outgoingActionsDAO.saveOutgoingAction(newAction);

        return savedAction;

    }

}
