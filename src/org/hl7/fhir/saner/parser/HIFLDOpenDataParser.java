package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.data.Model;
import org.json.HTTP;
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
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
			QueryAndParseRestJSON();
			// ParseJSON();
			break;

		case XML:
			ParseXML();
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
	 * Query opendata-API for USA-hosptials' dataset and parses JSON output.
	 */
	private void QueryAndParseRestJSON() {

		String sRestURL = "https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson";
		try {
			URL restURL = new URL(sRestURL);
			HttpURLConnection httpConnection = (HttpURLConnection) restURL
					.openConnection();
			httpConnection.setRequestMethod("GET");
			httpConnection.setRequestProperty("Accept", "application/json");

			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException(
						"HTTP GET Request Failed with Error code : "
								+ httpConnection.getResponseCode());
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));

			JSONObject hospitals = new JSONObject(new JSONTokener(reader));
			JSONArray features = hospitals.getJSONArray("features");
			// NAME NAICS_DESC ZIP COUNTYFIPS COUNTY TTL_STAFF BEDS STATUS
			// POPULATION,

			JSONObject property = new JSONObject();

			HashMap<String, JSONObject> countyWiseAnalysed = new HashMap<String, JSONObject>();
			JSONObject county = null;

			String ZIP, COUNTYFIPS, COUNTY, STATUS = null;
			int TTL_STAFF, BEDS, POPULATION;

			for (Object feature : features) {
				if (feature instanceof JSONObject) {
					property = (JSONObject) feature;
					property = property.getJSONObject("properties");
					
					// Set CountyFIPS as the key, if exists aggregate hospital-resources.
					COUNTYFIPS = property.getString("COUNTYFIPS");
					if (countyWiseAnalysed.containsKey(COUNTYFIPS)) {
						
						// Get existing hospital-attributes
						TTL_STAFF = property.getInt("TTL_STAFF");
						BEDS = property.getInt("BEDS");
						POPULATION = property.getInt("POPULATION");

						// Aggregate countywise hospitals' resources
						county = countyWiseAnalysed.get(COUNTYFIPS);
						county.put("TTL_STAFF", county.getInt("TTL_STAFF") + TTL_STAFF);
						county.put("BEDS", county.getInt("BEDS") + BEDS);
						county.put("POPULATION", county.getInt("POPULATION") + POPULATION);

						// Replace the existing with aggregated values;
						countyWiseAnalysed.replace(COUNTYFIPS, county);
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
						
						countyWiseAnalysed.put(COUNTYFIPS, county);
					}					
				}
			}

			reader.close();
			httpConnection.disconnect();
			System.out.println("Done : Aggregate-report of countywise critical-resource in USA Hosptials");
			System.out.println("Number of records in aggregate-report:" +countyWiseAnalysed.size());
			
			//String xml = this.convertJSONToXML(countyWiseAnalysed.values().toString(), "CountyWiseAggregated");
			//System.out.print(xml);

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
