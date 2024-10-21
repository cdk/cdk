/*
 * Copyright (c) 2013 European Bioinformatics Institute (EMBL-EBI)
 *                    John May
 *               2018 John Mayfield (ne May)
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IDoubleBondStereochemistry;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.interfaces.ITetrahedralChirality;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.stereo.Octahedral;
import org.openscience.cdk.stereo.SquarePlanar;
import org.openscience.cdk.stereo.TrigonalBipyramidal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

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
final class QueryStereoFilter implements Predicate<int[]> {

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
     * Indicates the stereo group config for a given atom idx, 0=unsed, 1=stored, -1=inverted.
     * Initially all entries start as 0, if we hit a stereo-element in a group &1, &2, or1, or2
     * then we check if we have already "set" the group, if not then we "set" the group to make
     * the first element match, this means we may choose to flip the group to be the enantiomer.
     */
    private int[] groupConfigAdjust;

    /**
     * Create a predicate for checking mappings between a provided
     * {@code query} and {@code target}.
     *
     * @param query query container
     * @param target target container
     */
    public QueryStereoFilter(IAtomContainer query, IAtomContainer target) {

        if (!(query instanceof IQueryAtomContainer))
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
    public boolean test(final int[] mapping) {

        // reset augment group config if it was initialised
        if (groupConfigAdjust != null)
            Arrays.fill(groupConfigAdjust, 0);

        for (final int u : queryStereoIndices) {
            switch (queryTypes[u]) {
                case Tetrahedral:
                    if (!checkTetrahedral(u, mapping))
                        return false;
                    break;
                case Geometric:
                    if (!checkGeometric(u, otherIndex(u), mapping))
                        return false;
                    break;
                case Octahedral:
                    if (!checkOctahedral(u, mapping))
                        return false;
                    break;
                case TrigonalBipyramidal:
                    if (!checkTrigonalBipyramidal(u, mapping))
                        return false;
                    break;
                default:
                    System.err.println("ERROR: Unhandled stereochemistry " + this.queryTypes[u]);
                    return false;
            }
        }
        return true;
    }

    private static int indexOf(int[] xs, int x) {
        for (int i = 0; i < xs.length; i++)
            if (xs[i] == x)
                return i;
        return -1;
    }

    /**
     * Verify the tetrahedral stereo-chemistry (clockwise/anticlockwise) of atom
     * {@code u} is preserved in the target when the {@code mapping} is used.
     *
     * @param u       tetrahedral index in the target
     * @param mapping mapping of vertices
     * @return the tetrahedral configuration is preserved
     */
    private boolean checkTetrahedral(int u, int[] mapping) {

        int v = mapping[u];

        if (targetTypes[v] != null && targetTypes[v] != Type.Tetrahedral)
            return false;

        ITetrahedralChirality queryElement  = (ITetrahedralChirality) queryElements[u];
        ITetrahedralChirality targetElement = (ITetrahedralChirality) targetElements[v];

        IAtom queryAtom  = query.getAtom(u);
        IAtom targetAtom = target.getAtom(v);

        // check if unspecified was allowed
        if (targetTypes[v] == null)
            return ((QueryAtom)queryAtom).getExpression().matches(targetAtom, 0);

        // target was non-tetrahedral
        if (targetTypes[v] != Type.Tetrahedral)
            return false;

        int[] us = map(u, v, neighbors(queryElement, queryMap), mapping);
        int[] vs = neighbors(targetElement, targetMap);

        // adjustment needed for implicit neighbor (H or lone pair)
        int focusIdx = targetMap.get(targetAtom);
        for (int i = 0; i < 4; i++) {
            // find mol neighbor in mapped query list
            int j = indexOf(us, vs[i]);
            // not found then it was implicit, replace the implicit neighbor
            // (which we store as focusIdx) with this neighbor
            if (j < 0)
                us[indexOf(us, focusIdx)] = vs[i];

        }

        int parity = permutationParity(us)
                     * permutationParity(vs)
                     * parity(targetElement.getStereo());

        int groupInfo = targetElement.getGroupInfo();

        if (groupInfo != 0) {
            if (groupConfigAdjust == null)
                groupConfigAdjust = new int[target.getAtomCount()];

            // initialise the group
            if (groupConfigAdjust[v] == 0) {

                boolean leftOk = ((QueryAtom) queryAtom).getExpression().matches(targetAtom, IStereoElement.LEFT);
                boolean rghtOk = ((QueryAtom) queryAtom).getExpression().matches(targetAtom, IStereoElement.RIGHT);

                // Note: [C@,Si@@] can not happen since the target atom can't be both
                // but [C;@,@@] can in which case we can't 'set' the group based on this
                // element so wait till we find the next one
                if (leftOk && rghtOk) {
                    return true;
                }

                int adjust = 1;
                if ((parity == -1 && !leftOk) || (parity == 1 && !rghtOk))
                    adjust = -1;

                for (int idx : targetStereoIndices) {
                    if (targetElements[idx].getGroupInfo() == groupInfo) {
                        groupConfigAdjust[idx] = adjust;
                    }
                }

            }

            // make the adjustment
            parity *= groupConfigAdjust[v];
        }

        if (parity < 0)
            return ((QueryAtom) queryAtom).getExpression()
                                          .matches(targetAtom, IStereoElement.LEFT);
        else if (parity > 0)
            return ((QueryAtom) queryAtom).getExpression()
                                          .matches(targetAtom, IStereoElement.RIGHT);
        else
            return ((QueryAtom) queryAtom).getExpression()
                                          .matches(targetAtom, 0);
    }

    private boolean checkTrigonalBipyramidal(int u, int[] mapping) {
        int v = mapping[u];
        IAtom queryAtom = this.query.getAtom(u);
        IAtom targetAtom = this.target.getAtom(v);
        if (this.targetTypes[v] == null)
            return ((QueryAtom) queryAtom).getExpression().matches(targetAtom, 0);
        TrigonalBipyramidal queryElement = (TrigonalBipyramidal) this.queryElements[u];
        IStereoElement<IAtom, IAtom> targetElement = this.targetElements[v];
        Set<IAtom> used = new HashSet<>();
        List<IAtom> requiredOrdering = new ArrayList<>();
        for (IAtom atom : queryElement.getCarriers()) {
            IAtom mappedAtom = this.target.getAtom(mapping[(Integer) this.queryMap.get(atom)]);
            used.add(mappedAtom);
            requiredOrdering.add(mappedAtom);
        }
        List<IAtom> currentOrdering = new ArrayList<>();
        if (this.targetTypes[v] == Type.TrigonalBipyramidal) {
            TrigonalBipyramidal trigonalBipyramidal = null;
            if (targetElement instanceof TrigonalBipyramidal)
                trigonalBipyramidal = ((TrigonalBipyramidal) targetElement).normalize();
            if (trigonalBipyramidal == null)
                return false;
            for (IAtom atom : trigonalBipyramidal.getCarriers()) {
                if (used.contains(atom)) {
                    currentOrdering.add(atom);
                    continue;
                }
                currentOrdering.add((IAtom) trigonalBipyramidal.getFocus());
            }
        } else if (this.targetTypes[v] == Type.Octahedral) {
            Octahedral octahedral = new Octahedral(targetAtom, currentOrdering.<IAtom>toArray(new IAtom[0]),
                                                   targetElement.getConfigOrder());
            TrigonalBipyramidal tbpy = octahedral.asTrigonalBipyramidal();
            if (tbpy == null)
                return false;
            currentOrdering = tbpy.getCarriers();
        } else {
            return false;
        }
        int cfg = TrigonalBipyramidal.reorder(requiredOrdering, currentOrdering);
        if (cfg < 0)
            return false;
        cfg |= 0x5200;
        return ((QueryAtom) queryAtom).getExpression().matches(targetAtom, cfg);
    }

    private boolean checkOctahedral(int u, int[] mapping) {
        int v = mapping[u];
        IAtom queryAtom = this.query.getAtom(u);
        IAtom targetAtom = this.target.getAtom(v);
        if (this.targetTypes[v] == null)
            return ((QueryAtom) queryAtom).getExpression().matches(targetAtom, 0);
        Octahedral queryElement = (Octahedral) this.queryElements[u];
        IStereoElement<IAtom,IAtom> targetElement = this.targetElements[v];
        Set<IAtom> used = new HashSet<>();
        List<IAtom> requiredOrdering = new ArrayList<>();
        List<IAtom> currentOrdering = new ArrayList<>();
        for (IAtom atom : queryElement.getCarriers()) {
            IAtom mappedAtom = this.target.getAtom(mapping[(Integer) this.queryMap.get(atom)]);
            used.add(mappedAtom);
            requiredOrdering.add(mappedAtom);
        }
        if (this.targetTypes[v] == Type.Octahedral) {
            Octahedral octahedral = null;
            if (targetElement instanceof Octahedral)
                octahedral = ((Octahedral) targetElement).normalize();
            if (octahedral == null)
                return false;
            for (IAtom atom : octahedral.getCarriers()) {
                if (used.contains(atom)) {
                    currentOrdering.add(atom);
                    continue;
                }
                currentOrdering.add((IAtom) octahedral.getFocus());
            }
        } else if (this.targetTypes[v] == Type.TrigonalBipyramidal) {
            TrigonalBipyramidal tbpy = new TrigonalBipyramidal(targetAtom,
                                                               currentOrdering.<IAtom>toArray(new IAtom[0]),
                                                               targetElement.getConfigOrder());
            Octahedral oc = tbpy.asOctahedral();
            if (oc == null)
                return false;
            currentOrdering = oc.getCarriers();
        } else {
            return false;
        }
        int cfg = Octahedral.reorder(requiredOrdering, currentOrdering);
        if (cfg < 0)
            return false;
        cfg |= 0x6100;
        return ((QueryAtom) queryAtom).getExpression().matches(targetAtom, cfg);
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

        if (targetTypes[v1] != null && targetTypes[v1] != Type.Geometric)
            return false;
        if (targetTypes[v2] != null && targetTypes[v2] != Type.Geometric)
            return false;

        IDoubleBondStereochemistry queryElement = (IDoubleBondStereochemistry) queryElements[u1];
        IBond qbond = queryElement.getStereoBond();
        IBond tbond;

        int config = 0;
        // no configuration in target
        if (targetTypes[v1] == Type.Geometric && targetTypes[v2] == Type.Geometric) {
            IDoubleBondStereochemistry targetElement = (IDoubleBondStereochemistry) targetElements[v1];
            tbond = targetElement.getStereoBond();

            // although the atoms were mapped and 'v1' and 'v2' are bond in double-bond
            // elements they are not in the same element
            if (!targetElement.getStereoBond().contains(target.getAtom(v1))
                || !targetElement.getStereoBond().contains(target.getAtom(v2))) return false;

            IBond[] qbonds = queryElement.getBonds();
            IBond[] tbonds = targetElement.getBonds();

            // bond is undirected, so we need to ensure v1 is the first atom in the bond
            // we also need to swap the substituents later
            if (!queryElement.getStereoBond().getBegin().equals(query.getAtom(u1)))
                swap(qbonds, 0, 1);
            if (!targetElement.getStereoBond().getBegin().equals(target.getAtom(v1)))
                swap(tbonds, 0, 1);

            if (getMappedBond(qbonds[0], mapping).equals(tbonds[0]) !=
                getMappedBond(qbonds[1], mapping).equals(tbonds[1]))
                config = targetElement.getConfigOrder() ^ 0x3; // flipped
            else
                config = targetElement.getConfigOrder();
        } else {
            tbond = target.getBond(target.getAtom(v1), target.getAtom(v2));
        }

        Expr expr = ((QueryBond) qbond).getExpression();
        return expr.matches(tbond, config);
    }

    private IBond getMappedBond(IBond qbond, int[] mapping) {
        return target.getBond(target.getAtom(mapping[query.indexOf(qbond.getBegin())]),
                              target.getAtom(mapping[query.indexOf(qbond.getEnd())]));
    }

    private void swap(IBond[] tbonds, int i, int j) {
        IBond tmp = tbonds[i];
        tbonds[i] = tbonds[j];
        tbonds[j] = tmp;
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
        return queryMap.get(element.getStereoBond().getOther(query.getAtom(i)));
    }

    /**
     * Create an index of atoms for the provided {@code container}.
     *
     * @param container the container to index the atoms of
     * @return the index/lookup of atoms to the index they appear
     */
    private static Map<IAtom, Integer> indexAtoms(IAtomContainer container) {
        Map<IAtom, Integer> map = new HashMap<>(2*container.getAtomCount());
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
                int idx1 = (Integer) map.get(dbs.getStereoBond().getBegin());
                int idx2 = (Integer) map.get(dbs.getStereoBond().getEnd());
                elements[idx1] = element;
                elements[idx2] = element;
                types[idx2] = Type.Geometric;
                types[idx1] = Type.Geometric;
                indices[nElements++] = idx1;
                continue;
            }
            if (element instanceof Octahedral) {
                Octahedral oc = (Octahedral) element;
                int idx = (Integer) map.get(oc.getFocus());
                elements[idx] = element;
                types[idx] = Type.Octahedral;
                indices[nElements++] = idx;
                continue;
            }
            if (element instanceof SquarePlanar) {
                Octahedral oc = ((SquarePlanar) element).asOctahedral();
                int idx = (Integer) map.get(oc.getFocus());
                elements[idx] = oc;
                types[idx] = Type.Octahedral;
                indices[nElements++] = idx;
                continue;
            }
            if (element instanceof TrigonalBipyramidal) {
                TrigonalBipyramidal tbpy = (TrigonalBipyramidal) element;
                int idx = (Integer) map.get(tbpy.getFocus());
                elements[idx] = tbpy;
                types[idx] = Type.TrigonalBipyramidal;
                indices[nElements++] = idx;
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

    /**
     * Backwards compatible method from when we used GUAVA predicates.
     * @param ints atom index bijection
     * @return true/false
     * @see #test(int[])
     */
    public boolean apply(int[] ints) {
        return test(ints);
    }

    enum Type {
        Tetrahedral, Geometric, TrigonalBipyramidal, Octahedral;
    }
}
