package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.builder3d.*;

/**
 *  Call the minimization methods. Check the convergence.
 *
 *@author     vlabarta
 *
 */
public class GeometricMinimizer {

	Hashtable mmff94ParameterSet = null;
	int SDMaximumIteration = 0;
	int CGMaximumIteration = 0;
	int NRMaximumIteration = 0;

	double CGconvergenceCriterion = 0.001;
	double SDconvergenceCriterion = 0.001;
	double NRconvergenceCriterion = 0.001;

	GVector kCoordinates = new GVector(3);
	GVector kplus1Coordinates = new GVector(3);
	int AtomsNumber =1;
	int dimension = 1;

	GVector gradient = new GVector(3);

	GVector steepestDescentsMinimum = new GVector(3);
	GVector conjugateGradientMinimum = new GVector(3);
	GVector newtonRaphsonMinimum = new GVector(3);

	int iterationNumber = 0;
	int iterationNumberBefore = -1;
	boolean convergence = false;
	double RMSD = 0;
	double RMS = 0;
	double d = 0;
	ForceField forceFieldObject = new ForceField();

/*
	double B11 = 0;
	double B12 = 0;
	double B21 = 0;
	double B22 = 0;
	double B = 0;
	double difference = 0;
	double temp = 0;
*/

	/**
	 *  Constructor for the GeometricMinimizer object
	 */
	public GeometricMinimizer() { }


	/**
	 *  Assign MMFF94 atom types to the molecule.
	 *  
	 *@param  molecule  The molecule like an AtomContainer object.
	 */
	public void setMMFF94Tables(AtomContainer molecule) throws Exception {
		
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mmff94");
		RingSet rs = ffc.assignAtomTyps((Molecule) molecule);
		mmff94ParameterSet = ffc.getParameterSet();
	}
	
	public Hashtable getMMFF94Tables() {
		return mmff94ParameterSet;
	}


	public void initializeMinimizationParameters(GVector initialCoord) {
		
		dimension = initialCoord.getSize();
		AtomsNumber = dimension/3;
		kCoordinates.setSize(dimension);
		kCoordinates.set(initialCoord);
		//System.out.println("Coordinates at iteration 1: X1 = " + kCoordinates);
		gradient.setSize(dimension);
		kplus1Coordinates.setSize(kCoordinates.getSize());
		
		convergence = false;
		iterationNumber = 0;
		iterationNumberBefore = -1;
				
		return;
	}


	/**
	 *  To check convergence
	 */
	public void checkConvergence(double convergenceCriterion) {
		
		RMS = 0;
		RMS = gradient.dot(gradient);
		RMS = RMS / dimension;
		RMS = Math.sqrt(RMS);
		//System.out.print("RMS = " + RMS);

		if (RMS < convergenceCriterion) {
			convergence = true;
			//System.out.println("RMS convergence");
		}

		RMSD = 0;
		for (int i = 0; i < AtomsNumber; i++) {
			d = forceFieldObject.calculate3dDistanceBetweenTwoAtomFromTwo3xNCoordinates(kplus1Coordinates, kCoordinates, i, i);
			RMSD = RMSD + Math.pow(d, 2);
		}
		RMSD = RMSD / dimension;
		RMSD = Math.sqrt(RMSD);
		//System.out.print("RMSD = " + RMSD);

		if (RMSD < convergenceCriterion) {
			convergence = true;
			//System.out.println("RMSD convergence");
		}

		
/*
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
				if (B < convergenceCriterion) {
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
			if (B < convergenceCriterion) {
				convergence = true;
				//System.out.println("Quadratic convergence");
			}
		}
*/
		return;
	}


	public void setkplus1Coordinates(GVector direction, double stepSize) {
		kplus1Coordinates.set(direction);
		kplus1Coordinates.scale(stepSize);
		kplus1Coordinates.add(kCoordinates);
		return;
	}


	/**
	 *  Set convergence parameters for Steepest Decents Method
	 *
	 * @param  changeSDMaximumIteration  Maximum number of iteration for steepest descents method. 
	 * @param  changeSDConvergenceCriterion  Convergence criterion for steepest descents method.
	 */
	public void setConvergenceParametersForSDM(int changeSDMaximumIteration, double changeSDConvergenceCriterion){
		SDMaximumIteration = changeSDMaximumIteration;
		SDconvergenceCriterion = changeSDConvergenceCriterion;
		return;
	}


	/**
	 *  Minimize the potential energy function using steepest descents method
	 *
	 * @param  forceField		The potential function to be used
	 */
	public void steepestDescentsMinimization(GVector initialCoordinates, PotentialFunction forceField) {
		
		initializeMinimizationParameters(initialCoordinates);
		gradient.set(forceField.gradientInPoint(kCoordinates));
		//System.out.println("gradient at iteration 1 : g1 = " + gradient);

		steepestDescentsMinimum.setSize(kCoordinates.getSize());
		steepestDescentsMinimum.set(kCoordinates);

		LineSearch ls = new LineSearch();

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(kCoordinates);
				
		while ((iterationNumber < SDMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			iterationNumberBefore += 1;
			//System.out.println("");
			//System.out.println("SD Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				kCoordinates.set(kplus1Coordinates);
			} 			
			//System.out.println("Search direction: ");
			sdm.setSk(gradient, iterationNumber);

			//System.out.println("");
			//System.out.println("Start the line search: ");
			ls.bracketingTheMinimum(kCoordinates, sdm.sk, forceField, iterationNumberBefore);
			setkplus1Coordinates(sdm.sk, ls.arbitraryStepSize);			
			//System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));

			//System.out.println("Parabolic interpolation: ");
			ls.parabolicInterpolation();
			setkplus1Coordinates(sdm.sk, ls.parabolMinimumLambda);			
			//System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));

			gradient.set(forceField.gradientInPoint(kplus1Coordinates));

			checkConvergence(SDconvergenceCriterion);
			//System.out.println("convergence: " + convergence);

			//System.out.println("");
			//System.out.println("f(x" + iterationNumberBefore + ") = " + forceField.functionInPoint(kCoordinates));
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));

		}
		steepestDescentsMinimum.set(kplus1Coordinates);
		//   System.out.println("The SD minimum energy is at: " + steepestDescentsMinimum);
		
		return;
	}


	/**
	 *  Gets the steepestDescentsMinimum attribute of the GeometricMinimizer object
	 *
	 *@return    The minimumCoordinates value
	 */
	public GVector getSteepestDescentsMinimum() {
		return steepestDescentsMinimum;
	}
	
	
	/**
	 *  Set convergence parameters for Conjugate Gradient Method
	 *
	 * @param  changeCGMaximumIteration  Maximum number of iteration for conjugated gradient method
	 * @param  changeCGConvergenceCriterion  Convergence criterion for conjugated gradient method
	 */
	public void setConvergenceParametersForCGM(int changeCGMaximumIteration, double changeCGConvergenceCriterion){
		CGMaximumIteration = changeCGMaximumIteration;
		CGconvergenceCriterion = changeCGConvergenceCriterion;
		return;
	}


	/**
	 *  Minimize the potential energy function using conjugate gradient method
	 *
	 * @param  forceField		The potential function to be used
	 */
	public void conjugateGradientMinimization(GVector initialCoordinates,  PotentialFunction forceField) {
		
		initializeMinimizationParameters(initialCoordinates);
		gradient.set(forceField.gradientInPoint(kCoordinates));
		//System.out.println("gradient at iteration 1 : g1 = " + gradient);

		conjugateGradientMinimum.setSize(kCoordinates.getSize());
		conjugateGradientMinimum.set(kCoordinates);
		LineSearch ls = new LineSearch();
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS ConjugatedGradientTest");
		
		ConjugateGradientMethod cgm = new ConjugateGradientMethod(kCoordinates);
		
		while ((iterationNumber < CGMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			iterationNumberBefore += 1;
			//System.out.println("");
			//System.out.println("");
			//System.out.println("CG Iteration number: " + iterationNumber);
			
			if (iterationNumber != 1) {
				cgm.setuk(kCoordinates, kplus1Coordinates, forceField);
				kCoordinates.set(kplus1Coordinates);
			}
			//System.out.println("Search direction: ");
			cgm.setvk(gradient, iterationNumber);

			//System.out.println("");
			//System.out.println("Start the line search: ");
			ls.bracketingTheMinimum(kCoordinates, cgm.vk, forceField, iterationNumberBefore);
			setkplus1Coordinates(cgm.vk, ls.arbitraryStepSize);
			//System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));

			//System.out.println("Parabolic interpolation: ");
			ls.parabolicInterpolation();
			setkplus1Coordinates(cgm.vk, ls.parabolMinimumLambda);
			//System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
			
			gradient.set(forceField.gradientInPoint(kplus1Coordinates));

			checkConvergence(CGconvergenceCriterion);
			//System.out.println("convergence: " + convergence);

			//System.out.println("");
			//System.out.println("f(x" + iterationNumberBefore + ") = " + forceField.functionInPoint(kCoordinates));
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
		 }
		 
		 conjugateGradientMinimum.set(kplus1Coordinates);
		 //System.out.println("The CG minimum energy is at: " + conjugateGradientMinimum);
		 
		return;
	}


	/**
	 *  Gets the conjugatedGradientMinimum attribute of the GeometricMinimizer object
	 *
	 *@return    The minimumCoordinates value
	 */
	public GVector getConjugateGradientMinimum() {
		return conjugateGradientMinimum;
	}


	/**
	 *  Set convergence parameters for Newton-Raphson Method
	 *
	 * @param  changeNRMaximumIteration  Maximum number of iteration for Newton-Raphson method
	 * @param  changeNRConvergenceCriterion  Convergence criterion for Newton-Raphson method
	 */
	public void setConvergenceParametersForNRM(int changeNRMaximumIteration, double changeNRConvergenceCriterion){
		NRMaximumIteration = changeNRMaximumIteration;
		NRconvergenceCriterion = changeNRConvergenceCriterion;
		return;
	}


	/**
	 *  Minimize the potential energy function using the Newton-Raphson method
	 *
	 * @param  forceField		The potential function to be used
	 */
	public void newtonRaphsonMinimization(GVector initialCoordinates, PotentialFunction forceField) {
		
		initializeMinimizationParameters(initialCoordinates);
		gradient.set(forceField.gradientInPoint(kCoordinates));
		//System.out.println("gradient at iteration 1 : g1 = " + gradient);

		newtonRaphsonMinimum.setSize(kCoordinates.getSize());
		newtonRaphsonMinimum.set(kCoordinates);

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS NewtonRaphsonTest");
		
		NewtonRaphsonMethod nrm = new NewtonRaphsonMethod(kCoordinates);
		GMatrix hessian = new GMatrix(dimension,dimension);
		
		while ((iterationNumber < NRMaximumIteration) & (convergence == false)) {
			
			iterationNumber += 1;
			iterationNumberBefore += 1;
			//System.out.println("");
			//System.out.println("NR Iteration number: " + iterationNumber);

			if (iterationNumber != 1) {
				kCoordinates.set(kplus1Coordinates);
			}
 			hessian.set(forceField.hessianInPoint(kCoordinates));
			//System.out.println("hessian = " + hessian);
			nrm.gradientPerInverseHessian(gradient,hessian);
			//System.out.println("GradientPerInverseHessian = " + nrm.getGradientPerInverseHessian());
			
			setkplus1Coordinates(nrm.getGradientPerInverseHessian(), -1);
			//System.out.print("x" + iterationNumber + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));


			gradient.set(forceField.gradientInPoint(kplus1Coordinates));

			checkConvergence(NRconvergenceCriterion);
			//System.out.println("convergence: " + convergence);

			//System.out.println("");
			//System.out.println("f(x" + iterationNumberBefore + ") = " + forceField.functionInPoint(kCoordinates));
			//System.out.println("f(x" + iterationNumber + ") = " + forceField.functionInPoint(kplus1Coordinates));
		}
		   newtonRaphsonMinimum.set(kplus1Coordinates);
		   //System.out.println("The NR minimum energy is at: " + newtonRaphsonMinimum);
		 
		return;
	}


	/**
	 *  Gets the newtonRaphsonMinimum attribute of the GeometricMinimizer object
	 *
	 *@return    The newtonRaphsonMinimum value
	 */
	public GVector getNewtonRaphsonMinimum() {
		return newtonRaphsonMinimum;
	}


}

