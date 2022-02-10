/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Hashtable;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Tests the functionality of {@link org.openscience.cdk.interfaces.IStrand} implementations.
 *
 * @author Martin Eklund &lt;martin.eklund@farmbio.uu.se&gt;
 * @cdk.module test-interfaces
 */
public abstract class AbstractStrandTest extends AbstractAtomContainerTest {

    @Test
    public void testGetStrandName() {
        IStrand oStrand = (IStrand) newChemObject();
        oStrand.setStrandName("A");

        Assert.assertEquals("A", oStrand.getStrandName());
    }

    @Test
    public void testGetStrandType() {
        IStrand oStrand = (IStrand) newChemObject();
        oStrand.setStrandType("DNA");

        Assert.assertEquals("DNA", oStrand.getStrandType());
    }

    /** The methods above effectively test SetStrandName and
     * SetStrandType as well, but I include SetStrandName and
     * SetStrandType explicitly as well (for concinstency).
     */

    @Test
    public void testSetStrandName_String() {
        IStrand oStrand = (IStrand) newChemObject();
        oStrand.setStrandName("A");

        Assert.assertEquals("A", oStrand.getStrandName());
    }

    @Test
    public void testSetStrandType_String() {
        IStrand oStrand = (IStrand) newChemObject();
        oStrand.setStrandType("DNA");

        Assert.assertEquals("DNA", oStrand.getStrandType());
    }

    @Test
    @Override
    public void testAddAtom_IAtom() {
        IStrand oStrand = (IStrand) newChemObject();
        IAtom oAtom1 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom1);
        oStrand.addAtom(oAtom2);

        Assert.assertEquals(2, oStrand.getAtomCount());
    }

    @Test
    public void testAddAtom_IAtom_IMonomer() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom1);
        oStrand.addAtom(oAtom2);
        oStrand.addAtom(oAtom3, oMono1);

        Assert.assertEquals(2, oStrand.getMonomer("").getAtomCount());
        Assert.assertEquals(1, oStrand.getMonomer("TRP279").getAtomCount());
    }

    @Test
    public void testGetMonomerCount() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom2, oMono1);
        oStrand.addAtom(oAtom3, oMono2);

        Assert.assertEquals(2, oStrand.getMonomerCount());
    }

    @Test
    public void testGetMonomer_String() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom2, oMono1);
        oStrand.addAtom(oAtom3, oMono2);

        Assert.assertEquals(oMono1, oStrand.getMonomer("TRP279"));
        Assert.assertEquals(oMono2, oStrand.getMonomer("HOH"));
        Assert.assertNull(oStrand.getMonomer("TEST"));
    }

    @Test
    public void testGetMonomerNames() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom2, oMono1);
        oStrand.addAtom(oAtom3, oMono2);
        Map<String, IMonomer> monomers = new Hashtable<>();
        IMonomer oMon = oStrand.getBuilder().newInstance(IMonomer.class);
        oMon.setMonomerName("");
        oMon.setMonomerType("UNKNOWN");
        monomers.put("", oMon);
        monomers.put("TRP279", oMono1);
        monomers.put("HOH", oMono2);

        Assert.assertEquals(monomers.keySet(), oStrand.getMonomerNames());
        /*
         * Assert.assertEquals(3, oStrand.getMonomerNames().size());
         * Assert.assertTrue
         * (oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
         * Assert.
         * assertTrue(oStrand.getMonomerNames().contains(oMono2.getMonomerName
         * ()));
         */
    }

    @Test
    public void testRemoveMonomer_String() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom1, oMono1);
        Assert.assertTrue(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
        Assert.assertEquals(1, oStrand.getAtomCount());
        oStrand.removeMonomer("TRP279");
        Assert.assertFalse(oStrand.getMonomerNames().contains(oMono1.getMonomerName()));
        Assert.assertEquals(0, oStrand.getAtomCount());
    }

    @Test
    public void testGetMonomers() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom2, oMono1);
        oStrand.addAtom(oAtom3, oMono2);
        Map<String, IMonomer> monomers = new Hashtable<>();
        IMonomer oMon = oStrand.getBuilder().newInstance(IMonomer.class);
        oMon.setMonomerName("");
        oMon.setMonomerType("UNKNOWN");
        monomers.put("", oMon);
        monomers.put("TRP279", oMono1);
        monomers.put("HOH", oMono2);

        Assert.assertEquals(monomers.keySet(), oStrand.getMonomerNames());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IStrand oStrand = (IStrand) newChemObject();
        IMonomer oMono1 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oStrand.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom2 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oStrand.getBuilder().newInstance(IAtom.class, "C");
        oStrand.addAtom(oAtom2, oMono1);
        oStrand.addAtom(oAtom3, oMono2);
        IMonomer oMon = oStrand.getBuilder().newInstance(IMonomer.class);
        oMon.setMonomerName("");
        oMon.setMonomerType("UNKNOWN");
        String description = oStrand.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue('\n' != description.charAt(i));
            Assert.assertTrue('\r' != description.charAt(i));
        }
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IStrand strand = (IStrand) newChemObject();
        Object clone = strand.clone();
        Assert.assertNotNull(clone);
    }

}
