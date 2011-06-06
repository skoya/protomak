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

	/** The name of the single element with complex type XSD file */
	public static final String SINGLE_ELEMENT_WITH_COMPLEX_TYPE_FILE_NAME = "single-element-with-complex-type.xsd";
	
	/** The name of the single element with complex type XSD file */
	public static final String SINGLE_ELEMENT_WITH_INHERITED_COMPLEX_TYPE_FILE_NAME = "single-element-with-inherited-complex-type.xsd";
	
	/** The name of the simple single element XSD file. */
	public static final String SIMPLE_SINGLE_ELEMENT_FILE_NAME = "one-simple-single-element.xsd";

	/** The name of the simple one level XSD file. */
	public static final String SIMPLE_ONE_LEVEL_FILE_NAME = "simple-one-level.xsd";

	/** The name of the multiple simple elements xsd file */
	public static final String SIMPLE_MULTIPLE_SIMPLE_ELEMENTS_FILE_NAME = "multiple-simple-elements.xsd";

	/**
	 * The name of the complex element and simple type with restrictions XSD
	 * file
	 */
	public static final String ELEMENT_COMPLEX_AND_SIMPLE_TYPE_WITH_RESTRICTIONS_FILE_NAME = "element-complex-and-simple-type-with-restrictions.xsd";

	/** The name of the anonymous types xsd file. */
	public static final String ANONYMOUS_TYPES_FILE_NAME = "anonymous-types.xsd";

	/** The folder containing the test xsds */
	public static final String TEST_XSDS_FOLDER = "src/test/resources/test-xsds";

	/** The full path to a non-existing file. */
	public static final String NON_EXISTING_FILE_PATH = "foobarbaz";

	/** The full path to the folder where to put the generated protos. */
	public static final String PROTOS_OUTPUT_DIR = "target/generated-sources/protos";

	/** The path to the expected proto files directory */
	public static final String EXPECTED_PROTO_DIR = "src/test/resources/expected-proto-files";

	/** An import string for a proto file. */
	public static final String PROTO_IMPORT_1 = "uk/co/jemos/proto1.proto";

	/** An import string for a proto file. */
	public static final String PROTO_IMPORT_2 = "uk/co/jemos/proto2.proto";

	/** A package name for a proto file. */
	public static final String PROTO_PACKAGE = "uk.co.jemos.protomak.test.unit";

	/** The file name for the multiple-complex-types-only XSD file */
	public static final String MULTIPLE_COMPLEX_TYPES_ONLY_FILE_NAME = "multiple-complex-types-only.xsd";

	/** The full path to the XSD to test. */
	public static final String SIMPLE_ONE_LEVEL_XSD_PATH = TEST_XSDS_FOLDER + File.separatorChar
			+ SIMPLE_ONE_LEVEL_FILE_NAME;

	/** The path to one-simple-single-element */
	public static final String SIMPLE_SINGLE_ELEMENT_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + SIMPLE_SINGLE_ELEMENT_FILE_NAME;

	/** The path to multiple-simple-elements */
	public static final String SIMPLE_MULTIPLE_ELEMENTS_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + SIMPLE_MULTIPLE_SIMPLE_ELEMENTS_FILE_NAME;

	/** The path to single-element-with-complex-type */
	public static final String SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + SINGLE_ELEMENT_WITH_COMPLEX_TYPE_FILE_NAME;

	/** The path to single-element-with-complex-type */
	public static final String SINGLE_ELEMENT_WITH_INHERITED_COMPLEX_TYPE_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + SINGLE_ELEMENT_WITH_INHERITED_COMPLEX_TYPE_FILE_NAME;

	/** The path to multiple-complex-types-only */
	public static final String MULTIPLE_COMPLEX_TYPES_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + MULTIPLE_COMPLEX_TYPES_ONLY_FILE_NAME;

	/** The path to the anonymous-types XSD file */
	public static final String ANONYMOUS_TYPES_XSD_PATH = TEST_XSDS_FOLDER + File.separatorChar
			+ ANONYMOUS_TYPES_FILE_NAME;

	/**
	 * The full path to element-complex-and single-type-with-restrictions XSD
	 * file.
	 */
	public static final String ELEMENT_WITH_COMPLEX_AND_SIMPLE_TYPE_WITH_RESTRICTIONS_XSD_PATH = TEST_XSDS_FOLDER
			+ File.separatorChar + ELEMENT_COMPLEX_AND_SIMPLE_TYPE_WITH_RESTRICTIONS_FILE_NAME;

	/** A target name space which contains HTTP as prefix. */
	public static final String TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX = "http://www.jemos.eu/simple-one-level";

	/** A target name space which contains HTTP as prefix with some upper cases. */
	public static final String TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX_AND_SOME_UPPERCASE = "http://wWw.JEMOS.eu/FOO";

	/** A relative target name space */
	public static final String TEST_TARGET_NAMESPACE_WITH_RELATIVE_URL = "../../../foo/bar/baz/my_namespace";

	/** The output folder where to put PROTO XML definition */
	public static final String PROTO_XML_DEFINITION_OUTPUT_DIR = "target/generated-sources/proto-xml-definition";

	/** The name of the proto metadata definition XML file */
	public static final String PROTO_XML_DEFINITION_FILE_NAME = "proto-metadata-definition.xml";

	/**
	 * The path to public static final String
	 * SINGLE_ELEMENT_WITH_COMPLEX_TYPE_XSD_PATH = null;
	 * 
	 * /** Non instantiable constructor
	 */
	private ProtomakEngineTestConstants() {
	}

}
