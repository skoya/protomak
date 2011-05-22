/**
 * 
 */
package uk.co.jemos.protomak.engine.utils;

import java.util.HashMap;
import java.util.Map;

import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageRuntimeType;
import uk.co.jemos.xsds.protomak.proto.ProtoRuntimeType;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;

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

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
