/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.test.graph;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.LonePair;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 *  Checks the functionality of the ConnectivityChecker
 *
 * @cdk.module test
 *
 * @author     steinbeck
 * @cdk.created    2001-07-24
 */
public class ConnectivityCheckerTest extends CDKTestCase {

	/**
	 *  Constructor for the ConnectivityCheckerTest object
	 *
	 * @param  name  A Name of the test
	 */
	public ConnectivityCheckerTest(String name) {
		super(name);
	}

	/**
	 *  The JUnit setup method
	 */
	public void setUp() {
	}

	/**
	 * This test tests the function of the partitionIntoMolecule() method.
	 */
	public void testPartitionIntoMolecules() {
		//System.out.println(atomCon);
        AtomContainer atomCon = new org.openscience.cdk.AtomContainer();
		atomCon.add(MoleculeFactory.make4x3CondensedRings());
		atomCon.add(MoleculeFactory.makeAlphaPinene());
		atomCon.add(MoleculeFactory.makeSpiroRings());
        SetOfMolecules moleculeSet = null;
		try {
			moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
		} catch (Exception exc) {
            fail(exc.toString());
		}
        assertNotNull(moleculeSet);
		assertEquals(3, moleculeSet.getMoleculeCount());
	}

    /**
     * Test for SF bug #903551
     */
	public void testPartitionIntoMoleculesKeepsAtomIDs() {
        AtomContainer atomCon = new org.openscience.cdk.AtomContainer();
        Atom atom1 = new Atom("C");
        atom1.setID("atom1");
        Atom atom2 = new Atom("C");
        atom2.setID("atom2");
        atomCon.addAtom(atom1);
        atomCon.addAtom(atom2);
        SetOfMolecules moleculeSet = null;
		try {
			moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
		} catch (Exception exc) {
            fail(exc.toString());
		}
        assertNotNull(moleculeSet);
		assertEquals(2, moleculeSet.getMoleculeCount());
		org.openscience.cdk.interfaces.IAtom copy1 = moleculeSet.getMolecule(0).getAtomAt(0);
		org.openscience.cdk.interfaces.IAtom copy2 = moleculeSet.getMolecule(1).getAtomAt(0);
        
        assertEquals(atom1.getID(), copy1.getID());
        assertEquals(atom2.getID(), copy2.getID());
    }

    /**
	 * This test tests the consitency between isConnected() and
     * partitionIntoMolecules().
	 */
	public void testPartitionIntoMolecules_IsConnected_Consistency() {
		//System.out.println(atomCon);
        AtomContainer atomCon = new org.openscience.cdk.AtomContainer();
		atomCon.add(MoleculeFactory.make4x3CondensedRings());
		atomCon.add(MoleculeFactory.makeAlphaPinene());
		atomCon.add(MoleculeFactory.makeSpiroRings());
        SetOfMolecules moleculeSet = null;
		try {
			moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
		} catch (Exception exc) {
            fail(exc.toString());
		}
        assertNotNull(moleculeSet);
		assertEquals(3, moleculeSet.getMoleculeCount());
        
		org.openscience.cdk.interfaces.Molecule[] molecules = moleculeSet.getMolecules();
        assertTrue(ConnectivityChecker.isConnected(molecules[0]));
        assertTrue(ConnectivityChecker.isConnected(molecules[1]));
        assertTrue(ConnectivityChecker.isConnected(molecules[2]));
	}

    /**
	 * This test makes sure that it is checked that the partitionIntoMolecules()
     * method keeps LonePairs and SingleElectrons with its associated atoms.
	 */
	public void testDontDeleteSingleElectrons() {
        AtomContainer atomCon = new org.openscience.cdk.AtomContainer();
        // make two molecules; one with an LonePair, the other with a SingleElectron
        Molecule mol1 = new Molecule();
        Atom atom1 = new Atom("C");
        mol1.addAtom(atom1);
        LonePair lp1 = new LonePair(atom1);
        mol1.addElectronContainer(lp1);
        // mol2
        Molecule mol2 = new Molecule();
        Atom atom2 = new Atom("C");
        mol2.addAtom(atom2);
        SingleElectron se2 = new SingleElectron(atom2);
        mol2.addElectronContainer(se2);
        
        atomCon.add(mol1);
        atomCon.add(mol2);
        
        // now partition
        SetOfMolecules moleculeSet = null;
		try {
			moleculeSet = ConnectivityChecker.partitionIntoMolecules(atomCon);
		} catch (Exception exc) {
            fail(exc.toString());
		}
        assertNotNull(moleculeSet);
		assertEquals(2, moleculeSet.getMoleculeCount());
        
		org.openscience.cdk.interfaces.Molecule[] molecules = moleculeSet.getMolecules();
        assertTrue(ConnectivityChecker.isConnected(molecules[0]));
        assertTrue(ConnectivityChecker.isConnected(molecules[1]));
        
        // make sure
        assertEquals(1, molecules[0].getAtomCount());
        assertEquals(1, molecules[0].getElectronContainerCount());
        assertEquals(1, molecules[1].getAtomCount());
        assertEquals(1, molecules[1].getElectronContainerCount());
        // we don't know which partition contains the LP and which the electron
        assertTrue(molecules[0].getSingleElectronSum(molecules[0].getAtomAt(0)) == 0 ||
                   molecules[1].getSingleElectronSum(molecules[1].getAtomAt(0)) == 0);
        assertTrue(molecules[0].getLonePairCount(molecules[0].getAtomAt(0)) == 0 ||
                   molecules[1].getLonePairCount(molecules[1].getAtomAt(0)) == 0);
	}
    
	/**
	 * This test tests the algorithm behind isConnected().
	 */
	public void testIsConnected() {
        Molecule spiro = MoleculeFactory.makeSpiroRings();
        assertTrue(ConnectivityChecker.isConnected(spiro));
	}

	/**
	 *  A unit test suite for JUnit
	 *
	 * @return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(ConnectivityCheckerTest.class);
	}
}

