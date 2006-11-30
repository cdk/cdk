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
package org.openscience.cdk.test.qsar.descriptors.molecular;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.ValenceCarbonConnectivityOrderOneDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class ValenceCarbonConnectivityOrderOneDescriptorTest extends CDKTestCase {

    public ValenceCarbonConnectivityOrderOneDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(ValenceCarbonConnectivityOrderOneDescriptorTest.class);
    }

    public void testValenceCarbonConnectivityOrderOneDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double [] testResult = {1.4883912, 1.0606601};
        IMolecularDescriptor descriptor = new ValenceCarbonConnectivityOrderOneDescriptor();
        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("O=C(O)CC");
        DoubleResult retval = (DoubleResult) descriptor.calculate(mol).getValue();
        // chi1v_C
        assertEquals(testResult[1], retval.doubleValue(), 0.0001);
    }
    
    /**
     * @cdk.bug 1298108
     */
    public void testBug1298108() throws CDKException {
    	SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer mol = sp.parseSmiles("[Cu]12(-O-C(-C(-O-2)=O)=O)(-O-C(-C(-O-1)=O)=O)(-O)-O");
        IMolecularDescriptor descriptor = new ValenceCarbonConnectivityOrderOneDescriptor();
        DoubleResult retval = (DoubleResult) descriptor.calculate(mol).getValue();
        assertNotNull(retval);
        
        mol = sp.parseSmiles("[Mn]12(-O-C(-C(-O-2)=O)=O)(-O-C(-C(-O-1)=O)=O)(-O)-O");
        retval = (DoubleResult) descriptor.calculate(mol).getValue();
        assertNotNull(retval);
        
        mol = sp.parseSmiles("[Co]12(-O-C(-C(-O-2)=O)=O)(-O-C(-C(-O-1)=O)=O)(-O)-O");
        retval = (DoubleResult) descriptor.calculate(mol).getValue();
        assertNotNull(retval);
    }
}

