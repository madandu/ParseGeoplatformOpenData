package org.hl7.fhir.saner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

		// Read parser results on console.
		try (InputStream inStream = Application.class.getClassLoader().getResourceAsStream("./config.properties")) {
			
			if (inStream == null) {
				System.out.println(" 'config.properties' file not found.");
				return;
			}
			Properties prop = new Properties();
			prop.load(inStream);
			String json = prop.getProperty("json.path");
			String url = prop.getProperty("rest.url");
			
			inStream.close();

			openAndAnalyzeJSONFile(json);
			openAndAnalyzeRESTUrl(url);
			
		} catch (FileNotFoundException e) {
			System.out.println("'config.properties' are not valid.");
		} catch (IOException io) {
			io.printStackTrace();
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