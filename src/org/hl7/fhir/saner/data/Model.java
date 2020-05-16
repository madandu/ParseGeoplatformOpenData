package org.hl7.fhir.saner.data;

import org.json.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Model {

	private Context context = null;

	/**
	 * Constructor
	 * 
	 * @param Context
	 *            for source/data.
	 */
	public Model(Context _ctx) {
		context = _ctx;
	}

		
	public Context getContext() {
		return context;
	}

	public JSONObject loadJSON() {
		JSONObject jso = null;

		try (FileReader reader = new FileReader(context.Source())) {

			JSONTokener tokener = new JSONTokener(reader);
			jso = new JSONObject(tokener);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jso;
	}

	//TODO: Load xml content.
	public org.json.XML loadXML() {
		org.json.XML xmo = null;

		try (FileReader reader = new FileReader(context.Source())) {

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return xmo;
	}
}
