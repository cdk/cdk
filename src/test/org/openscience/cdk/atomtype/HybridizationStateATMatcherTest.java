/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.atomtype;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.HybridizationStateATMatcher;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.NewCDKTestCase;

/**
 * Checks the functionality of the AtomType-HybridizationStateATMatcher.
 *
 * @cdk.module test-atomtype
 *
 * @see org.openscience.cdk.atomtype.HybridizationStateATMatcher
 */
public class HybridizationStateATMatcherTest extends NewCDKTestCase {

    @Test
    public void testHybridizationStateATMatcher() throws ClassNotFoundException, CDKException, java.lang.Exception {
	    HybridizationStateATMatcher matcher = new HybridizationStateATMatcher();
	    Assert.assertNotNull(matcher);
	    
    }

    @Test
    public void testFindMatchingAtomType_IAtomContainer_IAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Molecule mol = new Molecule();
        // smiles source: C#CCC=O
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addBond(0, 1, IBond.Order.TRIPLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("C"));
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addAtom(new Atom("O"));
        mol.addBond(3, 4, IBond.Order.DOUBLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(5, 0, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(6, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(7, 2, IBond.Order.SINGLE);
        mol.addAtom(new Atom("H"));
        mol.addBond(8, 3, IBond.Order.SINGLE);
        
        IAtom atom = mol.getAtom(0);
        
        HybridizationStateATMatcher atm = new HybridizationStateATMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        
        IAtomType.Hybridization hybridization = matched.getHybridization();
        IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP1;
        
        Assert.assertEquals(thisHybridization, hybridization);
    }
}
