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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.qsar.result.*;
import java.io.*;

/**
 * @cdk.module test
 */
public class EffectivePolarizabilityDescriptorTest extends TestCase {
	
	public  EffectivePolarizabilityDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(EffectivePolarizabilityDescriptorTest.class);
	}
	
	public void testEffectivePolarizabilityDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={6.13};
		Descriptor descriptor = new EffectivePolarizabilityDescriptor();
		Object[] params = {new Integer(1)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("NCCN(C)(C)"); 
		HydrogenAdder hAdder = new HydrogenAdder();
		try {
			hAdder.addExplicitHydrogensToSatisfyValency(mol);
		} catch (Exception ex1) {
			throw new CDKException("Problems with HydrogenAdder due to " + ex1.toString());
		}
		double retval = ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue();
		// position 0 =  heavy atom
		// positions 1... = protons
		assertEquals(testResult[0], retval, 0.01);
	}
}

