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
 *@cdk.module     builder3d
 *
 */
public class GeometricMinimizer {

	Hashtable PotentialParameterSet = null;
	int SDMaximumIteration = 1000;
	int CGMaximumIteration = 500;
	int NRMaximumIteration = 100;

	double CGconvergenceCriterion = 0.001;
	double SDconvergenceCriterion = 0.001;
	double NRconvergenceCriterion = 0.001;

        GVector kCoordinates = null;
	GVector kplus1Coordinates = null;
	int atomNumbers = 1;

	GVector gradientk = null;
	GVector gradientkplus1 = null;

	GVector steepestDescentsMinimum = null;
	GVector conjugateGradientMinimum = null;
	GVector newtonRaphsonMinimum = null;

	int iterationNumberkplus1 = 0;
	int iterationNumberk = -1;
	boolean convergence = false;
	double RMSD = 0;
	double RMS = 0;
	double d = 0;
	
	ForceFieldTools ffTools = new ForceFieldTools();

	Molecule molecule;


	/**
	 *  Constructor for the GeometricMinimizer object
	 */
	public GeometricMinimizer() { }


	public void setMolecule(Molecule mol, boolean clone) {

		if (clone) {
			this.molecule = (Molecule) mol.clone();
		} else {
			this.molecule = mol;
		}
	}

    	public Molecule getMolecule() {
		return this.molecule;
	}


	/**
	 *  Assign MMFF94 atom types to the molecule.
	 *  
	 *@param  molecule  The molecule like an AtomContainer object.
	 */
	public void setMMFF94Tables(AtomContainer molecule) throws Exception {
		
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		ffc.setForceFieldConfigurator("mmff94");
		RingSet rs = ffc.assignAtomTyps((Molecule) molecule);
		PotentialParameterSet = ffc.getParameterSet();
	}
	
	public Hashtable getPotentialParameterSet() {
		return PotentialParameterSet;
	}

	public double[] getConvergenceParametersForSDM(){
		double[] parameters={SDMaximumIteration,SDconvergenceCriterion};
		return parameters;
	}
	
	public double[] getConvergenceParametersForCGM(){
		double[] parameters={CGMaximumIteration,CGconvergenceCriterion};
		return parameters;
	}
		
	public double[] getConvergenceParametersForNRM(){
		double[] parameters={NRMaximumIteration,NRconvergenceCriterion};
		return parameters;
	}
	
	public void initializeMinimizationParameters(GVector initialCoord) {
		
		//kCoordinates.setSize(dimension);
		//kCoordinates.set(initialCoord);
		kCoordinates=initialCoord;
		//System.out.println("Coordinates at iteration 1: X1 = " + kCoordinates);
		gradientk = new GVector(kCoordinates.getSize());
		kplus1Coordinates = new GVector(kCoordinates.getSize());
		gradientkplus1 = new GVector(kCoordinates.getSize());
		
		convergence = false;
		iterationNumberkplus1 = 0;
		iterationNumberk = -1;
		
		atomNumbers = initialCoord.getSize()/3;


	}


	/**
	 *  To check convergence
	 */
	public void checkConvergence(double convergenceCriterion) {
		
		RMS = 0;
		RMS = gradientkplus1.dot(gradientkplus1);
		RMS = RMS / kCoordinates.getSize();
		RMS = Math.sqrt(RMS);
		//System.out.print("RMS = " + RMS);

		if (RMS < convergenceCriterion) {
			convergence = true;
			//System.out.println("RMS convergence");
		}

		RMSD = 0;
		for (int i = 0; i < atomNumbers; i++) {
			d = ffTools.distanceBetweenTwoAtomFromTwo3xNCoordinates(kplus1Coordinates, kCoordinates, i, i);
			RMSD = RMSD + Math.pow(d, 2);
		}
		RMSD = RMSD / kCoordinates.getSize();
		RMSD = Math.sqrt(RMSD);
		//System.out.print("RMSD = " + RMSD);

		if (RMSD < convergenceCriterion) {
			convergence = true;
			//System.out.println("RMSD convergence");
		}
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

		//System.out.println("STEEPESTDM: initial coords:"+initialCoordinates);
		//System.out.println("STEEPESTDM: kcoordinates coords:"+kCoordinates);
				
		forceField.setEnergyGradient(kCoordinates);
		gradientk.set(forceField.getEnergyGradient());
		//System.out.println("Initial gradient : g0 = " + gradientk);

		steepestDescentsMinimum = new GVector(kCoordinates);
		GVector sk = new GVector(gradientk.getSize());
		GVector skplus1 = new GVector(gradientk.getSize());

		LineSearch ls = new LineSearch();

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(kCoordinates);
				
		while ((iterationNumberkplus1 < SDMaximumIteration) & (convergence == false)) {
			
			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			

			//System.out.println("");
			//System.out.println("SD Iteration number: " + iterationNumberkplus1);
			//System.out.println("gm.steepestDescentsMinimisation, Energy Gradient:"+forceField.getEnergyGradient());
			
			if (iterationNumberkplus1 != 1) {
				kCoordinates.set(kplus1Coordinates);
				gradientk.set(gradientkplus1);
				sk.set(skplus1);
			} 			
			//System.out.println("Search direction: ");
			sdm.setSk(gradientk, iterationNumberkplus1);
			skplus1.set(sdm.sk);
			
			ls.setLineSearchLambda(kCoordinates, sdm.sk, forceField, iterationNumberk);
			setkplus1Coordinates(sdm.sk, ls.lineSearchLambda);			
			//System.out.print("x" + iterationNumberkplus1 + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));

			forceField.setEnergyGradient(kplus1Coordinates);
			gradientkplus1.set(forceField.getEnergyGradient());
			
			checkConvergence(SDconvergenceCriterion);

			//System.out.println("");
			//System.out.println("f(x" + iterationNumberk + ") = " + forceField.energyFunction(kCoordinates));
			//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));
			//System.out.println("gradientkplus1 = " + gradientkplus1);
			if (iterationNumberkplus1 != 1) {
				//System.out.println("sk+1.sk = " + skplus1.dot(sk));
				//System.out.println("gk+1.gk = " + gradientkplus1.dot(gradientk));
			}
			
			if (molecule != null){
			    //System.out.println("STEEPESTDM: kplus1Coordinates:"+kplus1Coordinates);
			    ffTools.assignCoordinatesToMolecule(kplus1Coordinates, molecule); 
			}
		}
		steepestDescentsMinimum.set(kplus1Coordinates);
		forceField.setEnergyGradient(steepestDescentsMinimum);
		
		//forceField.setEnergyHessian(steepestDescentsMinimum);
		//NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
		//kCoordinates.set(kplus1Coordinates);
		//nrm.gradientPerInverseHessian(forceField.getEnergyGradient(),forceField.getEnergyHessian());
		//setkplus1Coordinates(nrm.getGradientPerInverseHessian(), -1);
		//System.out.println("x" + iterationNumberkplus1 + " = " + kplus1Coordinates);
		//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));

		
		//System.out.println("The SD minimum energy is at: " + steepestDescentsMinimum);
		
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
	}


	/**
	 *  Minimize the potential energy function using conjugate gradient method
	 *
	 * @param  forceField		The potential function to be used
	 */
	public void conjugateGradientMinimization(GVector initialCoordinates,  PotentialFunction forceField) {
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS ConjugatedGradientTest");
		
		initializeMinimizationParameters(initialCoordinates);
		
		forceField.setEnergyGradient(kCoordinates);
		gradientk.set(forceField.getEnergyGradient());
		//System.out.println("gradient at iteration 1 : g1 = " + gradientk);

		conjugateGradientMinimum = new GVector(kCoordinates);
		LineSearch ls = new LineSearch();
		
		ConjugateGradientMethod cgm = new ConjugateGradientMethod(kCoordinates);
		       
		while ((iterationNumberkplus1 < CGMaximumIteration) & (convergence == false)) {
			
			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			//System.out.println("");
			//System.out.println("");
			//System.out.println("CG Iteration number: " + iterationNumberkplus1);
			
			if (iterationNumberkplus1 != 1) {
				cgm.setuk(gradientk, gradientkplus1);
				kCoordinates.set(kplus1Coordinates);
				gradientk.set(gradientkplus1);
			}
			//System.out.println("Search direction: ");
			cgm.setvk(gradientk, iterationNumberkplus1);

			ls.setLineSearchLambda(kCoordinates, cgm.vk, forceField, iterationNumberk);
			setkplus1Coordinates(cgm.vk, ls.lineSearchLambda);			
			//System.out.print("x" + iterationNumberkplus1 + " = " + kplus1Coordinates + "	");
			//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));
			
			forceField.setEnergyGradient(kplus1Coordinates);
			gradientkplus1.set(forceField.getEnergyGradient());
			
			checkConvergence(CGconvergenceCriterion);
			
			//System.out.println("");
			//System.out.println("f(x" + iterationNumberk + ") = " + forceField.energyFunction(kCoordinates));
			//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));
			//System.out.println("gradientkplus1 = " + gradientkplus1);
			if (iterationNumberkplus1 != 1) {
				//System.out.println("gk+1.gk = " + gradientkplus1.dot(gradientk));
			}
			
			if (molecule !=null){
			    //System.out.println("CGM: kplus1Coordinates:"+kplus1Coordinates);
			    ffTools.assignCoordinatesToMolecule(kplus1Coordinates, molecule); 
			}
			
		 }
		 
		 conjugateGradientMinimum.set(kplus1Coordinates);
		 forceField.setEnergyGradient(conjugateGradientMinimum);
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

		forceField.setEnergyGradient(kCoordinates);
		gradientk.set(forceField.getEnergyGradient());
		//System.out.println("gradient at iteration 1 : g1 = " + gradientk);

		newtonRaphsonMinimum = new GVector(kCoordinates);

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS NewtonRaphsonTest");
		
		NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
		GMatrix hessian = new GMatrix(initialCoordinates.getSize(),initialCoordinates.getSize());
		
		while ((iterationNumberkplus1 < NRMaximumIteration) & (convergence == false)) {
			
			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			//System.out.println("");
			//System.out.println("NR Iteration number: " + iterationNumberkplus1);

			if (iterationNumberkplus1 != 1) {
				kCoordinates.set(kplus1Coordinates);
				gradientk.set(gradientkplus1);
			}
 			forceField.setEnergyHessian(kCoordinates);
			hessian.set(forceField.getEnergyHessian());
			//System.out.println("hessian = " + hessian);
			nrm.gradientPerInverseHessian(gradientk,hessian);
			//System.out.println("GradientPerInverseHessian = " + nrm.getGradientPerInverseHessian());
			
			setkplus1Coordinates(nrm.getGradientPerInverseHessian(), -1);
			//System.out.println("x" + iterationNumberkplus1 + " = " + kplus1Coordinates);
			//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));

			forceField.setEnergyGradient(kplus1Coordinates);
			gradientkplus1.set(forceField.getEnergyGradient());

			checkConvergence(NRconvergenceCriterion);
			//System.out.println("convergence: " + convergence);

			//System.out.println("");
			//System.out.println("f(x" + iterationNumberk + ") = " + forceField.energyFunction(kCoordinates));
			//System.out.println("f(x" + iterationNumberkplus1 + ") = " + forceField.energyFunction(kplus1Coordinates));
			//System.out.println("gradientkplus1 = " + gradientkplus1);
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

