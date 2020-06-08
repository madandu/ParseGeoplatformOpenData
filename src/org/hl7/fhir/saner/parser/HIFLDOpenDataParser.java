package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.data.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * @author madan upadhyay
 * email: madandu@gmail.com
 */
public class HIFLDOpenDataParser implements ModelParser {

	private Model model = null;

	/**
	 * Constructor
	 * 
	 * @param absolute
	 *            or relative-path to the source file
	 * @param data
	 *            -type JSON, CSV or XML.
	 */
	public HIFLDOpenDataParser(Model _data) {
		this.model = _data;
	}

	/**
	 * Parses source-file based on data-type
	 * 
	 * @return prints-out attributes of source-file
	 */
	public void parseData() {

		switch (this.model.Context().Type()) {
		case JSON:
			ParseJSON();
			break;

		case XML:
			ParseXML();
			break;
			
		case REST:
			QueryAndParseRESTUrl();
			break;

		default:
			break;
		}
		// Test message;
	}

	/**
	 * Parses JSONObject Prints out all hospitals' attributes within JSONObject.
	 */
	private void ParseJSON() {

		JSONObject jso = null;

		try (FileReader reader = new FileReader(this.model.Context().Source())) {
			
			JSONTokener tokener = new JSONTokener(reader);
			jso = new JSONObject(tokener);

			JSONArray jArr = null;
			jArr = jso.getJSONArray("features");
			System.out.println("Begin: listing all hospital]s attributes");

			for (Object item : jArr) {
				if (item instanceof JSONObject) {
					JSONObject hospitalAttributes = ((JSONObject) item)
							.getJSONObject("attributes");
					System.out.println(hospitalAttributes);
				}
			}

			System.out.println("End.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query opendata-API for USA-hospitals' records and parse JSON output.
	 */
	public void QueryAndParseRESTUrl() {

		try {
			
			URL restURL = new URL(this.model.Context().Source());
			
			long startTime = System.currentTimeMillis();

			HttpURLConnection httpConn = (HttpURLConnection) restURL
					.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Accept", "application/json");

			if (httpConn.getResponseCode() != 200) {
				throw new RuntimeException(
						"HTTP GET Request Failed with Error code : "
								+ httpConn.getResponseCode());
			}

			// Read RESTUrl response.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					(httpConn.getInputStream())));

			// Process and aggregate critical-resources in hospitals CountyWise.
			JSONObject hospitals = new JSONObject(new JSONTokener(reader));
			JSONArray features = hospitals.getJSONArray("features");
			
			// NAME NAICS_DESC ZIP COUNTYFIPS COUNTY TTL_STAFF BEDS STATUS, POPULATION,

			HashMap<String, JSONObject> aggregateCountyWise = new HashMap<String, JSONObject>();
			JSONObject property, county = null;
			String ZIP, COUNTYFIPS, COUNTY = null;
			int TTL_STAFF, BEDS, POPULATION;

			for (Object feature : features) {
				
				if (feature instanceof JSONObject) {
					property = (JSONObject) feature;
					property = property.getJSONObject("properties");
					
					// Use CountyFIPS as key, if exists aggregate hospital-resources.
					COUNTYFIPS = property.getString("COUNTYFIPS");
					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {
						
						// Get existing hospital-attributes
						TTL_STAFF = property.getInt("TTL_STAFF");
						BEDS = property.getInt("BEDS");
						POPULATION = property.getInt("POPULATION");
						COUNTY = property.getString("COUNTY");
						ZIP = property.getString("ZIP");

						// Aggregate countywise hospitals' resources
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("COUNTY", COUNTY);
						county.put("ZIP", ZIP);
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") + TTL_STAFF);
						county.put("BEDS", county.getInt("BEDS") + BEDS);
						county.put("POPULATION", county.getInt("POPULATION") + POPULATION);

						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);
						
					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("NAME",  property.getString("NAME"));
						county.put("NAICS_DESC",  property.getString("NAICS_DESC"));
						county.put("COUNTY",  property.getString("COUNTY"));
						county.put("COUNTYFIPS",  property.getString("COUNTYFIPS"));
						county.put("ZIP",  property.getInt("ZIP"));
						county.put("TTL_STAFF",  property.getInt("TTL_STAFF"));
						county.put("BEDS",  property.getInt("BEDS"));
						county.put("POPULATION",  property.getInt("POPULATION"));
						
						aggregateCountyWise.put(COUNTYFIPS, county);					
					}
				}
			}
			reader.close();
			httpConn.disconnect();
			long endTime = System.currentTimeMillis();
			
			System.out.println("Completed: Countywise aggregate of critical-resources in hospitals.");
			System.out.println("Processed: " +aggregateCountyWise.size() +" records of hospitals.");
			System.out.println("It took " + (endTime - startTime) +" Milliseconds to get and process records");

			//Clear hashMap in the end.
			aggregateCountyWise.clear();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test query opendata-API for USA-hospitals' records and parse JSON output.
	 */
	public void TestQueryAndParseRESTUrl() {

		try {
			
			URL restURL = new URL(this.model.Context().Source());
			
			long startTime = System.currentTimeMillis();

			HttpURLConnection httpConn = (HttpURLConnection) restURL
					.openConnection();
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Accept", "application/json");

			if (httpConn.getResponseCode() != 200) {
				throw new RuntimeException(
						"HTTP GET Request Failed with Error code : "
								+ httpConn.getResponseCode());
			}

			// Read RESTUrl response.
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					(httpConn.getInputStream())));

			// Process and aggregate critical-resources in hospitals CountyWise.
			JSONObject hospitals = new JSONObject(new JSONTokener(reader));
			JSONArray features = hospitals.getJSONArray("features");
			
			// NAME NAICS_DESC ZIP COUNTYFIPS COUNTY TTL_STAFF BEDS STATUS, POPULATION,

			HashMap<String, JSONObject> aggregateCountyWise = new HashMap<String, JSONObject>();
			JSONObject property, county = null;
			String COUNTYFIPS = null;

			for (Object feature : features) {
				
				if (feature instanceof JSONObject) {
					property = ((JSONObject) feature).getJSONObject("properties");
					
					// Use CountyFIPS as key, if exists aggregate hospital-resources.
					COUNTYFIPS = property.getString("COUNTYFIPS");
					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {						
						// Aggregate hospitals' resources countywise.
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") + property.getInt("TTL_STAFF"));
						county.put("BEDS", county.getInt("BEDS") + property.getInt("BEDS"));
						county.put("POPULATION", county.getInt("POPULATION") + property.getInt("POPULATION"));

						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);
						
					} else { // Setup  County object for resource-aggregation
						county = new JSONObject();
						county.put("NAME",  property.getString("NAME"));
						county.put("NAICS_DESC",  property.getString("NAICS_DESC"));
						county.put("COUNTY",  property.getString("COUNTY"));
						county.put("COUNTYFIPS",  property.getString("COUNTYFIPS"));
						county.put("ZIP",  property.getInt("ZIP"));
						county.put("TTL_STAFF",  property.getInt("TTL_STAFF"));
						county.put("BEDS",  property.getInt("BEDS"));
						county.put("POPULATION",  property.getInt("POPULATION"));
						
						aggregateCountyWise.put(COUNTYFIPS, county);					
					}
				}
			}
			reader.close();
			httpConn.disconnect();
			long endTime = System.currentTimeMillis();
			
			System.out.println("Completed: Countywise aggregate of critical-resources in hospitals.");
			System.out.println("Processed: " +aggregateCountyWise.size() +" records of hospitals.");
			System.out.println("Overall took " + (endTime - startTime) +" milliseconds to query and process hospitals records");

			//Clear hashMap in the end.
			aggregateCountyWise.clear();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses XML Prints out all hospitals' attributes within XMLObject.
	 */
	private void ParseXML() {

		XML xmo = null;
		try (FileReader reader = new FileReader(this.model.Context().Source())) {

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String convertJSONToXMLReport(String jsonObject, String root)
			throws JSONException {
		String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<"
				+ root + ">" + XML.toString(jsonObject) + "</" + root + ">";
		
		return xml;
	}
}
