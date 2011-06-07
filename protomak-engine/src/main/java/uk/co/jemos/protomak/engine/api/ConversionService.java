/**
 * 
 */
package uk.co.jemos.protomak.engine.api;

/**
 * Main contract for conversion services.
 * 
 * @author mtedone
 * 
 */
public interface ConversionService {

	/**
	 * Given an input file, it generates one or more proto files and places them
	 * in the given output folder.
	 * 
	 * @param inputPath
	 *            The full path to an input file to use for the generation of
	 *            proto files.
	 * @param outputPath
	 *            The folder where to place the generated files.
	 */
	public void generateProtoFiles(String inputPath, String outputPath);

}
