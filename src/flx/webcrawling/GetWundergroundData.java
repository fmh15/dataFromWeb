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
		System.out.println(webURL);

		URL url = new URL(webURL);
		InputStream is = url.openStream();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			boolean isListening = false;
			int dataCollectionStatus = 0;

			while ((line = br.readLine()) != null) {
				String time;
				String temperature;
				String humidity;
				String pressure;
				String sight;
				String windspeed;
				String winddirection;
				String generalDescription;

				// read geographical information of station
				if (line.contains("TheLat=")) {
					if (isFirstDay) {
						isFirstDay = false;
						String lat = line.substring(line.indexOf("TheLat=") + 7, line.indexOf("TheLat=") + 13);
						String lon = line.substring(line.indexOf("TheLon=") + 7, line.indexOf("TheLon=") + 13);
						station.setLat(Double.parseDouble(lat));
						station.setLon(Double.parseDouble(lon));
					}
					isListening = true;
				}

				if (isListening) {
					if (line.contains(":")) {
						dataCollectionStatus++;
						time = line.substring(line.indexOf(":") - 2, line.indexOf(":"));
						if (time.contains(">")) {
							time = "" + time.charAt(1);
						}
						String ampm = line.substring(line.indexOf(":") + 4, line.indexOf(":") + 6);

						System.out.println(time + " " + ampm);
					} else

					if (dataCollectionStatus == 1 && line.contains("wx-value")) {
						dataCollectionStatus++;
						temperature = line.substring(line.indexOf("wx-value") + 10, line.indexOf("</span>"));
						System.out.println("Temperature: " + temperature + " F");
					} else if (line.contains("%")) {
						dataCollectionStatus++;
						humidity = line.substring(line.indexOf("%") - 2, line.indexOf("%"));
						System.out.println("Humidity: " + humidity + "%");
					} else if (dataCollectionStatus == 3 && line.contains("wx-value")) {
						dataCollectionStatus++;
						pressure = line.substring(line.indexOf("wx-value") + 10, line.indexOf("</span>"));
						System.out.println("Pressure: " + pressure + " inch");
//					} else if (line.contains("&nbsp;mi")) {
//						dataCollectionStatus++;
//						sight = line.substring(line.indexOf("wx-value") + 10, line.indexOf("</span>"));
//						System.out.println("Sight: " + sight + " mi");
					} else if (line.contains("<td >") && line.contains("</td>") && dataCollectionStatus == 4) {
						dataCollectionStatus++;
						winddirection = line.substring(line.indexOf("<td >") + 5, line.indexOf("</td>"));
						System.out.println("Wind direction: " + winddirection);
					} else if (line.contains("mph")) {
						dataCollectionStatus++;
						windspeed = line.substring(line.indexOf("wx-value") + 10, line.indexOf("</span>"));
						System.out.println("Wind speed: " + windspeed + " mph");
					} else if (line.contains("<td >") && line.contains("</td>") && dataCollectionStatus == 6) {
						dataCollectionStatus = 0;
						generalDescription = line.substring(line.indexOf("<td >") + 5, line.indexOf("</td>"));
						System.out.println("General description: " + generalDescription);
						System.out.println();
					}
				}

				// end reading after this line, all data collected
				if (isListening && line.contains("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890")) {
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
					System.err.println(
							"error while crawling " + s.getCityName() + " data of: " + currentDate.toLocaleString());
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
