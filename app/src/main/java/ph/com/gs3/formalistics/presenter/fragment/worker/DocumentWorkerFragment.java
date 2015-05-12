package ph.com.gs3.formalistics.presenter.fragment.worker;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.DocumentType;
import ph.com.gs3.formalistics.global.constants.FileStatus;
import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.FilesDAO;
import ph.com.gs3.formalistics.model.dao.FormsDAO;
import ph.com.gs3.formalistics.model.dao.OutgoingActionsDAO;
import ph.com.gs3.formalistics.model.dao.facade.FilesDataWriterFacade;
import ph.com.gs3.formalistics.model.dao.facade.FormsDataReaderFacade;
import ph.com.gs3.formalistics.model.dao.facade.OutgoingActionsDataWriterFacade;
import ph.com.gs3.formalistics.model.values.application.FieldOutgoingFileReference;
import ph.com.gs3.formalistics.model.values.application.FileInfo;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.document.DocumentHeaderData;
import ph.com.gs3.formalistics.model.values.business.document.OutgoingAction;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.content.EmbeddedViewData;
import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;
import ph.com.gs3.formalistics.view.document.contents.fields.FDynamicImage;

/**
 * Created by Ervinne on 4/14/2015.
 */
public class DocumentWorkerFragment extends Fragment {

    public static final String TAG = DocumentWorkerFragment.class.getSimpleName();

    private User activeUser;
    private Form currentForm;
    private DocumentType documentType;
    private Document currentDocument;
    private OutgoingAction currentOutgoingAction;

    private int parentDocumentId;

    private FormsDAO formsDAO;
    private DocumentsDAO documentsDAO;
    private OutgoingActionsDAO outgoingActionsDAO;
    private DynamicFormFieldsDAO dynamicFormFieldsDAO;
    private FilesDAO filesDAO;

    private FormsDataReaderFacade formsDataReaderFacade;
    private OutgoingActionsDataWriterFacade outgoingActionsDataWriterFacade;
    private FilesDataWriterFacade filesDataWriterFacade;

    private int originalStarMark;

    private List<FileInfo> queuedFileInfoList;

    public static DocumentWorkerFragment createInstance(Context context, User activeUser) {

        DocumentWorkerFragment instance = new DocumentWorkerFragment();

        instance.initializeDependencies(context);
        instance.activeUser = activeUser;
        instance.queuedFileInfoList = new ArrayList<>();

        return instance;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    //<editor-fold desc="Initialization Methods">
    private void initializeDependencies(Context context) {

        formsDAO = new FormsDAO(context);
        documentsDAO = new DocumentsDAO(context);
        outgoingActionsDAO = new OutgoingActionsDAO(context);
        dynamicFormFieldsDAO = new DynamicFormFieldsDAO(context);
        filesDAO = new FilesDAO(context);

        formsDataReaderFacade = new FormsDataReaderFacade(context);
        outgoingActionsDataWriterFacade = new OutgoingActionsDataWriterFacade(context);
        filesDataWriterFacade = new FilesDataWriterFacade(context);

    }

    public void initialize(
            DocumentType documentType, int formId, int documentId, int parentDocumentId)
            throws InvalidFormException {

        this.documentType = documentType;
        this.parentDocumentId = parentDocumentId;

        try {
            currentForm = formsDataReaderFacade.getForm(formId);
        } catch (DataAccessObject.DataAccessObjectException e) {
            throw new InvalidFormException("Failed to load form, it may be corrupted");
        }

        try {
            switch (documentType) {
                case NEW_DOCUMENT:
                case CHILD_DOCUMENT:
                    currentDocument = createNewBlankDocument(formId, activeUser.getId());
                    originalStarMark = StarMark.UNSTARRED;
                    break;
                case EXISTING_DOCUMENT:
                    currentDocument = documentsDAO.getDocument(documentId);
                    originalStarMark = currentDocument.getStarMark();
                    // Null if no outgoing action, throws DataAccessObjectException
                    currentOutgoingAction = outgoingActionsDAO.getOutgoingActionByDocumentId(documentId);
                    break;
                case OUTGOING_DOCUMENT:
                    currentOutgoingAction = outgoingActionsDAO.getOutgoingAction(documentId);
                    originalStarMark = currentOutgoingAction.getIsStarredCode();
                    // Null if no document, throws DataAccessObjectException
                    currentDocument = currentOutgoingAction.getDocument();
                    break;
            }
        } catch (DataAccessObject.DataAccessObjectException e) {
            e.printStackTrace();
        }

    }
    //</editor-fold>

    //<editor-fold desc="Functional Methods">
    public void saveDocumentAction(JSONObject documentFieldValues, String action) {

        FLLogger.d(TAG, "documentType: " + documentType);

        OutgoingAction outgoingAction = null;

        switch (documentType) {
            case NEW_DOCUMENT:
                outgoingAction = outgoingActionsDataWriterFacade.saveDocumentAction(0, currentForm, documentFieldValues, action, activeUser);
                break;
            case EXISTING_DOCUMENT:
                int documentId = currentDocument.getId();
                outgoingAction = outgoingActionsDataWriterFacade.saveDocumentAction(
                        documentId, currentForm, documentFieldValues, action, activeUser);
                break;
            case OUTGOING_DOCUMENT:
                FLLogger.d(TAG, "Add a cancel action for outgoing documents");
                break;
            case CHILD_DOCUMENT:
                outgoingAction = outgoingActionsDataWriterFacade.saveDocumentAction(
                        0, currentForm, documentFieldValues, action, activeUser, parentDocumentId);
                break;
        }

        if (outgoingAction != null) {

            // Save FileInfos that should be saved upon submiting action
            for (FileInfo fileInfo : queuedFileInfoList) {
                fileInfo.getFieldOutgoingFileReference().setOutgoingActionId(outgoingAction.getId());
                filesDataWriterFacade.saveOutgoingFileInfo(fileInfo);
            }

        }

    }

    public Form findForm(int formWebId, int companyId) {

        try {
            return formsDAO.getForm(formWebId, companyId);
        } catch (DataAccessObject.DataAccessObjectException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Document createNewBlankDocument(int formId, int authorId) {

        Document document = new Document();

        document = new Document();
        document.setWebId(0);
        document.setFormId(formId);
        document.setAuthorId(authorId);

        return document;

    }

    public String lookupData(String formName, String returnFieldName, String compareToOtherFormFieldName, String compareToThisFormFieldValue) throws LookupFailedException {

        List<String> searchResultFieldNames = new ArrayList<>();
        searchResultFieldNames.add(returnFieldName);

        List<SearchCondition> searchConditions = new ArrayList<>();
        searchConditions.add(new SearchCondition(compareToOtherFormFieldName, "=", compareToThisFormFieldValue));

        try {
            Form form = formsDAO.getFormByName(formName, activeUser.getCompany().getId());

            if (form != null) {
                List<JSONObject> searchResults = dynamicFormFieldsDAO.search(form, searchResultFieldNames, activeUser.getId(), searchConditions);

                if (searchResults.size() > 0) {
                    return searchResults.get(0).getString(returnFieldName);
                } else {
                    throw new LookupFailedException("No value for " + returnFieldName + " found");
                }
            } else {
                throw new LookupFailedException("Lookup failed, cannot find form " + formName);
            }

        } catch (DataAccessObject.DataAccessObjectException e) {
            throw new LookupFailedException("Lookup failed, failed to find form " + formName);
        } catch (JSONException e) {
            throw new LookupFailedException("Search failed: " + e.getMessage());
        }

    }

    public List<JSONObject> searchDataForEmbeddedView(String searchCompareToFieldValue, EmbeddedViewData embeddedViewData) throws DataAccessObject.DataAccessObjectException, JSONException {

        // throws DataAccessObjectException
        Form searchForm = formsDataReaderFacade.getForm(embeddedViewData.getSearchFormWebId(), activeUser.getCompany().getId());
        String searchFieldName = embeddedViewData.getSearchFieldId();
        String operator = embeddedViewData.getSearchConditionalOperator();
        SearchCondition condition = new SearchCondition(searchFieldName, operator, searchCompareToFieldValue);

        List<SearchCondition> searchConditions = new ArrayList<>();
        searchConditions.add(condition);

        List<ViewColumn> searchViewColumns = embeddedViewData.getViewColumns();
        List<String> searchColumnNames = new ArrayList<>();

        for (ViewColumn column : searchViewColumns) {
            searchColumnNames.add(column.getName());
        }

        // throws JSONException
        return dynamicFormFieldsDAO.search(searchForm, searchColumnNames, activeUser.getId(), searchConditions);

    }

    public boolean hasNewChildOutgoingActions() {
        return outgoingActionsDAO.hasNewChildOutgoingActions();
    }

    public void clearOrphanedOutgoingActions() {
        outgoingActionsDAO.clearOrphanedOutgoingActions();
    }

    public void updateChildOutgoingActionsForSubmition() {
        outgoingActionsDAO.updateChildOutgoingActionsForSubmition(currentDocument.getId());
    }

    public File moveFileToInternalStorage(File file) {
        return filesDAO.moveFileToInternal(file, getActivity());
    }

    public File saveBitmapToFile(Bitmap bitmap) {

        String fileName = DateUtilities.getServerFormattedCurrentDateTime() + ".bmp";
        return filesDAO.saveBitmap(bitmap, fileName);

    }

    public String getRealPathFromURI(Uri imageURI) {
        return filesDAO.getRealPathFromURI(imageURI);
    }

    public Bitmap getBitmapFromDynamicImageField(FDynamicImage dynamicImage) {

        String fileRemoteURL = dynamicImage.getValue();
        Bitmap bitmap = null;

        if (fileRemoteURL != null && !"".equals(fileRemoteURL)) {

            try {
                // try first if the value is avalid URL, if failed, the value is the local location of the file
                new URL(fileRemoteURL);
                bitmap = filesDAO.getBitmapWithFileRemoteURL(fileRemoteURL);
            } catch (MalformedURLException e) {
                bitmap = filesDAO.getBitmapFromPath(fileRemoteURL);
            }


        } else {
            FLLogger.d(TAG, "Cannot load " + dynamicImage.getFieldName() + ", value is null or missing.");
        }

        return bitmap;

    }

    public Bitmap getBitmapFromPath(String path) {
        return filesDAO.getBitmapFromPath(path);
    }

    public void saveOutgoingFileFromDynamicImageField(FDynamicImage dynamicImageField) {

        FileInfo fileInfo = new FileInfo();
        FieldOutgoingFileReference fieldOutgoingFileReference = new FieldOutgoingFileReference();

        fieldOutgoingFileReference.setFormId(currentForm.getId());
        fieldOutgoingFileReference.setFieldName(dynamicImageField.getFieldName());

        fileInfo.setLocalPath(dynamicImageField.getImageLocalPath());
        fileInfo.setStatus(FileStatus.OUTGOING);
        fileInfo.setOwnerId(activeUser.getId());
        fileInfo.setFieldOutgoingFileReference(fieldOutgoingFileReference);

        queuedFileInfoList.add(fileInfo);

    }

    //</editor-fold>

    //<editor-fold desc="Getters">

    public int getOriginalStarMark() {
        return originalStarMark;
    }

    public User getActiveUser() {
        return activeUser;
    }

    public Form getCurrentForm() {
        return currentForm;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }

    public OutgoingAction getCurrentOutgoingAction() {
        return currentOutgoingAction;
    }

    public DocumentHeaderData getDocumentHeaderData() {

        switch (documentType) {
            case CHILD_DOCUMENT:
            case NEW_DOCUMENT:
                return DocumentHeaderData.createFromForm(getCurrentForm());
            case EXISTING_DOCUMENT:
                return DocumentHeaderData.createFromDocument(getCurrentDocument(), getCurrentForm());
            case OUTGOING_DOCUMENT:
                return DocumentHeaderData.createFromOutgoingAction(getCurrentOutgoingAction(), getCurrentForm());
            default:
                return null;
        }

    }

    public int getParentDocumentId() {
        return parentDocumentId;
    }
    //</editor-fold>

    public static class InvalidFormException extends Exception {
        public InvalidFormException(String message) {
            super(message);
        }
    }

    public static class LookupFailedException extends Exception {

        public LookupFailedException(String detailMessage) {
            super(detailMessage);
        }

        public LookupFailedException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public LookupFailedException(Throwable throwable) {
            super(throwable);
        }
    }

}
