package types;

public class ItemPriceType {
	
	public int id;
	public String name;
	
	
	public ItemPriceType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
