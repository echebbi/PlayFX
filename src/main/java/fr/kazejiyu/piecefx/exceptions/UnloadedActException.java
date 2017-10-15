package fr.kazejiyu.piecefx.exceptions;

public class UnloadedActException extends RuntimeException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -4078168305111418174L;

	public UnloadedActException(String name) {
		super("Unable to load " + name + " : the act has not been loaded");
	}
	
	

}
