/**
 * 
 */
package uk.co.jemos.protomak.engine.api;

import java.io.File;

import uk.co.jemos.protomak.engine.exceptions.ProtomakEngineSerialisationError;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

/**
 * General contract for services which ultimately produce proto files
 * 
 * @author mtedone
 * 
 */
public interface ProtoSerialisationService {

	/**
	 * Serialise the {@link ProtoType} domain model object to an output
	 * directory.
	 * 
	 * @param fileName
	 *            The name to give to the proto file
	 * @param outputPath
	 *            The full path to the output folder. If the folder does not
	 *            exist, Protomak will create it.
	 * @param proto
	 *            The root of the Proto domain model
	 * @throws ProtomakEngineSerialisationError
	 *             If an error occured during serialisation
	 */
	public void writeProtoFile(String fileName, File outputPath, ProtoType proto)
			throws ProtomakEngineSerialisationError;

}
