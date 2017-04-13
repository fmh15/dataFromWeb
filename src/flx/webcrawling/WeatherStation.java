package flx.webcrawling;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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
	
	@SuppressWarnings("deprecation")
	public void writeStationDataToCSV() {
		
		 PrintWriter pw;
		try {
			pw = new PrintWriter(new File(cityName + GetWundergroundData.startDate.toLocaleString() + "-" + GetWundergroundData.endDate.toLocaleString() +".csv"));
			StringBuilder sb = new StringBuilder();
			
			for (WeatherDataSeries wd: weatherData){
				sb.append(wd.getTimestamp());
				sb.append(',');
				sb.append(wd.getTemperature());
				sb.append(',');
				sb.append(wd.getPressure());
				sb.append(',');
				sb.append(wd.getSight());
				sb.append(',');
				sb.append(wd.getHumidity());
				sb.append(',');
				sb.append(wd.getWinddirection());
				sb.append(',');
				sb.append(wd.getWindspeed());
				sb.append(',');
				sb.append(wd.getGeneralDescription());
				sb.append('\n');
			}
	        pw.write(sb.toString());
	        pw.close();
	        System.out.println("done!");
		} catch (FileNotFoundException e) {
			System.err.println("error while writing " + cityName + " data.");
		}
	        
		
	}

}
