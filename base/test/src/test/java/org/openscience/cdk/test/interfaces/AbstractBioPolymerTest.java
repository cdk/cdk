/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Hashtable;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IBioPolymer} implementations.
 *
 */
public abstract class AbstractBioPolymerTest extends AbstractPolymerTest {

    @Test
    @Override
    public void testGetMonomerCount() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        Assertions.assertEquals(0, oBioPolymer.getMonomerCount());

        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1);
        oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom3, oMono2, oStrand2);
        Assertions.assertNotNull(oBioPolymer.getAtom(0));
        Assertions.assertNotNull(oBioPolymer.getAtom(1));
        Assertions.assertNotNull(oBioPolymer.getAtom(2));
        Assertions.assertEquals(oAtom1, oBioPolymer.getAtom(0));
        Assertions.assertEquals(oAtom2, oBioPolymer.getAtom(1));
        Assertions.assertEquals(oAtom3, oBioPolymer.getAtom(2));

        Assertions.assertEquals(2, oBioPolymer.getMonomerCount());
    }

    @Test
    @Override
    public void testGetMonomerNames() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        Assertions.assertEquals(0, oBioPolymer.getMonomerNames().size());

        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1);
        oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom3, oMono2, oStrand2);
        Assertions.assertNotNull(oBioPolymer.getAtom(0));
        Assertions.assertNotNull(oBioPolymer.getAtom(1));
        Assertions.assertNotNull(oBioPolymer.getAtom(2));
        Assertions.assertEquals(oAtom1, oBioPolymer.getAtom(0));
        Assertions.assertEquals(oAtom2, oBioPolymer.getAtom(1));
        Assertions.assertEquals(oAtom3, oBioPolymer.getAtom(2));

        Assertions.assertEquals(3, oBioPolymer.getMonomerNames().size());
        Assertions.assertTrue(oBioPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
        Assertions.assertTrue(oBioPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
    }

    @Test
    public void testGetMonomer_String_String() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();

        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom3, oMono2, oStrand2);

        Assertions.assertEquals(oMono1, oBioPolymer.getMonomer("TRP279", "A"));
        Assertions.assertEquals(oMono2, oBioPolymer.getMonomer("HOH", "B"));
    }

    @Test
    @Override
    public void testAddAtom_IAtom() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();

        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1);
        oBioPolymer.addAtom(oAtom2);

        Assertions.assertEquals(2, oBioPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IAtom_IStrand() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oStrand1);
        oBioPolymer.addAtom(oAtom2, oStrand1);
        oBioPolymer.addAtom(oAtom3, oMono1, oStrand1);

        Assertions.assertEquals(2, oBioPolymer.getMonomer("", "A").getAtomCount());
        Assertions.assertEquals(1, oBioPolymer.getMonomer("TRP279", "A").getAtomCount());
        Assertions.assertEquals(3, oBioPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IAtom_IMonomer_IStrand() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom2, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom1, null, oStrand1);

        Assertions.assertEquals(2, oBioPolymer.getMonomer("TRP279", "A").getAtomCount());
        Assertions.assertEquals(0, oBioPolymer.getMonomer("", "A").getAtomCount());
    }

    @Test
    public void testGetStrandCount() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);

        Assertions.assertEquals(1, oBioPolymer.getStrandCount());
    }

    @Test
    public void testGetStrand_String() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);

        Assertions.assertEquals(oStrand1, oBioPolymer.getStrand("A"));
    }

    @Test
    public void testGetStrandNames() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        oStrand2.setStrandName("B");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("GLY123");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom2, oMono2, oStrand2);
        Map<String, IStrand> strands = new Hashtable<>();
        strands.put("A", oStrand1);
        strands.put("B", oStrand2);

        Assertions.assertEquals(strands.keySet(), oBioPolymer.getStrandNames());
    }

    @Test
    public void testRemoveStrand_String() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);

        Assertions.assertTrue(oBioPolymer.getStrandNames().contains(oStrand1.getStrandName()));
        Assertions.assertEquals(1, oBioPolymer.getAtomCount());
        oBioPolymer.removeStrand("A");
        Assertions.assertFalse(oBioPolymer.getStrandNames().contains(oStrand1.getStrandName()));
        Assertions.assertEquals(0, oBioPolymer.getAtomCount());
    }

    @Test
    public void testGetStrands() {
        IBioPolymer oBioPolymer = (IBioPolymer) newChemObject();
        IStrand oStrand1 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        IStrand oStrand2 = oBioPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        oStrand2.setStrandName("B");
        IMonomer oMono1 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oBioPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("GLY123");
        IAtom oAtom1 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oBioPolymer.getBuilder().newInstance(IAtom.class, "C");
        oBioPolymer.addAtom(oAtom1, oMono1, oStrand1);
        oBioPolymer.addAtom(oAtom2, oMono2, oStrand2);
        Map<String, IStrand> strands = new Hashtable<>();
        strands.put("A", oStrand1);
        strands.put("B", oStrand2);

        Assertions.assertEquals(strands, oBioPolymer.getStrands());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IBioPolymer bp = (IBioPolymer) newChemObject();
        String description = bp.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue('\n' != description.charAt(i));
            Assertions.assertTrue('\r' != description.charAt(i));
        }
    }

    /**
     * Method to test the clone() method
     */
    @Test
    @Override
    public void testClone() throws Exception {
        IBioPolymer polymer = (IBioPolymer) newChemObject();
        Object clone = polymer.clone();
        Assertions.assertTrue(clone instanceof IBioPolymer);
    }

}
