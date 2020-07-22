package org.saner.opendata;

/**
 * @author Madan Upadhyay
 * email: madandu@gmail.com
 */
public class Context {
	
	private String source = "";
	private Context.Type type = Type.JSON; // default
	
	public enum Type {
	    JSON,
	    REST,
	    FHIR,
	    XML,
	    CSV
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
