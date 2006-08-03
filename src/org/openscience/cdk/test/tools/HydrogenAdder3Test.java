/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.tools;

import java.io.InputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.ValencyHybridChecker;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

/**
 * Tests CDK's hydrogen adding capabilities in terms of
 * example molecules.
 *
 * @cdk.module test-valencycheck
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2004-06-13
 */
public class HydrogenAdder3Test extends HydrogenAdderTest {

	private LoggingTool logger;
	
    public HydrogenAdder3Test(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    /**
     * The JUnit setup method
     */
    public void setUp() {
        try {
            ValencyHybridChecker checker = new ValencyHybridChecker();
            adder = new HydrogenAdder(checker);
        } catch (Exception exception) {
            fail("Could not setup HydrogenAdder3Test: " + exception.getMessage());
        }
    }

    /**
     * A unit test suite for JUnit
     *
     * @return    The test suite
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(HydrogenAdder3Test.class);
        return suite;
    }

    public void testNaCl() {
        Molecule mol = new Molecule();
        Atom cl = new Atom("Cl");
        cl.setFormalCharge(-1);
        mol.addAtom(cl);
        Atom na = new Atom("Na");
        na.setFormalCharge(+1);
        mol.addAtom(na);
        
        try {
            adder.addExplicitHydrogensToSatisfyValency(mol);
        } catch (Exception exception) {
            System.err.println(exception);
            exception.printStackTrace();
            fail();
        }
        
        assertEquals(2, mol.getAtomCount());
        assertEquals(0, new MFAnalyser(mol).getAtomCount("H"));
        assertEquals(0, mol.getBondCount(cl));
        assertEquals(0, mol.getBondCount(na));
    }
    
    /**
     * @cdk.bug 1244612
     */
    public void testSulfurCompound() {
        String filename = "data/mdl/sulfurCompound.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new ChemFile());
            IAtomContainer[] containers = ChemFileManipulator.getAllAtomContainers(chemFile);
            assertEquals(1, containers.length);
            
            assertEquals(10, containers[0].getAtomCount());
            IAtom sulfur = containers[0].getAtom(1);
            assertEquals("S", sulfur.getSymbol());
            assertEquals(0, sulfur.getHydrogenCount());
            assertEquals(3, containers[0].getConnectedAtoms(sulfur).length);
            
            // add explicit hydrogens
            adder.addExplicitHydrogensToSatisfyValency(containers[0]);
            assertEquals(21, containers[0].getAtomCount());
            
            assertEquals(0, sulfur.getHydrogenCount());
            assertEquals(3, containers[0].getConnectedAtoms(sulfur).length);
        } catch (Exception exception) {
        	exception.printStackTrace();
        	fail(exception.getMessage());
        }
    }

    /**
     * @cdk.bug 1244612
     */
    public void testSulfurCompound_ImplicitHydrogens() {
        String filename = "data/mdl/sulfurCompound.mol";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            MDLReader reader = new MDLReader(ins);
            IChemFile chemFile = (IChemFile)reader.read(new ChemFile());
            IAtomContainer[] containers = ChemFileManipulator.getAllAtomContainers(chemFile);
            assertEquals(1, containers.length);
            
            assertEquals(10, containers[0].getAtomCount());
            IAtom sulfur = containers[0].getAtom(1);
            assertEquals("S", sulfur.getSymbol());
            assertEquals(0, sulfur.getHydrogenCount());
            assertEquals(3, containers[0].getConnectedAtoms(sulfur).length);
            
            // add explicit hydrogens
            adder.addImplicitHydrogensToSatisfyValency(containers[0]);
            assertEquals(10, containers[0].getAtomCount());
            
            assertEquals(0, sulfur.getHydrogenCount());
            assertEquals(3, containers[0].getConnectedAtoms(sulfur).length);
        } catch (Exception exception) {
        	exception.printStackTrace();
        	fail(exception.getMessage());
        }
    }
}

