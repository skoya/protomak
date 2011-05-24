/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import java.io.File;
import java.io.FilenameFilter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import uk.co.jemos.protomak.engine.api.ConversionService;
import uk.co.jemos.protomak.engine.impl.XsomXsdToProtoConversionServiceImpl;
import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;

/**
 * Unit Tests for the conversion of XSDs to Proto files.
 * 
 * @author mtedone
 * 
 */
public class XsdToProtoUnitTest {

	//------------------->> Constants

	/** A filter to extract .proto files from a folder */
	private static final FilenameFilter filter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			return name.endsWith(ProtomakEngineConstants.PROTO_FILE_EXTENSION_NAME);
		}
	};

	//------------------->> Instance / Static variables

	private ConversionService service;

	//------------------->> Constructors

	//------------------->> Public methods

	@Before
	public void init() {
		service = new XsomXsdToProtoConversionServiceImpl();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConversionEngineForNonExistingXsdFile() {

		service.generateProtoFiles(ProtomakEngineTestConstants.NON_EXISTING_FILE_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

	}

	@Test
	public void testSimpleSingleElementXsd() {
		service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		File protosOutputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		Assert.assertTrue("The output folder: " + ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR
				+ " should exist!", protosOutputDir.exists() && protosOutputDir.isDirectory());

		//TODO To test that .proto files exist in output folder
	}

	@Test
	public void testSimpleMultipleElementsXsd() {

		service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_MULTIPLE_ELEMENTS_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		File protosOutputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		Assert.assertTrue("The output folder: " + ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR
				+ " should exist!", protosOutputDir.exists() && protosOutputDir.isDirectory());

		//TODO To test that .proto files exist in output folder

	}

	@Test
	public void testSingleElementWithComplexType() {
		service.generateProtoFiles(
				ProtomakEngineTestConstants.SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		File protosOutputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		Assert.assertTrue("The output folder: " + ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR
				+ " should exist!", protosOutputDir.exists() && protosOutputDir.isDirectory());

		//TODO To test that .proto files exist in output folder
	}

	@Test
	public void testSimpleOneLevelXsd() {

		service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_ONE_LEVEL_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		File protosOutputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
		Assert.assertTrue("The output folder: " + ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR
				+ " should exist!", protosOutputDir.exists() && protosOutputDir.isDirectory());

		//TODO To test that .proto files exist in output folder

	}
	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
