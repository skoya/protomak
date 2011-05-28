/**
 * 
 */
package uk.co.jemos.protomak.engine.test.unit;

import junit.framework.Assert;

import org.junit.Test;

import uk.co.jemos.protomak.engine.test.utils.ProtomakEngineTestConstants;
import uk.co.jemos.protomak.engine.utils.ProtomakEngineHelper;

/**
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
	public void testHttpTargetNsToProtoPackageConversion() {

		String expectedPackageName = "simple_one_level.eu.jemos.www";

		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(ProtomakEngineTestConstants.TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX);
		verifyPackageName(expectedPackageName, packageName);

	}

	@Test
	public void testTargetNsToProtoPackageConversionWithSimpleName() {
		String expectedPackageName = "foo";
		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(expectedPackageName);
		verifyPackageName(expectedPackageName, packageName);
	}

	@Test
	public void testTargetNsToProtoPackageConversionWithSimpleNameWithDots() {
		String expectedPackageName = "foo.bar.baz";
		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(expectedPackageName);
		verifyPackageName(expectedPackageName, packageName);
	}

	@Test
	public void testTargetNsToProtoPackageConversionWithSimpleNameWithSlashes() {
		String expectedPackageName = "foo.bar.baz";
		String packageName = ProtomakEngineHelper.convertTargetNsToProtoPackageName("foo/bar/baz");
		verifyPackageName(expectedPackageName, packageName);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTargetNsToProtoPackageConversionWithPackageNameEndingWithDot() {
		ProtomakEngineHelper.convertTargetNsToProtoPackageName("foo.");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTargetNsToProtoPackageConversionWithPackageNameStartingWithDot() {
		ProtomakEngineHelper.convertTargetNsToProtoPackageName(".foo");
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
	public void testHttpTargetNsWithUpperCasesToProtoPackageConversion() {

		String expectedPackageName = "foo.eu.jemos.www";

		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(ProtomakEngineTestConstants.TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX_AND_SOME_UPPERCASE);
		verifyPackageName(expectedPackageName, packageName);

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
