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
		
		//Default is read and parse JSON from local file-path		
		openAndAnalyzeJSONFile("");
		
		//openAndAnalyzeRESTUrl("");
		
	    //Read results on console.
	}
	
	/** Load and parse HIFLD open-data of USA based hospitals
	 */
	private static void openAndAnalyzeRESTUrl(String sRESTUrl)
	{
		// Use default url if no url provided.
		if (sRESTUrl == null || sRESTUrl.isEmpty())
		sRESTUrl = "https://opendata.arcgis.com/datasets/6ac5e325468c4cb9b905f1728d6fbf0f_0.geojson";
		
		final Context ctx = new Context(sRESTUrl, Context.Type.REST);
		final Model model = new Model(ctx);
		final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
		hldParser.parseData();
	}
	
	/** Open and parse json file @ sourcePath on local file-system
	 */
	private static void openAndAnalyzeJSONFile(String _jsonFilePath)
	{
		// Use default local-file no filePath provided.
		if (_jsonFilePath == null || _jsonFilePath.isEmpty()){
			_jsonFilePath = "data/hifld-geoplatform.opendata.arcgis.com.api.json";			
			// TODO report exception if source FilePath is not found.

			final Context ctx = new Context(_jsonFilePath, Context.Type.JSON);
			final Model model = new Model(ctx);
			final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
			hldParser.parseData();
		}
	}
}