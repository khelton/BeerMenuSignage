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
	public String scheduleTimeStart;
	public String scheduleTimeEnd;
	public String scheduleDays;
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
		boolean dayValid = checkDay();
		boolean timeValid = checkTime();
		if (dayValid && timeValid) {
			return true;
		}
		return false;
	}
	
	public boolean checkDay() {
		if (scheduleDays == null || scheduleDays.length() != 7) {
			return false;
		}
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		char dayValue = scheduleDays.charAt(day);
		if (dayValue == '1') {
			return true;
		}
		return false;	
	}
	
	public boolean checkTime() {
		if (scheduleTimeStart == null || scheduleTimeEnd == null 
				|| scheduleTimeStart.length() == 0 || scheduleTimeEnd.length() == 0 ) {
			return false;
		}
		try {
			LocalTime start = LocalTime.parse( scheduleTimeStart );
			LocalTime end = LocalTime.parse( scheduleTimeEnd );

			LocalTime now = LocalTime.now();
			System.out.println(start.getHour() + " " + end.getHour() + " " + now.getHour());
			if (now.isAfter(start) && now.isBefore(end)) {
		        //checkes whether the current time is between 14:49:00 and 20:11:13.
		        return true;
		    }
			/*
			LocalTime target = LocalTime.parse( "01:00:00" );
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		    Date time1 = sdf.parse(scheduleTimeStart);
		    Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);
	
		    Date time2 = sdf.parse(scheduleTimeEnd);
		    Calendar calendar2 = Calendar.getInstance();
		    calendar2.setTime(time2);
		    //calendar2.add(Calendar.DATE, 1);
	
		    Calendar calendar3 = Calendar.getInstance();
		    Date now = calendar3.getTime();
		    if (now.after(calendar1.getTime()) && now.before(calendar2.getTime())) {
		        //checkes whether the current time is between 14:49:00 and 20:11:13.
		        return true;
		    }*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
