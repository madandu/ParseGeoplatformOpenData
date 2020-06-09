package org.hl7.fhir.saner;

import org.hl7.fhir.saner.data.*;
import org.hl7.fhir.saner.parser.*;

/**
 * @author Madan Upadhyay
 * @email: madandu@gmail.com
 */
public class Application {

	/** Load and parse HIFLD open-data of USA based hospitals
	 * @param args
	 */
	public static void main(String[] args) {
		
		openAndAnalyzeRESTUrl();
		
		//OpenAndAnalyzeFile(_sourcePath);
	    //Read results on console.
	}
	
	/** Load and parse HIFLD open-data of USA based hospitals
	 */
	private static void openAndAnalyzeRESTUrl()
	{
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		final String sRESTUrl = "https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson";
		final Context ctx = new Context(sRESTUrl, Context.Type.REST);
		final Model model = new Model(ctx);
		final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
		hldParser.parseData();
	}
	
	/** Open and parse json file @ sourcePath on local file-system
	 */
	private static void openAndAnalyzeJSONFile(String _jsonFilePath)
	{
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		if (_jsonFilePath == null || _jsonFilePath.isEmpty()){
			// TODO report exception if source FilePath is not found.
			_jsonFilePath = "./data/hifld/hifld-geoplatform.opendata.arcgis.com.api.json";
			
			final Context ctx = new Context(_jsonFilePath, Context.Type.JSON);
			final Model model = new Model(ctx);
			final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
			hldParser.parseData();
		}
	}
}