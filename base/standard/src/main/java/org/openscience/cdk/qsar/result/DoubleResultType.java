/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.ne>
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

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;

/**
 * IDescriptorResult type for booleans.
 *
 * @cdk.module standard
 * @cdk.githash
 */
@TestClass("org.openscience.cdk.qsar.result.DoubleResultTypeTest")
public class DoubleResultType implements IDescriptorResult {

    private static final long serialVersionUID = -6641147506132424322L;

    @TestMethod("testToString")
    @Override
    public String toString() {
        return "DoubleResultType";
    }

    @TestMethod("testLength")
    @Override
    public int length() {
        return 1;
    }
}
