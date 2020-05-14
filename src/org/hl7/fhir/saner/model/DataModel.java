package org.hl7.fhir.saner.model;

public class DataModel {

	public enum Type {
	    JSON,
	    XML,
	    CSV
	}
	
	private String source;
	private Type type;
}
