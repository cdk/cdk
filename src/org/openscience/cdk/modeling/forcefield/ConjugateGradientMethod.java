package org.openscience.cdk.modeling.forcefield;

import javax.vecmath.GVector;

import org.openscience.cdk.tools.LoggingTool;


/**
 *  Find a direction from a point of the 3xN coordinates space using the conjugate gradient approach.
 *
 *@author     vlabarta
 *@cdk.module     builder3d
 *
 */
public class ConjugateGradientMethod {
	double uk_FletcherReeves = 0;
	double uk_PolankRibiere = 0;
	GVector vk = null;
	GVector vkminus1 = null;
	GVector gkminus1 = null;
	int lastGradientDirectionIteration = 0;

	private LoggingTool logger;


	/**
	 *  Constructor for the ConjugateGradientMethod object
	 */
	public ConjugateGradientMethod() {
		logger = new LoggingTool(this);
	}


	/**
	 *  Fletcher-Reeves: uk = gk gk / gk-1 gk-1
	 *
	 */
	public void setFletcherReeves_uk(GVector gkminus1, GVector gk) {
		
		uk_FletcherReeves = gk.dot(gk) / gkminus1.dot(gkminus1);
		
		logger.debug("uk_FletcherReeves = " + uk_FletcherReeves);
		return;
	}


	/**
	 *  Polak-Ribiere: uk = (gk - gk-1) gk / gk-1 gk-1
	 *
	 */
	public void setPolankRibiere_uk(GVector gkminus1, GVector gk) {
		
		GVector diffgkgkminus1 = new GVector(gk);
		diffgkgkminus1.sub(gkminus1);
		uk_PolankRibiere = diffgkgkminus1.dot(gk) / gkminus1.dot(gkminus1);
		
		logger.debug("uk_PolankRibiere = " + uk_PolankRibiere);
		return;
	}


	/**
	 *  vk=-gk + uk vk-1
	 *
	 * @param  gk  Gradient at coordinates Xk
	 * @param  iterNumber  Iteration number
	*/
	public void setvk(GVector gk, int iterNumber, boolean derivativeSmallEnough) {	//To reprogram in a better way.

		if (iterNumber != 1) {
		//logger.debug("gk.angle(gkminus1) = " + gk.angle(gkminus1));
			if (derivativeSmallEnough) {
				vkminus1.set(vk);
				setPolankRibiere_uk(gkminus1,gk);
				vkminus1.scale(uk_PolankRibiere);
				vk.set(gk);
				vk.scale(-1);
				vk.add(vkminus1);
				//vk.normalize();
				gkminus1.set(gk);
				//logger.debug("vector vk : " + vk);
			} else {
				vk.set(gk);
				vk.normalize();
				vk.scale(-1);
				//logger.debug("vectorvk : " + vk);
				
				gkminus1.set(gk);
			}
		} else {
			vk = new GVector(gk);
			vkminus1 = new GVector(gk.getSize());
			gkminus1 = new GVector(gk);
		
			vk.normalize();
			vk.scale(-1);
			//logger.debug("vectorvk : " + vk);
		}
		return;

		/*if (iterNumber != 1) {
			vkminus1.set(vk);
			setuk(gkminus1,gk);
			vkminus1.scale(uk);
			vk.set(gk);
			vk.scale(-1);
			vk.add(vkminus1);
			logger.debug("vk.angle(vkminus1) = " + vk.angle(vkminus1));
			if (vk.angle(vkminus1) > 1) {}
			else { if (iterNumber < lastGradientDirectionIteration + 10) {}
				else {
					logger.debug("the gradient direction was take");
					vk.set(gk);
					vk.normalize();
					vk.scale(-1);
					lastGradientDirectionIteration = iterNumber;
				}
			}
		} else {
			vk = new GVector(gk);
			vkminus1 = new GVector(gk.getSize());
			gkminus1 = new GVector(gk);
		
			vk.normalize();
			vk.scale(-1);
			//logger.debug("vectorvk : " + vk);
		}
		return;
		*/
	}
}

