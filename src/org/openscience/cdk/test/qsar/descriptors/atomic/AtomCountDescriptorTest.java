/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2006  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.atomic.AtomCountDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs a test for the AtomCountDescriptor.
 *
 * @cdk.module test-extra
 */
 
public class AtomCountDescriptorTest extends CDKTestCase {
	
	public  AtomCountDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(AtomCountDescriptorTest.class);
	}
    
	public void testCarbonCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        IMolecularDescriptor descriptor  = new AtomCountDescriptor();
        Object[] params = {"C"};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        AtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
	}
}

