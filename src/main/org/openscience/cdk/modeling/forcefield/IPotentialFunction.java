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

import javax.vecmath.GMatrix;
import javax.vecmath.GVector;


/**
 *  Interface for a potential energy function to optimise in cdk/modeling/forcefield
 *
 * @author     vlabarta
 *@cdk.module     forcefield
 * @cdk.svnrev  $Revision$
 * 
 */

public interface IPotentialFunction {
	String energyFunctionShape = "";
	double energy = 0;			//Energy function in a 3xN point
	GVector energyGradient = null;		//Gradient of the energy function in a 3xN point.
	GMatrix energyHessian = null;
	double[] forHessian = null;
	int functionEvaluationNumber = 0;


	/**
	 *  Evaluate the potential energy function given the cartesian coordinates.
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 * @return    Energy function value in the wished 3xN point.
	 */
	double energyFunction(GVector coords3d);


	/**
	 *  Evaluate the gradient of the potential energy function in a given point.
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 */
	void setEnergyGradient(GVector coords3d);


	/**
	 *  Get the gradient of the potential energy function in a given point.
	 *
	 * @return    Gradient energy value in the wished point.
	 */
	GVector getEnergyGradient();


	/**
	 *  Evaluate the hessian of the potential energy function in a given point.
	 *
	 *@param  coords3d  Current molecule 3xN coordinates.
	 */
	void setEnergyHessian(GVector coords3d);


	/**
	 *  Get the hessian of the potential energy function in a given point.
	 *
	 * @return    Hessian energy value in the wished point.
	 */
	GMatrix getEnergyHessian();


	/**
	 *  Get the hessian of the potential energy function in a given point.
	 *
	 *@return        Hessian energy value in the wished point.
	 */
	double[] getForEnergyHessian();

}
