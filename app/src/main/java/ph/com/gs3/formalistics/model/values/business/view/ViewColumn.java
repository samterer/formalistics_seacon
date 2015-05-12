package ph.com.gs3.formalistics.model.values.business.view;

import java.io.Serializable;

public class ViewColumn implements Serializable {

	private static final long serialVersionUID = -8585412607438811303L;

	private String name;
	private String label;

	public ViewColumn() {}

	public ViewColumn(String name, String label) {
		this.name = name;
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
