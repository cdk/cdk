/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io.cml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module test
 */
public class CMLRoundTripTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public CMLRoundTripTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    }

    public static Test suite() {
        return new TestSuite(CMLRoundTripTest.class);
    }

    public void testAtom() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getSymbol(), roundTrippedAtom.getSymbol());
    }
    
    public void testAtomId() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        atom.setID("N1");
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getID(), roundTrippedAtom.getID());
    }
    
    public void testAtom2D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2d(p2d);
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getX2d(), roundTrippedAtom.getX2d(), 0.00001);
        assertEquals(atom.getY2d(), roundTrippedAtom.getY2d(), 0.00001);
    }
    
    public void testAtom3D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3d(p3d);
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getX3d(), roundTrippedAtom.getX3d(), 0.00001);
        assertEquals(atom.getY3d(), roundTrippedAtom.getY3d(), 0.00001);
        assertEquals(atom.getZ3d(), roundTrippedAtom.getZ3d(), 0.00001);
    }
    
    public void testAtomFract3D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(0.3, 0.4, 0.9);
        atom.setFractionalPoint3d(p3d);
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getFractX3d(), roundTrippedAtom.getFractX3d(), 0.00001);
        assertEquals(atom.getFractY3d(), roundTrippedAtom.getFractY3d(), 0.00001);
        assertEquals(atom.getFractZ3d(), roundTrippedAtom.getFractZ3d(), 0.00001);
    }
    
    public void testPseudoAtom() {
        Molecule mol = new Molecule();
        PseudoAtom atom = new PseudoAtom("N");
        atom.setLabel("Glu55");
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertNotNull(roundTrippedAtom);
        assertTrue(roundTrippedAtom instanceof PseudoAtom);
        assertEquals("Glu55", ((PseudoAtom)roundTrippedAtom).getLabel());
    }
    
    public void testAtomFormalCharge() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        int formalCharge = +1;
        atom.setFormalCharge(formalCharge);
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getFormalCharge(), roundTrippedAtom.getFormalCharge());
    }
    
    public void testAtomPartialCharge() {
        if (true) return;
        fail("Have to figure out how to store partial charges in CML2");
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        double partialCharge = 0.5;
        atom.setCharge(partialCharge);
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getCharge(), roundTrippedAtom.getCharge(), 0.0001);
    }
    
    public void testAtomStereoParity() {
        if (true) return;
        fail("Have to figure out how to store atom parity in CML2");
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        int stereo = CDKConstants.STEREO_ATOM_PARITY_PLUS;
        atom.setStereoParity(stereo);
        mol.addAtom(atom);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(atom.getStereoParity(), roundTrippedAtom.getStereoParity());
    }
    
    public void testBond() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, 1.0);
        mol.addBond(bond);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        Bond roundTrippedBond = roundTrippedMol.getBondAt(0);
        assertEquals(2, roundTrippedBond.getAtomCount());
        assertEquals("C", roundTrippedBond.getAtomAt(0).getSymbol()); // preserved direction?
        assertEquals("O", roundTrippedBond.getAtomAt(1).getSymbol());
        assertEquals(bond.getOrder(), roundTrippedBond.getOrder(), 0.0001);
    }
    
    public void testBondStereo() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, 1.0);
        int stereo = CDKConstants.STEREO_BOND_DOWN;
        bond.setStereo(stereo);
        mol.addBond(bond);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        Bond roundTrippedBond = roundTrippedMol.getBondAt(0);
        assertEquals(bond.getStereo(), roundTrippedBond.getStereo());
    }
    
    /**
     * Convert a Molecule to CML and back to a Molecule again.
     * Given that CML reading is working, the problem is with the
     * CMLWriter.
     *
     * @see org.openscience.cdk.CMLFragmentsTest
     */
    private Molecule roundTripMolecule(Molecule mol) {
        StringWriter stringWriter = new StringWriter();
        try {
            CMLWriter writer = new CMLWriter(stringWriter);
            writer.write(mol);
        } catch (Exception exception) {
            String message = "Failed when writing CML";
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        Molecule roundTrippedMol = null;
        try {
            String cmlString = stringWriter.toString();
            logger.debug("CML string: " + cmlString);
            CMLReader reader = new CMLReader(new StringReader(cmlString));
            
            ChemFile file = (ChemFile)reader.read(new ChemFile());
            assertNotNull(file);
            assertEquals(1, file.getChemSequenceCount());
            ChemSequence sequence = file.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(1, sequence.getChemModelCount());
            ChemModel chemModel = sequence.getChemModel(0);
            assertNotNull(chemModel);
            SetOfMolecules moleculeSet = chemModel.getSetOfMolecules();
            assertNotNull(moleculeSet);
            assertEquals(1, moleculeSet.getMoleculeCount());
            roundTrippedMol = moleculeSet.getMolecule(0);
            assertNotNull(roundTrippedMol);
        } catch (Exception exception) {
            String message = "Failed when reading CML";
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        return roundTrippedMol;
    }
    
    public void testPartialCharge() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        double charge = -0.267;
        atom.setCharge(charge);
        
        Molecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        Atom roundTrippedAtom = roundTrippedMol.getAtomAt(0);
        assertEquals(charge, roundTrippedAtom.getCharge(), 0.0001);
    }

}
