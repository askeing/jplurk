package com.google.jplurk.validator;


public class EmailValidator implements IValidator {

	public boolean validate(String value) {
		return value.matches("(\\w+)(\\.\\w+)*@(\\w+\\.)(\\w+)(\\.\\w+)*");
	}

}
