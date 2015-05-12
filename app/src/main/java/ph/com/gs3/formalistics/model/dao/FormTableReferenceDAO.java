package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.tables.FormTableReferenceTable;

/**
 * Created by Ervinne on 4/7/2015.
 */
public class FormTableReferenceDAO extends DataAccessObject {

    public static final String TAG = FormTableReferenceDAO.class.getSimpleName();

    public FormTableReferenceDAO(Context context) {
        super(context);
    }

    public void saveTableName(int formId, String tableName) throws SQLiteException {

        ContentValues cv = createCVFromTable(formId, tableName);

        long insertId = database.insert(FormTableReferenceTable.NAME, null, cv);

        if (insertId <= 0) {
            throw new SQLiteException("Failed to insert " + tableName + " to references");
        }

    }

    protected ContentValues createCVFromTable(int formId, String tableName) {

        ContentValues cv = new ContentValues();

        cv.put(FormTableReferenceTable.COL_ID, formId);
        cv.put(FormTableReferenceTable.COL_TABLE_NAME, tableName);

        return cv;

    }

    public static final void dropFormTables(SQLiteDatabase db) {
        Cursor cursor = db.query(FormTableReferenceTable.NAME,
                FormTableReferenceTable.COLUMN_COLLECTION, null, null, null, null, null);

        cursor.moveToFirst();

        List<String> tableNames = new ArrayList<>();

        while (!cursor.isAfterLast()) {

            int tableNameIndex = cursor
                    .getColumnIndexOrThrow(FormTableReferenceTable.COL_TABLE_NAME);
            String tableName = cursor.getString(tableNameIndex);

            tableNames.add(tableName);

            cursor.moveToNext();
        }

        cursor.close();

        for (String tableName : tableNames) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName);
            FLLogger.d(TAG, "Cleanup done: " + tableName);
        }
    }

}
