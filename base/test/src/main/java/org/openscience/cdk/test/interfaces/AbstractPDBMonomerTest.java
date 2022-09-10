/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IPDBMonomer;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IPDBMonomer} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractPDBMonomerTest extends AbstractMonomerTest {

    @Test
    public void testSetICode_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setICode(null);
        Assert.assertNull(monomer.getICode());
    }

    @Test
    public void testGetICode() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assert.assertNull(monomer.getICode());
        monomer.setICode("iCode");
        Assert.assertNotNull(monomer.getICode());
        Assert.assertEquals("iCode", monomer.getICode());
    }

    @Test
    public void testSetChainID_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setChainID(null);
        Assert.assertNull(monomer.getChainID());
    }

    @Test
    public void testGetChainID() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assert.assertNull(monomer.getChainID());
        monomer.setChainID("chainA");
        Assert.assertNotNull(monomer.getChainID());
        Assert.assertEquals("chainA", monomer.getChainID());
    }

    @Test
    public void testSetResSeq_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setResSeq(null);
        Assert.assertNull(monomer.getResSeq());
    }

    @Test
    public void testGetResSeq() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assert.assertNull(monomer.getResSeq());
        monomer.setResSeq("reqSeq");
        Assert.assertNotNull(monomer.getResSeq());
        Assert.assertEquals("reqSeq", monomer.getResSeq());
    }

    @Test
    @Override
    public void testToString() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        String description = monomer.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

}
