package image;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class ItemImage {
	
	public int id;
	public int beerId;
	public int rank;
	public String imageSrc;
	public ImageType imageType;
	public ImageSourceType imageSourceType;
	public int enabled;
	
	public ItemImage(int id, int beerId, int rank, String imageSrc, 
			ImageType imageType, ImageSourceType imageSourceType, int enabled) {
		this.id = id;
		this.beerId = beerId;
		this.rank = rank;
		this.imageSrc = imageSrc;
		this.imageType = imageType;
		this.imageSourceType = imageSourceType;
		this.enabled = enabled;
	}
	
	public boolean isValid() {
		return (enabled == 1);
	}
	

}
