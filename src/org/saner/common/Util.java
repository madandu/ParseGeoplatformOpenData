package org.saner.common;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {

	/**
	 * Parses XML Prints out all hospitals' attributes within XMLObject.
	 */
	public static String parseXML(String xmlFilePath) {
		String msg = "";
		
		try (FileReader reader = new FileReader(xmlFilePath)) {
			// TODO Read and parse XML
		} catch (FileNotFoundException e) {
			msg = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			msg = "Parser error: " +e.getMessage();
		} catch (Exception e) {
			msg = "Parser error: " +e.getMessage();
		}
		return msg;
	}
	
	/**
	 * Write JSONObjects to CSV file.
	 */
	public static String writeToCSV(Collection<JSONObject> counties) {		
		String msg = "";
		String csvFile = "Hifld_USA-Countywise-"+System.currentTimeMillis() +".csv";
		
		try (FileWriter csv = new FileWriter(csvFile)){
				csv.append("Country");
				csv.append(",");
				csv.append("State");
				csv.append(",");
				csv.append("County");
				csv.append(",");
				csv.append("Latitude");
				csv.append(",");
				csv.append("Longitude");
				csv.append(",");
				csv.append("Population");
				csv.append(",");
				csv.append("Total-Staff");
				csv.append(",");
				csv.append("Bed"+"\n");
				
				for (JSONObject county : counties) {
					csv.append(county.getString("COUNTRY") +",");
					csv.append(county.getString("STATE")+",");
					csv.append(county.getString("COUNTY")+",");
					csv.append(county.getDouble("LATITUDE")+",");
					csv.append(county.getDouble("LONGITUDE")+",");
					csv.append(county.getInt("POPULATION")+",");
					csv.append(county.getInt("TTL_STAFF")+",");
					csv.append(county.getInt("BEDS")+"\n");
				}
				
				csv.flush();
				csv.close();
				counties.clear();
				counties = null;
				msg = "Results in: " +csvFile ;
				
		} catch (IOException e) {
			msg = "Parser error: " +e.getMessage();
		}
		
		return msg;
	}

	/**
	 * Converts JSON object to XML returns XML string.
	 */
	public static String convertJSONToXMLReport(JSONObject jsonObject, String root) throws JSONException {		
		String xml = "";
		
		try {
				xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-15\"?>\n<"
					+ root + ">" +org.json.XML.toString( jsonObject) + "</" + root + ">";
			
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return xml;
	}
}
