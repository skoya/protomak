/**
 * 
 */
package uk.co.jemos.protomak.engine.exceptions;

/**
 * Wrapper runtime exception thrown when an error occurred during the XSD to
 * Proto file generation.
 * 
 * @author mtedone
 * 
 */
public class ProtomakXsdToProtoConversionError extends RuntimeException {

	//------------------->> Constants

	private static final long serialVersionUID = 1L;

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	/**
	 * Constructor which accepts the originating exception.
	 * 
	 * @param cause
	 *            The cause for this exception.
	 */
	public ProtomakXsdToProtoConversionError(Throwable cause) {
		super(cause);
	}

	//------------------->> Public methods

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
