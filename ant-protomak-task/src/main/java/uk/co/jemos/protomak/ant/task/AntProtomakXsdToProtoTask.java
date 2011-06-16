/**
 * 
 */
package uk.co.jemos.protomak.ant.task;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

import uk.co.jemos.protomak.engine.api.ConversionService;
import uk.co.jemos.protomak.engine.impl.XsomXsdToProtoDomainConversionServiceImpl;

/**
 * Ant task to launch the XSD to Proto conversion.
 * 
 * @author mtedone
 * 
 */
public class AntProtomakXsdToProtoTask extends Task {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	/** The input XSD file */
	private String inputXsdFile;

	/** The output Folder */
	private String outputFolder;

	//------------------->> Constructors

	//------------------->> Public methods

	@Override
	public void execute() throws BuildException {

		log("Invoking XSD to Proto conversion...", Project.MSG_INFO);
		ConversionService conversionService = new XsomXsdToProtoDomainConversionServiceImpl();
		conversionService.generateProtoFiles(inputXsdFile, outputFolder);
		log("XSD to Proto conversion completed...", Project.MSG_INFO);

	}

	// ------------------->> Getters / Setters

	/**
	 * @param inputXsdFile
	 *            the inputXsdFile to set
	 */
	public void setInputXsdFile(String inputXsdFile) {
		this.inputXsdFile = inputXsdFile;
	}

	/**
	 * @param outputFolder
	 *            the outputFolder to set
	 */
	public void setOutputFolder(String outputFolder) {
		this.outputFolder = outputFolder;
	}

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
