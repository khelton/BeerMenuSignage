package sidefeature;

import java.io.File;

public class SideFeatureItem {

	public String title;
	public String imageSrc;
	public String description;
	
	public SideFeatureItem(String title, String imageSrc, String description) {
		this.title = title;
		this.imageSrc = imageSrc;
		this.description = description;
	}
	
	public File getImageFile() {
		return new File(imageSrc);
	}
}
