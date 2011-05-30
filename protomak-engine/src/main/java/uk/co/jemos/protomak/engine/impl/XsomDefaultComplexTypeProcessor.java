/**
 * 
 */
package uk.co.jemos.protomak.engine.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.jpatterns.gof.VisitorPattern;
import org.jpatterns.gof.VisitorPattern.ConcreteVisitor;

import uk.co.jemos.protomak.engine.api.XsomComplexTypeProcessor;
import uk.co.jemos.protomak.engine.exceptions.ProtomakXsdToProtoConversionError;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;
import uk.co.jemos.xsds.protomak.proto.EnumType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageRuntimeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoRuntimeType;

import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSAttGroupDecl;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSIdentityConstraint;
import com.sun.xml.xsom.XSModelGroup;
import com.sun.xml.xsom.XSModelGroupDecl;
import com.sun.xml.xsom.XSNotation;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.XSWildcard;
import com.sun.xml.xsom.XSXPath;
import com.sun.xml.xsom.visitor.XSVisitor;

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
			ComplexTypeVisitor visitor = new XsomDefaultComplexTypeProcessor.ComplexTypeVisitor(
					protoMessages, retValue);

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

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

	/**
	 * This class implements an XSOM visitor, invoked by the XSOM parser when
	 * traversing the XSD elements.
	 * 
	 * <p>
	 * This class is not thread-safe; its state should not be shared amongst
	 * concurrent threads but rather a new instance should be created for each
	 * execution.
	 * </p>
	 * 
	 * @author mtedone
	 * 
	 */
	@NotThreadSafe
	@VisitorPattern
	@ConcreteVisitor
	private static final class ComplexTypeVisitor implements XSVisitor {

		/** The {@link MessageType} that will be generated from this visit. */
		private MessageType messageType;

		/** The Message attribute optionality. */
		private MessageAttributeOptionalType attributeOptionality;

		/**
		 * The {@link MessageAttributeType} ordinal within a {@link MessageType}
		 * 
		 */
		private int messageAttributeOrdinal = 1;

		/** The collection of proto messages */
		private List<MessageType> protoMessages;

		/**
		 * Full constructor.
		 * 
		 * @param protoMessages
		 *            The collection of proto messages
		 * 
		 * @param complexType
		 *            The complex type for this visitor
		 * 
		 * @param messageType
		 *            The MessageType to be filled for this complex type, or
		 *            null if this is the root message type.
		 */
		public ComplexTypeVisitor(List<MessageType> protoMessages, MessageType messageType) {
			this.protoMessages = protoMessages;
			this.messageType = messageType;
		}

		public void wildcard(XSWildcard wc) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void modelGroupDecl(XSModelGroupDecl decl) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void modelGroup(XSModelGroup group) {
			LOG.debug("XS Visitor: In Model group");
			XSParticle[] children = group.getChildren();
			for (XSParticle xsParticle : children) {
				int minOccurs = xsParticle.getMinOccurs();
				int maxOccurs = xsParticle.getMaxOccurs();

				MessageAttributeOptionalType attributeOptionality = ProtomakEngineHelper
						.getMessageAttributeOptionality(minOccurs, maxOccurs);
				this.attributeOptionality = attributeOptionality;

				xsParticle.getTerm().visit(this);

				messageAttributeOrdinal++;
			}

			//Let's clean after ourselves
			protoMessages.add(messageType);

		}

		public void elementDecl(XSElementDecl element) {

			String elementType = element.getType().getName();

			if (element.getType().isSimpleType()) {

				// If this is a custom simple type, we need to get the element from the XSD
				ProtoRuntimeType protoRuntimeType = ProtomakEngineHelper.XSD_TO_PROTO_TYPE_MAPPING
						.get(elementType);

				if (null == protoRuntimeType) {
					LOG.info("Processing custom simple type: " + elementType);
					XSSimpleType simpleType = element.getOwnerSchema().getSimpleType(elementType);
					simpleType.visit(this);
				}

				MessageAttributeType messageAttribute = ProtomakEngineHelper.getMessageAttribute(
						element, messageAttributeOrdinal, attributeOptionality);

				messageType.getMsgAttribute().add(messageAttribute);

			} else {

				//We assume the complex type is declared within the schema. This probably 
				//needs to change if the complex type is declared externally
				XSType elementDeclaredType = element.getOwnerSchema().getType(elementType);
				if (null != elementDeclaredType) {

					if (elementDeclaredType.isComplexType()) {

						MessageAttributeType msgAttributeType = new MessageAttributeType();
						msgAttributeType.setName(element.getName());
						msgAttributeType.setOptionality(attributeOptionality);
						msgAttributeType.setIndex(messageAttributeOrdinal);
						MessageRuntimeType runtimeType = new MessageRuntimeType();
						runtimeType.setCustomType(elementDeclaredType.getName());
						msgAttributeType.setRuntimeType(runtimeType);
						messageType.getMsgAttribute().add(msgAttributeType);

					}
				}

			}

		}

		public void simpleType(XSSimpleType simpleType) {
			LOG.debug("In Visitor: processing custom simple type...");
			XSType baseType = simpleType.getBaseType();
			LOG.debug("Simple type base type: " + baseType.getName());
			if (simpleType.isRestriction()) {
				XSRestrictionSimpleType restriction = simpleType.asRestriction();
				if (baseType.getName().equals("string")) {
					List<XSFacet> enumarationFacets = restriction.getFacets("enumeration");
					if (!enumarationFacets.isEmpty()) {
						List<EnumType> enumerations = messageType.getEnum();
						EnumType enumType = new EnumType();
						enumType.setName(simpleType.getName());
						List<String> enumEntries = enumType.getEnumEntry();
						for (XSFacet xsFacet : enumarationFacets) {
							enumEntries.add(xsFacet.getValue().toString());
						}
						enumerations.add(enumType);
					}

				} else {

					LOG.warn("The string base restriction for type: " + simpleType.getName()
							+ " does not seem to have any enumerations");

				}
			}

		}

		public void particle(XSParticle particle) {
			LOG.debug("XSVisitor: In particle");
			XSTerm term = particle.getTerm();
			term.visit(this);
			LOG.debug("Exiting from particle. messageType name = " + messageType.getName());
		}

		public void empty(XSContentType empty) {
			LOG.debug("In visitor: " + this + ". Exiting for empty content type.");
		}

		public void annotation(XSAnnotation ann) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void attGroupDecl(XSAttGroupDecl decl) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void attributeDecl(XSAttributeDecl decl) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void attributeUse(XSAttributeUse use) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void complexType(XSComplexType type) {
			messageType.getNestedMessage().add(
					XsomDefaultComplexTypeProcessor.getInstance().processComplexType(protoMessages,
							type));

		}

		public void schema(XSSchema schema) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void facet(XSFacet facet) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void notation(XSNotation notation) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void identityConstraint(XSIdentityConstraint decl) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		public void xpath(XSXPath xp) {
			throw new UnsupportedOperationException("Not implemented.");

		}

		/**
		 * @return the messageAttributeOrdinal
		 */
		public int getMessageAttributeOrdinal() {
			return messageAttributeOrdinal;
		}

		/**
		 * Constructs a <code>String</code> with all attributes in name = value
		 * format.
		 * 
		 * @return a <code>String</code> representation of this object.
		 */
		@Override
		public String toString() {
			final String TAB = "    ";

			StringBuilder retValue = new StringBuilder();

			retValue.append("ComplexTypeVisitor ( ").append("parent = ").append(messageType)
					.append(TAB).append(" )");

			return retValue.toString();
		}

	}

}
