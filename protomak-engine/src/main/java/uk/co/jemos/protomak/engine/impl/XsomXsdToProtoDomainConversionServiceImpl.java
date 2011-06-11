/**
 * 
 */
package uk.co.jemos.protomak.engine.impl;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.SAXException;

import uk.co.jemos.protomak.engine.api.ConversionService;
import uk.co.jemos.protomak.engine.api.ProtoSerialisationService;
import uk.co.jemos.protomak.engine.api.XsomComplexTypeProcessor;
import uk.co.jemos.protomak.engine.exceptions.ProtomakEngineSerialisationError;
import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * XSD to Proto conversion service.
 * 
 * <p>
 * The mail goal of this class is to convert a given XSD to one or more proto
 * files.
 * </p>
 * 
 * @author mtedone
 * 
 */
public class XsomXsdToProtoDomainConversionServiceImpl implements ConversionService {

	//------------------->> Constants

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(XsomXsdToProtoDomainConversionServiceImpl.class);

	//------------------->> Instance / Static variables

	/** The processor for complex types */
	private final XsomComplexTypeProcessor complexTypeProcessor;

	/** The proto serialisation service */
	private final ProtoSerialisationService protoSerialisationService;

	/** The XSOM Schema parser */
	private XSOMParser parser = new XSOMParser();

	//------------------->> Constructors

	/**
	 * Default constructor.
	 */
	public XsomXsdToProtoDomainConversionServiceImpl() {
		this(XsomDefaultComplexTypeProcessor.getInstance(), PojoToProtoSerialisationServiceImpl
				.getInstance());
	}

	/**
	 * Full constructor
	 * 
	 * @param complexTypeProcessor
	 *            The complex type processor
	 * 
	 * @param protoSerialisationService
	 *            The proto serialisation service.
	 */
	public XsomXsdToProtoDomainConversionServiceImpl(XsomComplexTypeProcessor complexTypeProcessor,
			ProtoSerialisationService protoSerialisationService) {
		super();
		this.complexTypeProcessor = complexTypeProcessor;
		this.protoSerialisationService = protoSerialisationService;
	}

	//------------------->> Public methods

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             If the {@code inputPath} does not exist.
	 * @throws ProtomakXsdToProtoConversionError
	 *             If an error occured while parsing the file
	 * 
	 * @throws ProtomakEngineSerialisationError
	 *             If an error occurred while serialisation the
	 *             {@link ProtoType} to an output destination.
	 */
	public void generateProtoFiles(String inputPath, String outputPath) {

		File inputFilePath = new File(inputPath);
		if (!inputFilePath.exists()) {
			String errMsg = "The XSD input file: " + inputFilePath.getAbsolutePath()
					+ " does not exist. Throwing an exception.";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		ProtoType proto = new ProtoType();

		try {
			parser.parse(inputFilePath);
			XSSchemaSet sset = parser.getResult();
			if (null == sset) {
				throw new IllegalStateException(
						"An error occurred while parsing the schema. Aborting.");
			}

			LOG.info("Processing all complex types in the XSD...");
			manageComplexTypes(proto, sset, inputPath);

			LOG.info("Processing all elements in the XSD...");
			manageElements(proto, sset, inputPath);

			//Sorts the Message Types in order of their names
			LOG.info("Sorting Message Types based on their names...");
			Collections.sort(proto.getMessage(), ProtomakEngineConstants.MESSAGE_TYPE_COMPARATOR);

			String protoFileName = ProtomakEngineHelper
					.extractProtoFileNameFromXsdName(inputFilePath.getName());

			File outputDir = new File(outputPath);

			protoSerialisationService.writeProtoFile(protoFileName, outputDir, proto);
			LOG.info("Proto file: " + protoFileName + " written to " + outputPath);

		} catch (SAXException e) {
			String errMsg = "A SAX Exception occurred while parsing the XSD Schema.";
			LOG.error(errMsg, e);
			throw new ProtomakXsdToProtoConversionError(e);
		} catch (IOException e) {
			String errMsg = "An IO Exception occurred while parsing the XSD Schema.";
			LOG.error(errMsg, e);
			throw new ProtomakXsdToProtoConversionError(e);
		}

	}

	// ------------------->> Getters / Setters

	/**
	 * Setter method mainly for testing to inject mocks.
	 * 
	 * @param parser
	 *            the parser to set
	 */
	public void setParser(XSOMParser parser) {
		this.parser = parser;
	}

	//------------------->> Private methods

	/**
	 * It goes through all complex types in the XSD and for each one it creates
	 * a message in proto.
	 * 
	 * @param proto
	 *            The proto object
	 * @param schema
	 *            The representation of the XSD Schema
	 * @param inputPath
	 *            The full path to the XSD file
	 */
	private void manageComplexTypes(ProtoType proto, XSSchemaSet schema, String inputPath) {

		List<MessageType> protoMessages = proto.getMessage();

		Iterator<XSComplexType> complexTypesIterator = schema.iterateComplexTypes();

		XSComplexType complexType = null;

		while (complexTypesIterator.hasNext()) {

			complexType = complexTypesIterator.next();
			if (complexType.getName().equals(ProtomakEngineConstants.ANY_TYPE_NAME)) {
				LOG.debug("Skipping anyType: " + complexType.getName());
				continue;
			}
			if (null == proto.getPackage()) {

				String packageName = ProtomakEngineHelper
						.convertTargetNsToProtoPackageName(complexType.getTargetNamespace());
				LOG.info("Proto package will be: " + packageName);
				proto.setPackage(packageName);
			}
			LOG.debug("Processing complex type: " + complexType.getName());
			complexTypeProcessor.processComplexType(protoMessages, complexType, inputPath);

		}

		LOG.info("All complex types have been processed.");
	}

	/**
	 * It goes through all elements defined in the XSD and for each one it
	 * creates a default message.
	 * 
	 * @param proto
	 *            The root proto object
	 * @param schema
	 *            The XSD schema representation
	 * @param inputPath
	 *            The full path to the XSD file
	 */
	private void manageElements(ProtoType proto, XSSchemaSet schema, String inputPath) {
		//Iterates over the elements
		Iterator<XSElementDecl> declaredElementsIterator = schema.iterateElementDecls();
		int messageSuffix = 1;
		while (declaredElementsIterator.hasNext()) {
			MessageType msgType = new MessageType();
			XSElementDecl element = declaredElementsIterator.next();
			XSType type = element.getType();
			if (type.isLocal()) {
				LOG.debug("Type for element: " + element.getName() + " is local");
				TypeVisitor visitor = new TypeVisitor(proto.getMessage(), msgType, inputPath);
				type.visit(visitor);
			}

			String nameForAnonymousType = ProtomakEngineHelper.getMessageTypeName(
					element.getName(), inputPath);
			msgType.setName(nameForAnonymousType);
			List<MessageAttributeType> msgAttributes = msgType.getMsgAttribute();
			MessageAttributeType msgAttrType = ProtomakEngineHelper.getMessageAttribute(element,
					messageSuffix, MessageAttributeOptionalType.REQUIRED);
			msgAttributes.add(msgAttrType);

			messageSuffix++;

			proto.getMessage().add(msgType);

			if (null == proto.getPackage()) {

				String packageName = ProtomakEngineHelper.convertTargetNsToProtoPackageName(type
						.getTargetNamespace());
				LOG.info("Proto package will be: " + packageName);
				proto.setPackage(packageName);
			}
		}
	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
