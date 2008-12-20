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
 */
package org.openscience.cdk.interfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * Checks the functionality of {@link IMolecule} implementations.
 *
 * @cdk.module test-interfaces
 */
public class IMoleculeTest extends IAtomContainerTest {

	@Test public void testClone() throws Exception {
        IMolecule molecule = (IMolecule)newChemObject();
        Object clone = molecule.clone();
        Assert.assertTrue(clone instanceof IMolecule);
	Assert.assertNotSame(molecule, clone);
    }    

    /** Test for RFC #9 */
    @Test public void testToString() {
        IMolecule m = (IMolecule)newChemObject();
        String description = m.toString();
        for (int i=0; i< description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }
}
