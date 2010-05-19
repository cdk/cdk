/* Copyright (C) 2009-2010  Syed Asad Rahman {asad@ebi.ac.uk}
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.openscience.cdk.smsd.algorithm.vflib.validator;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.global.BondType;

/**
 * Checks if a bond is matching between query and target molecules.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
@TestClass("org.openscience.cdk.smsd.algorithm.vflib.VFLibTest")
public class VFBondMatcher extends Bond implements IQueryBond {

    static final long serialVersionUID = -7861469841127325812L;
    private IBond queryBond;
    private int unsaturation = 0;
    /**
     * Bond type flag
     */
    private boolean bondTypeFlag = BondType.getInstance().isBondSensitive();

    /**
     * Constructor
     */
    public VFBondMatcher() {
        this.queryBond = null;
        this.unsaturation = -1;
    }

    /**
     * Constructor
     * @param queryBond
     */
    public VFBondMatcher(IBond queryBond) {
        this.queryBond = queryBond;
        this.unsaturation = getUnsaturation(queryBond);
    }

    /** {@inheritDoc}
     *
     * @param targetBond
     */
    @Override
    public boolean matches(IBond targetBond) {
        if (isBondTypeFlag()) {
            return isBondTypeMatch(targetBond);
        } else if (queryBond != null && targetBond != null) {
            return true;
        }
        return false;
    }

//        Uncomment this when chemistry in CDK improves
//        if (this.unsaturation >= 0 && this.unsaturation == getUnsaturation(targetBond)) {
//            return true;
//        }
//
//        return false;
//    private boolean isAtomSymbolsMatch(IBond targetBond) {
//        IAtom qAtom1 = queryBond.getAtom(0);
//        IAtom qAtom2 = queryBond.getAtom(1);
//        IAtom tAtom1 = targetBond.getAtom(0);
//        IAtom tAtom2 = targetBond.getAtom(1);
//        if (qAtom1.getSymbol().equals(tAtom1.getSymbol()) && qAtom2.getSymbol().equals(tAtom2.getSymbol())) {
//            return true;
//        } else if (qAtom1.getSymbol().equals(tAtom2.getSymbol()) && qAtom2.getSymbol().equals(tAtom1.getSymbol())) {
//            return true;
//        }
//        return false;
//    }

    /**
     *
     * @param ReactantBond
     * @param targetBond
     * @return
     */
    private boolean isBondTypeMatch(IBond targetBond) {

        if (targetBond instanceof IQueryBond && queryBond instanceof IBond) {
            IQueryBond bond = (IQueryBond) targetBond;
            IQueryAtom atom1 = (IQueryAtom) (targetBond.getAtom(0));
            IQueryAtom atom2 = (IQueryAtom) (targetBond.getAtom(1));
            if (bond.matches(queryBond)) {
                // ok, bonds match
                if (atom1.matches(queryBond.getAtom(0)) && atom2.matches(queryBond.getAtom(1))
                        || atom1.matches(queryBond.getAtom(1)) && atom2.matches(queryBond.getAtom(0))) {
                    // ok, atoms match in either order
                    return true;
                }
            }
        } else if (queryBond instanceof IQueryBond && targetBond instanceof IBond) {
            IQueryBond bond = (IQueryBond) queryBond;
            IQueryAtom atom1 = (IQueryAtom) (queryBond.getAtom(0));
            IQueryAtom atom2 = (IQueryAtom) (queryBond.getAtom(1));
            if (bond.matches(targetBond)) {
                // ok, bonds match
                if (atom1.matches(targetBond.getAtom(0)) && atom2.matches(targetBond.getAtom(1))
                        || atom1.matches(targetBond.getAtom(1)) && atom2.matches(targetBond.getAtom(0))) {
                    // ok, atoms match in either order
                    return true;
                }
            }
        } else {

            int ReactantBondType = queryBond.getOrder().ordinal();
            int ProductBondType = targetBond.getOrder().ordinal();


            if ((queryBond.getFlag(CDKConstants.ISAROMATIC) == targetBond.getFlag(CDKConstants.ISAROMATIC))
                    && (ReactantBondType == ProductBondType)) {
                return true;
            }

            if (queryBond.getFlag(CDKConstants.ISAROMATIC) && targetBond.getFlag(CDKConstants.ISAROMATIC)) {
                return true;
            }

        }
        return false;
    }

    private Integer getUnsaturation(IBond bond) {
        return getUnsaturation(bond.getAtom(0)) + getUnsaturation(bond.getAtom(1));
    }

    private Integer getUnsaturation(IAtom atom) {
        return (atom.getValency() == null || atom.getFormalNeighbourCount() == null) ? -1 : atom.getValency() - atom.getFormalNeighbourCount();
    }

    /**
     * is a bond sensitive match
     * @return the bondTypeFlag
     */
    protected boolean isBondTypeFlag() {
        return bondTypeFlag;
    }

    /**
     * set bond type match flag
     * @param bondTypeFlag the bondTypeFlag to set
     */
    protected void setBondTypeFlag(boolean bondTypeFlag) {
        this.bondTypeFlag = bondTypeFlag;
    }
}
