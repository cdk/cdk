package org.openscience.cdk.test.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.forcefield.*;
/**
 *  "Main" Class
 *
 *@author     labarta
 *@created    2004-12-03
 */
public class ForceFieldTests {

	ForceFieldTests(){}
	/**
	 *  The main program for the ForceFieldTests class
	 *
	 *@param  arg  The command line arguments
	 */
	public static void main(String[] arg) {
		/*
		 *  throws Exception
		 */
		System.out.println("START FORCEFIELDTESTS");

		//	Get atoms coordinates and force field function

		GeometricMinimizer geometricMinimizerObject = new GeometricMinimizer();
		geometricMinimizerObject.initialize();

		// Force field type *** The program has to decide the best force field to be used ***  Waiting to be completed
		TestPotentialFunction forceFieldFunction = new TestPotentialFunction(geometricMinimizerObject.getInitial3dCoordinates());
		/*int forceFieldCode = 1;
		switch (forceFieldCode) {
						case 1:
							TestPotentialFunction forceFieldFunction = new TestPotentialFunction(geometricMinimizerObject.initialCoordinates);
							break;
						//case 2:  ForceField1 forceFieldFunction = new ForceField1(geometricMinimizerObject.initialCoordinates); break;
						//case 3:  ForceField2 forceFieldFunction = new ForceField2(geometricMinimizerObject.initialCoordinates); break;
						default:
							System.out.println("Hey, that's not a valid force field code!");
							break;
		}*/
		System.out.println("Force field function : " + forceFieldFunction.functionShape);
		System.out.println("Gradient of the force field function : " + forceFieldFunction.gradientShape);
		System.out.println("r = " + geometricMinimizerObject.getInitial3dCoordinates());
		System.out.println("Wished point size : " + forceFieldFunction.wishedPoint.getSize());
		System.out.println("Wished point = " + forceFieldFunction.wishedPoint);
		System.out.println("Force field function evaluate in " + geometricMinimizerObject.getInitial3dCoordinates() + " : " + forceFieldFunction.evaluateInPoint(geometricMinimizerObject.getInitial3dCoordinates()));
		System.out.println("Gradient of the force field function evaluate in " + geometricMinimizerObject.getInitial3dCoordinates() + " : " + forceFieldFunction.gradientInPoint(geometricMinimizerObject.getInitial3dCoordinates()));

		System.out.println("");
		System.out.println("Started energy optimisation");
		GVector kplus1Coordinates = new GVector(geometricMinimizerObject.getInitial3dCoordinates());
		GVector kCoordinates = new GVector(geometricMinimizerObject.getInitial3dCoordinates().getSize());
		int SDMaxIter = 100;
		int iterationNumber=0;
		boolean convergenceSD;
		boolean convergenceIt;
		boolean convergence = false;
		GVector gK = new GVector(geometricMinimizerObject.getInitial3dCoordinates().getSize());
		
		System.out.println("");
		System.out.println("FORCEFIELDTESTS steepestDescentTest");
		
		SteepestDescentsMethod sdm = new SteepestDescentsMethod(geometricMinimizerObject.getInitial3dCoordinates());
		
		do { 
			System.out.println("");
			System.out.println("Iteration number: " + iterationNumber);
			gK.set(forceFieldFunction.gradientInPoint(kCoordinates));
			sdm.vectorSkCalculation(gK);
			System.out.println("Step size : " + sdm.getStepSize());
			kplus1Coordinates.set(sdm.newCoordinates(kCoordinates));
			System.out.println("New coordinates : " + kplus1Coordinates);
			System.out.println("");
			System.out.println("Slope of the line in the direction of the gradient in " + kCoordinates + " : " + forceFieldFunction.slopeInPoint(kCoordinates));
			iterationNumber = iterationNumber + 1;
			convergenceSD = geometricMinimizerObject.checkConvergence(forceFieldFunction.evaluateInPoint(kCoordinates), forceFieldFunction.evaluateInPoint(kplus1Coordinates));
			//convergenceIt = iterationNumber.equals(SDMaxIter);
			if ((convergenceSD == true) | (iterationNumber == 100)) {
				convergence = true;
			}
			else {
				kCoordinates.set(kplus1Coordinates);
			}	

		} while  (convergence != true);
	}
}

