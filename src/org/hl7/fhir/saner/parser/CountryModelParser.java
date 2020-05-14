package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.model.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CountryModelParser implements ModelParser {

	private DataModel.Type type;
	private String sourcePath = null; 

	/**
	Constructor
	@param absolute or relative-path to the source file
	@param data-type JSON, CSV or XML.
	*/
	public CountryModelParser(String _source, DataModel.Type _type) {
		sourcePath = _source;
		type = _type;
	}
	
	/**
	Parses source-file based on its FileType
	@return prints-out attributes of source-file
	*/
	public void parseData() {
		// TODO Auto-generated method stub
		
		switch (type) {
	    case JSON: 
	    	JSONParser();
	    	break;
		default:
			break;
		}
	}
	
	/**
	Parses JSON source-file
	@return prints-out attributes of JSON source-file
	@throws exception of type java.io.IOException
	*/
	private void JSONParser()
	{
		JSONParser parser = new JSONParser();
		try (FileReader reader = new FileReader(sourcePath)) {
			// Read JSON file
			Object obj = parser.parse(reader);
			JSONObject jso = (JSONObject) obj;
			Object hospitalArray = jso.get("features");

			System.out.println(hospitalArray);
			// Iterate over employee array

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void parseHospitals(JSONObject hospital) {
		// Get employee object within list
		JSONObject employeeObject = (JSONObject) hospital.get("hospital");

		// Get employee first name
		String firstName = (String) employeeObject.get("firstName");
		System.out.println(firstName);

		// Get employee last name
		String lastName = (String) employeeObject.get("lastName");
		System.out.println(lastName);

		// Get employee website name
		String website = (String) employeeObject.get("website");
		System.out.println(website);
	}
}
