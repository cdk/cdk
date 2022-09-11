package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @author John May
 * @cdk.module test-fingerprint
 */
class SimpleAtomComparatorTest {

    private final IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

    @Test
    void testCompare_NullHybridization() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        Assertions.assertEquals(0, comparator.compare(a1, a2), "Null hybridzation should be equals");

    }

    @Test
    void testCompare_SameHybridization() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        a1.setHybridization(IAtomType.Hybridization.SP3);
        a2.setHybridization(IAtomType.Hybridization.SP3);

        Assertions.assertEquals(0, comparator.compare(a1, a2), "Same hybridzation should be equals");

    }

    @Test
    void testCompare_DifferentHybridization() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        a1.setHybridization(IAtomType.Hybridization.SP2);
        a2.setHybridization(IAtomType.Hybridization.SP3);

        Assertions.assertEquals(-1, comparator.compare(a1, a2), "Atom 2 should have priority");

    }

    @Test
    void testCompare_DifferentSymbol() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "O");

        // can't do less than correctly without hamcrest?
        Assertions.assertTrue(comparator.compare(a1, a2) < 0, "oxygen should rank above carbon");
        Assertions.assertTrue(comparator.compare(a2, a1) > 0, "oxygen should rank above carbon (inverse)");

    }

}
