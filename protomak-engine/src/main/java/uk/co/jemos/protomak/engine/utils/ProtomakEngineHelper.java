/**
 * 
 */
package uk.co.jemos.protomak.engine.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageRuntimeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoRuntimeType;

import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSimpleType;
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

	/** A repository of anonymous type names bound to the XSD input file name */
	private static final ConcurrentMap<String, String> ANONYMOUS_TYPES_CACHE = new ConcurrentHashMap<String, String>();

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
	 * @param messageAttributeOrdinal
	 *            The proto index of this message attribute within a
	 *            {@link MessageType}.
	 * @param attributeOptionality
	 *            The optionality to assign to this message attribute.
	 * @return A {@link MessageType}
	 */
	public static MessageAttributeType getMessageAttribute(XSElementDecl element,
			int messageAttributeOrdinal, MessageAttributeOptionalType attributeOptionality) {

		MessageAttributeType msgAttrType = new MessageAttributeType();
		msgAttrType.setName(element.getName());
		msgAttrType.setIndex(messageAttributeOrdinal);
		msgAttrType.setOptionality(attributeOptionality);
		XSType elementType = element.getType();
		MessageRuntimeType runtimeType = new MessageRuntimeType();

		if (elementType.isComplexType()) {

			runtimeType.setCustomType(elementType.getName());

		} else {

			XSSimpleType simpleType = elementType.asSimpleType();

			if (simpleType.isRestriction() && simpleType.isLocal()) {

				runtimeType
						.setCustomType(ProtomakEngineConstants.ANONYMOUS_ENUM_DEFAULT_MESSAGE_TYPE_NAME
								+ messageAttributeOrdinal);

			} else {

				ProtoRuntimeType protoRuntimeType = ProtomakEngineHelper.XSD_TO_PROTO_TYPE_MAPPING
						.get(elementType.getName());
				if (null == protoRuntimeType) {
					LOG.debug("For element: " + element.getName() + " the SimpleType: "
							+ elementType.getName() + " appears to be custom.");
					//This is a custom SimpleType
					runtimeType.setCustomType(elementType.getName());

				} else {

					runtimeType.setProtoType(protoRuntimeType);

				}
			}

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

		URI uri = getNormalisedUri(targetNameSpace);

		String authority = uri.getAuthority();

		List<String> packageTokens = new ArrayList<String>();

		if (authority != null) {
			//Reverses the authority to form the package name
			String[] authorityTokens = uri.getHost().split("\\.");
			for (int i = authorityTokens.length - 1; i >= 0; i--) {
				packageTokens.add(authorityTokens[i]);
			}
		}

		String path = uri.getPath();
		if (null != path) {

			//It removes all ../
			int idx = 0;
			while ((idx = path.indexOf("../")) >= 0) {
				path = path.substring(idx + 3);
			}

			String[] pathTokens = path.split("/");
			if (pathTokens != null && pathTokens.length > 0) {
				for (String pathToken : pathTokens) {
					if (pathToken.equals("")) {
						continue;
					}
					packageTokens.add(0, pathToken);
				}
			} else {
				packageTokens.add(0, path);
			}

		}

		StringBuilder buff = new StringBuilder();

		for (int i = 0; i < packageTokens.size(); i++) {
			buff.append(packageTokens.get(i));
			if (i + 1 < packageTokens.size()) {
				buff.append(".");
			}
		}

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
	 * It creates, validates and returns the URI given as argument, throwing
	 * exception in case of any anomalies.
	 * 
	 * @param targetNameSpace
	 *            The package name to validate
	 * @return
	 * 
	 * @throws IllegalArgumentException
	 *             <ul>
	 *             <li>
	 *             If the package name does not adhere to standard naming
	 *             conventions, as specified <a href=
	 *             "http://download.oracle.com/javase/tutorial/java/package/namingpkgs.html"
	 *             >here</a></li>
	 *             <li>If the target NS is not a valid {@link URI}</li>
	 *             </ul>
	 */
	private static URI getNormalisedUri(String targetNameSpace) {

		String errMsg = null;

		if (null == targetNameSpace || "".equals(targetNameSpace)) {
			errMsg = "Target name space cannot be null or empty";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		if (targetNameSpace.endsWith(".")) {
			errMsg = "Target name space cannot end with a dot";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		//E.g. ".foo"
		if (targetNameSpace.startsWith(".") && !targetNameSpace.startsWith("..")) {
			targetNameSpace = targetNameSpace.substring(1);
		}

		if (Character.isDigit(targetNameSpace.charAt(0))) {
			errMsg = "The target name space: " + targetNameSpace + " cannot start with a digit";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		targetNameSpace = targetNameSpace.replace('-', '_');
		targetNameSpace = targetNameSpace.toLowerCase().trim();

		URI uri = null;

		try {
			uri = new URI(targetNameSpace);
		} catch (URISyntaxException e) {
			errMsg = "The specified target namespace: " + targetNameSpace
					+ " does not seem to be a valid URI";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg, e);
		}

		return uri.normalize();

	}

	/**
	 * It retrieves a name for an anonymous type, guaranteeing uniqueness for
	 * duplicates.
	 * 
	 * @param candidateName
	 *            A candidate name for a message type
	 * 
	 * @param inputPath
	 *            The full path to the XSD input file which originated the
	 *            request
	 * @return A name for an anonymous type, guaranteeing that it is unique per
	 *         proto file.
	 */
	public static String getMessageTypeName(String candidateName, String inputPath) {

		//Guarantees multi-threading support
		inputPath = inputPath.replace('\\', '/');//I prefer a normalised URL
		String capitalisedName = capitaliseString(candidateName);
		String key = inputPath + capitalisedName;
		String retValue = ANONYMOUS_TYPES_CACHE.get(key);
		if (retValue == null) {
			String added = ANONYMOUS_TYPES_CACHE.putIfAbsent(key, capitalisedName);
			if (added != null) {
				retValue = added;
			} else {
				retValue = capitalisedName;
			}
		} else {
			int idx = 0;
			key = inputPath + capitalisedName + idx;
			String value = capitalisedName + idx;
			while (ANONYMOUS_TYPES_CACHE.containsKey(key)) {
				idx++;
				key = inputPath + capitalisedName + idx;
				value = capitalisedName + idx;
			}
			String added = ANONYMOUS_TYPES_CACHE.putIfAbsent(key, value);
			if (added != null) {
				retValue = added;
			} else {
				retValue = value;
			}
		}

		return retValue;
	}

	//------------------->> equals() / hashcode() / toString()

	/**
	 * @param retValue
	 * @return
	 */
	public static String capitaliseString(String retValue) {
		retValue = Character.toUpperCase(retValue.charAt(0)) + retValue.substring(1);
		return retValue;
	}

	//------------------->> Inner classes

}
