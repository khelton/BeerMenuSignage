package image;

public class ImageType {

	public int id;
	public String name;
	
	
	public ImageType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
