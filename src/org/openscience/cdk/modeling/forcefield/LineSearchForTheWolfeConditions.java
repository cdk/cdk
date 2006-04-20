package org.openscience.cdk.modeling.forcefield;

import javax.vecmath.GVector;

//import org.openscience.cdk.tools.LoggingTool;
/**
 * 
 *
 * @author     vlabarta
 *@cdk.module     forcefield
 * 
 */
public class LineSearchForTheWolfeConditions {
	
	//initial values
	GVector x = null;
	double linearFunctionInAlpha0;
	GVector dfx = null;
	GVector direction = null;
	double linearFunctionDerivativeInAlpha0;
	IPotentialFunction pf = null;
	double alphaInitialStep;

	
	//line search algorithm
	double[] alpha = new double[3];
	double[] linearFunctionInAlpha = new double[3];
	double[] linearFunctionDerivativeInAlpha = new double[3];

	GVector[] dfInAlpha = new GVector[3];
	double[] brentStep = new double[3];

	double c1 = 0.0001;
	double c2 = 0.1;		//Important to implement: 0.1 for conjugate gradient method and 0.9 for Newton-Raphson method

	double linearFunctionGoldenAlpha;
	double linearFunctionAlphaInterpolation;
	boolean derivativeSmallEnough = true;

	double alphaOptimum;
	double linearFunctionInAlphaOptimum;
	GVector dfOptimum = null;
	
	//zoom
	double alphaj;
	double linearFunctionInAlphaj;
	double linearFunctionDerivativeInAlphaj;
	GVector dfInAlphaj;
	int functionEvaluationNumber;
	
	//energy function evaluation
	GVector xAlpha = null;

	//interpolation
	double a;
	double b;
	
	//cubic interpolation
	double alphaTemporal;
	double linearFunctionInAlphaTemporal;
	double linearFunctionDerivativeInAlphaTemporal;
	double d1;
	double d2;
	double alphaiplus1;
	
	//private LoggingTool logger;

	
	public LineSearchForTheWolfeConditions(IPotentialFunction pfUser) {
		this.pf = pfUser;
	}
	
	public void initialize(GVector xUser, double fxUser, GVector dfxUser, GVector directionUser, double linearFunctionDerivativeUser, double alphaInitialStepUser) {
		this.x = xUser;
		this.linearFunctionInAlpha0 = fxUser;
		this.dfx = dfxUser;
		//System.out.println("derivativeSmallEnough = " + this.derivativeSmallEnough);
		this.direction = directionUser;
		this.linearFunctionDerivativeInAlpha0 = linearFunctionDerivativeUser;
		//System.out.println("linearFunctionDerivativeInAlpha0 = " + linearFunctionDerivativeInAlpha0);
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
		
		//System.out.println("Line search for the strong wolfe conditions");

		alpha[0] = 0.0;
		linearFunctionInAlpha[0] = linearFunctionInAlpha0; 
		linearFunctionDerivativeInAlpha[0] = linearFunctionDerivativeInAlpha0;	//To Analyse the possibility of eliminate linearFunctionDerivativeInAlpha[0]
		dfInAlpha[0] = this.dfx;
		
		alpha[1] = this.alphaInitialStep;
		
		//System.out.println("alpha[1] = " + alpha[1]);
		
		if (alpha[1] > alphaMax) {
			alpha[1] = alphaMax/2;
		}
		
		brentStep[0] = alpha[0];
		brentStep[1] = alpha[1];
		
		int i=1;

		try {
		do {

			if (alpha[1] == 0) {
				//System.out.println("alpha[1] == 0");
				break;
			}
			
			//System.out.println("alpha[" + i + "] = " + alpha[i]);
			linearFunctionInAlpha[i] = evaluateEnergyFunction(alpha[i]);
			//System.out.println("linearFunctionInAlpha[" + i + "] = " + linearFunctionInAlpha[i]);
			
			if ((linearFunctionInAlpha[i] > linearFunctionInAlpha[0] + c1 * alpha[i] * linearFunctionDerivativeInAlpha[0]) | 
					((linearFunctionInAlpha[i] >= linearFunctionInAlpha[i-1]) & (i>1))) {			//The interval alpha[i-1] and alpha[i] brackets the desired step lengths.
				//System.out.println("zoom");
				//dfInAlpha[i] = evaluateEnergyFunctionDerivative(alpha[i]);
				//linearFunctionDerivativeInAlpha[i] = dfInAlpha[i].dot(direction);
				//zoom(alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i-1], dfInAlpha[i-1], alpha[i], linearFunctionInAlpha[i], linearFunctionDerivativeInAlpha[i], dfInAlpha[i]);
				zoom(alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i-1], dfInAlpha[i-1], alpha[i], linearFunctionInAlpha[i]);
				break;
			} 

			//The first strong Wolfe condition is satisfied for alpha[i].
			dfInAlpha[i] = evaluateEnergyFunctionDerivative(alpha[i]);
			//System.out.println("dfOptimum = " + dfOptimum);
			linearFunctionDerivativeInAlpha[i] = dfInAlpha[i].dot(direction);
			//System.out.println("linearFunctionDerivativeInAlpha[" + i + "] = " + linearFunctionDerivativeInAlpha[i]);
			
			if (Math.abs(linearFunctionDerivativeInAlpha[i]) <= -c2 * linearFunctionDerivativeInAlpha[0]) { //The second strong Wolfe condition is also satisfied for alpha[i]
				//System.out.println("The second strong Wolfe condition is also satisfied for " + alpha[i]);
				alphaOptimum = alpha[i];
				linearFunctionInAlphaOptimum = linearFunctionInAlpha[i];
				dfOptimum = dfInAlpha[i];
				//System.out.println("alphaOptimun = " + alphaOptimum);
				//System.out.println("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
				//System.out.println("dfOptimum = " + dfOptimum);
				this.derivativeSmallEnough = true;
				break;
			}
			
			if (linearFunctionDerivativeInAlpha[i] >= 0) {		//The interval alpha[i-1] and alpha[i] brackets the desired step lengths.
				//System.out.println("zoom");
				/*zoom(alpha[i], linearFunctionInAlpha[i], linearFunctionDerivativeInAlpha[i], dfInAlpha[i], 
						alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i], dfInAlpha[i]);*/
				zoom(alpha[i-1], linearFunctionInAlpha[i-1], linearFunctionDerivativeInAlpha[i-1], dfInAlpha[i-1], 
						alpha[i], linearFunctionInAlpha[i]);
				break;
			}
		
			if (alpha[i] == alphaMax) {	
				alphaOptimum = alpha[i];
				linearFunctionInAlphaOptimum = linearFunctionInAlpha[i];
				dfOptimum = dfInAlpha[i];
				//System.out.println("alphaOptimun = " + alphaOptimum);
				//System.out.println("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
				//System.out.println("dfOptimum = " + dfOptimum);
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

			if (brentStep[2] > alphaMax) {brentStep[2] = alphaMax;}
			/*linearFunctionInBrentStep = this.evaluateEnergyFunction(brentStep[2]);
			linearFunctionDerivativeInBrentStep = this.evaluateEnergyFunctionDerivative(brentStep[2]).dot(direction);
			*/
			alpha[2] = brentStep[2];
			/*alpha[2] = this.cubicInterpolation(alpha[1], linearFunctionInAlpha[1], linearFunctionDerivativeInAlpha[1], 
					brentStep[2], linearFunctionInBrentStep, linearFunctionDerivativeInBrentStep, alpha[1], brentStep[2]);
					*/

			i=2;
			
			//functionEvaluationNumber = functionEvaluationNumber + 1;
			
		} while ((alpha[2] <= alphaMax) & (alpha[1] < alpha[2]) /*& (functionEvaluationNumber < 10)*/);
		
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
	public void zoom (double alphaLow, double linearFunctionInAlphaLow, double linearFunctionDerivativeInAlphaLow, GVector dfInAlphaLow, 
			double alphaHigh, double linearFunctionInAlphaHigh) {
		
		functionEvaluationNumber = 0;
		
		/*double a;
		double b;
		if (alphaLow < alphaHigh) {a = alphaLow; b = alphaHigh;}
		else {a = alphaHigh; b = alphaLow;}
		*/
		
		do {
			//Interpolation 
			
			//alphaj = this.cubicInterpolation(alphaLow, linearFunctionInAlphaLow, linearFunctionDerivativeInAlphaLow, alphaHigh, linearFunctionInAlphaHigh, linearFunctionDerivativeInAlphaHigh, a, b);
			/*System.out.println("cubicInterpolation(" + alphaLow + ", " + linearFunctionInAlphaLow + ", " + linearFunctionDerivativeInAlphaLow + ", "
					+ alphaHigh + ", " + linearFunctionInAlphaHigh + ", " + linearFunctionDerivativeInAlphaHigh + ", " + a + ", " + b + ");");*/

			alphaj = this.interpolation(alphaLow, linearFunctionInAlphaLow, linearFunctionDerivativeInAlphaLow, alphaHigh, linearFunctionInAlphaHigh);
			//System.out.println("alphaj = " + alphaj);
			linearFunctionInAlphaj = this.linearFunctionAlphaInterpolation;
			//System.out.println("linearFunctionInAlphaj = " + linearFunctionInAlphaj);
			
			if ((linearFunctionInAlphaj > linearFunctionInAlpha0 + c1 * alphaj * linearFunctionDerivativeInAlpha0) | //The interval 0 and alphaj brackets the desired step lengths.
					(linearFunctionInAlphaj >= linearFunctionInAlphaLow)) {			

				//System.out.println("The minimum is between alpha1 and alphaj");
				alphaHigh = alphaj;
				linearFunctionInAlphaHigh = linearFunctionInAlphaj;
				//dfInAlphaHigh = this.evaluateEnergyFunctionDerivative(alphaHigh); 
				//linearFunctionDerivativeInAlphaHigh = dfInAlphaHigh.dot(direction);
			} 
			else {
				dfInAlphaj = evaluateEnergyFunctionDerivative(alphaj);
				linearFunctionDerivativeInAlphaj = dfInAlphaj.dot(direction);
				//System.out.println("linearFunctionDerivativeInAlphaj = " + linearFunctionDerivativeInAlphaj);
				if (Math.abs(linearFunctionDerivativeInAlphaj) <= -c2 * linearFunctionDerivativeInAlpha0) { //alphaj satisfied the second strong Wolfe condition.
					//System.out.println("Derivative small enough : " + Math.abs(linearFunctionDerivativeInAlphaj) + " <= " + (-c2 * linearFunctionDerivativeInAlpha0));
					this.derivativeSmallEnough = true;
					alphaOptimum = alphaj;
					linearFunctionInAlphaOptimum = linearFunctionInAlphaj;
					dfOptimum = dfInAlphaj;
					//System.out.println("alphaOptimun = " + alphaOptimum);
					//System.out.println("linearFunctionInAlphaOptimun = " + linearFunctionInAlphaOptimum);
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
			
			//System.out.println("AlphaLow = " + alphaLow + ", AlphaHigh = " + alphaHigh);
			//System.out.println("linearFunctionInAlphaLow = " + linearFunctionInAlphaLow + ", linearFunctionInAlphaHigh = " + linearFunctionInAlphaHigh);
			functionEvaluationNumber = functionEvaluationNumber + 1;
			//System.out.println("functionEvaluationNumber = " + functionEvaluationNumber);

			if ((functionEvaluationNumber == 10) | (Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) <= 0.000001) | (Math.abs(alphaLow - alphaHigh) <= 0.0000001)) {
				this.alphaOptimum = alphaLow;
				this.linearFunctionInAlphaOptimum = linearFunctionInAlphaLow;
				this.dfOptimum = dfInAlphaLow;
				
				/*System.out.println("(functionEvaluationNumber == 10) | (Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) <= 0.000001) 
										| (Math.abs(alphaLow - alphaHigh) <= 0.0000001)");*/
				//System.out.println("this.alphaOptimum = " + this.alphaOptimum); 
				//System.out.println("this.linearFunctionInAlphaOptimum = " + this.linearFunctionInAlphaOptimum); 
				break;
			}

		
		} while ((Math.abs(linearFunctionInAlphaHigh - linearFunctionInAlphaLow) > 0.000001) 
					& (functionEvaluationNumber < 10) 
					& (Math.abs(alphaLow - alphaHigh) > 0.0000001));
		
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
		
		//System.out.println("The interval [" + a + ", " + b + "] contains acceptable step lengths.");
		
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
		//System.out.println("d1 = " + d1);
		
		//System.out.println("linearFunctionDerivativeInAlphaiMinus1 = " + linearFunctionDerivativeInAlphaiMinus1);
		//System.out.println("linearFunctionDerivativeInAlphai = " + linearFunctionDerivativeInAlphai);
		
		this.d2 = Math.sqrt(Math.abs(Math.pow(d1,2) - linearFunctionDerivativeInAlphaiMinus1 * linearFunctionDerivativeInAlphai));
		//System.out.println("d2 = " + d2);
		
		this.alphaiplus1 = alphai-(alphai-alphaiMinus1) * ((linearFunctionDerivativeInAlphai + d2 - d1) / (linearFunctionDerivativeInAlphai - linearFunctionDerivativeInAlphaiMinus1 + 2 * d2));
		
		//System.out.println("alphaiplus1 = " + alphaiplus1);
		
		if (alphaiplus1 < a) {alphaiplus1 = a;}
		if (alphaiplus1 > b) {alphaiplus1 = b;}
		
		//System.out.println("alphaiplus1 = " + alphaiplus1);
		
		if (Math.abs(alphaiplus1 - alphai) < 0.000000001) {
			/*System.out.println("We reset alphaiplus1 = (alphaiMinus1 + alphai) / 2, because alphaiplus1 = " + alphaiplus1 + " is too close to its predecessor " +
					"alphaiMinus1 = " + alphaiMinus1); */
			alphaiplus1 = (alphaiMinus1 + alphai) / 2;
		} else {if (alphaiplus1 < (alphai - 9 * (alphai-alphaiMinus1) / 10)) {
			//System.out.println("We reset alphaiplus1 = (alphaiMinus1 + alphai) / 2, because alphaiplus1 = " + alphaiplus1 + " is 	too much smaller than alphai = " + alphai); 
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
	public double interpolation(double alphaLow, double linearFunctionInAlphaLow, double linearFunctionDerivativeInAlphaLow, 
								double alphaHigh, double linearFunctionInAlphaHigh) {
		
		double minAlpha = Math.min(alphaLow, alphaHigh);
		double alphaDiff = Math.abs(alphaHigh - alphaLow);
		double alphaInterpolation;

		//System.out.println("We form a quadratic approximation to the linear function");
		double alpha1 = -1 * ((linearFunctionDerivativeInAlphaLow * Math.pow(alphaDiff,2)) / (2 * (linearFunctionInAlphaHigh - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alphaDiff)));
		
		//System.out.println("The value alpha1 = " + alpha1 + ", is the minimizer of this quadratic function");
		
		if ((alpha1 > alphaDiff) | (Math.abs(alpha1 - alphaDiff) < 0.000000001)) {
			/*System.out.println("We reset alphai = alphaiMinus1 / 2, because alphaInterpolation = " + alphai + " is too close to its predecessor " +
					"alphaiMinus1 = " + alphaiMinus1); */
			alpha1 = alphaDiff / 2;
		} else {if ((alpha1 < 0) & (alpha1 < (alphaDiff - 9 * alphaDiff / 10))) {
			//System.out.println("We reset alphai = alphaiMinus1 / 2, because alphaInterpolation = " + alphai + " is 	too much smaller than alphaiMinus1 = " + alphaiMinus1); 
			alpha1 = alphaDiff / 2;
			}
		}

		//System.out.println("alpha1 = " + alpha1);

		alphaInterpolation = minAlpha + alpha1;
		this.linearFunctionAlphaInterpolation = this.evaluateEnergyFunction(alphaInterpolation);
		//System.out.println("alphaInterpolation = " + alphaInterpolation);
		//System.out.println("linearFunctionAlphaInterpolation = " + this.linearFunctionAlphaInterpolation);
		if (this.linearFunctionAlphaInterpolation <= this.linearFunctionInAlpha0 + this.c1 * (alphaInterpolation) * this.linearFunctionDerivativeInAlpha0) {
			//System.out.println("The sufficient decrease condition is satisfied at alpha1 and we termine the interpolation");
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
					
				//System.out.println("We construct a cubic function that interpolates the fours pieces of information");	
				a = 1/(Math.pow(alphaDiff,2) * Math.pow(alpha1, 2) * (alpha1-alphaDiff));
				b = a;
				a = a * (Math.pow(alphaDiff,2) * (this.linearFunctionAlphaInterpolation - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alpha1) 
						+ (-Math.pow(alpha1,2)) * (linearFunctionInAlphaHigh - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alphaDiff));
				b = b * (- Math.pow(alphaDiff,3) * (this.linearFunctionAlphaInterpolation - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alpha1) 
						+ Math.pow(alpha1,3) * (linearFunctionInAlphaHigh - linearFunctionInAlphaLow - linearFunctionDerivativeInAlphaLow * alphaDiff));
				
				//System.out.println("a = " + a);
				//System.out.println("b = " + b);
				
				alphai = (-b + Math.sqrt(Math.pow(b,2) - 3 * a * linearFunctionDerivativeInAlphaLow)) / (3 * a);
				//System.out.println("alphai = " + alphai);
				
				if (Math.abs(alphai - alpha1) < 0.000000001) {
					/*System.out.println("We reset alphai = alpha1 / 2, because alphaInterpolation = " + alphai + " is too close to its predecessor " +
							"alpha1 = " + alpha1); */
					alphai = alpha1 / 2;
				} else {if (alphai < (alpha1 - 9 * alpha1 / 10)) {
					//System.out.println("We reset alphai = alpha1 / 2, because alphaInterpolation = " + alphai + " is 	too much smaller than alpha1 = " + alpha1); 
					alphai = alpha1 / 2;
					}
				}
			
				alphaInterpolation = minAlpha + alphai;
				this.linearFunctionAlphaInterpolation = this.evaluateEnergyFunction(alphaInterpolation);
				//System.out.println("alphaInterpolation = " + alphaInterpolation);
				//System.out.println("linearFunctionAlphaInterpolation = " + this.linearFunctionAlphaInterpolation);
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
	public double evaluateEnergyFunction(double alpha) {
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
	public GVector evaluateEnergyFunctionDerivative(double alpha) {
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
	public double goldenSectionMethod(double lambdaMin, double flambdaMin, double lambdaMax, double flambdaMax) {

		//System.out.println("Golden Section Search");
		double goldenLambda;
		
		double lambda1 = lambdaMin;
		double lambda4 = lambdaMax;
		double lambda2 = lambda1 + 0.3819660112 * (lambda4 - lambda1);
		double lambda3 = lambda1 + 0.6180339887498948482 * (lambda4 - lambda1);
		
		//double flambda1 = flambdaMin;
		double flambda2 = evaluateEnergyFunction(lambda2);
		double flambda3 = evaluateEnergyFunction(lambda3);
		//double flambda4 = flambdaMax;
		
		//System.out.println("lambda1 = " + lambda1 + ", flambda1 = " + flambda1);
		//System.out.println("lambda2 = " + lambda2 + ", flambda2 = " + flambda2);
		//System.out.println("lambda3 = " + lambda3 + ", flambda3 = " + flambda3);
		//System.out.println("lambda4 = " + lambda4 + ", flambda4 = " + flambda4);

		if (flambda2 < flambda3) {		//we can bracket the minimum by the interval [lambda1, lambda3]
			goldenLambda = lambda2;
			linearFunctionGoldenAlpha = flambda2;
		}	
		else {			//we can bracket the minimum by the interval [lambda2, lambda4]
			goldenLambda = lambda3;
			linearFunctionGoldenAlpha = flambda3;
		}

		return goldenLambda;
	}


}
