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

import org.openscience.cdk.tools.LoggingTool;


/**
 * The LineSearch class include the first and second step of the line search approach: 
 * Bracket the minimum and interpolation. The interpolation is quadratic.
 *  
 * @author      vlabarta
 * @cdk.module  forcefield
 * @cdk.svnrev  $Revision$
 *
 * @cdk.keyword line search
 */
public class LineSearch {

	IPotentialFunction pf = null;
	GVector direction = null;	// Fix value
	GVector x = null;		// Fix value
	GVector directionStep = null;
	GVector xlambda = null;	
	double lambdaa = 0;
	double lambdab = 0.5;
	double lambdac = 0;
	double fxa = 0;
	double fxb = 0;
	double fxc = 0;
	double parabolMinimumLambda = 0;
	double lineSearchLambda = 0;
	double fxLS = 0;
	double lambdabOld = 0;
	double tol = 0.0001;
	private LoggingTool logger;


	/**
	 *  Constructor for the LineSearch object
	 */
	public LineSearch() {        
		logger = new LoggingTool(this);
	}


	/**
	 *  Bracketing the minimum: The bracketing phase determines the range of points on the line to be searched.
	 *  Look for 3 points along the line where the energy of the middle point is lower than the energy of the two outer points.
	 *  The bracket corresponds to an interval specifying the range of values of Lambda.
	 *
	 */
	public void bracketingTheMinimum() {

		//logger.debug(" ");
		//logger.debug("Bracketing the minimum:");

		lambdaa = 0;	fxa = f(lambdaa);
		//logger.debug("lambdaa = " + lambdaa);
		//logger.debug("fxa = " + fxa);

		if (lambdab < 0.5) {}
		else {
			lambdab = 0.5;
		}
		fxb = f(lambdab);
		//logger.debug("lambdab = " + lambdab);
		//logger.debug("fxb = " + fxb);

		boolean finish = false;
		if (fxb > fxa) {
			while (finish == false) {
				
				//logger.debug("The energy increase with the current step size. The step size will be halve");
				
				lambdab = lambdab / 2;
				fxb = f(lambdab);
				
				//logger.debug("lambdaa = " + lambdaa);
				//logger.debug("fxa = " + fxa);
				//logger.debug("lambdab = " + lambdab);
				//logger.debug("fxb = " + fxb);
				
				if (fxb < fxa) {
					finish = true;
				}
				if (lambdab < 0.0000000000000001) {
					finish = true;
					lambdac = lambdab;
				}
			}
		}
		
		//logger.debug("lambdaa = " + lambdaa + "	");
		//logger.debug("fxa = " + fxa);
		//logger.debug("lambdab = " + lambdab + "	");
		//logger.debug("fxb = " + fxb);
		
		finish = false;
		if (fxb < fxa) {

			//logger.debug("Brent's exponential search");
					
			lambdac = 1.2 * lambdab;
			fxc = f(lambdac);
			
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
					lambdaa = lambdab;
					fxa = fxb;
					
					lambdabOld = lambdab;
					lambdab = lambdac;
					fxb = fxc;
					
					lambdac = 1.618 * (lambdac-lambdabOld) + lambdac;	// Brent's exponential search
					fxc = f(lambdac);

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

			//logger.debug("Golden Section Method");
			/*xc.set(xb);
			lambdac = lambdab;
			fxc = fxb;
			
			double lambdab1 = 0.3819660112 * (lambdac - lambdaa);
			double lambdab2 = 0.6180339887498948482 * (lambdac - lambdaa);
			
			GVector xb1 = new GVector(kPoint);
			directionStep.set(searchDirection);
			directionStep.scale(lambdab1);
			xb1.add(directionStep);
			double fxb1 = forceFieldFunction.energyFunction(xb1);
			
			GVector xb2 = new GVector(kPoint);
			directionStep.set(searchDirection);
			directionStep.scale(lambdab2);
			xb2.add(directionStep);
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
						xb1.set(kPoint);	directionStep.set(searchDirection);		directionStep.scale(lambdab1);
						xb1.add(directionStep);	fxb1 = forceFieldFunction.energyFunction(xb1);
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
							xb2.set(kPoint);	directionStep.set(searchDirection);		directionStep.scale(lambdab2);
							xb2.add(directionStep);	fxb2 = forceFieldFunction.energyFunction(xb2);
						}
					} else {//we can bracket the minimum by the interval [lambdaa, lambdab2]
						lambdac = lambdab2;	xc.set(xb2);	fxc = fxb2;
						lambdab2 = lambdab1;	xb2.set(xb1); 	fxb2 = fxb1;
						lambdab1 = lambdaa + 0.3819660112 * (lambdac - lambdaa);
						xb1.set(kPoint);	directionStep.set(searchDirection);		directionStep.scale(lambdab1);
						xb1.add(directionStep);	fxb1 = forceFieldFunction.energyFunction(xb1);
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
			}*/

		return;
	}


	/**
	 *  Given a function f, and given a bracketing triplet of abscissas lambdaa, lambdab, lambdac (such that 
	 *  lambdab is between lambdaa and lambdac, and f(lambdab) is less than both f(lambdaa) and f(lambdac)), 
	 *  this routine isolates the minimum to a fractional precision of about t01=0.00001 using Brent`s method.
	 */
	public void brentsMethod() {
		//logger.debug(" ");
		//logger.debug("brentsMethod");
		int itmax = 100;	//maximum allowed number of iterations
		double CGold = 0.3819660;	//golden ratio
		double zeps = 0.0000000000000001;	//small number that protects against trying to achieve fractional accuracy for a minimum that happens to be exactly zero.
							//zeps = numeric_limits<DP>::epsilon()*1.0e-3
		double a,b,d = 0;
		double etemp, fu, fv, fw, fx;
		double p,q,r,tol1,tol2,u,v,w,x,xm;
		double e = 0;	// This will be the distance moved on the step before last.
		a = lambdaa;
		b = lambdac;		// a and b  must be in ascending order.
		x=w=v=lambdab;
		fw=fv=fx=fxb;
		u=lambdab; fu=fxb; // included later
		
		for (int iter=0; iter < itmax; iter++) {	// Main method loop
			logger.debug("iter = " + iter);
			//logger.debug("a (bracket the minimum) = " + a);
			//logger.debug("b (bracket the minimum) = " + b);
			//logger.debug("x (least function value found) = " + x + " ; f(x) = " + fx);
			//logger.debug("w (second least function value) = " + w + " ; f(w) = " + fw);
			//logger.debug("v (previous value of w) = " + v + " ; f(v) = " + fv);
			//logger.debug("u (most recently evaluation) = " + u + " ; f(u) = " + fu);

			xm = 0.5 * (a + b);
			//logger.debug("xm = " + xm);
			
			tol1= tol * Math.abs(x) + zeps;
			tol2 = 2.0 * tol1;
			//logger.debug("tol = " + tol + " ; tol1 = " + tol1 + " ; tol2 = " + tol2);
			//logger.debug("Math.abs(x-xm) = " + Math.abs(x-xm));
			//logger.debug("tol2-0.5*(b-a) = " + (tol2-0.5*(b-a)));
			//logger.debug("if (Math.abs(x-xm) <= (tol2-0.5*(b-a))) ; " + (Math.abs(x-xm) <= (tol2-0.5*(b-a))));
			if (Math.abs(x-xm) <= (tol2-0.5*(b-a))) {	// Test for done hear
				break;
			} else {
				//logger.debug("if (Math.abs(e) > tol1) ; " + (Math.abs(e) > tol1));
				if (Math.abs(e) > tol1) {		// Construct a trial parabolic fit.
					//logger.debug("Construct a trial parabolic fit.");
					r = (x-w) * (fx-fv);
					//logger.debug("r = " + r);
					q = (x-v) * (fx-fw);
					//logger.debug("q = " + q);
					p = (x - v) * q - (x - w) * r;
					//logger.debug("p = " + p);
					q = 2.0 * (q - r);
					if (q > 0.0) {
						p = -p;
					}
					q = Math.abs(q);
					etemp = e;
					e = d;
					//logger.debug("if (Math.abs(p) >= Math.abs(0.5 * q * etemp) | p <= q * (a - x) | p >= q * (b-x)) ; " + (Math.abs(p) >= Math.abs(0.5 * q * etemp) | p <= q * (a - x) | p >= q * (b-x)));
					//logger.debug("The above conditions determine the acceptability of the parabolic fit.");
					if (Math.abs(p) >= Math.abs(0.5 * q * etemp) | p <= q * (a - x) | p >= q * (b-x)) {
						// The above conditions determine the acceptability of the parabolic fit.
						logger.debug("Parabolic fit");
						if (x >= xm) {
							e = a-x;
						}
						else {
							e = b-x;
						}
						d = CGold * e;
					} else {	// Here we take the golden section step into the larger of the two segments.
						logger.debug("golden section");
						d = p/q;	// Take the parabolic step
						u = x + d;
						if (u-a < tol2 | b-u < tol2) {
							d = sign(tol1,xm-x);
						}
					}
				} else {
					if (x >= xm) {
						//logger.debug("Prepare e, x >= xm");
						e = a-x;	
						//logger.debug("e = " + e);
					} else {
						//logger.debug("Prepare e, x < xm");
						e = b-x;
						//logger.debug("e = " + e);
					}
					d = CGold * e;
					//logger.debug("d = " + d);
				}
				if (Math.abs(d) >= tol1) {
					u = x + d;
					//logger.debug("u = x + d = " + u);
				} else {
					u = x + sign(tol1,d);
					//logger.debug("u = x + sign(tol1,d) = " + u);
				}
				fu = f(u);	// This is the one function evaluation per iteration
				//logger.debug("Function evaluation: f(u) = " + fu);
				if (fu <= fx) {		// Now decide what to do with our function evaluation.
					if (u >= x) {
						a=x;
					} else {
						b=x;
					}
					v = w; w = x; x = u;	// Housekeeping follows
					fv = fw; fw = fx; fx = fu;
				} else {
					if (u<x) {
						a=u;
					} else {
						b=u;
					}
					if (fu <= fw | w==x) {
						v=w;
						w=u;
						fv=fw;
						fw=fu;
					} else if (fu <= fv | v == x | v == w) {
						v=u;
						fv=fu;
					}
				}		
			}	// Done with housekeeping. Back for another iteration.
			if (iter == itmax-1) {
				logger.debug("Too many iterations in brent");
			}
		}

		lineSearchLambda = x;
		fxLS = fx;

		
		//Parabolic interpolation - that was working before
		 		/*GVector xI = new GVector(kPoint.getSize());
				double fxI = 0;
				double fMinimumMinus1 = fxa;
				double fMinimum = fxb;
				
				do {
					parabolicInterpolation();
					xI.set(kPoint);
					directionStep.set(searchDirection);
					directionStep.scale(parabolMinimumLambda);
					xI.add(directionStep);
					
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
				*/	
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
		/*lineSearchLambda = x;
		fxLS = fx;
		*/
	}

	
	/**
	 *  Given a function f and its derivative function df, and given a bracketing triplet of abscissas lambdaa, 
	 *  lambdab, lambdac (such that lambdab is between lambdaa and lambdac, and f(lambdab) is less than both 
	 *  f(lambdaa) and f(lambdac)), this routine isolates the minimum to a fractional precision of about 
	 *  t01=0.00001 using a modification of Brent`s method that uses derivatives.
	 */
	public void dbrentsMethod() {
		//logger.debug(" ");
		//logger.debug("brentsMethod");
		int itmax = 100;	//maximum allowed number of iterations
		double CGold = 0.3819660;	//golden ratio
		double zeps = 0.0000000000000001;	//small number that protects against trying to achieve fractional accuracy for a minimum that happens to be exactly zero.
							//zeps = numeric_limits<DP>::epsilon()*1.0e-3
		double a,b,d = 0;
		double etemp, fu, fv, fw, fx;
		double p,q,r,tol1,tol2,u,v,w,x,xm;
		double e = 0;	// This will be the distance moved on the step before last.
		a = lambdaa;
		b = lambdac;		// a and b  must be in ascending order.
		x=w=v=lambdab;
		fw=fv=fx=fxb;
		u=lambdab; fu=fxb; // included later
		
		for (int iter=0; iter < itmax; iter++) {	// Main method loop
			logger.debug("iter = " + iter);
			//logger.debug("a (bracket the minimum) = " + a);
			//logger.debug("b (bracket the minimum) = " + b);
			//logger.debug("x (least function value found) = " + x + " ; f(x) = " + fx);
			//logger.debug("w (second least function value) = " + w + " ; f(w) = " + fw);
			//logger.debug("v (previous value of w) = " + v + " ; f(v) = " + fv);
			//logger.debug("u (most recently evaluation) = " + u + " ; f(u) = " + fu);

			xm = 0.5 * (a + b);
			//logger.debug("xm = " + xm);
			
			tol1= tol * Math.abs(x) + zeps;
			tol2 = 2.0 * tol1;
			//logger.debug("tol = " + tol + " ; tol1 = " + tol1 + " ; tol2 = " + tol2);
			//logger.debug("Math.abs(x-xm) = " + Math.abs(x-xm));
			//logger.debug("tol2-0.5*(b-a) = " + (tol2-0.5*(b-a)));
			//logger.debug("if (Math.abs(x-xm) <= (tol2-0.5*(b-a))) ; " + (Math.abs(x-xm) <= (tol2-0.5*(b-a))));
			if (Math.abs(x-xm) <= (tol2-0.5*(b-a))) {	// Test for done hear
				break;
			} else {
				//logger.debug("if (Math.abs(e) > tol1) ; " + (Math.abs(e) > tol1));
				if (Math.abs(e) > tol1) {		// Construct a trial parabolic fit.
					//logger.debug("Construct a trial parabolic fit.");
					r = (x-w) * (fx-fv);
					//logger.debug("r = " + r);
					q = (x-v) * (fx-fw);
					//logger.debug("q = " + q);
					p = (x - v) * q - (x - w) * r;
					//logger.debug("p = " + p);
					q = 2.0 * (q - r);
					if (q > 0.0) {
						p = -p;
					}
					q = Math.abs(q);
					etemp = e;
					e = d;
					//logger.debug("if (Math.abs(p) >= Math.abs(0.5 * q * etemp) | p <= q * (a - x) | p >= q * (b-x)) ; " + (Math.abs(p) >= Math.abs(0.5 * q * etemp) | p <= q * (a - x) | p >= q * (b-x)));
					//logger.debug("The above conditions determine the acceptability of the parabolic fit.");
					if (Math.abs(p) >= Math.abs(0.5 * q * etemp) | p <= q * (a - x) | p >= q * (b-x)) {
						// The above conditions determine the acceptability of the parabolic fit.
						logger.debug("Parabolic fit");
						if (x >= xm) {
							e = a-x;
						}
						else {
							e = b-x;
						}
						d = CGold * e;
					} else {	// Here we take the golden section step into the larger of the two segments.
						logger.debug("golden section");
						d = p/q;	// Take the parabolic step
						u = x + d;
						if (u-a < tol2 | b-u < tol2) {
							d = sign(tol1,xm-x);
						}
					}
				} else {
					if (x >= xm) {
						//logger.debug("Prepare e, x >= xm");
						e = a-x;	
						//logger.debug("e = " + e);
					} else {
						//logger.debug("Prepare e, x < xm");
						e = b-x;
						//logger.debug("e = " + e);
					}
					d = CGold * e;
					//logger.debug("d = " + d);
				}
				if (Math.abs(d) >= tol1) {
					u = x + d;
					//logger.debug("u = x + d = " + u);
				} else {
					u = x + sign(tol1,d);
					//logger.debug("u = x + sign(tol1,d) = " + u);
				}
				fu = f(u);	// This is the one function evaluation per iteration
				//logger.debug("Function evaluation: f(u) = " + fu);
				if (fu <= fx) {		// Now decide what to do with our function evaluation.
					if (u >= x) {
						a=x;
					} else {
						b=x;
					}
					v = w; w = x; x = u;	// Housekeeping follows
					fv = fw; fw = fx; fx = fu;
				} else {
					if (u<x) {
						a=u;
					} else {
						b=u;
					}
					if (fu <= fw | w==x) {
						v=w;
						w=u;
						fv=fw;
						fw=fu;
					} else if (fu <= fv | v == x | v == w) {
						v=u;
						fv=fu;
					}
				}		
			}	// Done with housekeeping. Back for another iteration.
			if (iter == itmax-1) {
				logger.debug("Too many iterations in brent");
			}
		}

		lineSearchLambda = x;
		fxLS = fx;

		
		//Parabolic interpolation - that was working before
		 		/*GVector xI = new GVector(kPoint.getSize());
				double fxI = 0;
				double fMinimumMinus1 = fxa;
				double fMinimum = fxb;
				
				do {
					parabolicInterpolation();
					xI.set(kPoint);
					directionStep.set(searchDirection);
					directionStep.scale(parabolMinimumLambda);
					xI.add(directionStep);
					
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
				*/	
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
		/*lineSearchLambda = x;
		fxLS = fx;
		*/
	}
	
	
	public boolean wolfeConditions(double lambda) {
		boolean wolfeConditions = false;
		return wolfeConditions;
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
	 *  Given the atom coordinates and a direction where decrease the energy function, 
	 *  Bracket a local minimum in this direction and find the minimum, that determine the direction movement.
	 *@param  kPoint              Current point, xk
	 *@param  searchDirection     Search direction
	 *@param  forceFieldFunction  Potential energy function
	 */
	public void setLineStep(GVector kPoint, GVector searchDirection, IPotentialFunction forceFieldFunction) {
		//logger.debug("");
		//logger.debug("Start the line search: ");
		
		pf = forceFieldFunction;
		//logger.debug("pf.energyfunction(kPoint) = " + pf.energyFunction(kPoint));
		direction = searchDirection;
		x = kPoint;
		//logger.debug("pf.energyfunction(x) = " + pf.energyFunction(x));
		//logger.debug("f(0) = " + f(0));
		
		
		bracketingTheMinimum();
		
		if (lambdaa < lambdab & lambdab < lambdac & fxb < fxa & fxb < fxc) {
			brentsMethod();
			if (wolfeConditions(lineSearchLambda)) {}
			else {//logger.debug("The Wolfe Conditions are not satisfy");
			}
		} else {
			lineSearchLambda = 0;
			//logger.debug("(lambdaa < lambdab & lambdab < lambdac & fxb < fxa & fxb < fxc) false");
			/*logger.debug("lambdaa = " + lambdaa);
			logger.debug("lambdab = " + lambdab);
			logger.debug("lambdac = " + lambdac);
			logger.debug("fxa = " + fxa);
			logger.debug("fxb = " + fxb);
			logger.debug("fxc = " + fxc);*/
		}
	}

	public double f(double lambda) {
		//logger.debug("lambda= " + lambda);
		xlambda = new GVector(x);
		//logger.debug("xlambda = " + xlambda);
		directionStep = direction;
		//logger.debug("directionStep = " + directionStep);
		xlambda.scaleAdd(lambda, directionStep, xlambda);
		//logger.debug("xlambda = " + xlambda);
		double fx = pf.energyFunction(xlambda);
		//logger.debug("fx = " + fx);
		return fx;
	}
	
	
	public double sign(double a, double b) {
		double c = a;
		if (b >= 0) {
			if (a >= 0) {} 
			else { c = -1 * c; }
		} else if (a >= 0) { c= -1 * c; } 
		return c;
	}


}

