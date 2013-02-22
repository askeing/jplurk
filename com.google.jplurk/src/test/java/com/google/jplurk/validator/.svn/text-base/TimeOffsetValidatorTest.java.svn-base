package com.google.jplurk.validator;

import com.google.jplurk.exception.PlurkException;

import junit.framework.TestCase;

public class TimeOffsetValidatorTest extends TestCase {
	private boolean validate(String json) throws PlurkException {
		return IValidator.ValidatorUtils.validate(TimeOffsetValidator.class, json);
	}

	public void testValidate() throws Exception, PlurkException {
		assertFalse(validate(null));
		assertFalse(validate(""));
		assertTrue(validate("2009-6-20T21:55:34"));
		assertTrue(validate("2009-10-20T1:55:4"));
	}
}
