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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */

package org.openscience.cdk.test.atomtype;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.AtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * Checks the functionality of the AtomType-HybridizationStateATMatcher.
 *
 * @cdk.module test
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
    
    public void testFindMatchingAtomType_AtomContainer_Atom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        SmilesParser sp = new SmilesParser();
	Molecule mol = sp.parseSmiles("C#CCC=O");
	HydrogenAdder hAdder = new HydrogenAdder();
	hAdder.addExplicitHydrogensToSatisfyValency(mol);
	org.openscience.cdk.interfaces.Atom atom = mol.getAtomAt(0);
	
	HybridizationStateATMatcher atm = new HybridizationStateATMatcher();
	AtomType matched = atm.findMatchingAtomType(mol, atom);
	
	AtomTypeManipulator.configure(atom, matched);
	
	int hybridization = atom.getHybridization();
	int thisHybridization = CDKConstants.HYBRIDIZATION_SP1;
	
	assertEquals(thisHybridization, hybridization);
        //assertEquals("C", at.getSymbol());
    }
}
