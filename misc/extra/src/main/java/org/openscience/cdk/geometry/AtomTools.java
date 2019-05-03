/* Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 *  */
package org.openscience.cdk.geometry;

import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IBond.Order;

/**
 * A set of static utility classes for geometric calculations on Atoms.
 *
 * @author Peter Murray-Rust
 * @cdk.githash
 * @cdk.created 2003-06-14
 */
public class AtomTools {

    public final static double TETRAHEDRAL_ANGLE = 2.0 * Math.acos(1.0 / Math.sqrt(3.0));

    /**
     * Generate coordinates for all atoms which are singly bonded and have
     * no coordinates. This is useful when hydrogens are present but have
     * no coordinates. It knows about C, O, N, S only and will give tetrahedral or
     * trigonal geometry elsewhere. Bond lengths are computed from covalent radii
     * if available. Angles are tetrahedral or trigonal
     *
     * @param atomContainer the set of atoms involved
     *
     * @cdk.keyword coordinate calculation
     * @cdk.keyword 3D model
     */
    public static void add3DCoordinates1(IAtomContainer atomContainer) {
        // atoms without coordinates
        IAtomContainer noCoords = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        // get vector of possible referenceAtoms?
        IAtomContainer refAtoms = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            IAtom atom = atomContainer.getAtom(i);
            // is this atom without 3D coords, and has only one ligand?
            if (atom.getPoint3d() == null) {
                List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);
                if (connectedAtoms.size() == 1) {
                    IAtom refAtom = (IAtom) connectedAtoms.get(0);;
                    if (refAtom.getPoint3d() != null) {
                        refAtoms.addAtom(refAtom);
                        // store atoms with no coords and ref atoms in a
                        // single container
                        noCoords.addAtom(atom);
                        noCoords.addAtom(refAtom);
                        // bond is required to extract ligands
                        noCoords.addBond(atomContainer.getBuilder().newInstance(IBond.class, atom, refAtom,
                                Order.SINGLE));
                    }
                }
            }
        }
        // now add coordinates to ligands of reference atoms
        // use default length of 1.0, which can be adjusted later
        double length = 1.0;
        double angle = TETRAHEDRAL_ANGLE;
        for (int i = 0; i < refAtoms.getAtomCount(); i++) {
            IAtom refAtom = refAtoms.getAtom(i);
            List<IAtom> noCoordLigands = noCoords.getConnectedAtomsList(refAtom);
            int nLigands = noCoordLigands.size();
            int nwanted = nLigands;
            String elementType = refAtom.getSymbol();
            // try to deal with lone pairs on small hetero
            if (elementType.equals("N") || elementType.equals("O") || elementType.equals("S")) {
                nwanted = 3;
            }
            Point3d[] newPoints = calculate3DCoordinatesForLigands(atomContainer, refAtom, nwanted, length, angle);
            for (int j = 0; j < nLigands; j++) {
                IAtom ligand = (IAtom) noCoordLigands.get(j);
                Point3d newPoint = rescaleBondLength(refAtom, ligand, newPoints[j]);
                ligand.setPoint3d(newPoint);
            }
        }
    }

    /**
     * Rescales Point2 so that length 1-2 is sum of covalent radii.
     * if covalent radii cannot be found, use bond length of 1.0
     *
     * @param  atom1  stationary atom
     * @param  atom2  movable atom
     * @param  point2 coordinates for atom 2
     * @return        new coords for atom 2
     */
    public static Point3d rescaleBondLength(IAtom atom1, IAtom atom2, Point3d point2) {
        Point3d point1 = atom1.getPoint3d();
        double d1 = atom1.getCovalentRadius();
        double d2 = atom2.getCovalentRadius();
        // in case we have no covalent radii, set to 1.0
        double distance = (d1 < 0.1 || d2 < 0.1) ? 1.0 : atom1.getCovalentRadius() + atom2.getCovalentRadius();
        Vector3d vect = new Vector3d(point2);
        vect.sub(point1);
        vect.normalize();
        vect.scale(distance);
        Point3d newPoint = new Point3d(point1);
        newPoint.add(vect);
        return newPoint;
    }

    /**
     * Adds 3D coordinates for singly-bonded ligands of a reference atom (A).
     * Initially designed for hydrogens. The ligands of refAtom are identified
     * and those with 3D coordinates used to generate the new points. (This
     * allows structures with partially known 3D coordinates to be used, as when
     * groups are added.)
     * "Bent" and "non-planar" groups can be formed by taking a subset of the
     * calculated points. Thus R-NH2 could use 2 of the 3 points calculated
     * from (1,iii)
     * nomenclature: A is point to which new ones are "attached".
     *     A may have ligands B, C...
     *     B may have ligands J, K..
     *     points X1, X2... are returned
     * The cases (see individual routines, which use idealised geometry by default):
     * (0) zero ligands of refAtom. The resultant points are randomly oriented:
     *    (i) 1 points  required; +x,0,0
     *    (ii) 2 points: use +x,0,0 and -x,0,0
     *    (iii) 3 points: equilateral triangle in xy plane
     *    (iv) 4 points x,x,x, x,-x,-x, -x,x,-x, -x,-x,x
     * (1a) 1 ligand(B) of refAtom which itself has a ligand (J)
     *    (i) 1 points  required; vector along AB vector
     *    (ii) 2 points: 2 vectors in ABJ plane, staggered and eclipsed wrt J
     *    (iii) 3 points: 1 staggered wrt J, the others +- gauche wrt J
     * (1b) 1 ligand(B) of refAtom which has no other ligands. A random J is
     * generated and (1a) applied
     * (2) 2 ligands(B, C) of refAtom A
     *    (i) 1 points  required; vector in ABC plane bisecting AB, AC. If ABC is
     *        linear, no points
     *    (ii) 2 points: 2 vectors at angle ang, whose resultant is 2i
     * (3) 3 ligands(B, C, D) of refAtom A
     *    (i) 1 points  required; if A, B, C, D coplanar, no points.
     *       else vector is resultant of BA, CA, DA

     * fails if atom itself has no coordinates or &gt;4 ligands
     *
     * @param atomContainer describing the ligands of refAtom. It could be the
     * whole molecule, or could be a selected subset of ligands
     * @param refAtom (A) to which new ligands coordinates could be added
     * @param length A-X length
     * @param angle B-A-X angle (used in certain cases)
     * @return Point3D[] points calculated. If request could not be fulfilled (e.g.
     * too many atoms, or strange geometry, returns empty array (zero length,
     * not null)
     *
     * @cdk.keyword coordinate generation
     */
    public static Point3d[] calculate3DCoordinatesForLigands(IAtomContainer atomContainer, IAtom refAtom, int nwanted,
            double length, double angle) {
        Point3d newPoints[] = new Point3d[0];
        Point3d aPoint = refAtom.getPoint3d();
        // get ligands
        List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(refAtom);
        if (connectedAtoms == null) {
            return newPoints;
        }
        int nligands = connectedAtoms.size();
        IAtomContainer ligandsWithCoords = atomContainer.getBuilder().newInstance(IAtomContainer.class);
        for (int i = 0; i < nligands; i++) {
            IAtom ligand = connectedAtoms.get(i);
            if (ligand.getPoint3d() != null) {
                ligandsWithCoords.addAtom(ligand);
            }
        }
        int nwithCoords = ligandsWithCoords.getAtomCount();
        // too many ligands at present
        if (nwithCoords > 3) {
            return newPoints;
        }
        if (nwithCoords == 0) {
            newPoints = calculate3DCoordinates0(refAtom.getPoint3d(), nwanted, length);
        } else if (nwithCoords == 1) {
            // ligand on A
            IAtom bAtom = ligandsWithCoords.getAtom(0);
            connectedAtoms = ligandsWithCoords.getConnectedAtomsList(bAtom);
            // does B have a ligand (other than A)
            IAtom jAtom = null;
            for (int i = 0; i < connectedAtoms.size(); i++) {
                IAtom connectedAtom = connectedAtoms.get(i);
                if (!connectedAtom.equals(refAtom)) {
                    jAtom = connectedAtom;
                    break;
                }
            }
            newPoints = calculate3DCoordinates1(aPoint, bAtom.getPoint3d(),
                    (jAtom != null) ? jAtom.getPoint3d() : null, nwanted, length, angle);
        } else if (nwithCoords == 2) {
            Point3d bPoint = ligandsWithCoords.getAtom(0).getPoint3d();
            Point3d cPoint = ligandsWithCoords.getAtom(1).getPoint3d();
            newPoints = calculate3DCoordinates2(aPoint, bPoint, cPoint, nwanted, length, angle);
        } else if (nwithCoords == 3) {
            Point3d bPoint = ligandsWithCoords.getAtom(0).getPoint3d();
            Point3d cPoint = ligandsWithCoords.getAtom(1).getPoint3d();
            Point3d dPoint = ligandsWithCoords.getAtom(2).getPoint3d();
            newPoints = new Point3d[1];
            newPoints[0] = calculate3DCoordinates3(aPoint, bPoint, cPoint, dPoint, length);
        }
        return newPoints;
    }

    /**
     * Calculates substituent points.
     * Calculate substituent points for
     * (0) zero ligands of aPoint. The resultant points are randomly oriented:
     *    (i) 1 points  required; +x,0,0
     *    (ii) 2 points: use +x,0,0 and -x,0,0
     *    (iii) 3 points: equilateral triangle in xy plane
     *    (iv) 4 points x,x,x, x,-x,-x, -x,x,-x, -x,-x,x where 3x**2 = bond length
     *
     * @param aPoint to which substituents are added
     * @param nwanted number of points to calculate (1-4)
     * @param length from aPoint
     *
     * @return Point3d[] nwanted points (or zero if failed)
     */
    public static Point3d[] calculate3DCoordinates0(Point3d aPoint, int nwanted, double length) {
        Point3d[] points;
        if (nwanted == 1) {
            points = new Point3d[1];
            points[0] = new Point3d(aPoint);
            points[0].add(new Vector3d(length, 0.0, 0.0));
        } else if (nwanted == 2) {
            points = new Point3d[2];
            points[0] = new Point3d(aPoint);
            points[0].add(new Vector3d(length, 0.0, 0.0));
            points[1] = new Point3d(aPoint);
            points[1].add(new Vector3d(-length, 0.0, 0.0));
        } else if (nwanted == 3) {
            points = new Point3d[3];
            points[0] = new Point3d(aPoint);
            points[0].add(new Vector3d(length, 0.0, 0.0));
            points[1] = new Point3d(aPoint);
            points[1].add(new Vector3d(-length * 0.5, -length * 0.5 * Math.sqrt(3.0), 0.0f));
            points[2] = new Point3d(aPoint);
            points[2].add(new Vector3d(-length * 0.5, length * 0.5 * Math.sqrt(3.0), 0.0f));
        } else if (nwanted == 4) {
            points = new Point3d[4];
            double dx = length / Math.sqrt(3.0);
            points[0] = new Point3d(aPoint);
            points[0].add(new Vector3d(dx, dx, dx));
            points[1] = new Point3d(aPoint);
            points[1].add(new Vector3d(dx, -dx, -dx));
            points[2] = new Point3d(aPoint);
            points[2].add(new Vector3d(-dx, -dx, dx));
            points[3] = new Point3d(aPoint);
            points[3].add(new Vector3d(-dx, dx, -dx));
        } else
            points = new Point3d[0];
        return points;
    }

    /**
     * Calculate new point(s) X in a B-A system to form B-A-X.
     * Use C as reference for * staggering about the B-A bond
     *
     * (1a) 1 ligand(B) of refAtom (A) which itself has a ligand (C)
     *    (i) 1 points  required; vector along AB vector
     *    (ii) 2 points: 2 vectors in ABC plane, staggered and eclipsed wrt C
     *    (iii) 3 points: 1 staggered wrt C, the others +- gauche wrt C
     * If C is null, a random non-colinear C is generated
     *
     * @param aPoint to which substituents are added
     * @param nwanted number of points to calculate (1-3)
     * @param length A-X length
     * @param angle B-A-X angle
     *
     * @return Point3d[] nwanted points (or zero if failed)
     */
    public static Point3d[] calculate3DCoordinates1(Point3d aPoint, Point3d bPoint, Point3d cPoint, int nwanted,
            double length, double angle) {
        Point3d points[] = new Point3d[nwanted];
        // BA vector
        Vector3d ba = new Vector3d(aPoint);
        ba.sub(bPoint);
        ba.normalize();
        // if no cPoint, generate a random reference
        if (cPoint == null) {
            Vector3d cVector = getNonColinearVector(ba);
            cPoint = new Point3d(cVector);
        }
        // CB vector
        Vector3d cb = new Vector3d(bPoint);
        cb.sub(cPoint);
        cb.normalize();
        // if A, B, C colinear, replace C by random point
        double cbdotba = cb.dot(ba);
        if (cbdotba > 0.999999) {
            Vector3d cVector = getNonColinearVector(ba);
            cPoint = new Point3d(cVector);
            cb = new Vector3d(bPoint);
            cb.sub(cPoint);
        }
        // cbxba = c x b
        Vector3d cbxba = new Vector3d();
        cbxba.cross(cb, ba);
        cbxba.normalize();
        // create three perp axes ba, cbxba, and ax
        Vector3d ax = new Vector3d();
        ax.cross(cbxba, ba);
        ax.normalize();
        double drot = Math.PI * 2.0 / (double) nwanted;
        for (int i = 0; i < nwanted; i++) {
            double rot = (double) i * drot;
            points[i] = new Point3d(aPoint);
            Vector3d vx = new Vector3d(ba);
            vx.scale(-Math.cos(angle) * length);
            Vector3d vy = new Vector3d(ax);
            vy.scale(Math.cos(rot) * length);
            Vector3d vz = new Vector3d(cbxba);
            vz.scale(Math.sin(rot) * length);
            points[i].add(vx);
            points[i].add(vy);
            points[i].add(vz);
        }
        return points;
    }

    /**
     * Calculate new point(s) X in a B-A-C system. It forms form a B-A(-C)-X system.
     *
     * (2) 2 ligands(B, C) of refAtom A
     *    (i) 1 points  required; vector in ABC plane bisecting AB, AC. If ABC is
     *        linear, no points
     *    (ii) 2 points: 2 points X1, X2, X1-A-X2 = angle about 2i vector
     *
     * @param aPoint to which substituents are added
     * @param bPoint first ligand of A
     * @param cPoint second ligand of A
     * @param nwanted number of points to calculate (1-2)
     * @param length A-X length
     * @param angle B-A-X angle
     *
     * @return Point3d[] nwanted points (or zero if failed)
     */
    public static Point3d[] calculate3DCoordinates2(Point3d aPoint, Point3d bPoint, Point3d cPoint, int nwanted,
            double length, double angle) {
        Point3d newPoints[] = new Point3d[0];
        double ang2 = angle / 2.0;

        Vector3d ba = new Vector3d(aPoint);
        ba.sub(bPoint);
        Vector3d ca = new Vector3d(aPoint);
        ca.sub(cPoint);
        Vector3d baxca = new Vector3d();
        baxca.cross(ba, ca);
        if (baxca.length() < 0.00000001) {
            ; // linear
        } else if (nwanted == 1) {
            newPoints = new Point3d[1];
            Vector3d ax = new Vector3d(ba);
            ax.add(ca);
            ax.normalize();
            ax.scale(length);
            newPoints[0] = new Point3d(aPoint);
            newPoints[0].add(ax);
        } else if (nwanted == 2) {
            newPoints = new Point3d[2];
            Vector3d ax = new Vector3d(ba);
            ax.add(ca);
            ax.normalize();
            baxca.normalize();
            baxca.scale(Math.sin(ang2) * length);
            ax.scale(Math.cos(ang2) * length);
            newPoints[0] = new Point3d(aPoint);
            newPoints[0].add(ax);
            newPoints[0].add(baxca);
            newPoints[1] = new Point3d(aPoint);
            newPoints[1].add(ax);
            newPoints[1].sub(baxca);
        }
        return newPoints;
    }

    /**
     * Calculate new point X in a B-A(-D)-C system. It forms a B-A(-D)(-C)-X system.
     *
     * (3) 3 ligands(B, C, D) of refAtom A
     *    (i) 1 points  required; if A, B, C, D coplanar, no points.
     *       else vector is resultant of BA, CA, DA
     *
     * @param aPoint to which substituents are added
     * @param bPoint first ligand of A
     * @param cPoint second ligand of A
     * @param dPoint third ligand of A
     * @param length A-X length
     *
     * @return Point3d nwanted points (or null if failed (coplanar))
     */
    public static Point3d calculate3DCoordinates3(Point3d aPoint, Point3d bPoint, Point3d cPoint, Point3d dPoint,
            double length) {
        Vector3d v1 = new Vector3d(aPoint);
        v1.sub(bPoint);
        Vector3d v2 = new Vector3d(aPoint);
        v2.sub(cPoint);
        Vector3d v3 = new Vector3d(aPoint);
        v3.sub(dPoint);
        Vector3d v = new Vector3d(bPoint);
        v.add(cPoint);
        v.add(dPoint);
        if (v.length() < 0.00001) {
            return null;
        }
        v.normalize();
        v.scale(length);
        Point3d point = new Point3d(aPoint);
        point.add(v);
        return point;
    }

    final static Vector3d XV = new Vector3d(1.0, 0.0, 0.0);
    final static Vector3d YV = new Vector3d(0.0, 1.0, 0.0);

    // gets a point not on vector a...b; this can be used to define a plan or cross products
    private static Vector3d getNonColinearVector(Vector3d ab) {
        Vector3d cr = new Vector3d();
        cr.cross(ab, XV);
        if (cr.length() > 0.00001) {
            return XV;
        } else {
            return YV;
        }
    }
}
