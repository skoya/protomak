/**
 * 
 */
package uk.co.jemos.protomak.engine.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.io.IOUtils;

import uk.co.jemos.protomak.engine.api.ProtoSerialisationService;
import uk.co.jemos.protomak.engine.exceptions.ProtomakEngineSerialisationError;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineConstants;
import uk.co.jemos.xsds.protomak.proto.EnumType;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeType;
import uk.co.jemos.xsds.protomak.proto.MessageType;
import uk.co.jemos.xsds.protomak.proto.ProtoType;

/**
 * Implementation of a {@link ProtoSerialisationService} which serialises a
 * {@link ProtoType} to some output directory.
 * 
 * @author mtedone
 * 
 */
public class PojoToProtoSerialisationServiceImpl implements ProtoSerialisationService {

	//------------------->> Constants

	/** The application logger. */
	public static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(PojoToProtoSerialisationServiceImpl.class);

	//------------------->> Instance / Static variables

	/** The Singleton instance */
	private static final PojoToProtoSerialisationServiceImpl SINGLETON = new PojoToProtoSerialisationServiceImpl();

	//------------------->> Constructors

	/**
	 * It implements the Singleton pattern.
	 */
	private PojoToProtoSerialisationServiceImpl() {

	}

	//------------------->> Public methods

	/**
	 * This is the Singleton factory method.
	 * 
	 * @return The singleton instance of this class
	 */
	public static PojoToProtoSerialisationServiceImpl getInstance() {
		return SINGLETON;
	}

	/**
	 * {@inheritDoc}
	 */
	public void writeProtoFile(String fileName, File outputPath, ProtoType proto)
			throws ProtomakEngineSerialisationError {

		if (!outputPath.exists()) {
			boolean created = outputPath.mkdirs();
			if (!created) {
				String errMsg = "An error occurred while creating the output folder: " + outputPath;
				LOG.error(errMsg);
				throw new ProtomakEngineSerialisationError(errMsg);

			}
		}

		StringBuilder buff = new StringBuilder();

		writeProtoDisclaimer(buff);

		writePackage(proto, buff);

		List<MessageType> messages = proto.getMessage();
		for (MessageType messageType : messages) {
			openMessage(buff, messageType);
			if (!messageType.getEnum().isEmpty()) {
				writeEnums(messageType.getEnum(), buff);
			}
			List<MessageAttributeType> msgAttributes = messageType.getMsgAttribute();
			for (MessageAttributeType messageAttribute : msgAttributes) {
				writeMessageAttribute(buff, messageAttribute);
			}
			closeMessage(buff);

		}

		if (!fileName.endsWith(".proto")) {
			fileName = fileName + ProtomakEngineConstants.PROTO_SUFFIX;
		}

		BufferedOutputStream bos = null;

		try {
			File outputFileName = new File(outputPath.getAbsolutePath() + File.separatorChar
					+ fileName);
			bos = new BufferedOutputStream(new FileOutputStream(outputFileName));

			bos.write(buff.toString().getBytes("UTF-8"));
			bos.flush();
			LOG.info("The proto content has been serialised to: " + fileName);
		} catch (FileNotFoundException e) {
			String errMsg = "An error occurred while trying to create the output file: " + fileName;
			LOG.error(errMsg, e);
			throw new ProtomakEngineSerialisationError(errMsg, e);

		} catch (UnsupportedEncodingException e) {
			throwGenericSerialisationError(e);
		} catch (IOException e) {
			throwGenericSerialisationError(e);
		} finally {
			IOUtils.closeQuietly(bos);
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * It writes the enums within the message type
	 * 
	 * @param enums
	 *            The list of enums to write
	 * @param buffer
	 *            The buffer where to write the enums to
	 */
	private void writeEnums(List<EnumType> enums, StringBuilder buffer) {

		for (EnumType enumType : enums) {
			int idx = 1;
			buffer.append("\t")//
					.append(ProtomakEngineConstants.ENUM_TOKEN)//
					.append(ProtomakEngineConstants.WHITE_SPACE)//
					.append(enumType.getName()).append(ProtomakEngineConstants.WHITE_SPACE) //
					.append("{").append(ProtomakEngineConstants.NEW_LINE);//

			List<String> enumEntries = enumType.getEnumEntry();
			for (String enumEntry : enumEntries) {
				buffer.append("\t\t").append(enumEntry).append(" = ").append(idx).append(";")
						.append(ProtomakEngineConstants.NEW_LINE);
				idx++;

			}

		}

		buffer.append("\t").append("}").append(ProtomakEngineConstants.NEW_LINE);

	}

	/**
	 * It writes a disclaimer comment to the proto file
	 * 
	 * @param buffer
	 *            The buffer to write the disclaimer to.
	 */
	private void writeProtoDisclaimer(StringBuilder buffer) {
		buffer.append("//\t").append("This file was auto-generated by Protomak. Do not edit!")
				.append(ProtomakEngineConstants.NEW_LINE).append(ProtomakEngineConstants.NEW_LINE)
				.append(ProtomakEngineConstants.NEW_LINE);

	}

	/**
	 * It throws a {@link ProtomakEngineSerialisationError} when an error
	 * occurred during serialisation.
	 * 
	 * @param cause
	 *            The error which occurred.
	 */
	private void throwGenericSerialisationError(Throwable cause) {
		String errMsg = "An error occurred while writing the output buffer to the output file";
		LOG.error(errMsg);
		throw new ProtomakEngineSerialisationError(errMsg, cause);
	}

	/**
	 * It writes the package session of a proto file.
	 * 
	 * @param proto
	 *            The {@link ProtoType} object containing data
	 * @param buffer
	 *            The buffer where to write data to
	 */
	private void writePackage(ProtoType proto, StringBuilder buffer) {
		if (null != proto.getPackage()) {
			buffer.append(ProtomakEngineConstants.PACKAGE_TOKEN)
					.append(ProtomakEngineConstants.WHITE_SPACE).append(proto.getPackage())
					.append(ProtomakEngineConstants.NEW_LINE)
					.append(ProtomakEngineConstants.NEW_LINE);
		}

	}

	/**
	 * It writes the opening part of a proto {@code message} element.
	 * 
	 * @param buffer
	 *            The buffer where to write the opening message
	 * @param messageType
	 *            The {@link MessageType} containing the data
	 */
	private void openMessage(StringBuilder buffer, MessageType messageType) {
		buffer.append(ProtomakEngineConstants.PROTO_TOKENS_MESSAGE)
				.append(ProtomakEngineConstants.WHITE_SPACE).append(messageType.getName())
				.append(ProtomakEngineConstants.WHITE_SPACE).append("{")
				.append(ProtomakEngineConstants.NEW_LINE);
	}

	/**
	 * It writes a message attribute to the proto file
	 * 
	 * @param buff
	 * @param messageAttribute
	 */
	private void writeMessageAttribute(StringBuilder buff, MessageAttributeType messageAttribute) {

		buff.append("\t")
				.append(messageAttribute.getOptionality().name().toLowerCase())
				.append(ProtomakEngineConstants.WHITE_SPACE)
				.append(messageAttribute.getRuntimeType().getCustomType() != null ? messageAttribute
						.getRuntimeType().getCustomType() : messageAttribute.getRuntimeType()
						.getProtoType().name().toLowerCase())
				.append(ProtomakEngineConstants.WHITE_SPACE).append(messageAttribute.getName())
				.append(ProtomakEngineConstants.WHITE_SPACE).append("= ")
				.append(messageAttribute.getIndex()).append(";")
				.append(ProtomakEngineConstants.NEW_LINE);

	}

	/**
	 * It writes the closing part of a proto {@code message} element.
	 * 
	 * @param buffer
	 *            The buff where to write the closing message to
	 */
	private void closeMessage(StringBuilder buffer) {
		buffer.append("}").append(ProtomakEngineConstants.NEW_LINE)
				.append(ProtomakEngineConstants.NEW_LINE);
	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
