package org.hl7.fhir.saner;

import org.hl7.fhir.saner.model.*;
import org.hl7.fhir.saner.parser.*;

public class Application {

	/** @author Madan (mady)
	 * Use this method to to load and parse HIFLD open-data of USA based hospitals
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Initial use JSONParser to load HIFLD hospitals' open-data.
		String sourcePath = "./data/hifld/hifld-geoplatform.opendata.arcgis.com.api.json";
		ModelParser countyWise = new CountryModelParser(sourcePath, DataModel.Type.JSON);
		countyWise.parseData();
	}
}
