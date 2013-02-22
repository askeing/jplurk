package com.google.jplurk.validator;

import com.google.jplurk.org.apache.commons.lang.math.NumberUtils;

public class PositiveIntegerValidator implements IValidator {

	public boolean validate(String value) {
		return NumberUtils.toInt(value, -1) > 0;
	}

}
