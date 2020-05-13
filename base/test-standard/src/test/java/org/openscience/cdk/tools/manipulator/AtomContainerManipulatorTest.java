/* Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import org.hamcrest.CoreMatchers;
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
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.silent.PseudoAtom;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.templates.TestMoleculeFactory;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.number.IsCloseTo.closeTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.openscience.cdk.tools.manipulator.AtomContainerManipulator.*;

/**
 * @cdk.module test-standard
 */
public class AtomContainerManipulatorTest extends CDKTestCase {

    IAtomContainer ac;

    @Before
    public void setUp() {
        ac = TestMoleculeFactory.makeAlphaPinene();
    }

    @Test
    public void testExtractSubstructure() throws CloneNotSupportedException {
        IAtomContainer source = TestMoleculeFactory.makeEthylCyclohexane();
        IAtomContainer ringSubstructure = AtomContainerManipulator.extractSubstructure(source, 0, 1, 2, 3, 4, 5);
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

    @Test
    public void testConvertImplicitToExplicitHydrogens_IAtomContainer() throws Exception {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setImplicitHydrogenCount(2);
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setImplicitHydrogenCount(2);
        mol.addBond(0, 1, Order.DOUBLE);
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
        mol.addBond(0, 1, Order.SINGLE);
        Assert.assertEquals(2, mol.getAtomCount());
        Assert.assertEquals(1, mol.getBondCount());

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        Assert.assertEquals(8, mol.getAtomCount());
        Assert.assertEquals(7, mol.getBondCount());
    }

    @Test
    public void testGetTotalHydrogenCount_IAtomContainer_zeroImplicit() throws IOException, ClassNotFoundException,
            CDKException {
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

    @Test
    public void testGetTotalHydrogenCount_IAtomContainer_nullImplicit() throws IOException, ClassNotFoundException,
            CDKException {
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

    @Test
    public void testGetTotalHydrogenCount_ImplicitHydrogens() throws Exception {
        IAtomContainer mol = new AtomContainer();
        Atom carbon = new Atom("C");
        carbon.setImplicitHydrogenCount(4);
        mol.addAtom(carbon);
        Assert.assertEquals(4, AtomContainerManipulator.getTotalHydrogenCount(mol));
    }

    @Test
    public void testRemoveHydrogens_IAtomContainer() throws IOException, ClassNotFoundException, CDKException {
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
        for (IAtom atom : mol.atoms())
            atom.setImplicitHydrogenCount(0);
        mol.setFlag(CDKConstants.ISAROMATIC, true);

        Assert.assertEquals(6, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(2, ac.getAtomCount());
        assertTrue(ac.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test
    public void dontSuppressHydrogensOnPseudoAtoms() throws Exception {
        IAtomContainer mol = new AtomContainer(); // *[H]
        mol.addAtom(new PseudoAtom("*"));
        mol.addAtom(new Atom("H"));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        mol.addBond(0, 1, Order.SINGLE);
        Assert.assertEquals(2, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(2, ac.getAtomCount());
    }

    @Test
    public void suppressHydrogensKeepsRadicals() throws Exception {
        IAtomContainer mol = new AtomContainer(); // *[H]
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(1).setImplicitHydrogenCount(1);
        mol.getAtom(2).setImplicitHydrogenCount(1);
        mol.getAtom(3).setImplicitHydrogenCount(1);
        mol.addBond(0, 1, Order.SINGLE);
        mol.addBond(0, 2, Order.SINGLE);
        mol.addBond(0, 3, Order.SINGLE);
        mol.addSingleElectron(0);
        Assert.assertEquals(4, mol.getAtomCount());
        Assert.assertEquals(1, mol.getSingleElectronCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(1, ac.getAtomCount());
        Assert.assertEquals(1, ac.getSingleElectronCount());
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

    @Test
    public void testRemoveNonChiralHydrogens_StereoElement() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        IAtom[] ligands = new IAtom[]{molecule.getAtom(4), molecule.getAtom(3), molecule.getAtom(2),
                molecule.getAtom(0)};

        TetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1), ligands,
                ITetrahedralChirality.Stereo.CLOCKWISE);
        molecule.addStereoElement(chirality);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test
    public void testRemoveNonChiralHydrogens_StereoParity() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getAtom(1).setStereoParity(CDKConstants.STEREO_ATOM_PARITY_MINUS);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test
    public void testRemoveNonChiralHydrogens_StereoBond() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getBond(2).setStereo(IBond.Stereo.UP);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test
    public void testRemoveNonChiralHydrogens_StereoBondHeteroAtom() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();
        molecule.getBond(3).setStereo(IBond.Stereo.UP);

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(6, ac.getAtomCount());
    }

    @Test
    public void testRemoveNonChiralHydrogens_IAtomContainer() throws Exception {

        IAtomContainer molecule = getChiralMolTemplate();

        Assert.assertEquals(8, molecule.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeNonChiralHydrogens(molecule);
        Assert.assertEquals(5, ac.getAtomCount());
    }

    @Test
    public void testRemoveHydrogensZeroHydrogenCounts() throws IOException, ClassNotFoundException, CDKException {
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
        mol.addBond(1, 4, IBond.Order.SINGLE);
        mol.addBond(1, 5, IBond.Order.SINGLE);

        mol.getAtom(0).setImplicitHydrogenCount(0);
        mol.getAtom(1).setImplicitHydrogenCount(0);
        mol.getAtom(2).setImplicitHydrogenCount(0);
        mol.getAtom(3).setImplicitHydrogenCount(0);
        mol.getAtom(4).setImplicitHydrogenCount(0);
        mol.getAtom(5).setImplicitHydrogenCount(0);

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

    @Test
    public void testGetAllIDs_IAtomContainer() {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H"));
        mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H"));
        mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H"));
        mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H"));
        mol.getAtom(5).setID("a6");

        List<String> ids = AtomContainerManipulator.getAllIDs(mol);
        Assert.assertEquals(6, ids.size());
        assertTrue(ids.contains("a1"));
        assertTrue(ids.contains("a2"));
        assertTrue(ids.contains("a3"));
        assertTrue(ids.contains("a4"));
        assertTrue(ids.contains("a5"));
        assertTrue(ids.contains("a6"));
    }

    @Test
    public void testGetAtomArray_IAtomContainer() {
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

    @Test
    public void testGetAtomArray_List() {
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
        mol.setFlag(CDKConstants.ISAROMATIC, true);

        IAtom[] atoms = AtomContainerManipulator.getAtomArray(mol.getConnectedAtomsList(mol.getAtom(0)));
        Assert.assertEquals(3, atoms.length);
        Assert.assertEquals(mol.getAtom(1), atoms[0]);
        Assert.assertEquals(mol.getAtom(2), atoms[1]);
        Assert.assertEquals(mol.getAtom(3), atoms[2]);
    }

    @Test
    public void testGetBondArray_List() {
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
        mol.setFlag(CDKConstants.ISAROMATIC, true);

        IBond[] bonds = AtomContainerManipulator.getBondArray(mol.getConnectedBondsList(mol.getAtom(0)));
        Assert.assertEquals(3, bonds.length);
        Assert.assertEquals(mol.getBond(0), bonds[0]);
        Assert.assertEquals(mol.getBond(1), bonds[1]);
        Assert.assertEquals(mol.getBond(2), bonds[2]);
    }

    @Test
    public void testGetBondArray_IAtomContainer() {
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
        mol.setFlag(CDKConstants.ISAROMATIC, true);

        IBond[] bonds = AtomContainerManipulator.getBondArray(mol);
        Assert.assertEquals(5, bonds.length);
        Assert.assertEquals(mol.getBond(0), bonds[0]);
        Assert.assertEquals(mol.getBond(1), bonds[1]);
        Assert.assertEquals(mol.getBond(2), bonds[2]);
        Assert.assertEquals(mol.getBond(3), bonds[3]);
        Assert.assertEquals(mol.getBond(4), bonds[4]);
    }

    @Test
    public void testGetAtomById_IAtomContainer_String() throws Exception {
        IAtomContainer mol = new AtomContainer(); // ethene
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setID("a1");
        mol.addAtom(new Atom("C"));
        mol.getAtom(1).setID("a2");
        mol.addAtom(new Atom("H"));
        mol.getAtom(2).setID("a3");
        mol.addAtom(new Atom("H"));
        mol.getAtom(3).setID("a4");
        mol.addAtom(new Atom("H"));
        mol.getAtom(4).setID("a5");
        mol.addAtom(new Atom("H"));
        mol.getAtom(5).setID("a6");
        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(1, 4, IBond.Order.DOUBLE);
        mol.addBond(1, 5, IBond.Order.DOUBLE);
        mol.setFlag(CDKConstants.ISAROMATIC, true);

        Assert.assertEquals(mol.getAtom(0), AtomContainerManipulator.getAtomById(mol, "a1"));
        Assert.assertEquals(mol.getAtom(1), AtomContainerManipulator.getAtomById(mol, "a2"));
        Assert.assertEquals(mol.getAtom(2), AtomContainerManipulator.getAtomById(mol, "a3"));
        Assert.assertEquals(mol.getAtom(3), AtomContainerManipulator.getAtomById(mol, "a4"));
        Assert.assertEquals(mol.getAtom(4), AtomContainerManipulator.getAtomById(mol, "a5"));
        Assert.assertEquals(mol.getAtom(5), AtomContainerManipulator.getAtomById(mol, "a6"));
    }

    /**
     * Test removeHydrogens for B2H6, which contains two multiply bonded H.
     * The old behaviour would removed these but now the bridged hydrogens are
     * kept.
     */
    @Test
    public void testRemoveHydrogensBorane() throws Exception {
        IAtomContainer borane = new AtomContainer();
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addBond(0, 2, Order.SINGLE);
        borane.addBond(1, 2, Order.SINGLE);
        borane.addBond(2, 3, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(2, 4, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(3, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(4, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(5, 6, Order.SINGLE);
        borane.addBond(5, 7, Order.SINGLE);
        for (IAtom atom : borane.atoms())
            atom.setImplicitHydrogenCount(0);
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(borane);

        // bridged hydrogens are now kept
        Assert.assertEquals("incorrect atom count", 4, ac.getAtomCount());
        Assert.assertEquals("incorrect bond count", 4, ac.getBondCount());
        for (IAtom atom : ac.atoms()) {
            if (atom.getAtomicNumber() == 1) continue;
            Assert.assertEquals("incorrect hydrogen count", 2, atom.getImplicitHydrogenCount().intValue());
        }
    }

    /**
     * Test total formal charge.
     *
     */
    @Test
    public void testGetTotalFormalCharge_IAtomContainer() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalFormalCharge(mol);

        Assert.assertEquals(1, totalCharge);
    }

    /**
     * Test total Exact Mass.
     *
     */
    @Test
    public void testGetTotalExactMass_IAtomContainer() throws Exception {

        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCl");
        mol.getAtom(0).setExactMass(12.00);
        mol.getAtom(1).setExactMass(34.96885268);
        double totalExactMass = AtomContainerManipulator.getTotalExactMass(mol);

        Assert.assertEquals(49.992327775, totalExactMass, 0.000001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNaturalExactMassNeedsHydrogens() {
        IAtomContainer mol = new AtomContainer();
        IAtom atom = new Atom("C");
        atom.setImplicitHydrogenCount(null);
        mol.addAtom(atom);
        AtomContainerManipulator.getNaturalExactMass(mol);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNaturalExactMassNeedsAtomicNumber() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.getAtom(0).setAtomicNumber(null);
        AtomContainerManipulator.getNaturalExactMass(mol);
    }

    @Test
    public void testGetNaturalExactMass_IAtomContainer() throws Exception {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("Cl"));

        mol.getAtom(0).setImplicitHydrogenCount(4);
        mol.getAtom(1).setImplicitHydrogenCount(1);

        double expectedMass = 0.0;
        expectedMass += Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "C"));
        expectedMass += Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "Cl"));
        expectedMass += 5 * Isotopes.getInstance().getNaturalMass(builder.newInstance(IElement.class, "H"));

        double totalExactMass = AtomContainerManipulator.getNaturalExactMass(mol);

        Assert.assertEquals(expectedMass, totalExactMass, 0.000001);
    }

    /**
     * Test total natural abundance.
     *
     */
    @Test
    public void testGetTotalNaturalAbundance_IAtomContainer() throws Exception {

        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("CCl");
        mol.getAtom(0).setNaturalAbundance(98.93);
        mol.getAtom(1).setNaturalAbundance(75.78);
        double totalAbudance = AtomContainerManipulator.getTotalNaturalAbundance(mol);

        Assert.assertEquals(0.749432, totalAbudance, 0.000001);
    }

    /**
     * Test total positive formal charge.
     *
     */
    @Test
    public void testGetTotalPositiveFormalCharge_IAtomContainer() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalPositiveFormalCharge(mol);

        Assert.assertEquals(2, totalCharge);
    }

    /**
     * Test total negative formal charge.
     *
     */
    @Test
    public void testGetTotalNegativeFormalCharge_IAtomContainer() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = parser.parseSmiles("[O-]C([N+])C([N+])C");
        int totalCharge = AtomContainerManipulator.getTotalNegativeFormalCharge(mol);

        Assert.assertEquals(-1, totalCharge);
    }

    @Test
    public void testGetIntersection_IAtomContainer_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtom c1 = builder.newInstance(IAtom.class, "C");
        IAtom o = builder.newInstance(IAtom.class, "O");
        IAtom c2 = builder.newInstance(IAtom.class, "C");
        IAtom c3 = builder.newInstance(IAtom.class, "C");

        IBond b1 = builder.newInstance(IBond.class, c1, o);
        IBond b2 = builder.newInstance(IBond.class, o, c2);
        IBond b3 = builder.newInstance(IBond.class, c2, c3);

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
        assertTrue(intersection.contains(b2));
        assertTrue(intersection.contains(o));
        assertTrue(intersection.contains(c2));
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
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);
        container.addBond(new Bond(atom1, atom2, IBond.Order.SINGLE));
        container.addBond(new Bond(atom2, atom3, IBond.Order.SINGLE));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        for (IAtom atom : container.atoms()) {
            assertTrue(atom.getAtomTypeName() != CDKConstants.UNSET);
            assertTrue(atom.getHybridization() != CDKConstants.UNSET);
        }

        AtomContainerManipulator.clearAtomConfigurations(container);
        for (IAtom atom : container.atoms()) {
            assertTrue(atom.getAtomTypeName() == CDKConstants.UNSET);
            assertTrue(atom.getHybridization() == CDKConstants.UNSET);
        }
    }

    @Test
    public void atomicNumberIsNotCleared() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "O");
        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);
        container.addBond(new Bond(atom1, atom2, IBond.Order.SINGLE));
        container.addBond(new Bond(atom2, atom3, IBond.Order.SINGLE));

        AtomContainerManipulator.clearAtomConfigurations(container);
        for (IAtom atom : container.atoms()) {
            Assert.assertNotNull(atom.getAtomicNumber());
        }
    }

    @Test
    public void testGetMaxBondOrder() {
        Assert.assertEquals(Order.DOUBLE, AtomContainerManipulator.getMaximumBondOrder(ac));
    }

    @Test
    public void testGetSBE() {
        Assert.assertEquals(12, AtomContainerManipulator.getSingleBondEquivalentSum(ac));
    }

    @Test
    public void testGetTotalCharge() throws IOException, ClassNotFoundException, CDKException {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        double totalCharge = AtomContainerManipulator.getTotalCharge(container);

        Assert.assertEquals(1.0, totalCharge, 0.01);
    }

    /**
     * @cdk.bug 1254
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCountExplicitH_Null_IAtom() {
        AtomContainerManipulator.countExplicitHydrogens(null,
                DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class));
    }

    /**
     * @cdk.bug 1254
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCountExplicitH_IAtomContainer_Null() {
        AtomContainerManipulator.countExplicitHydrogens(
                DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class), null);
    }

    @Test
    public void testCountExplicitH() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        Assert.assertEquals(0, AtomContainerManipulator.countExplicitHydrogens(container, atom1));
        Assert.assertEquals(0, AtomContainerManipulator.countExplicitHydrogens(container, atom2));

        for (int i = 0; i < 3; i++) {
            IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
            container.addAtom(h);
            container.addBond(new Bond(atom1, h, Order.SINGLE));
        }
        Assert.assertEquals(3, AtomContainerManipulator.countExplicitHydrogens(container, atom1));
    }

    @Test
    public void testCountH() throws Exception {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        // no atom type perception, so implicit count is 0
        Assert.assertEquals(0, AtomContainerManipulator.countHydrogens(container, atom1));
        Assert.assertEquals(0, AtomContainerManipulator.countHydrogens(container, atom2));

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder ha = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
        ha.addImplicitHydrogens(container);

        Assert.assertEquals(3, AtomContainerManipulator.countHydrogens(container, atom1));
        Assert.assertEquals(2, AtomContainerManipulator.countHydrogens(container, atom2));

        for (int i = 0; i < 3; i++) {
            IAtom h = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "H");
            container.addAtom(h);
            container.addBond(new Bond(atom1, h, Order.SINGLE));
        }
        Assert.assertEquals(6, AtomContainerManipulator.countHydrogens(container, atom1));

    }

    /**
     * @cdk.bug 1254
     */
    @Test
    public void testGetImplicitHydrogenCount_unperceived() throws Exception {
        IAtomContainer container = TestMoleculeFactory.makeAdenine();
        Assert.assertEquals("Container has not been atom-typed - should have 0 implicit hydrogens", 0,
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
        IAtomContainer container = TestMoleculeFactory.makeAdenine();
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(container);
        CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance()).addImplicitHydrogens(container);
        Assert.assertEquals("Adenine should have 5 implicit hydrogens", 5,
                AtomContainerManipulator.getImplicitHydrogenCount(container));

    }

    @Test
    public void testReplaceAtom() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assert.assertEquals(atom3, container.getAtom(1));
    }

    @Test
    public void testReplaceAtom_lonePair() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));
        container.addLonePair(1);

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assert.assertEquals(atom3, container.getLonePair(0).getAtom());
    }

    @Test
    public void testReplaceAtom_singleElectron() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "C");
        atom1.setCharge(1.0);
        IAtom atom2 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "N");

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addBond(new Bond(atom1, atom2, Order.SINGLE));
        container.addSingleElectron(1);

        IAtom atom3 = DefaultChemObjectBuilder.getInstance().newInstance(IAtom.class, "Br");

        AtomContainerManipulator.replaceAtomByAtom(container, atom2, atom3);
        Assert.assertEquals(atom3, container.getSingleElectron(0).getAtom());
    }

    @Test
    public void testReplaceAtom_stereochemistry() throws Exception {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("N[C@H](CC)O");
        IAtom newAtom = bldr.newInstance(IAtom.class, "Cl");
        newAtom.setImplicitHydrogenCount(0);
        AtomContainerManipulator.replaceAtomByAtom(mol, mol.getAtom(0), newAtom);
        assertThat(SmilesGenerator.isomeric().create(mol), is("Cl[C@H](CC)O"));
    }

    @Test
    public void testGetHeavyAtoms_IAtomContainer() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer container = builder.newInstance(IAtomContainer.class);
        container.addAtom(builder.newInstance(IAtom.class, "C"));
        for (int i = 0; i < 4; i++)
            container.addAtom(builder.newInstance(IAtom.class, "H"));
        container.addAtom(builder.newInstance(IAtom.class, "O"));
        Assert.assertEquals(2, AtomContainerManipulator.getHeavyAtoms(container).size());
    }

    /**
     * Test removeHydrogensPreserveMultiplyBonded for B2H6, which contains two multiply bonded H.
     *
     */
    @Test
    public void testRemoveHydrogensPreserveMultiplyBonded() throws Exception {
        IAtomContainer borane = new AtomContainer();
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "B"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addAtom(borane.getBuilder().newInstance(IAtom.class, "H"));
        borane.addBond(0, 2, Order.SINGLE);
        borane.addBond(1, 2, Order.SINGLE);
        borane.addBond(2, 3, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(2, 4, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(3, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(4, 5, Order.SINGLE); // REALLY 3-CENTER-2-ELECTRON
        borane.addBond(5, 6, Order.SINGLE);
        borane.addBond(5, 7, Order.SINGLE);
        for (IAtom atom : borane.atoms())
            atom.setImplicitHydrogenCount(0);
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(borane);

        // Should be two connected Bs with H-count == 2 and two explicit Hs.
        Assert.assertEquals("incorrect atom count", 4, ac.getAtomCount());
        Assert.assertEquals("incorrect bond count", 4, ac.getBondCount());

        int b = 0;
        int h = 0;
        for (int i = 0; i < ac.getAtomCount(); i++) {
            final org.openscience.cdk.interfaces.IAtom atom = ac.getAtom(i);
            String sym = atom.getSymbol();
            if (sym.equals("B")) {
                // Each B has two explicit and two implicit H.
                b++;
                Assert.assertEquals("incorrect hydrogen count", 2, atom.getImplicitHydrogenCount().intValue());
                List<IAtom> nbs = ac.getConnectedAtomsList(atom);
                Assert.assertEquals("incorrect connected count", 2, nbs.size());
                Assert.assertEquals("incorrect bond", "H", ((IAtom) nbs.get(0)).getSymbol());
                Assert.assertEquals("incorrect bond", "H", ((IAtom) nbs.get(1)).getSymbol());
            } else if (sym.equals("H")) {
                h++;
            }
        }
        Assert.assertEquals("incorrect no. Bs", 2, b);
        Assert.assertEquals("incorrect no. Hs", 2, h);
    }

    @Test
    public void testCreateAnyAtomAnyBondAtomContainer_IAtomContainer() throws Exception {
        String smiles = "c1ccccc1";
        SmilesParser sp = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles(smiles);
        mol = AtomContainerManipulator.createAllCarbonAllSingleNonAromaticBondAtomContainer(mol);
        String smiles2 = "C1CCCCC1";
        IAtomContainer mol2 = sp.parseSmiles(smiles2);
        assertTrue(new UniversalIsomorphismTester().isIsomorph(mol, mol2));
    }

    @Test
    public void testAnonymise() throws Exception {

        IAtomContainer cyclohexane = TestMoleculeFactory.makeCyclohexane();

        cyclohexane.getAtom(0).setSymbol("O");
        cyclohexane.getAtom(2).setSymbol("O");
        cyclohexane.getAtom(1).setAtomTypeName("remove me");
        cyclohexane.getAtom(3).setFlag(CDKConstants.ISAROMATIC, true);
        cyclohexane.getAtom(4).setImplicitHydrogenCount(2);
        cyclohexane.getBond(0).setFlag(CDKConstants.SINGLE_OR_DOUBLE, true);
        cyclohexane.getBond(1).setFlag(CDKConstants.ISAROMATIC, true);

        IAtomContainer anonymous = AtomContainerManipulator.anonymise(cyclohexane);

        assertTrue(new UniversalIsomorphismTester().isIsomorph(anonymous, TestMoleculeFactory.makeCyclohexane()));

        assertThat(anonymous.getAtom(0).getSymbol(), is("C"));
        assertThat(anonymous.getAtom(2).getSymbol(), is("C"));
        assertNull(anonymous.getAtom(1).getAtomTypeName());
        assertThat(anonymous.getAtom(4).getImplicitHydrogenCount(), is(0));
        assertFalse(anonymous.getAtom(3).getFlag(CDKConstants.ISAROMATIC));

        assertFalse(anonymous.getBond(1).getFlag(CDKConstants.ISAROMATIC));
        assertFalse(anonymous.getBond(1).getFlag(CDKConstants.SINGLE_OR_DOUBLE));
    }

    @Test
    public void skeleton() throws Exception {

        IAtomContainer adenine = TestMoleculeFactory.makeAdenine();
        IAtomContainer skeleton = AtomContainerManipulator.skeleton(adenine);

        assertThat(skeleton, is(not(sameInstance(adenine))));

        for (IBond bond : skeleton.bonds())
            assertThat(bond.getOrder(), is(IBond.Order.SINGLE));

        for (int i = 0; i < skeleton.getAtomCount(); i++) {
            assertThat(skeleton.getAtom(i).getSymbol(), is(adenine.getAtom(i).getSymbol()));
        }
    }

    /**
     * https://sourceforge.net/p/cdk/mailman/message/20639023/
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

        Map<IAtom,Double> exactMass = new HashMap<>();

        Isotopes.getInstance().configureAtoms(ac);

        for (IAtom atom : ac.atoms()) {
            exactMass.put(atom, atom.getExactMass());
        }

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ac);

        for (IAtom atom : ac.atoms()) {
            Double expected = exactMass.get(atom);
            Double actual   = atom.getExactMass();
            if (expected == null)
                assertNull(actual);
            else
                org.hamcrest.MatcherAssert.assertThat(actual,
                                  is(closeTo(expected, 0.001)));
        }
    }

    @Test
    public void setSingleOrDoubleFlags() {
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
     * - note now molecular hydrogen is preserved to avoid information loss.
     *
     * @cdk.bug 2366528
     */
    @Test
    public void testRemoveHydrogensFromMolecularHydrogen() {
        IAtomContainer mol = new AtomContainer(); // molecular hydrogen
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addBond(0, 1, IBond.Order.SINGLE);

        Assert.assertEquals(2, mol.getAtomCount());
        IAtomContainer ac = AtomContainerManipulator.removeHydrogens(mol);
        Assert.assertEquals(2, ac.getAtomCount());
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

    @Test
    public void convertExplicitHydrogen_chiralCarbon() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles("C[C@H](CC)O");

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(m);

        assertThat(SmilesGenerator.isomeric().create(m), is("C([C@](C(C([H])([H])[H])([H])[H])(O[H])[H])([H])([H])[H]"));
    }

    @Test
    public void convertExplicitHydrogen_sulfoxide() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles("[S@](=O)(C)CC");

        AtomContainerManipulator.convertImplicitToExplicitHydrogens(m);

        assertThat(SmilesGenerator.isomeric().create(m), is("[S@](=O)(C([H])([H])[H])C(C([H])([H])[H])([H])[H]"));
    }

    @Test
    public void removeHydrogens_chiralCarbon1() throws Exception {
        assertRemoveH("C[C@@](CC)([H])O", "C[C@H](CC)O");
    }

    @Test
    public void removeHydrogens_chiralCarbon2() throws Exception {
        assertRemoveH("C[C@@]([H])(CC)O", "C[C@@H](CC)O");
    }

    @Test
    public void removeHydrogens_chiralCarbon3() throws Exception {
        assertRemoveH("C[C@@](CC)(O)[H]", "C[C@@H](CC)O");
    }

    @Test
    public void removeHydrogens_chiralCarbon4() throws Exception {
        assertRemoveH("[H][C@@](C)(CC)O", "[C@@H](C)(CC)O");
    }

    @Test
    public void removeHydrogens_db_trans1() throws Exception {
        assertRemoveH("C/C([H])=C([H])/C", "C/C=C/C");
        assertRemoveH("C\\C([H])=C([H])\\C", "C/C=C/C");
    }

    @Test
    public void removeHydrogens_db_cis1() throws Exception {
        assertRemoveH("C/C([H])=C([H])\\C", "C/C=C\\C");
        assertRemoveH("C\\C([H])=C([H])/C", "C/C=C\\C");
    }

    @Test
    public void removeHydrogens_db_trans2() throws Exception {
        assertRemoveH("CC(/[H])=C([H])/C", "C/C=C/C");
    }

    @Test
    public void removeHydrogens_db_cis2() throws Exception {
        assertRemoveH("CC(\\[H])=C([H])/C", "C/C=C\\C");
    }

    @Test
    public void removeHydrogens_db_trans3() throws Exception {
        assertRemoveH("CC(/[H])=C(\\[H])C", "C/C=C/C");
    }

    @Test
    public void removeHydrogens_db_cis3() throws Exception {
        assertRemoveH("CC(\\[H])=C(\\[H])C", "C/C=C\\C");
    }

    // hydrogen isotopes should not be removed
    @Test
    public void removeHydrogens_isotopes() throws Exception {
        assertRemoveH("C([H])([2H])([3H])[H]", "C([2H])[3H]");
    }

    // hydrogens with charge should not be removed
    @Test
    public void removeHydrogens_ions() throws Exception {
        assertRemoveH("C([H])([H+])([H-])[H]", "C([H+])[H-]");
    }

    @Test
    public void removeHydrogens_molecularH() throws Exception {
        assertRemoveH("[H][H]", "[H][H]");
        assertRemoveH("[HH]", "[H][H]");
    }

    @Test
    public void testSgroupSuppressionSRU() throws Exception {
        assertRemoveH("CCC([H])CC |Sg:n:1,2,3,4:n:ht|",
                      "CCCCC |Sg:n:1,2,3:n:ht|");
    }

    @Test
    public void testSgroupSuppressionSRUUpdated() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("CCC([H])CC |Sg:n:1,2,3,4:n:ht|");
        AtomContainerManipulator.suppressHydrogens(mol);
        Collection<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        assertNotNull(sgroups);
        assertThat(sgroups.size(), is(1));
        Sgroup sgroup = sgroups.iterator().next();
        assertThat(sgroup.getAtoms().size(), is(3));
    }

    @Test
    public void testSgroupSuppressionPositionalVariation() throws Exception {
        assertRemoveH("*[H].C1=CC=CC=C1 |m:0:2.3.4|",
                      "*[H].C1=CC=CC=C1 |m:0:2.3.4|");
    }

    @Test
    public void testSgroupSuppressionSRUCrossingBond() throws Exception {
        assertRemoveH("CCC[H] |Sg:n:2:n:ht|",
                      "CCC[H] |Sg:n:2:n:ht|");
    }

    @Test
    public void molecularWeight() throws InvalidSmilesException, IOException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[13CH4]CO");
        double molecularWeight = AtomContainerManipulator.getMolecularWeight(mol);
        double naturalExactMass = AtomContainerManipulator.getNaturalExactMass(mol);
        Isotopes isotopes = Isotopes.getInstance();
        for (IAtom atom : mol.atoms()) {
            if (atom.getMassNumber() == null)
                atom.setExactMass(isotopes.getMajorIsotope(atom.getAtomicNumber())
                                          .getExactMass());
            else
                isotopes.configure(atom);
        }
        double exactMass = AtomContainerManipulator.getTotalExactMass(mol);
        assertThat(molecularWeight, closeTo(48.069, 0.001));
        assertThat(naturalExactMass, closeTo(47.076, 0.001));
        assertThat(exactMass, closeTo(48.053, 0.001));
    }

    @Test
    public void removeBondStereo() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[2H]/C=C/[H]");
        AtomContainerManipulator.suppressHydrogens(mol);
        assertThat(mol.stereoElements().iterator().hasNext(),
                   CoreMatchers.is(false));
    }

    @Test
    public void keep1Hisotopes() throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[2H]/C=C/[1H]");
        AtomContainerManipulator.suppressHydrogens(mol);
        assertThat(mol.getAtomCount(), is(4));
    }

    // util for testing hydrogen removal using SMILES
    static void assertRemoveH(String smiIn, String smiExp) throws Exception {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer m = smipar.parseSmiles(smiIn);

        String smiAct = new SmilesGenerator().create(AtomContainerManipulator.removeHydrogens(m));

        assertThat(smiAct, is(smiExp));
    }

    @Test public void getMassC6Br6() throws InvalidSmilesException {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        SmilesParser smipar = new SmilesParser(bldr);
        IAtomContainer mol = smipar.parseSmiles("Brc1c(Br)c(Br)c(Br)c(Br)c1Br");
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(551.485, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(551.485, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(545.510, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(551.503, 0.001));
    }

    @Test public void getMassCranbin() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol =
                MolecularFormulaManipulator.getAtomContainer("C202H315N55O64S6",
                                                              bldr);
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(4727.140, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(4729.147, 0.001));
    }

    @Test public void getMassCranbinSpecIsotopes() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol =
                MolecularFormulaManipulator.getAtomContainer("[12]C200[13]C2[1]H315[14]N55[16]O64[32]S6",
                                                             bldr);
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(4729.147, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(4729.147, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(4729.147, 0.001));
    }

    @Test public void getMassCranbinMixedSpecIsotopes() {
        IChemObjectBuilder bldr = SilentChemObjectBuilder.getInstance();
        IAtomContainer mol =
                MolecularFormulaManipulator.getAtomContainer("C200[13]C2H315N55O64S6",
                                                             bldr);
        assertThat(AtomContainerManipulator.getMass(mol, MolWeight),
                   closeTo(4732.382, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MolWeightIgnoreSpecified),
                   closeTo(4730.397, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MonoIsotopic),
                   closeTo(4729.147, 0.001));
        assertThat(AtomContainerManipulator.getMass(mol, MostAbundant),
                   closeTo(4731.154, 0.001));
    }
}
