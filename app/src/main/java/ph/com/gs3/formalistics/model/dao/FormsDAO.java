package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.FormJSONParserV2;
import ph.com.gs3.formalistics.model.tables.FormsTable;
import ph.com.gs3.formalistics.model.values.business.Company;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData.InvalidFormFieldException;
import ph.com.gs3.formalistics.model.values.business.form.FormViewContentData;

/**
 * Created by Ervinne on 4/11/2015.
 */
public class FormsDAO extends DataAccessObject {

    public static final String TAG = FormsDAO.class.getSimpleName();

    public FormsDAO(Context context) {
        super(context);
    }

    // <editor-fold desc="Query Methods">

    public Form getForm(int webId, int companyId) throws DataAccessObjectException {

        String whereClauseRaw = "f.web_id = %d AND f.company_id = %d";
        String whereClause = String.format(whereClauseRaw, webId, companyId);

        return getForm(whereClause);

    }

    public Form getForm(int id) throws DataAccessObjectException {

        String whereClauseRaw = "f._id = %d";
        String whereClause = String.format(Locale.ENGLISH, whereClauseRaw, id);

        return getForm(whereClause);

    }

    public Form getFormByName(String name, int companyId) throws DataAccessObjectException {

        String whereClause = "f." + FormsTable.COL_NAME + "='" + name + "' AND " +
                "f." + FormsTable.COL_COMPANY_ID + "=" + companyId;
        return getForm(whereClause);

    }

    protected Form getForm(String whereClause) throws DataAccessObjectException {

        // @formatter:off
		String rawQuery = "SELECT "
					+ "f.*, c._id AS company_id, c.web_id AS company_web_id, "
					+ "c.name AS company_name, c.server FROM Forms f "
					+ "LEFT JOIN Companies c ON f.company_id = c._id "
					+ "WHERE %s";
		// @formatter:on

        String query = String.format(Locale.ENGLISH, rawQuery, whereClause);

        FLLogger.d(TAG, query);

        Form form = null;

        try {
            open();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                form = cursorToForm(cursor);
                Company company = CompaniesDAO.cursorToCompanyWithAliasedColumns(cursor);
                form.setCompany(company);
            }
        } finally {
            close();
        }

        return form;

    }

    public List<Form> getCompanyForms(int companyId) throws DataAccessObjectException {

        List<Form> forms = new ArrayList<>();

        // @formatter:off
		String query = "SELECT "
				+ "f.*, c._id AS company_id, c.web_id AS company_web_id, "
				+ "c.name AS company_name, c.server FROM Forms f "
				+ "LEFT JOIN Companies c ON f.company_id = c._id "
				+ "WHERE f.company_id = ?";
		// @formatter:on

        try {
            open();
            Cursor cursor = database.rawQuery(query, new String[]{Integer.toString(companyId)});

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Form form = cursorToForm(cursor);
                Company company = CompaniesDAO.cursorToCompanyWithAliasedColumns(cursor);
                form.setCompany(company);

                forms.add(form);

                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return forms;

    }

    public long getFormsCount(int companyId) {

        String selection = FormsTable.COL_COMPANY_ID + " = ?";
        String[] selectionArgs = {Integer.toString(companyId)};

        long count = -1;

        try {
            open();
            Cursor cursor = database.query(FormsTable.NAME, new String[]{FormsTable.COL_ID},
                    selection, selectionArgs, null, null, null);

            count = cursor.getCount();
            cursor.close();
        } finally {
            close();
        }

        return count;

    }

    // </editor-fold>

    // <editor-fold desc="Insert & Update Methods">

    public int insertForm(Form form) throws SQLException {

        try {
            open();
            ContentValues cv = createCVFromForm(form);
            return (int) database.insertOrThrow(FormsTable.NAME, null, cv);
        } finally {
            close();
        }
    }

    public void updateDocumentsLastUpdateDate(int formId, String documentsLastUpdateDate) {

        ContentValues cv = new ContentValues();

        cv.put(FormsTable.COL_DOCUMENTS_LAST_UPDATE_DATE, documentsLastUpdateDate);

        String whereClause = FormsTable.COL_ID + " = ?";
        String[] whereArgs = new String[]{Integer.toString(formId)};

        try {
            open();
            long affectedRows = database.update(FormsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows <= 0) {
                throw new SQLException("Failed to update form.");
            }
        } finally {
            close();
        }

    }

    public Form updateForm(Form form) throws DataAccessObjectException {
        ContentValues cv = createCVFromForm(form);

        int webId = form.getWebId();
        int companyId = form.getCompany().getId();

        String whereClause = FormsTable.COL_WEB_ID + " = ? AND " + FormsTable.COL_COMPANY_ID
                + " = ?";
        String[] whereArgs = new String[]{Integer.toString(webId), Integer.toString(companyId)};

        try {
            open();
            long affectedRows = database.update(FormsTable.NAME, cv, whereClause, whereArgs);

            if (affectedRows > 0) {
                return getForm(webId, companyId);
            } else {
                throw new SQLException("Failed to update form.");
            }
        } finally {
            close();
        }

    }

    // </editor-fold>

    // <editor-fold desc="Converter Methods">

    private ContentValues createCVFromForm(Form form) {

        ContentValues cv = new ContentValues();

        cv.put(FormsTable.COL_WEB_ID, form.getWebId());
        cv.put(FormsTable.COL_NAME, form.getName());

        cv.put(FormsTable.COL_WORKFLOW_ID, form.getWorkflowId());
        cv.put(FormsTable.COL_WEB_TABLE_NAME, form.getWebTableName());

        cv.put(FormsTable.COL_COMPANY_ID, form.getCompany().getId());

        // Fields
        try {
            JSONArray fieldsJSONArray = FormJSONParserV2.createJSONFromFormContents(form.getActiveContents());
            cv.put(FormsTable.COL_ACTIVE_FIELDS, fieldsJSONArray.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return cv;

    }

    private Form cursorToForm(Cursor cursor) throws DataAccessObjectException {
        // @formatter:off
		// Initialize indices

		int idIndex 	= cursor.getColumnIndexOrThrow(FormsTable.COL_ID);
		int webIdIndex 	= cursor.getColumnIndexOrThrow(FormsTable.COL_WEB_ID);
		int nameIndex 	= cursor.getColumnIndexOrThrow(FormsTable.COL_NAME);

		int fieldsIndex = cursor.getColumnIndexOrThrow(FormsTable.COL_ACTIVE_FIELDS);

		int workflowIdIndex 	= cursor.getColumnIndexOrThrow(FormsTable.COL_WORKFLOW_ID);
		int webTableNameIndex = cursor.getColumnIndexOrThrow(FormsTable.COL_WEB_TABLE_NAME);

		int documentsLastUpdateDateIndex = cursor.getColumnIndexOrThrow(FormsTable.COL_DOCUMENTS_LAST_UPDATE_DATE);

		// Initialize values
		int id = cursor.getInt(idIndex);
		int webId = cursor.getInt(webIdIndex);
		String name = cursor.getString(nameIndex);

		int workflowId 	    = cursor.getInt(workflowIdIndex);
		String webTableName = cursor.getString(webTableNameIndex);

		String rawFields					= cursor.getString(fieldsIndex);

		String documentsLastUpdateDate		= cursor.getString(documentsLastUpdateDateIndex);

		// @formatter:on

        // Generate the form fields
        List<FormViewContentData> formContents = null;
        try {
            JSONArray fieldsJSON = new JSONArray(rawFields);
            formContents = FormJSONParserV2.createFormContentsFromJSON(fieldsJSON);
        } catch (InvalidFormFieldException | JSONException e) {
            e.printStackTrace();
            // TODO: throw this exception up to the top layer
            FLLogger.e(TAG, "This form has invalid fields: " + e.getMessage());
            throw new DataAccessObjectException("Failed to parse form's fields: " + e.getMessage(), e);
        }

        Form form = new Form();

        form.setId(id);
        form.setWebId(webId);
        form.setName(name);

        form.setWorkflowId(workflowId);
        form.setWebTableName(webTableName);

        form.setActiveContents(formContents);

        form.setDocumentsLastUpdateDate(documentsLastUpdateDate);

        return form;
    }

    // </editor-fold>


}
