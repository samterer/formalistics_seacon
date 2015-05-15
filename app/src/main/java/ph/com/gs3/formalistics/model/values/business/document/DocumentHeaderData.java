package ph.com.gs3.formalistics.model.values.business.document;

import java.io.Serializable;
import java.util.List;

import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.constants.WorkflowObjectNodeType;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.model.values.business.form.Form;
import ph.com.gs3.formalistics.model.values.business.form.WorkflowObject;

public class DocumentHeaderData implements Serializable{

    public static final String TAG = DocumentHeaderData.class.getSimpleName();

    private int webId;
    private int formWebId;
    private String trackingNumber;

    private String dateCreated;
    private String dateUpdated;

    private WorkflowObject workflowObject;

    private int starMarkInt;

    public static DocumentHeaderData createFromForm(Form form) {

        DocumentHeaderData data = new DocumentHeaderData();

        data.setWebId(0);
        data.setTrackingNumber(null);
        data.setFormWebId(form.getWebId());

        data.setDateCreated(DateUtilities.getServerFormattedCurrentDateTime());
        data.setDateUpdated(DateUtilities.getServerFormattedCurrentDateTime());

        WorkflowObject workflowObject = findStartingWorkflowObject(form);

        if (workflowObject == null) {
            // FIXME - change this to a checked exception
            throw new IllegalStateException("The form does not have a starting point workflow object, re synchronizing may solve this issue.");
        }

        data.setWorkflowObject(workflowObject);
        data.setStarMarkInt(StarMark.UNSTARRED);

        return data;

    }

    public static DocumentHeaderData createFromDocument(Document document, Form form) {

        DocumentHeaderData data = new DocumentHeaderData();

        data.setWebId(document.getWebId());
        data.setTrackingNumber(document.getTrackingNumber());
        data.setFormWebId(form.getWebId());

        data.setDateCreated(document.getDateCreated());
        data.setDateUpdated(document.getDateUpdated());

        WorkflowObject workflowObject = findWorkflowNode(form, document.getWorkflowNodeId());

        if (workflowObject == null) {
            // FIXME - change this to a checked exception
            throw new IllegalStateException("The form does not have a workflow object of the node " + document.getWorkflowNodeId() + ", re synchronizing may solve this issue.");
        }

        data.setWorkflowObject(workflowObject);
        data.setStarMarkInt(document.getStarMark());

        return data;

    }

    public static DocumentHeaderData createFromOutgoingAction(OutgoingAction outgoingAction, Form form) {

        Document document = outgoingAction.getDocument();
        DocumentHeaderData data = null;

        if (document != null && document.getWebId() != 0) {
            data = createFromDocument(document, form);
        } else {
            data = createFromForm(form);
        }

        // Empty actions and field properties
        WorkflowObject emptyWorkflow = WorkflowObject.createEmptyWorkflow();

        emptyWorkflow.setStatus("Outgoing Action");
        data.setWorkflowObject(emptyWorkflow);

        return data;

    }

    public static WorkflowObject findStartingWorkflowObject(Form form) {

        List<WorkflowObject> workflowObjects = form.getWorkflowObjects();
        for (WorkflowObject workflowObject : workflowObjects) {
            if (workflowObject.getWorkflowObjectNodeType() == WorkflowObjectNodeType.START_NODE) {
                return workflowObject;
            }
        }

        // not found
        return null;
    }

    public static WorkflowObject findWorkflowNode(Form form, String nodeId) {

        List<WorkflowObject> workflowObjects = form.getWorkflowObjects();
        for (WorkflowObject workflowObject : workflowObjects) {
            if (nodeId.equalsIgnoreCase(workflowObject.getNodeId())) {
                return workflowObject;
            }
        }

        // not found
        return null;
    }

    // ==============================================================
    //<editor-fold desc="Getters & Setters">
    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public int getFormWebId() {
        return formWebId;
    }

    public void setFormWebId(int formWebId) {
        this.formWebId = formWebId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public WorkflowObject getWorkflowObject() {
        return workflowObject;
    }

    public void setWorkflowObject(WorkflowObject workflowObject) {
        this.workflowObject = workflowObject;
    }

    public int getStarMarkInt() {
        return starMarkInt;
    }

    public void setStarMarkInt(int starMarkInt) {
        this.starMarkInt = starMarkInt;
    }

//</editor-fold>

}
