package org.openscience.cdk.modeling.forcefield;

import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.*;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Call the minimization methods. Check the convergence.
 *
 *@author     vlabarta
 *@cdk.module     forcefield
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
	GVector g0 = null;
	int atomNumbers = 1;
	
	double fxk = 0;
	double fxkplus1 = 0;

	GVector gradientk = null;
	GVector gradientkplus1 = null;

	GVector steepestDescentsMinimum = null;
	GVector conjugateGradientMinimum = null;
	GVector newtonRaphsonMinimum = null;
	
	double minimumFunctionValueCGM;

	int iterationNumberkplus1 = 0;
	int iterationNumberk = -1;
	boolean convergence = false;
	double RMSD = 0;
	double RMS = 0;
	double d = 0;
	
	ForceFieldTools ffTools = new ForceFieldTools();
	NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
	private LoggingTool logger;

	Molecule molecule;


	/**
	 *  Constructor for the GeometricMinimizer object
	 */
	public GeometricMinimizer() {
		logger = new LoggingTool(this);
	}


	public void setMolecule(Molecule mol, boolean clone) {

		if (clone) {
			try {
				this.molecule = (Molecule) mol.clone();
			} catch (CloneNotSupportedException e) {
				logger.error("Should clone, but exception occured: ", e.getMessage());
				logger.debug(e);
			}
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
		RingSet rs = (RingSet) ffc.assignAtomTyps((Molecule) molecule);
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
		//logger.debug("Coordinates at iteration 1: X1 = " + kCoordinates);
		gradientk = new GVector(kCoordinates.getSize());
		kplus1Coordinates = new GVector(kCoordinates.getSize());
		gradientkplus1 = new GVector(kCoordinates.getSize());
		
		convergence = false;
		iterationNumberkplus1 = 0;
		iterationNumberk = -1;
		
		atomNumbers = initialCoord.getSize()/3;
		
		g0 = new GVector(kCoordinates.getSize());
		g0.zero();
	}


	/**
	 *  To check convergence
	 */
	public void checkConvergence(double convergenceCriterion) {
		
		//System.out.println("Checking convergence : ");
		RMS = 0;
		RMS = gradientkplus1.dot(gradientkplus1);
		RMS = RMS / kCoordinates.getSize();
		RMS = Math.sqrt(RMS);
		//logger.debug("RMS = " + RMS);

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
		//logger.debug("RMSD = " + RMSD);

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
    public void steepestDescentsMinimization(GVector initialCoordinates, IPotentialFunction forceField) {
		
		initializeMinimizationParameters(initialCoordinates);
		fxk = forceField.energyFunction(initialCoordinates);
		//logger.debug("STEEPESTDM: initial coords:"+initialCoordinates);
		//logger.debug("STEEPESTDM: kcoordinates coords:"+kCoordinates);
		//System.out.println("f(x0) = " + fxk);
				
		forceField.setEnergyGradient(kCoordinates);
		gradientk.set(forceField.getEnergyGradient());
		//logger.debug("Initial gradient : g0 = " + gradientk);

		GVector sk = new GVector(gradientk.getSize());
		GVector skplus1 = new GVector(gradientk.getSize());
		
		double linearFunctionDerivativek =1;
		
		double alphaInitialStep = 0.5;

		if (gradientk.equals(g0)) {
			convergence = true;
			kplus1Coordinates.set(kCoordinates);
		}		

		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(kCoordinates);
		LineSearchForTheWolfeConditions ls = new LineSearchForTheWolfeConditions(forceField);
				
		while ((iterationNumberkplus1 < SDMaximumIteration) & (convergence == false)) {
			
			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			

//			if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 2) {
				//System.out.println("");
				//System.out.println("SD Iteration number: " + iterationNumberkplus1);
//			}
			
			//logger.debug("gm.steepestDescentsMinimisation, Energy Gradient:"+forceField.getEnergyGradient());
			
			if (iterationNumberkplus1 != 1) {
				alphaInitialStep = ls.alphaOptimum * linearFunctionDerivativek;							 
				kCoordinates.set(kplus1Coordinates);
				fxk = fxkplus1;
				gradientk.set(gradientkplus1);
				sk.set(skplus1);
			} 			
			//logger.debug("Search direction: ");
			sdm.setSk(gradientk);
			skplus1.set(sdm.sk);
			linearFunctionDerivativek = gradientk.dot(skplus1);
			if (iterationNumberkplus1 != 1) {
				alphaInitialStep = alphaInitialStep / linearFunctionDerivativek;
			}
			
			ls.initialize(kCoordinates, fxk, gradientk, sdm.sk, linearFunctionDerivativek, alphaInitialStep);
			ls.lineSearchAlgorithm (5);
			setkplus1Coordinates(sdm.sk, ls.alphaOptimum);
			fxkplus1 = ls.linearFunctionInAlphaOptimum; 			
			//logger.debug("x" + iterationNumberkplus1 + " = " + kplus1Coordinates + "	");
			//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);

			gradientkplus1.set(ls.dfOptimum);
			
			checkConvergence(SDconvergenceCriterion);

			/*if (fxkplus1 <= fxk + 0.0001 * ls.alphaOptimum * linearFunctionDerivativek) {}
			else {
				System.out.println("Sufficient Decrease Condition not satisfied");
				break;
			}
			if ((Math.abs(gradientkplus1.dot(sdm.sk)) <= -0.1 * linearFunctionDerivativek) | (ls.alphaOptimum == 5)) {}
			else {
				System.out.println("Curvature Condition not satisfied");
				//System.out.println("linearFunctionDerivativekplus1 = " + gradientkplus1.dot(sdm.sk));
				//System.out.println("linearFunctionDerivativek = " + linearFunctionDerivativek);
			}*/
					
			//if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 1) {
				//System.out.println("");
				//System.out.println("f(x" + iterationNumberk + ") = " + fxk);
				//System.out.println("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);
				//System.out.println("fxkplus1 - fxk = " + (fxkplus1 - fxk));
				//System.out.println("gradientkplus1, gradientk angle = " + gradientkplus1.angle(gradientk));
			//}

			/*if (iterationNumberkplus1 != 1) {
				logger.debug("sk+1.sk = " + skplus1.dot(sk));
				logger.debug("gk+1.gk = " + gradientkplus1.dot(gradientk));
			}*/
			
		}
		steepestDescentsMinimum = kplus1Coordinates;
		//System.out.println("The minimum energy is " + fxkplus1);
		//System.out.println("SD Iteration number: " + iterationNumberkplus1);
		
		//forceField.setEnergyHessian(steepestDescentsMinimum);
		//NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
		//kCoordinates.set(kplus1Coordinates);
		//nrm.gradientPerInverseHessian(forceField.getEnergyGradient(),forceField.getEnergyHessian());
		//setkplus1Coordinates(nrm.getGradientPerInverseHessian(), -1);
		//logger.debug("x" + iterationNumberkplus1 + " = " + kplus1Coordinates);
		//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);

		
		//logger.debug("The SD minimum energy is at: " + steepestDescentsMinimum);
		
		if (molecule != null){
		    //logger.debug("STEEPESTDM: kplus1Coordinates:"+kplus1Coordinates);
		    ffTools.assignCoordinatesToMolecule(kplus1Coordinates, molecule);
		}

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
	public void conjugateGradientMinimization(GVector initialCoordinates, IPotentialFunction forceField) {
		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS ConjugatedGradientTest");
		
		initializeMinimizationParameters(initialCoordinates);
		fxk = forceField.energyFunction(initialCoordinates);
		//System.out.println("f(x0) = " + fxk);
		
		forceField.setEnergyGradient(kCoordinates);
		gradientk.set(forceField.getEnergyGradient());
		//logger.debug("gradient at iteration 1 : g1 = " + gradientk);

		double linearFunctionDerivativek =1;
		
		double alphaInitialStep = 0.5;

		if (gradientk.equals(g0)) {
			convergence = true;
			kplus1Coordinates.set(kCoordinates);
		}		
		
		ConjugateGradientMethod cgm = new ConjugateGradientMethod();
		LineSearchForTheWolfeConditions ls = new LineSearchForTheWolfeConditions(forceField);
		       
		while ((iterationNumberkplus1 < CGMaximumIteration) & (convergence == false)) {
			
			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			//logger.debug("");
			//logger.debug("");
			//if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 2) {
				//System.out.println("");
				//System.out.println("CG Iteration number: " + iterationNumberkplus1);
			//}
			
			if (iterationNumberkplus1 != 1) {
				alphaInitialStep = ls.alphaOptimum * linearFunctionDerivativek;							 
				kCoordinates.set(kplus1Coordinates);
				fxk = fxkplus1;
				gradientk.set(gradientkplus1);
				//System.out.println("gradientk = " + gradientk);
			}
			//logger.debug("Search direction: ");
			cgm.setvk(gradientk, iterationNumberkplus1, ls.derivativeSmallEnough);
			linearFunctionDerivativek = gradientk.dot(cgm.vk);
			if (iterationNumberkplus1 != 1) {
				alphaInitialStep = alphaInitialStep / linearFunctionDerivativek;
			}

			ls.initialize(kCoordinates, fxk, gradientk, cgm.vk, linearFunctionDerivativek, alphaInitialStep);
			ls.lineSearchAlgorithm (5);
			setkplus1Coordinates(cgm.vk, ls.alphaOptimum);			
			fxkplus1 = ls.linearFunctionInAlphaOptimum; 			
			//logger.debug("x" + iterationNumberkplus1 + " = " + kplus1Coordinates + "	");
			//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);
			
			gradientkplus1.set(ls.dfOptimum);
			//System.out.println("gradientkplus1 = " + gradientkplus1);
			
			checkConvergence(CGconvergenceCriterion);
			//logger.debug(" ");
			//logger.debug("convergence = " + convergence);
			
			/*if (fxkplus1 <= fxk + 0.0001 * ls.alphaOptimum * linearFunctionDerivativek) {}
			else {
				System.out.println("Sufficient Decrease Condition not satisfied");
				break;
			}
			if (Math.abs(gradientkplus1.dot(cgm.vk)) <= -0.1 * linearFunctionDerivativek) {}
			else {
				System.out.println("Curvature Condition not satisfied");
				//System.out.println("linearFunctionDerivativekplus1 = " + gradientkplus1.dot(cgm.vk));
				//System.out.println("linearFunctionDerivativek = " + linearFunctionDerivativek);
			}*/
					
			//if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 1) {
				//System.out.println("");
				//System.out.println("f(x" + iterationNumberk + ") = " + fxk);
				//System.out.println("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);
				//System.out.println("fxkplus1 - fxk = " + (fxkplus1 - fxk));
			//}
			
			/*if (iterationNumberkplus1 != 1) {
				logger.debug("gk+1.gk = " + gradientkplus1.dot(gradientk));
			}*/
			//forceField.setEnergyHessian(kplus1Coordinates);
			//nrm.determinat(gradientkplus1, forceField.getEnergyHessian());
			//nrm.hessianEigenValues(forceField.getForEnergyHessian(), kplus1Coordinates.getSize());

		 }
		 
		 conjugateGradientMinimum = kplus1Coordinates;
		 minimumFunctionValueCGM = fxkplus1;
		 //logger.debug("conjugateGradientMinimum, forceField.getEnergyGradient().norm() = " + forceField.getEnergyGradient().norm());
		 //logger.debug("The CG minimum energy is at: " + conjugateGradientMinimum);
		 //System.out.println("f(x" + iterationNumberk + ") = " + fxk);
		 //System.out.println("f(minimum) = " + fxkplus1);
		 //System.out.println("CG Iteration number: " + iterationNumberkplus1);
		 
		if (molecule !=null){
		    //logger.debug("CGM: kplus1Coordinates:"+kplus1Coordinates);
		    ffTools.assignCoordinatesToMolecule(kplus1Coordinates, molecule);
		}

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
	 *  Gets the conjugatedGradientMinimum attribute of the GeometricMinimizer object
	 *
	 *@return    The minimumCoordinates value
	 */
	public double getMinimumFunctionValueCGM() {
		return minimumFunctionValueCGM;
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
	public void newtonRaphsonMinimization(GVector initialCoordinates, IPotentialFunction forceField) {
		
		initializeMinimizationParameters(initialCoordinates);

		forceField.setEnergyGradient(kCoordinates);
		gradientk.set(forceField.getEnergyGradient());
		//logger.debug("gradient at iteration 1 : g1 = " + gradientk);

		newtonRaphsonMinimum = new GVector(kCoordinates);

		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS NewtonRaphsonTest");
		
		GMatrix hessian = new GMatrix(initialCoordinates.getSize(),initialCoordinates.getSize());
		
		while ((iterationNumberkplus1 < NRMaximumIteration) & (convergence == false)) {
			
			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			//logger.debug("");
			//logger.debug("NR Iteration number: " + iterationNumberkplus1);

			if (iterationNumberkplus1 != 1) {
				kCoordinates.set(kplus1Coordinates);
				gradientk.set(gradientkplus1);
			}
 			forceField.setEnergyHessian(kCoordinates);
			hessian.set(forceField.getEnergyHessian());
			//logger.debug("hessian = " + hessian);
			nrm.gradientPerInverseHessian(gradientk,hessian);
			//logger.debug("GradientPerInverseHessian = " + nrm.getGradientPerInverseHessian());
			
			setkplus1Coordinates(nrm.getGradientPerInverseHessian(), -1);
			//logger.debug("x" + iterationNumberkplus1 + " = " + kplus1Coordinates);
			//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);

			forceField.setEnergyGradient(kplus1Coordinates);
			gradientkplus1.set(forceField.getEnergyGradient());

			checkConvergence(NRconvergenceCriterion);
			//logger.debug("convergence: " + convergence);

			//logger.debug("");
			//logger.debug("f(x" + iterationNumberk + ") = " + fxk);
			//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);
			//logger.debug("gradientkplus1 = " + gradientkplus1);
		}
		   newtonRaphsonMinimum.set(kplus1Coordinates);
		   //logger.debug("The NR minimum energy is at: " + newtonRaphsonMinimum);
		 
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

