/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2005-2007  Violeta Labarta <vlabarta@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.modeling.forcefield;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.modeling.builder3d.ForceFieldConfigurator;

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;
import java.util.Hashtable;
//import org.openscience.cdk.tools.LoggingTool;
import java.util.Map;


/**
 * Call the minimization methods. Check the convergence.
 *
 * @author      vlabarta
 * @cdk.module  forcefield
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword geometry
 * @cdk.keyword 3D coordinates
 */
public class GeometricMinimizer {

	Map PotentialParameterSet = null;
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
	boolean gradientSmallEnoughFlag = false;
	double infiniteNorm;

	NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
	//private LoggingTool logger;

	IMolecule molecule;


	/**
	 *  Constructor for the GeometricMinimizer object
	 */
	public GeometricMinimizer() {
		//logger = new LoggingTool(this);
	}


	public void setMolecule(IMolecule mol, boolean clone) throws Exception {

		if (clone) {
			this.molecule = (IMolecule) mol.clone();
		} else {
			this.molecule = mol;
		}
	}

    	public IMolecule getMolecule() {
		return this.molecule;
	}


	/**
	 *  Assign MMFF94 atom types to the molecule.
	 *
	 *@param  molecule  The molecule like an AtomContainer object.
	 */
	public void setMMFF94Tables(IAtomContainer molecule) throws Exception {
		//logger.debug("Start setMMFF94Tables");
		ForceFieldConfigurator ffc = new ForceFieldConfigurator();
		//logger.debug("setForceFieldConfigurator");
		ffc.setForceFieldConfigurator("mmff94");
		//logger.debug("assignAtomTyps");
		ffc.assignAtomTyps((IMolecule) molecule); // returns non-used RingSet
		//logger.debug("PotentialParameterSet");
		PotentialParameterSet = ffc.getParameterSet();
		//logger.debug("PotentialParameterSet = " + PotentialParameterSet);
	}

	public Map getPotentialParameterSet() {
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
	 *  Calculate the Root Mean Square gradient (RMS)
	 *
	 * @param  gradient  Gradient of the energy function in the new calculated point xk+1.
	 */
	public double rootMeanSquareGradient(GVector gradient) {

		RMS = 0;
		RMS = gradient.dot(gradient);
		RMS = RMS / gradient.getSize();
		RMS = Math.sqrt(RMS);
		//logger.debug("RMS = " + RMS);

		return RMS;
	}


	/**
	 *  Analyse if the gradient is small enough, using the criteria || gradient || < 10-5 (1 + |function|)
	 *
	 * @param  function  energy function value.
	 * @param  gradient  Gradient of the energy function.
	 */
	public void gradientSmallEnough(double function, GVector gradient) {

		//logger.debug("Analyse if the gradient is small enough");

		infiniteNorm = 0;

		for (int i=0; i < gradient.getSize(); i++) {
		infiniteNorm = Math.max(infiniteNorm, Math.abs(gradient.getElement(i)));
		}

		//logger.debug("infiniteNorm = " + infiniteNorm);
		//logger.debug("0.00005  * (1 + Math.abs(function)) = " + (0.00005  * (1 + Math.abs(function))));

		if (infiniteNorm < 0.00005  * (1 + Math.abs(function))) {gradientSmallEnoughFlag = true;}

	}


	/**
	 *  To check convergence
	 */
	public void checkConvergence(double convergenceCriterion) {

		//logger.debug("Checking convergence : ");

		RMS = rootMeanSquareGradient(gradientkplus1);
		//logger.debug("RMS = " + RMS);

		if (RMS < convergenceCriterion) {
			convergence = true;
			//logger.debug("RMS convergence");
			//logger.debug("RMS = " + RMS);
		}

		gradientSmallEnough(fxkplus1, gradientkplus1);

		if (gradientSmallEnoughFlag == true) {
			convergence = true;
			//logger.debug("Gradient Small Enough");
		}


		if (Math.abs(this.fxk - this.fxkplus1) < 0.0001) {
			RMSD = 0;
			for (int i = 0; i < atomNumbers; i++) {
				d = ForceFieldTools.distanceBetweenTwoAtomFromTwo3xNCoordinates(kplus1Coordinates, kCoordinates, i, i);
				RMSD = RMSD + Math.pow(d, 2);
			}
			RMSD = RMSD / kCoordinates.getSize();
			RMSD = Math.sqrt(RMSD);
			//logger.debug("RMSD = " + RMSD);

			if (RMSD < 0.000001) {
				convergence = true;
				//logger.debug("RMSD convergence");
			}
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
		//logger.debug("f(x0) = " + fxk);

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
		LineSearchForTheWolfeConditions ls = new LineSearchForTheWolfeConditions(forceField, "sdm");

		while ((iterationNumberkplus1 < SDMaximumIteration) & (convergence == false)) {

			iterationNumberkplus1 += 1;
			iterationNumberk += 1;


//			if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 2) {
				//logger.debug("");
				//logger.debug("SD Iteration number: " + iterationNumberkplus1);
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
				//logger.debug("linearFunctionDerivativekplus1 = " + gradientkplus1.dot(sdm.sk));
				//logger.debug("linearFunctionDerivativek = " + linearFunctionDerivativek);
			}*/

			//if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 1) {
				//logger.debug("");
				//logger.debug("f(x" + iterationNumberk + ") = " + fxk);
				//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);
				//logger.debug("fxkplus1 - fxk = " + (fxkplus1 - fxk));
				//logger.debug("gradientkplus1, gradientk angle = " + gradientkplus1.angle(gradientk));
			//}

			/*if (iterationNumberkplus1 != 1) {
				logger.debug("sk+1.sk = " + skplus1.dot(sk));
				logger.debug("gk+1.gk = " + gradientkplus1.dot(gradientk));
			}*/

		}
		steepestDescentsMinimum = kplus1Coordinates;
		//logger.debug("The minimum energy is " + fxkplus1);
		//logger.debug("SD Iteration number: " + iterationNumberkplus1);

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
		    ForceFieldTools.assignCoordinatesToMolecule(kplus1Coordinates, molecule);
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
		//logger.debug("f(x0) = " + fxk);

		forceField.setEnergyGradient(kCoordinates);
		gradientk = forceField.getEnergyGradient();
		//logger.debug("gradient at iteration 1 : g1 = " + gradientk);

		double linearFunctionDerivativek =1;

		double alphaInitialStep = 0.01;

		if (gradientk.equals(g0)) {
			convergence = true;
			kplus1Coordinates.set(kCoordinates);
		}

		ConjugateGradientMethod cgm = new ConjugateGradientMethod();
		cgm.initialize(gradientk);
		LineSearchForTheWolfeConditions ls = new LineSearchForTheWolfeConditions(forceField, "cgm");

		gradientSmallEnough(fxk, gradientk);
		if (gradientSmallEnoughFlag == true) {
			convergence = true;
			//logger.debug("Gradient Small Enough");
		}


		while ((iterationNumberkplus1 < CGMaximumIteration) & (convergence == false)) {

			iterationNumberkplus1 += 1;
			iterationNumberk += 1;
			//logger.debug("");
			//logger.debug("");
			//if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 2) {
				//logger.debug("");
				//logger.debug("CG Iteration number: " + iterationNumberkplus1);
			//}

			if (iterationNumberkplus1 != 1) {
				//logger.debug("ls.alphaOptimum = " + ls.alphaOptimum);
				//logger.debug("linearFunctionDerivativek = " + linearFunctionDerivativek);
				alphaInitialStep = ls.alphaOptimum * linearFunctionDerivativek;
				kCoordinates.set(kplus1Coordinates);
				fxk = fxkplus1;
				cgm.setDirection(gradientkplus1, gradientk);
				gradientk.set(gradientkplus1);
			}

			//logger.debug("Search direction: ");
			linearFunctionDerivativek = gradientk.dot(cgm.conjugatedGradientDirection);
			if (iterationNumberkplus1 != 1) {
				alphaInitialStep = alphaInitialStep / linearFunctionDerivativek;
				//alphaInitialStep = Math.min(1.01 * alphaInitialStep,1);
			}

			//logger.debug("linearFunctionDerivativek = " + linearFunctionDerivativek);
			//logger.debug("alphaInitialStep = " + alphaInitialStep);
			ls.initialize(kCoordinates, fxk, gradientk, cgm.conjugatedGradientDirection, linearFunctionDerivativek, alphaInitialStep);
			ls.lineSearchAlgorithm(5);
			//if (ls.alphaOptimum == 0) {convergence = true;}
			//logger.debug("ls.alphaOptimum = " + ls.alphaOptimum);
			setkplus1Coordinates(cgm.conjugatedGradientDirection, ls.alphaOptimum);
			fxkplus1 = ls.linearFunctionInAlphaOptimum;
			//logger.debug("x" + iterationNumberkplus1 + " = " + kplus1Coordinates + "	");
			//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);

			gradientkplus1.set(ls.dfOptimum);
			//logger.debug("gradientkplus1 = " + gradientkplus1);


			checkConvergence(CGconvergenceCriterion);
			/*if (convergence == true) {
				forceField.setEnergyHessian(kplus1Coordinates);
				NewtonRaphsonMethod nrm = new NewtonRaphsonMethod();
				//nrm.determinat(gradientkplus1,forceField.getEnergyHessian());
				nrm.hessianEigenValues(forceField.getForEnergyHessian(),kplus1Coordinates.getSize());
			}*/

			//logger.debug(" ");
			//logger.debug("convergence = " + convergence);

			if (fxkplus1 <= fxk + 0.0001 * ls.alphaOptimum * linearFunctionDerivativek) {}
			else {
				//logger.debug("SUFFICIENT DECREASE CONDITION NOT SATISFIED");
				break;
			}
			if (Math.abs(gradientkplus1.dot(cgm.conjugatedGradientDirection)) <= -0.09 * linearFunctionDerivativek) {}
			else {
				//logger.debug("CURVATURE CONDITION NOT SATISFIED");
				//logger.debug("linearFunctionDerivativekplus1 = " + gradientkplus1.dot(cgm.conjugatedGradientDirection));
				//logger.debug("linearFunctionDerivativek = " + linearFunctionDerivativek);
			}

			//if (iterationNumberkplus1 % 50 == 0 | iterationNumberkplus1 == 1) {
				//logger.debug("");
				//logger.debug("f(x" + iterationNumberk + ") = " + fxk);
				//logger.debug("f(x" + iterationNumberkplus1 + ") = " + fxkplus1);
				//logger.debug("fxkplus1 - fxk = " + (fxkplus1 - fxk));
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
		 //logger.debug("f(x" + iterationNumberk + ") = " + fxk);
		 //logger.debug("f(minimum) = " + fxkplus1);
		 //logger.debug("CG converge at iteration " + iterationNumberkplus1);
		 //logger.debug("Energy function evaluation number : " + forceField.functionEvaluationNumber);

		if (molecule !=null){
		    //logger.debug("CGM: kplus1Coordinates:"+kplus1Coordinates);
		    ForceFieldTools.assignCoordinatesToMolecule(kplus1Coordinates, molecule);
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
		gradientk = forceField.getEnergyGradient();
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

