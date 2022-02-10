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
import org.openscience.cdk.interfaces.IPolymer;

/**
 * TestCase for {@link org.openscience.cdk.interfaces.IPolymer} implementations.
 *
 * @author Edgar Luttmann &lt;edgar@uni-paderborn.de&gt;
 * @author Martin Eklund &lt;martin.eklund@farmbio.uu.se&gt;
 * @cdk.created 2001-08-09
 * @cdk.module  test-interfaces
 */
public abstract class AbstractPolymerTest extends AbstractMoleculeTest {

    @Test
    @Override
    public void testAddAtom_IAtom() {
        IPolymer oPolymer = (IPolymer) newChemObject();

        IAtom oAtom1 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        oPolymer.addAtom(oAtom1);
        oPolymer.addAtom(oAtom2);

        Assert.assertEquals(2, oPolymer.getAtomCount());
        Assert.assertEquals(0, oPolymer.getMonomerCount());
    }

    @Test
    public void testAddAtom_IAtom_IMonomer() {
        IPolymer oPolymer = (IPolymer) newChemObject();
        IMonomer oMono1 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = null;
        IAtom oAtom1 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oPolymer.getBuilder().newInstance(IAtom.class, "C");

        oPolymer.addAtom(oAtom1);
        oPolymer.addAtom(oAtom2, oMono1);
        oPolymer.addAtom(oAtom3, oMono2);
        Assert.assertNotNull(oPolymer.getAtom(0));
        Assert.assertNotNull(oPolymer.getAtom(1));
        Assert.assertNotNull(oPolymer.getAtom(2));
        Assert.assertEquals(oAtom1, oPolymer.getAtom(0));
        Assert.assertEquals(oAtom2, oPolymer.getAtom(1));
        Assert.assertEquals(oAtom3, oPolymer.getAtom(2));
        Assert.assertEquals(3, oPolymer.getAtomCount());
        Assert.assertEquals(1, oPolymer.getMonomer("TRP279").getAtomCount());
        Assert.assertEquals(1, oPolymer.getMonomerCount());

        Assert.assertNotNull(oPolymer.getMonomer("TRP279"));
        Assert.assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
    }

    @Test
    public void testGetMonomerCount() {
        IPolymer oPolymer = (IPolymer) newChemObject();
        Assert.assertEquals(0, oPolymer.getMonomerCount());

        IMonomer oMono1 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom1 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        oPolymer.addAtom(oAtom1);
        oPolymer.addAtom(oAtom2, oMono1);
        oPolymer.addAtom(oAtom3, oMono2);

        Assert.assertEquals(3, oPolymer.getAtomCount());
        Assert.assertEquals(2, oPolymer.getMonomerCount());
    }

    @Test
    public void testGetMonomer_String() {
        IPolymer oPolymer = (IPolymer) newChemObject();

        IMonomer oMono1 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom1 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        oPolymer.addAtom(oAtom1, oMono1);
        oPolymer.addAtom(oAtom2, oMono1);
        oPolymer.addAtom(oAtom3, oMono2);

        Assert.assertEquals(oMono1, oPolymer.getMonomer("TRP279"));
        Assert.assertEquals(oMono2, oPolymer.getMonomer("HOH"));
        Assert.assertNull(oPolymer.getMonomer("Mek"));
    }

    @Test
    public void testGetMonomerNames() {
        IPolymer oPolymer = (IPolymer) newChemObject();
        Assert.assertEquals(0, oPolymer.getMonomerNames().size());

        IMonomer oMono1 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom1 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom2 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        oPolymer.addAtom(oAtom1);
        oPolymer.addAtom(oAtom2, oMono1);
        oPolymer.addAtom(oAtom3, oMono2);
        Map<String, IMonomer> monomers = new Hashtable<>();
        //IMonomer oMon = getBuilder().newMonomer();
        monomers.put("TRP279", oMono1);
        monomers.put("HOH", oMono2);

        Assert.assertEquals(2, oPolymer.getMonomerNames().size());
        Assert.assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
        Assert.assertTrue(oPolymer.getMonomerNames().contains(oMono2.getMonomerName()));
        Assert.assertEquals(monomers.keySet(), oPolymer.getMonomerNames());
    }

    @Test
    public void testRemoveMonomer_String() {
        IPolymer oPolymer = (IPolymer) newChemObject();
        IMonomer oMono1 = oPolymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IAtom oAtom1 = oPolymer.getBuilder().newInstance(IAtom.class, "C");
        oPolymer.addAtom(oAtom1, oMono1);
        Assert.assertTrue(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
        Assert.assertEquals(1, oPolymer.getAtomCount());

        oPolymer.removeMonomer("TRP279");
        Assert.assertFalse(oPolymer.getMonomerNames().contains(oMono1.getMonomerName()));
        Assert.assertEquals(0, oPolymer.getAtomCount());
    }

    /**
     * Method to test whether the class complies with RFC #9.
     */
    @Test
    @Override
    public void testToString() {
        IPolymer polymer = (IPolymer) newChemObject();
        IMonomer oMono1 = polymer.getBuilder().newInstance(IMonomer.class);
        oMono1.setMonomerName("TRP279");
        IMonomer oMono2 = polymer.getBuilder().newInstance(IMonomer.class);
        oMono2.setMonomerName("HOH");
        IAtom oAtom2 = polymer.getBuilder().newInstance(IAtom.class, "C");
        IAtom oAtom3 = polymer.getBuilder().newInstance(IAtom.class, "C");
        polymer.addAtom(oAtom2, oMono1);
        polymer.addAtom(oAtom3, oMono2);
        String description = polymer.toString();
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
        IPolymer polymer = (IPolymer) newChemObject();
        Object clone = polymer.clone();
        Assert.assertNotNull(clone);
    }

}
