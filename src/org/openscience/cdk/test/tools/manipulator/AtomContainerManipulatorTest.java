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
 */
package org.openscience.cdk.test.tools.manipulator;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * @cdk.module test-standard
 */
public class AtomContainerManipulatorTest extends CDKTestCase {
    IAtomContainer ac;

    public AtomContainerManipulatorTest(String name)
    {
        super(name);
    }

    public void setUp()
    {
        ac = (IAtomContainer) MoleculeFactory.makeAlphaPinene();
    }

    public static Test suite() 
    {
        return new TestSuite(AtomContainerManipulatorTest.class);
    }

    public void testGetTotalHydrogenCount() throws IOException, ClassNotFoundException, CDKException {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0,1,2);
        mol.addBond(0,2,1);
        mol.addBond(0,3,1);
        mol.addBond(1,4,1);
        mol.addBond(1,5,1);
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
        assertEquals(0, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    public void testGetTotalHydrogenCount_ImplicitHydrogens() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon = new Atom("C");
        carbon.setHydrogenCount(4);
        mol.addAtom(carbon);
        assertEquals(4, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    public void testRemoveHydrogens() throws IOException, ClassNotFoundException, CDKException{
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0,1,2);
        mol.addBond(0,2,1);
        mol.addBond(0,3,1);
        mol.addBond(1,4,2);
        mol.addBond(1,5,2);
        mol.setFlag(5,true);
        
        assertEquals(6, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        assertEquals(2, ac.getAtomCount());
        assertTrue(ac.getFlag(5));
    }

   public void testAddRemoveHydrogens() {

		SmilesParser parser = new SmilesParser();
		HydrogenAdder hydrogenAdder = null;

		try {

			Molecule mol = parser.parseSmiles("c1ccccc1");

			hydrogenAdder = new HydrogenAdder(
					"org.openscience.cdk.tools.ValencyChecker");

			hydrogenAdder.addExplicitHydrogensToSatisfyValency(mol);

			mol = (Molecule) AtomContainerManipulator.removeHydrogens(mol);

			assertEquals(0, mol.getAtom(0).getHydrogenCount());
			assertEquals(0, AtomContainerManipulator.getTotalHydrogenCount(mol));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

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
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens((IAtomContainer)mol);

        // Should be two disconnected Bs with H-count == 4
        assertEquals("incorrect atom count", 2, ac.getAtomCount());
        assertEquals("incorrect bond count", 0, ac.getBondCount());
        assertEquals("incorrect hydrogen count", 4, ac.getAtom(0).getHydrogenCount());
        assertEquals("incorrect hydrogen count", 4, ac.getAtom(1).getHydrogenCount());
    }
    /**
     * Test total formal charge.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public void testgetTotalFormalCharge() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser();
        Molecule mol = parser.parseSmiles("[C-]C[C+][C+]C");
        int totalCharge = AtomContainerManipulator.getTotalFormalCharge((IAtomContainer)mol);

        assertEquals(1,totalCharge);
    }
    /**
     * Test total positive formal charge.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public void testgetTotalPositiveFormalCharge() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser();
        Molecule mol = parser.parseSmiles("[C-]C[C+][C+]C");
        int totalCharge = AtomContainerManipulator.getTotalPositiveFormalCharge((IAtomContainer)mol);

        assertEquals(2,totalCharge);
    }
    /**
     * Test total negative formal charge.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public void testgetTotalNegativeFormalCharge() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser();
        Molecule mol = parser.parseSmiles("[C-]C[C+][C+]C");
        int totalCharge = AtomContainerManipulator.getTotalNegativeFormalCharge((IAtomContainer)mol);

        assertEquals(-1,totalCharge);
    }

    public void testGetIntersection_IAtomContainer() {
    	IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtom c1 = builder.newAtom("C");
        IAtom o = builder.newAtom("O");
        IAtom c2 = builder.newAtom("C");
        IAtom c3 = builder.newAtom("C");
        
        IBond b1 = builder.newBond(c1, o);
        IBond b2 = builder.newBond(o, c2);
        IBond b3 = builder.newBond(c2, c3);
        
        IAtomContainer container1 = new org.openscience.cdk.AtomContainer();
        container1.addAtom(c1);
        container1.addAtom(o);
        container1.addAtom(c2);
        container1.addBond(b1);
        container1.addBond(b2);
        IAtomContainer container2 = new org.openscience.cdk.AtomContainer();
        container2.addAtom(o);
        container2.addAtom(c3);
        container2.addAtom(c2);
        container2.addBond(b3);
        container2.addBond(b2);

        IAtomContainer intersection = AtomContainerManipulator.getIntersection(container1, container2);
        assertEquals(2, intersection.getAtomCount());
        assertEquals(1, intersection.getBondCount());
        assertTrue(intersection.contains(b2));
        assertTrue(intersection.contains(o));
        assertTrue(intersection.contains(c2));
    }
    
}


