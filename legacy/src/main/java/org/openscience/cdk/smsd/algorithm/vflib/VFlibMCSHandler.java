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
 * You should have received commonAtomList copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.algorithm.vflib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.INode;
import org.openscience.cdk.smsd.algorithm.vflib.interfaces.IQuery;
import org.openscience.cdk.smsd.algorithm.vflib.map.VFMCSMapper;
import org.openscience.cdk.smsd.algorithm.vflib.query.QueryCompiler;
import org.openscience.cdk.smsd.interfaces.AbstractMCSAlgorithm;
import org.openscience.cdk.smsd.interfaces.IMCSBase;
import org.openscience.cdk.smsd.tools.MolHandler;

/**
 * Finds a connected maximum common substructure, maximizing the number of atoms
 * and then the number of compatible common bonds.
 * Bonds may be deleted from either graph; all tied optimum mappings are stored.
 *
 * <p>Inputs are borrowed simple two-centre graphs and must remain stable while
 * searching. Ordinary atoms match by element and ordinary bonds match by strict
 * order/aromaticity when enabled. Explicit query predicates remain directional.
 * This handler does not apply mapping-level stereochemical filters or compare
 * conformer geometry.</p>
 *
 * <p>Results are read-only snapshots from source to target. A cooperative timeout
 * returns the best mappings found so far and sets the searching thread's
 * {@link org.openscience.cdk.smsd.global.TimeOut} flag. Instances are mutable and
 * must not be shared by concurrent or nested searches.</p>
 *
 * @see org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlusHandler
 * @cdk.threadnonsafe
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class VFlibMCSHandler extends AbstractMCSAlgorithm implements IMCSBase {

    private final List<Map<IAtom, IAtom>> allAtomMCS = new ArrayList<>();
    private final List<Map<Integer, Integer>> allMCS = new ArrayList<>();
    private IAtomContainer source;
    private IAtomContainer target;
    private boolean bondMatchFlag;

    /** Creates a handler with no inputs or results. */
    public VFlibMCSHandler() {
    }

    /**
     * Replaces the stored results with connected maximum common mappings.
     * Query/target preparation precedes the captured search budget. Predicate
     * failures propagate to the caller; deadline checks resume when callbacks
     * return. A timed-out result is not a certified optimum.
     *
     * @param bondTypeMatch whether ordinary bond order/aromaticity must match;
     *        explicit query bond predicates are always applied
     * @throws IllegalStateException if inputs have not been set
     * @throws IllegalArgumentException if an input is not a supported simple graph
     * @throws NullPointerException if required query objects or ordinary
     *         element-matching metadata are missing
     */
    @Override
    public void searchMCS(boolean bondTypeMatch) {
        if (source == null || target == null) {
            throw new IllegalStateException("Set source and target before searching");
        }
        allAtomMCS.clear();
        allMCS.clear();
        setBondMatchFlag(bondTypeMatch);

        // Query predicates belong on the query side even when it is larger.
        boolean reverse = source.getAtomCount() > target.getAtomCount()
                && !hasPredicates(source) && !hasPredicates(target);
        IAtomContainer queryMolecule = reverse ? target : source;
        IAtomContainer targetMolecule = reverse ? source : target;
        IQuery query = new QueryCompiler(queryMolecule, bondTypeMatch).compile();
        for (Map<INode, IAtom> solution : new VFMCSMapper(query).getMaps(targetMolecule)) {
            if (solution.isEmpty()) continue;
            Map<IAtom, IAtom> atomMapping = new HashMap<>();
            Map<Integer, Integer> indexMapping = new TreeMap<>();
            for (Map.Entry<INode, IAtom> entry : solution.entrySet()) {
                IAtom queryAtom = query.getAtom(entry.getKey());
                IAtom targetAtom = entry.getValue();
                IAtom sourceAtom = reverse ? targetAtom : queryAtom;
                IAtom productAtom = reverse ? queryAtom : targetAtom;
                atomMapping.put(sourceAtom, productAtom);
                indexMapping.put(source.indexOf(sourceAtom), target.indexOf(productAtom));
            }
            allAtomMCS.add(Collections.unmodifiableMap(atomMapping));
            allMCS.add(Collections.unmodifiableMap(indexMapping));
        }
    }

    private boolean hasPredicates(IAtomContainer molecule) {
        if (molecule instanceof IQueryAtomContainer) return true;
        for (IAtom atom : molecule.atoms()) {
            if (AtomRef.deref(atom) instanceof IQueryAtom) return true;
        }
        for (IBond bond : molecule.bonds()) {
            if (BondRef.deref(bond) instanceof IQueryBond) return true;
        }
        return false;
    }

    /**
     * Borrows the molecules prepared by the supplied handlers.
     * Results from the preceding search remain available until the next search.
     *
     * @param reactant handler containing the source molecule
     * @param product handler containing the target molecule
     * @throws NullPointerException if a handler or its molecule is null;
     *         neither input is replaced in this case
     */
    @Override
    public void set(MolHandler reactant, MolHandler product) {
        IAtomContainer sourceMolecule = Objects.requireNonNull(
                Objects.requireNonNull(reactant, "source handler").getMolecule(), "source molecule");
        IAtomContainer targetMolecule = Objects.requireNonNull(
                Objects.requireNonNull(product, "target handler").getMolecule(), "target molecule");
        source = sourceMolecule;
        target = targetMolecule;
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
     * Returns the atom mappings from the most recent search.
     *
     * @return a read-only snapshot of read-only source-to-target maps;
     *         atom objects are borrowed from the inputs
     */
    @Override
    public List<Map<IAtom, IAtom>> getAllAtomMapping() {
        return Collections.unmodifiableList(new ArrayList<>(allAtomMCS));
    }

    /**
     * Returns the index mappings from the most recent search.
     *
     * @return a read-only snapshot of read-only, zero-based source-to-target maps;
     *         empty before a search or when no nonempty overlap is found
     */
    @Override
    public List<Map<Integer, Integer>> getAllMapping() {
        return Collections.unmodifiableList(new ArrayList<>(allMCS));
    }

    /**
     * Returns the first stored atom mapping, without ranking tied mappings.
     *
     * @return a read-only source-to-target map, or an empty map if no result exists
     */
    @Override
    public Map<IAtom, IAtom> getFirstAtomMapping() {
        return allAtomMCS.isEmpty() ? Collections.emptyMap() : allAtomMCS.get(0);
    }

    /**
     * Returns the first stored index mapping, without ranking tied mappings.
     *
     * @return a read-only source-to-target map, or an empty map if no result exists
     */
    @Override
    public Map<Integer, Integer> getFirstMapping() {
        return allMCS.isEmpty() ? Collections.emptyMap() : allMCS.get(0);
    }

    /**
     * Returns the last requested ordinary bond-matching setting.
     *
     * @return whether ordinary bond order/aromaticity is matched
     */
    public boolean isBondMatchFlag() {
        return bondMatchFlag;
    }

    /**
     * Sets the reported ordinary bond-matching setting.
     * The argument to the next {@link #searchMCS(boolean)} replaces this value.
     *
     * @param shouldMatchBonds whether ordinary bond order/aromaticity should match
     */
    public void setBondMatchFlag(boolean shouldMatchBonds) {
        this.bondMatchFlag = shouldMatchBonds;
    }
}
