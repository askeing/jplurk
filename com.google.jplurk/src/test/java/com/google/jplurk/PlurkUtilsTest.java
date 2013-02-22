package com.google.jplurk;

import junit.framework.TestCase;

public class PlurkUtilsTest extends TestCase {

    public void testBirthday() throws Exception {
        assertEquals("1911-01-01", DateTime.create(1911, 1, 1).birthday());
    }

    public void testOffset() throws Exception {
        assertEquals("2009-6-20T21:55:34", DateTime.create(2009, 6, 20, 21, 55,
                34).toTimeOffset());
    }

}
