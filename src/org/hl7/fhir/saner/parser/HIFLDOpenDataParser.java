package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.data.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
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
			JSONArray attributes = jso.getJSONArray("features");

			HashMap<String, JSONObject> aggregateCountyWise = new HashMap<String, JSONObject>();
			String COUNTYFIPS  = null;
			JSONObject attribute, county = null;

			for (Object item : attributes) {
				if (item instanceof JSONObject) {
					attribute = ((JSONObject)item).getJSONObject("attributes");
					// Use CountyFIPS as key, if exists aggregate critical hospital-resources.
					COUNTYFIPS = attribute.getString("COUNTYFIPS");

					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {
						// Aggregate county-wise hospitals' resources
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("POPULATION", county.getInt("POPULATION")	+attribute.getInt("POPULATION"));
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +attribute.getInt("TTL_STAFF"));
						county.put("BEDS", county.getInt("BEDS") +attribute.getInt("BEDS"));
						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);

					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("COUNTRY", attribute.getString("COUNTRY"));
						county.put("STATE", attribute.getString("STATE"));
						county.put("COUNTY", attribute.getString("COUNTY"));
						county.put("COUNTYFIPS", COUNTYFIPS);
						county.put("POPULATION", attribute.getInt("POPULATION"));
						county.put("TTL_STAFF", attribute.getInt("TTL_STAFF"));
						county.put("BEDS", attribute.getInt("BEDS"));
						// Add new county object;
						aggregateCountyWise.put(COUNTYFIPS, county);
					}
				}
			}
			System.out.println("Done aggregating critical-resources county-wise.");
			//Validate results in .csv file:
			writeToCSV(aggregateCountyWise);	
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

			for (Object item : features) {
				if (item instanceof JSONObject) {
					property = ((JSONObject) item).getJSONObject("properties");
					COUNTYFIPS = property.getString("COUNTYFIPS");
					// Use CountyFIPS as key, if exists county, aggregate critical-resources in its hospitals.
					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {
						// Aggregate county-wise hospitals' resources
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("POPULATION", county.getInt("POPULATION")	+property.getInt("POPULATION"));
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +property.getInt("TTL_STAFF"));
						county.put("BEDS", county.getInt("BEDS") +property.getInt("BEDS"));
						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);
						
					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("COUNTRY", property.getString("COUNTRY"));
						county.put("STATE", property.getString("STATE"));
						county.put("COUNTY", property.getString("COUNTY"));
						county.put("COUNTYFIPS", COUNTYFIPS);
						county.put("POPULATION", property.getInt("POPULATION"));
						county.put("TTL_STAFF", property.getInt("TTL_STAFF"));
						county.put("BEDS",  property.getInt("BEDS"));
						// Add new county object;
						aggregateCountyWise.put(COUNTYFIPS, county);
					}
				}
			}
			
			System.out.println("Done aggregating critical-resources county-wise.");
			//Validate results in .csv file:
			writeToCSV(aggregateCountyWise);
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
	 * Converts JSON object to CSV.
	 */
	private void  writeToCSV(HashMap<String, JSONObject> jsoCounty) {
		
		String rCSVFile = "HIFLD_json-" +System.currentTimeMillis() +".csv";
		
		try (FileWriter csvWriter = new FileWriter(rCSVFile)){
				csvWriter.append("Country");
				csvWriter.append(",");
				csvWriter.append("State");
				csvWriter.append(",");
				csvWriter.append("County");
				csvWriter.append(",");
				csvWriter.append("Population");
				csvWriter.append(",");
				csvWriter.append("Total-Staff");
				csvWriter.append(",");
				csvWriter.append("Bed");
				csvWriter.append("\n");
				
				for (String key : jsoCounty.keySet()) {
					csvWriter.append(jsoCounty.get(key).getString("COUNTRY") +",");
					csvWriter.append(jsoCounty.get(key).getString("STATE")+",");
					csvWriter.append(jsoCounty.get(key).getString("COUNTY")+",");
					csvWriter.append(String.valueOf( jsoCounty.get(key).getInt("POPULATION"))+",");
					csvWriter.append(String.valueOf( jsoCounty.get(key).getInt("TTL_STAFF"))+",");
					csvWriter.append(String.valueOf( jsoCounty.get(key).getInt("BEDS")));
				    csvWriter.append("\n");
				}
				csvWriter.flush();
				csvWriter.close();
			    System.out.println("Results available in the file: " +rCSVFile); 
			    
		} catch (IOException io) {
			io.printStackTrace();
		} 
	}

	/**
	 * Converts JSON object to XML returns XML string.
	 */
	private String convertJSONToXMLReport(JSONObject jsonObject, String root) throws JSONException {
		
		String xml = "";
		try {
				xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<"
					+ root + ">" +org.json.XML.toString( jsonObject) + "</" + root + ">";
			
		} catch (JSONException je) {
			je.printStackTrace();
		} 
		return xml;
	}
}
