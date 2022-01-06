/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMapping;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IMapping} implementations.
 *
 * @cdk.module test-interfaces
 */
public abstract class AbstractMappingTest extends AbstractChemObjectTest {

    /**
     * Method to test whether the class complies with RFC #9.
     */
    public void testToString() {
        IMapping mapping = (IMapping) newChemObject();
        String description = mapping.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Override
    public void testClone() throws Exception {
        IMapping mapping = (IMapping) newChemObject();
        Object clone = mapping.clone();
        Assert.assertTrue(clone instanceof IMapping);
    }

    public void testGetChemObject_int() {
        IChemObject object = newChemObject();
        IAtom atom0 = object.getBuilder().newInstance(IAtom.class);
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class);
        IMapping mapping = object.getBuilder().newInstance(IMapping.class, atom0, atom1);
        Assert.assertEquals(atom0, mapping.getChemObject(0));
        Assert.assertEquals(atom1, mapping.getChemObject(1));
    }

    public void testRelatedChemObjects() {
        IChemObject object = newChemObject();
        IAtom atom0 = object.getBuilder().newInstance(IAtom.class);
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class);
        IMapping mapping = object.getBuilder().newInstance(IMapping.class, atom0, atom1);

        Iterator<IChemObject> iter = mapping.relatedChemObjects().iterator();
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(atom0, (IAtom) iter.next());
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(atom1, (IAtom) iter.next());
        Assert.assertFalse(iter.hasNext());
    }

    public void testClone_ChemObject() throws Exception {
        IMapping mapping = (IMapping) newChemObject();

        IMapping clone = (IMapping) mapping.clone();
        //IChemObject[] map = mapping.getRelatedChemObjects();
        //IChemObject[] mapClone = clone.getRelatedChemObjects();
        //assertEquals(map.length, mapClone.length);
        for (int f = 0; f < 2; f++) {
            for (int g = 0; g < 2; g++) {
                Assert.assertNotNull(mapping.getChemObject(f));
                Assert.assertNotNull(clone.getChemObject(g));
                Assert.assertNotSame(mapping.getChemObject(f), clone.getChemObject(g));
            }
        }
    }
}
