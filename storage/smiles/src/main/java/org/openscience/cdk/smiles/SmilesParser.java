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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.smiles.CxSmilesState.CxDataSgroup;
import org.openscience.cdk.smiles.CxSmilesState.CxPolymerSgroup;
import org.openscience.cdk.stereo.Atropisomeric;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.manipulator.ReactionSetManipulator;
import uk.ac.ebi.beam.Graph;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
 * <p>
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
 * <p>
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
 * <p>
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
 * @author Christoph Steinbeck
 * @author Egon Willighagen
 * @author John May
 * @cdk.created 2002-04-29
 * @cdk.keyword SMILES, parser
 */
public final class SmilesParser {

    private final ILoggingTool logger = LoggingToolFactory.createLoggingTool(SmilesParser.class);

    /**
     * The builder determines which CDK domain objects to create.
     */
    private final IChemObjectBuilder builder;

    /**
     * Direct converter from Beam to CDK.
     */
    private final BeamToCDK beamToCDK;

    /**
     * Kekulise the molecule on load. Generally this is a good idea as a
     * lower-case symbols in a SMILES do not really mean 'aromatic' but rather
     * 'conjugated'. Loading with kekulise 'on' will automatically assign
     * bond orders (if possible) using an efficient algorithm from the
     * underlying Beam library (soon to be added to CDK).
     */
    private boolean kekulise = true;

    /**
     * Whether the parser is in strict mode or not.
     */
    private boolean strict = false;

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

        final int first = smiles.indexOf('>');
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
            IReactionSet rset = reaction.getBuilder().newInstance(IReactionSet.class);
            rset.addReaction(reaction);
            parseRxnCXSMILES(title, rset);
            reaction.setProperty(CDKConstants.TITLE, rset.getProperty(CDKConstants.TITLE));
        } catch (Exception e) {
            throw new InvalidSmilesException("Error parsing CXSMILES", e);
        }

        return reaction;
    }

    /**
     * Parse a SMILES that describes a set of reactions representing multiple
     * synthesis steps or a metabolic pathway. This is a logical extension to
     * the SMILES reaction syntax. The basic idea is the product(s) of the
     * previous step become the reactants of the next step.
     *
     * <pre>{@code
     * {reactant}>{agent_1}>{product_1}>{agent_2}>{product_2}
     * }</pre>
     * <p>
     * Results in a reaction set with two reactions:
     * <pre>{@code
     * {reactant}>{agent_1}>{product_1} step 1
     * {product_1}>{agent_2}>{product_2} step 2
     * }</pre>
     *
     * @param smiles the SMILES input string
     * @return the reaction set
     * @throws InvalidSmilesException the input was invalid (with reason)
     */
    public IReactionSet parseReactionSetSmiles(String smiles)
            throws InvalidSmilesException {

        int delim = smiles.length();
        for (int i = smiles.lastIndexOf('>'); i < smiles.length(); i++) {
            if (smiles.charAt(i) == ' ' || smiles.charAt(i) == '\t') {
                delim = i;
                break;
            }
        }

        String[] parts = smiles.substring(0, delim).split(">", -1);
        String title = smiles.substring(delim).trim();

        if (parts.length < 3 || parts.length % 2 == 0)
            throw new IllegalArgumentException("Unexpected number of parts: " + parts.length + ", should be 3,5,7,..");

        IReactionSet reactions = builder.newInstance(IReactionSet.class);
        IReaction reaction = builder.newReaction();

        for (int i = 0; i < parts.length; i++) {
            IAtomContainer mol = parseSmiles(parts[i], true);
            IAtomContainerSet mols = ConnectivityChecker.partitionIntoMolecules(mol);
            if (i == 0) {
                // first step's reactants
                for (IAtomContainer container : mols.atomContainers())
                    reaction.addReactant(container);
            } else if (i == 1) {
                // first step's agents
                for (IAtomContainer container : mols.atomContainers())
                    reaction.addAgent(container);
            } else if (i % 2 == 0) {
                // the products of which ever step we are on
                for (IAtomContainer container : mols.atomContainers())
                    reaction.addProduct(container);
                reactions.addReaction(reaction);
            } else {
                // the agents of which ever step we are on, creates the new
                // reaction step and copies the previous products as reactants
                IReaction nextReaction = builder.newReaction();
                for (IAtomContainer container : reaction.getProducts().atomContainers())
                    nextReaction.addReactant(container);
                reaction = nextReaction;
                for (IAtomContainer container : mols.atomContainers())
                    reaction.addAgent(container);
            }
        }
        try {
            parseRxnCXSMILES(title, reactions);
        } catch (Exception e) {
            throw new InvalidSmilesException("Error parsing CXSMILES", e);
        }


        return reactions;
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
                    throw new InvalidSmilesException("Error parsing CXSMILES", e);
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
    private void parseMolCXSMILES(String title, IAtomContainer mol) throws InvalidSmilesException {
        CxSmilesState cxstate;
        int pos;
        if (title != null && title.startsWith("|")) {
            if ((pos = CxSmilesParser.processCx(title, cxstate = new CxSmilesState())) >= 0) {

                // set the correct title
                mol.setTitle(title.substring(pos));

                final Map<IAtom, IAtomContainer> atomToMol = new HashMap<>(2 * mol.getAtomCount());
                final List<IAtom> atoms = new ArrayList<>(mol.getAtomCount());
                final List<IBond> bonds = new ArrayList<>(mol.getBondCount());

                for (IAtom atom : mol.atoms()) {
                    atoms.add(atom);
                    atomToMol.put(atom, mol);
                }
                for (IBond bond : mol.bonds())
                    bonds.add(bond);

                assignCxSmilesInfo(mol.getBuilder(), mol, atoms, bonds, atomToMol, cxstate);
            }
        }
    }

    /**
     * Parses CXSMILES layer and set attributes for atoms and bonds on the provided reaction.
     *
     * @param title SMILES title field
     * @param rxns  parsed reactions
     */
    private void parseRxnCXSMILES(String title, IReactionSet rxns) throws InvalidSmilesException {
        CxSmilesState cxstate;
        int pos;
        if (title != null && title.startsWith("|")) {
            if ((pos = CxSmilesParser.processCx(title, cxstate = new CxSmilesState())) >= 0) {

                // set the correct title
                rxns.setProperty(CDKConstants.TITLE, title.substring(pos));

                final Map<IAtom, IAtomContainer> atomToMol = new HashMap<>(100);
                final List<IAtom> atoms = new ArrayList<>();
                final List<IBond> bonds = new ArrayList<>();

                // collect atom offsets before handling fragment groups
                Set<IAtomContainer> uniqueMolecules = new HashSet<>();
                for (IAtomContainer mol : ReactionSetManipulator.getAllAtomContainers(rxns)) {
                    if (!uniqueMolecules.add(mol))
                        continue;
                    for (IAtom atom : mol.atoms())
                        atoms.add(atom);
                    for (IBond bond : mol.bonds())
                        bonds.add(bond);
                }

                handleFragmentGrouping(rxns, cxstate);

                // merge all together
                for (IAtomContainer mol : ReactionSetManipulator.getAllAtomContainers(rxns))
                    for (IAtom atom : mol.atoms())
                        atomToMol.put(atom, mol);

                assignCxSmilesInfo(rxns.getBuilder(), rxns, atoms, bonds, atomToMol, cxstate);
            }

            String arrowType = rxns.getProperty(CDKConstants.REACTION_ARROW);
            if (arrowType != null && !arrowType.isEmpty()) {
                for (IReaction rxn : rxns.reactions()) {
                    switch (arrowType) {
                        case "RES":
                            rxn.setDirection(IReaction.Direction.RESONANCE);
                            break;
                        case "EQU":
                            rxn.setDirection(IReaction.Direction.BIDIRECTIONAL);
                            break;
                        case "RET":
                            rxn.setDirection(IReaction.Direction.RETRO_SYNTHETIC);
                            break;
                        case "NGO":
                            rxn.setDirection(IReaction.Direction.NO_GO);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Handle fragment grouping of a reaction that specifies certain disconnected components
     * are actually considered a single molecule. Normally used for salts, [Na+].[OH-].
     *
     * @param rxns    reaction set
     * @param cxstate state
     */
    private void handleFragmentGrouping(IReactionSet rxns, CxSmilesState cxstate) {

        if (cxstate.fragGroups == null && cxstate.racemicFrags == null)
            return; // nothing to do here

        final int reactant = 1;
        final int agent = 2;
        final int product = 3;

        Set<IAtomContainer> unique = new HashSet<>();
        List<IAtomContainer> fragments = new ArrayList<>();
        Map<IAtomContainer, List<Integer>> roleMap = new HashMap<>();
        Map<IAtomContainer, List<IReaction>> molToReaction = new HashMap<>();

        for (IReaction reaction : rxns.reactions()) {
            for (IAtomContainer mol : ReactionManipulator.getAllAtomContainers(reaction)) {
                molToReaction.computeIfAbsent(mol, k -> new ArrayList<>()).add(reaction);
                if (unique.add(mol))
                    fragments.add(mol);
            }
            for (IAtomContainer mol : reaction.getReactants().atomContainers())
                roleMap.computeIfAbsent(mol, k -> new ArrayList<>()).add(reactant);
            for (IAtomContainer mol : reaction.getAgents().atomContainers())
                roleMap.computeIfAbsent(mol, k -> new ArrayList<>()).add(agent);
            for (IAtomContainer mol : reaction.getProducts().atomContainers())
                roleMap.computeIfAbsent(mol, k -> new ArrayList<>()).add(product);
        }

        if (cxstate.racemicFrags != null) {
            for (Integer grp : cxstate.racemicFrags) {
                if (grp >= fragments.size())
                    continue;
                IAtomContainer mol = fragments.get(grp);
                if (mol == null)
                    continue;
                for (IStereoElement<?, ?> e : mol.stereoElements()) {
                    // maybe also Al and AT?
                    if (e.getConfigClass() == IStereoElement.TH) {
                        e.setGroupInfo(IStereoElement.GRP_RAC1);
                    }
                }
            }
        }

        // repartition/merge fragments
        if (cxstate.fragGroups != null) {

            // check validity of group
            boolean invalid = false;
            Set<Integer> visit = new HashSet<>();

            for (List<Integer> grouping : cxstate.fragGroups) {
                if (grouping.get(0) >= fragments.size())
                    continue;
                IAtomContainer dest = fragments.get(grouping.get(0));
                if (dest == null)
                    continue;
                if (!visit.add(grouping.get(0)))
                    invalid = true;
                for (int i = 1; i < grouping.size(); i++) {
                    if (!visit.add(grouping.get(i)))
                        invalid = true;
                    if (grouping.get(i) >= fragments.size())
                        continue;
                    IAtomContainer src = fragments.get(grouping.get(i));
                    if (src != null) {
                        dest.add(src);
                        roleMap.put(src, Collections.emptyList()); // no-role
                    }
                }
            }

            if (!invalid) {

                for (IReaction reaction : rxns.reactions()) {
                    reaction.getReactants().removeAllAtomContainers();
                    reaction.getAgents().removeAllAtomContainers();
                    reaction.getProducts().removeAllAtomContainers();
                }

                for (IAtomContainer mol : fragments) {
                    List<IReaction> reactions = molToReaction.get(mol);
                    List<Integer> roles = roleMap.get(mol);
                    if (roles.isEmpty())
                        continue;
                    for (int i = 0; i < reactions.size(); i++) {
                        IReaction rxn = reactions.get(i);
                        int role = roles.get(i);
                        switch (role) {
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
                                    List<IBond> bonds,
                                    Map<IAtom, IAtomContainer> atomToMol,
                                    CxSmilesState cxstate) throws InvalidSmilesException {

        // atom-labels - must be done first as we replace atoms
        if (cxstate.atomLabels != null) {
            for (Map.Entry<Integer, String> e : cxstate.atomLabels.entrySet()) {

                // bounds check
                if (e.getKey() >= atoms.size())
                    continue;

                IAtom old = atoms.get(e.getKey());
                IPseudoAtom pseudo;
                if (old instanceof IPseudoAtom) {
                    pseudo = (IPseudoAtom) old;
                } else {
                    // possibly a warning, is "CCO |$R$|" valid?
                    pseudo = bldr.newInstance(IPseudoAtom.class);
                    IAtomContainer mol = atomToMol.get(old);
                    AtomContainerManipulator.replaceAtomByAtom(mol, old, pseudo);
                    atomToMol.put(pseudo, mol);
                    atoms.set(e.getKey(), mol.getAtom(old.getIndex()));
                }

                String val = e.getValue();

                // specialised label handling
                if (val.endsWith("_p")) // pseudo label
                    val = val.substring(0, val.length() - 2);
                else if (val.startsWith("_AP")) // attachment point
                    pseudo.setAttachPointNum(parseIntSafe(val.substring(3)));

                pseudo.setLabel(val);
                pseudo.setAtomicNumber(0);
                pseudo.setImplicitHydrogenCount(0);
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

        if (cxstate.bondDisplay != null) {
            for (Map.Entry<Map.Entry<Integer, Integer>, IBond.Display> e : cxstate.bondDisplay) {
                Integer atmIdx = e.getKey().getKey();
                Integer bndIdx = e.getKey().getValue();
                IBond.Display style = e.getValue();
                IAtom atomToWedgeFrom = atmIdx < atoms.size() ? atoms.get(atmIdx) : null;
                IBond bondToWedge = bndIdx < bonds.size() ? bonds.get(bndIdx) : null;
                if (bondToWedge == null)
                    continue;
                if (atomToWedgeFrom == null)
                    continue;
                if (cxstate.atomCoords == null) {
                  handleRdkitAtropisomerExtension(atomToMol, atomToWedgeFrom, bondToWedge, style);
                } else {
                    if (bondToWedge.getBegin().equals(atomToWedgeFrom)) {
                        bondToWedge.setDisplay(style);
                    } else if (bondToWedge.getEnd().equals(atomToWedgeFrom)) {
                        if (style == IBond.Display.WedgeBegin)
                            bondToWedge.setDisplay(IBond.Display.WedgeEnd);
                        else if (style == IBond.Display.WedgedHashBegin)
                            bondToWedge.setDisplay(IBond.Display.WedgedHashEnd);
                        else
                            bondToWedge.setDisplay(style);
                    }
                }
            }
        }

        Map<IAtomContainer, List<Sgroup>> sgroupMap = new HashMap<>();
        Map<CxSmilesState.CxSgroup, Sgroup> sgroupRemap = new HashMap<>();

        // positional-variation
        if (cxstate.positionVar != null) {
            for (Map.Entry<Integer, List<Integer>> e : cxstate.positionVar.entrySet()) {
                Sgroup sgroup = new Sgroup();
                sgroup.setType(SgroupType.ExtMulticenter);
                IAtom beg = atoms.get(e.getKey());
                IAtomContainer mol = atomToMol.get(beg);
                List<IBond> connectedBonds = mol.getConnectedBondsList(beg);
                if (connectedBonds.isEmpty())
                    continue; // possibly okay
                sgroup.addAtom(beg);
                sgroup.addBond(connectedBonds.get(0));
                for (Integer endpt : e.getValue())
                    sgroup.addAtom(atoms.get(endpt));
                sgroupMap.computeIfAbsent(mol, k -> new ArrayList<>())
                         .add(sgroup);
            }
        }

        // ligand ordering
        if (cxstate.ligandOrdering != null) {
            for (Map.Entry<Integer, List<Integer>> e : cxstate.ligandOrdering.entrySet()) {
                Sgroup sgroup = new Sgroup();
                sgroup.setType(SgroupType.ExtAttachOrdering);
                IAtom beg = atoms.get(e.getKey());
                IAtomContainer mol = atomToMol.get(beg);
                List<IBond> connectedBonds = mol.getConnectedBondsList(beg);
                if (connectedBonds.isEmpty())
                    throw new InvalidSmilesException("CXSMILES LO: no bonds to order");
                if (connectedBonds.size() != e.getValue().size())
                    throw new InvalidSmilesException("CXSMILES LO: bond count and ordering count was different");
                sgroup.addAtom(beg);
                for (Integer endpt : e.getValue()) {
                    IBond bond = beg.getBond(atoms.get(endpt));
                    if (bond == null)
                        throw new InvalidSmilesException("CXSMILES LO: defined ordering to non-existant bond");
                    sgroup.addBond(bond);
                }
                sgroupMap.computeIfAbsent(mol, k -> new ArrayList<>())
                         .add(sgroup);
            }
        }

        // polymer Sgroups
        if (cxstate.mysgroups != null) {

            PolySgroup:
            for (CxSmilesState.CxSgroup cxsgroup : cxstate.mysgroups) {
                if (!(cxsgroup instanceof CxPolymerSgroup))
                    continue;
                CxPolymerSgroup psgroup = (CxPolymerSgroup) cxsgroup;
                Sgroup sgroup = new Sgroup();

                Set<IAtom> atomset = new HashSet<>();
                IAtomContainer mol = null;
                for (Integer idx : psgroup.atoms) {
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
                        IAtom nbor = bond.getOther(atom);
                        if (!atomset.contains(nbor)) {
                            boolean crossing = true;

                            // check for variable attachments see https://github.com/cdk/depict/issues/36
                            if (cxstate.positionVar != null) {
                                List<Integer> ends = cxstate.positionVar.get(mol.indexOf(nbor));
                                if (ends != null) {
                                    for (Integer end : ends) {
                                        if (atomset.contains(mol.getAtom(end)))
                                            crossing = false;
                                    }
                                }
                            }

                            if (crossing)
                                sgroup.addBond(bond);
                        }
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

                sgroupMap.computeIfAbsent(mol, k -> new ArrayList<>())
                         .add(sgroup);
                // CxState Sgroup => CDK Sgroup lookup
                sgroupRemap.put(psgroup, sgroup);
            }
        }

        // data sgroups
        if (cxstate.mysgroups != null) {

            DataSgroup:
            for (CxSmilesState.CxSgroup cxsgroup : cxstate.mysgroups) {
                if (!(cxsgroup instanceof CxDataSgroup))
                    continue;
                CxDataSgroup dsgroup = (CxDataSgroup) cxsgroup;

                Set<IAtom> atomset = new HashSet<>();
                IAtomContainer mol = null;
                for (Integer idx : dsgroup.atoms) {
                    if (idx >= atoms.size())
                        continue;
                    IAtom atom = atoms.get(idx);
                    IAtomContainer amol = atomToMol.get(atom);

                    if (mol == null)
                        mol = amol;
                    else if (amol != mol)
                        continue DataSgroup;

                    atomset.add(atom);
                }

                if (dsgroup.field != null && dsgroup.field.startsWith("cdk:")) {
                    chemObj.setProperty(dsgroup.field, dsgroup.value);
                } else {
                    Sgroup cdkSgroup = new Sgroup();
                    cdkSgroup.setType(SgroupType.CtabData);
                    for (IAtom atom : atomset)
                        cdkSgroup.addAtom(atom);
                    cdkSgroup.putValue(SgroupKey.DataFieldName, dsgroup.field);
                    cdkSgroup.putValue(SgroupKey.DataFieldUnits, dsgroup.unit);
                    cdkSgroup.putValue(SgroupKey.Data, dsgroup.value);
                    sgroupRemap.put(dsgroup, cdkSgroup);
                    if (mol != null)
                        sgroupMap.computeIfAbsent(mol, k -> new ArrayList<>())
                                 .add(cdkSgroup);
                    else if (chemObj instanceof IAtomContainer)
                        sgroupMap.computeIfAbsent((IAtomContainer) chemObj, k -> new ArrayList<>())
                                 .add(cdkSgroup);
                }
            }
        }

        if (cxstate.mysgroups != null) {
            for (CxSmilesState.CxSgroup parent : cxstate.mysgroups) {
                Sgroup cdkParent = sgroupRemap.get(parent);
                if (cdkParent == null)
                    continue;
                for (CxSmilesState.CxSgroup child : parent.children) {
                    Sgroup cdkChild = sgroupRemap.get(child);
                    if (cdkChild == null)
                        continue;
                    cdkChild.addParent(cdkParent);
                }
            }
        }

        // IMPORTANT: state.racemicComps is handled in the fragment grouping step
        if (cxstate.racemic) {
            if (chemObj instanceof IAtomContainer) {
                for (IStereoElement<?, ?> e : ((IAtomContainer) chemObj).stereoElements()) {
                    // maybe also Al and AT?
                    if (e.getConfigClass() == IStereoElement.TH) {
                        e.setGroupInfo(IStereoElement.GRP_RAC1);
                    }
                }
            } else if (chemObj instanceof IReaction) {
                for (IAtomContainer mol : ReactionManipulator.getAllAtomContainers((IReaction) chemObj)) {
                    for (IStereoElement<?, ?> e : mol.stereoElements()) {
                        // maybe also Al and AT?
                        if (e.getConfigClass() == IStereoElement.TH) {
                            e.setGroupInfo(IStereoElement.GRP_RAC1);
                        }
                    }
                }
            }
        }


        if (cxstate.stereoGrps != null) {
            for (Map.Entry<Integer, Integer> e : cxstate.stereoGrps.entrySet()) {
                IAtom atm = atoms.get(e.getKey());
                IAtomContainer mol = atomToMol.get(atm);
                for (IStereoElement<?, ?> stereo : mol.stereoElements()) {
                    // maybe also Al and AT?
                    if (stereo.getConfigClass() == IStereoElement.TH &&
                        stereo.getFocus().equals(atm)) {
                        stereo.setGroupInfo(e.getValue());
                    }
                }
            }
        }

        // assign Sgroups
        for (Map.Entry<IAtomContainer, List<Sgroup>> e : sgroupMap.entrySet())
            e.getKey().setProperty(CDKConstants.CTAB_SGROUPS, new ArrayList<>(e.getValue()));
    }


    /**
     * This logic is used to allow reading of Atropisomer configurations (e.g.
     * BiNOL) from CXSMILES.
     *
     * @param atomToMol mapping from atom to the containing molecule
     * @param atomToWedgeFrom atom at small end of wedge
     * @param bondToWedge the bond being wedged
     * @param style the wedge style
     */
    private void handleRdkitAtropisomerExtension(Map<IAtom, IAtomContainer> atomToMol,
                                                 IAtom atomToWedgeFrom,
                                                 IBond bondToWedge,
                                                 IBond.Display style) {
        // check for RDKit atropisomers
        if (!isAtropisomerAtom(atomToWedgeFrom))
            return;

        // We need ring flags set, this is not cached but
        // (hopefully) there are only a few wedges
        Cycles.markRingAtomsAndBonds(atomToMol.get(atomToWedgeFrom));

        // Find the bond to apply the atropisomerism to
        IBond atropisomerBond = null;
        for (IBond b : atomToWedgeFrom.bonds()) {
            if (!b.equals(bondToWedge) &&
                b.getOrder() == IBond.Order.SINGLE &&
                isAtropisomerAtom(b.getOther(atomToWedgeFrom)) &&
                Cycles.smallRingSize(b, 7) == 0) {
                if (atropisomerBond != null) {
                    atropisomerBond = null;
                    break;
                }
                atropisomerBond = b;
            }
        }

        if (atropisomerBond != null) {
            IAtom atBeg = atropisomerBond.getBegin();
            IAtom atEnd = atropisomerBond.getEnd();
            if (bondToWedge.contains(atBeg) ||
                bondToWedge.contains(atEnd)) {
                List<IAtom> storeOrder = new ArrayList<>();

                for (IBond b : atBeg.bonds()) {
                    if (b.equals(atropisomerBond))
                        continue;
                    IAtom nbor = b.getOther(atBeg);
                    storeOrder.add(nbor);
                }
                for (IBond b : atEnd.bonds()) {
                    if (b.equals(atropisomerBond))
                        continue;
                    IAtom nbor = b.getOther(atEnd);
                    storeOrder.add(nbor);
                }

                if (storeOrder.size() == 4) {

                    if (storeOrder.get(0).getIndex() > storeOrder.get(1).getIndex())
                        swap(storeOrder, 0, 1);
                    if (storeOrder.get(2).getIndex() > storeOrder.get(3).getIndex())
                        swap(storeOrder, 2, 3);

                    IBond.Display bond1dir = IBond.Display.Solid;
                    IBond.Display bond2dir = IBond.Display.Solid;
                    if (bondToWedge.contains(atBeg)) {
                        if (bondToWedge.contains(storeOrder.get(0))) {
                            bond1dir = style;
                        } else if (bondToWedge.contains(storeOrder.get(1))) {
                            bond1dir = flip(style);
                        }
                    } else if (bondToWedge.contains(atEnd)) {
                        if (bondToWedge.contains(storeOrder.get(2))) {
                            bond2dir = style;
                        } else if (bondToWedge.contains(storeOrder.get(3))) {
                            bond2dir = flip(style);
                        }
                    }

                    int cfg = 0;
                    if (bond1dir == IBond.Display.WedgeBegin ||
                        bond2dir == IBond.Display.WedgedHashBegin) {
                        cfg = IStereoElement.LEFT;
                    } else if (bond1dir == IBond.Display.WedgedHashBegin ||
                               bond2dir == IBond.Display.WedgeBegin) {
                        cfg = IStereoElement.RIGHT;
                    }

                    IAtomContainer mol = atomToMol.get(atomToWedgeFrom);
                    mol.addStereoElement(new Atropisomeric(atropisomerBond,
                                                           storeOrder.toArray(new IAtom[4]),
                                                           cfg));
                }
            }
        }
    }

    private static void swap(List<IAtom> atoms, int i, int j) {
        IAtom tmp = atoms.get(i);
        atoms.set(i, atoms.get(j));
        atoms.set(j, tmp);
    }

    private static IBond.Display flip(IBond.Display disp) {
        if (disp == IBond.Display.WedgeBegin)
            return IBond.Display.WedgedHashBegin;
        else if (disp == IBond.Display.WedgeEnd)
            return IBond.Display.WedgedHashEnd;
        else if (disp == IBond.Display.WedgedHashBegin)
            return IBond.Display.WedgeBegin;
        else if (disp == IBond.Display.WedgedHashEnd)
            return IBond.Display.WedgeEnd;
        return IBond.Display.Solid;
    }

    // Check if an atom can potentially by part of an atropisomer
    private boolean isAtropisomerAtom(IAtom atom) {
        if (atom.getBondCount() != 3)
            return false;
        if (atom.isAromatic())
            return true;
        int dbcount = 0;
        for (IBond bond : atom.bonds()) {
            if (bond.getOrder() == IBond.Order.DOUBLE)
                dbcount++;
        }
        return dbcount == 1 || (dbcount == 0 &&
                                atom.getFormalCharge() == 0 &&
                                (atom.getAtomicNumber() == IAtom.N ||
                                 atom.getAtomicNumber() == IAtom.P ||
                                 atom.getAtomicNumber() == IAtom.As));
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
