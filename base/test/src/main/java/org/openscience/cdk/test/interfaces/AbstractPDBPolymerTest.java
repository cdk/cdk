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
        Assert.assertEquals(0, pdbPolymer.getStructures().size());
        IPDBStructure structure = pdbPolymer.getBuilder().newInstance(IPDBStructure.class);
        pdbPolymer.addStructure(structure);
        Assert.assertEquals(structure, pdbPolymer.getStructures().iterator().next());
    }

    @Test
    public void testAddStructure_IPDBStructure() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IPDBStructure structure = pdbPolymer.getBuilder().newInstance(IPDBStructure.class);
        pdbPolymer.addStructure(structure);
        Assert.assertEquals(1, pdbPolymer.getStructures().size());
    }

    @Test
    @Override
    public void testGetMonomerCount() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        Assert.assertEquals(0, pdbPolymer.getMonomerCount());

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName(new String("HOH"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);
        Assert.assertNotNull(pdbPolymer.getAtom(0));
        Assert.assertNotNull(pdbPolymer.getAtom(1));
        Assert.assertNotNull(pdbPolymer.getAtom(2));
        Assert.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
        Assert.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
        Assert.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));

        Assert.assertEquals(2, pdbPolymer.getMonomerCount());
    }

    @Test
    @Override
    public void testGetMonomerNames() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        Assert.assertEquals(0, pdbPolymer.getMonomerNames().size());

        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IStrand oStrand2 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand2.setStrandName("B");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName(new String("HOH"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);
        Assert.assertNotNull(pdbPolymer.getAtom(0));
        Assert.assertNotNull(pdbPolymer.getAtom(1));
        Assert.assertNotNull(pdbPolymer.getAtom(2));
        Assert.assertEquals(oPDBAtom1, pdbPolymer.getAtom(0));
        Assert.assertEquals(oPDBAtom2, pdbPolymer.getAtom(1));
        Assert.assertEquals(oPDBAtom3, pdbPolymer.getAtom(2));

        Assert.assertEquals(3, pdbPolymer.getMonomerNames().size());
        Assert.assertTrue(pdbPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
        Assert.assertTrue(pdbPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
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
        oMono1.setMonomerName(new String("TRP279"));
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName(new String("HOH"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono2, oStrand2);

        Assert.assertEquals(oMono1, pdbPolymer.getMonomer("TRP279", "A"));
        Assert.assertEquals(oMono2, pdbPolymer.getMonomer("HOH", "B"));
    }

    @Test
    public void testAddAtom_IPDBAtom() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();

        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1);
        pdbPolymer.addAtom(oPDBAtom2);

        Assert.assertEquals(2, pdbPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IPDBAtom_IStrand() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IPDBMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IPDBMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

        Assert.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
        Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
        Assert.assertEquals(3, pdbPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IPDBAtom_IMonomer_IStrand() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IPDBMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IPDBMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom3 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oStrand1);
        pdbPolymer.addAtom(oPDBAtom3, oMono1, oStrand1);

        Assert.assertEquals(2, pdbPolymer.getMonomer("", "A").getAtomCount());
        Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
        Assert.assertEquals(3, pdbPolymer.getAtomCount());
    }

    @Test
    public void testAddAtom_IPDBAtom_IMonomer() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IPDBMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IPDBMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assert.assertEquals(1, pdbPolymer.getMonomer("TRP279", "A").getAtomCount());
    }

    @Test
    @Override
    public void testGetStrandCount() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assert.assertEquals(1, pdbPolymer.getStrandCount());
    }

    @Test
    @Override
    public void testGetStrand_String() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assert.assertEquals(oStrand1, pdbPolymer.getStrand("A"));
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
        oMono1.setMonomerName(new String("TRP279"));
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName(new String("GLY123"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
        Map<String, IStrand> strands = new Hashtable<>();
        strands.put("A", oStrand1);
        strands.put("B", oStrand2);

        Assert.assertEquals(strands.keySet(), pdbPolymer.getStrandNames());
    }

    @Test
    @Override
    public void testRemoveStrand_String() {
        IPDBPolymer pdbPolymer = (IPDBPolymer) newChemObject();
        IStrand oStrand1 = pdbPolymer.getBuilder().newInstance(IStrand.class);
        oStrand1.setStrandName("A");
        IMonomer oMono1 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName(new String("TRP279"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);

        Assert.assertTrue(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
        Assert.assertEquals(1, pdbPolymer.getAtomCount());
        pdbPolymer.removeStrand("A");
        Assert.assertFalse(pdbPolymer.getStrandNames().contains(oStrand1.getStrandName()));
        Assert.assertEquals(0, pdbPolymer.getAtomCount());
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
        oMono1.setMonomerName(new String("TRP279"));
        IMonomer oMono2 = pdbPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName(new String("GLY123"));
        IPDBAtom oPDBAtom1 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        IPDBAtom oPDBAtom2 = pdbPolymer.getBuilder().newInstance(IPDBAtom.class, "C");
        pdbPolymer.addAtom(oPDBAtom1, oMono1, oStrand1);
        pdbPolymer.addAtom(oPDBAtom2, oMono2, oStrand2);
        Map<String, IStrand> strands = new Hashtable<>();
        strands.put("A", oStrand1);
        strands.put("B", oStrand2);

        Assert.assertEquals(strands, pdbPolymer.getStrands());
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
        IPDBPolymer polymer = (IPDBPolymer) newChemObject();
        Object clone = polymer.clone();
        Assert.assertTrue(clone instanceof IBioPolymer);
    }

}
