/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 *  Copyright (C) 2004-2007  Miguel Rojas <miguel.rojas@uni-koeln.de>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.openscience.cdk.DefaultChemObjectBuilder;

import org.openscience.cdk.charges.MMFF94PartialCharges;
import org.openscience.cdk.exception.CDKException;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 * @cdk.bug    1627763
 */
public class PartialTChargeMMFF94DescriptorTest extends AtomicDescriptorTest {
	
	private final double METHOD_ERROR = 0.16;
	
		
	private final double[] AtomInChIToMMFF94PartialCharges(String InChI) {

		InChIGeneratorFactory factory = null;
		
		try {
			factory = InChIGeneratorFactory.getInstance();
		} catch (CDKException e2) {

			e2.printStackTrace();
		}
	
		InChIToStructure parser = null;
		try {
			parser = factory.getInChIToStructure(
					InChI, DefaultChemObjectBuilder.getInstance()
			);
		} catch (CDKException e1) {

			e1.printStackTrace();
		}
		
		IAtomContainer ac = parser.getAtomContainer();
		try {
			addExplicitHydrogens(ac);
		} catch (Exception e) {

			e.printStackTrace();
		}

		MMFF94PartialCharges mmff = new MMFF94PartialCharges();
		try {
			mmff.assignMMFF94PartialCharges(ac);
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		double[] testResult = new double[ac.getAtomCount()];
		int i = 0;
		for (IAtom atom : ac.atoms()) {

			//System.out.println(atom.getAtomTypeName() + " " + atom.getProperty("MMFF94charge").toString());
			testResult[i] = atom.getProperty("MMFF94charge");
			i++;

		}

		return testResult;

	}
	
	/**
	 *  Constructor for the PartialTChargeMMFF94DescriptorTest object
	 *  
	 *  All values taken from table V of Merck Molecular Force Field. II. Thomas A. Halgren
	 *  DOI: 10.1002/(SICI)1096-987X(199604)17:5/6<520::AID-JCC2>3.0.CO;2-W
	 *  
	 */
	public  PartialTChargeMMFF94DescriptorTest() {}
	
    @Before
    public void setUp() throws Exception {
    	setDescriptor(PartialTChargeMMFF94Descriptor.class);
    }
    
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	/**
	 *  A unit test for JUnit with Methanol
	 */
	@Test
	public void testPartialTotalChargeDescriptor_Methanol()
			throws ClassNotFoundException, CDKException, java.lang.Exception {

		double[] expectedResult = { 0.28, -0.68, 0.0, 0.0, 0.0, 0.4 };
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH4O/c1-2/h2H,1H3");

		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

	}
	/**
	 *  A unit test for JUnit with Methylamine
	 */
	@Test
    public void testPartialTotalChargeDescriptor_Methylamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] expectedResult={0.27,-0.99,0.0,0.0,0.0,0.36,0.36};		
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH5N/c1-2/h2H2,1H3");
		
		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);
				
	}
	/**
	 *  A unit test for JUnit with ethoxyethane
	 */
	@Test
    public void testPartialTotalChargeDescriptor_Ethoxyethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] expectedResult={0.0,0.0,0.28,0.28,-0.56,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C4H10O/c1-3-5-4-2/h3-4H2,1-2H3");
		
		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);
				

	}
	/**
	 *  A unit test for JUnit with Methanethiol
	 */
	@Test
    public void testPartialTotalChargeDescriptor_Methanethiol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] expectedResult={0.23,-0.41,0.0,0.0,0.0,0.18};
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH4S/c1-2/h2H,1H3");
		
		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);
	}
	/**
	 *  A unit test for JUnit with Chloromethane
	 */
	@Test
    public void testPartialTotalChargeDescriptor_Chloromethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] expectedResult={0.29,-0.29,0.0,0.0,0.0};
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/CH3Cl/c1-2/h1H3");
		
		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

	}
	/**
	 *  A unit test for JUnit with Benzene
	 */
	@Test
    public void testPartialTotalChargeDescriptor_Benzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] expectedResult={-0.15,-0.15,-0.15,-0.15,-0.15,-0.15,0.15,0.15,0.15,0.15,0.15,0.15};
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/C6H6/c1-2-4-6-5-3-1/h1-6H");
		
		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

	}
	/**
	 *  A unit test for JUnit with Water
	 */
	@Test
    public void testPartialTotalChargeDescriptor_Water() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double[] expectedResult={-0.86,0.43,0.43};
		double[] actualResult = AtomInChIToMMFF94PartialCharges("InChI=1S/H2O/h1H2");
		
		Assert.assertArrayEquals(expectedResult, actualResult, METHOD_ERROR);

	}	


}

