/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
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
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Override
    public void testClone() throws Exception {
        IMapping mapping = (IMapping) newChemObject();
        Object clone = mapping.clone();
        Assertions.assertTrue(clone instanceof IMapping);
    }

    public void testGetChemObject_int() {
        IChemObject object = newChemObject();
        IAtom atom0 = object.getBuilder().newInstance(IAtom.class);
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class);
        IMapping mapping = object.getBuilder().newInstance(IMapping.class, atom0, atom1);
        Assertions.assertEquals(atom0, mapping.getChemObject(0));
        Assertions.assertEquals(atom1, mapping.getChemObject(1));
    }

    public void testRelatedChemObjects() {
        IChemObject object = newChemObject();
        IAtom atom0 = object.getBuilder().newInstance(IAtom.class);
        IAtom atom1 = object.getBuilder().newInstance(IAtom.class);
        IMapping mapping = object.getBuilder().newInstance(IMapping.class, atom0, atom1);

        Iterator<IChemObject> iter = mapping.relatedChemObjects().iterator();
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertEquals(atom0, iter.next());
        Assertions.assertTrue(iter.hasNext());
        Assertions.assertEquals(atom1, iter.next());
        Assertions.assertFalse(iter.hasNext());
    }

    public void testClone_ChemObject() throws Exception {
        IMapping mapping = (IMapping) newChemObject();

        IMapping clone = (IMapping) mapping.clone();
        //IChemObject[] map = mapping.getRelatedChemObjects();
        //IChemObject[] mapClone = clone.getRelatedChemObjects();
        //assertEquals(map.length, mapClone.length);
        for (int f = 0; f < 2; f++) {
            for (int g = 0; g < 2; g++) {
                Assertions.assertNotNull(mapping.getChemObject(f));
                Assertions.assertNotNull(clone.getChemObject(g));
                Assertions.assertNotSame(mapping.getChemObject(f), clone.getChemObject(g));
            }
        }
    }
}
