package types;

public class ItemPrice {
	
	public int id;
	public int beerId;
	public int rank;
	public double price;
	public int size;
	public ItemPriceType priceType;
	public int enabled;
	//public int priceTypeId;
	//public String priceTypeString;
	
	public ItemPrice(int id, int beerId, int rank, double price, int size, ItemPriceType priceType, int enabled) {
		this.id = id;
		this.beerId = beerId;
		this.rank = rank;
		this.price = price;
		this.size = size;
		this.priceType = priceType;
		this.enabled = enabled;
		//this.priceTypeId = priceTypeId;
		//this.priceTypeString = priceTypeString;
	}

}
