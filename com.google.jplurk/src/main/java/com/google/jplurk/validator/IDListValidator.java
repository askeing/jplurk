package com.google.jplurk.validator;

import org.json.JSONArray;

public class IDListValidator implements IValidator {

	public boolean validate(String value) {
		try {
			JSONArray a = new JSONArray(value);
			if (a.length() == 0) {
				return false;
			}
			for (int i = 0; i < a.length(); i++) {
				a.getInt(i);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
