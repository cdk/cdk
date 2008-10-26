/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.*;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.LoggingTool;

import java.io.InputStream;
import java.util.Iterator;

/**
 * @cdk.module test-standard
 */
public class SSSRFinderTest extends CDKTestCase {

    private final LoggingTool logger = new LoggingTool(SSSRFinderTest.class);

    public SSSRFinderTest() {
        super();
    }

    @Test
    public void testSSSRFinder_IAtomContainer()
    {
        IMolecule molecule = MoleculeFactory.makeAlphaPinene();
        SSSRFinder finder = new SSSRFinder(molecule);
        Assert.assertNotNull(finder);
    }

    @Test public void testFindSSSR()
    {
        IMolecule molecule = MoleculeFactory.makeAlphaPinene();
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
    }

    @Test public void testFindSSSR_IAtomContainer()
    {
        IMolecule molecule = MoleculeFactory.makeAlphaPinene();
        SSSRFinder sssrFinder = new SSSRFinder(molecule);
        IRingSet ringSet = sssrFinder.findSSSR();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
    }

    @Test public void testGetAtomContainerCount() throws Exception
    {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("c1ccccc1");
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(1, ringSet.getAtomContainerCount());
    }

    @Test public void testRingFlags1() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("c1ccccc1");
        new SSSRFinder(molecule).findSSSR();

        int count = 0;
        Iterator atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getFlag(CDKConstants.ISINRING)) count++;
        }
        Assert.assertEquals("All atoms in benzene were not marked as being in a ring", 6, count);
    }

    @Test public void testRingFlags2() throws Exception {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("c1cccc1CC");
        new SSSRFinder(molecule).findSSSR();

        int count = 0;
        Iterator atoms = molecule.atoms().iterator();
        while (atoms.hasNext()) {
            IAtom atom = (IAtom) atoms.next();
            if (atom.getFlag(CDKConstants.ISINRING)) count++;
        }
        Assert.assertEquals("All ring atoms in 2-ethyl cyclopentane were not marked as being in a ring", 5, count);
    }

    @Test public void testBicyclicCompound() throws Exception
    {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("C1CCC(CCCCC2)C2C1");
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(2, ringSet.getAtomContainerCount());
    }

    /**
     * @cdk.bug 826942
     */
    @Test public void testSFBug826942() throws Exception
    {
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = sp.parseSmiles("C1CCC2C(C1)C4CCC3(CCCCC23)(C4)");
        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        Assert.assertEquals(4, ringSet.getAtomContainerCount());
    }

    @Test public void testProblem1() throws Exception
    {
        IMolecule molecule = null;
        IRing ring = null;
        String filename = "data/mdl/figueras-test-sep3D.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(3, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
        {
            ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    @Test public void testLoopProblem() throws Exception
    {
        String filename = "data/mdl/ring_03419.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        IMolecule molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(12, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
        {
            IRing ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }


    @Test public void testProblem2() throws Exception
    {
        IMolecule molecule = null;
        IRing ring = null;
        String filename = "data/mdl/figueras-test-buried.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(10, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
        {
            ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    @Test public void testProblem3() throws Exception {
        IMolecule molecule = null;
        IRing ring = null;
        String filename = "data/mdl/figueras-test-inring.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
        logger.debug("Testing " + filename);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        logger.debug("Found ring set of size: " + ringSet.getAtomContainerCount());
        Assert.assertEquals(5, ringSet.getAtomContainerCount());
        for (int f = 0; f < ringSet.getAtomContainerCount(); f++)
        {
            ring = (IRing) ringSet.getAtomContainer(f);
            logger.debug("ring: " + toString(ring, molecule));
        }
    }

    /**
     * @cdk.bug 891021
     */
    @Test public void testBug891021() throws Exception {
        IMolecule molecule = null;
        String filename = "data/mdl/too.many.rings.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molecule = (IMolecule) reader.read((IChemObject) new org.openscience.cdk.Molecule());
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
    private String toString(IRing ring, IMolecule molecule) throws Exception
    {
        String str = "";
        for (int f = 0; f < ring.getAtomCount(); f++)
        {
            str += molecule.getAtomNumber(ring.getAtom(f)) +  " - ";
        }
        return str;
    }
}


