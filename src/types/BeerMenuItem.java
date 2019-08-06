package types;

import java.util.ArrayList;

import image.ItemImage;

public class BeerMenuItem implements IMenuItem {

	public int 		id = 0;
	public int 		beerNumber = 0;
	public String 	beerName = "Name";
	public String	beerNameColor = "#FFFFFF";
	public String 	company = "Company";
	public String 	notes = "Notes";
	public String 	style = "Style";
	public double 	abv = 5;
	public int		ibu = 0;
	public double	srm = 0;
	public String	beerPourColor = "#FFDD00";
	
	public ArrayList<ItemPrice> priceList;
	public ArrayList<ItemImage> imageList;
	
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
		imageList = new ArrayList<ItemImage>();
	}
	
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
	
	@Override
	public String toString() {
		return this.company + " - " + this.beerName;
	}
	
}
