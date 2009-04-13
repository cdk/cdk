/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.HydrogenAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RecursiveSmartsAtom;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.util.*;

/**
 * This class provides a easy to use wrapper around SMARTS matching
 * functionality. <p/> User code that wants to do SMARTS matching should use
 * this rather than using SMARTSParser (and UniversalIsomorphismTester)
 * directly. Example usage would be
 * <p/>
 * <pre>
 * SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 * IAtomContainer atomContainer = sp.parseSmiles(&quot;CC(=O)OC(=O)C&quot;);
 * SMARTSQueryTool querytool = new SMARTSQueryTool(&quot;O=CO&quot;);
 * boolean status = querytool.matches(atomContainer);
 * if (status) {
 *    int nmatch = querytool.countMatches();
 *    List mappings = querytool.getMatchingAtoms();
 *    for (int i = 0; i &lt; nmatch; i++) {
 *       List atomIndices = (List) mappings.get(i);
 *    }
 * }
 * </pre>
 *
 * <h3>SMARTS Extensions</h3>
 *
 * Currently the CDK supports the following SMARTS symbols, that are not described
 * in the Daylight specification. However they are supported by other packages and
 * are noted as such.
 *
 * <table border=1 cellpadding=3>
 * <thead>
 * <tr>
 * <th>Symbol</th><th>Meaning</th><th>Default</th><th>Notes</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>Gx</td><td>Periodic group number</td><td>None</td><td>x must be specified and must be a number between
 *  1 and 18. This symbol is supported by the MOE SMARTS implementation</td>
 * <tr>
 * <td>#X</td><td>Any non-carbon heavy element</td><td>None</td><td>This
 * symbol is supported by the MOE SMARTS implementation</td>
 * </tr>
 * <tr>
 * <td>^x</td><td>Any atom with the a specified hybridization state</td><td>None</td><td>x must be specified and
 * should be between 1 and 8 (inclusive), corresponding to SP1, SP2, SP3, SP3D1, SP3D2
 * SP3D3, SP3D4 and SP3D5. Supported by the OpenEye SMARTS implementation</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <h3>Notes</h3>
 * <ul>
 * <li>As <a href="http://sourceforge.net/mailarchive/message.php?msg_name=4964F605.1070502%40emolecules.com">described</a>
 * by Craig James the <code>h&lt;n&gt;</code> SMARTS pattern should not be used. It was included in the Daylight spec for
 * backwards compatibility. To match hydrogens, use the <code>H&lt;n&gt;</cod> pattern.</li>
 * <li>The wild card pattern (<code>*</code>) will not match hydrogens (explicit or implicit) unless an isotope is specified.
 * In other words, <code>*</code> gives two hits against <code>C[2H]</code> but 1 hit against
 * <code>C[H]</code>. This also means that
 * it gives no hits against <code>[H][H]</code>. This is contrary to what is shown by Daylights
 * <a href="http://www.daylight.com/daycgi_tutorials/depictmatch.cgi">depictmatch</a> service, but is based on this
 * <a href="https://sourceforge.net/mailarchive/message.php?msg_name=4964FF9D.3040004%40emolecules.com">discussion</a>.
 * A work around to get <code>*</code> to match <code>[H][H]</code> is to write it in the form <code>[1H][1H]</code>.
 * <p>
 * It's not entirely clear what the behavior of * should be with respect to hydrogens.
 * it is possible that the code will be updated so that <code>*</code> will not match <i>any</i> hydrogen in the future.</li>
 * <li>The {@link org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector} only considers single rings and
 * two fused non-spiro rings. As a result, it does not properly detect aromaticity in polycyclic systems such
 * as <code>[O-]C(=O)c1ccccc1c2c3ccc([O-])cc3oc4cc(=O)ccc24</code>. Thus SMARTS patterns that depend on proper
 * aromaticity detection may not work correctly in such polycyclic systems</li>
 * </ul>
 *
 * @author Rajarshi Guha
 * @cdk.created 2007-04-08
 * @cdk.module smarts
 * @cdk.svnrev $Revision$
 * @cdk.keyword SMARTS
 * @cdk.keyword substructure search
 * @cdk.bug 1760973
 * @cdk.bug 1761027
 */
@TestClass("org.openscience.cdk.smiles.smarts.SMARTSQueryToolTest")
public class SMARTSQueryTool {
    private LoggingTool logger;
    private String smarts;
    private IAtomContainer atomContainer = null;
    private QueryAtomContainer query = null;

    private List<List<Integer>> matchingAtoms = null;

    public SMARTSQueryTool(String smarts) throws CDKException {
        logger = new LoggingTool(this);
        this.smarts = smarts;
        initializeQuery();
    }

    /**
     * Returns the current SMARTS pattern being used.
     *
     * @return The SMARTS pattern
     */
    @TestMethod("testQueryTool")
    public String getSmarts() {
        return smarts;
    }

    /**
     * Set a new SMARTS pattern.
     *
     * @param smarts The new SMARTS pattern
     * @throws CDKException if there is an error in parsing the pattern
     */
    @TestMethod("testQueryTool, testQueryToolResetSmart")
    public void setSmarts(String smarts) throws CDKException {
        this.smarts = smarts;
        initializeQuery();
    }


    /**
     * Perform a SMARTS match and check whether the query is present in the
     * target molecule. <p/> This function simply checks whether the query
     * pattern matches the specified molecule. However the function will also,
     * internally, save the mapping of query atoms to the target molecule
     * <p>
     * <b>Note</b>: This method performs a simple caching scheme, by comparing the current
     * molecule to the previous molecule by reference. If you repeatedly match
     * different SMARTS on the same molecule, this method will avoid initializing (
     * ring perception, aromaticity etc.) the
     * molecule each time. If however, you modify the molecule between such multiple
     * matchings you should use the other form of this method to force initialization.
     *
     * @param atomContainer The target moleculoe
     * @return true if the pattern is found in the target molecule, false
     *         otherwise
     * @throws CDKException if there is an error in ring, aromaticity or isomorphism
     *                      perception
     * @see #getMatchingAtoms()
     * @see #countMatches()
     * @see #matches(org.openscience.cdk.interfaces.IAtomContainer, boolean)
     */
    public boolean matches(IAtomContainer atomContainer) throws CDKException {
        return matches(atomContainer, false);
    }

    /**
     * Perform a SMARTS match and check whether the query is present in the
     * target molecule. <p/> This function simply checks whether the query
     * pattern matches the specified molecule. However the function will also,
     * internally, save the mapping of query atoms to the target molecule
     *
     * @param atomContainer The target moleculoe
     * @param forceInitialization If true, then the molecule is initialized (ring perception, aromaticity etc).
     * If false, the molecule is only initialized if it is different (in terms of object reference)
     * than one supplied in a previous call to this method.
     * @return true if the pattern is found in the target molecule, false
     *         otherwise
     * @throws CDKException if there is an error in ring, aromaticity or isomorphism
     *                      perception
     * @see #getMatchingAtoms()
     * @see #countMatches()
     * @see #matches(org.openscience.cdk.interfaces.IAtomContainer) 
     */
    @TestMethod("testQueryTool, testQueryToolSingleAtomCase, testQuery")
    public boolean matches(IAtomContainer atomContainer, boolean forceInitialization) throws CDKException {

        if (this.atomContainer == atomContainer) {
            if (forceInitialization) initializeMolecule();
        } else {
            this.atomContainer = atomContainer;
            initializeMolecule();
        }
        
        // First calculate the recursive smarts
        initializeRecursiveSmarts(this.atomContainer);

        // lets see if we have a single atom query
        if (query.getAtomCount() == 1) {
            // lets get the query atom
            IQueryAtom queryAtom = (IQueryAtom) query.getAtom(0);

            matchingAtoms = new ArrayList<List<Integer>>();
            for (IAtom atom : this.atomContainer.atoms()) {
                if (queryAtom.matches(atom)) {
                    List<Integer> tmp = new ArrayList<Integer>();
                    tmp.add(this.atomContainer.getAtomNumber(atom));
                    matchingAtoms.add(tmp);
                }
            }
        } else {
            List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(this.atomContainer, query);
            matchingAtoms = getAtomMappings(bondMapping, this.atomContainer);
        }

        return matchingAtoms.size() != 0;
    }

    /**
     * Returns the number of times the pattern was found in the target molecule.
     * <p/> This function should be called after
     * {@link #matches(org.openscience.cdk.interfaces.IAtomContainer)}. If not,
     * the results may be undefined.
     *
     * @return The number of times the pattern was found in the target molecule
     */
    @TestMethod("testQueryTool")
    public int countMatches() {
        return matchingAtoms.size();
    }

    /**
     * Get the atoms in the target molecule that match the query pattern. <p/>
     * Since there may be multiple matches, the return value is a List of List
     * objects. Each List object contains the indices of the atoms in the target
     * molecule, that match the query pattern
     *
     * @return A List of List of atom indices in the target molecule
     */
    @TestMethod("testQueryTool")
    public List<List<Integer>> getMatchingAtoms() {
        return matchingAtoms;
    }

    /**
     * Get the atoms in the target molecule that match the query pattern. <p/>
     * Since there may be multiple matches, the return value is a List of List
     * objects. Each List object contains the unique set of indices of the atoms in the target
     * molecule, that match the query pattern
     *
     * @return A List of List of atom indices in the target molecule
     */
    @TestMethod("testUniqueQueries")
    public List<List<Integer>> getUniqueMatchingAtoms() {
        List<List<Integer>> ret = new ArrayList<List<Integer>>();
        for (List<Integer> atomMapping : matchingAtoms) {
            Collections.sort(atomMapping);

            // see if this sequence of atom indices is present
            // in the return container
            boolean present = false;
            for (List<Integer> r : ret) {
                if (r.size() != atomMapping.size()) continue;
                Collections.sort(r);
                boolean matches = true;
                for (int i = 0; i < atomMapping.size(); i++) {
                    int index1 = atomMapping.get(i);
                    int index2 = r.get(i);
                    if (index1 != index2) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    present = true;
                    break;
                }
            }
            if (!present) ret.add(atomMapping);
        }
        return ret;
    }

    /**
     * Prepare the target molecule for analysis. <p/> We perform ring perception
     * and aromaticity detection and set up the appropriate properties. Right
     * now, this function is called each time we need to do a query and this is
     * inefficient.
     *
     * @throws CDKException if there is a problem in ring perception or aromaticity
     *                      detection, which is usually related to a timeout in the ring
     *                      finding code.
     */
    private void initializeMolecule() throws CDKException {
        // Code copied from 
        // org.openscience.cdk.qsar.descriptors.atomic.AtomValenceDescriptor;
        Map<String, Integer> valencesTable = new HashMap<String, Integer>();
        valencesTable.put("H", 1);
        valencesTable.put("Li", 1);
        valencesTable.put("Be", 2);
        valencesTable.put("B", 3);
        valencesTable.put("C", 4);
        valencesTable.put("N", 5);
        valencesTable.put("O", 6);
        valencesTable.put("F", 7);
        valencesTable.put("Na", 1);
        valencesTable.put("Mg", 2);
        valencesTable.put("Al", 3);
        valencesTable.put("Si", 4);
        valencesTable.put("P", 5);
        valencesTable.put("S", 6);
        valencesTable.put("Cl", 7);
        valencesTable.put("K", 1);
        valencesTable.put("Ca", 2);
        valencesTable.put("Ga", 3);
        valencesTable.put("Ge", 4);
        valencesTable.put("As", 5);
        valencesTable.put("Se", 6);
        valencesTable.put("Br", 7);
        valencesTable.put("Rb", 1);
        valencesTable.put("Sr", 2);
        valencesTable.put("In", 3);
        valencesTable.put("Sn", 4);
        valencesTable.put("Sb", 5);
        valencesTable.put("Te", 6);
        valencesTable.put("I", 7);
        valencesTable.put("Cs", 1);
        valencesTable.put("Ba", 2);
        valencesTable.put("Tl", 3);
        valencesTable.put("Pb", 4);
        valencesTable.put("Bi", 5);
        valencesTable.put("Po", 6);
        valencesTable.put("At", 7);
        valencesTable.put("Fr", 1);
        valencesTable.put("Ra", 2);
        valencesTable.put("Cu", 2);
        valencesTable.put("Mn", 2);
        valencesTable.put("Co", 2);

        // do all ring perception
        AllRingsFinder arf = new AllRingsFinder();
        IRingSet allRings;
        try {
            allRings = arf.findAllRings(atomContainer);
        } catch (CDKException e) {
            logger.debug(e.toString());
            throw new CDKException(e.toString());
        }

        // sets SSSR information
        SSSRFinder finder = new SSSRFinder(atomContainer);
        IRingSet sssr = finder.findEssentialRings();

        for (IAtom atom : atomContainer.atoms()) {

            // add a property to each ring atom that will be an array of
            // Integers, indicating what size ring the given atom belongs to
            // Add SSSR ring counts
            if (allRings.contains(atom)) { // it's in a ring
                atom.setFlag(CDKConstants.ISINRING, true);
                // lets find which ring sets it is a part of
                List<Integer> ringsizes = new ArrayList<Integer>();
                IRingSet currentRings = allRings.getRings(atom);
                int min = 0;
                for (int i = 0; i < currentRings.getAtomContainerCount(); i++) {
                    int size = currentRings.getAtomContainer(i).getAtomCount();
                    if (min > size) min = size;
                    ringsizes.add(size);
                }
                atom.setProperty(CDKConstants.RING_SIZES, ringsizes);
                atom.setProperty(CDKConstants.SMALLEST_RINGS, sssr.getRings(atom));
            } else {
                atom.setFlag(CDKConstants.ISINRING, false);
            }

            // determine how many rings bonds each atom is a part of
            int hCount;
            if (atom.getHydrogenCount() == CDKConstants.UNSET) hCount = 0;
            else hCount = atom.getHydrogenCount();

            List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);
            int total = hCount + connectedAtoms.size();
            for (IAtom connectedAtom : connectedAtoms) {
                if (connectedAtom.getSymbol().equals("H")) {
                    hCount++;
                }
            }
            atom.setProperty(CDKConstants.TOTAL_CONNECTIONS, total);
            atom.setProperty(CDKConstants.TOTAL_H_COUNT, hCount);

            if (valencesTable.get(atom.getSymbol()) != null) {
                int formalCharge = atom.getFormalCharge() == CDKConstants.UNSET ? 0 : atom.getFormalCharge();
                atom.setValency(valencesTable.get(atom.getSymbol()) - formalCharge);
            }
        }

        for (IBond bond : atomContainer.bonds()) {
            if (allRings.getRings(bond).getAtomContainerCount() > 0) {
                bond.setFlag(CDKConstants.ISINRING, true);
            }
        }

        for (IAtom atom : atomContainer.atoms()) {
            List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);

            int counter = 0;
            IAtom any;
            for (IAtom connectedAtom : connectedAtoms) {
                any = connectedAtom;
                if (any.getFlag(CDKConstants.ISINRING)) {
                    counter++;
                }
            }
            atom.setProperty(CDKConstants.RING_CONNECTIONS, counter);
        }

        // check for atomaticity
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
            CDKHueckelAromaticityDetector.detectAromaticity(atomContainer);
        } catch (CDKException e) {
            logger.debug(e.toString());
            throw new CDKException(e.toString());
        }
    }

    /**
     * Initializes recursive smarts atoms in the query.
     *
     * We loop over the SMARTS atoms in the query and associate the
     * target molecule with each of the SMARTS atoms that need it
     *
     * @param atomContainer
     * @throws CDKException
     */
    private void initializeRecursiveSmarts(IAtomContainer atomContainer) throws CDKException {
        for (IAtom atom : query.atoms()) {
            initializeRecursiveSmartsAtom(atom, atomContainer);
        }
    }

    /**
     * Recursively initializes recursive smarts atoms
     *
     * @param atom
     * @param atomContainer
     * @throws CDKException
     */
    private void initializeRecursiveSmartsAtom(IAtom atom, IAtomContainer atomContainer) throws CDKException {
        if (atom instanceof LogicalOperatorAtom) {
            initializeRecursiveSmartsAtom(((LogicalOperatorAtom) atom).getLeft(), atomContainer);
            if (((LogicalOperatorAtom) atom).getRight() != null) {
                initializeRecursiveSmartsAtom(((LogicalOperatorAtom) atom).getRight(), atomContainer);
            }
        } else if (atom instanceof RecursiveSmartsAtom) {
            ((RecursiveSmartsAtom) atom).setAtomContainer(atomContainer);
        } else if (atom instanceof HydrogenAtom) {
            ((HydrogenAtom) atom).setAtomContainer(atomContainer);
        }
    }

    private void initializeQuery() throws CDKException {
        matchingAtoms = null;
        query = SMARTSParser.parse(smarts);
    }


    private List<List<Integer>> getAtomMappings(List bondMapping, IAtomContainer atomContainer) {
        List<List<Integer>> atomMapping = new ArrayList<List<Integer>>();

        // loop over each mapping
        for (Object aBondMapping : bondMapping) {
            List list = (List) aBondMapping;

            List<Integer> tmp = new ArrayList<Integer>();
            IAtom atom1 = null;
            IAtom atom2 = null;
            // loop over this mapping
            for (Object aList : list) {
                RMap map = (RMap) aList;
                int bondID = map.getId1();

                // get the atoms in this bond
                IBond bond = atomContainer.getBond(bondID);
                atom1 = bond.getAtom(0);
                atom2 = bond.getAtom(1);

                Integer idx1 = atomContainer.getAtomNumber(atom1);
                Integer idx2 = atomContainer.getAtomNumber(atom2);

                if (!tmp.contains(idx1)) tmp.add(idx1);
                if (!tmp.contains(idx2)) tmp.add(idx2);
            }
            if (tmp.size() > 0) atomMapping.add(tmp);

            // If there is only one bond, check if it matches both ways.
            if (list.size() == 1 && atom1.getAtomicNumber() == atom2.getAtomicNumber()) {
                List<Integer> tmp2 = new ArrayList<Integer>();
                tmp2.add(tmp.get(0));
                tmp2.add(tmp.get(1));
                atomMapping.add(tmp2);
            }
        }


        return atomMapping;
    }
}
