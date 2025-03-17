/*  Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
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

package org.openscience.cdk.geometry;

import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.AtomMappingTools;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.tools.diff.AtomContainerDiff;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class defines regression tests that should ensure that the source code
 * of the {@link org.openscience.cdk.geometry.GeometryUtil} is not broken.
 *
 *
 * @author     Egon Willighagen
 * @cdk.created    2004-01-30
 *
 * @see org.openscience.cdk.geometry.GeometryUtil
 */
class GeometryUtilTest extends CDKTestCase {

    @Test
    void testHas2DCoordinates_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertTrue(GeometryUtil.has2DCoordinates(container));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertFalse(GeometryUtil.has2DCoordinates(container));
    }

    @Test
    void testHas2DCoordinates_EmptyAtomContainer() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertFalse(GeometryUtil.has2DCoordinates(container));
        Assertions.assertFalse(GeometryUtil.has2DCoordinates((IAtomContainer) null));
    }

    @Test
    void testHas2DCoordinates_Partial() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        container.addAtom(atom1);
        Assertions.assertTrue(GeometryUtil.has2DCoordinates(container));
        container.addAtom(atom2);
        Assertions.assertFalse(GeometryUtil.has2DCoordinates(container));
    }

    /**
     * @cdk.bug 2936440
     */
    @Test
    void testHas2DCoordinates_With000() throws CDKException {
        String filenameMol = "with000coordinate.mol";
        InputStream ins = this.getClass().getResourceAsStream(filenameMol);
        IAtomContainer molOne;
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molOne = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());
        Assertions.assertTrue(GeometryUtil.has2DCoordinates(molOne));
    }

    @Test
    void get2DCoordinateCoverage_EmptyAtomContainer() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.NONE, GeometryUtil.get2DCoordinateCoverage(container));
        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.NONE, GeometryUtil.get2DCoordinateCoverage(null));
    }

    @Test
    void get2DCoordinateCoverage_Partial() {

        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint2d(new Point2d(1, 1));
        atom3.setPoint2d(new Point2d(1, 1));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.PARTIAL, GeometryUtil.get2DCoordinateCoverage(container));

    }

    @Test
    void get2DCoordinateCoverage_Full() {

        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint2d(new Point2d(1, 1));
        atom2.setPoint2d(new Point2d(2, 1));
        atom3.setPoint2d(new Point2d(1, 2));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.FULL, GeometryUtil.get2DCoordinateCoverage(container));

    }

    @Test
    void get2DCoordinateCoverage_None_3D() {

        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint3d(new Point3d(1, 1, 0));
        atom2.setPoint3d(new Point3d(2, 1, 0));
        atom3.setPoint3d(new Point3d(1, 2, 0));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.NONE, GeometryUtil.get2DCoordinateCoverage(container));

    }

    @Test
    void testTranslateAllPositive_IAtomContainer() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IAtom atom = new Atom(Elements.CARBON);
        atom.setPoint2d(new Point2d(-3, -2));
        container.addAtom(atom);
        GeometryUtil.translateAllPositive(container);
        Assertions.assertTrue(0 <= atom.getPoint2d().x);
        Assertions.assertTrue(0 <= atom.getPoint2d().y);
    }

    @Test
    void testGetLength2D_IBond() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond bond = new Bond(c, o);

        Assertions.assertEquals(1.0, GeometryUtil.getLength2D(bond), 0.001);
    }

    @Test
    void testMapAtomsOfAlignedStructures() throws Exception {
        String filenameMolOne = "murckoTest6_3d_2.mol";
        String filenameMolTwo = "murckoTest6_3d.mol";
        InputStream ins = this.getClass().getResourceAsStream(filenameMolOne);
        IAtomContainer molOne;
        IAtomContainer molTwo;
        Map<Integer, Integer> mappedAtoms = new HashMap<>();
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molOne = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());

        ins = this.getClass().getResourceAsStream(filenameMolTwo);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        molTwo = reader.read(DefaultChemObjectBuilder.getInstance().newAtomContainer());

        mappedAtoms = AtomMappingTools.mapAtomsOfAlignedStructures(molOne, molTwo, mappedAtoms);
        //logger.debug("mappedAtoms:"+mappedAtoms.toString());
        //logger.debug("***** ANGLE VARIATIONS *****");
        double AngleRMSD = GeometryUtil.getAngleRMSD(molOne, molTwo, mappedAtoms);
        //logger.debug("The Angle RMSD between the first and the second structure is :"+AngleRMSD);
        //logger.debug("***** ALL ATOMS RMSD *****");
        Assertions.assertEquals(0.2, AngleRMSD, 0.1);
        double AllRMSD = GeometryUtil.getAllAtomRMSD(molOne, molTwo, mappedAtoms, true);
        //logger.debug("The RMSD between the first and the second structure is :"+AllRMSD);
        Assertions.assertEquals(0.242, AllRMSD, 0.001);
        //logger.debug("***** BOND LENGTH RMSD *****");
        double BondLengthRMSD = GeometryUtil.getBondLengthRMSD(molOne, molTwo, mappedAtoms, true);
        //logger.debug("The Bond length RMSD between the first and the second structure is :"+BondLengthRMSD);
        Assertions.assertEquals(0.2, BondLengthRMSD, 0.1);
    }

    /*
     * @cdk.bug 1649007
     */
    @Test
    void testRotate_IAtomContainer_Point2d_double() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        GeometryUtil.rotate(ac, new Point2d(0, 0), Math.PI / 2);
        Assertions.assertEquals(atom1.getPoint2d().x, -1, .2);
        Assertions.assertEquals(atom1.getPoint2d().y, 1, .2);
        Assertions.assertEquals(atom2.getPoint2d().x, 0, .2);
        Assertions.assertEquals(atom2.getPoint2d().y, 1, .2);
        atom2.setPoint2d(new Point2d(0, 0));
        GeometryUtil.rotate(ac, new Point2d(0, 0), Math.PI);
        Assertions.assertFalse(Double.isNaN(atom2.getPoint2d().x));
        Assertions.assertFalse(Double.isNaN(atom2.getPoint2d().y));
    }

    @Test
    void testGetMinMax_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        double[] minmax = GeometryUtil.getMinMax(ac);
        Assertions.assertEquals(minmax[0], 1d, .1);
        Assertions.assertEquals(minmax[1], 0d, .1);
        Assertions.assertEquals(minmax[2], 1d, .1);
        Assertions.assertEquals(minmax[3], 1d, .1);
    }

    /** @cdk.bug 2094881 */
    @Test
    void testGetMinMax2() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(-2, -1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(-5, -1));
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        double[] minmax = GeometryUtil.getMinMax(ac);
        Assertions.assertEquals(-5, minmax[0], .1);
        Assertions.assertEquals(-1, minmax[1], .1);
        Assertions.assertEquals(-2, minmax[2], .1);
        Assertions.assertEquals(-1, minmax[3], .1);
    }

    @Test
    void testRotate_IAtom_Point3d_Point3d_double() {
        Atom atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 0));
        GeometryUtil.rotate(atom1, new Point3d(2, 0, 0), new Point3d(2, 2, 0), 90);
        assertEquals(new Point3d(2.0, 1.0, 1.0), atom1.getPoint3d(), 0.2);
    }

    @Test
    void testNormalize_Point3d() {
        Point3d p = new Point3d(1, 1, 0);
        GeometryUtil.normalize(p);
        Assertions.assertEquals(p.x, 0.7, .1);
        Assertions.assertEquals(p.y, 0.7, .1);
        Assertions.assertEquals(p.z, 0.0, .1);
    }

    @Test
    void testGet2DCenter_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        Point2d p = GeometryUtil.get2DCenter(ac);
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testGet2DCenterOfMass_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        atom1.setExactMass(12.0);
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        atom2.setExactMass(12.0);
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        Point2d p = GeometryUtil.get2DCentreOfMass(ac);
        Assertions.assertNotNull(p);
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testGet2DCenter_arrayIAtom() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        container.addAtom(atom1);
        container.addAtom(atom2);
        Point2d p = GeometryUtil.get2DCenter(container.atoms());
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testGet2DCenter_IRingSet() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IRing ac = DefaultChemObjectBuilder.getInstance().newInstance(IRing.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        IRingSet ringset = DefaultChemObjectBuilder.getInstance().newInstance(IRingSet.class);
        ringset.addAtomContainer(ac);
        Point2d p = GeometryUtil.get2DCenter(ac);
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testGet2DCenter_Iterator() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        Point2d p = GeometryUtil.get2DCenter(ac.atoms());
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testHas2DCoordinates_IAtom() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Assertions.assertTrue(GeometryUtil.has2DCoordinates(atom1));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        Assertions.assertFalse(GeometryUtil.has2DCoordinates(atom1));
    }

    @Test
    void testHas2DCoordinates_IBond() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IBond bond = new Bond(atom1, atom2);
        Assertions.assertTrue(GeometryUtil.has2DCoordinates(bond));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        bond = new Bond(atom1, atom2);
        Assertions.assertFalse(GeometryUtil.has2DCoordinates(bond));
    }

    @Test
    void testHas2DCoordinatesNew_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertEquals(2, GeometryUtil.has2DCoordinatesNew(container));

        atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 1));
        container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertEquals(1, GeometryUtil.has2DCoordinatesNew(container));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertEquals(0, GeometryUtil.has2DCoordinatesNew(container));
    }

    @Test
    void testHas3DCoordinates_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertFalse(GeometryUtil.has3DCoordinates(container));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertTrue(GeometryUtil.has3DCoordinates(container));
    }

    @Test
    void testHas3DCoordinates_EmptyAtomContainer() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertFalse(GeometryUtil.has3DCoordinates(container));
        Assertions.assertFalse(GeometryUtil.has3DCoordinates((IAtomContainer) null));
    }

    @Test
    void get3DCoordinateCoverage_EmptyAtomContainer() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.NONE, GeometryUtil.get3DCoordinateCoverage(container));
        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.NONE, GeometryUtil.get3DCoordinateCoverage(null));
    }

    @Test
    void get3DCoordinateCoverage_Partial() {

        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint3d(new Point3d(1, 1, 0));
        atom3.setPoint3d(new Point3d(1, 1, 0));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.PARTIAL, GeometryUtil.get3DCoordinateCoverage(container));

    }

    @Test
    void get3DCoordinateCoverage_Full() {

        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint3d(new Point3d(1, 1, 0));
        atom2.setPoint3d(new Point3d(2, 1, 0));
        atom3.setPoint3d(new Point3d(1, 2, 0));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.FULL, GeometryUtil.get3DCoordinateCoverage(container));

    }

    @Test
    void get3DCoordinateCoverage_None_2D() {

        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint2d(new Point2d(1, 1));
        atom2.setPoint2d(new Point2d(2, 1));
        atom3.setPoint2d(new Point2d(1, 2));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryUtil.CoordinateCoverage.NONE, GeometryUtil.get3DCoordinateCoverage(container));

    }

    @Test
    void testTranslateAllPositive_IAtomContainer_HashMap() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(-1, -1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        GeometryUtil.translateAllPositive(ac);
        Assertions.assertEquals(atom1.getPoint2d().x, 0.0, 0.01);
        Assertions.assertEquals(atom1.getPoint2d().y, 0.0, 0.01);
        Assertions.assertEquals(atom2.getPoint2d().x, 2.0, 0.01);
        Assertions.assertEquals(atom2.getPoint2d().y, 1.0, 0.01);
    }

    @Test
    void testGetLength2D_IBond_HashMap() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(-1, -1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IBond bond = new Bond(atom1, atom2);
        IAtomContainer ac = DefaultChemObjectBuilder.getInstance().newInstance(IAtomContainer.class);
        ac.addAtom(atom1);
        ac.addAtom(atom2);
        Assertions.assertEquals(GeometryUtil.getLength2D(bond), 2.23, 0.01);
    }

    @Test
    void testGetClosestAtom_Multiatom() {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(-1, -1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtom atom3 = new Atom("C");
        atom3.setPoint2d(new Point2d(5, 0));
        IAtomContainer acont = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addAtom(atom3);
        Assertions.assertEquals(atom2, GeometryUtil.getClosestAtom(acont, atom1));
        Assertions.assertEquals(atom1, GeometryUtil.getClosestAtom(acont, atom2));
        Assertions.assertEquals(atom2, GeometryUtil.getClosestAtom(acont, atom3));
    }

    @Test
    void testGetClosestAtom_Double_Double_IAtomContainer_IAtom() {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(5, 0));
        IAtomContainer acont = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        Assertions.assertEquals(atom2, GeometryUtil.getClosestAtom(1.0, 0.0, acont, atom1));
        Assertions.assertEquals(atom1, GeometryUtil.getClosestAtom(1.0, 0.0, acont, null));
    }

    /**
     * Tests if not the central atom is returned as closest atom.
     */
    @Test
    void testGetClosestAtom_IAtomContainer_IAtom() {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(-1, -1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer acont = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        Assertions.assertEquals(atom2, GeometryUtil.getClosestAtom(acont, atom1));
        Assertions.assertEquals(atom1, GeometryUtil.getClosestAtom(acont, atom2));
    }

    @Test
    void testShiftContainerHorizontal_IAtomContainer_Rectangle2D_Rectangle2D_double() throws Exception {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer react1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        IAtomContainer react2 = react1.clone();

        // shift the second molecule right
        GeometryUtil.shiftContainer(react2, GeometryUtil.getMinMax(react2), GeometryUtil.getMinMax(react1), 1.0);
        // assert all coordinates of the second molecule moved right
        AtomContainerDiff.diff(react1, react2);
        for (int i = 0; i < 2; i++) {
            atom1 = react1.getAtom(0);
            atom2 = react2.getAtom(0);
            // so, y coordinates should be the same
            Assertions.assertEquals(atom1.getPoint2d().y, atom2.getPoint2d().y, 0.0);
            // but, x coordinates should not
            Assertions.assertTrue(atom1.getPoint2d().x < atom2.getPoint2d().x);
        }
    }

    /**
     * Unit tests that tests the situation where two vertical two-atom
     * molecules are with the same x coordinates.
     *
     * @throws Exception Thrown when the cloning failed.
     */
    @Test
    void testShiftContainerHorizontal_Two_vertical_molecules() throws Exception {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(0, 1));
        IAtomContainer react1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        IAtomContainer react2 = react1.clone();

        // shift the second molecule right
        GeometryUtil.shiftContainer(react2, GeometryUtil.getMinMax(react2), GeometryUtil.getMinMax(react1), 1.0);
        // assert all coordinates of the second molecule moved right
        AtomContainerDiff.diff(react1, react2);
        for (int i = 0; i < 2; i++) {
            atom1 = react1.getAtom(0);
            atom2 = react2.getAtom(0);
            // so, y coordinates should be the same
            Assertions.assertEquals(atom1.getPoint2d().y, atom2.getPoint2d().y, 0.0);
            // but, x coordinates should not
            Assertions.assertTrue(atom1.getPoint2d().x < atom2.getPoint2d().x);
        }
    }

    @Test
    void testGetBondLengthAverage_IReaction() {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer acont = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IReaction reaction = new Reaction();
        reaction.addReactant(acont);
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addBond(0, 1, IBond.Order.SINGLE);
        Assertions.assertEquals(1.0, GeometryUtil.getBondLengthAverage(reaction), 0.0);
    }

    /**
     * Tests if the bond length average is calculated based on all
     * {@link org.openscience.cdk.interfaces.IAtomContainer}s in the IReaction.
     */
    @Test
    void testGetBondLengthAverage_MultiReaction() {
        IReaction reaction = new Reaction();

        // mol 1
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer acont = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        reaction.addReactant(acont);
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addBond(0, 1, IBond.Order.SINGLE);

        // mol 2
        atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(3, 0));
        acont = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        reaction.addProduct(acont);
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addBond(0, 1, IBond.Order.SINGLE);

        Assertions.assertEquals(2.0, GeometryUtil.getBondLengthAverage(reaction), 0.0);
    }

    @Test
    void testShiftReactionVertical_IAtomContainer_Rectangle2D_Rectangle2D_double() throws Exception {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer react1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IReaction reaction = new Reaction();
        reaction.addReactant(react1);
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        react1.addBond(0, 1, IBond.Order.SINGLE);
        IReaction reaction2 = (IReaction) reaction.clone();
        IAtomContainer react2 = reaction2.getReactants().getAtomContainer(0);

        // shift the second reaction up
        GeometryUtil.shiftReactionVertical(reaction2, GeometryUtil.getMinMax(react2), GeometryUtil.getMinMax(react1),
                                           1.0);
        // assert all coordinates of the second reaction moved up
        AtomContainerDiff.diff(react1, react2);
        for (int i = 0; i < 2; i++) {
            atom1 = react1.getAtom(0);
            atom2 = react2.getAtom(0);
            // so, x coordinates should be the same
            Assertions.assertEquals(atom1.getPoint2d().x, atom2.getPoint2d().x, 0.0);
            // but, y coordinates should not
            Assertions.assertTrue(atom1.getPoint2d().y < atom2.getPoint2d().y);
        }
    }

    /**
     * Unit tests that tests the situation where two horizontal two-atom
     * molecules are with the same y coordinates.
     *
     * @throws Exception Thrown when the cloning failed.
     */
    @Test
    void testShiftReactionVertical_Two_horizontal_molecules() throws Exception {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer react1 = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        IReaction reaction = new Reaction();
        reaction.addReactant(react1);
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        react1.addBond(0, 1, IBond.Order.SINGLE);
        IReaction reaction2 = (IReaction) reaction.clone();
        IAtomContainer react2 = reaction2.getReactants().getAtomContainer(0);

        // shift the second reaction up
        GeometryUtil.shiftReactionVertical(reaction2, GeometryUtil.getMinMax(react2), GeometryUtil.getMinMax(react1),
                                           1.0);
        // assert all coordinates of the second reaction moved up
        AtomContainerDiff.diff(react1, react2);
        for (int i = 0; i < 2; i++) {
            atom1 = react1.getAtom(0);
            atom2 = react2.getAtom(0);
            // so, x coordinates should be the same
            Assertions.assertEquals(atom1.getPoint2d().x, atom2.getPoint2d().x, 0.0);
            // but, y coordinates should not
            Assertions.assertTrue(atom1.getPoint2d().y < atom2.getPoint2d().y);
        }
    }

    @Test
    void testGetBestAlignmentForLabelXY() {
        final String TYPE = "C";
        IAtom zero = new Atom("O");
        zero.setPoint2d(new Point2d());
        IAtom pX = new Atom(TYPE);
        pX.setPoint2d(new Point2d(1, 0));
        IAtom nX = new Atom(TYPE);
        nX.setPoint2d(new Point2d(-1, 0));
        IAtom pY = new Atom(TYPE);
        pY.setPoint2d(new Point2d(0, 1));
        IAtom nY = new Atom(TYPE);
        nY.setPoint2d(new Point2d(0, -1));

        Assertions.assertEquals(-1, alignmentTestHelper(zero, pX));
        Assertions.assertEquals(1, alignmentTestHelper(zero, nX));
        Assertions.assertEquals(-2, alignmentTestHelper(zero, pY));
        Assertions.assertEquals(2, alignmentTestHelper(zero, nY));

        Assertions.assertEquals(1, alignmentTestHelper(zero, pY, nY));
    }

    @Test
    void medianBondLength() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1.5)));
        container.addAtom(atomAt(new Point2d(0, -1.5)));
        container.addAtom(atomAt(new Point2d(0, 5)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(0, 2, IBond.Order.SINGLE);
        container.addBond(0, 3, IBond.Order.SINGLE);
        assertThat(GeometryUtil.getBondLengthMedian(container), is(1.5));
    }

    @Test
    void medianBondLengthNoBonds() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
            container.addAtom(atomAt(new Point2d(0, 0)));
            container.addAtom(atomAt(new Point2d(0, 1.5)));
            container.addAtom(atomAt(new Point2d(0, -1.5)));
            container.addAtom(atomAt(new Point2d(0, 5)));
            GeometryUtil.getBondLengthMedian(container);
        });
    }

    @Test
    void medianBondLengthNoPoints() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
            container.addAtom(atomAt(new Point2d(0, 0)));
            container.addAtom(atomAt(new Point2d(0, 1.5)));
            container.addAtom(atomAt(null));
            container.addAtom(atomAt(new Point2d(0, 5)));
            container.addBond(0, 1, IBond.Order.SINGLE);
            container.addBond(0, 2, IBond.Order.SINGLE);
            container.addBond(0, 3, IBond.Order.SINGLE);
            GeometryUtil.getBondLengthMedian(container);
        });
    }

    @Test
    void medianBondLengthOneBond() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1.5)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        assertThat(GeometryUtil.getBondLengthMedian(container), is(1.5));
    }

    @Test
    void medianBondLengthWithZeroLengthBonds() {
        IAtomContainer container = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1)));
        container.addAtom(atomAt(new Point2d(0, 2)));
        container.addAtom(atomAt(new Point2d(0, 3)));
        for (int i = 0; i < container.getAtomCount()-1; i++) {
            container.addBond(i, i+1, IBond.Order.SINGLE);
        }
        assertThat(GeometryUtil.getBondLengthMedian(container), is(1d));
    }

    @Test
    void testBasicReflection() {
        IAtomContainer container = SilentChemObjectBuilder.getInstance().newAtomContainer();
        container.addAtom(atomAt(new Point2d(-1, 1)));
        container.addAtom(atomAt(new Point2d(-1, -1)));
        container.addAtom(atomAt(new Point2d(1, 1)));
        container.addAtom(atomAt(new Point2d(1, -1)));
        assertThat(container.getAtom(0).getPoint2d().x, IsCloseTo.closeTo(-1, 0.001));
        assertThat(container.getAtom(0).getPoint2d().y, IsCloseTo.closeTo(1, 0.001));
        assertThat(container.getAtom(3).getPoint2d().x, IsCloseTo.closeTo(1, 0.001));
        assertThat(container.getAtom(3).getPoint2d().y, IsCloseTo.closeTo(-1, 0.001));
        GeometryUtil.reflect(Arrays.asList(container.getAtom(0),
                container.getAtom(3)),
                new Point2d(-1,-1), new Point2d(1,1));
        assertThat(container.getAtom(0).getPoint2d().x, IsCloseTo.closeTo(1, 0.001));
        assertThat(container.getAtom(0).getPoint2d().y, IsCloseTo.closeTo(-1, 0.001));
        assertThat(container.getAtom(3).getPoint2d().x, IsCloseTo.closeTo(-1, 0.001));
        assertThat(container.getAtom(3).getPoint2d().y, IsCloseTo.closeTo(1, 0.001));
    }

    @Test
    void testBasicReflectionBonds() throws IOException, CDKException {
        String molfile = "\n" +
                "  Mrv1810 05052109112D          \n" +
                "\n" +
                " 10 10  0  0  0  0            999 V2000\n" +
                "   -1.5138   -0.0596    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -2.2282   -0.4722    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -2.2282   -1.2972    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.5138   -1.7097    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.7993   -1.2972    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.7993   -0.4722    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -1.5138    0.7654    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.7993    1.1779    0.0000 C   0  0  1  0  0  0  0  0  0  0  0  0\n" +
                "   -0.7993    2.0029    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.0848    0.7654    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  3  4  1  0  0  0  0\n" +
                "  4  5  1  0  0  0  0\n" +
                "  5  6  1  0  0  0  0\n" +
                "  1  6  1  0  0  0  0\n" +
                "  7  8  1  0  0  0  0\n" +
                "  8  9  1  0  0  0  0\n" +
                "  8 10  1  1  0  0  0\n" +
                "  1  7  2  0  0  0  0\n" +
                "M  END\n";
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            List<IAtom> atoms = new ArrayList<>();
            atoms.add(mol.getAtom(6));
            atoms.add(mol.getAtom(7));
            atoms.add(mol.getAtom(8));
            atoms.add(mol.getAtom(9));
            GeometryUtil.reflect(atoms, mol.getBond(9));
            assertThat(mol.getBond(8).getDisplay(), is(IBond.Display.WedgedHashBegin));
            assertThat(mol.getBond(8).getStereo(), is(IBond.Stereo.DOWN));
        }
    }

    private IAtom atomAt(Point2d p) {
        IAtom atom = new Atom("C");
        atom.setPoint2d(p);
        return atom;
    }

    private int alignmentTestHelper(IAtom zero, IAtom... pos) {
        IAtomContainer mol = DefaultChemObjectBuilder.getInstance().newAtomContainer();
        mol.addAtom(zero);
        for (IAtom atom : pos) {
            mol.addAtom(atom);
            mol.addBond(new Bond(zero, atom));
        }
        return GeometryUtil.getBestAlignmentForLabelXY(mol, zero);
    }


    @Test
    public void findCrossingBonds_1() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("[C@@H]12C[C@@H](CCC1)CC2 |(-7.14,4.4,;-7.68,3.86,;-6.9,4.12,;-6.14,3.78,;-5.91,4.58,;-6.32,4.08,;-7.07,4.93,;-7.32,5.2,)|");
        List<Map.Entry<IBond,IBond>> crossing = GeometryUtil.intersectingBonds(mol);
        Assertions.assertEquals(1, crossing.size());
        Assertions.assertEquals(5, crossing.get(0).getKey().getIndex());
        Assertions.assertEquals(6, crossing.get(0).getValue().getIndex());
    }

    @Test
    public void findCrossingBonds_2() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("C12C3C4C1C5C2C3C45 |(2.39,4.98,;3.03,4.82,;3.03,3.8,;2.38,3.96,;1.56,3.84,;1.57,4.86,;2.22,4.7,;2.21,3.68,)|");
        List<Map.Entry<IBond,IBond>> crossing = GeometryUtil.intersectingBonds(mol);
        Assertions.assertEquals(2, crossing.size());
        Assertions.assertEquals(3, crossing.get(0).getKey().getIndex());
        Assertions.assertEquals(8, crossing.get(0).getValue().getIndex());
        Assertions.assertEquals(4, crossing.get(1).getKey().getIndex());
        Assertions.assertEquals(9, crossing.get(1).getValue().getIndex());
    }

    @Test
    public void findCrossingBonds_3() throws InvalidSmilesException {
        SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
        IAtomContainer mol = smipar.parseSmiles("c12c3c4c5c1c6c7c8c2c9c%10c3c%11c%12c4c%13c%14c5c%15c6c%16c7c%17c%18c8c9c%19c%20c%10c%11c%21c%22c%12c%13c%23c%24c%14c%15c%25c%16c%26c%17c%27c%18c%19c%28c%20c%21c%29c%22c%23c%30c%24c%25c%26c%31c%27c%28c%29c%30%31 |(-1.18,4.19,;-1.25,3.95,;-2.89,2.89,;-3.4,2.51,;-2.35,3.5,;-1.85,2.36,;-0.24,2.34,;0.9,3.16,;0.62,4.13,;1.64,3.95,;1.44,3.76,;-0.18,3.78,;-0.22,2.55,;-1.68,1.63,;-3.12,1.86,;-3.79,0.18,;-4.25,-0.17,;-4.09,1.02,;-3.53,0.2,;-2.43,0.88,;-1.19,0.06,;0.04,0.83,;1.56,0.18,;2.62,0.94,;2.4,2.6,;2.78,2.95,;3.76,1.66,;3.54,1.5,;2.21,2.47,;1.25,1.8,;1.42,-0.1,;0.01,-0.96,;-1.58,-0.17,;-2.92,-1.05,;-2.52,-2.69,;-3,-2.99,;-3.85,-1.66,;-3.41,-1.49,;-2.24,-2.31,;-1.13,-1.6,;0.24,-2.29,;1.69,-1.43,;2.9,-1.69,;3.49,-0.25,;4.04,0.09,;4.04,-1.03,;3.71,-0.29,;2.72,-0.98,;2.01,-2.61,;0.36,-2.64,;-0.87,-3.45,;-0.47,-4.19,;-1.75,-3.85,;-1.41,-3.59,;0.2,-3.49,;1.42,-3.81,;2.81,-2.84,;3.34,-2.68,;2.44,-3.41,;1.21,-4.2,)| fullerene");
        List<Map.Entry<IBond,IBond>> crossing = GeometryUtil.intersectingBonds(mol);
        Assertions.assertEquals(22, crossing.size());
    }
}
