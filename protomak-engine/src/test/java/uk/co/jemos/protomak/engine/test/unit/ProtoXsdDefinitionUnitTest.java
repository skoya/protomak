/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.jemos.protomak.engine.generated.xsd.ObjectFactory;
import uk.co.jemos.protomak.engine.generated.xsd.ProtoType;
import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;

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
	public void testProtoXmlConformsToXsd() throws Exception {

		JAXBContext ctx = JAXBContext.newInstance("uk.co.jemos.protomak.engine.generated.xsd");
		Assert.assertNotNull("The JAXB context cannot be null!");
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		ProtoType proto = new ProtoType();
		proto.getImport().add(ProtomakEngineTestConstants.PROTO_IMPORT_1);
		proto.getImport().add(ProtomakEngineTestConstants.PROTO_IMPORT_2);
		proto.setPackage(ProtomakEngineTestConstants.PROTO_PACKAGE);
		JAXBElement<ProtoType> jaxbElement = new ObjectFactory().createProto(proto);
		marshaller.marshal(jaxbElement, System.out);

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
