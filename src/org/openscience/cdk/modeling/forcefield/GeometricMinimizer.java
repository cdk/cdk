package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  Call the minimization methods. Check the convergence.
 *
 *@author     vlabarta
 *
 */
public class GeometricMinimizer {

	GVector kCoordinates = new GVector(3);
	GVector kplus1Coordinates = new GVector(3);
	GVector gradient = new GVector(3);
	GVector minimumCoordinates = new GVector(3);
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
	 *  Gets the minimumCoordinates attribute of the GeometricMinimizer object
	 *
	 *@return    The minimumCoordinates value
	 */
	public GVector getMinimumCoordinates() {
		return minimumCoordinates;
	}


	/**
	 *  To check convergence
	 */
	public void checkConvergence() {

		RMS = 0;
		RMS = gradient.dot(gradient);
		RMS = RMS / dimension;
		RMS = Math.sqrt(RMS);
		////ln("");		
		//System.out.print("RMS = " + RMS);
		if (RMS < 0.001) {
			convergence = true;
			//System.out.println("RMS convergence");
		}

		RMSD = 0;
		for (int i = 0; i < dimension; i++) {
			d = 0;
			for (int j = 0; j < 3; j++) {
				difference = kplus1Coordinates.getElement(i + j) - kCoordinates.getElement(i + j);
				difference = Math.pow(difference, 2);
				d = d + difference;
			}
			d = Math.sqrt(d);
			i = i + 2;
			RMSD = RMSD + Math.pow(d, 2);
		}
		RMSD = RMSD / dimension;
		RMSD = Math.sqrt(RMSD);
		//System.out.print("RMSD = " + RMSD);

		if (RMSD < 0.001) {
			convergence = true;
			//System.out.println("RMSD convergence");
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

		//System.out.println("B11 = " + B11);
		//System.out.println("B12 = " + B12);

		if (iterationNumber>1) {
			B = B12 - B11;
			B = Math.abs(B);
			//System.out.print("B = " + B);
			if (B == 0) {
				convergence = true;
				//System.out.println("Superlinear convergence");
			}
			else {
				if (B < 0.000001) {
					convergence = true;
					//System.out.println("Linear convergence");
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
				temp = kCoordinates.norm();
				temp = Math.pow(kCoordinates.norm(), 2);
				B22 = kplus1Coordinates.norm() / (Math.pow(kCoordinates.norm(), 2));
			}
		}

		//System.out.println("B21 = " + B21);
		//System.out.println("B22 = " + B22);

		if (iterationNumber>1) {
			B = B22 - B21;
			B = Math.abs(B);
			//System.out.print("B = " + B);
			if (B < 0.0000001) {
				convergence = true;
				//System.out.println("Quadratic convergence");
			}
		}
		return;
	}


	public void setConvergenceParameters(){
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
	public void energyMinimization(GVector initialCoordinates, int SDMaximumIteration, int CGMaximumIteration, int NRMaximumIteration, PotentialFunction forceField) {
		
		dimension = initialCoordinates.getSize();
		kCoordinates.setSize(initialCoordinates.getSize());
		kCoordinates.set(initialCoordinates);
		//System.out.println("Coordinates at iteration 1: X1 = " + kCoordinates);
		gradient.setSize(initialCoordinates.getSize());
		gradient.set(forceField.gradientInPoint(kCoordinates));
		//System.out.println("gradient at iteration 1 : g1 = " + gradient);
		kplus1Coordinates.setSize(kCoordinates.getSize());
		minimumCoordinates.setSize(kCoordinates.getSize());
		minimumCoordinates.set(kCoordinates);
		int iterationNumberBefore = -1;
		LineSearch ls = new LineSearch();
		
		System.out.println("");
		System.out.println("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(kCoordinates);
				
		while ((iterationNumber < SDMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			iterationNumberBefore += 1;
			System.out.println("");
			System.out.println("");
			System.out.println("SD Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				kCoordinates.set(kplus1Coordinates);
			} 			
			//System.out.println("Search direction: ");
			sdm.setSk(gradient, iterationNumber);
			//System.out.println("");
			//System.out.println("Start the line search: ");
			ls.bracketingTheMinimum(kCoordinates, sdm.sk, forceField, iterationNumberBefore);
			kplus1Coordinates.set(sdm.sk);
			kplus1Coordinates.scale(ls.arbitraryStepSize);
			kplus1Coordinates.add(kCoordinates);
			System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
			//System.out.println("Parabolic interpolation: ");
			ls.parabolicInterpolation();
			kplus1Coordinates.set(sdm.sk);
			kplus1Coordinates.scale(ls.parabolMinimumLambda);
			kplus1Coordinates.add(kCoordinates);
			System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
			//kplus1Coordinates.set(sdm.setNewCoordinates(kCoordinates, sdm.getArbitraryStepSize()));

			gradient.set(forceField.gradientInPoint(kplus1Coordinates));
			checkConvergence();
			System.out.println("convergence: " + convergence);
			System.out.println("");
			System.out.println("f(x" + iterationNumberBefore + ") = " + forceField.functionInPoint(kCoordinates));
			System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));

		}
		minimumCoordinates.set(kplus1Coordinates);
		System.out.println("The SD minimum energy is at: " + minimumCoordinates);
		
		convergence = false;
		iterationNumber = 0;
		iterationNumberBefore = -1;
				
		System.out.println("");
		System.out.println("FORCEFIELDTESTS ConjugatedGradientTest");
		
		ConjugateGradientMethod cgm = new ConjugateGradientMethod(minimumCoordinates);
		kCoordinates.set(minimumCoordinates);
		
		while ((iterationNumber < CGMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			iterationNumberBefore += 1;
			System.out.println("");
			System.out.println("");
			System.out.println("CG Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				cgm.setµk(kCoordinates, kplus1Coordinates, forceField);
				kCoordinates.set(kplus1Coordinates);
			}
			//System.out.println("Search direction: ");
			cgm.setvk(gradient, iterationNumber);
			//System.out.println("");
			//System.out.println("Start the line search: ");
			ls.bracketingTheMinimum(kCoordinates, cgm.vk, forceField, iterationNumberBefore);
			kplus1Coordinates.set(cgm.vk);
			kplus1Coordinates.scale(ls.arbitraryStepSize);
			kplus1Coordinates.add(kCoordinates);
			System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
			System.out.println("Parabolic interpolation: ");
			ls.parabolicInterpolation();
			kplus1Coordinates.set(cgm.vk);
			kplus1Coordinates.scale(ls.parabolMinimumLambda);
			kplus1Coordinates.add(kCoordinates);
			System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
			
			gradient.set(forceField.gradientInPoint(kplus1Coordinates));
			checkConvergence();
			System.out.println("convergence: " + convergence);
			System.out.println("");
			System.out.println("f(x" + iterationNumberBefore + ") = " + forceField.functionInPoint(kCoordinates));
			System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
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

