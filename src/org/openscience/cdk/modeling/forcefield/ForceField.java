package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  To work with the coordinates of the molecule, like get the 3d coordinates of the atoms or 
 *  calculate the distance between two atoms.
 *@author     vlabarta
 *
 */
public class ForceField {
	
	/**
	 *  Constructor for the ForceField object
	 */
	public ForceField() {}
	
	
	/**
	 *  Read the 3d coordinates of atoms in a molecule from its atom container
	 *
	 *@param  molecule  molecule store in an AtomContainer 
	 *@return           GVector with 3N coordinates (N: atom numbers)
	 */
	public GVector getCoordinatesVector(AtomContainer molecule) {
		
		System.out.println("molecule: " + molecule.toString());
		System.out.println("Atoms number = " + molecule.getAtomCount());
		GVector point0 = new GVector(3 * (molecule.getAtomCount()));
		System.out.println("point0 = " + point0);

		Atom thisAtom = new Atom();
		int j = 0;
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			thisAtom = molecule.getAtomAt(i);
			System.out.println("thisAtom = " + thisAtom);
			System.out.println("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());
			
			j = 3 * i;
			point0.setElement(j, thisAtom.getX3d());
			point0.setElement(j + 1, thisAtom.getY3d());
			point0.setElement(j + 2, thisAtom.getZ3d());
		}
		
		System.out.println("Atoms coordinates vector: " + point0);
		
		return point0;
	}
	
	/**
	 *  Read the coordinates vector of atoms in a molecule from its atom container
	 *
	 *@param  molecule  molecule store in an AtomContainer 
	 *@return           GVector with 3N coordinates (N: atom numbers)
	 */
	public Vector getPoint3dCoordinates(AtomContainer molecule) {
		
		System.out.println("molecule: " + molecule.toString());
		System.out.println("Atoms number = " + molecule.getAtomCount());
		Vector point3dCoordinates = new Vector();

		Atom thisAtom = new Atom();
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			thisAtom = molecule.getAtomAt(i);
			System.out.println("thisAtom = " + thisAtom);
			System.out.println("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());
			
			point3dCoordinates.add( new Point3d(thisAtom.getPoint3d()) );
			//Point3d ia = (Point3d)point3dCoordinates.get(i);
			//System.out.println(i + "a = " + ia);
		}
		
		System.out.println("Atoms 3d coordinates : " + point3dCoordinates);
		
		return point3dCoordinates;
	}
	
	public double get3dDistanceBetweenTwoAtoms(Vector atoms3dCoordinates, int atomNum1, int atomNum2) {
		
		Point3d atom13dCoord = (Point3d)atoms3dCoordinates.get(atomNum1);
		Point3d atom23dCoord = (Point3d)atoms3dCoordinates.get(atomNum2);
		
		double atomsDistance = 0;
		atomsDistance = atom13dCoord.distance(atom23dCoord);
		System.out.println("atomsDistance = " + atomsDistance);
		
		return atomsDistance;
	}
	public double get3dDistanceBetweenTwoAtomOfTwoDiffMolecules(GVector atomsCoordinatesVector1, GVector atomsCoordinatesVector2, int atomNumM1, int atomNumM2) {
		
		double atomsDistance = 0;
		double difference = 0;
		for (int j = 0; j < 3; j++) {
			difference = atomsCoordinatesVector2.getElement(atomNumM2*3+j) - atomsCoordinatesVector2.getElement(atomNumM1*3+j);
			difference = Math.pow(difference, 2);
			atomsDistance = atomsDistance + difference;
		}
		atomsDistance = Math.sqrt(atomsDistance);
		System.out.println("atomsDistance = " + atomsDistance);
		return atomsDistance;
	}
}


