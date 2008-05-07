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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class PartialTChargePEOEDescriptorTest extends AtomicDescriptorTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    LonePairElectronChecker lpcheck = new LonePairElectronChecker();
	
    /**
	 *  Constructor for the PartialTChargePEOEDescriptorTest object
	 *
	 */
	public  PartialTChargePEOEDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(PartialTChargePEOEDescriptorTest.class);
	}
    
    public void setUp() throws Exception {
    	setDescriptor(PartialTChargePEOEDescriptor.class);
    }
	
	/**
	 *  A unit test for JUnit with Ethyl Fluoride
	 *  
	 *  @cdk.inchi InChI=1/CH3F/c1-2/h1H3
	 */
	public void testPartialTChargeDescriptor_Methyl_Fluoride() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.2527,0.0795,0.0577,0.0577,0.0577};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		 
		IMolecule molecule = builder.newMolecule();
        molecule.addAtom(builder.newAtom("F"));
        molecule.addAtom(builder.newAtom("C"));
        molecule.addBond(0, 1, IBond.Order.SINGLE);
        
		addExplicitHydrogens(molecule);
		lpcheck.saturate(molecule);
		
		addExplicitHydrogens(molecule);
		for (int i = 0 ; i < molecule.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(molecule.getAtom(i),molecule).getValue()).doubleValue();
			
			assertEquals(testResult[i],result, 0.01);
		}
	}
	/**
	 *  A unit test for JUnit with Fluoroethylene
	 */
	public void testPartialTChargeDescriptor_Fluoroethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1839,0.079,-0.1019,0.0942,0.0563,0.0563};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("F-C=C");

		addExplicitHydrogens(mol);
		lpcheck.saturate(mol);
		
		for (int i = 0 ; i < mol.getAtomCount() ; i++){
	        double result= ((DoubleResult)descriptor.calculate(mol.getAtom(i),mol).getValue()).doubleValue();
	        
	        assertEquals(testResult[i],result, 0.04);
		}
	}
	/**
	 *  A unit test for JUnit with Formic Acid
	 *  
	 *  @cdk.inchi  InChI=1/CH2O2/c2-1-3/h1H,(H,2,3)/f/h2H
	 */
	public void testPartialTChargeDescriptor_FormicAcid() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.2672,-0.3877,-0.2365,0.1367,0.2203};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(0, 2, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule);
		lpcheck.saturate(molecule);
		
		for (int i = 0 ; i < molecule.getAtomCount() ; i++){
			double result= ((DoubleResult)descriptor.calculate(molecule.getAtom(i),molecule).getValue()).doubleValue();
			
			assertEquals(testResult[i],result, 0.05);
		}
	}
	/**
	 *  A unit test for JUnit with Fluorobenzene
	 *  
	 *  @cdk.inchi InChI=1/C6H5F/c7-6-4-2-1-3-5-6/h1-5H
	 */
	public void testPartialTChargeDescriptor_Fluorobenzene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1785,0.1227,-0.0373,-0.0598,-0.0683};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		
		IMolecule molecule = builder.newMolecule();
		 molecule.addAtom(builder.newAtom("F"));
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(0, 1, IBond.Order.SINGLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(1, 2, IBond.Order.DOUBLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(2, 3, IBond.Order.SINGLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(3, 4, IBond.Order.DOUBLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(4, 5, IBond.Order.SINGLE);
		 molecule.addAtom(builder.newAtom("C"));
		 molecule.addBond(5, 6, IBond.Order.DOUBLE);
		 molecule.addBond(6, 1, IBond.Order.SINGLE);

		addExplicitHydrogens(molecule);
		lpcheck.saturate(molecule);
		
		for (int i = 0 ; i < 5 ; i++){
			double result= ((DoubleResult)descriptor.calculate(molecule.getAtom(i),molecule).getValue()).doubleValue();

			assertEquals(testResult[i],result, 0.012);
		}
	}
	/**
	 *  A unit test for JUnit with Methoxyethylene
	 *  
	 *  @cdk.inchi InChI=1/C3H6O/c1-3-4-2/h3H,1H2,2H3
	 */
	public void testPartialTChargeDescriptor_Methoxyethylene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1211,0.0314,-0.3121,0.0429,0.056,0.056,0.0885,0.056,0.056,0.056};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule);
		lpcheck.saturate(molecule);
				
		for (int i = 0 ; i < molecule.getAtomCount(); i++){
	        double result= ((DoubleResult)descriptor.calculate(molecule.getAtom(i),molecule).getValue()).doubleValue();
	        
	        assertEquals(testResult[i],result, 0.05);
		}
	}
	/**
	 *  A unit test for JUnit with 1-Methoxybutadiene
	 *  
	 *  @cdk.inchi InChI=1/C5H8O/c1-3-4-5-6-2/h3-5H,1H2,2H3
	 */
	public void testPartialTChargeDescriptor_1_Methoxybutadiene() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={-0.1331,-0.0678,-0.0803,0.0385,-0.2822,0.0429,0.0541,0.0541,0.0619,0.0644,0.0891,0.0528,0.0528,0.0528,0.0528};/* from Petra online: http://www2.chemie.uni-erlangen.de/services/petra/smiles.phtml*/
		IAtomicDescriptor descriptor = new PartialTChargePEOEDescriptor();
		
		IMolecule molecule = builder.newMolecule();
		molecule.addAtom(builder.newAtom("C"));
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(0, 1, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(1, 2, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(2, 3, IBond.Order.DOUBLE);
		molecule.addAtom(builder.newAtom("O"));
		molecule.addBond(3, 4, IBond.Order.SINGLE);
		molecule.addAtom(builder.newAtom("C"));
		molecule.addBond(4, 5, IBond.Order.SINGLE);
		
		addExplicitHydrogens(molecule);
		lpcheck.saturate(molecule);
		
		for (int i = 0 ; i < molecule.getAtomCount(); i++){
	        double result= ((DoubleResult)descriptor.calculate(molecule.getAtom(i),molecule).getValue()).doubleValue();
	        
	        assertEquals(testResult[i],result, 0.3);
		}
	}
}