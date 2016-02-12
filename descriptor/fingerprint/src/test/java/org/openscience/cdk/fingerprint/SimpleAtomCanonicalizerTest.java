package org.openscience.cdk.fingerprint;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author John May
 * @cdk.module test-fingerprint
 */
public class SimpleAtomCanonicalizerTest {

    @Test
    public void testCanonicalizeAtoms() throws CDKException {

        IAtomContainer container = TestMoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);

        Collection<IAtom> atoms = new SimpleAtomCanonicalizer().canonicalizeAtoms(container);

        List<IAtom> mutable = new ArrayList<IAtom>(atoms);
        for (IAtom atom : mutable.subList(0, 5)) {
            Assert.assertEquals("expect sp2 carbons in first 4 entries", "C", atom.getSymbol());
            Assert.assertEquals("expect sp2 carbons in first 4 entries", IAtomType.Hybridization.SP2,
                    atom.getHybridization());
        }
        for (IAtom atom : mutable.subList(5, 8)) {
            Assert.assertEquals("expect sp2 nitrogen at indices 5-7", "N", atom.getSymbol());
            Assert.assertEquals("expect sp2 nitrogen at indices 5-7", IAtomType.Hybridization.SP2,
                    atom.getHybridization());
        }

        Assert.assertEquals("expect nitrogen at indices 8", "N", mutable.get(8).getSymbol());
        Assert.assertEquals("expect sp3 nitrogen at indices 8", IAtomType.Hybridization.SP3, mutable.get(8)
                .getHybridization());

        Assert.assertEquals("expect nitrogen at indices 9", "N", mutable.get(9).getSymbol());
        Assert.assertEquals("expect sp3 nitrogen at indices 9", IAtomType.Hybridization.PLANAR3, mutable.get(9)
                .getHybridization());

    }

}
