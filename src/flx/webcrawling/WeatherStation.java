package flx.webcrawling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherStation {
	private String cityName;
	private String country;
	private double lat;
	private double lon;
	private int wmo;
	private String ICAO;

	private ArrayList<WeatherDataSeries> weatherData;

	public WeatherStation(String cityName, String country, String wmo, String ICAO) {
		this.cityName = cityName;
		this.country = country;
		this.wmo = Integer.parseInt(wmo);
		this.ICAO = ICAO;

		weatherData = new ArrayList<WeatherDataSeries>();
	}

	public WeatherDataSeries getDataOfHour(SimpleDateFormat date) {
		WeatherDataSeries result = null;

		for (WeatherDataSeries wds : weatherData) {
			if (date.equals(wds.getTimestamp())) {
				result = wds;
				break;
			}
		}

		return result;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public String getCityName() {
		return cityName;
	}

	public String getCountry() {
		return country;
	}

	public String getICAO() {
		return ICAO;
	}

	public int getWmo() {
		return wmo;
	}

	public String getURLFromStationForDate(Date date) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("/yyyy/MM/dd");
		String formattedDate = formatter.format(date);
		
		return GetWundergroundData.wundergroundURL[0] + ICAO + formattedDate + GetWundergroundData.wundergroundURL[1]
				+ cityName + GetWundergroundData.wundergroundURL[2] + country + GetWundergroundData.wundergroundURL[3]
				+ wmo;
	}

}
