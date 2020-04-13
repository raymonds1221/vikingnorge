package no.incent.viking.pojo;

public class Traffic {
	private int roadId;
	private String roadName;
	private String areaName;
	private String latitude;
	private String longitude;
	private String optionalText;
	private String shortText;
	private String startTime;
	private String endTime;
	private long timestamp;
	
	public int getRoadId() {
		return roadId;
	}
	public void setRoadId(int roadId) {
		this.roadId = roadId;
	}
	public String getRoadName() {
		return roadName;
	}
	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getOptionalText() {
		return optionalText;
	}
	public void setOptionalText(String optionalText) {
		this.optionalText = optionalText;
	}
	public String getShortText() {
		return shortText;
	}
	public void setShortText(String shortText) {
		this.shortText = shortText;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
