package org.openscience.cdk.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import Jama.*;
import org.openscience.cdk.*;

/**
 *  Methods of Newton-Raphson approach.
 *
 *@author     vlabarta
 *
 */
public class NewtonRaphsonMethod {
	GVector gradientPerInverseHessianVector = new GVector(3);

	/**
	 *  Constructor for the NR object
	 */
	public NewtonRaphsonMethod() { }


	/**
	 *  Constructor for the NewtonRaphsonMethod object
	 *
	 *@param  coords3d  Coordinates from current point
	 */
	public NewtonRaphsonMethod(GVector coords3d) {
		gradientPerInverseHessianVector.setSize(coords3d.getSize());
	}


	public void gradientPerInverseHessian(GVector gradientOfxk, GMatrix hessianOfxk) {
		
		int dimen = gradientOfxk.getSize();
		
		double [][] forDeterminatCalculation = new double[dimen][];
		for (int i = 0; i < dimen; i++) {
			forDeterminatCalculation[i] = new double[dimen];
			for (int j = 0; j < forDeterminatCalculation[i].length;j++) {
				forDeterminatCalculation [i][j] = hessianOfxk.getElement(i,j);
			}
		}

		/*
		System.out.println();
		for (int i = 0; i < forDeterminatCalculation.length; i++) {
			for (int j = 0; j < forDeterminatCalculation[i].length; j++) {
				System.out.print(forDeterminatCalculation[i][j] + " ");
			}
			System.out.println();
		}		
		*/
		
		Matrix matrixForDeterminatCalculation = new Matrix(forDeterminatCalculation);
		//matrixForDeterminatCalculation.print(dimen, dimen);
			
		//System.out.println("matrixForDeterminatCalculation.det() = " + matrixForDeterminatCalculation.det());
		
		if (matrixForDeterminatCalculation.det() != 0) {
			hessianOfxk.invert();
			//System.out.println("hessianOfxk.invert() = " + hessianOfxk);
			gradientPerInverseHessianVector.mul(gradientOfxk, hessianOfxk);
		}
		else {System.out.println("The Newton-Raphson method can't be execute because the hessian can't be inverted");
		}
		return;
	}
	
	
	public GVector getGradientPerInverseHessian(){
		return gradientPerInverseHessianVector;
	}

}

