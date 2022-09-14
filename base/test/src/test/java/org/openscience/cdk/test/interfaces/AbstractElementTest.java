/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.interfaces;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.tools.diff.ElementDiff;

/**
 * Checks the functionality of {@link org.openscience.cdk.interfaces.IElement} implementations.
 *
 * @cdk.module test-interfaces
 *
 * @see org.openscience.cdk.Element
 */
public abstract class AbstractElementTest extends AbstractChemObjectTest {

    // test methods

    @Test
    public void testSetSymbol_String() {
        IElement e = (IElement) newChemObject();
        e.setSymbol("C");
        Assertions.assertEquals("C", e.getSymbol());
    }

    @Test
    public void testGetSymbol() {
        IElement e = (IElement) newChemObject();
        e.setSymbol("Ir");
        Assertions.assertEquals("Ir", e.getSymbol());
    }

    @Test
    public void testSetAtomicNumber_Integer() {
        IElement e = (IElement) newChemObject();
        e.setAtomicNumber(1);
        Assertions.assertEquals(1, e.getAtomicNumber().intValue());
    }

    @Test
    public void testGetAtomicNumber() {
        IElement e = (IElement) newChemObject();
        e.setAtomicNumber(1);
        Assertions.assertEquals(1, e.getAtomicNumber().intValue());
    }

    @Test
    @Override
    public void testClone() throws Exception {
        IElement elem = (IElement) newChemObject();
        Object clone = elem.clone();
        Assertions.assertTrue(clone instanceof IElement);

        // test that everything has been cloned properly
        String diff = ElementDiff.diff(elem, (IElement) clone);
        Assertions.assertNotNull(diff);
        Assertions.assertEquals(0, diff.length());
    }

    @Test
    public void testCloneDiff() throws Exception {
        IElement elem = (IElement) newChemObject();
        IElement clone = (IElement) elem.clone();
        Assertions.assertEquals("", ElementDiff.diff(elem, clone));
    }

    @Test
    public void testClone_Symbol() throws Exception {
        IElement elem = (IElement) newChemObject();
        elem.setSymbol("C");
        IElement clone = (IElement) elem.clone();

        // test cloning of symbol
        elem.setSymbol("H");
        Assertions.assertEquals("C", clone.getSymbol());
    }

    @Test
    public void testClone_IAtomicNumber() throws Exception {
        IElement elem = (IElement) newChemObject();
        elem.setAtomicNumber(6);
        IElement clone = (IElement) elem.clone();

        // test cloning of atomic number
        elem.setAtomicNumber(5); // don't care about symbol
        Assertions.assertEquals(6, clone.getAtomicNumber().intValue());
    }

    /** Test for RFC #9 */
    @Test
    public void testToString() {
        IElement elem = (IElement) newChemObject();
        String description = elem.toString();
        for (int i = 0; i < description.length(); i++) {
            Assertions.assertTrue(description.charAt(i) != '\n');
            Assertions.assertTrue(description.charAt(i) != '\r');
        }
    }
}
