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
public class TotalValencyAtom extends SMARTSAtom {

    private static final long serialVersionUID = -8067867220731999668L;
    
    private int valency;
    
    public TotalValencyAtom(int valency) {
        this.valency = valency;
    }
    public TotalValencyAtom(){
        this.valency = Default;
    }
     
    public int getOperator(){
        if(ID!=null && this.valency==Default)
            return 1;
        else if(ID!=null && this.valency!=Default)
            return 2;
        else if(this.valency==Default)
            return 3;
        else if(this.valency!=Default)
            return 4;
        return 5;
    }
    private int getVV(IAtom atom){
       return ((Integer)atom.getProperty("org.openscience." +
               "cdk.Atom.totalValency")).intValue();
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
        if(getVV(atom)!=0)return true;
        return false;
    }
    private boolean nonDefaultCheck(IAtom atom){
        if(getVV(atom)!=0 && getVV(atom)==this.valency) return true;
        return false;
    }
    private boolean defaultOperatorCheck(IAtom atom){
        if(getVV(atom)==0)return true;
        return false;
    }
    private boolean nonDefaultOperatorCheck(IAtom atom){
        if(getVV(atom)!=0 && getVV(atom)!=this.valency) return false;
        return false;
    }

    public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("TotalValency(");
        s.append(this.hashCode() + ", ");
		s.append("V:" + valency);
        s.append(")");
		return s.toString();
    }
}

