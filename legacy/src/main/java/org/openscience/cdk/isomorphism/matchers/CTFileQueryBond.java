/* Copyright (C) 2010  Mark Rijnbeek <markr@ebi.ac.uk>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Captures query bond types defined in the CTFile.
 *
 * @cdk.module  isomorphism
 * @cdk.githash
 * @deprecated Use {@link Expr.Type#SINGLE_OR_AROMATIC} etc.
 */
@Deprecated
public class CTFileQueryBond extends QueryBond implements IQueryBond {

    /*
     * Bond types, as stated in the CTFile manual 1 = Single, 2 = Double, 3 =
     * Triple, 4 = Aromatic, 5 = Single or Double, 6 = Single or Aromatic, 7 =
     * Double or Aromatic, 8 = Any
     */
    public enum Type {
        SINGLE, DOUBLE, TRIPLE, AROMATIC, SINGLE_OR_DOUBLE, SINGLE_OR_AROMATIC, DOUBLE_OR_AROMATIC, ANY
    }

    public CTFileQueryBond(IChemObjectBuilder builder) {
        super(builder);
    }

    /**
     * The type of this bond.
     */
    protected Type type = (Type) CDKConstants.UNSET;

    /**
     * Getter for bond type
     * @param type
     */
    public void setType(CTFileQueryBond.Type type) {
        this.type = type;
    }

    /**
     * Getter for type
     * @return the type of this bond
     */
    public CTFileQueryBond.Type getType() {
        return type;
    }

    @Override
    public boolean matches(IBond bond) {
        return false;
    }

    /**
     * Create a CTFileQueryBond of the specified type (from the MDL spec). The
     * bond copies the atoms and sets the type using the value 'type', 5 = single
     * or double, 8 = any, etc.
     *
     * @param bond an existing bond
     * @param type the specified type
     * @return a new CTFileQueryBond
     */
    public static CTFileQueryBond ofType(IBond bond, int type) {
        CTFileQueryBond queryBond = new CTFileQueryBond(bond.getBuilder());
        queryBond.setOrder(Order.UNSET);
        queryBond.setAtoms(new IAtom[]{bond.getBegin(), bond.getEnd()});
        switch (type) {
            case 1:
                queryBond.setType(Type.SINGLE);
                break;
            case 2:
                queryBond.setType(Type.DOUBLE);
                break;
            case 3:
                queryBond.setType(Type.TRIPLE);
                break;
            case 4:
                queryBond.setType(Type.AROMATIC);
                break;
            case 5:
                queryBond.setType(Type.SINGLE_OR_DOUBLE);
                break;
            case 6:
                queryBond.setType(Type.SINGLE_OR_AROMATIC);
                break;
            case 7:
                queryBond.setType(Type.DOUBLE_OR_AROMATIC);
                break;
            case 8:
                queryBond.setType(Type.ANY);
                break;
            default:
                throw new IllegalArgumentException("Unknown bond type: " + type);
        }
        return queryBond;
    }
}
