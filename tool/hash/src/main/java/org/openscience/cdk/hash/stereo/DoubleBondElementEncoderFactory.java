/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.hash.stereo;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.OPPOSITE;

/**
 * Defines a stereo encoder factory for the hash code. The factory allows the
 * generation of stereo hash codes for molecules with predefined {@link
 * IDoubleBondStereochemistry} stereo elements.
 *
 * @author John May
 * @cdk.module hash
 */
public final class DoubleBondElementEncoderFactory implements StereoEncoderFactory {

    /**
     *{@inheritDoc}
     */
    @Override
    public StereoEncoder create(IAtomContainer container, int[][] graph) {

        // index atoms for quick lookup - wish we didn't have to do this
        // but the it's better than calling getAtomNumber every time - we use
        // a lazy creation so it's only created if there was a need for it
        Map<IAtom, Integer> atomToIndex = null;

        List<StereoEncoder> encoders = new ArrayList<StereoEncoder>();

        // for each double-bond element - create a new encoder
        for (IStereoElement se : container.stereoElements()) {
            if (se instanceof IDoubleBondStereochemistry) {
                encoders.add(encoder((IDoubleBondStereochemistry) se, atomToIndex = indexMap(atomToIndex, container),
                        graph));
            }
        }

        return encoders.isEmpty() ? StereoEncoder.EMPTY : new MultiStereoEncoder(encoders);
    }

    /**
     * Create an encoder for the {@link IDoubleBondStereochemistry} element.
     *
     * @param dbs          stereo element from an atom container
     * @param atomToIndex  map of atoms to indices
     * @param graph        adjacency list of connected vertices
     * @return a new geometry encoder
     */
    private static GeometryEncoder encoder(IDoubleBondStereochemistry dbs, Map<IAtom, Integer> atomToIndex,
            int[][] graph) {

        IBond db = dbs.getStereoBond();
        int u = atomToIndex.get(db.getAtom(0));
        int v = atomToIndex.get(db.getAtom(1));

        // we now need to expand our view of the environment - the vertex arrays
        // 'us' and 'vs' hold the neighbors of each end point of the double bond
        // ('u' or 'v'). The first neighbor is always the one stored in the
        // stereo element. The second is the other non-double bonded vertex
        // which we must find from the neighbors list (findOther). If there is
        // no additional atom attached (or perhaps it is an implicit Hydrogen)
        // we use either double bond end point.
        IBond[] bs = dbs.getBonds();
        int[] us = new int[2];
        int[] vs = new int[2];

        us[0] = atomToIndex.get(bs[0].getConnectedAtom(db.getAtom(0)));
        us[1] = graph[u].length == 2 ? u : findOther(graph[u], v, us[0]);

        vs[0] = atomToIndex.get(bs[1].getConnectedAtom(db.getAtom(1)));
        vs[1] = graph[v].length == 2 ? v : findOther(graph[v], u, vs[0]);

        int parity = dbs.getStereo() == OPPOSITE ? +1 : -1;

        GeometricParity geomParity = GeometricParity.valueOf(parity);

        // the permutation parity is combined - but note we only use this if we
        // haven't used 'u' or 'v' as place holders (i.e. implicit hydrogens)
        // otherwise there is only '1' and the parity is just '1' (identity)
        PermutationParity permParity = new CombinedPermutationParity(us[1] == u ? BasicPermutationParity.IDENTITY
                : new BasicPermutationParity(us), vs[1] == v ? BasicPermutationParity.IDENTITY
                : new BasicPermutationParity(vs));
        return new GeometryEncoder(new int[]{u, v}, permParity, geomParity);
    }

    /**
     * Finds a vertex in 'vs' which is not 'u' or 'x'.
     * .
     * @param vs fixed size array of 3 elements
     * @param u  a vertex in 'vs'
     * @param x  another vertex in 'vs'
     * @return the other vertex
     */
    private static int findOther(int[] vs, int u, int x) {
        for (int v : vs) {
            if (v != u && v != x) return v;
        }
        throw new IllegalArgumentException("vs[] did not contain another vertex");
    }

    /**
     * Lazy creation of an atom index map.
     *
     * @param map       existing map (possibly null)
     * @param container the container we want the map for
     * @return a usable atom to index map for the given container
     */
    private static Map<IAtom, Integer> indexMap(Map<IAtom, Integer> map, IAtomContainer container) {
        if (map != null) return map;
        map = new HashMap<IAtom, Integer>();
        for (IAtom a : container.atoms()) {
            map.put(a, map.size());
        }
        return map;
    }
}
