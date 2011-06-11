/**
 * 
 */
package uk.co.jemos.protomak.engine.api;

import java.util.List;

import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.xsds.protomak.proto.MessageType;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSType;

/**
 * Contract for complex type processors based on XSOM.
 * <p>
 * The role of a complex type processor is to process a {@link XSComplexType}
 * and to return a {@link MessageType}.
 * </p>
 * 
 * @author mtedone
 * 
 */
public interface XsomComplexTypeProcessor {

	/**
	 * It creates and returns a {@link MessageType} for the given
	 * {@link XSComplexType}.
	 * 
	 * @param protoMessages
	 *            The list of proto {@link MessageType}s
	 * 
	 * @param complexType
	 *            The complex type to convert into a message type.
	 * @param inputPath
	 *            The full path to the input XSD file
	 * @return A {@link MessageType}
	 * @throws ProtomakXsdToProtoConversionError
	 *             If an exception occurred while converting a
	 *             {@link XSComplexType} into a {@link MessageType}
	 */
	public MessageType processComplexType(List<MessageType> protoMessages, XSType complexType,
			String inputPath) throws ProtomakXsdToProtoConversionError;

}
