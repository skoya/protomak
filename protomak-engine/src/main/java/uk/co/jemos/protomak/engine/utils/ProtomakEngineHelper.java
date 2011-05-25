/**
 * 
 */
package uk.co.jemos.protomak.engine.utils;

import java.util.HashMap;
import java.util.Map;

import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageRuntimeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoRuntimeType;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSType;

/**
 * Helper class for Protomak Engine.
 * 
 * @author mtedone
 * 
 */
public class ProtomakEngineHelper {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	/** Mapping between XSD and Proto types */
	public static final Map<String, ProtoRuntimeType> XSD_TO_PROTO_TYPE_MAPPING = new HashMap<String, ProtoRuntimeType>();

	static {

		XSD_TO_PROTO_TYPE_MAPPING.put("boolean", ProtoRuntimeType.BOOL);
		XSD_TO_PROTO_TYPE_MAPPING.put("float", ProtoRuntimeType.FLOAT);
		XSD_TO_PROTO_TYPE_MAPPING.put("double", ProtoRuntimeType.DOUBLE);
		XSD_TO_PROTO_TYPE_MAPPING.put("integer", ProtoRuntimeType.SFIXED_32);
		XSD_TO_PROTO_TYPE_MAPPING.put("int", ProtoRuntimeType.SFIXED_32);
		XSD_TO_PROTO_TYPE_MAPPING.put("long", ProtoRuntimeType.SFIXED_64);
		XSD_TO_PROTO_TYPE_MAPPING.put("short", ProtoRuntimeType.SFIXED_32);
		XSD_TO_PROTO_TYPE_MAPPING.put("byte", ProtoRuntimeType.SFIXED_32);
		XSD_TO_PROTO_TYPE_MAPPING.put("string", ProtoRuntimeType.STRING);
		XSD_TO_PROTO_TYPE_MAPPING.put("base64Binary", ProtoRuntimeType.BYTES);
		XSD_TO_PROTO_TYPE_MAPPING.put("hexBinary", ProtoRuntimeType.BYTES);

	}

	//------------------->> Constructors

	/** Non instantiable constructor */
	private ProtomakEngineHelper() {
		throw new AssertionError();
	}

	//------------------->> Public methods

	/**
	 * Given an XSAttributeUse it creates and returns a
	 * {@link MessageAttributeType}
	 * 
	 * @param protoCounter
	 *            The counter to associate with the given attribute.
	 * @param xsdAttribute
	 *            The {@link XSAttributeUse} used to create a
	 *            {@link MessageAttributeType}.
	 * 
	 * @return A {@link MessageAttributeType}
	 */
	public static MessageAttributeType convertXsomAttributeToMessageAttributeType(int protoCounter,
			XSAttributeUse xsdAttribute) {

		MessageAttributeType retValue = new MessageAttributeType();
		//FIXME Sort out how to fill repeated
		if (xsdAttribute.isRequired()) {
			retValue.setOptionality(MessageAttributeOptionalType.REQUIRED);
		} else {
			retValue.setOptionality(MessageAttributeOptionalType.OPTIONAL);
		}

		XSAttributeDecl attributeDeclaration = xsdAttribute.getDecl();
		retValue.setName(attributeDeclaration.getName());

		MessageRuntimeType runtimeType = new MessageRuntimeType();
		String attributeTypeName = attributeDeclaration.getType().getName();
		ProtoRuntimeType protoRuntimeType = XSD_TO_PROTO_TYPE_MAPPING.get(attributeTypeName);
		if (null != protoRuntimeType) {
			runtimeType.setProtoType(protoRuntimeType);
		} else {
			runtimeType.setCustomType(attributeTypeName);
		}

		retValue.setRuntimeType(runtimeType);
		retValue.setIndex(protoCounter);

		return retValue;
	}

	/**
	 * Given an XSD element declaration it manufactures and returns a
	 * {@link MessageAttributeType}.
	 * 
	 * @param element
	 *            The XSD element
	 * @param messageSuffix
	 *            The messageSuffix
	 * @return A {@link MessageType}
	 */
	public static MessageAttributeType getMessageTypeForElement(XSElementDecl element,
			int messageSuffix) {

		MessageAttributeType msgAttrType = new MessageAttributeType();
		msgAttrType.setName(element.getName());
		msgAttrType.setIndex(1);//Always one attribute per element
		//For single elements it appears there are no other options than required
		msgAttrType.setOptionality(MessageAttributeOptionalType.REQUIRED);
		XSType elementType = element.getType();
		MessageRuntimeType runtimeType = new MessageRuntimeType();
		if (elementType.isSimpleType()) {
			ProtoRuntimeType protoRuntimeType = ProtomakEngineHelper.XSD_TO_PROTO_TYPE_MAPPING
					.get(elementType.getName());
			if (null == protoRuntimeType) {
				throw new IllegalStateException(
						"For the XSD type: "
								+ elementType.getName()
								+ " no mapping could be found in ProtomakEngineHelper.XSD_TO_PROTO_TYPE_MAPPING");
			}

			runtimeType.setProtoType(protoRuntimeType);
		} else {
			runtimeType.setCustomType(elementType.getName());
		}

		msgAttrType.setRuntimeType(runtimeType);

		return msgAttrType;
	}

	/**
	 * Given an input XSD file name it returns the same file with the proto
	 * extension.
	 * 
	 * </p> If the file name has got an invalid format (e.g. it does not end
	 * with a {@code .extension} this method returns a default value </p>
	 * 
	 * @param inputPath
	 *            The XSD input file name
	 * @return The proto file name
	 */
	public static String extractProtoFileNameFromXsdName(String inputPath) {

		String retValue = ProtomakEngineConstants.DEFAULT_PROTO_FILE_NAME;

		int idx = inputPath.lastIndexOf(".");
		if (idx >= 0) {
			String fileName = inputPath.substring(0, idx);
			retValue = fileName + ProtomakEngineConstants.PROTO_FILE_EXTENSION_NAME;
		}

		return retValue;
	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
