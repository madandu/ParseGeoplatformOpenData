package org.hl7.fhir.saner;

import org.hl7.fhir.saner.data.*;
import org.hl7.fhir.saner.parser.*;

/**
 * @author Madan Upadhyay
 * @email: madandu@gmail.com
 */
public class Application {

	/**
	 * Load and parse HIFLD open-data of USA based hospitals
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String dType, dPath = "";

		if (args.length > 1) {
			dType = args[0].toUpperCase();
			dPath = args[1];
			// See parser results on console.
			switch (dType) {
			case "REST":
				openAndAnalyzeRESTUrl(dPath);
				break;
			case "XML":
				break;
				
			case "JSON":
				openAndAnalyzeJSONFile(dPath);
				break;
			default: 
				System.out.println("Valid arguments are: ");
				System.out.println("JSON	'full-path to JSON file' ");
				System.out.println("REST	'full-url of  resource at RESTful service");
			}
		}
		else {
			System.out.println("Valid arguments are: ");
			System.out.println("JSON	'full-path to JSON file' ");
			System.out.println("REST	'full-url of  resource at RESTful service");
			}
	}

	/**
	 * Load and parse HIFLD open-data of USA based hospitals
	 */
	private static void openAndAnalyzeRESTUrl(String sRESTUrl) {

		if (sRESTUrl == null || sRESTUrl.isBlank())
			return;
		else {
			final Context ctx = new Context(sRESTUrl, Context.Type.REST);
			final Model model = new Model(ctx);
			final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
			hldParser.parseData();
		}
	}

	/**
	 * Open and parse json file @ sourcePath on local file-system
	 */
	private static void openAndAnalyzeJSONFile(String _jsonFilePath) {

		if (_jsonFilePath == null || _jsonFilePath.isBlank())
			return;
		else {
			final Context ctx = new Context(_jsonFilePath, Context.Type.JSON);
			final Model model = new Model(ctx);
			final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(model);
			hldParser.parseData();
		}
	}
}