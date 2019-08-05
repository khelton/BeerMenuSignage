package types;

import java.util.ArrayList;

public class BeerMenuItem implements IMenuItem {

	public int 		id = 0;
	public int 		beerNumber = 0;
	public String 	beerName = "Name";
	public String	beerNameColor = "#FFFFFF";
	public String 	company = "Company";
	//public String 	location = "Company";
	public String 	notes = "Notes";
	public String 	style = "Style";
	public double 	abv = 5;
	public int		ibu = 0;
	public double	srm = 0;
	public String	beerPourColor = "#FFDD00";
	//public double 	price1 = -1;
	//public int	 	price1Size = 16;
	//public double 	price2 = -1;
	//public int	 	price2Size = 20;
	
	public ArrayList<ItemPrice> priceList;
	
	public BeerMenuItem() {
		priceList = new ArrayList<ItemPrice>();
	}
	
	public BeerMenuItem(int id, String beerName, String beerNameColor, String company, String notes, 
			String style, double abv, int ibu, double srm, String beerPourColor) {
		
		this.id = 			id;
		this.beerName = 	beerName;
		this.beerNameColor =beerNameColor;
		this.company = 		company;
		this.notes = 		notes;
		this.style = 		style;
		this.abv = 			abv;
		this.ibu = 			ibu;
		this.srm =			srm;
		this.beerPourColor =beerPourColor;		
		
		
		priceList = new ArrayList<ItemPrice>();
	}
	/*
	public BeerMenuItem(int id, String beerName, String beerNameColor, String company, String notes, String style, double abv, int ibu, 
			double price1, int price1Size, double price2, int price2Size) {
		
		this.id = 			id;
		this.beerName = 	beerName;
		this.beerNameColor =beerNameColor;
		this.company = 		company;
		this.notes = 		notes;
		this.style = 		style;
		this.abv = 			abv;
		this.ibu = 			ibu;
		this.price1 = 		price1;
		this.price1Size = 	price1Size;
		this.price2 = 		price2;
		this.price2Size = 	price2Size;

		priceList = new ArrayList<ItemPrice>();
		
	}*/
	
	public void addPrice(ItemPrice item) {
		this.priceList.add(item); 
	}

	
	public ArrayList<ItemPrice> getPrices() {
		ArrayList<ItemPrice> validPrices = new ArrayList<ItemPrice>();
		
		for ( int i = 0 ; i < priceList.size() ; i++ ) {
			if (priceList.get(i).isValid())
				validPrices.add(priceList.get(i));
		}
		
		return validPrices;
	}
	
	/*
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
			this.price1 = 		price1;
			this.price1Size = 	price1Size;
			this.price2 = 		price2;
			this.price2Size = 	price2Size;
		} else {
			this.price1 = 		price2;
			this.price1Size = 	price2Size;
			this.price2 = 		price1;
			this.price2Size = 	price1Size;
		}
	}*/
	
	@Override
	public String toString() {
		return this.company + " - " + this.beerName;
	}
	
}
