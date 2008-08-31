/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io.cml;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;
import nu.xom.Element;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SingleElectron;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemSequence;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.libio.cml.Convertor;
import org.openscience.cdk.libio.cml.QSARCustomizer;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.WeightDescriptor;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.BondManipulator;

/**
 * TestCase for the reading CML 2 files using a few test files
 * in data/cmltest.
 *
 * @cdk.module  test-libiocml
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
        convertor.registerCustomizer(new QSARCustomizer());
    }

    /* Called from MlibiocmlTests */ 
    public static Test suite() {
        return new TestSuite(CMLRoundTripTest.class);
    }

    public void testAtom() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getSymbol(), roundTrippedAtom.getSymbol());
    }
    
    public void testAtomId() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        atom.setID("N1");
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getID(), roundTrippedAtom.getID());
    }
    
    public void testAtom2D() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2d(p2d);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint2d(), roundTrippedAtom.getPoint2d(), 0.00001);
    }
    
    public void testAtom3D() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3d(p3d);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint3d(), roundTrippedAtom.getPoint3d(), 0.00001);
    }
    
    public void testAtom2DAnd3D() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point2d p2d = new Point2d(1.3, 1.4);
        atom.setPoint2d(p2d);
        Point3d p3d = new Point3d(1.3, 1.4, 0.9);
        atom.setPoint3d(p3d);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getPoint2d(), roundTrippedAtom.getPoint2d(), 0.00001);
        assertEquals(atom.getPoint3d(), roundTrippedAtom.getPoint3d(), 0.00001);
    }
    
    public void testAtomFract3D() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        Point3d p3d = new Point3d(0.3, 0.4, 0.9);
        atom.setFractionalPoint3d(p3d);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getFractionalPoint3d(), roundTrippedAtom.getFractionalPoint3d(), 0.00001);
    }
    
    public void testPseudoAtom() throws Exception {
        Molecule mol = new Molecule();
        PseudoAtom atom = new PseudoAtom("N");
        atom.setLabel("Glu55");
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertNotNull(roundTrippedAtom);
        assertTrue(roundTrippedAtom instanceof PseudoAtom);
        assertEquals("Glu55", ((PseudoAtom)roundTrippedAtom).getLabel());
    }
    
    /**
     * @cdk.bug 1455346
     */
    public void testChemModel() throws Exception {
    	ChemModel model = new ChemModel();
    	MoleculeSet moleculeSet = new MoleculeSet();
        Molecule mol = new Molecule();
        PseudoAtom atom = new PseudoAtom("N");
        mol.addAtom(atom);
        moleculeSet.addAtomContainer(mol);
        model.setMoleculeSet(moleculeSet);
        
        IChemModel roundTrippedModel = roundTripChemModel(model);
        
        IMoleculeSet roundTrippedMolSet = roundTrippedModel.getMoleculeSet(); 
        assertNotNull(roundTrippedMolSet);
        assertEquals(1, roundTrippedMolSet.getAtomContainerCount());
        IMolecule roundTrippedMolecule = roundTrippedMolSet.getMolecule(0);
        assertNotNull(roundTrippedMolecule);
        assertEquals(1, roundTrippedMolecule.getAtomCount());
    }
    
    public void testAtomFormalCharge() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        int formalCharge = +1;
        atom.setFormalCharge(formalCharge);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getFormalCharge(), roundTrippedAtom.getFormalCharge());
    }
    
    public void testAtomPartialCharge() throws Exception {
        if (true) return;
        fail("Have to figure out how to store partial charges in CML2");
        Molecule mol = new Molecule();
        Atom atom = new Atom("N");
        double partialCharge = 0.5;
        atom.setCharge(partialCharge);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getCharge(), roundTrippedAtom.getCharge(), 0.0001);
    }
    
    public void testAtomStereoParity() throws Exception {
        if (true) return;
        fail("Have to figure out how to store atom parity in CML2");
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        int stereo = CDKConstants.STEREO_ATOM_PARITY_PLUS;
        atom.setStereoParity(stereo);
        mol.addAtom(atom);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getStereoParity(), roundTrippedAtom.getStereoParity());
    }
    
    public void testIsotope() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber(13);
        mol.addAtom(atom);
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getMassNumber(), roundTrippedAtom.getMassNumber());
    }
    
    /**
     * Test roundtripping of MassNumber.
     * @throws Exception
     */
    public void testMassNumber() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber( new Integer(12) );
        mol.addAtom(atom);
        assertEquals( 12, atom.getMassNumber().intValue() );
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(atom.getMassNumber(), roundTrippedAtom.getMassNumber());
    }

    
    public void testBond() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        mol.addBond(bond);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(2, roundTrippedBond.getAtomCount());
        assertEquals("C", roundTrippedBond.getAtom(0).getSymbol()); // preserved direction?
        assertEquals("O", roundTrippedBond.getAtom(1).getSymbol());
        assertEquals(bond.getOrder(), roundTrippedBond.getOrder());
    }
    
    public void testBondID() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        bond.setID("b1");
        mol.addBond(bond);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(bond.getID(), roundTrippedBond.getID());
    }
    
    public void testBondStereo() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("O");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        int stereo = CDKConstants.STEREO_BOND_DOWN;
        bond.setStereo(stereo);
        mol.addBond(bond);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(bond.getStereo(), roundTrippedBond.getStereo());
    }
    
    public void testBondAromatic() throws Exception {
        Molecule mol = new Molecule();
        // surely, this bond is not aromatic... but fortunately, file formats do not care about chemistry
        Atom atom = new Atom("C");
        Atom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        Bond bond = new Bond(atom, atom2, IBond.Order.SINGLE);
        bond.setFlag(CDKConstants.ISAROMATIC, true);
        mol.addBond(bond);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(2, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getBondCount());
        IBond roundTrippedBond = roundTrippedMol.getBond(0);
        assertEquals(bond.getFlag(CDKConstants.ISAROMATIC), roundTrippedBond.getFlag(CDKConstants.ISAROMATIC));
    }

    /**
     * Convert a Molecule to CML and back to a Molecule again.
     * Given that CML reading is working, the problem is with the
     * CMLWriter.
     *
     * @see org.openscience.cdk.CMLFragmentsTest
     */
    private IMolecule roundTripMolecule(IMolecule mol) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkMoleculeToCMLMolecule(mol);
        cmlString = cmlDOM.toXML();
        
        IMolecule roundTrippedMol = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        assertNotNull(file);
        assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        assertNotNull(sequence);
        assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        assertNotNull(chemModel);
        IMoleculeSet moleculeSet = chemModel.getMoleculeSet();
        assertNotNull(moleculeSet);
        assertEquals(1, moleculeSet.getMoleculeCount());
        roundTrippedMol = moleculeSet.getMolecule(0);
        assertNotNull(roundTrippedMol);
        
        return roundTrippedMol;
    }
    
    private IChemModel roundTripChemModel(IChemModel model) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkChemModelToCMLList(model);
        cmlString = cmlDOM.toXML();
        
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile)reader.read(model.getBuilder().newChemFile());
        assertNotNull(file);
        assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        assertNotNull(sequence);
        assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        assertNotNull(chemModel);
        
        return chemModel;
    }

    private IReaction roundTripReaction(IReaction reaction) throws Exception {
        String cmlString = "<!-- failed -->";
        Element cmlDOM = convertor.cdkReactionToCMLReaction(reaction);
        cmlString = cmlDOM.toXML();
        
        IReaction roundTrippedReaction = null;
        logger.debug("CML string: ", cmlString);
        CMLReader reader = new CMLReader(new ByteArrayInputStream(cmlString.getBytes()));

        IChemFile file = (IChemFile)reader.read(new org.openscience.cdk.ChemFile());
        assertNotNull(file);
        assertEquals(1, file.getChemSequenceCount());
        IChemSequence sequence = file.getChemSequence(0);
        assertNotNull(sequence);
        assertEquals(1, sequence.getChemModelCount());
        IChemModel chemModel = sequence.getChemModel(0);
        assertNotNull(chemModel);
        IReactionSet reactionSet = chemModel.getReactionSet();
        assertNotNull(reactionSet);
        assertEquals(1, reactionSet.getReactionCount());
        roundTrippedReaction = reactionSet.getReaction(0);
        assertNotNull(roundTrippedReaction);
        
        return roundTrippedReaction;
    }

    public void testPartialCharge() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        double charge = -0.267;
        atom.setCharge(charge);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(charge, roundTrippedAtom.getCharge(), 0.0001);
    }

    public void testInChI() throws Exception {
        Molecule mol = new Molecule();
        String inchi = "InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)";
        mol.setProperty(CDKConstants.INCHI, inchi);
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        assertNotNull(roundTrippedMol);
        
        assertEquals(inchi, roundTrippedMol.getProperty(CDKConstants.INCHI));
    }

    public void testSpinMultiplicity() throws Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        mol.addAtom(atom);
        mol.addSingleElectron(new SingleElectron(atom));
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        
        assertEquals(1, roundTrippedMol.getAtomCount());
        assertEquals(1, roundTrippedMol.getElectronContainerCount());
        IAtom roundTrippedAtom = roundTrippedMol.getAtom(0);
        assertEquals(1, roundTrippedMol.getConnectedSingleElectronsCount(roundTrippedAtom));
    }

    public void testReaction() throws Exception {
    	logger.debug("********** TEST REACTION **********");
        IReaction reaction = new Reaction();
        reaction.setID("reaction.1");
        IMolecule reactant = reaction.getBuilder().newMolecule();
        reactant.setID("react");
        IAtom atom = reaction.getBuilder().newAtom("C");
        reactant.addAtom(atom);
        reaction.addReactant(reactant);
        
        IMolecule product = reaction.getBuilder().newMolecule();
        product.setID("product");
        atom = reaction.getBuilder().newAtom("X");
        product.addAtom(atom);
        reaction.addProduct(product);
        
        IMolecule agent = reaction.getBuilder().newMolecule();
        agent.setID("water");
        atom = reaction.getBuilder().newAtom("H");
        agent.addAtom(atom);
        reaction.addAgent(agent);
        
        IReaction roundTrippedReaction = roundTripReaction(reaction);
        assertNotNull(roundTrippedReaction);
        assertEquals("reaction.1", roundTrippedReaction.getID());
        
        assertNotNull(roundTrippedReaction);
        IMoleculeSet reactants = roundTrippedReaction.getReactants();
        assertNotNull(reactants);
        assertEquals(1, reactants.getMoleculeCount());
        IMolecule roundTrippedReactant = reactants.getMolecule(0);
        assertEquals("react", roundTrippedReactant.getID());
        assertEquals(1, roundTrippedReactant.getAtomCount());
        
        IMoleculeSet products = roundTrippedReaction.getProducts();
        assertNotNull(products);
        assertEquals(1, products.getMoleculeCount());
        IMolecule roundTrippedProduct = products.getMolecule(0);
        assertEquals("product", roundTrippedProduct.getID());
        assertEquals(1, roundTrippedProduct.getAtomCount());
        
        IMoleculeSet agents = roundTrippedReaction.getAgents();
        assertNotNull(agents);
        assertEquals(1, agents.getMoleculeCount());
        IMolecule roundTrippedAgent = agents.getMolecule(0);
        assertEquals("water", roundTrippedAgent.getID());
        assertEquals(1, roundTrippedAgent.getAtomCount());
    }

    public void testDescriptorValue_QSAR() throws Exception {
    	Molecule molecule = MoleculeFactory.makeBenzene();
        IMolecularDescriptor descriptor = new WeightDescriptor();

        DescriptorValue originalValue = null;
        originalValue = descriptor.calculate(molecule);
        molecule.setProperty(originalValue.getSpecification(), originalValue);
        IMolecule roundTrippedMol = roundTripMolecule(molecule);

        assertEquals(1, roundTrippedMol.getProperties().size());
        Object object = roundTrippedMol.getProperties().keySet().toArray()[0];
        assertTrue(object instanceof DescriptorSpecification);
        DescriptorSpecification spec = (DescriptorSpecification)object;
        assertEquals(descriptor.getSpecification().getSpecificationReference(),
        		     spec.getSpecificationReference());
        assertEquals(descriptor.getSpecification().getImplementationIdentifier(),
   		     spec.getImplementationIdentifier());
        assertEquals(descriptor.getSpecification().getImplementationTitle(),
   		     spec.getImplementationTitle());
        assertEquals(descriptor.getSpecification().getImplementationVendor(),
   		     spec.getImplementationVendor());
        
        Object value = roundTrippedMol.getProperty(spec);
        assertNotNull(value);
        assertTrue(value instanceof DescriptorValue);
        DescriptorValue descriptorResult = (DescriptorValue)value;
        assertEquals(originalValue.getClass().getName(),
        	descriptorResult.getClass().getName());
        assertEquals(originalValue.getValue().toString(),
            	descriptorResult.getValue().toString());
    }

    public void testDescriptorValue() throws Exception {
    	Molecule molecule = MoleculeFactory.makeBenzene();

    	String propertyName = "testKey";
    	String propertyValue = "testValue";
    	
    	molecule.setProperty(propertyName, propertyValue);
        IMolecule roundTrippedMol = roundTripMolecule(molecule);

        assertNotNull(roundTrippedMol.getProperty(propertyName));
        assertEquals(propertyValue, roundTrippedMol.getProperty(propertyName));
    }

    /**
     * Tests of bond order information is stored even when aromiticity is given.
     * 
     * @throws Exception
     */
    public void testAromaticity() throws Exception {
    	IMolecule molecule = MoleculeFactory.makeBenzene();
    	for (Iterator bonds=molecule.bonds(); bonds.hasNext();) {
    		((IBond)bonds.next()).setFlag(CDKConstants.ISAROMATIC, true);
    	}
    	
        IMolecule roundTrippedMol = roundTripMolecule(molecule);
        Iterator<IBond> bonds = roundTrippedMol.bonds();
        double orderSum = BondManipulator.getSingleBondEquivalentSum(bonds);
        while (bonds.hasNext()) {
    		assertTrue(bonds.next().getFlag(CDKConstants.ISAROMATIC));
    	}
        assertEquals(9.0, orderSum, 0.001);
    }
    
    /**
     * Tests whether the custom atom properties survive the CML round-trip
     * @throws Exception
     * 
     * @cdk.bug 1930029 
     */
    public void testAtomProperty() throws Exception {
 	   String key = "customAtomProperty";
 	   String value = "true";
 	   
        Molecule mol = MoleculeFactory.makeBenzene();
        for (Iterator<IAtom> it = mol.atoms(); it.hasNext();) {
           IAtom a = it.next();
           a.setProperty(key, value);
        }       
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        //assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(), 
     	//	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
        
        for (Iterator<IAtom> it = roundTrippedMol.atoms(); it.hasNext();) {
            IAtom a = it.next();
            String actual = (String)a.getProperty(key);
            assertNotNull(actual);
            assertEquals(value, actual);
         }       
    }
    
    /**
     * Tests whether the custom bond properties survive the CML round-trip
     * @throws Exception
     * 
     * @cdk.bug 1930029 
     */
    public void testBondProperty() throws Exception {
 	   String key = "customBondProperty";
 	   String value = "true";
        Molecule mol = MoleculeFactory.makeBenzene();
        for (Iterator<IBond> it = mol.bonds(); it.hasNext();) {
           IBond b = it.next();
           b.setProperty(key, value);
        }       
        
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        //assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(), 
        //	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
        
        for (Iterator<IBond> it = roundTrippedMol.bonds(); it.hasNext();) {
            IBond b = it.next();
            String actual = (String)b.getProperty(key); 
            assertNotNull(actual);
            assertEquals(value, actual);
         }       
    }
    
    /**
     * Tests whether the custom molecule properties survive the CML round-trip
     * @throws Exception
     * 
     * @cdk.bug 1930029 
     */
    public void testMoleculeProperty() throws Exception {
 	   String key = "customMoleculeProperty";
 	   String value = "true";      

 	   IMolecule mol = MoleculeFactory.makeAdenine();
        mol.setProperty(key, value);	   
        IMolecule roundTrippedMol = roundTripMolecule(mol);
        //assertEquals(convertor.cdkMoleculeToCMLMolecule(mol).toXML(), 
     	//	   convertor.cdkMoleculeToCMLMolecule(roundTrippedMol).toXML());
        String actual = (String)roundTrippedMol.getProperty(key);
        assertNotNull(actual);
        assertEquals(value, actual);
    }

    public void testMoleculeSet() throws Exception {
    	MoleculeSet list = new MoleculeSet();
    	list.addAtomContainer(new Molecule());
    	list.addAtomContainer(new Molecule());
    	IChemModel model = new ChemModel();
    	model.setMoleculeSet(list);
    	
    	IChemModel roundTripped = roundTripChemModel(model);
    	IMoleculeSet newList = roundTripped.getMoleculeSet();
    	assertNotNull(newList);
    	assertEquals(2, newList.getAtomContainerCount());
    	assertNotNull(newList.getAtomContainer(0));
    	assertNotNull(newList.getAtomContainer(1));
    }
    
}

