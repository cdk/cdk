/* Copyright (C) 2017  Gilleain Torrance <gilleain.torrance@gmail.com>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.group;

import java.util.Arrays;

/**
 * @author maclean
 * @cdk.module group
 */
class IntegerListInvariant implements Invariant {
    
    private int[] values;
    
    public IntegerListInvariant(int[] values) {
        this.values = values;
    }

    @Override
    public int compareTo(Invariant o) {
        int[] other = ((IntegerListInvariant)o).values;
        for (int index = 0; index < values.length; index++) {
            if (values[index] > other[index]) {
                return -1;
            } else if (values[index] < other[index]) {
                return 1;
            } else {
                continue;
            }
        }
        return 0;
    }
    
    public int hashCode() {
        return Arrays.hashCode(values);
    }
    
    public boolean equals(Object other) {
        return other instanceof IntegerListInvariant
                && Arrays.equals(values, ((IntegerListInvariant)other).values);
    }
    
    public String toString() {
        return Arrays.toString(values);
    }
    
}
