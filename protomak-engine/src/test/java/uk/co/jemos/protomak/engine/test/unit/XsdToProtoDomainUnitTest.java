/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xml.sax.SAXException;

import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.impl.XsomXsdToProtoDomainConversionServiceImpl;
import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;

import com.sun.xml.xsom.parser.XSOMParser;

/**
 * Unit Tests for the conversion of XSDs to Proto files.
 * 
 * @author mtedone
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(XSOMParser.class)
public class XsdToProtoDomainUnitTest {

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

	@Test
	public void testMultipleComplexTypes() throws Exception {

		service.generateProtoFiles(ProtomakEngineTestConstants.MULTIPLE_COMPLEX_TYPES_XSD_PATH,
				ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		File outputDir = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR);

		verifyProtoFilesHaveBeenWritten(outputDir,
				ProtomakEngineTestConstants.MULTIPLE_COMPLEX_TYPES_XSD_PATH);

		File expectedProtoFile = new File(
				ProtomakEngineTestConstants.EXPECTED_MULTIPLE_COMPLEX_TYPES_PROTO_FILE_NAME);
		Assert.assertTrue("The proto file: " + expectedProtoFile + " does not exist!",
				expectedProtoFile.exists());
		StringBuilder expectedOutputBuff = extractBufferFromProtoFile(expectedProtoFile);
		Assert.assertNotNull("The string builder for the expected file cannot be null!",
				expectedOutputBuff);
		File protoFile = new File(
				ProtomakEngineHelper
						.extractProtoFileNameFromXsdName(ProtomakEngineTestConstants.MULTIPLE_COMPLEX_TYPES_XSD_PATH));
		File protoOutputFile = new File(ProtomakEngineTestConstants.PROTOS_OUTPUT_DIR
				+ File.separatorChar + protoFile.getName());
		Assert.assertTrue("The file: " + protoOutputFile.getAbsolutePath() + " must exist!",
				protoOutputFile.exists());
		StringBuilder actualOutputBuff = extractBufferFromProtoFile(protoOutputFile);
		Assert.assertNotNull("The string builder for the actual file cannot be null!",
				actualOutputBuff);
		Assert.assertEquals("The expected and actual proto files do not match!",
				expectedOutputBuff.toString(), actualOutputBuff.toString());

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

	/**
	 * It creates and returns a {@link StringBuilder} from the given proto file
	 * 
	 * @param protoFile
	 *            The proto file
	 * @return a {@link StringBuilder} from the given proto file
	 * @throws IOException
	 *             An exception occurred
	 */
	private StringBuilder extractBufferFromProtoFile(File protoFile) throws IOException {

		StringBuilder buff = new StringBuilder();

		byte[] buf = new byte[512];

		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bis = new BufferedInputStream(new FileInputStream(protoFile));
			int read = 0;
			while ((read = bis.read(buf, 0, buf.length)) >= 0) {
				bos.write(buf, 0, read);
			}
			bos.flush();
			buff.append(new String(bos.toByteArray()));
			return buff;
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bos);
		}

	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
