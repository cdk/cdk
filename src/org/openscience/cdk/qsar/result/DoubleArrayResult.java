/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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
import java.util.Iterator;
import java.util.List;

/**
 * @cdk.module standard
 */
public class DoubleArrayResult implements IDescriptorResult {

    private List<Double> array;

    public DoubleArrayResult() {
        this.array = new ArrayList<Double>();
    }

    public DoubleArrayResult(int size) {
        this.array = new ArrayList<Double>(size);
    }

    public void add(double value) {
        array.add(new Double(value));
    }

    /**
     * The first double is at index = 0;
     */
    public double get(int index) {
        return ((Double) this.array.get(index)).doubleValue();
    }

    public int length() {
        return this.array.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator<Double> iterator = array.iterator(); iterator.hasNext();) {
            Double value = iterator.next();
            buf.append(value.doubleValue());
            if (iterator.hasNext()) buf.append(",");
        }
        return buf.toString();
    }
}

