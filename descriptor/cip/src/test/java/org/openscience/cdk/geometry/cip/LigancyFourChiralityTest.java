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
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import org.openscience.cdk.stereo.TetrahedralChirality;

/**
 * @cdk.module test-cip
 */
public class LigancyFourChiralityTest extends CDKTestCase {

    private static IAtomContainer molecule;
    private static ILigand[]      ligands;

    @BeforeClass
    public static void setup() throws Exception {
        molecule = new AtomContainer();
        molecule.addAtom(new Atom("Cl"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("Br"));
        molecule.addAtom(new Atom("I"));
        molecule.addAtom(new Atom("H"));
        molecule.addBond(0, 1, Order.SINGLE);
        molecule.addBond(1, 2, Order.SINGLE);
        molecule.addBond(1, 3, Order.SINGLE);
        molecule.addBond(1, 4, Order.SINGLE);
        ILigand ligand1 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(4));
        ILigand ligand2 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(3));
        ILigand ligand3 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(2));
        ILigand ligand4 = new Ligand(molecule, new VisitedAtoms(), molecule.getAtom(1), molecule.getAtom(0));
        ligands = new ILigand[]{ligand1, ligand2, ligand3, ligand4};
    }

    @Test
    public void testConstructor() {
        LigancyFourChirality chirality = new LigancyFourChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        Assert.assertNotNull(chirality);
        Assert.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
        for (int i = 0; i < ligands.length; i++) {
            Assert.assertEquals(ligands[i], chirality.getLigands()[i]);
        }
        Assert.assertEquals(Stereo.CLOCKWISE, chirality.getStereo());
    }

    @Test
    public void testConstructor_ILigancyFourChirality() {
        List<IAtom> ligandAtoms = new ArrayList<IAtom>();
        for (ILigand ligand : ligands)
            ligandAtoms.add(ligand.getLigandAtom());
        ITetrahedralChirality cdkChiral = new TetrahedralChirality(molecule.getAtom(1),
                (IAtom[]) ligandAtoms.toArray(new IAtom[]{}), Stereo.CLOCKWISE);
        LigancyFourChirality chirality = new LigancyFourChirality(molecule, cdkChiral);
        Assert.assertNotNull(chirality);
        Assert.assertEquals(molecule.getAtom(1), chirality.getChiralAtom());
        for (int i = 0; i < ligands.length; i++) {
            Assert.assertEquals(ligands[i].getLigandAtom(), chirality.getLigands()[i].getLigandAtom());
            Assert.assertEquals(ligands[i].getCentralAtom(), chirality.getLigands()[i].getCentralAtom());
            Assert.assertEquals(ligands[i].getAtomContainer(), chirality.getLigands()[i].getAtomContainer());
        }
        Assert.assertEquals(Stereo.CLOCKWISE, chirality.getStereo());
    }

    /**
     * Checks if projecting onto itself does not change the stereochemistry.
     */
    @Test
    public void testProject() {
        LigancyFourChirality chirality = new LigancyFourChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        chirality.project(ligands);
        Assert.assertEquals(Stereo.CLOCKWISE, chirality.getStereo());
    }

    @Test
    public void testProject_OneChange() {
        LigancyFourChirality chirality = new LigancyFourChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        ILigand[] newLigands = new ILigand[]{ligands[0], ligands[1], ligands[3], ligands[2]};
        chirality = chirality.project(newLigands);
        Assert.assertEquals(Stereo.ANTI_CLOCKWISE, chirality.getStereo());
    }

    @Test
    public void testProject_TwoChanges() {
        LigancyFourChirality chirality = new LigancyFourChirality(molecule.getAtom(1), ligands, Stereo.CLOCKWISE);
        ILigand[] newLigands = new ILigand[]{ligands[1], ligands[0], ligands[3], ligands[2]};
        chirality = chirality.project(newLigands);
        Assert.assertEquals(Stereo.CLOCKWISE, chirality.getStereo());
    }
}
