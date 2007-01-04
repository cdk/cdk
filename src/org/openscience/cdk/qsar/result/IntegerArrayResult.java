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
public class IntegerArrayResult implements IDescriptorResult {

    private List array;

    public IntegerArrayResult() {
        this.array = new ArrayList();
    }

    public IntegerArrayResult(int size) {
        this.array = new ArrayList(size);
    }

    public void add(int value) {
        array.add(new Integer(value));
    }

    /**
     * The first int is at index = 0;
     */
    public int get(int index) {
        return ((Integer)this.array.get(index)).intValue();
    }

    public int size() {
        return this.array.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        for (Iterator iterator = array.iterator(); iterator.hasNext();) {
            Integer integer = (Integer) iterator.next();
            buf.append(integer.intValue());
            if (iterator.hasNext()) buf.append(",");
        }
        return buf.toString();
    }

}

