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

import org.openscience.cdk.modeling.builder3d.ModelBuilder3D;
import org.openscience.cdk.modeling.forcefield.*;
import org.openscience.cdk.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.AtomTools;
import org.openscience.cdk.io.MDLWriter;

/**
 *  Check results of GeometricMinimizer using some examples.
 *
 *@author         vlabarta
 *@created        March 22, 2005
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


	/**
	 *  Constructor for GeometricMinimizerTest object
	 */
	public ForceFieldTests() { }


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
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Steepest Descents Minimization");

		gm.setConvergenceParametersForSDM(5, 0.001);
		gm.steepestDescentsMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getSteepestDescentsMinimum().getElement(i), 0.1);
		}
	}


	/**
	 *  A unit test for JUnit (Conjugate Gradient Method minimization)
	 */
	public void testConjugateGradientMinimization() {
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Conjugate Gradient Minimization");

		gm.setConvergenceParametersForCGM(2, 0.0001);
		gm.conjugateGradientMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getConjugateGradientMinimum().getElement(i), 0.00001);
		}
	}


	/**
	 *  A unit test for JUnit (Newton-Raphson Method minimization)
	 */
	public void testNewtonRaphsonMinimization() {
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Newton-Raphson Minimization");

		gm.setConvergenceParametersForNRM(1, 0.0001);
		gm.newtonRaphsonMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getNewtonRaphsonMinimum().getElement(i), 0.00001);
		}
	}


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
		Atom a = new Atom();
		a = ac.getAtomAt(0);
		Point3d atomCoordinate0 = new Point3d(1, 0, 0);
		a.setPoint3d(atomCoordinate0);
		ac.setAtomAt(0, a);
		a = ac.getAtomAt(1);
		Point3d atomCoordinate1 = new Point3d(2, 0, 0);
		a.setPoint3d(atomCoordinate1);
		ac.setAtomAt(1, a);
		AtomTools at = new AtomTools();
		at.add3DCoordinates1(ac);

		ForceFieldTools ffTools = new ForceFieldTools();
		acCoordinates.setSize(ac.getAtomCount() * 3);
		acCoordinates.set(ffTools.getCoordinates3xNVector(ac));
		//System.out.println("acCoordinates : " + acCoordinates);

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
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Bond Stretching");

		double testResult_SumEB = 228.51003288118426;
		double[] testResult_gradientSumEB = {1080.8997353820591, 0.0, -2.8421709430404007E-14, -1080.8997353820591,
				-1.1368683772161603E-13, 2.8421709430404007E-14, 26.617033497457708, -79.85110049237318,
				0.0, 26.617033497457708, 39.92555024618657, 69.15308154653927, 26.617033497457708,
				39.92555024618661, -69.15308154653924, -26.617033497457687, -79.85110049237318, 0.0,
				-26.617033497457722, 39.925550246186624, -69.15308154653937, -26.617033497457722,
				39.92555024618667, 69.15308154653934};
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

		//System.out.println("bs.functionMMFF94SumEB(acCoordinates) = " + bs.functionMMFF94SumEB(acCoordinates));
		assertEquals(testResult_SumEB, bs.functionMMFF94SumEB(acCoordinates), 0.00001);

		bs.setGradientMMFF94SumEB(acCoordinates);
		//System.out.println("gradientMMFF94SumEB = " + bs.getGradientMMFF94SumEB());
		for (int i = 0; i < testResult_gradientSumEB.length; i++) {
			assertEquals(testResult_gradientSumEB[i], bs.getGradientMMFF94SumEB().getElement(i), 0.00001);
		}

		bs.setHessianMMFF94SumEB(acCoordinates);
		//System.out.println("HessianMMFF94SumEB = " + bs.getHessianMMFF94SumEB());
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				assertEquals(testResult_hessianSumEB[i * 24 + j], bs.getHessianMMFF94SumEB().getElement(i, j), 0.00001);
			}
		}

	}


	/**
	 *  A unit test for JUnit (AngleBending)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testAngleBending() throws ClassNotFoundException, CDKException, java.lang.Exception {
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Angle Bending");

		double testResult_SumEA = 2.5626995828317845E8;
		double[] testResult_gradientSumEA = {-460695.4430488355, -1.1641532182693481E-10, 6.984919309616089E-10, 460695.4430488365,
				-1.1641532182693481E-10, -4.817608823842778E-10, 153565.1476829451, 51188.382560981845, -1.1641532182693481E-10,
				153565.14768294524, -25594.191280490835, -44330.439676446666, 153565.14768294513, -25594.19128049095,
				44330.43967644649, -153565.14768294542, 51188.3825609818, -5.820766091346741E-11, -153565.14768294568,
				-25594.19128049095, 44330.439676446724, -153565.14768294545, -25594.191280490835, -44330.439676446666};
		double[] testResult_hessianSumEA = {3700746.0072377375, 9549159.616507957,  1.5122498239088112E7, 1.5122498239088112E7,
			1.0287335116940476E7,5452171.9947928395, 4257343.448739336,  2972132.569563469,  1776630.3041617107, 543048.3017491314, 
			-665729.841304185,  -1884226.60131351, -3156562.060085164,  -4423470.387677092, -5495293.725397054, -5495293.725397054, 
			-4579411.437830878, -3663529.1502647027, -3663529.1502647027,-2747646.862698527, -1831764.5751323511,-1831764.5751323514,
			-915882.2875661755, 4.6566128730773926E-10, 5848413.609270222,1.0402462602184748E7,1.5925459008227788E7,1.109029588608015E7,
			8295030.885432094,  3459867.763284457, 2171881.35751756, 1754923.5347738175, 559421.2693720589,  -647969.1103857418, 
			-1800023.722602844, -2772486.5248701903, -4038007.0891666035,-5277256.9781916225,-6544772.057116449, -5640330.173539949, 
			-5636516.7055433905,-4720634.417977215, -3799031.928416202, -3276199.4965547845,-2060661.4704867117,-1139058.9809256983,
			-616226.5490642814, -2.561137080192566E-9,
  			5573338.622580152,1.1096335028623192E7,1.6173555682385229E7,1.1338392560237592E7,6503229.438089955,  3707964.437441901, 
			2512462.172040142,   1316959.9066383836, 36133.400340718246, -1179959.683131894, -2152422.4853992397,-3046933.3094552164, 
			-4121160.32371189,  -5388675.402636715, -6544772.057116446, -5628889.76955027,  -4713007.481984095, -4017168.7288343916,   
			-3111194.1217528316,-1895656.0956847589,-1718836.3063999144,-793046.3383491231, -176819.7892848443, 
			5.820766091346741E-10,
  			4.661961783586849E-10, -4835163.122147637, -9670326.244295275, -5969580.237057531, -469947.73501898116,5103390.887561175,  
			5103390.887561175,  6019273.175127351,  6935155.462693526,  6935155.462693526,  7851037.750259702,  8766920.037825879,  
			8766920.037825879,  9682802.325392054,  1.059868461295823E7,9403856.066904724,  8298062.415277073,  7102560.149875315,  
			5868978.147462735,  4686751.759712535,  3468254.9997032094, 2195919.540931555,  1071823.3377199671, 
			5.471520125865936E-9,
  			-4835163.122147633,-7630428.122795684, -1.246559124494332E7,-6965958.742904768, -2411909.7499902416,3413139.955275491, 
			4340462.646831342,   4344276.1148279, 5260158.402394076,  6170320.487965414,  6693152.919826832,  7309379.468891111,  
			8219541.55446245, 8742373.986323867,  9957912.01239194,   8854893.887355322,  8437936.064611578,  7242433.79920982,   
			6058819.648164105,  4906765.0359470025, 3488223.307410832,  2362739.340903728,  1123489.4518787074, 1.5541445463895798E-8,
  			-4835163.122147623,-9670326.244295262, -1.2465591244943317E7,-6892252.622363162, -1067202.91709743,  4010017.7366646053, 
			4925900.024230781,   5841782.311796957,  6537621.06494666,   7443595.67202822,   8059822.2210925, 8236642.010377345,  
			9162431.978428137,  1.037797000449621E7,1.0554789793781053E7,9359287.528379295,  8163785.262977537,  6882958.756679872,  
			5666865.67320726,4248323.944671088,  3353813.1206151107, 2279586.106358437,  1156096.6544797448, 1.3620592653751373E-8,
  			-1194828.546053491,-4305549.245842045, -7323785.805265459, -7323785.805265459, -6396463.113709608, -5480580.826143432, 
			-5056303.338299569,  -4615866.979254789, -4008288.8812475703,-3623013.3521427503,-2751532.527548789, -2224082.0364153218,   
			-1838806.5073105018,-967325.6827165403, 1.2223608791828156E-8,1.2223608791828156E-8,1.2223608791828156E-8,1.2223608791828156E-8, 
			1.2223608791828156E-8,1.2223608791828156E-8,1.2223608791828156E-8,1.2223608791828156E-8,1.2223608791828156E-8,1.2223608791828156E-8,
  			-3107945.1731975097,  -3524902.9959412524,-6543139.5553646665,-5627257.267798491, -5623443.799801933, -4707561.512235757, 
			-4267125.153190977,  -3936709.2902230932,-3329131.1922158743,-2453317.9289225917,-2411953.683032941, -1737878.1280384455,   
			-862064.8647451622, -820700.6188555118, 1.210719347000122E-8,1.210719347000122E-8,1.210719347000122E-8,
			1.210719347000122E-8,  1.210719347000122E-8,1.210719347000122E-8,1.210719347000122E-8,1.210719347000122E-8,
			1.210719347000122E-8,1.210719347000122E-8,
  			-3018236.559423402,-6036473.118846816, -7317299.625144481, -6401417.337578305, -5485535.050012129, -4789696.296862426,
			-4182118.198855208,  -3574540.100847989, -3250253.757058193, -2720301.9312754245,-2053730.380228832, -1923379.675549749, 
			-958555.3274824977,  -130350.70467907062,1.2456439435482025E-8,1.2456439435482025E-8,1.2456439435482025E-8,
			1.2456439435482025E-8, 1.2456439435482025E-8,1.2456439435482025E-8,1.2456439435482025E-8,1.2456439435482025E-8,
			1.2456439435482025E-8,1.2456439435482025E-8,
  			-1233582.0024125667,  -4263706.676192022, -7302534.053686289, -7302534.053686289, -6392371.968114951, -5486397.36103339, 
			-5101121.831928571,  -4225308.568635288, -3695356.7428525193,-3232325.798649581, -2483046.646622532, -1630035.123566904, 
			-1244759.594462084,  -749889.4215742992, 1.1175870895385742E-8,1.1175870895385742E-8,1.1175870895385742E-8,
			1.1175870895385742E-8, 1.1175870895385742E-8,1.1175870895385742E-8,1.1175870895385742E-8,1.1175870895385742E-8,
			1.1175870895385742E-8,1.1175870895385742E-8,
  			-3031512.43707496,-4183567.049292062, -6978764.145581063, -6062881.858014887, -5540049.42615347,  -4923822.87708919, 
			-4052342.052495229,  -4010977.806605579, -3344406.255558986, -2595127.103531937, -2182113.1031397013,-1420854.1958605333,   
			-925984.0229727486, -751140.0888989496, 1.1408701539039612E-8,1.1408701539039612E-8,1.1408701539039612E-8,
			1.1408701539039612E-8, 1.1408701539039612E-8,1.1408701539039612E-8,1.1408701539039612E-8,1.1408701539039612E-8,
			1.1408701539039612E-8,1.1408701539039612E-8,
  			-3041231.054030969,-5836428.150319969, -6730938.9743759455,-5815056.68680977,  -5198830.13774549,  -5022010.348460645, 
			-4494559.857327178,  -3820484.302332683, -3690133.5976536,   -2837122.0745979724,-2075863.1673188047,-1485393.8537216892,   
			-740507.1014459806, 3128.9835050774273, 1.1635342009543199E-8,1.1635342009543199E-8,1.1635342009543199E-8,
			1.1635342009543199E-8, 1.1635342009543199E-8,1.1635342009543199E-8,1.1635342009543199E-8,1.1635342009543199E-8,
			1.1635342009543199E-8,1.1635342009543199E-8,
  			-1272335.458771642,-4360590.31708971,  -7257551.625368041, -7257551.625368041, -6347389.539796703, -5421599.571745912, 
			-5036324.042641092,  -4160510.7793478086,-3195686.431280557, -2810410.902175737, -2315540.729287952, -1570653.977012244, 
			-1068869.5764502306, -261460.23988456847, 9.691575542092323E-9, 9.691575542092323E-9, 9.691575542092323E-9, 
			9.691575542092323E-9,  9.691575542092323E-9, 9.691575542092323E-9, 9.691575542092323E-9, 9.691575542092323E-9, 
			9.691575542092323E-9, 9.691575542092323E-9,
  			-3089642.6216135737,  -4328892.510638593, -7419141.883585074, -6503259.596018898, -5980427.164157481, -4764889.138089408, 
			-3893408.3134954465, -3852044.0676057963,-3023839.4448023695,-2528969.2719145846,-2354125.3378407857,-1610489.2528897277,   
			-803079.9163240656, -302870.6391239119, 1.0739313438534737E-8,1.0739313438534737E-8, 1.0739313438534737E-8, 
			1.0739313438534737E-8, 1.0739313438534737E-8, 1.0739313438534737E-8,1.0739313438534737E-8, 1.0739313438534737E-8, 
			1.0739313438534737E-8, 1.0739313438534737E-8,
  			-2894557.6317416066,  -5984807.004688088, -7140903.659167819, -6225021.371601643, -5009483.34553357,  -4832663.556248725, 
			-3865337.8735321728, -3044637.254676649, -2914286.549997566, -2164397.1284232554,-1413257.039524294, -1416386.0230293598,   
			-1154925.7831447818,-852055.144020859,  9.89530235528946E-9, 9.89530235528946E-9, 9.89530235528946E-9, 
			9.89530235528946E-9,   9.89530235528946E-9,9.89530235528946E-9, 9.89530235528946E-9,9.89530235528946E-9,
			9.89530235528946E-9,9.89530235528946E-9,
  			9.818048734772133E-9, 904441.8835765106,  1820324.1711426864, 625495.6250891807,  -2300256.7939690943,-5318493.353392509, 
			-5318493.353392509,  -5318493.353392509, -5318493.353392509, -5318493.353392509, -5318493.353392509, -5318493.353392509, 
			-5318493.353392509,  -5318493.353392509, -5318493.353392509, -4894215.865548644, -4119496.028578987, 
			-3511917.930571768, -3126642.4014669475, -2503347.0522108893,-1975896.5610774218,-1590621.031972601, 
			-967325.682716542,  1.0593794286251068E-8,
  			915882.2875661865,919695.7555627448,  1835578.0431289207, -1092949.9025203863,-1509907.7252641283,-4528144.284687542, 
			-4528144.284687542,  -4528144.284687542, -4528144.284687542, -4528144.284687542, -4528144.284687542, -4528144.284687542, 
			-4528144.284687542,  -4528144.284687542, -4528144.284687542, -3753424.447717885, -3423008.5847500013, 
			-2815430.4867427824,   -2196467.576186046, -2155103.330296396, -1334402.7114408722,-715439.800884135,  
			-674075.5549944845, 1.1757947504520416E-8,
  			915882.2875661876,1831764.5751323635, 2527603.3282820666, -490633.231141348,  -3508869.7905647624,-4789696.296862427,
			-4789696.296862427,  -4789696.296862427, -4789696.296862427, -4789696.296862427, -4789696.296862427, -4789696.296862427, 
			-4789696.296862427,  -4789696.296862427, -4789696.296862427, -4182118.1988552087,-3574540.10084799,  
			-3250253.757058194, -2720301.9312754255, -1892097.3084719987,-1761746.603792916, -796922.2557256644, 
			-130350.70467907097,1.1976226232945919E-8,
  			1.1956912827816588E-8,   921602.4895610255,  1827577.096642586,  593995.0942300055,  -2412353.350837366, -5451180.728331635, 
			-5451180.728331635, -5451180.728331635, -5451180.728331635, -5451180.728331635, -5451180.728331635, -5451180.728331635, 
			-5451180.728331635, -5451180.728331635, -5451180.728331635, -5065905.199226814, -4446942.288670078, 
			-3916990.4628873095,   -3453959.5186843704,-2988082.474696983, -2135070.951641355, -1749795.4225365347,
			-749889.4215743, 1.0826624929904938E-8,
  			915882.2875661869,1438714.7194276042, 2654252.745495677,  -350707.9362761788, -1502762.5484932812,-4744038.571051108, 
			-4744038.571051108,  -4744038.571051108, -4744038.571051108, -4744038.571051108, -4744038.571051108, -4744038.571051108, 
			-4744038.571051108, -4744038.571051108, -4744038.571051108, -4120743.2217950495,-4079378.975905399, 
			-3251174.353101972, -2785297.3091145847, -2372283.308722349, -1918386.0199870798, -918480.0190248451, 
			-743636.084951046,  1.1990778148174286E-8,
  			915882.287566188,2131420.313634261,  2308240.1029191054, -732990.9511118752, -3974266.9736697017,-4868777.797725678, 
			-4868777.797725678,  -4868777.797725678, -4868777.797725678, -4868777.797725678, -4868777.797725678, -4868777.797725678, 
			-4868777.797725678,  -4868777.797725678, -4868777.797725678, -4341327.306592211, -3520626.6877366873,
			-3390275.9830576046,   -2537264.460001976, -2083367.171266707, -1492897.8576695914,-748011.1053938826, 
			3128.9835050785914, 1.2973589341266566E-8,
  			1.2954275936137235E-8,   921602.4895610263,  1847392.4576118176, 575056.9988401625,  -2373161.261688598, -5270122.569966928, 
			-5270122.569966928, -5270122.569966928, -5270122.569966928, -5270122.569966928, -5270122.569966928, -5270122.569966928, 
			-5270122.569966928, -5270122.569966928, -5270122.569966928, -4884847.040862108, -4265884.13030537,  
			-3301059.782238119, -2915784.2531332984,-1915878.2521710638,-1170991.4998953552,-669207.0993333411, 
			-261460.23988456576,1.2660166248679161E-8,
  			915882.2875661886,1438714.7194276056, 2054941.2684918842, -891889.2287413607, -2131139.1177663812,-5077362.863666729,
			-5077362.863666729,  -5077362.863666729, -5077362.863666729, -5077362.863666729, -5077362.863666729, -5077362.863666729, 
			-5077362.863666729,  -5077362.863666729, -5077362.863666729, -4454067.51441067,  -4412703.268521019, 
			-3746131.717474426, -2746225.7165121916, -2571381.7824383928,-1820241.693539431, -1412494.8340906557, 
			-912285.5568905018, 1.30385160446167E-8,
  			915882.287566189,1532108.8366304678, 1708928.625915313,  -1185629.005826305, -4131852.7517266525,-5287949.406206385, 
			-5287949.406206385,  -5287949.406206385, -5287949.406206385, -5287949.406206385, -5287949.406206385, -5287949.406206385, 
			-5287949.406206385,  -5287949.406206385, -5287949.406206385, -4320623.723489832, -3646548.168495336, 
			-3516197.463816253, -2766308.042241942,  -2022671.957290884, -2025800.94079595,  -1764340.7009113717, 
			-852055.1440208568, 1.257285475730896E-8};

		createTestMoleculeAndSetMMFF94Parameters();

		AngleBending ab = new AngleBending();
		ab.setMMFF94AngleBendingParameters(ac, mmff94Tables);

		//System.out.println("ab.functionMMFF94SumEA(acCoordinates) = " + ab.functionMMFF94SumEA(acCoordinates));
		assertEquals(testResult_SumEA, ab.functionMMFF94SumEA(acCoordinates), 0.00001);

		ab.setGradientMMFF94SumEA(acCoordinates);
		//System.out.println("ab.getGradientMMFF94SumEA() = " + ab.getGradientMMFF94SumEA());
		for (int i = 0; i < testResult_gradientSumEA.length; i++) {
			assertEquals(testResult_gradientSumEA[i], ab.getGradientMMFF94SumEA().getElement(i), 0.00001);
		}

		ab.setHessianMMFF94SumEA(acCoordinates);
		//System.out.println("HessianMMFF94SumEA = " + ab.getHessianMMFF94SumEA());
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				assertEquals(testResult_hessianSumEA[i * 24 + j], ab.getHessianMMFF94SumEA().getElement(i, j), 0.00001);
			}
		}

	}


	/**
	 *  A unit test for JUnit (StretchBendInteraction)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testStretchBendInteraction() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with StretchBendInteraction");

		double testResult_SumEBA = 384.0621424329229;
		double[] testResult_gradientSumEBA = {-61985.71873683456, -61985.71873683456, -61985.71873683456, -61985.71873683456,
				-61985.71873683456, -61985.71873683456, -61985.71873683456, -61985.71873683456,
				-61985.71873683456, -61985.71873683456, -61985.71873683456, -61985.71873683456,
				-61985.71873683456, -61985.71873683456, -61985.71873683456, -61985.71873683456,
				-61985.71873683456, -61985.71873683456, -61985.71873683456, -61985.71873683456,
				-61985.71873683456, -61985.71873683456, -61985.71873683456, -61985.71873683456};

		createTestMoleculeAndSetMMFF94Parameters();

		StretchBendInteractions sbi = new StretchBendInteractions();
		sbi.setMMFF94StretchBendParameters(ac, mmff94Tables);

		//System.out.println("sbi.functionMMFF94SumEBA(acCoordinates) = " + sbi.functionMMFF94SumEBA(acCoordinates));
		assertEquals(testResult_SumEBA, sbi.functionMMFF94SumEBA(acCoordinates), 0.00001);

		sbi.setGradientMMFF94SumEBA(acCoordinates);
		//System.out.println("sbi.getGradientMMFF94SumEBA(acCoordinates) = " + sbi.getGradientMMFF94SumEBA(acCoordinates));
		for (int i = 0; i < testResult_gradientSumEBA.length; i++) {
			assertEquals(testResult_gradientSumEBA[i], sbi.getGradientMMFF94SumEBA().getElement(i), 0.00001);
		}

		//System.out.println("HessianMMFF94SumEBA = " + sbi.HessianMMFF94SumEBA(acCoordinates));
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
		System.out.println("Number of pertubated Atoms:" + nPertubatedAtoms);
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
		/*
		 *  System.out.println("TEST FORCE FIELD");
		 *  ModelBuilder3D mb3d=new ModelBuilder3D();
		 *  HydrogenAdder hAdder=new HydrogenAdder();
		 *  String JMOLPATH = "java -jar /home/cho/SOFTWARE-DEVELOPMENT/cdk-project/Jmol10pre14/Jmol.jar";
		 *  String TMP_FILE_PATH = "/home/cho/SOFTWARE-DEVELOPMENT/cdk-project/Modeling/data/cdk/data/mdl/";
		 *  String []molfile={"TEST3DSTARTMolecule.mol","TEST3DMINIMIZEDMolecule.mol"};
		 *  String command = "";
		 *  BufferedWriter fout = null;
		 *  SetOfMolecules som=new SetOfMolecules();
		 *  Molecule molecule=null;
		 *  SmilesParser sp = new SmilesParser();
		 *  ForceField ff=new ForceField();
		 *  String smile="CC";
		 *  try{
		 *  molecule = sp.parseSmiles(smile);
		 *  hAdder.addExplicitHydrogensToSatisfyValency(molecule);
		 *  /mb3d.setTemplateHandler();
		 *  mb3d.setForceField("mmff94");
		 *  mb3d.setMolecule(molecule,false);
		 *  mb3d.generate3DCoordinates();
		 *  molecule = mb3d.getMolecule();
		 *  som.addMolecule(molecule);
		 *  }catch (Exception ex1){
		 *  System.out.println("Error in generating 3D coordinates for molecule due to:"+ex1.toString());
		 *  }
		 *  /createTestMoleculeAndSetMMFF94Parameters();
		 *  /mb3d.setTemplateHandler();
		 *  try{
		 *  ff.setMolecule(molecule,true);
		 *  ff.minimize();
		 *  som.addMolecule(new Molecule(coordinateScrambler((AtomContainer)ff.getMolecule(),1,1,05)));
		 *  }catch(Exception ex2){
		 *  System.out.println("Error in minimizing molecule due to:"+ex2.toString());
		 *  }
		 *  for (int i=0;i<molfile.length;i++){
		 *  try {
		 *  fout = new BufferedWriter(new FileWriter(molfile[i]));
		 *  } catch (Exception ex3) {
		 *  }
		 *  MDLWriter mdlw = new MDLWriter(fout);
		 *  try {
		 *  mdlw.write(som.getMolecule(i));
		 *  mdlw.close();
		 *  } catch (Exception ex3) {
		 *  }
		 *  command = JMOLPATH + " " +molfile[i];
		 *  try {
		 *  Runtime.getRuntime().exec(command);
		 *  } catch (Exception ex4) {
		 *  System.out.println("Error in viewer for molecule due to:"+ex4.toString());
		 *  }
		 *  }
		 */
	}


	/**
	 *  A unit test for JUnit (Torsions)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testTorsions() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Torsions");

		double testResult_MMFF94SumET = 3.4089232551466013;
		double[] testResult_gradientSumET = {2.3288726725032127, 2.3288726725032127, 2.3288726725032127,
				2.3288726725032127, 2.3288726725032127, 2.3288726725032127, 2.3288726725032127,
				2.3288726725032127, 2.3288726725032127, 2.3288726725032127, 2.3288726725032127,
				2.3288726725032127, 2.3288726725032127, 2.3288726725032127, 2.3288726725032127,
				2.3288726725032127, 2.3288726725032127, 2.3288726725032127, 2.3288726725032127,
				2.3288726725032127, 2.3288726725032127, 2.3288726725032127, 2.3288726725032127,
				2.3288726725032127};

		createTestMoleculeAndSetMMFF94Parameters();

		Torsions t = new Torsions();
		t.setMMFF94TorsionsParameters(ac, mmff94Tables);

		//System.out.println("functionMMFF94SumET = " + t.functionMMFF94SumET(acCoordinates));

		assertEquals(testResult_MMFF94SumET, t.functionMMFF94SumET(acCoordinates), 0.00001);

		t.setGradientMMFF94SumET(acCoordinates);
		//System.out.println("t.getGradientMMFF94SumET(acCoordinates) = " + t.getGradientMMFF94SumET(acCoordinates));
		for (int i = 0; i < testResult_gradientSumET.length; i++) {
			assertEquals(testResult_gradientSumET[i], t.getGradientMMFF94SumET().getElement(i), 0.00001);
		}

		//System.out.println("HessianMMFF94SumET = " + t.HessianMMFF94SumET(acCoordinates));
	}


	/**
	 *  A unit test for JUnit (VanDerWaalsInteraction)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testVanDerWaalsInteraction() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with VanDerWaalsInteraction");

		double testResult_MMFF94SumEvdW = 19.781709492460102;
		double testResult_CCGSumEvdWSK = 19.781709492460102;
		double testResult_CCGSumEvdWAv = 20.18009568070273;
		double[] testResult_gradientSumEwdW = {-825.8720446886186, -825.8720446886186, -825.8720446886186,
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

		//System.out.println("functionMMFF94SumEvdW = " + vdwi.functionMMFF94SumEvdW(acCoordinates));
		//System.out.println("functionCCGSumEvdWSK = " + vdwi.functionCCGSumEvdWSK(ac,sf.getSmoothingFunction()));
		//System.out.println("functionCCGSumEvdWAv = " + vdwi.functionCCGSumEvdWAv(ac,sf.getSmoothingFunction()));

		assertEquals(testResult_MMFF94SumEvdW, vdwi.functionMMFF94SumEvdW(acCoordinates), 0.00001);
		assertEquals(testResult_CCGSumEvdWSK, vdwi.functionCCGSumEvdWSK(acCoordinates, sf.getSmoothingFunction()), 0.00001);
		assertEquals(testResult_CCGSumEvdWAv, vdwi.functionCCGSumEvdWAv(acCoordinates, sf.getSmoothingFunction()), 0.00001);

		vdwi.setGradientMMFF94SumEvdW(acCoordinates);
		//System.out.println("vdwi.gradientMMFF94SumEvdW(acCoordinates) = " + vdwi.gradientMMFF94SumEvdW(acCoordinates));
		for (int i = 0; i < testResult_gradientSumEwdW.length; i++) {
			assertEquals(testResult_gradientSumEwdW[i], vdwi.getGradientMMFF94SumEvdW().getElement(i), 0.00001);
		}

		//System.out.println("HessianMMFF94SumEvdW = " + vdwi.HessianMMFF94SumEvdW(acCoordinates));
	}

}

