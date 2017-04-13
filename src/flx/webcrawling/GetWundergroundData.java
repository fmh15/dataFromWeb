package flx.webcrawling;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class GetWundergroundData {

	@SuppressWarnings("deprecation")
	public static Date startDate = new Date(2015, 1, 1);
	@SuppressWarnings("deprecation")
	public static Date endDate = new Date(2015, 1, 2);

	final static String csvFile = "U:/Documents/stationMasterdata.csv";

	public static final String[] wundergroundURL = { "https://www.wunderground.com/history/airport/",
			"DailyHistory.html?req_city=", "&req_state=&req_statename=", "&reqdb.zip=00000&reqdb.magic=1&reqdb.wmo=" };

	public static ArrayList<WeatherStation> listOfStations;

	public static void readFromWeb(String webURL, boolean isFirstDay, WeatherStation station) throws IOException {
		URL url = new URL(webURL);
		InputStream is = url.openStream();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			boolean isListening = false;
			
			
			while ((line = br.readLine()) != null) {
				
				// read geographical information of station
				if (line.contains("TheLat=")){
					if (isFirstDay){
						isFirstDay = false;
						String lat = line.substring(line.indexOf("TheLat=")+7, line.indexOf("TheLat=")+13);
						String lon = line.substring(line.indexOf("TheLon=")+7, line.indexOf("TheLon=")+13);
						station.setLat(Double.parseDouble(lat));
						station.setLon(Double.parseDouble(lon));
					}
					isListening = true;
				}
				
				
				
				// end reading after this line, all data collected
				if (isListening && line.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890")){
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new MalformedURLException("URL is malformed!!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}

	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		listOfStations = new ArrayList<WeatherStation>();
		readStationInfoFromCSV();

		for (WeatherStation s : listOfStations) {
			Date currentDate = startDate;
			while (currentDate.compareTo(endDate) <= 0) {

				try {
					readFromWeb(s.getURLFromStationForDate(currentDate), currentDate == startDate, s);
				} catch (IOException e) {
					System.err.println("error while crawling " + s.getCityName() + " data of: " + currentDate.toLocaleString());
				}

				currentDate.setTime(currentDate.getTime() + 24 * 60 * 60 * 1000);
			}
			s.writeStationDataToCSV();
		}
	}

	private static void readStationInfoFromCSV() {

		BufferedReader br = null;
		String line = "";
		final String cvsSplitBy = ",";

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {
				String[] stationInfo = line.split(cvsSplitBy);
				listOfStations.add(new WeatherStation(stationInfo[0], stationInfo[1], stationInfo[2], stationInfo[3]));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
