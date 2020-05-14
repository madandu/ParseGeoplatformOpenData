package org.hl7.fhir.saner.parser;

public interface ModelParser {
		
	public enum FileType {
	    JSON,
	    XML,
	    CSV
	}
	 public void parseData();
}
