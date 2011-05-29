/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import java.io.File;

import org.easymock.EasyMock;
import org.junit.Test;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.protomak.engine.api.ProtoSerialisationService;
import uk.co.jemos.protomak.engine.exceptions.ProtomakEngineSerialisationError;
import uk.co.jemos.protomak.engine.impl.PojoToProtoSerialisationServiceImpl;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

/**
 * Unit test for the Proto Domain Serialisation.
 * 
 * @author mtedone
 * 
 */
public class ProtoDomainSerialisationUnitTest {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	//------------------->> Public methods

	@Test(expected = ProtomakEngineSerialisationError.class)
	public void testSerialisationServiceWhenFolderAlreadyExists() {

		ProtoSerialisationService service = PojoToProtoSerialisationServiceImpl.getInstance();

		File fileMock = EasyMock.createMock(File.class);
		EasyMock.expect(fileMock.exists()).andReturn(false);
		EasyMock.expect(fileMock.mkdirs()).andReturn(false);

		EasyMock.replay(fileMock);

		PodamFactory factory = new PodamFactoryImpl();
		ProtoType pojo = factory.manufacturePojo(ProtoType.class);
		try {
			service.writeProtoFile("foo", fileMock, pojo);
		} finally {
			EasyMock.verify(fileMock);
		}

	}

	@Test(expected = ProtomakEngineSerialisationError.class)
	public void testSerialisationServiceForFileNotFoundException() {

		ProtoSerialisationService service = PojoToProtoSerialisationServiceImpl.getInstance();

		File fileMock = EasyMock.createMock(File.class);
		EasyMock.expect(fileMock.exists()).andReturn(true);
		EasyMock.expect(fileMock.getAbsolutePath()).andReturn("fooPath");

		EasyMock.replay(fileMock);

		PodamFactory factory = new PodamFactoryImpl();
		ProtoType pojo = factory.manufacturePojo(ProtoType.class);
		try {
			service.writeProtoFile("foo", fileMock, pojo);
		} finally {
			EasyMock.verify(fileMock);
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
