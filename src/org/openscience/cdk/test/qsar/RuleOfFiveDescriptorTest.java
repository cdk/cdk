/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */
 
public class RuleOfFiveDescriptorTest extends TestCase {
	
	public  RuleOfFiveDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(RuleOfFiveDescriptorTest.class);
	}
    
	public void testRuleOfFiveDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Descriptor descriptor = new RuleOfFiveDescriptor();
        Object[] params = { new Boolean(true) };
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        Molecule mol = sp.parseSmiles("CCCC(OCC)OCC(c1cccc2ccccc12)C4CCC(CCCO)C(CC3CNCNC3)C4"); // 
	HydrogenAdder hAdder = new HydrogenAdder();
	hAdder.addExplicitHydrogensToSatisfyValency(mol);
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol)).intValue());
	}
}

