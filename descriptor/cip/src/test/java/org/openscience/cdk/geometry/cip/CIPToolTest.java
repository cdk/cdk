/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
 */
package org.openscience.cdk.geometry.cip;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.geometry.cip.CIPTool.CIP_CHIRALITY;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.silent.ChemFile;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.stereo.StereoTool;
import org.openscience.cdk.stereo.TetrahedralChirality;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @cdk.module test-cip
 */
public class CIPToolTest extends CDKTestCase {

    static SmilesParser   smiles = new SmilesParser(SilentChemObjectBuilder.getInstance());
    static IAtomContainer molecule;
    static ILigand[]      ligands;

    @BeforeClass
    public static void setup() throws Exception {
        molecule = smiles.parseSmiles("ClC(Br)(I)[H]");
        VisitedAtoms visitedAtoms = new VisitedAtoms();
        ILigand ligand1 = new Ligand(molecule, visitedAtoms, molecule.getAtom(1), molecule.getAtom(4));
        ILigand ligand2 = new Ligand(molecule, visitedAtoms, molecule.getAtom(1), molecule.getAtom(3));
        ILigand ligand3 = new Ligand(molecule, visitedAtoms, molecule.getAtom(1), molecule.getAtom(2));
        ILigand ligand4 = new Ligand(molecule, visitedAtoms, molecule.getAtom(1), molecule.getAtom(0));
        ligands = new ILigand[]{ligand1, ligand2, ligand3, ligand4};
    }

    @Test
    public void testCheckIfAllLigandsAreDifferent() {
        Assert.assertTrue(CIPTool.checkIfAllLigandsAreDifferent(ligands));
    }

    @Test
    public void testCheckIfAllLigandsAreDifferent_False() {
        ILigand[] sameLigands = new ILigand[]{ligands[0], ligands[0], ligands[1], ligands[2]};
        Assert.assertFalse(CIPTool.checkIfAllLigandsAreDifferent(sameLigands));
    }

    @Test
    public void testOrder() {
        ILigand[] ligandCopy = CIPTool.order(ligands);
        Assert.assertEquals("H", ligandCopy[0].getLigandAtom().getSymbol());
        Assert.assertEquals("Cl", ligandCopy[1].getLigandAtom().getSymbol());
        Assert.assertEquals("Br", ligandCopy[2].getLigandAtom().getSymbol());
        Assert.assertEquals("I", ligandCopy[3].getLigandAtom().getSymbol());
    }

    @Test
    public void testGetCIPChirality() {
        LigancyFourChirality chirality = new LigancyFourChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_Anti() {
        ILigand[] antiLigands = new ILigand[]{ligands[0], ligands[1], ligands[3], ligands[2]};

        LigancyFourChirality chirality = new LigancyFourChirality(molecule.getAtom(1), antiLigands,
                Stereo.ANTI_CLOCKWISE);
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_ILigancyFourChirality() {
        List<IAtom> ligandAtoms = new ArrayList<IAtom>();
        for (ILigand ligand : ligands)
            ligandAtoms.add(ligand.getLigandAtom());
        ITetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1),
                (IAtom[]) ligandAtoms.toArray(new IAtom[]{}), Stereo.CLOCKWISE);
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(molecule, chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_Anti_ILigancyFourChirality() {
        ILigand[] antiLigands = new ILigand[]{ligands[0], ligands[1], ligands[3], ligands[2]};
        List<IAtom> ligandAtoms = new ArrayList<IAtom>();
        for (ILigand ligand : antiLigands)
            ligandAtoms.add(ligand.getLigandAtom());

        ITetrahedralChirality chirality = new TetrahedralChirality(molecule.getAtom(1),
                (IAtom[]) ligandAtoms.toArray(new IAtom[]{}), Stereo.ANTI_CLOCKWISE);
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(molecule, chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_DoubleBond_Together() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles("CCC(C)=C(C)CC");
        CIP_CHIRALITY label = CIPTool.getCIPChirality(
                container,
                new DoubleBondStereochemistry(container.getBond(container.getAtom(2), container.getAtom(4)),
                        new IBond[]{container.getBond(container.getAtom(2), container.getAtom(3)),
                                container.getBond(container.getAtom(4), container.getAtom(5))},
                        IDoubleBondStereochemistry.Conformation.TOGETHER));
        assertThat(label, is(CIPTool.CIP_CHIRALITY.Z));
    }

    @Test
    public void testGetCIPChirality_DoubleBond_Opposite() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles("CCC(C)=C(C)CC");
        CIP_CHIRALITY label = CIPTool.getCIPChirality(
                container,
                new DoubleBondStereochemistry(container.getBond(container.getAtom(2), container.getAtom(4)),
                        new IBond[]{container.getBond(container.getAtom(2), container.getAtom(3)),
                                container.getBond(container.getAtom(4), container.getAtom(6))},
                        IDoubleBondStereochemistry.Conformation.OPPOSITE));
        assertThat(label, is(CIPTool.CIP_CHIRALITY.Z));
    }

    @Test
    public void label() throws Exception {
        IAtomContainer container = new SmilesParser(SilentChemObjectBuilder.getInstance())
                .parseSmiles("C/C=C/[C@@H](C)C(/C)=C(/C)C[C@H](C)O");
        CIPTool.label(container);
        assertThat(container.getAtom(3).getProperty(CDKConstants.CIP_DESCRIPTOR, String.class), CoreMatchers.is("R"));
        assertThat(container.getAtom(10).getProperty(CDKConstants.CIP_DESCRIPTOR, String.class), CoreMatchers.is("S"));
        assertThat(
                container.getBond(container.getAtom(1), container.getAtom(2)).getProperty(CDKConstants.CIP_DESCRIPTOR,
                        String.class), CoreMatchers.is("E"));
        assertThat(
                container.getBond(container.getAtom(5), container.getAtom(7)).getProperty(CDKConstants.CIP_DESCRIPTOR,
                        String.class), CoreMatchers.is("Z"));
    }

    @Test
    public void testDefineLigancyFourChirality() {
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(molecule, 1, 0, 2, 3, 4,
                Stereo.ANTI_CLOCKWISE);
        Assert.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
        Assert.assertEquals(Stereo.ANTI_CLOCKWISE, chirality.getStereo());
        ILigand[] ligands = chirality.getLigands();
        Assert.assertEquals(molecule, ligands[0].getAtomContainer());
        Assert.assertEquals(molecule.getAtom(0), ligands[0].getLigandAtom());
        Assert.assertEquals(molecule.getAtom(1), ligands[0].getCentralAtom());
        Assert.assertEquals(molecule, ligands[1].getAtomContainer());
        Assert.assertEquals(molecule.getAtom(2), ligands[1].getLigandAtom());
        Assert.assertEquals(molecule.getAtom(1), ligands[1].getCentralAtom());
        Assert.assertEquals(molecule, ligands[2].getAtomContainer());
        Assert.assertEquals(molecule.getAtom(3), ligands[2].getLigandAtom());
        Assert.assertEquals(molecule.getAtom(1), ligands[2].getCentralAtom());
        Assert.assertEquals(molecule, ligands[3].getAtomContainer());
        Assert.assertEquals(molecule.getAtom(4), ligands[3].getLigandAtom());
        Assert.assertEquals(molecule.getAtom(1), ligands[3].getCentralAtom());
    }

    @Test
    public void testDefineLigand() {
        ILigand ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 1, 2);
        Assert.assertEquals(molecule, ligand.getAtomContainer());
        Assert.assertEquals(molecule.getAtom(1), ligand.getCentralAtom());
        Assert.assertEquals(molecule.getAtom(2), ligand.getLigandAtom());
    }

    /**
     * Tests if it returns the right number of ligands, for single bonds only.
     */
    @Test
    public void testGetLigandLigands() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(C)C(CC)(C(C)(C)C)[H]");
        ILigand ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 1);
        ILigand[] sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(2, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 4);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(1, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 6);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(3, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 10);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(0, sideChains.length);
    }

    /**
     * Tests if it returns the right number of ligands, for single bonds only.
     */
    @Test
    public void testGetLigandLigands_VisitedTracking() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(C)C(CC)(C(C)(C)C)[H]");
        ILigand ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 1);
        ILigand[] sideChains = CIPTool.getLigandLigands(ligand);
        for (ILigand ligand2 : sideChains) {
            Assert.assertNotSame(ligand2.getVisitedAtoms(), ligand.getVisitedAtoms());
        }
    }

    /**
     * Tests if it returns the right number of ligands, for double bonds.
     */
    @Test
    public void testGetLigandLigands_DoubleTriple() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(C)C(C#N)(C(=C)C)[H]");
        ILigand ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 1);
        ILigand[] sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(2, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 4);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(3, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 6);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(3, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, 9);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(0, sideChains.length);
    }

    @Test
    public void testDefineLigand_ImplicitHydrogen() throws Exception {
        IAtomContainer molecule = smiles.parseSmiles("CC(C)C(C#N)(C(=C)C)");
        ILigand ligand = CIPTool.defineLigand(molecule, new VisitedAtoms(), 3, CIPTool.HYDROGEN);
        Assert.assertTrue(ligand instanceof ImplicitHydrogenLigand);
    }

    @Test
    //(timeout=5000)
    public void testTermination() {
        int ringSize = 7;
        IAtomContainer ring = new AtomContainer();
        for (int i = 0; i < ringSize; i++) {
            ring.addAtom(new Atom("C"));
        }
        for (int j = 0; j < ringSize - 1; j++) {
            ring.addBond(j, j + 1, IBond.Order.SINGLE);
        }
        ring.addBond(ringSize - 1, 0, IBond.Order.SINGLE);

        ring.addAtom(new Atom("Cl"));
        ring.addAtom(new Atom("F"));
        ring.addBond(0, ringSize, IBond.Order.SINGLE);
        ring.addBond(0, ringSize + 1, IBond.Order.SINGLE);
        ring.addAtom(new Atom("O"));
        ring.addBond(1, ringSize + 2, IBond.Order.SINGLE);
        IAtom[] atoms = new IAtom[]{ring.getAtom(ringSize), ring.getAtom(ringSize + 1), ring.getAtom(ringSize - 1),
                ring.getAtom(1)};
        ITetrahedralChirality stereoCenter = new TetrahedralChirality(ring.getAtom(0), atoms, Stereo.ANTI_CLOCKWISE);
        ring.addStereoElement(stereoCenter);
        SmilesGenerator generator = new SmilesGenerator();
        CIPTool.getCIPChirality(ring, stereoCenter);
    }

    @Test
    public void testOla28() throws Exception {
        String filename = "data/cml/mol28.cml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        CMLReader reader = new CMLReader(ins);
        IChemFile file = reader.read(new ChemFile());
        reader.close();
        IAtomContainer mol = ChemFileManipulator.getAllAtomContainers(file).get(0);

        for (IAtom atom : mol.atoms()) {
            List<IAtom> neighbors = mol.getConnectedAtomsList(atom);
            if (neighbors.size() == 4) {
                System.out.println("Atom " + mol.getAtomNumber(atom));
                Stereo stereo = StereoTool.getStereo(neighbors.get(0), neighbors.get(1), neighbors.get(2),
                        neighbors.get(3));
                ITetrahedralChirality stereoCenter = new TetrahedralChirality(mol.getAtom(0),
                        neighbors.toArray(new IAtom[]{}), stereo);
                CIP_CHIRALITY chirality = CIPTool.getCIPChirality(mol, stereoCenter);
                System.out.println("chirality: " + chirality);
            }
        }
    }

    /**
     * @cdk.inchi InChI=1S/C27H43FO6/c1-23(2,28)9-8-22(32)26(5,33)21-7-11-27(34)16-12-18(29)17-13-19(30)20(31)14-24(17,3)15(16)6-10-25(21,27)4/h12,15,17,19-22,30-34H,6-11,13-14H2,1-5H3/t15-,17-,19+,20-,21-,22+,24+,25+,26+,27+/m0/s1
     */
    @Test
    public void testSteroid() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = builder.newInstance(IAtomContainer.class);
        IAtom a1 = builder.newInstance(IAtom.class, "F");
        a1.setFormalCharge(0);
        a1.setPoint3d(new Point3d(7.0124, 2.5853, -0.9016));
        mol.addAtom(a1);
        IAtom a2 = builder.newInstance(IAtom.class, "O");
        a2.setFormalCharge(0);
        a2.setPoint3d(new Point3d(-0.5682, -0.2861, 2.1733));
        mol.addAtom(a2);
        IAtom a3 = builder.newInstance(IAtom.class, "O");
        a3.setFormalCharge(0);
        a3.setPoint3d(new Point3d(2.2826, -2.9598, -0.5754));
        mol.addAtom(a3);
        IAtom a4 = builder.newInstance(IAtom.class, "O");
        a4.setFormalCharge(0);
        a4.setPoint3d(new Point3d(-6.6808, -1.9515, 0.4596));
        mol.addAtom(a4);
        IAtom a5 = builder.newInstance(IAtom.class, "O");
        a5.setFormalCharge(0);
        a5.setPoint3d(new Point3d(4.2201, -1.7701, -1.7827));
        mol.addAtom(a5);
        IAtom a6 = builder.newInstance(IAtom.class, "O");
        a6.setFormalCharge(0);
        a6.setPoint3d(new Point3d(-7.0886, 0.761, 0.0885));
        mol.addAtom(a6);
        IAtom a7 = builder.newInstance(IAtom.class, "O");
        a7.setFormalCharge(0);
        a7.setPoint3d(new Point3d(-3.3025, 3.5973, -0.657));
        mol.addAtom(a7);
        IAtom a8 = builder.newInstance(IAtom.class, "C");
        a8.setFormalCharge(0);
        a8.setPoint3d(new Point3d(0.4862, -0.9146, 0.0574));
        mol.addAtom(a8);
        IAtom a9 = builder.newInstance(IAtom.class, "C");
        a9.setFormalCharge(0);
        a9.setPoint3d(new Point3d(-0.1943, 0.2177, 0.8706));
        mol.addAtom(a9);
        IAtom a10 = builder.newInstance(IAtom.class, "C");
        a10.setFormalCharge(0);
        a10.setPoint3d(new Point3d(1.7596, -1.1559, 0.9089));
        mol.addAtom(a10);
        IAtom a11 = builder.newInstance(IAtom.class, "C");
        a11.setFormalCharge(0);
        a11.setPoint3d(new Point3d(-2.4826, -0.4593, -0.073));
        mol.addAtom(a11);
        IAtom a12 = builder.newInstance(IAtom.class, "C");
        a12.setFormalCharge(0);
        a12.setPoint3d(new Point3d(-3.7166, 0.0102, -0.941));
        mol.addAtom(a12);
        IAtom a13 = builder.newInstance(IAtom.class, "C");
        a13.setFormalCharge(0);
        a13.setPoint3d(new Point3d(-0.4659, -2.1213, 0.0044));
        mol.addAtom(a13);
        IAtom a14 = builder.newInstance(IAtom.class, "C");
        a14.setFormalCharge(0);
        a14.setPoint3d(new Point3d(-1.485, 0.6715, 0.2231));
        mol.addAtom(a14);
        IAtom a15 = builder.newInstance(IAtom.class, "C");
        a15.setFormalCharge(0);
        a15.setPoint3d(new Point3d(0.9729, 1.1842, 1.122));
        mol.addAtom(a15);
        IAtom a16 = builder.newInstance(IAtom.class, "C");
        a16.setFormalCharge(0);
        a16.setPoint3d(new Point3d(2.1976, 0.2666, 1.3272));
        mol.addAtom(a16);
        IAtom a17 = builder.newInstance(IAtom.class, "C");
        a17.setFormalCharge(0);
        a17.setPoint3d(new Point3d(-1.8034, -1.7401, -0.6501));
        mol.addAtom(a17);
        IAtom a18 = builder.newInstance(IAtom.class, "C");
        a18.setFormalCharge(0);
        a18.setPoint3d(new Point3d(-4.2265, 1.3894, -0.4395));
        mol.addAtom(a18);
        IAtom a19 = builder.newInstance(IAtom.class, "C");
        a19.setFormalCharge(0);
        a19.setPoint3d(new Point3d(2.8802, -1.9484, 0.2485));
        mol.addAtom(a19);
        IAtom a20 = builder.newInstance(IAtom.class, "C");
        a20.setFormalCharge(0);
        a20.setPoint3d(new Point3d(0.862, -0.4809, -1.3862));
        mol.addAtom(a20);
        IAtom a21 = builder.newInstance(IAtom.class, "C");
        a21.setFormalCharge(0);
        a21.setPoint3d(new Point3d(-4.8907, -1.0078, -0.8633));
        mol.addAtom(a21);
        IAtom a22 = builder.newInstance(IAtom.class, "C");
        a22.setFormalCharge(0);
        a22.setPoint3d(new Point3d(-1.7576, 1.9697, 0.0241));
        mol.addAtom(a22);
        IAtom a23 = builder.newInstance(IAtom.class, "C");
        a23.setFormalCharge(0);
        a23.setPoint3d(new Point3d(-4.9064, 1.3293, 0.9405));
        mol.addAtom(a23);
        IAtom a24 = builder.newInstance(IAtom.class, "C");
        a24.setFormalCharge(0);
        a24.setPoint3d(new Point3d(-3.339, 0.1527, -2.4408));
        mol.addAtom(a24);
        IAtom a25 = builder.newInstance(IAtom.class, "C");
        a25.setFormalCharge(0);
        a25.setPoint3d(new Point3d(-3.1038, 2.4096, -0.4054));
        mol.addAtom(a25);
        IAtom a26 = builder.newInstance(IAtom.class, "C");
        a26.setFormalCharge(0);
        a26.setPoint3d(new Point3d(-5.5668, -1.0627, 0.5104));
        mol.addAtom(a26);
        IAtom a27 = builder.newInstance(IAtom.class, "C");
        a27.setFormalCharge(0);
        a27.setPoint3d(new Point3d(3.7564, -1.0338, -0.652));
        mol.addAtom(a27);
        IAtom a28 = builder.newInstance(IAtom.class, "C");
        a28.setFormalCharge(0);
        a28.setPoint3d(new Point3d(-6.0498, 0.3154, 0.9627));
        mol.addAtom(a28);
        IAtom a29 = builder.newInstance(IAtom.class, "C");
        a29.setFormalCharge(0);
        a29.setPoint3d(new Point3d(3.6914, -2.6828, 1.3258));
        mol.addAtom(a29);
        IAtom a30 = builder.newInstance(IAtom.class, "C");
        a30.setFormalCharge(0);
        a30.setPoint3d(new Point3d(4.9535, -0.3812, 0.0661));
        mol.addAtom(a30);
        IAtom a31 = builder.newInstance(IAtom.class, "C");
        a31.setFormalCharge(0);
        a31.setPoint3d(new Point3d(5.4727, 0.8461, -0.696));
        mol.addAtom(a31);
        IAtom a32 = builder.newInstance(IAtom.class, "C");
        a32.setFormalCharge(0);
        a32.setPoint3d(new Point3d(6.7079, 1.5265, -0.0844));
        mol.addAtom(a32);
        IAtom a33 = builder.newInstance(IAtom.class, "C");
        a33.setFormalCharge(0);
        a33.setPoint3d(new Point3d(6.4387, 2.104, 1.3013));
        mol.addAtom(a33);
        IAtom a34 = builder.newInstance(IAtom.class, "C");
        a34.setFormalCharge(0);
        a34.setPoint3d(new Point3d(7.9342, 0.6197, -0.0661));
        mol.addAtom(a34);
        IAtom a35 = builder.newInstance(IAtom.class, "H");
        a35.setFormalCharge(0);
        a35.setPoint3d(new Point3d(1.4474, -1.6941, 1.8161));
        mol.addAtom(a35);
        IAtom a36 = builder.newInstance(IAtom.class, "H");
        a36.setFormalCharge(0);
        a36.setPoint3d(new Point3d(-2.8575, -0.7521, 0.9166));
        mol.addAtom(a36);
        IAtom a37 = builder.newInstance(IAtom.class, "H");
        a37.setFormalCharge(0);
        a37.setPoint3d(new Point3d(-0.0529, -2.952, -0.5733));
        mol.addAtom(a37);
        IAtom a38 = builder.newInstance(IAtom.class, "H");
        a38.setFormalCharge(0);
        a38.setPoint3d(new Point3d(-0.6583, -2.5149, 1.01));
        mol.addAtom(a38);
        IAtom a39 = builder.newInstance(IAtom.class, "H");
        a39.setFormalCharge(0);
        a39.setPoint3d(new Point3d(1.1462, 1.8516, 0.2703));
        mol.addAtom(a39);
        IAtom a40 = builder.newInstance(IAtom.class, "H");
        a40.setFormalCharge(0);
        a40.setPoint3d(new Point3d(0.8186, 1.8095, 2.0087));
        mol.addAtom(a40);
        IAtom a41 = builder.newInstance(IAtom.class, "H");
        a41.setFormalCharge(0);
        a41.setPoint3d(new Point3d(2.5044, 0.2648, 2.3797));
        mol.addAtom(a41);
        IAtom a42 = builder.newInstance(IAtom.class, "H");
        a42.setFormalCharge(0);
        a42.setPoint3d(new Point3d(2.9822, 0.7582, 0.7671));
        mol.addAtom(a42);
        IAtom a43 = builder.newInstance(IAtom.class, "H");
        a43.setFormalCharge(0);
        a43.setPoint3d(new Point3d(-2.4854, -2.5906, -0.5319));
        mol.addAtom(a43);
        IAtom a44 = builder.newInstance(IAtom.class, "H");
        a44.setFormalCharge(0);
        a44.setPoint3d(new Point3d(-1.6353, -1.6475, -1.7261));
        mol.addAtom(a44);
        IAtom a45 = builder.newInstance(IAtom.class, "H");
        a45.setFormalCharge(0);
        a45.setPoint3d(new Point3d(-4.9616, 1.7691, -1.1638));
        mol.addAtom(a45);
        IAtom a46 = builder.newInstance(IAtom.class, "H");
        a46.setFormalCharge(0);
        a46.setPoint3d(new Point3d(-0.0354, -0.2446, -1.9684));
        mol.addAtom(a46);
        IAtom a47 = builder.newInstance(IAtom.class, "H");
        a47.setFormalCharge(0);
        a47.setPoint3d(new Point3d(1.3691, -1.2574, -1.9625));
        mol.addAtom(a47);
        IAtom a48 = builder.newInstance(IAtom.class, "H");
        a48.setFormalCharge(0);
        a48.setPoint3d(new Point3d(1.4296, 0.4511, -1.4252));
        mol.addAtom(a48);
        IAtom a49 = builder.newInstance(IAtom.class, "H");
        a49.setFormalCharge(0);
        a49.setPoint3d(new Point3d(-4.5596, -2.0138, -1.147));
        mol.addAtom(a49);
        IAtom a50 = builder.newInstance(IAtom.class, "H");
        a50.setFormalCharge(0);
        a50.setPoint3d(new Point3d(-5.6512, -0.7511, -1.6149));
        mol.addAtom(a50);
        IAtom a51 = builder.newInstance(IAtom.class, "H");
        a51.setFormalCharge(0);
        a51.setPoint3d(new Point3d(-1.0464, 2.7559, 0.2488));
        mol.addAtom(a51);
        IAtom a52 = builder.newInstance(IAtom.class, "H");
        a52.setFormalCharge(0);
        a52.setPoint3d(new Point3d(-4.1786, 1.0807, 1.7222));
        mol.addAtom(a52);
        IAtom a53 = builder.newInstance(IAtom.class, "H");
        a53.setFormalCharge(0);
        a53.setPoint3d(new Point3d(-5.2947, 2.3265, 1.1848));
        mol.addAtom(a53);
        IAtom a54 = builder.newInstance(IAtom.class, "H");
        a54.setFormalCharge(0);
        a54.setPoint3d(new Point3d(-2.421, 0.7311, -2.5838));
        mol.addAtom(a54);
        IAtom a55 = builder.newInstance(IAtom.class, "H");
        a55.setFormalCharge(0);
        a55.setPoint3d(new Point3d(-3.2008, -0.8224, -2.9194));
        mol.addAtom(a55);
        IAtom a56 = builder.newInstance(IAtom.class, "H");
        a56.setFormalCharge(0);
        a56.setPoint3d(new Point3d(-4.1353, 0.658, -3.0004));
        mol.addAtom(a56);
        IAtom a57 = builder.newInstance(IAtom.class, "H");
        a57.setFormalCharge(0);
        a57.setPoint3d(new Point3d(-4.8758, -1.4669, 1.2574));
        mol.addAtom(a57);
        IAtom a58 = builder.newInstance(IAtom.class, "H");
        a58.setFormalCharge(0);
        a58.setPoint3d(new Point3d(-0.9312, 0.4562, 2.6867));
        mol.addAtom(a58);
        IAtom a59 = builder.newInstance(IAtom.class, "H");
        a59.setFormalCharge(0);
        a59.setPoint3d(new Point3d(3.1882, -0.2287, -1.0977));
        mol.addAtom(a59);
        IAtom a60 = builder.newInstance(IAtom.class, "H");
        a60.setFormalCharge(0);
        a60.setPoint3d(new Point3d(-6.4869, 0.2469, 1.965));
        mol.addAtom(a60);
        IAtom a61 = builder.newInstance(IAtom.class, "H");
        a61.setFormalCharge(0);
        a61.setPoint3d(new Point3d(4.102, -2.0082, 2.0826));
        mol.addAtom(a61);
        IAtom a62 = builder.newInstance(IAtom.class, "H");
        a62.setFormalCharge(0);
        a62.setPoint3d(new Point3d(4.5162, -3.2434, 0.8708));
        mol.addAtom(a62);
        IAtom a63 = builder.newInstance(IAtom.class, "H");
        a63.setFormalCharge(0);
        a63.setPoint3d(new Point3d(3.0747, -3.4251, 1.8469));
        mol.addAtom(a63);
        IAtom a64 = builder.newInstance(IAtom.class, "H");
        a64.setFormalCharge(0);
        a64.setPoint3d(new Point3d(1.8961, -3.6368, 0.0058));
        mol.addAtom(a64);
        IAtom a65 = builder.newInstance(IAtom.class, "H");
        a65.setFormalCharge(0);
        a65.setPoint3d(new Point3d(5.7631, -1.1204, 0.1084));
        mol.addAtom(a65);
        IAtom a66 = builder.newInstance(IAtom.class, "H");
        a66.setFormalCharge(0);
        a66.setPoint3d(new Point3d(4.743, -0.1036, 1.1001));
        mol.addAtom(a66);
        IAtom a67 = builder.newInstance(IAtom.class, "H");
        a67.setFormalCharge(0);
        a67.setPoint3d(new Point3d(-6.3482, -2.8223, 0.1828));
        mol.addAtom(a67);
        IAtom a68 = builder.newInstance(IAtom.class, "H");
        a68.setFormalCharge(0);
        a68.setPoint3d(new Point3d(4.6594, -1.153, -2.3908));
        mol.addAtom(a68);
        IAtom a69 = builder.newInstance(IAtom.class, "H");
        a69.setFormalCharge(0);
        a69.setPoint3d(new Point3d(-7.3836, 1.6319, 0.4047));
        mol.addAtom(a69);
        IAtom a70 = builder.newInstance(IAtom.class, "H");
        a70.setFormalCharge(0);
        a70.setPoint3d(new Point3d(5.716, 0.5715, -1.7297));
        mol.addAtom(a70);
        IAtom a71 = builder.newInstance(IAtom.class, "H");
        a71.setFormalCharge(0);
        a71.setPoint3d(new Point3d(4.6721, 1.5926, -0.7787));
        mol.addAtom(a71);
        IBond b1 = builder.newInstance(IBond.class, a1, a32, IBond.Order.SINGLE);
        mol.addBond(b1);
        IBond b2 = builder.newInstance(IBond.class, a2, a9, IBond.Order.SINGLE);
        mol.addBond(b2);
        IBond b3 = builder.newInstance(IBond.class, a2, a58, IBond.Order.SINGLE);
        mol.addBond(b3);
        IBond b4 = builder.newInstance(IBond.class, a3, a19, IBond.Order.SINGLE);
        mol.addBond(b4);
        IBond b5 = builder.newInstance(IBond.class, a3, a64, IBond.Order.SINGLE);
        mol.addBond(b5);
        IBond b6 = builder.newInstance(IBond.class, a4, a26, IBond.Order.SINGLE);
        mol.addBond(b6);
        IBond b7 = builder.newInstance(IBond.class, a4, a67, IBond.Order.SINGLE);
        mol.addBond(b7);
        IBond b8 = builder.newInstance(IBond.class, a5, a27, IBond.Order.SINGLE);
        mol.addBond(b8);
        IBond b9 = builder.newInstance(IBond.class, a5, a68, IBond.Order.SINGLE);
        mol.addBond(b9);
        IBond b10 = builder.newInstance(IBond.class, a6, a28, IBond.Order.SINGLE);
        mol.addBond(b10);
        IBond b11 = builder.newInstance(IBond.class, a6, a69, IBond.Order.SINGLE);
        mol.addBond(b11);
        IBond b12 = builder.newInstance(IBond.class, a7, a25, IBond.Order.DOUBLE);
        mol.addBond(b12);
        IBond b13 = builder.newInstance(IBond.class, a8, a9, IBond.Order.SINGLE);
        mol.addBond(b13);
        IBond b14 = builder.newInstance(IBond.class, a8, a10, IBond.Order.SINGLE);
        mol.addBond(b14);
        IBond b15 = builder.newInstance(IBond.class, a8, a13, IBond.Order.SINGLE);
        mol.addBond(b15);
        IBond b16 = builder.newInstance(IBond.class, a8, a20, IBond.Order.SINGLE);
        mol.addBond(b16);
        IBond b17 = builder.newInstance(IBond.class, a9, a14, IBond.Order.SINGLE);
        mol.addBond(b17);
        IBond b18 = builder.newInstance(IBond.class, a9, a15, IBond.Order.SINGLE);
        mol.addBond(b18);
        IBond b19 = builder.newInstance(IBond.class, a10, a16, IBond.Order.SINGLE);
        mol.addBond(b19);
        IBond b20 = builder.newInstance(IBond.class, a10, a19, IBond.Order.SINGLE);
        mol.addBond(b20);
        IBond b21 = builder.newInstance(IBond.class, a10, a35, IBond.Order.SINGLE);
        mol.addBond(b21);
        IBond b22 = builder.newInstance(IBond.class, a11, a12, IBond.Order.SINGLE);
        mol.addBond(b22);
        IBond b23 = builder.newInstance(IBond.class, a11, a14, IBond.Order.SINGLE);
        mol.addBond(b23);
        IBond b24 = builder.newInstance(IBond.class, a11, a17, IBond.Order.SINGLE);
        mol.addBond(b24);
        IBond b25 = builder.newInstance(IBond.class, a11, a36, IBond.Order.SINGLE);
        mol.addBond(b25);
        IBond b26 = builder.newInstance(IBond.class, a12, a18, IBond.Order.SINGLE);
        mol.addBond(b26);
        IBond b27 = builder.newInstance(IBond.class, a12, a21, IBond.Order.SINGLE);
        mol.addBond(b27);
        IBond b28 = builder.newInstance(IBond.class, a12, a24, IBond.Order.SINGLE);
        mol.addBond(b28);
        IBond b29 = builder.newInstance(IBond.class, a13, a17, IBond.Order.SINGLE);
        mol.addBond(b29);
        IBond b30 = builder.newInstance(IBond.class, a13, a37, IBond.Order.SINGLE);
        mol.addBond(b30);
        IBond b31 = builder.newInstance(IBond.class, a13, a38, IBond.Order.SINGLE);
        mol.addBond(b31);
        IBond b32 = builder.newInstance(IBond.class, a14, a22, IBond.Order.DOUBLE);
        mol.addBond(b32);
        IBond b33 = builder.newInstance(IBond.class, a15, a16, IBond.Order.SINGLE);
        mol.addBond(b33);
        IBond b34 = builder.newInstance(IBond.class, a15, a39, IBond.Order.SINGLE);
        mol.addBond(b34);
        IBond b35 = builder.newInstance(IBond.class, a15, a40, IBond.Order.SINGLE);
        mol.addBond(b35);
        IBond b36 = builder.newInstance(IBond.class, a16, a41, IBond.Order.SINGLE);
        mol.addBond(b36);
        IBond b37 = builder.newInstance(IBond.class, a16, a42, IBond.Order.SINGLE);
        mol.addBond(b37);
        IBond b38 = builder.newInstance(IBond.class, a17, a43, IBond.Order.SINGLE);
        mol.addBond(b38);
        IBond b39 = builder.newInstance(IBond.class, a17, a44, IBond.Order.SINGLE);
        mol.addBond(b39);
        IBond b40 = builder.newInstance(IBond.class, a18, a23, IBond.Order.SINGLE);
        mol.addBond(b40);
        IBond b41 = builder.newInstance(IBond.class, a18, a25, IBond.Order.SINGLE);
        mol.addBond(b41);
        IBond b42 = builder.newInstance(IBond.class, a18, a45, IBond.Order.SINGLE);
        mol.addBond(b42);
        IBond b43 = builder.newInstance(IBond.class, a19, a27, IBond.Order.SINGLE);
        mol.addBond(b43);
        IBond b44 = builder.newInstance(IBond.class, a19, a29, IBond.Order.SINGLE);
        mol.addBond(b44);
        IBond b45 = builder.newInstance(IBond.class, a20, a46, IBond.Order.SINGLE);
        mol.addBond(b45);
        IBond b46 = builder.newInstance(IBond.class, a20, a47, IBond.Order.SINGLE);
        mol.addBond(b46);
        IBond b47 = builder.newInstance(IBond.class, a20, a48, IBond.Order.SINGLE);
        mol.addBond(b47);
        IBond b48 = builder.newInstance(IBond.class, a21, a26, IBond.Order.SINGLE);
        mol.addBond(b48);
        IBond b49 = builder.newInstance(IBond.class, a21, a49, IBond.Order.SINGLE);
        mol.addBond(b49);
        IBond b50 = builder.newInstance(IBond.class, a21, a50, IBond.Order.SINGLE);
        mol.addBond(b50);
        IBond b51 = builder.newInstance(IBond.class, a22, a25, IBond.Order.SINGLE);
        mol.addBond(b51);
        IBond b52 = builder.newInstance(IBond.class, a22, a51, IBond.Order.SINGLE);
        mol.addBond(b52);
        IBond b53 = builder.newInstance(IBond.class, a23, a28, IBond.Order.SINGLE);
        mol.addBond(b53);
        IBond b54 = builder.newInstance(IBond.class, a23, a52, IBond.Order.SINGLE);
        mol.addBond(b54);
        IBond b55 = builder.newInstance(IBond.class, a23, a53, IBond.Order.SINGLE);
        mol.addBond(b55);
        IBond b56 = builder.newInstance(IBond.class, a24, a54, IBond.Order.SINGLE);
        mol.addBond(b56);
        IBond b57 = builder.newInstance(IBond.class, a24, a55, IBond.Order.SINGLE);
        mol.addBond(b57);
        IBond b58 = builder.newInstance(IBond.class, a24, a56, IBond.Order.SINGLE);
        mol.addBond(b58);
        IBond b59 = builder.newInstance(IBond.class, a26, a28, IBond.Order.SINGLE);
        mol.addBond(b59);
        IBond b60 = builder.newInstance(IBond.class, a26, a57, IBond.Order.SINGLE);
        mol.addBond(b60);
        IBond b61 = builder.newInstance(IBond.class, a27, a30, IBond.Order.SINGLE);
        mol.addBond(b61);
        IBond b62 = builder.newInstance(IBond.class, a27, a59, IBond.Order.SINGLE);
        mol.addBond(b62);
        IBond b63 = builder.newInstance(IBond.class, a28, a60, IBond.Order.SINGLE);
        mol.addBond(b63);
        IBond b64 = builder.newInstance(IBond.class, a29, a61, IBond.Order.SINGLE);
        mol.addBond(b64);
        IBond b65 = builder.newInstance(IBond.class, a29, a62, IBond.Order.SINGLE);
        mol.addBond(b65);
        IBond b66 = builder.newInstance(IBond.class, a29, a63, IBond.Order.SINGLE);
        mol.addBond(b66);
        IBond b67 = builder.newInstance(IBond.class, a30, a31, IBond.Order.SINGLE);
        mol.addBond(b67);
        IBond b68 = builder.newInstance(IBond.class, a30, a65, IBond.Order.SINGLE);
        mol.addBond(b68);
        IBond b69 = builder.newInstance(IBond.class, a30, a66, IBond.Order.SINGLE);
        mol.addBond(b69);
        IBond b70 = builder.newInstance(IBond.class, a31, a32, IBond.Order.SINGLE);
        mol.addBond(b70);
        IBond b71 = builder.newInstance(IBond.class, a31, a70, IBond.Order.SINGLE);
        mol.addBond(b71);
        IBond b72 = builder.newInstance(IBond.class, a31, a71, IBond.Order.SINGLE);
        mol.addBond(b72);
        IBond b73 = builder.newInstance(IBond.class, a32, a33, IBond.Order.SINGLE);
        mol.addBond(b73);
        IBond b74 = builder.newInstance(IBond.class, a32, a34, IBond.Order.SINGLE);
        mol.addBond(b74);

        IAtom[] ligandAtoms = new IAtom[4];
        ligandAtoms[0] = a1; // F
        ligandAtoms[1] = a33; // Me
        ligandAtoms[2] = a34; // Me
        ligandAtoms[3] = a31; // rest of molecule
        Stereo stereo = StereoTool.getStereo(ligandAtoms[0], ligandAtoms[1], ligandAtoms[2], ligandAtoms[3]);
        ITetrahedralChirality tetraStereo = new TetrahedralChirality(a32, ligandAtoms, stereo);

        Assert.assertEquals(CIP_CHIRALITY.NONE, CIPTool.getCIPChirality(mol, tetraStereo));
    }
}
