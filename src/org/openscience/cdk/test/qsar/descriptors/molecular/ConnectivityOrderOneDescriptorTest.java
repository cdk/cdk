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
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.descriptors.molecular.ConnectivityOrderOneDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class ConnectivityOrderOneDescriptorTest extends CDKTestCase {

    public ConnectivityOrderOneDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(ConnectivityOrderOneDescriptorTest.class);
    }

    public void testConnectivityOrderOneDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
        double [] testResult = {1.414};
        IMolecularDescriptor descriptor = new ConnectivityOrderOneDescriptor();
        SmilesParser sp = new SmilesParser();
        AtomContainer mol = sp.parseSmiles("FCF");
        DoubleResult retval = (DoubleResult) descriptor.calculate(mol).getValue();
        // position 0 =  chi1
        assertEquals(testResult[0], retval.doubleValue(), 0.001);
    }
}

