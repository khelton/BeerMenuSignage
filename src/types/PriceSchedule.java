package types;

import java.time.LocalTime;
import java.util.Calendar;

public class PriceSchedule {
	
	public int id;
	public String name;
	public LocalTime startTime = null;
	public LocalTime endTime = null;
	private String daysEnabledString = null;
	
	
	public PriceSchedule(int id, String name, String startTime, String endTime, String daysEnabledString) {
		this.id = id;
		this.name = name;
		this.daysEnabledString = daysEnabledString;
		decodeTimes(startTime, endTime);
	}
	
	public PriceSchedule(int id, String name, LocalTime startTime, LocalTime endTime, String daysEnabledString) {
		this.id = id;
		this.name = name;
		this.startTime = startTime;
		this.endTime = endTime;
		this.daysEnabledString = daysEnabledString;
	}
	
	private void decodeTimes(String startTime, String endTime) {
		LocalTime start = null;
		LocalTime end = null;
		try {
			start = LocalTime.parse( startTime );
			end = LocalTime.parse( endTime );
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.startTime = start;
		this.endTime = end;
	}
	
	public String getDaysEnabledString() {
		return daysEnabledString;
	}
	
	public boolean checkDay() {
		if (daysEnabledString == null || daysEnabledString.length() != 7) {
			return false;
		}
		int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
		char dayValue = daysEnabledString.charAt(day);
		if (dayValue == '1') {
			return true;
		}
		return false;	
	}
	
	public boolean checkTime() {
		if (startTime == null || endTime == null ) {
			return false;
		}
		LocalTime now = LocalTime.now();
		//System.out.println(start.getHour() + " " + end.getHour() + " " + now.getHour());
		if (now.isAfter(startTime) && now.isBefore(endTime)) {
	        return true;
	    }
		return false;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
