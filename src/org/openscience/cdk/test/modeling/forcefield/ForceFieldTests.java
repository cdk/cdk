package org.openscience.cdk.test.modeling.forcefield;

import java.io.*;
import java.lang.*;
import java.util.*;
import javax.vecmath.*;
import org.openscience.cdk.*;
import org.openscience.cdk.modeling.forcefield.*;

/**
 * "Main" Class
 *
 * @cdk.module applications
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

		/* GeometricMinimizer geometricMinimizerObject = new GeometricMinimizer();
		geometricMinimizerObject.setInitial3dCoordinates();

		// Force field type *** The program has to decide the best force field to be used ***  Waiting to be completed

		PotentialFunction forceFieldFunction = new TestPotentialFunction(geometricMinimizerObject.getInitial3dCoordinates()); */

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

		/* System.out.println("r = " + geometricMinimizerObject.getInitial3dCoordinates());
		System.out.println("Force field function evaluate in " + geometricMinimizerObject.getInitial3dCoordinates() + " : " + forceFieldFunction.functionInPoint(geometricMinimizerObject.getInitial3dCoordinates()));
		System.out.println("Gradient of the force field function evaluate in " + geometricMinimizerObject.getInitial3dCoordinates() + " : " + forceFieldFunction.gradientInPoint(geometricMinimizerObject.getInitial3dCoordinates()));

		System.out.println("");
		System.out.println("START ENERGY OPTIMISATION");
		geometricMinimizerObject.energyOptimization(10,10,50,forceFieldFunction);
		GVector minimum = new GVector(geometricMinimizerObject.getMinimumCoordinates());
		System.out.println("The minimum energy is at " + minimum + " coordinates"); */
	}
}

