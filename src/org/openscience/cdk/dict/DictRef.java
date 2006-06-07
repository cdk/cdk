/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.dict;

/**
 * Object that can be used as key in IChemObject.setProperty(key, value) to
 * denote that this property is a dictionary reference for this IChemObject.
 *
 * @author      Egon Willighagen
 * @cdk.created 2003-08-24
 * @cdk.module  standard
 */
public class DictRef implements java.io.Serializable, Cloneable  {

    private static final long serialVersionUID = -3691244168587563625L;
    
    String type;
    String dictRef;
    
    public DictRef(String type, String dictRef) {
        this.type = type;
        this.dictRef = dictRef;
    }
    
    public String getDictRef() {
        return dictRef;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return "DictRef{T=" + this.type + ", R=" + dictRef +"}";
    }
    
}
