package ph.com.gs3.formalistics.model.dao.facade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.model.DatabaseHelperFactory;
import ph.com.gs3.formalistics.model.dao.DataAccessObject;
import ph.com.gs3.formalistics.model.dao.DocumentsDAO;
import ph.com.gs3.formalistics.model.dao.DynamicFormFieldsDAO;
import ph.com.gs3.formalistics.model.dao.UserDocumentsDAO;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.Document;
import ph.com.gs3.formalistics.model.values.business.form.Form;

/**
 * Created by Ervinne on 4/12/2015.
 */
public class DocumentsDataWriterFacade {

    public static final String TAG = DocumentsDataWriterFacade.class.getSimpleName();

    private final Context context;

    private DynamicFormFieldsDAO dynamicFormFieldsDAO;
    private DocumentsDAO documentsDAO;
    private UserDocumentsDAO userDocumentsDAO;

    public DocumentsDataWriterFacade(Context context) {
        this.context = context;

        initializeDAOs(context, null);
    }

    public DocumentsDataWriterFacade(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        this.context = context;

        initializeDAOs(context, preOpenedDatabaseWithTransaction);
    }

    private void initializeDAOs(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {

        if (preOpenedDatabaseWithTransaction != null) {
            dynamicFormFieldsDAO = new DynamicFormFieldsDAO(context, preOpenedDatabaseWithTransaction);
            documentsDAO = new DocumentsDAO(context, preOpenedDatabaseWithTransaction);
            userDocumentsDAO = new UserDocumentsDAO(context, preOpenedDatabaseWithTransaction);
        } else {
            dynamicFormFieldsDAO = new DynamicFormFieldsDAO(context);
            documentsDAO = new DocumentsDAO(context);
            userDocumentsDAO = new UserDocumentsDAO(context);
        }

    }

    /**
     * Saves or updates a document passed to this method. If the document is not yet
     * existing, a new one is saved along with its field values in its corresponding form
     * fields table, otherwise, the existing record and its form field values are updated.
     *
     * @param document The document to save or update.
     * @param form     The form of the document to save or update.
     * @return The updated document instance that contains the new data and form fields
     * data.
     */
    public Document updateOrSaveDocument(Document document, Form form, User user) {

        int webId = document.getWebId();
        int formId = document.getFormId();

        Document existingDocument = null;
        Document savedDocument = null;
        JSONObject savedFieldValues = null;

        String formTableName = form.getGeneratedFormTableName();

        try {
            existingDocument = documentsDAO.getDocument(webId, formId, user.getId());

            JSONObject fieldValuesJSON = null;

            if (existingDocument == null) {
                // throws DataAccessObjectException
                savedDocument = documentsDAO.saveDocument(document);
                fieldValuesJSON = new JSONObject(savedDocument.getFieldValuesJSONString());
                savedFieldValues = dynamicFormFieldsDAO.insertDocumentFieldValues(savedDocument.getId(),
                        formTableName, fieldValuesJSON, form.getActiveFields());
            } else {
                // throws DataAccessObjectException
                savedDocument = documentsDAO.updateDocument(document, user.getId());
                fieldValuesJSON = new JSONObject(savedDocument.getFieldValuesJSONString());
                savedFieldValues = dynamicFormFieldsDAO.updateDocumentFieldValues(savedDocument.getId(),
                        formTableName, fieldValuesJSON, form.getActiveFields());
            }

//            savedDocument.setFieldValuesJSONString(savedFieldValues.toString());
            insertUserDocument(savedDocument, document, user);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return savedDocument;
    }

    public void insertUserDocument(Document savedDocument, Document originalDocument, User user) {
        int originalUserId = userDocumentsDAO.getUserIdOfDocument(savedDocument.getId());

//        if (originalUserId != user.getName()) {
//            userDocumentsDAO.insertUserDocument(user.getName(), savedDocument.getName(), originalDocument.getStarMark());
//        }

        if (originalUserId == 0 || originalUserId != user.getId()) {
            userDocumentsDAO.insertUserDocument(user.getId(), savedDocument.getId(), originalDocument.getStarMark());
        } else {
            userDocumentsDAO.changeDocumentStarMark(user.getId(), savedDocument.getId(), originalDocument.getStarMark());
        }

    }

    public void deleteDocument(Form form, int documentId, int userId) throws DataAccessObject.DataAccessObjectException {

        SQLiteOpenHelper sqLiteOpenHelper = DatabaseHelperFactory.getDatabaseHelper(context);
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        initializeDAOs(context, database);

        try {
            database.beginTransactionNonExclusive();

            userDocumentsDAO.deleteUserDocumentReference(userId, documentId);
            documentsDAO.deleteDocument(documentId);
            dynamicFormFieldsDAO.deleteDocumentFieldValues(documentId, form.getGeneratedFormTableName());

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

}
