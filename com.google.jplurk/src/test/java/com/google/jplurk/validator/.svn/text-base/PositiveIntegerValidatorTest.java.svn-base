package com.google.jplurk.validator;

import com.google.jplurk.exception.PlurkException;

import junit.framework.TestCase;

public class PositiveIntegerValidatorTest extends TestCase {

	private boolean validate(String json) throws PlurkException {
		return IValidator.ValidatorUtils.validate(
				PositiveIntegerValidator.class, json);
	}

	public void testValidate() throws Exception, PlurkException {

		assertFalse(validate(""));
		assertFalse(validate("abc"));
		assertFalse(validate("1a2b"));
		assertFalse(validate("-1"));
		assertFalse(validate("0"));
		assertTrue(validate("1"));
		
	}

}
