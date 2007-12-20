/* $Revision: 9643 $ $Author: egonw $ $Date: 2007-12-20 08:56:18 +0100 (Thu, 20 Dec 2007) $
 * 
 * Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
import org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor;

/**
 * TestSuite for the VAdjMaDescriptor.
 *
 * @cdk.module test-qsarmolecular
 */
public class VAdjMaDescriptorTest extends MolecularDescriptorTest {

    public static Test suite() {
        return new TestSuite(VAdjMaDescriptorTest.class);
    }
    
    protected void setUp() throws CDKException {
        descriptor = new TPSADescriptor();
    }

    public void testCalculate_IAtomContainer() {
    	fail("Not tested");
    }
    
}

