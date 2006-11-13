/*
 *  $RCSfile: ForceFieldTests.java,v $
 *  $Author$
 *  $Date$
 *  *
 *  Copyright (C) 2005-2006  Violeta Labarta Beceiro (vlabarta@yahoo.com)
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

import java.io.FileWriter;
import java.io.InputStream;
import java.util.Hashtable;

import javax.vecmath.GVector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.modeling.builder3d.ForceFieldConfigurator;
import org.openscience.cdk.modeling.forcefield.AngleBending;
import org.openscience.cdk.modeling.forcefield.BondStretching;
import org.openscience.cdk.modeling.forcefield.ElectrostaticInteractions;
import org.openscience.cdk.modeling.forcefield.ForceField;
import org.openscience.cdk.modeling.forcefield.ForceFieldTools;
import org.openscience.cdk.modeling.forcefield.GeometricMinimizer;
import org.openscience.cdk.modeling.forcefield.MMFF94EnergyFunction;
import org.openscience.cdk.modeling.forcefield.SmoothingFunctions;
import org.openscience.cdk.modeling.forcefield.StretchBendInteractions;
import org.openscience.cdk.modeling.forcefield.Torsions;
import org.openscience.cdk.modeling.forcefield.VanDerWaalsInteractions;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;



/**
 *  Check forcefield package using some examples.
 *
 *@author         vlabarta
 *@cdk.module     test-forcefield
 *@cdk.created    2005-01-17
 */
public class ForceFieldTests extends CDKTestCase {

	IMolecule molecule = null;
	IAtomContainer ac = null;
	GVector moleculeCoordinates = null;
	GeometricMinimizer gm = new GeometricMinimizer();
	Hashtable mmff94Tables = null;
	MMFF94EnergyFunction mmff94Energy = null;

	double[] molecule3Coord = {9, 9, 0};
	GVector molecule3Coordinates = new GVector(molecule3Coord);

	TestPotentialFunction tpf = new TestPotentialFunction();
	double[] testResult3C = {0, 0, 0};
	
	String input;

	private LoggingTool logger;
	private boolean standAlone = false;

	/**
	 *  Constructor for ForceFieldTests object
	 */
	public ForceFieldTests() {
		logger = new LoggingTool(this);
		
		try {

			input = "Ethane-TestFF";
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/mdl/" + input + ".mol");
			MDLReader mdlReader = new MDLReader(is);
        	molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
        	mdlReader.close();
        	//System.out.println("molecule: " +  molecule);
 
        	gm.setMMFF94Tables(molecule);
    		mmff94Tables = gm.getPotentialParameterSet();

    		moleculeCoordinates = ForceFieldTools.getCoordinates3xNVector(molecule);

        } catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }       
	    
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
	 *  Get MMFF94 energy of a molecule (methylbenzol).
	 */
	public void testGetMMFF94EnergyOfAMolecule() {
		
		double testResult_mmff94Energy = 92473.5759007652; //(methylbenzol)
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS Get MMFF94 energy of a molecule (methylbenzol)");

		double energy = 0;
		String localInput = "methylbenzol";
		
		try {

			InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/mdl/" + localInput + ".mol");
			//FileReader fileReader = new FileReader("data/mdl/" + localInput + ".mol");
			MDLReader mdlReader = new MDLReader(is);
			molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
			mdlReader.close();
			//System.out.println("molecule: " +  molecule);

		} catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }

		try {

			ForceFieldConfigurator ffc = new ForceFieldConfigurator();
			ffc.setForceFieldConfigurator("mmff94");
			RingSet rs = (RingSet) ffc.assignAtomTyps((IMolecule) molecule);
			mmff94Tables = ffc.getParameterSet();

		} catch (Exception exception) {
            System.out.println("Error whit ForceFieldConfigurator: " + exception.getMessage());
            System.out.println(exception);
        }
      
		try {

			mmff94Energy = new MMFF94EnergyFunction(molecule, mmff94Tables);
			energy = mmff94Energy.energyFunctionOfAMolecule(molecule);
		
			//System.out.println("molecule energy = " + energy);
		
		} catch (Exception exception) {
            System.out.println("Error whit MMFF94EnergyFunction: " + exception.getMessage());
            System.out.println(exception);
        }

		assertEquals(testResult_mmff94Energy, energy, 0.00001);

	}

	
	/**
	 *  A unit test for JUnit (Steepest Descents Method minimization)
	 */
	public void testSteepestDescentsMinimization() {
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Steepest Descents Minimization");

		gm.setConvergenceParametersForSDM(100, 0.00001);
		gm.steepestDescentsMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getSteepestDescentsMinimum().getElement(i), 0.0001);
		}
	}


 	/**
	 *  A unit test for JUnit (Conjugate Gradient Method minimization)
	 */
	public void testConjugateGradientMinimization() {
		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Conjugate Gradient Minimization");

		gm.setConvergenceParametersForCGM(100, 0.00001);
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

		gm.setConvergenceParametersForNRM(1000, 0.00001);
		gm.newtonRaphsonMinimization(molecule3Coordinates, tpf);

		for (int i = 0; i < molecule3Coordinates.getSize(); i++) {
			assertEquals(testResult3C[i], gm.getNewtonRaphsonMinimum().getElement(i), 0.00001);
		}
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

		double testResult_SumEB = 164.37972112718;
		double[] testResult_gradientSumEB = {-5.45515932404852,-6.48301447319298,62.375490960204004,7.215112040885087,
				17.691850072855214,-17.864815087533966,-25.53709462892573,8.797695542553555,-41.040060828236314,19.93844402757681,
				-14.563941666352736,6.046315156377083,205.23205964792473,716.6248883628301,-66.50259680461075,-122.18702194641659,
				-676.5540659625659,89.00042339306896,-54.54280594897065,-38.771392180593594,-36.50126444120599,-24.663533868025148,
				-6.742019695533713,4.486507651936976};
		double[] testResult_hessianSumEB = {838.7345581626308,	-490.508256979552,	206.25489080239663,	-41.10811797233039,	
				-136.67859760328315, 187.6749877479929,	-226.92130670470732, 142.11402590661103, -393.5494485762997, 
				-505.1462369756591, 410.5007840411007, -136.46777719213765, -65.55889650993397, 74.5720446351234, 
				136.08734721804785, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				
				-490.50825697955213, 738.4815880177924, -553.7035840137708, -136.67859760328315, -371.02265039698926, 
				424.31011368353694, 142.11402590661103, 14.978948563791874, 199.51850681209447, 410.50078404110076, 
				-258.32923010712227, 141.20135553496644, 74.5720446351234, -124.1086560774728, -211.32639201682707, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				
				206.2548908023966, -553.7035840137708, 1428.6338291193285, 187.6749877479929, 424.31011368353694, 
			   -378.79825004582136, -393.5494485762997, 199.51850681209447, -661.3335946380954, -136.46777719213765, 
			   141.20135553496644, -24.666367005076978, 136.08734721804788, -211.32639201682707, -363.8356174303348, 
			   0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -41.10811797233039, -136.67859760328315,	187.6749877479929, 41.10811797233039, 136.67859760328315, 
			   -187.6749877479929,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -136.67859760328315,	-371.02265039698926, 424.31011368353694, 136.67859760328315, 371.02265039698926, 
			   -424.31011368353694, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   187.6749877479929, 424.31011368353694, -378.79825004582136, -187.6749877479929, -424.31011368353694, 
			   378.79825004582136, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -226.92130670470732,	142.11402590661103,	-393.5494485762997,	0.0, 0.0, 0.0, 226.92130670470732, 
			   -142.11402590661103,	393.5494485762997, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   142.11402590661103, 14.978948563791874, 199.51850681209447, 0.0, 0.0, 0.0, -142.11402590661103, 
			   -14.978948563791874, -199.51850681209447, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -393.5494485762997, 199.51850681209447, -661.3335946380954, 0.0, 0.0, 0.0, 393.5494485762997, 
			   -199.51850681209447, 661.3335946380954, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -505.1462369756591, 410.5007840411007, -136.46777719213765, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 505.1462369756591, 
			   -410.5007840411007, 136.46777719213765, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   410.50078404110076, -258.32923010712227, 141.20135553496644, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -410.50078404110076, 
			   258.32923010712227, -141.20135553496644, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -136.46777719213765, 141.20135553496644, -24.666367005076978, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 136.46777719213765, 
			   -141.20135553496644, 24.666367005076978,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   -65.55889650993397, 74.5720446351234, 136.08734721804785, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
			   1418.9840577897937, 1004.3556303528381, 144.36150490775267, -447.3029123948977, -765.7233975935093, -325.9998704144742,
			   -150.97069785751137, -124.59079592322227, -120.79368882662007, -755.1515510274509, -188.61348147123016, 166.34470711529374,
			   
			   74.5720446351234, -124.1086560774728, -211.32639201682707, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1004.3556303528383, 
			   2792.5216158896105, 345.24343338658866, -765.7233975935094, -2528.829417489526, -94.06396144264568, -124.59079592322227, 
			   -105.83881743981195, -103.1396690217453, -188.61348147123016, -33.74472488280005, 63.286589094629434,
			   
			   136.08734721804788, -211.32639201682707, -363.8356174303348, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 144.36150490775267, 
			   345.24343338658866, 880.0739245494975, -325.9998704144742, -94.06396144264568, -414.35640716564694, -120.79368882662007, 
			   -103.1396690217453, -100.59856011043759, 166.34470711529374, 63.286589094629434, -1.2833398430781315,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -447.3029123948977, -765.7233975935093, -325.9998704144742, 
			   447.3029123948977, 765.7233975935093, 325.9998704144742, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -765.7233975935094, -2528.829417489526, -94.06396144264568, 
			   765.7233975935094, 2528.829417489526, 94.06396144264568, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -325.9998704144742, -94.06396144264568, -414.35640716564694, 
			   325.9998704144742, 94.06396144264568, 414.35640716564694, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -150.97069785751137, -124.59079592322227, -120.79368882662007, 
			   0.0, 0.0, 0.0, 150.97069785751137, 124.59079592322227, 120.79368882662007, 0.0, 0.0, 0.0,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -124.59079592322227, -105.83881743981195, 
			   -103.1396690217453, 0.0, 0.0, 0.0, 124.59079592322227, 105.83881743981195, 103.1396690217453, 0.0, 0.0, 0.0,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -120.79368882662007, -103.1396690217453, 
			   -100.59856011043759, 0.0, 0.0, 0.0, 120.79368882662007, 103.1396690217453, 100.59856011043759, 0.0, 0.0, 0.0,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -755.1515510274509, -188.61348147123016, 
			   166.34470711529374, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 755.1515510274509, 188.61348147123016, -166.34470711529374,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -188.61348147123016, -33.74472488280005, 
			   63.286589094629434, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 188.61348147123016, 33.74472488280005, -63.286589094629434,
			   
			   0.0, 0.0, 0.0, 0.0,	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 166.34470711529374, 63.286589094629434, 
			   -1.2833398430781315, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -166.34470711529374, -63.286589094629434, 1.2833398430781315};

				
 		BondStretching bs = new BondStretching();
		bs.setMMFF94BondStretchingParameters(molecule, mmff94Tables);
		
		moleculeCoordinates = ForceFieldTools.getCoordinates3xNVector(molecule);

		//System.out.println("bs.functionMMFF94SumEB(moleculeCoordinates) = " + bs.functionMMFF94SumEB(moleculeCoordinates));
		assertEquals(testResult_SumEB, bs.functionMMFF94SumEB(moleculeCoordinates), 0.00001);

		bs.setGradientMMFF94SumEB(moleculeCoordinates);
		//System.out.println("gradientMMFF94SumEB = " + bs.getGradientMMFF94SumEB());
		for (int i = 0; i < testResult_gradientSumEB.length; i++) {
			assertEquals(testResult_gradientSumEB[i], bs.getGradientMMFF94SumEB().getElement(i), 0.00001);
		}

		bs.setHessianMMFF94SumEB(moleculeCoordinates);
		//System.out.println("HessianMMFF94SumEB = " + bs.getHessianMMFF94SumEB());
		//System.out.println("bs.getHessianMMFF94SumEB() = " + bs.getHessianMMFF94SumEB());
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 24; j++) {
				//System.out.println("testResult_hessianSumEB[" + (i * 24 + j) + "] = " + testResult_hessianSumEB[i * 24 + j]);
				//System.out.println("bs.getHessianMMFF94SumEB().getElement(" + i + ", " + j + ") = " + bs.getHessianMMFF94SumEB().getElement(i, j));
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

		double testResult_SumEA = 5187817.002469799;
		
		double[] testResult_gradientSumEA = {-1826900.665119202,4874019.073980178,-2976646.8885927363,5436647.926898837,1544395.937926154,
				3725157.2422888074,-3634259.3802939164,-5890831.429145373,998601.9376623626,94272.78336650936,-573645.144687769,
				-1692632.0872584942,2458165.6181304716,3314333.7562086596,-3216324.9653861565,-3105947.007562904,213419.56169778062,
				-2641746.3421344426,556439.2636210282,-4580435.001781547,4033835.1368662477,21581.4605026533,1098743.245967926,1769755.9673429523};
		
		double[] testResult_hessianSumEA = {130683.846306297, 212234.67510572585, 138891.4346616306, -80599.65158382931, -53146.94541093669, 
				45893.317429985385, -54160.68316382803, -83689.35171815247, -98865.42524238293, -14903.08247197629, -64184.89548225285, 
				-99023.921275829, 24590.85157595449, -17445.984023657293, 2968.898084443173, 1397.0504531806494, -559.3323603501285, 
				-2333.889438750818, -6926.681667675539, 2977.9491827488955, 7187.1871412436385, -81.64944651698873, 3813.884705820904, 
				5282.398638849346, 

				212234.67510126764, 87516.59233210611, -187258.53172228593, 53419.88265097874, -50011.86767227145, 9393.850514464806, 
				-198811.08959716235, -36146.112080451254, 41138.636059514356, -47516.490064955666, 8898.72966880846, 138770.88158207343, 
				-19885.175456215504, -12011.450433445103, 408.8454232442999, 3346.901551962076, -1604.0283799561603, -7598.435639203067, 
				-2718.996685236438, 1396.2766003744364, 2579.8043541464435, -69.70750058277125, 1961.8599657671407, 2564.9494262623402, 

				138891.43457206065, -187258.53198167408, -191890.7010834845, -103185.85973846416, -92294.07475740704, -43150.6506178261, 
				-5443.366011976426, 193963.55031348663, 100253.26435341185, -21999.7516472814, 77454.26424948158, 143095.77794200176, 
				-5679.896433895151, 6624.031942966264, -13799.19447220064, -1350.5359677090662, 691.7104453495699, 3404.046124861083, 
				-1238.9557622832572, 402.6599779111938, 1423.6338551214842, 6.93099003010714, 416.389808994245, 663.8238984393021, 

				-80599.65159598814, 53419.88267497215, -103185.85975662133, 179380.20764235142, 139513.64524180314, 79531.96532808243, 
				-94960.42712482285, -188066.98339200337, 18773.29946551125, -3582.7648366270428, -3227.9781116855847, 4039.2653389452153, 
				-237.36408542003247, -1638.5664128431617, 841.3296234339632, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-53146.945405424696, -50011.86770080415, -92294.07474751787, 139513.6452257535, 16197.570887443146, 35039.83362694745, 
				-88746.42749334988, 27733.015687505962, 61167.369022432635, 1431.2236348940694, 1309.029207315117, -1566.5321893879832, 
				948.5040384107085, 4772.251918620979, -2346.5957144196464, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				45893.317440279854, 9393.850485932107, -43150.650622365385, 79531.96532159773, 35039.83365385897, -23102.425180222977, 
				-126239.12422068256, -48490.62231012322, 68157.17795855619, -29.614395128894433, -7.337061007133339, 79.98399350771885, 
				843.4558533259291, 4064.2752313392707, -1984.0861496376535, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-54160.68314182056, -198811.08964806728, -5443.366046345358, -94960.4271235259, -88746.42751604634, -126239.12423462467, 
				146417.80254400117, 237198.69021138066, 74366.18369090796, 12431.489620896387, 43962.06201152392, 64898.427643459014, 
				-9728.181898902607, 6396.76494283019, -7582.121052424239, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-83689.35171811194, -36146.11200652563, 193963.5502081102, -188066.98336411914, 27733.015677941024, -48490.62229261452, 
				237198.69016209693, 15082.538316111328, -69540.60905763856, 23983.982421088807, -1651.1259684352246, -83067.1541863393, 
				10573.662500392951, -5018.316017551379, 7134.835329130672, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-98865.42526285027, 41138.63618385856, 100253.26434757562, 18773.29948496536, 61167.369018541816, 68157.17796309547, 
				74366.1836516755, -69540.60908519855, -116467.99081155541, -2594.059308579129, -27709.253321017884, -58189.89476465562, 
				8320.001435923366, -5056.142798129351, 6247.443265377826, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-14903.082485016625, -47516.490054519345, -21999.75169690965, -3582.764840173365, 1431.2236402844796, -29.614394439894628, 
				12431.489626367857, 23983.982415566676, -2594.059307484835, 6411.1481773136165, 21802.123298208393, 24938.42400139562, 
				-356.79047812671826, 299.1607000545007, -314.9986046687674, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-64184.895518242956, 8898.729731547972, 77454.26419744184, -3227.978104957704, 1309.0292078825285, -7.337059061722124, 
				43962.06201411781, -1651.1259665708722, -27709.25330853483, 21802.123308735903, -7634.676522585171, -50929.93294224163, 
				1648.6883004989331, -921.9564497070453, 1192.2591136932797, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-99023.92125292988, 138770.88166394283, 143095.77794540624, 4039.2653282454535, -1566.5321876046896, 79.98399691218847, 
				64898.427642162074, -83067.1542019026, -58189.894752172564, 24938.423985052137, -50929.932944835506, -88896.4430660727, 
				5147.804297216913, -3207.2623300863943, 3910.5758770616617, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				24590.851596057073, -19885.17545929574, -5679.896465670201, -237.36408574426767, 948.5040401940022, 843.4558502456947, 
				-9728.181898578372, 10573.662501689892, 8320.001439489954, -356.79047680951277, 1648.688300579992, 5147.804304512206, 
				5716.720520027591, 35013.212915586104, 154526.77983828308, -13596.22369683916, 31333.539137130505, 3480.5325716780835, 
				-4958.2788337923175, 1690.5794186236562, -68702.35309475935, -1430.733124321035, -61323.010854893444, -97936.32444394157, 

				-17445.984011984827, -12011.450448846275, 6624.031967608139, -1638.5664102492801, 4772.251923484508, 4064.2752219364497, 
				6396.764938290897, -5018.316022090672, -5056.142799426292, 299.1607011386622, -921.9564497881041, -3207.2623320318057, 
				35013.212885108, 79396.41251541644, -210074.69638666182, -63286.85892570783, 5099.100595618962, -35045.66981065975, 
				46388.19337690248, -78366.91867704301, 159732.38118002846, -5725.922553802079, 7050.87656367371, 82963.08296034145, 

				2968.898090157819, 408.8454201640655, -13799.194492951692, 841.329623109728, -2346.595719931645, -1984.0861486649478, 
				-7582.121048209181, 7134.835328806436, 6247.443266026296, -314.9986044458557, 1192.2591115857508, 3910.575877223779, 
				154526.77986227648, -210074.69670246693, -207591.68396697848, 25192.896537386798, 16255.462950070405, -39029.38728106642, 
				-144151.80073982108, 76186.99947422123, 90648.00722994712, -31480.983720728294, 111242.89013674008, 161598.32551532952, 

				1397.0504535048847, 3346.90155098937, -1350.5359680333015, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -13596.223698622454, 
				-63286.85892311394, 25192.89653803527, 14188.205288747156, -24328.794041545403, 50580.12308833832, 3767.7300089144505, 
				63853.467515515455, -73454.72653928083, -5756.76205276695, 20415.28389906643, -967.7571193836814, 

				-559.3323604311873, -1604.0283795913958, 691.7104465654519, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 31333.53914067683, 
				5099.100592356345, 16255.462952340053, -24328.79404472696, 2110.3737460210023, -30436.002199695413, -7620.5647206287895, 
				-1584.8390776281606, 13070.599265693543, 1175.1519857281833, -4020.606882150761, 418.2295357448376, 

				-2333.8894383455245, -7598.435636609185, 3404.0461255095533, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 3480.532568922084, 
				-35045.66981325363, -39029.38728430877, 50580.12308898679, -30436.002197283913, 35260.620117340644, -52756.56180232125, 
				75615.7718945662, -1485.9165868406699, 1029.7955810252772, -2535.6642493851577, 1850.6376282992396, 

				-6926.681670634185, -2718.9966884787905, -1238.9557626074923, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -4958.278844654196, 
				46388.19336036649, -144151.80073495753, 3767.7300141022138, -7620.564721196201, -52756.56180167278, 1987.6447707301681, 
				-57419.69026185732, 132336.3659389965, 6129.585732391279, 21371.058310436296, 65810.95236024134, 

				2977.9491815735432, 1396.276599725966, 402.6599772627234, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1690.5794092208355, 
				-78366.91868028537, 76186.99936462974, 63853.467514866985, -1584.8390788035133, 75615.77189489045, -57419.690259587675, 
				58174.56288014186, -121800.14623942792, -11102.30584603316, 20380.918277559344, -30405.284997354993, 

				7187.187135488463, 2579.80435236315, 1423.6338554457195, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -68702.35308276265, 
				159732.38127859597, 90648.00728685039, -73454.72653765966, 13070.599267071542, -1485.9165810044362, 132336.36593283602, 
				-121800.14626180014, -24542.566560063817, 2633.526551489897, -53582.638636635806, -66043.15800187633, 

				-81.64944707933417, -69.70750029399926, 6.930990460732019, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1430.7331251113583, 
				-5725.922572840765, -31480.98367333929, -5756.762054651566, 1175.1519860422861, 1029.7955817041448, 6129.585735116881, 
				-11102.305847897513, 2633.526544721487, 1139.5588915581948, 15722.783935516873, 27810.7305571014, 

				3813.8847032270223, 1961.8599675504342, 416.3898064003634, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -61323.010835601446, 
				7050.876572752295, 111242.89005260104, 20415.283896148314, -4020.6068811577907, -2535.664247601864, 21371.058308328767, 
				20380.918272857933, -53582.63861604687, 15722.783927856815, -25373.04793133414, -55540.97699535267, 

				5282.398640105757, 2564.9494251275173, 663.8239015195365, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -97936.32445269593, 
				82963.08307171625, 161598.32557417822, -967.7571200321518, 418.22953566377885, 1850.6376276507692, 65810.95236867145, 
				-30405.285018754515, -66043.15799458104, 27810.730563905272, -55540.977013509844, -98069.62910876748 };

		AngleBending ab = new AngleBending();
		ab.setMMFF94AngleBendingParameters(molecule, mmff94Tables, true);

		//System.out.println("ab.functionMMFF94SumEA(moleculeCoordinates) = " + ab.functionMMFF94SumEA(moleculeCoordinates));
		assertEquals(testResult_SumEA, ab.functionMMFF94SumEA(moleculeCoordinates), 0.00001);

		ab.set2ndOrderErrorApproximateGradientMMFF94SumEA(moleculeCoordinates);
		//System.out.println("ab.get2ndOrderErrorApproximateGradientMMFF94SumEA() = " + ab.get2ndOrderErrorApproximateGradientMMFF94SumEA());
		for (int i = 0; i < testResult_gradientSumEA.length; i++) {
			assertEquals(testResult_gradientSumEA[i], ab.get2ndOrderErrorApproximateGradientMMFF94SumEA().getElement(i), 0.00001);
		}

		ab.set2ndOrderErrorApproximateHessianMMFF94SumEA(moleculeCoordinates);
		//System.out.println("HessianMMFF94SumEA = ");
		for (int i = 0; i < 24; i++) {
			//System.out.println("");
			//System.out.println("");
			for (int j = 0; j < 24; j++) {
				//System.out.print(ab.get2ndOrderErrorApproximateHessianMMFF94SumEA().getElement(i, j) + ", ");
				assertEquals(testResult_hessianSumEA[i * 24 + j], ab.get2ndOrderErrorApproximateHessianMMFF94SumEA().getElement(i, j), 0.00001);
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

		double testResult_SumEBA = 1096.132616057917;
		double[] testResult_gradientSumEBA = {-232.43662469584442,524.0391673938808,-1874.0779288399374,-486.25278124318277,-720.7065644970131,
				758.1905008692862,1034.5493204593322,-257.8988343873911,1489.817837890341,-528.9122465691897,359.7080555533199,61.6691149874617,
				1952.239939601563,1746.8123237595212,-140.60603775064803,-1253.2985689229197,-1091.033422248623,112.88500346789247,
				-985.499642614552,-1614.601758362966,-397.1951497231564,499.610604177308,1053.681032637286,-10.683340789783628};
		double[] testResult_hessianSumEBA = {1064.2077764580126, 1575.6393183074854, 1226.1412665366695, -393.947169145911, -343.837592232787, 
				-513.9028780299227, -507.85530855179326, -772.238005499823, -388.48029580240586, -79.31812391430647, -336.2348384955319, 
				-182.76372735139236, -83.08717484600216, -123.3288820793438, -140.9943653529483, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				1575.6393183074854, 1116.967165694249, 1753.2393339810665, -343.837592232787, -220.9661639115649, -637.9755633319388, 
				-772.238005499823, -681.1564432365352, -813.363475441418, -336.2348384955319, -148.57124553427576, -260.67348917385783, 
				-123.3288820793438, -66.27331301187328, -41.22680603385182, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				1226.1412665366695, 1753.2393339810665, 625.0887056046764, -513.9028780299227, -637.9755633319388, -216.8892563753322, 
				-388.48029580240586, -813.363475441418, -196.63553602080208, -182.76372735139236, -260.67348917385783, -214.13351323123067, 
				-140.9943653529483, -41.22680603385182, 2.569600022688479, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-393.947169145911, -343.837592232787, -513.9028780299227, 393.947169145911, 343.837592232787, 513.9028780299227, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-343.837592232787, -220.9661639115649, -637.9755633319388, 343.837592232787, 220.9661639115649, 637.9755633319388, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-513.9028780299227, -637.9755633319388, -216.8892563753322, 513.9028780299227, 637.9755633319388, 216.8892563753322, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-507.85530855179326, -772.238005499823, -388.48029580240586, 0.0, 0.0, 0.0, 507.85530855179326, 772.238005499823, 
				388.48029580240586, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-772.238005499823, -681.1564432365352, -813.363475441418, 0.0, 0.0, 0.0, 772.238005499823, 681.1564432365352, 813.363475441418, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-388.48029580240586, -813.363475441418, -196.63553602080208, 0.0, 0.0, 0.0, 388.48029580240586, 813.363475441418, 
				196.63553602080208, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-79.31812391430647, -336.2348384955319, -182.76372735139236, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 79.31812391430647, 336.2348384955319, 
				182.76372735139236, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-336.2348384955319, -148.57124553427576, -260.67348917385783, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 336.2348384955319, 148.57124553427576, 
				260.67348917385783, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-182.76372735139236, -260.67348917385783, -214.13351323123067, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 182.76372735139236, 260.67348917385783, 
				214.13351323123067, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-83.08717484600216, -123.3288820793438, -140.9943653529483, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 546.7114953969686, 
				811.0439170434668, 1066.7352501510989, -265.47621611669643, -192.52635084559526, -293.26664098187786, -185.68048777681068, 
				-286.8449832142811, -301.4065393757353, -12.467616657459397, -208.3437009042466, -331.0677044405373, 

				-123.3288820793438, -66.27331301187328, -41.22680603385182, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 811.0439170434668, 
				475.51900857408253, 1052.1740189955967, -192.52635084559526, 211.39975500735378, -346.4029625990998, -286.8449832142811, 
				-358.75709442886864, -369.10808013399856, -208.3437009042466, -261.88835614069444, -295.4361702286467, 

				-140.9943653529483, -41.22680603385182, 2.569600022688479, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1066.7352501510989, 
				1052.1740189955967, 922.4192564573183, -293.26664098187786, -346.4029625990998, -273.0242327467457, -301.4065393757353, 
				-369.10808013399856, -378.8529988795061, -331.0677044405373, -295.4361702286467, -273.11162485375485, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -265.47621611669643, -192.52635084559526, -293.26664098187786, 
				265.47621611669643, 192.52635084559526, 293.26664098187786, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -192.52635084559526, 211.39975500735378, -346.4029625990998, 
				192.52635084559526, -211.39975500735378, 346.4029625990998, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -293.26664098187786, -346.4029625990998, -273.0242327467457, 
				293.26664098187786, 346.4029625990998, 273.0242327467457, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -185.68048777681068, -286.8449832142811, -301.4065393757353, 
				0.0, 0.0, 0.0, 185.68048777681068, 286.8449832142811, 301.4065393757353, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -286.8449832142811, -358.75709442886864, -369.10808013399856, 
				0.0, 0.0, 0.0, 286.8449832142811, 358.75709442886864, 369.10808013399856, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -301.4065393757353, -369.10808013399856, -378.8529988795061, 
				0.0, 0.0, 0.0, 301.4065393757353, 369.10808013399856, 378.8529988795061, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -12.467616657459397, -208.3437009042466, -331.0677044405373, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 12.467616657459397, 208.3437009042466, 331.0677044405373, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -208.3437009042466, -261.88835614069444, -295.4361702286467, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 208.3437009042466, 261.88835614069444, 295.4361702286467, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -331.0677044405373, -295.4361702286467, -273.11162485375485, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 331.0677044405373, 295.4361702286467, 273.11162485375485};
		
		StretchBendInteractions sbi = new StretchBendInteractions();
		sbi.setMMFF94StretchBendParameters(molecule, mmff94Tables, false);

		sbi.setFunctionMMFF94SumEBA(moleculeCoordinates);
		//System.out.println("sbi.getFunctionMMFF94SumEBA() = " + sbi.getFunctionMMFF94SumEBA());
		assertEquals(testResult_SumEBA, sbi.getFunctionMMFF94SumEBA(), 0.00001);

		sbi.set2ndOrderErrorApproximateGradientMMFF94SumEBA(moleculeCoordinates);
		//System.out.println("sbi.get2ndOrderErrorApproximateGradientMMFF94SumEBA() = " + sbi.get2ndOrderErrorApproximateGradientMMFF94SumEBA());
		for (int i = 0; i < testResult_gradientSumEBA.length; i++) {
			assertEquals(testResult_gradientSumEBA[i], sbi.get2ndOrderErrorApproximateGradientMMFF94SumEBA().getElement(i), 0.00001);
		}

		sbi.setHessianMMFF94SumEBA(moleculeCoordinates);
		//System.out.println("HessianMMFF94SumEBA = ");
		for (int i = 0; i < 24; i++) {
			//System.out.println("");
			//System.out.println("");
			for (int j = 0; j < 24; j++) {
				//System.out.print(sbi.getHessianMMFF94SumEBA().getElement(i, j) + ", ");
				assertEquals(testResult_hessianSumEBA[i * 24 + j], sbi.getHessianMMFF94SumEBA().getElement(i, j), 0.00001);
			}
		}

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
	public IAtomContainer coordinateScrambler(IAtomContainer molecule, int min, double positiveShift, double negativeShift) {
		double nPertubatedAtoms = min + Math.random() * molecule.getAtomCount();
		double coord = 0.0;
		logger.debug("Number of pertubated Atoms:" + nPertubatedAtoms);
		for (int i = 0; i < nPertubatedAtoms; i++) {
			coord = Math.random();
			if (coord <= 0.33) {
				if (Math.random() <= 0.) {
					molecule.getAtom(i).setX3d(molecule.getAtom(i).getPoint3d().x + positiveShift);
				} else {
					molecule.getAtom(i).setX3d(molecule.getAtom(i).getPoint3d().x - negativeShift);
				}
			} else if (coord <= 0.66) {
				if (Math.random() <= 0.) {
					molecule.getAtom(i).setY3d(molecule.getAtom(i).getPoint3d().y + positiveShift);
				} else {
					molecule.getAtom(i).setY3d(molecule.getAtom(i).getPoint3d().y - negativeShift);
				}
			} else {
				if (Math.random() <= 0.) {
					molecule.getAtom(i).setZ3d(molecule.getAtom(i).getPoint3d().z + positiveShift);
				} else {
					molecule.getAtom(i).setZ3d(molecule.getAtom(i).getPoint3d().z - negativeShift);
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

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with Torsions");

		double testResult_MMFF94SumET = 11.369615843222473;
		double[] testResult_gradientSumET = {-2.9021441715059266,0.365841131889513,-1.3798123459631118,2.334296171767999,
				-7.741921297512765E-4,0.9419919236223522,-1.0919747760635934,-1.4243907634213469,0.3741346043388054,
				0.8706944865031971,1.081124943590873,-0.2670804663450631,1.9172157299433363,0.1735281313651372,
				0.6740797133199399,-1.0891396653529841,0.12919063902722416,-0.5131917159860654,0.2969423891730237,
				-1.1492480840394526,0.7770108103021226,-0.33589016359430374,0.8247281932428495,-0.6071325229723439};
		double[] testResult_hessianSumET = {0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 
				0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 
				0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 
				0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 0.618245987948552, 
				0.618245987948552, 

				-1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, 
				-1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, 
				-1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, 
				-1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, -1.9559306031700696, 

				1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 
				1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 
				1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 
				1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 1.3679397254378274, 

				-1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, 
				-1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, 
				-1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, 
				-1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, -1.9611727123824523, 

				6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 
				6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 
				6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 
				6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 6.504419782056093E-4, 

				-0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, 
				-0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, 
				-0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, 
				-0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, -0.7914200768597389, 

				-1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, 
				-1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, 
				-1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, 
				-1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, -1.651798423864038, 

				-2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, 
				-2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, 
				-2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, 
				-2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, -2.154634400475277, 

				0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 
				0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 
				0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 
				0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 0.5659425137028654, 

				3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 
				3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 
				3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 
				3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 3.378802068568724, 

				4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 
				4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 
				4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 
				4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 4.195394885123073, 

				-1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, 
				-1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, 
				-1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, 
				-1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, -1.0364278700844103, 

				-0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, 
				-0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, 
				-0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, 
				-0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, -0.4839472141487817, 

				-2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, 
				-2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, 
				-2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, 
				-2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, -2.625665858605127, 

				1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 
				1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 
				1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 
				1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 1.3063765272395833, 

				1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 
				1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 
				1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 
				1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 1.4130145712631725, 

				-0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, 
				-0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, 
				-0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, 
				-0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, -0.16760775622175592, 

				0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 
				0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 
				0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 
				0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 0.6657983322868645, 

				0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 
				0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 
				0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 
				0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 0.36499296253183455, 

				-1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, 
				-1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, 
				-1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, 
				-1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, -1.4126223743318331, 

				0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 
				0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 
				0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 
				0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 0.9550791263416403, 

				-1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, 
				-1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, 
				-1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, 
				-1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, -1.6781372399120633, 

				4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 
				4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 
				4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 
				4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 4.1204156663162665, 

				-3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, 
				-3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, 
				-3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, 
				-3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316, -3.0332882772928316};

		Torsions t = new Torsions();
		t.setMMFF94TorsionsParameters(molecule, mmff94Tables);

		//logger.debug("functionMMFF94SumET = " + t.functionMMFF94SumET(moleculeCoordinates));

		assertEquals(testResult_MMFF94SumET, t.functionMMFF94SumET(moleculeCoordinates), 0.00001);

		t.set2ndOrderErrorApproximateGradientMMFF94SumET(moleculeCoordinates);
		//System.out.println("t.get2ndOrderErrorApproximateGradientMMFF94SumET() = " + t.get2ndOrderErrorApproximateGradientMMFF94SumET());
		for (int i = 0; i < testResult_gradientSumET.length; i++) {
			assertEquals(testResult_gradientSumET[i], t.get2ndOrderErrorApproximateGradientMMFF94SumET().getElement(i), 0.00001);
		}

		t.set2ndOrderErrorApproximateHessianMMFF94SumET(moleculeCoordinates);
		//System.out.println("HessianMMFF94SumET = ");
		for (int i = 0; i < 24; i++) {
			//System.out.println("");
			//System.out.println("");
			for (int j = 0; j < 24; j++) {
				//System.out.print(t.get2ndOrderErrorApproximateHessianMMFF94SumET().getElement(i, j) + ", ");
				assertEquals(testResult_hessianSumET[i * 24 + j], t.get2ndOrderErrorApproximateHessianMMFF94SumET().getElement(i, j), 0.00001);
			}
		}
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

		double testResult_MMFF94SumEvdW = -0.018315208443555175;
		double testResult_CCGSumEvdWSK = -0.018315208443555175;
		double testResult_CCGSumEvdWAv = -0.01850601324794888;
		double[] testResult_gradientSumEvdW = {0.0,0.0,0.0,0.08689361826411737,-0.29019969341910473,-0.43372641964010566,
				0.17856051586843263,-0.03192618756787894,-0.3851429443130647,0.07502699191795303,-0.2145185420473048,
				-0.19094439695781296,0.0,0.0,0.0,-0.06217431032762669,0.2299101716788855,0.23770994928883152,
				-0.04530893553380176,0.2501302303266474,0.3169509609136706,-0.23299788018907458,0.0566040210287556,
				0.4551528507084811};
		double[] testResult_hessianSumEvdW = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				-0.49533950589829034, -0.49533950589829034, -0.49533950589829034, -0.6313613355244059, -0.8396575394554282, -0.87933019708822, 
				-0.49533950589829034, -0.49533950589829034, -0.49533950589829034, -0.49533950589829034, -0.49533950589829034, -0.49533950589829034, 
				-0.49533950589829034, -0.49533950589829034, -0.49533950589829034, -0.38956801034811495, -0.21396298051840593, -0.2182233583925277, 
				-0.43649700491524, -0.4404739425217814, -0.45492671577482213, -0.5239316728054003, -0.48726356109754576, -0.4288777523375914, 

				1.2214708065904647, 1.2214708065904647, 1.2214708065904647, 0.877152773033327, 1.5598471380761487, 1.6156438888325493, 
				1.2214708065904647, 1.2214708065904647, 1.2214708065904647, 1.2214708065904647, 1.2214708065904647, 1.2214708065904647, 
				1.2214708065904647, 1.2214708065904647, 1.2214708065904647, 1.502847331970349, 0.8368104957874781, 0.8529693074284388, 
				1.2763363699669736, 1.2540860897372723, 1.173225315243967, 1.2295467513912093, 1.2351395027609595, 1.2440447148569038, 

				2.1345402262911017, 2.1345402262911017, 2.1345402262911017, 1.7505495351011717, 2.5287133085331863, 2.9051286869238795, 
				2.1345402262911017, 2.1345402262911017, 2.1345402262911017, 2.1345402262911017, 2.1345402262911017, 2.1345402262911017, 
				2.1345402262911017, 2.1345402262911017, 2.1345402262911017, 2.4116563737968644, 1.7660387271290754, 1.7817021459939755, 
				2.1749530164145696, 2.0862947349446035, 1.7640975657000928, 2.2010019798518003, 2.1571141345575406, 2.0872325065464583, 

				-0.6297778101582324, -0.6297778101582324, -0.6297778101582324, -0.6297778101582324, -0.6297778101582324, -0.6297778101582324, 
				-0.6089325204028525, -0.8604451401401128, -1.279529368262116, -0.6297778101582324, -0.6297778101582324, -0.6297778101582324, 
				-0.6297778101582324, -0.6297778101582324, -0.6297778101582324, -0.6320285705874827, -0.6289559903994892, -0.629651963362336, 
				-0.6295391552100761, -0.6280490163314937, -0.6182458048153735, -0.6486109944325186, -0.40166109376183434, 0.008315895806895436, 

				0.34441611599074545, 0.34441611599074545, 0.34441611599074545, 0.34441611599074545, 0.34441611599074545, 0.34441611599074545, 
				0.11374878600886529, 0.15672812306998973, 0.2567881639921953, 0.34441611599074545, 0.34441611599074545, 0.34441611599074545, 
				0.34441611599074545, 0.34441611599074545, 0.34441611599074545, 0.3452379357494887, 0.36530464624691805, 0.36075931720585486, 
				0.3461449098174842, 0.34800561233253935, 0.36024665951668944, 0.5725328323871437, 0.5076260823135346, 0.39987032324824223, 

				1.7950970816508836, 1.7950970816508836, 1.7950970816508836, 1.7950970816508836, 1.7950970816508836, 1.7950970816508836, 
				1.145345523547, 1.7074691296523334, 2.6501399497449976, 1.7950970816508836, 1.7950970816508836, 1.7950970816508836, 
				1.7950970816508836, 1.7950970816508836, 1.7950970816508836, 1.79522292844678, 1.8114402828659928, 1.8077668750087206, 
				1.8066290869937425, 1.8109276251768274, 1.839206518925974, 2.4331907876160117, 1.8505512889083802, 0.8832749829238413, 

				-0.5439414689838414, -0.5439414689838414, -0.5439414689838414, -0.5439414689838414, -0.5439414689838414, -0.5439414689838414, 
				-0.5439414689838414, -0.5439414689838414, -0.5439414689838414, -0.573711549977258, -0.7815604539668985, -0.7807285851515327, 
				-0.5439414689838414, -0.5439414689838414, -0.5439414689838414, -0.5440124324947035, -0.5686587599124093, -0.5546135168962161, 
				-0.45567876653944217, -0.3290175063805018, -0.34810924538566984, -0.602363126923962, -0.4965291556755561, -0.492314528501947, 

				1.1359183917803006, 1.1359183917803006, 1.1359183917803006, 1.1359183917803006, 1.1359183917803006, 1.1359183917803006, 
				1.1359183917803006, 1.1359183917803006, 1.1359183917803006, 0.8982994067972434, 1.4602334223259112, 1.4163747768880244, 
				1.1359183917803006, 1.1359183917803006, 1.1359183917803006, 1.1112011008517326, 1.1871127634614052, 1.143852859823585, 
				1.3508423543836399, 0.760355344848344, 0.8493598586029334, 1.183330705088586, 1.135972036485542, 1.1340860718066597, 

				0.9564440551953459, 0.9564440551953459, 0.9564440551953459, 0.9564440551953459, 0.9564440551953459, 0.9564440551953459, 
				0.9564440551953459, 0.9564440551953459, 0.9564440551953459, 0.7196569390276547, 1.2369004403030694, 1.1769215835558002, 
				0.9564440551953459, 0.9564440551953459, 0.9564440551953459, 0.9457720072829713, 0.9643785232386305, 0.9537751974833226, 
				1.1522762787935175, 0.6698855220179788, 0.7425966131227676, 1.0080709956772402, 0.9546117352217048, 0.9524828266194932, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.26913213005937325, 0.26913213005937325, 0.26913213005937325, 0.3749036256095486, 0.5505086554392575, 0.5462482775651357, 
				0.26688136963012293, 0.26995394981811643, 0.26925797685526975, 0.2690611665485111, 0.2444148391308054, 0.2584600821469985, 
				0.26913213005937325, 0.26913213005937325, 0.26913213005937325, 0.16568235844931034, 0.011651075849313478, 0.0025621836700887995, 
				0.26913213005937325, 0.26913213005937325, 0.26913213005937325, 0.26913213005937325, 0.26913213005937325, 0.26913213005937325, 

				-1.2155125612018092, -1.2155125612018092, -1.2155125612018092, -0.934136035821925, -1.6001728720047956, -1.584014060363835, 
				-1.2146907414430659, -1.1946240309456364, -1.1991693599866997, -1.2402298521303772, -1.1643181895207046, -1.2075780931585247, 
				-1.2155125612018092, -1.2155125612018092, -1.2155125612018092, -1.4729936154118688, -0.9029351523361, -0.8712887312981772, 
				-1.2155125612018092, -1.2155125612018092, -1.2155125612018092, -1.2155125612018092, -1.2155125612018092, -1.2155125612018092, 

				-1.1356750227757766, -1.1356750227757766, -1.1356750227757766, -0.858558875270014, -1.5041765219378027, -1.4885131030729026, 
				-1.13554917597988, -1.1193318215606671, -1.1230052294179393, -1.1463470706881513, -1.1277405547324921, -1.1383438804877999, 
				-1.1356750227757766, -1.1356750227757766, -1.1356750227757766, -1.402244969165061, -0.7914511928721446, -0.7928378781244645, 
				-1.1356750227757766, -1.1356750227757766, -1.1356750227757766, -1.1356750227757766, -1.1356750227757766, -1.1356750227757766, 

				0.1389109884602232, 0.1389109884602232, 0.1389109884602232, 0.1977534894432735, 0.19377655183673212, 0.17932377858369142, 
				0.13914964340837963, 0.14063978228696195, 0.15044299380308213, 0.22717369090462247, 0.3538349510635629, 0.33474321205839475, 
				0.1389109884602232, 0.1389109884602232, 0.1389109884602232, 0.1389109884602232, 0.1389109884602232, 0.1389109884602232, 
				-0.008432869915382862, -0.13260733134636407, -0.10886603060427552, 0.1389109884602232, 0.1389109884602232, 0.1389109884602232, 

				-1.199176020429921, -1.199176020429921, -1.199176020429921, -1.1443104570534122, -1.1665607372831137, -1.247421511776419, 
				-1.1974472266031824, -1.1955865240881272, -1.1833454769039773, -0.9842520578265814, -1.5747390673618775, -1.4857345536072883, 
				-1.199176020429921, -1.199176020429921, -1.199176020429921, -1.199176020429921, -1.199176020429921, -1.199176020429921, 
				-1.4706943402365082, -0.8598177529865658, -0.8802025394320001, -1.199176020429921, -1.199176020429921, -1.199176020429921, 

				-1.6780627528193968, -1.6780627528193968, -1.6780627528193968, -1.6376499626959284, -1.7263082441658946, -2.0485054134104055, 
				-1.6665307474765376, -1.6622322092934527, -1.6339533155443058, -1.482230529221225, -1.9646212859967638, -1.891910194891975, 
				-1.6780627528193968, -1.6780627528193968, -1.6780627528193968, -1.6780627528193968, -1.6780627528193968, -1.6780627528193968, 
				-1.9258397718838953, -1.3590892718214755, -1.1378820874309001, -1.6780627528193968, -1.6780627528193968, -1.6780627528193968, 

				1.2610156665207677, 1.2610156665207677, 1.2610156665207677, 1.2324234996136578, 1.2690916113215123, 1.3274774200814667, 
				1.2421824822464815, 1.489132382917166, 1.8991093724858958, 1.2025940085806472, 1.308427979829053, 1.3126426070026622, 
				1.2610156665207677, 1.2610156665207677, 1.2610156665207677, 1.2610156665207677, 1.2610156665207677, 1.2610156665207677, 
				1.2610156665207677, 1.2610156665207677, 1.2610156665207677, 1.3668626756422846, 0.9774106920153398, 0.5048332665130466, 

				-0.2871167327297804, -0.2871167327297804, -0.2871167327297804, -0.27904078792903586, -0.2734480365592854, -0.26454282446334115, 
				-0.0590000163333822, -0.12390676640699119, -0.23166252547228358, -0.23970441942149506, -0.28706308802453917, -0.28894905270342147, 
				-0.2871167327297804, -0.2871167327297804, -0.2871167327297804, -0.2871167327297804, -0.2871167327297804, -0.2871167327297804, 
				-0.2871167327297804, -0.2871167327297804, -0.2871167327297804, -0.5707217072352084, -0.4640490399283057, -0.3633125282800753, 

				-2.0723435875421576, -2.0723435875421576, -2.0723435875421576, -2.0058818339814586, -2.0497696792757183, -2.1196513072868006, 
				-1.4342498815770297, -2.016889380284661, -2.9841656862691996, -2.0207166470602633, -2.074175907515799, -2.0763048161180104, 
				-2.0723435875421576, -2.0723435875421576, -2.0723435875421576, -2.0723435875421576, -2.0723435875421576, -2.0723435875421576, 
				-2.0723435875421576, -2.0723435875421576, -2.0723435875421576, -2.828525987549879, -2.1485393830924524, -1.1092525404946196};

		VanDerWaalsInteractions vdwi = new VanDerWaalsInteractions();
		vdwi.setMMFF94VanDerWaalsParameters(molecule, mmff94Tables);
		vdwi.setAtomDistance(moleculeCoordinates);

		SmoothingFunctions sf = new SmoothingFunctions();
		sf.setSmoothingFunction(vdwi.getAtomDistance());

		vdwi.setFunctionMMFF94SumEvdW(moleculeCoordinates);
		//System.out.println("functionMMFF94SumEvdW = " + vdwi.getFunctionMMFF94SumEvdW());
		//System.out.println("functionCCGSumEvdWSK = " + vdwi.functionCCGSumEvdWSK(moleculeCoordinates,sf.getSmoothingFunction()));
		//System.out.println("functionCCGSumEvdWAv = " + vdwi.functionCCGSumEvdWAv(moleculeCoordinates,sf.getSmoothingFunction()));

		assertEquals(testResult_MMFF94SumEvdW, vdwi.getFunctionMMFF94SumEvdW(), 0.00001);
		assertEquals(testResult_CCGSumEvdWSK, vdwi.functionCCGSumEvdWSK(moleculeCoordinates, sf.getSmoothingFunction()), 0.00001);
		assertEquals(testResult_CCGSumEvdWAv, vdwi.functionCCGSumEvdWAv(moleculeCoordinates, sf.getSmoothingFunction()), 0.00001);

		vdwi.setGradientMMFF94SumEvdW(moleculeCoordinates);
		//System.out.println("vdwi.gradientMMFF94SumEvdW(moleculeCoordinates) = " + vdwi.getGradientMMFF94SumEvdW());
		for (int i = 0; i < testResult_gradientSumEvdW.length; i++) {
			assertEquals(testResult_gradientSumEvdW[i], vdwi.getGradientMMFF94SumEvdW().getElement(i), 0.00001);
		}

		vdwi.setHessianMMFF94SumEvdW(moleculeCoordinates);
		//System.out.println("HessianMMFF94SumEQ = ");
		for (int i = 0; i < 24; i++) {
			//System.out.println("");
			//System.out.println("");
			for (int j = 0; j < 24; j++) {
				//System.out.print(vdwi.getHessianMMFF94SumEvdW().getElement(i, j) + ", ");
				assertEquals(testResult_hessianSumEvdW[i * 24 + j], vdwi.getHessianMMFF94SumEvdW().getElement(i, j), 0.00001);
			}
		}
	}


	/**
	 *  A unit test for JUnit (ElectrostaticInteraction)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testElectrostaticInteraction() throws ClassNotFoundException, CDKException, java.lang.Exception {

		//System.out.println("");
		//System.out.println("FORCEFIELDTESTS with ElectrostaticInteraction");

		double testResult_MMFF94SumEQ = 0.0;
		double[] testResult_gradientSumEQ = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		double[] testResult_hessianSumEQ = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 

				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

		ElectrostaticInteractions ei = new ElectrostaticInteractions();
		ei.setMMFF94ElectrostaticParameters(molecule, mmff94Tables);
		ei.setInternuclearSeparation(moleculeCoordinates);

		//System.out.println("functionMMFF94SumEQ = " + ei.functionMMFF94SumEQ(moleculeCoordinates));

		assertEquals(testResult_MMFF94SumEQ, ei.functionMMFF94SumEQ(moleculeCoordinates), 0.00001);

		ei.setGradientMMFF94SumEQ(moleculeCoordinates);
		//System.out.println("ei.gradientMMFF94SumEQ(moleculeCoordinates) = " + ei.getGradientMMFF94SumEQ());
		for (int i = 0; i < testResult_gradientSumEQ.length; i++) {
			assertEquals(testResult_gradientSumEQ[i], ei.getGradientMMFF94SumEQ().getElement(i), 0.00001);
		}

		ei.setHessianMMFF94SumEQ(moleculeCoordinates);
		//System.out.println("HessianMMFF94SumEQ = ");
		for (int i = 0; i < 24; i++) {
			//System.out.println("");
			//System.out.println("");
			for (int j = 0; j < 24; j++) {
				//System.out.print(ei.getHessianMMFF94SumEQ().getElement(i, j) + ", ");
				assertEquals(testResult_hessianSumEQ[i * 24 + j], ei.getHessianMMFF94SumEQ().getElement(i, j), 0.00001);
			}
		}
	}
	
	
	/**
	 *  A unit test for JUnit (Ethane test)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testEthaneMoleculeMinimization() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTEST with Ethane molecule minimization");
		
	    // here goes the FF code

		ForceField forceField = new ForceField();
        forceField.setUsedGMMethods(false, true, false);
        forceField.setConvergenceParametersForCGM(100000, 0.000001);
        forceField.setConvergenceParametersForNRM(100000, 1);
        forceField.setPotentialFunction("mmff94");

     	try {
        	//set partial charges
        	forceField.setMolecule(molecule, false);
            forceField.minimize();
        } catch (Exception exception) {
        	logger.error("Error while running ForceField minimization: ", exception.getMessage());
            logger.debug(exception);
        }
 
        molecule = forceField.getMolecule();
        //logger.debug("Molecule: ", molecule);

        try {
        	FileWriter fileWriter = new FileWriter("./" + input + "-output.mol");
        	//stringWriter.write(input + "-output.mol");
        	MDLWriter mdlWriter = new MDLWriter(fileWriter);
    		mdlWriter.write(molecule);
            mdlWriter.close();
            
        } catch (Exception exception) {
        	System.out.println("Could not write Molecule to MDL file : " + exception.getMessage());
        	System.out.println(exception);
        }       

		assertEquals(34.01, forceField.getMinimumFunctionValueCGM(), 0.01);

	}

	/**
	 *  A unit test for JUnit (Ethan test)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
/*	public void testButaneMoleculeMinimization() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTEST with Butane molecule minimization");
		input = "Butane-TestFF";
		
		try {

			FileReader fileReader = new FileReader("src/data/mdl/" + input + ".mol");
			MDLReader mdlReader = new MDLReader(fileReader);
        	molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
        	mdlReader.close();
 
         } catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }       
		
	    // here goes the FF code

	    //System.out.println("molecule.getAtomCount() : " + molecule.getAtomCount());
	    //System.out.println("molecule.getBondCount() : " + molecule.getBondCount());
	    if (molecule.getAtomCount() == 12 & molecule.getBondCount() == 11) {
	       	molecule.getAtom(3).setCharge(1);
	       	molecule.getAtom(8).setCharge(1);
	    }
	    //logger.debug("Molecule: ", molecule);

		ForceField forceField = new ForceField();
        forceField.setUsedGMMethods(false, true, false);
        //forceField.setConvergenceParametersForSDM(15, 0.000000001);
        forceField.setConvergenceParametersForCGM(100000, 1);
        forceField.setPotentialFunction("mmff94");

     	try {
        	//set partial charges
        	forceField.setMolecule(molecule, false);
            forceField.minimize();
        } catch (Exception exception) {
        	logger.error("Error while running ForceField minimization: ", exception.getMessage());
            logger.debug(exception);
        }
 
        molecule = forceField.getMolecule();
        //logger.debug("Molecule: ", molecule);

        try {
        	StringWriter stringWriter = new StringWriter();
        	stringWriter.write("./" + input + "-output.mol");
        	MDLWriter mdlWriter = new MDLWriter(stringWriter);
    		mdlWriter.write(molecule);
            mdlWriter.close();
            
        } catch (Exception exception) {
        	System.out.println("Could not write Molecule to MDL file : " + exception.getMessage());
        	System.out.println(exception);
        }       

		assertEquals(310.60564687498936, forceField.getMinimumFunctionValueCGM(), 0.00001);
	}
*/
	
	/**
	 *  A unit test for JUnit (Heptane test)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testHeptaneMoleculeMinimization() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTEST with Heptane molecule minimization");
		input = "heptane-modelbuilder";
		//input = "src/data/mdl/Heptane-TestFF";
		try {

			InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/mdl/" + input + ".mol");
			MDLReader mdlReader = new MDLReader(is);
        	molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
        	mdlReader.close();
 
         } catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }       

        // here goes the FF code

		ForceField forceField = new ForceField();
        forceField.setUsedGMMethods(false, true, false);
        //forceField.setConvergenceParametersForSDM(15, 0.000000001);
        forceField.setConvergenceParametersForCGM(100000, 1);
        forceField.setPotentialFunction("mmff94");

        //System.out.println("Setup completed");
     	try {
        	//set partial charges
        	forceField.setMolecule(molecule, false);
        	//System.out.println("Molecule assigned to force field");
            forceField.minimize();
        } catch (Exception exception) {
        	logger.error("Error while running ForceField minimization: ", exception.getMessage());
            logger.debug(exception);
        }
 
        molecule = forceField.getMolecule();
        //logger.debug("Molecule: ", molecule);

        try {
        	// Please don't write a file in the test cases.
        	FileWriter fileWriter = new FileWriter("./" + input + "-output.mol");
        	//stringWriter.write(input + "-output.mol");
        	MDLWriter mdlWriter = new MDLWriter(fileWriter);
    		mdlWriter.write(molecule);
            mdlWriter.close();
            
        } catch (Exception exception) {
        	System.out.println("Could not write Molecule to MDL file : " + exception.getMessage());
        	System.out.println(exception);
        }       

        if(!standAlone) assertEquals(734.17, forceField.getMinimumFunctionValueCGM(), 0.01);

	}

	
	/**
	 *  A unit test for JUnit (Butanoic Acid test)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
/*	public void testButanoicAcidMoleculeMinimization() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTEST with Butanoic Acid molecule minimization");
		input = "butanoic_acid";
		//input = "src/data/mdl/Heptane-TestFF";
		try {

			InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/mdl/" + input + ".mol");
			MDLReader mdlReader = new MDLReader(is);
        	molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
        	mdlReader.close();
 
         } catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }       

        // here goes the FF code

		ForceField forceField = new ForceField();
        forceField.setUsedGMMethods(false, true, false);
        //forceField.setConvergenceParametersForSDM(15, 0.000000001);
        forceField.setConvergenceParametersForCGM(100000, 1);
        forceField.setPotentialFunction("mmff94");

        //System.out.println("Setup completed");
     	try {
        	//set partial charges
        	forceField.setMolecule(molecule, false);
        	//System.out.println("Molecule assigned to force field");
            forceField.minimize();
        } catch (Exception exception) {
        	logger.error("Error while running ForceField minimization: ", exception.getMessage());
            logger.debug(exception);
        }
 
        molecule = forceField.getMolecule();
        //logger.debug("Molecule: ", molecule);

        try {
        	// Please don't write a file in the test cases.
        	FileWriter fileWriter = new FileWriter("./" + input + "-output.mol");
        	//stringWriter.write(input + "-output.mol");
        	MDLWriter mdlWriter = new MDLWriter(fileWriter);
    		mdlWriter.write(molecule);
            mdlWriter.close();
            
        } catch (Exception exception) {
        	System.out.println("Could not write Molecule to MDL file : " + exception.getMessage());
        	System.out.println(exception);
        }       

        if(!standAlone) assertEquals(244.96085356615137, forceField.getMinimumFunctionValueCGM(), 0.00001);
 
	}
*/	
	
	/**
	 *  A unit test for JUnit (Cyclo Propane test)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
/*	public void testCycloPropaneMoleculeMinimization() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTEST with Cyclo Propane molecule minimization");
		input = "cyclopropane";
		//input = "src/data/mdl/Heptane-TestFF";
		try {

			InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/mdl/" + input + ".mol");
			MDLReader mdlReader = new MDLReader(is);
        	molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
        	mdlReader.close();
 
         } catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }       

        // here goes the FF code

		ForceField forceField = new ForceField();
        forceField.setUsedGMMethods(false, true, false);
        //forceField.setConvergenceParametersForSDM(15, 0.000000001);
        forceField.setConvergenceParametersForCGM(100000, 1);
        forceField.setPotentialFunction("mmff94");

        //System.out.println("Setup completed");
     	try {
        	//set partial charges
        	forceField.setMolecule(molecule, false);
        	//System.out.println("Molecule assigned to force field");
            forceField.minimize();
        } catch (Exception exception) {
        	logger.error("Error while running ForceField minimization: ", exception.getMessage());
            logger.debug(exception);
        }
 
        molecule = forceField.getMolecule();
        //logger.debug("Molecule: ", molecule);

        try {
        	// Please don't write a file in the test cases.
        	FileWriter fileWriter = new FileWriter("./" + input + "-output.mol");
        	//stringWriter.write(input + "-output.mol");
        	MDLWriter mdlWriter = new MDLWriter(fileWriter);
    		mdlWriter.write(molecule);
            mdlWriter.close();
            
        } catch (Exception exception) {
        	System.out.println("Could not write Molecule to MDL file : " + exception.getMessage());
        	System.out.println(exception);
        }       

        if(!standAlone) assertEquals(1.8620299738612417E7, forceField.getMinimumFunctionValueCGM(), 0.00001);
 
	}
*/
	
	/**
	 *  A unit test for JUnit (Heptan test)
	 *
	 *@exception  ClassNotFoundException  Description of the Exception
	 *@exception  CDKException            Description of the Exception
	 *@exception  java.lang.Exception     Description of the Exception
	 */
	public void testMethylbenzolMoleculeMinimization() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		//System.out.println("");
		//System.out.println("FORCEFIELDTEST with methylbenzol molecule minimization");
		input = "methylbenzol";
		//input = "src/data/mdl/methylbenzol";
		try {

			InputStream is = this.getClass().getClassLoader().getResourceAsStream("data/mdl/" + input + ".mol");
			MDLReader mdlReader = new MDLReader(is);
        	molecule = (IMolecule)mdlReader.read(new org.openscience.cdk.Molecule());
        	mdlReader.close();
 
         } catch (Exception exception) {
            System.out.println("Could not read Molecule from file due to: " + exception.getMessage());
            System.out.println(exception);
        }       

        // here goes the FF code

		ForceField forceField = new ForceField();
        forceField.setUsedGMMethods(false, true, false);
        //forceField.setConvergenceParametersForSDM(15, 0.000000001);
        forceField.setConvergenceParametersForCGM(100000, 1);
        forceField.setPotentialFunction("mmff94");

        //System.out.println("Setup completed");
     	try {
        	//set partial charges
        	forceField.setMolecule(molecule, false);
        	//System.out.println("Molecule assigned to force field");
            forceField.minimize();
        } catch (Exception exception) {
        	logger.error("Error while running ForceField minimization: ", exception.getMessage());
            logger.debug(exception);
        }
 
        molecule = forceField.getMolecule();
        //logger.debug("Molecule: ", molecule);

        try {
        	// Please don't write a file in the test cases.
        	FileWriter fileWriter = new FileWriter("./" + input + "-output.mol");
        	//stringWriter.write(input + "-output.mol");
        	MDLWriter mdlWriter = new MDLWriter(fileWriter);
    		mdlWriter.write(molecule);
            mdlWriter.close();
            
        } catch (Exception exception) {
        	System.out.println("Could not write Molecule to MDL file : " + exception.getMessage());
        	System.out.println(exception);
        }       

        if(!standAlone) assertEquals(33.320297771974055, forceField.getMinimumFunctionValueCGM(), 0.00001);

	}

	
	public static void main(String[] args)
	{
		
		ForceFieldTests fft = new ForceFieldTests();
		fft.standAlone = true;
		try {
			fft.testGetMMFF94EnergyOfAMolecule();
			//fft.testSteepestDescentsMinimization();
			//fft.testConjugateGradientMinimization();
			//fft.testNewtonRaphsonMinimization();
			//fft.testBondStretching();
			//fft.testAngleBending();
			//fft.testStretchBendInteraction();
			//fft.testTorsions();
			//fft.testVanDerWaalsInteraction();
			//fft.testElectrostaticInteraction();
			//fft.testEthaneMoleculeMinimization();
			//fft.testButaneMoleculeMinimization();
			//fft.testHeptaneMoleculeMinimization();
			//fft.testButanoicAcidMoleculeMinimization();
			//fft.testCycloPropaneMoleculeMinimization();
			//fft.testMethylbenzolMoleculeMinimization();
		//} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//} catch (CDKException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

}
