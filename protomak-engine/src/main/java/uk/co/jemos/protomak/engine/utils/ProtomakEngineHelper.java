/**
 * 
 */
package uk.co.jemos.protomak.engine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(ProtomakEngineHelper.class);

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

	/**
	 * Given a target name space, it returns a proto package.
	 * <p>
	 * This method converts an XSD target namespace to a proto package name,
	 * following the rules specified <a href=
	 * "http://download.oracle.com/javase/tutorial/java/package/namingpkgs.html"
	 * >here</a>
	 * </p>
	 * 
	 * <p>
	 * Details of the proto language and the proto package format can be found
	 * <a href=
	 * "http://code.google.com/apis/protocolbuffers/docs/proto.html#packages"
	 * >online</a>
	 * </p>
	 * 
	 * <p>
	 * </p>
	 * 
	 * <p>
	 * Generally: <br/>
	 * <ul>
	 * <li>Forward slashes will be replaced by dots</li>
	 * <li>Hyphens will be replaced by underscores</li>
	 * <li>The server part of a URI, when present, will be reversed. So
	 * www.jemos.eu will be written as eu.jemos.www</li>
	 * </ul>
	 * </p>
	 * 
	 * 
	 * 
	 * @param targetNameSpace
	 *            A target name space to convert into a proto package name.
	 * @return A package name in proto format.
	 * 
	 * @throws IllegalArgumentException
	 *             If the target name space is null or it violates naming
	 *             standards
	 */
	public static String convertTargetNsToProtoPackageName(String targetNameSpace) {

		validatePackageName(targetNameSpace);

		targetNameSpace = targetNameSpace.toLowerCase().trim();

		List<String> packageNameTokens = new ArrayList<String>();

		StringBuilder buff = new StringBuilder();

		if (targetNameSpace.startsWith(ProtomakEngineConstants.HTTP_PREFIX)) {
			targetNameSpace = targetNameSpace.substring(targetNameSpace
					.indexOf(ProtomakEngineConstants.HTTP_PREFIX)
					+ ProtomakEngineConstants.HTTP_PREFIX.length());

			int serverTokenIdx = targetNameSpace.indexOf("/");
			if (serverTokenIdx >= 0) {
				String serverToken = targetNameSpace.substring(0, serverTokenIdx);
				String[] serverTokenParts = serverToken.split("\\.");
				for (int i = serverTokenParts.length - 1; i >= 0; i--) {
					packageNameTokens.add(serverTokenParts[i]);
				}

				targetNameSpace = targetNameSpace.substring(serverTokenIdx + 1);
			}

		}

		LOG.debug("After removing protocol, target ns is: " + targetNameSpace);

		String[] packageTokens = targetNameSpace.split("/");
		String packageToken = null;
		for (int i = packageTokens.length - 1; i >= 0; i--) {
			packageToken = packageTokens[i];
			packageToken = packageToken.replace('-', '_');
			packageNameTokens.add(0, packageToken);
		}

		for (int i = 0; i < packageNameTokens.size(); i++) {
			buff.append(packageNameTokens.get(i));
			if (i + 1 < packageNameTokens.size()) {
				buff.append(".");
			}
		}

		LOG.info("Returning target namespace: " + buff.toString());
		return buff.toString();

	}

	/**
	 * Given a min and max occurrence, it returns the appropriate
	 * {@link MessageAttributeOptionalType}.
	 * 
	 * @param minOccurs
	 *            The XSD element {@code minOccurs} element.
	 * @param maxOccurs
	 *            The XSD element {@code maxOccurs} element.
	 * @return The appropriate {@link MessageAttributeOptionalType} for the
	 *         given parameters.
	 * 
	 * @throws IllegalArgumentException
	 *             If min or max occurs have got invalid values.
	 */
	public static MessageAttributeOptionalType getMessageAttributeOptionality(int minOccurs,
			int maxOccurs) {

		if (minOccurs < 0 || minOccurs > 1) {
			String errMsg = "minOccurs must either be 0 or 1 but it was: " + minOccurs;
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		if (maxOccurs < -1 || maxOccurs > 1) {
			String errMsg = "maxOccurs must either be -1, 0 or 1 but it was: " + maxOccurs;
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		MessageAttributeOptionalType retValue = null;

		if (minOccurs == 0) {
			if (maxOccurs == 1) {
				retValue = MessageAttributeOptionalType.OPTIONAL;
			} else {
				retValue = MessageAttributeOptionalType.REPEATED;
			}
		} else { //minOccurs = 1

			if (maxOccurs == 1) {
				retValue = MessageAttributeOptionalType.REQUIRED;
			} else {
				retValue = MessageAttributeOptionalType.REPEATED;
			}

		}

		return retValue;
	}

	//------------------->> Private methods

	// ------------------->> Getters / Setters

	/**
	 * It validates the package name and throws an exception if the package name
	 * violates the standards.
	 * 
	 * @param targetNameSpace
	 *            The package name to validate
	 * 
	 * @throws IllegalArgumentException
	 *             If the package name does not adhere to standard naming
	 *             conventions, as specified <a href=
	 *             "http://download.oracle.com/javase/tutorial/java/package/namingpkgs.html"
	 *             >here</a>
	 */
	private static void validatePackageName(String targetNameSpace) {

		String errMsg = null;

		if (null == targetNameSpace || "".equals(targetNameSpace)) {
			errMsg = "Target name space cannot be null or empty";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		if (targetNameSpace.startsWith(".")) {
			errMsg = "The target name space " + targetNameSpace + " cannot start with a dot (.)";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		if (targetNameSpace.endsWith(".")) {
			errMsg = "The target name space " + targetNameSpace + " cannot end with a dot (.)";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		if (Character.isDigit(targetNameSpace.charAt(0))) {
			errMsg = "The target name space: " + targetNameSpace + " cannot start with a digit";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
