package org.saner.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.saner.common.Util;
import org.saner.opendata.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Madan Upadhyay
 * @email: madandu@gmail.com Parse for HIFLD opendata to parse variety of data.
 */
public class HIFLDOpenDataParser implements ModelParser {

	private Model model = null;

	/**
	 * Constructor
	 * 
	 * @param data
	 *            has absolute or relative-path to the source data and data-type
	 *            JSON, CSV file, URL or XML.
	 */
	public HIFLDOpenDataParser(Model _data) {
		this.model = _data;
	}

	/**
	 * Parses source-file based on data-context
	 * 
	 * @return prints-out attributes of source-file
	 */
	public String parseData() {
		String pMsg = "";
		
		switch (this.model.Context().Type()) {
		case JSON:
			pMsg = parseJSONFile(this.model.Context().Source());
			break;

		case REST:
			pMsg = queryAndParseRESTUrl(this.model.Context().Source());
			break;

		// TODO query and parse xml data from HIFLD url.
		case XML:
			pMsg = Util.parseXML(this.model.Context().Source());
			break;
			
		default:
			break;
		}
		return pMsg; 
	}

	/**
	 * Parses JSONObject Prints out all hospitals' attributes within JSONObject.
	 * 
	 * @param JSON file-path 
	 */
	private String parseJSONFile(String jPath) {
		String msg = "";
 
		try (FileReader read = new FileReader(jPath)) {

			JSONTokener tokener = new JSONTokener(read);
			JSONObject jso = new JSONObject(tokener);
			read.close();
			
			JSONArray features = jso.getJSONArray("features");

			HashMap<String, JSONObject> aggCounties = new HashMap<String, JSONObject>();
			String COUNTYFIPS  = null;
			JSONObject attr, county = null;

			for (Object item : features) {
				if (item instanceof JSONObject) {
					attr = ((JSONObject)item).getJSONObject("attributes");
					// Use CountyFIPS as key, if exists aggregate critical hospital-resources.
					COUNTYFIPS = attr.getString("COUNTYFIPS");

					if (aggCounties.containsKey(COUNTYFIPS)) {
						// Aggregate county-wise hospitals' resources
						county = aggCounties.get(COUNTYFIPS);
						county.put("POPULATION", county.getInt("POPULATION")	+attr.getInt("POPULATION"));
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +attr.getInt("TTL_STAFF"));
						county.put("BEDS", county.getInt("BEDS") +attr.getInt("BEDS"));
						// Replace the existing with aggregated values;
						aggCounties.replace(COUNTYFIPS, county);

					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("COUNTRY", attr.getString("COUNTRY"));
						county.put("STATE", attr.getString("STATE"));
						county.put("COUNTY", attr.getString("COUNTY"));
						county.put("COUNTYFIPS", COUNTYFIPS);
						county.put("LATITUDE", attr.getDouble("LATITUDE"));
						county.put("LONGITUDE", attr.getDouble("LONGITUDE"));
						county.put("POPULATION", attr.getInt("POPULATION"));
						county.put("TTL_STAFF", attr.getInt("TTL_STAFF"));
						county.put("BEDS", attr.getInt("BEDS"));
						// Add new county object;
						aggCounties.put(COUNTYFIPS, county);
					}
				}
			}
			msg +="Completed analysis of critical hospital-resources in USA-counties.\n\r";
			//Write JSONObjects in .csv file to validate results,
			msg += Util.writeToCSV(aggCounties.values());
			// Clear hashMap in the end.
			aggCounties.clear();
		} catch (JSONException e) {
			msg = "Parser error: " +e.getMessage();
		} catch (FileNotFoundException e) {
			msg = "Parser error: " +e.getMessage();
		} catch (IOException e) {
			msg = "Parser error: " +e.getMessage();
		}
		
		return msg;
	}
	
	/**
	 * Parse JSONObject got from the url to get critical-resource in hospitals county-wise.
	 * 
	 * @param JSON object containing records.
	 */
	private String parseUrlCountyWise(JSONObject jso) {
		String msg = "";
		
		try  {			
			JSONArray features = jso.getJSONArray("features");
			if (features == null || features.length() ==0) return msg;
			
			HashMap<String, JSONObject> aggCounties = new HashMap<String, JSONObject>();
			JSONObject props, county = null;
			String COUNTYFIPS = null;

			for (Object item : features) {
				if (item instanceof JSONObject) {
					props = ((JSONObject) item).getJSONObject("properties");
					COUNTYFIPS = props.getString("COUNTYFIPS");
					// Use CountyFIPS as key, if exists county, aggregate critical-resources in its hospitals.
					if (aggCounties.containsKey(COUNTYFIPS)) {
						// Aggregate county-wise hospitals' resources
						county = aggCounties.get(COUNTYFIPS);
						county.put("POPULATION", county.getInt("POPULATION") +props.getInt("POPULATION"));
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +props.getInt("TTL_STAFF"));
						county.put("BEDS", county.getInt("BEDS") +props.getInt("BEDS"));
						// Replace the existing with aggregated values;
						aggCounties.replace(COUNTYFIPS, county);
						
					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("COUNTRY", props.getString("COUNTRY"));
						county.put("STATE", props.getString("STATE"));
						county.put("COUNTY", props.getString("COUNTY"));
						county.put("COUNTYFIPS", COUNTYFIPS);
						county.put("LATITUDE", props.getDouble("LATITUDE"));
						county.put("LONGITUDE", props.getDouble("LONGITUDE"));
						county.put("POPULATION", props.getInt("POPULATION"));
						county.put("TTL_STAFF", props.getInt("TTL_STAFF"));
						county.put("BEDS",  props.getInt("BEDS"));
						// Add new county object;
						aggCounties.put(COUNTYFIPS, county);
					}
				}
			}
			msg ="Completed analysis of critical hospital-resources in USA-counties.\n\r";
			//Write JSONObjects in .csv file to validate results.
			msg += Util.writeToCSV(aggCounties.values());
			// Clear hashMap in the end.
			aggCounties.clear();
			
		} catch (JSONException e) {
			msg = "Parser error: " + e.getMessage();
		} catch (Exception e) {
			msg = "Parser error: " +e.getMessage();
		}
			return msg;
	}

	/**
	 * Query opendata-API for USA-hospitals' records and parse JSON output.
	 * 
	 * @param the URL of RESTful query to GET data.
	 */
	public String queryAndParseRESTUrl(String urlPath) {
		String msg = "";
		
		try {
			URL url = new URL(urlPath);
			// Compute time taken to get/parse output from RESTUrl.
			long sTime = System.currentTimeMillis();
			
			// TODO Make HTTP UrlConnection more efficient and handle connection time-outs.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("HTTP GET Request Failed with Error code : "+ conn.getResponseCode());
			}

			// TODO make reading more efficient to handle large data from RESTUrl.
			BufferedReader read = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			JSONObject hospitalRecords = new JSONObject(new JSONTokener(read));

			conn.disconnect();
			read.close();

			// Process and aggregate critical-resources in hospitals-records county-wise.
			msg= parseUrlCountyWise(hospitalRecords);			
			msg += "\n\rTook " + (System.currentTimeMillis() - sTime)+ " milliseconds to get and parse records.";

		} catch (MalformedURLException e) {
			msg = "Parser error: " +e.getMessage();
		} catch (IOException e) {
			msg = "Parser error: " +e.getMessage();
		} catch (Exception e) {
			msg = "Parser error: " +e.getMessage();
		}

		return msg;	
	}	
}
