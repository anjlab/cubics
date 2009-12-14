package anjlab.cubics.test;

public class Fact {

	private int year;
	private int month;
	private int day;
	private int hour;
	private int duration;
	private int succeeded;
	
	public Fact(int year, int month, int day, int hour, int duration, int succeeded) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.duration = duration;
		this.succeeded = succeeded;
	}
	
	public int getYear() {
		return year;
	}
	public int getMonth() {
		return month;
	}
	public int getDay() {
		return day;
	}
	public int getHour() {
		return hour;
	}
	public int getDuration() {
		return duration;
	}
	public int getSucceeded() {
		return succeeded;
	}
}
