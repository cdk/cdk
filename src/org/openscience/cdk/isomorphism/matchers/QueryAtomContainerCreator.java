/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;

/**
 * @cdk.module extra
 */
public class QueryAtomContainerCreator {

    /**
     * Creates a QueryAtomContainerCreator with SymbolQueryAtom's and
     * OrderQueryBond's.
     */
    public static QueryAtomContainer createBasicQueryContainer(AtomContainer container) {
        QueryAtomContainer queryContainer = new QueryAtomContainer();
        Atom[] atoms = container.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            queryContainer.addAtom(new SymbolQueryAtom(atoms[i]));
        }
        Bond[] bonds = container.getBonds();
        for (int i=0; i<bonds.length; i++) {
            int index1 = container.getAtomNumber(bonds[i].getAtomAt(0));
            int index2 = container.getAtomNumber(bonds[i].getAtomAt(1));
            queryContainer.addBond(new OrderQueryBond((QueryAtom)queryContainer.getAtomAt(index1),
                                                      (QueryAtom)queryContainer.getAtomAt(index2),
                                                      bonds[i].getOrder()));
        }
        return queryContainer;
    }

}

