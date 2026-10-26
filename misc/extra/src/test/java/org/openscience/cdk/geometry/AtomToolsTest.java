/*
 * =====================================
 *  Copyright (c) 2020 NextMove Software
 * =====================================
 */

package org.openscience.cdk.geometry;

import org.hamcrest.number.IsCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

import javax.vecmath.Point3d;
import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;

class AtomToolsTest {

    @Test
    void calculate3DCoordinates3() throws Exception {
        String molfile = "\n" +
                "  Mrv1810 12152010123D          \n" +
                "\n" +
                "  4  3  0  0  0  0            999 V2000\n" +
                "    2.0575    1.4744   -0.0102 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "    1.5159    0.0200    0.0346 C   0  0  1  0  0  0  0  0  0  0  0  0\n" +
                "    2.0575   -0.7460    1.2717 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "   -0.0359   -0.0059   -0.0102 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
                "  1  2  1  0  0  0  0\n" +
                "  2  3  1  0  0  0  0\n" +
                "  2  4  1  0  0  0  0\n" +
                "M  END\n";
        try (MDLV2000Reader mdlr = new MDLV2000Reader(new StringReader(molfile))) {
            IAtomContainer mol = mdlr.read(SilentChemObjectBuilder.getInstance().newAtomContainer());
            Point3d newP = AtomTools.calculate3DCoordinates3(
                    mol.getAtom(1).getPoint3d(),
                    mol.getAtom(0).getPoint3d(),
                    mol.getAtom(2).getPoint3d(),
                    mol.getAtom(3).getPoint3d(), 1.5);
            Assertions.assertNotNull(newP);
            assertThat(newP.x, IsCloseTo.closeTo(2.0160, 0.001));
            assertThat(newP.y, IsCloseTo.closeTo(-0.6871, 0.001));
            assertThat(newP.z, IsCloseTo.closeTo(-1.1901, 0.001));
        }
    }

    @Test
    void add3DCoordinates1_secondaryAmine() {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();

        IAtom c1 = mol.getBuilder().newInstance(IAtom.class, "C");
        c1.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        c1.setCovalentRadius(0.77);

        IAtom n = mol.getBuilder().newInstance(IAtom.class, "N");
        n.setPoint3d(new Point3d(1.4, 0.0, 0.0));
        n.setCovalentRadius(0.75);

        IAtom c2 = mol.getBuilder().newInstance(IAtom.class, "C");
        c2.setPoint3d(new Point3d(2.1, 1.2, 0.0));
        c2.setCovalentRadius(0.77);

        IAtom h = mol.getBuilder().newInstance(IAtom.class, "H");
        h.setPoint3d(null);
        h.setCovalentRadius(0.37);

        mol.addAtom(c1);
        mol.addAtom(n);
        mol.addAtom(c2);
        mol.addAtom(h);

        mol.addBond(mol.getBuilder().newInstance(IBond.class, c1, n, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, c2, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h, IBond.Order.SINGLE));

        AtomTools.add3DCoordinates1(mol);

        Point3d hPoint = h.getPoint3d();
        Assertions.assertNotNull(hPoint);
        Point3d nPoint = n.getPoint3d();
        Assertions.assertEquals(1.12, nPoint.distance(hPoint), 0.05);
        // Non-planar pyramidal geometry: c1, n, c2 are in z=0 plane, h should be out of plane
        Assertions.assertTrue(Math.abs(hPoint.z) > 0.1, "Secondary amine H should have non-planar pyramidal geometry");
    }

    @Test
    void add3DCoordinates1_primaryAmine() {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();

        IAtom c = mol.getBuilder().newInstance(IAtom.class, "C");
        c.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        c.setCovalentRadius(0.77);

        IAtom n = mol.getBuilder().newInstance(IAtom.class, "N");
        n.setPoint3d(new Point3d(1.4, 0.0, 0.0));
        n.setCovalentRadius(0.75);

        IAtom h1 = mol.getBuilder().newInstance(IAtom.class, "H");
        h1.setPoint3d(null);
        h1.setCovalentRadius(0.37);

        IAtom h2 = mol.getBuilder().newInstance(IAtom.class, "H");
        h2.setPoint3d(null);
        h2.setCovalentRadius(0.37);

        mol.addAtom(c);
        mol.addAtom(n);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, n, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h1, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, n, h2, IBond.Order.SINGLE));

        AtomTools.add3DCoordinates1(mol);

        Assertions.assertNotNull(h1.getPoint3d());
        Assertions.assertNotNull(h2.getPoint3d());
        Point3d nPoint = n.getPoint3d();
        Assertions.assertEquals(1.12, nPoint.distance(h1.getPoint3d()), 0.05);
        Assertions.assertEquals(1.12, nPoint.distance(h2.getPoint3d()), 0.05);
        Assertions.assertTrue(h1.getPoint3d().distance(h2.getPoint3d()) > 0.5);
    }

    @Test
    void add3DCoordinates1_alcohol() {
        IAtomContainer mol = SilentChemObjectBuilder.getInstance().newAtomContainer();

        IAtom c = mol.getBuilder().newInstance(IAtom.class, "C");
        c.setPoint3d(new Point3d(0.0, 0.0, 0.0));
        c.setCovalentRadius(0.77);

        IAtom o = mol.getBuilder().newInstance(IAtom.class, "O");
        o.setPoint3d(new Point3d(1.4, 0.0, 0.0));
        o.setCovalentRadius(0.66);

        IAtom h = mol.getBuilder().newInstance(IAtom.class, "H");
        h.setPoint3d(null);
        h.setCovalentRadius(0.37);

        mol.addAtom(c);
        mol.addAtom(o);
        mol.addAtom(h);

        mol.addBond(mol.getBuilder().newInstance(IBond.class, c, o, IBond.Order.SINGLE));
        mol.addBond(mol.getBuilder().newInstance(IBond.class, o, h, IBond.Order.SINGLE));

        AtomTools.add3DCoordinates1(mol);

        Assertions.assertNotNull(h.getPoint3d());
        Point3d oPoint = o.getPoint3d();
        Assertions.assertEquals(1.03, oPoint.distance(h.getPoint3d()), 0.05);
    }
}