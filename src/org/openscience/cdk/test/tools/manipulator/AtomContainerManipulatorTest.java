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
 */
package org.openscience.cdk.test.tools.manipulator;

import java.io.IOException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
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

    public void testGetTotalHydrogenCount_IAtomContainer() throws IOException, ClassNotFoundException, CDKException {
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
        
    public void testConvertImplicitToExplicitHydrogens_IAtomContainer() throws Exception {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setHydrogenCount(2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setHydrogenCount(2);
        mol.addBond(0,1, CDKConstants.BONDORDER_DOUBLE);
        assertEquals(2, mol.getAtomCount());
        assertEquals(1, mol.getBondCount());
        
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
    }
        
    public void testGetTotalHydrogenCount_IAtomContainer_zeroImplicit() throws IOException, ClassNotFoundException, CDKException {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setHydrogenCount(0);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setHydrogenCount(0);
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

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
    }

    public void testGetTotalHydrogenCount_IAtomContainer_nullImplicit() throws IOException, ClassNotFoundException, CDKException {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setHydrogenCount(null);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setHydrogenCount(null);
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

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        assertEquals(6, mol.getAtomCount());
        assertEquals(5, mol.getBondCount());
    }

    public void testGetTotalHydrogenCount_ImplicitHydrogens() throws Exception {
        Molecule mol = new Molecule();
        Atom carbon = new Atom("C");
        carbon.setHydrogenCount(4);
        mol.addAtom(carbon);
        assertEquals(4, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    public void testRemoveHydrogens_IAtomContainer() throws IOException, ClassNotFoundException, CDKException{
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

    public void testGetAllIDs_IAtomContainer() {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C")); mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C")); mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H")); mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H")); mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H")); mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H")); mol.getAtom(5).setID("a6");
        
        List ids = AtomContainerManipulator.getAllIDs(mol);
        assertEquals(6, ids.size());
        assertTrue(ids.contains("a1"));
        assertTrue(ids.contains("a2"));
        assertTrue(ids.contains("a3"));
        assertTrue(ids.contains("a4"));
        assertTrue(ids.contains("a5"));
        assertTrue(ids.contains("a6"));
    }

    public void testGetAtomArray_IAtomContainer() {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        assertEquals(6, atoms.length);
        assertEquals(mol.getAtom(0), atoms[0]);
        assertEquals(mol.getAtom(1), atoms[1]);
        assertEquals(mol.getAtom(2), atoms[2]);
        assertEquals(mol.getAtom(3), atoms[3]);
        assertEquals(mol.getAtom(4), atoms[4]);
        assertEquals(mol.getAtom(5), atoms[5]);
    }

    public void testGetAtomArray_List() {
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
        
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(
        	mol.getConnectedAtomsList(mol.getAtom(0))
        );
        assertEquals(3, atoms.length);
        assertEquals(mol.getAtom(1), atoms[0]);
        assertEquals(mol.getAtom(2), atoms[1]);
        assertEquals(mol.getAtom(3), atoms[2]);
    }

    public void testGetBondArray_List() {
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
        
        IBond[] bonds = AtomContainerManipulator.getBondArray(
        	mol.getConnectedBondsList(mol.getAtom(0))
        );
        assertEquals(3, bonds.length);
        assertEquals(mol.getBond(0), bonds[0]);
        assertEquals(mol.getBond(1), bonds[1]);
        assertEquals(mol.getBond(2), bonds[2]);
    }

    public void testGetBondArray_IAtomContainer() {
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
        
        IBond[] bonds = AtomContainerManipulator.getBondArray(mol);
        assertEquals(5, bonds.length);
        assertEquals(mol.getBond(0), bonds[0]);
        assertEquals(mol.getBond(1), bonds[1]);
        assertEquals(mol.getBond(2), bonds[2]);
        assertEquals(mol.getBond(3), bonds[3]);
        assertEquals(mol.getBond(4), bonds[4]);
    }

    public void testGetAtomById_IAtomContainer_String() throws CDKException {
        Molecule mol = new Molecule(); // ethene
        mol.addAtom(new Atom("C")); mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C")); mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H")); mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H")); mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H")); mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H")); mol.getAtom(5).setID("a6");
        mol.addBond(0,1,2);
        mol.addBond(0,2,1);
        mol.addBond(0,3,1);
        mol.addBond(1,4,2);
        mol.addBond(1,5,2);
        mol.setFlag(5,true);
        
        assertEquals(mol.getAtom(0), AtomContainerManipulator.getAtomById(mol, "a1"));
        assertEquals(mol.getAtom(1), AtomContainerManipulator.getAtomById(mol, "a2"));
        assertEquals(mol.getAtom(2), AtomContainerManipulator.getAtomById(mol, "a3"));
        assertEquals(mol.getAtom(3), AtomContainerManipulator.getAtomById(mol, "a4"));
        assertEquals(mol.getAtom(4), AtomContainerManipulator.getAtomById(mol, "a5"));
        assertEquals(mol.getAtom(5), AtomContainerManipulator.getAtomById(mol, "a6"));
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
    	IAtomContainer borane = new Molecule();
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("B"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("B"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addAtom(borane.getBuilder().newAtom("H"));
    	borane.addBond(0,2,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(2,3,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(2,4,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(3,5,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(4,5,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(5,6,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(5,7,CDKConstants.BONDORDER_SINGLE);
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(borane);

        // Should be two disconnected Bs with H-count == 4
        assertEquals("incorrect atom count", 2, ac.getAtomCount());
        assertEquals("incorrect bond count", 0, ac.getBondCount());
        assertEquals("incorrect hydrogen count", 4, ac.getAtom(0).getHydrogenCount().intValue());
        assertEquals("incorrect hydrogen count", 4, ac.getAtom(1).getHydrogenCount().intValue());
    }
    /**
     * Test total formal charge.
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws CDKException
     */
    public void testGetTotalFormalCharge_IAtomContainer() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
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
    public void testGetTotalPositiveFormalCharge_IAtomContainer() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
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
    public void testGetTotalNegativeFormalCharge_IAtomContainer() throws IOException, ClassNotFoundException, CDKException
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalNegativeFormalCharge((IAtomContainer)mol);

        assertEquals(-1,totalCharge);
    }

    public void testGetIntersection_IAtomContainer_IAtomContainer() {
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


