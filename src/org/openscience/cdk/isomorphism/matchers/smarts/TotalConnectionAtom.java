
/*
 *  $RCSfile$
 *  $Author: Sushil Ronghe $
 *  $Date: 2007-04-12  $
 *  $Revision: 6631 $
 *
 *  Copyright (C) 2002-2006  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */

        
 package org.openscience.cdk.isomorphism.matchers.smarts;
 import org.openscience.cdk.interfaces.IAtom;
 import org.openscience.cdk.CDKConstants;

/**
 *
 * @author niper
 */
public class TotalConnectionAtom extends SMARTSAtom{
    private int Count;
    /** Creates a new instance of TotalConnectionAtom */
    public TotalConnectionAtom() {
    }
    public TotalConnectionAtom(int m_XX){
        Count = m_XX;
    }
    public int getOperator(){
         if(ID!=null && this.Count==Default)
            return 1;
        else if(ID!=null && this.Count!=Default)
            return 2;
        else if(this.Count==Default)
            return 3;
        else if(this.Count!=Default)
            return 4;
        return 5;
    }
    public int getXX(IAtom atom){
        if(atom.getProperty(CDKConstants.TOTAL_CONNECTIONS)!=null)
          return ((Integer)atom.getProperty(CDKConstants.TOTAL_CONNECTIONS)).intValue();
        else
           return 0;
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
        if(getXX(atom)!=0)return true;
        return false;
    }
    private boolean nonDefaultCheck(IAtom atom){
        if(getXX(atom)!=0 && getXX(atom)==this.Count) return true;
        return false;
    }
    private boolean defaultOperatorCheck(IAtom atom){
        if(getXX(atom)==0) return true;
        return false;
    }
    private boolean nonDefaultOperatorCheck(IAtom atom){
        if(getXX(atom)!=0 && getXX(atom)!=this.Count) return false;
        return false;
    }
}
