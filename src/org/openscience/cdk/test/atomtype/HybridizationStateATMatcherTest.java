/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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

package org.openscience.cdk.test.atomtype;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-HybridizationStateATMatcher.
 *
 * @cdk.module test-core
 *
 * @see org.openscience.cdk.atomtype.HybridizationStateATMatcher
 */
public class HybridizationStateATMatcherTest extends CDKTestCase {

    public HybridizationStateATMatcherTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(HybridizationStateATMatcherTest.class);
    }
    
    public void testHybridizationStateATMatcher() throws ClassNotFoundException, CDKException, java.lang.Exception {
	    HybridizationStateATMatcher matcher = new HybridizationStateATMatcher();
	    assertNotNull(matcher);
	    
    }
    
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Molecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(1,2,3);
        mol.addAtom(new Atom("C"));
        mol.addBond(2,3,1);
        mol.addAtom(new Atom("C"));
        mol.addBond(3,4,1);
        mol.addAtom(new Atom("O"));
        mol.addBond(4,5,2);
        
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        org.openscience.cdk.interfaces.IAtom atom = mol.getAtomAt(0);
        
        HybridizationStateATMatcher atm = new HybridizationStateATMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        
        AtomTypeManipulator.configure(atom, matched);
        
        int hybridization = atom.getHybridization();
        int thisHybridization = CDKConstants.HYBRIDIZATION_SP1;
        
        assertEquals(thisHybridization, hybridization);
        //assertEquals("C", at.getSymbol());
    }
}
