/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAminoAcid;
import org.openscience.cdk.interfaces.IAtom;

/**
 * TestCase for {@link org.openscience.cdk.interfaces.IAminoAcid} implementations.
 *
 * @cdk.module test-interfaces
 *
 * @author Edgar Luttman &lt;edgar@uni-paderborn.de&gt;
 * @cdk.created 2001-08-09
 */
public abstract class AbstractAminoAcidTest extends AbstractMonomerTest {

    @Test
    public void testAddCTerminus_IAtom() {
        IAminoAcid m = (IAminoAcid) newChemObject();
        IAtom cTerminus = m.getBuilder().newInstance(IAtom.class, "C");
        m.addCTerminus(cTerminus);
        Assertions.assertEquals(cTerminus, m.getCTerminus());
    }

    @Test
    public void testGetCTerminus() {
        IAminoAcid m = (IAminoAcid) newChemObject();
        Assertions.assertNull(m.getCTerminus());
    }

    @Test
    public void testAddNTerminus_IAtom() {
        IAminoAcid m = (IAminoAcid) newChemObject();
        IAtom nTerminus = m.getBuilder().newInstance(IAtom.class, "N");
        m.addNTerminus(nTerminus);
        Assertions.assertEquals(nTerminus, m.getNTerminus());
    }

    @Test
    public void testGetNTerminus() {
        IAminoAcid m = (IAminoAcid) newChemObject();
        Assertions.assertNull(m.getNTerminus());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IAminoAcid m = (IAminoAcid) newChemObject();
        IAtom nTerminus = m.getBuilder().newInstance(IAtom.class, "N");
        m.addNTerminus(nTerminus);
        String description = m.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
        }

        m = (IAminoAcid) newChemObject();
        IAtom cTerminus = m.getBuilder().newInstance(IAtom.class, "C");
        m.addCTerminus(cTerminus);
        description = m.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
        }
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IAminoAcid aa = (IAminoAcid) newChemObject();
        Object clone = aa.clone();
        Assertions.assertTrue(clone instanceof IAminoAcid);
        Assertions.assertNotSame(aa, clone);

        aa = (IAminoAcid) newChemObject();
        IAtom nTerminus = aa.getBuilder().newInstance(IAtom.class, "N");
        aa.addNTerminus(nTerminus);
        clone = aa.clone();
        Assertions.assertTrue(clone instanceof IAminoAcid);
        Assertions.assertNotSame(aa, clone);

        aa = (IAminoAcid) newChemObject();
        IAtom cTerminus = aa.getBuilder().newInstance(IAtom.class, "C");
        aa.addCTerminus(cTerminus);
        clone = aa.clone();
        Assertions.assertTrue(clone instanceof IAminoAcid);
        Assertions.assertNotSame(aa, clone);
    }
}
