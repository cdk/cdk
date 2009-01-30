/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.graph.invariant;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the MorganNumberTools.
 *
 * @cdk.module test-standard
 */
public class MorganNumbersToolsTest extends CDKTestCase
{
	public MorganNumbersToolsTest() {
		super();
	}


    @Test
    public void testGetMorganNumbers_IAtomContainer()
	{
		// This is an array with the expected Morgan Numbers for a-pinene
		long[] reference = {
				28776,
				17899,
				23549,
				34598,
				31846,
				36393,
				9847,
				45904,
				15669,
				15669
		};

		Molecule mol = MoleculeFactory.makeAlphaPinene();
		long[] morganNumbers = MorganNumbersTools.getMorganNumbers((AtomContainer)mol);
		Assert.assertEquals(reference.length, morganNumbers.length);
		for (int f = 0; f < morganNumbers.length; f ++)
		{
			//logger.debug(morganNumbers[f]);
			Assert.assertEquals(reference[f], morganNumbers[f]);
		}
	}

    @Test
    public void testPhenylamine() {
		// This is an array with the expected Morgan Numbers for a-pinene
		String[] reference = {"C-457","C-428","C-325","C-354","C-325","C-428","N-251"};

		Molecule mol = MoleculeFactory.makePhenylAmine();
		String[] morganNumbers = MorganNumbersTools.getMorganNumbersWithElementSymbol((AtomContainer)mol);
		Assert.assertEquals(reference.length, morganNumbers.length);
		for (int f = 0; f < morganNumbers.length; f ++) {
			//logger.debug(morganNumbers[f]);
			Assert.assertEquals(reference[f], morganNumbers[f]);
		}
	}
	
}
