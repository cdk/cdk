/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
 *               2009  Mark Rijnbeek <markr@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.ringsearch;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.SlowTest;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * @cdk.module test-standard
 */
public class SSSRFinderTest extends CDKTestCase {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(SSSRFinderTest.class);

    public SSSRFinderTest() {
        super();
    }

    @Test
    public void testSSSRFinder_IAtomContainer() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        SSSRFinder finder = new SSSRFinder(molecule);
        Assert.assertNotNull(finder);
    }

    @Test
    public void testFindSSSR() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
    }

    @Test
    public void testFindSSSR_IAtomContainer() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlphaPinene();
        SSSRFinder sssrFinder = new SSSRFinder(molecule);
        IRingSet ringSet = sssrFinder.findSSSR();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
    }

    @Test
    public void testGetAtomContainerCount() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("c1ccccc1");
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(1, ringSet.getAtomContainerCount());
    }

    @Test
    public void testRingFlags1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("c1ccccc1");
        new SSSRFinder(molecule).findSSSR();

        int count = 0;
        Iterator atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getFlag(CDKConstants.ISINRING)) count++;
        }
        Assert.assertEquals("All atoms in benzene were not marked as being in a ring", 6, count);
    }

    @Test
    public void testRingFlags2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("C1CCCC1CC");
        new SSSRFinder(molecule).findSSSR();

        int count = 0;
        Iterator atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getFlag(CDKConstants.ISINRING)) count++;
        }
        Assert.assertEquals("All ring atoms in 2-ethyl cyclopentane were not marked as being in a ring", 5, count);
    }

    @Test
    public void testBicyclicCompound() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("C1CCC(CCCCC2)C2C1");
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
    }

    /**
     * @cdk.bug 826942
     */
    @Test
    public void testSFBug826942() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer molecule = sp.parseSmiles("C1CCC2C(C1)C4CCC3(CCCCC23)(C4)");
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(4, ringSet.getAtomContainerCount());
    }

    @Test
    public void testProblem1() throws Exception {
        IAtomContainer molecule = null;
        IRing ring = null;
        String filename = "data/mdl/figueras-test-sep3D.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IAtomContainer) reader.read((IChemObject) new AtomContainer());
        reader.close();
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(3, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++) {
            ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    @Test
    public void testLoopProblem() throws Exception {
        String filename = "data/mdl/ring_03419.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IAtomContainer molecule = (IAtomContainer) reader.read((IChemObject) new AtomContainer());
        reader.close();
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(12, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++) {
            IRing ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    @Test
    public void testProblem2() throws Exception {
        IAtomContainer molecule = null;
        IRing ring = null;
        String filename = "data/mdl/figueras-test-buried.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IAtomContainer) reader.read((IChemObject) new AtomContainer());
        reader.close();
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(10, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++) {
            ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    @Test
    public void testProblem3() throws Exception {
        IAtomContainer molecule = null;
        IRing ring = null;
        String filename = "data/mdl/figueras-test-inring.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IAtomContainer) reader.read((IChemObject) new AtomContainer());
        reader.close();
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(5, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++) {
            ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    /**
     * @cdk.bug 891021
     */
    @Test
    public void testBug891021() throws Exception {
        IAtomContainer molecule = null;
        String filename = "data/mdl/too.many.rings.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IAtomContainer) reader.read((IChemObject) new AtomContainer());
        reader.close();
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(57, ringSet.getAtomContainerCount());
    }

    /**
     * Convenience method for giving a string representation
     * of this ring based on the number of the atom in a given
     * molecule.
     *
     * @param molecule  A molecule to determine an atom number for each ring atom
     * @return          string representation of this ring
     */
    private String toString(IRing ring, IAtomContainer molecule) throws Exception {
        String str = "";
        for (int f = 0; f < ring.getAtomCount(); f++) {
            str += molecule.getAtomNumber(ring.getAtom(f)) + " - ";
        }
        return str;
    }

    /**
     * Method findRelevantRings() computes the rings (cycles) that are contained
     * in *some* SSSR (minimum cycle basis).
     */
    @Category(SlowTest.class)
    @Test
    public void testBuckyballRelevantRings() throws Exception {
        IAtomContainer buckyball = createBuckyBall();
        IRingSet ringSetRelevant = new SSSRFinder(buckyball).findRelevantRings();
        ringCount(ringSetRelevant, 6, 20);
        ringCount(ringSetRelevant, 5, 12);

        Assert.assertFalse("Duplicate rings exist", checkForDuplicateRingsInSet(ringSetRelevant));
    }

    /**
     * Method findSSSR() computes one (of possibly several) SSSRs.
     */
    @Category(SlowTest.class)
    @Test
    public void testBuckyballSSSR() throws Exception {
        IAtomContainer buckyball = createBuckyBall();
        IRingSet ringSetSSSR = new SSSRFinder(buckyball).findSSSR();
        ringCount(ringSetSSSR, 6, 19);
        ringCount(ringSetSSSR, 5, 12);
        Assert.assertFalse("Duplicate rings exist", checkForDuplicateRingsInSet(ringSetSSSR));
    }

    /**
     * Method findEssentialRings() computes the rings (cycles) contained
     * in *any* SSSR (minimum cycle basis). A SSSR for the bucky ball has
     * 19 6-rings; by symmetry, this means that any 19 out of the 20 6-rings
     * can be chosen for a SSSR. In other words, none of the 20 6-rings
     * is essential.
     */
    @Test
    public void testBuckyballEssentialRings() throws Exception {
        IAtomContainer buckyball = createBuckyBall();
        IRingSet ringSetEssential = new SSSRFinder(buckyball).findEssentialRings();
        ringCount(ringSetEssential, 6, 0);
        ringCount(ringSetEssential, 5, 12);
        Assert.assertFalse("Duplicate rings exist", checkForDuplicateRingsInSet(ringSetEssential));
    }

    /**
     * Creates a bucky ball molecule.
     * @return bucky ball molecule
     */
    private IAtomContainer createBuckyBall() throws Exception {
        IAtomContainer molecule = null;
        String filename = "data/mdl/buckyball.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IAtomContainer) reader.read(new AtomContainer());
        reader.close();
        Assert.assertTrue("Atom count is 60 ", molecule.getAtomCount() == 60);
        Assert.assertTrue("Bond count is 90 ", molecule.getBondCount() == 90);
        return molecule;
    }

    /**
     * Validates that the SSSR has found the expected number of rings
     * of a particular size.
     * @param ringSet constructed by SSSR.
     * @param ringSizeForCounting particular ring size to count
     * @param expectedNumOfRings the expected number of rings
     */
    private void ringCount(IRingSet ringSet, int ringSizeForCounting, int expectedNumOfRings) {
        int ringCount = 0;
        for (IAtomContainer ring : ringSet.atomContainers()) {
            if (ring.getAtomCount() == ringSizeForCounting) {
                ringCount++;
            }
        }
        Assert.assertTrue("Counting rings of size " + ringSizeForCounting, expectedNumOfRings == ringCount);
    }

    /**
     * Checks if the ringSet (created by SSSR) contains rings with
     * exactly the same atoms.
     */
    static boolean checkForDuplicateRingsInSet(IRingSet ringset) {
        // Make a list of rings
        List<IAtomContainer> ringList = new ArrayList<IAtomContainer>();
        for (IAtomContainer atCont : ringset.atomContainers()) {
            ringList.add(atCont);
        }
        //Outer loop over rings
        for (IAtomContainer ring : ringList) {
            // Inner loop over rings
            for (IAtomContainer otherRing : ringList) {
                if (otherRing.hashCode() != ring.hashCode() && otherRing.getAtomCount() == ring.getAtomCount()) {

                    // check if the two rings have all the same atoms in them -
                    // this should not happen (="duplicate" rings)
                    boolean sameAtoms = true;
                    DUP_LOOP: for (IAtom at : ring.atoms()) {
                        if (!otherRing.contains(at)) {
                            sameAtoms = false;
                            break DUP_LOOP;
                        }
                    }
                    if (sameAtoms) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
