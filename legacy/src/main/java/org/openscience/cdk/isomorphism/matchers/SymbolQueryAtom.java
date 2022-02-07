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
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * @cdk.module  isomorphism
 * @cdk.githash
 */
@Deprecated
public class SymbolQueryAtom extends QueryAtom implements IQueryAtom {

    private static final long serialVersionUID = -5774610415273279451L;
    private String            ID;
    private int               HCount           = 0;

    public SymbolQueryAtom(IChemObjectBuilder builder) {
        super(builder);
    }

    public SymbolQueryAtom(IAtom atom) {
        super(atom.getSymbol(), atom.getBuilder());
    }

    public void setHCount(int m_HCount) {
        HCount = m_HCount;
    }

    @Override
    public boolean matches(IAtom atom) {
        if (ID != null && HCount == 0)
            return this.getSymbol() != (atom.getSymbol());
        else if (ID == null && HCount != 0) {
            return (this.getImplicitHydrogenCount() == HCount);
        } else
            return this.getSymbol().equals(atom.getSymbol());
    };

    public void setOperator(String str) {
        ID = str;

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("SymbolQueryAtom(");
        s.append(this.hashCode() + ", ");
        s.append(getSymbol());
        s.append(')');
        return s.toString();
    }

    @Override
    public IAtom clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
