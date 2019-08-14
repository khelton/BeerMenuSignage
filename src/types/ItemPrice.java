package types;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class ItemPrice {
	
	public int id;
	public int beerId;
	public int rank;
	public double price;
	public int size;
	public ItemPriceType priceType;
	//public String scheduleTimeStart;
	//public String scheduleTimeEnd;
	//public String scheduleDays;
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
	
	public boolean isValid() {
		boolean isEnabled = (enabled == 1);
		boolean typeEnabled = (priceType.enabled == 1);
		boolean dayValid = priceType.schedule.checkDay();
		boolean timeValid = priceType.schedule.checkTime();
		//System.out.println("" + isEnabled + " " + typeEnabled + " " + dayValid + " " + timeValid );
		if (isEnabled && typeEnabled && dayValid && timeValid) {
			return true;
		}
		return false;
	}

}
