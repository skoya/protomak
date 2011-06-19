/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;
import uk.co.jemos.xsds.protomak.proto.MessageAttributeOptionalType;

/**
 * Unit tests for the {@link ProtomakEngineHelper} class.
 * 
 * @author mtedone
 * 
 */
public class ProtomakEngineHelperUnitTest {

	//------------------->> Constants

	//------------------->> Instance / Static variables

	//------------------->> Constructors

	//------------------->> Public methods

	@Test(expected = IllegalArgumentException.class)
	public void testTargetNameSpaceToProtoPackageConversionForNullString() {
		ProtomakEngineHelper.convertTargetNsToProtoPackageName(null);
	}

	@Test
	public void testTargetNsToProtoPackageConversion() {

		String expectedPackageName = "simple_one_level.eu.jemos.www;";

		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(ProtomakEngineTestConstants.TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX);
		verifyPackageName(expectedPackageName, packageName);

	}

	@Test
	public void testTargetNsToProtoPackageConversionWithSimpleName() {
		String expectedPackageName = "foo";
		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(expectedPackageName);
		verifyPackageName(expectedPackageName + ";", packageName);
	}

	@Test
	public void testTargetNsToProtoPackageConversionWithSimpleNameWithDots() {
		String expectedPackageName = "foo.bar.baz";
		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(expectedPackageName);
		verifyPackageName(expectedPackageName + ";", packageName);
	}

	@Test
	public void testTargetNsToProtoPackageConversionWithSimpleNameWithSlashes() {
		String expectedPackageName = "baz.bar.foo";
		String packageName = ProtomakEngineHelper.convertTargetNsToProtoPackageName("foo/bar/baz");
		verifyPackageName(expectedPackageName + ";", packageName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTargetNsToProtoPackageConversionWithPackageNameEndingWithDot() {
		ProtomakEngineHelper.convertTargetNsToProtoPackageName("foo.");
	}

	@Test
	public void testTargetNsToProtoPackageConversionWithPackageNameStartingWithDot() {
		String expectedPackageName = "foo";
		String actualPackageName = ProtomakEngineHelper.convertTargetNsToProtoPackageName(".foo");
		verifyPackageName(expectedPackageName + ";", actualPackageName);
	}

	@Test
	public void testTargetNsToProtoPackageConversionWithPackageNameStartingWithNumber() {
		for (int i = 0; i < 10; i++) {
			try {
				ProtomakEngineHelper.convertTargetNsToProtoPackageName(i + "foo");
				Assert.fail("The method for " + i + "foo should have thrown an exception");
			} catch (IllegalArgumentException e) {
				//OK, expected
			}
		}

	}

	@Test
	public void testTargetNsWithUpperCasesToProtoPackageConversion() {

		String expectedPackageName = "foo.eu.jemos.www;";

		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(ProtomakEngineTestConstants.TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX_AND_SOME_UPPERCASE);
		verifyPackageName(expectedPackageName, packageName);

	}

	@Test
	public void testTargetNsWithRelativeUrl() {

		String expectedPackageName = "my_namespace.baz.bar.foo;";

		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(ProtomakEngineTestConstants.TEST_TARGET_NAMESPACE_WITH_RELATIVE_URL);
		verifyPackageName(expectedPackageName, packageName);

	}

	@Test
	public void testOptionality() {

		int minOccurs = 0;
		int maxOccurs = 1;

		MessageAttributeOptionalType type = ProtomakEngineHelper.getMessageAttributeOptionality(
				minOccurs, maxOccurs);
		Assert.assertEquals("The message attribute optional type does not match the expected one!",
				MessageAttributeOptionalType.OPTIONAL, type);

		minOccurs = 0;
		maxOccurs = -1;
		type = ProtomakEngineHelper.getMessageAttributeOptionality(minOccurs, maxOccurs);
		Assert.assertEquals("The message attribute optional type does not match the expected one!",
				MessageAttributeOptionalType.REPEATED, type);

		minOccurs = 1;
		maxOccurs = 1;
		type = ProtomakEngineHelper.getMessageAttributeOptionality(minOccurs, maxOccurs);
		Assert.assertEquals("The message attribute optional type does not match the expected one!",
				MessageAttributeOptionalType.REQUIRED, type);

		minOccurs = 1;
		maxOccurs = -1;
		type = ProtomakEngineHelper.getMessageAttributeOptionality(minOccurs, maxOccurs);
		Assert.assertEquals("The message attribute optional type does not match the expected one!",
				MessageAttributeOptionalType.REPEATED, type);

		//Now let's test some negative scenarios
		maxOccurs = 1;
		for (int i = -1; i < 3; i++) {
			minOccurs = i;
			if (minOccurs < 0 || minOccurs > 1) {
				try {
					ProtomakEngineHelper.getMessageAttributeOptionality(minOccurs, maxOccurs);
					Assert.fail("The method should have failed since minOccurs is: " + minOccurs);
				} catch (IllegalArgumentException e) {
					//OK, expected
				}
			}
		}

		minOccurs = 0;
		for (int i = -2; i < 3; i++) {
			maxOccurs = i;
			if (maxOccurs < -1 || maxOccurs > 1) {
				try {
					ProtomakEngineHelper.getMessageAttributeOptionality(minOccurs, maxOccurs);
					Assert.fail("The method should have failed since maxOccurs is: " + maxOccurs);
				} catch (IllegalArgumentException e) {
					//OK, expected
				}
			}
		}

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	/**
	 * It verifies that the package name matches the expectations.
	 * 
	 * @param expectedPackageName
	 *            The expected package name
	 * @param packageName
	 *            The actual package name
	 */
	private void verifyPackageName(String expectedPackageName, String packageName) {
		Assert.assertNotNull("The package name cannot be null!", packageName);
		Assert.assertEquals("The expected and actual proto package name don't match",
				expectedPackageName, packageName);
	}

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
