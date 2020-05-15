package org.hl7.fhir.saner;

import org.hl7.fhir.saner.data.*;
import org.hl7.fhir.saner.parser.*;
import org.json.JSONObject;

public class Application {

	/** @author Madan (mady)
	 * Use this method to to load and parse HIFLD open-data of USA based hospitals
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		String source = "./data/hifld/hifld-geoplatform.opendata.arcgis.com.api.json";
		Model model = new Model(new Context(source, Context.Type.JSON));
		JSONObject obj = (JSONObject) model.load();
	    CountyJSONModelParser parser = new CountyJSONModelParser(obj);
	    parser.parseData();
	    // Read console output.
	}
}