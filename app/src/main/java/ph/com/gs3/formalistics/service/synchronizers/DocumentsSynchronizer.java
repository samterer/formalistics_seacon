package ph.com.gs3.formalistics.service.synchronizers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.LoggingType;
import ph.com.gs3.formalistics.global.constants.SyncType;
import ph.com.gs3.formalistics.global.constants.UserUpdateOptions;
import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.DatabaseHelperFactory;
import ph.com.gs3.formalistics.model.api.DocumentsAPI;
import ph.com.gs3.formalistics.model.api.HttpCommunicator;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.DocumentsJSONParser;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.UserJSONParser;
import ph.com.gs3.formalistics.model.api.factory.APIFactory;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.UserDocumentsDAO;
import ph.com.gs3.formalistics.model.dao.facade.DocumentsDataWriterFacade;
import ph.com.gs3.formalistics.model.dao.facade.FilesDataWriterFacade;
import ph.com.gs3.formalistics.model.values.application.APIResponse;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationFailedException;
import ph.com.gs3.formalistics.service.synchronizers.exceptions.SynchronizationPrematureException;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class DocumentsSynchronizer extends AbstractSynchronizer {

    public static final String TAG = DocumentsSynchronizer.class.getSimpleName();
    public static LoggingType LOGGING_TYPE;

    private final User activeUser;

    private final Context context;

    private final DocumentsAPI documentsAPI;

    private final UserDocumentsDAO userDocumentsDAO;
    private final FormsDAO formsDAO;

    private DocumentsDataWriterFacade documentsDataWriterFacade;
    private final FilesDataWriterFacade filesDataWriterFacade;

    private UsersSynchronizer usersSynchronizer;

    protected final DocumentsSynchronizerEventListener eventListener;

    public DocumentsSynchronizer(Context context, User currentUser, DocumentsSynchronizerEventListener eventListener) {
        super(TAG, LOGGING_TYPE == null ? LoggingType.DISABLED : LOGGING_TYPE);

        this.context = context;

        this.activeUser = currentUser;
        this.eventListener = eventListener;

        APIFactory apiFactory = new APIFactory();

        documentsAPI = apiFactory.createDocumentsAPI(currentUser);

        userDocumentsDAO = new UserDocumentsDAO(context);
        formsDAO = new FormsDAO(context);

        documentsDataWriterFacade = new DocumentsDataWriterFacade(context);
        filesDataWriterFacade = new FilesDataWriterFacade(context);

        usersSynchronizer = new UsersSynchronizer(context, currentUser);
    }

    public void synchronize(SyncType syncType, String partialSyncFormIds) throws SynchronizationFailedException, SynchronizationPrematureException {
        List<Form> forms = null;
        try {
            if (syncType == SyncType.PARTIAL || syncType == SyncType.PARTIAL_WITH_FORMS) {
                List<String> partialSyncFormIdList = Serializer.unserializeList(partialSyncFormIds);
                forms = new ArrayList<>();
                for (String partialSyncFormId : partialSyncFormIdList) {
                    Form form = formsDAO.getForm(Integer.parseInt(partialSyncFormId), activeUser.getCompany().getId());

                    if (form != null) {
                        forms.add(form);
                    }
                }

                FLLogger.d(TAG, "Sychnronizing documents of " + forms.size() + " forms");
            } else {
                forms = formsDAO.getCompanyForms(activeUser.getCompany().getId());
            }

        } catch (DataAccessObject.DataAccessObjectException e) {
            throw new SynchronizationFailedException(e);
        }

        List<SynchronizationFailedException> syncFailures = new ArrayList<>();

        for (Form form : forms) {

//            if (form.getWebId() == SeaconSearchDataProvider.CONTAINER_INFORMATION_FORM_WEB_ID || form.getWebId() == SeaconSearchDataProvider.EIR_FORM_WEB_ID || form.getWebId() == SeaconSearchDataProvider.JOB_ORDER_FORM_WEB_ID) {
//                log("Skipping " + form.toString() + "'s documents");
//                continue;
//            }

            log("Synchronizing " + form.toString() + "'s documents");
            try {
                int lastIndex = 0;
                int fetchSize = 100;
                boolean hasSynchronizedDocuments = false;

                List<Document> downloadedDocuments;
                do {
                    downloadedDocuments = synchronizeFormDocuments(form, lastIndex, fetchSize);
                    lastIndex += 100;
                    hasSynchronizedDocuments = true;
                    // continue on until there are no documents that can be downloaded or the size of downloaded documents is below 100

                    if (eventListener != null && hasSynchronizedDocuments) {
                        eventListener.onNewDocumentsDownloaded();
                    }
                }
                while (downloadedDocuments != null && downloadedDocuments.size() >= 100);
            } catch (SynchronizationFailedException e) {
                syncFailures.add(e);
            }
            log("Done");
        }

        if (syncFailures.size() > 0) {
            throw new SynchronizationPrematureException(syncFailures);
        }

    }

    public List<Document> synchronizeFormDocuments(Form form, int from, int fetchCount) throws SynchronizationFailedException {

        // check if a form has any fields that will need downloading of contents
        List<FormFieldData> formFieldsWithDownloadableData = form.getFieldsWithDownloadableData();

        String documentsLastUpdateDate;
        int userDocumentCount = userDocumentsDAO.getUserDocumentsCount(activeUser.getId());

        if (userDocumentCount > 0) {
            documentsLastUpdateDate = form.getDocumentsLastUpdateDate();
            log("Getting documents updated later than " + documentsLastUpdateDate);
        } else {
            documentsLastUpdateDate = null;
            log("Getting all documents");
        }

        List<Document> synchronizedDocuments = new ArrayList<>();

        APIResponse response;
        try {
            response = documentsAPI.getFormDocumentUpdates(
                    form.getWebId(), documentsLastUpdateDate, from, fetchCount
            );
        } catch (APIResponse.InvalidResponseException e) {
            throw new SynchronizationFailedException(
                    "The server gave an invalid response while getting form document updates for form web id = "
                            + form.getWebId(), e);
        } catch (HttpCommunicator.CommunicationException e) {
            throw new SynchronizationFailedException(e);
        }

        if (response.isOperationSuccessful()) {

            // Throws JSONException
            JSONArray documentsJSONArray;
            try {
                documentsJSONArray = new JSONArray(response.getResults());
            } catch (JSONException e) {
                throw new SynchronizationFailedException(
                        "The server response is not a valid JSON array.", e);
            }

            int documentCount = documentsJSONArray.length();

            SQLiteOpenHelper sqLiteOpenHelper = DatabaseHelperFactory.getDatabaseHelper(context);
            SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
            documentsDataWriterFacade = new DocumentsDataWriterFacade(context, database);

            usersSynchronizer = new UsersSynchronizer(context, database, activeUser);

            try {
                database.beginTransactionNonExclusive();
                for (int i = 0; i < documentCount; i++) {

                    // Throws JSONException
                    JSONObject documentJSON;
                    Document document;

                    try {
                        // Extract document
                        documentJSON = new JSONObject(documentsJSONArray.getString(i));
                        document = DocumentsJSONParser.createFromJSON(documentJSON, form.getActiveFields());
                    } catch (JSONException e) {
                        throw new SynchronizationFailedException(
                                "One or more of the document JSON objects is invalid", e);
                    }

                    // Get the users (author and processor)
                    document = synchronizeUsersFromDocumentUpdate(document, documentJSON);

                    // Build the document using extracted data and current user data
                    document.setFormId(form.getId());

                    // Save the document
                    document = documentsDataWriterFacade.updateOrSaveDocument(document, form, activeUser);
                    synchronizedDocuments.add(document);

                    // If the document has downloadable content:
                    if (formFieldsWithDownloadableData.size() > 0) {
                        log(form.getName() + " has " + formFieldsWithDownloadableData.size() + " downloadable value fields");

                        try {
                            JSONObject documentData = new JSONObject(document.getFieldValuesJSONString());
                            for (FormFieldData formFieldData : formFieldsWithDownloadableData) {
                                // Save a record about incoming files, this will be synchronized by
                                // another synchronizer later.
                                String remoteURL = documentData.getString(formFieldData.getName());
                                int userId = activeUser.getId();
                                filesDataWriterFacade.saveIncomingFileInfo(remoteURL, userId);
                            }

                        } catch (JSONException e) {
                            // FIXME: find a way to throw an exception but still specify that the document has been synchronized
                            FLLogger.e(TAG, "Failed to save an incoming file reference because the system failed to get document data: " + e.getMessage());
                        }
                    }

                    log("Updated document: " + document.toString() + " from form: " + form.getName());
                }

                database.setTransactionSuccessful();
            } finally {
                database.endTransaction();
                database.close();
            }

            // Update the documents' last update date of the current form
            formsDAO.updateDocumentsLastUpdateDate(form.getId(), response.getServerDate());
        } else {
            // Server error
            FLLogger.w(TAG, "Failed to get document updates: " + response.getErrorMessage());
            throw new SynchronizationFailedException(response.getErrorMessage());
        }

        return synchronizedDocuments;

    }

    /**
     * Extracts user (author and processor) data from the document update
     * (documentUpdateJSON) and saves any updates to the database.
     *
     * @param documentUpdateJSON
     * @throws SynchronizationFailedException
     */
    protected Document synchronizeUsersFromDocumentUpdate(Document document, JSONObject documentUpdateJSON)
            throws SynchronizationFailedException {

        EnumSet<UserUpdateOptions> userUpdateOptions = EnumSet
                .of(UserUpdateOptions.UPDATE_EXCEPT_IS_ACTIVE,
                        UserUpdateOptions.UPDATE_EXCEPT_PASSWORD);

        // Retrieve the user from the JSON and update the database
        try {
            User author = UserJSONParser.createAuthorFromDocumentJSON(documentUpdateJSON);

            if (activeUser == null) {
                FLLogger.d(TAG, "active user is null: " + documentUpdateJSON.toString());
            }

            if (author == null) {
                FLLogger.d(TAG, "author is null: " + documentUpdateJSON.toString());
            }

            // FIXME: temporary only
            if (author != null) {
                author.setCompany(activeUser.getCompany());
                author = usersSynchronizer.updateUser(author, userUpdateOptions);
                document.setAuthorId(author.getId());
            }
        } catch (JSONException e) {
            throw new SynchronizationFailedException(
                    "The server gave an invalid response about a document's author/requestor data.",
                    e);
        }

        // Retrieve the processor from the JSON and update the database
//        try {
//            User processor = UserJSONParser.createProcessorFromDocumentJSON(documentUpdateJSON);
//            if (processor != null) {
//                processor.setCompany(activeUser.getCompany());
//                processor = usersSynchronizer.updateUser(processor, userUpdateOptions);
//                // document.setProcessor(processor.getWebId());
//            }
//        } catch (JSONException e) {
//            throw new SynchronizationFailedException(
//                    "The server gave an invalid response about a document's processor data.", e);
//        }

        return document;

    }

    public interface DocumentsSynchronizerEventListener {
        void onNewDocumentsDownloaded();

        void onAllFormsDownloaded();
    }

}
