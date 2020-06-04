package org.hl7.fhir.saner;

import org.hl7.fhir.saner.data.*;
import org.hl7.fhir.saner.parser.*;


/**
 * @author madan upadhyay
 * email: madandu@gmail.com
 */
public class Application {

	/** Load and parse HIFLD open-data of USA based hospitals
	 * @param args
	 */
	public static void main(String[] args) {
		
		AnalyzeOpenDataAtRESTUrl();
		
		//AnalyzeOpenDataFromFile(_sourcePath);
	    //Read results on console.
	}
	
	/** Load and parse HIFLD open-data of USA based hospitals
	 */
	private static void AnalyzeOpenDataAtRESTUrl()
	{
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		final String sRESTUrl = "https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson";
		HIFLDOpenDataParser.QueryRESTUrlAndAnalyzeData(sRESTUrl);
	}
	
	/** Load and parse json file @ sourcePath on local file-system
	 */
	private static void AnalyzeOpenDataFromFile(String _sourcePath)
	{
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		if (_sourcePath == null || _sourcePath.isEmpty()){
			_sourcePath = "./data/hifld/hifld-geoplatform.opendata.arcgis.com.api.json";
		}
		final Context ctx = new Context(_sourcePath, Context.Type.JSON);
		final Model model = new Model(ctx);
		final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
		hldParser.parseData();
	}
}