package flx.webcrawling;

import java.util.Date;

public class WeatherDataSeries {
	private Date timestamp;
	private double temperature;
	private int humidity;
	private double pressure;
	private double sight;
	private double windspeed;
	private String winddirection;
	private String generalDescription;
	
	
	
	public WeatherDataSeries(Date timestamp, double temperature, int humidity, double pressure,
			double sight, double windspeed, String winddirection, String generalDescription) {
		this.timestamp = timestamp;
		this.temperature = temperature;
		this.humidity = humidity;
		this.pressure = pressure;
		this.sight = sight;
		this.windspeed = windspeed;
		this.winddirection = winddirection;
		this.generalDescription = generalDescription;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public double getTemperature() {
		return temperature;
	}
	public int getHumidity() {
		return humidity;
	}
	public double getPressure() {
		return pressure;
	}
	public double getSight() {
		return sight;
	}
	public double getWindspeed() {
		return windspeed;
	}
	public String getWinddirection() {
		return winddirection;
	}
	public String getGeneralDescription() {
		return generalDescription;
	}
}
