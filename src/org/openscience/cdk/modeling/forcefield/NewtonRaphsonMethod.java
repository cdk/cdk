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


	/**
	 *  Description of the Method
	 *
	 *@param  gradientk  Description of the Parameter
	 *@param  hessiank   Description of the Parameter
	 */
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


	/**
	 *  Description of the Method
	 *
	 *@param  gradientk  Description of the Parameter
	 *@param  hessiank   Description of the Parameter
	 */
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

