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
public class SymbolAndChargeQueryAtom extends QueryAtom implements IQueryAtom {

    private static final long serialVersionUID = 3328313175614956150L;

    public SymbolAndChargeQueryAtom(IChemObjectBuilder builder) {
        super(builder);
    }

    public SymbolAndChargeQueryAtom(IAtom atom) {
        super(atom.getSymbol(), atom.getBuilder());
        setFormalCharge(atom.getFormalCharge());
    }

    public void setOperator(String str) {}

    @Override
    public boolean matches(IAtom atom) {
        int requiredCharge = this.getFormalCharge() == null ? 0 : this.getFormalCharge();
        int foundCharge = atom.getFormalCharge() == null ? 0 : atom.getFormalCharge();
        return this.getSymbol().equals(atom.getSymbol()) && requiredCharge == foundCharge;
    };

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("SymbolAndChargeQueryAtom(");
        s.append(this.hashCode() + ", ");
        s.append(getSymbol() + ", ");
        s.append(getFormalCharge());
        s.append(')');
        return s.toString();
    }

    @Override
    public IAtom clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
