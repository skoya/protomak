/**
 * 
 */
package uk.co.jemos.protomak.engine.test.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
