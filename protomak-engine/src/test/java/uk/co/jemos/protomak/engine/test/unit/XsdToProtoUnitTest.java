/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.protomak.engine.api.ProtoSerialisationService;
import uk.co.jemos.protomak.engine.exceptions.ProtomakEngineSerialisationError;
import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.impl.PojoToProtoSerialisationServiceImpl;
import uk.co.jemos.protomak.engine.impl.XsomXsdToProtoDomainConversionServiceImpl;
import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

import com.sun.xml.xsom.parser.XSOMParser;

/**
 * Unit Tests for the conversion of XSDs to Proto files.
 * 
 * @author mtedone
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(XSOMParser.class)
public class XsdToProtoUnitTest {

	//------------------->> Constants

	/** A filter to extract .proto files from a folder */
	private static final FilenameFilter filter = new FilenameFilter() {

		public boolean accept(File dir, String name) {
			return name.endsWith(ProtomakEngineConstants.PROTO_FILE_EXTENSION_NAME);
		}
	};

	//------------------->> Instance / Static variables

	private XsomXsdToProtoDomainConversionServiceImpl service;

	//------------------->> Constructors

	//------------------->> Public methods

	@Before
	public void init() {
		service = new XsomXsdToProtoDomainConversionServiceImpl();
		XSOMParser parser = new XSOMParser();
		service.setParser(parser);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConversionEngineForNonExistingXsdFile() {

		service.generateProtoFiles(ProtomakEngineTestConstants.NON_EXISTING_FILE_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);
	}

	@Test(expected = ProtomakXsdToProtoConversionError.class)
	public void testConversionEngineWithIOException() throws Exception {

		File xsdSchema = new File(ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH);

		XSOMParser parserMock = PowerMock.createMock(XSOMParser.class);

		service.setParser(parserMock);
		parserMock.parse(xsdSchema);
		EasyMock.expectLastCall().andThrow(new IOException("Mocked Sax exception"));

		PowerMock.replay(parserMock);

		try {

			service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH,
					ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		} finally {

			PowerMock.verify(parserMock);
		}

	}

	@Test(expected = ProtomakXsdToProtoConversionError.class)
	public void testConversionEngineWithSaxException() throws Exception {

		File xsdSchema = new File(ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH);

		XSOMParser parserMock = PowerMock.createMock(XSOMParser.class);

		service.setParser(parserMock);
		parserMock.parse(xsdSchema);
		EasyMock.expectLastCall().andThrow(new SAXException("Mocked Sax exception"));

		PowerMock.replay(parserMock);

		try {

			service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH,
					ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		} finally {

			PowerMock.verify(parserMock);
		}

	}

	@Test
	public void testSimpleSingleElementXsd() {
		service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		File outputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		verifyProtoFilesHaveBeenWritten(outputDir,
				ProtomakEngineTestConstants.SIMPLE_SINGLE_ELEMENT_XSD_PATH);

	}

	@Test
	public void testSimpleMultipleElementsXsd() {

		service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_MULTIPLE_ELEMENTS_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		File outputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		verifyProtoFilesHaveBeenWritten(outputDir,
				ProtomakEngineTestConstants.SIMPLE_MULTIPLE_ELEMENTS_XSD_PATH);

	}

	@Test
	public void testSingleElementWithComplexType() {
		service.generateProtoFiles(
				ProtomakEngineTestConstants.SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		File outputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		verifyProtoFilesHaveBeenWritten(outputDir,
				ProtomakEngineTestConstants.SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH);

	}

	@Test
	public void testSimpleOneLevelXsd() {

		service.generateProtoFiles(ProtomakEngineTestConstants.SIMPLE_ONE_LEVEL_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		File outputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		verifyProtoFilesHaveBeenWritten(outputDir,
				ProtomakEngineTestConstants.SIMPLE_ONE_LEVEL_XSD_PATH);

	}

	@Test(expected = ProtomakEngineSerialisationError.class)
	public void testSerialisationServiceWhenFolderAlreadyExists() {

		ProtoSerialisationService service = PojoToProtoSerialisationServiceImpl.getInstance();

		File fileMock = EasyMock.createMock(File.class);
		EasyMock.expect(fileMock.exists()).andReturn(false);
		EasyMock.expect(fileMock.mkdirs()).andReturn(false);

		EasyMock.replay(fileMock);

		PodamFactory factory = new PodamFactoryImpl();
		ProtoType pojo = factory.manufacturePojo(ProtoType.class);
		try {
			service.writeProtoFile("foo", fileMock, pojo);
		} finally {
			EasyMock.verify(fileMock);
		}

	}

	@Test(expected = ProtomakEngineSerialisationError.class)
	public void testSerialisationServiceForFileNotFoundException() {

		ProtoSerialisationService service = PojoToProtoSerialisationServiceImpl.getInstance();

		File fileMock = EasyMock.createMock(File.class);
		EasyMock.expect(fileMock.exists()).andReturn(true);
		EasyMock.expect(fileMock.getAbsolutePath()).andReturn("fooPath");

		EasyMock.replay(fileMock);

		PodamFactory factory = new PodamFactoryImpl();
		ProtoType pojo = factory.manufacturePojo(ProtoType.class);
		try {
			service.writeProtoFile("foo", fileMock, pojo);
		} finally {
			EasyMock.verify(fileMock);
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * It verifies that the output folder has been created and at least one
	 * proto file has been written to it.
	 * 
	 * @param simpleSingleElementXsdPath
	 * 
	 * @param outputDir2
	 */
	private void verifyProtoFilesHaveBeenWritten(File outputDir, String inputFilePath) {

		Assert.assertTrue("The output folder must exist!", outputDir.exists());
		Assert.assertTrue("The output folder must be a folder!", outputDir.isDirectory());
		File[] listFiles = outputDir.listFiles(filter);
		Assert.assertNotNull("The list of proto files cannot be null!", listFiles);
		Assert.assertTrue("There must be at least a .proto file in the output folder",
				listFiles.length > 0);

		File inputFile = new File(inputFilePath);

		String protoFileName = ProtomakEngineHelper.extractProtoFileNameFromXsdName(inputFile
				.getName());

		File protoFile = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR
				+ File.separatorChar + protoFileName);
		Assert.assertTrue("The file: " + protoFile.getAbsolutePath() + " does not exist.",
				protoFile.exists());

	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
