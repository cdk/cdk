/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
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
        Assertions.assertNull(monomer.getICode());
    }

    @Test
    public void testGetICode() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assertions.assertNull(monomer.getICode());
        monomer.setICode("iCode");
        Assertions.assertNotNull(monomer.getICode());
        Assertions.assertEquals("iCode", monomer.getICode());
    }

    @Test
    public void testSetChainID_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setChainID(null);
        Assertions.assertNull(monomer.getChainID());
    }

    @Test
    public void testGetChainID() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assertions.assertNull(monomer.getChainID());
        monomer.setChainID("chainA");
        Assertions.assertNotNull(monomer.getChainID());
        Assertions.assertEquals("chainA", monomer.getChainID());
    }

    @Test
    public void testSetResSeq_String() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        monomer.setResSeq(null);
        Assertions.assertNull(monomer.getResSeq());
    }

    @Test
    public void testGetResSeq() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        Assertions.assertNull(monomer.getResSeq());
        monomer.setResSeq("reqSeq");
        Assertions.assertNotNull(monomer.getResSeq());
        Assertions.assertEquals("reqSeq", monomer.getResSeq());
    }

    @Test
    @Override
    public void testToString() {
        IPDBMonomer monomer = (IPDBMonomer) newChemObject();
        String description = monomer.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
        }
    }

}
