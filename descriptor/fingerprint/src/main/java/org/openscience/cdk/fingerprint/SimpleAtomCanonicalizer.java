/* Copyright (C) 2012   Syed Asad Rahman <asad@ebi.ac.uk>
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * <P> This code returns a sorted set of atoms for a container according to its
 * symbol and hybridization states. This will aid in finding a deterministic
 * path rather than Stochastic one. </P>
 *
 * @author Syed Asad Rahman (2012)
 * @cdk.keyword fingerprint
 * @cdk.keyword similarity
 * @cdk.module fingerprint
 * @cdk.githash
 *
 */
public class SimpleAtomCanonicalizer {

    /**
     * @param container the container
     * @return canonicalized atoms
     */
    public Collection<IAtom> canonicalizeAtoms(IAtomContainer container) {

        List<IAtom> canonicalizedVertexList = new ArrayList<IAtom>();
        for (IAtom atom : container.atoms()) {
            canonicalizedVertexList.add(atom);
        }
        Collections.sort(canonicalizedVertexList, new SimpleAtomComparator());
        return canonicalizedVertexList;
    }
}
