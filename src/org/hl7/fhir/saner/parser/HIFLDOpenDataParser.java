package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.data.Model;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

public class HIFLDOpenDataParser implements ModelParser {

	private Model data = null;
	/**
	 * Constructor
	 * 
	 * @param absolute
	 *            or relative-path to the source file
	 * @param data
	 *            -type JSON, CSV or XML.
	 */
	public HIFLDOpenDataParser(Model  _data) {
		data  = _data;
	}
	
	/**
	 * Parses source-file based on data-type
	 * 
	 * @return prints-out attributes of source-file
	 */
	public void parseData() {
		
		switch (data.getContext().Type()) {
		case JSON:
			JSONObject jso = data.loadJSON();
			ParseJSON(jso);
			break;

		case XML:
			org.json.XML xmo = data.loadXML();
			ParseXML(xmo);
			break;

		default:
			break;
		}
		// Test message;
	}
	
	/**
	 * Parses JSONObject
	 * Prints out all hospitals' attributes within JSONObject.
	 */
	private void ParseJSON(JSONObject jso) {
		
		JSONArray jArr = null;
		jArr = jso.getJSONArray("features");
		System.out.println("Begin: listing all hospital]s attributes");
		
		for (Object item : jArr) {
			if (item instanceof JSONObject){
				JSONObject hospitalAttributes = ((JSONObject)item).getJSONObject("attributes");
				System.out.println(hospitalAttributes);
			}
		}
		
		System.out.println("End.");
	}

	/**
	 * Parses XML
	 * Prints out all hospitals' attributes within XMLObject.
	 */
	private void ParseXML(XML xmo) {
		
		// Parse JSON file
/*		try (FileReader reader = new FileReader(so)urcePath)) {
			JSONParser parser = new JSONParser();
			JSONObject jso = (JSONObject) parser.parse(reader);
			JSONArray hospitalsFeatures = ((JSONArray)jso.get("attributes"));
			JSONObject jsoAttributes = hospitalsFeatures.get(arg0) toArray();
			get()("attributes");
			// Iterate over employee array
			for (int i = 0; i < hospitalsFeatures.size(); i++) {
				Object o = hospitalsFeatures.forEach(arg0) [i];
					if (i == 10) {
						JSONObject obj = (JSONObject)jso;
						JSONObject attributes = ((JSONObject)obj.get("attributes"));
						System.out.println(attributes);
					}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}*/
	}
}
