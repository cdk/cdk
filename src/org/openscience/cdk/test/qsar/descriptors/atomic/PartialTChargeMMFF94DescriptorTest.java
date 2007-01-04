/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-30 00:42:34 +0200 (Thu, 30 Mar 2006) $
 * $Revision: 5865 $
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
package org.openscience.cdk.test.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.PartialTChargeMMFF94Descriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */
public class PartialTChargeMMFF94DescriptorTest extends CDKTestCase {
	
	private final double METHOD_ERROR = 0.1;
	private HydrogenAdder haad=new HydrogenAdder();
	
	private final IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
	
	/**
	 *  Constructor for the PartialTChargeMMFF94DescriptorTest object
	 *
	 */
	public  PartialTChargeMMFF94DescriptorTest() {}
	
	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite() {
		return new TestSuite(PartialTChargeMMFF94DescriptorTest.class);
	}
	/**
	 *  A unit test for JUnit with Methanol
	 */
	public void testPartialTotalChargeDescriptor_Methanol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.28,-0.67,0.0,0.0,0.0,0.4};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(oxygen);
		mol.addBond(builder.newBond(carbon, oxygen, CDKConstants.BONDORDER_SINGLE));
		haad.addExplicitHydrogensToSatisfyValency(mol);

		for (int i = 0 ; i < mol.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
	/**
	 *  A unit test for JUnit with Methylamine
	 */
	public void testPartialTotalChargeDescriptor_Methylamine() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.27,-0.99,0.0,0.0,0.0,0.36};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom nitrogen = builder.newAtom(Elements.NITROGEN);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(nitrogen);
		mol.addBond(builder.newBond(carbon, nitrogen, CDKConstants.BONDORDER_SINGLE));
		haad.addExplicitHydrogensToSatisfyValency(mol);

		for (int i = 0 ; i < 6 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
	/**
	 *  A unit test for JUnit with ethoxyethane
	 */
	public void testPartialTotalChargeDescriptor_Ethoxyethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.28,-0.56,0.28,};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
//		IMolecule mol = sp.parseSmiles("COC");
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		IAtom carbon2 = builder.newAtom(Elements.CARBON);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(oxygen);
		mol.addAtom(carbon2); 
		mol.addBond(builder.newBond(carbon, oxygen, CDKConstants.BONDORDER_SINGLE));
		mol.addBond(builder.newBond(carbon2, oxygen, CDKConstants.BONDORDER_SINGLE));
		haad.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 3 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
	/**
	 *  A unit test for JUnit with Methanethiol
	 */
	public void testPartialTotalChargeDescriptor_Methanethiol() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.23,-0.41,0.0,};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom sulfur = builder.newAtom(Elements.SULFUR);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(sulfur);
		mol.addBond(builder.newBond(carbon, sulfur, CDKConstants.BONDORDER_SINGLE));
		haad.addExplicitHydrogensToSatisfyValency(mol);

		for (int i = 0 ; i < 3 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
	/**
	 *  A unit test for JUnit with Chloromethane
	 */
	public void testPartialTotalChargeDescriptor_Chloromethane() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.29,-0.29,0.0};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
//		IMolecule mol = sp.parseSmiles("CCl");
		IMolecule mol = builder.newMolecule();
		IAtom carbon = builder.newAtom(Elements.CARBON);
		IAtom chlorine = builder.newAtom(Elements.CHLORINE);
		// making sure the order matches the test results
		mol.addAtom(carbon); 
		mol.addAtom(chlorine);
		mol.addBond(builder.newBond(carbon, chlorine, CDKConstants.BONDORDER_SINGLE));
		haad.addExplicitHydrogensToSatisfyValency(mol);

		for (int i = 0 ; i < 3 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
	/**
	 *  A unit test for JUnit with Benzene
	 */
	public void testPartialTotalChargeDescriptor_Benzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.15,0.15,-0.15,0.15,-0.15,0.15,-0.15,0.15,-0.15, 0.15,-0.15, 0.15};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
//		IMolecule mol = sp.parseSmiles("c1ccccc1");
		IMolecule mol = builder.newMolecule();
		for (int i=0; i<6; i++) {
			IAtom carbon = builder.newAtom(Elements.CARBON);
			carbon.setFlag(CDKConstants.ISAROMATIC, true);
			// making sure the order matches the test results
			mol.addAtom(carbon);			
		}
		IBond ringBond = builder.newBond(mol.getAtom(0), mol.getAtom(1), CDKConstants.BONDORDER_DOUBLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(1), mol.getAtom(2), CDKConstants.BONDORDER_SINGLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(2), mol.getAtom(3), CDKConstants.BONDORDER_DOUBLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(3), mol.getAtom(4), CDKConstants.BONDORDER_SINGLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(4), mol.getAtom(5), CDKConstants.BONDORDER_DOUBLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		ringBond = builder.newBond(mol.getAtom(5), mol.getAtom(0), CDKConstants.BONDORDER_SINGLE);
		ringBond.setFlag(CDKConstants.ISAROMATIC, true);
		mol.addBond(ringBond);
		haad.addExplicitHydrogensToSatisfyValency(mol);
		
		for (int i = 0 ; i < 12 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
	/**
	 *  A unit test for JUnit with Water
	 */
	public void testPartialTotalChargeDescriptor_Water() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.86,0.43,0.43};/* from Merck Molecular Force Field. II. Thomas A. Halgren*/
		IAtomicDescriptor descriptor = new PartialTChargeMMFF94Descriptor();
        
		IMolecule mol = builder.newMolecule();
		IAtom oxygen = builder.newAtom(Elements.OXYGEN);
		// making sure the order matches the test results
		mol.addAtom(oxygen);
		haad.addExplicitHydrogensToSatisfyValency(mol);

		for (int i = 0 ; i < 3 ; i++){
			double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
			assertEquals(testResult[i],result,METHOD_ERROR);
		}
	}
}

