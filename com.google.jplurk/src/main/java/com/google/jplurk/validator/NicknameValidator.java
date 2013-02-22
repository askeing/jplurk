package com.google.jplurk.validator;

import java.util.regex.Pattern;

public class NicknameValidator implements IValidator {

	private static Pattern pattern = Pattern.compile("[\\w\\d_]{3,}");

	public boolean validate(String value) {
		return pattern.matcher(value).matches();
	}

}
