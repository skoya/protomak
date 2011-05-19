/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import org.junit.Test;

import uk.co.jemos.protomak.engine.api.ConversionService;
import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.xsd.XsdToProtoConversionServiceImpl;

/**
 * Unit Tests for the conversion of XSDs to Proto files.
 * 
 * @author mtedone
 * 
 */
public class XsdToProtoUnitTest {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	//------------------->> Public methods

	@Test(expected = IllegalArgumentException.class)
	public void testConversionEngineForNonExistingXsdFile() {

		ConversionService service = new XsdToProtoConversionServiceImpl();
		String xsdFilePath = ProtomakEngineTestConstants.NON_EXISTING_FILE_PATH;
		String outputPath = ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR;

		service.generateProtoFiles(xsdFilePath, outputPath);

	}
	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
