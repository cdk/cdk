/*  Copyright (C) 1997-2014  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A set of static utility classes for geometric calculations and operations. This class is
 * extensively used, for example, by JChemPaint to edit molecule. All methods in this class change
 * the coordinates of the atoms. Use GeometryTools if you use an external set of coordinates (e. g.
 * renderingCoordinates from RendererModel)
 *
 * @author seb
 * @author Stefan Kuhn
 * @author Egon Willighagen
 * @author Ludovic Petain
 * @author Christian Hoppe
 * @author Niels Out
 * @author John May
 * @cdk.githash
 */
public final class GeometryUtil {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(GeometryUtil.class);

    /**
     * Provides the coverage of coordinates for this molecule.
     *
     * @see GeometryUtil#get2DCoordinateCoverage(org.openscience.cdk.interfaces.IAtomContainer)
     * @see GeometryUtil#get3DCoordinateCoverage(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public static enum CoordinateCoverage {

        /**
         * All atoms have coordinates.
         */
        FULL,

        /**
         * At least one atom has coordinates but not all.
         */
        PARTIAL,

        /**
         * No atoms have coordinates.
         */
        NONE

    }

    /**
     * Static utility class can not be instantiated.
     */
    private GeometryUtil() {}

    /**
     * Adds an automatically calculated offset to the coordinates of all atoms such that all
     * coordinates are positive and the smallest x or y coordinate is exactly zero. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param atomCon AtomContainer for which all the atoms are translated to positive coordinates
     */
    public static void translateAllPositive(IAtomContainer atomCon) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (IAtom atom : atomCon.atoms()) {
            if (atom.getPoint2d() != null) {
                if (atom.getPoint2d().x < minX) {
                    minX = atom.getPoint2d().x;
                }
                if (atom.getPoint2d().y < minY) {
                    minY = atom.getPoint2d().y;
                }
            }
        }
        logger.debug("Translating: minx=" + minX + ", minY=" + minY);
        translate2D(atomCon, minX * -1, minY * -1);
    }

    /**
     * Translates the given molecule by the given Vector. See comment for center(IAtomContainer
     * atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param atomCon The molecule to be translated
     * @param transX  translation in x direction
     * @param transY  translation in y direction
     */
    public static void translate2D(IAtomContainer atomCon, double transX, double transY) {
        translate2D(atomCon, new Vector2d(transX, transY));
    }

    /**
     * Scales a molecule such that it fills a given percentage of a given dimension. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param atomCon    The molecule to be scaled {width, height}
     * @param areaDim    The dimension to be filled {width, height}
     * @param fillFactor The percentage of the dimension to be filled
     */
    public static void scaleMolecule(IAtomContainer atomCon, double[] areaDim, double fillFactor) {
        double[] molDim = get2DDimension(atomCon);
        double widthFactor = (double) areaDim[0] / (double) molDim[0];
        double heightFactor = (double) areaDim[1] / (double) molDim[1];
        double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
        scaleMolecule(atomCon, scaleFactor);
    }

    /**
     * Multiplies all the coordinates of the atoms of the given molecule with the scalefactor. See
     * comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates)
     * for details on coordinate sets
     *
     * @param atomCon     The molecule to be scaled
     * @param scaleFactor Description of the Parameter
     */
    public static void scaleMolecule(IAtomContainer atomCon, double scaleFactor) {
        for (int i = 0; i < atomCon.getAtomCount(); i++) {
            if (atomCon.getAtom(i).getPoint2d() != null) {
                atomCon.getAtom(i).getPoint2d().scale(scaleFactor);
            }
        }
        // scale Sgroup brackets
        if (atomCon.getProperty(CDKConstants.CTAB_SGROUPS) != null) {
            List<Sgroup> sgroups = atomCon.getProperty(CDKConstants.CTAB_SGROUPS);
            for (Sgroup sgroup : sgroups) {
                List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
                if (brackets != null) {
                    for (SgroupBracket bracket : brackets) {
                        bracket.getFirstPoint().scale(scaleFactor);
                        bracket.getSecondPoint().scale(scaleFactor);
                    }
                }
            }
        }
    }

    /**
     * Centers the molecule in the given area. See comment for center(IAtomContainer atomCon,
     * Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param atomCon molecule to be centered
     * @param areaDim dimension in which the molecule is to be centered, array containing
     *                {width, height}
     */
    public static void center(IAtomContainer atomCon, double[] areaDim) {
        double[] molDim = get2DDimension(atomCon);
        double transX = (areaDim[0] - molDim[0]) / 2;
        double transY = (areaDim[1] - molDim[1]) / 2;
        translateAllPositive(atomCon);
        translate2D(atomCon, new Vector2d(transX, transY));
    }

    /**
     * Translates a molecule from the origin to a new point denoted by a vector. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param atomCon molecule to be translated
     * @param vector  dimension that represents the translation vector
     */
    public static void translate2D(IAtomContainer atomCon, Vector2d vector) {
        for (IAtom atom : atomCon.atoms()) {
            if (atom.getPoint2d() != null) {
                atom.getPoint2d().add(vector);
            } else {
                logger.warn("Could not translate atom in 2D space");
            }
        }
        // translate Sgroup brackets
        if (atomCon.getProperty(CDKConstants.CTAB_SGROUPS) != null) {
            List<Sgroup> sgroups = atomCon.getProperty(CDKConstants.CTAB_SGROUPS);
            for (Sgroup sgroup : sgroups) {
                List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
                if (brackets != null) {
                    for (SgroupBracket bracket : brackets) {
                        bracket.getFirstPoint().add(vector);
                        bracket.getSecondPoint().add(vector);
                    }
                }
            }
        }
    }

    /**
     * Rotates a molecule around a given center by a given angle.
     *
     * @param atomCon The molecule to be rotated
     * @param center  A point giving the rotation center
     * @param angle   The angle by which to rotate the molecule, in radians
     */
    public static void rotate(IAtomContainer atomCon, Point2d center, double angle) {
        Point2d point;
        double costheta = Math.cos(angle);
        double sintheta = Math.sin(angle);
        IAtom atom;
        for (int i = 0; i < atomCon.getAtomCount(); i++) {
            atom = atomCon.getAtom(i);
            point = atom.getPoint2d();
            double relativex = point.x - center.x;
            double relativey = point.y - center.y;
            point.x = relativex * costheta - relativey * sintheta + center.x;
            point.y = relativex * sintheta + relativey * costheta + center.y;
        }
    }

    /**
     * Rotates a 3D point about a specified line segment by a specified angle.
     *
     * The code is based on code available <a href="http://astronomy.swin.edu.au/~pbourke/geometry/rotate/source.c">here</a>.
     * Positive angles are anticlockwise looking down the axis towards the origin. Assume right hand
     * coordinate system.
     *
     * @param atom  The atom to rotate
     * @param p1    The  first point of the line segment
     * @param p2    The second point of the line segment
     * @param angle The angle to rotate by (in degrees)
     */
    public static void rotate(IAtom atom, Point3d p1, Point3d p2, double angle) {
        double costheta, sintheta;

        Point3d r = new Point3d();

        r.x = p2.x - p1.x;
        r.y = p2.y - p1.y;
        r.z = p2.z - p1.z;
        normalize(r);

        angle = angle * Math.PI / 180.0;
        costheta = Math.cos(angle);
        sintheta = Math.sin(angle);

        Point3d p = atom.getPoint3d();
        p.x -= p1.x;
        p.y -= p1.y;
        p.z -= p1.z;

        Point3d q = new Point3d(0, 0, 0);
        q.x += (costheta + (1 - costheta) * r.x * r.x) * p.x;
        q.x += ((1 - costheta) * r.x * r.y - r.z * sintheta) * p.y;
        q.x += ((1 - costheta) * r.x * r.z + r.y * sintheta) * p.z;

        q.y += ((1 - costheta) * r.x * r.y + r.z * sintheta) * p.x;
        q.y += (costheta + (1 - costheta) * r.y * r.y) * p.y;
        q.y += ((1 - costheta) * r.y * r.z - r.x * sintheta) * p.z;

        q.z += ((1 - costheta) * r.x * r.z - r.y * sintheta) * p.x;
        q.z += ((1 - costheta) * r.y * r.z + r.x * sintheta) * p.y;
        q.z += (costheta + (1 - costheta) * r.z * r.z) * p.z;

        q.x += p1.x;
        q.y += p1.y;
        q.z += p1.z;

        atom.setPoint3d(q);
    }

    /**
     * Normalizes a point.
     *
     * @param point The point to normalize
     */
    public static void normalize(Point3d point) {
        double sum = Math.sqrt(point.x * point.x + point.y * point.y + point.z * point.z);
        point.x = point.x / sum;
        point.y = point.y / sum;
        point.z = point.z / sum;
    }

    /**
     * Returns the dimension of a molecule (width/height).
     *
     * @param atomCon of which the dimension should be returned
     * @return array containing {width, height}
     */
    public static double[] get2DDimension(IAtomContainer atomCon) {
        double[] minmax = getMinMax(atomCon);
        double maxX = minmax[2];
        double maxY = minmax[3];
        double minX = minmax[0];
        double minY = minmax[1];
        return new double[]{maxX - minX, maxY - minY};
    }

    /**
     * Returns the minimum and maximum X and Y coordinates of the atoms.
     * The output is returned as: <pre>
     *   minmax[0] = minX;
     *   minmax[1] = minY;
     *   minmax[2] = maxX;
     *   minmax[3] = maxY;
     * </pre>
     *
     * @param atoms the atoms.
     * @return An four int array as defined above.
     */
    public static double[] getMinMax(Iterable<IAtom> atoms) {
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        for (IAtom atom : atoms) {
            if (atom.getPoint2d() != null) {
                if (atom.getPoint2d().x > maxX) {
                    maxX = atom.getPoint2d().x;
                }
                if (atom.getPoint2d().x < minX) {
                    minX = atom.getPoint2d().x;
                }
                if (atom.getPoint2d().y > maxY) {
                    maxY = atom.getPoint2d().y;
                }
                if (atom.getPoint2d().y < minY) {
                    minY = atom.getPoint2d().y;
                }
            }
        }
        double[] minmax = new double[4];
        minmax[0] = minX;
        minmax[1] = minY;
        minmax[2] = maxX;
        minmax[3] = maxY;
        return minmax;
    }

    /**
     * Returns the minimum and maximum X and Y coordinates of the atoms in the
     * AtomContainer. The output is returned as: <pre>
     *   minmax[0] = minX;
     *   minmax[1] = minY;
     *   minmax[2] = maxX;
     *   minmax[3] = maxY;
     * </pre>
     * See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap
     * renderingCoordinates) for details on coordinate sets
     *
     * @param container Description of the Parameter
     * @return An four int array as defined above.
     */
    public static double[] getMinMax(IAtomContainer container) {
        return getMinMax(container.atoms());
    }

    /**
     * Translates a molecule from the origin to a new point denoted by a vector. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param atomCon molecule to be translated
     * @param p       Description of the Parameter
     */
    public static void translate2DCentreOfMassTo(IAtomContainer atomCon, Point2d p) {
        Point2d com = get2DCentreOfMass(atomCon);
        Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
        for (IAtom atom : atomCon.atoms()) {
            if (atom.getPoint2d() != null) {
                atom.getPoint2d().add(translation);
            }
        }
    }

    /**
     * Calculates the center of the given atoms and returns it as a Point2d. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param atoms The vector of the given atoms
     * @return The center of the given atoms as Point2d
     */
    public static Point2d get2DCenter(Iterable<IAtom> atoms) {
        double xsum = 0;
        double ysum = 0;
        int length = 0;
        for (IAtom atom : atoms) {
            if (atom.getPoint2d() != null) {
                xsum += atom.getPoint2d().x;
                ysum += atom.getPoint2d().y;
                length++;
            }
        }
        return new Point2d(xsum / (double) length, ysum / (double) length);
    }

    /**
     * Calculates the center of the given atoms and returns it as a Point2d. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param atoms The Iterator of the given atoms
     * @return The center of the given atoms as Point2d
     */
    public static Point2d get2DCenter(Iterator<IAtom> atoms) {
        IAtom atom;
        double xsum = 0;
        double ysum = 0;
        int length = 0;
        while (atoms.hasNext()) {
            atom = atoms.next();
            if (atom.getPoint2d() != null) {
                xsum += atom.getPoint2d().x;
                ysum += atom.getPoint2d().y;
            }
            ++length;
        }
        return new Point2d(xsum / (double) length, ysum / (double) length);
    }

    /**
     * Returns the geometric center of all the rings in this ringset. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param ringSet Description of the Parameter
     * @return the geometric center of the rings in this ringset
     */
    public static Point2d get2DCenter(IRingSet ringSet) {
        double centerX = 0;
        double centerY = 0;
        for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
            Point2d centerPoint = get2DCenter(ringSet.getAtomContainer(i));
            centerX += centerPoint.x;
            centerY += centerPoint.y;
        }
        return new Point2d(centerX / ((double) ringSet.getAtomContainerCount()), centerY
                / ((double) ringSet.getAtomContainerCount()));
    }

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the AtomContainer for the 2D
     * coordinates. See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap
     * renderingCoordinates) for details on coordinate sets
     *
     * @param ac AtomContainer for which the center of mass is calculated
     * @return Null, if any of the atomcontainer {@link org.openscience.cdk.interfaces.IAtom}'s
     * masses are null
     * @cdk.keyword center of mass
     */
    public static Point2d get2DCentreOfMass(IAtomContainer ac) {
        double xsum = 0.0;
        double ysum = 0.0;

        double totalmass = 0.0;

        for (IAtom a : ac.atoms()) {
            Double mass = a.getExactMass();
            if (mass == null) return null;
            totalmass += mass;
            xsum += mass * a.getPoint2d().x;
            ysum += mass * a.getPoint2d().y;
        }

        return new Point2d(xsum / totalmass, ysum / totalmass);
    }

    /**
     * Returns the geometric center of all the atoms in the atomContainer. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param container Description of the Parameter
     * @return the geometric center of the atoms in this atomContainer
     */
    public static Point2d get2DCenter(IAtomContainer container) {
        double centerX = 0;
        double centerY = 0;
        double counter = 0;
        for (IAtom atom : container.atoms()) {
            if (atom.getPoint2d() != null) {
                centerX += atom.getPoint2d().x;
                centerY += atom.getPoint2d().y;
                counter++;
            }
        }
        return new Point2d(centerX / (counter), centerY / (counter));
    }

    /**
     * Translates the geometric 2DCenter of the given AtomContainer container to the specified
     * Point2d p.
     *
     * @param container AtomContainer which should be translated.
     * @param p         New Location of the geometric 2D Center.
     * @see #get2DCenter
     * @see #translate2DCentreOfMassTo
     */
    public static void translate2DCenterTo(IAtomContainer container, Point2d p) {
        Point2d com = get2DCenter(container);
        Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
        for (IAtom atom : container.atoms()) {
            if (atom.getPoint2d() != null) {
                atom.getPoint2d().add(translation);
            }
        }
    }

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the AtomContainer.
     *
     * @param ac AtomContainer for which the center of mass is calculated
     * @return The center of mass of the molecule, or <code>NULL</code> if the molecule
     * does not have 3D coordinates or if any of the atoms do not have a valid atomic mass
     * @cdk.keyword center of mass
     * @cdk.dictref blue-obelisk:calculate3DCenterOfMass
     */
    public static Point3d get3DCentreOfMass(IAtomContainer ac) {
        double xsum = 0.0;
        double ysum = 0.0;
        double zsum = 0.0;

        double totalmass = 0.0;
        Isotopes isotopes;
        try {
            isotopes = Isotopes.getInstance();
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize Isotopes");
        }

        for (IAtom a : ac.atoms()) {
            Double mass = a.getExactMass();
            // some sanity checking
            if (a.getPoint3d() == null) return null;
            if (mass == null)
                mass = isotopes.getNaturalMass(a);

            totalmass += mass;
            xsum += mass * a.getPoint3d().x;
            ysum += mass * a.getPoint3d().y;
            zsum += mass * a.getPoint3d().z;
        }

        return new Point3d(xsum / totalmass, ysum / totalmass, zsum / totalmass);
    }

    /**
     * Returns the geometric center of all the atoms in this atomContainer. See comment for
     * center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details
     * on coordinate sets
     *
     * @param ac Description of the Parameter
     * @return the geometric center of the atoms in this atomContainer
     */
    public static Point3d get3DCenter(IAtomContainer ac) {
        double centerX = 0;
        double centerY = 0;
        double centerZ = 0;
        double counter = 0;
        for (IAtom atom : ac.atoms()) {
            if (atom.getPoint3d() != null) {
                centerX += atom.getPoint3d().x;
                centerY += atom.getPoint3d().y;
                centerZ += atom.getPoint3d().z;
                counter++;
            }
        }
        return new Point3d(centerX / (counter), centerY / (counter), centerZ / (counter));
    }

    /**
     * Gets the angle attribute of the GeometryTools class.
     *
     * @param xDiff Description of the Parameter
     * @param yDiff Description of the Parameter
     * @return The angle value
     */
    public static double getAngle(double xDiff, double yDiff) {
        double angle = 0;
        //		logger.debug("getAngle->xDiff: " + xDiff);
        //		logger.debug("getAngle->yDiff: " + yDiff);
        if (xDiff >= 0 && yDiff >= 0) {
            angle = Math.atan(yDiff / xDiff);
        } else if (xDiff < 0 && yDiff >= 0) {
            angle = Math.PI + Math.atan(yDiff / xDiff);
        } else if (xDiff < 0 && yDiff < 0) {
            angle = Math.PI + Math.atan(yDiff / xDiff);
        } else if (xDiff >= 0 && yDiff < 0) {
            angle = 2 * Math.PI + Math.atan(yDiff / xDiff);
        }
        return angle;
    }

    /**
     * Gets the coordinates of two points (that represent a bond) and calculates for each the
     * coordinates of two new points that have the given distance vertical to the bond.
     *
     * @param coords The coordinates of the two given points of the bond like this [point1x,
     *               point1y, point2x, point2y]
     * @param dist   The vertical distance between the given points and those to be calculated
     * @return The coordinates of the calculated four points
     */
    public static int[] distanceCalculator(int[] coords, double dist) {
        double angle;
        if ((coords[2] - coords[0]) == 0) {
            angle = Math.PI / 2;
        } else {
            angle = Math.atan(((double) coords[3] - (double) coords[1]) / ((double) coords[2] - (double) coords[0]));
        }
        int begin1X = (int) (Math.cos(angle + Math.PI / 2) * dist + coords[0]);
        int begin1Y = (int) (Math.sin(angle + Math.PI / 2) * dist + coords[1]);
        int begin2X = (int) (Math.cos(angle - Math.PI / 2) * dist + coords[0]);
        int begin2Y = (int) (Math.sin(angle - Math.PI / 2) * dist + coords[1]);
        int end1X = (int) (Math.cos(angle - Math.PI / 2) * dist + coords[2]);
        int end1Y = (int) (Math.sin(angle - Math.PI / 2) * dist + coords[3]);
        int end2X = (int) (Math.cos(angle + Math.PI / 2) * dist + coords[2]);
        int end2Y = (int) (Math.sin(angle + Math.PI / 2) * dist + coords[3]);

        return new int[]{begin1X, begin1Y, begin2X, begin2Y, end1X, end1Y, end2X, end2Y};
    }

    public static double[] distanceCalculator(double[] coords, double dist) {
        double angle;
        if ((coords[2] - coords[0]) == 0) {
            angle = Math.PI / 2;
        } else {
            angle = Math.atan((coords[3] - coords[1]) / (coords[2] - coords[0]));
        }
        double begin1X = (Math.cos(angle + Math.PI / 2) * dist + coords[0]);
        double begin1Y = (Math.sin(angle + Math.PI / 2) * dist + coords[1]);
        double begin2X = (Math.cos(angle - Math.PI / 2) * dist + coords[0]);
        double begin2Y = (Math.sin(angle - Math.PI / 2) * dist + coords[1]);
        double end1X = (Math.cos(angle - Math.PI / 2) * dist + coords[2]);
        double end1Y = (Math.sin(angle - Math.PI / 2) * dist + coords[3]);
        double end2X = (Math.cos(angle + Math.PI / 2) * dist + coords[2]);
        double end2Y = (Math.sin(angle + Math.PI / 2) * dist + coords[3]);

        return new double[]{begin1X, begin1Y, begin2X, begin2Y, end1X, end1Y, end2X, end2Y};
    }

    /**
     * Writes the coordinates of the atoms participating the given bond into an array. See comment
     * for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for
     * details on coordinate sets
     *
     * @param bond The given bond
     * @return The array with the coordinates
     */
    public static int[] getBondCoordinates(IBond bond) {
        if (bond.getBegin().getPoint2d() == null || bond.getEnd().getPoint2d() == null) {
            logger.error("getBondCoordinates() called on Bond without 2D coordinates!");
            return new int[0];
        }
        int beginX = (int) bond.getBegin().getPoint2d().x;
        int endX = (int) bond.getEnd().getPoint2d().x;
        int beginY = (int) bond.getBegin().getPoint2d().y;
        int endY = (int) bond.getEnd().getPoint2d().y;
        return new int[]{beginX, beginY, endX, endY};
    }

    /**
     * Returns the atom of the given molecule that is closest to the given coordinates. See comment
     * for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for
     * details on coordinate sets
     *
     * @param xPosition The x coordinate
     * @param yPosition The y coordinate
     * @param atomCon   The molecule that is searched for the closest atom
     * @return The atom that is closest to the given coordinates
     */
    public static IAtom getClosestAtom(int xPosition, int yPosition, IAtomContainer atomCon) {
        IAtom closestAtom = null;
        IAtom currentAtom;
        double smallestMouseDistance = -1;
        double mouseDistance;
        double atomX;
        double atomY;
        for (int i = 0; i < atomCon.getAtomCount(); i++) {
            currentAtom = atomCon.getAtom(i);
            atomX = currentAtom.getPoint2d().x;
            atomY = currentAtom.getPoint2d().y;
            mouseDistance = Math.sqrt(Math.pow(atomX - xPosition, 2) + Math.pow(atomY - yPosition, 2));
            if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
                smallestMouseDistance = mouseDistance;
                closestAtom = currentAtom;
            }
        }
        return closestAtom;
    }

    /**
     * Returns the atom of the given molecule that is closest to the given atom (excluding itself).
     *
     * @param atomCon The molecule that is searched for the closest atom
     * @param atom    The atom to search around
     * @return The atom that is closest to the given coordinates
     */
    public static IAtom getClosestAtom(IAtomContainer atomCon, IAtom atom) {
        IAtom closestAtom = null;
        double min = Double.MAX_VALUE;
        Point2d atomPosition = atom.getPoint2d();
        for (int i = 0; i < atomCon.getAtomCount(); i++) {
            IAtom currentAtom = atomCon.getAtom(i);
            if (!currentAtom.equals(atom)) {
                double d = atomPosition.distance(currentAtom.getPoint2d());
                if (d < min) {
                    min = d;
                    closestAtom = currentAtom;
                }
            }
        }
        return closestAtom;
    }

    /**
     * Returns the atom of the given molecule that is closest to the given coordinates and is not
     * the atom. See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap
     * renderingCoordinates) for details on coordinate sets
     *
     * @param xPosition The x coordinate
     * @param yPosition The y coordinate
     * @param atomCon   The molecule that is searched for the closest atom
     * @param toignore  This molecule will not be returned.
     * @return The atom that is closest to the given coordinates
     */
    public static IAtom getClosestAtom(double xPosition, double yPosition, IAtomContainer atomCon, IAtom toignore) {
        IAtom closestAtom = null;
        IAtom currentAtom;
        // we compare squared distances, allowing us to do one sqrt()
        // calculation less
        double smallestSquaredMouseDistance = -1;
        double mouseSquaredDistance;
        double atomX;
        double atomY;
        for (int i = 0; i < atomCon.getAtomCount(); i++) {
            currentAtom = atomCon.getAtom(i);
            if (!currentAtom.equals(toignore)) {
                atomX = currentAtom.getPoint2d().x;
                atomY = currentAtom.getPoint2d().y;
                mouseSquaredDistance = Math.pow(atomX - xPosition, 2) + Math.pow(atomY - yPosition, 2);
                if (mouseSquaredDistance < smallestSquaredMouseDistance || smallestSquaredMouseDistance == -1) {
                    smallestSquaredMouseDistance = mouseSquaredDistance;
                    closestAtom = currentAtom;
                }
            }
        }
        return closestAtom;
    }

    /**
     * Returns the atom of the given molecule that is closest to the given coordinates. See comment
     * for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for
     * details on coordinate sets
     *
     * @param xPosition The x coordinate
     * @param yPosition The y coordinate
     * @param atomCon   The molecule that is searched for the closest atom
     * @return The atom that is closest to the given coordinates
     */
    public static IAtom getClosestAtom(double xPosition, double yPosition, IAtomContainer atomCon) {
        IAtom closestAtom = null;
        IAtom currentAtom;
        double smallestMouseDistance = -1;
        double mouseDistance;
        double atomX;
        double atomY;
        for (int i = 0; i < atomCon.getAtomCount(); i++) {
            currentAtom = atomCon.getAtom(i);
            atomX = currentAtom.getPoint2d().x;
            atomY = currentAtom.getPoint2d().y;
            mouseDistance = Math.sqrt(Math.pow(atomX - xPosition, 2) + Math.pow(atomY - yPosition, 2));
            if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
                smallestMouseDistance = mouseDistance;
                closestAtom = currentAtom;
            }
        }
        return closestAtom;
    }

    /**
     * Returns the bond of the given molecule that is closest to the given coordinates. See comment
     * for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for
     * details on coordinate sets
     *
     * @param xPosition The x coordinate
     * @param yPosition The y coordinate
     * @param atomCon   The molecule that is searched for the closest bond
     * @return The bond that is closest to the given coordinates
     */
    public static IBond getClosestBond(int xPosition, int yPosition, IAtomContainer atomCon) {
        Point2d bondCenter;
        IBond closestBond = null;

        double smallestMouseDistance = -1;
        double mouseDistance;
        for (IBond currentBond : atomCon.bonds()) {
            bondCenter = get2DCenter(currentBond.atoms());
            mouseDistance = Math.sqrt(Math.pow(bondCenter.x - xPosition, 2) + Math.pow(bondCenter.y - yPosition, 2));
            if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
                smallestMouseDistance = mouseDistance;
                closestBond = currentBond;
            }
        }
        return closestBond;
    }

    /**
     * Returns the bond of the given molecule that is closest to the given coordinates. See comment
     * for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for
     * details on coordinate sets
     *
     * @param xPosition The x coordinate
     * @param yPosition The y coordinate
     * @param atomCon   The molecule that is searched for the closest bond
     * @return The bond that is closest to the given coordinates
     */
    public static IBond getClosestBond(double xPosition, double yPosition, IAtomContainer atomCon) {
        Point2d bondCenter;
        IBond closestBond = null;

        double smallestMouseDistance = -1;
        double mouseDistance;
        for (IBond currentBond : atomCon.bonds()) {
            bondCenter = get2DCenter(currentBond.atoms());
            mouseDistance = Math.sqrt(Math.pow(bondCenter.x - xPosition, 2) + Math.pow(bondCenter.y - yPosition, 2));
            if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
                smallestMouseDistance = mouseDistance;
                closestBond = currentBond;
            }
        }
        return closestBond;
    }

    /**
     * Sorts a Vector of atoms such that the 2D distances of the atom locations from a given point
     * are smallest for the first atoms in the vector. See comment for center(IAtomContainer
     * atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param point The point from which the distances to the atoms are measured
     * @param atoms The atoms for which the distances to point are measured
     */
    public static void sortBy2DDistance(IAtom[] atoms, Point2d point) {
        double distance1;
        double distance2;
        IAtom atom1;
        IAtom atom2;
        boolean doneSomething;
        do {
            doneSomething = false;
            for (int f = 0; f < atoms.length - 1; f++) {
                atom1 = atoms[f];
                atom2 = atoms[f + 1];
                distance1 = point.distance(atom1.getPoint2d());
                distance2 = point.distance(atom2.getPoint2d());
                if (distance2 < distance1) {
                    atoms[f] = atom2;
                    atoms[f + 1] = atom1;
                    doneSomething = true;
                }
            }
        } while (doneSomething);
    }

    /**
     * Determines the scale factor for displaying a structure loaded from disk in a frame. An
     * average of all bond length values is produced and a scale factor is determined which would
     * scale the given molecule such that its See comment for center(IAtomContainer atomCon,
     * Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param container  The AtomContainer for which the ScaleFactor is to be calculated
     * @param bondLength The target bond length
     * @return The ScaleFactor with which the AtomContainer must be scaled to have the target bond
     * length
     */

    public static double getScaleFactor(IAtomContainer container, double bondLength) {
        double currentAverageBondLength = getBondLengthMedian(container);
        if (currentAverageBondLength == 0 || Double.isNaN(currentAverageBondLength)) return 1;
        return bondLength / currentAverageBondLength;
    }

    /**
     * An average of all 2D bond length values is produced. Bonds which have Atom's with no
     * coordinates are disregarded. See comment for center(IAtomContainer atomCon, Dimension
     * areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param container The AtomContainer for which the average bond length is to be calculated
     * @return the average bond length
     */
    public static double getBondLengthAverage(IAtomContainer container) {
        double bondLengthSum = 0;
        Iterator<IBond> bonds = container.bonds().iterator();
        int bondCounter = 0;
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
            IAtom atom1 = bond.getBegin();
            IAtom atom2 = bond.getEnd();
            if (atom1.getPoint2d() != null && atom2.getPoint2d() != null) {
                bondCounter++;
                bondLengthSum += getLength2D(bond);
            }
        }
        return bondLengthSum / bondCounter;
    }

    /**
     * Returns the geometric length of this bond in 2D space. See comment for center(IAtomContainer
     * atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param bond Description of the Parameter
     * @return The geometric length of this bond
     */
    public static double getLength2D(IBond bond) {
        if (bond.getBegin() == null || bond.getEnd() == null) {
            return 0.0;
        }
        Point2d point1 = bond.getBegin().getPoint2d();
        Point2d point2 = bond.getEnd().getPoint2d();
        if (point1 == null || point2 == null) {
            return 0.0;
        }
        return point1.distance(point2);
    }

    /**
     * Determines if all this {@link org.openscience.cdk.interfaces.IAtomContainer}'s atoms contain
     * 2D coordinates. If any atom is null or has unset 2D coordinates this method will return
     * false.
     *
     * @param container the atom container to examine
     * @return indication that all 2D coordinates are available
     * @see org.openscience.cdk.interfaces.IAtom#getPoint2d()
     */
    public static boolean has2DCoordinates(IAtomContainer container) {

        if (container == null || container.getAtomCount() == 0) return Boolean.FALSE;

        for (IAtom atom : container.atoms()) {

            if (atom == null || atom.getPoint2d() == null) return Boolean.FALSE;

        }

        return Boolean.TRUE;

    }

    /**
     * Determine if all parts of a reaction have coodinates
     *
     * @param reaction a reaction
     * @return the reaction has coordinates
     */
    public static boolean has2DCoordinates(IReaction reaction) {
        for (IAtomContainer mol : reaction.getReactants().atomContainers())
            if (!has2DCoordinates(mol))
                return false;
        for (IAtomContainer mol : reaction.getProducts().atomContainers())
            if (!has2DCoordinates(mol))
                return false;
        for (IAtomContainer mol : reaction.getAgents().atomContainers())
            if (!has2DCoordinates(mol))
                return false;
        return true;
    }

    /**
     * Determines the coverage of this {@link org.openscience.cdk.interfaces.IAtomContainer}'s 2D
     * coordinates. If all atoms are non-null and have 2D coordinates this method will return {@link
     * CoordinateCoverage#FULL}. If one or more atoms does have 2D coordinates and any others atoms
     * are null or are missing 2D coordinates this method will return {@link
     * CoordinateCoverage#PARTIAL}. If all atoms are null or are all missing 2D coordinates this
     * method will return {@link CoordinateCoverage#NONE}. If the provided container is null {@link
     * CoordinateCoverage#NONE} is also returned.
     *
     * @param container the container to inspect
     * @return {@link CoordinateCoverage#FULL}, {@link CoordinateCoverage#PARTIAL} or {@link
     * CoordinateCoverage#NONE} depending on the number of 3D coordinates present
     * @see CoordinateCoverage
     * @see #has2DCoordinates(org.openscience.cdk.interfaces.IAtomContainer)
     * @see #get3DCoordinateCoverage(org.openscience.cdk.interfaces.IAtomContainer)
     * @see org.openscience.cdk.interfaces.IAtom#getPoint2d()
     */
    public static CoordinateCoverage get2DCoordinateCoverage(IAtomContainer container) {

        if (container == null || container.getAtomCount() == 0) return CoordinateCoverage.NONE;

        int count = 0;

        for (IAtom atom : container.atoms()) {
            count += atom != null && atom.getPoint2d() != null ? 1 : 0;
        }

        return count == 0 ? CoordinateCoverage.NONE : count == container.getAtomCount() ? CoordinateCoverage.FULL
                : CoordinateCoverage.PARTIAL;

    }

    /**
     * Determines if this AtomContainer contains 2D coordinates for some or all molecules. See
     * comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates)
     * for details on coordinate sets
     *
     * @param container the molecule to be considered
     * @return 0 no 2d, 1=some, 2= for each atom
     * @see #get2DCoordinateCoverage(org.openscience.cdk.interfaces.IAtomContainer)
     * @deprecated use {@link #get2DCoordinateCoverage(org.openscience.cdk.interfaces.IAtomContainer)}
     * for determining partial coordinates
     */
    @Deprecated
    public static int has2DCoordinatesNew(IAtomContainer container) {
        if (container == null) return 0;

        boolean no2d = false;
        boolean with2d = false;
        for (IAtom atom : container.atoms()) {
            if (atom.getPoint2d() == null) {
                no2d = true;
            } else {
                with2d = true;
            }
        }
        if (!no2d && with2d) {
            return 2;
        } else if (no2d && with2d) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Determines if this Atom contains 2D coordinates. See comment for center(IAtomContainer
     * atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param atom Description of the Parameter
     * @return boolean indication that 2D coordinates are available
     */
    public static boolean has2DCoordinates(IAtom atom) {
        return (atom.getPoint2d() != null);
    }

    /**
     * Determines if this Bond contains 2D coordinates. See comment for center(IAtomContainer
     * atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param bond Description of the Parameter
     * @return boolean indication that 2D coordinates are available
     */
    public static boolean has2DCoordinates(IBond bond) {
        for (IAtom iAtom : bond.atoms()) {
            if (iAtom.getPoint2d() == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if all this {@link org.openscience.cdk.interfaces.IAtomContainer}'s atoms contain
     * 3D coordinates. If any atom is null or has unset 3D coordinates this method will return
     * false. If the provided container is null false is returned.
     *
     * @param container the atom container to examine
     * @return indication that all 3D coordinates are available
     * @see org.openscience.cdk.interfaces.IAtom#getPoint3d()
     */
    public static boolean has3DCoordinates(IAtomContainer container) {

        if (container == null || container.getAtomCount() == 0) return Boolean.FALSE;

        for (IAtom atom : container.atoms()) {

            if (atom == null || atom.getPoint3d() == null) return Boolean.FALSE;

        }

        return Boolean.TRUE;

    }

    /**
     * Determines the coverage of this {@link org.openscience.cdk.interfaces.IAtomContainer}'s 3D
     * coordinates. If all atoms are non-null and have 3D coordinates this method will return {@link
     * CoordinateCoverage#FULL}. If one or more atoms does have 3D coordinates and any others atoms
     * are null or are missing 3D coordinates this method will return {@link
     * CoordinateCoverage#PARTIAL}. If all atoms are null or are all missing 3D coordinates this
     * method will return {@link CoordinateCoverage#NONE}. If the provided container is null {@link
     * CoordinateCoverage#NONE} is also returned.
     *
     * @param container the container to inspect
     * @return {@link CoordinateCoverage#FULL}, {@link CoordinateCoverage#PARTIAL} or {@link
     * CoordinateCoverage#NONE} depending on the number of 3D coordinates present
     * @see CoordinateCoverage
     * @see #has3DCoordinates(org.openscience.cdk.interfaces.IAtomContainer)
     * @see #get2DCoordinateCoverage(org.openscience.cdk.interfaces.IAtomContainer)
     * @see org.openscience.cdk.interfaces.IAtom#getPoint3d()
     */
    public static CoordinateCoverage get3DCoordinateCoverage(IAtomContainer container) {

        if (container == null || container.getAtomCount() == 0) return CoordinateCoverage.NONE;

        int count = 0;

        for (IAtom atom : container.atoms()) {
            count += atom != null && atom.getPoint3d() != null ? 1 : 0;
        }

        return count == 0 ? CoordinateCoverage.NONE : count == container.getAtomCount() ? CoordinateCoverage.FULL
                : CoordinateCoverage.PARTIAL;

    }

    /**
     * Determines the normalized vector orthogonal on the vector p1-&gt;p2.
     *
     * @param point1 Description of the Parameter
     * @param point2 Description of the Parameter
     * @return Description of the Return Value
     */
    public static Vector2d calculatePerpendicularUnitVector(Point2d point1, Point2d point2) {
        Vector2d vector = new Vector2d();
        vector.sub(point2, point1);
        vector.normalize();

        // Return the perpendicular vector
        return new Vector2d(-1.0 * vector.y, vector.x);
    }

    /**
     * Calculates the normalization factor in order to get an average bond length of 1.5. It takes
     * only into account Bond's with two atoms. See comment for center(IAtomContainer atomCon,
     * Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param container Description of the Parameter
     * @return The normalizationFactor value
     */
    public static double getNormalizationFactor(IAtomContainer container) {
        double bondlength = 0.0;
        double ratio;
        /*
         * Desired bond length for storing structures in MDL mol files This
         * should probably be set externally (from system wide settings)
         */
        double desiredBondLength = 1.5;
        // loop over all bonds and determine the mean bond distance
        int counter = 0;
        for (IBond bond : container.bonds()) {
            // only consider two atom bonds into account
            if (bond.getAtomCount() == 2) {
                counter++;
                IAtom atom1 = bond.getBegin();
                IAtom atom2 = bond.getEnd();
                bondlength += Math.sqrt(Math.pow(atom1.getPoint2d().x - atom2.getPoint2d().x, 2)
                        + Math.pow(atom1.getPoint2d().y - atom2.getPoint2d().y, 2));
            }
        }
        bondlength = bondlength / counter;
        ratio = desiredBondLength / bondlength;
        return ratio;
    }

    /**
     * Determines the best alignment for the label of an atom in 2D space. It returns 1 if left
     * aligned, and -1 if right aligned. See comment for center(IAtomContainer atomCon, Dimension
     * areaDim, HashMap renderingCoordinates) for details on coordinate sets
     *
     * @param container Description of the Parameter
     * @param atom      Description of the Parameter
     * @return The bestAlignmentForLabel value
     */
    public static int getBestAlignmentForLabel(IAtomContainer container, IAtom atom) {
        double overallDiffX = 0;
        for (IAtom connectedAtom : container.getConnectedAtomsList(atom)) {
            overallDiffX += connectedAtom.getPoint2d().x - atom.getPoint2d().x;
        }
        if (overallDiffX <= 0) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Determines the best alignment for the label of an atom in 2D space. It returns 1 if right
     * (=default) aligned, and -1 if left aligned. returns 2 if top aligned, and -2 if H is aligned
     * below the atom See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap
     * renderingCoordinates) for details on coordinate sets
     *
     * @param container Description of the Parameter
     * @param atom      Description of the Parameter
     * @return The bestAlignmentForLabel value
     */
    public static int getBestAlignmentForLabelXY(IAtomContainer container, IAtom atom) {
        double overallDiffX = 0;
        double overallDiffY = 0;
        for (IAtom connectedAtom : container.getConnectedAtomsList(atom)) {
            overallDiffX += connectedAtom.getPoint2d().x - atom.getPoint2d().x;
            overallDiffY += connectedAtom.getPoint2d().y - atom.getPoint2d().y;
        }
        if (Math.abs(overallDiffY) > Math.abs(overallDiffX)) {
            if (overallDiffY < 0)
                return 2;
            else
                return -2;
        } else {
            if (overallDiffX <= 0)
                return 1;
            else
                return -1;
        }
    }

    /**
     * Returns the atoms which are closes to an atom in an AtomContainer by distance in 3d.
     *
     * @param container The AtomContainer to examine
     * @param startAtom the atom to start from
     * @param max       the number of neighbours to return
     * @return the average bond length
     * @throws org.openscience.cdk.exception.CDKException Description of the Exception
     */
    public static List<IAtom> findClosestInSpace(IAtomContainer container, IAtom startAtom, int max)
            throws CDKException {
        Point3d originalPoint = startAtom.getPoint3d();
        if (originalPoint == null) {
            throw new CDKException("No point3d, but findClosestInSpace is working on point3ds");
        }
        Map<Double, IAtom> atomsByDistance = new TreeMap<Double, IAtom>();
        for (IAtom atom : container.atoms()) {
            if (!atom.equals(startAtom)) {
                if (atom.getPoint3d() == null) {
                    throw new CDKException("No point3d, but findClosestInSpace is working on point3ds");
                }
                double distance = atom.getPoint3d().distance(originalPoint);
                atomsByDistance.put(distance, atom);
            }
        }
        // FIXME: should there not be some sort here??
        Set<Double> keySet = atomsByDistance.keySet();
        Iterator<Double> keyIter = keySet.iterator();
        List<IAtom> returnValue = new ArrayList<IAtom>();
        int i = 0;
        while (keyIter.hasNext() && i < max) {
            returnValue.add(atomsByDistance.get(keyIter.next()));
            i++;
        }
        return (returnValue);
    }

    /**
     * Returns a Map with the AtomNumbers, the first number corresponds to the first (or the largest
     * AtomContainer) atomcontainer. It is recommend to sort the atomContainer due to their number
     * of atoms before calling this function.
     *
     * The molecules needs to be aligned before! (coordinates are needed)
     *
     * @param firstAtomContainer  the (largest) first aligned AtomContainer which is the reference
     * @param secondAtomContainer the second aligned AtomContainer
     * @param searchRadius        the radius of space search from each atom
     * @return a Map of the mapped atoms
     * @throws org.openscience.cdk.exception.CDKException Description of the Exception
     */
    public static Map<Integer, Integer> mapAtomsOfAlignedStructures(IAtomContainer firstAtomContainer,
            IAtomContainer secondAtomContainer, double searchRadius, Map<Integer, Integer> mappedAtoms)
            throws CDKException {
        getLargestAtomContainer(firstAtomContainer, secondAtomContainer);
        double[][] distanceMatrix = new double[firstAtomContainer.getAtomCount()][secondAtomContainer.getAtomCount()];
        for (int i = 0; i < firstAtomContainer.getAtomCount(); i++) {
            Point3d firstAtomPoint = firstAtomContainer.getAtom(i).getPoint3d();
            for (int j = 0; j < secondAtomContainer.getAtomCount(); j++) {
                distanceMatrix[i][j] = firstAtomPoint.distance(secondAtomContainer.getAtom(j).getPoint3d());
            }
        }

        double minimumDistance;
        for (int i = 0; i < firstAtomContainer.getAtomCount(); i++) {
            minimumDistance = searchRadius;
            for (int j = 0; j < secondAtomContainer.getAtomCount(); j++) {
                if (distanceMatrix[i][j] < searchRadius && distanceMatrix[i][j] < minimumDistance) {
                    //check atom properties
                    if (checkAtomMapping(firstAtomContainer, secondAtomContainer, i, j)) {
                        minimumDistance = distanceMatrix[i][j];
                        mappedAtoms.put(firstAtomContainer.indexOf(firstAtomContainer.getAtom(i)),
                                secondAtomContainer.indexOf(secondAtomContainer.getAtom(j)));
                    }
                }
            }
        }
        return mappedAtoms;
    }

    // FIXME: huh!?!?!
    private static void getLargestAtomContainer(IAtomContainer firstAC, IAtomContainer secondAC) {
        if (firstAC.getAtomCount() < secondAC.getAtomCount()) {
            IAtomContainer tmp;
            try {
                tmp = firstAC.clone();
                firstAC = secondAC.clone();
                secondAC = tmp.clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private static boolean checkAtomMapping(IAtomContainer firstAC, IAtomContainer secondAC, int posFirstAtom,
            int posSecondAtom) {
        IAtom firstAtom = firstAC.getAtom(posFirstAtom);
        IAtom secondAtom = secondAC.getAtom(posSecondAtom);
        // XXX: floating point comparision!
        return firstAtom.getSymbol().equals(secondAtom.getSymbol())
                && firstAC.getConnectedAtomsList(firstAtom).size() == secondAC.getConnectedAtomsList(secondAtom).size()
                && firstAtom.getBondOrderSum().equals(secondAtom.getBondOrderSum())
                && firstAtom.getMaxBondOrder() == secondAtom.getMaxBondOrder();
    }

    private static IAtomContainer setVisitedFlagsToFalse(IAtomContainer atomContainer) {
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            atomContainer.getAtom(i).setFlag(CDKConstants.VISITED, false);
        }
        return atomContainer;
    }

    /**
     * Return the RMSD of bonds length between the 2 aligned molecules.
     *
     * @param firstAtomContainer  the (largest) first aligned AtomContainer which is the reference
     * @param secondAtomContainer the second aligned AtomContainer
     * @param mappedAtoms         Map: a Map of the mapped atoms
     * @param Coords3d            boolean: true if moecules has 3D coords, false if molecules has 2D
     *                            coords
     * @return double: all the RMSD of bonds length
     */
    public static double getBondLengthRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer,
            Map<Integer, Integer> mappedAtoms, boolean Coords3d) {
        //logger.debug("**** GT getBondLengthRMSD ****");
        Iterator<Integer> firstAtoms = mappedAtoms.keySet().iterator();
        IAtom centerAtomFirstMolecule;
        IAtom centerAtomSecondMolecule;
        List<IAtom> connectedAtoms;
        double sum = 0;
        double n = 0;
        double distance1 = 0;
        double distance2 = 0;
        setVisitedFlagsToFalse(firstAtomContainer);
        setVisitedFlagsToFalse(secondAtomContainer);
        while (firstAtoms.hasNext()) {
            centerAtomFirstMolecule = firstAtomContainer.getAtom(firstAtoms.next());
            centerAtomFirstMolecule.setFlag(CDKConstants.VISITED, true);
            centerAtomSecondMolecule = secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer
                    .indexOf(centerAtomFirstMolecule)));
            connectedAtoms = firstAtomContainer.getConnectedAtomsList(centerAtomFirstMolecule);
            for (int i = 0; i < connectedAtoms.size(); i++) {
                IAtom conAtom = connectedAtoms.get(i);
                //this step is built to know if the program has already calculate a bond length (so as not to have duplicate values)
                if (!conAtom.getFlag(CDKConstants.VISITED)) {
                    if (Coords3d) {
                        distance1 = centerAtomFirstMolecule.getPoint3d().distance(conAtom.getPoint3d());
                        distance2 = centerAtomSecondMolecule.getPoint3d().distance(
                                secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer.indexOf(conAtom)))
                                        .getPoint3d());
                        sum = sum + Math.pow((distance1 - distance2), 2);
                        n++;
                    } else {
                        distance1 = centerAtomFirstMolecule.getPoint2d().distance(conAtom.getPoint2d());
                        distance2 = centerAtomSecondMolecule.getPoint2d().distance(
                                secondAtomContainer.getAtom(
                                        (mappedAtoms.get(firstAtomContainer.indexOf(conAtom)))).getPoint2d());
                        sum = sum + Math.pow((distance1 - distance2), 2);
                        n++;
                    }
                }
            }
        }
        setVisitedFlagsToFalse(firstAtomContainer);
        setVisitedFlagsToFalse(secondAtomContainer);
        return Math.sqrt(sum / n);
    }

    /**
     * Return the variation of each angle value between the 2 aligned molecules.
     *
     * @param firstAtomContainer  the (largest) first aligned AtomContainer which is the reference
     * @param secondAtomContainer the second aligned AtomContainer
     * @param mappedAtoms         Map: a Map of the mapped atoms
     * @return double: the value of the RMSD
     */
    public static double getAngleRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer,
            Map<Integer, Integer> mappedAtoms) {
        //logger.debug("**** GT getAngleRMSD ****");
        Iterator<Integer> firstAtoms = mappedAtoms.keySet().iterator();
        //logger.debug("mappedAtoms:"+mappedAtoms.toString());
        IAtom firstAtomfirstAC;
        IAtom centerAtomfirstAC;
        IAtom firstAtomsecondAC;
        IAtom secondAtomsecondAC;
        IAtom centerAtomsecondAC;
        double angleFirstMolecule;
        double angleSecondMolecule;
        double sum = 0;
        double n = 0;
        while (firstAtoms.hasNext()) {
            int firstAtomNumber = firstAtoms.next();
            centerAtomfirstAC = firstAtomContainer.getAtom(firstAtomNumber);
            List<IAtom> connectedAtoms = firstAtomContainer.getConnectedAtomsList(centerAtomfirstAC);
            if (connectedAtoms.size() > 1) {
                //logger.debug("If "+centerAtomfirstAC.getSymbol()+" is the center atom :");
                for (int i = 0; i < connectedAtoms.size() - 1; i++) {
                    firstAtomfirstAC = connectedAtoms.get(i);
                    for (int j = i + 1; j < connectedAtoms.size(); j++) {
                        angleFirstMolecule = getAngle(centerAtomfirstAC, firstAtomfirstAC, connectedAtoms.get(j));
                        centerAtomsecondAC = secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer
                                .indexOf(centerAtomfirstAC)));
                        firstAtomsecondAC = secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer
                                .indexOf(firstAtomfirstAC)));
                        secondAtomsecondAC = secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer
                                .indexOf(connectedAtoms.get(j))));
                        angleSecondMolecule = getAngle(centerAtomsecondAC, firstAtomsecondAC, secondAtomsecondAC);
                        sum = sum + Math.pow(angleFirstMolecule - angleSecondMolecule, 2);
                        n++;
                        //logger.debug("Error for the "+firstAtomfirstAC.getSymbol().toLowerCase()+"-"+centerAtomfirstAC.getSymbol()+"-"+connectedAtoms[j].getSymbol().toLowerCase()+" Angle :"+deltaAngle+" degrees");
                    }
                }
            }//if
        }
        return Math.sqrt(sum / n);
    }

    private static double getAngle(IAtom atom1, IAtom atom2, IAtom atom3) {

        Vector3d centerAtom = new Vector3d();
        centerAtom.x = atom1.getPoint3d().x;
        centerAtom.y = atom1.getPoint3d().y;
        centerAtom.z = atom1.getPoint3d().z;
        Vector3d firstAtom = new Vector3d();
        Vector3d secondAtom = new Vector3d();

        firstAtom.x = atom2.getPoint3d().x;
        firstAtom.y = atom2.getPoint3d().y;
        firstAtom.z = atom2.getPoint3d().z;

        secondAtom.x = atom3.getPoint3d().x;
        secondAtom.y = atom3.getPoint3d().y;
        secondAtom.z = atom3.getPoint3d().z;

        firstAtom.sub(centerAtom);
        secondAtom.sub(centerAtom);

        return firstAtom.angle(secondAtom);
    }

    /**
     * Return the RMSD between the 2 aligned molecules.
     *
     * @param firstAtomContainer  the (largest) first aligned AtomContainer which is the reference
     * @param secondAtomContainer the second aligned AtomContainer
     * @param mappedAtoms         Map: a Map of the mapped atoms
     * @param Coords3d            boolean: true if molecules has 3D coords, false if molecules has
     *                            2D coords
     * @return double: the value of the RMSD
     * @throws org.openscience.cdk.exception.CDKException if there is an error in getting mapped
     *                                                    atoms
     */
    public static double getAllAtomRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer,
            Map<Integer, Integer> mappedAtoms, boolean Coords3d) throws CDKException {
        //logger.debug("**** GT getAllAtomRMSD ****");
        double sum = 0;
        double RMSD;
        Iterator<Integer> firstAtoms = mappedAtoms.keySet().iterator();
        int firstAtomNumber;
        int secondAtomNumber;
        int n = 0;
        while (firstAtoms.hasNext()) {
            firstAtomNumber = firstAtoms.next();
            try {
                secondAtomNumber = mappedAtoms.get(firstAtomNumber);
                IAtom firstAtom = firstAtomContainer.getAtom(firstAtomNumber);
                if (Coords3d) {
                    sum = sum
                            + Math.pow(
                                    firstAtom.getPoint3d().distance(
                                            secondAtomContainer.getAtom(secondAtomNumber).getPoint3d()), 2);
                    n++;
                } else {
                    sum = sum
                            + Math.pow(
                                    firstAtom.getPoint2d().distance(
                                            secondAtomContainer.getAtom(secondAtomNumber).getPoint2d()), 2);
                    n++;
                }
            } catch (Exception ex) {
                throw new CDKException(ex.getMessage(), ex);
            }
        }
        RMSD = Math.sqrt(sum / n);
        return RMSD;
    }

    /**
     * Return the RMSD of the heavy atoms between the 2 aligned molecules.
     *
     * @param firstAtomContainer  the (largest) first aligned AtomContainer which is the reference
     * @param secondAtomContainer the second aligned AtomContainer
     * @param mappedAtoms         Map: a Map of the mapped atoms
     * @param hetAtomOnly         boolean: true if only hetero atoms should be considered
     * @param Coords3d            boolean: true if molecules has 3D coords, false if molecules has
     *                            2D coords
     * @return double: the value of the RMSD
     */
    public static double getHeavyAtomRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer,
            Map<Integer, Integer> mappedAtoms, boolean hetAtomOnly, boolean Coords3d) {
        //logger.debug("**** GT getAllAtomRMSD ****");
        double sum = 0;
        double RMSD;
        Iterator<Integer> firstAtoms = mappedAtoms.keySet().iterator();
        int firstAtomNumber;
        int secondAtomNumber;
        int n = 0;
        while (firstAtoms.hasNext()) {
            firstAtomNumber = firstAtoms.next();
            secondAtomNumber = mappedAtoms.get(firstAtomNumber);
            IAtom firstAtom = firstAtomContainer.getAtom(firstAtomNumber);
            if (hetAtomOnly) {
                if (!firstAtom.getSymbol().equals("H") && !firstAtom.getSymbol().equals("C")) {
                    if (Coords3d) {
                        sum = sum
                                + Math.pow(
                                        firstAtom.getPoint3d().distance(
                                                secondAtomContainer.getAtom(secondAtomNumber).getPoint3d()), 2);
                        n++;
                    } else {
                        sum = sum
                                + Math.pow(
                                        firstAtom.getPoint2d().distance(
                                                secondAtomContainer.getAtom(secondAtomNumber).getPoint2d()), 2);
                        n++;
                    }
                }
            } else {
                if (!firstAtom.getSymbol().equals("H")) {
                    if (Coords3d) {
                        sum = sum
                                + Math.pow(
                                        firstAtom.getPoint3d().distance(
                                                secondAtomContainer.getAtom(secondAtomNumber).getPoint3d()), 2);
                        n++;
                    } else {
                        sum = sum
                                + Math.pow(
                                        firstAtom.getPoint2d().distance(
                                                secondAtomContainer.getAtom(secondAtomNumber).getPoint2d()), 2);
                        n++;
                    }
                }
            }

        }
        RMSD = Math.sqrt(sum / n);
        return RMSD;
    }

    /**
     * An average of all 3D bond length values is produced, using point3ds in atoms. Atom's with no
     * coordinates are disregarded.
     *
     * @param container The AtomContainer for which the average bond length is to be calculated
     * @return the average bond length
     */
    public static double getBondLengthAverage3D(IAtomContainer container) {
        double bondLengthSum = 0;
        int bondCounter = 0;
        for (IBond bond : container.bonds()) {
            IAtom atom1 = bond.getBegin();
            IAtom atom2 = bond.getEnd();
            if (atom1.getPoint3d() != null && atom2.getPoint3d() != null) {
                bondCounter++;
                bondLengthSum += atom1.getPoint3d().distance(atom2.getPoint3d());
            }
        }
        return bondLengthSum / bondCounter;
    }

    /**
     * Shift the container horizontally to the right to make its bounds not overlap with the other
     * bounds. To avoid dependence on Java AWT, rectangles are described by arrays of double. Each
     * rectangle is specified by {minX, minY, maxX, maxY}.
     *
     * @param container the {@link IAtomContainer} to shift to the
     *                  right
     * @param bounds    the bounds of the {@link IAtomContainer} to shift
     * @param last      the bounds that is used as reference
     * @param gap       the gap between the two rectangles
     * @return the rectangle of the {@link IAtomContainer} after the shift
     */
    public static double[] shiftContainer(IAtomContainer container, double[] bounds, double[] last, double gap) {

        assert bounds.length == 4;
        assert last.length == 4;

        final double boundsMinX = bounds[0];
        final double boundsMinY = bounds[1];
        final double boundsMaxX = bounds[2];
        final double boundsMaxY = bounds[3];

        final double lastMaxX = last[2];

        // determine if the containers are overlapping
        if (lastMaxX + gap >= boundsMinX) {
            double xShift = lastMaxX + gap - boundsMinX;
            Vector2d shift = new Vector2d(xShift, 0.0);
            GeometryUtil.translate2D(container, shift);
            return new double[]{boundsMinX + xShift, boundsMinY, boundsMaxX + xShift, boundsMaxY};
        } else {
            // the containers are not overlapping
            return bounds;
        }
    }

    /*
     * Returns the average 2D bond length values of all products and reactants
     * of the given reaction. The method uses {@link
     * #getBondLengthAverage(IAtomContainer)} internally.
     * @param reaction The IReaction for which the average 2D bond length is
     * calculated
     * @return the average 2D bond length
     * @see #getBondLengthAverage(IAtomContainer)
     */
    public static double getBondLengthAverage(IReaction reaction) {
        double bondlenghtsum = 0.0;
        int containercount = 0;
        List<IAtomContainer> containers = ReactionManipulator.getAllAtomContainers(reaction);
        for (IAtomContainer container : containers) {
            containercount++;
            bondlenghtsum += getBondLengthAverage(container);
        }
        return bondlenghtsum / containercount;
    }

    /**
     * Calculate the median bond length of an atom container.
     *
     * @param container structure representation
     * @return median bond length
     * @throws java.lang.IllegalArgumentException unset coordinates or no bonds
     */
    public static double getBondLengthMedian(final IAtomContainer container) {
        if (container.getBondCount() == 0) throw new IllegalArgumentException("Container has no bonds.");
        int nBonds = 0;
        double[] lengths = new double[container.getBondCount()];
        for (int i = 0; i < container.getBondCount(); i++) {
            final IBond bond = container.getBond(i);
            final IAtom atom1 = bond.getBegin();
            final IAtom atom2 = bond.getEnd();
            Point2d p1 = atom1.getPoint2d();
            Point2d p2 = atom2.getPoint2d();
            if (p1 == null || p2 == null)
                throw new IllegalArgumentException("An atom has no 2D coordinates.");
            if (p1.x != p2.x || p1.y != p2.y)
                lengths[nBonds++] = p1.distance(p2);
        }
        Arrays.sort(lengths, 0, nBonds);
        return lengths[nBonds / 2];
    }

    /**
     * Determines if this model contains 3D coordinates for all atoms.
     *
     * @param chemModel the ChemModel to consider
     * @return Boolean indication that 3D coordinates are available for all atoms.
     */
    public static boolean has3DCoordinates(IChemModel chemModel) {
        List<IAtomContainer> acs = ChemModelManipulator.getAllAtomContainers(chemModel);
        for (IAtomContainer ac : acs) {
            if (!has3DCoordinates(ac)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Shift the containers in a reaction vertically upwards to not overlap with the reference
     * rectangle. The shift is such that the given gap is realized, but only if the reactions are
     * actually overlapping. To avoid dependence on Java AWT, rectangles are described by
     * arrays of double. Each rectangle is specified by {minX, minY, maxX, maxY}.
     *
     * @param reaction the reaction to shift
     * @param bounds   the bounds of the reaction to shift
     * @param last     the bounds of the last reaction
     * @return the rectangle of the shifted reaction
     */
    public static double[] shiftReactionVertical(IReaction reaction, double[] bounds, double[] last, double gap) {
        assert bounds.length == 4;
        assert last.length == 4;

        final double boundsMinX = bounds[0];
        final double boundsMinY = bounds[1];
        final double boundsMaxX = bounds[2];
        final double boundsMaxY = bounds[3];

        final double lastMinY = last[1];
        final double lastMaxY = last[3];

        final double boundsHeight = boundsMaxY - boundsMinY;
        final double lastHeight = lastMaxY - lastMinY;

        // determine if the reactions are overlapping
        if (lastMaxY + gap >= boundsMinY) {
            double yShift = boundsHeight + lastHeight + gap;
            Vector2d shift = new Vector2d(0, yShift);
            List<IAtomContainer> containers = ReactionManipulator.getAllAtomContainers(reaction);
            for (IAtomContainer container : containers) {
                translate2D(container, shift);
            }
            return new double[]{boundsMinX, boundsMinY + yShift, boundsMaxX, boundsMaxY + yShift};
        } else {
            // the reactions were not overlapping
            return bounds;
        }
    }

}
