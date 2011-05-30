/**
 * 
 */
package uk.co.jemos.protomak.engine.utils;

/**
 * Constants for Protomak Engine.
 * 
 * @author mtedone
 * 
 */
public class ProtomakEngineConstants {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	/** The package name where all classes are being generated */
	public static final String GENERATED_CODE_PACKAGE_NAME = "uk.co.jemos.xsds.protomak.proto";

	/** The default name for a proto message */
	public static final String PROTO_MESSAGE_DEFAULT_NAME = "DefaultMessage";

	/** The extension for proto files. */
	public static final String PROTO_FILE_EXTENSION_NAME = ".proto";

	/** The name of the first complex type returned by XSOM iteration. */
	public static final Object ANY_TYPE_NAME = "anyType";

	/**
	 * The prefix for a default message name in the proto file.
	 * <p>
	 * This constant is used for simple elements defined in an XSD.
	 * </p>
	 */
	public static final String DEFAULT_MESSAGE_NAME = "DefaultMessage";

	/** The suffix for a proto file */
	public static final String PROTO_SUFFIX = ".proto";

	/** The Token {@code message} within a proto file */
	public static final String PROTO_TOKENS_MESSAGE = "message";

	/** The Token {@code package} within a proto file */
	public static final String PACKAGE_TOKEN = "package";

	/** A blank space */
	public static final String WHITE_SPACE = " ";

	/** The default proto file name */
	public static final String DEFAULT_PROTO_FILE_NAME = "default.proto";

	/** An OS system independent new line */
	public static final String NEW_LINE = System.getProperty("line.separator");

	/** The HTTP protocol prefix */
	public static final String HTTP_PREFIX = "http://";

	/** The file protocol prefix */
	public static final String FILE_PREFIX = "file:///";

	/** The proto enum token */
	public static final String ENUM_TOKEN = "enum";

	/** The default enum name for anonymous types */
	public static final String ANONYMOUS_ENUM_DEFAULT_MESSAGE_TYPE_NAME = "AnonymousEnum";

	/** Non instantiable contructor */
	private ProtomakEngineConstants() {
		throw new AssertionError();
	}

	//------------------->> Public methods

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
