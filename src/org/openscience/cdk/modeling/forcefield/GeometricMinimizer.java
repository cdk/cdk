package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Call the minimization methods and check results
 *
 *@author     vlabarta
 *
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
	double B11 = 0;
	double B12 = 0;
	double B21 = 0;
	double B22 = 0;
	double B = 0;
	double RMSD = 0;
	double RMS = 0;
	double d = 0;
	double difference = 0;
	int dimension = 1;
	double temp = 0;


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
	 *  Set the initial coordinates of the molecule from the Atom container 
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
	 */
	public void checkConvergence() {

		RMS = 0;
		RMS = gradient.dot(gradient);
		//System.out.println("RMS = gradient.dot(gradient) = " + RMS);
		RMS = RMS / dimension;
		//System.out.println("RMS = gradient.dot(gradient) / gradient.getSize() = " + RMS);
		RMS = Math.sqrt(RMS);
		//System.out.println("RMS = sqrt( gradient.dot(gradient) / gradient.getSize()) = " + RMS);
		System.out.println("RMS = " + RMS);
		if (RMS < 0.001) {
			convergence = true;
			System.out.println("RMS convergence");
		}


		RMSD = 0;
		for (int i = 0; i < dimension; i++) {
			//System.out.println("i = " + i);
			d = 0;
			for (int j = 0; j < 3; j++) {
				difference = kplus1Coordinates.getElement(i + j) - kCoordinates.getElement(i + j);
				difference = Math.pow(difference, 2);
				d = d + difference;
			}
			//System.out.println("d = " + d);
			d = Math.sqrt(d);
			//System.out.println("d = " + d);
			i = i + 2;
			RMSD = RMSD + Math.pow(d, 2);
			//System.out.println("RMSD = " + RMSD);
		}
		//System.out.println("RMSD = " + RMSD);
		RMSD = RMSD / dimension;
		//System.out.println("RMSD = " + RMSD);
		RMSD = Math.sqrt(RMSD);
		System.out.println("RMSD = " + RMSD);

		if (RMSD < 0.001) {
			convergence = true;
			System.out.println("RMSD convergence");
		}
		
		if (iterationNumber == 1) {
			B11 = kplus1Coordinates.norm() / kCoordinates.norm();
		}
		
		else {
			if (iterationNumber == 2) {
				B12 = kplus1Coordinates.norm() / kCoordinates.norm();
			}
			else {
				B11 = B12;
				B12 = kplus1Coordinates.norm() / kCoordinates.norm();
			}
		}

		System.out.println("B11 = " + B11);
		System.out.println("B12 = " + B12);

		if (iterationNumber>1) {
			B = B12 - B11;
			B = Math.abs(B);
			System.out.println("B = " + B);
			if (B == 0) {
				convergence = true;
				System.out.println("Superlinear convergence");
			}
			else {
				if (B < 0.000001) {
					convergence = true;
					System.out.println("Linear convergence");
				}
			}
		}	

		if (iterationNumber == 1) {
			B21 = kplus1Coordinates.norm() / (Math.pow(kCoordinates.norm(), 2));
		}
		
		else {
			if (iterationNumber == 2) {
				B22 = kplus1Coordinates.norm() / (Math.pow(kCoordinates.norm(), 2));
			}
			else {
				B21 = B22;
				temp = kplus1Coordinates.norm();
				System.out.println("numerator = " + temp);
				temp = kCoordinates.norm();
				System.out.println("denominator = " + temp);
				temp = Math.pow(kCoordinates.norm(), 2);
				System.out.println("denominator = " + temp);
				B22 = kplus1Coordinates.norm() / (Math.pow(kCoordinates.norm(), 2));
			}
		}

		System.out.println("B21 = " + B21);
		System.out.println("B22 = " + B22);

		if (iterationNumber>1) {
			B = B22 - B21;
			B = Math.abs(B);
			System.out.println("B = " + B);
			if (B < 0.0000001) {
				convergence = true;
				System.out.println("Quadratic convergence");
			}
		}
		return;
	}


	public void setConvergence(){
		return;
	}

	/**
	 *  Optimize the potential energy function
	 *
	 * @param  SDMaximumIteration  Maximum number of iteration for steepest descents method 
	 * @param  CGMaximumIteration  Maximum number of iteration for conjugate gradient method
	 * @param  NRMaximumIteration  Maximum number of iteration for Newton-Raphson method
	 * @param  forceField		The potential function to be used
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
		minimumCoordinates.set(kCoordinates);
		//	kplus1CoordinatesToShow.setSize(kplus1CoordinatesToShow.size());
		
		System.out.println("");
		System.out.println("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(kCoordinates);
		LineSearch ls = new LineSearch(kCoordinates);
		
		while ((iterationNumber < SDMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			System.out.println("");
			System.out.println("SD Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				kCoordinates.set(kplus1Coordinates);
			} 			
			sdm.setSk(gradient);
			ls.bracketingTheMinimum(kCoordinates, sdm.sk, forceField);
			//kplus1Coordinates.set(sdm.setNewCoordinates(kCoordinates, sdm.getArbitraryStepSize()));
			kplus1Coordinates.set(ls.kplus1Point2);

			gradient.set(forceField.gradientInPoint(kplus1Coordinates));
			checkConvergence();
			System.out.println("convergence: " + convergence);
			System.out.println("f(xk) = " + forceField.functionInPoint(kCoordinates));
			System.out.println("f(xk+1) = " + forceField.functionInPoint(kplus1Coordinates));

		}
		minimumCoordinates.set(kplus1Coordinates);
		System.out.println("The SD minimum energy is at: " + minimumCoordinates);
		
		
		convergence = false;
		iterationNumber = 0;
				
		System.out.println("");
		System.out.println("FORCEFIELDTESTS ConjugatedGradientTest");
		
		ConjugateGradientMethod cgm = new ConjugateGradientMethod(minimumCoordinates);
		kCoordinates.set(minimumCoordinates);
		
		while ((iterationNumber < CGMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			System.out.println("");
			System.out.println("CG Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				cgm.setmicrok(kCoordinates, kplus1Coordinates, forceField);
				kCoordinates.set(kplus1Coordinates);
			}
			cgm.setvk(gradient, iterationNumber);
			ls.bracketingTheMinimum(kCoordinates, cgm.vk, forceField);
			kplus1Coordinates.set(ls.kplus1Point2);
			
/*			if (iterationNumber == 1) {
				kplus1Coordinates.setElement(0,4);
				kplus1Coordinates.setElement(1,-1);
				kplus1Coordinates.setElement(2,0);
				System.out.println("Xk+1 coordinates : " + kplus1Coordinates);
			}
*/			
			
			gradient.set(forceField.gradientInPoint(kplus1Coordinates));
			checkConvergence();
			System.out.println("convergence: " + convergence);
			System.out.println("f(xk) = " + forceField.functionInPoint(kCoordinates));
			System.out.println("f(xk+1) = " + forceField.functionInPoint(kplus1Coordinates));
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

