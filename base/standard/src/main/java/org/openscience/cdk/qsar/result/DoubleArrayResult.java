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


import java.util.ArrayList;
import java.util.List;

/**
 * @cdk.module standard
 * @cdk.githash
 */
public class DoubleArrayResult extends DoubleArrayResultType {

    private List<Double>      array;
    private static final long serialVersionUID = 2345607580017306612L;

    public DoubleArrayResult() {
        super(0);
        this.array = new ArrayList<Double>();
    }

    public DoubleArrayResult(int size) {
        super(size);
        this.array = new ArrayList<Double>(size);
    }

    public void add(double value) {
        array.add(value);
    }

    /**
     * The first double is at index = 0;
     */
    public double get(int index) {
        if (index >= this.array.size()) {
            return 0.0;
        }
        return this.array.get(index);
    }

    @Override
    public int length() {
        return Math.max(super.length(), this.array.size());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length(); i++) {
            buf.append(get(i));
            if (i + 1 < length()) buf.append(',');
        }
        return buf.toString();
    }
}
