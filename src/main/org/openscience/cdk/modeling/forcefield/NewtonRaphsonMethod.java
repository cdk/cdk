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
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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

import org.openscience.cdk.tools.LoggingTool;

import Jama.Matrix;

/**
 * Methods of Newton-Raphson approach.
 *
 * @author        vlabarta
 * @cdk.created   2005-06-01
 * @cdk.module    forcefield
 * @cdk.svnrev  $Revision$
 * 
 * @cdk.keyword   Newton-Raphson
 */
public class NewtonRaphsonMethod {
	GVector gradientPerInverseHessianVector = null;
	Matrix matrixForDeterminatCalculation = null;
	private LoggingTool logger;


	/**
	 *  Constructor for the NR object
	 */
	public NewtonRaphsonMethod() {
		logger = new LoggingTool(this);
	}


	/**
	 *  Calculate the eigen values for the hessian matrix.
	 *
	 *@param  forMatrix  Hessian matrix
	 *@param  size       coordinates dimension
	 */
	public void hessianEigenValues(double[] forMatrix, int size) {
		Matrix A = new Matrix(forMatrix, size);
		Matrix As = A.plus(A.transpose());	// Simetric matrix: As = 1/2 * (A + AT);
		As.timesEquals(0.5); 
		//logger.debug("Simetric matrix Hs = 1/2 * (H + HT) = ");
		//As.print(As.getRowDimension(), As.getColumnDimension());
		double[] realEigenvalues = As.eig().getRealEigenvalues();
		double[] imagEigenvalues = As.eig().getImagEigenvalues();
		System.out.println(" ");
		System.out.println("Hs EigenValues :");
		for (int i=0; i < As.getColumnDimension(); i++) {
			System.out.println("Eigen value " + i + ": real part = " + realEigenvalues[i]);
			System.out.println(", imaginary part = " + imagEigenvalues[i]);
		 }

	}

    public void determinat(GVector gradientk, GMatrix hessiank) {
		//logger.debug(" ");
		//logger.debug("calculate hessian determinat: ");
		double[][] forDeterminatCalculation = new double[gradientk.getSize()][];
		for (int i = 0; i < gradientk.getSize(); i++) {
			forDeterminatCalculation[i] = new double[gradientk.getSize()];
			for (int j = 0; j < forDeterminatCalculation[i].length; j++) {
				forDeterminatCalculation[i][j] = hessiank.getElement(i, j);
			}
		}

		//logger.debug("gradientk.getSize() = " + gradientk.getSize());
		/*
		 *  if (gradientk.getSize() == 36) {
		 *  logger.debug();
		 *  for (int i = 0; i < forDeterminatCalculation.length; i++) {
		 *  for (int j = 0; j < forDeterminatCalculation[i].length; j++) {
		 *  logger.debug(forDeterminatCalculation[i][j] + " ");
		 *  }
		 *  logger.debug();
		 *  }
		 *  }
		 */
		matrixForDeterminatCalculation = new Matrix(forDeterminatCalculation);
		//matrixForDeterminatCalculation.print(gradientk.getSize(), gradientk.getSize());

		//logger.debug("matrixForDeterminatCalculation.det() = " + matrixForDeterminatCalculation.det());

		return;
	}

	public void gradientPerInverseHessian(GVector gradientk, GMatrix hessiank) {
		this.determinat(gradientk, hessiank);
		if (matrixForDeterminatCalculation.det() != 0) {
			hessiank.invert();
			//logger.debug("hessiank.invert() = " + hessiank);
			gradientPerInverseHessianVector = new GVector(gradientk.getSize());
			gradientPerInverseHessianVector.mul(gradientk, hessiank);
		} else {
			logger.debug("The Newton-Raphson method can't be execute because the hessian can't be inverted");
		}
		return;
	}


	/**
	 *  Gets the gradientPerInverseHessian attribute of the NewtonRaphsonMethod
	 *  object
	 *
	 *@return    The gradientPerInverseHessian value
	 */
	public GVector getGradientPerInverseHessian() {
		return gradientPerInverseHessianVector;
	}

}

