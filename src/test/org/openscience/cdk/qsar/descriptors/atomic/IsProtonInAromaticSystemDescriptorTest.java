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
package org.openscience.cdk.qsar.descriptors.atomic;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.qsar.IAtomicDescriptor;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.smiles.SmilesParser;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-qsaratomic
 */
public class IsProtonInAromaticSystemDescriptorTest extends AtomicDescriptorTest {
	
	public  IsProtonInAromaticSystemDescriptorTest() {}
    
    @Before
    public void setUp() throws Exception {
    	setDescriptor(IsProtonInAromaticSystemDescriptor.class);
    }
	
	@Test
    public void testIsProtonInAromaticSystemDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		IAtomicDescriptor descriptor  = new IsProtonInAromaticSystemDescriptor();
		Object[] params = {new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
		IMolecule mol = sp.parseSmiles("Oc1cc(OC)c(cc1Br)Br"); 
		addExplicitHydrogens(mol);
		Assert.assertEquals(1, ((IntegerResult)descriptor.calculate(mol.getAtom(13),mol).getValue()).intValue());
	}
}

