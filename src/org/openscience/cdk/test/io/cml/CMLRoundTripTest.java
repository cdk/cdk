/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.test.io.cml;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;
import nu.xom.Element;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.CMLWriter;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module  test-io
 * @cdk.require xom-1.0.jar
 * @cdk.require java1.5+
 */
public class CMLRoundTripTest extends CDKTestCase {

    private LoggingTool logger;
    private Convertor convertor;

    public CMLRoundTripTest(String name) {
        super(name);
        logger = new LoggingTool(this);
        convertor = new Convertor(false, "");
    }

    public static Test suite() {
        return new TestSuite(CMLRoundTripTest.class);
    }

    public void testAtom() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        mol.addAtom(atom);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getSymbol(), roundTrippedAtom.getSymbol());
    }
    
    public void testAtomId() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        atom.setID("N1");
        mol.addAtom(atom);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getID(), roundTrippedAtom.getID());
    }
    
    public void testAtom2D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2d(p2d);
        mol.addAtom(atom);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint2d(), roundTrippedAtom.getPoint2d(), 0.00001);
    }
    
    public void testAtom3D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3d(p3d);
        mol.addAtom(atom);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint3d(), roundTrippedAtom.getPoint3d(), 0.00001);
    }
    
    public void testAtomFract3D() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(0.3, 0.4, 0.9);
        atom.setFractionalPoint3d(p3d);
        mol.addAtom(atom);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getFractionalPoint3d(), roundTrippedAtom.getFractionalPoint3d(), 0.00001);
    }
    
    public void testPseudoAtom() {
        Molecule mol = new Molecule();
        PseudoAtom atom = new PseudoAtom("N");
        atom.setLabel("Glu55");
        mol.addAtom(atom);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
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
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
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
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
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
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getStereoParity(), roundTrippedAtom.getStereoParity());
    }
    
    public void testIsotope() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber(13);
        mol.addAtom(atom);
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getMassNumber(), roundTrippedAtom.getMassNumber());
    }
    
    public void testBond() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, 1.0);
        mol.addBond(bond);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        org.openscience.cdk.interfaces.IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(2, roundTrippedBond.getAtomCount());
        assertEquals("C", roundTrippedBond.getAtom(0).getSymbol()); // preserved direction?
        assertEquals("O", roundTrippedBond.getAtom(1).getSymbol());
        assertEquals(bond.getOrder(), roundTrippedBond.getOrder(), 0.0001);
    }
    
    public void testBondID() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, 1.0);
        bond.setID("b1");
        mol.addBond(bond);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        org.openscience.cdk.interfaces.IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(bond.getID(), roundTrippedBond.getID());
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
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        org.openscience.cdk.interfaces.IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(bond.getStereo(), roundTrippedBond.getStereo());
    }
    
    /**
     * Convert a Molecule to CML and back to a Molecule again.
     * Given that CML reading is working, the problem is with the
     * CMLWriter.
     *
     * @see org.openscience.cdk.CMLFragmentsTest
     */
    private org.openscience.cdk.interfaces.IMolecule roundTripMolecule(Molecule mol) {
        String cmlString = "<!-- failed -->";
        try {
            Element cmlDOM = convertor.cdkMoleculeToCMLMolecule(mol);
            cmlString = cmlDOM.toXML();
        } catch (Exception exception) {
            String message = "Failed when writing CML: " + exception.getMessage();
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = null;
        try {
            logger.debug("CML string: " + cmlString);
            CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
            
            IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
            assertNotNull(file);
            assertEquals(1, file.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = file.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(1, sequence.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel chemModel = sequence.getChemModel(0);
            assertNotNull(chemModel);
            org.openscience.cdk.interfaces.IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
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
    
    private org.openscience.cdk.interfaces.IReaction roundTripReaction(Reaction reaction) {
        String cmlString = "<!-- failed -->";
        try {
            Element cmlDOM = convertor.cdkReactionToCMLReaction(reaction);
            cmlString = cmlDOM.toXML();
        } catch (Exception exception) {
            String message = "Failed when writing CML: " + exception.getMessage();
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        org.openscience.cdk.interfaces.IReaction roundTrippedReaction = null;
        try {
            logger.debug("CML string: ", cmlString);
            CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));
            
            IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
            assertNotNull(file);
            assertEquals(1, file.getChemSequenceCount());
            org.openscience.cdk.interfaces.IChemSequence sequence = file.getChemSequence(0);
            assertNotNull(sequence);
            assertEquals(1, sequence.getChemModelCount());
            org.openscience.cdk.interfaces.IChemModel chemModel = sequence.getChemModel(0);
            assertNotNull(chemModel);
            org.openscience.cdk.interfaces.IReactionSet reactionSet = chemModel.getReactionSet();
            assertNotNull(reactionSet);
            assertEquals(1, reactionSet.getReactionCount());
            roundTrippedReaction = reactionSet.getReaction(0);
            assertNotNull(roundTrippedReaction);
        } catch (Exception exception) {
            String message = "Failed when reading CML";
            logger.error(message);
            logger.debug(exception);
            fail(message);
        }
        
        return roundTrippedReaction;
    }

    public void testPartialCharge() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        double charge = -0.267;
        atom.setCharge(charge);
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(charge, roundTrippedAtom.getCharge(), 0.0001);
    }

    public void testInChI() {
        Molecule mol = new Molecule();
        String inchi = "InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)";
        mol.setProperty(CDKConstants.INCHI, inchi);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        assertNotNull(roundTrippedMol);
        
        assertEquals(inchi, roundTrippedMol.getProperty(CDKConstants.INCHI));
    }

    public void testSpinMultiplicity() {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        mol.addElectronContainer(new SingleElectron(atom));
        
        org.openscience.cdk.interfaces.IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getElectronContainerCount());
        org.openscience.cdk.interfaces.IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(1, roundTrippedMol.getSingleElectronSum(roundTrippedAtom));
    }

    public void testReaction() {
    	logger.debug("********** TEST REACTION **********");
        Reaction reaction = new Reaction();
        Molecule reactant = new Molecule();
        Atom atom = new Atom("C");
        reactant.addAtom(atom);
        reaction.addReactant(reactant);
        
        org.openscience.cdk.interfaces.IReaction roundTrippedReaction = roundTripReaction(reaction);
        
        assertNotNull(roundTrippedReaction);
        org.openscience.cdk.interfaces.IMoleculeSet reactants = roundTrippedReaction.getReactants();
        assertNotNull(reactants);
        assertEquals(1, reactants.getMoleculeCount());
        org.openscience.cdk.interfaces.IMolecule roundTrippedReactant = reactants.getMolecule(0);
        assertEquals(1, roundTrippedReactant.getAtomCount());
    }

    public void testDescriptorValue() {
    	Molecule molecule = MoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        try {
            DescriptorValue value = descriptor.calculate(molecule);
            molecule.setProperty(value.getSpecification(), value);
        } catch (Exception exception) {
            logger.error("Error while creating an CML2 file: ", exception.getMessage());
            logger.debug(exception);
            fail(exception.getMessage());
        }
        IMolecule roundTrippedMol = roundTripMolecule(molecule);

        assertNotNull(roundTrippedMol.getProperty(descriptor.getSpecification()));
    }
}

