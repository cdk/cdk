/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: steinbeck@ice.mpg.de, gezelter@maul.chem.nd.edu, egonw@sci.kun.nl
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

import javax.vecmath.*;
import org.openscience.cdk.*;
import java.awt.Dimension;
import java.util.Vector;

/**
 * A set of static utility classes for geometric calculations and operations.
 * This class is extensively used, for example, by JChemPaint to edit molecule.
 */
public class GeometryTools {

	/**
	 * Adds an automatically calculated offset to the coordinates of all atoms
	 * such that all coordinates are positive and the smallest x or y coordinate 
	 * is exactly zero.
	 *
	 * @param   molecule for which all the atoms are translated to positive coordinates
	 */
	public static void translateAllPositive(AtomContainer atomCon)
	{
		double transX = Double.MAX_VALUE, transY = Double.MAX_VALUE;
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			if (atomCon.getAtomAt(i).getPoint2D() != null)
			{
				if (atomCon.getAtomAt(i).getPoint2D().x < transX)
				{
					transX = atomCon.getAtomAt(i).getPoint2D().x;
				}
				if (atomCon.getAtomAt(i).getPoint2D().y < transY)
				{
					transY = atomCon.getAtomAt(i).getPoint2D().y;
				}
			}
		}
		translate2D(atomCon,transX * -1,transY * -1);		
	}
	

	/**
	 * Translates the given molecule by the given Vector.
	 *
	 * @param   molecule  The molecule to be translated
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
	 * @param   molecule  The molecule to be scaled
	 */
	public static void scaleMolecule(AtomContainer atomCon, double scaleFactor)
	{
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			if (atomCon.getAtomAt(i).getPoint2D() != null)
			{
				atomCon.getAtomAt(i).getPoint2D().x *= scaleFactor;
				atomCon.getAtomAt(i).getPoint2D().y *= scaleFactor;
			}
		}
	}
	

	/**
	 * Rotates a molecule around a given center by a given angle
	 *
	 * @param   molecule  The molecule to be rotated
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
			p = atom.getPoint2D();
			offsetAngle = GeometryTools.getAngle(p.x - center.x, p.y - center.y);
			distance = p.distance(center);
			p.x = center.x + (Math.sin(angle + offsetAngle) * distance);
			p.y = center.y - (Math.cos(angle + offsetAngle) * distance);				
		}
	}


    /**
	 * Scales a molecule such that it fills a given percentage of a given dimension
	 *
	 * @param   molecule  The molecule to be scaled
	 * @param   dim       The dimension to be filled
	 * @param   percentage  The percentage of the dimension to be filled
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
	 * @param   molecule of which the dimension should be returned
	 * @return The java.awt.Dimension of this molecule
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
     * atoms in the AtomContainer as:
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
			if (atom.getPoint2D() != null) {
				if (atom.getX2D() > maxX) maxX = atom.getX2D();
				if (atom.getX2D() < minX) minX = atom.getX2D();
				if (atom.getY2D() > maxY) maxY = atom.getY2D();
				if (atom.getY2D() < minY) minY = atom.getY2D();
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
	public static void translate2D(AtomContainer atomCon, Vector2d vector)
	{
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			if (atomCon.getAtomAt(i).getPoint2D() != null)
			{
				atomCon.getAtomAt(i).getPoint2D().add(vector);
			}
		}
	}
	
	/**
	 * Translates a molecule from the origin to a new point denoted by a vector.
	 *
	 * @param atomCon  molecule to be translated
	 * @param vector   dimension that represents the translation vector
	 */
	public static void translate2DCentreOfMassTo(AtomContainer atomCon, Point2d p) {
        Point2d com = get2DCentreOfMass(atomCon);
        Vector2d translation = new Vector2d(p.x-com.x, p.y-com.y);
        Atom[] atoms = atomCon.getAtoms();
		for (int i = 0; i < atoms.length; i++) {
			if (atoms[i].getPoint2D() != null) {
				atoms[i].getPoint2D().add(translation);
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
			if (atom.getPoint2D() != null)
			{
				x += atom.getX2D();
				y += atom.getY2D();						
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
     * @keyword center of mass
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
            x += mass*a.getX2D();
            y += mass*a.getY2D();
        }

        return new Point2d(x/totalmass, y/totalmass);
    }

    /**
     * Calculates the center of mass for the <code>Atom</code>s in the
     * AtomContainer for the 2D coordinates.
     *
     * @param ac        AtomContainer for which the center of mass is calculated
     *
     * @keyword center of mass
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
            x += mass*a.getX3D();
            y += mass*a.getY3D();
            z += mass*a.getZ3D();
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
		int beginX = (int)bond.getAtomAt(0).getPoint2D().x;
		int endX = (int)bond.getAtomAt(1).getPoint2D().x;
		int beginY = (int)bond.getAtomAt(0).getPoint2D().y;
		int endY = (int)bond.getAtomAt(1).getPoint2D().y;
		int[] coords = {beginX,beginY,endX,endY};
		return coords;
	}
	

	/**
	 * Returns the atom of the given molecule that is closest to the given 
	 * coordinates.
	 *
	 * @param   xPosition  The x coordinate
	 * @param   yPosition  The y coordinate	
	 * @param   molecule  The molecule that is searched for the closest atom
	 * @return   The atom that is closest to the given coordinates  
	 */
	public static Atom getClosestAtom(int xPosition, int yPosition, AtomContainer atomCon)
	{
		Atom closestAtom = null, currentAtom;
		double smallestMouseDistance = -1, mouseDistance, atomX, atomY;
		for (int i = 0; i < atomCon.getAtomCount(); i++)
		{
			currentAtom = atomCon.getAtomAt(i);
			atomX = currentAtom.getX2D();
			atomY = currentAtom.getY2D();
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
	 * @param   molecule  The molecule that is searched for the closest bond
	 * @return   The bond that is closest to the given coordinates  
	 */
	public static Bond getClosestBond(int xPosition, int yPosition, AtomContainer atomCon)
	{
		Point2d bondCenter;
		Bond closestBond = null, currentBond;
		double smallestMouseDistance = -1, mouseDistance, bondCenterX, bondCenterY;
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
				distance1 = point.distance(atom1.getPoint2D());				
				distance2 = point.distance(atom2.getPoint2D());
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
	  * An average of all bond length values is produced and the structure is scaled
	  * such that the resulting bond length divided by the
	  * character size equals the current Chemistry Development Kit (CKD)Models bondlengthToCharactersizeRatio
	  * setting.
	  *
	  * @param   ac The AtomContainer for which the ScaleFactor is to be calculated
	  * @return  The ScaleFactor with which the AtomContainer must be scaled 
	 */

	public static double getScaleFactor(AtomContainer ac, double bondLength)
	{
		double bondLengthSum = 0;
		Bond bond = null; 
		Atom a1 = null, a2 = null; 
        Bond[] bonds = ac.getBonds();
		for (int f = 0; f < bonds.length; f++) {
			bond = bonds[f];
			bondLengthSum += 1.0;
		}
		return bondLength/(bondLengthSum/ac.getBondCount());

	}
	
	/** Determines if this AtomContainer contains 2D coordinates.
	  *
	  * @return  boolean indication that 2D coordinates are available 
	 */
     public static boolean has2DCoordinates(AtomContainer m) {
         Atom[] atoms = m.getAtoms();
         for (int i=0; i < atoms.length; i++) {
             if (atoms[i].getPoint2D() == null) return false;
         }
         return true;
     }

	/** Determines if this Atom contains 2D coordinates.
	  *
	  * @return  boolean indication that 2D coordinates are available 
	 */
     public static boolean has2DCoordinates(Atom a) {
         return (a.getPoint2D() != null);
     }
     
	/** Determines if this Bond contains 2D coordinates.
	  *
	  * @return  boolean indication that 2D coordinates are available 
	 */
     public static boolean has2DCoordinates(Bond b) {
         Atom[] atoms = b.getAtoms();
         for (int i=0; i < atoms.length; i++) {
             if (atoms[i].getPoint2D() == null) return false;
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
          if (atoms[i].getPoint3D() == null) hasinfo = false;
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

}



