/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.ConnectivityChecker;

/**
 *  Checks the functionality of the ConnectivityChecker
 *
 * @cdk.module test
 *
 * @author     steinbeck
 * @cdk.created    2001-07-24
 */
public class ConnectivityCheckerTest extends TestCase {

	ConnectivityChecker cc = null;

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
		cc = new ConnectivityChecker();
	}

	/**
	 * This test tests the function of the partitionIntoMolecule() method.
	 */
	public void testPartitionIntoMolecules() {
		//System.out.println(atomCon);
        AtomContainer atomCon = new AtomContainer();
		atomCon.add(MoleculeFactory.make4x3CondensedRings());
		atomCon.add(MoleculeFactory.makeAlphaPinene());
		atomCon.add(MoleculeFactory.makeSpiroRings());
        SetOfMolecules moleculeSet = null;
		try {
			moleculeSet = cc.partitionIntoMolecules(atomCon);
		} catch (Exception exc) {
            fail(exc.toString());
		}
        assertNotNull(moleculeSet);
		assertEquals(3, moleculeSet.getMoleculeCount());
	}

	/**
	 * This test tests the consitency between isConnected() and
     * partitionIntoMolecules().
	 */
	public void testPartitionIntoMolecules_IsConnected_Consistency() {
		//System.out.println(atomCon);
        AtomContainer atomCon = new AtomContainer();
		atomCon.add(MoleculeFactory.make4x3CondensedRings());
		atomCon.add(MoleculeFactory.makeAlphaPinene());
		atomCon.add(MoleculeFactory.makeSpiroRings());
        SetOfMolecules moleculeSet = null;
		try {
			moleculeSet = cc.partitionIntoMolecules(atomCon);
		} catch (Exception exc) {
            fail(exc.toString());
		}
        assertNotNull(moleculeSet);
		assertEquals(3, moleculeSet.getMoleculeCount());
        
        Molecule[] molecules = moleculeSet.getMolecules();
        assertTrue(cc.isConnected(molecules[0]));
        assertTrue(cc.isConnected(molecules[1]));
        assertTrue(cc.isConnected(molecules[2]));
	}

	/**
	 * This test tests the algorithm behind isConnected().
	 */
	public void testIsConnected() {
        Molecule spiro = MoleculeFactory.makeSpiroRings();
        assertTrue(cc.isConnected(spiro));
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

