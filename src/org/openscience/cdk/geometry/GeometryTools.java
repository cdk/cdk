/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.geometry;

import java.awt.Dimension;
import java.util.Vector;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomEnumeration;
import org.openscience.cdk.Bond;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A set of static utility classes for geometric calculations and operations.
 * This class is extensively used, for example, by JChemPaint to edit molecule.
 *
 * @cdk.module standard
 */
public class GeometryTools {

    private static LoggingTool logger = null;
    
    static {
        if (logger == null) logger = new LoggingTool("org.openscience.cdk.geometry.GeometryTools");
    }
    
	/**
	 * Adds an automatically calculated offset to the coordinates of all atoms
	 * such that all coordinates are positive and the smallest x or y coordinate 
	 * is exactly zero.
	 *
	 * @param   atomCon AtomContainer for which all the atoms are translated to positive coordinates
	 */
    public static void translateAllPositive(AtomContainer atomCon) {
        double minX = Double.MAX_VALUE, 
               minY = Double.MAX_VALUE;
        Atom[] atoms = atomCon.getAtoms();
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
	 * Translates the given molecule by the given Vector.
	 *
	 * @param   atomCon The molecule to be translated
	 * @param   transX  translation in x direction
	 * @param   transY  translation in y direction
	 */
	public static void translate2D(AtomContainer atomCon, double transX, double transY)
	{
		translate2D(atomCon, new Vector2d(transX, transY));
	}
	

	/**
	 * Multiplies all the coordinates of the atoms of the given molecule with the scalefactor.
	 *
	 * @param   atomCon  The molecule to be scaled
	 */
	public static void scaleMolecule(AtomContainer atomCon, double scaleFactor) {
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			if (atomCon.getAtomAt(i).getPoint2d() != null)
			{
				atomCon.getAtomAt(i).getPoint2d().x *= scaleFactor;
				atomCon.getAtomAt(i).getPoint2d().y *= scaleFactor;
			}
		}
	}
	

	/**
	 * Rotates a molecule around a given center by a given angle
	 *
	 * @param   atomCon  The molecule to be rotated
	 * @param   center    A point giving the rotation center
	 * @param   angle      The angle by which to rotate the molecule 
	 */
	public static void rotate(AtomContainer atomCon, Point2d center, double angle)
	{
		Point2d p = null;
		double distance;
		double offsetAngle;
		Atom atom = null;
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			atom = atomCon.getAtomAt(i);		
			p = atom.getPoint2d();
			offsetAngle = GeometryTools.getAngle(p.x - center.x, p.y - center.y);
			distance = p.distance(center);
			p.x = center.x + (Math.sin(angle + offsetAngle) * distance);
			p.y = center.y - (Math.cos(angle + offsetAngle) * distance);				
		}
	}


    /**
	 * Scales a molecule such that it fills a given percentage of a given dimension
	 *
	 * @param   atomCon    The molecule to be scaled
	 * @param   areaDim    The dimension to be filled
	 * @param   fillFactor The percentage of the dimension to be filled
	 */
	public static void scaleMolecule(AtomContainer atomCon, Dimension areaDim, double fillFactor)
	{
		Dimension molDim = get2DDimension(atomCon);
		double widthFactor = (double)areaDim.width / (double)molDim.width;
		double heightFactor = (double)areaDim.height / (double)molDim.height;
		double scaleFactor = Math.min(widthFactor, heightFactor) * fillFactor;
		scaleMolecule(atomCon, scaleFactor);
	}

	/**
	 * Returns the java.awt.Dimension of a molecule
	 *
	 * @param  atomCon of which the dimension should be returned
	 * @return         The java.awt.Dimension of this molecule
	 */
	public static Dimension get2DDimension(AtomContainer atomCon) {
        double[] minmax = getMinMax(atomCon);
		double maxX = minmax[2],
               maxY = minmax[3], 
               minX = minmax[0], 
               minY = minmax[1];
		return new Dimension((int)(maxX - minX + 1), (int)(maxY - minY + 1));
	}

    /**
     * Returns the minimum and maximum X and Y coordinates of the
     * atoms in the AtomContainer. The output is returned as:
     * <pre>
     *   minmax[0] = minX;
     *   minmax[1] = minY;
     *   minmax[2] = maxX;
     *   minmax[3] = maxY;
     * </pre>
     *
     * @return An four int array as defined above.
     */
    public static double[] getMinMax(AtomContainer container) {
		double maxX = Double.MIN_VALUE, 
               maxY = Double.MIN_VALUE, 
               minX = Double.MAX_VALUE, 
               minY = Double.MAX_VALUE;
		for (int i = 0; i < container.getAtomCount(); i++) {
            Atom atom = container.getAtomAt(i);
			if (atom.getPoint2d() != null) {
				if (atom.getX2d() > maxX) maxX = atom.getX2d();
				if (atom.getX2d() < minX) minX = atom.getX2d();
				if (atom.getY2d() > maxY) maxY = atom.getY2d();
				if (atom.getY2d() < minY) minY = atom.getY2d();
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
	 * Translates a molecule from the origin to a new point denoted by a vector.
	 *
	 * @param atomCon  molecule to be translated
	 * @param vector   dimension that represents the translation vector
	 */
    public static void translate2D(AtomContainer atomCon, Vector2d vector) {
        Atom[] atoms = atomCon.getAtoms();
        for (int i = 0; i < atoms.length; i++) {
            if (atoms[i].getPoint2d() != null) {
                atoms[i].getPoint2d().add(vector);
            } else {
                logger.warn("Could not translate atom in 2D space");
            }
        }
    }
	
	/**
	 * Translates a molecule from the origin to a new point denoted by a vector.
	 *
	 * @param atomCon  molecule to be translated
	 */
	public static void translate2DCentreOfMassTo(AtomContainer atomCon, Point2d p) {
        Point2d com = get2DCentreOfMass(atomCon);
        Vector2d translation = new Vector2d(p.x-com.x, p.y-com.y);
        Atom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2d() != null) {
				atoms[i].getPoint2d().add(translation);
			}
		}
	}
	
	/**
	 * Centers the molecule in the given area
	 *
	 * @param atomCon  molecule to be centered
	 * @param areaDim  dimension in which the molecule is to be centered
	 */
	public static void center(AtomContainer atomCon, Dimension areaDim)
	{
		Dimension molDim = get2DDimension(atomCon);		
		int transX = (int)((areaDim.width - molDim.width) / 2);
		int transY = (int)((areaDim.height - molDim.height) / 2);
		translateAllPositive(atomCon);
		translate2D(atomCon, new Vector2d(transX, transY));
	}
	

	/**
	 * Calculates the center of the given atoms and returns it as a Point2d
	 *
	 * @param   atoms  The vector of the given atoms
	 * @return     The center of the given atoms as Point2d
	 */
	public static Point2d get2DCenter(Vector atoms)
	{
		Atom atom; 
		double x = 0, y = 0;
		for (int f = 0; f < atoms.size(); f++)
		{
			atom = (Atom)atoms.elementAt(f);
			if (atom.getPoint2d() != null)
			{
				x += atom.getX2d();
				y += atom.getY2d();						
			}
		}
		return new Point2d(x/(double)atoms.size(), y/(double)atoms.size());
	}

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the
     * AtomContainer for the 2D coordinates.
     *
     * @param ac        AtomContainer for which the center of mass is calculated
     *
     * @cdk.keyword center of mass
     */
    public static Point2d get2DCentreOfMass(AtomContainer ac) {
        double x = 0.0;
        double y = 0.0;

        double totalmass = 0.0;

        AtomEnumeration atoms = (AtomEnumeration)ac.atoms();
        while (atoms.hasMoreElements()) {
            Atom a = (Atom)atoms.nextElement();
            double mass = a.getExactMass();
            totalmass += mass;
            x += mass*a.getX2d();
            y += mass*a.getY2d();
        }

        return new Point2d(x/totalmass, y/totalmass);
    }

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the
     * AtomContainer for the 2D coordinates.
     *
     * @param ac        AtomContainer for which the center of mass is calculated
     *
     * @cdk.keyword center of mass
     */
    public static Point3d get3DCentreOfMass(AtomContainer ac) {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;

        double totalmass = 0.0;

        AtomEnumeration atoms = (AtomEnumeration)ac.atoms();
        while (atoms.hasMoreElements()) {
            Atom a = (Atom)atoms.nextElement();
            double mass = a.getExactMass();
            totalmass += mass;
            x += mass*a.getX3d();
            y += mass*a.getY3d();
            z += mass*a.getZ3d();
        }

        return new Point3d(x/totalmass, y/totalmass, z/totalmass);
    }

	public static double getAngle(double xDiff, double yDiff)
	{
		double angle = 0; 
//		System.out.println("getAngle->xDiff: " + xDiff);
//		System.out.println("getAngle->yDiff: " + yDiff);		
		if (xDiff >= 0 && yDiff >= 0)
		{
		    angle = Math.atan(yDiff / xDiff);
		}
		else if (xDiff < 0 && yDiff >= 0)
		{
			angle = Math.PI + Math.atan(yDiff / xDiff);
		}
		else if (xDiff < 0 && yDiff < 0)
		{
		    angle = Math.PI + Math.atan(yDiff / xDiff);
		}
		else if (xDiff >= 0 && yDiff < 0)
		{
		    angle = 2 * Math.PI + Math.atan(yDiff / xDiff);
		}
		return angle;
	}
	/**
	 * Gets the coordinates of two points (that represent a bond) and
	 * calculates for each the coordinates of two new points that have the given
	 * distance vertical to the bond.
	 *
	 * @param   coords  The coordinates of the two given points of the bond
	 *					like this [point1x, point1y, point2x, point2y]
	 * @param   dist  The vertical distance between the given points and those to be calculated
	 * @return     The coordinates of the calculated four points
	 */
	public static int[] distanceCalculator(int[] coords, double dist)
	{
		double angle;
		if ((coords[2] - coords[0]) == 0) angle = Math.PI/2;
		else
		{
			angle = Math.atan(((double)coords[3] - (double)coords[1]) / ((double)coords[2] - (double)coords[0]));
		}
		int begin1X = (int)(Math.cos(angle + Math.PI/2) * dist + coords[0]);
		int begin1Y = (int)(Math.sin(angle + Math.PI/2) * dist + coords[1]);
		int begin2X = (int)(Math.cos(angle - Math.PI/2) * dist + coords[0]);
		int begin2Y = (int)(Math.sin(angle - Math.PI/2) * dist + coords[1]);
		int end1X = (int)(Math.cos(angle - Math.PI/2) * dist + coords[2]);
		int end1Y = (int)(Math.sin(angle - Math.PI/2) * dist + coords[3]);
		int end2X = (int)(Math.cos(angle + Math.PI/2) * dist + coords[2]);
		int end2Y = (int)(Math.sin(angle + Math.PI/2) * dist + coords[3]);
		
		int[] newCoords = {begin1X,begin1Y,begin2X,begin2Y,end1X,end1Y,end2X,end2Y};
		return newCoords; 
	}


	/**
	 * Writes the coordinates of the atoms participating the given bond into an array.
	 *
	 * @param   bond   The given bond 
	 * @return     The array with the coordinates
	 */
	public static int[] getBondCoordinates(Bond bond) {
        if (bond.getAtomAt(0).getPoint2d() == null ||
            bond.getAtomAt(1).getPoint2d() == null) {
            logger.error("getBondCoordinates() called on Bond without 2D coordinates!");
            return new int[0];
        }
		int beginX = (int)bond.getAtomAt(0).getPoint2d().x;
		int endX = (int)bond.getAtomAt(1).getPoint2d().x;
		int beginY = (int)bond.getAtomAt(0).getPoint2d().y;
		int endY = (int)bond.getAtomAt(1).getPoint2d().y;
		int[] coords = {beginX,beginY,endX,endY};
		return coords;
	}
	

	/**
	 * Returns the atom of the given molecule that is closest to the given 
	 * coordinates.
	 *
	 * @param   xPosition  The x coordinate
	 * @param   yPosition  The y coordinate	
	 * @param   atomCon    The molecule that is searched for the closest atom
	 * @return             The atom that is closest to the given coordinates  
	 */
	public static Atom getClosestAtom(int xPosition, int yPosition, AtomContainer atomCon)
	{
		Atom closestAtom = null, currentAtom;
		double smallestMouseDistance = -1, mouseDistance, atomX, atomY;
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			currentAtom = atomCon.getAtomAt(i);
			atomX = currentAtom.getX2d();
			atomY = currentAtom.getY2d();
			mouseDistance = Math.sqrt(Math.pow(atomX - xPosition, 2) + Math.pow(atomY - yPosition, 2));
			if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1)
			{
				smallestMouseDistance = mouseDistance;
				closestAtom = currentAtom;
			}
		}
		return closestAtom;
	}
	

	/**
	 * Returns the bond of the given molecule that is closest to the given 
	 * coordinates.
	 *
	 * @param   xPosition  The x coordinate
	 * @param   yPosition  The y coordinate	
	 * @param   atomCon    The molecule that is searched for the closest bond
	 * @return             The bond that is closest to the given coordinates  
	 */
	public static Bond getClosestBond(int xPosition, int yPosition, AtomContainer atomCon)
	{
		Point2d bondCenter;
		Bond closestBond = null, currentBond;
		double smallestMouseDistance = -1, mouseDistance;
        Bond[] bonds = atomCon.getBonds();
		for (int i = 0; i < bonds.length; i++)
		{
			currentBond = bonds[i];
			bondCenter = get2DCenter(currentBond.getAtomsVector());
			mouseDistance = Math.sqrt(Math.pow(bondCenter.x - xPosition, 2) + Math.pow(bondCenter.y - yPosition, 2));
			if (mouseDistance < smallestMouseDistance || smallestMouseDistance == -1)
			{
				smallestMouseDistance = mouseDistance;
				closestBond = currentBond;
			}
		}
		return closestBond;
	}


	/**
	 * Sorts a Vector of atoms such that the 2D distances of the 
	 * atom locations from a given point are smallest for the first
	 * atoms in the vector 
	 *
	 * @param   point  The point from which the distances to the atoms are measured
	 * @param   atoms  The atoms for which the distances to point are measured
	 */
	public static void sortBy2DDistance(Atom[] atoms, Point2d point)
	{
		double distance1, distance2;
		Atom atom1 = null, atom2 = null;
		boolean doneSomething = false;
		do
		{
			doneSomething = false;
			for (int f = 0; f < atoms.length - 1; f++)
			{
				atom1 = atoms[f];
				atom2 = atoms[f + 1];
				distance1 = point.distance(atom1.getPoint2d());				
				distance2 = point.distance(atom2.getPoint2d());
				if (distance2 < distance1)
				{
					atoms[f] = atom2;
					atoms[f + 1] = atom1;					
					doneSomething = true;
				}								
			}
		}while(doneSomething);
	}
	
	/** Determines the scale factor for displaying a structure loaded from disk in a frame.
	  * An average of all bond length values is produced and a scale factor 
	  * is determined which would scale the given molecule such that its 
	  *
	  * @param   ac The AtomContainer for which the ScaleFactor is to be calculated
	  * @param   bondLength The target bond length
	  * @return  The ScaleFactor with which the AtomContainer must be scaled to have the target bond length
	 */

	public static double getScaleFactor(AtomContainer ac, double bondLength)
	{
		double currentAverageBondLength = getBondLengthAverage(ac);
		return bondLength/currentAverageBondLength;

	}
	
	/** 
	  * An average of all 2D bond length values is produced. Bonds which have
      * Atom's with no coordinates are disregarded.
	  *
	  * @param   ac The AtomContainer for which the average bond length is to be calculated
	  * @return  the average bond length 
	 */
    public static double getBondLengthAverage(AtomContainer ac) {
		double bondLengthSum = 0;
		Bond[] bonds = ac.getBonds();
        int bondCounter = 0;
		for (int f = 0; f < bonds.length; f++) {
			Bond bond = bonds[f];
            Atom atom1 = bond.getAtomAt(0);
            Atom atom2 = bond.getAtomAt(1);
            if (atom1.getPoint2d() != null &&
                atom2.getPoint2d() != null) {
                bondCounter++;
                bondLengthSum += getLength2D(bond);
            }
		}
		return bondLengthSum/bondCounter;
	}
	
	/**
	 * Returns the geometric length of this bond in 2D space.
	 *
	 * @return    The geometric length of this bond
	 */
	public static double getLength2D(Bond bond) {
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
	
	/** Determines if this AtomContainer contains 2D coordinates.
	 *
	 * @return  boolean indication that 2D coordinates are available 
     */
    public static boolean has2DCoordinates(AtomContainer m) {
        Atom[] atoms = m.getAtoms();
        for (int i=0; i < atoms.length; i++) {
            if (atoms[i].getPoint2d() == null) return false;
        }
        return true;
    }

	/** Determines if this Atom contains 2D coordinates.
	  *
	  * @return  boolean indication that 2D coordinates are available 
	 */
     public static boolean has2DCoordinates(Atom a) {
         return (a.getPoint2d() != null);
     }
     
	/** Determines if this Bond contains 2D coordinates.
	  *
	  * @return  boolean indication that 2D coordinates are available 
	 */
     public static boolean has2DCoordinates(Bond b) {
         Atom[] atoms = b.getAtoms();
         for (int i=0; i < atoms.length; i++) {
             if (atoms[i].getPoint2d() == null) return false;
         }
         return true;
     }

	/** Determines if this model contains 3D coordinates
	  *
	  * @return  boolean indication that 3D coordinates are available 
	 */
  public static boolean has3DCoordinates(AtomContainer m) {
      boolean hasinfo = true;
      Atom[] atoms = m.getAtoms();
      for (int i=0; i < atoms.length; i++) {
          if (atoms[i].getPoint3d() == null) return false;
      }
      return hasinfo;
  }

    /** Determines the normalized vector orthogonal on the vector p1->p2.
     *
     */
    public static Vector2d calculatePerpendicularUnitVector(Point2d p1, Point2d p2) {
        Vector2d v = new Vector2d();
        v.sub(p2, p1);
        v.normalize();

        // Return the perpendicular vector
        return new Vector2d(-1.0 * v.y, v.x);
    }

    /**
     * Calculates the normalization factor in order to get an average
     * bond length of 1.5. It takes only into account Bond's with two
     * atoms.
     */
    public static double getNormalizationFactor(AtomContainer container) {
        Bond[] bonds = container.getBonds();
        double bondlength = 0.0, ratio = 0.0;
        /* Desired bond length for storing structures in MDL mol files
           This should probably be set externally (from system wide settings) */
        double desiredBondLength = 1.5;
        // loop over all bonds and determine the mean bond distance
        int counter = 0;
        for (int f = 0; f < bonds.length; f++) {
            // only consider two atom bonds into account
            if (bonds[f].getAtomCount() == 2) {
                counter++;
                Atom atom1 = bonds[f].getAtomAt(0);
                Atom atom2 = bonds[f].getAtomAt(1);
                bondlength += Math.sqrt(Math.pow(atom1.getX2d() - atom2.getX2d(), 2) + 
                                        Math.pow(atom1.getY2d() - atom2.getY2d(), 2));
            }
        }
        bondlength = bondlength / counter;
        ratio = desiredBondLength / bondlength;
        return ratio;
    }
    
    /**
     * Determines the best alignment for the label of an atom in 2D space.
     * It returns 1 if left aligned, and -1 if right aligned.
     */
    public static int getBestAlignmentForLabel(AtomContainer container, Atom atom) {
        Atom[] connectedAtoms = container.getConnectedAtoms(atom);
        int overallDiffX = 0;
        for (int i=0; i<connectedAtoms.length; i++) {
            Atom connectedAtom = connectedAtoms[i];
            overallDiffX = overallDiffX + (int)(connectedAtom.getX2d() - atom.getX2d());
        }
        if (overallDiffX <= 0) {
            return 1;
        } else {
            return -1;
        }
    }
    
}



