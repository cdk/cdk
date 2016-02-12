/* Copyright (C) 2012 Daniel Szisz
 *
 * Contact: orlando@caesar.elte.hu
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
package org.openscience.cdk.modeling.builder3d;

import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.TestMoleculeFactory;


/**
 * Tests not-yet-tested functionalities of {@link AtomPlacer3D}.
 *
 * @author danielszisz
 * @cdk.module test-builder3d
 * @created 04/10/2012
 * @version 04/22/2012
 */

public class FurtherAtomPlacer3DTest extends AtomPlacer3DTest {

    @Test
    public void testAllHeavyAtomsPlaced_benzene() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        for (IAtom atom : benzene.atoms()) {
            atom.setFlag(CDKConstants.ISPLACED, true);
        }
        Assert.assertTrue(atmplacer.allHeavyAtomsPlaced(benzene));
    }

    @Test
    @Override
    public void testNumberOfUnplacedHeavyAtoms_IAtomContainer() {
        IAtomContainer molecule = TestMoleculeFactory.makeAlkane(5);
        for (int i = 0; i < 3; i++) {
            (molecule.getAtom(i)).setFlag(CDKConstants.ISPLACED, true);
        }
        int placedAtoms = new AtomPlacer3D().numberOfUnplacedHeavyAtoms(molecule);
        Assert.assertEquals(2, placedAtoms);
    }

    @Test
    @Override
    public void testGetPlacedHeavyAtoms_IAtomContainer_IAtom() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();
        for (int j = 0; j < 3; j++) {
            (molecule.getAtom(j)).setFlag(CDKConstants.ISPLACED, true);
        }
        IAtomContainer placedAndConnectedTo1 = atmplacer.getPlacedHeavyAtoms(molecule, molecule.getAtom(1));
        IAtomContainer placedAndConnectedTo2 = atmplacer.getPlacedHeavyAtoms(molecule, molecule.getAtom(2));
        IAtomContainer placedAndConnectedTo4 = atmplacer.getPlacedHeavyAtoms(molecule, molecule.getAtom(4));

        Assert.assertEquals(2, placedAndConnectedTo1.getAtomCount());
        Assert.assertEquals(1, placedAndConnectedTo2.getAtomCount());
        Assert.assertEquals(0, placedAndConnectedTo4.getAtomCount());

    }

    @Test
    @Override
    public void testGetPlacedHeavyAtom_IAtomContainer_IAtom_IAtom() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeAlkane(7);
        for (int j = 0; j < 5; j++) {
            molecule.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
        }
        IAtom atom2 = atmplacer.getPlacedHeavyAtom(molecule, molecule.getAtom(1), molecule.getAtom(0));
        IAtom atom3 = atmplacer.getPlacedHeavyAtom(molecule, molecule.getAtom(2), molecule.getAtom(1));
        IAtom nullAtom = atmplacer.getPlacedHeavyAtom(molecule, molecule.getAtom(0), molecule.getAtom(1));

        Assert.assertEquals(atom2, molecule.getAtom(2));
        Assert.assertEquals(atom3, molecule.getAtom(3));
        Assert.assertNull(nullAtom);
    }

    @Test
    @Override
    public void testGetPlacedHeavyAtom_IAtomContainer_IAtom() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeCyclohexane();
        //		for(IAtom a : m.atoms()) a.setFlag(CDKConstants.ISPLACED, true);
        for (int i = 0; i < 3; i++) {
            molecule.getAtom(i).setFlag(CDKConstants.ISPLACED, true);
        }

        IAtom atom1 = atmplacer.getPlacedHeavyAtom(molecule, molecule.getAtom(0));
        Assert.assertEquals(atom1, molecule.getAtom(1));
        IAtom atom2 = atmplacer.getPlacedHeavyAtom(molecule, molecule.getAtom(2));
        Assert.assertEquals(atom2, molecule.getAtom(1));
        IAtom atom3 = atmplacer.getPlacedHeavyAtom(molecule, molecule.getAtom(4));
        Assert.assertNull(atom3);

    }

    @Test
    @Override
    public void testGeometricCenterAllPlacedAtoms_IAtomContainer() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeAlkane(2);
        for (IAtom atom : molecule.atoms()) {
            atom.setFlag(CDKConstants.ISPLACED, true);
        }
        molecule.getAtom(0).setPoint3d(new Point3d(-1.0, 0.0, 0.0));
        molecule.getAtom(1).setPoint3d(new Point3d(1.0, 0.0, 0.0));

        Point3d center = atmplacer.geometricCenterAllPlacedAtoms(molecule);
        Assert.assertEquals(0.0, center.x, 0.01);
        Assert.assertEquals(0.0, center.y, 0.01);
        Assert.assertEquals(0.0, center.z, 0.01);

    }

    @Test
    public void testGetUnplacedRingHeavyAtom_IAtomContainer_IAtom() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeCyclopentane();

        for (IAtom atom : molecule.atoms())
            atom.setFlag(CDKConstants.ISINRING, true);
        for (int j = 0; j < 2; j++) {
            molecule.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
        }
        IAtom atom0 = molecule.getAtom(0);
        IAtom atom1 = molecule.getAtom(1);
        IAtom natom = molecule.getAtom(4);

        IAtom atom0pair = atmplacer.getUnplacedRingHeavyAtom(molecule, atom0);
        IAtom atom1pair = atmplacer.getUnplacedRingHeavyAtom(molecule, atom1);
        IAtom natompair = atmplacer.getUnplacedRingHeavyAtom(molecule, natom);

        Assert.assertEquals(atom0pair, molecule.getAtom(4));
        Assert.assertEquals(atom1pair, molecule.getAtom(2));
        Assert.assertEquals(atom0.getFlag(CDKConstants.ISPLACED), true);

        for (IBond bond : molecule.bonds()) {
            if (bond.getConnectedAtom(molecule.getAtom(4)) != null
                    && !bond.getConnectedAtom(molecule.getAtom(4)).getFlag(CDKConstants.ISPLACED)) {
                natompair = bond.getConnectedAtom(molecule.getAtom(4));
            }
        }
        Assert.assertEquals(natompair, molecule.getAtom(3));
    }

    @Test
    public void testGetFarthestAtom_Point3d_IAtomContainer() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeBenzene();

        molecule.getAtom(0).setPoint3d(new Point3d(0.0, 0.0, 0.0));
        molecule.getAtom(1).setPoint3d(new Point3d(1.0, 1.0, 1.0));
        molecule.getAtom(4).setPoint3d(new Point3d(3.0, 2.0, 1.0));
        molecule.getAtom(5).setPoint3d(new Point3d(4.0, 4.0, 4.0));

        IAtom farthestFromAtoma = atmplacer.getFarthestAtom(molecule.getAtom(0).getPoint3d(), molecule);
        IAtom farthestFromAtomb = atmplacer.getFarthestAtom(molecule.getAtom(4).getPoint3d(), molecule);

        Assert.assertEquals(molecule.getAtom(5), farthestFromAtoma);
        Assert.assertEquals(molecule.getAtom(0), farthestFromAtomb);

    }

    @Test
    public void testGetNextPlacedHeavyAtomWithUnplacedRingNeighbour_IAtomContainer() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer acyclicAlkane = TestMoleculeFactory.makeAlkane(3);
        IAtomContainer cycloPentane = TestMoleculeFactory.makeCyclopentane();

        //TestMoleculeFactory does not set ISINRING flags for cyclic molecules
        Assert.assertEquals(false, cycloPentane.getAtom(0).getFlag(CDKConstants.ISINRING));
        for (IAtom atom : cycloPentane.atoms()) {
            atom.setFlag(CDKConstants.ISINRING, true);
        }

        //acyclic molecule so null is expected
        for (IAtom atom : acyclicAlkane.atoms()) {
            atom.setFlag(CDKConstants.ISPLACED, true);
        }
        Assert.assertNull(atmplacer.getNextPlacedHeavyAtomWithUnplacedRingNeighbour(acyclicAlkane));

        for (int j = 0; j < 3; j++) {
            cycloPentane.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
        }
        Assert.assertEquals(cycloPentane.getAtom(2),
                atmplacer.getNextPlacedHeavyAtomWithUnplacedRingNeighbour(cycloPentane));

    }

    @Test
    public void testGetNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour_IAtomContainer() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer benzene = TestMoleculeFactory.makeBenzene();
        IAtomContainer acyclicAlkane = TestMoleculeFactory.makeAlkane(5);

        for (IAtom atom : benzene.atoms())
            atom.setFlag(CDKConstants.ISINRING, true);
        for (IAtom atom : acyclicAlkane.atoms())
            atom.setFlag(CDKConstants.ISALIPHATIC, true);

        for (int j = 0; j < 3; j++)
            benzene.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
        IAtom searchedatom1 = atmplacer.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(benzene);
        Assert.assertNull(searchedatom1);

        for (IAtom atom : benzene.atoms()) {
            if (!atom.getFlag(CDKConstants.ISPLACED)) {
                atom.setFlag(CDKConstants.ISPLACED, true);
            }
        }
        IAtom searchedatom2 = atmplacer.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(benzene);
        Assert.assertNull(searchedatom2);

        for (int k = 0; k < 3; k++) {
            acyclicAlkane.getAtom(k).setFlag(CDKConstants.ISPLACED, true);
        }
        IAtom nextAtom = atmplacer.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(acyclicAlkane);
        Assert.assertEquals(acyclicAlkane.getAtom(2), nextAtom);

    }

    @Test
    public void testGetNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour_IAtomContainer() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer cyclobutane = TestMoleculeFactory.makeCyclobutane();
        IAtomContainer acyclicAlkane = TestMoleculeFactory.makeAlkane(6);

        for (IAtom atom : cyclobutane.atoms()) {
            atom.setFlag(CDKConstants.ISINRING, true);
        }
        for (IAtom atom : acyclicAlkane.atoms()) {
            atom.setFlag(CDKConstants.ISALIPHATIC, true);
        }
        for (int j = 0; j < 3; j++) {
            cyclobutane.getAtom(j).setFlag(CDKConstants.ISPLACED, true);
        }
        IAtom nextHeavyAtom = atmplacer.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(cyclobutane);
        Assert.assertNull(nextHeavyAtom);

        for (IAtom atom : cyclobutane.atoms()) {
            if (!atom.getFlag(CDKConstants.ISPLACED)) {
                atom.setFlag(CDKConstants.ISPLACED, true);
            }
        }
        IAtom nextHeavyAtom2 = atmplacer.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(cyclobutane);
        Assert.assertNull(nextHeavyAtom2);

        for (int k = 0; k < 3; k++) {
            acyclicAlkane.getAtom(k).setFlag(CDKConstants.ISPLACED, true);
        }
        IAtom nextSuchUnPlacedHeavyAtom = atmplacer.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(acyclicAlkane);
        Assert.assertEquals(acyclicAlkane.getAtom(3), nextSuchUnPlacedHeavyAtom);

        for (IAtom atom : acyclicAlkane.atoms()) {
            atom.setFlag(CDKConstants.ISPLACED, true);
        }
        nextSuchUnPlacedHeavyAtom = atmplacer.getNextUnplacedHeavyAtomWithAliphaticPlacedNeighbour(acyclicAlkane);
        Assert.assertNull(nextSuchUnPlacedHeavyAtom);
    }

    /**
     * @cdk.bug #3224093
     */
    @Test
    public void testGetAngleValue_String_String_String() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "CCCCCC";
        IAtomContainer molecule = parser.parseSmiles(smiles);
        Assert.assertNotNull(molecule);
        ForceFieldConfigurator ffc = new ForceFieldConfigurator();
        ffc.setForceFieldConfigurator("mmff94", DefaultChemObjectBuilder.getInstance());
        AtomPlacer3D atomPlacer3d = new AtomPlacer3D();
        atomPlacer3d.initilize(ffc.getParameterSet());
        ffc.assignAtomTyps(molecule);

        String id1 = molecule.getAtom(1).getAtomTypeName();
        String id2 = molecule.getAtom(2).getAtomTypeName();
        String id3 = molecule.getAtom(3).getAtomTypeName();

        double anglev = atomPlacer3d.getAngleValue(id1, id2, id3);
        Assert.assertEquals(109.608, anglev, 0.001);

    }

    /**
     * @cdk.bug #3524092
     */
    @Test
    public void testGetBondLengthValue_String_String() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "CCCCCC";
        IAtomContainer molecule = parser.parseSmiles(smiles);
        Assert.assertNotNull(molecule);
        ForceFieldConfigurator ffc = new ForceFieldConfigurator();
        ffc.setForceFieldConfigurator("mmff94", DefaultChemObjectBuilder.getInstance());
        AtomPlacer3D atomPlacer3d = new AtomPlacer3D();
        atomPlacer3d.initilize(ffc.getParameterSet());
        ffc.assignAtomTyps(molecule);

        String id1 = molecule.getAtom(1).getAtomTypeName();
        String id2 = molecule.getAtom(2).getAtomTypeName();
        String mmff94id1 = "C";
        String mmff94id2 = "C";
        Assert.assertNotSame(mmff94id1, id1);
        Assert.assertNotSame(mmff94id2, id2);

        double bondlength = atomPlacer3d.getBondLengthValue(id1, id2);
        Assert.assertEquals(1.508, bondlength, 0.001);
    }

    /**
     * @cdk.bug #3523247
     */
    @Test
    public void testGetBondLengthValue_bug_CNBond() throws Exception {
        SmilesParser parser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        String smiles = "CCCN";
        IAtomContainer molecule = parser.parseSmiles(smiles);
        Assert.assertNotNull(molecule);
        ForceFieldConfigurator ffc = new ForceFieldConfigurator();
        ffc.setForceFieldConfigurator("mmff94", DefaultChemObjectBuilder.getInstance());
        AtomPlacer3D atomPlacer3d = new AtomPlacer3D();
        atomPlacer3d.initilize(ffc.getParameterSet());
        ffc.assignAtomTyps(molecule);

        String id1 = molecule.getAtom(2).getAtomTypeName();
        String id2 = molecule.getAtom(3).getAtomTypeName();
        double bondlength = atomPlacer3d.getBondLengthValue(id1, id2);
        Assert.assertEquals(1.451, bondlength, 0.001);

    }

    @Test
    public void testMarkPlaced_IAtomContainer() {
        AtomPlacer3D atmplacer = new AtomPlacer3D();
        IAtomContainer molecule = TestMoleculeFactory.makeAlkane(5);
        IAtomContainer placedMolecule = atmplacer.markPlaced(molecule);
        for (IAtom atom : placedMolecule.atoms()) {
            Assert.assertTrue(atom.getFlag(CDKConstants.ISPLACED));
        }
    }

    /**
     * This class only places 'chains' - i.e. no branching. Check an exception
     * is thrown.
     * @cdk.inchi InChI=1/C14H30/c1-4-7-10-13-14(11-8-5-2)12-9-6-3/h14H,4-13H2,1-3H3
     */
    @Test(expected = CDKException.class)
    public void invalidChain() throws CDKException {

        String input = "CCCCCC(CCCC)CCCC";
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer m = sp.parseSmiles(input);

        ForceFieldConfigurator ffc = new ForceFieldConfigurator();
        ffc.setForceFieldConfigurator("mmff92", DefaultChemObjectBuilder.getInstance());
        ffc.assignAtomTyps(m);

        AtomPlacer3D ap3d = new AtomPlacer3D();
        ap3d.initilize(ffc.getParameterSet());
        ap3d.placeAliphaticHeavyChain(m, m);
    }

}
