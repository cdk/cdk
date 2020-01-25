/* Copyright (C) 2002-2007  Oliver Horlacher
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
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
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.ConnectedComponents;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.ISingleElectron;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.smiles.CxSmilesState.CxPolymerSgroup;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import uk.ac.ebi.beam.Functions;
import uk.ac.ebi.beam.Graph;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SMILES {@cdk.cite WEI88, WEI89} provides a compact representation of
 * chemical structures and reactions.
 * <br>
 * Different <i>flavours</i> of SMILES can be generated and are fully configurable.
 * The standard flavours of SMILES defined by Daylight are:
 * <ul>
 *     <li><b>Generic</b> - non-canonical SMILES string, different atom ordering
 *         produces different SMILES. No isotope or stereochemistry encoded.
 *         </li>
 *     <li><b>Unique</b> - canonical SMILES string, different atom ordering
 *         produces the same* SMILES. No isotope or stereochemistry encoded.
 *         </li>
 *     <li><b>Isomeric</b> - non-canonical SMILES string, different atom ordering
 *         produces different SMILES. Isotope and stereochemistry is encoded.
 *         </li>
 *     <li><b>Absolute</b> - canonical SMILES string, different atom ordering
 *         produces the same SMILES. Isotope and stereochemistry is encoded.</li>
 * </ul>
 *
 * To output a given flavour the flags in {@link SmiFlavor} are used:
 *
 * <pre>
 * SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Isomeric);
 * </pre>
 * {@link SmiFlavor} provides more fine grained control, for example,
 * for the following is equivalent to {@link SmiFlavor#Isomeric}:
 * <pre>
 * SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo |
 *                                              SmiFlavor.AtomicMass);
 * </pre>
 * Bitwise logic can be used such that we can remove options:
 * {@link SmiFlavor#Isomeric} <code>^</code> {@link SmiFlavor#AtomicMass}
 * will generate isomeric SMILES without atomic mass.
 *
 *
 * 
 * A generator instance is created using one of the static methods, the SMILES
 * are then created by invoking {@link #create(IAtomContainer)}.
 * <blockquote><pre>
 * IAtomContainer  ethanol = ...;
 * SmilesGenerator sg      = new SmilesGenerator(SmiFlavor.Generic);
 * String          smi     = sg.create(ethanol); // CCO, C(C)O, C(O)C, or OCC
 *
 * SmilesGenerator sg      = new SmilesGenerator(SmiFlavor.Unique);
 * String          smi     = sg.create(ethanol); // only CCO
 * </pre></blockquote>
 *
 * 
 *
 * The isomeric and absolute generator encode tetrahedral and double bond
 * stereochemistry using {@link org.openscience.cdk.interfaces.IStereoElement}s
 * provided on the {@link IAtomContainer}. If stereochemistry is not being
 * written it may need to be determined from 2D/3D coordinates using
 * {@link org.openscience.cdk.stereo.StereoElementFactory}.
 *
 * 
 *
 * By default the generator will not write aromatic SMILES. Kekulé SMILES are
 * generally preferred for compatibility and aromaticity can easily be
 * re-perceived by most tool kits whilst kekulisation may fail. If you
 * really want aromatic SMILES the following code demonstrates
 *
 * <blockquote><pre>
 * IAtomContainer  benzene = ...;
 *
 * // 'benzene' molecule has no arom flags, we always get Kekulé output
 * SmilesGenerator sg      = new SmilesGenerator(SmiFlavor.Generic);
 * String          smi     = sg.create(benzene); // C1=CC=CC=C1
 *
 * SmilesGenerator sg      = new SmilesGenerator(SmiFlavor.Generic |
 *                                               SmiFlavor.UseAromaticSymbols);
 * String          smi     = sg.create(benzene); // C1=CC=CC=C1 flags not set!
 *
 * // Note, in practice we'd use an aromaticity algorithm
 * for (IAtom a : benzene.atoms())
 *     a.setIsAromatic(true);
 * for (IBond b : benzene.bond())
 *     a.setIsAromatic(true);
 *
 * // 'benzene' molecule now has arom flags, we always get aromatic SMILES if we request it
 * SmilesGenerator sg      = new SmilesGenerator(SmiFlavor.Generic);
 * String          smi     = sg.create(benzene); // C1=CC=CC=C1
 *
 * SmilesGenerator sg      = new SmilesGenerator(SmiFlavor.Generic |
 *                                               SmiFlavor.UseAromaticSymbols);
 * String          smi     = sg.create(benzene); // c1ccccc1
 * </pre></blockquote>
 * 
 *
 * It can be useful to know the output order of SMILES. On input the order of the atoms
 * reflects the atom index. If we know this order we can refer to atoms by index and
 * associate data with the SMILES string.
 * The output order is obtained by parsing in an auxiliary array during creation. The
 * following snippet demonstrates how we can write coordinates in order.
 *
 * <blockquote><pre>{@code
 * IAtomContainer  mol = ...;
 * SmilesGenerator sg  = new SmilesGenerator(SmiFlavor.Generic);
 *
 * int   n     = mol.getAtomCount();
 * int[] order = new int[n];
 *
 * // the order array is filled up as the SMILES is generated
 * String smi = sg.create(mol, order);
 *
 * // load the coordinates array such that they are in the order the atoms
 * // are read when parsing the SMILES
 * Point2d[] coords = new Point2d[mol.getAtomCount()];
 * for (int i = 0; i < coords.length; i++)
 *     coords[order[i]] = container.getAtom(i).getPoint2d();
 *
 * // SMILES string suffixed by the coordinates
 * String smi2d = smi + " " + Arrays.toString(coords);
 *
 * }</pre></blockquote>
 *
 * Using the output order of SMILES forms the basis of
 * <a href="https://www.chemaxon.com/marvin-archive/latest/help/formats/cxsmiles-doc.html">
 * ChemAxon Extended SMILES (CXSMILES)</a> which can also be generated. Extended SMILES
 * allows additional structure data to be serialized including, atom labels/values, fragment
 * grouping (for salts in reactions), polymer repeats, multi center bonds, and coordinates.
 * The CXSMILES layer is appended after the SMILES so that parser which don't interpret it
 * can ignore it.
 *
 * The two aggregate flavours are {@link SmiFlavor#CxSmiles} and {@link SmiFlavor#CxSmilesWithCoords}.
 * As with other flavours, fine grain control is possible {@link SmiFlavor}.
 * <br>
 * <b>*</b> the unique SMILES generation uses a fast equitable labelling procedure
 *   and as such there are some structures which may not be unique. The number
 *   of such structures is generally minimal.
 *
 * @author         Oliver Horlacher
 * @author         Stefan Kuhn (chiral smiles)
 * @author         John May
 * @cdk.keyword    SMILES, generator
 * @cdk.module     smiles
 * @cdk.githash
 *
 * @see org.openscience.cdk.aromaticity.Aromaticity
 * @see org.openscience.cdk.stereo.Stereocenters
 * @see org.openscience.cdk.stereo.StereoElementFactory
 * @see org.openscience.cdk.interfaces.ITetrahedralChirality
 * @see org.openscience.cdk.interfaces.IDoubleBondStereochemistry
 * @see org.openscience.cdk.CDKConstants
 * @see SmilesParser
 */
public final class SmilesGenerator {

    private final int       flavour;

    /**
     * Create the SMILES generator, the default output is described by: {@link SmiFlavor#Default}
     * but is best to choose/set this flavor.
     *
     * @see SmiFlavor#Default
     * @deprecated use {@link #SmilesGenerator(int)} configuring with {@link SmiFlavor}.
     */
    @Deprecated
    public SmilesGenerator() {
        this(SmiFlavor.Default);
    }

    /**
     * Create a SMILES generator with the specified {@link SmiFlavor}.
     *
     * <blockquote><pre>
     * SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.Stereo |
     *                                              SmiFlavor.Canonical);
     * </pre></blockquote>
     *
     * @param flavour SMILES flavour flags {@link SmiFlavor}
     */
    public SmilesGenerator(int flavour) {
        this.flavour   = flavour;
    }

    /**
     * Derived a new generator that writes aromatic atoms in lower case.
     * The preferred way of doing this is now to use the {@link #SmilesGenerator(int)} constructor:
     *
     * <pre>
     * SmilesGenerator smigen = new SmilesGenerator(SmiFlavor.UseAromaticSymbols);
     * </pre>
     *
     * @return a generator for aromatic SMILES
     * @deprecated configure with {@link SmiFlavor}
     */
    public SmilesGenerator aromatic() {
        return new SmilesGenerator(this.flavour | SmiFlavor.UseAromaticSymbols);
    }

    /**
     * Specifies that the generator should write atom classes in SMILES. Atom
     * classes are provided by the {@link org.openscience.cdk.CDKConstants#ATOM_ATOM_MAPPING}
     * property. This method returns a new SmilesGenerator to use.
     *
     * <blockquote><pre>
     * IAtomContainer  container = ...;
     * SmilesGenerator smilesGen = SmilesGenerator.unique()
     *                                            .atomClasses();
     * smilesGen.createSMILES(container); // C[CH2:4]O second atom has class = 4
     * </pre></blockquote>
     *
     * @return a generator for SMILES with atom classes
     * @deprecated configure with {@link SmiFlavor}
     */
    @Deprecated
    public SmilesGenerator withAtomClasses() {
        return new SmilesGenerator(this.flavour | SmiFlavor.AtomAtomMap);
    }

    /**
     * Create a generator for generic SMILES. Generic SMILES are
     * non-canonical and useful for storing information when it is not used
     * as an index (i.e. unique keys). The generated SMILES is dependant on
     * the input order of the atoms.
     *
     * @return a new arbitrary SMILES generator
     */
    public static SmilesGenerator generic() {
        return new SmilesGenerator(SmiFlavor.Generic);
    }

    /**
     * Convenience method for creating an isomeric generator. Isomeric SMILES
     * are non-unique but contain isotope numbers (e.g. {@code [13C]}) and
     * stereo-chemistry.
     *
     * @return a new isomeric SMILES generator
     */
    public static SmilesGenerator isomeric() {
        return new SmilesGenerator(SmiFlavor.Isomeric);
    }

    /**
     * Create a unique SMILES generator. Unique SMILES use a fast canonisation
     * algorithm but does not encode isotope or stereo-chemistry.
     *
     * @return a new unique SMILES generator
     */
    public static SmilesGenerator unique() {
        return new SmilesGenerator(SmiFlavor.Unique);
    }

    /**
     * Create a absolute SMILES generator. Unique SMILES uses the InChI to
     * canonise SMILES and encodes isotope or stereo-chemistry. The InChI
     * module is not a dependency of the SMILES module but should be present
     * on the classpath when generation absolute SMILES.
     *
     * @return a new absolute SMILES generator
     */
    public static SmilesGenerator absolute() {
        return new SmilesGenerator(SmiFlavor.Absolute);
    }

    /**
     * Create a SMILES string for the provided molecule.
     *
     * @param molecule the molecule to create the SMILES of
     * @return a SMILES string
     * @deprecated use #create
     */
    @Deprecated
    public String createSMILES(IAtomContainer molecule) {
        try {
            return create(molecule);
        } catch (CDKException e) {
            throw new IllegalArgumentException(
                    "SMILES could not be generated, please use the new API method 'create()'"
                            + "to catch the checked exception", e);
        }
    }

    /**
     * Create a SMILES string for the provided reaction.
     *
     * @param reaction the reaction to create the SMILES of
     * @return a reaction SMILES string
     * @deprecated use #createReactionSMILES
     */
    @Deprecated
    public String createSMILES(IReaction reaction) {
        try {
            return createReactionSMILES(reaction);
        } catch (CDKException e) {
            throw new IllegalArgumentException(
                    "SMILES could not be generated, please use the new API method 'create()'"
                            + "to catch the checked exception", e);
        }
    }

    /**
     * Generate SMILES for the provided {@code molecule}.
     *
     * @param molecule The molecule to evaluate
     * @return the SMILES string
     * @throws CDKException SMILES could not be created
     */
    public String create(IAtomContainer molecule) throws CDKException {
        return create(molecule, new int[molecule.getAtomCount()]);
    }

    /**
     * Creates a SMILES string of the flavour specified in the constructor
     * and write the output order to the provided array.
     * <br>
     * The output order allows one to arrange auxiliary atom data in the
     * order that a SMILES string will be read. A simple example is seen below
     * where 2D coordinates are stored with a SMILES string. This method
     * forms the basis of CXSMILES.
     *
     * <blockquote><pre>{@code
     * IAtomContainer  mol = ...;
     * SmilesGenerator sg  = new SmilesGenerator();
     *
     * int   n     = mol.getAtomCount();
     * int[] order = new int[n];
     *
     * // the order array is filled up as the SMILES is generated
     * String smi = sg.create(mol, order);
     *
     * // load the coordinates array such that they are in the order the atoms
     * // are read when parsing the SMILES
     * Point2d[] coords = new Point2d[mol.getAtomCount()];
     * for (int i = 0; i < coords.length; i++)
     *     coords[order[i]] = container.getAtom(i).getPoint2d();
     *
     * // SMILES string suffixed by the coordinates
     * String smi2d = smi + " " + Arrays.toString(coords);
     *
     * }</pre></blockquote>
     *
     * @param molecule the molecule to write
     * @param order    array to store the output order of atoms
     * @return the SMILES string
     * @throws CDKException SMILES could not be created
     */
    public String create(IAtomContainer molecule, int[] order) throws CDKException {
        return create(molecule, this.flavour, order);
    }

    /**
     * Creates a SMILES string of the flavour specified as a parameter
     * and write the output order to the provided array.
     * <br>
     * The output order allows one to arrange auxiliary atom data in the
     * order that a SMILES string will be read. A simple example is seen below
     * where 2D coordinates are stored with a SMILES string. This method
     * forms the basis of CXSMILES.
     *
     * <blockquote><pre>{@code
     * IAtomContainer  mol = ...;
     * SmilesGenerator sg  = new SmilesGenerator();
     *
     * int   n     = mol.getAtomCount();
     * int[] order = new int[n];
     *
     * // the order array is filled up as the SMILES is generated
     * String smi = sg.create(mol, order);
     *
     * // load the coordinates array such that they are in the order the atoms
     * // are read when parsing the SMILES
     * Point2d[] coords = new Point2d[mol.getAtomCount()];
     * for (int i = 0; i < coords.length; i++)
     *     coords[order[i]] = container.getAtom(i).getPoint2d();
     *
     * // SMILES string suffixed by the coordinates
     * String smi2d = smi + " " + Arrays.toString(coords);
     *
     * }</pre></blockquote>
     *
     * @param molecule the molecule to write
     * @param order    array to store the output order of atoms
     * @return the SMILES string
     * @throws CDKException a valid SMILES could not be created
     */
    public static String create(IAtomContainer molecule, int flavour, int[] order) throws CDKException {
        try {
            if (order.length != molecule.getAtomCount())
                throw new IllegalArgumentException("the array for storing output order should be"
                        + "the same length as the number of atoms");

            Graph g = CDKToBeam.toBeamGraph(molecule, flavour);

            // apply the canonical labelling
            if (SmiFlavor.isSet(flavour, SmiFlavor.Canonical)) {

                // determine the output order
                int[] labels = labels(flavour, molecule);

                g = g.permute(labels);

                if ((flavour & SmiFlavor.AtomAtomMapRenumber) == SmiFlavor.AtomAtomMapRenumber)
                    g = Functions.renumberAtomMaps(g);

                if (!SmiFlavor.isSet(flavour, SmiFlavor.UseAromaticSymbols))
                    g = g.resonate();

                if (SmiFlavor.isSet(flavour, SmiFlavor.StereoCisTrans)) {

                    // FIXME: required to ensure canonical double bond labelling
                    g.sort(new Graph.VisitHighOrderFirst());

                    // canonical double-bond stereo, output be C/C=C/C or C\C=C\C
                    // and we need to normalise to one
                    g = Functions.normaliseDirectionalLabels(g);

                    // visit double bonds first, prefer C1=CC=C1 to C=1C=CC1
                    // visit hydrogens first
                    g.sort(new Graph.VisitHighOrderFirst()).sort(new Graph.VisitHydrogenFirst());
                }

                String smiles = g.toSmiles(order);

                // the SMILES has been generated on a reordered molecule, transform
                // the ordering
                int[] canorder = new int[order.length];
                for (int i = 0; i < labels.length; i++)
                    canorder[i] = order[labels[i]];
                System.arraycopy(canorder, 0, order, 0, order.length);

                if (SmiFlavor.isSet(flavour, SmiFlavor.CxSmilesWithCoords)) {
                    smiles += CxSmilesGenerator.generate(getCxSmilesState(flavour, molecule),
                                                         flavour, null, order);
                }

                return smiles;
            } else {
                String smiles = g.toSmiles(order);

                if (SmiFlavor.isSet(flavour, SmiFlavor.CxSmilesWithCoords)) {
                    smiles += CxSmilesGenerator.generate(getCxSmilesState(flavour, molecule), flavour, null, order);
                }

                return smiles;
            }
        } catch (IOException e) {
            throw new CDKException(e.getMessage());
        }
    }

    /**
     * Create a SMILES for a reaction.
     *
     * @param reaction CDK reaction instance
     * @return reaction SMILES
     * @deprecated use {@link #create(IAtomContainer)}
     * @throws CDKException a valid SMILES could not be created
     */
    @Deprecated
    public String createReactionSMILES(IReaction reaction) throws CDKException {
        return create(reaction);
    }

    /**
     * Create a SMILES for a reaction of the flavour specified in the constructor.
     *
     * @param reaction CDK reaction instance
     * @return reaction SMILES
     */
    public String create(IReaction reaction) throws CDKException {
        return create(reaction, new int[ReactionManipulator.getAtomCount(reaction)]);
    }

    // utility method that safely collects the Sgroup from a molecule
    private void safeAddSgroups(List<Sgroup> sgroups, IAtomContainer mol) {
        List<Sgroup> molSgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (molSgroups != null)
            sgroups.addAll(molSgroups);
    }

    /**
     * Create a SMILES for a reaction of the flavour specified in the constructor and
     * write the output order to the provided array.
     *
     * @param reaction CDK reaction instance
     * @return reaction SMILES
     */
    public String create(IReaction reaction, int[] ordering) throws CDKException {

        IAtomContainerSet reactants = reaction.getReactants();
        IAtomContainerSet agents    = reaction.getAgents();
        IAtomContainerSet products  = reaction.getProducts();

        IAtomContainer    reactantPart = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer    agentPart    = reaction.getBuilder().newInstance(IAtomContainer.class);
        IAtomContainer    productPart  = reaction.getBuilder().newInstance(IAtomContainer.class);

        List<Sgroup> sgroups = new ArrayList<>();

        for (IAtomContainer reactant : reactants.atomContainers()) {
            reactantPart.add(reactant);
            safeAddSgroups(sgroups, reactant);
        }
        for (IAtomContainer agent : agents.atomContainers()) {
            agentPart.add(agent);
            safeAddSgroups(sgroups, agent);
        }
        for (IAtomContainer product : products.atomContainers()) {
            productPart.add(product);
            safeAddSgroups(sgroups, product);
        }

        int[] reactantOrder = new int[reactantPart.getAtomCount()];
        int[] agentOrder    = new int[agentPart.getAtomCount()];
        int[] productOrder  = new int[productPart.getAtomCount()];

        final int expectedSize = reactantOrder.length + agentOrder.length + productOrder.length;
        if (expectedSize != ordering.length) {
            throw new CDKException("Output ordering array does not have correct amount of space: " + ordering.length +
                                   " expected: " + expectedSize);
        }

        // we need to make sure we generate without the CXSMILES layers
        String smi = create(reactantPart, flavour &~ SmiFlavor.CxSmilesWithCoords, reactantOrder) + ">" +
                     create(agentPart, flavour &~ SmiFlavor.CxSmilesWithCoords, agentOrder) + ">" +
                     create(productPart, flavour &~ SmiFlavor.CxSmilesWithCoords, productOrder);

        // copy ordering back to unified array and adjust values
        int agentBeg = reactantOrder.length;
        int agentEnd = reactantOrder.length + agentOrder.length;
        int prodEnd  = reactantOrder.length + agentOrder.length + productOrder.length;
        System.arraycopy(reactantOrder, 0, ordering, 0, agentBeg);
        System.arraycopy(agentOrder, 0, ordering, agentBeg, agentEnd-agentBeg);
        System.arraycopy(productOrder, 0, ordering, agentEnd, prodEnd-agentEnd);
        for (int i = agentBeg; i < agentEnd; i++)
            ordering[i] += agentBeg;
        for (int i = agentEnd; i < prodEnd; i++)
            ordering[i] += agentEnd;

        if (SmiFlavor.isSet(flavour, SmiFlavor.CxSmilesWithCoords)) {
            IAtomContainer unified = reaction.getBuilder().newInstance(IAtomContainer.class);
            unified.add(reactantPart);
            unified.add(agentPart);
            unified.add(productPart);
            unified.setProperty(CDKConstants.CTAB_SGROUPS, sgroups);

            // base CXSMILES state information
            final CxSmilesState cxstate = getCxSmilesState(flavour, unified);

            int[] components = null;

            // extra state info on fragment grouping, specific to reactions
            if (SmiFlavor.isSet(flavour, SmiFlavor.CxFragmentGroup)) {

                cxstate.fragGroups = new ArrayList<>();

                // calculate the connected components
                components = new ConnectedComponents(GraphUtil.toAdjList(unified)).components();

                // AtomContainerSet is ordered so this is safe, it was actually a set we
                // would need some extra data structures
                Set<Integer> tmp = new HashSet<>();
                int beg = 0, end = 0;
                for (IAtomContainer mol : reactants.atomContainers()) {
                    end = end + mol.getAtomCount();
                    tmp.clear();
                    for (int i = beg; i < end; i++)
                        tmp.add(components[i]);
                    if (tmp.size() > 1)
                        cxstate.fragGroups.add(new ArrayList<>(tmp));
                    beg = end;
                }
                for (IAtomContainer mol : agents.atomContainers()) {
                    end = end + mol.getAtomCount();
                    tmp.clear();
                    for (int i = beg; i < end; i++)
                        tmp.add(components[i]);
                    if (tmp.size() > 1)
                        cxstate.fragGroups.add(new ArrayList<>(tmp));
                    beg = end;
                }
                for (IAtomContainer mol : products.atomContainers()) {
                    end = end + mol.getAtomCount();
                    tmp.clear();
                    for (int i = beg; i < end; i++)
                        tmp.add(components[i]);
                    if (tmp.size() > 1)
                        cxstate.fragGroups.add(new ArrayList<>(tmp));
                    beg = end;
                }

            }


            smi += CxSmilesGenerator.generate(cxstate, flavour, components, ordering);
        }

        return smi;
    }

    /**
     * Indicates whether output should be an aromatic SMILES.
     *
     * @param useAromaticityFlag if false only SP2-hybridized atoms will be lower case (default),
     * true=SP2 or aromaticity trigger lower case
     * @deprecated since 1.5.6, use {@link #aromatic} - invoking this method
     *             does nothing
     */
    @Deprecated
    public void setUseAromaticityFlag(boolean useAromaticityFlag) {

    }

    /**
     * Given a molecule (possibly disconnected) compute the labels which
     * would order the atoms by increasing canonical labelling. If the SMILES
     * are isomeric (i.e. stereo and isotope specific) the InChI numbers are
     * used. These numbers are loaded via reflection and the 'cdk-inchi' module
     * should be present on the classpath.
     *
     * @param molecule the molecule to
     * @return the permutation
     * @see Canon
     */
    private static int[] labels(int flavour, final IAtomContainer molecule) throws CDKException {
        // FIXME: use SmiOpt.InChiLabelling
        long[] labels = SmiFlavor.isSet(flavour, SmiFlavor.Isomeric) ? inchiNumbers(molecule)
                : Canon.label(molecule,
                              GraphUtil.toAdjList(molecule),
                              createComparator(molecule, flavour));
        int[] cpy = new int[labels.length];
        for (int i = 0; i < labels.length; i++)
            cpy[i] = (int) labels[i] - 1;
        return cpy;
    }

    /**
     * Obtain the InChI numbering for canonising SMILES. The cdk-smiles module
     * does not and should not depend on cdk-inchi and so the numbers are loaded
     * via reflection. If the class cannot be found on the classpath an
     * exception is thrown.
     *
     * @param container a structure
     * @return the inchi numbers
     * @throws CDKException the inchi numbers could not be obtained
     */
    private static long[] inchiNumbers(IAtomContainer container) throws CDKException {
        // TODO: create an interface so we don't have to dynamically load the
        // class each time
        String cname = "org.openscience.cdk.graph.invariant.InChINumbersTools";
        String mname = "getUSmilesNumbers";

        List<IAtom> rgrps = getRgrps(container, Elements.Rutherfordium);
        for (IAtom rgrp : rgrps) {
            rgrp.setAtomicNumber(Elements.Rutherfordium.number());
            rgrp.setSymbol(Elements.Rutherfordium.symbol());
        }

        try {
            Class<?> c = Class.forName(cname);
            Method method = c.getDeclaredMethod("getUSmilesNumbers", IAtomContainer.class);
            return (long[]) method.invoke(c, container);
        } catch (ClassNotFoundException e) {
            throw new CDKException("The cdk-inchi module is not loaded,"
                    + " this module is need when generating absolute SMILES.");
        } catch (NoSuchMethodException e) {
            throw new CDKException("The method " + mname + " was not found", e);
        } catch (InvocationTargetException e) {
            throw new CDKException("An InChI could not be generated and used to canonise SMILES: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new CDKException("Could not access method to obtain InChI numbers.");
        } finally {
            for (IAtom rgrp : rgrps) {
                rgrp.setAtomicNumber(Elements.Unknown.number());
                rgrp.setSymbol("*");
            }
        }
    }

    private static List<IAtom> getRgrps(IAtomContainer container, Elements reversed) {
        List<IAtom> res = new ArrayList<>();
        for (IAtom atom : container.atoms()) {
            if (atom.getAtomicNumber() == 0) {
                res.add(atom);
            } else if (atom.getAtomicNumber() == reversed.number()) {
                return Collections.emptyList();
            }
        }
        return res;
    }

    // utility safety check to guard against invalid state
    private static Integer ensureNotNull(Integer x) {
        if (x == null)
            throw new IllegalStateException("Inconsistent CXSMILES state! Check the SGroups.");
        return x;
    }

    // utility method maps the atoms to their indices using the provided map.
    private static List<Integer> toAtomIdxs(Collection<IAtom> atoms, Map<IAtom, Integer> atomidx) {
        List<Integer> idxs = new ArrayList<>(atoms.size());
        for (IAtom atom : atoms)
            idxs.add(ensureNotNull(atomidx.get(atom)));
        return idxs;
    }

    // Creates a CxSmilesState from a molecule with atom labels, repeat units, multicenter bonds etc
    private static CxSmilesState getCxSmilesState(int flavour, IAtomContainer mol) {
        CxSmilesState state = new CxSmilesState();
        state.atomCoords = new ArrayList<>();
        state.coordFlag = false;

        // set the atom labels, values, and coordinates,
        // and build the atom->idx map required by other parts
        Map<IAtom, Integer> atomidx = new HashMap<>();
        for (int idx = 0; idx < mol.getAtomCount(); idx++) {
            IAtom atom = mol.getAtom(idx);
            if (atom instanceof IPseudoAtom) {

                if (state.atomLabels == null)
                    state.atomLabels = new HashMap<>();

                IPseudoAtom pseudo = (IPseudoAtom) atom;
                if (pseudo.getAttachPointNum() > 0) {
                    state.atomLabels.put(idx, "_AP" + pseudo.getAttachPointNum());
                } else {
                    if (!"*".equals(pseudo.getLabel()))
                        state.atomLabels.put(idx, pseudo.getLabel());
                }
            }
            Object comment = atom.getProperty(CDKConstants.COMMENT);
            if (comment != null) {
                if (state.atomValues == null)
                    state.atomValues = new HashMap<>();
                state.atomValues.put(idx, comment.toString());
            }
            atomidx.put(atom, idx);

            Point2d p2 = atom.getPoint2d();
            Point3d p3 = atom.getPoint3d();

            if (SmiFlavor.isSet(flavour, SmiFlavor.Cx2dCoordinates) && p2 != null) {
                state.atomCoords.add(new double[]{p2.x, p2.y, 0});
                state.coordFlag = true;
            } else if (SmiFlavor.isSet(flavour, SmiFlavor.Cx3dCoordinates) && p3 != null) {
                state.atomCoords.add(new double[]{p3.x, p3.y, p3.z});
                state.coordFlag = true;
            } else if (SmiFlavor.isSet(flavour, SmiFlavor.CxCoordinates)) {
                state.atomCoords.add(new double[3]);
            }
        }

        if (!state.coordFlag)
            state.atomCoords = null;

        // radicals
        if (mol.getSingleElectronCount() > 0) {
            state.atomRads = new HashMap<>();
            for (ISingleElectron radical : mol.singleElectrons()) {
                CxSmilesState.Radical val = state.atomRads.get(ensureNotNull(atomidx.get(radical.getAtom())));

                // 0->1, 1->2, 2->3
                if (val == null)
                    val = CxSmilesState.Radical.Monovalent;
                else if (val == CxSmilesState.Radical.Monovalent)
                    val = CxSmilesState.Radical.Divalent;
                else if (val == CxSmilesState.Radical.Divalent)
                    val = CxSmilesState.Radical.Trivalent;
                else if (val == CxSmilesState.Radical.Trivalent)
                    throw new IllegalArgumentException("Invalid radical state, can not be more than trivalent");

                state.atomRads.put(atomidx.get(radical.getAtom()),
                                   val);
            }
        }

        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        Map<Sgroup, CxSmilesState.CxSgroup> mapping = new HashMap<>();
        if (sgroups != null) {
            state.mysgroups = new ArrayList<>();
            state.positionVar = new HashMap<>();
            state.ligandOrdering = new HashMap<>();
            for (Sgroup sgroup : sgroups) {
                switch (sgroup.getType()) {
                    // polymer SRU
                    case CtabStructureRepeatUnit:
                    case CtabMonomer:
                    case CtabMer:
                    case CtabCopolymer:
                    case CtabCrossLink:
                    case CtabModified:
                    case CtabMixture:
                    case CtabFormulation:
                    case CtabAnyPolymer:
                    case CtabGeneric:
                    case CtabComponent:
                    case CtabGraft:
                        if ((flavour&SmiFlavor.CxPolymer) == 0)
                            break;
                        String supscript = sgroup.getValue(SgroupKey.CtabConnectivity);
                        CxPolymerSgroup cxSgrp;
                        cxSgrp= new CxPolymerSgroup(getSgroupPolymerKey(sgroup),
                                                                 toAtomIdxs(sgroup.getAtoms(), atomidx),
                                                                 sgroup.getSubscript(),
                                                                 supscript);
                        state.mysgroups.add(cxSgrp);
                        mapping.put(sgroup, cxSgrp);
                        break;

                    case ExtMulticenter: {
                        IAtom       beg   = null;
                        List<IAtom> ends  = new ArrayList<>();
                        Set<IBond>  bonds = sgroup.getBonds();
                        if (bonds.size() != 1)
                            throw new IllegalArgumentException("Multicenter Sgroup in inconsistent state!");
                        IBond bond = bonds.iterator().next();
                        for (IAtom atom : sgroup.getAtoms()) {
                            if (bond.contains(atom)) {
                                if (beg != null)
                                    throw new IllegalArgumentException("Multicenter Sgroup in inconsistent state!");
                                beg = atom;
                            } else {
                                ends.add(atom);
                            }
                        }
                        state.positionVar.put(ensureNotNull(atomidx.get(beg)),
                                              toAtomIdxs(ends, atomidx));
                        }
                        break;
                    case ExtAttachOrdering: {
                        IAtom       beg   = null;
                        List<IAtom> ends  = new ArrayList<>();
                        if (sgroup.getAtoms().size() != 1)
                            throw new IllegalArgumentException("Attach ordering in inconsistent state!");
                        beg = sgroup.getAtoms().iterator().next();
                        for (IBond bond : sgroup.getBonds()) {
                            IAtom nbr = bond.getOther(beg);
                            if (nbr == null)
                                throw new IllegalArgumentException("Attach ordering in inconsistent state!");
                            ends.add(nbr);
                        }
                        state.ligandOrdering.put(ensureNotNull(atomidx.get(beg)),
                                                 toAtomIdxs(ends, atomidx));
                        }
                        break;
                    case CtabAbbreviation:
                    case CtabMultipleGroup:
                        // display shortcuts are not output
                        break;
                    case CtabData:
                        if ((flavour&SmiFlavor.CxDataSgroups) == 0)
                            break;
                        // can be generated but currently ignored
                        CxSmilesState.CxDataSgroup cxDataSgrp;
                        cxDataSgrp= new CxSmilesState.CxDataSgroup(toAtomIdxs(sgroup.getAtoms(), atomidx),
                                                                  (String)sgroup.getValue(SgroupKey.DataFieldName),
                                                                  (String)sgroup.getValue(SgroupKey.Data),
                                                                   null,
                                                                  (String)sgroup.getValue(SgroupKey.DataFieldUnits),
                                                                  null);
                        state.mysgroups.add(cxDataSgrp);
                        mapping.put(sgroup, cxDataSgrp);
                        break;
                    default:
                        throw new UnsupportedOperationException("Unsupported Sgroup Polymer");
                }
            }

            for (Sgroup sgroup : sgroups) {
                CxSmilesState.CxSgroup cxChild = mapping.get(sgroup);
                if (cxChild == null)
                    continue;
                for (Sgroup parent : sgroup.getParents()) {
                    CxSmilesState.CxSgroup cxParent = mapping.get(parent);
                    if (cxParent == null)
                        continue;
                    cxParent.children.add(cxChild);
                }
            }
        }

        return state;
    }

    private static String getSgroupPolymerKey(Sgroup sgroup) {
        switch (sgroup.getType()) {
            case CtabStructureRepeatUnit:
                return "n";
            case CtabMonomer:
                return "mon";
            case CtabMer:
                return "mer";
            case CtabCopolymer:
                String subtype = sgroup.getValue(SgroupKey.CtabSubType);
                if (subtype == null)
                    return "co";
                switch (subtype) {
                    case "RAN":
                        return "ran";
                    case "ALT":
                        return "alt";
                    case "BLO":
                        return "blk";
                }
            case CtabCrossLink:
                return "xl";
            case CtabModified:
                return "mod";
            case CtabMixture:
                return "mix";
            case CtabFormulation:
                return "f";
            case CtabAnyPolymer:
                return "any";
            case CtabGeneric:
                return "gen";
            case CtabComponent:
                return "c";
            case CtabGraft:
                return "grf";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Comparator<IAtom> createComparator(final IAtomContainer mol,
                                                     final int flavor) {
        return new Comparator<IAtom>() {

            final int unbox(Integer x) {
                return x != null ? x : 0;
            }

            @Override
            public int compare(IAtom a, IAtom b) {
                final List<IBond> aBonds = mol.getConnectedBondsList(a);
                final List<IBond> bBonds = mol.getConnectedBondsList(b);
                final int aH = unbox(a.getImplicitHydrogenCount());
                final int bH = unbox(b.getImplicitHydrogenCount());
                int cmp;

                // 1) Connectivity, X=D+h
                if ((cmp = Integer.compare(aBonds.size() + aH,
                                           bBonds.size() + bH)) != 0)
                    return cmp;
                // 2) Degree, D
                if ((cmp = Integer.compare(aBonds.size(),
                                           bBonds.size())) != 0)
                    return cmp;
                // 3) Element, #<N>
                if ((cmp = Integer.compare(unbox(a.getAtomicNumber()),
                                           unbox(b.getAtomicNumber()))) != 0)
                    return cmp;
                // 4a) charge sign
                int aQ = unbox(a.getFormalCharge());
                int bQ = unbox(b.getFormalCharge());
                if ((cmp = Integer.compare((aQ >> 31) & 0x1,
                                           (bQ >> 31) & 0x1)) != 0)
                    return cmp;
                // 4b) charge magnitude
                if ((cmp = Integer.compare(Math.abs(aQ),
                                           Math.abs(bQ))) != 0)
                    return cmp;

                int aTotalH = aH;
                int bTotalH = bH;
                for (IBond bond : aBonds)
                    aTotalH += bond.getOther(a).getAtomicNumber() == 1 ? 1 : 0;
                for (IBond bond : bBonds)
                    bTotalH += bond.getOther(b).getAtomicNumber() == 1 ? 1 : 0;
                // 5) total H count
                if ((cmp = Integer.compare(aTotalH, bTotalH)) != 0)
                    return cmp;

                // XXX: valence and ring membership should also be used to split
                //      ties, but will change the current canonical labelling!

                // extra 1) atomic mass
                if (SmiFlavor.isSet(flavor, SmiFlavor.Isomeric)
                    && (cmp = Integer.compare(a.getMassNumber(), b.getMassNumber())) != 0)
                    return cmp;
                // extra 2) atom map
                if (SmiFlavor.isSet(flavor, SmiFlavor.AtomAtomMap) &&
                    (flavor & SmiFlavor.AtomAtomMapRenumber) != SmiFlavor.AtomAtomMapRenumber) {
                    Integer aMapIdx = a.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                    Integer bMapIdx = b.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                    if ((cmp = Integer.compare(unbox(aMapIdx), unbox(bMapIdx))) != 0)
                        return cmp;
                }
                return 0;
            }
        };
    }

}
