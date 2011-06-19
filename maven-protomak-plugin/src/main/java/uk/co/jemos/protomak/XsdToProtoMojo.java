package uk.co.jemos.protomak;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import uk.co.jemos.protomak.engine.api.ConversionService;
import uk.co.jemos.protomak.engine.impl.XsomXsdToProtoDomainConversionServiceImpl;

/**
 * Goal which triggers the generation of proto files from an XSD file
 * 
 * @goal xsd-to-proto
 * 
 * @phase process-sources
 * 
 * @requiresDependencyResolution compile
 */
public class XsdToProtoMojo extends AbstractMojo {

	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${inputXsdFile}"
	 * @required
	 */
	private String inputXsdFile;

	/**
	 * The location where the generated files will be placed.
	 * 
	 * @parameter expression="${outputFolder}"
	 * @required
	 */
	private String outputFolder;

	public void execute() throws MojoExecutionException {

		//Will use the default serialisation strategy
		ConversionService conversionService = new XsomXsdToProtoDomainConversionServiceImpl();
		getLog().info("Invoking the XSOM conversion from XSD to Proto...");
		conversionService.generateProtoFiles(inputXsdFile, outputFolder);
		getLog().info("XSOM conversion from XSD to Proto completed successfully");

	}
}
