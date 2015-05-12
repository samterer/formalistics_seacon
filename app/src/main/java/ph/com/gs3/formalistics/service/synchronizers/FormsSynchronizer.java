package ph.com.gs3.formalistics.service.synchronizers;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.API;
import ph.com.gs3.formalistics.model.api.FormsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.factory.APIFactory;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.UsersDAO;
import ph.com.gs3.formalistics.model.dao.facade.FormsDataWriterFacade;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationFailedException;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class FormsSynchronizer extends AbstractSynchronizer{

    public static final String TAG = FormsSynchronizer.class.getSimpleName();
    public static LoggingType LOGGING_TYPE;

    private User currentUser;

    // <editor-fold desc="Dependencies">

    private FormsDataWriterFacade formsDataWriterFacade;
    private FormsAPI formsAPI;
    private FormsDAO formsDAO;
    private UsersDAO usersDAO;

    // </editor-fold>

    public FormsSynchronizer(Context context, User currentUser) {
        super(TAG, LOGGING_TYPE == null ? LoggingType.DISABLED : LOGGING_TYPE);
        this.currentUser = currentUser;

        formsDataWriterFacade = new FormsDataWriterFacade(context);

        APIFactory formsAPIFactory = new APIFactory();
        formsAPI = formsAPIFactory.createFormsAPI(currentUser.getCompany().getServer());
        formsDAO = new FormsDAO(context);
        usersDAO = new UsersDAO(context);

    }

    /**
     * Downloads all form updates from the server starting from the last update date and
     * saves the updates to the database.
     *
     * @return The list of forms updated after calling this method
     * @throws SynchronizationFailedException
     */
    public List<Form> synchronize() throws SynchronizationFailedException {

        if (currentUser == null) {
            // Unrecoverable error
            throw new RuntimeException("Cannot run synchronizer without a user");
        }

        String lastFormsUpdate = null;
        int companyId = currentUser.getCompany().getId();

        // Download the new forms. If there are existing forms in the database, get only
        // the new and newly updated forms.
        if (formsDAO.getFormsCount(companyId) > 0) {
            lastFormsUpdate = currentUser.getFormsLastUpdateDate();
        }

        try {
            List<Form> updatedForms = formsAPI.getForms(lastFormsUpdate);
            String lastUpdateDate = ((API) formsAPI).getLastSuccessfulRequestServerDate();
            updateUserFormsLastUpdate(lastUpdateDate);

            List<Form> savedForms = new ArrayList<>();
            for (Form updatedForm : updatedForms) {
                updatedForm.setCompany(currentUser.getCompany());

                log("Now saving form: " + updatedForm.getName());
                savedForms.add(formsDataWriterFacade.saveForm(updatedForm));
            }

            return savedForms;
        } catch (APIResponse.InvalidResponseException | HttpCommunicator.CommunicationException e) {
            FLLogger.e(TAG, e.getMessage());
            throw new SynchronizationFailedException(e);
        }

    }

    /**
     * Updates the form's last updated date of the user. If the update failed, this method
     * will log a warning about it.
     *
     * @param updateDate
     *            The new date when the forms are updated. This must be based on the
     *            server date and time.
     */
    protected void updateUserFormsLastUpdate(String updateDate) {

        try {
            usersDAO.setFormsLastUpdate(currentUser.getId(), updateDate);
        } catch (SQLiteException e) {
            // Code level exception
            FLLogger.w(TAG, "Saving of a last forms update failed. " + e.getMessage());
        }

    }

}
