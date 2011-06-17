package uk.co.jemos.protomak.engine.impl;

import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import org.jpatterns.gof.VisitorPattern;
import org.jpatterns.gof.VisitorPattern.ConcreteVisitor;

import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
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
public class TypeVisitor implements XSVisitor {

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

	/** The full path to the XSD file which originated the request */
	private String inputPath;

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
	 *            The MessageType to be filled for this complex type, or null if
	 *            this is the root message type.
	 * @param inputPath
	 *            The full path to the XSD which originated the request
	 */
	public TypeVisitor(List<MessageType> protoMessages, MessageType messageType, String inputPath) {
		this.protoMessages = protoMessages;
		this.messageType = messageType;
		this.inputPath = inputPath;
	}

	public void wildcard(XSWildcard wc) {
		throw new UnsupportedOperationException("Not implemented.");

	}

	public void modelGroupDecl(XSModelGroupDecl decl) {
		throw new UnsupportedOperationException("Not implemented.");

	}

	public void modelGroup(XSModelGroup group) {
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: In Model group line: " + group.getLocator().getLineNumber() + " column: " + group.getLocator().getColumnNumber());
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
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: In Model added message: " + messageType);
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: Exit Model group line: " + group.getLocator().getLineNumber() + " column: " + group.getLocator().getColumnNumber());		
	}

	public void elementDecl(XSElementDecl element) {
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: In elementDecl, element name: " + element.getName());
		String elementType = element.getType().getName();

		if (element.getType().isSimpleType()) {
			// If this is a custom simple type, we need to get the element from the XSD
			ProtoRuntimeType protoRuntimeType = ProtomakEngineHelper.XSD_TO_PROTO_TYPE_MAPPING
					.get(elementType);

			if (null == protoRuntimeType) {
				XsomDefaultComplexTypeProcessor.LOG.info("Processing custom simple type: "
						+ elementType);
				XSSimpleType simpleType = element.getOwnerSchema().getSimpleType(elementType);
				simpleType.visit(this);
			}

			MessageAttributeType messageAttribute = ProtomakEngineHelper.getMessageAttribute(
					element, messageAttributeOrdinal, attributeOptionality);

			messageType.getMsgAttribute().add(messageAttribute);

		} else {

			//We assume the complex type is declared within the schema. This probably 
			//needs to change if the complex type is declared externally
			XSType elementDeclaredType = element.getType();
			
			if (elementDeclaredType.isLocal()) {
				XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: In elementDecl, element name: " + element.getName() + " is anonymous need to create message type.");
				elementDeclaredType.visit(this);
			}
				
			if (null != elementDeclaredType) {

				if (elementDeclaredType.isComplexType()) {

					MessageAttributeType msgAttributeType = new MessageAttributeType();
					msgAttributeType.setName(element.getName());
					msgAttributeType.setOptionality(attributeOptionality);
					msgAttributeType.setIndex(messageAttributeOrdinal);
					MessageRuntimeType runtimeType = new MessageRuntimeType();
					runtimeType.setCustomType(elementDeclaredType.isGlobal() ? elementDeclaredType.getName() : element.getName());					
					msgAttributeType.setRuntimeType(runtimeType);
					messageType.getMsgAttribute().add(msgAttributeType);

				}
			}
		}

	}

	public void simpleType(XSSimpleType simpleType) {
		XsomDefaultComplexTypeProcessor.LOG.debug("In Visitor: processing custom simple type...");
		XSType baseType = simpleType.getBaseType();
		XsomDefaultComplexTypeProcessor.LOG.debug("Simple type base type: " + baseType.getName());
		if (simpleType.isRestriction()) {
			XSRestrictionSimpleType restriction = simpleType.asRestriction();
			if (baseType.getName().equals("string")) {
				List<XSFacet> enumarationFacets = restriction.getFacets("enumeration");
				if (!enumarationFacets.isEmpty()) {
					List<EnumType> enumerations = messageType.getEnum();
					EnumType enumType = new EnumType();
					if (null != enumType.getName()) {
						enumType.setName(simpleType.getName());
					} else {
						//This is for anonymous local types
						enumType.setName(ProtomakEngineConstants.ANONYMOUS_ENUM_DEFAULT_MESSAGE_TYPE_NAME
								+ messageAttributeOrdinal);
					}

					List<String> enumEntries = enumType.getEnumEntry();
					for (XSFacet xsFacet : enumarationFacets) {
						enumEntries.add(xsFacet.getValue().toString());
					}
					enumerations.add(enumType);
				}

			} else {

				XsomDefaultComplexTypeProcessor.LOG.warn("The string base restriction for type: "
						+ simpleType.getName() + " does not seem to have any enumerations");

			}
		}

	}

	public void particle(XSParticle particle) {
		XSTerm term = particle.getTerm();
		term.visit(this);
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: Exiting from particle. messageType name = "
				+ messageType.getName());
	}

	public void empty(XSContentType empty) {
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: In empty");
		protoMessages.add(messageType);
		XsomDefaultComplexTypeProcessor.LOG.debug("XSVisitor: Exit empty");		
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
						type, inputPath));

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