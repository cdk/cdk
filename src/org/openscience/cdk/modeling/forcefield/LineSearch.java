package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  The LineSearch class include the first and second step of the line search approach: 
 *  Bracket the minimum and interpolation. The interpolation is quadratic.
 *  
 *@author     vlabarta
 *@cdk.module     builder3d
 *
 */
public class LineSearch {

	GVector direction = new GVector(3);
	double lambdaa = 0;
	double lambdab = 1;
	double lambdac = 0;
	double fxa = 0;
	double fxb = 0;
	double fxc = 0;
	double parabolMinimumLambda = 0;
	double lineSearchLambda = 0;
	private LoggingTool logger;


	/**
	 *  Constructor for the LineSearch object
	 */
	public LineSearch() {        
		logger = new LoggingTool(this);
	}


	/**
	 *  Bracket the minimum using the Golden Section Search. 
	 *  The bracketing phase determines the range of points on the search line to be consider for the interpolation.
	 *  Look for 3 points along the line where the energy of the middle point is lower than the energy of the two outer points.
	 *  The bracket corresponds to an interval specifying the range of values of Lambda.
	 *
	 *@param  kPoint              Current point, xk
	 *@param  searchDirection     Search direction
	 *@param  forceFieldFunction  Potential energy function
	 */
	public void goldenSectionSearch(GVector kPoint, GVector searchDirection, PotentialFunction forceFieldFunction) {

		//logger.debug("Bracketing the minimum using the Golden Section Search:");
		
		double lambda1 = 0;		GVector x1 = new GVector(kPoint);	double fx1 = forceFieldFunction.energyFunction(x1);
		double lambda4 = 100 * lambdab;
		double lambda2 = 0.3819660112 * (lambda4 - lambda1);
		double lambda3 = 0.6180339887498948482 * (lambda4 - lambda1);
		
		direction.set(searchDirection);		direction.scale(lambda2);	direction.add(kPoint);
		GVector x2 = new GVector(direction);	double fx2 = forceFieldFunction.energyFunction(x2);		
		direction.set(searchDirection);		direction.scale(lambda3);	direction.add(kPoint);
		GVector x3 = new GVector(direction);	double fx3 = forceFieldFunction.energyFunction(x3);
		direction.set(searchDirection);		direction.scale(lambda4);	direction.add(kPoint);		
		GVector x4 = new GVector(direction);	double fx4 = forceFieldFunction.energyFunction(x4);
		
		boolean finish = false;
		while (finish != true) {
			if (fx2 < fx3) {//we can bracket the minimum by the interval [x1, x3]
				lambda4 = lambda3;	x4.set(x3);	fx4 = fx3;
				lambda3 = lambda2;	x3.set(x2); 	fx3 = fx2;
				lambda2 = lambda1 + 0.3819660112 * (lambda4 - lambda1);
				direction.set(searchDirection);		direction.scale(lambda2);	direction.add(kPoint);
				x2.set(direction);	fx2 = forceFieldFunction.energyFunction(x2);
			} else {//we can bracket the minimum by the interval [x2, x4]
				lambda1 = lambda2;	x1.set(x2);	fx1 = fx2;
				lambda2 = lambda3;	x2.set(x3); 	fx2 = fx3;
				lambda3 = lambda1 + 0.6180339887498948482 * (lambda4 - lambda1);
				direction.set(searchDirection);		direction.scale(lambda3);	direction.add(kPoint);
				x3.set(direction);	fx3 = forceFieldFunction.energyFunction(x3);
			}
			if (fx4-fx1<0.0000001) {
				finish = true;
				if (fx2 < fx3) {lineSearchLambda = lambda2;}
				else {lineSearchLambda = lambda3;}
			}
			System.out.println();
			System.out.println("lambda1= " + lambda1 + " ; fx1 = " + fx1);
			System.out.println("lambda2= " + lambda2 + " ; fx2 = " + fx2);
			System.out.println("lambda3= " + lambda3 + " ; fx3 = " + fx3);
			System.out.println("lambda4= " + lambda4 + " ; fx4 = " + fx4);
			System.out.println("finish = " + finish);
		}
		return;
	}


	/**
	 *  Bracketing the minimum: The bracketing phase determines the range of points on the line to be searched.
	 *  Look for 3 points along the line where the energy of the middle point is lower than the energy of the two outer points.
	 *  The bracket corresponds to an interval specifying the range of values of Lambda.
	 *
	 *@param  kPoint              Current point, xk
	 *@param  searchDirection     Search direction
	 *@param  forceFieldFunction  Potential energy function
	 */
	public void bracketingTheMinimum(GVector kPoint, GVector searchDirection, PotentialFunction forceFieldFunction) {

		//logger.debug("Bracketing the minimum:");

		lambdaa = 0;
		GVector xa = new GVector(kPoint);
		fxa = forceFieldFunction.energyFunction(xa);

		lambdab = 2 * lambdab;
		GVector xb = new GVector(kPoint);
		direction.set(searchDirection);
		direction.scale(lambdab);
		xb.add(direction);
		fxb = forceFieldFunction.energyFunction(xb);

		GVector xc = new GVector(kPoint.getSize());
		
		//logger.debug("lambdaa = " + lambdaa + "	");
		//logger.debug("fxa = " + fxa);
		//logger.debug("lambdab = " + lambdab + "	");
		//logger.debug("fxb = " + fxb);

		boolean finish = false;
		
		if (fxb < fxa) {

			lambdac = 1.2 * lambdab;
			xc.set(kPoint);
			direction.set(searchDirection);
			direction.scale(lambdac);
			xc.add(direction);
			fxc = forceFieldFunction.energyFunction(xc);
		
			//logger.debug("lambdaa = " + lambdaa + "	");
			//logger.debug("fxa = " + fxa);
			//logger.debug("lambdab = " + lambdab + "	");
			//logger.debug("fxb = " + fxb);
			//logger.debug("lambdac = " + lambdac + "	");
			//logger.debug("fxc = " + fxc);

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
					
					lambdac = 1.2 * lambdac;
					xc.set(kPoint);
					direction.set(searchDirection);
					direction.scale(lambdac);
					xc.add(direction);
					fxc = forceFieldFunction.energyFunction(xc);

					//logger.debug("lambdaa = " + lambdaa + "	");
					//logger.debug("fxa = " + fxa);
					//logger.debug("lambdab = " + lambdab + "	");
					//logger.debug("fxb = " + fxb);
					//logger.debug("lambdac = " + lambdac + "	");
					//logger.debug("fxc = " + fxc);

				}
			}
		} 
		else {
			while (finish == false) {
				
				xc.set(xb);
				lambdac = lambdab;
				fxc = fxb;
				
				lambdab = lambdab / 2;
				xb.set(kPoint);
				direction.set(searchDirection);
				direction.scale(lambdab);
				xb.add(direction);
				fxb = forceFieldFunction.energyFunction(xb);

				//logger.debug("lambdaa = " + lambdaa + "	");
				//logger.debug("fxa = " + fxa);
				//logger.debug("lambdab = " + lambdab + "	");
				//logger.debug("fxb = " + fxb);
				//logger.debug("lambdac = " + lambdac + "	");
				//logger.debug("fxc = " + fxc);

				if (fxb < fxa) {
					finish = true;
				}
				if (lambdab < 0.0000000000000001) {
					finish = true;
				}
			}
		}
		return;
	}


	public double parabolicInterpolation() {
		parabolMinimumLambda = fxa * (Math.pow(lambdac,2) - Math.pow(lambdab,2)) + fxb * (Math.pow(lambdaa,2) - Math.pow(lambdac,2)) + fxc * (Math.pow(lambdab,2) - Math.pow(lambdaa,2));
		parabolMinimumLambda = parabolMinimumLambda / (fxa * (lambdac-lambdab) + fxb * (lambdaa-lambdac) + fxc * (lambdab-lambdaa));
		parabolMinimumLambda = 0.5 * parabolMinimumLambda;
		
		//logger.debug("parabolMinimumLambda = " + parabolMinimumLambda);
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


	/**
	 *  Bracket the minimum and then iterative parabolic interpolation.
	 *  
	 *@param  kPoint              Current point, xk
	 *@param  searchDirection     Search direction
	 *@param  forceFieldFunction  Potential energy function
	 */
	public void setLineSearchLambda(GVector kPoint, GVector searchDirection, PotentialFunction forceFieldFunction) {
		//logger.debug("");
		//logger.debug("Start the line search: ");
		
		direction.setSize(searchDirection.getSize());
		
//		if (lambdab > 0.00000001) {
			bracketingTheMinimum(kPoint, searchDirection, forceFieldFunction);
			if (fxb < fxa & fxb < fxc) {
				GVector xI = new GVector(kPoint.getSize());
				double fxI = 0;
				double fMinimumMinus1 = fxa;
				double fMinimum = fxb;
				
				do {
					parabolicInterpolation();
					xI.set(kPoint);
					direction.set(searchDirection);
					direction.scale(parabolMinimumLambda);
					xI.add(direction);
					
					fxI = forceFieldFunction.energyFunction(xI);
					//logger.debug("fxI = " + fxI);
					
					// Take new 3 points : If fxI > fxb new interval is (lambdaa, lambdab, parabolMinimumLambda), otherwise new interval is (lambdab, parabolMinimumLambda, lambdac)
					if (parabolMinimumLambda > lambdaa & parabolMinimumLambda < lambdac) {
						if (parabolMinimumLambda < lambdab) {
							if (fxI < fxb) {//aIb
								lambdac = lambdab;			fxc = fxb;
								lambdab = parabolMinimumLambda;		fxb = fxI;
							} else {//Ibc
								lambdaa = parabolMinimumLambda;		fxa = fxI;
							}
						} else if (fxI < fxb) {//bIc
							lambdaa = lambdab;			fxa = fxb;
							lambdab = parabolMinimumLambda;		fxb = fxI;
						}else {//abI
							lambdac = parabolMinimumLambda;		fxc = fxI;
						}
						fMinimumMinus1 = fMinimum;
						fMinimum = fxb;
					}
				} while (Math.abs(fMinimum - fMinimumMinus1) > 0.001);
					
					/*if (fxI > fxb) {
						lambdac = parabolMinimumLambda; fxc = fxI;
					} else {
						if (parabolMinimumLambda < lambdab) {
							fxc = fxb; lambdac = lambdab;
							fxb = fxI; lambdab = parabolMinimumLambda;
						} else {
							fxa = fxb; lambdaa = lambdab;
							fxb = fxI; lambdab = parabolMinimumLambda;
						}
					}*/
				lineSearchLambda = lambdab;
			}else {lineSearchLambda = 0;}
		//}else {
		//	goldenSectionSearch(kPoint, searchDirection, forceFieldFunction);
		//}
		
		//logger.debug("xb = " + xb + "	");
		//logger.debug("fxb = " + fxb);
                
		//logger.debug("Successive parabolic interpolations : ");
		
	}
}

