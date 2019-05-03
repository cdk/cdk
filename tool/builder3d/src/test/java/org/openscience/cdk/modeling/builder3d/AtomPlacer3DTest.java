/* Copyright (C) 2005-2007  Christian Hoppe <chhoppe@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openscience.cdk.modeling.builder3d;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.AtomContainer;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for AtomPlacer3D
 *
 * @cdk.module test-builder3d
 * @cdk.githash
 */
public class AtomPlacer3DTest extends CDKTestCase {

    boolean standAlone = false;

    @BeforeClass
    public static void setUpClass() throws Exception {}

    @AfterClass
    public static void tearDownClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    @After
    public void tearDown() throws Exception {}

    /**
     *  Sets the standAlone attribute
     *
     *@param  standAlone  The new standAlone value
     */
    public void setStandAlone(boolean standAlone) {
        this.standAlone = standAlone;
    }

    /**
     * Create a test molecule (alpha-pinene).
     * This code has been inlined from MoleculeFactory.java
     *
     * @return the created test molecule
     */
    private IAtomContainer makeAlphaPinene() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("C"));

        mol.addBond(0, 1, IBond.Order.DOUBLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE);
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(5, 0, IBond.Order.SINGLE);
        mol.addBond(0, 6, IBond.Order.SINGLE);
        mol.addBond(3, 7, IBond.Order.SINGLE);
        mol.addBond(5, 7, IBond.Order.SINGLE);
        mol.addBond(7, 8, IBond.Order.SINGLE);
        mol.addBond(7, 9, IBond.Order.SINGLE);
        try {
            Isotopes.getInstance().configureAtoms(mol);
        } catch (IOException ex) {
            Logger.getLogger(AtomPlacer3DTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mol;
    }

    private IAtomContainer makeMethaneWithExplicitHydrogens() {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));
        mol.addAtom(new Atom("H"));

        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 2, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 4, IBond.Order.SINGLE);

        return mol;
    }

    @Test
    public void testAllHeavyAtomsPlaced_IAtomContainer() {
        IAtomContainer ac = makeAlphaPinene();
        Assert.assertFalse(new AtomPlacer3D().allHeavyAtomsPlaced(ac));
        for (IAtom atom : ac.atoms()) {
            atom.setFlag(CDKConstants.ISPLACED, true);
        }
        Assert.assertTrue(new AtomPlacer3D().allHeavyAtomsPlaced(ac));
    }

    @Test
    public void testFindHeavyAtomsInChain_IAtomContainer_IAtomContainer() throws Exception {
        String filename = "data/mdl/allmol232.mol";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        // TODO: shk3-cleanuptests: best to use the STRICT IO mode here
        MDLV2000Reader reader = new MDLV2000Reader(ins);
        ChemFile chemFile = (ChemFile) reader.read((ChemObject) new ChemFile());
        reader.close();
        List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(chemFile);
        IAtomContainer ac = new AtomContainer(containersList.get(0));
        addExplicitHydrogens(ac);
        IAtomContainer chain = ac.getBuilder().newInstance(IAtomContainer.class);
        for (int i = 16; i < 25; i++) {
            chain.addAtom(ac.getAtom(i));
        }
        chain.addAtom(ac.getAtom(29));
        chain.addAtom(ac.getAtom(30));
        int[] result = new AtomPlacer3D().findHeavyAtomsInChain(ac, chain);
        Assert.assertEquals(16, result[0]);
        Assert.assertEquals(11, result[1]);
    }

    @Test
    public void testNumberOfUnplacedHeavyAtoms_IAtomContainer() {
        IAtomContainer ac = makeAlphaPinene();
        int count = new AtomPlacer3D().numberOfUnplacedHeavyAtoms(ac);
        Assert.assertEquals(10, count);
    }

    /**
     * Demonstrate bug where AtomPlacer3D().numberOfUnplacedHeavyAtoms() counts
     * explicit hydrogens as heavy atoms.
     *
     */
    @Test
    public void testNumberOfUnplacedHeavyAtoms_IAtomContainerWithExplicitHydrogens() {
        IAtomContainer ac = makeMethaneWithExplicitHydrogens();
        int count = new AtomPlacer3D().numberOfUnplacedHeavyAtoms(ac);
        Assert.assertEquals(1, count);
    }

    @Test
    public void testGetPlacedHeavyAtoms_IAtomContainer_IAtom() {
        IAtomContainer ac = makeAlphaPinene();
        IAtomContainer acplaced = new AtomPlacer3D().getPlacedHeavyAtoms(ac, ac.getAtom(0));
        Assert.assertEquals(0, acplaced.getAtomCount());
        ac.getAtom(1).setFlag(CDKConstants.ISPLACED, true);
        acplaced = new AtomPlacer3D().getPlacedHeavyAtoms(ac, ac.getAtom(0));
        Assert.assertEquals(1, acplaced.getAtomCount());
    }

    @Test
    public void testGetPlacedHeavyAtom_IAtomContainer_IAtom_IAtom() {
        IAtomContainer ac = makeAlphaPinene();
        IAtom acplaced = new AtomPlacer3D().getPlacedHeavyAtom(ac, ac.getAtom(0), ac.getAtom(1));
        Assert.assertNull(acplaced);
        ac.getAtom(1).setFlag(CDKConstants.ISPLACED, true);
        acplaced = new AtomPlacer3D().getPlacedHeavyAtom(ac, ac.getAtom(0), ac.getAtom(2));
        Assert.assertEquals(ac.getAtom(1), acplaced);
        acplaced = new AtomPlacer3D().getPlacedHeavyAtom(ac, ac.getAtom(0), ac.getAtom(1));
        Assert.assertNull(acplaced);
    }

    @Test
    public void testGetPlacedHeavyAtom_IAtomContainer_IAtom() {
        IAtomContainer ac = makeAlphaPinene();
        IAtom acplaced = new AtomPlacer3D().getPlacedHeavyAtom(ac, ac.getAtom(0));
        Assert.assertNull(acplaced);
        ac.getAtom(1).setFlag(CDKConstants.ISPLACED, true);
        acplaced = new AtomPlacer3D().getPlacedHeavyAtom(ac, ac.getAtom(0));
        Assert.assertEquals(ac.getAtom(1), acplaced);
    }

    @Test
    public void testGeometricCenterAllPlacedAtoms_IAtomContainer() throws Exception {
        IAtomContainer ac = makeAlphaPinene();
        for (int i = 0; i < ac.getAtomCount(); i++) {
            ac.getAtom(i).setFlag(CDKConstants.ISPLACED, true);
        }
        ac.getAtom(0).setPoint3d(new Point3d(1.39, 2.04, 0));
        ac.getAtom(0).setPoint3d(new Point3d(2.02, 2.28, -1.12));
        ac.getAtom(0).setPoint3d(new Point3d(3.44, 2.80, -1.09));
        ac.getAtom(0).setPoint3d(new Point3d(3.91, 2.97, 0.35));
        ac.getAtom(0).setPoint3d(new Point3d(3.56, 1.71, 1.16));
        ac.getAtom(0).setPoint3d(new Point3d(2.14, 2.31, 1.29));
        ac.getAtom(0).setPoint3d(new Point3d(0, 1.53, 0));
        ac.getAtom(0).setPoint3d(new Point3d(2.83, 3.69, 1.17));
        ac.getAtom(0).setPoint3d(new Point3d(3.32, 4.27, 2.49));
        ac.getAtom(0).setPoint3d(new Point3d(2.02, 4.68, 0.35));
        Point3d center = new AtomPlacer3D().geometricCenterAllPlacedAtoms(ac);
        Assert.assertEquals(2.02, center.x, 0.01);
        Assert.assertEquals(4.68, center.y, 0.01);
        Assert.assertEquals(0.35, center.z, 0.01);
    }

    @Test
    public void testIsUnplacedHeavyAtom() {

        IAtomContainer ac = makeMethaneWithExplicitHydrogens();
        IAtom carbon = ac.getAtom(0);
        IAtom hydrogen = ac.getAtom(1);
        AtomPlacer3D placer = new AtomPlacer3D();

        boolean result = false;
        result = placer.isUnplacedHeavyAtom(carbon);
        Assert.assertTrue(result);
        result = placer.isUnplacedHeavyAtom(hydrogen);
        Assert.assertFalse(result);

        carbon.setFlag(CDKConstants.ISPLACED, true);
        result = placer.isUnplacedHeavyAtom(carbon);
        Assert.assertFalse(result);
        hydrogen.setFlag(CDKConstants.ISPLACED, true);
        result = placer.isUnplacedHeavyAtom(hydrogen);
        Assert.assertFalse(result);
    }

    @Test
    public void testIsPlacedHeavyAtom() {

        IAtomContainer ac = makeMethaneWithExplicitHydrogens();
        IAtom carbon = ac.getAtom(0);
        IAtom hydrogen = ac.getAtom(1);
        AtomPlacer3D placer = new AtomPlacer3D();

        boolean result = false;
        result = placer.isPlacedHeavyAtom(carbon);
        Assert.assertFalse(result);
        result = placer.isPlacedHeavyAtom(hydrogen);
        Assert.assertFalse(result);

        carbon.setFlag(CDKConstants.ISPLACED, true);
        result = placer.isPlacedHeavyAtom(carbon);
        Assert.assertTrue(result);
        hydrogen.setFlag(CDKConstants.ISPLACED, true);
        result = placer.isPlacedHeavyAtom(hydrogen);
        Assert.assertFalse(result);
    }

    @Test
    public void testIsAliphaticHeavyAtom() {

        IAtomContainer ac = makeMethaneWithExplicitHydrogens();
        IAtom carbon = ac.getAtom(0);
        IAtom hydrogen = ac.getAtom(1);
        AtomPlacer3D placer = new AtomPlacer3D();

        boolean result = false;
        result = placer.isAliphaticHeavyAtom(carbon);
        Assert.assertFalse(result);
        result = placer.isAliphaticHeavyAtom(hydrogen);
        Assert.assertFalse(result);

        carbon.setFlag(CDKConstants.ISALIPHATIC, true);
        result = placer.isAliphaticHeavyAtom(carbon);
        Assert.assertTrue(result);
        hydrogen.setFlag(CDKConstants.ISALIPHATIC, true);
        result = placer.isAliphaticHeavyAtom(hydrogen);
        Assert.assertFalse(result);
    }

    @Test
    public void testIsRingHeavyAtom() {

        IAtomContainer ac = makeMethaneWithExplicitHydrogens();
        IAtom carbon = ac.getAtom(0);
        IAtom hydrogen = ac.getAtom(1);
        AtomPlacer3D placer = new AtomPlacer3D();

        boolean result = false;
        result = placer.isRingHeavyAtom(carbon);
        Assert.assertFalse(result);
        result = placer.isRingHeavyAtom(hydrogen);
        Assert.assertFalse(result);

        carbon.setFlag(CDKConstants.ISINRING, true);
        result = placer.isRingHeavyAtom(carbon);
        Assert.assertTrue(result);
        hydrogen.setFlag(CDKConstants.ISINRING, true);
        result = placer.isRingHeavyAtom(hydrogen);
        Assert.assertFalse(result);
    }

    @Test
    public void testIsHeavyAtom() {

        IAtomContainer ac = makeMethaneWithExplicitHydrogens();
        IAtom carbon = ac.getAtom(0);
        IAtom hydrogen = ac.getAtom(1);
        AtomPlacer3D placer = new AtomPlacer3D();

        boolean result = false;
        result = placer.isHeavyAtom(carbon);
        Assert.assertTrue(result);
        result = placer.isHeavyAtom(hydrogen);
        Assert.assertFalse(result);
    }
}
