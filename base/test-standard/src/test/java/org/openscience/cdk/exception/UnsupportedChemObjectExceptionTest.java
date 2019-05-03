/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.exception;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.exception.UnsupportedChemObjectException;
import org.openscience.cdk.CDKTestCase;

/**
 * @cdk.module test-standard
 */
public class UnsupportedChemObjectExceptionTest extends CDKTestCase {

    public UnsupportedChemObjectExceptionTest() {
        super();
    }

    @Test
    public void testUnsupportedChemObjectException_String() {
        final String EXPLANATION = "No, CDK cannot compute the multidollar ligand you search for target X.";
        UnsupportedChemObjectException exception = new UnsupportedChemObjectException(EXPLANATION);
        Assert.assertNotNull(exception);
        Assert.assertEquals(EXPLANATION, exception.getMessage());
    }
}
