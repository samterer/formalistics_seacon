package ph.com.gs3.formalistics.service.synchronizers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.DocumentsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator.CommunicationException;
import ph.com.gs3.formalistics.model.api.factory.APIFactory;
import ph.com.gs3.formalistics.model.dao.DataAccessObject.DataAccessObjectException;
import ph.com.gs3.formalistics.model.dao.FieldOutgoingFileReferenceDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.OutgoingActionsDAO;
import ph.com.gs3.formalistics.model.dao.facade.OutgoingActionsDataWriterFacade;
import ph.com.gs3.formalistics.model.values.application.APIResponse.InvalidResponseException;
import ph.com.gs3.formalistics.model.values.application.APIResponse.ServerErrorException;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.SubmitReadyAction;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationFailedException;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationPrematureException;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class OutgoingActionsSynchronizer extends AbstractSynchronizer {

    public static final String TAG = OutgoingActionsSynchronizer.class.getSimpleName();
    public static LoggingType LOGGING_TYPE;

    private User activeUser;

    private DocumentsAPI documentsAPI;
    private OutgoingActionsDAO outgoingActionsDAO;
    private FormsDAO formsDAO;
    private FieldOutgoingFileReferenceDAO fieldOutgoingFileReferenceDAO;

    private OutgoingActionsDataWriterFacade outgoingActionsDataWriterFacade;

    public OutgoingActionsSynchronizer(Context context, User activeUser) {
        super(TAG, LOGGING_TYPE == null ? LoggingType.DISABLED : LOGGING_TYPE);

        this.activeUser = activeUser;

        APIFactory apiFactory = new APIFactory();
        documentsAPI = apiFactory.createDocumentsAPI(activeUser);
        outgoingActionsDAO = new OutgoingActionsDAO(context);
        formsDAO = new FormsDAO(context);
        fieldOutgoingFileReferenceDAO = new FieldOutgoingFileReferenceDAO(context);

        outgoingActionsDataWriterFacade = new OutgoingActionsDataWriterFacade(context);
    }

    public void synchronize() throws SynchronizationFailedException, SynchronizationPrematureException {

        List<SynchronizationFailedException> synchFailures = new ArrayList<>();
        List<SubmitReadyAction> submitReadyActions = null;

        submitReadyActions = outgoingActionsDAO.getAllSubmitReadyOutgoingActions(activeUser.getId());

        log("There are " + submitReadyActions.size() + " outgoing actions to send.");

        for (SubmitReadyAction submitReadyAction : submitReadyActions) {

            int formWebId = submitReadyAction.getFormWebId();
            int documentWebId = submitReadyAction.getDocumentWebId();

            try {
                // TODO: make a combined API request for submitting documents and changing star marks

                // Check if this action is about submitting a document
                if (!SubmitReadyAction.ACTION_NO_DOCUMENT_SUBMISSION.equals(submitReadyAction.getAction())) {

                    String fieldUpdates = submitReadyAction.getFieldUpdates();

                    Form form = formsDAO.getForm(formWebId, activeUser.getCompany().getId());
                    List<FormFieldData> fieldsWithDownloadableData = form.getFieldsWithDownloadableData();

                    // if there are fields with downloadable data, make the necessary changes in the field updates
                    if (fieldsWithDownloadableData.size() > 0) {

                        try {
                            JSONObject fieldUpatesJSON = new JSONObject(fieldUpdates);
                            for (FormFieldData formFieldData : fieldsWithDownloadableData) {
                                String url = fieldOutgoingFileReferenceDAO.findFieldFileURL(submitReadyAction.getId(), formFieldData.getName());
                                fieldUpatesJSON.put(formFieldData.getName(), url);
                            }

                            fieldUpdates = fieldUpatesJSON.toString();
                        } catch (JSONException e) {
                            FLLogger.e(TAG, "Unable to update field values: " + e.getMessage());
                        }
                    }

                    log("Submitting action " + submitReadyAction.getAction() + " to form " + submitReadyAction.getFormWebId());
                    documentsAPI.submitDocumentAction(formWebId, documentWebId, fieldUpdates, submitReadyAction.getAction());
                }

                // Check if the document must be marked as starred/unstarred
                if (submitReadyAction.getIsStarredCode() != StarMark.NO_CHANGE) {
                    documentsAPI.markDocumentStar(formWebId, documentWebId, submitReadyAction.getIsStarredCode());
                }

                // Remove the outgoing action from the database upon success
                outgoingActionsDataWriterFacade.removeOutgoingAction(submitReadyAction.getId(), formWebId, activeUser.getCompany().getId());

            } catch (DataAccessObjectException | CommunicationException | InvalidResponseException | ServerErrorException e) {
                FLLogger.e(TAG, "Unable to send outgoing action " + submitReadyAction.getAction() + " to form " + submitReadyAction.getFormWebId() + ": " + e.getMessage());
                // Save the error message about the outgoing action to the database upon fail
                setOutgoingActionError(e.getMessage());
                synchFailures.add(new SynchronizationFailedException(e));
            }
        }

    }


    private void setOutgoingActionError(String errorMessage) {
        // TODO: implementation
    }

}
