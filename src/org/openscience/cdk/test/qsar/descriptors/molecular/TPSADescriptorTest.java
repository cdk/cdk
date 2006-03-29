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
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */

public class TPSADescriptorTest extends CDKTestCase {

    public TPSADescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(TPSADescriptorTest.class);
    }

    public void testTPSADescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {

        // each test is for one or more atom types:

        IDescriptor descriptor = new TPSADescriptor();
        Object[] params = {new Boolean(true)};
        descriptor.setParameters(params);
        SmilesParser sp = new SmilesParser();
        //Molecule mol = sp.parseSmiles("O=C(O)CC"); at: 29, 31
        //Molecule mol = sp.parseSmiles("C=NC(CC#N)N(C)C"); // at 1,2,3
        //Molecule mol = sp.parseSmiles("CCCN(=O)=O"); // at 4
        //Molecule mol = sp.parseSmiles("C#N=CC(CNC)N1CC1"); // at 5,6,7
        //Molecule mol = sp.parseSmiles("c1ccncc1");//at:  19
        Molecule mol = sp.parseSmiles("[H][N+]([H])(C)C");//at:  16
        HydrogenAdder hAdder = new HydrogenAdder();
        hAdder.addExplicitHydrogensToSatisfyValency(mol);
        // each test id done for one or more atom types:
        // assertEquals(37.299999, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); at:  29, 31
        // assertEquals(39.394, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  1,2,3
        // assertEquals(45.824, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  4
        //assertEquals(28.632, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  5,6,7
        // assertEquals(12.892, ((DoubleResult)descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  19
        assertEquals(16.61, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }
}

