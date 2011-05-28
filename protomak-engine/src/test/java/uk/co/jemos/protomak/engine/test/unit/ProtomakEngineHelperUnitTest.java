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

	public void testTargetNameSpaceToProtoPackageConversion() {

		String expectedPackageName = "simple-one-level.eu.jemos.www";

		String packageName = ProtomakEngineHelper
				.convertTargetNsToProtoPackageName(ProtomakEngineTestConstants.TEST_TARGET_NAMESPACE_WITH_HTTP_PREFIX);
		Assert.assertNotNull("The package name cannot be null!", packageName);

	}

	// ------------------->> Getters / Setters

	//------------------->> Private methods

	//------------------->> equals() / hashcode() / toString()

	//------------------->> Inner classes

}
