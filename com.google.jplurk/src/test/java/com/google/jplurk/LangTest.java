package com.google.jplurk;

import junit.framework.TestCase;

public class LangTest extends TestCase {

	public void testContains() throws Exception {

		assertFalse(Lang.contains("foo"));
		assertTrue(Lang.contains("en"));
		assertTrue(Lang.contains("tr_ch"));
	}
}
