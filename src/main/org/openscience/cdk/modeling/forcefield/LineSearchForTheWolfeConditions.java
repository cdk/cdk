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

import javax.vecmath.GVector;

//import org.openscience.cdk.tools.LoggingTool;
/**
 * 
 *
 * @author     vlabarta
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 * 
 */
public class LineSearchForTheWolfeConditions {
	
	//initial values
	private GVector x = null;
	private double linearFunctionInAlpha0;
	private GVector dfx = null;
	private GVector direction = null;
	private double linearFunctionDerivativeInAlpha0;
	private IPotentialFunction pf = null;
	private double alphaInitialStep;

	
	//line search algorithm
	private double[] alpha = new double[3];
	private double[] linearFunctionInAlpha = new double[3];
	private double[] linearFunctionDerivativeInAlpha = new double[3];

	private GVector[] dfInAlpha = new GVector[3];
	private double[] brentStep = new double[3];

	private final double c1 = 0.0001;
	private double c2;
	
	//private double linearFunctionGoldenAlpha;
	private double linearFunctionAlphaInterpolation;
	
	public boolean derivativeSmallEnough = true;

	public double alphaOptimum;
	public double linearFunctionInAlphaOptimum;
	public GVector dfOptimum = null;
	
	//zoom
	private double alphaj;
	private double linearFunctionInAlphaj;
	private double linearFunctionDerivativeInAlphaj;
	private GVector dfInAlphaj;
	private int functionEvaluationNumber;
	
	//energy function evaluation
	private GVector xAlpha = null;

	//interpolation
	private double a;
	private double b;
	
	//cubic interpolation
	private double alphaTemporal;
	private double linearFunctionInAlphaTemporal;
	private double linearFunctionDerivativeInAlphaTemporal;
	private double d1;
	private double d2;
	private double alphaiplus1;
	
	//private LoggingTool logger;

	
	public LineSearchForTheWolfeConditions(IPotentialFunction pfUser, String method) {
		this.pf = pfUser;
		if ((method == "sdm") | (method == "cgm")) {c2 = 0.07;}
		else {c2 = 0.9;} 
	}
	
	public void initialize(GVector xUser, double fxUser, GVector dfxUser, GVector directionUser, double linearFunctionDerivativeUser, double alphaInitialStepUser) {
		this.x = xUser;
		this.linearFunctionInAlpha0 = fxUser;
		this.dfx = dfxUser;
		//logger.debug("derivativeSmallEnough = " + this.derivativeSmallEnough);
		this.direction = directionUser;
		this.linearFunctionDerivativeInAlpha0 = linearFunctionDerivativeUser;
		//logger.debug("linearFunctionDerivativeInAlpha0 = " + linearFunctionDerivativeInAlpha0);
		this.alphaOptimum = 0;
		this.linearFunctionInAlphaOptimum = linearFunctionInAlpha0;
		dfOptimum = this.dfx;
		this.alphaInitialStep = alphaInitialStepUser;
		this.derivativeSmallEnough = false;
		this.xAlpha = new GVector(x.getSize());
	}
	
	 /*	Line Search Algorithm for the Wolfe conditions. Jorge Nocedal and Stephen J.Wright. Numerical Optimization. 1999.
	 * The algorithm has two stages. This first stage begins with a trial estimate alpha1, 
	 * and keeps increasing it until it finds either an acceptable step length or an interval 
	 * that brackets the desired step lengths. In the later case, the second stage is invoked 
	 * by calling a function called zoom, which successively decreases the size of the interval 
	 * until an acceptable step length is identified. 
	 *
 	 * @param alphaMax				Maximum step length
	 */
	public void lineSearchAlgorithm (double alphaMax) {
		
		//logger.debug("Line search for the strong wolfe conditions");

		alpha[0] = 0.0;
		linearFunctionInAlpha[0] = linearFunctionInAlpha0; 
		linearFunctionDerivativeInAlpha[0] = linearFunctionDerivativeInAlpha0;	//To Analyse the possibility of eliminate linearFunctionDerivativeInAlpha[0]
		dfInAlpha[0] = this.dfx;
		
		alpha[1] = this.alphaInitialStep;
		
		//logger.debug("alpha[1] = this.alphaInitialStep = " + alpha[1]);
		
		brentStep[0] = alpha[0];
		brentStep[1] = alpha[1];
		
		int i=1;
		
		this.functionEvaluationNumber = 0;

		if (alpha[1] > alphaMax) {
			alpha[1] = alphaMax;
			//logger.debug("line search algorithm error: alphaInitialStep > alphaMax");
		}
		//	alpha[1] = alphaMax/2;
		//}

		try {
		do {

			if (alpha[1] == 0) {
				System.out.println("alpha[1] == 0");
				break;
			}
			
			//logger.debug("alpha[" + i + "] = " + alpha[i]);
			linearFunctionInAlpha[i] = evaluateEnergyFunction(alpha[i]);
			//logger.debug("linearFunctionInAlpha[" + i + "] = " + linearFunctionInAlpha[i]);
			
			if ((linearFunctionInAlpha[i] > linearFunctionInAlpha[0] + c1 * alpha[i] * linearFunctionDerivativeInAlpha[0]) | 
					((linearFunctionInAlpha[i] >= linearFunctionInAlpha[i-1]) & (i>1))) {			//The interval alpha[i-1] and alpha[i] brackets the desired step lengths.
				//logger.debug("zoom(" + alpha[i-1] + ", " + linearFunctionInAlpha[i-1] + ", " + linearFunctionDerivativeInAlpha[i-1] + ", " + dfInAlpha[i-1] + ", " + alpha[i] + ", " + linearFunctionInAlpha[i] + ")");
				//dfInAlpha[i] = evaluateEnergyFunctionDerivative(alpha[i]);
				//linearFunctionDerivativeInAlpha[i] = dfInAlpha[i].dot(direction);
				//zoom(alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i-1], dfInAlpha[i-1], alpha[i], linearFunctionInAlpha[i], linearFunctionDerivativeInAlpha[i], dfInAlpha[i]);
				zoom(alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i-1], dfInAlpha[i-1], alpha[i], linearFunctionInAlpha[i]);
				break;
			} 

			//The first strong Wolfe condition is satisfied for alpha[i].
			dfInAlpha[i] = evaluateEnergyFunctionDerivative(alpha[i]);
			//logger.debug("dfOptimum = " + dfOptimum);
			linearFunctionDerivativeInAlpha[i] = dfInAlpha[i].dot(direction);
			//logger.debug("linearFunctionDerivativeInAlpha[" + i + "] = " + linearFunctionDerivativeInAlpha[i]);
			
			if (Math.abs(linearFunctionDerivativeInAlpha[i]) <= -c2 * linearFunctionDerivativeInAlpha[0]) { //The second strong Wolfe condition is also satisfied for alpha[i]
				//logger.debug("The second strong Wolfe condition is also satisfied for " + alpha[i]);
				alphaOptimum = alpha[i];
				linearFunctionInAlphaOptimum = linearFunctionInAlpha[i];
				dfOptimum = dfInAlpha[i];
				//logger.debug("alphaOptimun = " + alphaOptimum);
				//logger.debug("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
				//logger.debug("dfOptimum = " + dfOptimum);
				this.derivativeSmallEnough = true;
				break;
			}
			
			if (linearFunctionDerivativeInAlpha[i] >= 0) {		//The interval alpha[i-1] and alpha[i] brackets the desired step lengths.
				/*System.out.println("zoom(" + alpha[i-1] + ", " + linearFunctionInAlpha[i-1] + ", " + linearFunctionDerivativeInAlpha[i-1] + ", " + dfInAlpha[i-1] + ", " + 
						alpha[i] + ", " + linearFunctionInAlpha[i] + ")");*/
				
				/*zoom(alpha[i], linearFunctionInAlpha[i], linearFunctionDerivativeInAlpha[i], dfInAlpha[i], 
						alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i], dfInAlpha[i]);*/
				zoom(alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i-1], dfInAlpha[i-1], 
						alpha[i], linearFunctionInAlpha[i]);
				break;
			}
		
			if (alpha[i] == alphaMax) {	
				//logger.debug("LINE SEARCH ALGORITHM WAS TERMINATE EARLIER BECAUSE alpha[i] == alphaMax");
				alphaOptimum = alpha[i];
				linearFunctionInAlphaOptimum = linearFunctionInAlpha[i];
				dfOptimum = dfInAlpha[i];
				//logger.debug("alphaOptimun = " + alphaOptimum);
				//logger.debug("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
				//logger.debug("dfOptimum = " + dfOptimum);
				break;
			}
			
			functionEvaluationNumber = functionEvaluationNumber + 1;
			if (functionEvaluationNumber == 10) {
				//logger.debug("LINE SEARCH ALGORITHM WAS TERMINATE EARLIER BECAUSE functionEvaluationNumber == 10");
				alphaOptimum = alpha[i];
				linearFunctionInAlphaOptimum = linearFunctionInAlpha[i];
				dfOptimum = dfInAlpha[i];
				//logger.debug("alphaOptimun = " + alphaOptimum);
				//logger.debug("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
				//logger.debug("dfOptimum = " + dfOptimum);
				break;
			}
			
			if (i>1) {
				brentStep[0] = brentStep[1];
				brentStep[1] = brentStep[2];

				alpha[1] = alpha[2];
				linearFunctionInAlpha[1] = linearFunctionInAlpha[2];
				linearFunctionDerivativeInAlpha[1] = linearFunctionDerivativeInAlpha[2];
				dfInAlpha[1] = dfInAlpha[2];
			} 
			
			brentStep[2] = brentStep[1] + 1.618 * (brentStep[1]-brentStep[0]);
			//logger.debug("brentStep[2] = " + brentStep[2]);

			if (brentStep[2] > alphaMax) {brentStep[2] = alphaMax;}
			/*linearFunctionInBrentStep = this.evaluateEnergyFunction(brentStep[2]);
			linearFunctionDerivativeInBrentStep = this.evaluateEnergyFunctionDerivative(brentStep[2]).dot(direction);
			*/
			alpha[2] = brentStep[2];
			/*alpha[2] = this.cubicInterpolation(alpha[1], linearFunctionInAlpha[1], linearFunctionDerivativeInAlpha[1], 
					brentStep[2], linearFunctionInBrentStep, linearFunctionDerivativeInBrentStep, alpha[1], brentStep[2]);
					*/

			i=2;
			
		} while ((alpha[2] <= alphaMax) & (alpha[1] < alpha[2]) & (functionEvaluationNumber < 10));
		
		} catch (Exception exception) {
        	System.out.println("Line search for the strong wolfe conditions: " + exception.getMessage());
        	System.out.println(exception);
        }
		

	}
	

	 /*	Each iteration of zoom generates an iterate alphaj between alphaLow and alphaHigh, 
	 * and then replaces one of these endpoints by alphaj in such a way that the properties 
	 * (a), (b) and (c) continue to hold.
	 * (a)The interval bounded by alphaLow and alphaHigh contains step lengths that satisfy the strong Wolfe conditions.  
     * (b)alphaLow is, among all step lengths generated so far and satisfying the sufficient decrease condition, 
     * the one giving the smallest function value.
     * (c)alphaHigh is chosen so that linearFunctionDerivativeInAlphaj * (alphaHigh-alphaLow) < 0
     *   
   	 *@param  alphaLow              				Among all step lengths generated so far and satisfying the sufficient decrease condition, the one giving the smallest function value.
   	 *@param  linearFunctionInAlphaLow       		Function value at alphaLow.
   	 *@param  linearFunctionDerivativeInAlphaLow	Derivative value at alphaLow.
   	 *@param  dfInAlphaLow              			Gradient at alphaLow.
   	 *@param  alphaHigh              				AlphaHigh is chosen so that linearFunctionDerivativeInAlphaj * (alphaHigh-alphaLow) < 0
   	 *@param  linearFunctionInAlphaHigh             Function value at alphaHigh.
	 */
	private void zoom (double alphaLow, double linearFunctionInAlphaLow, double linearFunctionDerivativeInAlphaLow, GVector dfInAlphaLow, 
			double alphaHigh, double linearFunctionInAlphaHigh) {
		
		//logger.debug("zoom");
		
		functionEvaluationNumber = 0;
		
		/*double a;
		double b;
		if (alphaLow < alphaHigh) {a = alphaLow; b = alphaHigh;}
		else {a = alphaHigh; b = alphaLow;}
		*/
		
		do {
			//Interpolation 
			
			//alphaj = this.cubicInterpolation(alphaLow, linearFunctionInAlphaLow, linearFunctionDerivativeInAlphaLow, alphaHigh, linearFunctionInAlphaHigh, linearFunctionDerivativeInAlphaHigh, a, b);
			/*System.out.println("interpolation(" + alphaLow + ", " + linearFunctionInAlphaLow + ", " + linearFunctionDerivativeInAlphaLow + ", "
					+ alphaHigh + ", " + linearFunctionInAlphaHigh + ");");*/

			alphaj = this.interpolation(alphaLow, linearFunctionInAlphaLow, linearFunctionDerivativeInAlphaLow, alphaHigh, linearFunctionInAlphaHigh);
			//logger.debug("alphaj = " + alphaj);
			linearFunctionInAlphaj = this.linearFunctionAlphaInterpolation;
			//logger.debug("linearFunctionInAlphaj = " + linearFunctionInAlphaj);
			
			if ((linearFunctionInAlphaj > linearFunctionInAlpha0 + c1 * alphaj * linearFunctionDerivativeInAlpha0) | //The interval 0 and alphaj brackets the desired step lengths.
					(linearFunctionInAlphaj >= linearFunctionInAlphaLow)) {			

				//logger.debug("The minimum is between alpha1 and alphaj");
				alphaHigh = alphaj;
				linearFunctionInAlphaHigh = linearFunctionInAlphaj;
				//dfInAlphaHigh = this.evaluateEnergyFunctionDerivative(alphaHigh); 
				//linearFunctionDerivativeInAlphaHigh = dfInAlphaHigh.dot(direction);
			} 
			else {
				dfInAlphaj = evaluateEnergyFunctionDerivative(alphaj);
				linearFunctionDerivativeInAlphaj = dfInAlphaj.dot(direction);
				//logger.debug("linearFunctionDerivativeInAlphaj = " + linearFunctionDerivativeInAlphaj);
				if (Math.abs(linearFunctionDerivativeInAlphaj) <= -c2 * linearFunctionDerivativeInAlpha0) { //alphaj satisfied the second strong Wolfe condition.
					//logger.debug("Derivative small enough : " + Math.abs(linearFunctionDerivativeInAlphaj) + " <= " + (-c2 * linearFunctionDerivativeInAlpha0));
					this.derivativeSmallEnough = true;
					alphaOptimum = alphaj;
					linearFunctionInAlphaOptimum = linearFunctionInAlphaj;
					dfOptimum = dfInAlphaj;
					//logger.debug("alphaOptimun = " + alphaOptimum);
					//logger.debug("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
					break;
				}
				if (linearFunctionDerivativeInAlphaj * (alphaHigh-alphaLow) >= 0) {		
					alphaHigh = alphaLow;
					linearFunctionInAlphaHigh = linearFunctionInAlphaLow;
					//linearFunctionDerivativeInAlphaHigh = linearFunctionDerivativeInAlphaLow;
				}
				alphaLow = alphaj;
				linearFunctionInAlphaLow = linearFunctionInAlphaj;
				linearFunctionDerivativeInAlphaLow = linearFunctionDerivativeInAlphaj;
				dfInAlphaLow = dfInAlphaj;
			}
			
			//logger.debug("AlphaLow = " + alphaLow + ", AlphaHigh = " + alphaHigh);
			//logger.debug("linearFunctionInAlphaLow = " + linearFunctionInAlphaLow + ", linearFunctionInAlphaHigh = " + linearFunctionInAlphaHigh);
			functionEvaluationNumber = functionEvaluationNumber + 1;
			//logger.debug("functionEvaluationNumber = " + functionEvaluationNumber);

			if ((functionEvaluationNumber == 10) | (Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) <= 0.000001) | (Math.abs(alphaLow - alphaHigh) <= 0.000000000001)) {
				//logger.debug("ZOOM WAS TERMINATE EARLIER");
				/*System.out.println("functionEvaluationNumber = " + functionEvaluationNumber + 
						", Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) = " + Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) + 
						", Math.abs(alphaLow - alphaHigh) = " + Math.abs(alphaLow - alphaHigh));*/
				this.alphaOptimum = alphaLow;
				this.linearFunctionInAlphaOptimum = linearFunctionInAlphaLow;
				this.dfOptimum = dfInAlphaLow;
				
				//logger.debug("(functionEvaluationNumber == 10) | (Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) <= 0.000001) | (Math.abs(alphaLow - alphaHigh) <= 0.0000001)");
				//logger.debug("zoom end -> this.alphaOptimum = " + this.alphaOptimum); 
				//logger.debug("zoom end -> this.linearFunctionInAlphaOptimum = " + this.linearFunctionInAlphaOptimum); 
				break;
			}

		
		} while ((Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) > 0.000001) 
					& (functionEvaluationNumber < 10) 
					& (Math.abs(alphaLow - alphaHigh) > 0.000000000001));
		
		//logger.debug("zoom end");
		return;
	}

	
	 /*
	 * Cubic interpolation in the interval [a,b] known to contain desirable step length 
	 * and given two previous step length estimates in this interval.
	 *
	 *@param 	alphai										Previous step length.									
	 *@param 	linearFunctionInAlphai						Function value at the previous step length alphai.
	 *@param 	linearFunctionDerivativeInAlphai			Derivative at the previous step length alphai.
	 *@param 	alphaiMinus1								Previous step length.
	 *@param 	linearFunctionInAlphaiMinus1	 			Function value at the previous step length alphaiMinus1.
	 *@param 	linearFunctionDerivativeInAlphaiMinus1	 	Derivative value at the previous step length alphaiMinus1.
	 *@param 	a											Inferior value of the interval [a,b].
	 *@param 	b											Superior value of the interval [a,b].	
 	 *	  
	 * @return												Cubic interpolation in the interval [a,b]
	 */
	public double cubicInterpolation(double alphai, double linearFunctionInAlphai, double linearFunctionDerivativeInAlphai, 
									double alphaiMinus1, double linearFunctionInAlphaiMinus1, double linearFunctionDerivativeInAlphaiMinus1, 
									double a, double b) {
		
		//logger.debug("The interval [" + a + ", " + b + "] contains acceptable step lengths.");
		
		if (alphai < alphaiMinus1) {
			this.alphaTemporal = alphai;
			this.linearFunctionInAlphaTemporal = linearFunctionInAlphai;
			this.linearFunctionDerivativeInAlphaTemporal = linearFunctionDerivativeInAlphai;
			alphai = alphaiMinus1;
			linearFunctionInAlphai = linearFunctionInAlphaiMinus1;
			linearFunctionDerivativeInAlphai = linearFunctionDerivativeInAlphaiMinus1;
			alphaiMinus1 = this.alphaTemporal;
			linearFunctionInAlphaiMinus1 = this.linearFunctionInAlphaTemporal;
			linearFunctionDerivativeInAlphaiMinus1 = this.linearFunctionDerivativeInAlphaTemporal;
		}
		
		this.d1 = linearFunctionDerivativeInAlphaiMinus1 + linearFunctionDerivativeInAlphai - 3 * ((linearFunctionInAlphaiMinus1 - linearFunctionInAlphai)/(alphaiMinus1 - alphai));
		//logger.debug("d1 = " + d1);
		
		//logger.debug("linearFunctionDerivativeInAlphaiMinus1 = " + linearFunctionDerivativeInAlphaiMinus1);
		//logger.debug("linearFunctionDerivativeInAlphai = " + linearFunctionDerivativeInAlphai);
		
		this.d2 = Math.sqrt(Math.abs(Math.pow(d1,2) - linearFunctionDerivativeInAlphaiMinus1 * linearFunctionDerivativeInAlphai));
		//logger.debug("d2 = " + d2);
		
		this.alphaiplus1 = alphai-(alphai-alphaiMinus1) * ((linearFunctionDerivativeInAlphai + d2 - d1) / (linearFunctionDerivativeInAlphai - linearFunctionDerivativeInAlphaiMinus1 + 2 * d2));
		
		//logger.debug("alphaiplus1 = " + alphaiplus1);
		
		if (alphaiplus1 < a) {alphaiplus1 = a;}
		if (alphaiplus1 > b) {alphaiplus1 = b;}
		
		//logger.debug("alphaiplus1 = " + alphaiplus1);
		
		if (Math.abs(alphaiplus1 - alphai) < 0.000000001) {
			/*System.out.println("We reset alphaiplus1 = (alphaiMinus1 + alphai) / 2, because alphaiplus1 = " + alphaiplus1 + " is too close to its predecessor " +
					"alphaiMinus1 = " + alphaiMinus1); */
			alphaiplus1 = (alphaiMinus1 + alphai) / 2;
		} else {if (alphaiplus1 < (alphai - 9 * (alphai-alphaiMinus1) / 10)) {
			//logger.debug("We reset alphaiplus1 = (alphaiMinus1 + alphai) / 2, because alphaiplus1 = " + alphaiplus1 + " is 	too much smaller than alphai = " + alphai); 
			alphaiplus1 = (alphaiMinus1 + alphai) / 2;;
			}
		}
	
		return alphaiplus1;
	}
	

	 /*
	 *	The aim is to find a value of alpha that satisfies the sufficient decrease condition, without being too small.
	 *	The procedures generate a value alphai such that is not too much smaller than its predecesor alphai-1. 
	 *  The interpolation in the first is quadratic but if the sufficient decrease condition is not satisfied 
	 *  then the interpolation is cubic.
 	 *
	 * @param alphaLow								Among all step lengths generated so far and satisfying the sufficient decrease condition, the one giving the smallest function value.
	 * @param linearFunctionInAlphaLow				Energy function value at alphaLow.
	 * @param linearFunctionDerivativeInAlphaLow	Derivative value at alphaLow.
	 * @param alphaHigh								AlphaHigh is chosen so that linearFunctionDerivativeInAlphaj * (alphaHigh-alphaLow) < 0
	 * @param linearFunctionInAlphaHigh				Energy function value at alphaHigh.
	 * @return										Value of alpha that satisfies the sufficient decrease condition, without being too small.
	 */
	private double interpolation(double alphaLow, double linearFunctionInAlphaLow, double linearFunctionDerivativeInAlphaLow, 
								double alphaHigh, double linearFunctionInAlphaHigh) {
		
		double minAlpha = Math.min(alphaLow, alphaHigh);
		double alphaDiff = Math.abs(alphaHigh - alphaLow);
		double alphaInterpolation;

		//logger.debug("We form a quadratic approximation to the linear function");
		double alpha1 = -1 * ((linearFunctionDerivativeInAlphaLow * Math.pow(alphaDiff,2)) / (2 * (linearFunctionInAlphaHigh - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alphaDiff)));
		
		//logger.debug("The value alpha1 = " + alpha1 + ", is the minimizer of this quadratic function");
		
		if ((alpha1 > alphaDiff) | (Math.abs(alpha1 - alphaDiff) < 0.000000001)) {
			if (alpha1 < 1E-7) {}
			else {
				/*System.out.println("We reset alpha1 = alphaDiff / 2, because alphaInterpolation = " + alpha1 + " is too close to its predecessor " +
						"alphaiMinus1 = " + alphaDiff); */
				alpha1 = alphaDiff / 2;
			}
		} else {
			if ((alpha1 < 0) & (alpha1 < (alphaDiff - 9 * alphaDiff / 10))) {
				if (alpha1 < 1E-7) {}
				else {
					//logger.debug("We reset alphai = alphaiMinus1 / 2, because alphaInterpolation = " + alpha1 + " is 	too much smaller than alphaiMinus1 = " + alphaDiff); 
					alpha1 = alphaDiff / 2;
				}
			}
		}

		//logger.debug("alpha1 = " + alpha1);

		alphaInterpolation = minAlpha + alpha1;
		this.linearFunctionAlphaInterpolation = this.evaluateEnergyFunction(alphaInterpolation);
		//logger.debug("alphaInterpolation = " + alphaInterpolation);
		//logger.debug("linearFunctionAlphaInterpolation = " + this.linearFunctionAlphaInterpolation);
		if (this.linearFunctionAlphaInterpolation <= this.linearFunctionInAlpha0 + this.c1 * (alphaInterpolation) * this.linearFunctionDerivativeInAlpha0) {
			//logger.debug("The sufficient decrease condition is satisfied at alpha1 and we termine the interpolation");
		}
		else {
			//double alphaiMinus2;
			//double alphaiMinus1 = alphaDiff;
			//double linearFunctionInAlphaiMinus2;
			//double linearFunctionInAlphaiMinus1 = linearFunctionInAlphaHigh;
			double alphai; // = alpha1;
			//double linearFunctionInAlphai = this.linearFunctionAlphaInterpolation;
				
			//do {
				//alphaiMinus2 = alphaiMinus1;
				//alphaiMinus1 = alphai;
				//linearFunctionInAlphaiMinus2 = linearFunctionInAlphaiMinus1;
				//linearFunctionInAlphaiMinus1 = linearFunctionInAlphai;
					
				//logger.debug("We construct a cubic function that interpolates the fours pieces of information");	
				a = 1/(Math.pow(alphaDiff,2) * Math.pow(alpha1, 2) * (alpha1-alphaDiff));
				b = a;
				a = a * (Math.pow(alphaDiff,2) * (this.linearFunctionAlphaInterpolation - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alpha1) 
						+ (-Math.pow(alpha1,2)) * (linearFunctionInAlphaHigh - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alphaDiff));
				b = b * (- Math.pow(alphaDiff,3) * (this.linearFunctionAlphaInterpolation - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alpha1) 
						+ Math.pow(alpha1,3) * (linearFunctionInAlphaHigh - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alphaDiff));
				
				//logger.debug("a = " + a);
				//logger.debug("b = " + b);
				
				alphai = (-b + Math.sqrt(Math.pow(b,2) - 3 * a * linearFunctionDerivativeInAlphaLow)) / (3 * a);
				//logger.debug("alphai = " + alphai);
				
				if (Math.abs(alphai - alpha1) < 0.000000001) {
					/*System.out.println("We reset alphai = alpha1 / 2, because alphaInterpolation = " + alphai + " is too close to its predecessor " +
							"alpha1 = " + alpha1); */
					alphai = alpha1 / 2;
				} else {if (alphai < (alpha1 - 9 * alpha1 / 10)) {
					//logger.debug("We reset alphai = alpha1 / 2, because alphaInterpolation = " + alphai + " is 	too much smaller than alpha1 = " + alpha1); 
					alphai = alpha1 / 2;
					}
				}
			
				alphaInterpolation = minAlpha + alphai;
				this.linearFunctionAlphaInterpolation = this.evaluateEnergyFunction(alphaInterpolation);
				//logger.debug("alphaInterpolation = " + alphaInterpolation);
				//logger.debug("linearFunctionAlphaInterpolation = " + this.linearFunctionAlphaInterpolation);
				//functionEvaluationNumber = functionEvaluationNumber + 1;
				
			/*} while (((linearFunctionInAlphai > linearFunctionInAlphaLow + this.c1 * (alphaLow + alphai) * linearFunctionDerivativeInAlphaLow) & (functionEvaluationNumber < 5)) 
					| ((linearFunctionInAlphai - this.linearFunctionAlphaInterpolation) < 0.00000001) | ((alphai - alpha1) < 0.00000001));*/
				
				
		}
			
		return alphaInterpolation;
	}
	

	/**Evaluate the energy function from an alpha value, using the current coordinates and the current direction.
	 * 
	 * @param alpha	
	 * @return			Energy function value.
	 */
	private double evaluateEnergyFunction(double alpha) {
		//logger.debug("alpha= " + alpha);
		this.xAlpha.set(this.x);
		//logger.debug("xAlpha = " + xAlpha);
		GVector directionStep = direction;
		//logger.debug("directionStep = " + directionStep);
		xAlpha.scaleAdd(alpha, directionStep, xAlpha);
		//logger.debug("xAlpha = " + xAlpha);
		double fxAlpha = pf.energyFunction(xAlpha);
		//logger.debug("fxAlpha = " + fxAlpha);
		return fxAlpha;
	}
	
	
	/**Evaluate the gradient of the energy function from an alpha value, 
	 * using the current coordinates and the current direction.
	 * 
	 * @param alpha		Alpha value for the one-dimensional problem generate from the current coordinates and the current direction.
	 * @return				Gradient of the energy function at alpha. 
	 */
	private GVector evaluateEnergyFunctionDerivative(double alpha) {
		//logger.debug("alpha= " + alpha);
		this.xAlpha.set(this.x);
		//logger.debug("xAlpha = " + xAlpha);
		GVector directionStep = direction;
		//logger.debug("directionStep = " + directionStep);
		xAlpha.scaleAdd(alpha, directionStep, xAlpha);
		//logger.debug("xAlpha = " + xAlpha);
		pf.setEnergyGradient(xAlpha);
		GVector dfxAlpha = pf.getEnergyGradient();
		//logger.debug("dfxAlpha = " + dfxAlpha);
		return dfxAlpha;
	}

	
	/**
	 *	From the interval [a, b] that bracket the minimum, evaluates the energy function at an intermediate point x 
	 *  and obtain a new, smaller bracketing interval, either (a,x) or (x,b).	 
	 *
	 * @param lambdaMin		a
	 * @param flambdaMin		Energy function at a.
	 * @param lambdaMax		b
	 * @param flambdaMax		Energy function at b.
	 * @return					An intermediate point x
	 */
	/*private double goldenSectionMethod(double lambdaMin, double flambdaMin, double lambdaMax, double flambdaMax) {

		//logger.debug("Golden Section Search");
		double goldenLambda;
		
		double lambda1 = lambdaMin;
		double lambda4 = lambdaMax;
		double lambda2 = lambda1 + 0.3819660112 * (lambda4 - lambda1);
		double lambda3 = lambda1 + 0.6180339887498948482 * (lambda4 - lambda1);
		
		//double flambda1 = flambdaMin;
		double flambda2 = evaluateEnergyFunction(lambda2);
		double flambda3 = evaluateEnergyFunction(lambda3);
		//double flambda4 = flambdaMax;
		
		//logger.debug("lambda1 = " + lambda1 + ", flambda1 = " + flambda1);
		//logger.debug("lambda2 = " + lambda2 + ", flambda2 = " + flambda2);
		//logger.debug("lambda3 = " + lambda3 + ", flambda3 = " + flambda3);
		//logger.debug("lambda4 = " + lambda4 + ", flambda4 = " + flambda4);

		if (flambda2 < flambda3) {		//we can bracket the minimum by the interval [lambda1, lambda3]
			goldenLambda = lambda2;
			linearFunctionGoldenAlpha = flambda2;
		}	
		else {			//we can bracket the minimum by the interval [lambda2, lambda4]
			goldenLambda = lambda3;
			linearFunctionGoldenAlpha = flambda3;
		}

		return goldenLambda;
	}*/


}
