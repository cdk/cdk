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
package org.openscience.cdk.test.tools.manipulator;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test
 */
public class AtomContainerManipulatorTest extends CDKTestCase {
    AtomContainer ac;

    public AtomContainerManipulatorTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        ac = (AtomContainer) MoleculeFactory.makeAlphaPinene();
    }

    public static Test suite() 
    {
        return new TestSuite(AtomContainerManipulatorTest.class);
    }

    public void testGetTotalHydrogenCount() throws IOException, ClassNotFoundException, CDKException {
        Molecule mol = MoleculeFactory.makeAlphaPinene();
        new HydrogenAdder().addImplicitHydrogensToSatisfyValency(mol);
        assertEquals(16, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    public void testGetTotalHydrogenCount_ImplicitHydrogens() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon = new Atom("C");
        carbon.setHydrogenCount(4);
        mol.addAtom(carbon);
        assertEquals(4, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    public void testGetTotalHydrogenCount_ExplicitHydrogens() throws Exception {
        SmilesParser parser = new SmilesParser();
        Molecule mol = parser.parseSmiles("[H]C([H])([H])[H]");
        assertEquals(0, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
    
    public void testRemoveHydrogens() throws IOException, ClassNotFoundException, CDKException{
        Molecule mol=MoleculeFactory.makeAlphaPinene();
        mol.setFlag(5,true);
        new HydrogenAdder().addHydrogensToSatisfyValency(mol);
        AtomContainer ac=AtomContainerManipulator.removeHydrogens((AtomContainer)mol);
        assertEquals(10, ac.getAtomCount());
        assertTrue(ac.getFlag(5));
    }

    /**
     * Test removeHydrogens for B2H6, which contains two multiply bonded H.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public void testRemoveHydrogensBorane() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser();
        Molecule mol = parser.parseSmiles("B1([H])([H])[H]B([H])([H])[H]1");
        AtomContainer ac = AtomContainerManipulator.removeHydrogens((AtomContainer)mol);

        // Should be two disconnected Bs with H-count == 4
        assertEquals("incorrect atom count", 2, ac.getAtomCount());
        assertEquals("incorrect bond count", 0, ac.getBondCount());
        assertEquals("incorrect hydrogen count", 4, ac.getAtomAt(0).getHydrogenCount());
        assertEquals("incorrect hydrogen count", 4, ac.getAtomAt(1).getHydrogenCount());
    }
}


