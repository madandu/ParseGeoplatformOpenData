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
			parseJSON(this.model.Context().Source());
			break;

		case XML:
			parseXML(this.model.Context().Source());
			break;

		case REST:
			queryAndParseRESTUrl(this.model.Context().Source());
			break;

		default:
			break;
		}
		// Test message;
	}

	/**
	 * Parses JSONObject Prints out all hospitals' attributes within JSONObject.
	 * 
	 * @param the
	 *            path to JSON file
	 */
	private void parseJSON(String jsonFilePath) {

		try (FileReader reader = new FileReader(jsonFilePath)) {

			JSONTokener tokener = new JSONTokener(reader);
			JSONObject jso = new JSONObject(tokener);
			
			parseCountyWise(jso);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}		
	}
	
	private void parseCountyWise(JSONObject jso) {

		try  {	
			System.out.println("Start processing HIFLD hospital-records county-wise.");
			JSONArray features = jso.getJSONArray("features");

			// NAME NAICS_DESC ZIP COUNTYFIPS COUNTY TTL_STAFF BEDS STATUS,
			// POPULATION,
			HashMap<String, JSONObject> aggregateCountyWise = new HashMap<String, JSONObject>();
			JSONObject property, county = null;
			String ZIP, COUNTYFIPS, COUNTY = null;
			int TTL_STAFF, BEDS, POPULATION;

			for (Object feature : features) {

				if (feature instanceof JSONObject) {
					property = (JSONObject) feature;
					property = property.getJSONObject("properties");

					// Use CountyFIPS as key, if exists aggregate critical hospital-resources.
					COUNTYFIPS = property.getString("COUNTYFIPS");

					if (aggregateCountyWise.containsKey(COUNTYFIPS)) {
						// Get existing hospital-attributes
						TTL_STAFF = property.getInt("TTL_STAFF");
						BEDS = property.getInt("BEDS");
						POPULATION = property.getInt("POPULATION");
						COUNTY = property.getString("COUNTY");
						ZIP = property.getString("ZIP");

						// Aggregate county-wise hospitals' resources
						county = aggregateCountyWise.get(COUNTYFIPS);
						county.put("COUNTY", COUNTY);
						county.put("ZIP", ZIP);
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") +TTL_STAFF);
						county.put("BEDS", county.getInt("BEDS") + BEDS);
						county.put("POPULATION", county.getInt("POPULATION")	+POPULATION);

						// Replace the existing with aggregated values;
						aggregateCountyWise.replace(COUNTYFIPS, county);

					} else { // Initialize CountyObject for aggregation
						county = new JSONObject();
						county.put("NAME", property.getString("NAME"));
						county.put("NAICS_DESC", property.getString("NAICS_DESC"));
						county.put("COUNTY", property.getString("COUNTY"));
						county.put("COUNTYFIPS", property.getString("COUNTYFIPS"));
						county.put("ZIP", property.getInt("ZIP"));
						county.put("TTL_STAFF", property.getInt("TTL_STAFF"));
						county.put("BEDS", property.getInt("BEDS"));
						county.put("POPULATION", property.getInt("POPULATION"));

						aggregateCountyWise.put(COUNTYFIPS, county);
					}
				}
			}
			System.out.println("Processed " + aggregateCountyWise.size()	+" records of critical-resources in hospitals.");
			// Clear hashMap in the end.
			aggregateCountyWise.clear();	

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query opendata-API for USA-hospitals' records and parse JSON output.
	 * 
	 * @param the
	 *            URL of RESTful query to GET data.
	 */
	public void queryAndParseRESTUrl(String urlPath) {
		
		// Compute time taken to get/parse output from RESTUrl.
		long startTime = System.currentTimeMillis();

		try {
			
			URL url = new URL(urlPath);

			System.out.println("Open HIFLD url to get hospitals-records.");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("HTTP GET Request Failed with Error code : "+ conn.getResponseCode());
			}

			// TODO make reading more efficient to handle large data from RESTUrl.
			BufferedReader reader = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			JSONObject hospitals = new JSONObject(new JSONTokener(reader));
			reader.close();
			conn.disconnect();
			System.out.println("Done reading of hospital-records,  closed HIFLD url-connection.");	

			// Process and aggregate critical-resources in hospitals-records county-wise.
			parseCountyWise(hospitals);

			long endTime = System.currentTimeMillis();
			System.out.println("Took " + (endTime - startTime)+ " milliseconds to get and process records.");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
