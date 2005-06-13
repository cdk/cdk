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
	double lambdab = 0.5;
	double lambdac = 0;
	double fxa = 0;
	double fxb = 0;
	double fxc = 0;
	double parabolMinimumLambda = 0;
	double lineSearchLambda = 0;
	double lambdabOld = 0;
	private LoggingTool logger;


	/**
	 *  Constructor for the LineSearch object
	 */
	public LineSearch() {        
		logger = new LoggingTool(this);
	}


	/**
	 *  Initially Bracketing a Minimum: Look for 3 points along the line search 
	 *  where the energy of the middle point is lower than the energy of the two outer points.
	 *  (Numerical recipes in C++. The Art of Scientific Computing. Second Edition.)
	 *
	 *@param  kPoint              Current point, xk
	 *@param  searchDirection     Search direction
	 *@param  forceFieldFunction  Potential energy function
	 */
	public void initiallyBracketingAMinimum(GVector kPoint, GVector searchDirection, PotentialFunction forceFieldFunction) {

		//logger.debug("Initially Bracketing a Minimum:");
		
		direction.set(searchDirection);
		lambdaa = 0;	
		if (lambdab < 0.5) {} 
		else {
			lambdab = 0.5;
		}

		double gold = 1.618034;	//Default ratio by which successive intervals are magnified.
		double glimit = 100;	//Maximum magnification allowed for a parabolic-fit step.
		double tiny = 1.0E-20;	//It is used to prevent any possible division by zero.

		double lambdau = 0;
		double r = 0;
		double q = 0;
		double fxu = 0;
		double ulim = 0;

		GVector xa = new GVector(kPoint);	fxa = forceFieldFunction.energyFunction(xa);
		GVector xb = new GVector(kPoint.getSize());	xb.scaleAdd(lambdab, direction, kPoint);	//Sets the value of this vector to the scalar multiplication by s of vector v1 plus vector v2 (this = s*v1 + v2)
		fxb = forceFieldFunction.energyFunction(xb);

		if (fxb >fxa) {
			double lambdat = lambdaa;	GVector xt = new GVector(xa);	double fxt = fxa;
			lambdaa = lambdab;		xa.set(xb);			fxa = fxb;
			lambdab = lambdat;		xb.set(xt);			fxb = fxt;
		}
		
		lambdac = lambdab + gold * (lambdab - lambdaa);
		GVector xc = new GVector(kPoint.getSize());	xc.scaleAdd(lambdac, direction, kPoint);	
		fxc = forceFieldFunction.energyFunction(xc);
		
		double sign = 0;
		GVector xu = new GVector(kPoint.getSize());

		while (fxb > fxc) {
			r = (lambdab-lambdaa) * (fxb-fxc);
			q = (lambdab-lambdac) * (fxb-fxa);
			if (q-r > 0) {sign = 1;}
			else {sign = -1;}
			lambdau = lambdab - ((lambdab-lambdac) * q - (lambdab-lambdaa) * r) / (2.0 * sign * Math.max(Math.abs(q-r),tiny));
			ulim = lambdab + glimit * (lambdac - lambdab);	//We won't go farther than this.
			
			//Test various possibilities:
			if ((lambdab - lambdau) * (lambdau - lambdac) > 0.0) {	//Parabolic u is between b and c
				xu.scaleAdd(lambdau, direction, kPoint); fxu = forceFieldFunction.energyFunction(xu);
				if (fxu < fxc) {	// Got a minimum between b and c
					lambdaa = lambdab;	fxa = fxb;
					lambdab = lambdau;	fxb = fxu;
				} else {
					if (fxu > fxb) {	// Got a minimum between a and u
						lambdac = lambdau;
						fxc = fxu;
					}
				}
				lambdau = lambdac + gold * (lambdac-lambdab);	// Parabolic fit was not use. Use default magnification.
				xu.scaleAdd(lambdau, direction, kPoint); fxu = forceFieldFunction.energyFunction(xu);
			} else if ((lambdac-lambdau) * (lambdau-ulim) > 0.0) {	// Parabolic fit is between c and its allowed limit.
				xu.scaleAdd(lambdau, direction, kPoint); fxu = forceFieldFunction.energyFunction(xu);
				if (fxu < fxc) {
					//shft3(lambdab,lambdac,lambdau,lambdau+gold*(lambdau-lambdac));	//?
					//shft3(fxb,fxc,fxu,func(u));
				}
			} else if (lambdau > ulim) {	// Limit parabolic u to maximum allowed value
				lambdau = ulim;
				xu.scaleAdd(lambdau, direction, kPoint); fxu = forceFieldFunction.energyFunction(xu);
			} else {	// Reject parabolic u, use default magnification.
				lambdau = lambdac + gold * (lambdac-lambdab);
				xu.scaleAdd(lambdau, direction, kPoint); fxu = forceFieldFunction.energyFunction(xu);
			}
			//shft3(lambdaa,lambdab,lambdac,lambdau);	// ? Eliminate oldest point and continue
			//shft3(fxa,fxb,fxc,fxu);
		}
		
		//logger.debug("lambdaa = " + lambdaa);
		//logger.debug("fxa = " + fxa);
		//logger.debug("lambdab = " + lambdab);
		//logger.debug("fxb = " + fxb);
		//logger.debug("lambdac = " + lambdac);
		//logger.debug("fxc = " + fxc);

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

		if (lambdab < 0.5) {} 
		else {
			lambdab = 0.5;
		}
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

			//logger.debug("Brent's exponential search");
					
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
				
				if (fxc >= fxb) {
					finish = true;
				} 
				else {
					xa.set(xb);
					lambdaa = lambdab;
					fxa = fxb;
					
					xb.set(xc);
					lambdabOld = lambdab;
					lambdab = lambdac;
					fxb = fxc;
					
					lambdac = 1.618 * (lambdac-lambdabOld) + lambdac;	// Brent's exponential search
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
					
					if (fxc < 0.0000000000000001) {
						finish = true;
						lineSearchLambda = lambdab;
						//logger.debug("fxb < 0.0000000000000001");
						//logger.debug("lambdaa = " + lambdaa + "	");
						//logger.debug("fxa = " + fxa);
						//logger.debug("lambdab = " + lambdab + "	");
						//logger.debug("fxb = " + fxb);
						//logger.debug("lambdac = " + lambdac + "	");
						//logger.debug("fxc = " + fxc);
					}
				}
			}
		} 
		else {
			//logger.debug("Golden Section Method");
			xc.set(xb);
			lambdac = lambdab;
			fxc = fxb;
			
			double lambdab1 = 0.3819660112 * (lambdac - lambdaa);
			double lambdab2 = 0.6180339887498948482 * (lambdac - lambdaa);
			
			GVector xb1 = new GVector(kPoint);
			direction.set(searchDirection);
			direction.scale(lambdab1);
			xb1.add(direction);
			double fxb1 = forceFieldFunction.energyFunction(xb1);
			
			GVector xb2 = new GVector(kPoint);
			direction.set(searchDirection);
			direction.scale(lambdab2);
			xb2.add(direction);
			double fxb2 = forceFieldFunction.energyFunction(xb2);
			
			//logger.debug("lambdaa = " + lambdaa + "	");
			//logger.debug("fxa = " + fxa);
			//logger.debug("lambdab1 = " + lambdab1 + "	");
			//logger.debug("fxb1 = " + fxb1);
			//logger.debug("lambdab2 = " + lambdab2 + "	");
			//logger.debug("fxb2 = " + fxb2);
			//logger.debug("lambdac = " + lambdac + "	");
			//logger.debug("fxc = " + fxc);
			
			while (finish != true) {
				if (fxb1 <= fxb2) {//we can bracket the minimum by the interval [lambdaa, lambdab2]
					lambdac = lambdab2;	xc.set(xb2);	fxc = fxb2;
					if (fxa > fxb1) {
						finish = true;
						lambdab = lambdab1;	xb.set(xb1);	fxb = fxb1;
					} else {
						lambdab2 = lambdab1;	xb2.set(xb1); 	fxb2 = fxb1;
						lambdab1 = lambdaa + 0.3819660112 * (lambdac - lambdaa);
						xb1.set(kPoint);	direction.set(searchDirection);		direction.scale(lambdab1);
						xb1.add(direction);	fxb1 = forceFieldFunction.energyFunction(xb1);
					}
				} else {//we can bracket the minimum by the interval [lambdab1, lambdac]
					if (fxa > fxb1) {
						lambdaa = lambdab1;	xa.set(xb1);	fxa = fxb1;
						if (fxb2 < fxc) {
							finish = true;
							lambdab = lambdab2;	xb.set(xb2);	fxb = fxb2;
						} else {
							lambdab1 = lambdab2;	xb1.set(xb2); 	fxb1 = fxb2;
							lambdab2 = lambdaa + 0.6180339887498948482 * (lambdac - lambdaa);
							xb2.set(kPoint);	direction.set(searchDirection);		direction.scale(lambdab2);
							xb2.add(direction);	fxb2 = forceFieldFunction.energyFunction(xb2);
						}
					} else {//we can bracket the minimum by the interval [lambdaa, lambdab2]
						lambdac = lambdab2;	xc.set(xb2);	fxc = fxb2;
						lambdab2 = lambdab1;	xb2.set(xb1); 	fxb2 = fxb1;
						lambdab1 = lambdaa + 0.3819660112 * (lambdac - lambdaa);
						xb1.set(kPoint);	direction.set(searchDirection);		direction.scale(lambdab1);
						xb1.add(direction);	fxb1 = forceFieldFunction.energyFunction(xb1);
					}
				}
				if (Math.abs(fxc-fxa) < 0.000000000000001) {
					finish = true;
					logger.debug("fxc-fxa < 0.00000000001");
					if (fxb1 < fxb2) {lineSearchLambda = lambdab1;}
					else {lineSearchLambda = lambdab2;}
				}
				if (lambdab1 < 0.0000000000000001) {
					finish = true;
					logger.debug("lambdab < 0.0000000000000001");
					lineSearchLambda = lambdaa;
					//logger.debug("lambdaa = " + lambdaa + "	");
					//logger.debug("fxa = " + fxa);
					//logger.debug("lambdab = " + lambdab1 + "	");
					//logger.debug("fxb = " + fxb1);
					//logger.debug("lambdac = " + lambdac + "	");
					//logger.debug("fxc = " + fxc);
				}

				//logger.debug(" ");
				//logger.debug("lambdaa= " + lambdaa + " ; fxa = " + fxa);
				//logger.debug("lambdab1= " + lambdab1 + " ; fxb1 = " + fxb1);
				//logger.debug("lambdab2= " + lambdab2 + " ; fxb2 = " + fxb2);
				//logger.debug("lambdac= " + lambdac + " ; fxc = " + fxc);
				//logger.debug("finish = " + finish);
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
		return lineSearchLambda;
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
		
		bracketingTheMinimum(kPoint, searchDirection, forceFieldFunction);
		if (fxb < fxa & fxb < fxc) {
			if (Math.abs(fxc-fxa)/Math.abs(lambdac-lambdaa) < 1) {
				goldenSectionMethod(kPoint, searchDirection, forceFieldFunction);
			} else {
				//logger.debug("Quadratic interpolation");
				
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
			}
		}
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
	public void goldenSectionMethod(GVector kPoint, GVector searchDirection, PotentialFunction forceFieldFunction) {

		logger.debug("Golden Section Search");
		
		double lambda1 = 0;		GVector x1 = new GVector(kPoint);	double fx1 = forceFieldFunction.energyFunction(x1);
		double lambda4 = lambdac;
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
			if (fx4-fx1 < 0.000000001) {
				finish = true;
				if (fx2 < fx3) {lineSearchLambda = lambda2;}
				else {lineSearchLambda = lambda3;}
			}
			//logger.debug();
			//logger.debug("lambda1= " + lambda1 + " ; fx1 = " + fx1);
			//logger.debug("lambda2= " + lambda2 + " ; fx2 = " + fx2);
			//logger.debug("lambda3= " + lambda3 + " ; fx3 = " + fx3);
			//logger.debug("lambda4= " + lambda4 + " ; fx4 = " + fx4);
			//logger.debug("finish = " + finish);
		}
		return;
	}


}

