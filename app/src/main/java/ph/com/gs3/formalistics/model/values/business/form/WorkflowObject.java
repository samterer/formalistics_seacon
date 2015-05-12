package ph.com.gs3.formalistics.model.values.business.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ervinne on 4/17/2015.
 */
public class WorkflowObject implements Serializable {

    private int id;
    private int webId;
    private int workflowId;
    private int workflowFormId;

    private int workflowObjectNodeType;
    private String nodeId;

    private int processorType;
    private String processor;

    private String status;
    private List<String> fieldsEnabled;
    private List<String> fieldsRequired;
    private List<String> fieldsHidden;
    private List<WorkflowAction> workflowActions;

    public static WorkflowObject createEmptyWorkflow() {

        WorkflowObject workflowObject = new WorkflowObject();

        workflowObject.setFieldsEnabled(new ArrayList<String>());
        workflowObject.setFieldsRequired(new ArrayList<String>());
        workflowObject.setFieldsHidden(new ArrayList<String>());
        workflowObject.setWorkflowActions(new ArrayList<WorkflowAction>());

        return workflowObject;

    }

    //<editor-fold desc="Getters & Setters">
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWebId() {
        return webId;
    }

    public void setWebId(int webId) {
        this.webId = webId;
    }

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public int getWorkflowFormId() {
        return workflowFormId;
    }

    public void setWorkflowFormId(int workflowFormId) {
        this.workflowFormId = workflowFormId;
    }

    public int getWorkflowObjectNodeType() {
        return workflowObjectNodeType;
    }

    public void setWorkflowObjectNodeType(int workflowObjectNodeType) {
        this.workflowObjectNodeType = workflowObjectNodeType;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getProcessorType() {
        return processorType;
    }

    public void setProcessorType(int processorType) {
        this.processorType = processorType;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getFieldsEnabled() {
        return fieldsEnabled;
    }

    public void setFieldsEnabled(List<String> fieldsEnabled) {
        this.fieldsEnabled = fieldsEnabled;
    }

    public List<String> getFieldsRequired() {
        return fieldsRequired;
    }

    public void setFieldsRequired(List<String> fieldsRequired) {
        this.fieldsRequired = fieldsRequired;
    }

    public List<String> getFieldsHidden() {
        return fieldsHidden;
    }

    public void setFieldsHidden(List<String> fieldsHidden) {
        this.fieldsHidden = fieldsHidden;
    }

    public List<WorkflowAction> getWorkflowActions() {
        return workflowActions;
    }

    public void setWorkflowActions(List<WorkflowAction> workflowActions) {
        this.workflowActions = workflowActions;
    }

//</editor-fold>
}
