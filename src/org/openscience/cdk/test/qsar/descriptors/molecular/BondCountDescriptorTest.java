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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.descriptors.molecular.BondCountDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsar
 */

public class BondCountDescriptorTest extends MolecularDescriptorTest {

	private static final SmilesParser sp = new SmilesParser(NoNotificationChemObjectBuilder.getInstance());
	
    public  BondCountDescriptorTest() {}

    public static Test suite() {
        return new TestSuite(BondCountDescriptorTest.class);
    }

    public void setUp() {
    	descriptor = new BondCountDescriptor();
    }

    public void testSingleBondCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {IBond.Order.SINGLE};
        descriptor.setParameters(params);
        
        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("C=C=C");
        assertEquals(0, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
    }

    public void testDoubleBondCount() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Object[] params = {IBond.Order.DOUBLE};
        descriptor.setParameters(params);

        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        assertEquals(0, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("C=C=C");
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
    }

    /**
     * The default setting should be to count *all* bonds.
     * 
     * @throws ClassNotFoundException
     * @throws CDKException
     * @throws java.lang.Exception
     * 
     * @cdk.bug 1651263
     */
    public void testDefaultSetting() throws ClassNotFoundException, CDKException, java.lang.Exception {
    	IMolecularDescriptor descriptor  = new BondCountDescriptor();
        IAtomContainer mol = sp.parseSmiles("CCO"); // ethanol
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("C=C=C");
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("CC=O");
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
        mol = sp.parseSmiles("CC#N");
        assertEquals(2, ((IntegerResult)descriptor.calculate(mol).getValue()).intValue());
    }
}

