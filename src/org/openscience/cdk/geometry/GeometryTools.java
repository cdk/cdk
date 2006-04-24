/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.geometry;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  A set of static utility classes for geometric calculations and operations.
 *  This class is extensively used, for example, by JChemPaint to edit molecule.
 *
 *@author        seb
 *@cdk.module    standard
 */
public class GeometryTools {

	private static LoggingTool logger = new LoggingTool(GeometryTools.class);


	/**
	 *  Adds an automatically calculated offset to the coordinates of all atoms
	 *  such that all coordinates are positive and the smallest x or y coordinate
	 *  is exactly zero, using an external set of coordinates.
	 *
	 *@param  atomCon  AtomContainer for which all the atoms are translated to
	 *      positive coordinates
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void translateAllPositive(IAtomContainer atomCon,HashMap renderingCoordinates) {
		double minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		IAtom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (renderingCoordinates.get(atoms[i]) == null && atoms[i].getPoint2d()!=null) {
				renderingCoordinates.put(atoms[i],new Point2d(atoms[i].getPoint2d().x,atoms[i].getPoint2d().y));
			}
			if (renderingCoordinates.get(atoms[i]) != null) {
				if (((Point2d)renderingCoordinates.get(atoms[i])).x < minX) {
					minX = ((Point2d)renderingCoordinates.get(atoms[i])).x;
				}
				if (((Point2d)renderingCoordinates.get(atoms[i])).y < minY) {
					minY = ((Point2d)renderingCoordinates.get(atoms[i])).y;
				}
			}
		}
		logger.debug("Translating: minx=" + minX + ", minY=" + minY);
		translate2D(atomCon, minX * -1, minY * -1, renderingCoordinates);
	}


	/**
	 *  Adds an automatically calculated offset to the coordinates of all atoms
	 *  such that all coordinates are positive and the smallest x or y coordinate
	 *  is exactly zero.
	 *
	 *@param  atomCon  AtomContainer for which all the atoms are translated to
	 *      positive coordinates
	 */
	public static void translateAllPositive(IAtomContainer atomCon) {
		double minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		IAtom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				if (atoms[i].getPoint2d().x < minX) {
					minX = atoms[i].getPoint2d().x;
				}
				if (atoms[i].getPoint2d().y < minY) {
					minY = atoms[i].getPoint2d().y;
				}
			}
		}
		logger.debug("Translating: minx=" + minX + ", minY=" + minY);
		translate2D(atomCon, minX * -1, minY * -1);
	}
	
	
	/**
	 *  Translates the given molecule by the given Vector.
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
	 *
	 *@param  atomCon      The molecule to be scaled
	 *@param  scaleFactor  Description of the Parameter
	 */
	public static void scaleMolecule(IAtomContainer atomCon, double scaleFactor) {
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			if (atomCon.getAtomAt(i).getPoint2d() != null) {
				atomCon.getAtomAt(i).getPoint2d().x *= scaleFactor;
				atomCon.getAtomAt(i).getPoint2d().y *= scaleFactor;
			}
		}
	}
	
	
	/**
	 *  Centers the molecule in the given area
	 *
	 *@param  atomCon  molecule to be centered
	 *@param  areaDim  dimension in which the molecule is to be centered
	 */
	public static void center(IAtomContainer atomCon, Dimension areaDim) {
		Dimension molDim = get2DDimension(atomCon);
		int transX = (int) ((areaDim.width - molDim.width) / 2);
		int transY = (int) ((areaDim.height - molDim.height) / 2);
		translateAllPositive(atomCon);
		translate2D(atomCon, new Vector2d(transX, transY));
	}
	
	
	/**
	 *  Translates a molecule from the origin to a new point denoted by a vector.
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  vector   dimension that represents the translation vector
	 */
	public static void translate2D(IAtomContainer atomCon, Vector2d vector) {
		IAtom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				atoms[i].getPoint2d().add(vector);
			} else {
				logger.warn("Could not translate atom in 2D space");
			}
		}
	}
	
	
	/**
	 *  Translates the given molecule by the given Vector, using an external set of coordinates.
	 *
	 *@param  atomCon  The molecule to be translated
	 *@param  transX   translation in x direction
	 *@param  transY   translation in y direction
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void translate2D(IAtomContainer atomCon, double transX, double transY,HashMap renderingCoordinates) {
		translate2D(atomCon, new Vector2d(transX, transY), renderingCoordinates);
	}


	/**
	 *  Multiplies all the coordinates of the atoms of the given molecule with the
	 *  scalefactor, using an external set of coordinates..
	 *
	 *@param  atomCon      The molecule to be scaled
	 *@param  scaleFactor  Description of the Parameter
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void scaleMolecule(IAtomContainer atomCon, double scaleFactor, HashMap renderingCoordinates) {
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			if(renderingCoordinates.get(atomCon.getAtomAt(i))!=null){
				((Point2d)renderingCoordinates.get(atomCon.getAtomAt(i))).x *= scaleFactor;
				((Point2d)renderingCoordinates.get(atomCon.getAtomAt(i))).y *= scaleFactor;
			}
		}
	}


	/**
	 *  Rotates a molecule around a given center by a given angle
	 *
	 *@param  atomCon  The molecule to be rotated
	 *@param  center   A point giving the rotation center
	 *@param  angle    The angle by which to rotate the molecule
	 */
	public static void rotate(IAtomContainer atomCon, Point2d center, double angle) {
		Point2d p = null;
		double distance;
		double offsetAngle;
		IAtom atom = null;
		for (int i = 0; i < atomCon.getAtomCount(); i++) {
			atom = atomCon.getAtomAt(i);
			p = atom.getPoint2d();
			offsetAngle = GeometryTools.getAngle(p.x - center.x, p.y - center.y);
			distance = p.distance(center);
			p.x = center.x + (Math.sin(angle + offsetAngle) * distance);
			p.y = center.y - (Math.cos(angle + offsetAngle) * distance);
		}
	}


	/**
	 *  Scales a molecule such that it fills a given percentage of a given
	 *  dimension, using an external set of coordinates
	 *
	 *@param  atomCon     The molecule to be scaled
	 *@param  areaDim     The dimension to be filled
	 *@param  fillFactor  The percentage of the dimension to be filled
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void scaleMolecule(IAtomContainer atomCon, Dimension areaDim, double fillFactor, HashMap renderingCoordinates) {
		Dimension molDim = get2DDimension(atomCon);
		double widthFactor = (double) areaDim.width / (double) molDim.width;
		double heightFactor = (double) areaDim.height / (double) molDim.height;
		double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
		scaleMolecule(atomCon, scaleFactor, renderingCoordinates);
	}


	/**
	 *  Returns the java.awt.Dimension of a molecule
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
	 *
	 *@param  container  Description of the Parameter
	 *@return            An four int array as defined above.
	 */
	public static double[] getMinMax(IAtomContainer container) {
		double maxX = Double.MIN_VALUE;
		double
				maxY = Double.MIN_VALUE;
		double
				minX = Double.MAX_VALUE;
		double
				minY = Double.MAX_VALUE;
		for (int i = 0; i < container.getAtomCount(); i++) {
			IAtom atom = container.getAtomAt(i);
			if (atom.getPoint2d() != null) {
				if (atom.getX2d() > maxX) {
					maxX = atom.getX2d();
				}
				if (atom.getX2d() < minX) {
					minX = atom.getX2d();
				}
				if (atom.getY2d() > maxY) {
					maxY = atom.getY2d();
				}
				if (atom.getY2d() < minY) {
					minY = atom.getY2d();
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
	 *  Translates a molecule from the origin to a new point denoted by a vector, using an external set of coordinates.
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  vector   dimension that represents the translation vector
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void translate2D(IAtomContainer atomCon, Vector2d vector, HashMap renderingCoordinates) {
		IAtom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (renderingCoordinates.get(atoms[i]) == null && atoms[i].getPoint2d()!=null) {
				renderingCoordinates.put(atoms[i],new Point2d(atoms[i].getPoint2d().x,atoms[i].getPoint2d().y));
			}
			if (((Point2d)renderingCoordinates.get(atoms[i])) != null) {
				((Point2d)renderingCoordinates.get(atoms[i])).add(vector);
			} else {
				logger.warn("Could not translate atom in 2D space");
			}
		}
	}


	/**
	 *  Translates a molecule from the origin to a new point denoted by a vector.
	 *
	 *@param  atomCon  molecule to be translated
	 *@param  p        Description of the Parameter
	 */
	public static void translate2DCentreOfMassTo(IAtomContainer atomCon, Point2d p) {
		Point2d com = get2DCentreOfMass(atomCon);
		Vector2d translation = new Vector2d(p.x - com.x, p.y - com.y);
		IAtom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				atoms[i].getPoint2d().add(translation);
			}
		}
	}


	/**
	 *  Centers the molecule in the given area, using an external set of coordinates
	 *
	 *@param  atomCon  molecule to be centered
	 *@param  areaDim  dimension in which the molecule is to be centered
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 */
	public static void center(IAtomContainer atomCon, Dimension areaDim, HashMap renderingCoordinates) {
		Dimension molDim = get2DDimension(atomCon);
		int transX = (int) ((areaDim.width - molDim.width) / 2);
		int transY = (int) ((areaDim.height - molDim.height) / 2);
		translateAllPositive(atomCon,renderingCoordinates);
		translate2D(atomCon, new Vector2d(transX, transY),renderingCoordinates);
	}


	/**
	 *  Calculates the center of the given atoms and returns it as a Point2d
	 *
	 *@param  atoms  The vector of the given atoms
	 *@return        The center of the given atoms as Point2d
	 */
	public static Point2d get2DCenter(IAtom[] atoms) {
		IAtom atom;
		double x = 0;
		double y = 0;
		for (int f = 0; f < atoms.length; f++) {
			atom = (IAtom) atoms[f];
			if (atom.getPoint2d() != null) {
				x += atom.getX2d();
				y += atom.getY2d();
			}
		}
		return new Point2d(x / (double) atoms.length, y / (double) atoms.length);
	}


	/**
	 *  Returns the geometric center of all the rings in this ringset.
	 *
	 *@param  ringSet  Description of the Parameter
	 *@return          the geometric center of the rings in this ringset
	 */
	public static Point2d get2DCenter(IRingSet ringSet) {
		double centerX = 0;
		double centerY = 0;
		for (int i = 0; i < ringSet.size(); i++) {
			Point2d centerPoint = GeometryTools.get2DCenter((org.openscience.cdk.interfaces.IRing)ringSet.get(i));
			centerX += centerPoint.x;
			centerY += centerPoint.y;
		}
		Point2d point = new Point2d(centerX / ((double) ringSet.size()), centerY / ((double) ringSet.size()));
		return point;
	}


	/**
	 *  Calculates the center of mass for the <code>Atom</code>s in the
	 *  AtomContainer for the 2D coordinates.
	 *
	 *@param  ac      AtomContainer for which the center of mass is calculated
	 *@return         Description of the Return Value
	 *@cdk.keyword    center of mass
	 */
	public static Point2d get2DCentreOfMass(IAtomContainer ac) {
		double x = 0.0;
		double y = 0.0;

		double totalmass = 0.0;

		Enumeration atoms = ac.atoms();
		while (atoms.hasMoreElements()) {
			IAtom a = (IAtom) atoms.nextElement();
			double mass = a.getExactMass();
			totalmass += mass;
			x += mass * a.getX2d();
			y += mass * a.getY2d();
		}

		return new Point2d(x / totalmass, y / totalmass);
	}


	/**
	 *  Returns the geometric center of all the atoms in the atomContainer.
	 *
	 *@param  container  Description of the Parameter
	 *@return            the geometric center of the atoms in this atomContainer
	 */
	public static Point2d get2DCenter(IAtomContainer container) {
		double centerX = 0;
		double centerY = 0;
		double counter = 0;
		IAtom[] atoms = container.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				centerX += atoms[i].getPoint2d().x;
				centerY += atoms[i].getPoint2d().y;
				counter++;
			}
		}
		Point2d point = new Point2d(centerX / (counter), centerY / (counter));
		return point;
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
		IAtom[] atoms = container.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				atoms[i].getPoint2d().add(translation);
			}
		}
	}


	/**
	 *  Calculates the center of mass for the <code>Atom</code>s in the
	 *  AtomContainer for the 2D coordinates.
	 *
	 *@param  ac      AtomContainer for which the center of mass is calculated
	 *@return         Description of the Return Value
	 *@cdk.keyword    center of mass
     * @cdk.dictref   blue-obelisk:calculate3DCenterOfMass
	 */
	public static Point3d get3DCentreOfMass(IAtomContainer ac) {
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;

		double totalmass = 0.0;

		Enumeration atoms = ac.atoms();
		while (atoms.hasMoreElements()) {
			IAtom a = (IAtom) atoms.nextElement();
			double mass = a.getExactMass();
			totalmass += mass;
			x += mass * a.getX3d();
			y += mass * a.getY3d();
			z += mass * a.getZ3d();
		}

		return new Point3d(x / totalmass, y / totalmass, z / totalmass);
	}


	/**
	 *  Returns the geometric center of all the atoms in this atomContainer.
	 *
	 *@param  ac  Description of the Parameter
	 *@return     the geometric center of the atoms in this atomContainer
	 */
	public static Point3d get3DCenter(IAtomContainer ac) {
		double centerX = 0;
		double centerY = 0;
		double centerZ = 0;
		double counter = 0;
		IAtom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint3d() != null) {
				centerX += atoms[i].getPoint3d().x;
				centerY += atoms[i].getPoint3d().y;
				centerZ += atoms[i].getPoint3d().z;
				counter++;
			}
		}
		Point3d point = new Point3d(centerX / (counter), centerY / (counter), centerZ / (counter));
		return point;
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
//		System.out.println("getAngle->xDiff: " + xDiff);
//		System.out.println("getAngle->yDiff: " + yDiff);
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

		int[] newCoords = {begin1X, begin1Y, begin2X, begin2Y, end1X, end1Y, end2X, end2Y};
		return newCoords;
	}


	/**
	 *  Writes the coordinates of the atoms participating the given bond into an
	 *  array, using renderingCoordinates, using an external set of coordinates.
	 *
	 *@param  bond  The given bond
	 *@param  renderingCoordindates The rendering coordinates, this is the hashmap getRenderingCoordinates from the Renderer2dModel
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return       The array with the coordinates
	 */
	public static int[] getBondCoordinates(IBond bond, HashMap renderingCoordinates) {
		if (renderingCoordinates.get(bond.getAtomAt(0)) == null && bond.getAtomAt(0).getPoint2d()!=null) {
			renderingCoordinates.put(bond.getAtomAt(0),new Point2d(bond.getAtomAt(0).getPoint2d().x,bond.getAtomAt(0).getPoint2d().y));
		}
		if (renderingCoordinates.get(bond.getAtomAt(1)) == null && bond.getAtomAt(1).getPoint2d()!=null) {
			renderingCoordinates.put(bond.getAtomAt(1),new Point2d(bond.getAtomAt(1).getPoint2d().x,bond.getAtomAt(1).getPoint2d().y));
		}		
		if (bond.getAtomAt(0).getPoint2d() == null || bond.getAtomAt(1).getPoint2d() == null) {
			logger.error("getBondCoordinates() called on Bond without 2D coordinates!");
			return new int[0];
		}
		int beginX = (int) ((Point2d)renderingCoordinates.get(bond.getAtomAt(0))).x;
		int endX = (int) ((Point2d)renderingCoordinates.get(bond.getAtomAt(1))).x;
		int beginY = (int) ((Point2d)renderingCoordinates.get(bond.getAtomAt(0))).y;
		int endY = (int) ((Point2d)renderingCoordinates.get(bond.getAtomAt(1))).y;
		int[] coords = {beginX, beginY, endX, endY};
		return coords;
	}
	
	
	/**
	 *  Writes the coordinates of the atoms participating the given bond into an
	 *  array.
	 *
	 *@param  bond  The given bond
	 *@return       The array with the coordinates
	 */
	public static int[] getBondCoordinates(IBond bond) {
		if (bond.getAtomAt(0).getPoint2d() == null ||
				bond.getAtomAt(1).getPoint2d() == null) {
			logger.error("getBondCoordinates() called on Bond without 2D coordinates!");
			return new int[0];
		}
		int beginX = (int) bond.getAtomAt(0).getPoint2d().x;
		int endX = (int) bond.getAtomAt(1).getPoint2d().x;
		int beginY = (int) bond.getAtomAt(0).getPoint2d().y;
		int endY = (int) bond.getAtomAt(1).getPoint2d().y;
		int[] coords = {beginX, beginY, endX, endY};
		return coords;
	}
	

	/**
	 *  Returns the atom of the given molecule that is closest to the given
	 *  coordinates, using an external set of coordinates.
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest atom
	 *@param   renderingCoordinates  The set of coordinates to use coming from RendererModel2D
	 *@return            The atom that is closest to the given coordinates
	 */
	public static IAtom getClosestAtom(int xPosition, int yPosition, IChemModel model, IAtom ignore, HashMap renderingCoordinates) {
		IAtom closestAtom = null;
		IAtom currentAtom;
		double smallestMouseDistance = -1;
		double mouseDistance;
		double atomX;
		double atomY;
		if(model.getSetOfMolecules()!=null){
			for(int k=0;k<model.getSetOfMolecules().getAtomContainerCount();k++){
				IAtomContainer atomCon=model.getSetOfMolecules().getAtomContainer(k);
				for (int i = 0; i < atomCon.getAtomCount(); i++) {
					currentAtom = atomCon.getAtomAt(i);
					if (renderingCoordinates.get(currentAtom) == null && currentAtom.getPoint2d()!=null) {
						renderingCoordinates.put(currentAtom,new Point2d(currentAtom.getPoint2d().x,currentAtom.getPoint2d().y));
					}
					if(currentAtom!=ignore && renderingCoordinates.get(currentAtom)!=null){
						atomX =((Point2d) renderingCoordinates.get(currentAtom)).x;
						atomY = ((Point2d)renderingCoordinates.get(currentAtom)).y;
						mouseDistance = Math.sqrt(Math.pow(atomX - xPosition, 2) + Math.pow(atomY - yPosition, 2));
						if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1) {
							smallestMouseDistance = mouseDistance;
							closestAtom = currentAtom;
						}
					}
				}
			}
		}
		return closestAtom;
	}
	
	
	/**
	 *  Returns the atom of the given molecule that is closest to the given
	 *  coordinates.
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
			currentAtom = atomCon.getAtomAt(i);
			atomX = currentAtom.getX2d();
			atomY = currentAtom.getY2d();
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
	 *
	 *@param  xPosition  The x coordinate
	 *@param  yPosition  The y coordinate
	 *@param  atomCon    The molecule that is searched for the closest bond
	 *@return            The bond that is closest to the given coordinates
	 */
	public static IBond getClosestBond(int xPosition, int yPosition, IAtomContainer atomCon) {
		Point2d bondCenter;
		IBond closestBond = null;
		IBond currentBond;
		double smallestMouseDistance = -1;
		double mouseDistance;
		IBond[] bonds = atomCon.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			currentBond = bonds[i];
			bondCenter = get2DCenter(currentBond.getAtoms());
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
	 *
	 *@param  point  The point from which the distances to the atoms are measured
	 *@param  atoms  The atoms for which the distances to point are measured
	 */
	public static void sortBy2DDistance(IAtom[] atoms, Point2d point) {
		double distance1;
		double distance2;
		IAtom atom1 = null;
		IAtom atom2 = null;
		boolean doneSomething = false;
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
	 *
	 *@param  ac          The AtomContainer for which the ScaleFactor is to be
	 *      calculated
	 *@param  bondLength  The target bond length
	 *@return             The ScaleFactor with which the AtomContainer must be
	 *      scaled to have the target bond length
	 */

	public static double getScaleFactor(IAtomContainer ac, double bondLength) {
		double currentAverageBondLength = getBondLengthAverage(ac);
    if(currentAverageBondLength==0 || Double.isNaN(currentAverageBondLength))
      return 1;
		return bondLength / currentAverageBondLength;
	}


	/**
	 *  Determines the scale factor for displaying a structure loaded from disk in
	 *  a frame. An average of all bond length values is produced and a scale
	 *  factor is determined which would scale the given molecule such that its
	 *
	 *@param  ac          The AtomContainer for which the ScaleFactor is to be
	 *      calculated
	 *@param  bondLength  The target bond length
	 *@return             The ScaleFactor with which the AtomContainer must be
	 *      scaled to have the target bond length
	 */

	public static double getScaleFactor(IAtomContainer ac, double bondLength, HashMap renderingCoordinates) {
		IAtom[] atoms = ac.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (renderingCoordinates.get(atoms[i]) == null && atoms[i].getPoint2d()!=null) {
				renderingCoordinates.put(atoms[i],new Point2d(atoms[i].getPoint2d().x,atoms[i].getPoint2d().y));
			}
		}
		double currentAverageBondLength = getBondLengthAverage(ac,renderingCoordinates);
    if(currentAverageBondLength==0 || Double.isNaN(currentAverageBondLength))
      return 1;
		return bondLength / currentAverageBondLength;
	}
	
	
	/**
	 *  An average of all 2D bond length values is produced. Bonds which have
	 *  Atom's with no coordinates are disregarded.
	 *
	 *@param  ac  The AtomContainer for which the average bond length is to be
	 *      calculated
	 *@return     the average bond length
	 */
	public static double getBondLengthAverage(IAtomContainer ac, HashMap renderingCoordinates) {
		double bondLengthSum = 0;
		IBond[] bonds = ac.getBonds();
		int bondCounter = 0;
		for (int f = 0; f < bonds.length; f++) {
			IBond bond = bonds[f];
			org.openscience.cdk.interfaces.IAtom atom1 = bond.getAtomAt(0);
			org.openscience.cdk.interfaces.IAtom atom2 = bond.getAtomAt(1);
			if (renderingCoordinates.get(atom1) != null &&
					renderingCoordinates.get(atom2) != null) {
				bondCounter++;
				bondLengthSum += getLength2D(bond, renderingCoordinates);
			}
		}
		return bondLengthSum / bondCounter;
	}
	
	
	/**
	 *  An average of all 2D bond length values is produced. Bonds which have
	 *  Atom's with no coordinates are disregarded.
	 *
	 *@param  ac  The AtomContainer for which the average bond length is to be
	 *      calculated
	 *@return     the average bond length
	 */
	public static double getBondLengthAverage(IAtomContainer ac) {
		double bondLengthSum = 0;
		IBond[] bonds = ac.getBonds();
		int bondCounter = 0;
		for (int f = 0; f < bonds.length; f++) {
			IBond bond = bonds[f];
			org.openscience.cdk.interfaces.IAtom atom1 = bond.getAtomAt(0);
			org.openscience.cdk.interfaces.IAtom atom2 = bond.getAtomAt(1);
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
	 *
	 *@param  bond  Description of the Parameter
	 *@return       The geometric length of this bond
	 */
	public static double getLength2D(IBond bond, HashMap renderingCoordinates) {
		if (bond.getAtomAt(0) == null ||
				bond.getAtomAt(1) == null) {
			return 0.0;
		}
		Point2d p1 = ((Point2d)renderingCoordinates.get(bond.getAtomAt(0)));
		Point2d p2 = ((Point2d)renderingCoordinates.get(bond.getAtomAt(1)));
		if (p1 == null || p2 == null) {
			return 0.0;
		}
		return p1.distance(p2);
	}
	
	/**
	 *  Returns the geometric length of this bond in 2D space.
	 *
	 *@param  bond  Description of the Parameter
	 *@return       The geometric length of this bond
	 */
	public static double getLength2D(IBond bond) {
		if (bond.getAtomAt(0) == null ||
				bond.getAtomAt(1) == null) {
			return 0.0;
		}
		Point2d p1 = bond.getAtomAt(0).getPoint2d();
		Point2d p2 = bond.getAtomAt(1).getPoint2d();
		if (p1 == null || p2 == null) {
			return 0.0;
		}
		return p1.distance(p2);
	}


	/**
	 *  Determines if this AtomContainer contains 2D coordinates.
	 *
	 *@param  m  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
	 */
	public static boolean has2DCoordinates(IAtomContainer m) {
		return has2DCoordinatesNew(m)>0;
	}
	
	
	/**
	 *  Determines if this AtomContainer contains 2D coordinates for some or all molecules.
	 *
	 *@param  m  Description of the Parameter
	 *@return    0 no 2d, 1=some, 2= for each atom
	 */
	public static int has2DCoordinatesNew(IAtomContainer m) {
		boolean no2d=false;
		boolean with2d=false;
		IAtom[] atoms = m.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() == null) {
				no2d=true;
			}else{
				with2d=true;
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
	 *
	 *@param  a  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
	 */
	public static boolean has2DCoordinates(IAtom a) {
		return (a.getPoint2d() != null);
	}


	/**
	 *  Determines if this Bond contains 2D coordinates.
	 *
	 *@param  b  Description of the Parameter
	 *@return    boolean indication that 2D coordinates are available
	 */
	public static boolean has2DCoordinates(IBond b) {
		org.openscience.cdk.interfaces.IAtom[] atoms = b.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() == null) {
				return false;
			}
		}
		return true;
	}


	/**
	 *  Determines if this model contains 3D coordinates
	 *
	 *@param  m  Description of the Parameter
	 *@return    boolean indication that 3D coordinates are available
	 */
	public static boolean has3DCoordinates(IAtomContainer m) {
		boolean hasinfo = true;
		IAtom[] atoms = m.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint3d() == null) {
				return false;
			}
		}
		return hasinfo;
	}


	/**
	 *  Determines the normalized vector orthogonal on the vector p1->p2.
	 *
	 *@param  p1  Description of the Parameter
	 *@param  p2  Description of the Parameter
	 *@return     Description of the Return Value
	 */
	public static Vector2d calculatePerpendicularUnitVector(Point2d p1, Point2d p2) {
		Vector2d v = new Vector2d();
		v.sub(p2, p1);
		v.normalize();

		// Return the perpendicular vector
		return new Vector2d(-1.0 * v.y, v.x);
	}


	/**
	 *  Calculates the normalization factor in order to get an average bond length
	 *  of 1.5. It takes only into account Bond's with two atoms.
	 *
	 *@param  container  Description of the Parameter
	 *@return            The normalizationFactor value
	 */
	public static double getNormalizationFactor(IAtomContainer container) {
		IBond[] bonds = container.getBonds();
		double bondlength = 0.0;
		double ratio = 0.0;
		/*
		 *  Desired bond length for storing structures in MDL mol files
		 *  This should probably be set externally (from system wide settings)
		 */
		double desiredBondLength = 1.5;
		// loop over all bonds and determine the mean bond distance
		int counter = 0;
		for (int f = 0; f < bonds.length; f++) {
			// only consider two atom bonds into account
			if (bonds[f].getAtomCount() == 2) {
				counter++;
				org.openscience.cdk.interfaces.IAtom atom1 = bonds[f].getAtomAt(0);
				org.openscience.cdk.interfaces.IAtom atom2 = bonds[f].getAtomAt(1);
				bondlength += Math.sqrt(Math.pow(atom1.getX2d() - atom2.getX2d(), 2) +
						Math.pow(atom1.getY2d() - atom2.getY2d(), 2));
			}
		}
		bondlength = bondlength / counter;
		ratio = desiredBondLength / bondlength;
		return ratio;
	}


	/**
	 *  Determines the best alignment for the label of an atom in 2D space. It
	 *  returns 1 if left aligned, and -1 if right aligned.
	 *
	 *@param  container  Description of the Parameter
	 *@param  atom       Description of the Parameter
	 *@return            The bestAlignmentForLabel value
	 */
	public static int getBestAlignmentForLabel(IAtomContainer container, IAtom atom) {
		IAtom[] connectedAtoms = container.getConnectedAtoms(atom);
		int overallDiffX = 0;
		for (int i = 0; i < connectedAtoms.length; i++) {
			IAtom connectedAtom = connectedAtoms[i];
			overallDiffX = overallDiffX + (int) (connectedAtom.getX2d() - atom.getX2d());
		}
		if (overallDiffX <= 0) {
			return 1;
		} else {
			return -1;
		}
	}


	/**
	 *  Returns the atoms which are closes to an atom in an AtomContainer by
	 *  distance in 3d.
	 *
	 *@param  ac                The AtomContainer to examine
	 *@param  a                 the atom to start from
	 *@param  max               the number of neighbours to return
	 *@return                   the average bond length
	 *@exception  CDKException  Description of the Exception
	 */
	public static Vector findClosestInSpace(IAtomContainer ac, IAtom a, int max) throws CDKException {
		IAtom[] atoms = ac.getAtoms();
		Point3d originalPoint = a.getPoint3d();
		if (originalPoint == null) {
			throw new CDKException("No point3d, but findClosestInSpace is working on point3ds");
		}
		Map hm = new TreeMap();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i] != a) {
				if (atoms[i].getPoint3d() == null) {
					throw new CDKException("No point3d, but findClosestInSpace is working on point3ds");
				}
				double distance = atoms[i].getPoint3d().distance(originalPoint);
				hm.put(new Double(distance), atoms[i]);
			}
		}
		Set ks = hm.keySet();
		Iterator it = ks.iterator();
		Vector returnValue = new Vector();
		int i = 0;
		while (it.hasNext() && i < max) {
			returnValue.add(hm.get(it.next()));
			i++;
		}
		return (returnValue);
	}
}


