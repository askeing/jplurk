package com.google.jplurk;


public class PlurkBlocksTest extends PlurkClientTestCase {

    public void testBlocks() throws Exception {
        assertNotNull(client.getBlocks());
        assertNotNull(client.getBlocks(0));
    }

    public void testBlock() throws Exception {
        isSuccessTextOk(client.block("" + PLURK_API_UID));
        isSuccessTextOk(client.unblock("" + PLURK_API_UID));
    }

}
