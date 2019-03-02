package types;

import java.util.ArrayList;

public class BeerMenuItem {

	public int 		id;
	public String 	beerName;
	public String 	company;
	public String 	notes;
	public String 	style;
	public String 	abv;
	public String	ibu;
	public String 	price1;
	public String 	price1Size;
	public String 	price2;
	public String 	price2Size;
	
	public ArrayList<ItemPrice> priceList;
	
	
	public BeerMenuItem(int id, String beerName, String company, String notes, String style, double abv, int ibu) {
		
		this.id = 			id;
		this.beerName = 	beerName;
		this.company = 		company;
		this.notes = 		notes;
		this.style = 		style;
		this.abv = 			String.valueOf(abv);
		this.ibu = 			String.valueOf(ibu);
		this.price1 = 		"NA";
		this.price1Size = 	"NA";
		this.price2 = 		"NA";
		this.price2Size = 	"NA";
		
		priceList = new ArrayList<ItemPrice>();
	}
	
	public BeerMenuItem(int id, String beerName, String company, String notes, String style, double abv, int ibu, 
			double price1, int price1Size, double price2, int price2Size) {
		
		this.id = 			id;
		this.beerName = 	beerName;
		this.company = 		company;
		this.notes = 		notes;
		this.style = 		style;
		this.abv = 			String.valueOf(abv);
		this.ibu = 			String.valueOf(ibu);
		this.price1 = 		String.valueOf(price1);
		this.price1Size = 	String.valueOf(price1Size);
		this.price2 = 		String.valueOf(price2);
		this.price2Size = 	String.valueOf(price2Size);

		priceList = new ArrayList<ItemPrice>();
		
	}
	
	public void addPrice(ItemPrice item) {
		this.priceList.add(item); 
	}
	
	public void resolvePrices() {
		if (this.priceList != null && this.priceList.size() > 1) {
			resolvePrices(priceList.get(0), priceList.get(1));
		}
	}
	public void resolvePrices(ArrayList<ItemPrice> _priceList) {
		if (_priceList != null && _priceList.size() > 1) {
			this.priceList = _priceList;
			resolvePrices(priceList.get(0), priceList.get(1));
		}
	}
	
	public void resolvePrices(ItemPrice i1, ItemPrice i2) {
		resolvePrices(i1.price, i1.size, i2.price, i2.size);
	}
	
	public void resolvePrices(double price1, int price1Size, double price2, int price2Size) {
		if (price1Size < price2Size) {
			this.price1 = 		String.valueOf(price1);
			this.price1Size = 	String.valueOf(price1Size);
			this.price2 = 		String.valueOf(price2);
			this.price2Size = 	String.valueOf(price2Size);
		} else {
			this.price1 = 		String.valueOf(price2);
			this.price1Size = 	String.valueOf(price2Size);
			this.price2 = 		String.valueOf(price1);
			this.price2Size = 	String.valueOf(price1Size);
		}
	}
	
	@Override
	public String toString() {
		return this.company + " - " + this.beerName;
	}
	
}
