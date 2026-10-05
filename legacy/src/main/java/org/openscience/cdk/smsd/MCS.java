/* Copyright (C) 2026  Syed Asad Rahman <s9asad@gmail.com>
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
package org.openscience.cdk.smsd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.openscience.cdk.exception.Intractable;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;

/**
 * Finds the maximum common substructure (MCS) of a query and a target
 * molecule: the connected common substructure with the most atoms and, of
 * those, the most common bonds. Bonds may be left out on either side.
 * <br><br>
 * Options are set with the {@code with...} methods, each of which returns a
 * new instance. An instance keeps no other state, so it can be used for many
 * targets and shared between threads. The time limit applies to each search.
 * <br><br>
 * Usage:
 * <pre>{@code
 * MCS mcs = MCS.find(query)
 *              .withTimeout(10, TimeUnit.SECONDS);
 * int[] mapping = mcs.match(target);
 * for (int i = 0; i < mapping.length; i++) {
 *     if (mapping[i] >= 0) {
 *         // query.getAtom(i) is mapped to target.getAtom(mapping[i])
 *     }
 * }
 * // every maximum mapping
 * for (int[] p : mcs.matchAll(target)) {
 * }
 * }</pre>
 * By default atoms match by element symbol, and bonds match if both are
 * aromatic, or both are not aromatic and have the same order.
 * {@link #withMatching(Expr.Type...)} matches with the expressions of
 * {@link QueryAtomContainer#create(IAtomContainer, Expr.Type...)} instead.
 * Query atoms and bonds ({@link IQueryAtom}, {@link IQueryBond}) in the query
 * are matched by their own expressions; the target should be a molecule, not
 * a query. Aromaticity and ring flags are used as they are set on the
 * molecules, and stereochemistry is not checked.
 * <br><br>
 * The search is exact. It can take a long time for some large molecules with
 * many nearly as good mappings, so a time limit is recommended.
 * <br><br>
 * <b>References</b>
 * <ul>
 *     <li>{@cdk.cite SMSD2009}</li>
 *     <li>{@cdk.cite Cordella04}</li>
 * </ul>
 *
 * @author Syed Asad Rahman
 * @cdk.keyword maximum common substructure
 * @cdk.keyword MCS
 * @see Isomorphism
 */
public final class MCS {

    private final IAtomContainer query;
    // expression types for QueryAtomContainer.create, null for the default matching
    private final Expr.Type[]    matching;
    // time limit in nanoseconds, -1 for none
    private final long           timeLimit;

    private MCS(IAtomContainer query, Expr.Type[] matching, long timeLimit) {
        this.query = query;
        this.matching = matching;
        this.timeLimit = timeLimit;
    }

    /**
     * Create an MCS search for the {@code query} with the default matching
     * and no time limit.
     *
     * @param query the query molecule
     * @return an MCS search for the query
     */
    public static MCS find(IAtomContainer query) {
        return new MCS(Objects.requireNonNull(query, "query"), null, -1);
    }

    /**
     * Match atoms and bonds with the expressions that
     * {@link QueryAtomContainer#create(IAtomContainer, Expr.Type...)} makes
     * from the query and {@code opts}, at the start of each search. For
     * example {@code withMatching(ELEMENT)} ignores bond orders, and
     * {@code withMatching(ELEMENT, IS_IN_RING)} also maps ring atoms and bonds
     * of the query only to ring atoms and bonds. Without any types every atom
     * and bond matches. {@code STEREOCHEMISTRY} has no effect, as
     * stereochemistry is not checked.
     *
     * @param opts the expression types to match
     * @return a new MCS search with this matching
     * @throws IllegalArgumentException the query has query atoms or bonds,
     *                                  which are matched by their own
     *                                  expressions
     */
    public MCS withMatching(Expr.Type... opts) {
        if (hasQueryFeatures(query)) {
            throw new IllegalArgumentException("A query molecule is matched by its own expressions");
        }
        Expr.Type[] copy = opts.clone();
        for (Expr.Type opt : copy) {
            Objects.requireNonNull(opt, "opts");
        }
        return new MCS(query, copy, timeLimit);
    }

    /**
     * Stop each search that runs longer than the given time, see
     * {@link #match(IAtomContainer)}. The limit is checked as the search
     * runs, so a search can take slightly longer; building the query and
     * target graphs before the search starts is not counted.
     *
     * @param time the time limit, greater than zero
     * @param unit the unit of the time limit
     * @return a new MCS search with this time limit
     * @throws IllegalArgumentException the time limit is not greater than zero
     */
    public MCS withTimeout(long time, TimeUnit unit) {
        if (time <= 0) {
            throw new IllegalArgumentException("The time limit must be greater than zero: " + time);
        }
        return new MCS(query, matching, unit.toNanos(time));
    }

    /**
     * Find a maximum common substructure of the query and the {@code target}.
     * The mapping has one entry per query atom: the index of the target atom
     * it is mapped to, or -1 if the atom is not in the common substructure.
     * If the molecules have no atom in common an empty array is returned.
     *
     * @param target the target molecule
     * @return the mapping from the query to the target atoms
     * @throws Intractable the time limit was reached or the thread was
     *                     interrupted
     */
    public int[] match(IAtomContainer target) throws Intractable {
        List<int[]> mappings = search(target, 1);
        return mappings.isEmpty() ? new int[0] : mappings.get(0);
    }

    /**
     * Find every maximum common substructure mapping of the query and the
     * {@code target}, up to 1000 of them; symmetric molecules can have far
     * more. All have the same number of atoms and common bonds.
     *
     * @param target the target molecule
     * @return the mappings from the query to the target atoms, see
     *         {@link #match(IAtomContainer)}; an unmodifiable list, empty if
     *         the molecules have no atom in common
     * @throws Intractable the time limit was reached or the thread was
     *                     interrupted
     */
    public List<int[]> matchAll(IAtomContainer target) throws Intractable {
        return Collections.unmodifiableList(search(target, VFMCSMapper.MAX_MAPPINGS));
    }

    private List<int[]> search(IAtomContainer target, int maxMappings) throws Intractable {
        Objects.requireNonNull(target, "target");
        IAtomContainer mol = matching != null ? QueryAtomContainer.create(query, matching) : query;
        // the smaller molecule is the faster query, the mappings are turned round afterwards
        boolean swap = matching == null && query.getAtomCount() > target.getAtomCount()
                && !hasQueryFeatures(query) && !hasQueryFeatures(target);
        IQuery compiled = new QueryCompiler(swap ? target : mol, true).compile();
        VFMCSMapper mapper = new VFMCSMapper(compiled, timeLimit, maxMappings);
        List<Map<INode, IAtom>> solutions = mapper.getMaps(swap ? query : target);
        if (mapper.isTimedOut()) {
            if (Thread.currentThread().isInterrupted()) {
                throw new Intractable("MCS search was interrupted");
            }
            throw Intractable.timeout("MCS search", TimeUnit.NANOSECONDS.toMillis(timeLimit));
        }
        List<int[]> mappings = new ArrayList<>();
        for (Map<INode, IAtom> solution : solutions) {
            if (solution.isEmpty()) {
                continue;
            }
            int[] mapping = new int[query.getAtomCount()];
            Arrays.fill(mapping, -1);
            // when swapped, the compiled atoms are target atoms and the mapped atoms query atoms
            for (Map.Entry<INode, IAtom> entry : solution.entrySet()) {
                IAtom compiledAtom = compiled.getAtom(entry.getKey());
                IAtom mappedAtom = entry.getValue();
                if (swap) {
                    mapping[query.indexOf(mappedAtom)] = target.indexOf(compiledAtom);
                } else {
                    mapping[mol.indexOf(compiledAtom)] = target.indexOf(mappedAtom);
                }
            }
            mappings.add(mapping);
        }
        return mappings;
    }

    private static boolean hasQueryFeatures(IAtomContainer mol) {
        if (mol instanceof IQueryAtomContainer) {
            return true;
        }
        for (IAtom atom : mol.atoms()) {
            if (atom instanceof IQueryAtom) {
                return true;
            }
        }
        for (IBond bond : mol.bonds()) {
            if (bond instanceof IQueryBond) {
                return true;
            }
        }
        return false;
    }
}
