package org.openscience.cdk.rinchi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyBase26Test {

    @Test
    void getBase26Triplet_2703_DZZ() {
        assertEquals("DZZ", KeyBase26.getBase26Triplet(2703));
    }

    @Test
    void getBase26Triplet_2704_FAA() {
        assertEquals("FAA", KeyBase26.getBase26Triplet(2704));
    }
    
    @Test
    void getBase26Triplet_2704_FaAA() {
        assertEquals("FAA", KeyBase26.getBase26Triplet(16383));
    }

    @Test
    public void base26Triplet_701_BAZ_Test() {
        assertEquals("BAZ", KeyBase26.getBase26Triplet(701));
    }

    @Test
    public void base26Triplet_676_BAA_Test() {
        assertEquals("BAA",  KeyBase26.getBase26Triplet(676));
    }

    @Test
    public void base26Triplet_0_AAA_Test() {
        assertEquals("AAA", KeyBase26.getBase26Triplet(0));
    }

    @Test
    public void base26Doublet_675_ZZ_Test() {
        assertEquals("ZZ", KeyBase26.getBase26Doublet(675));
    }

    @Test
    public void base26Doublet_256_JW_Test() {
        assertEquals("JW", KeyBase26.getBase26Doublet(256));
    }
}