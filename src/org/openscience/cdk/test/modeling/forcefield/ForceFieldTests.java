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

import org.openscience.cdk.modeling.forcefield.*;
import org.openscience.cdk.*;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.AtomTools;

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

		createTestMoleculeAndSetMMFF94Parameters();
		
		BondStretching bs = new BondStretching();
		bs.setMMFF94BondStretchingParameters(ac, mmff94Tables);

		//System.out.println("bs.functionMMFF94SumEB_InPoint(ac) = " + bs.functionMMFF94SumEB_InPoint(ac));
		assertEquals(testResult_SumEB, bs.functionMMFF94SumEB_InPoint(ac), 0.00001);
		
		bs.setGradientMMFF94SumEB_InPoint(ac);
		//System.out.println("gradientMMFF94SumEB_InPoint = " + bs.getGradientMMFF94SumEB_InWishedCoordinates());
		for (int i = 0; i < testResult_gradientSumEB.length; i++) {
			assertEquals(testResult_gradientSumEB[i], bs.getGradientMMFF94SumEB_InWishedCoordinates().getElement(i), 0.00001);
		}
		
		//System.out.println("hessian = " + bs.hessianInPoint(ac));
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
		
		double testResult_SumEA = 2.6627825055933344E8;
		double[] testResult_gradientSumEA = {-7254575.574502064,-7254575.574502064,-7254575.574502064,-7254575.574502064,
						-7254575.574502064,-7254575.574502064,-7254575.574502064,-7254575.574502064,
						-7254575.574502064,-7254575.574502064,-7254575.574502064,-7254575.574502064,
						-7254575.574502064,-7254575.574502064,-7254575.574502064,-7254575.574502064,
						-7254575.574502064,-7254575.574502064,-7254575.574502064,-7254575.574502064,
						-7254575.574502064,-7254575.574502064,-7254575.574502064,-7254575.574502064};

		createTestMoleculeAndSetMMFF94Parameters();
		
		AngleBending ab = new AngleBending();
		ab.setMMFF94AngleBendingParameters(ac, mmff94Tables);

		//System.out.println("ab.functionMMFF94SumEA_InPoint(ac) = " + ab.functionMMFF94SumEA_InPoint(ac));
		assertEquals(testResult_SumEA, ab.functionMMFF94SumEA_InPoint(ac), 0.00001);
		
		//System.out.println("ab.gradientMMFF94SumEA_InPoint(ac) = " + ab.gradientMMFF94SumEA_InPoint(ac));
		
		GVector gradientSumEA = new GVector(ab.gradientMMFF94SumEA_InPoint(ac));
		for (int i = 0; i < testResult_gradientSumEA.length; i++) {
			assertEquals(testResult_gradientSumEA[i], gradientSumEA.getElement(i), 0.00001);
		}
		
		//System.out.println("hessian = " + ab.hessianInPoint(acCoordinates));
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
		
		double testResult_SumEBA = 17052.61896814143;
		double[] testResult_gradientSumEBA = {-62623.72567235314,-62623.72567235314,-62623.72567235314,-62623.72567235314,
						-62623.72567235314,-62623.72567235314,-62623.72567235314,-62623.72567235314,
						-62623.72567235314,-62623.72567235314,-62623.72567235314,-62623.72567235314,
						-62623.72567235314,-62623.72567235314,-62623.72567235314,-62623.72567235314,
						-62623.72567235314,-62623.72567235314,-62623.72567235314,-62623.72567235314,
						-62623.72567235314,-62623.72567235314,-62623.72567235314,-62623.72567235314};

		createTestMoleculeAndSetMMFF94Parameters();
		
		StretchBendInteractions sbi = new StretchBendInteractions();
		sbi.setMMFF94StretchBendParameters(ac, mmff94Tables);

		//System.out.println("sbi.functionMMFF94SumEBA_InPoint(ac) = " + sbi.functionMMFF94SumEBA_InPoint(ac));
		assertEquals(testResult_SumEBA, sbi.functionMMFF94SumEBA_InPoint(ac), 0.00001);
		
		//System.out.println("sbi.gradientMMFF94SumEBA_InPoint(ac) = " + sbi.gradientMMFF94SumEBA_InPoint(ac));
		
		GVector gradientSumEBA = new GVector(sbi.gradientMMFF94SumEBA_InPoint(ac));
		for (int i = 0; i < testResult_gradientSumEBA.length; i++) {
			assertEquals(testResult_gradientSumEBA[i], gradientSumEBA.getElement(i), 0.00001);
		}
		
		//System.out.println("hessian = " + sbi.hessianInPoint(acCoordinates));
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
		
		//System.out.println("functionMMFF94SumET_InPoint = " + t.functionMMFF94SumET_InPoint(ac));
		
		assertEquals(testResult_MMFF94SumET, t.functionMMFF94SumET_InPoint(ac), 0.00001);
		
		//System.out.println("t.gradientMMFF94SumET_InPoint(ac) = " + t.gradientMMFF94SumET_InPoint(ac));
		
		GVector gradientSumET = new GVector(t.gradientMMFF94SumET_InPoint(ac));
		for (int i = 0; i < testResult_gradientSumET.length; i++) {
			assertEquals(testResult_gradientSumET[i], gradientSumET.getElement(i), 0.00001);
		}
		
		//System.out.println("hessian = " + t.hessianInPoint(acCoordinates));
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
		vdwi.setAtomDistance(ac);
		
		SmoothingFunctions sf = new SmoothingFunctions();
		sf.setSmoothingFunction(vdwi.getAtomDistance());
		
		//System.out.println("functionMMFF94SumEvdW_InPoint = " + vdwi.functionMMFF94SumEvdW_InPoint(ac));
		//System.out.println("functionCCGSumEvdWSK_InPoint = " + vdwi.functionCCGSumEvdWSK_InPoint(ac,sf.getSmoothingFunction()));
		//System.out.println("functionCCGSumEvdWAv_InPoint = " + vdwi.functionCCGSumEvdWAv_InPoint(ac,sf.getSmoothingFunction()));
		
		assertEquals(testResult_MMFF94SumEvdW, vdwi.functionMMFF94SumEvdW_InPoint(ac), 0.00001);
		assertEquals(testResult_CCGSumEvdWSK, vdwi.functionCCGSumEvdWSK_InPoint(ac,sf.getSmoothingFunction()), 0.00001);
		assertEquals(testResult_CCGSumEvdWAv, vdwi.functionCCGSumEvdWAv_InPoint(ac,sf.getSmoothingFunction()), 0.00001);
		
		//System.out.println("vdwi.gradientMMFF94SumEvdW_InPoint(ac) = " + vdwi.gradientMMFF94SumEvdW_InPoint(ac));

		vdwi.setGradientMMFF94SumEvdW_InPoint(ac);
		for (int i = 0; i < testResult_gradientSumEwdW.length; i++) {
			assertEquals(testResult_gradientSumEwdW[i], vdwi.getGradientMMFF94SumEvdW_InWishedCoordinates().getElement(i), 0.00001);
		}
		
		//System.out.println("hessian = " + vdwi.hessianInPoint(acCoordinates));
	}

}
