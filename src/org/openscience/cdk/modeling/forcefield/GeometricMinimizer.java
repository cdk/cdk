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

	GVector initial3dCoordinates = new GVector(3);
	GVector kCoordinates = new GVector(3);
//	Vector kCoordinatesToShow = new Vector();
	GVector kplus1Coordinates = new GVector(3);
//	Vector kplus1CoordinatesToShow = new Vector();
	GVector gradient = new GVector(3);
	GVector minimumCoordinates = new GVector(3);
//	Vector minimumCoordinatesToShow = new Vector();
	int iterationNumber = 0;
	boolean convergence = false;
	double B1 = 0;
	double B2 = 0;
	double RMSD = 0;
	double RMS = 0;
	double d = 0;
	double difference = 0;
	int dimension = 1;


	/**
	 *  Constructor for the GeometricMinimizer object
	 */

	public GeometricMinimizer() { }


	/**
	 *  Gets the initial3dCoordinates attribute of the GeometricMinimizer object
	 *
	 *@return    The initial3dCoordinates value
	 */
	public GVector getInitial3dCoordinates() {
		return initial3dCoordinates;
	}


	/**
	 *  Gets the minimumCoordinates attribute of the GeometricMinimizer object
	 *
	 *@return    The minimumCoordinates value
	 */
	public GVector getMinimumCoordinates() {
		return minimumCoordinates;
	}


	/**
	 *  Description of the Method
	 */
	public void setInitial3dCoordinates() {

		AtomContainer testMolecule = new AtomContainer();

		//	Pending: Load actual AtomContainer
		//	Read Molecule
		// Creation of artificial molecule: AtomNumbers: 1 ("C"), coordinates (9,9,0).

		Point3d atomCoordinate = new Point3d(9, 9, 0);
		Atom exampleAtom = new Atom("C");
		exampleAtom.setPoint3d(atomCoordinate);
		testMolecule.addAtom(exampleAtom);

		System.out.println("");
		System.out.println("Test Molecule :  " + testMolecule);

		//	Read atoms coordinates

		initial3dCoordinates.setSize(3 * (testMolecule.getAtomCount()));
		ForceField forceFieldObject = new ForceField();
		initial3dCoordinates.set(forceFieldObject.readAtomsCoordinates(testMolecule));
		//	System.out.println("initial3dCoordinates: " + initial3dCoordinates);
		System.out.println("");
		System.out.println("Initial coordinates: " + forceFieldObject.point0ToShow);
		//	To check initialPoint size

		return;
	}


	/**
	 *  To check convergence
	 *
	 *@param  forceField  Potential energy surface
	 */
	public void checkConvergence() {

		RMS = 0;
		RMS = gradient.dot(gradient);
		System.out.println("RMS = gradient.dot(gradient) = " + RMS);
		RMS = RMS / dimension;
		System.out.println("RMS = gradient.dot(gradient) / gradient.getSize() = " + RMS);
		RMS = Math.sqrt(RMS);
		System.out.println("RMS = sqrt( gradient.dot(gradient) / gradient.getSize()) = " + RMS);

		RMSD = 0;
		for (int i = 0; i < dimension; i++) {
			System.out.println("i = " + i);
			d = 0;
			for (int j = 0; j < 3; j++) {
				difference = kplus1Coordinates.getElement(i + j) - kCoordinates.getElement(i + j);
				difference = Math.pow(difference, 2);
				d = d + difference;
			}
			System.out.println("d = " + d);
			d = Math.sqrt(d);
			System.out.println("d = " + d);
			i = i + 2;
			RMSD = RMSD + Math.pow(d, 2);
			System.out.println("RMSD = " + RMSD);
		}
		System.out.println("RMSD = " + RMSD);
		RMSD = RMSD / dimension;
		System.out.println("RMSD = " + RMSD);
		RMSD = Math.sqrt(RMSD);
		System.out.println("RMSD = " + RMSD);

		B1 = kplus1Coordinates.norm() / kCoordinates.norm();
		System.out.println("B1 = " + B1);

		B2 = kplus1Coordinates.norm() / (Math.pow(kCoordinates.norm(), 2));
		System.out.println("B2 = " + B2);

		if (RMS < 0.001) {
			convergence = true;
			System.out.println("RMS convergence");
		}

		if (RMSD < 0.001) {
			convergence = true;
			System.out.println("RMSD convergence");
		}

		if ((B1 < 0.01) & (B1 != 0)) {
			convergence = true;
			System.out.println("Linear convergence");
		}
		if (B1 == 0) {
			convergence = true;
			System.out.println("Superlinear convergence");
		}
		if (B2 < 0.01) {
			convergence = true;
			System.out.println("Quadratic convergence");
		}
		return;
	}


	//setConvergence (To change convergence parameters)

	/**
	 *  Description of the Method
	 *
	 *@param  SDMaximumIteration  Description of the Parameter
	 *@param  CGMaximumIteration  Description of the Parameter
	 *@param  NRMaximumIteration  Description of the Parameter
	 *@param  forceField          Description of the Parameter
	 */
	public void energyOptimization(int SDMaximumIteration, int CGMaximumIteration, int NRMaximumIteration, PotentialFunction forceField) {
		
		//	System.out.println("initial3dCoordinates : " + initial3dCoordinates);
		dimension = initial3dCoordinates.getSize();
		System.out.println("dimension : " + dimension);
		kCoordinates.setSize(initial3dCoordinates.getSize());
		kCoordinates.set(initial3dCoordinates);
		System.out.println("Coordinates at iteration 1: X1 = " + kCoordinates);
		gradient.setSize(initial3dCoordinates.getSize());
		gradient.set(forceField.gradientInPoint(kCoordinates));
		System.out.println("gradient: g1 = " + gradient);
		/*
		 *  kCoordinatesToShow.setSize(point0ToShow.size());
		 *  for (int i=0;i<kCoordinatesToShow.size();i++) {
		 *  kCoordinatesToShow.setElementAt(point0ToShow.elementAt(i),i);
		 *  }
		 *  System.out.println("kCoordinatesToShow : " + kCoordinatesToShow);
		 */
		kplus1Coordinates.setSize(kCoordinates.getSize());
		//	kplus1CoordinatesToShow.setSize(kplus1CoordinatesToShow.size());
		
		
		/*
		System.out.println("");
		System.out.println("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(kCoordinates);
		
		while ((iterationNumber < SDMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			System.out.println("");
			System.out.println("SD Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				sdm.setArbitraryStepSize(kCoordinates, kplus1Coordinates, forceField);
				kCoordinates.set(kplus1Coordinates);
			} else {
				System.out.println("Arbitrary step size : " + sdm.arbitraryStepSize);
			}
			
			System.out.println("Xk coordinates : " + kCoordinates);
			System.out.println("Gradient value in Xk coordinates, g(Xk) : " + forceField.gradientInPoint(kCoordinates));
			
			sdm.setSk(forceField.gradientInPoint(kCoordinates));
			// To be included here the linesearch approach
			kplus1Coordinates.set(sdm.newCoordinatesCalculation(kCoordinates, sdm.getArbitraryStepSize()));
			
			gradient.set(forceField.gradientInPoint(kplus1Coordinates));
			checkConvergence();
			System.out.println("convergence: " + convergence);
		}
		minimumCoordinates.set(kplus1Coordinates);
		System.out.println("The SD minimum energy is at: " + minimumCoordinates);
		*/
		
		
		convergence = false;
		iterationNumber = 0;
				
		System.out.println("");
		System.out.println("FORCEFIELDTESTS ConjugatedGradientTest");
		
		ConjugateGradientMethod cgm = new ConjugateGradientMethod(minimumCoordinates);
		
		while ((iterationNumber < CGMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			System.out.println("");
			System.out.println("CG Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				cgm.setArbitraryStepSize(kCoordinates, kplus1Coordinates, forceField);
				cgm.µkCalculation(kCoordinates, kplus1Coordinates, forceField);
				kCoordinates.set(kplus1Coordinates);
			} 
			else {
				System.out.println("Arbitrary step size : " + cgm.arbitraryStepSize);
			}
			
			System.out.println("Xk coordinates : " + kCoordinates);
			System.out.println("Gradient value in Xk coordinates, g(Xk) : " + gradient);
			cgm.setvk(gradient, iterationNumber);
			
			// To be included here the linesearch approach
			kplus1Coordinates.set(cgm.newCoordinatesCalculation(kCoordinates, cgm.getArbitraryStepSize()));
			
			if (iterationNumber == 1) {
				kplus1Coordinates.setElement(0,4);
				kplus1Coordinates.setElement(1,-1);
				kplus1Coordinates.setElement(2,0);
				System.out.println("Xk+1 coordinates : " + kplus1Coordinates);
			}
			
			
			gradient.set(forceField.gradientInPoint(kplus1Coordinates));
			checkConvergence();
			System.out.println("convergence: " + convergence);
		 }
		 
		 minimumCoordinates.set(kplus1Coordinates);
		 System.out.println("The CG minimum energy is at: " + minimumCoordinates);
		 
		 /*
		 *  convergence = false;
		 *  iterationNumber = 0;
		 *  System.out.println("");
		 *  System.out.println("FORCEFIELDTESTS NewtonRaphsonTest");
		 *  NewtonRaphsonMethod nrm = new NewtonRaphsonMethod(minimumCoordinates);
		 *  while ((iterationNumber < NRMaximumIteration) & (convergence == false)) {
		 *  iterationNumber += 1;
		 *  System.out.println("");
		 *  System.out.println("NR Iteration number: " + iterationNumber);
		 *  if iterationNumber =! 1 {
		 *  sdm.setArbitraryStepSize(kCoordinates, kplus1Coordinates,forceField);
		 *  }
		 *  kCoordinates.set(kplus1Coordinates);
		 *  checkConvergence();
		 *  }
		 *  minimumCoordinates.set(kplus1Coordinates);
		 *  System.out.println("The NR minimum energy is at: " + minimumCoordinates);
		 */
		return;
	}
}

