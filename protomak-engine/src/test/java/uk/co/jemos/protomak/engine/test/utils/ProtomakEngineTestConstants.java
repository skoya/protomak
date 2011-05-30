/**
 * 
 */
package uk.co.jemos.protomak.engine.test.utils;

import java.io.File;

/**
 * Test constants for Protomak engine.
 * 
 * @author mtedone
 * 
 */
public class ProtomakEngineTestConstants {

	/** The folder containing the test xsds */
	public static final String TEST_XSDS_FOLDER = "src/test/resources/test-xsds";

	/** The full path to a non-existing file. */
	public static final String NON_EXISTING_FILE_PATH = "foobarbaz";

	/** The full path to the folder where to put the generated protos. */
	public static final String PROTOS_OUTPUT_DIR = "target/generated-sources/protos";

	/** An import string for a proto file. */
	public static final String PROTO_IMPORT_1 = "uk/co/jemos/proto1.proto";

	/** An import string for a proto file. */
	public static final String PROTO_IMPORT_2 = "uk/co/jemos/proto2.proto";

	/** A package name for a proto file. */
	public static final String PROTO_PACKAGE = "uk.co.jemos.protomak.test.unit";

	/** The full path to the XSD to test. */
	public static final String SIMPLE_ONE_LEVEL_XSD_PATH = TEST_XSDS_FOLDER + File.separatorChar
			+ "simple-one-level.xsd";

	/** The path to one-simple-single-element */
	public static final String SIMPLE_SINGLE_ELEMENT_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + "one-simple-single-element.xsd";

	/** The path to multiple-simple-elements */
	public static final String SIMPLE_MULTIPLE_ELEMENTS_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + "multiple-simple-elements.xsd";

	/** The path to single-element-with-complex-type */
	public static final String SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + "single-element-with-complex-type.xsd";

	/** The path to multiple-complex-types-only */
	public static final String MULTIPLE_COMPLEX_TYPES_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + "multiple-complex-types-only.xsd";

	/** The path to the anonymous-types XSD file */
	public static final String ANONYMOUS_TYPES_XSD_PATH = TEST_XSDS_FOLDER + File.separatorChar
			+ "anonymous-types.xsd";

	/**
	 * The full path to element-complex-and single-type-with-restrictions XSD
	 * file.
	 */
	public static final String ELEMENT_WITH_COMPLEX_AND_SIMPLE_TYPE_WITH_RESTRICTIONS_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + "element-complex-and single-type-with-restrictions.xsd";

	/** A target name space which contains HTTP as prefix. */
	public static final String TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX = "http://www.jemos.eu/simple-one-level";

	/** A target name space which contains HTTP as prefix with some upper cases. */
	public static final String TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX_AND_SOME_UPPERCASE = "http://wWw.JEMOS.eu/FOO";

	/**
	 * The path to public static final String
	 * SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH = null;
	 * 
	 * /** Non instantiable constructor
	 */
	private ProtomakEngineTestConstants() {
	}

}
