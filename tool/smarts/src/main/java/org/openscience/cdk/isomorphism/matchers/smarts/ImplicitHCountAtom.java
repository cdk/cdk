/* Copyright (C) 2004-2007  The Chemistry Development Kit (CDK) project
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * This matcher checks the number of implicit hydrogens of the Atom.
 *
 * @cdk.module  smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 */
public class ImplicitHCountAtom extends SMARTSAtom {
    
    private static final long serialVersionUID = 6752937431492584928L;
    
    /**
     * Creates a new instance
     *
     * @param hCount
     */
    public ImplicitHCountAtom(int hCount, IChemObjectBuilder builder) {
        super(builder);
        this.setImplicitHydrogenCount(hCount);
    }
   
    /**
     * Returns the implicit hydrogen count of an atom
     * 
     * @param atom an atom
     * @return obtain the implicit hydrogen count, 0 if null 
     */
    private int getIMPH(IAtom atom){
        if (atom.getImplicitHydrogenCount() == CDKConstants.UNSET) return 0;
        else return atom.getImplicitHydrogenCount();
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org.openscience.cdk.interfaces.IAtom)
     */
    public boolean matches(IAtom atom) {
        return (getIMPH(atom)!=0 && getIMPH(atom)==getIMPH(this));
    }
    /* (non-Javadoc)
     * @see org.openscience.cdk.PseudoAtom#toString()
     */
    public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("ImplicitHCountAtom(");
        s.append(this.hashCode()).append(", ");
		s.append("IH:" + getIMPH(this));
		s.append(')');
		return s.toString();
    }
}

