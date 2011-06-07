/**
 * 
 */
package uk.co.jemos.protomak.engine.test.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.io.IOUtils;

/**
 * Helper class for tests
 * 
 * @author mtedone
 * 
 */
public class ProtomakEngineTestHelper {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	private ProtomakEngineTestHelper() {
	}

	//------------------->> Public methods

	/**
	 * It returns a {@link String} with the content of the file whose path has
	 * been given as argument.
	 * 
	 * @param inputFilePath
	 *            The full path to the file to retrieve.
	 * @return a {@link String} with the content of the file whose path has been
	 *         given as argument.
	 * 
	 * @throws IllegalArgumentException
	 *             If the file path given as argument does not exist.
	 * 
	 * @throws IllegalStateException
	 *             If an error occurred while reading the file content.
	 */
	public static String retrieveFileContent(String inputFilePath) {

		String retValue = null;

		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] buf = new byte[512];

		int read = 0;

		try {
			bis = new BufferedInputStream(new FileInputStream(new File(inputFilePath)));
			while ((read = bis.read(buf, 0, buf.length)) >= 0) {
				bos.write(buf, 0, read);
			}
			bos.flush();
			retValue = new String(bos.toByteArray());
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("The file: " + inputFilePath + " does not exist!");
		} catch (IOException e) {
			throw new IllegalStateException("If an error occurred while reading the file");
		} finally {
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(bos);
		}

		return retValue;
	}

	/**
	 * It compares expected and actual proto and throws an exception if the
	 * content does not match.
	 * 
	 * @param expectedProtoFileContent
	 *            The content of the expected proto file.
	 * @param actualProtoFileContent
	 *            The content of the actual proto file.
	 * @throws IOException
	 *             If an exception occurred while reading the stream.
	 * @throws AssertionError
	 *             If a single line of the actual proto file does not match the
	 *             single line of the expected proto file.
	 */
	public static void compareExpectedAndActualProtos(String expectedProtoFileContent,
			String actualProtoFileContent) throws IOException {
		BufferedReader expectedReader = new BufferedReader(new StringReader(
				expectedProtoFileContent));

		BufferedReader actualReader = new BufferedReader(new StringReader(actualProtoFileContent));

		try {

			String expectedLine = null;
			String actualLine = null;

			while ((expectedLine = expectedReader.readLine()) != null) {
				actualLine = actualReader.readLine();
				if (expectedLine.equals("")) {
					continue;
				}
				if (!expectedLine.equals(actualLine)) {
					throw new AssertionError("The expected and actual line don't match: "
							+ expectedLine + " vs " + actualLine);
				}
			}

		} finally {
			IOUtils.closeQuietly(expectedReader);
			IOUtils.closeQuietly(actualReader);
		}
	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
