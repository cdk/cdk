package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;

/**
 *  The line-search method searches along the line containing the current point, xk, and parallel to the search direction
 *
 *@author     vlabarta
 *@cdk.module     builder3d
 *
 */
public class LineSearch {

	double lambdaa = 0;
	double lambdab = 0;
	double lambdac = 0;
	double fxa = 0;
	double fxb = 0;
	double fxc = 0;
	double parabolMinimumLambda = 0;
	double arbitraryStepSize = 0;

	/**
	 *  Constructor for the LineSearch object
	 */
	public LineSearch() { }


	/**
	 *  Bracketing the minimum: The bracketing phase determines the range of points on the line to be searched.
	 *  Look for 3 points along the line where the energy of the middle point is lower than the energy of the two outer points.
	 *  The bracket corresponds to an interval specifying the range of values of Lambda.
	 *
	 *@param  kPoint              Current point, xk
	 *@param  searchDirection     Search direction
	 *@param  forceFieldFunction  Potential energy function
	 */
	public void bracketingTheMinimum(GVector kPoint, GVector searchDirection, PotentialFunction forceFieldFunction, int iterNum) {

		//System.out.println("Bracketing the minimum:");
		//System.out.print("X" + iterNum + " = " + kPoint + "	");
		//System.out.println("f(X" + iterNum + ") = " + forceFieldFunction.energyFunction(kPoint));

		GVector xa = new GVector(kPoint);
		lambdaa = 0;

		GVector xb = new GVector(kPoint);
		lambdab = 2;
		GVector direction = new GVector(searchDirection);
		direction.scale(lambdab);
		xb.add(direction);

		GVector xc = new GVector(kPoint.getSize());

		fxa = forceFieldFunction.energyFunction(xa);
		fxb = forceFieldFunction.energyFunction(xb);
		
		//System.out.print("lambdaa = " + lambdaa + "	");
		//System.out.println("fxa = " + fxa);
		//System.out.print("lambdab = " + lambdab + "	");
		//System.out.println("fxb = " + fxb);

		boolean finish = false;
		if (fxb < fxa) {

			//System.out.println("The energy decrease with the current step size. The stepsize will be increase by 20%");
			lambdac = 1.2 * lambdab;
			xc.set(kPoint);
			direction.set(searchDirection);
			direction.scale(lambdac);
			xc.add(direction);
			fxc = forceFieldFunction.energyFunction(xc);
		
			//System.out.print("lambdaa = " + lambdaa + "	");
			//System.out.println("fxa = " + fxa);
			//System.out.print("lambdab = " + lambdab + "	");
			//System.out.println("fxb = " + fxb);
			//System.out.print("lambdac = " + lambdac + "	");
			//System.out.println("fxc = " + fxc);

			while (finish == false) {

				if (fxc > fxb) {
					finish = true;
				} 
				else {
					xa.set(xb);
					lambdaa = lambdab;
					fxa = fxb;
					
					xb.set(xc);
					lambdab = lambdac;
					fxb = fxc;
					
					//System.out.println("The energy decrease with the current step size. The stepsize will be increase by 20%");
					lambdac = 1.2 * lambdac;
					xc.set(kPoint);
					direction.set(searchDirection);
					direction.scale(lambdac);
					xc.add(direction);
					fxc = forceFieldFunction.energyFunction(xc);

					//System.out.print("lambdaa = " + lambdaa + "	");
					//System.out.println("fxa = " + fxa);
					//System.out.print("lambdab = " + lambdab + "	");
					//System.out.println("fxb = " + fxb);
					//System.out.print("lambdac = " + lambdac + "	");
					//System.out.println("fxc = " + fxc);

				}
			}
		} 
		else {
			while (finish == false) {
				
				//System.out.println("The energy increase with the current step size. The step size will be halve");
				
				xc.set(xb);
				lambdac = lambdab;
				fxc = fxb;
				
				lambdab = lambdab / 2;
				xb.set(kPoint);
				direction.set(searchDirection);
				direction.scale(lambdab);
				xb.add(direction);
				fxb = forceFieldFunction.energyFunction(xb);

				//System.out.print("lambdaa = " + lambdaa + "	");
				//System.out.println("fxa = " + fxa);
				//System.out.print("lambdab = " + lambdab + "	");
				//System.out.println("fxb = " + fxb);
				//System.out.print("lambdac = " + lambdac + "	");
				//System.out.println("fxc = " + fxc);

				if (fxb < fxa) {
					finish = true;
				}
				if (lambdab < 0.00001) {
					finish = true;
				}
			}
		}
		arbitraryStepSize = lambdab;
		//System.out.println("");
		//System.out.println("ArbitraryStep: ");
		//System.out.println("arbitraryStepSize = " + arbitraryStepSize);
		return;
	}


	public double parabolicInterpolation() {
		parabolMinimumLambda = fxa * (Math.pow(lambdac,2) - Math.pow(lambdab,2)) + fxb * (Math.pow(lambdaa,2) - Math.pow(lambdac,2)) + fxc * (Math.pow(lambdab,2) - Math.pow(lambdaa,2));
		parabolMinimumLambda = parabolMinimumLambda / (fxa * (lambdac-lambdab) + fxb * (lambdaa-lambdac) + fxc * (lambdab-lambdaa));
		parabolMinimumLambda = 0.5 * parabolMinimumLambda;
		//System.out.println("parabolMinimumLambda = " + parabolMinimumLambda);
		return parabolMinimumLambda;
	}


	/**
	 *  Gets the stepSize attribute of the LineSearch object
	 *
	 *@return    The stepSize value
	 */
	public double getStepSize() {
		return lambdab;
	}


	/**
	 *  xk+1= Xk + Lambdak Sk
	 *
	 *@param  oldCoordinates   Coordinates of the previous step, k
	 *@param  currentStepSize  Step size estimated
	 *@return                  New coordinates of the atoms, k+1 step
	 */
	public GVector setCoordinates(GVector oldCoordinates, double currentStepSize) {
		GVector coordinates = new GVector(oldCoordinates.getSize());
		return coordinates;
	}


	/**
	 */
	public void setStepSize() {
		return;
	}

}

