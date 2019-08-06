package image;

public class ImageSourceType {

	public int id;
	public String name;
	
	
	public ImageSourceType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
