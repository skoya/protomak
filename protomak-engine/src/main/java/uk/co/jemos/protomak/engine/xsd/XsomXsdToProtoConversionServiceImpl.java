/**
 * 
 */
package uk.co.jemos.protomak.engine.xsd;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import uk.co.jemos.protomak.engine.api.ConversionService;
import uk.co.jemos.protomak.engine.api.XsomComplexTypeProcessor;
import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.impl.XsomDefaultComplexTypeProcessor;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageRuntimeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

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
public class XsomXsdToProtoConversionServiceImpl implements ConversionService {

	//------------------->> Constants

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(XsomXsdToProtoConversionServiceImpl.class);

	//------------------->> Instance / Static variables

	private final XsomComplexTypeProcessor complexTypeProcessor;

	//------------------->> Constructors

	/**
	 * Default constructor.
	 */
	public XsomXsdToProtoConversionServiceImpl() {
		this(XsomDefaultComplexTypeProcessor.getInstance());
	}

	/**
	 * Full constructor
	 * 
	 * @param complexTypeProcessor
	 *            The complex type processor
	 */
	public XsomXsdToProtoConversionServiceImpl(XsomComplexTypeProcessor complexTypeProcessor) {
		super();
		this.complexTypeProcessor = complexTypeProcessor;
	}

	//------------------->> Public methods

	/**
	 * {@inheritDoc}
	 * 
	 * @throws IllegalArgumentException
	 *             If the {@code inputPath} does not exist.
	 */
	public void generateProtoFiles(String inputPath, String outputPath) {

		File inputFilePath = new File(inputPath);
		if (!inputFilePath.exists()) {
			String errMsg = "The XSD input file: " + inputFilePath.getAbsolutePath()
					+ " does not exist. Throwing an exception.";
			LOG.error(errMsg);
			throw new IllegalArgumentException(errMsg);
		}

		File protosOutputFolder = new File(outputPath);
		if (!protosOutputFolder.exists()) {
			LOG.info("Output folder: " + outputPath + " does not exist. Creating it.");
			protosOutputFolder.mkdirs();
		}

		ProtoType proto = new ProtoType();
		List<MessageType> messageList = proto.getMessage();

		XsdToProtoErrorHandler errorHandler = new XsdToProtoErrorHandler();
		XsdToProtoEntityResolver entityResolver = new XsdToProtoEntityResolver();

		//Let's start the dance by reading the XSD file
		XSOMParser parser = new XSOMParser();
		parser.setErrorHandler(errorHandler);
		parser.setEntityResolver(entityResolver);

		try {
			parser.parse(inputFilePath);
			XSSchemaSet sset = parser.getResult();
			if (null != sset) {

				//Extracting the root element
				Iterator<XSElementDecl> elementsIterator = sset.iterateElementDecls();
				XSElementDecl rootElement = null;
				if (elementsIterator.hasNext()) {
					rootElement = elementsIterator.next();
				} else {
					throw new IllegalStateException("It appears the schema: " + inputFilePath
							+ " does not contain a root element");
				}

				XSType rootElementType = rootElement.getType();
				LOG.info("XSD Schema root element: " + rootElement.getName()
						+ ". Root element type: " + rootElementType.getName());

				MessageType rootMessage = new MessageType();

				boolean isRootMessage = true;

				if (rootElementType.isComplexType()) {

					rootMessage = complexTypeProcessor.processComplexType(
							rootElementType.asComplexType(), isRootMessage);

					fillRootMessageForComplexType(rootElement, rootMessage);

				}

				messageList.add(rootMessage);

			} else {
				LOG.warn("No XSOM Schema set could be created from XSD file: " + inputFilePath
						+ ". Nothing will be done.");
			}

		} catch (SAXException e) {
			throw new ProtomakXsdToProtoConversionError(e);
		} catch (IOException e) {
			throw new ProtomakXsdToProtoConversionError(e);
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * It fills and returns the root {@link MessageType}
	 * 
	 * @param rootElement
	 *            The type of the root element
	 * @param rootMessage
	 *            The root {@link MessageType}
	 * @param nestedMessageType
	 *            The nested {@link MessageType} if any.
	 */
	private void fillRootMessageForComplexType(XSElementDecl rootElement, MessageType rootMessage) {

		rootMessage.setName(rootElement.getType().getName());
		MessageAttributeType rootMessageAttributeType = new MessageAttributeType();
		rootMessageAttributeType.setName(rootElement.getName());
		rootMessageAttributeType.setIndex(1);
		rootMessageAttributeType.setOptionality(MessageAttributeOptionalType.REQUIRED);

		MessageRuntimeType runtimeType = new MessageRuntimeType();
		runtimeType.setCustomType(rootElement.getType().getName());
		rootMessageAttributeType.setRuntimeType(runtimeType);
		rootMessage.getMsgAttribute().add(rootMessageAttributeType);
	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

	private static class XsdToProtoErrorHandler implements ErrorHandler {

		public void warning(SAXParseException exception) throws SAXException {
			LOG.warn("Warning from XSOM Parser: ", exception);
			throw exception;
		}

		public void error(SAXParseException exception) throws SAXException {
			LOG.error("Error from XSOM Parser: ", exception);
			throw exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			LOG.fatal("Fatal error from XSOM Parser: ", exception);
			throw exception;

		}

	}

	private static class XsdToProtoEntityResolver implements EntityResolver {

		public InputSource resolveEntity(String publicId, String systemId) throws SAXException,
				IOException {
			throw new UnsupportedOperationException("Not implemented yet.");
		}

	}

}
