/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar.result;

import java.util.ArrayList;
import java.util.List;

/**
 * @cdk.module qsar
 */
public class DoubleArrayResult implements DescriptorResult {
    
    private List array;
    
    public DoubleArrayResult() {
        this.array = new ArrayList();
    }
    
    public DoubleArrayResult(int size) {
        this.array = new ArrayList(size);
    }
    
    public void add(double value) {
        array.add(new Double(value));
    }
    
    /**
     * The first double is at index = 0;
     */
    public double get(int index) {
        return ((Double)this.array.get(index)).doubleValue();
    }
    
    public int size() {
        return this.array.size();
    }

}

