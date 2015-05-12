package ph.com.gs3.formalistics.model.values.application;

public class NavigationDrawerItem {

	private int id;
	private int imageResourceId;
	private String label;

	public NavigationDrawerItem() {}

	public NavigationDrawerItem(int id, int imageResourceId, String label) {

		this.id = id;
		this.imageResourceId = imageResourceId;
		this.label = label;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getImageResourceId() {
		return imageResourceId;
	}

	public void setImageResourceId(int imageResourceId) {
		this.imageResourceId = imageResourceId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
