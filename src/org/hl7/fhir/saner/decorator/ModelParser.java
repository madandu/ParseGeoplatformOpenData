package org.hl7.fhir.saner.decorator;

public interface ModelParser {
		
	public enum FileType {
	    JSON,
	    XML,
	    CSV
	}
	 public void parseData();
}
