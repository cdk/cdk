/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import java.util.Vector;

/**
 * Entry in a Dictionary.
 * 
 * @author       Egon Willighagen <egonw@users.sf.net>
 * @cdk.created  2003-08-23
 * @cdk.keyword  dictionary
 *
 * @see          Dictionary
 */
public class Entry {
    
    private String term;
    private String id;
    private Vector descriptorInfo;
    
    public Entry(String id, String term) {
        this.id = id.toLowerCase();
        this.term = term;
        this.descriptorInfo = new Vector();
    }
    
    public Entry() {
        this("", "");
        this.descriptorInfo = new Vector();
    }
    
    public void setTerm(String term) {
        this.term = term;
    }
    
    public String getTerm() {
        return this.term;
    }
    
    public void setID(String id) {
        this.id = id.toLowerCase();
    }
    
    public String getID() {
        return this.id;
    }

    public void setDescriptorMetadata(String metadata) {
        this.descriptorInfo.add( metadata );
    }
    public Vector getDescriptorMetadata() {
        return this.descriptorInfo;
    }
    
    public String toString() {
        return "Entry[" + getID() + "](" + getTerm() + ")";
    }
}
