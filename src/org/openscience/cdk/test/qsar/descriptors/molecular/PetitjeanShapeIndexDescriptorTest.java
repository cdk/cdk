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
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.descriptors.molecular.PetitjeanShapeIndexDescriptor;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test-extra
 */

public class PetitjeanShapeIndexDescriptorTest extends CDKTestCase {

    public PetitjeanShapeIndexDescriptorTest() {
    }

    public static Test suite() {
        return new TestSuite(PetitjeanShapeIndexDescriptorTest.class);
    }

    public void testPetitjeanShapeIndexDescriptor() throws ClassNotFoundException, CDKException, Exception {
        IMolecularDescriptor descriptor = new PetitjeanShapeIndexDescriptor();

        // napthalene .667 .802
        // nbutane .5 .536
        /*
        SmilesParser sp = new SmilesParser();
        AtomContainer mol = sp.parseSmiles("O=C(O)CC");

        DescriptorValue result = descriptor.calculate(mol);
        DoubleArrayResult dar = (DoubleArrayResult) result.getValue();

        assertEquals(0.33333334, dar.get(0), 0.0001) ;
        */
    }
}

