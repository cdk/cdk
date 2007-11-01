/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class TPSADescriptorTest extends CDKTestCase {

    private SmilesParser sp;
    private IMolecularDescriptor descriptor;

    public TPSADescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(TPSADescriptorTest.class);
    }

    protected void setUp() throws CDKException {
        sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        descriptor = new TPSADescriptor();
        Object[] params = {true};
        descriptor.setParameters(params);
    }


    public void testTPSA1() throws Exception {
        IMolecule mol = sp.parseSmiles("O=C(O)CC");
        addExplicitHydrogens(mol);
        assertEquals(37.29, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    public void testTPSA2() throws Exception {
        IMolecule mol = sp.parseSmiles("C=NC(CC#N)N(C)C");
        addExplicitHydrogens(mol);
        assertEquals(39.39, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    public void testTPSA3() throws Exception {
        IMolecule mol = sp.parseSmiles("CCCN(=O)=O");
        addExplicitHydrogens(mol);
        assertEquals(45.82, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    public void testTPSA4() throws Exception {
        IMolecule mol = sp.parseSmiles("C#N=CC(CNC)N1CC1");
        addExplicitHydrogens(mol);
        assertEquals(28.632, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }


    public void testTPSA5() throws Exception {
        IMolecule mol = sp.parseSmiles("c1ccncc1");
        addExplicitHydrogens(mol);
        assertEquals(12.892, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.01);
    }

    public void testTPSA6() throws java.lang.Exception {
        IMolecule mol = sp.parseSmiles("[H][N+]([H])(C)C");//at:  16
        addExplicitHydrogens(mol);
        assertEquals(16.61, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testTPSA7() throws java.lang.Exception {
        IMolecule mol = sp.parseSmiles("C(I)I");//at:  16
        addExplicitHydrogens(mol);
        assertEquals(0.0, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }

    public void testTPSA8() throws java.lang.Exception {
        IMolecule mol = sp.parseSmiles("C(O)O");//at:  16
        addExplicitHydrogens(mol);
        assertEquals(40.45, ((DoubleResult) descriptor.calculate(mol).getValue()).doubleValue(), 0.1); //at:  16
    }


}

