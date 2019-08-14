package types;

public class ItemPriceType {
	
	public int id;
	public String name;
	public PriceSchedule schedule;
	public int enabled = 1;
	
	
	public ItemPriceType(int id, String name, PriceSchedule schedule, int enabled) {
		this.id = id;
		this.name = name;
		this.schedule = schedule;
		this.enabled = enabled;
	}
	
	@Override
	public String toString() {
		return name;
	}

}
