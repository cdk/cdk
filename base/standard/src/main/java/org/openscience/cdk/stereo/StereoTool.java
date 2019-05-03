/* Copyright (C) 2005-2009  The Jmol Development Team
 * Copyright (C) 2010 Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.stereo;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;

/**
 * Methods to determine or check the stereo class of a set of atoms.
 *
 * Some of these methods were adapted from Jmol's smiles search package.
 *
 * @author maclean
 * @cdk.module standard
 * @cdk.githash
 */
public class StereoTool {

    /**
     * Currently unused, but intended for the StereoTool to indicate what it
     * 'means' by an assignment of some atoms to a class.
     *
     */
    public enum StereoClass {
        TETRAHEDRAL, SQUARE_PLANAR, TRIGONAL_BIPYRAMIDAL, OCTAHEDRAL
    }

    /**
     * The handedness of a tetrahedron, in terms of the point-plane distance
     * of three of the corners, compared to the fourth.
     *
     * PLUS indices a positive point-plane distance,
     * MINUS is a negative point-plane distance.
     */
    public enum TetrahedralSign {
        PLUS, MINUS
    }

    /**
     * The shape that four atoms take in a plane.
     */
    public enum SquarePlanarShape {
        U_SHAPE, FOUR_SHAPE, Z_SHAPE
    }

    /**
     * The maximum angle in radians for two lines to be 'diaxial'.
     * Where 0.95 is about 172 degrees.
     */
    public static final double MAX_AXIS_ANGLE      = 0.95;

    /**
     * The maximum tolerance for the normal calculated during colinearity.
     */
    public static final double MIN_COLINEAR_NORMAL = 0.05;

    public static final double PLANE_TOLERANCE     = 0.05;

    /**
     * Checks these four atoms for square planarity.
     *
     * @param atomA an atom in the plane
     * @param atomB an atom in the plane
     * @param atomC an atom in the plane
     * @param atomD an atom in the plane
     * @return true if all the atoms are in the same plane
     */
    public static boolean isSquarePlanar(IAtom atomA, IAtom atomB, IAtom atomC, IAtom atomD) {
        Point3d pointA = atomA.getPoint3d();
        Point3d pointB = atomB.getPoint3d();
        Point3d pointC = atomC.getPoint3d();
        Point3d pointD = atomD.getPoint3d();

        return isSquarePlanar(pointA, pointB, pointC, pointD);
    }

    private static boolean isSquarePlanar(Point3d pointA, Point3d pointB, Point3d pointC, Point3d pointD) {
        return isSquarePlanar(pointA, pointB, pointC, pointD, new Vector3d());
    }

    private static boolean isSquarePlanar(Point3d pointA, Point3d pointB, Point3d pointC, Point3d pointD,
            Vector3d normal) {
        // define a plane using ABC, also checking that the are not colinear
        Vector3d vectorAB = new Vector3d();
        Vector3d vectorAC = new Vector3d();
        getRawNormal(pointA, pointB, pointC, normal, vectorAB, vectorAC);
        if (StereoTool.isColinear(normal)) return false;

        // check that F is in the same plane as CDE
        return StereoTool.allCoplanar(normal, pointC, pointD);
    }

    /**
     * <p>Given four atoms (assumed to be in the same plane), returns the
     * arrangement of those atoms in that plane.</p>
     *
     * <p>The 'shapes' returned represent arrangements that look a little like
     * the characters 'U', '4', and 'Z'.</p>
     *
     * @param atomA an atom in the plane
     * @param atomB an atom in the plane
     * @param atomC an atom in the plane
     * @param atomD an atom in the plane
     * @return the shape (U/4/Z)
     */
    public static SquarePlanarShape getSquarePlanarShape(IAtom atomA, IAtom atomB, IAtom atomC, IAtom atomD) {
        Point3d pointA = atomA.getPoint3d();
        Point3d pointB = atomB.getPoint3d();
        Point3d pointC = atomC.getPoint3d();
        Point3d pointD = atomD.getPoint3d();

        // normalA normalB normalC are right-hand normals for the given
        // triangles
        // A-B-C, B-C-D, C-D-A
        Vector3d normalA = new Vector3d();
        Vector3d normalB = new Vector3d();
        Vector3d normalC = new Vector3d();

        // these are temporary vectors that are re-used in the calculations
        Vector3d tmpX = new Vector3d();
        Vector3d tmpY = new Vector3d();

        // the normals (normalA, normalB, normalC) are calculated
        StereoTool.getRawNormal(pointA, pointB, pointC, normalA, tmpX, tmpY);
        StereoTool.getRawNormal(pointB, pointC, pointD, normalB, tmpX, tmpY);
        StereoTool.getRawNormal(pointC, pointD, pointA, normalC, tmpX, tmpY);

        // normalize the normals
        normalA.normalize();
        normalB.normalize();
        normalC.normalize();

        // sp1 up up up U-shaped
        // sp2 up up DOWN 4-shaped
        // sp3 up DOWN DOWN Z-shaped
        double aDotB = normalA.dot(normalB);
        double aDotC = normalA.dot(normalC);
        double bDotC = normalB.dot(normalC);
        if (aDotB > 0 && aDotC > 0 && bDotC > 0) { // UUU or DDD
            return SquarePlanarShape.U_SHAPE;
        } else if (aDotB > 0 && aDotC < 0 && bDotC < 0) { // UUD or DDU
            return SquarePlanarShape.FOUR_SHAPE;
        } else { // UDD or DUU
            return SquarePlanarShape.Z_SHAPE;
        }
    }

    /**
     * Check that all the points in the list are coplanar (in the same plane)
     * as the plane defined by the planeNormal and the pointInPlane.
     *
     * @param planeNormal the normal to the plane
     * @param pointInPlane any point know to be in the plane
     * @param points an array of points to test
     * @return false if any of the points is not in the plane
     */
    public static boolean allCoplanar(Vector3d planeNormal, Point3d pointInPlane, Point3d... points) {
        for (Point3d point : points) {
            double distance = StereoTool.signedDistanceToPlane(planeNormal, pointInPlane, point);
            if (distance < PLANE_TOLERANCE) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks these 7 atoms to see if they are at the points of an octahedron.
     *
     * @param atomA one of the axial atoms
     * @param atomB the central atom
     * @param atomC one of the equatorial atoms
     * @param atomD one of the equatorial atoms
     * @param atomE one of the equatorial atoms
     * @param atomF one of the equatorial atoms
     * @param atomG the other axial atom
     * @return true if the geometry is octahedral
     */
    public static boolean isOctahedral(IAtom atomA, IAtom atomB, IAtom atomC, IAtom atomD, IAtom atomE, IAtom atomF,
            IAtom atomG) {
        Point3d pointA = atomA.getPoint3d();
        Point3d pointB = atomB.getPoint3d();
        Point3d pointC = atomC.getPoint3d();
        Point3d pointD = atomD.getPoint3d();
        Point3d pointE = atomE.getPoint3d();
        Point3d pointF = atomF.getPoint3d();
        Point3d pointG = atomG.getPoint3d();

        // the points on the axis should be in a line
        boolean isColinearABG = isColinear(pointA, pointB, pointG);
        if (!isColinearABG) return false;

        // check that CDEF are in a plane
        Vector3d normal = new Vector3d();
        isSquarePlanar(pointC, pointD, pointE, pointF, normal);

        // now check rotation in relation to the first atom
        Vector3d vectorAB = new Vector3d(pointA);
        vectorAB.sub(pointB);

        // that is, they point in opposite directions
        return normal.dot(vectorAB) < 0;
    }

    /**
     * Checks these 6 atoms to see if they form a trigonal-bipyramidal shape.
     *
     * @param atomA one of the axial atoms
     * @param atomB the central atom
     * @param atomC one of the equatorial atoms
     * @param atomD one of the equatorial atoms
     * @param atomE one of the equatorial atoms
     * @param atomF the other axial atom
     * @return true if the geometry is trigonal-bipyramidal
     */
    public static boolean isTrigonalBipyramidal(IAtom atomA, IAtom atomB, IAtom atomC, IAtom atomD, IAtom atomE,
            IAtom atomF) {
        Point3d pointA = atomA.getPoint3d();
        Point3d pointB = atomB.getPoint3d();
        Point3d pointC = atomC.getPoint3d();
        Point3d pointD = atomD.getPoint3d();
        Point3d pointE = atomE.getPoint3d();
        Point3d pointF = atomF.getPoint3d();

        boolean isColinearABF = StereoTool.isColinear(pointA, pointB, pointF);
        if (isColinearABF) {
            // the normal to the equatorial plane
            Vector3d normal = StereoTool.getNormal(pointC, pointD, pointE);

            // get the side of the plane that axis point A is
            TetrahedralSign handednessCDEA = StereoTool.getHandedness(normal, pointC, pointF);

            // get the side of the plane that axis point F is
            TetrahedralSign handednessCDEF = StereoTool.getHandedness(normal, pointC, pointA);

            // in other words, the two axial points (A,F) are on opposite sides
            // of the equatorial plane CDE
            return handednessCDEA != handednessCDEF;
        } else {
            return false;
        }
    }

    /**
     * Take four atoms, and return Stereo.CLOCKWISE or Stereo.ANTI_CLOCKWISE.
     * The first atom is the one pointing towards the observer.
     *
     * @param atom1 the atom pointing towards the observer
     * @param atom2 the second atom (points away)
     * @param atom3 the third atom (points away)
     * @param atom4 the fourth atom (points away)
     * @return clockwise or anticlockwise
     */
    public static Stereo getStereo(IAtom atom1, IAtom atom2, IAtom atom3, IAtom atom4) {

        // a normal is calculated for the base atoms (2, 3, 4) and compared to
        // the first atom. PLUS indicates ACW.
        TetrahedralSign sign = StereoTool.getHandedness(atom2, atom3, atom4, atom1);

        if (sign == TetrahedralSign.PLUS) {
            return Stereo.ANTI_CLOCKWISE;
        } else {
            return Stereo.CLOCKWISE;
        }
    }

    /**
     * Gets the tetrahedral handedness of four atoms - three of which form the
     * 'base' of the tetrahedron, and the other the apex. Note that it assumes
     * a right-handed coordinate system, and that the points {A,B,C} are in
     * a counter-clockwise order in the plane they share.
     *
     * @param baseAtomA the first atom in the base of the tetrahedron
     * @param baseAtomB the second atom in the base of the tetrahedron
     * @param baseAtomC the third atom in the base of the tetrahedron
     * @param apexAtom the atom in the point of the tetrahedron
     * @return the sign of the tetrahedron
     */
    public static TetrahedralSign getHandedness(IAtom baseAtomA, IAtom baseAtomB, IAtom baseAtomC, IAtom apexAtom) {
        Point3d pointA = baseAtomA.getPoint3d();
        Point3d pointB = baseAtomB.getPoint3d();
        Point3d pointC = baseAtomC.getPoint3d();
        Point3d pointD = apexAtom.getPoint3d();
        return StereoTool.getHandedness(pointA, pointB, pointC, pointD);
    }

    private static TetrahedralSign getHandedness(Point3d pointA, Point3d pointB, Point3d pointC, Point3d pointD) {
        // assumes anti-clockwise for a right-handed system
        Vector3d normal = StereoTool.getNormal(pointA, pointB, pointC);

        // it doesn't matter which of points {A,B,C} is used
        return StereoTool.getHandedness(normal, pointA, pointD);
    }

    private static TetrahedralSign getHandedness(Vector3d planeNormal, Point3d pointInPlane, Point3d testPoint) {
        double distance = signedDistanceToPlane(planeNormal, pointInPlane, testPoint);

        // The point-plane distance is the absolute value,
        // the sign of the distance gives the side of the plane the point is on
        // relative to the plane normal.
        if (distance > 0) {
            return TetrahedralSign.PLUS;
        } else {
            return TetrahedralSign.MINUS;
        }
    }

    /**
     * Checks the three supplied points to see if they fall on the same line.
     * It does this by finding the normal to an arbitrary pair of lines between
     * the points (in fact, A-B and A-C) and checking that its length is 0.
     *
     * @param ptA
     * @param ptB
     * @param ptC
     * @return true if the tree points are on a straight line
     */
    public static boolean isColinear(Point3d ptA, Point3d ptB, Point3d ptC) {
        Vector3d vectorAB = new Vector3d();
        Vector3d vectorAC = new Vector3d();
        Vector3d normal = new Vector3d();

        StereoTool.getRawNormal(ptA, ptB, ptC, normal, vectorAB, vectorAC);
        return isColinear(normal);
    }

    private static boolean isColinear(Vector3d normal) {
        double baCrossACLen = normal.length();
        return baCrossACLen < StereoTool.MIN_COLINEAR_NORMAL;
    }

    /**
     * Given a normalized normal for a plane, any point in that plane, and
     * a point, will return the distance between the plane and that point.
     *
     * @param planeNormal the normalized plane normal
     * @param pointInPlane an arbitrary point in that plane
     * @param point the point to measure
     * @return the signed distance to the plane
     */
    public static double signedDistanceToPlane(Vector3d planeNormal, Point3d pointInPlane, Point3d point) {
        if (planeNormal == null) return Double.NaN;

        Vector3d pointPointDiff = new Vector3d();
        pointPointDiff.sub(point, pointInPlane);
        return planeNormal.dot(pointPointDiff);
    }

    /**
     * <p>Given three points (A, B, C), makes the vectors A-B and A-C, and makes
     * the cross product of these two vectors; this has the effect of making a
     * third vector at right angles to AB and AC.</p>
     *
     * <p>NOTE : the returned normal is normalized; that is, it has been
     * divided by its length.</p>
     *
     * @param ptA the 'middle' point
     * @param ptB one of the end points
     * @param ptC one of the end points
     * @return the vector at right angles to AB and AC
     */
    public static Vector3d getNormal(Point3d ptA, Point3d ptB, Point3d ptC) {
        Vector3d vectorAB = new Vector3d();
        Vector3d vectorAC = new Vector3d();
        Vector3d normal = new Vector3d();
        StereoTool.getRawNormal(ptA, ptB, ptC, normal, vectorAB, vectorAC);
        normal.normalize();
        return normal;
    }

    private static void getRawNormal(Point3d ptA, Point3d ptB, Point3d ptC, Vector3d normal, Vector3d vcAB,
            Vector3d vcAC) {
        // make A->B and A->C
        vcAB.sub(ptB, ptA);
        vcAC.sub(ptC, ptA);

        // make the normal to this
        normal.cross(vcAB, vcAC);
    }

}
