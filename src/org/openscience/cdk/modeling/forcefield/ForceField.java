package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Description of the Class
 *
 *@author     labarta
 *@created    December 9, 2004
 */
public class ForceField {
	Vector point0ToShow = new Vector();
	
	/**
	 *  Constructor for the ForceField object
	 */
	public ForceField() {
	}
	
	
	/**
	 *  Description of the Method
	 *
	 *@param  molecule  Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public GVector readAtomsCoordinates(AtomContainer molecule) {
		//	input: molecule like AtomContainer object,
		//	output: GVector with 3N coordinates (N: atom numbers)
		
	//	System.out.println("molecule to optimize: " + molecule.toString());
	//	System.out.println("Atoms number = " + molecule.getAtomCount());
		GVector point0 = new GVector(3 * (molecule.getAtomCount()));
	//	System.out.println("point0 = " + point0);
		Atom thisAtom = new Atom();
		
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			thisAtom = molecule.getAtomAt(i);
		//	System.out.println("i = " + i);
		//	System.out.println("thisAtom = " + thisAtom);
		//	System.out.println("point0ToShow = " + point0ToShow);
		//	System.out.println("thisAtom.getPoint3d() = " + thisAtom.getPoint3d());
			point0ToShow.add(i, thisAtom.getPoint3d());
			//	point0ToShow is a Vector with 3D atom coordinates
		//	System.out.println("point0ToShow = " + point0ToShow);
			
			int j = 3 * i;
			point0.setElement(j, thisAtom.getX3d());
			//	point0 is a GVector with 3N coordinates of the molecule
			point0.setElement(j + 1, thisAtom.getY3d());
			point0.setElement(j + 2, thisAtom.getZ3d());
		}
		
	//	System.out.println("Starting coordinates : " + point0ToShow);
	//	System.out.println("Starting coordinates : " + point0);
		
		return point0;
	}
}
