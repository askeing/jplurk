package com.google.jplurk.action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Meta {

	String uri();

	String[] require();

	boolean isHttps() default false;

	Type type() default Type.GET;

}
