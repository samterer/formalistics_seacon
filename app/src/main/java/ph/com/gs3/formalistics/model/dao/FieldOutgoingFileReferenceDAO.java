package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import ph.com.gs3.formalistics.model.tables.FieldOutgoingFileReferenceTable;
import ph.com.gs3.formalistics.model.tables.FilesTable;
import ph.com.gs3.formalistics.model.values.application.FieldOutgoingFileReference;

/**
 * Created by Ervinne on 4/23/2015.
 */
public class FieldOutgoingFileReferenceDAO extends DataAccessObject {
    public FieldOutgoingFileReferenceDAO(Context context) {
        super(context);
    }

    public void insertFieldOutgoingFileReference(FieldOutgoingFileReference fieldOutgoingFileReference) {

        ContentValues cv = createCVFromFieldOutgoingFileReference(fieldOutgoingFileReference);
        try {
            open();
            database.insert(FieldOutgoingFileReferenceTable.NAME, null, cv);
        } finally {
            close();
        }

    }

    public String findFieldFileURL(int outgoingAtionId, String fieldName) {

        String query =
                String.format("SELECT file.%s FROM %s file " +
                                "LEFT JOIN %s reference ON file.%s = reference.%s ",
                        FilesTable.COL_REMOTE_URL,
                        FilesTable.NAME,
                        FieldOutgoingFileReferenceTable.NAME,
                        FilesTable.COL_ID,
                        FieldOutgoingFileReferenceTable.COL_OUTGOING_FILE_ID
                );
        String whereClause = String.format("WHERE " +
                        FieldOutgoingFileReferenceTable.COL_OUTGOING_ACTION_ID + "=%d AND " +
                        FieldOutgoingFileReferenceTable.COL_FIELD_NAME + "='%s'",
                outgoingAtionId,
                fieldName
        );

        query = query + whereClause;

        String url = null;

        try {
            open();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            if (!cursor.isAfterLast()) {
                url = cursor.getString(cursor.getColumnIndexOrThrow(FilesTable.COL_REMOTE_URL));
            }
            cursor.close();
        } finally {
            close();
        }

        return url;

    }

    private ContentValues createCVFromFieldOutgoingFileReference(FieldOutgoingFileReference fieldOutgoingFileReference) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(FieldOutgoingFileReferenceTable.COL_OUTGOING_FILE_ID, fieldOutgoingFileReference.getOutgoingFileId());
        contentValues.put(FieldOutgoingFileReferenceTable.COL_FORM_ID, fieldOutgoingFileReference.getFormId());
        contentValues.put(FieldOutgoingFileReferenceTable.COL_OUTGOING_ACTION_ID, fieldOutgoingFileReference.getOutgoingActionId());
        contentValues.put(FieldOutgoingFileReferenceTable.COL_FIELD_NAME, fieldOutgoingFileReference.getFieldName());

        return contentValues;

    }

}
