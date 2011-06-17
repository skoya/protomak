/**
 * 
 */
package uk.co.jemos.protomak.engine.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.co.jemos.protomak.engine.api.XsomComplexTypeProcessor;
import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;
import uk.co.jemos.xsds.protomak.proto.ExtendType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;

import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSType;

/**
 * Default implementation for the {@link XsomComplexTypeProcessor} interface.
 * 
 * @author mtedone
 * 
 */
public class XsomDefaultComplexTypeProcessor implements XsomComplexTypeProcessor {

	//------------------->> Constants

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(XsomDefaultComplexTypeProcessor.class);

	//------------------->> Instance / Static variables

	/** The singleton of this class */
	private static final XsomDefaultComplexTypeProcessor SINGLETON = new XsomDefaultComplexTypeProcessor();

	//------------------->> Constructors

	//------------------->> Public methods

	/** The singleton method */
	public static XsomDefaultComplexTypeProcessor getInstance() {
		return SINGLETON;
	}

	/**
	 * {@inheritDoc}
	 */
	public MessageType processComplexType(List<MessageType> protoMessages, XSType type,
			String inputPath) throws ProtomakXsdToProtoConversionError {

		LOG.info("Processing type: " + type.getName() + ". Is it complex? " + type.isComplexType());

		MessageType retValue = new MessageType();

		//Simple types don't have attributes
		if (type.isComplexType()) {
			XSComplexType complexType = type.asComplexType();
			String messageTypeName = ProtomakEngineHelper.getMessageTypeName(complexType.getName() != null ? complexType.getName() : complexType.getScope().getName(),
					inputPath);
			retValue.setName(messageTypeName);
			TypeVisitor visitor = new TypeVisitor(protoMessages, retValue, inputPath);

			//Determine if the complex type extends another type other than the default anyType
			XSType baseType = complexType.getBaseType();
			if (baseType != null
					&& !baseType.getName().equals(ProtomakEngineConstants.ANY_TYPE_NAME)) {
				LOG.info("Processing type: " + type.getName() + " extends " + baseType.getName());
				ExtendType extend = new ExtendType();
				extend.setMessageName(baseType.getName());
				retValue.setExtend(extend);
			}

			//The visitor fills in the values
			complexType.getContentType().visit(visitor);
			List<MessageAttributeType> messageAttributeTypes = retrieveComplexTypeAttributes(
					visitor.getMessageAttributeOrdinal(), complexType);

			retValue.getMsgAttribute().addAll(messageAttributeTypes);

		}

		return retValue;
	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * Given a {@link XSComplexType} in returns a {@link List} of
	 * {@link MessageAttributeType}s for each attribute.
	 * 
	 * @param protoCounter
	 *            The proto index to number the message attributes.
	 * @param complexType
	 *            The complex type to search for attributes.
	 * @return a {@link List} of {@link MessageAttributeType}s for each
	 *         attribute contained within the given complex type.
	 */
	private List<MessageAttributeType> retrieveComplexTypeAttributes(int protoCounter,
			XSComplexType complexType) {

		List<MessageAttributeType> attributes = new ArrayList<MessageAttributeType>();

		XSAttributeUse complexTypeAttribute = null;
		MessageAttributeType messageAttributeType = null;
		
		Collection<? extends XSAttributeUse> attributesCollection = complexType.getAttributeUses();
		for (Iterator<? extends XSAttributeUse> i = attributesCollection.iterator(); i.hasNext();){
			complexTypeAttribute = i.next();
			messageAttributeType = ProtomakEngineHelper.convertXsomAttributeToMessageAttributeType(
					protoCounter, complexTypeAttribute);
			LOG.debug(complexType.getName() + ", found attribute : " + messageAttributeType.getName());
			attributes.add(messageAttributeType);
		}
		
		Collections.sort(attributes, ProtomakEngineConstants.MESSAGE_ATTRIBUTE_COMPARATOR);

		for (MessageAttributeType attr : attributes) {
			attr.setIndex(protoCounter++);
		}

		return attributes;
	}

}
