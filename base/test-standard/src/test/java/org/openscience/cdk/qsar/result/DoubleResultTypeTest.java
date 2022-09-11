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
package org.openscience.cdk.qsar.result;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-standard
 */
class DoubleResultTypeTest extends CDKTestCase {

    DoubleResultTypeTest() {
        super();
    }

    @Test
    void testDoubleResultType() {
        IDescriptorResult type = new DoubleResultType();
        Assertions.assertNotNull(type);
    }

    @Test
    void testToString() {
        Assertions.assertEquals("DoubleResultType", new DoubleResultType().toString());
    }

    @Test
    void testLength() {
        Assertions.assertEquals(1, new DoubleResultType().length());
    }

}
