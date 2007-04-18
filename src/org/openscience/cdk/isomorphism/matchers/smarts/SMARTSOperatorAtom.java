
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
import org.openscience.cdk.isomorphism.matchers.*;
import org.openscience.cdk.interfaces.IAtom;

/**
 *    THis class is special for matching the complex operator expression.
 * 
 */
public class SMARTSOperatorAtom extends SMARTSAtom{
    private IQueryAtomContainer IQAT;
    private OperatorContainer OPC;
    private IQueryAtom Previousatom;
    private IQueryAtom Nextatom;
    private boolean value;
    /** Creates a new instance of SMARTSOperatorAtom */
    public SMARTSOperatorAtom(IQueryAtomContainer m_IQAT,OperatorContainer m_OPC){
        IQAT = m_IQAT;
        OPC = m_OPC;
    }
    public boolean matches(IAtom atom){
           int i=0;value = false;
        while(i<IQAT.getAtomCount()){
           Previousatom = (IQueryAtom)IQAT.getAtom(i);i++;Nextatom = (IQueryAtom)IQAT.getAtom(i);
            value = getOperationResult(atom);    
        }
        value = getOperationResult(atom);
        return value;
    }
    private boolean getOperationResult(IAtom atom){
        if(Previousatom!=null && Nextatom!=null && OPC.hasMoreElements()){
            switch(getOperatorValue(OPC.nextElement())){
                case 1:{if (Previousatom.matches(atom)||Nextatom.matches(atom)) return true;}
                case 2:{if (Previousatom.matches(atom)&& Nextatom.matches(atom)) return true;}
                case 3:{if (Previousatom.matches(atom)&& Nextatom.matches(atom)) return true;}
                default:return false;
            }
        }
        else if(getOperatorValue(OPC.nextElement())==1)
              return value;
        else
            return value;
    }
    private int getOperatorValue(String str){
        if(str.equals(","))return 1;
        else if(str.equals("&"))return 2;
        else if(str.equals(";")) return 3;
        else return 4;
    }
    public String toString(){
        return new String("SOperatorAtom -> " +
                "["+IQAT.getAtomCount()+"//"+OPC.size()+"]");
    }
}
