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
    private final int hcount;

    /**
     * Creates a new instance
     *
     * @param hcount
     */
    public ImplicitHCountAtom(int hcount, IChemObjectBuilder builder) {
        super(builder);
        this.hcount = hcount;
        this.setImplicitHydrogenCount(hcount);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom#matches(org
     * .openscience.cdk.interfaces.IAtom)
     */
    @Override
    public boolean matches(IAtom atom) {
        // h counts should be set before match throw runtime exception?
        if (atom.getImplicitHydrogenCount() == null)
            return false;
        return atom.getImplicitHydrogenCount() == hcount;
    }

    /*
     * (non-Javadoc)
     * @see org.openscience.cdk.PseudoAtom#toString()
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("ImplicitHCountAtom(");
        s.append(this.hashCode()).append(", ");
        s.append("IH:" + hcount);
        s.append(')');
        return s.toString();
    }
}
