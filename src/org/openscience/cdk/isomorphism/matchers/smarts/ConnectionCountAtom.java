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
package org.openscience.cdk.isomorphism.matchers.smarts;

import org.openscience.cdk.interfaces.IAtom;

/**
 * This matcher checks the total valency of the Atom.
 * This cannot be matched with a unpreprocessed Atom!
 *
 * @cdk.module extra
 */
public class ConnectionCountAtom extends SMARTSAtom {
    
    private static final long serialVersionUID = 8787570498467055257L;
    
    private int count;
    
    public ConnectionCountAtom(int count) {
        this.count = count;
    }
    public ConnectionCountAtom(){
        this.count = Default;
    }
    public int getOperator(){
        if(ID!=null && this.count==Default)
            return 1;
        else if(ID!=null && this.count!=Default)
            return 2;
        else if(this.count==Default)
            return 3;
        else if(this.count!=Default)
            return 4;
        return 5;
    }
    public int getCC(IAtom atom){
        return ((Integer)atom.getProperty("org.openscience." +
                "cdk.Atom.connectionCount")).intValue();
    }
    
    public boolean matches(IAtom atom) {
        switch(getOperator()){
            case 1:return defaultOperatorCheck(atom);
            case 2:return nonDefaultOperatorCheck(atom);
            case 3:return defaultCheck(atom);
            case 4:return nonDefaultCheck(atom);
            default:return false;
        }
    };
    
    private boolean defaultCheck(IAtom atom){
        if(getCC(atom)!=0)return true;
        return false;
    }
    private boolean nonDefaultCheck(IAtom atom){
        if(getCC(atom)!=0 && getCC(atom)==this.count) return true;
        return false;
    }
    private boolean defaultOperatorCheck(IAtom atom){
        if(getCC(atom)==0)return true;
        return false;
    }
    private boolean nonDefaultOperatorCheck(IAtom atom){
        if(getCC(atom)!=0 && getCC(atom)!=this.count) return false;
        return false;
    }

    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("ConnectionCountAtom(");
        s.append(this.hashCode() + ", ");
		s.append("CC:" + count);
		s.append(")");
		return s.toString();
    }
}

