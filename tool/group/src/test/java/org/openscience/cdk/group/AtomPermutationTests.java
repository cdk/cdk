package org.openscience.cdk.group;

import org.junit.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * @author maclean
 * @cdk.module test-group
 */
public class AtomPermutationTests extends CDKTestCase {

    public static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();

    /**
     * This test is checking all permutations of an atom container to see
     * if the refiner gives the canonical labelling map (effectively).
     */
    public void checkForCanonicalForm(IAtomContainer atomContainer) {
        AtomContainerAtomPermutor permutor = new AtomContainerAtomPermutor(atomContainer);
        AtomDiscretePartitionRefiner refiner = new AtomDiscretePartitionRefiner();
        refiner.refine(atomContainer);
        Permutation best = refiner.getBest().invert();
        String cert = AtomContainerPrinter.toString(atomContainer, best, true);
        refiner.reset();
        while (permutor.hasNext()) {
            IAtomContainer permutedContainer = permutor.next();
            refiner.refine(permutedContainer);
            best = refiner.getBest().invert();
            String permCert = AtomContainerPrinter.toString(permutedContainer, best, true);
            Assert.assertEquals(cert, permCert);
            refiner.reset();
        }
    }

    @Test
    public void testDisconnectedAtomCarbonCompound() {
        String acpString = "C0C1C2 0:2(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

    @Test
    public void testDisconnectedBondsCarbonCompound() {
        String acpString = "C0C1C2C3 0:2(1),1:3(2)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

    @Test
    public void testSimpleCarbonCompound() {
        String acpString = "C0C1C2C3 0:1(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

    @Test
    public void testCyclicCarbonCompound() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

    @Test
    public void testDoubleBondCyclicCarbonCompound() {
        String acpString = "C0C1C2C3 0:1(1),0:3(2),1:2(2),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

    @Test
    public void testSimpleCarbonOxygenCompound() {
        String acpString = "O0C1C2 0:1(2),1:2(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

    @Test
    public void testCyclicCarbonOxygenCompound() {
        String acpString = "O0C1O2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        checkForCanonicalForm(ac);
    }

}
