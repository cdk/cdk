/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  *
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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.modeling.forcefield;

import java.util.Hashtable;

import javax.vecmath.GVector;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.AtomTools;
import org.openscience.cdk.modeling.forcefield.AngleBending;
import org.openscience.cdk.modeling.forcefield.BondStretching;
import org.openscience.cdk.modeling.forcefield.ElectrostaticInteractions;
import org.openscience.cdk.modeling.forcefield.ForceFieldTools;
import org.openscience.cdk.modeling.forcefield.GeometricMinimizer;
import org.openscience.cdk.modeling.forcefield.MMFF94EnergyFunction;
import org.openscience.cdk.modeling.forcefield.SmoothingFunctions;
import org.openscience.cdk.modeling.forcefield.StretchBendInteractions;
import org.openscience.cdk.modeling.forcefield.Torsions;
import org.openscience.cdk.modeling.forcefield.VanDerWaalsInteractions;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Check results of GeometricMinimizer using some examples.
 *
 *@author         vlabarta
 *@cdk.module     test
 *@cdk.created    2005-01-17
 */
public class ForceFieldTests extends CDKTestCase {

	AtomContainer ac = null;
	GVector acCoordinates = new GVector(3);
	GeometricMinimizer gm = new GeometricMinimizer();
	Hashtable mmff94Tables = null;

	double[] molecule3Coord = {9, 9, 0};
	GVector molecule3Coordinates = new GVector(molecule3Coord);

	TestPotentialFunction tpf = new TestPotentialFunction();
	double[] testResult3C = {0, 0, 0};
	
	ForceFieldTools ffTools = new ForceFieldTools();

	private LoggingTool logger;


	/**
	 *  Constructor for GeometricMinimizerTest object
	 */
	public ForceFieldTests() {
		logger = new LoggingTool(this);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(ForceFieldTests.class);
	}


	/**
	 *  A unit test for JUnit (Steepest Descents Method minimization)
	 */
	public void testSteepestDescentsMinimization() {
		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with Steepest Descents Minimization");

		gm.setConvergenceParametersForSDM(100, 0.00001);
		gm.steepestDescentsMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getSteepestDescentsMinimum().getElement(i), 0.00001);
		}
	}


    	/**
	 *  A unit test for JUnit (MMFF94EnergyFunction minimization with Steepest Descents Method)
	 */
/*	public void testMMFF94EnergyFunctionMinimizationWithSteepestDescentsMethod()  throws Exception {
		//logger.debug("\n\nFORCEFIELDTESTS: MMFF94EnergyFunction minimization with Steepest Descents Method");
		
		createTestMoleculeAndSetMMFF94Parameters();

		//logger.debug("Molecule created:"+ac.getAtomCount()+" Size Table:"+mmff94Tables.size());
		MMFF94EnergyFunction mmff94EF = new MMFF94EnergyFunction(ac,mmff94Tables);
		//logger.debug("EnergyFunction is set");
		
		gm.setConvergenceParametersForSDM(100000, 0.0000000000000001);
		//logger.debug("SDM Parameters are set");
		gm.steepestDescentsMinimization(acCoordinates, mmff94EF);
		
*/		//logger.debug("gm.getSteepestDescentsMinimum() : " + gm.getSteepestDescentsMinimum());
		/*for (int i = 0; i < acCoordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getSteepestDescentsMinimum().getElement(i), 0.1);
		}*/
//	}


	/**
	 *  A unit test for JUnit (Conjugate Gradient Method minimization)
	 */
	public void testConjugateGradientMinimization() {
		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with Conjugate Gradient Minimization");

		gm.setConvergenceParametersForCGM(100, 0.00001);
		gm.conjugateGradientMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getConjugateGradientMinimum().getElement(i), 0.00001);
		}
	}


    	/**
	 *  A unit test for JUnit (MMFF94EnergyFunction minimization with Conjugate Gradient Method)
	 */
	public void testMMFF94EnergyFunctionMinimizationWithConjugateGradientMethod()  throws Exception {
    	if (!this.runSlowTests()) fail("Slow tests turned of");
    	
		double[] testResult = {-0.07595612546512087,-0.2802911393253983,0.07600307966977722,0.39068800204256576,
			-1.059191412353933,-1.1279995182727496,-0.8886677181682512,-0.8100194941301619,0.5795261081533215,
			0.7455735487932257,-0.14810094365682494,0.7847092409972136,-0.43744626781215895,0.7059121981158619,
			-0.2262853680234533,0.752138588967976,-2.0454101668080065,-0.8257161752480399,-0.43083068038259337,
			-1.1913450770810514,-1.836724982022171,1.2034283588944206,-0.5294834493958368,-1.6314983633569509};
	
		logger.debug("\n\nFORCEFIELDTESTS: MMFF94EnergyFunction minimization with Conjugate Gradient Method");
		
		createTestMoleculeAndSetMMFF94Parameters();

		//logger.debug("Molecule created:"+ac.getAtomCount()+" Size Table:"+mmff94Tables.size());
		MMFF94EnergyFunction mmff94EF = new MMFF94EnergyFunction(ac,mmff94Tables);
		//logger.debug("EnergyFunction is set");
		
		gm.setConvergenceParametersForCGM(1000, 0.0000001);
		//logger.debug("CGM Parameters are set");
		gm.conjugateGradientMinimization(acCoordinates, mmff94EF);
		
		//logger.debug("gm.getConjugateGradientMinimum() : " + gm.getConjugateGradientMinimum());
		/*for (int i = 0; i < acCoordinates.getSize(); i++) {
			assertEquals(testResult[i], gm.getConjugateGradientMinimum().getElement(i), 0.1);
		}*/
	}


    	/**
	 *  A unit test for JUnit (It compare minimization of MMFF94EnergyFunction with SDM And CGM)
	 */
/*	public void testMMFF94EnergyFunctionCompareMinimizationWithSDMAndCGM()  throws Exception {
		logger.debug("\n\nFORCEFIELDTESTS: Compare minimization of MMFF94EnergyFunction with SDM And CGM");
		
		createTestMoleculeAndSetMMFF94Parameters();

		logger.debug("Molecule created:"+ac.getAtomCount()+" Size Table:"+mmff94Tables.size());
		MMFF94EnergyFunction mmff94EF = new MMFF94EnergyFunction(ac,mmff94Tables);
		logger.debug("EnergyFunction is set");
		
		gm.setConvergenceParametersForSDM(20, 0.0001);
		logger.debug("SDM Parameters are set");
		gm.steepestDescentsMinimization(acCoordinates, mmff94EF);
		GVector steepestDescentsMinimum = new GVector(gm.getSteepestDescentsMinimum());
		
		gm.setConvergenceParametersForCGM(20, 0.0001);
		logger.debug("CGM Parameters are set");
		gm.conjugateGradientMinimization(acCoordinates, mmff94EF);
		GVector conjugateGradientMinimum = new GVector(gm.getConjugateGradientMinimum());

		logger.debug("steepestDescentsMinimum = " + steepestDescentsMinimum);
		logger.debug("conjugateGradientMinimum = " + conjugateGradientMinimum);

		double RMSD = 0;
		double d = 0;
		int atomNumbers = steepestDescentsMinimum.getSize() / 3;
		logger.debug("atomNumbers = " + atomNumbers);
		for (int i = 0; i < atomNumbers; i++) {
			d = ffTools.distanceBetweenTwoAtomFromTwo3xNCoordinates(steepestDescentsMinimum, conjugateGradientMinimum, i, i);
			RMSD = RMSD + Math.pow(d, 2);
		}
		RMSD = RMSD / conjugateGradientMinimum.getSize();
		RMSD = Math.sqrt(RMSD);
		System.out.print("RMSD (SDM, CGM) = " + RMSD);
	}
*/

	/**
	 *  A unit test for JUnit (Newton-Raphson Method minimization)
	 */
	public void testNewtonRaphsonMinimization() {
		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with Newton-Raphson Minimization");

		gm.setConvergenceParametersForNRM(1000, 0.000000000000001);
		gm.newtonRaphsonMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getNewtonRaphsonMinimum().getElement(i), 0.00001);
		}
	}


    	/**
	 *  A unit test for JUnit (MMFF94EnergyFunction minimization with Newton-Raphson Method)
	 */
/*	public void testMMFF94EnergyFunctionMinimizationWithNewtonRaphsonMethod()  throws Exception {
		logger.debug("\n\nFORCEFIELDTESTS: MMFF94EnergyFunction minimization with Newton-Raphson Method");
		
		createTestMoleculeAndSetMMFF94Parameters();

		logger.debug("Molecule created:"+ac.getAtomCount()+" Size Table:"+mmff94Tables.size());
		MMFF94EnergyFunction mmff94EF = new MMFF94EnergyFunction(ac,mmff94Tables);
		logger.debug("EnergyFunction is set");
		
		gm.setConvergenceParametersForSDM(5, 0.001);
		logger.debug("SDM Parameters are set");
		gm.steepestDescentsMinimization(acCoordinates, mmff94EF);
		
		gm.setConvergenceParametersForNRM(20, 0.0001);
		logger.debug("NRM Parameters are set");
		gm.newtonRaphsonMinimization(acCoordinates, mmff94EF);
		
		logger.debug("gm.getNewtonRaphsonMinimum() : " + gm.getNewtonRaphsonMinimum());
*/		/*for (int i = 0; i < acCoordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getConjugateGradientMinimum().getElement(i), 0.1);
		}*/
/*	}
*/

	/**
	 *  Description of the Method
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void createTestMoleculeAndSetMMFF94Parameters() throws ClassNotFoundException, CDKException, java.lang.Exception {
		HydrogenAdder hAdder = new HydrogenAdder();
		SmilesParser sp = new SmilesParser();
		ac = sp.parseSmiles("CC");
		hAdder.addExplicitHydrogensToSatisfyValency((Molecule) ac);
		org.openscience.cdk.interfaces.IAtom a = new Atom();
		a = ac.getAtomAt(0);
		Point3d atomCoordinate0 = new Point3d(1, 0, 0);
		a.setPoint3d(atomCoordinate0);
		ac.setAtomAt(0, a);
		a = ac.getAtomAt(1);
		Point3d atomCoordinate1 = new Point3d(2, 0, 0);
		a.setPoint3d(atomCoordinate1);
		ac.setAtomAt(1, a);
		AtomTools.add3DCoordinates1(ac);

		ForceFieldTools ffTools = new ForceFieldTools();
		acCoordinates.setSize(ac.getAtomCount() * 3);
		//acCoordinates.set(ffTools.getCoordinates3xNVector(ac));
		double[] m = new double[ac.getAtomCount() * 3];
		m[0] = 0.0070;
		m[1] = -0.0040;
		m[2] = 0.0030;
		m[3] = 0.5120;
		m[4] = -0.7200;
		m[5] = -1.2490;
		m[6] = -0.2850;
		m[7] = -0.7200;
		m[8] = 0.7260;
		m[9] = 0.5440;
		m[10] = -0.1890;
		m[11] = 0.8660;
		m[12] = -0.8240;
		m[13] = 0.6030;
		m[14] = -0.2490;
		m[15] = 0.1880;
		m[16] = -2.5140;
		m[17] = -1.0130;
		m[18] = -0.4010;
		m[19] = -1.3690;
		m[20] = -1.8600;
		m[21] = 1.5180;
		m[22] = -0.4450;
		m[23] = -1.4320;
		acCoordinates.set(m);
		//logger.debug("acCoordinates : " + acCoordinates);

		gm.setMMFF94Tables(ac);
		mmff94Tables = gm.getPotentialParameterSet();

	}


	/**
	 *  A unit test for JUnit (BondStretching)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testBondStretching() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with Bond Stretching");

		double testResult_SumEB = 164.37972112718;
		double[] testResult_gradientSumEB = {-5.455159324048518,-6.483014473192981,62.37549096020401,205.23205964792473,
				716.6248883628301,-66.50259680461075,7.215112040885087,17.691850072855214,-17.864815087533966,
				-25.53709462892573,8.797695542553555,-41.040060828236314,19.93844402757681,-14.563941666352736,
				6.046315156377083,-122.18702194641659,-676.5540659625659,89.00042339306896,-54.54280594897065,
				-38.771392180593594,-36.50126444120599,-24.663533868025148,-6.742019695533713,4.486507651936976};
		double[] testResult_hessianSumEB = {3627.3781763038755, -1413.262187101324, -1413.2621871013237, -3534.342675037568, 1160.7508358744321,
				1160.7508358744321, -31.011833755435887, 429.7173029021638, 84.17045040896397, -31.011833755435887,
				-88.60297583763585, -215.08190204790128, -31.011833755435887, -88.60297583763602, 383.42280286582906, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-1413.262187101324, 141.69864911807622, -1413.2621871013243, 1160.7508358744321, 1160.7508358744321,
				1160.7508358744321, 429.71730290216374, -952.4701070706362, 84.17045040896397, -88.60297583763585,
				-174.98968896093584, -364.70807827633394, -88.60297583763602, -174.98968896093635, 533.0489790942622,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-1413.262187101324, -1413.2621871013243, 141.69864911807548, 1160.7508358744321, 1160.7508358744321,
				1160.7508358744321, 84.17045040896397, 84.17045040896397, 84.17045040896397, -215.08190204790122, -364.70807827633394,
				-693.309967700736, 383.42280286582906, 533.0489790942622, -693.3099677007356, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0,
				-3534.342675037568, 1160.7508358744321, 1160.7508358744321, 3627.3781763038755, -1413.2621871013246,
				-1413.2621871013241, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -31.011833755435717, -261.37640208423556,
				84.17045040896397, -31.011833755435674, 256.94387665556394, -215.08190204790114, -31.011833755435674,
				256.94387665556417, 383.4228028658292,
				1160.7508358744321, 1160.7508358744321, 1160.7508358744321, -1413.2621871013248, 141.6986491180764,
				-1413.2621871013241, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -261.37640208423556, -952.4701070706362,
				84.17045040896397, 256.94387665556394, -174.98968896093587, 533.0489790942623, 256.94387665556417, -174.9896889609365,
				-364.7080782763345,
				1160.7508358744321, 1160.7508358744321, 1160.7508358744321, -1413.2621871013241, -1413.2621871013241, 
				141.6986491180766, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 84.17045040896397, 84.17045040896397, 
				84.17045040896397, -215.08190204790114, 533.0489790942624, -693.3099677007366, 383.4228028658292, 
				-364.7080782763345, -693.309967700736,
				-31.011833755435887, 429.7173029021638, 84.17045040896397, 0.0, 0.0, 0.0, 31.011833755435887, -429.7173029021638, 
				-84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				429.71730290216374, -952.4701070706362, 84.17045040896397, 0.0, 0.0, 0.0, -429.71730290216374, 952.4701070706362, 
				-84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				84.17045040896397, 84.17045040896397, 84.17045040896397, 0.0, 0.0, 0.0, -84.17045040896397, -84.17045040896397, 
				-84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-31.011833755435887, -88.60297583763585, -215.08190204790128, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 31.011833755435887, 
				88.60297583763585, 215.08190204790128, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-88.60297583763585, -174.98968896093584, -364.70807827633394, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 88.60297583763585, 
				174.98968896093584, 364.70807827633394, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-215.08190204790122, -364.70807827633394, -693.309967700736, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 215.08190204790122, 
				364.70807827633394, 693.309967700736, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-31.011833755435887, -88.60297583763602, 383.42280286582906, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 31.011833755435887, 
				88.60297583763602, -383.42280286582906, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				-88.60297583763602, -174.98968896093635, 533.0489790942622, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 88.60297583763602, 
				174.98968896093635, -533.0489790942622, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				383.42280286582906, 533.0489790942622, -693.3099677007356, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -383.42280286582906, 
				-533.0489790942622, 693.3099677007356, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, -31.011833755435717, -261.37640208423556, 84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				31.011833755435717, 261.37640208423556, -84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, -261.37640208423556, -952.4701070706362, 84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 261.37640208423556, 952.4701070706362, -84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 84.17045040896397, 84.17045040896397, 84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				-84.17045040896397, -84.17045040896397, -84.17045040896397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, -31.011833755435674, 256.94387665556394, -215.08190204790114, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 31.011833755435674, -256.94387665556394, 215.08190204790114, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 256.94387665556394, -174.98968896093587, 533.0489790942623, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, -256.94387665556394, 174.98968896093587, -533.0489790942623, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, -215.08190204790114, 533.0489790942624, -693.3099677007366, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 215.08190204790114, -533.0489790942624, 693.3099677007366, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, -31.011833755435674, 256.94387665556417, 383.4228028658292, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 31.011833755435674, -256.94387665556417, -383.4228028658292,
				0.0, 0.0, 0.0, 256.94387665556417, -174.9896889609365, -364.7080782763345, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -256.94387665556417, 174.9896889609365, 364.7080782763345,
				0.0, 0.0, 0.0, 383.4228028658292, -364.7080782763345, -693.309967700736, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -383.4228028658292, 364.7080782763345, 693.309967700736};

		createTestMoleculeAndSetMMFF94Parameters();

		BondStretching bs = new BondStretching();
		bs.setMMFF94BondStretchingParameters(ac, mmff94Tables);

		//logger.debug("bs.functionMMFF94SumEB(acCoordinates) = " + bs.functionMMFF94SumEB(acCoordinates));
		assertEquals(testResult_SumEB, bs.functionMMFF94SumEB(acCoordinates), 0.00001);

		bs.setGradientMMFF94SumEB(acCoordinates);
		//logger.debug("gradientMMFF94SumEB = " + bs.getGradientMMFF94SumEB());
		for (int i = 0; i < testResult_gradientSumEB.length; i++) {
			assertEquals(testResult_gradientSumEB[i], bs.getGradientMMFF94SumEB().getElement(i), 0.00001);
		}

		//bs.setHessianMMFF94SumEB(acCoordinates);
		//logger.debug("HessianMMFF94SumEB = " + bs.getHessianMMFF94SumEB());
		/*for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				assertEquals(testResult_hessianSumEB[i * 24 + j], bs.getHessianMMFF94SumEB().getElement(i, j), 0.00001);
			}
		}*/

	}


	/**
	 *  A unit test for JUnit (AngleBending)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testAngleBending() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with Angle Bending");

		double testResult_SumEA = 5187817.002469799;
		double[] testResult_gradientSumEA = {4568.596414777739,27508.49758516811,-142367.70349663895,7166.3094395713815,
				105150.19711133208,50811.180362133724,94769.17969944769,27001.645461545922,65014.90819184729,
				-98922.6188032544,-64571.88142024592,47712.222751566784,-1621.7418580498224,9917.470024933295,
				29236.395988785414,-50985.787052799555,3086.3777498925174,-46535.734414406164,45406.860296020466,
				-89088.7752674958,26779.29901528328,-380.7981357134522,-19003.531245130223,-30650.568398571373};
		double[] testResult_hessianSumEA = {1568.8626742584759, 3906.165743403598, 6243.895379734757, 6243.895379734757, 
				3023.680810562986, -196.53375860878458, -719.5480795303931, -671.5310330601114, -987.2941765121353, 
				-1510.2484012649602, -2007.8114954249556, -2638.4613903167074, -3161.3555189007498, -3658.8284688075746, 
				-3659.8609952463735, -3659.8609952463735, -3049.8841627053116, -2439.9073301642497, -2439.9073301642497, 
				-1829.9304976231876, -1219.9536650821256, -1219.9536650821256, -609.9768325410635, -1.3642420526593924E-12,
				2337.30306914512, 5508.940790537674, 7846.748494082077, 4626.533924910307, 1515.6999508848735, -1704.5146182868968,
				-2030.203681659631, -2179.803575315665, -2495.5667187676827, -2806.2767580061704, -3186.4525601569794, -3368.7803719178787,
				-3679.400266903195, -4059.4408526742423, -4508.873529457143, -4206.637693789036, -4104.057361498051, -3494.0805289569894,
				-2730.23319797945, -2357.0170970195577, -1590.7886305851607, -826.9412996076209, -453.7251986477287, -1.5916157281026244E-12,
				2337.7296363311575, 4675.53733987556, 7846.363762989538, 4626.149193817768, 1405.9346246459975, -1704.899349379436,
				-2020.662492831459, -2336.4256362834785, -2793.6403631055605, -3100.6512733238037, -3282.979085084703, -3509.077037892962,
				-3833.74854900527, -4283.181225788169, -4508.873529457141, -3898.896696916079, -3288.919864375017, -2825.491840525489,
				-2482.026529062354, -1715.7980626279568, -1523.0058074473363, -646.5174538283478, -192.7922551806207, 9.947598300641403E-14,
				2.229971365627168E-14, -3220.214569171771, -6440.429138343542, -4871.566464085068, -2533.7225294209047, -195.99289308974127,
				-195.99289308974127, 413.9839394513208, 1023.9607719923829, 1023.9607719923829, 1633.937604533445, 2243.914437074507,
				2243.914437074507, 2853.8912696155694, 3463.8681021566317, 2940.853781235023, 2261.3104478607006, 1945.547304408674,
				1422.5930796558494, 1288.6298869118068, 657.9799920200551, 135.0858634360128, 1.0325264387981685, -3.007372129104624E-12,
				-3220.214569171774, -6331.048543197208, -9551.263112368979, -7213.4191777048145, -4041.7814563122574, -1704.4421560473695,
				-786.7243266333523, -684.1439943423675, -74.16716180130538, 381.9391723032793, 755.1552732631717, 1208.880471910899,
				1664.9868060154834, 2038.2029069753755, 2804.4313734097727, 2498.594149878467, 2348.9942562224323, 2033.231112770407,
				1712.4148651048574, 1332.2390629540487, 883.0405878109104, 562.1341958921864, 182.09361012113922, -7.830402992681229E-13,
				-3220.214569171772, -6440.429138343543, -9551.263112368977, -7213.533476037814, -4876.1941757729255, -1705.3677526589502,
				-1095.390920117888, -485.414087576826, -21.986063727297903, 321.47924773583765, 775.2044463835648, 967.9967015641854,
				1844.485055183174, 2610.713521617571, 2803.505776798192, 2487.742633346166, 2171.9794898941436, 1714.7647630720633,
				1407.7538528538205, 958.5553777106823, 732.4574249024234, 407.7859137901125, 225.6923036689695, -1.852074049679686E-12,
				-523.0143209216104, -1286.5469253054505, -2040.1536097685798, -2040.1536097685798, -1122.4357803545627, -512.4589478135006,
				42.189525290001846, -165.09810023463334, -19.150253230929444, -34.96732932187686, -8.416129074649952, -19.485621341763277,
				-35.30269743270969, -8.751497185478286, -1.7814638653135262E-12, -1.7814638653135262E-12, -1.7814638653135262E-12, -1.7814638653135262E-12,
				-1.7814638653135262E-12, -1.7814638653135262E-12, -1.7814638653135262E-12, -1.7814638653135262E-12, -1.7814638653135262E-12, -1.7814638653135262E-12,
				-389.82649454082605, -539.4263881968604, -1293.0330726599855, -683.0562401189234, -580.4759078279385, 29.500924713123595,
				-177.7867008115116, -149.2406046384941, -3.2927576347933996, -9.724113872596408, -0.4873812765806063, -4.94987705949874,
				-11.381233297298408, -2.1445007012821056, -1.63158375698913E-12, -1.63158375698913E-12, -1.63158375698913E-12, -1.63158375698913E-12,
				-1.63158375698913E-12, -1.63158375698913E-12, -1.63158375698913E-12, -1.63158375698913E-12, -1.63158375698913E-12, -1.63158375698913E-12,
				-753.6066844631316, -1507.2133689262548, -1964.4280957483365, -1354.4512632072745, -744.4744306662124, -281.0464068166843,
				-135.09855981298037, 10.849287190720323, 44.498535543932434, 14.386555411625046, 67.05152322428914, 47.12025053396001,
				74.91423558463123, 19.931272690327525, -1.785516179353408E-12, -1.785516179353408E-12, -1.785516179353408E-12, -1.785516179353408E-12,
				-1.785516179353408E-12, -1.785516179353408E-12, -1.785516179353408E-12, -1.785516179353408E-12, -1.785516179353408E-12, -1.785516179353408E-12,
				-522.9542247528267, -1271.5078050024183, -2016.3622562317644, -2016.3622562317644, -1560.2559221271797, -1216.7906106640442,
				-1232.6076867549916, -1239.0390429927945, -1269.151023125102, -714.5626461903823, -392.0872071756872, 59.61539812884539,
				43.798322037897975, 20.20148540601246, -2.155386980007279E-12, -2.155386980007279E-12, -2.155386980007279E-12, -2.155386980007279E-12,
				-2.155386980007279E-12, -2.155386980007279E-12, -2.155386980007279E-12, -2.155386980007279E-12, -2.155386980007279E-12, -2.155386980007279E-12,
				-935.4066351711008, -1315.5824373219093, -1935.7537900939124, -1325.7769575528503, -952.5608565929579, -498.83565794523054,
				-472.2844576980037, -463.04772510198785, -410.38275728932376, -87.90731827462858, -55.669074346226346, 87.83484130399665,
				64.23800467211113, 29.722729338609525, -2.155609024612204E-12, -2.155609024612204E-12, -2.155609024612204E-12, -2.155609024612204E-12,
				-2.155609024612204E-12, -2.155609024612204E-12, -2.155609024612204E-12, -2.155609024612204E-12, -2.155609024612204E-12, -2.155609024612204E-12,
				-1068.4934359028568, -1688.6647886748597, -1914.7627414831184, -1304.7859089420563, -851.060710294329, -658.2684551137085,
				-669.3379473808218, -673.8004431637399, -693.731715854069, -242.02911054953628, -98.52519489931329, -69.10895982053434,
				-51.225469496160834, -23.820735239190288, -2.0036412290138568E-12, -2.0036412290138568E-12, -2.0036412290138568E-12, -2.0036412290138568E-12,
				-2.0036412290138568E-12, -2.0036412290138568E-12, -2.0036412290138568E-12, -2.0036412290138568E-12, -2.0036412290138568E-12, -2.0036412290138568E-12,
				-522.8941285840441, -1271.3575645804665, -2033.872616703881, -2033.872616703881, -1577.7662825992961, -701.2779289803077,
				-717.0950050712541, -723.5263613090538, -695.7323762583825, -711.5494523493298, -735.1462889812153, -717.2627986568418,
				-162.7345178909058, 159.6507768706169, -2.0306534231906426E-12, -2.0306534231906426E-12, -2.0306534231906426E-12, -2.0306534231906426E-12,
				-2.0306534231906426E-12, -2.0306534231906426E-12, -2.0306534231906426E-12, -2.0306534231906426E-12, -2.0306534231906426E-12, -2.0306534231906426E-12,
				-935.3164909179326, -1315.3570766889793, -2202.6332944829855, -1592.6564619419232, -1219.440360982031, -453.21189454763373,
				-426.6606943004023, -417.42396170438604, -472.4069245986897, -496.00376123057526, -530.5190365640768, -503.1143023071063,
				-180.72900754558356, -148.62597999694344, -1.5009660181419804E-12, -1.5009660181419804E-12, -1.5009660181419804E-12, -1.5009660181419804E-12,
				-1.5009660181419804E-12, -1.5009660181419804E-12, -1.5009660181419804E-12, -1.5009660181419804E-12, -1.5009660181419804E-12, -1.5009660181419804E-12,
				-438.87606744990666, -1326.1522852439145, -1551.8445889128861, -941.867756371824, -175.63928993742684, 17.152965243194046,
				25.904462428670552, 28.048963129951026, 8.117690439621716, -12.083794966392897, -41.80652430500458, -17.985789065816295,
				-177.63656593643523, -29.010585939493296, -1.5516476992161188E-12, -1.5516476992161188E-12, -1.5516476992161188E-12, -1.5516476992161188E-12,
				-1.5516476992161188E-12, -1.5516476992161188E-12, -1.5516476992161188E-12, -1.5516476992161188E-12, -1.5516476992161188E-12, -1.5516476992161188E-12,
				-1.603098545449547E-12, 302.2358356681055, 912.2126682091675, 389.19834728755893, -354.4824172548535, -1108.0891017179858,
				-1108.0891017179858, -1108.0891017179858, -1108.0891017179858, -1108.0891017179858, -1108.0891017179858, -1108.0891017179858,
				-1108.0891017179858, -1108.0891017179858, -1108.0891017179858, -553.4406286144839, -54.25730908244481, 91.69053792126033,
				75.87346183031292, 47.00426650144493, 35.9347742343316, 20.11769814338569, -8.751497185480028, -1.4017675908917226E-12,
				609.9768325410607, 712.5571648320455, 1322.5339973731075, 205.14712298767847, 55.547229331643784, -698.0594551314853,
				-698.0594551314853, -698.0594551314853, -698.0594551314853, -698.0594551314853, -698.0594551314853, -698.0594551314853,
				-698.0594551314853, -698.0594551314853, -698.0594551314853, -198.8761355994462, -170.33003942642847, -24.38219242272429,
				-20.268831266562376, -11.03209867054658, -8.887597969269539, -4.774236813103619, 4.462495782912926, -1.333599897179738E-12,
				609.9768325410606, 1219.9536650821226, 1683.3816889316506, 929.7750044685171, 176.16832000538506, -281.0464068166954,
				-281.0464068166954, -281.0464068166954, -281.0464068166954, -281.0464068166954, -281.0464068166954, -281.0464068166954,
				-281.0464068166954, -281.0464068166954, -281.0464068166954, -135.0985598129903, 10.849287190713897, 44.49853554392495,
				14.386555411617579, -40.59640748268761, -60.52768017301675, -32.73369512234308, 19.931272690327873, -1.554617545806991E-12,
				-1.567480257365348E-12, 763.847330977538, 1107.3126424406735, 584.3584176878484, -174.30137098880468, -919.1558222181511,
				-919.1558222181511, -919.1558222181511, -919.1558222181511, -919.1558222181511, -919.1558222181511, -919.1558222181511,
				-919.1558222181511, -919.1558222181511, -919.1558222181511, -934.9728983090985, -930.8595371529365, -960.9715172852437,
				-406.3831403505239, -436.96288535781673, 14.739719946715898, -1.0773561442315067, 20.201485406012914, -1.6942003355779889E-12,
				609.9768325410605, 983.1929335009529, 1749.42139993535, 1177.6146661802043, 797.4388640293956, -89.60315212484616,
				-89.60315212484616, -89.60315212484616, -89.60315212484616, -89.60315212484616, -89.60315212484616, -89.60315212484616,
				-89.60315212484616, -89.60315212484616, -89.60315212484616, -118.47234745371415, -109.23561485769835, -164.21857775200354,
				-194.79832275929638, -162.56007883089427, -14.168300473714963, 7.110541076529479, -27.404734256972148, -1.5480949855373183E-12,
				609.9768325410606, 1376.2052989754577, 1568.9975541560782, 500.5041182532229, -386.53789790101894, -612.6358507092777,
				-612.6358507092777, -612.6358507092777, -612.6358507092777, -612.6358507092777, -612.6358507092777, -612.6358507092777,
				-612.6358507092777, -612.6358507092777, -612.6358507092777, -623.7053429763911, -621.560842275114, -641.4921149654432,
				-189.78950966091043, -41.39773130373109, -11.9814962249521, 5.901994099421395, -23.820735239190245, -1.968381949714729E-12,
				-1.981244661273086E-12, 763.8473309775377, 1640.3356845965263, 1117.4415560124835, 358.69162308265254, -403.8234290407653,
				-403.8234290407653, -403.8234290407653, -403.8234290407653, -403.8234290407653, -403.8234290407653, -403.8234290407653,
				-403.8234290407653, -403.8234290407653, -403.8234290407653, -419.64050513171117, -415.52714397554524, -387.7331589248716,
				-403.550235015819, -382.27139346557453, -364.38790314120104, 190.14037762473484, 159.65077687061563, -2.4573121315540902E-12,
				609.9768325410596, 983.1929335009518, 1436.9181321486788, 865.0212541403571, 484.98066836930974, -134.95648276293997,
				-134.95648276293997, -134.95648276293997, -134.95648276293997, -134.95648276293997, -134.95648276293997, -134.95648276293997,
				-134.95648276293997, -134.95648276293997, -134.95648276293997, -163.82567809180568, -154.58894549578915, -101.9239776831182,
				-80.64513613287377, -115.1604114663754, -144.88314080498702, -175.3727415591062, -143.26971401046606, -2.574829238710663E-12,
				609.9768325410595, 1063.7020311887863, 1256.4942863694073, 817.6182189194991, 197.68106778725232, -28.01123588171919,
				-28.01123588171919, -28.01123588171919, -28.01123588171919, -28.01123588171919, -28.01123588171919, -28.01123588171919,
				-28.01123588171919, -28.01123588171919, -28.01123588171919, -19.259738696240564, -23.72223447915482, -43.65350716948425,
				-63.85499257549885, -36.45025831852825, -12.629523079339972, -172.28029994995808, -29.01058593949459, -2.9023450309750842E-12};

		createTestMoleculeAndSetMMFF94Parameters();

		AngleBending ab = new AngleBending();
		ab.setMMFF94AngleBendingParameters(ac, mmff94Tables, true);

		//logger.debug("ab.functionMMFF94SumEA(acCoordinates) = " + ab.functionMMFF94SumEA(acCoordinates));
		//assertEquals(testResult_SumEA, ab.functionMMFF94SumEA(acCoordinates), 0.00001);

		ab.setGradientMMFF94SumEA(acCoordinates);
		//logger.debug("ab.getGradientMMFF94SumEA() = " + ab.getGradientMMFF94SumEA());
		/*for (int i = 0; i < testResult_gradientSumEA.length; i++) {
			assertEquals(testResult_gradientSumEA[i], ab.getGradientMMFF94SumEA().getElement(i), 0.00001);
		}*/

		//ab.setHessianMMFF94SumEA(acCoordinates);
		//logger.debug("HessianMMFF94SumEA = " + ab.getHessianMMFF94SumEA());
		/*for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				assertEquals(testResult_hessianSumEA[i * 24 + j], ab.getHessianMMFF94SumEA().getElement(i, j), 0.00001);
			}
		}*/

	}


	/**
	 *  A unit test for JUnit (StretchBendInteraction)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testStretchBendInteraction() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with StretchBendInteraction");

		double testResult_SumEBA = 241.0516463157123;
		double[] testResult_gradientSumEBA = {-424.4366745401653,-424.4366745401653,-424.4366745401653,
				-424.4366745401653,-424.4366745401653,-424.4366745401653,-424.4366745401653,
				-424.4366745401653,-424.4366745401653,-424.4366745401653,-424.4366745401653,
				-424.4366745401653,-424.4366745401653,-424.4366745401653,-424.4366745401653,
				-424.4366745401653,-424.4366745401653,-424.4366745401653,-424.4366745401653,
				-424.4366745401653,-424.4366745401653,-424.4366745401653,-424.4366745401653,
				-424.4366745401653};

		createTestMoleculeAndSetMMFF94Parameters();

		StretchBendInteractions sbi = new StretchBendInteractions();
		sbi.setMMFF94StretchBendParameters(ac, mmff94Tables, false);

		//logger.debug("sbi.functionMMFF94SumEBA(acCoordinates) = " + sbi.functionMMFF94SumEBA(acCoordinates));
		//assertEquals(testResult_SumEBA, sbi.functionMMFF94SumEBA(acCoordinates), 0.00001);

		sbi.setGradientMMFF94SumEBA(acCoordinates);
		//logger.debug("sbi.getGradientMMFF94SumEBA() = " + sbi.getGradientMMFF94SumEBA());
		/*for (int i = 0; i < testResult_gradientSumEBA.length; i++) {
			assertEquals(testResult_gradientSumEBA[i], sbi.getGradientMMFF94SumEBA().getElement(i), 0.00001);
		}*/

		//logger.debug("HessianMMFF94SumEBA = " + sbi.HessianMMFF94SumEBA(acCoordinates));
	}


	/**
	 *  Description of the Method
	 *
	 *@param  molecule       Description of the Parameter
	 *@param  min            Description of the Parameter
	 *@param  positiveShift  Description of the Parameter
	 *@param  negativeShift  Description of the Parameter
	 *@return                Description of the Return Value
	 */
	public AtomContainer coordinateScrambler(AtomContainer molecule, int min, double positiveShift, double negativeShift) {
		double nPertubatedAtoms = min + Math.random() * molecule.getAtomCount();
		double coord = 0.0;
		logger.debug("Number of pertubated Atoms:" + nPertubatedAtoms);
		for (int i = 0; i < nPertubatedAtoms; i++) {
			coord = Math.random();
			if (coord <= 0.33) {
				if (Math.random() <= 0.) {
					molecule.getAtomAt(i).setX3d(molecule.getAtomAt(i).getX3d() + positiveShift);
				} else {
					molecule.getAtomAt(i).setX3d(molecule.getAtomAt(i).getX3d() - negativeShift);
				}
			} else if (coord <= 0.66) {
				if (Math.random() <= 0.) {
					molecule.getAtomAt(i).setY3d(molecule.getAtomAt(i).getY3d() + positiveShift);
				} else {
					molecule.getAtomAt(i).setY3d(molecule.getAtomAt(i).getY3d() - negativeShift);
				}
			} else {
				if (Math.random() <= 0.) {
					molecule.getAtomAt(i).setZ3d(molecule.getAtomAt(i).getZ3d() + positiveShift);
				} else {
					molecule.getAtomAt(i).setZ3d(molecule.getAtomAt(i).getZ3d() - negativeShift);
				}
			}
		}
		return molecule;
	}


	/**
	 *  A unit test for JUnit
	 *
	 *@exception  Exception     Description of the Exception
	 *@exception  CDKException  Description of the Exception
	 */
	public void testForceField() throws Exception, CDKException {
		
	}


	/**
	 *  A unit test for JUnit (Torsions)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testTorsions() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with Torsions");

		double testResult_MMFF94SumET = 11.369615843222473;
		double[] testResult_gradientSumET = {-2.9021441715059266,0.365841131968672,-1.3798123458839529,1.9172157299433363,
				0.1735281313651372,0.6740797133199399,2.33429617168884,-7.741921297512765E-4,0.9419919235431933,
				-1.0919747760635934,-1.4243907635796649,0.3741346044179644,0.8706944863448792,1.0811249436700319,
				-0.26708046642422206,-1.089139665432143,0.12919063910638315,-0.5131917161443834,0.2969423891730237,
				-1.1492480839602937,0.7770108103021226,-0.3358901637526217,0.8247281932428495,-0.6071325230515029};

		createTestMoleculeAndSetMMFF94Parameters();

		Torsions t = new Torsions();
		t.setMMFF94TorsionsParameters(ac, mmff94Tables);

		//logger.debug("functionMMFF94SumET = " + t.functionMMFF94SumET(acCoordinates));

		assertEquals(testResult_MMFF94SumET, t.functionMMFF94SumET(acCoordinates), 0.00001);

		t.set2ndOrderErrorApproximateGradientMMFF94SumET(acCoordinates);
		//logger.debug("t.get2ndOrderErrorApproximateGradientMMFF94SumET() = " + t.get2ndOrderErrorApproximateGradientMMFF94SumET());
		for (int i = 0; i < testResult_gradientSumET.length; i++) {
			assertEquals(testResult_gradientSumET[i], t.get2ndOrderErrorApproximateGradientMMFF94SumET().getElement(i), 0.00001);
		}

		//logger.debug("HessianMMFF94SumET = " + t.HessianMMFF94SumET(acCoordinates));
	}


	/**
	 *  A unit test for JUnit (VanDerWaalsInteraction)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testVanDerWaalsInteraction() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with VanDerWaalsInteraction");

		double testResult_MMFF94SumEvdW = 19.781709492460102;
		double testResult_CCGSumEvdWSK = 19.781709492460102;
		double testResult_CCGSumEvdWAv = 20.18009568070273;
		double[] testResult_gradientSumEvdW = {-825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186};

		createTestMoleculeAndSetMMFF94Parameters();

		VanDerWaalsInteractions vdwi = new VanDerWaalsInteractions();
		vdwi.setMMFF94VanDerWaalsParameters(ac, mmff94Tables);
		vdwi.setAtomDistance(acCoordinates);

		SmoothingFunctions sf = new SmoothingFunctions();
		sf.setSmoothingFunction(vdwi.getAtomDistance());

		//logger.debug("functionMMFF94SumEvdW = " + vdwi.functionMMFF94SumEvdW(acCoordinates));
		//logger.debug("functionCCGSumEvdWSK = " + vdwi.functionCCGSumEvdWSK(ac,sf.getSmoothingFunction()));
		//logger.debug("functionCCGSumEvdWAv = " + vdwi.functionCCGSumEvdWAv(ac,sf.getSmoothingFunction()));

		//assertEquals(testResult_MMFF94SumEvdW, vdwi.functionMMFF94SumEvdW(acCoordinates), 0.00001);
		//assertEquals(testResult_CCGSumEvdWSK, vdwi.functionCCGSumEvdWSK(acCoordinates, sf.getSmoothingFunction()), 0.00001);
		//assertEquals(testResult_CCGSumEvdWAv, vdwi.functionCCGSumEvdWAv(acCoordinates, sf.getSmoothingFunction()), 0.00001);

		vdwi.setGradientMMFF94SumEvdW(acCoordinates);
		//logger.debug("vdwi.gradientMMFF94SumEvdW(acCoordinates) = " + vdwi.gradientMMFF94SumEvdW(acCoordinates));
		/*for (int i = 0; i < testResult_gradientSumEwdW.length; i++) {
			assertEquals(testResult_gradientSumEwdW[i], vdwi.getGradientMMFF94SumEvdW().getElement(i), 0.00001);
		}*/

		//logger.debug("HessianMMFF94SumEvdW = " + vdwi.HessianMMFF94SumEvdW(acCoordinates));
	}


	/**
	 *  A unit test for JUnit (ElectrostaticInteraction)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testElectrostaticInteraction() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//logger.debug("");
		//logger.debug("FORCEFIELDTESTS with ElectrostaticInteraction");

		double testResult_MMFF94SumEQ = 19.781709492460102;
		double[] testResult_gradientSumEQ = {-825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186, -825.8720446886186, -825.8720446886186, -825.8720446886186,
				-825.8720446886186};

		createTestMoleculeAndSetMMFF94Parameters();

		ElectrostaticInteractions ei = new ElectrostaticInteractions();
		ei.setMMFF94ElectrostaticParameters(ac, mmff94Tables);
		ei.setInternuclearSeparation(acCoordinates);

		//logger.debug("functionMMFF94SumEQ = " + ei.functionMMFF94SumEQ(acCoordinates));

		//assertEquals(testResult_MMFF94SumEQ, ei.functionMMFF94SumEQ(acCoordinates), 0.00001);

		//ei.setGradientMMFF94SumEQ(acCoordinates);
		//logger.debug("ei.gradientMMFF94SumEQ(acCoordinates) = " + ei.gradientMMFF94SumEQ(acCoordinates));
		/*for (int i = 0; i < testResult_gradientSumEwdW.length; i++) {
			assertEquals(testResult_gradientSumEwdW[i], ei.getGradientMMFF94SumEQ().getElement(i), 0.00001);
		}*/

		//logger.debug("HessianMMFF94SumEQ = " + ei.HessianMMFF94SumEQ(acCoordinates));
	}

}
