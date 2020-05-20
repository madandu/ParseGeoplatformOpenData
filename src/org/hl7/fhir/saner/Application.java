package org.hl7.fhir.saner;

import org.hl7.fhir.saner.data.*;
import org.hl7.fhir.saner.parser.*;

public class Application {

	/** @author Madan (mady)
	 * Use this method to to load and parse HIFLD open-data of USA based hospitals
	 * @param args
	 */
	public static void main(String[] args) {
		
		AnalyzeOpenDataAtRESTUrl();
		
		//ReadOpenDataFromFile();
	    // Read console output.
	}
	
	private static void AnalyzeOpenDataAtRESTUrl()
	{
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		final String sRESTUrl = "https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson";
		HIFLDOpenDataParser.QueryRESTUrlAndAnalyzeData(sRESTUrl);
	}
	
	private static void AnalyzeOpenDataFromFile()
	{
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		final String sRESTUrl = "https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson";

		final String source = "./data/hifld/hifld-geoplatform.opendata.arcgis.com.api.json";
		final Context ctx = new Context(source, Context.Type.JSON);
		final Model model = new Model(ctx);
		final HIFLDOpenDataParser parser = new HIFLDOpenDataParser(model);
	    parser.parseData();
	}
}