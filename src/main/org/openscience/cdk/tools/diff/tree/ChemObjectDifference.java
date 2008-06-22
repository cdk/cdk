/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff.tree;

import java.util.Iterator;

import org.openscience.cdk.interfaces.IChemObject;

/**
 * {@link IDifference} between two {@link IChemObject}s.
 * 
 * @author     egonw
 * @cdk.module diff
 */
public class ChemObjectDifference extends AbstractDifferenceList implements IDifferenceList {

    private String name;
    
    public ChemObjectDifference(String name) {
        this.name = name;
    }
    
    public String toString() {
        if (differences.size() == 0) return "";
        
        StringBuffer diffBuffer = new StringBuffer();
        diffBuffer.append(this.name).append("{");
        Iterator<IDifference> children = getChildren();
        while (children.hasNext()) {
            diffBuffer.append(children.next().toString());
            if (children.hasNext()) {
                diffBuffer.append(", ");
            }
        }
        diffBuffer.append("}");
        
        return diffBuffer.toString();
    }

}
