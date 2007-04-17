/*
 *  $RCSfile$
 *  $Author: Sushil Ronghe $
 *  $Date: 2007-04-12 $
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import java.util.ArrayList;

/**
 * @author niper
 */
public class RingAtom extends SMARTSAtom {

    private static final long serialVersionUID = -5145049891214205622L;

    private int RING_SIZE;

    public RingAtom() {
        // this will match any ring atom
        RING_SIZE = 1;

    }

    public RingAtom(int m_Ring_SIZE) {
        // this will match ring atom of specific size
        RING_SIZE = m_Ring_SIZE;
    }
    
     public int getOperator(){
        if(ID!=null && this.RING_SIZE==Default)
            return 1;
        else if(ID!=null && this.RING_SIZE!=Default)
            return 2;
        else if(this.RING_SIZE==Default)
            return 3;
        else if(this.RING_SIZE!=Default)
            return 4;
        return 5;
    }
    public boolean matches(IAtom atom) {
        switch(getOperator()){
            case 1:return defaultOperatorCheck(atom);
            case 2:return nonDefaultOperatorCheck(atom);
            case 3:return defaultCheck(atom);
            case 4:return nonDefaultCheck(atom);
            default:return false;
        }
    }
    private boolean defaultCheck(IAtom atom){
        if(atom.getFlag(CDKConstants.ISINRING))
            return true;
        return false;
    }
    private boolean nonDefaultCheck(IAtom atom){
        if (atom.getFlag(CDKConstants.ISINRING)) {
                ArrayList ll = (ArrayList) atom.getProperty(CDKConstants.RING_SIZES);
                for (int i = 0; i < ll.size(); i++){ 
                    if (((Integer) ll.get(i)).intValue() == RING_SIZE){
                        return true; 
                    }
                }
            }
        return false;
    }
    private boolean defaultOperatorCheck(IAtom atom){
        if(!atom.getFlag(CDKConstants.ISINRING))return true;
        return false;
    }
    private boolean nonDefaultOperatorCheck(IAtom atom){
        if (atom.getFlag(CDKConstants.ISINRING)) {
                ArrayList ll = (ArrayList) atom.getProperty(CDKConstants.RING_SIZES);
                for (int i = 0; i < ll.size(); i++) 
                    if (((Integer) ll.get(i)).intValue() != RING_SIZE) 
                        return true;
            }
        return false;
    }

    public String toString() {
        if (RING_SIZE != 1)
            return ("ring atom of size:" + RING_SIZE + " operator " );
        return ("ring atom of size: Any");
    }
}


