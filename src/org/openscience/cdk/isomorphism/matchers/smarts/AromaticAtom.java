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
import org.openscience.cdk.CDKConstants;

/**
 * This matcher any aromatic atom. This assumes that aromaticity in the molecule
 * has been perceived.
 *
 * @cdk.module extra
 * @cdk.svnrev  $Revision$
 */
public class AromaticAtom extends SMARTSAtom {
    
    private static final long serialVersionUID = -3345204886992669829L;
    private IAtom Element=null;
    public AromaticAtom() {
    	setFlag(CDKConstants.ISAROMATIC, true);
    }
    public AromaticAtom(IAtom m_atom){
        Element = m_atom;
        setFlag(CDKConstants.ISAROMATIC, true);
    }
    public int getOperator(){
        if(ID!=null && Element!=null)
            return 1;
        else if(ID!=null && Element==null)
            return 2;
        else if(ID==null && Element==null)
            return 3;
        else if(ID==null && Element!=null)
            return 4;
        return 5;
    }
    public boolean matches(IAtom atom) {
    	switch(getOperator()){
                case 1: { if((!atom.getFlag(CDKConstants.ISAROMATIC)) &
                         (atom.getSymbol()!=Element.getSymbol())) return true; break;}
                case 2: { if(!atom.getFlag(CDKConstants.ISAROMATIC)) return true; break;}
                case 3:{if(atom.getFlag(CDKConstants.ISAROMATIC)) return true; break;}
                case 4:{
                         if(
                            (atom.getFlag(CDKConstants.ISAROMATIC)) &
                            (atom.getSymbol().equals(Element.getSymbol()))
                           ) 
                        return true; break;
                       }
                default:return false;
            }   
    	return false;
    };

    public String toString() {
		return "AromaticAtom()";
    }
}

