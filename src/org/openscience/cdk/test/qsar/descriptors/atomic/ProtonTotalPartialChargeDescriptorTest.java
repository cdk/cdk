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
package org.openscience.cdk.test.qsar.descriptors.atomic;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.ProtonTotalPartialChargeDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-qsar
 */
public class ProtonTotalPartialChargeDescriptorTest extends CDKTestCase {
	
	public  ProtonTotalPartialChargeDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(ProtonTotalPartialChargeDescriptorTest.class);
	}
	
	public void testProtonTotalPartialChargeDescriptorTest() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={0.07915,0.05783,0.05783,0.05783};
		IAtomicDescriptor descriptor  = new ProtonTotalPartialChargeDescriptor();
		
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("CF"); 
		addExplicitHydrogens(mol);
		DoubleArrayResult retval = (DoubleArrayResult)descriptor.calculate(mol.getAtom(0),mol).getValue();
		for (int i = 0; i < testResult.length; ++i) {
			assertEquals(testResult[i], retval.get(i), 0.00001);
		}
	}
}

