package types;

public class ItemPrice {
	
	public int id;
	public int beerId;
	public double price;
	public int size;
	
	public ItemPrice(int id, int beerId, double price, int size) {
		this.id = id;
		this.beerId = beerId;
		this.price = price;
		this.size = size;
	}

}