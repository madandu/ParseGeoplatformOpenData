package org.hl7.fhir.saner.data;

public class Context {
	
	private String source = "";
	private Context.Type type = Type.JSON; // default
	
	public enum Type {
	    JSON,
	    XML,
	    CSV,
	    SQL
	}
	
	/**
	 * Constructor
	 * 
	 * @param Context for source/data.
	 */
	public Context(String _source, Context.Type  _type) {
		source = _source;
		type = _type;
	}
	
	public String Source() {		
		return source;
	}
	
	public Context.Type Type(){
		return type;
	}
}
