/**
 * 
 */
package uk.co.jemos.protomak.engine.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.jemos.protomak.engine.api.XsomComplexTypeProcessor;
import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;
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
	public MessageType processComplexType(List<MessageType> protoMessages, XSType type)
			throws ProtomakXsdToProtoConversionError {

		LOG.info("Processing type: " + type.getName() + ". Is it complex? " + type.isComplexType());

		MessageType retValue = new MessageType();

		//Simple types don't have attributes
		if (type.isComplexType()) {
			XSComplexType complexType = type.asComplexType();
			retValue.setName(complexType.getName());

			TypeVisitor visitor = new TypeVisitor(protoMessages, retValue);

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

		Iterator<? extends XSAttributeUse> attributeUsesIterator = complexType
				.iterateAttributeUses();
		XSAttributeUse complexTypeAttribute = null;
		MessageAttributeType messageAttributeType = null;
		while (attributeUsesIterator.hasNext()) {
			complexTypeAttribute = attributeUsesIterator.next();
			messageAttributeType = ProtomakEngineHelper.convertXsomAttributeToMessageAttributeType(
					protoCounter, complexTypeAttribute);
			protoCounter++;
			attributes.add(messageAttributeType);

		}

		return attributes;
	}

}
