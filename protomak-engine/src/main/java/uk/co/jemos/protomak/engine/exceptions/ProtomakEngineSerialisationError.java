/**
 * 
 */
package uk.co.jemos.protomak.engine.exceptions;

/**
 * Error which occurs during serialisation of the proto domain model.
 * 
 * @author mtedone
 * 
 */
public class ProtomakEngineSerialisationError extends RuntimeException {

	//------------------->> Constants

	private static final long serialVersionUID = 1L;

	//------------------->> Constructors

	/**
	 * It creates an exception with the error message
	 * 
	 * @param errorMessage
	 *            The error message
	 */
	public ProtomakEngineSerialisationError(String errorMessage) {
		super(errorMessage);
	}

	/**
	 * It creates an exception with error message and error cause
	 * 
	 * @param errorMessage
	 *            The error message
	 * @param cause
	 *            The error cause
	 */
	public ProtomakEngineSerialisationError(String errorMessage, Throwable cause) {
		super(errorMessage, cause);
	}

}
