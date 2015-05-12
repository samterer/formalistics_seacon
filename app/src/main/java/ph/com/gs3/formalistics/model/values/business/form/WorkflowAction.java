package ph.com.gs3.formalistics.model.values.business.form;

import java.io.Serializable;

/**
 * Created by Ervinne on 4/18/2015.
 */
public class WorkflowAction implements Serializable {

    private String label;
    private String nodeId;

    //<editor-fold desc="Getters & Setters">
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    //</editor-fold>
}
