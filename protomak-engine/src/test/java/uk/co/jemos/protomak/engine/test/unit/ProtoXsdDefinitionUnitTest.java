/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.xsds.protomak.proto.EnumType;
import uk.co.jemos.xsds.protomak.proto.ExtendType;
import uk.co.jemos.xsds.protomak.proto.ExtensionType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ObjectFactory;
import uk.co.jemos.xsds.protomak.proto.OptionElementType;
import uk.co.jemos.xsds.protomak.proto.ProtoType;
import uk.co.jemos.xsds.protomak.proto.ServiceType;

/**
 * Unit test to check the XSD which defines a proto file
 * 
 * @author mtedone
 * 
 */
public class ProtoXsdDefinitionUnitTest {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	//------------------->> Public methods

	@Test
	public void testProtoXsd() throws Exception {

		JAXBContext ctx = JAXBContext
				.newInstance(ProtomakEngineConstants.GENERATED_CODE_PACKAGE_NAME);
		Assert.assertNotNull("The JAXB context cannot be null!");

		PodamFactory factory = new PodamFactoryImpl();

		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

		ProtoType proto = new ProtoType();
		proto.getImport().add(ProtomakEngineTestConstants.PROTO_IMPORT_1);
		proto.getImport().add(ProtomakEngineTestConstants.PROTO_IMPORT_2);

		MessageType msgType = getMessageType(factory);

		MessageType nestedMessageType = getMessageType(factory);
		msgType.getNestedMessage().add(nestedMessageType);

		proto.getMessage().add(msgType);

		proto.setPackage(ProtomakEngineTestConstants.PROTO_PACKAGE);

		ExtendType extendType = getExtendType(factory);
		proto.getExtend().add(extendType);

		proto.getService().add(factory.manufacturePojo(ServiceType.class));

		List<OptionElementType> options = proto.getOption();
		for (int i = 0; i < 2; i++) {
			options.add(factory.manufacturePojo(OptionElementType.class));
		}

		JAXBElement<ProtoType> jaxbElement = new ObjectFactory().createProto(proto);
		marshaller.marshal(jaxbElement, System.out);

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * It manufactures and returns a {@link MessageType}
	 * 
	 * @param factory
	 *            The Podam Factory to auto-fill POJO attributes
	 * @return A {@link MessageType}
	 */
	private MessageType getMessageType(PodamFactory factory) {

		MessageType msgType = factory.manufacturePojo(MessageType.class);

		List<EnumType> enums = msgType.getEnum();
		for (int i = 0; i < 5; i++) {
			EnumType enumType = factory.manufacturePojo(EnumType.class);
			List<String> values = enumType.getEnumEntry();
			for (int j = 0; j < 2; j++) {
				values.add(factory.manufacturePojo(String.class));
			}
			enums.add(enumType);
		}

		List<MessageAttributeType> msgAttributes = msgType.getMsgAttribute();

		int idx = 1;

		for (int i = 0; i < 4; i++) {
			MessageAttributeType messageAttributeType = factory
					.manufacturePojo(MessageAttributeType.class);
			messageAttributeType.setIndex(idx++);
			if (i % 2 == 0) {
				messageAttributeType.getRuntimeType().setCustomType(null);
			} else {
				messageAttributeType.getRuntimeType().setProtoType(null);
			}
			msgAttributes.add(messageAttributeType);
		}

		List<ExtensionType> extensions = msgType.getExtensions();
		extensions.add(factory.manufacturePojo(ExtensionType.class));

		ExtendType extendType = getExtendType(factory);
		msgType.getExtend().add(extendType);

		return msgType;
	}

	/**
	 * It manufactures and returns an {@link ExtensionType}
	 * 
	 * @param factory
	 *            The Podam factory
	 * @return An {@link ExtensionType}
	 */
	private ExtendType getExtendType(PodamFactory factory) {
		ExtendType extendType = factory.manufacturePojo(ExtendType.class);
		extendType.getExtendAttribute().getRuntimeType().setCustomType(null);
		return extendType;
	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
