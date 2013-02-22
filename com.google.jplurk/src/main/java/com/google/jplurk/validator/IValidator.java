package com.google.jplurk.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.jplurk.exception.PlurkException;

public interface IValidator {

	public boolean validate(String value);

	static class ValidatorUtils {

		static Log logger = LogFactory.getLog(ValidatorUtils.class);

		public static boolean validate(Class<? extends IValidator> validatorClazz, String value) throws PlurkException{

			if (value == null) {
				return false;
			}

			Object v = null;
			try {
				Constructor<?> ctor = validatorClazz.getConstructor(new Class<?>[0]);
				v = ctor.newInstance(new Object[0]);
			} catch (Exception e) {
				throw new PlurkException(
					"cannot create validator from [" + validatorClazz + "], please check validator has the default constructor.", e);
			}

			Boolean passValidation = false;
			try {
				Method method = validatorClazz.getMethod("validate", new Class<?>[]{String.class});
				passValidation = (Boolean) method.invoke(v, value);
			} catch (Exception e) {
				throw new PlurkException(
					"cannot invoke method from [" + validatorClazz + "]", e);
			}

			if (!passValidation) {
				logger.warn("value[" + value + "] cannot pass the validator[" + validatorClazz + "]");
			}

			return passValidation;

		}
	}

}
