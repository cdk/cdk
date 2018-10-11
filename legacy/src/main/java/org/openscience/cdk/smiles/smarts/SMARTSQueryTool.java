/* Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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
package org.openscience.cdk.smiles.smarts;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.aromaticity.ElectronDonation;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.ComponentGrouping;
import org.openscience.cdk.isomorphism.SmartsStereoMatch;
import org.openscience.cdk.isomorphism.Ullmann;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.SmartsMatchers;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.smiles.smarts.parser.TokenMgrError;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class provides a easy to use wrapper around SMARTS matching functionality.  User code that wants to do
 * SMARTS matching should use this rather than using SMARTSParser (and UniversalIsomorphismTester) directly. Example
 * usage would be
 * 
 * <pre>{@code
 * SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 * IAtomContainer atomContainer = sp.parseSmiles("CC(=O)OC(=O)C");
 * SMARTSQueryTool querytool = new SMARTSQueryTool("O=CO");
 * boolean status = querytool.matches(atomContainer);
 * if (status) {
 *    int nmatch = querytool.countMatches();
 *    List mappings = querytool.getMatchingAtoms();
 *    for (int i = 0; i < nmatch; i++) {
 *       List atomIndices = (List) mappings.get(i);
 *    }
 * }
 * }</pre>
 * <h3>SMARTS Extensions</h3>
 * 
 * Currently the CDK supports the following SMARTS symbols, that are not described in the Daylight specification.
 * However they are supported by other packages and are noted as such.
 * 
 * <table border=1 cellpadding=3><caption>Table 1 - Supported Extensions</caption> <thead>
 * <tr> <th>Symbol</th><th>Meaning</th><th>Default</th><th>Notes</th> </tr>
 * </thead> <tbody> <tr> <td>Gx</td><td>Periodic group number</td><td>None</td><td>x must be specified and must be a
 * number between 1 and 18. This symbol is supported by the MOE SMARTS implementation</td> <tr> <td>#X</td><td>Any
 * non-carbon heavy element</td><td>None</td><td>This symbol is supported by the MOE SMARTS implementation</td> </tr>
 * <tr> <td>^x</td><td>Any atom with the a specified hybridization state</td><td>None</td><td>x must be specified and
 * should be between 1 and 8 (inclusive), corresponding to SP1, SP2, SP3, SP3D1, SP3D2 SP3D3, SP3D4 and SP3D5. Supported
 * by the OpenEye SMARTS implementation</td> </tr> </tbody> </table>
 * 
 * <h3>Notes</h3> <ul> <li>As <a href="http://sourceforge.net/mailarchive/message.php?msg_name=4964F605.1070502%40emolecules.com">described</a>
 * by Craig James the <code>h&lt;n&gt;</code> SMARTS pattern should not be used. It was included in the Daylight spec
 * for backwards compatibility. To match hydrogens, use the <code>H&lt;n&gt;</code> pattern.</li> <li>The wild card
 * pattern (<code>*</code>) will not match hydrogens (explicit or implicit) unless an isotope is specified. In other
 * words, <code>*</code> gives two hits against <code>C[2H]</code> but 1 hit against <code>C[H]</code>. This also means
 * that it gives no hits against <code>[H][H]</code>. This is contrary to what is shown by Daylights <a
 * href="http://www.daylight.com/daycgi_tutorials/depictmatch.cgi">depictmatch</a> service, but is based on this <a
 * href="https://sourceforge.net/mailarchive/message.php?msg_name=4964FF9D.3040004%40emolecules.com">discussion</a>. A
 * work around to get <code>*</code> to match <code>[H][H]</code> is to write it in the form <code>[1H][1H]</code>.
 * 
 * It's not entirely clear what the behavior of * should be with respect to hydrogens. it is possible that the code will
 * be updated so that <code>*</code> will not match <i>any</i> hydrogen in the future.</li> <li>The
 * org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector only considers single rings and two fused non-spiro
 * rings. As a result, it does not properly detect aromaticity in polycyclic systems such as
 * <code>[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24</code>. Thus SMARTS patterns that depend on proper aromaticity
 * detection may not work correctly in such polycyclic systems</li> </ul>
 *
 * @author Rajarshi Guha
 * @cdk.created 2007-04-08
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS
 * @cdk.keyword substructure search
 * @deprecated use {@link org.openscience.cdk.smarts.SmartsPattern}
 */
@Deprecated
public class SMARTSQueryTool {

    private static ILoggingTool logger        = LoggingToolFactory.createLoggingTool(SMARTSQueryTool.class);
    private String              smarts;
    private IAtomContainer      atomContainer = null;
    private QueryAtomContainer  query         = null;
    private List<int[]>         mappings;

    /**
     * Defines which set of rings to define rings in the target.
     */
    private enum RingSet {

        /**
         * Smallest Set of Smallest Rings (or Minimum Cycle Basis - but not
         * strictly the same). Defines what is typically thought of as a 'ring'
         * however the non-uniqueness leads to ambiguous matching.
         */
        SmallestSetOfSmallestRings {

            @Override
            IRingSet ringSet(IAtomContainer m) {
                return Cycles.sssr(m).toRingSet();
            }
        },

        /**
         * Intersect of all Minimum Cycle Bases (or SSSR) and thus is a subset.
         * The set is unique but may excludes rings (e.g. from bridged systems).
         */
        EssentialRings {

            @Override
            IRingSet ringSet(IAtomContainer m) {
                return Cycles.essential(m).toRingSet();
            }
        },

        /**
         * Union of all Minimum Cycle Bases (or SSSR) and thus is a superset.
         * The set is unique but may include more rings then is necessary.
         */
        RelevantRings {

            @Override
            IRingSet ringSet(IAtomContainer m) {
                return Cycles.relevant(m).toRingSet();
            }
        };

        /**
         * Compute a ring set for a molecule.
         *
         * @param m molecule
         * @return the ring set for the molecule
         */
        abstract IRingSet ringSet(IAtomContainer m);
    }

    /** Which short cyclic set should be used. */
    private RingSet                  ringSet         = RingSet.EssentialRings;

    private final IChemObjectBuilder builder;

    /**
     * Aromaticity perception - dealing with SMARTS we should use the Daylight
     * model. This can be set to a different model using {@link #setAromaticity(Aromaticity)}.
     */
    private Aromaticity              aromaticity     = new Aromaticity(ElectronDonation.daylight(),
                                                             Cycles.allOrVertexShort());

    /**
     * Logical flag indicates whether the aromaticity model should be skipped.
     * Generally this should be left as false to ensure the structures being
     * matched are all treated the same. The flag can however be turned off if
     * the molecules being tests are known to all have the same aromaticity
     * model.
     */
    private boolean                  skipAromaticity = false;

    // a simplistic cache to store parsed SMARTS queries
    private int                      MAX_ENTRIES     = 20;
    Map<String, QueryAtomContainer>  cache           = new LinkedHashMap<String, QueryAtomContainer>(MAX_ENTRIES + 1,
                                                             .75F, true) {

                                                         @Override
                                                         public boolean removeEldestEntry(Map.Entry eldest) {
                                                             return size() > MAX_ENTRIES;
                                                         }
                                                     };

    /**
     * Create a new SMARTS query tool for the specified SMARTS string. Query
     * objects will contain a reference to the specified {@link
     * IChemObjectBuilder}.
     *
     * @param smarts SMARTS query string
     * @throws IllegalArgumentException if the SMARTS string can not be handled
     */
    public SMARTSQueryTool(String smarts, IChemObjectBuilder builder) {
        this.builder = builder;
        this.smarts = smarts;
        try {
            initializeQuery();
        } catch (TokenMgrError error) {
            throw new IllegalArgumentException("Error parsing SMARTS", error);
        } catch (CDKException error) {
            throw new IllegalArgumentException("Error parsing SMARTS", error);
        }
    }

    /**
     * Set the maximum size of the query cache.
     *
     * @param maxEntries The maximum number of entries
     */
    public void setQueryCacheSize(int maxEntries) {
        MAX_ENTRIES = maxEntries;
    }

    /**
     * Indicates that ring properties should use the Smallest Set of Smallest
     * Rings. The set is not unique and may lead to ambiguous matches.
     * @see #useEssentialRings()
     * @see #useRelevantRings()
     */
    public void useSmallestSetOfSmallestRings() {
        this.ringSet = RingSet.SmallestSetOfSmallestRings;
    }

    /**
     * Indicates that ring properties should use the Relevant Rings. The set is
     * unique and includes all of the SSSR but may be exponential in size.
     *
     * @see #useSmallestSetOfSmallestRings()
     * @see #useEssentialRings()
     */
    public void useRelevantRings() {
        this.ringSet = RingSet.RelevantRings;
    }

    /**
     * Indicates that ring properties should use the Essential Rings (default).
     * The set is unique but only includes a subset of the SSSR.
     *
     * @see #useSmallestSetOfSmallestRings()
     * @see #useEssentialRings()
     */
    public void useEssentialRings() {
        this.ringSet = RingSet.EssentialRings;
    }

    /**
     * Set the aromaticity perception to use. Different aromaticity models
     * may required certain attributes to be set (e.g. atom typing). These
     * will not be automatically configured and should be preset before matching.
     *
     * <blockquote><pre>
     * SMARTSQueryTool sqt = new SMARTSQueryTool(...);
     * sqt.setAromaticity(new Aromaticity(ElectronDonation.cdk(),
     *                                    Cycles.cdkAromaticSet));
     * for (IAtomContainer molecule : molecules) {
     *
     *     // CDK Aromatic model needs atom types
     *     AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
     *
     *     sqt.matches(molecule);
     * }
     * </pre></blockquote>
     *
     * @param aromaticity the new aromaticity perception
     * @see ElectronDonation
     * @see Cycles
     */
    public void setAromaticity(Aromaticity aromaticity) {
        this.aromaticity = checkNotNull(aromaticity, "aromaticity was not provided");
    }

    /**
     * Returns the current SMARTS pattern being used.
     *
     * @return The SMARTS pattern
     */
    public String getSmarts() {
        return smarts;
    }

    /**
     * Set a new SMARTS pattern.
     *
     * @param smarts The new SMARTS pattern
     * @throws CDKException if there is an error in parsing the pattern
     */
    public void setSmarts(String smarts) throws CDKException {
        this.smarts = smarts;
        initializeQuery();
    }

    /**
     * Perform a SMARTS match and check whether the query is present in the target molecule.  This function simply
     * checks whether the query pattern matches the specified molecule. However the function will also, internally, save
     * the mapping of query atoms to the target molecule
     * 
     * <b>Note</b>: This method performs a simple caching scheme, by comparing the current molecule to the previous
     * molecule by reference. If you repeatedly match different SMARTS on the same molecule, this method will avoid
     * initializing ( ring perception, aromaticity etc.) the molecule each time. If however, you modify the molecule
     * between such multiple matchings you should use the other form of this method to force initialization.
     *
     * @param atomContainer The target moleculoe
     * @return true if the pattern is found in the target molecule, false otherwise
     * @throws CDKException if there is an error in ring, aromaticity or isomorphism perception
     * @see #getMatchingAtoms()
     * @see #countMatches()
     * @see #matches(org.openscience.cdk.interfaces.IAtomContainer, boolean)
     */
    public boolean matches(IAtomContainer atomContainer) throws CDKException {
        return matches(atomContainer, false);
    }

    /**
     * Perform a SMARTS match and check whether the query is present in the target molecule.  This function simply
     * checks whether the query pattern matches the specified molecule. However the function will also, internally, save
     * the mapping of query atoms to the target molecule
     *
     * @param atomContainer       The target moleculoe
     * @param forceInitialization If true, then the molecule is initialized (ring perception, aromaticity etc). If
     *                            false, the molecule is only initialized if it is different (in terms of object
     *                            reference) than one supplied in a previous call to this method.
     * @return true if the pattern is found in the target molecule, false otherwise
     * @throws CDKException if there is an error in ring, aromaticity or isomorphism perception
     * @see #getMatchingAtoms()
     * @see #countMatches()
     * @see #matches(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public boolean matches(IAtomContainer atomContainer, boolean forceInitialization) throws CDKException {

        if (this.atomContainer == atomContainer) {
            if (forceInitialization) initializeMolecule();
        } else {
            this.atomContainer = atomContainer;
            initializeMolecule();
        }

        // lets see if we have a single atom query
        if (query.getAtomCount() == 1) {
            // lets get the query atom
            IQueryAtom queryAtom = (IQueryAtom) query.getAtom(0);

            mappings = new ArrayList<int[]>();
            for (int i = 0; i < atomContainer.getAtomCount(); i++) {
                if (queryAtom.matches(atomContainer.getAtom(i))) {
                    mappings.add(new int[]{i});
                }
            }
        } else {
            mappings = FluentIterable.from(VentoFoggia.findSubstructure(query).matchAll(atomContainer))
                    .filter(new SmartsStereoMatch(query, atomContainer))
                    .filter(new ComponentGrouping(query, atomContainer)).toList();
        }

        return !mappings.isEmpty();
    }

    /**
     * Returns the number of times the pattern was found in the target molecule.  This function should be called
     * after {@link #matches(org.openscience.cdk.interfaces.IAtomContainer)}. If not, the results may be undefined.
     *
     * @return The number of times the pattern was found in the target molecule
     */
    public int countMatches() {
        return mappings.size();
    }

    /**
     * Get the atoms in the target molecule that match the query pattern.  Since there may be multiple matches, the
     * return value is a List of List objects. Each List object contains the indices of the atoms in the target
     * molecule, that match the query pattern
     *
     * @return A List of List of atom indices in the target molecule
     */
    public List<List<Integer>> getMatchingAtoms() {
        List<List<Integer>> matched = new ArrayList<List<Integer>>(mappings.size());
        for (int[] mapping : mappings)
            matched.add(Ints.asList(mapping));
        return matched;
    }

    /**
     * Get the atoms in the target molecule that match the query pattern.  Since there may be multiple matches, the
     * return value is a List of List objects. Each List object contains the unique set of indices of the atoms in the
     * target molecule, that match the query pattern
     *
     * @return A List of List of atom indices in the target molecule
     */
    public List<List<Integer>> getUniqueMatchingAtoms() {
        List<List<Integer>> matched = new ArrayList<List<Integer>>(mappings.size());
        Set<BitSet> atomSets = Sets.newHashSetWithExpectedSize(mappings.size());
        for (int[] mapping : mappings) {
            BitSet atomSet = new BitSet();
            for (int x : mapping)
                atomSet.set(x);
            if (atomSets.add(atomSet)) matched.add(Ints.asList(mapping));
        }
        return matched;
    }

    /**
     * Prepare the target molecule for analysis.  We perform ring perception and aromaticity detection and set up
     * the appropriate properties. Right now, this function is called each time we need to do a query and this is
     * inefficient.
     *
     * @throws CDKException if there is a problem in ring perception or aromaticity detection, which is usually related
     *                      to a timeout in the ring finding code.
     */
    private void initializeMolecule() throws CDKException {

        // initialise required invariants - the query has ISINRING set if
        // the query contains ring queries [R?] [r?] [x?] etc.
        SmartsMatchers.prepare(atomContainer, true);

        // providing skip aromaticity has not be set apply the desired
        // aromaticity model
        try {
            if (!skipAromaticity) {
                aromaticity.apply(atomContainer);
            }
        } catch (CDKException e) {
            logger.debug(e.toString());
            throw new CDKException(e.toString(), e);
        }
    }

    private void initializeQuery() throws CDKException {
        mappings = null;
        query = cache.get(smarts);
        if (query == null) {
            query = SMARTSParser.parse(smarts, builder);
            cache.put(smarts, query);
        }
    }

    private List<Set<Integer>> matchedAtoms(List<List<RMap>> bondMapping, IAtomContainer atomContainer) {

        List<Set<Integer>> atomMapping = new ArrayList<Set<Integer>>();
        // loop over each mapping
        for (List<RMap> mapping : bondMapping) {

            Set<Integer> tmp = new TreeSet<Integer>();
            IAtom atom1 = null;
            IAtom atom2 = null;
            // loop over this mapping
            for (RMap map : mapping) {

                int bondID = map.getId1();

                // get the atoms in this bond
                IBond bond = atomContainer.getBond(bondID);
                atom1 = bond.getBegin();
                atom2 = bond.getEnd();

                Integer idx1 = atomContainer.indexOf(atom1);
                Integer idx2 = atomContainer.indexOf(atom2);

                if (!tmp.contains(idx1)) tmp.add(idx1);
                if (!tmp.contains(idx2)) tmp.add(idx2);
            }
            if (tmp.size() == query.getAtomCount()) atomMapping.add(tmp);

            // If there is only one bond, check if it matches both ways.
            if (mapping.size() == 1 && atom1.getAtomicNumber().equals(atom2.getAtomicNumber())) {
                atomMapping.add(new TreeSet<Integer>(tmp));
            }
        }

        return atomMapping;
    }
}
