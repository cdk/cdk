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
	 public ForceFieldTests() {}


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


	public void createTestMoleculeAndSetMMFF94Parameters() throws ClassNotFoundException, CDKException, java.lang.Exception {
		HydrogenAdder hAdder = new HydrogenAdder();
		SmilesParser sp = new SmilesParser();
		ac = sp.parseSmiles("CC");
		hAdder.addExplicitHydrogensToSatisfyValency((Molecule) ac);
		Atom a = new Atom();
		a = ac.getAtomAt(0);
		Point3d atomCoordinate0 = new Point3d(1,0,0);
		a.setPoint3d(atomCoordinate0);
		ac.setAtomAt(0, a);
		a = ac.getAtomAt(1);
		Point3d atomCoordinate1 = new Point3d(2,0,0);
		a.setPoint3d(atomCoordinate1);
		ac.setAtomAt(1, a);
		AtomTools at = new AtomTools();
		at.add3DCoordinates1(ac);
		ForceFieldTools ffTools = new ForceFieldTools();
		acCoordinates.setSize(ac.getAtomCount() * 3);
		acCoordinates.set(ffTools.getCoordinates3xNVector(ac));
		
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
		double[] testResult_gradientSumEB = {1080.8997353820591,0.0,-2.8421709430404007E-14,-1080.8997353820591,
					-1.1368683772161603E-13,2.8421709430404007E-14,26.617033497457708,-79.85110049237318,
					0.0,26.617033497457708,39.92555024618657,69.15308154653927,26.617033497457708,
					39.92555024618661,-69.15308154653924,-26.617033497457687,-79.85110049237318,0.0,
					-26.617033497457722,39.925550246186624,-69.15308154653937,-26.617033497457722,
					39.92555024618667,69.15308154653934};
		double[] testResult_hessianSumEB = {3627.3781763038755,-1413.262187101324,-1413.2621871013237,-3786.8540262644597,
					908.2394846475399,908.2394846475399,-31.011833755435887,429.7173029021638,84.17045040896397,
					-31.011833755435887,-88.60297583763585,-215.08190204790128,-31.011833755435887,-88.60297583763602,
					383.42280286582906,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					-1413.262187101324,141.69864911807622,-1413.2621871013243,908.2394846475399,
					908.2394846475399,908.2394846475399,429.71730290216374,-952.4701070706362,84.17045040896397,
					-88.60297583763585,-174.98968896093584,-364.70807827633394,-88.60297583763602,-174.98968896093635,
					533.0489790942622,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					-1413.262187101324,-1413.2621871013243,141.69864911807548,908.2394846475399,
					908.2394846475399,908.2394846475399,84.17045040896397,84.17045040896397,84.17045040896397,
					-215.08190204790122,-364.70807827633394,-693.309967700736,383.42280286582906,533.0489790942622,
					-693.3099677007356,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					-3786.8540262644597,908.2394846475404,908.2394846475404,3627.3781763038755,
					-1413.2621871013246,-1413.2621871013241,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,-31.011833755435717,-261.37640208423556,84.17045040896397,-31.011833755435674,
					256.94387665556394,-215.08190204790114,-31.011833755435674,256.94387665556417,383.4228028658292,
					908.2394846475404,908.2394846475404,908.2394846475404,-1413.2621871013248,
					141.6986491180764,-1413.2621871013241,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,-261.37640208423556,-952.4701070706362,84.17045040896397,256.94387665556394,
					-174.98968896093587,533.0489790942623,256.94387665556417,-174.9896889609365,-364.7080782763345,
					908.2394846475404,908.2394846475404,908.2394846475404,-1413.2621871013241,
					-1413.2621871013241,141.6986491180766,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,-215.08190204790114,
					533.0489790942624,-693.3099677007366,383.4228028658292,-364.7080782763345,-693.309967700736,
					961.3981013010684,1422.127237958668,1076.5803854654682,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,31.011833755435887,-429.7173029021638,-84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					1422.127237958668,39.93982798586802,1076.5803854654682,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,-429.71730290216374,952.4701070706362,-84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					1076.5803854654682,1076.5803854654682,1076.5803854654682,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,-84.17045040896397,-84.17045040896397,-84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					961.3981013010684,903.8069592188685,777.3280330086029,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,84.17045040896397,84.17045040896397,84.17045040896397,
					31.011833755435887,88.60297583763585,215.08190204790128,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					903.8069592188685,817.4202460955685,627.7018567801704,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,84.17045040896397,84.17045040896397,84.17045040896397,
					88.60297583763585,174.98968896093584,364.70807827633394,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					777.3280330086031,627.7018567801704,299.0999673557682,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,84.17045040896397,84.17045040896397,84.17045040896397,
					215.08190204790122,364.70807827633394,693.309967700736,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					961.3981013010684,903.8069592188683,1375.8327379223333,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,31.011833755435887,88.60297583763602,
					-383.42280286582906,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					903.8069592188683,817.420246095568,1525.4589141507663,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,88.60297583763602,174.98968896093635,
					-533.0489790942622,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					1375.8327379223333,1525.4589141507665,299.0999673557688,-1413.2621871013243,
					-1413.2621871013243,-1413.2621871013243,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,-383.42280286582906,-533.0489790942622,
					693.3099677007356,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1360.1035704477963,
					-1590.468138776596,-1244.9212862833965,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,31.011833755435717,261.37640208423556,-84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1590.468138776596,
					-2281.5618437629964,-1244.9212862833965,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,261.37640208423556,952.4701070706362,-84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1244.9212862833965,
					-1244.9212862833965,-1244.9212862833965,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,-84.17045040896397,-84.17045040896397,-84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1360.1035704477958,
					-1072.1478600367961,-1544.1736387402614,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,31.011833755435674,
					-256.94387665556394,215.08190204790114,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1072.1478600367961,
					-1504.081425653296,-796.0427575980978,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,-256.94387665556394,
					174.98968896093587,-533.0489790942623,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1544.1736387402614,
					-796.0427575980978,-2022.4017043930967,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,215.08190204790114,
					-533.0489790942624,693.3099677007366,84.17045040896411,84.17045040896411,84.17045040896411,
					908.2394846475404,908.2394846475404,908.2394846475404,-1360.1035704477958,
					-1072.1478600367961,-945.668933826531,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,31.011833755435674,-256.94387665556417,-383.4228028658292,
					908.2394846475404,908.2394846475404,908.2394846475404,-1072.147860036796,
					-1504.0814256532967,-1693.7998149686948,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,-256.94387665556417,174.9896889609365,364.7080782763345,
					908.2394846475404,908.2394846475404,908.2394846475404,-945.668933826531,
					-1693.7998149686946,-2022.401704393096,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,
					84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896397,84.17045040896411,
					84.17045040896411,84.17045040896411,-383.4228028658292,364.7080782763345,693.309967700736};


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
		//System.out.println("HessianMMFF94SumEB = " + bs.HessianMMFF94SumEB(acCoordinates));
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				assertEquals(testResult_hessianSumEB[i*24+j], bs.getHessianMMFF94SumEB().getElement(i,j), 0.00001);
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
		double[] testResult_gradientSumEA = {-7072408.357412168,-7072408.357412168,-7072408.357412168,-7072408.357412168,
						-7072408.357412168,-7072408.357412168,-7072408.357412168,-7072408.357412168,
						-7072408.357412168,-7072408.357412168,-7072408.357412168,-7072408.357412168,
						-7072408.357412168,-7072408.357412168,-7072408.357412168,-7072408.357412168,
						-7072408.357412168,-7072408.357412168,-7072408.357412168,-7072408.357412168,
						-7072408.357412168,-7072408.357412168,-7072408.357412168,-7072408.357412168};

		createTestMoleculeAndSetMMFF94Parameters();
		
		AngleBending ab = new AngleBending();
		ab.setMMFF94AngleBendingParameters(ac, mmff94Tables);

		//System.out.println("ab.functionMMFF94SumEA(acCoordinates) = " + ab.functionMMFF94SumEA(acCoordinates));
		assertEquals(testResult_SumEA, ab.functionMMFF94SumEA(acCoordinates), 0.00001);
		
		//System.out.println("ab.gradientMMFF94SumEA(acCoordinates) = " + ab.gradientMMFF94SumEA(acCoordinates));
		
		ab.setGradientMMFF94SumEA(acCoordinates);
		for (int i = 0; i < testResult_gradientSumEA.length; i++) {
			assertEquals(testResult_gradientSumEA[i], ab.getGradientMMFF94SumEA().getElement(i), 0.00001);
		}
		
		//System.out.println("HessianMMFF94SumEA = " + ab.HessianMMFF94SumEA(acCoordinates));
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
		double[] testResult_gradientSumEBA = {-61985.71873683456,-61985.71873683456,-61985.71873683456,-61985.71873683456,
						-61985.71873683456,-61985.71873683456,-61985.71873683456,-61985.71873683456,
						-61985.71873683456,-61985.71873683456,-61985.71873683456,-61985.71873683456,
						-61985.71873683456,-61985.71873683456,-61985.71873683456,-61985.71873683456,
						-61985.71873683456,-61985.71873683456,-61985.71873683456,-61985.71873683456,
						-61985.71873683456,-61985.71873683456,-61985.71873683456,-61985.71873683456};

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
	
	public AtomContainer coordinateScrambler(AtomContainer molecule, int min, double positiveShift, double negativeShift){
		double nPertubatedAtoms=min+Math.random()*molecule.getAtomCount();
		double coord=0.0;
		System.out.println("Number of pertubated Atoms:"+nPertubatedAtoms);
		for (int i=0;i<nPertubatedAtoms;i++){
			coord=Math.random();
			if (coord<=0.33){
				if (Math.random()<=0.){
					molecule.getAtomAt(i).setX3d(molecule.getAtomAt(i).getX3d()+positiveShift);
				}else{
					molecule.getAtomAt(i).setX3d(molecule.getAtomAt(i).getX3d()-negativeShift);
				}
			}else if (coord<=0.66){
				if (Math.random()<=0.){
					molecule.getAtomAt(i).setY3d(molecule.getAtomAt(i).getY3d()+positiveShift);
				}else{
					molecule.getAtomAt(i).setY3d(molecule.getAtomAt(i).getY3d()-negativeShift);
				}
			}else{
				if (Math.random()<=0.){
					molecule.getAtomAt(i).setZ3d(molecule.getAtomAt(i).getZ3d()+positiveShift);
				}else{
					molecule.getAtomAt(i).setZ3d(molecule.getAtomAt(i).getZ3d()-negativeShift);
				}
			}
		}
		return molecule;
	}
	
	public void testForceField() throws Exception, CDKException {
		/*System.out.println("TEST FORCE FIELD");
		ModelBuilder3D mb3d=new ModelBuilder3D();
		HydrogenAdder hAdder=new HydrogenAdder();
		String JMOLPATH = "java -jar /home/cho/SOFTWARE-DEVELOPMENT/cdk-project/Jmol10pre14/Jmol.jar";
		String TMP_FILE_PATH = "/home/cho/SOFTWARE-DEVELOPMENT/cdk-project/Modeling/data/cdk/data/mdl/";
		String []molfile={"TEST3DSTARTMolecule.mol","TEST3DMINIMIZEDMolecule.mol"};
		String command = "";
		BufferedWriter fout = null;
		SetOfMolecules som=new SetOfMolecules();
		Molecule molecule=null;
		SmilesParser sp = new SmilesParser();
		ForceField ff=new ForceField();
		
		String smile="CC";
		
		try{
			
			molecule = sp.parseSmiles(smile);
			hAdder.addExplicitHydrogensToSatisfyValency(molecule);
			//mb3d.setTemplateHandler();
			mb3d.setForceField("mmff94");
			mb3d.setMolecule(molecule,false);
			mb3d.generate3DCoordinates();
			molecule = mb3d.getMolecule();
			som.addMolecule(molecule);
		}catch (Exception ex1){
			System.out.println("Error in generating 3D coordinates for molecule due to:"+ex1.toString());
		}
		//createTestMoleculeAndSetMMFF94Parameters();
		//mb3d.setTemplateHandler();
		
		try{
			ff.setMolecule(molecule,true);
			ff.minimize();
			som.addMolecule(new Molecule(coordinateScrambler((AtomContainer)ff.getMolecule(),1,1,05)));
		}catch(Exception ex2){
			System.out.println("Error in minimizing molecule due to:"+ex2.toString());
		}
		
		for (int i=0;i<molfile.length;i++){
			try {
				fout = new BufferedWriter(new FileWriter(molfile[i]));
			} catch (Exception ex3) {
			}
		
			MDLWriter mdlw = new MDLWriter(fout);
			try {
				mdlw.write(som.getMolecule(i));
				mdlw.close();
			} catch (Exception ex3) {
			}
			command = JMOLPATH + " " +molfile[i];
			try {
				Runtime.getRuntime().exec(command);
			} catch (Exception ex4) {
				System.out.println("Error in viewer for molecule due to:"+ex4.toString());
			}
		}*/
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
		double[] testResult_gradientSumET = {2.3288726725032127,2.3288726725032127,2.3288726725032127,
				2.3288726725032127,2.3288726725032127,2.3288726725032127,2.3288726725032127,
				2.3288726725032127,2.3288726725032127,2.3288726725032127,2.3288726725032127,
				2.3288726725032127,2.3288726725032127,2.3288726725032127,2.3288726725032127,
				2.3288726725032127,2.3288726725032127,2.3288726725032127,2.3288726725032127,
				2.3288726725032127,2.3288726725032127,2.3288726725032127,2.3288726725032127,
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
		double[] testResult_gradientSumEwdW = {-825.8720446886186,-825.8720446886186,-825.8720446886186,
					-825.8720446886186,-825.8720446886186,-825.8720446886186,-825.8720446886186,
					-825.8720446886186,-825.8720446886186,-825.8720446886186,-825.8720446886186,
					-825.8720446886186,-825.8720446886186,-825.8720446886186,-825.8720446886186,
					-825.8720446886186,-825.8720446886186,-825.8720446886186,-825.8720446886186,
					-825.8720446886186,-825.8720446886186,-825.8720446886186,-825.8720446886186,
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
		assertEquals(testResult_CCGSumEvdWSK, vdwi.functionCCGSumEvdWSK(acCoordinates,sf.getSmoothingFunction()), 0.00001);
		assertEquals(testResult_CCGSumEvdWAv, vdwi.functionCCGSumEvdWAv(acCoordinates,sf.getSmoothingFunction()), 0.00001);
		
		vdwi.setGradientMMFF94SumEvdW(acCoordinates);
		//System.out.println("vdwi.gradientMMFF94SumEvdW(acCoordinates) = " + vdwi.gradientMMFF94SumEvdW(acCoordinates));
		for (int i = 0; i < testResult_gradientSumEwdW.length; i++) {
			assertEquals(testResult_gradientSumEwdW[i], vdwi.getGradientMMFF94SumEvdW().getElement(i), 0.00001);
		}
		
		//System.out.println("HessianMMFF94SumEvdW = " + vdwi.HessianMMFF94SumEvdW(acCoordinates));
	}

}
