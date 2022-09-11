/* Copyright (C) 2004-2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.geometry;

import java.awt.geom.Rectangle2D;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
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
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.diff.AtomContainerDiff;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * This class defines regression tests that should ensure that the source code
 * of the {@link GeometryTools} is not broken.
 *
 * @cdk.module test-standard
 *
 * @author     Egon Willighagen
 * @cdk.created    2004-01-30
 *
 * @see org.openscience.cdk.geometry.GeometryTools
 */
class GeometryToolsTest extends CDKTestCase {

    @Test
    void testHas2DCoordinates_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertTrue(GeometryTools.has2DCoordinates(container));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertFalse(GeometryTools.has2DCoordinates(container));
    }

    @Test
    void testHas2DCoordinates_EmptyAtomContainer() {
        IAtomContainer container = new AtomContainer();
        Assertions.assertFalse(GeometryTools.has2DCoordinates(container));
        Assertions.assertFalse(GeometryTools.has2DCoordinates((IAtomContainer) null));
    }

    @Test
    void testHas2DCoordinates_Partial() {
        IAtomContainer container = new AtomContainer();
        Atom atom1 = new Atom("C");
        Atom atom2 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        container.addAtom(atom1);
        Assertions.assertTrue(GeometryTools.has2DCoordinates(container));
        container.addAtom(atom2);
        Assertions.assertFalse(GeometryTools.has2DCoordinates(container));
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
        molOne = reader.read(new AtomContainer());
        Assertions.assertTrue(GeometryTools.has2DCoordinates(molOne));
    }

    @Test
    void get2DCoordinateCoverage_EmptyAtomContainer() {
        IAtomContainer container = new AtomContainer();
        Assertions.assertEquals(GeometryTools.CoordinateCoverage.NONE, GeometryTools.get2DCoordinateCoverage(container));
        Assertions.assertEquals(GeometryTools.CoordinateCoverage.NONE, GeometryTools.get2DCoordinateCoverage(null));
    }

    @Test
    void get2DCoordinateCoverage_Partial() {

        IAtomContainer container = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint2d(new Point2d(1, 1));
        atom3.setPoint2d(new Point2d(1, 1));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryTools.CoordinateCoverage.PARTIAL, GeometryTools.get2DCoordinateCoverage(container));

    }

    @Test
    void get2DCoordinateCoverage_Full() {

        IAtomContainer container = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint2d(new Point2d(1, 1));
        atom2.setPoint2d(new Point2d(2, 1));
        atom3.setPoint2d(new Point2d(1, 2));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryTools.CoordinateCoverage.FULL, GeometryTools.get2DCoordinateCoverage(container));

    }

    @Test
    void get2DCoordinateCoverage_None_3D() {

        IAtomContainer container = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint3d(new Point3d(1, 1, 0));
        atom2.setPoint3d(new Point3d(2, 1, 0));
        atom3.setPoint3d(new Point3d(1, 2, 0));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryTools.CoordinateCoverage.NONE, GeometryTools.get2DCoordinateCoverage(container));

    }

    @Test
    void testTranslateAllPositive_IAtomContainer() {
        IAtomContainer container = new AtomContainer();
        IAtom atom = new Atom(Elements.CARBON);
        atom.setPoint2d(new Point2d(-3, -2));
        container.addAtom(atom);
        GeometryTools.translateAllPositive(container);
        Assertions.assertTrue(0 <= atom.getPoint2d().x);
        Assertions.assertTrue(0 <= atom.getPoint2d().y);
    }

    @Test
    void testGetLength2D_IBond() {
        Atom o = new Atom("O", new Point2d(0.0, 0.0));
        Atom c = new Atom("C", new Point2d(1.0, 0.0));
        Bond bond = new Bond(c, o);

        Assertions.assertEquals(1.0, GeometryTools.getLength2D(bond), 0.001);
    }

    @Test
    void testMapAtomsOfAlignedStructures() throws Exception {
        String filenameMolOne = "murckoTest6_3d_2.mol";
        String filenameMolTwo = "murckoTest6_3d.mol";
        //String filenameMolTwo = "data/mdl/murckoTest6_3d_2.mol";
        InputStream ins = this.getClass().getResourceAsStream(filenameMolOne);
        IAtomContainer molOne;
        IAtomContainer molTwo;
        Map<Integer, Integer> mappedAtoms = new HashMap<>();
        MDLV2000Reader reader = new MDLV2000Reader(ins, Mode.STRICT);
        molOne = reader.read(new AtomContainer());

        ins = this.getClass().getResourceAsStream(filenameMolTwo);
        reader = new MDLV2000Reader(ins, Mode.STRICT);
        molTwo = reader.read(new AtomContainer());

        mappedAtoms = AtomMappingTools.mapAtomsOfAlignedStructures(molOne, molTwo, mappedAtoms);
        //logger.debug("mappedAtoms:"+mappedAtoms.toString());
        //logger.debug("***** ANGLE VARIATIONS *****");
        double AngleRMSD = GeometryTools.getAngleRMSD(molOne, molTwo, mappedAtoms);
        //logger.debug("The Angle RMSD between the first and the second structure is :"+AngleRMSD);
        //logger.debug("***** ALL ATOMS RMSD *****");
        Assertions.assertEquals(0.2, AngleRMSD, 0.1);
        double AllRMSD = GeometryTools.getAllAtomRMSD(molOne, molTwo, mappedAtoms, true);
        //logger.debug("The RMSD between the first and the second structure is :"+AllRMSD);
        Assertions.assertEquals(0.242, AllRMSD, 0.001);
        //logger.debug("***** BOND LENGTH RMSD *****");
        double BondLengthRMSD = GeometryTools.getBondLengthRMSD(molOne, molTwo, mappedAtoms, true);
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
        GeometryTools.rotate(ac, new Point2d(0, 0), Math.PI / 2);
        Assertions.assertEquals(atom1.getPoint2d().x, -1, .2);
        Assertions.assertEquals(atom1.getPoint2d().y, 1, .2);
        Assertions.assertEquals(atom2.getPoint2d().x, 0, .2);
        Assertions.assertEquals(atom2.getPoint2d().y, 1, .2);
        atom2.setPoint2d(new Point2d(0, 0));
        GeometryTools.rotate(ac, new Point2d(0, 0), Math.PI);
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
        double[] minmax = GeometryTools.getMinMax(ac);
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
        double[] minmax = GeometryTools.getMinMax(ac);
        Assertions.assertEquals(-5, minmax[0], .1);
        Assertions.assertEquals(-1, minmax[1], .1);
        Assertions.assertEquals(-2, minmax[2], .1);
        Assertions.assertEquals(-1, minmax[3], .1);
    }

    @Test
    void testGetRectangle2D_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(2, 2));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(5, 1));
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Rectangle2D rectangle = GeometryTools.getRectangle2D(container);
        Assertions.assertEquals(2.0, rectangle.getMinX(), 0.0);
        Assertions.assertEquals(3.0, rectangle.getWidth(), 0.0);
        Assertions.assertEquals(1.0, rectangle.getMinY(), 0.0);
        Assertions.assertEquals(1.0, rectangle.getHeight(), 0.0);
    }

    @Test
    void testRotate_IAtom_Point3d_Point3d_double() {
        Atom atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 0));
        GeometryTools.rotate(atom1, new Point3d(2, 0, 0), new Point3d(2, 2, 0), 90);
        assertEquals(new Point3d(2.0, 1.0, 1.0), atom1.getPoint3d(), 0.2);
    }

    @Test
    void testNormalize_Point3d() {
        Point3d p = new Point3d(1, 1, 0);
        GeometryTools.normalize(p);
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
        Point2d p = GeometryTools.get2DCenter(ac);
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
        Point2d p = GeometryTools.get2DCentreOfMass(ac);
        Assertions.assertNotNull(p);
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testGet2DCenter_arrayIAtom() {
        IAtomContainer container = new AtomContainer();
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        container.addAtom(atom1);
        container.addAtom(atom2);
        Point2d p = GeometryTools.get2DCenter(container.atoms());
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
        Point2d p = GeometryTools.get2DCenter(ac);
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
        Point2d p = GeometryTools.get2DCenter(ac.atoms());
        Assertions.assertEquals(p.x, 1.0, .1);
        Assertions.assertEquals(p.y, 0.5, .1);
    }

    @Test
    void testHas2DCoordinates_IAtom() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Assertions.assertTrue(GeometryTools.has2DCoordinates(atom1));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        Assertions.assertFalse(GeometryTools.has2DCoordinates(atom1));
    }

    @Test
    void testHas2DCoordinates_IBond() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IBond bond = new Bond(atom1, atom2);
        Assertions.assertTrue(GeometryTools.has2DCoordinates(bond));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        bond = new Bond(atom1, atom2);
        Assertions.assertFalse(GeometryTools.has2DCoordinates(bond));
    }

    @Test
    void testHas2DCoordinatesNew_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertEquals(2, GeometryTools.has2DCoordinatesNew(container));

        atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 1));
        container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertEquals(1, GeometryTools.has2DCoordinatesNew(container));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertEquals(0, GeometryTools.has2DCoordinatesNew(container));
    }

    @Test
    void testHas3DCoordinates_IAtomContainer() {
        Atom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 1));
        Atom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertFalse(GeometryTools.has3DCoordinates(container));

        atom1 = new Atom("C");
        atom1.setPoint3d(new Point3d(1, 1, 1));
        atom2 = new Atom("C");
        atom2.setPoint3d(new Point3d(1, 0, 5));
        container = new AtomContainer();
        container.addAtom(atom1);
        container.addAtom(atom2);
        Assertions.assertTrue(GeometryTools.has3DCoordinates(container));
    }

    @Test
    void testHas3DCoordinates_EmptyAtomContainer() {
        IAtomContainer container = new AtomContainer();
        Assertions.assertFalse(GeometryTools.has3DCoordinates(container));
        Assertions.assertFalse(GeometryTools.has3DCoordinates((IAtomContainer) null));
    }

    @Test
    void get3DCoordinateCoverage_EmptyAtomContainer() {
        IAtomContainer container = new AtomContainer();
        Assertions.assertEquals(GeometryTools.CoordinateCoverage.NONE, GeometryTools.get3DCoordinateCoverage(container));
        Assertions.assertEquals(GeometryTools.CoordinateCoverage.NONE, GeometryTools.get3DCoordinateCoverage(null));
    }

    @Test
    void get3DCoordinateCoverage_Partial() {

        IAtomContainer container = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint3d(new Point3d(1, 1, 0));
        atom3.setPoint3d(new Point3d(1, 1, 0));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryTools.CoordinateCoverage.PARTIAL, GeometryTools.get3DCoordinateCoverage(container));

    }

    @Test
    void get3DCoordinateCoverage_Full() {

        IAtomContainer container = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint3d(new Point3d(1, 1, 0));
        atom2.setPoint3d(new Point3d(2, 1, 0));
        atom3.setPoint3d(new Point3d(1, 2, 0));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryTools.CoordinateCoverage.FULL, GeometryTools.get3DCoordinateCoverage(container));

    }

    @Test
    void get3DCoordinateCoverage_None_2D() {

        IAtomContainer container = new AtomContainer();

        IAtom atom1 = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");

        atom1.setPoint2d(new Point2d(1, 1));
        atom2.setPoint2d(new Point2d(2, 1));
        atom3.setPoint2d(new Point2d(1, 2));

        container.addAtom(atom1);
        container.addAtom(atom2);
        container.addAtom(atom3);

        Assertions.assertEquals(GeometryTools.CoordinateCoverage.NONE, GeometryTools.get3DCoordinateCoverage(container));

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
        GeometryTools.translateAllPositive(ac);
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
        Assertions.assertEquals(GeometryTools.getLength2D(bond), 2.23, 0.01);
    }

    @Test
    void testGetClosestAtom_Multiatom() {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(-1, -1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtom atom3 = new Atom("C");
        atom3.setPoint2d(new Point2d(5, 0));
        IAtomContainer acont = new AtomContainer();
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addAtom(atom3);
        Assertions.assertEquals(atom2, GeometryTools.getClosestAtom(acont, atom1));
        Assertions.assertEquals(atom1, GeometryTools.getClosestAtom(acont, atom2));
        Assertions.assertEquals(atom2, GeometryTools.getClosestAtom(acont, atom3));
    }

    @Test
    void testGetClosestAtom_Double_Double_IAtomContainer_IAtom() {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(1, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(5, 0));
        IAtomContainer acont = new AtomContainer();
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        Assertions.assertEquals(atom2, GeometryTools.getClosestAtom(1.0, 0.0, acont, atom1));
        Assertions.assertEquals(atom1, GeometryTools.getClosestAtom(1.0, 0.0, acont, null));
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
        IAtomContainer acont = new AtomContainer();
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        Assertions.assertEquals(atom2, GeometryTools.getClosestAtom(acont, atom1));
        Assertions.assertEquals(atom1, GeometryTools.getClosestAtom(acont, atom2));
    }

    @Test
    void testShiftContainerHorizontal_IAtomContainer_Rectangle2D_Rectangle2D_double() throws Exception {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer react1 = new AtomContainer();
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        IAtomContainer react2 = react1.clone();

        // shift the second molecule right
        GeometryTools.shiftContainer(react2, GeometryTools.getRectangle2D(react2),
                GeometryTools.getRectangle2D(react1), 1.0);
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
        IAtomContainer react1 = new AtomContainer();
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        IAtomContainer react2 = react1.clone();

        // shift the second molecule right
        GeometryTools.shiftContainer(react2, GeometryTools.getRectangle2D(react2),
                GeometryTools.getRectangle2D(react1), 1.0);
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
        IAtomContainer acont = new AtomContainer();
        IReaction reaction = new Reaction();
        reaction.addReactant(acont);
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addBond(0, 1, IBond.Order.SINGLE);
        Assertions.assertEquals(1.0, GeometryTools.getBondLengthAverage(reaction), 0.0);
    }

    /**
     * Tests if the bond length average is calculated based on all
     * {@link IAtomContainer}s in the IReaction.
     */
    @Test
    void testGetBondLengthAverage_MultiReaction() {
        IReaction reaction = new Reaction();

        // mol 1
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer acont = new AtomContainer();
        reaction.addReactant(acont);
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addBond(0, 1, IBond.Order.SINGLE);

        // mol 2
        atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 0));
        atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(3, 0));
        acont = new AtomContainer();
        reaction.addProduct(acont);
        acont.addAtom(atom1);
        acont.addAtom(atom2);
        acont.addBond(0, 1, IBond.Order.SINGLE);

        Assertions.assertEquals(2.0, GeometryTools.getBondLengthAverage(reaction), 0.0);
    }

    @Test
    void testShiftReactionVertical_IAtomContainer_Rectangle2D_Rectangle2D_double() throws Exception {
        IAtom atom1 = new Atom("C");
        atom1.setPoint2d(new Point2d(0, 1));
        IAtom atom2 = new Atom("C");
        atom2.setPoint2d(new Point2d(1, 0));
        IAtomContainer react1 = new AtomContainer();
        IReaction reaction = new Reaction();
        reaction.addReactant(react1);
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        react1.addBond(0, 1, IBond.Order.SINGLE);
        IReaction reaction2 = (IReaction) reaction.clone();
        IAtomContainer react2 = reaction2.getReactants().getAtomContainer(0);

        // shift the second reaction up
        GeometryTools.shiftReactionVertical(reaction2, GeometryTools.getRectangle2D(react2),
                GeometryTools.getRectangle2D(react1), 1.0);
        // assert all coordinates of the second reaction moved up
        AtomContainerDiff.diff(react1, react2);
        LoggingToolFactory.createLoggingTool(getClass()).info("R1: " + GeometryTools.getRectangle2D(react1));
        LoggingToolFactory.createLoggingTool(getClass()).info("R2: " + GeometryTools.getRectangle2D(react2));
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
        IAtomContainer react1 = new AtomContainer();
        IReaction reaction = new Reaction();
        reaction.addReactant(react1);
        react1.addAtom(atom1);
        react1.addAtom(atom2);
        react1.addBond(0, 1, IBond.Order.SINGLE);
        IReaction reaction2 = (IReaction) reaction.clone();
        IAtomContainer react2 = reaction2.getReactants().getAtomContainer(0);

        // shift the second reaction up
        GeometryTools.shiftReactionVertical(reaction2, GeometryTools.getRectangle2D(react2),
                GeometryTools.getRectangle2D(react1), 1.0);
        // assert all coordinates of the second reaction moved up
        AtomContainerDiff.diff(react1, react2);
        LoggingToolFactory.createLoggingTool(getClass()).info("R1: " + GeometryTools.getRectangle2D(react1));
        LoggingToolFactory.createLoggingTool(getClass()).info("R2: " + GeometryTools.getRectangle2D(react2));
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
        IAtomContainer container = new AtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1.5)));
        container.addAtom(atomAt(new Point2d(0, -1.5)));
        container.addAtom(atomAt(new Point2d(0, 5)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(0, 2, IBond.Order.SINGLE);
        container.addBond(0, 3, IBond.Order.SINGLE);
        assertThat(GeometryTools.getBondLengthMedian(container), is(1.5));
    }

    @Test
    void medianBondLengthNoBonds() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1.5)));
        container.addAtom(atomAt(new Point2d(0, -1.5)));
        container.addAtom(atomAt(new Point2d(0, 5)));
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GeometryTools.getBondLengthMedian(container);
        });
    }

    @Test
    void medianBondLengthNoPoints() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1.5)));
        container.addAtom(atomAt(null));
        container.addAtom(atomAt(new Point2d(0, 5)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        container.addBond(0, 2, IBond.Order.SINGLE);
        container.addBond(0, 3, IBond.Order.SINGLE);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            GeometryTools.getBondLengthMedian(container);
        });
    }

    @Test
    void medianBondLengthOneBond() {
        IAtomContainer container = new AtomContainer();
        container.addAtom(atomAt(new Point2d(0, 0)));
        container.addAtom(atomAt(new Point2d(0, 1.5)));
        container.addBond(0, 1, IBond.Order.SINGLE);
        assertThat(GeometryTools.getBondLengthMedian(container), is(1.5));
    }

    private IAtom atomAt(Point2d p) {
        IAtom atom = new Atom("C");
        atom.setPoint2d(p);
        return atom;
    }

    private int alignmentTestHelper(IAtom zero, IAtom... pos) {
        IAtomContainer mol = new AtomContainer();
        mol.addAtom(zero);
        for (IAtom atom : pos) {
            mol.addAtom(atom);
            mol.addBond(new Bond(zero, atom));
        }
        return GeometryTools.getBestAlignmentForLabelXY(mol, zero);
    }
}
