package org.hl7.fhir.saner.parser;

import org.json.JSONArray;
import org.json.JSONObject;

public class CountyJSONModelParser implements ModelParser {

	private JSONObject data = null;
	/**
	 * Constructor
	 * 
	 * @param absolute
	 *            or relative-path to the source file
	 * @param data
	 *            -type JSON, CSV or XML.
	 */
	public CountyJSONModelParser(JSONObject  _data) {
		data  = _data;
	}
	
	/**
	 * Parses source-file based on its FileType
	 * 
	 * @return prints-out attributes of source-file
	 */
	public void parseData() {
		// TODO Auto-generated method stub
		JSONArray jArr = null;
		jArr = data.getJSONArray("features");
		System.out.println("Begin: listing all hospital]s attributes");
		for (Object item : jArr) {
			if (item instanceof JSONObject){
				JSONObject hospitalAttributes = ((JSONObject)item).getJSONObject("attributes");
				System.out.println(hospitalAttributes);
			}
		}		
		// Test message;
		System.out.println("End.");
	}

	/**
	 * Parses JSON source-file
	 * 
	 * @return 	-out attributes of JSON source-file
	 * @throws exception
	 *             of type java.io.IOException
	 */
	private void JSONParser(JSONObject jso) {
		
		// Parse JSON file
/*		try (FileReader reader = new FileReader(sourcePath)) {
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
