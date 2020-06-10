package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.data.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

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
	public void parseData() {

		switch (this.model.Context().Type()) {
		case JSON:
			parseJSONFile(this.model.Context().Source());
			break;

		case REST:
			queryAndParseRESTUrl(this.model.Context().Source());
			break;

		// TODO query and parse xml data from HIFLD url.
		case XML:
			parseXML(this.model.Context().Source());
			break;
			
		default:
			break;
		}
		// Test message;
	}

	/**
	 * Parses JSONObject Prints out all hospitals' attributes within JSONObject.
	 * 
	 * @param path to JSON file
	 */
	private void parseJSONFile(String jsonFilePath) {

		try (FileReader reader = new FileReader(jsonFilePath)) {

			JSONTokener tokener = new JSONTokener(reader);
			JSONObject jso = new JSONObject(tokener);
			reader.close();
			
			System.out.println("Processing HIFLD hospitals-records.....");
			JSONArray features = jso.getJSONArray("features");

			// NAME NAICS_DESC ZIP COUNTYFIPS COUNTY TTL_STAFF BEDS STATUS,
			// POPULATION,
			HashMap<String, JSONObject> aggregateCountyWise = new HashMap<String, JSONObject>();
			JSONObject property, county = null;
			String COUNTYFIPS  = null;
			int TTL_STAFF, BEDS, POPULATION;

			for (Object feature : features) {

				if (feature instanceof JSONObject) {
					property = (JSONObject) feature;
					property = property.getJSONObject("attributes");

					// Use CountyFIPS as key, if exists aggregate critical hospital-resources.
					COUNTYFIPS = property.getString("COUNTYFIPS");
					TTL_STAFF = property.getInt("TTL_STAFF");
					BEDS = property.getInt("BEDS");
					POPULATION = property.getInt("POPULATION");
					
					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {
						// Aggregate county-wise hospitals' resources
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("COUNTRY", county.getString("COUNTRY"));
						county.put("STATE", county.getString("STATE"));
						county.put("COUNTY", county.getString("COUNTY"));
						county.put("COUNTYFIPS", county.getString("COUNTYFIPS"));
						county.put("POPULATION", county.getInt("POPULATION")	+POPULATION);
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +TTL_STAFF);
						county.put("BEDS", county.getInt("BEDS") + BEDS);

						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);

					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("COUNTRY", property.getString("COUNTRY"));
						county.put("STATE", property.getString("STATE"));
						county.put("COUNTY", property.getString("COUNTY"));
						county.put("COUNTYFIPS", COUNTYFIPS);
						county.put("POPULATION", POPULATION);
						county.put("TTL_STAFF", TTL_STAFF);
						county.put("BEDS", BEDS);

						aggregateCountyWise.put(COUNTYFIPS, county);
					}
				}
			}
			
			//Validate code:
			  System.out.println("Aggregated critical-resources county-wise:" ); 
				for (String key : aggregateCountyWise.keySet()) {
					System.out.println(aggregateCountyWise.get(key).getString("COUNTRY")+ ", "
							+ aggregateCountyWise.get(key).getString("STATE")+ ", "
							+ aggregateCountyWise.get(key).getString("COUNTY")+ ": "
							+ "Population="+ aggregateCountyWise.get(key).getInt("POPULATION")+ ", "
							+ "TTL_Staff="+ aggregateCountyWise.get(key).getInt("TTL_STAFF")+ ", "
							+ "Beds="+ aggregateCountyWise.get(key).getInt("BEDS"));
				}
			  
				// Clear hashMap in the end.
			  aggregateCountyWise.clear();	

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Parse JSONObject got from the url to get critical-resource in hospitals county-wise.
	 * 
	 * @param JSON object containing records.
	 */
	private void parseUrlCountyWise(JSONObject jso) {
		try  {
			
			JSONArray features = jso.getJSONArray("features");
			if (features == null || features.length() ==0) return;
			
			HashMap<String, JSONObject> aggregateCountyWise = new HashMap<String, JSONObject>();
			JSONObject property, county = null;
			String COUNTYFIPS = null;
			int TTL_STAFF, BEDS, POPULATION;

			for (Object feature : features) {

				if (feature instanceof JSONObject) {
					property = (JSONObject) feature;
					property = property.getJSONObject("properties");

					// Use CountyFIPS as key, if exists county, aggregate critical-resources in its hospitals.
					COUNTYFIPS = property.getString("COUNTYFIPS");
					TTL_STAFF = property.getInt("TTL_STAFF");
					BEDS = property.getInt("BEDS");
					POPULATION = property.getInt("POPULATION");

					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {
						// Aggregate county-wise hospitals' resources
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("COUNTRY", property.getString("COUNTRY"));
						county.put("STATE", property.getString("STATE"));
						county.put("COUNTY", property.getString("COUNTY"));
						county.put("COUNTYFIPS", COUNTYFIPS);
						county.put("POPULATION", county.getInt("POPULATION")	+POPULATION);
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +TTL_STAFF);
						county.put("BEDS", county.getInt("BEDS") +BEDS);
						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);
						
					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("COUNTRY", property.getString("COUNTRY"));
						county.put("STATE", property.getString("STATE"));
						county.put("COUNTY", property.getString("COUNTY"));
						county.put("COUNTYFIPS",COUNTYFIPS);
						county.put("POPULATION", POPULATION);
						county.put("TTL_STAFF", TTL_STAFF);
						county.put("BEDS", BEDS);

						aggregateCountyWise.put(COUNTYFIPS, county);
					}
				}
			}
			
			//Validate results:
		    System.out.println("Aggregated critical-resources county-wise:" ); 
			for (String key : aggregateCountyWise.keySet()) {
				System.out.println(aggregateCountyWise.get(key).getString("COUNTRY")+ ", "
						+ aggregateCountyWise.get(key).getString("STATE")+ ", "
						+ aggregateCountyWise.get(key).getString("COUNTY")+ ": "
						+ "Population="+ aggregateCountyWise.get(key).getInt("POPULATION")+ ", "
						+ "TTL_Staff="+ aggregateCountyWise.get(key).getInt("TTL_STAFF")+ ", "
						+ "Beds="+ aggregateCountyWise.get(key).getInt("BEDS"));
			}
			
			// Clear hashMap in the end.
			aggregateCountyWise.clear();	
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query opendata-API for USA-hospitals' records and parse JSON output.
	 * 
	 * @param the URL of RESTful query to GET data.
	 */
	public void queryAndParseRESTUrl(String urlPath) {
		try {
			URL url = new URL(urlPath);
			
			System.out.println("Connecting HIFLD url to get hospitals-records......");
			// Compute time taken to get/parse output from RESTUrl.
			long startTime = System.currentTimeMillis();
			
			// TODO Make HTTP UrlConnection more efficient and handle connection time-outs.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("HTTP GET Request Failed with Error code : "+ conn.getResponseCode());
			}

			// TODO make reading more efficient to handle large data from RESTUrl.
			BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			JSONObject hospitalRecords = new JSONObject(new JSONTokener(reader));

			conn.disconnect();
			reader.close();

			// Process and aggregate critical-resources in hospitals-records county-wise.
			parseUrlCountyWise(hospitalRecords);

			System.out.println("Took " + (System.currentTimeMillis() - startTime)+ " milliseconds to get and parse records.");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	/**
	 * Parses XML Prints out all hospitals' attributes within XMLObject.
	 */
	private void parseXML(String xmlFilePath) {

		try (FileReader reader = new FileReader(xmlFilePath)) {
			// TODO Read and parse XML
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Converts JSON object to XML returns XML string.
	 */
	private String convertJSONToXMLReport(String jsonObject, String root)
			throws JSONException {
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<"
				+ root + ">" + XML.toString(jsonObject) + "</" + root + ">";

		return xml;
	}
}
