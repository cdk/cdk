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
import org.openscience.cdk.qsar.ProtonTotalPartialChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * @cdk.module test
 */
public class ProtonTotalPartialChargeDescriptorTest extends CDKTestCase {
	
	public  ProtonTotalPartialChargeDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(ProtonTotalPartialChargeDescriptorTest.class);
	}
	
	public void testProtonTotalPartialChargeDescriptorTest() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.07915,0.05783,0.05783,0.05783};
		Descriptor descriptor = new ProtonTotalPartialChargeDescriptor();
		
		Object[] params = {new Integer(0)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CF"); 
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		DoubleArrayResult retval = (DoubleArrayResult)descriptor.calculate(mol).getValue();
		assertEquals(0.05783, retval.get(2), 0.00001);
	}
}

