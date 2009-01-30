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
 *  Find a decrease direction of the energy fuction from a point of the 3xN coordinates space using the conjugate gradient approach.
 *
 *@author     vlabarta
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 *
 */
public class ConjugateGradientMethod {
	double uk_FletcherReeves = 0;
	double uk_PolankRibiere = 0;
	GVector conjugatedGradientDirection = null;
	GVector previousConjugatedGradientDirection = null;
	boolean orthogonalDirectionsProperty = true;
	GVector diffgk_gkminus1 = null;

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
	public void initialize(GVector gradient) {
		conjugatedGradientDirection = new GVector(gradient);
		//conjugatedGradientDirection.normalize();
		conjugatedGradientDirection.scale(-1);

		previousConjugatedGradientDirection = new GVector(gradient.getSize());
		diffgk_gkminus1 = new GVector(gradient.getSize());

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
	 *  Polak-Ribiere plus: uk = Max(0, (gk - gk-1) gk / gk-1 gk-1)
	 *
	 */
	public void setPolankRibierePlus_uk(GVector gkminus1, GVector gk) {
		diffgk_gkminus1.set(gk);
		diffgk_gkminus1.sub(gkminus1);
		uk_PolankRibiere = Math.max(0, diffgk_gkminus1.dot(gk) / gkminus1.dot(gkminus1));
		if (uk_PolankRibiere == 0) {
			//logger.debug("uk_PolankRibiere == 0");
		}
		return;
	}


	/**
	 *  Check if two consecutive conjugate gradient direction are mutually orthogonal.
	 *
	 * @param  pkminus1  	Conjugate Gradient direction at coordinates Xk-1
	 * @param  pk  		Conjugate Gradient direction at coordinates Xk
	 */
	private void checkingOrthogonality(GVector pkminus1, GVector pk) {
		//logger.debug("Math.abs(pk.dot(pkminus1)) / Math.pow(pk.norm(),2) = " + Math.abs(pk.dot(pkminus1)) / Math.pow(pk.norm(),2));
		//logger.debug("Math.abs(pk.dot(pkminus1)) / Math.pow(pk.normSquared(),2) = " + Math.abs(pk.dot(pkminus1)) / Math.pow(pk.normSquared(),2));
		if (Math.abs(pk.dot(pkminus1)) / Math.pow(pk.normSquared(),2) >= 0.1) {
			orthogonalDirectionsProperty = false;
			//logger.debug("orthogonalDirectionsProperty = false");
		} else {orthogonalDirectionsProperty = true;}
	}


	/**
	 *  Restart conjugate gradient direction: assign the gradient of xk as conjugate gradient direction.
	 *
	 * @param  gk  		gradient at coordinates Xk
	 */
	private void restartConjugateGradient(GVector gk) {
		conjugatedGradientDirection.set(gk);
		//conjugatedGradientDirection.normalize();
		conjugatedGradientDirection.scale(-1);
		//logger.debug("vectorvk : " + direction);
	}


	/**
	 *  Set the new direction conjugated to the previous direction: vk=-gk + uk vk-1
	 *
	 * @param  gradient  				gradient at coordinates Xk
	 * @param  previousGradient  		gradient at coordinates Xk-1
	 */
	private void setConjugateGradientDirection(GVector gradient, GVector previousGradient) {
		setPolankRibierePlus_uk(previousGradient,gradient);
		previousConjugatedGradientDirection.scale(uk_PolankRibiere);
		conjugatedGradientDirection.set(gradient);
		//conjugatedGradientDirection.normalize();
		conjugatedGradientDirection.scale(-1);
		conjugatedGradientDirection.add(previousConjugatedGradientDirection);
		previousConjugatedGradientDirection.scale(1/uk_PolankRibiere);
		//direction.normalize();
		//logger.debug("vector direction : " + direction);
	}


	/**
	 *  Calculate the conjugate gradient direction.
	 *
	 * @param  gradient  			Energy function gradient at coordinates Xk
	 * @param  previousGradient  	Energy function gradient at coordinates Xk-1
	*/
	public void setDirection(GVector gradient, GVector previousGradient) {
		previousConjugatedGradientDirection.set(conjugatedGradientDirection);
		setConjugateGradientDirection(gradient, previousGradient);
		checkingOrthogonality(previousConjugatedGradientDirection,conjugatedGradientDirection);
		if (orthogonalDirectionsProperty == false) {restartConjugateGradient(gradient);}
	}
			

}

