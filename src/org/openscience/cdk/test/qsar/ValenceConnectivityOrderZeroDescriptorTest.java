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

import org.openscience.cdk.qsar.*;
import org.openscience.cdk.qsar.result.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import java.util.ArrayList;
import java.io.*;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */

 public class ValenceConnectivityOrderZeroDescriptorTest extends TestCase {
	
	public  ValenceConnectivityOrderZeroDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(ValenceConnectivityOrderZeroDescriptorTest.class);
	}
    
	public void testValenceConnectivityOrderZeroDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={3.0625687,2.2071068};
		Descriptor descriptor = new ValenceConnectivityOrderZeroDescriptor();
		SmilesParser sp = new SmilesParser();
		AtomContainer mol = sp.parseSmiles("O=C(O)CC");
		DoubleArrayResult retval = (DoubleArrayResult)descriptor.calculate(mol);
		// position 0 =  chi0v
		// positions 1 = chi0v_C
		// THIS IS OK: assertEquals(testResult[0], ((Double)retval.get(0)).doubleValue(), 0.0001);
		assertEquals(testResult[1], retval.get(1), 0.0001);
	}
}

