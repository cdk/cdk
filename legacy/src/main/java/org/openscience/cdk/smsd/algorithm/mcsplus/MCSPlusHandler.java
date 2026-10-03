/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
 * MERCHANTABILITY or FITNESS FOR sourceAtom PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.mcsplus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Finds connected maximum common substructures through {@link MCSPlus}.
 * Results maximize the number of mapped atoms, then compatible common bonds;
 * bonds may be deleted from either input. All tied maximum mappings are retained.
 *
 * <p>Inputs are borrowed simple two-centre graphs. Atom and bond properties must
 * remain stable during a search. Ordinary atoms match by element; ordinary bonds
 * match by strict order/aromaticity when requested. Explicit query predicates
 * remain directional. Mapping-level stereochemistry and conformer geometry are
 * not checked by this handler.</p>
 *
 * <p>Result collections are read-only snapshots with source-to-target keys and
 * values. A timeout yields the best mappings found so far; consult
 * {@link org.openscience.cdk.smsd.global.TimeOut} on the searching thread before
 * treating a result as a certified maximum. An instance must not be used for
 * concurrent or nested searches.</p>
 *
 * <p>For containers already prepared by the caller:</p>
 * <pre>{@code
 * MCSPlusHandler handler = new MCSPlusHandler();
 * handler.set(new MolHandler(source, false, false),
 *             new MolHandler(target, false, false));
 * handler.searchMCS(true);
 * List<Map<Integer, Integer>> mappings = handler.getAllMapping();
 * boolean incomplete = org.openscience.cdk.smsd.global.TimeOut
 *         .getInstance().isTimeOutFlag();
 * }</pre>
 *
 * @see org.openscience.cdk.smsd.algorithm.vflib.VFlibMCSHandler
 * @cdk.threadnonsafe
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class MCSPlusHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private final List<Map<IAtom, IAtom>> allAtomMCS = new ArrayList<>();
    private final List<Map<Integer, Integer>> allMCS = new ArrayList<>();
    private IAtomContainer                     source       = null;
    private IAtomContainer                     target       = null;

    /**
     * Creates a handler with no inputs or results.
     */
    public MCSPlusHandler() {
    }

    /**
     * Borrows the molecules prepared by the supplied handlers.
     * Results from the preceding search remain available until the next search.
     *
     * @param source handler containing the source molecule
     * @param target handler containing the target molecule
     * @throws NullPointerException if a handler or its molecule is null;
     *         neither input is replaced in this case
     */
    @Override
    public synchronized void set(MolHandler source, MolHandler target) {
        IAtomContainer sourceMolecule = Objects.requireNonNull(
                Objects.requireNonNull(source, "source handler").getMolecule(), "source molecule");
        IAtomContainer targetMolecule = Objects.requireNonNull(
                Objects.requireNonNull(target, "target handler").getMolecule(), "target molecule");
        this.source = sourceMolecule;
        this.target = targetMolecule;
    }

    /**
     * Borrows a directional query and target without additional preparation.
     *
     * @param source query container whose predicates are applied to the target
     * @param target target container
     * @throws NullPointerException if either input is null;
     *         neither input is replaced in this case
     */
    @Override
    public void set(IQueryAtomContainer source, IAtomContainer target) {
        Objects.requireNonNull(source, "source query");
        Objects.requireNonNull(target, "target container");
        this.source = source;
        this.target = target;
    }

    /**
     * Replaces the stored results with the connected maximum common mappings.
     * The searching thread's timeout is cooperative; incomplete results are
     * accompanied by its timeout flag. A declared legacy {@code CDKException}
     * from the overlap operation is logged. Runtime validation and predicate
     * failures propagate to the caller.
     *
     * @param shouldMatchBonds whether ordinary bond order/aromaticity must match;
     *        explicit query bond predicates are always applied
     * @throws IllegalStateException if inputs have not been set
     * @throws IllegalArgumentException if an input is not a supported simple graph
     * @throws NullPointerException if required query objects or ordinary
     *         element-matching metadata are missing
     */
    @Override
    public synchronized void searchMCS(boolean shouldMatchBonds) {
        if (source == null || target == null) {
            throw new IllegalStateException("Set source and target before searching");
        }
        allAtomMCS.clear();
        allMCS.clear();
        try {
            for (List<Integer> pairs : new MCSPlus().getOverlaps(source, target, shouldMatchBonds)) {
                Map<Integer, Integer> indices = new TreeMap<>();
                Map<IAtom, IAtom> atoms = new HashMap<>();
                for (int i = 0; i < pairs.size(); i += 2) {
                    int sourceIndex = pairs.get(i);
                    int targetIndex = pairs.get(i + 1);
                    indices.put(sourceIndex, targetIndex);
                    atoms.put(source.getAtom(sourceIndex), target.getAtom(targetIndex));
                }
                allMCS.add(Collections.unmodifiableMap(indices));
                allAtomMCS.add(Collections.unmodifiableMap(atoms));
            }
        } catch (CDKException e) {
            LoggingToolFactory.createLoggingTool(MCSPlusHandler.class).warn("MCS search failed:", e);
        }
    }

    /**
     * Returns the index mappings from the most recent search.
     *
     * @return a read-only snapshot of read-only, zero-based source-to-target maps;
     *         empty before a search or when no nonempty overlap is found
     */
    @Override
    public synchronized List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(new ArrayList<>(allMCS));
    }

    /**
     * Returns the first stored index mapping, without ranking tied mappings.
     *
     * @return a read-only source-to-target map, or an empty map if no result exists
     */
    @Override
    public synchronized Map<Integer, Integer> getFirstMapping() {
        return allMCS.isEmpty() ? Collections.emptyMap() : allMCS.get(0);
    }

    /**
     * Returns the atom mappings from the most recent search.
     *
     * @return a read-only snapshot of read-only source-to-target maps;
     *         atom objects are borrowed from the inputs
     */
    @Override
    public synchronized List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(new ArrayList<>(allAtomMCS));
    }

    /**
     * Returns the first stored atom mapping, without ranking tied mappings.
     *
     * @return a read-only source-to-target map, or an empty map if no result exists
     */
    @Override
    public synchronized Map<IAtom, IAtom> getFirstAtomMapping() {
        return allAtomMCS.isEmpty() ? Collections.emptyMap() : allAtomMCS.get(0);
    }
}
