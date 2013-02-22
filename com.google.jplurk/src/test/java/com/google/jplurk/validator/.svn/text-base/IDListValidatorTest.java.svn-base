package com.google.jplurk.validator;

import junit.framework.TestCase;

import com.google.jplurk.exception.PlurkException;

public class IDListValidatorTest extends TestCase {

	private boolean validate(String json) throws PlurkException {
		return IValidator.ValidatorUtils.validate(IDListValidator.class, json);
	}

	public void testValidate() throws Exception, PlurkException {
		assertTrue(validate("[1]"));
		assertTrue(validate("[1, 2, 3,4]"));
		assertFalse(validate("[]"));
		assertFalse(validate("[23 4]"));
	}

}
