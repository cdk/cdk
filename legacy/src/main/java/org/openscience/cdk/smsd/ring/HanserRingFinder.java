/*
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
 *
 * Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 */
package org.openscience.cdk.smsd.ring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;

/**
 * Finds the Set of all Rings. This is an implementation of the algorithm
 * published in {@cdk.cite HAN96}. Some of the comments refer to pseudo code
 * fragments listed in this article. The concept is that a regular molecular
 * graph is first converted into a path graph (refer PathGraph.java),
 * i.e. a graph where the edges are actually paths. This can list several
 * nodes that are implicitly connecting the two nodes between the path
 * is formed (refer PathEdge.java).
 *
 * The paths that join source and sink node are step by step fused and the joined
 * nodes are deleted from the path graph (collapsed path). What remains is a graph
 * of paths that have the same start and endpoint and are thus rings (source=sink=ring).
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt; 2009-2010
 * @deprecated Use CDK AllRingsFinder. A more recent version of SMSD is available at
 *             <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class HanserRingFinder implements RingFinder {

    private List<List<IAtom>> rings;

    public HanserRingFinder() {
        rings = new ArrayList<List<IAtom>>();
    }

    /**
     * Returns a collection of rings.
     *
     * @param  molecule
     * @return a {@link Collection} of {@link List}s containing one ring each
     * @see org.openscience.cdk.smsd.ring.RingFinder#findRings(org.openscience.cdk.interfaces.IAtomContainer)
     */
    @Override
    public Collection<List<IAtom>> findRings(IAtomContainer molecule) {
        if (molecule == null) return null;
        rings.clear();
        PathGraph graph = new PathGraph(molecule);

        for (int i = 0; i < molecule.getAtomCount(); i++) {
            List<PathEdge> edges = graph.remove(molecule.getAtom(i));

            for (PathEdge edge : edges) {
                List<IAtom> ring = edge.getAtoms();
                rings.add(ring);
            }
        }

        return rings;
    }

    /**
     * Returns Ring set based on Hanser Ring Finding method
     * @param molecule
     * @return report collected the rings
     * @see org.openscience.cdk.smsd.ring.RingFinder#getRingSet(org.openscience.cdk.interfaces.IAtomContainer)
     */
    @Override
    public IRingSet getRingSet(IAtomContainer molecule) throws CDKException {

        Collection<List<IAtom>> cycles = findRings(molecule);

        IRingSet ringSet = molecule.getBuilder().newInstance(IRingSet.class);

        for (List<IAtom> ringAtoms : cycles) {
            IRing ring = molecule.getBuilder().newInstance(IRing.class);
            for (IAtom atom : ringAtoms) {
                atom.setFlag(CDKConstants.ISINRING, true);
                ring.addAtom(atom);
                for (IAtom atomNext : ringAtoms) {
                    if (!atom.equals(atomNext)) {
                        IBond bond = molecule.getBond(atom, atomNext);
                        if (bond != null) {
                            bond.setFlag(CDKConstants.ISINRING, true);
                            ring.addElectronContainer(bond);
                        }
                    }
                }
            }
            ringSet.addAtomContainer(ring);
        }
        return ringSet;
    }
}
