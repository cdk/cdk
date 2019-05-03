package org.openscience.cdk.fingerprint;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @author John May
 * @cdk.module test-fingerprint
 */
public class SimpleAtomComparatorTest {

    private IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();

    @Test
    public void testCompare_NullHybridization() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        Assert.assertEquals("Null hybridzation should be equals", 0, comparator.compare(a1, a2));

    }

    @Test
    public void testCompare_SameHybridization() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        a1.setHybridization(IAtomType.Hybridization.SP3);
        a2.setHybridization(IAtomType.Hybridization.SP3);

        Assert.assertEquals("Same hybridzation should be equals", 0, comparator.compare(a1, a2));

    }

    @Test
    public void testCompare_DifferentHybridization() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "C");

        a1.setHybridization(IAtomType.Hybridization.SP2);
        a2.setHybridization(IAtomType.Hybridization.SP3);

        Assert.assertEquals("Atom 2 should have priority", -1, comparator.compare(a1, a2));

    }

    @Test
    public void testCompare_DifferentSymbol() throws Exception {

        SimpleAtomComparator comparator = new SimpleAtomComparator();

        IAtom a1 = builder.newInstance(IAtom.class, "C");
        IAtom a2 = builder.newInstance(IAtom.class, "O");

        // can't do less than correctly without hamcrest?
        Assert.assertTrue("oxygen should rank above carbon", comparator.compare(a1, a2) < 0);
        Assert.assertTrue("oxygen should rank above carbon (inverse)", comparator.compare(a2, a1) > 0);

    }

}
