/* $Revision: 5889 $ $Author: egonw $ $Date: 2006-04-06 15:24:58 +0200 (Thu, 06 Apr 2006) $
 * 
 * Copyright (C) 2006-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.test.atomtype;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.atomtype.StructGenAtomTypeGuesser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-atomtype
 */
public class StructGenAtomTypeGuesserTest extends CDKTestCase {

    public StructGenAtomTypeGuesserTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(StructGenAtomTypeGuesserTest.class);
    }
    
    public void testStructGenAtomTypeGuesser() throws ClassNotFoundException, CDKException {
    	StructGenAtomTypeGuesser matcher = new StructGenAtomTypeGuesser();
        assertNotNull(matcher);
    }
    
    public void testPossbibleAtomTypes_IAtomContainer_IAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        atom.setHydrogenCount(3);
        Atom atom2 = new Atom("N");
        atom2.setHydrogenCount(2);
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(new Bond(atom, atom2, 1));

        StructGenAtomTypeGuesser atm = new StructGenAtomTypeGuesser();
        List matched = atm.possbibleAtomTypes(mol, atom);
        assertNotNull(matched);
        assertTrue(matched.size() > 0);
        assertTrue(matched.get(0) instanceof IAtomType);
        
        assertEquals("C", ((IAtomType)matched.get(0)).getSymbol());
    }
}
