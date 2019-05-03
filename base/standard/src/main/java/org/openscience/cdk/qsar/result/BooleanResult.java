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
 */
package org.openscience.cdk.qsar.result;


/**
 * Object that provides access to the calculated descriptor value.
 *
 * @cdk.module standard
 * @cdk.githash
 */
public class BooleanResult extends BooleanResultType {

    private static final long serialVersionUID = 3746767816253035856L;

    private boolean           value;

    public BooleanResult(boolean value) {
        this.value = value;
    }

    public boolean booleanValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
