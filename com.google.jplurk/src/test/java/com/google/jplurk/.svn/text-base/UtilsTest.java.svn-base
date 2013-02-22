package com.google.jplurk;

import junit.framework.TestCase;

public class UtilsTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBase36() {
        // sample from http://en.wikipedia.org/wiki/Base_36
        assertEquals("1", Utils.base36(1));
        assertEquals("a", Utils.base36(10));
        assertEquals("2s", Utils.base36(100));
        assertEquals("rs", Utils.base36(1000));
        assertEquals("7ps", Utils.base36(10000));
        //
        // 
        assertEquals("6oxfe3", Utils.base36(404667435));
    }

}
