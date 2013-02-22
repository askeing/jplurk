package com.google.jplurk.validator;

import com.google.jplurk.exception.PlurkException;

import junit.framework.TestCase;

public class NicknameValidatorTest extends TestCase {

	private boolean validate(String value) throws PlurkException {
		return IValidator.ValidatorUtils.validate(NicknameValidator.class, value);
	}

	public void testValidate() throws Exception, PlurkException {
		assertFalse(validate("1"));
		assertFalse(validate("@123"));
		assertTrue(validate("123"));
		assertTrue(validate("qrtt1"));
		assertTrue(validate("qr_tt_1"));
		assertTrue(validate("_qr_tt_1"));
	}
}
