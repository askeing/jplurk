package com.google.jplurk.validator;

import junit.framework.TestCase;

import com.google.jplurk.exception.PlurkException;

public class TrueFalseLiteralValidatorTest extends TestCase {

	private boolean validate(String email) throws PlurkException{
		return IValidator.ValidatorUtils.validate(TrueFalseLiteralValidator.class, email);
	}

	public void testValidate() throws Exception, PlurkException {

		assertFalse(validate("1"));
		assertFalse(validate("0"));
		assertFalse(validate("tRue"));
		assertFalse(validate("tue"));
		assertFalse(validate("False"));
		
		assertTrue(validate("true"));
		assertTrue(validate("false"));
	}

}
