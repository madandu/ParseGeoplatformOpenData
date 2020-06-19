package org.hl7.fhir.saner;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.hl7.fhir.saner.data.Context;
import org.hl7.fhir.saner.data.Model;
import org.hl7.fhir.saner.parser.HIFLDOpenDataParser;

public class ParserWorker extends SwingWorker<String, Integer> {
	
	private Model model; 

	public ParserWorker(Model data){
	this.model = data;
	}
	
	/**
	 * Load and parse HIFLD open-data of USA based hospitals
	 */
	protected String doInBackground() { 
				final HIFLDOpenDataParser hldParser = new HIFLDOpenDataParser(this.model);
				return hldParser.parseData();
	}

	protected void done() { 
	    try  
	    {  
	    	String result = get();
			JOptionPane.showMessageDialog(null,"Parsing finished.\n\r" +result);
	    }  
	    catch (Exception ignore)  
	    { 
	    } 
	}
}
