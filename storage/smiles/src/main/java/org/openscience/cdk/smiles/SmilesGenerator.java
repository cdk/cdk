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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.invariant.Canon;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IReaction;
import uk.ac.ebi.beam.Functions;
import uk.ac.ebi.beam.Graph;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Generate a SMILES {@cdk.cite WEI88, WEI89} string for a provided structure.
 * The generator can produce several <i>flavour</i> of SMILES.
 * <p/>
 * <ul>
 *     <li>generic - non-canonical SMILES string, different atom ordering
 *         produces different SMILES. No isotope or stereochemistry encoded.
 *         </li>
 *     <li>unique - canonical SMILES string, different atom ordering
 *         produces the same* SMILES. No isotope or stereochemistry encoded.
 *         </li>
 *     <li>isomeric - non-canonical SMILES string, different atom ordering
 *         produces different SMILES. Isotope and stereochemistry is encoded.
 *         </li>
 *     <li>absolute - canonical SMILES string, different atom ordering
 *         produces the same SMILES. Isotope and stereochemistry is encoded.</li>
 * </ul>
 *
 * <p/>
 * A generator instance is created using one of the static methods, the SMILES
 * are then created by invoking {@link #create(IAtomContainer)}.
 * <blockquote><pre>
 * IAtomContainer  ethanol = ...;
 * SmilesGenerator sg      = SmilesGenerator.generic();
 * String          smi     = sg.create(ethanol); // CCO or OCC
 *
 * SmilesGenerator sg      = SmilesGenerator.unique();
 * String          smi     = sg.create(ethanol); // only CCO
 * </pre></blockquote>
 *
 * <p/>
 *
 * The isomeric and absolute generator encode tetrahedral and double bond
 * stereochemistry using {@link org.openscience.cdk.interfaces.IStereoElement}s
 * provided on the {@link IAtomContainer}. If stereochemistry is not being
 * written it may need to be determined from 2D/3D coordinates using
 * {@link org.openscience.cdk.stereo.StereoElementFactory}.
 *
 * <p/>
 *
 * By default the generator will not write aromatic SMILES. Kekulé SMILES are
 * generally preferred for compatibility and aromaticity can easily be
 * reperceived. Modifying a generator to produce {@link #aromatic()} SMILES
 * will use the {@link org.openscience.cdk.CDKConstants#ISAROMATIC} flags.
 * These flags can be set manually or with the
 * {@link org.openscience.cdk.aromaticity.Aromaticity} utility.
 * <blockquote><pre>
 * IAtomContainer  benzene = ...;
 *
 * // with no flags set the output is always kekule
 * SmilesGenerator sg      = SmilesGenerator.generic();
 * String          smi     = sg.create(benzene); // C1=CC=CC=C1
 *
 * SmilesGenerator sg      = SmilesGenerator.generic()
 *                                          .aromatic();
 * String          smi     = sg.create(ethanol); // C1=CC=CC=C1
 *
 * for (IAtom a : benzene.atoms())
 *     a.setFlag(CDKConstants.ISAROMATIC, true);
 * for (IBond b : benzene.bond())
 *     b.setFlag(CDKConstants.ISAROMATIC, true);
 *
 * // with flags set, the aromatic generator encodes this information
 * SmilesGenerator sg      = SmilesGenerator.generic();
 * String          smi     = sg.create(benzene); // C1=CC=CC=C1
 *
 * SmilesGenerator sg      = SmilesGenerator.generic()
 *                                          .aromatic();
 * String          smi     = sg.create(ethanol); // c1ccccc1
 * </pre></blockquote>
 * <p/>
 * By default atom classes are not written. Atom classes can be written but
 * creating a generator {@link #withAtomClasses()}.
 *
 * <blockquote><pre>
 * IAtomContainer  benzene = ...;
 *
 * // see CDKConstants for property key
 * benzene.getAtom(3)
 *        .setProperty(ATOM_ATOM_MAPPING, 42);
 *
 * SmilesGenerator sg      = SmilesGenerator.generic();
 * String          smi     = sg.create(benzene); // C1=CC=CC=C1
 *
 * SmilesGenerator sg      = SmilesGenerator.generic()
 *                                          .withAtomClasses();
 * String          smi     = sg.create(ethanol); // C1=CC=[CH:42]C=C1
 * </pre></blockquote>
 * <p/>
 *
 * Auxiliary data can be stored with SMILES by knowing the output order of
 * atoms. The following example demonstrates the storage of 2D coordinates.
 *
 * <blockquote><pre>
 * IAtomContainer  mol = ...;
 * SmilesGenerator sg  = SmilesGenerator.generic();
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
 * </pre></blockquote>
 *
 * * the unique SMILES generation uses a fast equitable labelling procedure
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

    private final int options;
    private final CDKToBeam converter;

    /**
     * Create the generic SMILES generator.
     * @see #generic()
     * @deprecated use {@link #SmilesGenerator(int)} configuring with {@link SmiOpt}.
     */
    @Deprecated
    public SmilesGenerator() {
        this(0);
    }

    public SmilesGenerator(int options) {
        this.options = options;
        this.converter = new CDKToBeam(options);
    }

    /**
     * The generator should write aromatic (lower-case) SMILES. This option is
     * not recommended as different parsers can interpret where bonds should be
     * placed.
     *
     * <blockquote><pre>
     * IAtomContainer  container = ...;
     * SmilesGenerator smilesGen = SmilesGenerator.unique()
     *                                            .aromatic();
     * smilesGen.createSMILES(container);
     * </pre></blockquote>
     *
     * @return a generator for aromatic SMILES
     */
    public SmilesGenerator aromatic() {
        return new SmilesGenerator(this.options | SmiOpt.UseAromaticSymbols);
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
     */
    public SmilesGenerator withAtomClasses() {
        return new SmilesGenerator(this.options | SmiOpt.AtomAtomMap);
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
        return new SmilesGenerator(0);
    }

    /**
     * Convenience method for creating an isomeric generator. Isomeric SMILES
     * are non-unique but contain isotope numbers (e.g. {@code [13C]}) and
     * stereo-chemistry.
     *
     * @return a new isomeric SMILES generator
     */
    public static SmilesGenerator isomeric() {
        return new SmilesGenerator(SmiOpt.Isomeric);
    }

    /**
     * Create a unique SMILES generator. Unique SMILES use a fast canonisation
     * algorithm but does not encode isotope or stereo-chemistry.
     *
     * @return a new unique SMILES generator
     */
    public static SmilesGenerator unique() {
        return new SmilesGenerator(SmiOpt.Canonical);
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
        return new SmilesGenerator(SmiOpt.Absolute);
    }

    /**
     * Create a SMILES string for the provided molecule.
     *
     * @param molecule the molecule to create the SMILES of
     * @return a SMILES string
     * @throws CDKException SMILES could not be generated
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
     * @throws CDKException SMILES could not be generated
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
     * Create a SMILES string and obtain the order which the atoms were
     * written. The output order allows one to arrange auxiliary atom data in the
     * order that a SMILES string will be read. A simple example is seen below
     * where 2D coordinates are stored with a SMILES string. In reality a more
     * compact binary encoding would be used instead of printing the coordinates
     * as a string.
     *
     * <blockquote><pre>
     * IAtomContainer  mol = ...;
     * SmilesGenerator sg  = SmilesGenerator.generic();
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
     * </pre></blockquote>
     *
     * @param molecule the molecule to write
     * @param order    array to store the output order of atoms
     * @return the SMILES string
     * @throws CDKException SMILES could not be created
     */
    public String create(IAtomContainer molecule, int[] order) throws CDKException {

        try {
            if (order.length != molecule.getAtomCount())
                throw new IllegalArgumentException("the array for storing output order should be"
                        + "the same length as the number of atoms");

            Graph g = converter.toBeamGraph(molecule);

            // apply the canonical labelling
            if (SmiOpt.isSet(options, SmiOpt.Canonical)) {

                // determine the output order
                int[] labels = labels(molecule);

                g = g.permute(labels).resonate();

                if (SmiOpt.isSet(options, SmiOpt.StereoCisTrans)) {

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

                return smiles;
            } else {
                return g.toSmiles(order);
            }
        } catch (IOException e) {
            throw new CDKException(e.getMessage());
        }
    }

    /**
     * Generate a SMILES for the given <code>Reaction</code>.
     *
     * @param reaction the reaction in question
     * @return the SMILES representation of the reaction
     * @throws org.openscience.cdk.exception.CDKException if there is an error during SMILES generation
     */
    public String createReactionSMILES(IReaction reaction) throws CDKException {
        StringBuffer reactionSMILES = new StringBuffer();
        IAtomContainerSet reactants = reaction.getReactants();
        for (int i = 0; i < reactants.getAtomContainerCount(); i++) {
            reactionSMILES.append(create(reactants.getAtomContainer(i)));
            if (i + 1 < reactants.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        reactionSMILES.append('>');
        IAtomContainerSet agents = reaction.getAgents();
        for (int i = 0; i < agents.getAtomContainerCount(); i++) {
            reactionSMILES.append(create(agents.getAtomContainer(i)));
            if (i + 1 < agents.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        reactionSMILES.append('>');
        IAtomContainerSet products = reaction.getProducts();
        for (int i = 0; i < products.getAtomContainerCount(); i++) {
            reactionSMILES.append(create(products.getAtomContainer(i)));
            if (i + 1 < products.getAtomContainerCount()) {
                reactionSMILES.append('.');
            }
        }
        return reactionSMILES.toString();
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
    private int[] labels(final IAtomContainer molecule) throws CDKException {
        // FIXME: use SmiOpt.InChiLabelling
        long[] labels = SmiOpt.isSet(options, SmiOpt.Isomeric) ? inchiNumbers(molecule)
                                                               : Canon.label(molecule, GraphUtil.toAdjList(molecule));
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
    private long[] inchiNumbers(IAtomContainer container) throws CDKException {
        // TODO: create an interface so we don't have to dynamically load the
        // class each time
        String cname = "org.openscience.cdk.graph.invariant.InChINumbersTools";
        String mname = "getUSmilesNumbers";
        try {
            Class<?> c = Class.forName(cname);
            Method method = c.getDeclaredMethod("getUSmilesNumbers", IAtomContainer.class);
            return (long[]) method.invoke(c, container);
        } catch (ClassNotFoundException e) {
            throw new CDKException("The cdk-inchi module is not loaded,"
                    + " this module is need when gernating absolute SMILES.");
        } catch (NoSuchMethodException e) {
            throw new CDKException("The method " + mname + " was not found", e);
        } catch (InvocationTargetException e) {
            throw new CDKException("An InChI could not be generated and used to canonise SMILES: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new CDKException("Could not access method to obtain InChI numbers.");
        }
    }

}
