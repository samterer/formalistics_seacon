package ph.com.gs3.formalistics.model.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.global.utilities.Serializer;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.api.default_impl.parsers.json.WorkflowActionJSONParser;
import ph.com.gs3.formalistics.model.tables.FormWorkflowObjectsTable;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowAction;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class WorkflowObjectsDAO extends DataAccessObject {

    public static final String TAG = WorkflowObjectsDAO.class.getSimpleName();

    public WorkflowObjectsDAO(Context context) {
        super(context);
    }

    public int insertWorkflowObject(WorkflowObject workflowObject) {

        try {
            open();
            ContentValues cv = createCVFromWorkflowObject(workflowObject);
            return (int) database.insertOrThrow(FormWorkflowObjectsTable.NAME, null, cv);
        } finally {
            close();
        }

    }

    public List<WorkflowObject> getFormWorkflowObjects(int workflowId) {

        List<WorkflowObject> workflowObjects = new ArrayList<>();

        try {
            open();

            String whereClause = FormWorkflowObjectsTable.COL_WORKFLOW_ID + "=?";
            String whereArgs[] = new String[]{Integer.toString(workflowId)};

            Cursor cursor = database.query(
                    FormWorkflowObjectsTable.NAME,
                    FormWorkflowObjectsTable.COLUMN_COLLECTION,
                    whereClause,
                    whereArgs,
                    null, null, null);

            cursor.moveToFirst();

            while(!cursor.isAfterLast()) {
                workflowObjects.add(cursorToWorkflowObject(cursor));
                cursor.moveToNext();
            }

        } finally {
            close();
        }

        return workflowObjects;

    }

    private ContentValues createCVFromWorkflowObject(WorkflowObject workflowObject) {

        ContentValues cv = new ContentValues();

        cv.put(FormWorkflowObjectsTable.COL_WEB_ID, workflowObject.getWebId());

        cv.put(FormWorkflowObjectsTable.COL_WORKFLOW_ID, workflowObject.getWorkflowId());
        cv.put(FormWorkflowObjectsTable.COL_FORM_ID, workflowObject.getWorkflowFormId());

        cv.put(FormWorkflowObjectsTable.COL_NODE_TYPE, workflowObject.getWorkflowObjectNodeType());
        cv.put(FormWorkflowObjectsTable.COL_NODE_ID, workflowObject.getNodeId());

        cv.put(FormWorkflowObjectsTable.COL_PROCESSOR_TYPE, workflowObject.getProcessorType());
        cv.put(FormWorkflowObjectsTable.COL_PROCESSOR, workflowObject.getProcessor());

        cv.put(FormWorkflowObjectsTable.COL_STATUS, workflowObject.getStatus());

        cv.put(FormWorkflowObjectsTable.COL_FIELDS_ENABLED, Serializer.serializeList(workflowObject.getFieldsEnabled()));
        cv.put(FormWorkflowObjectsTable.COL_FIELDS_REQUIRED, Serializer.serializeList(workflowObject.getFieldsRequired()));
        cv.put(FormWorkflowObjectsTable.COL_FIELDS_HIDDEN, Serializer.serializeList(workflowObject.getFieldsHidden()));

        try {
            JSONArray actionsJSONArray = WorkflowActionJSONParser.createJSONArrayFromList(workflowObject.getWorkflowActions());
            cv.put(FormWorkflowObjectsTable.COL_ACTIONS, actionsJSONArray.toString());
        } catch (JSONException e) {
            FLLogger.e(TAG, "Failed to serialize actions for workflow object " + workflowObject.getWebId());
            e.printStackTrace();
        }

        return cv;

    }

    private WorkflowObject cursorToWorkflowObject(Cursor cursor) {

        // @formatter:off
        int idCol               = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_ID);
        int webIdCol            = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_WEB_ID);
        int workflowIdCol       = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_WORKFLOW_ID);
        int workflowFormIdCol   = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_FORM_ID);

        int workflowObjectNodeTypeCol   = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_NODE_TYPE);
        int nodeIdCol                    = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_NODE_ID);

        int processorTypeCol    = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_PROCESSOR_TYPE);
        int processorCol        = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_PROCESSOR);

        int statusCol           = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_STATUS);
        int fieldsEnabledCol    = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_FIELDS_ENABLED);
        int fieldsRequiredCol   = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_FIELDS_REQUIRED);
        int fieldsHiddenCol     = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_FIELDS_HIDDEN);
        int actionsCol          = cursor.getColumnIndexOrThrow(FormWorkflowObjectsTable.COL_ACTIONS);
        // @formatter:on

        int id = cursor.getInt(idCol);
        int webId = cursor.getInt(webIdCol);
        int workflowId = cursor.getInt(workflowIdCol);
        int formId = cursor.getInt(workflowFormIdCol);

        int nodeType = cursor.getInt(workflowObjectNodeTypeCol);
        String nodeId = cursor.getString(nodeIdCol);

        int processorType = cursor.getInt(processorTypeCol);
        String processor = cursor.getString(processorCol);

        String status = cursor.getString(statusCol);
        List<String> fieldsEnabled = Serializer.unserializeList(cursor.getString(fieldsEnabledCol));
        List<String> fieldsRequired = Serializer.unserializeList(cursor.getString(fieldsRequiredCol));
        List<String> fieldsHidden = Serializer.unserializeList(cursor.getString(fieldsHiddenCol));

        String actionsRawString = cursor.getString(actionsCol);

        WorkflowObject workflowObject = new WorkflowObject();

        workflowObject.setId(id);
        workflowObject.setWebId(webId);
        workflowObject.setWorkflowId(workflowId);
        workflowObject.setWorkflowFormId(formId);

        workflowObject.setWorkflowObjectNodeType(nodeType);
        workflowObject.setNodeId(nodeId);

        workflowObject.setProcessorType(processorType);
        workflowObject.setProcessor(processor);

        workflowObject.setStatus(status);
        workflowObject.setFieldsEnabled(fieldsEnabled);
        workflowObject.setFieldsRequired(fieldsRequired);
        workflowObject.setFieldsHidden(fieldsHidden);

        workflowObject.setWorkflowActions(new ArrayList<WorkflowAction>());

        if (actionsRawString != null && !"".equals(actionsRawString)) {
            try {
                JSONArray actionsRawJSON = new JSONArray(actionsRawString);
                workflowObject.setWorkflowActions(WorkflowActionJSONParser.createFromJSON(actionsRawJSON));
            } catch (JSONException e) {
                FLLogger.w(TAG, "Failed to parse actions: " + e.getMessage());
            }
        }

        return workflowObject;

    }

}
