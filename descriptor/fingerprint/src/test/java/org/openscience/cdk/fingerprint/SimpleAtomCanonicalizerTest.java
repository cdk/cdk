package org.openscience.cdk.fingerprint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
class SimpleAtomCanonicalizerTest {

    @Test
    void testCanonicalizeAtoms() throws CDKException {

        IAtomContainer container = TestMoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);

        Collection<IAtom> atoms = new SimpleAtomCanonicalizer().canonicalizeAtoms(container);

        List<IAtom> mutable = new ArrayList<>(atoms);
        for (IAtom atom : mutable.subList(0, 5)) {
            Assertions.assertEquals("C", atom.getSymbol(), "expect sp2 carbons in first 4 entries");
            Assertions.assertEquals(IAtomType.Hybridization.SP2, atom.getHybridization(), "expect sp2 carbons in first 4 entries");
        }
        for (IAtom atom : mutable.subList(5, 8)) {
            Assertions.assertEquals("N", atom.getSymbol(), "expect sp2 nitrogen at indices 5-7");
            Assertions.assertEquals(IAtomType.Hybridization.SP2, atom.getHybridization(), "expect sp2 nitrogen at indices 5-7");
        }

        Assertions.assertEquals("N", mutable.get(8).getSymbol(), "expect nitrogen at indices 8");
        Assertions.assertEquals(IAtomType.Hybridization.SP3, mutable.get(8)
                                                                    .getHybridization(), "expect sp3 nitrogen at indices 8");

        Assertions.assertEquals("N", mutable.get(9).getSymbol(), "expect nitrogen at indices 9");
        Assertions.assertEquals(IAtomType.Hybridization.PLANAR3, mutable.get(9)
                                                                        .getHybridization(), "expect sp3 nitrogen at indices 9");

    }

}
