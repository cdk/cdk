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
package org.openscience.cdk.tools.manipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.XMLIsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;

/**
 * @cdk.module test-standard
 */
public class AtomContainerManipulatorTest extends CDKTestCase {
    IAtomContainer ac;

    @Before
    public void setUp()
    {
        ac = MoleculeFactory.makeAlphaPinene();
    }

    @Test
    public void testExtractSubstructure() throws CloneNotSupportedException {
        IAtomContainer source = MoleculeFactory.makeEthylCyclohexane();
        IAtomContainer ringSubstructure = 
            AtomContainerManipulator.extractSubstructure(source, 0, 1, 2, 3, 4, 5);
        Assert.assertEquals(6, ringSubstructure.getAtomCount());
        Assert.assertEquals(6, ringSubstructure.getBondCount());
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    public void testGetTotalHydrogenCount_IAtomContainer() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        Assert.assertEquals(6, mol.getAtomCount());
        Assert.assertEquals(5, mol.getBondCount());
        // total includes explicit and implicit (we don't have any implicit to 4 is expected)
        Assert.assertEquals(4, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    @Test public void testConvertImplicitToExplicitHydrogens_IAtomContainer() throws Exception {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(2);
        mol.addBond(0,1, CDKConstants.BONDORDER_DOUBLE);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());
        
        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assert.assertEquals(6, mol.getAtomCount());
        Assert.assertEquals(5, mol.getBondCount());
    }

    @Test
    public void testConvertImplicitToExplicitHydrogens_IAtomContainer2() throws Exception {
        IAtomContainer mol = new AtomContainer(); // ethane
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(3);
        mol.getAtom(1).setImplicitHydrogenCount(3);
        mol.addBond(0, 1, CDKConstants.BONDORDER_SINGLE);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assert.assertEquals(8, mol.getAtomCount());
        Assert.assertEquals(7, mol.getBondCount());
    }


        
    @Test public void testGetTotalHydrogenCount_IAtomContainer_zeroImplicit() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(0);
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        Assert.assertEquals(6, mol.getAtomCount());
        Assert.assertEquals(5, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assert.assertEquals(6, mol.getAtomCount());
        Assert.assertEquals(5, mol.getBondCount());
    }

    @Test public void testGetTotalHydrogenCount_IAtomContainer_nullImplicit() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(null);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(null);
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);
        Assert.assertEquals(6, mol.getAtomCount());
        Assert.assertEquals(5, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assert.assertEquals(6, mol.getAtomCount());
        Assert.assertEquals(5, mol.getBondCount());
    }

    @Test public void testGetTotalHydrogenCount_ImplicitHydrogens() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom carbon = new Atom("C");
        carbon.setImplicitHydrogenCount(4);
        mol.addAtom(carbon);
        Assert.assertEquals(4, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }
        
    @Test public void testRemoveHydrogens_IAtomContainer() throws IOException, ClassNotFoundException, CDKException{
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(CDKConstants.ISAROMATIC,true);
        
        Assert.assertEquals(6, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(2, ac.getAtomCount());
        Assert.assertTrue(ac.getFlag(CDKConstants.ISAROMATIC));
    }

    private IAtomContainer getChiralMolTemplate() {
        IAtomContainer molecule = new AtomContainer();
        molecule.addAtom(new Atom("Cl"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Br"));
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("H"));
        molecule.addAtom(new Atom("Cl"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        molecule.addBond(1, 2, IBond.Order.SINGLE);
        molecule.addBond(1, 3, IBond.Order.SINGLE);
        molecule.addBond(1, 4, IBond.Order.SINGLE);
        molecule.addBond(4, 5, IBond.Order.SINGLE);
        molecule.addBond(4, 6, IBond.Order.SINGLE);
        molecule.addBond(4, 7, IBond.Order.SINGLE);

        return molecule;
    }

    @Test public void testRemoveNonChiralHydrogens_StereoElement() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        IAtom[] ligands = new IAtom[] {
                molecule.getAtom(4),
                molecule.getAtom(3),
                molecule.getAtom(2),
                molecule.getAtom(0)
        };

        TetrahedralChirality chirality = new TetrahedralChirality(
                molecule.getAtom(1), ligands, ITetrahedralChirality.Stereo.CLOCKWISE
        );
        molecule.addStereoElement(chirality);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test public void testRemoveNonChiralHydrogens_StereoParity() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getAtom(1).setStereoParity(CDKConstants.STEREO_ATOM_PARITY_MINUS);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test public void testRemoveNonChiralHydrogens_StereoBond() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getBond(2).setStereo(IBond.Stereo.UP);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test public void testRemoveNonChiralHydrogens_StereoBondHeteroAtom() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getBond(3).setStereo(IBond.Stereo.UP);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test public void testRemoveNonChiralHydrogens_IAtomContainer() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(5, ac.getAtomCount());
    }

    @Test public void testRemoveHydrogensZeroHydrogenCounts() throws IOException, ClassNotFoundException, CDKException{
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("Br"));
        mol.addAtom(new Atom("Br"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        
        Assert.assertEquals(6, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(4, ac.getAtomCount());
        Assert.assertNotNull(ac.getAtom(0).getImplicitHydrogenCount());
        Assert.assertNotNull(ac.getAtom(1).getImplicitHydrogenCount());
        Assert.assertNotNull(ac.getAtom(2).getImplicitHydrogenCount());
        Assert.assertNotNull(ac.getAtom(3).getImplicitHydrogenCount());
        Assert.assertEquals(0, ac.getAtom(0).getImplicitHydrogenCount().intValue());
        Assert.assertEquals(2, ac.getAtom(1).getImplicitHydrogenCount().intValue());
        Assert.assertEquals(0, ac.getAtom(2).getImplicitHydrogenCount().intValue());
        Assert.assertEquals(0, ac.getAtom(3).getImplicitHydrogenCount().intValue());
    }
    
    @Test public void testGetAllIDs_IAtomContainer() {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C")); mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C")); mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H")); mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H")); mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H")); mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H")); mol.getAtom(5).setID("a6");
        
        List<String> ids = AtomContainerManipulator.getAllIDs(mol);
        Assert.assertEquals(6, ids.size());
        Assert.assertTrue(ids.contains("a1"));
        Assert.assertTrue(ids.contains("a2"));
        Assert.assertTrue(ids.contains("a3"));
        Assert.assertTrue(ids.contains("a4"));
        Assert.assertTrue(ids.contains("a5"));
        Assert.assertTrue(ids.contains("a6"));
    }

    @Test public void testGetAtomArray_IAtomContainer() {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol);
        Assert.assertEquals(6, atoms.length);
        Assert.assertEquals(mol.getAtom(0), atoms[0]);
        Assert.assertEquals(mol.getAtom(1), atoms[1]);
        Assert.assertEquals(mol.getAtom(2), atoms[2]);
        Assert.assertEquals(mol.getAtom(3), atoms[3]);
        Assert.assertEquals(mol.getAtom(4), atoms[4]);
        Assert.assertEquals(mol.getAtom(5), atoms[5]);
    }

    @Test public void testGetAtomArray_List() {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(CDKConstants.ISAROMATIC,true);
        
        IAtom[] atoms = AtomContainerManipulator.getAtomArray(
        	mol.getConnectedAtomsList(mol.getAtom(0))
        );
        Assert.assertEquals(3, atoms.length);
        Assert.assertEquals(mol.getAtom(1), atoms[0]);
        Assert.assertEquals(mol.getAtom(2), atoms[1]);
        Assert.assertEquals(mol.getAtom(3), atoms[2]);
    }

    @Test public void testGetBondArray_List() {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(CDKConstants.ISAROMATIC,true);
        
        IBond[] bonds = AtomContainerManipulator.getBondArray(
        	mol.getConnectedBondsList(mol.getAtom(0))
        );
        Assert.assertEquals(3, bonds.length);
        Assert.assertEquals(mol.getBond(0), bonds[0]);
        Assert.assertEquals(mol.getBond(1), bonds[1]);
        Assert.assertEquals(mol.getBond(2), bonds[2]);
    }

    @Test public void testGetBondArray_IAtomContainer() {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(CDKConstants.ISAROMATIC,true);
        
        IBond[] bonds = AtomContainerManipulator.getBondArray(mol);
        Assert.assertEquals(5, bonds.length);
        Assert.assertEquals(mol.getBond(0), bonds[0]);
        Assert.assertEquals(mol.getBond(1), bonds[1]);
        Assert.assertEquals(mol.getBond(2), bonds[2]);
        Assert.assertEquals(mol.getBond(3), bonds[3]);
        Assert.assertEquals(mol.getBond(4), bonds[4]);
    }

    @Test public void testGetAtomById_IAtomContainer_String() throws Exception {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C")); mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C")); mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H")); mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H")); mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H")); mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H")); mol.getAtom(5).setID("a6");
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(CDKConstants.ISAROMATIC,true);
        
        Assert.assertEquals(mol.getAtom(0), AtomContainerManipulator.getAtomById(mol, "a1"));
        Assert.assertEquals(mol.getAtom(1), AtomContainerManipulator.getAtomById(mol, "a2"));
        Assert.assertEquals(mol.getAtom(2), AtomContainerManipulator.getAtomById(mol, "a3"));
        Assert.assertEquals(mol.getAtom(3), AtomContainerManipulator.getAtomById(mol, "a4"));
        Assert.assertEquals(mol.getAtom(4), AtomContainerManipulator.getAtomById(mol, "a5"));
        Assert.assertEquals(mol.getAtom(5), AtomContainerManipulator.getAtomById(mol, "a6"));
    }

    /**
     * Test removeHydrogens for B2H6, which contains two multiply bonded H.
     *
     */
    @Test public void testRemoveHydrogensBorane() throws Exception
    {
    	IAtomContainer borane = new AtomContainer();
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"B"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"B"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
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
        Assert.assertEquals("incorrect atom count", 2, ac.getAtomCount());
        Assert.assertEquals("incorrect bond count", 0, ac.getBondCount());
        Assert.assertEquals("incorrect hydrogen count", 4, ac.getAtom(0).getImplicitHydrogenCount().intValue());
        Assert.assertEquals("incorrect hydrogen count", 4, ac.getAtom(1).getImplicitHydrogenCount().intValue());
    }
    /**
     * Test total formal charge.
     *
     */
    @Test public void testGetTotalFormalCharge_IAtomContainer() throws Exception
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalFormalCharge(mol);

        Assert.assertEquals(1,totalCharge);
    }
    
    /**
     * Test total Exact Mass.
     *
     */
    @Test public void testGetTotalExactMass_IAtomContainer() throws Exception{
    	
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCl");
        mol.getAtom(0).setExactMass(12.00);
        mol.getAtom(1).setExactMass(34.96885268);
        double totalExactMass = AtomContainerManipulator.getTotalExactMass(mol);

        Assert.assertEquals(46.96885268,totalExactMass,0.000001);
    }
    
    @Test public void testGetNaturalExactMass_IAtomContainer() throws Exception {
    	IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance(); 
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("Cl"));
    	
        double expectedMass = 0.0;
        expectedMass += XMLIsotopeFactory.getInstance(builder).getNaturalMass(builder.newInstance(IElement.class,"C"));
        expectedMass += XMLIsotopeFactory.getInstance(builder).getNaturalMass(builder.newInstance(IElement.class,"Cl"));
        
    	double totalExactMass = AtomContainerManipulator.getNaturalExactMass(mol);

        Assert.assertEquals(expectedMass, totalExactMass, 0.000001);
    }
    
    /**
     * Test total natural abundance.
     *
     */
    @Test public void testGetTotalNaturalAbundance_IAtomContainer() throws Exception{
    	
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCl");
        mol.getAtom(0).setNaturalAbundance(98.93);
        mol.getAtom(1).setNaturalAbundance(75.78);
        double totalAbudance = AtomContainerManipulator.getTotalNaturalAbundance(mol);

        Assert.assertEquals(0.74969154,totalAbudance,0.000001);
    }
    
    /**
     * Test total positive formal charge.
     *
     */
    @Test public void testGetTotalPositiveFormalCharge_IAtomContainer() throws Exception
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalPositiveFormalCharge(mol);

        Assert.assertEquals(2,totalCharge);
    }
    
    /**
     * Test total negative formal charge.
     *
     */
    @Test public void testGetTotalNegativeFormalCharge_IAtomContainer() throws Exception
    {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalNegativeFormalCharge(mol);

        Assert.assertEquals(-1,totalCharge);
    }

    @Test public void testGetIntersection_IAtomContainer_IAtomContainer() {
    	IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtom c1 = builder.newInstance(IAtom.class,"C");
        IAtom o = builder.newInstance(IAtom.class,"O");
        IAtom c2 = builder.newInstance(IAtom.class,"C");
        IAtom c3 = builder.newInstance(IAtom.class,"C");
        
        IBond b1 = builder.newInstance(IBond.class,c1, o);
        IBond b2 = builder.newInstance(IBond.class,o, c2);
        IBond b3 = builder.newInstance(IBond.class,c2, c3);
        
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
        Assert.assertEquals(2, intersection.getAtomCount());
        Assert.assertEquals(1, intersection.getBondCount());
        Assert.assertTrue(intersection.contains(b2));
        Assert.assertTrue(intersection.contains(o));
        Assert.assertTrue(intersection.contains(c2));
    }
 
    @Test
    public void testPerceiveAtomTypesAndConfigureAtoms() {
    	IAtomContainer container = new AtomContainer();
        container.addAtom(new Atom("R"));
        
        // the next should not throw an exception
        try {
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
		} catch (CDKException e) {
			Assert.fail("The percieveAtomTypesAndConfigureAtoms must not throw exceptions when no atom type is perceived.");
		}
    }

    @Test
    public void testPerceiveAtomTypesAndConfigureUnsetProperties() throws Exception {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom("C");
        atom.setExactMass(13.0);
        container.addAtom(atom);
        IAtomType type = new AtomType("C");
        type.setAtomTypeName("C.sp3");
        type.setExactMass(12.0);
        
        AtomContainerManipulator.percieveAtomTypesAndConfigureUnsetProperties(container);
        Assert.assertNotNull(atom.getExactMass());
        Assert.assertEquals(13.0, atom.getExactMass(), 0.1);
        Assert.assertNotNull(atom.getAtomTypeName());
        Assert.assertEquals("C.sp3", atom.getAtomTypeName());
    }

    @Test
    public void testClearConfig() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"O");
        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"C");
        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);
        container.addBond(new Bond(atom1, atom2, IBond.Order.SINGLE));
        container.addBond(new Bond(atom2, atom3, IBond.Order.SINGLE));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        for (IAtom atom : container.atoms()) {
            Assert.assertTrue(atom.getAtomTypeName() != CDKConstants.UNSET);
            Assert.assertTrue(atom.getHybridization() != CDKConstants.UNSET);
            Assert.assertTrue(atom.getAtomicNumber() != CDKConstants.UNSET);
        }

        AtomContainerManipulator.clearAtomConfigurations(container);
        for (IAtom atom : container.atoms()) {
            Assert.assertTrue(atom.getAtomTypeName() == CDKConstants.UNSET);
            Assert.assertTrue(atom.getHybridization() == CDKConstants.UNSET);
            Assert.assertTrue(atom.getAtomicNumber() == CDKConstants.UNSET);
        }
    }

    @Test
    public void testGetMaxBondOrder() {
        Assert.assertEquals(CDKConstants.BONDORDER_DOUBLE, AtomContainerManipulator.getMaximumBondOrder(ac));
    }

    @Test
    public void testGetSBE() {
        Assert.assertEquals(12, AtomContainerManipulator.getSingleBondEquivalentSum(ac));
    }

    @Test
    public void testGetTotalCharge() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, CDKConstants.BONDORDER_SINGLE));

        double totalCharge = AtomContainerManipulator.getTotalCharge(container);

        Assert.assertEquals(1.0, totalCharge, 0.01);
    }

    /**
     * @cdk.bug 1254
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCountExplicitH_Null_IAtom() {
        AtomContainerManipulator.countExplicitHydrogens(null, DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class));
    }

    /**
     * @cdk.bug 1254
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCountExplicitH_IAtomContainer_Null() {
        AtomContainerManipulator.countExplicitHydrogens(DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class), null);
    }

    @Test
    public void testCountExplicitH() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, CDKConstants.BONDORDER_SINGLE));

        Assert.assertEquals(0, AtomContainerManipulator.countExplicitHydrogens(container, atom1));
        Assert.assertEquals(0, AtomContainerManipulator.countExplicitHydrogens(container, atom2));

        for (int i = 0; i < 3; i++) {
            IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"H");
            container.addAtom(h);
            container.addBond(new Bond(atom1, h, CDKConstants.BONDORDER_SINGLE));
        }
        Assert.assertEquals(3, AtomContainerManipulator.countExplicitHydrogens(container, atom1));                
    }

    @Test
    public void testCountH() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, CDKConstants.BONDORDER_SINGLE));

        // no atom type perception, so implicit count is 0
        Assert.assertEquals(0, AtomContainerManipulator.countHydrogens(container, atom1));
        Assert.assertEquals(0, AtomContainerManipulator.countHydrogens(container, atom2));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder ha = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
        ha.addImplicitHydrogens(container);

        Assert.assertEquals(3, AtomContainerManipulator.countHydrogens(container, atom1));
        Assert.assertEquals(2, AtomContainerManipulator.countHydrogens(container, atom2));

        
        for (int i = 0; i < 3; i++) {
            IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"H");
            container.addAtom(h);
            container.addBond(new Bond(atom1, h, CDKConstants.BONDORDER_SINGLE));
        }
        Assert.assertEquals(6, AtomContainerManipulator.countHydrogens(container, atom1));
        
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    public void testGetImplicitHydrogenCount_unperceived() throws Exception {
        IAtomContainer container = MoleculeFactory.makeAdenine();
        Assert.assertEquals("Container has not been atom-typed - should have 0 implicit hydrogens",
                            0,
                            AtomContainerManipulator.getImplicitHydrogenCount(container));
    }

    /**
     * @cdk.bug 1254
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetImplicitHydrogenCount_null() throws Exception {
        AtomContainerManipulator.getImplicitHydrogenCount(null);
    }

    /**
     * @cdk.bug 1254
     */
    @Test
    public void testGetImplicitHydrogenCount_adenine() throws Exception {
        IAtomContainer container = MoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance()).addImplicitHydrogens(container);
        Assert.assertEquals("Adenine should have 5 implicit hydrogens",
                            5,
                            AtomContainerManipulator.getImplicitHydrogenCount(container));

    }

    @Test
    public void testReplaceAtom() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, CDKConstants.BONDORDER_SINGLE));

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class,"Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assert.assertEquals(atom3, container.getAtom(1));
    }

    @Test public void testGetHeavyAtoms_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        container.addAtom(builder.newInstance(IAtom.class,"C"));
        for(int i = 0; i < 4; i++)
            container.addAtom(builder.newInstance(IAtom.class,"H"));
        container.addAtom(builder.newInstance(IAtom.class,"O"));
        Assert.assertEquals(2, AtomContainerManipulator.getHeavyAtoms(container).size());
    }
    
    /**
     * Test removeHydrogensPreserveMultiplyBonded for B2H6, which contains two multiply bonded H.
     *
     */
    @Test public void testRemoveHydrogensPreserveMultiplyBonded() throws Exception {
    	IAtomContainer borane = new AtomContainer();
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"B"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"B"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addAtom(borane.getBuilder().newInstance(IAtom.class,"H"));
    	borane.addBond(0,2,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(1,2,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(2,3,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(2,4,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(3,5,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(4,5,CDKConstants.BONDORDER_SINGLE); // REALLY 3-CENTER-2-ELECTRON
    	borane.addBond(5,6,CDKConstants.BONDORDER_SINGLE);
    	borane.addBond(5,7,CDKConstants.BONDORDER_SINGLE);
        IAtomContainer ac = AtomContainerManipulator.removeHydrogensPreserveMultiplyBonded(borane);

        // Should be two connected Bs with H-count == 2 and two explicit Hs.
        Assert.assertEquals("incorrect atom count", 4, ac.getAtomCount());
        Assert.assertEquals("incorrect bond count", 4, ac.getBondCount());

        int b = 0;
        int h = 0;
        for (int i = 0;
                i < ac.getAtomCount();
                i++)
        {
            final org.openscience.cdk.interfaces.IAtom atom = ac.getAtom(i);
            String sym = atom.getSymbol();
            if (sym.equals("B"))
            {
                // Each B has two explicit and two implicit H.
                b++;
                Assert.assertEquals("incorrect hydrogen count", 2, atom.getImplicitHydrogenCount().intValue());
                List<IAtom> nbs = ac.getConnectedAtomsList(atom);
                Assert.assertEquals("incorrect connected count", 2, nbs.size());
                Assert.assertEquals("incorrect bond", "H", ((IAtom)nbs.get(0)).getSymbol());
                Assert.assertEquals("incorrect bond", "H", ((IAtom)nbs.get(1)).getSymbol());
            }
            else if (sym.equals("H"))
            {
                h++;
            }
        }
        Assert.assertEquals("incorrect no. Bs", 2, b);
        Assert.assertEquals("incorrect no. Hs", 2, h);
    }
    
    @Test public void testCreateAnyAtomAnyBondAtomContainer_IAtomContainer() throws Exception {
        String smiles = "c1ccccc1";
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smiles);
        mol=AtomContainerManipulator.createAllCarbonAllSingleNonAromaticBondAtomContainer(mol);
        String smiles2 = "C1CCCCC1";
        IAtomContainer mol2 = sp.parseSmiles(smiles2);
        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(mol, mol2));
    }

    @Test public void testAnonymise() throws Exception {

        IAtomContainer cyclohexane = MoleculeFactory.makeCyclohexane();

        cyclohexane.getAtom(0).setSymbol("O");
        cyclohexane.getAtom(2).setSymbol("O");
        cyclohexane.getAtom(1).setAtomTypeName("remove me");
        cyclohexane.getAtom(3).setFlag(CDKConstants.ISAROMATIC, true);
        cyclohexane.getAtom(4).setImplicitHydrogenCount(2);
        cyclohexane.getBond(0).setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        cyclohexane.getBond(1).setFlag(CDKConstants.ISAROMATIC, true);

        IAtomContainer anonymous = AtomContainerManipulator.anonymise(cyclohexane);

        Assert.assertTrue(new UniversalIsomorphismTester().isIsomorph(anonymous,
                                                                      MoleculeFactory.makeCyclohexane()));

        assertThat(anonymous.getAtom(0).getSymbol(), is("C"));
        assertThat(anonymous.getAtom(2).getSymbol(), is("C"));
        assertNull(anonymous.getAtom(1).getAtomTypeName());
        assertNull(anonymous.getAtom(4).getImplicitHydrogenCount());
        assertFalse(anonymous.getAtom(3).getFlag(CDKConstants.ISAROMATIC));

        assertFalse(anonymous.getBond(1).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(anonymous.getBond(1).getFlag(CDKConstants.SINGLE_OR_DOUBLE));
    }

    /**
     * @cdk.bug  1969156
     */
    @Test
    public void testOverWriteConfig() throws Exception {
        String filename = "data/mdl/lobtest2.sdf";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = new MDLV2000Reader(ins);
        ChemFile content = (ChemFile) reader.read(new ChemFile());
        List<IAtomContainer> cList = ChemFileManipulator.getAllAtomContainers(content);
        IAtomContainer ac = cList.get(0);

        for (IAtom atom : ac.atoms()) {
            Assert.assertNotNull(atom.getExactMass());
            Assert.assertTrue(atom.getExactMass() > 0);
        }

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);

        for (IAtom atom : ac.atoms()) {
            Assert.assertNotNull("exact mass should not be null, after typing", atom.getExactMass());
            Assert.assertTrue(atom.getExactMass() > 0);
        }
    }
    
    @Test public void setSingleOrDoubleFlags() {
        IAtomContainer biphenyl = TestMoleculeFactory.makeBiphenyl();
        for (IBond bond : biphenyl.bonds()) {
            bond.setFlag(CDKConstants.ISAROMATIC, true);            
        }
        AtomContainerManipulator.setSingleOrDoubleFlags(biphenyl);
        assertTrue(biphenyl.getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        for (IAtom atom : biphenyl.atoms()) {
            assertTrue(biphenyl.getFlag(CDKConstants.SINGLE_OR_DOUBLE));
        }
        int n = 0;
        for (IBond bond : biphenyl.bonds()) {
            n += bond.getFlag(CDKConstants.SINGLE_OR_DOUBLE) ? 1 : 0;
        }
        // 13 bonds - the one which joins the two rings is now marked as single
        // or double
        assertThat(n, is(12)); 
    }

    /**
     * Molecular hydrogen is found in the first batch of PubChem entries, and
     * removal of hydrogen should simply return an empty IAtomContainer, not
     * throw an NullPointerException.
     * 
     * @cdk.bug 2366528
     */
    @Test public void testRemoveHydrogensFromMolecularHydrogen() {
        IAtomContainer mol = new AtomContainer(); // molecular hydrogen
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.SINGLE);

        Assert.assertEquals(2, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(0, ac.getAtomCount());
    }

    @Test
    public void testBondOrderSum() throws InvalidSmilesException {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("C=CC");
        double bosum = AtomContainerManipulator.getBondOrderSum(mol, mol.getAtom(0));
        Assert.assertEquals(2.0, bosum, 0.001);
        bosum = AtomContainerManipulator.getBondOrderSum(mol, mol.getAtom(1));
        Assert.assertEquals(3.0, bosum, 0.001);
        bosum = AtomContainerManipulator.getBondOrderSum(mol, mol.getAtom(2));
        Assert.assertEquals(1.0, bosum, 0.001);

    }          
}


