/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *   *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.modeling.forcefield;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;

import org.openscience.cdk.modeling.forcefield.*;

/**
 *  Check results of GeometricMinimizer using some examples.
 *
 * @cdk.module test
 *
 *@author     vlabarta
 *@cdk.created    2005-01-17
 */
public class ForceFieldTests extends TestCase {
	
	double[] testResult3C = {0, 0, 0};
	GeometricMinimizer gmo = new GeometricMinimizer();
	
	double[] molecule3Coord = {9, 9, 0};
	GVector molecule3Coordinates = new GVector(molecule3Coord);
	
	TestPotentialFunction tpf = new TestPotentialFunction();


	/**
	 *  Constructor for GeometricMinimizerTest object
	 */
	public  ForceFieldTests(){}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(ForceFieldTests.class);
	}


	/**
	 *  A unit test for JUnit (Steepest Descents Method minimization)
	 */
	public void testSteepestDescentsMinimization(){
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Steepest Descents Minimization");
		
		gmo.setConvergenceParametersForSDM(5,0.001);
		gmo.steepestDescentsMinimization(molecule3Coordinates, tpf);
		
		for (int i=0;i<molecule3Coordinates.getSize();i++){
			assertEquals(testResult3C[i],gmo.getSteepestDescentsMinimum().getElement(i),0.1);
		}
	}


	/**
	 *  A unit test for JUnit (Conjugate Gradient Method minimization)
	 */
	public void testConjugateGradientMinimization(){
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Conjugate Gradient Minimization");
		
		gmo.setConvergenceParametersForCGM(2,0.0001);
		gmo.conjugateGradientMinimization(molecule3Coordinates, tpf);
		
		for (int i=0;i<molecule3Coordinates.getSize();i++){
			assertEquals(testResult3C[i],gmo.getConjugateGradientMinimum().getElement(i),0.00001);
		}
	}


	/**
	 *  A unit test for JUnit (Newton-Raphson Method minimization)
	 */
	public void testNewtonRaphsonMinimization(){
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Newton-Raphson Minimization");
		
		gmo.setConvergenceParametersForNRM(1,0.0001);
		gmo.newtonRaphsonMinimization(molecule3Coordinates, tpf);
		
		for (int i=0;i<molecule3Coordinates.getSize();i++){
			assertEquals(testResult3C[i],gmo.getNewtonRaphsonMinimum().getElement(i),0.00001);
		}
	}


	/**
	 *  A unit test for JUnit with TestPotentialFunction6d
	 */
/*	public void testEnergyMinimization2(){
		System.out.println("");
		System.out.println("");
		double[] testResult6C = {0, 0, 0, 0, 0, 0};
		GeometricMinimizer gmo=new GeometricMinimizer();
		
		double[] molecule6Coord = {9, 9, 9, 8, 8, 8};
		GVector molecule6Coordinates = new GVector(molecule6Coord);
		TestPotentialFunction6d tpf6d = new TestPotentialFunction6d(molecule6Coordinates);

		gmo.energyMinimization(molecule6Coordinates,5,10,50,tpf6d);
		
		for (int i=0;i<molecule6Coordinates.getSize();i++){
			assertEquals(testResult6C[i],gmo.getMinimumCoordinates().getElement(i),0.00001);
		}
	}		
*/

}
