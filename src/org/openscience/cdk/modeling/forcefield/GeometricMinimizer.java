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
public class GeometricMinimizer {
	
	GVector initialCoordinates = new GVector(3);
	
	/**
	 *  Constructor for the GeometricMinimizer object
	 */
	public GeometricMinimizer() { 
	}
	
	
	 /**
	 *  Description of the Method
	 */
	public void initialize() {
		
		AtomContainer testMolecule = new AtomContainer();
		
		//	Pending: Load actual AtomContainer
		//	Read Molecule
		// Creation of artificial molecule: AtomNumbers: 1 ("C"), coordinates (9,9,0).
		
		Point3d atomCoordinate = new Point3d(9, 9, 0);
		Atom exampleAtom = new Atom("C");
		exampleAtom.setPoint3d(atomCoordinate);
		testMolecule.addAtom(exampleAtom);
		
		System.out.println("Test Molecule :  " + testMolecule);
		
		//	Read atoms coordinates
		
		initialCoordinates.setSize(3*testMolecule.getAtomCount());
		ForceField forceFieldObject = new ForceField();
		initialCoordinates = forceFieldObject.readAtomsCoordinates(testMolecule);
		System.out.println("Initial coordinates: " + forceFieldObject.point0ToShow);	//	To check initialPoint size
		
		return;
		
	}
	
	
	/**
	 *  To check convergence
	 *
	 *@param  fxkplus1  Description of the Parameter
	 *@param  fxk       Description of the Parameter
	 *@return           Description of the Return Value
	 */
	public boolean checkConvergence(double fxkplus1, double fxk) {	// Waiting to be completed
		boolean stop = false;
		if ((fxkplus1)/(fxk)<0.5) {
		stop = true;
		}
		return stop;
	}
	
}
