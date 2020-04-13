package no.incent.viking.pojo;

public class FirstAidItem {
	private int imageResourceId;
	private String description;
	
	public FirstAidItem() {}
	
	public FirstAidItem(int imageResourceId, String description) {
		this.imageResourceId = imageResourceId;
		this.description = description;
	}
	
	public int getImageResourceId() {
		return imageResourceId;
	}
	public void setImageResourceId(int imageResourceId) {
		this.imageResourceId = imageResourceId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
