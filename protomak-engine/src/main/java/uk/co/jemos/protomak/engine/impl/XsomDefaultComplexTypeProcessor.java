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
	public MessageType processComplexType(XSType type) throws ProtomakXsdToProtoConversionError {

		//Each proto message has numbered items starting from 1
		int protoCounter = 1;

		LOG.info("Processing type: " + type.getName() + ". Is it complex? " + type.isComplexType());

		MessageType retValue = new MessageType();

		//Simple types don't have attributes
		if (type.isComplexType()) {
			XSComplexType complexType = type.asComplexType();
			retValue.setName(complexType.getName());
			List<MessageAttributeType> messageAttributeTypes = retrieveComplexTypeAttributes(
					protoCounter, complexType);
			retValue.getMsgAttribute().addAll(messageAttributeTypes);
			ComplexTypeVisitor visitor = new XsomDefaultComplexTypeProcessor.ComplexTypeVisitor(
					retValue);

			//The visitor fills in the values
			complexType.getContentType().visit(visitor);

			if (!retValue.getName().equals(visitor.messageType.getName())) {
				retValue.getNestedMessage().add(visitor.messageType);
			}

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

	private static final class ComplexTypeVisitor implements XSVisitor {

		private MessageType messageType;

		/**
		 * Full constructor.
		 * 
		 * @param complexType
		 *            The complex type for this visitor
		 * 
		 * @param messageType
		 *            The MessageType to be filled for this complex type, or
		 *            null if this is the root message type.
		 */
		public ComplexTypeVisitor(MessageType messageType) {
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
				xsParticle.getTerm().visit(this);
			}

		}

		public void elementDecl(XSElementDecl decl) {

			String elementType = decl.getType().getName();

			XSType elementDeclaredType = decl.getOwnerSchema().getType(elementType);
			if (null != elementDeclaredType) {

				if (elementDeclaredType.isComplexType()) {

					LOG.debug("Found complex type: " + elementType);
					messageType = XsomDefaultComplexTypeProcessor.getInstance().processComplexType(
							elementDeclaredType.asComplexType());

				}
			}

		}

		public void simpleType(XSSimpleType simpleType) {
			System.out.println("Visitor simple type: " + simpleType.getName());

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
					XsomDefaultComplexTypeProcessor.getInstance().processComplexType(type));

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
