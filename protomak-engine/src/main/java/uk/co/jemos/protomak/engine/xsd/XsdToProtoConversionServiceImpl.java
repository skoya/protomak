/**
 * 
 */
package uk.co.jemos.protomak.engine.xsd;

import java.io.File;

import uk.co.jemos.protomak.engine.api.ConversionService;

/**
 * XSD to Proto conversion service.
 * 
 * <p>
 * The mail goal of this class is to convert a given XSD to one or more proto
 * files.
 * </p>
 * 
 * @author mtedone
 * 
 */
public class XsdToProtoConversionServiceImpl implements ConversionService {

	//------------------->> Constants

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(XsdToProtoConversionServiceImpl.class);

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	//------------------->> Public methods

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             If the {@code inputPath} does not exist.
	 */
	public void generateProtoFiles(String inputPath, String outputPath) {

		File inputFilePath = new File(inputPath);
		if (!inputFilePath.exists()) {
			String errMsg = "The XSD input file: " + inputFilePath.getAbsolutePath()
					+ " does not exist. Throwing an exception.";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
