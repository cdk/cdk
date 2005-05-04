package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Methods of Newton-Raphson approach.
 *
 *@author     vlabarta
 *@cdk.module     builder3d
 *
 */
public class NewtonRaphsonMethod {
	GVector gradientPerInverseHessianVector = null;
	private LoggingTool logger;


	/**
	 *  Constructor for the NR object
	 */
	public NewtonRaphsonMethod() {        
		logger = new LoggingTool(this);
	}


	public void hessianEigenValues(double[] forMatrix, int size) {
		
		Matrix matrixForEigenValuesCalculation = new Matrix(forMatrix, size);
		EigenvalueDecomposition eigenValues = new EigenvalueDecomposition(matrixForEigenValuesCalculation);
		eigenValues = matrixForEigenValuesCalculation.eig();
		double[] realEigenvalues = eigenValues.getRealEigenvalues(); 
		double[] imagEigenvalues = eigenValues.getImagEigenvalues(); 
		for (int i=0; i<size ; i++) { 
			logger.debug("Eigen values, real part, i=" + i + " : " + realEigenvalues[i]);
			logger.debug("Eigen values, imaginary part, i=" + i + "  : " + imagEigenvalues[i]);
		}
	}


	public void gradientPerInverseHessian(GVector gradientk, GMatrix hessiank) {
		
		double [][] forDeterminatCalculation = new double[gradientk.getSize()][];
		for (int i = 0; i < gradientk.getSize(); i++) {
			forDeterminatCalculation[i] = new double[gradientk.getSize()];
			for (int j = 0; j < forDeterminatCalculation[i].length;j++) {
				forDeterminatCalculation [i][j] = hessiank.getElement(i,j);
			}
		}

		/*
		logger.debug();
		for (int i = 0; i < forDeterminatCalculation.length; i++) {
			for (int j = 0; j < forDeterminatCalculation[i].length; j++) {
				logger.debug(forDeterminatCalculation[i][j] + " ");
			}
			logger.debug();
		}		
		*/
		
		Matrix matrixForDeterminatCalculation = new Matrix(forDeterminatCalculation);
		//matrixForDeterminatCalculation.print(dimen, dimen);
			
		//logger.debug("matrixForDeterminatCalculation.det() = " + matrixForDeterminatCalculation.det());
		
		if (matrixForDeterminatCalculation.det() != 0) {
			hessiank.invert();
			//logger.debug("hessiank.invert() = " + hessiank);
			gradientPerInverseHessianVector = new GVector(gradientk.getSize());
			gradientPerInverseHessianVector.mul(gradientk, hessiank);
		}
		else {logger.debug("The Newton-Raphson method can't be execute because the hessian can't be inverted");
		}
		return;
	}
	
	
	public GVector getGradientPerInverseHessian(){
		return gradientPerInverseHessianVector;
	}

}

