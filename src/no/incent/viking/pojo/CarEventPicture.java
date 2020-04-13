package no.incent.viking.pojo;

import java.io.File;

public class CarEventPicture {
	private int uid;
	private int ownerId;
	private int eventId;
	private String name;
	private String filename;
	private String fileType;
	private File imageFile;
	private File imageFileThumb;
	private String path;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(int ownerId) {
		this.ownerId = ownerId;
	}
	public int getEventId() {
		return eventId;
	}
	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public File getImageFile() {
		return imageFile;
	}
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
	public File getImageFileThumb() {
		return imageFileThumb;
	}
	public void setImageFileThumb(File imageFileThumb) {
		this.imageFileThumb = imageFileThumb;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}
