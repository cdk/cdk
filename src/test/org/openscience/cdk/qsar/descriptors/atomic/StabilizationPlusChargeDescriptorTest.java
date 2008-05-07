/* $RCSfile$
 * $Author: miguelrojasch $
 * $Date: 2008-05-01 02:39:02 +0200 (Thu, 01 May 2008) $
 * $Revision: 10742 $
 * 
 *  Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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

import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IBond.Order;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LonePairElectronChecker;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */ 
public class StabilizationPlusChargeDescriptorTest extends AtomicDescriptorTest {

	private final static  IChemObjectBuilder builder = NoNotificationChemObjectBuilder.getInstance();
    LonePairElectronChecker lpcheck = new LonePairElectronChecker();
    
	public  StabilizationPlusChargeDescriptorTest() {
		descriptor = new StabilizationPlusChargeDescriptor();
	}
    
    public void setUp() throws Exception {
    	setDescriptor(StabilizationPlusChargeDescriptor.class);
    }
    
    public static Test suite() {
        return new TestSuite(StabilizationPlusChargeDescriptorTest.class);
    }
    
    /**
	 *  A unit test for JUnit
	 *  
     * @throws Exception
     */
    public void testStabilizationPlusChargeDescriptor()  throws Exception  {
		
		IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.getAtom(0).setFormalCharge(-1);
		mol.addAtom(builder.newAtom("C"));
		mol.getAtom(1).setFormalCharge(1);
		mol.addBond(0, 1, Order.SINGLE);
		mol.addAtom(builder.newAtom("F"));
		mol.addBond(1, 2, Order.SINGLE);
		
		addExplicitHydrogens(mol);
		lpcheck.saturate(mol);
    	
		DoubleResult result = ((DoubleResult)descriptor.calculate(mol.getAtom(1),mol).getValue());

		assertNotSame(0.0,result.doubleValue());
	}
    
    /**
     * 
     */
	public void testNotCharged()  throws Exception {
		
		IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.getAtom(0).setFormalCharge(-1);
		mol.addAtom(builder.newAtom("C"));
		mol.addBond(0, 1, Order.DOUBLE);
		mol.addAtom(builder.newAtom("F"));
		mol.addBond(1, 2, Order.SINGLE);
		
		addExplicitHydrogens(mol);
		lpcheck.saturate(mol);
    	
		DoubleResult result = ((DoubleResult)descriptor.calculate(mol.getAtom(0),mol).getValue());

		assertEquals(0.0,result.doubleValue(), 0.00001);
        
	}
	/**
	 *  A unit test for JUnit
	 *  
     * @throws Exception
     */
    public void testStabilizationPlusChargeDescriptor2()  throws Exception  {
		
		IMolecule mol = builder.newMolecule();
		mol.addAtom(builder.newAtom("C"));
		mol.getAtom(0).setFormalCharge(-1);
		mol.addAtom(builder.newAtom("C"));
		mol.getAtom(1).setFormalCharge(1);
		mol.addBond(0, 1, Order.SINGLE);
		mol.addAtom(builder.newAtom("F"));
		mol.addBond(1, 2, Order.SINGLE);
		
		addExplicitHydrogens(mol);
		lpcheck.saturate(mol);
    	
		DoubleResult result = ((DoubleResult)descriptor.calculate(mol.getAtom(1),mol).getValue());
        
		assertNotSame(0.0,result.doubleValue());
        
	}

	/**
	 *  A unit test for JUnit
	 *  
     * @throws Exception
     */
    public void testStabilizationComparative()  throws Exception  {
		
		IMolecule mol1 = builder.newMolecule();
		mol1.addAtom(builder.newAtom("C"));
		mol1.addAtom(builder.newAtom("C"));
		mol1.getAtom(1).setFormalCharge(1);
		mol1.addBond(0, 1, Order.SINGLE);
		mol1.addAtom(builder.newAtom("C"));
		mol1.addBond(1, 2, Order.SINGLE);
		mol1.addAtom(builder.newAtom("O"));
		mol1.addBond(1, 3, Order.SINGLE);
		addExplicitHydrogens(mol1);
		lpcheck.saturate(mol1);
    	
		DoubleResult result1 = ((DoubleResult)descriptor.calculate(mol1.getAtom(1),mol1).getValue());
        
		IMolecule mol2 = builder.newMolecule();
		mol2.addAtom(builder.newAtom("C"));
		mol2.addAtom(builder.newAtom("C"));
		mol2.getAtom(1).setFormalCharge(1);
		mol2.addBond(0, 1, Order.SINGLE);
		mol2.addAtom(builder.newAtom("O"));
		mol2.addBond(1, 2, Order.SINGLE);
		addExplicitHydrogens(mol2);
		lpcheck.saturate(mol2);
    	
		DoubleResult result2 = ((DoubleResult)descriptor.calculate(mol2.getAtom(1),mol2).getValue());
        
		IMolecule mol3 = builder.newMolecule();
		mol3.addAtom(builder.newAtom("C"));
		mol3.addAtom(builder.newAtom("C"));
		mol3.getAtom(1).setFormalCharge(1);
		mol3.addBond(0, 1, Order.SINGLE);
		mol3.addAtom(builder.newAtom("C"));
		mol3.addBond(1, 2, Order.SINGLE);
		addExplicitHydrogens(mol3);
		lpcheck.saturate(mol3);
    	
		DoubleResult result3 = ((DoubleResult)descriptor.calculate(mol3.getAtom(1),mol3).getValue());
        
		assertTrue(result3.doubleValue() < result2.doubleValue());
		assertTrue(result2.doubleValue() < result1.doubleValue());
	}
}

