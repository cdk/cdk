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

package org.openscience.cdk.isomorphism;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SMARTSAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.StereoBond;

import java.util.Arrays;
import java.util.Map;

import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation;
import static org.openscience.cdk.interfaces.IDoubleBondStereochemistry.Conformation.TOGETHER;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo;
import static org.openscience.cdk.interfaces.ITetrahedralChirality.Stereo.CLOCKWISE;

/**
 * Filters SMARTS matches for those that have valid stereochemistry
 * configuration.
 *
 * Note: This class is internal and will be private in future.
 *
 * @author John May
 * @cdk.module smarts
 * @cdk.githash
 */
public final class SmartsStereoMatch implements Predicate<int[]> {

    /** Query and target contains. */
    private final IAtomContainer query, target;

    /** Atom to atom index lookup. */
    private final Map<IAtom, Integer> queryMap, targetMap;

    /** Indexed array of stereo elements. */
    private final IStereoElement[]    queryElements, targetElements;

    /** Indexed array of stereo element types. */
    private final Type[]              queryTypes, targetTypes;

    /** Indices of focus atoms of stereo elements. */
    private final int[]               queryStereoIndices, targetStereoIndices;

    /**
     * Create a predicate for checking mappings between a provided
     * {@code query} and {@code target}.
     *
     * @param query query container
     * @param target target container
     */
    public SmartsStereoMatch(IAtomContainer query, IAtomContainer target) {

        if (!(query instanceof IQueryAtomContainer))
            throw new IllegalArgumentException("match predicate is for SMARTS only");
        if (!(query.getAtom(0) instanceof SMARTSAtom))
            throw new IllegalArgumentException("match predicate is for SMARTS only");

        this.query = query;
        this.target = target;

        this.queryMap = indexAtoms(query);
        this.targetMap = indexAtoms(target);
        this.queryElements = new IStereoElement[query.getAtomCount()];
        this.targetElements = new IStereoElement[target.getAtomCount()];
        this.queryTypes = new Type[query.getAtomCount()];
        this.targetTypes = new Type[target.getAtomCount()];

        queryStereoIndices = indexElements(queryMap, queryElements, queryTypes, query);
        targetStereoIndices = indexElements(targetMap, targetElements, targetTypes, target);
    }

    /**
     * Is the {@code mapping} of the stereochemistry in the query preserved in
     * the target.
     *
     * @param mapping permutation of the query vertices
     * @return the stereo chemistry is value
     */
    @Override
    public boolean apply(final int[] mapping) {
        for (final int u : queryStereoIndices) {
            switch (queryTypes[u]) {
                case Tetrahedral:
                    if (!checkTetrahedral(u, mapping)) return false;
                    break;
                case Geometric:
                    if (!checkGeometric(u, otherIndex(u), mapping)) return false;
                    break;
            }
        }
        return true;
    }

    /**
     * Verify the tetrahedral stereochemistry (clockwise/anticlockwise) of atom
     * {@code u} is preserved in the target when the {@code mapping} is used.
     *
     * @param u       tetrahedral index in the target
     * @param mapping mapping of vertices
     * @return the tetrahedral configuration is preserved
     */
    private boolean checkTetrahedral(int u, int[] mapping) {

        int v = mapping[u];

        ITetrahedralChirality queryElement = (ITetrahedralChirality) queryElements[u];
        ITetrahedralChirality targetElement = (ITetrahedralChirality) targetElements[v];

        SMARTSAtom queryAtom = (SMARTSAtom) query.getAtom(u);
        IAtom targetAtom = target.getAtom(v);

        int[] us = neighbors(queryElement, queryMap);
        us = map(u, v, us, mapping);
        int p = permutationParity(us);

        // check if unspecified was allowed
        if (targetTypes[v] == null) return queryAtom.chiralityMatches(targetAtom, 0, p);

        // target was non-tetrahedral
        if (targetTypes[v] != Type.Tetrahedral) return false;

        int[] vs = neighbors(targetElement, targetMap);
        int q = permutationParity(vs) * parity(targetElement.getStereo());

        return queryAtom.chiralityMatches(targetAtom, q, p);
    }

    /**
     * Transforms the neighbors {@code us} adjacent to {@code u} into the target
     * indices using the mapping {@code mapping}. The transformation accounts
     * for an implicit hydrogen in the query being an explicit hydrogen in the
     * target.
     *
     * @param u       central atom of tetrahedral element
     * @param v       mapped central atom of the tetrahedral element
     * @param us      neighboring vertices of u (u plural)
     * @param mapping mapping from the query to the target
     * @return the neighbors us, transformed into the neighbors around v
     */
    private int[] map(int u, int v, int[] us, int[] mapping) {
        for (int i = 0; i < us.length; i++)
            us[i] = mapping[us[i]];
        return us;
    }

    /**
     * Verify the geometric stereochemistry (cis/trans) of the double bond
     * {@code u1=u2} is preserved in the target when the {@code mapping} is
     * used.
     *
     * @param u1      one index of the double bond
     * @param u2      other index of the double bond
     * @param mapping mapping of vertices
     * @return the geometric configuration is preserved
     */
    private boolean checkGeometric(int u1, int u2, int[] mapping) {

        int v1 = mapping[u1];
        int v2 = mapping[u2];

        IDoubleBondStereochemistry queryElement = (IDoubleBondStereochemistry) queryElements[u1];
        IBond[] queryBonds = queryElement.getBonds();

        boolean unspecified = ((StereoBond) queryBonds[0]).unspecified() || ((StereoBond) queryBonds[1]).unspecified();

        if (unspecified && (targetTypes[v1] == null || targetTypes[v2] == null)) return true;

        // no configuration in target
        if (targetTypes[v1] != Type.Geometric || targetTypes[v2] != Type.Geometric) return false;

        IDoubleBondStereochemistry targetElement = (IDoubleBondStereochemistry) targetElements[v1];

        // although the atoms were mapped and 'v1' and 'v2' are bond in double-bond
        // elements they are not in the same element
        if (!targetElement.getStereoBond().contains(target.getAtom(v1))
                || !targetElement.getStereoBond().contains(target.getAtom(v2))) return false;

        // bond is undirected so we need to ensure v1 is the first atom in the bond
        // we also need to to swap the substituents later
        boolean swap = false;
        if (targetElement.getStereoBond().getAtom(0) != target.getAtom(v1)) {
            int tmp = v1;
            v1 = v2;
            v2 = tmp;
            swap = true;
        }

        IBond[] targetBonds = targetElement.getBonds();

        int p = parity(queryElement.getStereo());
        int q = parity(targetElement.getStereo());

        int uLeft = queryMap.get(queryBonds[0].getConnectedAtom(query.getAtom(u1)));
        int uRight = queryMap.get(queryBonds[1].getConnectedAtom(query.getAtom(u2)));

        int vLeft = targetMap.get(targetBonds[0].getConnectedAtom(target.getAtom(v1)));
        int vRight = targetMap.get(targetBonds[1].getConnectedAtom(target.getAtom(v2)));

        if (swap) {
            int tmp = vLeft;
            vLeft = vRight;
            vRight = tmp;
        }
        if (mapping[uLeft] != vLeft) p *= -1;
        if (mapping[uRight] != vRight) p *= -1;

        return p == q;
    }

    /**
     * Access the neighbors of {@code element} as their indices.
     *
     * @param element tetrahedral element
     * @param map     atom index lookup
     * @return the neighbors
     */
    private int[] neighbors(ITetrahedralChirality element, Map<IAtom, Integer> map) {
        IAtom[] atoms = element.getLigands();
        int[] vs = new int[atoms.length];
        for (int i = 0; i < atoms.length; i++)
            vs[i] = map.get(atoms[i]);
        return vs;
    }

    /**
     * Compute the permutation parity of the values {@code vs}. The parity is
     * whether we need to do an odd or even number of swaps to put the values in
     * sorted order.
     *
     * @param vs values
     * @return parity of the permutation (odd = -1, even = +1)
     */
    private int permutationParity(int[] vs) {
        int n = 0;
        for (int i = 0; i < vs.length; i++)
            for (int j = i + 1; j < vs.length; j++)
                if (vs[i] > vs[j]) n++;
        return (n & 0x1) == 1 ? -1 : 1;
    }

    /**
     * Given an index of an atom in the query get the index of the other atom in
     * the double bond.
     *
     * @param i query atom index
     * @return the other atom index involved in a double bond
     */
    private int otherIndex(int i) {
        IDoubleBondStereochemistry element = (IDoubleBondStereochemistry) queryElements[i];
        return queryMap.get(element.getStereoBond().getConnectedAtom(query.getAtom(i)));
    }

    /**
     * Create an index of atoms for the provided {@code container}.
     *
     * @param container the container to index the atoms of
     * @return the index/lookup of atoms to the index they appear
     */
    private static Map<IAtom, Integer> indexAtoms(IAtomContainer container) {
        Map<IAtom, Integer> map = Maps.newHashMapWithExpectedSize(container.getAtomCount());
        for (int i = 0; i < container.getAtomCount(); i++)
            map.put(container.getAtom(i), i);
        return map;
    }

    /**
     * Index the stereo elements of the {@code container} into the the {@code
     * elements} and {@code types} arrays. The {@code map} is used for looking
     * up the index of atoms.
     *
     * @param map       index of atoms
     * @param elements  array to fill with stereo elements
     * @param types     type of stereo element indexed
     * @param container the container to index the elements of
     * @return indices of atoms involved in stereo configurations
     */
    private static int[] indexElements(Map<IAtom, Integer> map, IStereoElement[] elements, Type[] types,
            IAtomContainer container) {
        int[] indices = new int[container.getAtomCount()];
        int nElements = 0;
        for (IStereoElement element : container.stereoElements()) {
            if (element instanceof ITetrahedralChirality) {
                ITetrahedralChirality tc = (ITetrahedralChirality) element;
                int idx = map.get(tc.getChiralAtom());
                elements[idx] = element;
                types[idx] = Type.Tetrahedral;
                indices[nElements++] = idx;
            } else if (element instanceof IDoubleBondStereochemistry) {
                IDoubleBondStereochemistry dbs = (IDoubleBondStereochemistry) element;
                int idx1 = map.get(dbs.getStereoBond().getAtom(0));
                int idx2 = map.get(dbs.getStereoBond().getAtom(1));
                elements[idx2] = elements[idx1] = element;
                types[idx1] = types[idx2] = Type.Geometric;
                indices[nElements++] = idx1; // only visit the first atom
            }
        }
        return Arrays.copyOf(indices, nElements);
    }

    /**
     * Get the parity (-1,+1) of the tetrahedral configuration.
     *
     * @param stereo configuration
     * @return the parity
     */
    private int parity(Stereo stereo) {
        return stereo == CLOCKWISE ? 1 : -1;
    }

    /**
     * Get the parity (-1,+1) of the geometric (double bond) configuration.
     *
     * @param conformation configuration
     * @return the parity
     */
    private int parity(Conformation conformation) {
        return conformation == TOGETHER ? 1 : -1;
    }

    // could be moved into the IStereoElement to allow faster introspection
    private static enum Type {
        Tetrahedral, Geometric
    }
}
