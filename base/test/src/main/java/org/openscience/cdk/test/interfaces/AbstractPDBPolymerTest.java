/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Hashtable;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IBioPolymer;
import org.openscience.cdk.interfaces.IMonomer;
import org.openscience.cdk.interfaces.IPDBAtom;
import org.openscience.cdk.interfaces.IPDBMonomer;
import org.openscience.cdk.interfaces.IPDBPolymer;
import org.openscience.cdk.interfaces.IPDBStructure;
import org.openscience.cdk.interfaces.IStrand;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IPDBPolymer} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractPDBPolymerTest extends AbstractBioPolymerTest {

    @Test
    public void testGetStructures() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        Assertions.assertEquals(0, pdbPolymer.getStructures().size());
        IPDBStructure structure = pdbPolymer.getBuilder().newInstance(IPDBStructure.class);
        pdbPolymer.addStructure(structure);
        Assertions.assertEquals(structure, pdbPolymer.getStructures().iterator().next());
    }

    @Test
    public void testAddStructure_IPDBStructure() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IPDBStructure structure = pdbPolymer.getBuilder().newInstance(IPDBStructure.class);
        pdbPolymer.addStructure(structure);
        Assertions.assertEquals(1, pdbPolymer.getStructures().size());
    }

    @Test
    @Override
    public void testGetMonomerCount() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        Assertions.assertEquals(0, pdbPolymer.getMonomerCount());

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);
        Assertions.assertNotNull(pdbPolymer.getAtom(0));
        Assertions.assertNotNull(pdbPolymer.getAtom(1));
        Assertions.assertNotNull(pdbPolymer.getAtom(2));
        Assertions.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
        Assertions.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
        Assertions.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));

        Assertions.assertEquals(2, pdbPolymer.getMonomerCount());
    }

    @Test
    @Override
    public void testGetMonomerNames() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        Assertions.assertEquals(0, pdbPolymer.getMonomerNames().size());

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);
        Assertions.assertNotNull(pdbPolymer.getAtom(0));
        Assertions.assertNotNull(pdbPolymer.getAtom(1));
        Assertions.assertNotNull(pdbPolymer.getAtom(2));
        Assertions.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
        Assertions.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
        Assertions.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));

        Assertions.assertEquals(3, pdbPolymer.getMonomerNames().size());
        Assertions.assertTrue(pdbPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
        Assertions.assertTrue(pdbPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
    }

    @Test
    @Override
    public void testGetMonomer_String_String() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);

        Assertions.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
        Assertions.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
    }

    @Test
    public void testAddAtom_IPDBAtom() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();

        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2);

        Assertions.assertEquals(2, pdbPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IPDBAtom_IStrand() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IPDBMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IPDBMonomer.class);
        oMono1.setMonomerName("TRP279");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

        Assertions.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
        Assertions.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
        Assertions.assertEquals(3, pdbPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IPDBAtom_IMonomer_IStrand() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IPDBMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IPDBMonomer.class);
        oMono1.setMonomerName("TRP279");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

        Assertions.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
        Assertions.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
        Assertions.assertEquals(3, pdbPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IPDBAtom_IMonomer() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IPDBMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IPDBMonomer.class);
        oMono1.setMonomerName("TRP279");
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assertions.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
    }

    @Test
    @Override
    public void testGetStrandCount() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assertions.assertEquals(1, pdbPolymer.getStrandCount());
    }

    @Test
    @Override
    public void testGetStrand_String() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assertions.assertEquals(oStrand1, pdbPolymer.getStrand("A"));
    }

    @Test
    @Override
    public void testGetStrandNames() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("GLY123");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
        Map<String, IStrand> strands = new Hashtable<>();
        strands.put("A", oStrand1);
        strands.put("B", oStrand2);

        Assertions.assertEquals(strands.keySet(), pdbPolymer.getStrandNames());
    }

    @Test
    @Override
    public void testRemoveStrand_String() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assertions.assertTrue(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
        Assertions.assertEquals(1, pdbPolymer.getAtomCount());
        pdbPolymer.removeStrand("A");
        Assertions.assertFalse(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
        Assertions.assertEquals(0, pdbPolymer.getAtomCount());
    }

    @Test
    @Override
    public void testGetStrands() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("GLY123");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
        Map<String, IStrand> strands = new Hashtable<>();
        strands.put("A", oStrand1);
        strands.put("B", oStrand2);

        Assertions.assertEquals(strands, pdbPolymer.getStrands());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        String description = pdbPolymer.toString();
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
        IPDBPolymer polymer = (IPDBPolymer) newChemObject();
        Object clone = polymer.clone();
        Assertions.assertTrue(clone instanceof IBioPolymer);
    }

}
