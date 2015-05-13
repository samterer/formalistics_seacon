package ph.com.gs3.formalistics.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.dao.FormTableReferenceDAO;
import ph.com.gs3.formalistics.model.tables.CommentsTable;
import ph.com.gs3.formalistics.model.tables.CompaniesTable;
import ph.com.gs3.formalistics.model.tables.DocumentsTable;
import ph.com.gs3.formalistics.model.tables.FieldOutgoingFileReferenceTable;
import ph.com.gs3.formalistics.model.tables.FilesTable;
import ph.com.gs3.formalistics.model.tables.FormTableReferenceTable;
import ph.com.gs3.formalistics.model.tables.FormWorkflowObjectsTable;
import ph.com.gs3.formalistics.model.tables.FormsTable;
import ph.com.gs3.formalistics.model.tables.OutgoingActionsTable;
import ph.com.gs3.formalistics.model.tables.UserDocumentsTable;
import ph.com.gs3.formalistics.model.tables.UsersTable;

public class DefaultDatabaseHelper extends SQLiteOpenHelper {

    public static final String TAG = DefaultDatabaseHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "formalistics.db";
    private static final int DATABASE_VERSION = 1;

    public DefaultDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CompaniesTable.CREATION_QUERY);
        db.execSQL(UsersTable.CREATION_QUERY);
        db.execSQL(FormsTable.CREATION_QUERY);
        db.execSQL(FormWorkflowObjectsTable.CREATION_QUERY);
        db.execSQL(DocumentsTable.CREATION_QUERY);
        db.execSQL(UserDocumentsTable.CREATION_QUERY);
        db.execSQL(CommentsTable.CREATION_QUERY);
        db.execSQL(OutgoingActionsTable.CREATION_QUERY);
        db.execSQL(FilesTable.CREATION_QUERY);
        db.execSQL(FieldOutgoingFileReferenceTable.CREATION_QUERY);

        db.execSQL(FormTableReferenceTable.CREATION_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        FLLogger.d(TAG, "Version Update: " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + CompaniesTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UsersTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FormsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FormWorkflowObjectsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DocumentsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserDocumentsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CommentsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OutgoingActionsTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FilesTable.NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FieldOutgoingFileReferenceTable.NAME);

        FLLogger.d(TAG, "Cleanup done: " + CompaniesTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + UsersTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + FormsTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + FormWorkflowObjectsTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + DocumentsTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + UserDocumentsTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + CommentsTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + OutgoingActionsTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + FilesTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + FieldOutgoingFileReferenceTable.NAME);

        try {
            FormTableReferenceDAO.dropFormTables(db);
        } catch (SQLiteException e) {
            FLLogger.w(TAG, e.getMessage());
        }

        db.execSQL("DROP TABLE IF EXISTS " + FormTableReferenceTable.NAME);
        FLLogger.d(TAG, "Cleanup done: " + FormTableReferenceTable.NAME);

        onCreate(db);

    }

}
