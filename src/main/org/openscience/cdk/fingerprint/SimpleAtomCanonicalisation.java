/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2012   Syed Asad Rahman <asad@ebi.ac.uk>
 *           
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
 */
package org.openscience.cdk.fingerprint;

import java.util.*;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;

/**
 * <P> This code returns a sorted set of atoms for a container according to its symbol and hybridization states. This
 * will aid in finding a deterministic path rather than Stochastic one. </P>
 *
 * @author Syed Asad Rahman (2012) 
 * @cdk.keyword fingerprint 
 * @cdk.keyword similarity 
 * @cdk.module standard 
 * @cdk.githash
 */
public class SimpleAtomCanonicalisation {

    /**
     * @param container the container
     * @return canonicalized atoms
     */
    public Collection<IAtom> canonicalizeAtoms(IAtomContainer container) {

        List<IAtom> canonicalizedVertexList = new LinkedList<IAtom>();
        int i = 0;
        for (Iterator<IAtom> it = container.atoms().iterator(); it.hasNext();) {
            IAtom atom = it.next();
            canonicalizedVertexList.add(i, atom);
            i++;
        }
        Collections.sort(canonicalizedVertexList, new AtomComparator());
        return canonicalizedVertexList;
    }
}

class AtomComparator implements Comparator<IAtom> {

    @Override
    public int compare(IAtom o1, IAtom o2) {
        if (!(o1 instanceof IChemObject) || !(o2 instanceof IChemObject)) {
            throw new ClassCastException();
        }
        if (o1.getSymbol().equalsIgnoreCase(o2.getSymbol())) {
            if (o1.getHybridization() != null && o2.getHybridization() != null) {
                return o1.getHybridization().compareTo(o2.getHybridization());
            }
            return 0;
        }
        return 10 * o1.getSymbol().compareToIgnoreCase(o2.getSymbol());
    }
}
