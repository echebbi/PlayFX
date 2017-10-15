package fr.kazejiyu.piecefx.exceptions;

import fr.kazejiyu.piecefx.Act;

/**
 * Thrown when one attempt to access or use an {@link Act} that
 * does not have been loaded yet.
 * 
 * @author Emmanuel CHEBBI
 */
public class UnloadedActException extends RuntimeException {

	/**
	 * Generated serial ID
	 */
	private static final long serialVersionUID = -4078168305111418174L;

	public UnloadedActException(String name) {
		super("Unable to load " + name + " : the act has not been loaded");
	}

}
