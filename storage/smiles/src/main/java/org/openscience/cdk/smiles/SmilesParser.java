/*  Copyright (C) 2002-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                200?-2007  Egon Willighagen <egonw@users.sf.net>
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smiles;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.smiles.CxSmilesState.DataSgroup;
import org.openscience.cdk.smiles.CxSmilesState.PolymerSgroup;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import uk.ac.ebi.beam.Graph;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Read molecules and reactions from a SMILES {@cdk.cite SMILESTUT} string.
 *
 * <b>Example usage</b>
 *
 * <blockquote><pre>
 * try {
 *     SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
 *     IAtomContainer m   = sp.parseSmiles("c1ccccc1");
 * } catch (InvalidSmilesException e) {
 *     System.err.println(e.getMessage());
 * }
 * </pre>
 * </blockquote>
 *
 * <b>Reading Aromatic SMILES</b>
 *
 * Aromatic SMILES are automatically kekulised producing a structure with
 * assigned bond orders. The aromatic specification on the atoms is maintained
 * from the SMILES even if the structures are not considered aromatic. For
 * example 'c1ccc1' will correctly have two pi bonds assigned but the
 * atoms/bonds will still be flagged as aromatic. Recomputing or clearing the
 * aromaticty will remove these erroneous flags. If a kekulé structure could not
 * be assigned this is considered an error. The most common example is the
 * omission of hydrogens on aromatic nitrogens (aromatic pyrrole is specified as
 * '[nH]1cccc1' not 'n1cccc1'). These structures can not be corrected without
 * modifying their formula. If there are multiple locations a hydrogen could be
 * placed the returned structure would differ depending on the atom input order.
 * If you wish to skip the kekulistation (not recommended) then it can be
 * disabled with {@link #kekulise}. SMILES can be verified for validity with the
 * <a href="http://www.daylight.com/daycgi/depict">DEPICT</a> service.
 *
 * <b>Unsupported Features</b>
 *
 * The following features are not supported by this parser. <ul> <li>variable
 * order of bracket atom attributes, '[C-H]', '[CH@]' are considered invalid.
 * The predefined order required by this parser follows the <a
 * href="http://www.opensmiles.org/opensmiles.html">OpenSMILES</a> specification
 * of 'isotope', 'symbol', 'chiral', 'hydrogens', 'charge', 'atom class'</li>
 * <li>atom class indication - <i>this information is loaded but not annotated
 * on the structure</i> </li> <li>extended tetrahedral stereochemistry
 * (cumulated double bonds)</li> <li>trigonal bipyramidal stereochemistry</li>
 * <li>octahedral stereochemistry</li> </ul>
 *
 * <b>Atom Class</b>
 *
 * The atom class is stored as the {@link org.openscience.cdk.CDKConstants#ATOM_ATOM_MAPPING}
 * property.
 *
 * <blockquote><pre>
 *
 * SmilesParser   sp  = new SmilesParser(SilentChemObjectBuilder.getInstance());
 * IAtomContainer m   = sp.parseSmiles("c1[cH:5]cccc1");
 * Integer        c1  = m.getAtom(1)
 *                       .getProperty(CDKConstants.ATOM_ATOM_MAPPING); // 5
 * Integer        c2  = m.getAtom(2)
 *                       .getProperty(CDKConstants.ATOM_ATOM_MAPPING); // null
 *
 * </pre>
 * </blockquote>
 *
 *
 * @author Christoph Steinbeck
 * @author Egon Willighagen
 * @author John May
 * @cdk.module smiles
 * @cdk.githash
 * @cdk.created 2002-04-29
 * @cdk.keyword SMILES, parser
 */
public final class SmilesParser {

    private ILoggingTool logger = LoggingToolFactory.createLoggingTool(SmilesParser.class);

    /**
     * The builder determines which CDK domain objects to create.
     */
    private final IChemObjectBuilder builder;

    /**
     * Direct converter from Beam to CDK.
     */
    private final BeamToCDK          beamToCDK;

    /**
     * Kekulise the molecule on load. Generally this is a good idea as a
     * lower-case symbols in a SMILES do not really mean 'aromatic' but rather
     * 'conjugated'. Loading with kekulise 'on' will automatically assign
     * bond orders (if possible) using an efficient algorithm from the
     * underlying Beam library (soon to be added to CDK).
     */
    private boolean                  kekulise = true;

    /**
     * Whether the parser is in strict mode or not.
     */
    private boolean                  strict = false;

    /**
     * Create a new SMILES parser which will create {@link IAtomContainer}s with
     * the specified builder.
     *
     * @param builder used to create the CDK domain objects
     */
    public SmilesParser(final IChemObjectBuilder builder) {
        this.builder = builder;
        this.beamToCDK = new BeamToCDK(builder);
    }

    /**
     * Sets whether the parser is in strict mode. In non-strict mode (default)
     * recoverable issues with SMILES are reported as warnings.
     *
     * @param strict strict mode true/false.
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    /**
     * Parse a reaction SMILES.
     *
     * @param smiles The SMILES string to parse
     * @return An instance of {@link org.openscience.cdk.interfaces.IReaction}
     * @throws InvalidSmilesException if the string cannot be parsed
     * @see #parseSmiles(String)
     */
    public IReaction parseReactionSmiles(String smiles) throws InvalidSmilesException {

        if (!smiles.contains(">"))
            throw new InvalidSmilesException("Not a reaction SMILES: " + smiles);

        final int first  = smiles.indexOf('>');
        final int second = smiles.indexOf('>', first + 1);

        if (second < 0)
            throw new InvalidSmilesException("Invalid reaction SMILES:" + smiles);

        final String reactants = smiles.substring(0, first);
        final String agents = smiles.substring(first + 1, second);
        final String products = smiles.substring(second + 1, smiles.length());

        IReaction reaction = builder.newInstance(IReaction.class);

        // add reactants
        if (!reactants.isEmpty()) {
            IAtomContainer reactantContainer = parseSmiles(reactants, true);
            IAtomContainerSet reactantSet = ConnectivityChecker.partitionIntoMolecules(reactantContainer);
            for (int i = 0; i < reactantSet.getAtomContainerCount(); i++) {
                reaction.addReactant(reactantSet.getAtomContainer(i));
            }
        }

        // add agents
        if (!agents.isEmpty()) {
            IAtomContainer agentContainer = parseSmiles(agents, true);
            IAtomContainerSet agentSet = ConnectivityChecker.partitionIntoMolecules(agentContainer);
            for (int i = 0; i < agentSet.getAtomContainerCount(); i++) {
                reaction.addAgent(agentSet.getAtomContainer(i));
            }
        }

        String title = null;

        // add products
        if (!products.isEmpty()) {
            IAtomContainer productContainer = parseSmiles(products, true);
            IAtomContainerSet productSet = ConnectivityChecker.partitionIntoMolecules(productContainer);
            for (int i = 0; i < productSet.getAtomContainerCount(); i++) {
                reaction.addProduct(productSet.getAtomContainer(i));
            }
            reaction.setProperty(CDKConstants.TITLE, title = productContainer.getProperty(CDKConstants.TITLE));
        }

        try {
            // CXSMILES layer
            parseRxnCXSMILES(title, reaction);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InvalidSmilesException("Error parsing CXSMILES:" + e.getMessage());
        }

        return reaction;
    }

    /**
     * Parses a SMILES string and returns a structure ({@link IAtomContainer}).
     *
     * @param smiles A SMILES string
     * @return A structure representing the provided SMILES
     * @throws InvalidSmilesException thrown when the SMILES string is invalid
     */
    public IAtomContainer parseSmiles(String smiles) throws InvalidSmilesException {
        return parseSmiles(smiles, false);
    }

    private IAtomContainer parseSmiles(String smiles, boolean isRxnPart) throws InvalidSmilesException {
        try {
            // create the Beam object from parsing the SMILES
            Set<String> warnings = new HashSet<>();
            Graph g = Graph.parse(smiles, strict, warnings);
            for (String warning : warnings)
              logger.warn(warning);

            // convert the Beam object model to the CDK - note exception thrown
            // if a kekule structure could not be assigned.
            IAtomContainer mol = beamToCDK.toAtomContainer(kekulise ? g.kekule() : g,
                                                           kekulise);

            if (!isRxnPart) {
                try {
                    // CXSMILES layer
                    parseMolCXSMILES(g.getTitle(), mol);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new InvalidSmilesException("Error parsing CXSMILES:" + e.getMessage());
                }
            }

            return mol;
        } catch (IOException e) {
            throw new InvalidSmilesException("could not parse '" + smiles + "', " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidSmilesException("could not parse '" + smiles + "'");
        }
    }

    /**
     * Safely parses an integer from a string and will not fail if a number is missing.
     *
     * @param val value
     * @return the integer value
     */
    private int parseIntSafe(String val) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parses CXSMILES layer and set attributes for atoms and bonds on the provided molecule.
     *
     * @param title SMILES title field
     * @param mol   molecule
     */
    private void parseMolCXSMILES(String title, IAtomContainer mol) {
        CxSmilesState cxstate;
        int pos;
        if (title != null && title.startsWith("|")) {
            if ((pos = CxSmilesParser.processCx(title, cxstate = new CxSmilesState())) >= 0) {

                // set the correct title
                mol.setTitle(title.substring(pos));

                final Map<IAtom, IAtomContainer> atomToMol = Maps.newHashMapWithExpectedSize(mol.getAtomCount());
                final List<IAtom> atoms = new ArrayList<>(mol.getAtomCount());

                for (IAtom atom : mol.atoms()) {
                    atoms.add(atom);
                    atomToMol.put(atom, mol);
                }

                assignCxSmilesInfo(mol.getBuilder(), mol, atoms, atomToMol, cxstate);
            }
        }
    }

    /**
     * Parses CXSMILES layer and set attributes for atoms and bonds on the provided reaction.
     *
     * @param title SMILES title field
     * @param rxn   parsed reaction
     */
    private void parseRxnCXSMILES(String title, IReaction rxn) {
        CxSmilesState cxstate;
        int pos;
        if (title != null && title.startsWith("|")) {
            if ((pos = CxSmilesParser.processCx(title, cxstate = new CxSmilesState())) >= 0) {

                // set the correct title
                rxn.setProperty(CDKConstants.TITLE, title.substring(pos));

                final Map<IAtom, IAtomContainer> atomToMol = new HashMap<>(100);
                final List<IAtom> atoms = new ArrayList<>();
                handleFragmentGrouping(rxn, cxstate);

                // merge all together
                for (IAtomContainer mol : rxn.getReactants().atomContainers()) {
                    for (IAtom atom : mol.atoms()) {
                        atoms.add(atom);
                        atomToMol.put(atom, mol);
                    }
                }
                for (IAtomContainer mol : rxn.getAgents().atomContainers()) {
                    for (IAtom atom : mol.atoms()) {
                        atoms.add(atom);
                        atomToMol.put(atom, mol);
                    }
                }
                for (IAtomContainer mol : rxn.getProducts().atomContainers()) {
                    for (IAtom atom : mol.atoms()) {
                        atoms.add(atom);
                        atomToMol.put(atom, mol);
                    }
                }

                assignCxSmilesInfo(rxn.getBuilder(), rxn, atoms, atomToMol, cxstate);
            }
        }
    }

    /**
     * Handle fragment grouping of a reaction that specifies certain disconnected components
     * are actually considered a single molecule. Normally used for salts, [Na+].[OH-].
     *
     * @param rxn reaction
     * @param cxstate state
     */
    private void handleFragmentGrouping(IReaction rxn, CxSmilesState cxstate) {
        // repartition/merge fragments
        if (cxstate.fragGroups != null) {

            final int reactant = 1;
            final int agent    = 2;
            final int product  = 3;

            // note we don't use a list for fragmap as the indexes need to stay consistent
            Map<Integer,IAtomContainer> fragMap = new LinkedHashMap<>();
            Map<IAtomContainer,Integer> roleMap = new HashMap<>();

            for (IAtomContainer mol : rxn.getReactants().atomContainers()) {
                fragMap.put(fragMap.size(), mol);
                roleMap.put(mol, reactant);
            }
            for (IAtomContainer mol : rxn.getAgents().atomContainers()) {
                fragMap.put(fragMap.size(), mol);
                roleMap.put(mol, agent);
            }
            for (IAtomContainer mol : rxn.getProducts().atomContainers()) {
                fragMap.put(fragMap.size(), mol);
                roleMap.put(mol, product);
            }

            // check validity of group
            boolean invalid = false;
            Set<Integer> visit = new HashSet<>();

            for (List<Integer> grouping : cxstate.fragGroups) {
                IAtomContainer dest = fragMap.get(grouping.get(0));
                if (dest == null)
                    continue;
                if (!visit.add(grouping.get(0)))
                    invalid = true;
                for (int i = 1; i < grouping.size(); i++) {
                    if (!visit.add(grouping.get(i)))
                        invalid = true;
                    IAtomContainer src = fragMap.get(grouping.get(i));
                    if (src != null) {
                        dest.add(src);
                        roleMap.put(src, 0); // no-role
                    }
                }
            }

            if (!invalid) {
                rxn.getReactants().removeAllAtomContainers();
                rxn.getAgents().removeAllAtomContainers();
                rxn.getProducts().removeAllAtomContainers();
                for (IAtomContainer mol : fragMap.values()) {
                    switch (roleMap.get(mol)) {
                        case reactant:
                            rxn.getReactants().addAtomContainer(mol);
                            break;
                        case product:
                            rxn.getProducts().addAtomContainer(mol);
                            break;
                        case agent:
                            rxn.getAgents().addAtomContainer(mol);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Transfers the CXSMILES state onto the CDK atom/molecule data-structures.
     *
     * @param bldr      chem-object builder
     * @param atoms     atoms parsed from the molecule or reaction. Reaction molecules are list
     *                  left to right.
     * @param atomToMol look-up of atoms to molecules when connectivity/sgroups need modification
     * @param cxstate   the CXSMILES state to read from
     */
    private void assignCxSmilesInfo(IChemObjectBuilder bldr,
                                    IChemObject chemObj,
                                    List<IAtom> atoms,
                                    Map<IAtom, IAtomContainer> atomToMol,
                                    CxSmilesState cxstate) {

        // atom-labels - must be done first as we replace atoms
        if (cxstate.atomLabels != null) {
            for (Map.Entry<Integer, String> e : cxstate.atomLabels.entrySet()) {

                // bounds check
                if (e.getKey() >= atoms.size())
                    continue;

                IAtom old = atoms.get(e.getKey());
                IPseudoAtom pseudo = bldr.newInstance(IPseudoAtom.class);
                String val = e.getValue();

                // specialised label handling
                if (val.endsWith("_p")) // pseudo label
                    val = val.substring(0, val.length() - 2);
                else if (val.startsWith("_AP")) // attachment point
                    pseudo.setAttachPointNum(parseIntSafe(val.substring(3)));

                pseudo.setLabel(val);
                pseudo.setAtomicNumber(0);
                pseudo.setImplicitHydrogenCount(0);
                IAtomContainer mol = atomToMol.get(old);
                AtomContainerManipulator.replaceAtomByAtom(mol, old, pseudo);
                atomToMol.put(pseudo, mol);
                atoms.set(e.getKey(), pseudo);
            }
        }

        // atom-values - set as comment, mirrors Molfile reading behavior
        if (cxstate.atomValues != null) {
            for (Map.Entry<Integer, String> e : cxstate.atomValues.entrySet())
                atoms.get(e.getKey()).setProperty(CDKConstants.COMMENT, e.getValue());
        }

        // atom-coordinates
        if (cxstate.atomCoords != null) {
            final int numAtoms = atoms.size();
            final int numCoords = cxstate.atomCoords.size();
            final int lim = Math.min(numAtoms, numCoords);
            if (cxstate.coordFlag) {
                for (int i = 0; i < lim; i++)
                    atoms.get(i).setPoint3d(new Point3d(cxstate.atomCoords.get(i)));
            } else {
                for (int i = 0; i < lim; i++)
                    atoms.get(i).setPoint2d(new Point2d(cxstate.atomCoords.get(i)));
            }
        }

        // atom radicals
        if (cxstate.atomRads != null) {
            for (Map.Entry<Integer, CxSmilesState.Radical> e : cxstate.atomRads.entrySet()) {
                // bounds check
                if (e.getKey() >= atoms.size())
                    continue;

                int count = 0;
                switch (e.getValue()) {
                    case Monovalent:
                        count = 1;
                        break;
                    // no distinction in CDK between singled/triplet
                    case Divalent:
                    case DivalentSinglet:
                    case DivalentTriplet:
                        count = 2;
                        break;
                    // no distinction in CDK between doublet/quartet
                    case Trivalent:
                    case TrivalentDoublet:
                    case TrivalentQuartet:
                        count = 3;
                        break;
                }
                IAtom atom = atoms.get(e.getKey());
                IAtomContainer mol = atomToMol.get(atom);
                while (count-- > 0)
                    mol.addSingleElectron(bldr.newInstance(ISingleElectron.class, atom));
            }
        }

        Multimap<IAtomContainer, Sgroup> sgroupMap = HashMultimap.create();

        // positional-variation
        if (cxstate.positionVar != null) {
            for (Map.Entry<Integer, List<Integer>> e : cxstate.positionVar.entrySet()) {
                Sgroup sgroup = new Sgroup();
                sgroup.setType(SgroupType.ExtMulticenter);
                IAtom beg = atoms.get(e.getKey());
                IAtomContainer mol = atomToMol.get(beg);
                List<IBond> bonds = mol.getConnectedBondsList(beg);
                if (bonds.isEmpty())
                    continue; // bad
                sgroup.addAtom(beg);
                sgroup.addBond(bonds.get(0));
                for (Integer endpt : e.getValue())
                    sgroup.addAtom(atoms.get(endpt));
                sgroupMap.put(mol, sgroup);
            }
        }

        // data sgroups
        if (cxstate.dataSgroups != null) {
            for (DataSgroup dsgroup : cxstate.dataSgroups) {
                if (dsgroup.field != null && dsgroup.field.startsWith("cdk:")) {
                    chemObj.setProperty(dsgroup.field, dsgroup.value);
                }
            }
        }

        // polymer Sgroups
        if (cxstate.sgroups != null) {

            PolySgroup:
            for (PolymerSgroup psgroup : cxstate.sgroups) {

                Sgroup sgroup = new Sgroup();

                Set<IAtom> atomset = new HashSet<>();
                IAtomContainer mol = null;
                for (Integer idx : psgroup.atomset) {
                    if (idx >= atoms.size())
                        continue;
                    IAtom atom = atoms.get(idx);
                    IAtomContainer amol = atomToMol.get(atom);

                    if (mol == null)
                        mol = amol;
                    else if (amol != mol)
                        continue PolySgroup;

                    atomset.add(atom);
                }

                if (mol == null)
                    continue;

                for (IAtom atom : atomset) {
                    for (IBond bond : mol.getConnectedBondsList(atom)) {
                        if (!atomset.contains(bond.getOther(atom)))
                            sgroup.addBond(bond);
                    }
                    sgroup.addAtom(atom);
                }

                sgroup.setSubscript(psgroup.subscript);
                sgroup.putValue(SgroupKey.CtabConnectivity, psgroup.supscript);

                switch (psgroup.type) {
                    case "n":
                        sgroup.setType(SgroupType.CtabStructureRepeatUnit);
                        break;
                    case "mon":
                        sgroup.setType(SgroupType.CtabMonomer);
                        break;
                    case "mer":
                        sgroup.setType(SgroupType.CtabMer);
                        break;
                    case "co":
                        sgroup.setType(SgroupType.CtabCopolymer);
                        break;
                    case "xl":
                        sgroup.setType(SgroupType.CtabCrossLink);
                        break;
                    case "mod":
                        sgroup.setType(SgroupType.CtabModified);
                        break;
                    case "mix":
                        sgroup.setType(SgroupType.CtabMixture);
                        break;
                    case "f":
                        sgroup.setType(SgroupType.CtabFormulation);
                        break;
                    case "any":
                        sgroup.setType(SgroupType.CtabAnyPolymer);
                        break;
                    case "gen":
                        sgroup.setType(SgroupType.CtabGeneric);
                        break;
                    case "c":
                        sgroup.setType(SgroupType.CtabComponent);
                        break;
                    case "grf":
                        sgroup.setType(SgroupType.CtabGraft);
                        break;
                    case "alt":
                        sgroup.setType(SgroupType.CtabCopolymer);
                        sgroup.putValue(SgroupKey.CtabSubType, "ALT");
                        break;
                    case "ran":
                        sgroup.setType(SgroupType.CtabCopolymer);
                        sgroup.putValue(SgroupKey.CtabSubType, "RAN");
                        break;
                    case "blk":
                        sgroup.setType(SgroupType.CtabCopolymer);
                        sgroup.putValue(SgroupKey.CtabSubType, "BLO");
                        break;
                }

                sgroupMap.put(mol, sgroup);
            }
        }

        // assign Sgroups
        for (Map.Entry<IAtomContainer, Collection<Sgroup>> e : sgroupMap.asMap().entrySet())
            e.getKey().setProperty(CDKConstants.CTAB_SGROUPS, new ArrayList<>(e.getValue()));
    }

    /**
     * Makes the Smiles parser set aromaticity as provided in the Smiles itself,
     * without detecting it. Default false. Atoms will not be typed when set to
     * true.
     *
     * @param preservingAromaticity boolean to indicate if aromaticity is to be
     *                              preserved.
     * @see #kekulise
     */
    @Deprecated
    public void setPreservingAromaticity(boolean preservingAromaticity) {
        this.kekulise = !preservingAromaticity;
    }

    /**
     * Gets the (default false) setting to preserve aromaticity as provided in
     * the Smiles itself.
     *
     * @return true or false indicating if aromaticity is preserved.
     */
    @Deprecated
    public boolean isPreservingAromaticity() {
        return !kekulise;
    }

    /**
     * Indicated whether structures should be automatically kekulised if they
     * are provided as aromatic. Kekulisation is on by default but can be
     * turned off if it is believed the structures can be handled without
     * assigned bond orders (not recommended).
     *
     * @param kekulise should structures be kekulised
     */
    public void kekulise(boolean kekulise) {
        this.kekulise = kekulise;
    }
}
