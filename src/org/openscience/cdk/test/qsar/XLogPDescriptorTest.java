/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.qsar;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.Descriptor;
import org.openscience.cdk.qsar.XLogPDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */
 
public class XLogPDescriptorTest extends CDKTestCase {
	
	public XLogPDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(XLogPDescriptorTest.class);
	}
    
	public void testXLogPDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		
		// each test is for one or more atom types:
		// 1. Test for cumarine
		Descriptor descriptor = new XLogPDescriptor();
		Object[] params = {new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("COc1ccccc1C(C3=C(O)c2ccccc2CC3=O)c5c(O)c4ccccc4oc5=O"); // a cumarine
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		//System.out.println("Cumarine:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue());
		assertEquals(4.54, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
		
		
	}
	
	public void testApirinBug1296383() throws ClassNotFoundException, CDKException, java.lang.Exception {
//		2. Test for aspirin, a bug was filed 1296383
		Descriptor descriptor = new XLogPDescriptor();
		Object[] params = {new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CC(=O)OC1=CC=CC=C1C(=O)O"); // aspirin
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		//System.out.println("Aspirin:"+((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue());
		assertEquals(1.97, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
	}
	
}

