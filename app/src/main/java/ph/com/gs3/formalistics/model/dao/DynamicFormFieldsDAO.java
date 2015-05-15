package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.application.SearchCondition;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.FormFieldData;

/**
 * Created by Ervinne on 4/19/2015.
 */
public class DynamicFormFieldsDAO extends DataAccessObject {

    public static final String TAG = DynamicFormFieldsDAO.class.getSimpleName();

    public static final String FORM_TABLE_COL_ID = "_id";
    public static final String FORM_TABLE_COL_DOCUMENT_ID = "Document_Id";
    public static final String FORM_TABLE_COL_OUTGOING_ACTION_ID = "Outgoing_Action_Id";

    public static final String[] reservedColumns = {
            FORM_TABLE_COL_ID, FORM_TABLE_COL_DOCUMENT_ID, FORM_TABLE_COL_OUTGOING_ACTION_ID
    };

    public enum FieldValuesType {
        DOCUMENT, OUTGOING_ACTION
    }

    public DynamicFormFieldsDAO(Context context) {
        super(context);
    }

    public DynamicFormFieldsDAO(Context context, SQLiteDatabase preOpenedDatabaseWithTransaction) {
        super(context, preOpenedDatabaseWithTransaction);
    }

    public List<JSONObject> search(Form form, List<String> resultFieldNames, int ownerId) throws JSONException {

        return search(form, resultFieldNames, ownerId, null);

    }

    public List<JSONObject> search(Form form, List<String> resultFieldNames, int ownerId, List<SearchCondition> conditions)
            throws JSONException {
        return search(form, resultFieldNames, ownerId, conditions, "");
    }

    public List<JSONObject> search(Form form, List<String> resultFieldNames, int ownerId, List<SearchCondition> conditions, String rawConditions)
            throws JSONException {

        List<JSONObject> results = new ArrayList<>();

        // @formatter:off
		List<String> defaultFields = new ArrayList<>(
		        Arrays.asList(new String[]
		        		{ "fields.document_id, fields.outgoing_action_id, web_id, tracking_number, status, date_created, date_updated" }
		        ));
		// @formatter:on

        // Merge the default fields with the required result field names
        Set<String> resultFieldNamesSet = new HashSet<>(defaultFields);
        resultFieldNamesSet.addAll(resultFieldNames);

        List<String> mergedResultFields = new ArrayList<>(resultFieldNamesSet);
        String fieldSelectionString = Serializer.serializeList(mergedResultFields);

        /*
        Sample:
        SELECT * FROM Form_1_Developer_Profile_Fields fields
            LEFT JOIN User_Documents ud ON fields.Document_id = ud.document_id
            LEFT JOIN Documents doc ON fields.Document_id = doc._id
            LEFT JOIN Outgoing_Actions oa ON fields.Outgoing_Action_id = oa._id
        WHERE ud.user_id = 1 OR oa.issued_by_user_id = 1
         */

        // @formatter:off
//        String query = "SELECT %s FROM %s fields " +
//                "LEFT JOIN User_Documents ud ON fields.Document_id = ud.document_id " +
//                "LEFT JOIN Documents doc ON fields.Document_id = doc._id " +
//                "LEFT JOIN Outgoing_Actions oa ON fields.Outgoing_Action_id = oa._id " +
//            "WHERE (ud.user_id = %d OR oa.issued_by_user_id = %d)";

        String query = "SELECT %s FROM %s fields " +
                "LEFT JOIN Documents doc ON fields.Document_id = doc._id " +
                "LEFT JOIN Outgoing_Actions oa ON fields.Outgoing_Action_id = oa._id ";
        // @formatter:on

//        query = String.format(query, fieldSelectionString, form.getGeneratedFormTableName(), ownerId, ownerId);
        query = String.format(query, fieldSelectionString, form.getGeneratedFormTableName());
        if (conditions != null) {
            query += " WHERE " + generateWhereClauseFromConditions(conditions);
        }

        if (rawConditions != null && !"".equals(rawConditions.trim())) {
            if (conditions != null) {
                query += " AND " + rawConditions;
            } else {
                query += " WHERE " + rawConditions;
            }
        }

        FLLogger.d(TAG, TAG + ".search: " + query);

        try {
            open();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                // Throws JSONException
                JSONObject json = cursorToJSONObject(cursor);
                results.add(json);

                cursor.moveToNext();
            }

            cursor.close();
        } finally {
            close();
        }

        return results;

    }

    protected List<String> getOldFieldsIdFromDocumentsTable(Form form) {

        List<String> fields = new ArrayList<>();

        String query = "PRAGMA table_info(" + form.getGeneratedFormTableName() + ");";

        try {
            open();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            List<String> reservedColumnList = Arrays.asList(reservedColumns);

            while (!cursor.isAfterLast()) {
                int nameColIndex = cursor.getColumnIndexOrThrow("name");    // column name
                String columnName = cursor.getString(nameColIndex);

                if (!reservedColumnList.contains(columnName)) {
                    fields.add(columnName);
                }

                cursor.moveToNext();
            }
        } finally {
            close();
        }

        return fields;

    }

    public JSONObject getDocumentFieldValues(
            int documentId, String tableName, List<FormFieldData> formFields) {
        return getFieldValues(documentId, tableName, formFields, FieldValuesType.DOCUMENT);
    }

    public JSONObject getOutgoingActionFieldValues(
            int outgoingActionId, String tableName, List<FormFieldData> formFields) {
        return getFieldValues(outgoingActionId, tableName, formFields, FieldValuesType.OUTGOING_ACTION);
    }

    public JSONObject getFieldValues(
            int id, String tableName, List<FormFieldData> formFields, FieldValuesType fieldValuesType) {

        JSONObject fieldValues = new JSONObject();

        int fieldCount = formFields.size();
        String[] columns = new String[fieldCount];

        for (int i = 0; i < fieldCount; i++) {
            columns[i] = formFields.get(i).getName();
        }

        String whereClause;
        if (fieldValuesType == FieldValuesType.DOCUMENT) {
            whereClause = "Document_id = ?";
        } else {
            whereClause = "Outgoing_Actions_id = ?";
        }

        String[] whereArgs = {Integer.toString(id)};

        try {
            open();
            Cursor cursor = database.query(tableName, columns, whereClause, whereArgs, null, null, null);
            cursor.moveToFirst();

            if (!cursor.isAfterLast()) {
                for (FormFieldData formField : formFields) {
                    int columnIndex = cursor.getColumnIndexOrThrow(formField.getName());
                    fieldValues.put(formField.getName(), cursor.getString(columnIndex));
                }
            }

            cursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return fieldValues;

    }

    //<editor-fold desc="Insert, Update & Delete Methods">
    public JSONObject insertDocumentFieldValues(
            int documentId, String tableName, JSONObject fieldValuesJSON, List<FormFieldData> formFields) throws JSONException {
        return insertFieldValues(documentId, 0, tableName, fieldValuesJSON, formFields);
    }

    public JSONObject insertOutgoingActionFieldValues(
            int outgoingActionsId, String tableName, JSONObject fieldValuesJSON, List<FormFieldData> formFields) throws JSONException {
        return insertFieldValues(0, outgoingActionsId, tableName, fieldValuesJSON, formFields);
    }

    public JSONObject insertFieldValues(
            int documentId, int outgoingActionId, String tableName, JSONObject fieldValuesJSON, List<FormFieldData> formFields) {

        ContentValues cv = createCVFromJSON(fieldValuesJSON, formFields);
        cv.put("Document_id", documentId);
        cv.put("Outgoing_Action_id", outgoingActionId);

        try {
            open();
            long insertId = database.insert(tableName, null, cv);

            if (insertId > 0) {
                // Throws JSONException
                return getDocumentFieldValues((int) insertId, tableName, formFields);
            }
        } finally {
            close();
        }
        return null;

    }

    public JSONObject updateDocumentFieldValues(
            int documentId, String tableName, JSONObject fieldValuesJSON, List<FormFieldData> formFields) {

        ContentValues cv = createCVFromJSON(fieldValuesJSON, formFields);

        String whereClause = "Document_id = ?";
        String[] whereArgs = {Integer.toString(documentId)};

        try {
            open();
            int affectedRows = database.update(tableName, cv, whereClause, whereArgs);

            if (affectedRows >= 1) {
                // Throws JSONException
                return getDocumentFieldValues(documentId, tableName, formFields);
            }
        } finally {
            close();
        }

        return null;

    }

    public JSONObject updateOutgoingActionFieldValues(
            int outgoingActionId, String tableName, JSONObject fieldValuesJSON, List<FormFieldData> formFields) {

        ContentValues cv = createCVFromJSON(fieldValuesJSON, formFields);

        String whereClause = "Outgoing_Action_id = ?";
        String[] whereArgs = {Integer.toString(outgoingActionId)};

        try {
            open();
            int affectedRows = database.update(tableName, cv, whereClause, whereArgs);

            if (affectedRows >= 1) {
                // Throws JSONException
                return getOutgoingActionFieldValues(outgoingActionId, tableName, formFields);
            }
        } finally {
            close();
        }

        return null;

    }

    public void deleteOutgoingActionFieldValues(int outgoingActionId, String tableName) {
        String whereClause = "Outgoing_Action_Id = ?";
        String[] whereArgs = {Integer.toString(outgoingActionId)};

        try {
            open();
            int affectedRows = database.delete(tableName, whereClause, whereArgs);

            if (affectedRows > 1) {
                FLLogger.w(TAG,
                        "There are more than one record deleted when the app deleted outgoing action id: "
                                + outgoingActionId);
            }

            if (affectedRows <= 0) {
                FLLogger.e(TAG, "No deleted field values with id = " + outgoingActionId);
            }
        } finally {
            close();
        }
    }

    public void deleteDocumentFieldValues(int documentId, String tableName) throws DataAccessObjectException {
        String whereClause = "Document_Id = ?";
        String[] whereArgs = {Integer.toString(documentId)};

        try {
            open();
            int affectedRows = database.delete(tableName, whereClause, whereArgs);

            if (affectedRows > 1) {
                FLLogger.w(TAG,
                        "There are more than one record deleted when the app deleted document field values id: " + documentId);
            }

            if (affectedRows < 1) {
                throw new DataAccessObjectException("Failed to delete document with id " + documentId);
            }
        } finally {
            close();
        }
    }

    //</editor-fold>


    // <editor-fold desc="Table Creation & Updating">

    public void updateFormTable(Form form) {

        List<String> oldFields = getOldFieldsIdFromDocumentsTable(form);
        List<FormFieldData> newFields = getNewFields(form, oldFields);

        if (newFields.size() > 0) {
            String tableName = form.getGeneratedFormTableName();

            for (FormFieldData field : newFields) {
                String query = "ALTER TABLE " + tableName + " ADD COLUMN " + field.getName()
                        + " TEXT;";
                try {
                    open();
                    database.execSQL(query);
                } catch (SQLException e) {
                    FLLogger.w(TAG, "Failed altering table: " + tableName + ", " + e.getMessage());
                } finally {
                    close();
                }
            }

        }

    }

    public void createFormTable(Form form) {

        String formTableName = form.getGeneratedFormTableName();
        String query = "CREATE TABLE " + formTableName + " ("
                + FORM_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FORM_TABLE_COL_DOCUMENT_ID + " INTEGER DEFAULT 0, "
                + FORM_TABLE_COL_OUTGOING_ACTION_ID + " INTEGER DEFAULT 0, ";

        String parameterizedQueryColumns = "";
        List<FormFieldData> formFields = form.getActiveFields();

        for (FormFieldData field : formFields) {
            parameterizedQueryColumns += field.getName() + " TEXT, ";
        }

        // Trim the trailing comma
        parameterizedQueryColumns = parameterizedQueryColumns.substring(0,
                parameterizedQueryColumns.length() - 2);

        query += parameterizedQueryColumns + ");";

        try {
            open();
            database.execSQL(query);
        } finally {
            close();
        }

    }

    /**
     * Utility method for comparing old fields to the new fields in the newForm parameter.
     *
     * @param newForm
     * @param oldFormFieldIdList
     * @return
     */
    protected List<FormFieldData> getNewFields(Form newForm, List<String> oldFormFieldIdList) {
        List<FormFieldData> newFields = newForm.getActiveFields();

        for (Iterator<FormFieldData> newFieldsIterator = newFields.listIterator(); newFieldsIterator
                .hasNext(); ) {
            FormFieldData newField = newFieldsIterator.next();

            for (Iterator<String> oldFieldsIterator = oldFormFieldIdList.listIterator(); oldFieldsIterator
                    .hasNext(); ) {
                String oldField = oldFieldsIterator.next();
                if (oldField.equals(newField.getName())) {
                    newFieldsIterator.remove();
                    oldFieldsIterator.remove();
                    break;
                }
            }

        }

        return newFields;
    }

    // </editor-fold>

    protected static ContentValues createCVFromJSON(JSONObject json, List<FormFieldData> formFields) {

        ContentValues cv = new ContentValues();

        for (FormFieldData formField : formFields) {
            String key = formField.getName();
            if (json.has(key)) {
                try {
                    cv.put(key, json.getString(key));
                } catch (JSONException e) {
                    // This should not happen since we just checked the key
                    FLLogger.w(TAG, "Error finding key " + key);
                }
            }
        }

        return cv;
    }

    protected static ContentValues createCVFromMap(Map<String, String> map) {

        ContentValues cv = new ContentValues();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            cv.put(entry.getKey(), entry.getValue());
        }

        return cv;

    }

    public static JSONObject cursorToJSONObject(Cursor cursor) throws JSONException {

        JSONObject json = new JSONObject();

        int columnCount = cursor.getColumnCount();
        for (int i = 0; i < columnCount; i++) {

            String fieldName = cursor.getColumnName(i);
            String fieldValue = cursor.getString(i);

            if (fieldValue != null) {
                json.put(fieldName, fieldValue);
            } else {
                json.put(fieldName, "");
            }

        }

        return json;

    }

}
