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
 */
package org.openscience.cdk.test.atomtype;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.atomtype.*;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.*;

/**
 * @cdk.module test
 */
public class HybridizationMatcherTest extends CDKTestCase {

    public HybridizationMatcherTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(HybridizationMatcherTest.class);
    }
    
    public void testHybridizationMatcher() throws ClassNotFoundException, CDKException, java.lang.Exception {
        HybridizationMatcher matcher = new HybridizationMatcher();
        assertNotNull(matcher);
    }
    
    public void testFindMatchingAtomType_AtomContainer_Atom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        SmilesParser sp = new SmilesParser();
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        final int thisHybridization = CDKConstants.HYBRIDIZATION_SP1;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        HybridizationMatcher atm = new HybridizationMatcher();
        AtomType matched = atm.findMatchingAtomType(mol, atom);
        assertNotNull(matched);
        
        assertEquals(thisHybridization, matched.getHybridization());
        assertEquals("C", matched.getSymbol());
    }
}
