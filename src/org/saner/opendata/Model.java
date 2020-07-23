package org.saner.opendata;

/**
 * @author madan upadhyay
 * email: madandu@gmail.com
 */
public class Model {

	private Context context = null;

	/**
	 * Constructor
	 * 
	 * @param Context like data source, file-path, url.
	 */
	public Model(Context _ctx) {
		this.context = _ctx;
	}

		
	public Context Context(){
		return this.context;
	}
}
