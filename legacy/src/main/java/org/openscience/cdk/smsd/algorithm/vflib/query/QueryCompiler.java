/* Copyright (C) 2009-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 */
package org.openscience.cdk.smsd.algorithm.vflib.query;

import java.util.Objects;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.DefaultVFBondMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.VFAtomMatcher;
import org.openscience.cdk.smsd.algorithm.matchers.VFBondMatcher;
import org.openscience.cdk.smsd.algorithm.vflib.builder.VFQueryBuilder;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQueryCompiler;

/**
 * Creates an MCS/substructure query from a simple undirected graph. Atoms must
 * be unique, and each bond must connect two distinct atoms in the container.
 * Self-loops, parallel bonds and bonds with any other number of endpoints are
 * rejected. Query atom and bond predicates are preserved, including predicates
 * in ordinary containers and reference wrappers.
 *
 * <p>The compiled graph borrows its atoms and bonds; callers must not mutate
 * the container or its chemistry during compilation or matching. Callers must
 * prepare chemical properties needed by their predicates, such as aromaticity,
 * ring membership and hydrogen counts. Compilation does not supply missing
 * chemistry or apply {@code Pattern} stereo/component/reaction-map filters.
 * No synchronization is provided for mutable inputs or predicate state.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class QueryCompiler implements IQueryCompiler {

    private final IAtomContainer molecule;
    private final boolean shouldMatchBonds;

    /**
     * Construct a compiler for the supplied container.
     * @param molecule container to compile
     * @param shouldMatchBonds whether to compare ordinary bond chemistry;
     *                         query bond predicates are always evaluated
     * @throws NullPointerException if the container is null
     */
    public QueryCompiler(IAtomContainer molecule, boolean shouldMatchBonds) {
        this.molecule = Objects.requireNonNull(molecule, "Query container must not be null");
        this.shouldMatchBonds = shouldMatchBonds;
    }

    /**
     * Construct a compiler for the supplied query container.
     * @param molecule query container to compile
     * @throws NullPointerException if the container is null
     */
    public QueryCompiler(IQueryAtomContainer molecule) {
        this(molecule, true);
    }

    /**
     * Compile a new graph, retaining the container's atom and bond payloads.
     * @return newly compiled query with directional atom and bond predicates
     * @throws NullPointerException if an atom, bond or bond endpoint is null
     * @throws IllegalArgumentException if atoms repeat or the bonds do not
     *                                  form a simple graph over the container
     */
    @Override
    public IQuery compile() {
        VFQueryBuilder result = new VFQueryBuilder();
        for (IAtom atom : molecule.atoms()) {
            Objects.requireNonNull(atom, "Query atom must not be null");
            IAtom queryAtom = AtomRef.deref(atom);
            VFAtomMatcher matcher = queryAtom instanceof IQueryAtom
                    ? new DefaultVFAtomMatcher((IQueryAtom) queryAtom,
                            molecule instanceof IQueryAtomContainer ? (IQueryAtomContainer) molecule : null)
                    : new DefaultVFAtomMatcher(molecule, atom, shouldMatchBonds);
            result.addNode(matcher, atom);
        }
        for (IBond bond : molecule.bonds()) {
            Objects.requireNonNull(bond, "Query bond must not be null");
            if (bond.getAtomCount() != 2) {
                throw new IllegalArgumentException("Query bonds must have exactly two endpoints");
            }
            INode begin = result.getNode(Objects.requireNonNull(bond.getBegin(),
                    "Query bond endpoint must not be null"));
            INode end = result.getNode(Objects.requireNonNull(bond.getEnd(),
                    "Query bond endpoint must not be null"));
            if (begin == null || end == null) {
                throw new IllegalArgumentException("Query bond endpoints must belong to the query container");
            }
            IBond queryBond = BondRef.deref(bond);
            VFBondMatcher matcher = queryBond instanceof IQueryBond
                    ? new DefaultVFBondMatcher((IQueryBond) queryBond)
                    : new DefaultVFBondMatcher(molecule, bond, shouldMatchBonds);
            result.connect(begin, end, matcher);
        }
        return result;
    }
}
