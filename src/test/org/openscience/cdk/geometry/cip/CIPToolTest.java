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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.geometry.cip.CIPTool.CIP_CHIRALITY;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * @cdk.module test-cip
 */
public class CIPToolTest extends CDKTestCase {

    static SmilesParser smiles = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
    static IMolecule molecule;
    static ILigand[] ligands;

    @BeforeClass
    public static void setup() throws Exception {
        molecule = smiles.parseSmiles("ClC(Br)(I)[H]");
        ILigand ligand1 = new Ligand(
            molecule, molecule.getAtom(1), molecule.getAtom(4)
        );
        ILigand ligand2 = new Ligand(
            molecule, molecule.getAtom(1), molecule.getAtom(3)
        );
        ILigand ligand3 = new Ligand(
            molecule, molecule.getAtom(1), molecule.getAtom(2)
        );
        ILigand ligand4 = new Ligand(
            molecule, molecule.getAtom(1), molecule.getAtom(0)
        );
        ligands = new ILigand[] {
            ligand1, ligand2, ligand3, ligand4
        };
    }

    @Test
    public void testCheckIfAllLigandsAreDifferent() {
        Assert.assertTrue(CIPTool.checkIfAllLigandsAreDifferent(ligands));
    }

    @Test
    public void testCheckIfAllLigandsAreDifferent_False() {
        ILigand[] sameLigands = new ILigand[] {
            ligands[0], ligands[0], ligands[1], ligands[2]
        };
        Assert.assertFalse(CIPTool.checkIfAllLigandsAreDifferent(sameLigands));
    }

    @Test
    public void testOrder(){
        ILigand[] ligandCopy = CIPTool.order(ligands);
        Assert.assertEquals("H", ligandCopy[0].getLigandAtom().getSymbol());
        Assert.assertEquals("Cl", ligandCopy[1].getLigandAtom().getSymbol());
        Assert.assertEquals("Br", ligandCopy[2].getLigandAtom().getSymbol());
        Assert.assertEquals("I", ligandCopy[3].getLigandAtom().getSymbol());
    }

    @Test
    public void testGetCIPChirality() {
        LigancyFourChirality chirality = new LigancyFourChirality(
            molecule.getAtom(1), ligands, Stereo.CLOCKWISE
        );
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_Anti() {
        ILigand[] antiLigands = new ILigand[] {
            ligands[0], ligands[1], ligands[3], ligands[2]
        };
        
        LigancyFourChirality chirality = new LigancyFourChirality(
            molecule.getAtom(1), antiLigands, Stereo.ANTI_CLOCKWISE
        );
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_ILigancyFourChirality() {
        List<IAtom> ligandAtoms = new ArrayList<IAtom>();
        for (ILigand ligand : ligands) ligandAtoms.add(ligand.getLigandAtom());
        ITetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), (IAtom[])ligandAtoms.toArray(new IAtom[]{}), Stereo.CLOCKWISE
        );
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(molecule, chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }

    @Test
    public void testGetCIPChirality_Anti_ILigancyFourChirality() {
        ILigand[] antiLigands = new ILigand[] {
            ligands[0], ligands[1], ligands[3], ligands[2]
        };
        List<IAtom> ligandAtoms = new ArrayList<IAtom>();
        for (ILigand ligand : antiLigands) ligandAtoms.add(ligand.getLigandAtom());

        ITetrahedralChirality chirality = new TetrahedralChirality(
            molecule.getAtom(1), (IAtom[])ligandAtoms.toArray(new IAtom[]{}), Stereo.ANTI_CLOCKWISE
        );
        CIP_CHIRALITY rsChirality = CIPTool.getCIPChirality(molecule, chirality);
        Assert.assertEquals(CIP_CHIRALITY.S, rsChirality);
    }
    @Test
    public void testDefineLigancyFourChirality() {
        LigancyFourChirality chirality = CIPTool.defineLigancyFourChirality(
            molecule, 1, 0, 2, 3, 4, Stereo.ANTI_CLOCKWISE
        );
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
        ILigand ligand = CIPTool.defineLigand(molecule, 1, 2);
        Assert.assertEquals(molecule, ligand.getAtomContainer());
        Assert.assertEquals(molecule.getAtom(1), ligand.getCentralAtom());        
        Assert.assertEquals(molecule.getAtom(2), ligand.getLigandAtom());
    }

    /**
     * Tests if it returns the right number of ligands, for single bonds only.
     */
    @Test
    public void testGetLigandLigands() throws Exception {
        IMolecule molecule = smiles.parseSmiles("CC(C)C(CC)(C(C)(C)C)[H]");
        ILigand ligand = CIPTool.defineLigand(molecule, 3, 1);
        ILigand[] sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(2, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, 3, 4);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(1, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, 3, 6);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(3, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, 3, 10);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(0, sideChains.length);
    }

    /**
     * Tests if it returns the right number of ligands, for double bonds.
     */
    @Test
    public void testGetLigandLigands_DoubleTriple() throws Exception {
        IMolecule molecule = smiles.parseSmiles("CC(C)C(C#N)(C(=C)C)[H]");
        ILigand ligand = CIPTool.defineLigand(molecule, 3, 1);
        ILigand[] sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(2, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, 3, 4);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(3, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, 3, 6);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(3, sideChains.length);
        ligand = CIPTool.defineLigand(molecule, 3, 9);
        sideChains = CIPTool.getLigandLigands(ligand);
        Assert.assertEquals(0, sideChains.length);
    }

    @Test
    public void testDefineLigand_ImplicitHydrogen() throws Exception {
        IMolecule molecule = smiles.parseSmiles("CC(C)C(C#N)(C(=C)C)");
        ILigand ligand = CIPTool.defineLigand(molecule, 3, CIPTool.HYDROGEN);
        Assert.assertTrue(ligand instanceof ImplicitHydrogenLigand);
    }
}


