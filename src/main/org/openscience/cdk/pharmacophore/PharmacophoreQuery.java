/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2009  Rajarshi Guha <rajarshi.guha@gmail.com>
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
package org.openscience.cdk.pharmacophore;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a colleciton of pharmacophore groups and constraints.
 * <p/>
 * This extends {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainer} since
 * we need to be able to support things such as exclusion volumes, which cannot (easily)
 * be represented as atom or bond analogs.
 *
 * @cdk.author Rajarshi Guha
 * @cdk.module pcore
 * @cdk.keyword pharmacophore
 * @cdk.keyword 3D isomorphism
 * @cdk.svnrev $Revision$
 */
@TestClass("org.openscience.cdk.pharmacophore.PharmacophoreQueryTest")
public class PharmacophoreQuery extends QueryAtomContainer {
    private List<Object> exclusionVolumes;

    public PharmacophoreQuery() {
        super();
        exclusionVolumes = new ArrayList<Object>();
    }


    /**
     * String representation of this query.
     *
     * @return string representation of this query
     */
    @TestMethod("testToString")
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("PharmacophoreQuery(").append(this.hashCode() + ", ");
        stringBuffer.append("#A:" + getAtomCount() + ", ");
        stringBuffer.append("#EC:" + getElectronContainerCount() + ", ");
        for (IAtom atom : atoms()) {
            PharmacophoreQueryAtom qatom = (PharmacophoreQueryAtom) atom;
            stringBuffer.append(qatom.getSymbol()).append(", ");
        }
        for (IBond bond : bonds()) {
            stringBuffer.append(bond.toString()).append(", ");
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }
}
