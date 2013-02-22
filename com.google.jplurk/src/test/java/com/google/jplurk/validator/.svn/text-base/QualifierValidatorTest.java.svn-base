package com.google.jplurk.validator;

import junit.framework.TestCase;

import com.google.jplurk.exception.PlurkException;

public class QualifierValidatorTest extends TestCase {

	private boolean validate(String json) throws PlurkException {
		return IValidator.ValidatorUtils.validate(QualifierValidator.class, json);
	}

	public void testValidate() throws Exception, PlurkException {
		assertTrue(validate(":"));
		assertFalse(validate("___"));
		assertTrue(validate("has"));
		assertTrue(validate("says"));
	}

}
