/*  $Revision$ $Author$ $Date$
 *
 *  Copyright (C) 1997-2008  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A set of static utility classes for geometric calculations and operations.
 * This class is extensively used, for example, by JChemPaint to edit molecule.
 * All methods in this class change the coordinates of the atoms. Use GeometryTools if you use an external set of coordinates (e. g. rendeirngCoordinates from RendererModel)
 *
 * @author        seb
 * @author        Stefan Kuhn
 * @author        Egon Willighagen
 * @author        Ludovic Petain
 * @author        Christian Hoppe
 * @author        Niels Out
 * 
 * @cdk.module    standard
 * @cdk.svnrev    $Revision$
 */
public class GeometryTools {

	private static LoggingTool logger = new LoggingTool(GeometryTools.class);

	/**
	 *  Adds an automatically calculated offset to the coordinates of all atoms
	 *  such that all coordinates are positive and the smallest x or y coordinate
	 *  is exactly zero.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  AtomContainer for which all the atoms are translated to
	 *      positive coordinates
	 */
	public static void translateAllPositive(IAtomContainer atomCon) {
		double minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		Iterator<IAtom> atoms = atomCon.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
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
	 *  Translates the given molecule by the given Vector.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  The molecule to be translated
	 *@param  transX   translation in x direction
	 *@param  transY   translation in y direction
	 */
	public static void translate2D(IAtomContainer atomCon, double transX, double transY) {
		translate2D(atomCon, new Vector2d(transX, transY));
	}


	/**
	 *  Scales a molecule such that it fills a given percentage of a given
	 *  dimension
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon     The molecule to be scaled
	 *@param  areaDim     The dimension to be filled
	 *@param  fillFactor  The percentage of the dimension to be filled
	 */
	public static void scaleMolecule(IAtomContainer atomCon, Dimension areaDim, double fillFactor) {
		Dimension molDim = get2DDimension(atomCon);
		double widthFactor = (double) areaDim.width / (double) molDim.width;
		double heightFactor = (double) areaDim.height / (double) molDim.height;
		double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
		scaleMolecule(atomCon, scaleFactor);
	}


	/**
	 *  Multiplies all the coordinates of the atoms of the given molecule with the
	 *  scalefactor.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon      The molecule to be scaled
	 *@param  scaleFactor  Description of the Parameter
	 */
	public static void scaleMolecule(IAtomContainer atomCon, double scaleFactor) {
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			if (atomCon.getAtom(i).getPoint2d() != null) {
				atomCon.getAtom(i).getPoint2d().x *= scaleFactor;
				atomCon.getAtom(i).getPoint2d().y *= scaleFactor;
			}
		}
	}


	/**
	 *  Centers the molecule in the given area
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  molecule to be centered
	 *@param  areaDim  dimension in which the molecule is to be centered
	 */
	public static void center(IAtomContainer atomCon, Dimension areaDim) {
		Dimension molDim = get2DDimension(atomCon);
		int transX = (areaDim.width - molDim.width) / 2;
		int transY = (areaDim.height - molDim.height) / 2;
		translateAllPositive(atomCon);
		translate2D(atomCon, new Vector2d(transX, transY));
	}


	/**
	 *  Translates a molecule from the origin to a new point denoted by a vector.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  vector   dimension that represents the translation vector
	 */
	public static void translate2D(IAtomContainer atomCon, Vector2d vector) {
        for (IAtom atom : atomCon.atoms()) {
            if (atom.getPoint2d() != null) {
                atom.getPoint2d().add(vector);
            } else {
                logger.warn("Could not translate atom in 2D space");
            }
        }
	}


	/**
	 *  Rotates a molecule around a given center by a given angle
	 *
	 *@param  atomCon  The molecule to be rotated
	 *@param  center   A point giving the rotation center
	 *@param  angle    The angle by which to rotate the molecule, in radians
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
     * Positive angles are anticlockwise looking down the axis towards the origin.
     * Assume right hand coordinate system.
     *
     * @param atom The atom to rotate
     * @param p1  The  first point of the line segment
     * @param p2  The second point of the line segment
     * @param angle  The angle to rotate by (in degrees)
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
	 *  Returns the java.awt.Dimension of a molecule
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  of which the dimension should be returned
	 *@return          The java.awt.Dimension of this molecule
	 */
	public static Dimension get2DDimension(IAtomContainer atomCon) {
		double[] minmax = getMinMax(atomCon);
		double maxX = minmax[2];
		double
				maxY = minmax[3];
		double
				minX = minmax[0];
		double
				minY = minmax[1];
		return new Dimension((int) (maxX - minX + 1), (int) (maxY - minY + 1));
	}


	/**
	 *  Returns the minimum and maximum X and Y coordinates of the atoms in the
	 *  AtomContainer. The output is returned as: <pre>
	 *   minmax[0] = minX;
	 *   minmax[1] = minY;
	 *   minmax[2] = maxX;
	 *   minmax[3] = maxY;
	 * </pre>
	 * See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@return            An four int array as defined above.
	 */
	public static double[] getMinMax(IAtomContainer container) {
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		for (int i = 0; i < container.getAtomCount(); i++) {
			IAtom atom = container.getAtom(i);
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
	 *  Translates a molecule from the origin to a new point denoted by a vector.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  p        Description of the Parameter
	 */
	public static void translate2DCentreOfMassTo(IAtomContainer atomCon, Point2d p) {
		Point2d com = get2DCentreOfMass(atomCon);
		Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
		Iterator<IAtom> atoms = atomCon.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (atom.getPoint2d() != null) {
				atom.getPoint2d().add(translation);
			}
		}
	}


	/**
	 *  Calculates the center of the given atoms and returns it as a Point2d
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atoms  The vector of the given atoms
	 *@return        The center of the given atoms as Point2d
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
	 *  Calculates the center of the given atoms and returns it as a Point2d
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atoms  The Iterator of the given atoms
	 *@return        The center of the given atoms as Point2d
	 */
	public static Point2d get2DCenter(Iterator<IAtom> atoms) {
		IAtom atom;
		double xsum = 0;
		double ysum = 0;
		int length = 0;
		while (atoms.hasNext()) {
			atom = (IAtom)atoms.next();
			if (atom.getPoint2d() != null) {
				xsum += atom.getPoint2d().x;
				ysum += atom.getPoint2d().y;
			}
			++length;
		}
		return new Point2d(xsum / (double) length, ysum / (double) length);
	}
	

	/**
	 *  Returns the geometric center of all the rings in this ringset.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ringSet  Description of the Parameter
	 *@return          the geometric center of the rings in this ringset
	 */
	public static Point2d get2DCenter(IRingSet ringSet) {
		double centerX = 0;
		double centerY = 0;
		for (int i = 0; i < ringSet.getAtomContainerCount(); i++) {
			Point2d centerPoint = get2DCenter((org.openscience.cdk.interfaces.IRing)ringSet.getAtomContainer(i));
			centerX += centerPoint.x;
			centerY += centerPoint.y;
		}
        return new Point2d(centerX / ((double) ringSet.getAtomContainerCount()), centerY / ((double) ringSet.getAtomContainerCount()));
	}


	/**
	 *  Calculates the center of mass for the <code>Atom</code>s in the
	 *  AtomContainer for the 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac      AtomContainer for which the center of mass is calculated
	 *@return         Null, if any of the atomcontainer {@link IAtom}'s masses are null
	 *@cdk.keyword    center of mass
	 */
	public static Point2d get2DCentreOfMass(IAtomContainer ac) {
		double xsum = 0.0;
		double ysum = 0.0;

		double totalmass = 0.0;

		Iterator<IAtom> atoms = ac.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom a = (IAtom) atoms.next();
			Double mass = a.getExactMass();
			if (mass == null) return null;
			totalmass += mass;
			xsum += mass * a.getPoint2d().x;
			ysum += mass * a.getPoint2d().y;
		}

		return new Point2d(xsum / totalmass, ysum / totalmass);
	}


	/**
	 *  Returns the geometric center of all the atoms in the atomContainer.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@return            the geometric center of the atoms in this atomContainer
	 */
	public static Point2d get2DCenter(IAtomContainer container) {
		double centerX = 0;
		double centerY = 0;
		double counter = 0;
		Iterator<IAtom> atoms = container.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (atom.getPoint2d() != null) {
				centerX += atom.getPoint2d().x;
				centerY += atom.getPoint2d().y;
				counter++;
			}
		}
        return new Point2d(centerX / (counter), centerY / (counter));
	}

	
	/**
	 *  Translates the geometric 2DCenter of the given
	 *  AtomContainer container to the specified Point2d p.
	 *
	 *@param  container  AtomContainer which should be translated.
	 *@param  p          New Location of the geometric 2D Center.
	 *@see #get2DCenter
	 *@see #translate2DCentreOfMassTo
	 */
	public static void translate2DCenterTo(IAtomContainer container, Point2d p) {
		Point2d com = get2DCenter(container);
		Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
		Iterator<IAtom> atoms = container.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
			if (atom.getPoint2d() != null) {
				atom.getPoint2d().add(translation);
			}
		}
	}


	/**
	 *  Calculates the center of mass for the <code>Atom</code>s in the
	 *  AtomContainer for the 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac      AtomContainer for which the center of mass is calculated
	 *@return         Description of the Return Value
	 *@cdk.keyword    center of mass
     * @cdk.dictref   blue-obelisk:calculate3DCenterOfMass
	 */
	public static Point3d get3DCentreOfMass(IAtomContainer ac) {
		double xsum = 0.0;
		double ysum = 0.0;
		double zsum = 0.0;

		double totalmass = 0.0;

		Iterator<IAtom> atoms = ac.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom a = (IAtom)atoms.next();
			Double mass = a.getExactMass();
			// some sanity checking
			if (a.getPoint3d() == null) return null;
			if (mass == null) return null;

			totalmass += mass;
			xsum += mass * a.getPoint3d().x;
			ysum += mass * a.getPoint3d().y;
			zsum += mass * a.getPoint3d().z;
		}

		return new Point3d(xsum / totalmass, ysum / totalmass, zsum / totalmass);
	}


	/**
	 *  Returns the geometric center of all the atoms in this atomContainer.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  ac  Description of the Parameter
	 *@return     the geometric center of the atoms in this atomContainer
	 */
	public static Point3d get3DCenter(IAtomContainer ac) {
		double centerX = 0;
		double centerY = 0;
		double centerZ = 0;
		double counter = 0;
		Iterator<IAtom> atoms = ac.atoms().iterator();
		while (atoms.hasNext()) {
			IAtom atom = (IAtom)atoms.next();
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
	 *  Gets the angle attribute of the GeometryTools class
	 *
	 *@param  xDiff  Description of the Parameter
	 *@param  yDiff  Description of the Parameter
	 *@return        The angle value
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
	 *  Gets the coordinates of two points (that represent a bond) and calculates
	 *  for each the coordinates of two new points that have the given distance
	 *  vertical to the bond.
	 *
	 *@param  coords  The coordinates of the two given points of the bond like this
	 *      [point1x, point1y, point2x, point2y]
	 *@param  dist    The vertical distance between the given points and those to
	 *      be calculated
	 *@return         The coordinates of the calculated four points
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
			angle = Math.atan(((double) coords[3] - (double) coords[1]) / ((double) coords[2] - (double) coords[0]));
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
	 *  Writes the coordinates of the atoms participating the given bond into an
	 *  array.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  bond  The given bond
	 *@return       The array with the coordinates
	 */
	public static int[] getBondCoordinates(IBond bond) {
		if (bond.getAtom(0).getPoint2d() == null ||
				bond.getAtom(1).getPoint2d() == null) {
			logger.error("getBondCoordinates() called on Bond without 2D coordinates!");
			return new int[0];
		}
		int beginX = (int) bond.getAtom(0).getPoint2d().x;
		int endX = (int) bond.getAtom(1).getPoint2d().x;
		int beginY = (int) bond.getAtom(0).getPoint2d().y;
		int endY = (int) bond.getAtom(1).getPoint2d().y;
        return new int[]{beginX, beginY, endX, endY};
	}


	/**
	 *  Returns the atom of the given molecule that is closest to the given
	 *  coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest atom
	 *@return            The atom that is closest to the given coordinates
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
	 *  Returns the atom of the given molecule that is closest to the given
	 *  coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest atom
	 *@return            The atom that is closest to the given coordinates
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
	 *  Returns the bond of the given molecule that is closest to the given
	 *  coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest bond
	 *@return            The bond that is closest to the given coordinates
	 */
	public static IBond getClosestBond(int xPosition, int yPosition, IAtomContainer atomCon) {
		Point2d bondCenter;
		IBond closestBond = null;

		double smallestMouseDistance = -1;
		double mouseDistance;
        Iterator<IBond> bonds = atomCon.bonds().iterator();
        while (bonds.hasNext()) {
            IBond currentBond =  (IBond) bonds.next();
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
	 *  Returns the bond of the given molecule that is closest to the given
	 *  coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest bond
	 *@return            The bond that is closest to the given coordinates
	 */
	public static IBond getClosestBond(double xPosition, double yPosition, IAtomContainer atomCon) {
		Point2d bondCenter;
		IBond closestBond = null;

		double smallestMouseDistance = -1;
		double mouseDistance;
        Iterator<IBond> bonds = atomCon.bonds().iterator();
        while (bonds.hasNext()) {
            IBond currentBond =  (IBond) bonds.next();
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
	 *  Sorts a Vector of atoms such that the 2D distances of the atom locations
	 *  from a given point are smallest for the first atoms in the vector
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  point  The point from which the distances to the atoms are measured
	 *@param  atoms  The atoms for which the distances to point are measured
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
	 *  Determines the scale factor for displaying a structure loaded from disk in
	 *  a frame. An average of all bond length values is produced and a scale
	 *  factor is determined which would scale the given molecule such that its
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container          The AtomContainer for which the ScaleFactor is to be
	 *      calculated
	 *@param  bondLength  The target bond length
	 *@return             The ScaleFactor with which the AtomContainer must be
	 *      scaled to have the target bond length
	 */

	public static double getScaleFactor(IAtomContainer container, double bondLength) {
		double currentAverageBondLength = getBondLengthAverage(container);
    if(currentAverageBondLength==0 || Double.isNaN(currentAverageBondLength))
      return 1;
		return bondLength / currentAverageBondLength;
	}


	/**
	 *  An average of all 2D bond length values is produced. Bonds which have
	 *  Atom's with no coordinates are disregarded.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  The AtomContainer for which the average bond length is to be
	 *      calculated
	 *@return     the average bond length
	 */
	public static double getBondLengthAverage(IAtomContainer container) {
		double bondLengthSum = 0;
        Iterator<IBond> bonds = container.bonds().iterator();
        int bondCounter = 0;
        while (bonds.hasNext()) {
            IBond bond = bonds.next();
			IAtom atom1 = bond.getAtom(0);
			IAtom atom2 = bond.getAtom(1);
			if (atom1.getPoint2d() != null &&
					atom2.getPoint2d() != null) {
				bondCounter++;
				bondLengthSum += getLength2D(bond);
			}
		}
		return bondLengthSum / bondCounter;
	}


	/**
	 *  Returns the geometric length of this bond in 2D space.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  bond  Description of the Parameter
	 *@return       The geometric length of this bond
	 */
	public static double getLength2D(IBond bond) {
		if (bond.getAtom(0) == null ||
				bond.getAtom(1) == null) {
			return 0.0;
		}
		Point2d point1 = bond.getAtom(0).getPoint2d();
		Point2d point2 = bond.getAtom(1).getPoint2d();
		if (point1 == null || point2 == null) {
			return 0.0;
		}
		return point1.distance(point2);
	}


	/**
	 *  Determines if this AtomContainer contains 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
	 */
	public static boolean has2DCoordinates(IAtomContainer container) {
		return has2DCoordinatesNew(container)>0;
	}


	/**
	 *  Determines if this AtomContainer contains 2D coordinates for some or all molecules.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *
	 *@param container the molecule to be considered
     * @return    0 no 2d, 1=some, 2= for each atom
	 */
	public static int has2DCoordinatesNew(IAtomContainer container) {
		if (container == null) return 0;
		
		boolean no2d=false;
		boolean with2d=false;
        for (IAtom atom : container.atoms()) {
            if (atom.getPoint2d() == null) {
                no2d = true;
            } else {
                with2d = true;
            }
        }
		if(!no2d && with2d){
			return 2;
		} else if(no2d && with2d){
			return 1;
		} else{
			return 0;
		}
	}


	/**
	 *  Determines if this Atom contains 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  atom  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
	 */
	public static boolean has2DCoordinates(IAtom atom) {
		return (atom.getPoint2d() != null);
	}


	/**
	 *  Determines if this Bond contains 2D coordinates.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  bond  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
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
	 *  Determines if this model contains 3D coordinates
	 *
	 *@param container the molecule to consider
	 *@return    boolean indication that 3D coordinates are available
	 */
	public static boolean has3DCoordinates(IAtomContainer container) {
		boolean hasinfo = true;
        for (IAtom atom : container.atoms()) {
            if (atom.getPoint3d() == null) {
                return false;
            }
        }
		return hasinfo;
	}


	/**
	 *  Determines the normalized vector orthogonal on the vector p1->p2.
	 *
	 *@param  point1  Description of the Parameter
	 *@param  point2  Description of the Parameter
	 *@return     Description of the Return Value
	 */
	public static Vector2d calculatePerpendicularUnitVector(Point2d point1, Point2d point2) {
		Vector2d vector = new Vector2d();
		vector.sub(point2, point1);
		vector.normalize();

		// Return the perpendicular vector
		return new Vector2d(-1.0 * vector.y, vector.x);
	}


	/**
	 *  Calculates the normalization factor in order to get an average bond length
	 *  of 1.5. It takes only into account Bond's with two atoms.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@return            The normalizationFactor value
	 */
	public static double getNormalizationFactor(IAtomContainer container) {
		double bondlength = 0.0;
		double ratio;
		/*
		 *  Desired bond length for storing structures in MDL mol files
		 *  This should probably be set externally (from system wide settings)
		 */
		double desiredBondLength = 1.5;
		// loop over all bonds and determine the mean bond distance
		int counter = 0;
        for (IBond bond : container.bonds()) {
            // only consider two atom bonds into account
            if (bond.getAtomCount() == 2) {
                counter++;
                IAtom atom1 = bond.getAtom(0);
                IAtom atom2 = bond.getAtom(1);
                bondlength += Math.sqrt(Math.pow(atom1.getPoint2d().x - atom2.getPoint2d().x, 2) +
                        Math.pow(atom1.getPoint2d().y - atom2.getPoint2d().y, 2));
            }
        }
		bondlength = bondlength / counter;
		ratio = desiredBondLength / bondlength;
		return ratio;
	}


	/**
	 *  Determines the best alignment for the label of an atom in 2D space. It
	 *  returns 1 if left aligned, and -1 if right aligned.
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@param  atom       Description of the Parameter
	 *@return            The bestAlignmentForLabel value
	 */
	public static int getBestAlignmentForLabel(IAtomContainer container, IAtom atom) {
		int overallDiffX = 0;
		for (IAtom connectedAtom : container.getConnectedAtomsList(atom)) {
			overallDiffX = overallDiffX + (int) (connectedAtom.getPoint2d().x - atom.getPoint2d().x);
		}
		if (overallDiffX <= 0) {
			return 1;
		} else {
			return -1;
		}
	}

	/**
	 *  Determines the best alignment for the label of an atom in 2D space. It
	 *  returns 1 if right (=default) aligned, and -1 if left aligned.
	 *  returns 2 if top aligned, and -2 if H is aligned below the atom
	 *  See comment for center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) for details on coordinate sets
	 *
	 *@param  container  Description of the Parameter
	 *@param  atom       Description of the Parameter
	 *@return            The bestAlignmentForLabel value
	 */
	public static int getBestAlignmentForLabelXY(IAtomContainer container, IAtom atom) {
		double overallDiffX = 0;
		double overallDiffY = 0;
		for (IAtom connectedAtom : container.getConnectedAtomsList(atom)) {
			overallDiffX += connectedAtom.getPoint2d().x - atom.getPoint2d().x;
			overallDiffY += connectedAtom.getPoint2d().y - atom.getPoint2d().y;
		}
		if (overallDiffX <= 0) {
			if (overallDiffX < overallDiffY)
				return 1;//right aligned
			else
				return 2;//top aligned.
		} else {
			if (overallDiffX > overallDiffY)
				return -1;//left aligned
			else
				return -2;//H below aligned
		}
	}


	/**
	 *  Returns the atoms which are closes to an atom in an AtomContainer by
	 *  distance in 3d.
	 *
	 *@param  container         The AtomContainer to examine
	 *@param  startAtom         the atom to start from
	 *@param  max               the number of neighbours to return
	 *@return                   the average bond length
	 *@exception  CDKException  Description of the Exception
	 */
	public static List<IAtom> findClosestInSpace(IAtomContainer container, IAtom startAtom, int max) throws CDKException {
		Point3d originalPoint = startAtom.getPoint3d();
		if (originalPoint == null) {
			throw new CDKException("No point3d, but findClosestInSpace is working on point3ds");
		}
		Map<Double,IAtom> atomsByDistance = new TreeMap<Double,IAtom>();
		for (IAtom atom : container.atoms()) {
			if (atom != startAtom) {
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
	 *  Returns a Map with the AtomNumbers, the first number corresponds to the first (or the largest
	 *  AtomContainer) atomcontainer. It is recommend to sort the atomContainer due to their number of atoms before
	 *  calling this function.
	 *  
	 *  The molecules needs to be aligned before! (coordinates are needed)
	 *
	 *@param  firstAtomContainer                the (largest) first aligned AtomContainer which is the reference
	 *@param  secondAtomContainer               the second aligned AtomContainer
	 *@param  searchRadius               		the radius of space search from each atom
	 *@return                   				a Map of the mapped atoms
	 *@exception  CDKException  Description of the Exception
	 */
	public static Map<Integer,Integer> mapAtomsOfAlignedStructures(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer, 
			double searchRadius, Map<Integer,Integer> mappedAtoms)throws CDKException {
		//to return the mapping setProperty("MappedAtom",AtomNumber)
		//logger.debug("**** MAP ATOMS ****");
		getLargestAtomContainer(firstAtomContainer,secondAtomContainer);
		double[][] distanceMatrix=new double[firstAtomContainer.getAtomCount()][secondAtomContainer.getAtomCount()];
		for (int i=0;i<firstAtomContainer.getAtomCount();i++){
			Point3d firstAtomPoint=firstAtomContainer.getAtom(i).getPoint3d();
			//logger.debug("Closest atoms of "+firstAtomContainer.getAtoms()[i].getSymbol()+" :");
			for (int j=0;j<secondAtomContainer.getAtomCount();j++){
				distanceMatrix[i][j]=firstAtomPoint.distance(secondAtomContainer.getAtom(j).getPoint3d());
				//logger.debug("Distance "+i+" "+j+":"+distanceMatrix[i][j]);
			}
			//logger.debug(" Atoms from the secondAtomContainer");
		}
		
		//logger.debug();
		//logger.debug("\t");
		//for (int j=0;j<secondAtomContainer.getAtomCount();j++){
			//logger.debug(j+" "+secondAtomContainer.getAtomAt(j).getSymbol()+"\t");
		//}
		//DEBUGG
		/*double tmp=0;
		for(int i=0;i<firstAtomContainer.getAtomCount();i++){
			//logger.debug(i+" "+firstAtomContainer.getAtomAt(i).getSymbol()+"\t");
			for (int j=0;j<secondAtomContainer.getAtomCount();j++){
				tmp=Math.floor(distanceMatrix[i][j]*10);
				//logger.debug(tmp/10+"\t");
			}			
		}*/
		
		double minimumDistance;
        for(int i=0;i<firstAtomContainer.getAtomCount();i++){
            minimumDistance=searchRadius;
            for (int j=0;j<secondAtomContainer.getAtomCount();j++){
                if(distanceMatrix[i][j]< searchRadius && distanceMatrix[i][j]< minimumDistance){
                    //logger.debug("Distance OK "+i+" "+j+":"+distanceMatrix[i][j]+" AtomCheck:"+checkAtomMapping(firstAtomContainer,secondAtomContainer, i, j));
                    //check atom properties
                    if (checkAtomMapping(firstAtomContainer,secondAtomContainer, i, j)){
                        minimumDistance=distanceMatrix[i][j];
                        mappedAtoms.put(
                        	firstAtomContainer.getAtomNumber(firstAtomContainer.getAtom(i)),
                        	secondAtomContainer.getAtomNumber(secondAtomContainer.getAtom(j))
                        );
                        //firstAtomContainer.getAtomAt(i).setProperty("MappedAtom",new Integer(secondAtomContainer.getAtomNumber(secondAtomContainer.getAtomAt(j))));
                        //logger.debug("#:"+countMappedAtoms+" Atom:"+i+" is mapped to Atom"+j);
                        //logger.debug(firstAtomContainer.getConnectedAtoms(firstAtomContainer.getAtomAt(i)).length);
                    }
                }
            }
        }
		return mappedAtoms;
	}

	// FIXME: huh!?!?!
    private static void getLargestAtomContainer(IAtomContainer firstAC, IAtomContainer secondAC) {
        if (firstAC.getAtomCount() < secondAC.getAtomCount()){
            IAtomContainer tmp;
            try {
                tmp = (IAtomContainer) firstAC.clone();
                firstAC=(IAtomContainer)secondAC.clone();
                secondAC=(IAtomContainer)tmp.clone();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
	
	private static boolean checkAtomMapping(IAtomContainer firstAC, IAtomContainer secondAC, int posFirstAtom, int posSecondAtom){
		IAtom firstAtom=firstAC.getAtom(posFirstAtom);
		IAtom secondAtom=secondAC.getAtom(posSecondAtom);
		if (firstAtom.getSymbol().equals(secondAtom.getSymbol()) && firstAC.getConnectedAtomsList(firstAtom).size() == secondAC.getConnectedAtomsList(secondAtom).size() &&
				firstAtom.getBondOrderSum() == secondAtom.getBondOrderSum() &&
				firstAtom.getMaxBondOrder() == secondAtom.getMaxBondOrder() 
		        ){
			return true;
		}else {
			return false;
		}
	}
	
	private static IAtomContainer setVisitedFlagsToFalse(IAtomContainer atomContainer) {
        for (int i=0;i<atomContainer.getAtomCount();i++){
            atomContainer.getAtom(i).setFlag(CDKConstants.VISITED, false);
        }
        return atomContainer;
    }
	
	/**
	 *  Return the RMSD of bonds length between the 2 aligned molecules.
	 *
	 *@param  firstAtomContainer                the (largest) first aligned AtomContainer which is the reference
	 *@param  secondAtomContainer               the second aligned AtomContainer
	 *@param  mappedAtoms             			Map: a Map of the mapped atoms
	 *@param  Coords3d            			    boolean: true if moecules has 3D coords, false if molecules has 2D coords
	 *@return                   				double: all the RMSD of bonds length
	 *
	 **/
	public static double getBondLengthRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer,
			Map<Integer,Integer> mappedAtoms, boolean Coords3d) {
        //logger.debug("**** GT getBondLengthRMSD ****");
        Iterator<Integer> firstAtoms=mappedAtoms.keySet().iterator();
        IAtom centerAtomFirstMolecule;
        IAtom centerAtomSecondMolecule;
        List<IAtom> connectedAtoms;
        double sum=0;
        double n=0;
        double distance1=0;
        double distance2=0;
        setVisitedFlagsToFalse(firstAtomContainer);
        setVisitedFlagsToFalse(secondAtomContainer);
        while(firstAtoms.hasNext()){
            centerAtomFirstMolecule=firstAtomContainer.getAtom(firstAtoms.next());
            centerAtomFirstMolecule.setFlag(CDKConstants.VISITED, true);
            centerAtomSecondMolecule=secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer.getAtomNumber(centerAtomFirstMolecule)));
            connectedAtoms=firstAtomContainer.getConnectedAtomsList(centerAtomFirstMolecule);
            for (int i=0;i<connectedAtoms.size();i++){
            	IAtom conAtom = (IAtom)connectedAtoms.get(i);
                //this step is built to know if the program has already calculate a bond length (so as not to have duplicate values)
                if(!conAtom.getFlag(CDKConstants.VISITED)){
                    if (Coords3d){
                        distance1=((Point3d)centerAtomFirstMolecule.getPoint3d()).distance(conAtom.getPoint3d());
                        distance2=((Point3d)centerAtomSecondMolecule.getPoint3d()).distance(secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer.getAtomNumber(conAtom))).getPoint3d());
                        sum=sum+Math.pow((distance1-distance2),2);
                        n++;
                    }else{
                        distance1=((Point2d)centerAtomFirstMolecule.getPoint2d()).distance(conAtom.getPoint2d());
                        distance2=((Point2d)centerAtomSecondMolecule.getPoint2d()).distance(secondAtomContainer.getAtom((mappedAtoms.get(firstAtomContainer.getAtomNumber(conAtom)))).getPoint2d());
                        sum=sum+Math.pow((distance1-distance2),2);
                        n++;
                    }
                }
            }
        }
        setVisitedFlagsToFalse(firstAtomContainer);
        setVisitedFlagsToFalse(secondAtomContainer);
        return Math.sqrt(sum/n);
    }
	/**
	 *  Return the variation of each angle value between the 2 aligned molecules.
	 *
	 *@param  firstAtomContainer                the (largest) first aligned AtomContainer which is the reference
	 *@param  secondAtomContainer               the second aligned AtomContainer
	 *@param  mappedAtoms             			Map: a Map of the mapped atoms
	 *@return                   				double: the value of the RMSD
	 *
	 **/
	public static double getAngleRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer, Map<Integer,Integer> mappedAtoms) {
        //logger.debug("**** GT getAngleRMSD ****");
        Iterator<Integer> firstAtoms=mappedAtoms.keySet().iterator();
        //logger.debug("mappedAtoms:"+mappedAtoms.toString());
        IAtom firstAtomfirstAC;
        IAtom centerAtomfirstAC;
        IAtom firstAtomsecondAC;
        IAtom secondAtomsecondAC;
        IAtom centerAtomsecondAC;
        double angleFirstMolecule;
        double angleSecondMolecule;
        double sum=0;
        double n=0;
        while(firstAtoms.hasNext()){
            int firstAtomNumber= firstAtoms.next();
            centerAtomfirstAC=firstAtomContainer.getAtom(firstAtomNumber);
            List<IAtom> connectedAtoms=firstAtomContainer.getConnectedAtomsList(centerAtomfirstAC);
            if (connectedAtoms.size() >1){
                //logger.debug("If "+centerAtomfirstAC.getSymbol()+" is the center atom :");
                for (int i=0; i < connectedAtoms.size()-1;i++){
                    firstAtomfirstAC=(IAtom)connectedAtoms.get(i);
                    for (int j=i+1; j < connectedAtoms.size();j++){
                        angleFirstMolecule=getAngle(centerAtomfirstAC,firstAtomfirstAC,(IAtom)connectedAtoms.get(j));
                        centerAtomsecondAC=secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer.getAtomNumber(centerAtomfirstAC)));
                        firstAtomsecondAC=secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer.getAtomNumber(firstAtomfirstAC)));
                        secondAtomsecondAC=secondAtomContainer.getAtom(mappedAtoms.get(firstAtomContainer.getAtomNumber((IAtom)connectedAtoms.get(j))));
                        angleSecondMolecule=getAngle(centerAtomsecondAC,firstAtomsecondAC,secondAtomsecondAC);
                        sum=sum+Math.pow(angleFirstMolecule-angleSecondMolecule,2);
                        n++;
                        //logger.debug("Error for the "+firstAtomfirstAC.getSymbol().toLowerCase()+"-"+centerAtomfirstAC.getSymbol()+"-"+connectedAtoms[j].getSymbol().toLowerCase()+" Angle :"+deltaAngle+" degrees");
                    }
                }
            }//if
        }
        return Math.sqrt(sum/n);
    }
	
	private static double getAngle(IAtom atom1, IAtom atom2, IAtom atom3){
		
		Vector3d centerAtom = new Vector3d();
		centerAtom.x=atom1.getPoint3d().x;
		centerAtom.y=atom1.getPoint3d().y;
		centerAtom.z=atom1.getPoint3d().z;
		Vector3d firstAtom = new Vector3d();
		Vector3d secondAtom = new Vector3d();
			
		firstAtom.x=atom2.getPoint3d().x;
		firstAtom.y=atom2.getPoint3d().y;
		firstAtom.z=atom2.getPoint3d().z;
				
		secondAtom.x=atom3.getPoint3d().x;
		secondAtom.y=atom3.getPoint3d().y;
		secondAtom.z=atom3.getPoint3d().z;
				
		firstAtom.sub(centerAtom);
		secondAtom.sub(centerAtom);
				
		return firstAtom.angle(secondAtom);
	}
	
	/**
	 *  Return the RMSD between the 2 aligned molecules.
	 *
	 *@param  firstAtomContainer                the (largest) first aligned AtomContainer which is the reference
	 *@param  secondAtomContainer               the second aligned AtomContainer
	 *@param  mappedAtoms             			Map: a Map of the mapped atoms
	 *@param  Coords3d            			    boolean: true if moecules has 3D coords, false if molecules has 2D coords
	 *@return                   				double: the value of the RMSD 
	 *@exception  CDKException  if there is an error in getting mapped atoms
	 *
	 **/
	public static double getAllAtomRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer, 
		    Map<Integer,Integer> mappedAtoms, boolean Coords3d)throws CDKException {
		//logger.debug("**** GT getAllAtomRMSD ****");
		double sum=0;
		double RMSD;
		Iterator<Integer> firstAtoms=mappedAtoms.keySet().iterator();
		int firstAtomNumber;
		int secondAtomNumber;
		int n=0;
		while(firstAtoms.hasNext()){
			firstAtomNumber= firstAtoms.next();
			try{
				secondAtomNumber= mappedAtoms.get(firstAtomNumber);
				IAtom firstAtom=firstAtomContainer.getAtom(firstAtomNumber);
				if (Coords3d){
					sum=sum+Math.pow(firstAtom.getPoint3d().distance(secondAtomContainer.getAtom(secondAtomNumber).getPoint3d()),2);
					n++;
				}else{
					sum=sum+Math.pow(firstAtom.getPoint2d().distance(secondAtomContainer.getAtom(secondAtomNumber).getPoint2d()),2);
					n++;
				}
			}catch (Exception ex){
                throw new CDKException(ex.toString());
            }
		}
		RMSD=Math.sqrt(sum/n);
		return RMSD;
	}
	
	/**
	 *  Return the RMSD of the heavy atoms between the 2 aligned molecules.
	 *
	 *@param  firstAtomContainer                the (largest) first aligned AtomContainer which is the reference
	 *@param  secondAtomContainer               the second aligned AtomContainer
	 *@param  mappedAtoms             			Map: a Map of the mapped atoms
	 *@param hetAtomOnly                        boolean: true if only hetero atoms should be considered
	 *@param  Coords3d            			    boolean: true if moecules has 3D coords, false if molecules has 2D coords
	 *@return                   				double: the value of the RMSD 
	 *
	 **/
	public static double getHeavyAtomRMSD(IAtomContainer firstAtomContainer, IAtomContainer secondAtomContainer,
			Map<Integer,Integer> mappedAtoms, boolean hetAtomOnly ,boolean Coords3d) {
		//logger.debug("**** GT getAllAtomRMSD ****");
		double sum=0;
		double RMSD=0;
		Iterator<Integer> firstAtoms=mappedAtoms.keySet().iterator();
		int firstAtomNumber=0;
		int secondAtomNumber=0;
		int n=0;
		while(firstAtoms.hasNext()){
			firstAtomNumber=firstAtoms.next();
				secondAtomNumber=mappedAtoms.get(firstAtomNumber);
				IAtom firstAtom=firstAtomContainer.getAtom(firstAtomNumber);
				if (hetAtomOnly){
					if (!firstAtom.getSymbol().equals("H") && !firstAtom.getSymbol().equals("C")){
						if (Coords3d){
							sum=sum+Math.pow(((Point3d)firstAtom.getPoint3d()).distance(secondAtomContainer.getAtom(secondAtomNumber).getPoint3d()),2);
							n++;
						}else{
							sum=sum+Math.pow(((Point2d)firstAtom.getPoint2d()).distance(secondAtomContainer.getAtom(secondAtomNumber).getPoint2d()),2);
							n++;
						}
					}
				}else{
					if (!firstAtom.getSymbol().equals("H")){
						if (Coords3d){
							sum=sum+Math.pow(((Point3d)firstAtom.getPoint3d()).distance(secondAtomContainer.getAtom(secondAtomNumber).getPoint3d()),2);
							n++;
						}else{
							sum=sum+Math.pow(((Point2d)firstAtom.getPoint2d()).distance(secondAtomContainer.getAtom(secondAtomNumber).getPoint2d()),2);
							n++;
						}
					}
				}

		}
		RMSD=Math.sqrt(sum/n);
		return RMSD;
	}



	/**
	 *  An average of all 3D bond length values is produced, using point3ds in atoms.
	 *  Atom's with no coordinates are disregarded.
	 *
	 *@param  container  The AtomContainer for which the average bond length is to be
	 *      calculated
	 *@return     the average bond length
	 */
	public static double getBondLengthAverage3D(IAtomContainer container) {
		double bondLengthSum = 0;
        int bondCounter = 0;
        for (IBond bond : container.bonds()) {
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if (atom1.getPoint3d() != null &&
                    atom2.getPoint3d() != null) {
                bondCounter++;
                bondLengthSum += atom1.getPoint3d().distance(atom2.getPoint3d());
            }
        }
		return bondLengthSum / bondCounter;
	}
}
