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
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.ConnectedComponents;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * A predicate for verifying component level grouping in query/target structure
 * matching. The grouping is used by SMARTS and is critical to querying
 * reactions. The grouping specifies that substructures in the query should
 * match to separate components in the target. The grouping specification is
 * indicated by an {@code int[]} array of length (|V(query)| + 1). The final
 * index indicates the maximum component group (in the query). A specification
 * of '0' indicates there are no grouping restrictions.
 *
 * <blockquote><pre>
 * // grouping is actually set by SMARTS parser but this shows how it's stored
 * query.setProperty(ComponentGrouping.KEY, grouping);
 *
 * IAtomContainer query, target;
 * Pattern        pattern = ...; // create pattern for query
 *
 * // filter for mappings which respect component grouping in the query
 * Iterables.filter(pattern.matchAll(target),
 *                  new ComponentGrouping(query, target));
 * </pre></blockquote>
 *
 * @author John May
 * @cdk.module isomorphism
 * @see Pattern
 */
public final class ComponentGrouping implements Predicate<int[]> {

    /**
     * Key indicates where the grouping should be store in the query
     * properties.
     */
    public static final String        KEY = "COMPONENT.GROUPING";

    /** The required  (query) and the targetComponents of the target. */
    private final int[]               queryComponents, targetComponents;

    private final int maxComponentIdx;

    /**
     * Create a predicate to match components for the provided query and target.
     * The target is converted to an adjacency list ({@link
     * GraphUtil#toAdjList(IAtomContainer)}) and the query components extracted
     * from the property {@link #KEY} in the query.
     *
     * @param query  query structure
     * @param target target structure
     */
    public ComponentGrouping(IAtomContainer query, IAtomContainer target) {
        this(query.getProperty(KEY, int[].class),
             query.getProperty(KEY) != null ? determineComponents(target) : null);
    }

    private static int[] determineComponents(IAtomContainer target) {
        int[] components = null;
        // no atoms -> no components
        if (target.isEmpty())
            components = new int[0];
        // defined by reaction grouping
        if (components == null && target.getAtom(0).getProperty(CDKConstants.REACTION_GROUP) != null) {
            components = new int[target.getAtomCount()];
            for (int i = 0; i < target.getAtomCount(); i++) {
                Integer grp = target.getAtom(i).getProperty(CDKConstants.REACTION_GROUP);
                if (grp == null) {
                    components = null;
                    break;
                }
                components[i] = grp;
            }
        }
        // calculate from connection table
        if (components == null)
            components = new ConnectedComponents(GraphUtil.toAdjList(target)).components();
        return components;
    }

    /**
     * Create a predicate to match components for the provided query (grouping)
     * and target (connected components).
     *
     * @param grouping  query grouping
     * @param targetComponents connected component of the target
     */
    public ComponentGrouping(int[] grouping, int[] targetComponents) {
        this.queryComponents  = grouping;
        this.targetComponents = targetComponents;
        int max = 0;
        if (targetComponents != null) {
            for (int i = 0; i < targetComponents.length; i++)
                if (targetComponents[i] > max)
                    max = targetComponents[i];
        }
        this.maxComponentIdx = max;
    }

    /**
     * Does the mapping respected the component grouping specified by the
     * query.
     *
     * @param mapping a permutation of the query vertices
     * @return the mapping preserves the specified grouping
     */
    @Override
    public boolean apply(final int[] mapping) {

        // no grouping required
        if (queryComponents == null) return true;

        // bidirectional map of query/target components, last index
        // of query components holds the count
        int[] usedBy = new int[maxComponentIdx+1];
        int[] usedIn = new int[queryComponents[mapping.length] + 1];

        // verify we don't have any collisions
        for (int v = 0; v < mapping.length; v++) {
            if (queryComponents[v] == 0) continue;

            int w = mapping[v];

            int queryComponent = queryComponents[v];
            int targetComponent = targetComponents[w];

            // is the target component already used by a query component?
            if (usedBy[targetComponent] == 0)
                usedBy[targetComponent] = queryComponent;
            else if (usedBy[targetComponent] != queryComponent) return false;

            // is the query component already used in a target component?
            if (usedIn[queryComponent] == 0)
                usedIn[queryComponent] = targetComponent;
            else if (usedIn[queryComponent] != targetComponent) return false;

        }

        return true;
    }
}
