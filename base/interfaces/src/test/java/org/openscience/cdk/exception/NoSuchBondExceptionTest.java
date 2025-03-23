/* Copyright (C) 2025  Egon Willighagen <egon.willighagen@maastrichtuniversity.nl>
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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Checks the functionality of the NoSuchBondException class.
 *
 *
 * @see org.openscience.cdk.exception.NoSuchBondException
 */
class NoSuchBondExceptionTest {

    @Test
    void testNoSuchAtomException_String() {
        final String EXPLANATION = "Buckybull is not an element!";
        NoSuchBondException exception = new NoSuchBondException(EXPLANATION);
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(EXPLANATION, exception.getMessage());
    }
}
