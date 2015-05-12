package ph.com.gs3.formalistics.model.values.business.document;

import java.io.Serializable;

public class DocumentAction implements Serializable {

	private String action;
	private String label;

	public DocumentAction() {}

	public DocumentAction(String label, String action) {
		this.label = label;
		this.action = action;
	}

    @Override
    public String toString() {
        return label;
    }

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
