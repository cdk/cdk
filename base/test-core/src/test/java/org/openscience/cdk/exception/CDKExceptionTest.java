/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.exception;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.CDKTestCase;

/**
 * Checks the functionality of the CDKException class.
 *
 * @cdk.module test-core
 *
 * @see org.openscience.cdk.exception.CDKException
 */
public class CDKExceptionTest extends CDKTestCase {

    @Test
    public void testCDKException_String() {
        final String EXPLANATION = "No, CDK cannot compute the multidollar ligand you search for target X.";
        CDKException exception = new CDKException(EXPLANATION);
        Assert.assertNotNull(exception);
        Assert.assertEquals(EXPLANATION, exception.getMessage());
    }

    @Test
    public void testCDKException_String_Throwable() {
        final String EXPLANATION = "No, CDK cannot compute the multidollar ligand you search for target X.";
        try {
            int[] array = new int[0];
            int dummy = array[50];
            dummy = dummy + 1;
            Assert.fail("Should not have reached this place. The test *requires* the error to occur!");
        } catch (Exception exception) {
            CDKException cdkException = new CDKException(EXPLANATION, exception);
            Assert.assertNotNull(cdkException);
            Assert.assertEquals(EXPLANATION, cdkException.getMessage());
        }
    }
}
