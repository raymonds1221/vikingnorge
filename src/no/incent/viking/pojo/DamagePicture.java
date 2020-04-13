package no.incent.viking.pojo;

import java.io.File;

public class DamagePicture {
	private String name;
	private File file;
	private File thumbFile;
	
	public DamagePicture() {}
	
	public DamagePicture(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public File getThumbFile() {
		return thumbFile;
	}
	public void setThumbFile(File thumbFile) {
		this.thumbFile = thumbFile;
	}
	
}
