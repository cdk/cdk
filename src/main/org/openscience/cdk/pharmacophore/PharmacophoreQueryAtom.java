/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2008  Rajarshi Guha <rajarshi.guha@gmail.com>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.Atom;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;

/**
 * Represents a query pharmacophore group.
 * <p/>
 * This class is meant to be used to construct pharmacophore queries in conjunction
 * with {@link org.openscience.cdk.pharmacophore.PharmacophoreQueryBond} and an
 * {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainer}.
 *
 * @author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.svnrev  $Revision$
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @see org.openscience.cdk.pharmacophore.PharmacophoreQueryBond
 * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainer
 * @see org.openscience.cdk.pharmacophore.PharmacophoreMatcher
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreQueryAtomTest")
public class PharmacophoreQueryAtom extends Atom implements IQueryAtom {
    private String smarts;

    /**
     * Creat a new query pharmacophore group
     *
     * @param symbol The symbol for the group
     * @param smarts The SMARTS pattern to be used for matching
     */
    public PharmacophoreQueryAtom(String symbol, String smarts) {
        setSymbol(symbol);
        this.smarts = smarts;
    }

    /**
     * Get the SMARTS pattern for this pharmacophore group.
     *
     * @return The SMARTS pattern
     */
    @TestMethod("testGetSmarts")
    public String getSmarts() {
        return smarts;
    }

    /**
     * Checks whether this query atom matches a target atom.
     * <p/>
     * Currently a query pharmacophore atom will match a target pharmacophore group if the
     * symbols of the two groups match. This is based on the assumption that
     * pharmacophore groups with the same symbol will have the same SMARTS
     * pattern.
     *
     * @param atom A target pharmacophore group
     * @return true if the current query group has the same symbol as the target group
     */
    @TestMethod("testMatches")
    public boolean matches(IAtom atom) {
        PharmacophoreAtom patom = (PharmacophoreAtom) atom;
        return patom.getSymbol().equals(getSymbol());
    }

    @TestMethod("testSetOperator")
    public void setOperator(String ID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
