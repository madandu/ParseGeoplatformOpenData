package org.hl7.fhir.saner.parser;

import org.hl7.fhir.saner.data.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.XML;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class HIFLDOpenDataParser implements ModelParser {

	private Model model = null;

	/**
	 * Constructor
	 * 
	 * @param absolute
	 *            or relative-path to the source file
	 * @param data
	 *            -type JSON, CSV or XML.
	 */
	public HIFLDOpenDataParser(Model _data) {
		this.model = _data;
	}

	/**
	 * Parses source-file based on data-type
	 * 
	 * @return prints-out attributes of source-file
	 */
	public void parseData() {

		switch (this.model.Context().Type()) {
		case JSON:
			ParseJSON();
			break;

		case XML:
			ParseXML();
			break;

		default:
			break;
		}
		// Test message;
	}

	/**
	 * Parses JSONObject Prints out all hospitals' attributes within JSONObject.
	 */
	private void ParseJSON() {

		JSONObject jso = null;

		try (FileReader reader = new FileReader(this.model.Context().Source())) {
			JSONTokener tokener = new JSONTokener(reader);
			jso = new JSONObject(tokener);

			JSONArray jArr = null;
			jArr = jso.getJSONArray("features");
			System.out.println("Begin: listing all hospital]s attributes");

			for (Object item : jArr) {
				if (item instanceof JSONObject) {
					JSONObject hospitalAttributes = ((JSONObject) item)
							.getJSONObject("attributes");
					System.out.println(hospitalAttributes);
				}
			}

			System.out.println("End.");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses XML Prints out all hospitals' attributes within XMLObject.
	 */
	private void ParseXML() {

		XML xmo = null;
		try (FileReader reader = new FileReader(this.model.Context().Source())){

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * try (FileReader reader = new FileReader(so)urcePath)) { JSONParser
		 * parser = new JSONParser(); JSONObject jso = (JSONObject)
		 * parser.parse(reader); JSONArray hospitalsFeatures =
		 * ((JSONArray)jso.get("attributes")); JSONObject jsoAttributes =
		 * hospitalsFeatures.get(arg0) toArray(); get()("attributes"); //
		 * Iterate over employee array for (int i = 0; i <
		 * hospitalsFeatures.size(); i++) { Object o =
		 * hospitalsFeatures.forEach(arg0) [i]; if (i == 10) { JSONObject obj =
		 * (JSONObject)jso; JSONObject attributes =
		 * ((JSONObject)obj.get("attributes")); System.out.println(attributes);
		 * } } } catch (FileNotFoundException e) { e.printStackTrace(); } catch
		 * (IOException e) { e.printStackTrace(); } catch (ParseException e) {
		 * e.printStackTrace(); }
		 */
	}
}
