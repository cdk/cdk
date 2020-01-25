/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
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
 *
 */
package org.openscience.cdk.layout;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.ConnectedComponents;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.interfaces.IStereoElement;
import org.openscience.cdk.isomorphism.AtomMatcher;
import org.openscience.cdk.isomorphism.BondMatcher;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.VentoFoggia;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupBracket;
import org.openscience.cdk.sgroup.SgroupKey;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.stereo.DoubleBondStereochemistry;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ReactionManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates 2D coordinates for a molecule for which only connectivity is known
 * or the coordinates have been discarded for some reason. Usage: Create an
 * instance of this class, thereby assigning a molecule, call
 * generateCoordinates() and get your molecule back:
 * <pre>
 * StructureDiagramGenerator sdg = new StructureDiagramGenerator();
 * sdg.setMolecule(someMolecule);
 * sdg.generateCoordinates();
 * IAtomContainer layedOutMol = sdg.getMolecule();
 * </pre>
 * 
 * <p>The method will fail if the molecule is disconnected. The
 * partitionIntoMolecules(AtomContainer) can help here.
 *
 * @author steinbeck
 * @cdk.created 2004-02-02
 * @cdk.keyword Layout
 * @cdk.keyword Structure Diagram Generation (SDG)
 * @cdk.keyword 2D-coordinates
 * @cdk.keyword Coordinate generation, 2D
 * @cdk.dictref blue-obelisk:layoutMolecule
 * @cdk.module sdg
 * @cdk.githash
 * @cdk.bug 1536561
 * @cdk.bug 1788686
 * @see org.openscience.cdk.graph.ConnectivityChecker#partitionIntoMolecules(IAtomContainer)
 */
public class StructureDiagramGenerator {

    static final double DEFAULT_BOND_LENGTH           = 1.5;
    static final double SGROUP_BRACKET_PADDING_FACTOR = 0.5;
    private static final Vector2d                   DEFAULT_BOND_VECTOR      = new Vector2d(0, 1);
    private static final IdentityTemplateLibrary    DEFAULT_TEMPLATE_LIBRARY = IdentityTemplateLibrary.loadFromResource("custom-templates.smi")
                                                                                                   .add(IdentityTemplateLibrary.loadFromResource("chebi-ring-templates.smi"));
    private static final double                     RAD_30                   = Math.toRadians(-30);
    private static final ILoggingTool               logger                   = LoggingToolFactory.createLoggingTool(StructureDiagramGenerator.class);

    public static final Comparator<IAtomContainer> LARGEST_FIRST_COMPARATOR = new Comparator<IAtomContainer>() {
        @Override
        public int compare(IAtomContainer o1, IAtomContainer o2) {
            return Integer.compare(o2.getBondCount(), o1.getBondCount());
        }
    };

    private IAtomContainer molecule;
    private IRingSet       sssr;
    private final double bondLength = DEFAULT_BOND_LENGTH;
    private Vector2d firstBondVector;
    private RingPlacer       ringPlacer          = new RingPlacer();
    private AtomPlacer       atomPlacer          = new AtomPlacer();
    private MacroCycleLayout macroPlacer         = null;
    private List<IRingSet>   ringSystems         = null;
    private Set<IAtom>       afix                = null;
    private Set<IBond>       bfix                = null;
    private boolean          useIdentTemplates   = true;
    private boolean          alignMappedReaction = true;

    // show we orient the structure (false: keep de facto ring systems drawn
    // the right way up)
    private boolean selectOrientation = true;

    /**
     * Identity templates - for laying out primary ring system.
     */
    private IdentityTemplateLibrary identityLibrary;




    /**
     * The empty constructor.
     */
    public StructureDiagramGenerator() {
        this(DEFAULT_TEMPLATE_LIBRARY);
    }

    private StructureDiagramGenerator(IdentityTemplateLibrary identityLibrary) {
        this.identityLibrary = identityLibrary;
    }

    /**
     * Creates an instance of this class while assigning a molecule to be layed
     * out.
     *
     * @param molecule The molecule to be layed out.
     */
    public StructureDiagramGenerator(IAtomContainer molecule) {
        this();
        setMolecule(molecule, false);
    }

    /**
     * <p>Convenience method for generating 2D coordinates.</p>
     *
     * <p>The method is short-hand for calling:</p>
     * <pre>
     * sdg.setMolecule(mol, false);
     * sdg.generateCoordinates();
     * </pre>
     *
     * @param mol molecule to layout
     * @throws CDKException problem with layout
     */
    public final void generateCoordinates(IAtomContainer mol) throws CDKException {
        setMolecule(mol, false);
        generateCoordinates();
    }

    /**
     * <p>Convenience method to generate 2D coordinates for a reaction. If atom-atom
     * maps are present on a reaction, the substructures are automatically aligned.</p>
     * <p>This feature can be disabled by changing the {@link #setAlignMappedReaction(boolean)}</p>
     *
     * @param reaction reaction to layout
     * @throws CDKException problem with layout
     */
    public final void generateCoordinates(final IReaction reaction) throws CDKException {

        // layout products and agents
        for (IAtomContainer mol : reaction.getProducts().atomContainers())
            generateCoordinates(mol);
        for (IAtomContainer mol : reaction.getAgents().atomContainers())
            generateCoordinates(mol);

        // do not align = simple layout of reactants
        if (alignMappedReaction) {
            final Set<IBond> mapped = ReactionManipulator.findMappedBonds(reaction);

            Multimap<Integer, Map<Integer, IAtom>> refmap = HashMultimap.create();

            for (IAtomContainer mol : reaction.getProducts().atomContainers()) {
                Cycles.markRingAtomsAndBonds(mol);
                final ConnectedComponents cc = new ConnectedComponents(GraphUtil.toAdjListSubgraph(mol, mapped));
                final IAtomContainerSet parts = ConnectivityChecker.partitionIntoMolecules(mol, cc.components());
                for (IAtomContainer part : parts.atomContainers()) {
                    // skip single atoms (unmapped)
                    if (part.getAtomCount() == 1)
                        continue;
                    final Map<Integer, IAtom> map = new HashMap<>();
                    for (IAtom atom : part.atoms()) {
                        // safe as substructure should only be mapped bonds and therefore atoms!
                        int idx = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                        if (map.put(idx, atom) == null)
                            refmap.put(idx, map);
                    }
                }
            }

            Map<IAtom,IAtom> afix = new HashMap<>();
            Set<IBond>       bfix = new HashSet<>();

            for (IAtomContainer mol : reaction.getReactants().atomContainers()) {
                Cycles.markRingAtomsAndBonds(mol);
                final ConnectedComponents cc = new ConnectedComponents(GraphUtil.toAdjListSubgraph(mol, mapped));
                final IAtomContainerSet parts = ConnectivityChecker.partitionIntoMolecules(mol, cc.components());

                // we only aligned the largest part
                IAtomContainer largest = null;
                for (IAtomContainer part : parts.atomContainers()) {
                    if (largest == null || part.getBondCount() > largest.getBondCount())
                        largest = part;
                }

                afix.clear();
                bfix.clear();

                boolean aggresive = false;

                if (largest != null && largest.getAtomCount() > 1) {

                    int idx = largest.getAtom(0).getProperty(CDKConstants.ATOM_ATOM_MAPPING);

                    // select the largest and use those coordinates
                    Map<Integer, IAtom> reference = select(refmap.get(idx));
                    for (IAtom atom : largest.atoms()) {
                        idx = atom.getProperty(CDKConstants.ATOM_ATOM_MAPPING);
                        final IAtom src = reference.get(idx);
                        if (src == null) continue;
                        if (!aggresive) {
                            // no way to get the container of 'src' without
                            // lots of refactoring, instead we just use the
                            // new API points - first checking these will not
                            // fail
                            if (src.getContainer() != null
                                && atom.getContainer() != null
                                && AtomPlacer.isColinear(src, src.bonds())
                                   != AtomPlacer.isColinear(atom, atom.bonds()))
                                continue;
                        }
                        atom.setPoint2d(new Point2d(src.getPoint2d()));
                        afix.put(atom, src);
                    }
                }

                if (!afix.isEmpty()) {
                    if (aggresive) {
                        for (IBond bond : mol.bonds()) {
                            if (afix.containsKey(bond.getBegin()) && afix.containsKey(bond.getEnd())) {
                                // only fix acyclic bonds if the source atoms were also acyclic
                                if (!bond.isInRing()) {
                                    IAtom srcBeg = afix.get(bond.getBegin());
                                    IAtom srcEnd = afix.get(bond.getEnd());
                                    for (IAtomContainer product : reaction.getProducts().atomContainers()) {
                                        IBond srcBond = product.getBond(srcBeg, srcEnd);
                                        if (srcBond != null) {
                                            if (!srcBond.isInRing())
                                                bfix.add(bond); // safe to add
                                            break;
                                        }
                                    }
                                } else {
                                    bfix.add(bond);
                                }
                            }
                        }
                    } else {
                        for (IBond bond : mol.bonds()) {
                            if (afix.containsKey(bond.getBegin()) && afix.containsKey(bond.getEnd())) {
                                // only fix bonds that match their ring membership status
                                IAtom srcBeg = afix.get(bond.getBegin());
                                IAtom srcEnd = afix.get(bond.getEnd());
                                for (IAtomContainer product : reaction.getProducts().atomContainers()) {
                                    IBond srcBond = product.getBond(srcBeg, srcEnd);
                                    if (srcBond != null) {
                                        if (srcBond.isInRing() == bond.isInRing())
                                            bfix.add(bond);
                                        break;
                                    }
                                }
                            }
                        }

                        afix.clear();
                        for (IBond bond : bfix) {
                            afix.put(bond.getBegin(), null);
                            afix.put(bond.getEnd(), null);
                        }

                        int[] parts2 = new int[mol.getAtomCount()];
                        int numParts = 0;
                        Deque<IAtom> queue = new ArrayDeque<>();
                        for (IAtom atom : afix.keySet()) {
                            if (parts2[mol.indexOf(atom)] != 0)
                                continue;
                            parts2[mol.indexOf(atom)] = ++numParts;
                            for (IBond bond : mol.getConnectedBondsList(atom)) {
                                if (bfix.contains(bond))
                                    queue.add(bond.getOther(atom));
                            }
                            while (!queue.isEmpty()) {
                                atom = queue.poll();
                                if (parts2[mol.indexOf(atom)] != 0)
                                    continue;
                                parts2[mol.indexOf(atom)] = numParts;
                                for (IBond bond : mol.getConnectedBondsList(atom)) {
                                    if (bfix.contains(bond))
                                        queue.add(bond.getOther(atom));
                                }
                            }
                        }

                        if (numParts > 1) {
                            int best     = 0;
                            int bestSize = 0;
                            for (int part = 1; part <= numParts; part++) {
                                int size = 0;
                                for (int i = 0; i < parts2.length; i++) {
                                    if (parts2[i] == part)
                                        ++size;
                                }
                                if (size > bestSize) {
                                    bestSize = size;
                                    best = part;
                                }
                            }

                            for (IAtom atom : new ArrayList<>(afix.keySet())) {
                                if (parts2[mol.indexOf(atom)] != best) {
                                    afix.remove(atom);
                                    bfix.removeAll(mol.getConnectedBondsList(atom));
                                }
                            }
                        }
                    }
                }

                setMolecule(mol, false, afix.keySet(), bfix);
                generateCoordinates();
            }

            // reorder reactants such that they are in the same order they appear on the right
            reaction.getReactants().sortAtomContainers(new Comparator<IAtomContainer>() {
                @Override
                public int compare(IAtomContainer a, IAtomContainer b) {
                    Point2d aCenter = GeometryUtil.get2DCenter(a);
                    Point2d bCenter = GeometryUtil.get2DCenter(b);
                    if (aCenter == null || bCenter == null)
                        return 0;
                    else
                        return Double.compare(aCenter.x, bCenter.x);
                }
            });

        } else {
            for (IAtomContainer mol : reaction.getReactants().atomContainers())
                generateCoordinates(mol);
        }
    }

    private Map<Integer, IAtom> select(Collection<Map<Integer, IAtom>> refs) {
        Map<Integer, IAtom> largest = Collections.emptyMap();
        for (Map<Integer, IAtom> ref : refs) {
            if (ref.size() > largest.size())
                largest = ref;
        }
        return largest;
    }

    public void setMolecule(IAtomContainer mol, boolean clone) {
        setMolecule(mol, clone, Collections.<IAtom>emptySet(), Collections.<IBond>emptySet());
    }

    /**
     * Assigns a molecule to be laid out. After, setting the molecule call generateCoordinates() to assign
     * 2D coordinates. An optional set of atoms/bonds can be parsed in to allow partial layout, these will
     * be 'fixed' in place. This only applies to non-cloned molecules, and only atoms with coordinates can
     * be fixed.
     *
     * @param mol   the molecule for which coordinates are to be generated.
     * @param clone Should the whole process be performed with a cloned copy?
     * @param afix  Atoms that should be fixed in place, coordinates are not changed.
     * @param bfix  Bonds that should be fixed in place, they will not be flipped, bent, or streched.
     */
    public void setMolecule(IAtomContainer mol, boolean clone, Set<IAtom> afix, Set<IBond> bfix) {
        if (clone) {
            if (!afix.isEmpty() || !bfix.isEmpty())
                throw new IllegalArgumentException("Laying out a cloned molecule, can't fix atom or bonds.");
            try {
                this.molecule = (IAtomContainer) mol.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("Should clone, but exception occurred: ", e.getMessage());
                logger.debug(e);
            }
        } else {
            this.molecule = mol;
        }
        this.afix = afix;
        this.bfix = bfix;
        for (IAtom atom : molecule.atoms()) {

            boolean afixed = afix.contains(atom);

            if (afixed && atom.getPoint2d() == null) {
                afixed = false;
                afix.remove(atom);
            }

            if (afixed) {
                atom.setFlag(CDKConstants.ISPLACED, true);
                atom.setFlag(CDKConstants.VISITED, true);
            } else {
                atom.setPoint2d(null);
                atom.setFlag(CDKConstants.ISPLACED, false);
                atom.setFlag(CDKConstants.VISITED, false);
                atom.setFlag(CDKConstants.ISINRING, false);
                atom.setFlag(CDKConstants.ISALIPHATIC, false);
            }
        }
        atomPlacer.setMolecule(this.molecule);
        ringPlacer.setMolecule(this.molecule);
        ringPlacer.setAtomPlacer(this.atomPlacer);
        macroPlacer = new MacroCycleLayout(mol);
        selectOrientation = afix.isEmpty();
    }

    /**
     * Sets whether to use templates or not. Some complicated ring systems
     * like adamantane are only nicely layouted when using templates. This
     * option is by default set true.
     *
     * @param useTemplates set true to use templates, false otherwise
     * @deprecated always false, substructure templates are not used anymore
     */
    @Deprecated
    public void setUseTemplates(boolean useTemplates) {

    }

    /**
     * Set whether identity templates are used. Identity templates use an exact match
     * are are very fast. They are used for layout of the 'primary' ring system
     * in de facto orientation.
     *
     * @param use whether to use identity templates
     */
    public void setUseIdentityTemplates(boolean use) {
        this.useIdentTemplates = use;
    }

    /**
     * Returns whether the use of templates is enabled or disabled.
     *
     * @return true, when the use of templates is enables, false otherwise
     * @deprecated always false, substructure templates are not used anymore
     */
    @Deprecated
    public boolean getUseTemplates() {
        return false;
    }

    /**
     * Sets the templateHandler attribute of the StructureDiagramGenerator object
     *
     * @param templateHandler The new templateHandler value
     * @deprecated substructure templates are no longer used for layout but those provided here
     * will be converted to identity templates
     */
    @Deprecated
    public void setTemplateHandler(TemplateHandler templateHandler) {
        IdentityTemplateLibrary lib = templateHandler.toIdentityTemplateLibrary();
        lib.add(identityLibrary);
        identityLibrary = lib; // new ones take priority
    }

    /**
     * Gets the templateHandler attribute of the StructureDiagramGenerator object
     *
     * @return The templateHandler value
     * @deprecated always null, substructure templates are not used anymore
     */
    @Deprecated
    public TemplateHandler getTemplateHandler() {
        return null;
    }

    /**
     * Assings a molecule to be layed out. Call generateCoordinates() to do the
     * actual layout.
     *
     * @param molecule the molecule for which coordinates are to be generated.
     */
    public void setMolecule(IAtomContainer molecule) {
        setMolecule(molecule, true);
    }

    /**
     * Set whether reaction reactants should be allignned to their product.
     *
     * @param align align setting
     */
    public void setAlignMappedReaction(boolean align) {
        this.alignMappedReaction = align;
    }

    /**
     * Returns the molecule, usually used after a call of generateCoordinates()
     *
     * @return The molecule with new coordinates (if generateCoordinates() had
     * been called)
     */
    public IAtomContainer getMolecule() {
        return molecule;
    }

    /**
     * This method uses generateCoordinates, but it removes the hydrogens first,
     * lays out the structure and then adds them again.
     *
     * @throws CDKException if an error occurs
     * @see #generateCoordinates
     * @deprecated use {@link #generateCoordinates()}
     */
    @Deprecated
    public void generateExperimentalCoordinates() throws CDKException {
        generateExperimentalCoordinates(DEFAULT_BOND_VECTOR);
    }

    /**
     * Generates 2D coordinates on the non-hydrogen skeleton, after which
     * coordinates for the hydrogens are calculated.
     *
     * @param firstBondVector the vector of the first bond to lay out
     * @throws CDKException if an error occurs
     * @deprecated use {@link #generateCoordinates()}
     */
    @Deprecated
    public void generateExperimentalCoordinates(Vector2d firstBondVector) throws CDKException {
        // first make a shallow copy: Atom/Bond references are kept
        IAtomContainer original = molecule;
        IAtomContainer shallowCopy = molecule.getBuilder().newInstance(IAtomContainer.class, molecule);
        // delete single-bonded H's from
        //IAtom[] atoms = shallowCopy.getAtoms();
        for (IAtom curAtom : shallowCopy.atoms()) {
            if (curAtom.getSymbol().equals("H")) {
                if (shallowCopy.getConnectedBondsCount(curAtom) < 2) {
                    shallowCopy.removeAtom(curAtom);
                    curAtom.setPoint2d(null);
                }
            }
        }
        // do layout on the shallow copy
        molecule = shallowCopy;
        generateCoordinates(firstBondVector);
        double bondLength = GeometryUtil.getBondLengthAverage(molecule);
        // ok, now create the coordinates for the hydrogens
        HydrogenPlacer hPlacer = new HydrogenPlacer();
        molecule = original;
        hPlacer.placeHydrogens2D(molecule, bondLength);
    }

    /**
     * The main method of this StructurDiagramGenerator. Assign a molecule to the
     * StructurDiagramGenerator, call the generateCoordinates() method and get
     * your molecule back.
     *
     * @param firstBondVector The vector of the first bond to lay out
     * @throws CDKException if an error occurs
     */
    public void generateCoordinates(Vector2d firstBondVector) throws CDKException {
        generateCoordinates(firstBondVector, false, false);
    }

    /**
     * The main method of this StructureDiagramGenerator. Assign a molecule to the
     * StructureDiagramGenerator, call the generateCoordinates() method and get
     * your molecule back.
     *
     * @param firstBondVector the vector of the first bond to lay out
     * @param isConnected     the 'molecule' attribute is guaranteed to be connected (we have checked)
     * @param isSubLayout     the 'molecule' is being laid out as part of a large collection of fragments
     * @throws CDKException problem occurred during layout
     */
    private void generateCoordinates(Vector2d firstBondVector, boolean isConnected, boolean isSubLayout) throws CDKException {

        // defensive copy, vectors are mutable!
        if (firstBondVector == DEFAULT_BOND_VECTOR)
            firstBondVector = new Vector2d(firstBondVector);

        final int numAtoms = molecule.getAtomCount();
        final int numBonds = molecule.getBondCount();
        this.firstBondVector = firstBondVector;

        // if molecule contains only one Atom, don't fail, simply set
        // coordinates to simplest: 0,0. See bug #780545
        logger.debug("Entry point of generateCoordinates()");
        logger.debug("We have a molecules with " + numAtoms + " atoms.");
        if (numAtoms == 0) {
            return;
        } 
        if (numAtoms == 1) {
            molecule.getAtom(0).setPoint2d(new Point2d(0, 0));
            return;
        } else if (molecule.getBondCount() == 1 && molecule.getAtomCount() == 2) {
            double xOffset = 0;
            for (IAtom atom : molecule.atoms()) {
                atom.setPoint2d(new Point2d(xOffset, 0));
                xOffset += bondLength;
            }
            return;
        }

        // intercept fragment molecules and lay them out in a grid
        if (!isConnected) {
            final IAtomContainerSet frags = ConnectivityChecker.partitionIntoMolecules(molecule);
            if (frags.getAtomContainerCount() > 1) {
                IAtomContainer rollback = molecule;

                // large => small (e.g. salt will appear on the right)
                List<IAtomContainer> fragList = toList(frags);
                Collections.sort(fragList, LARGEST_FIRST_COMPARATOR);
                generateFragmentCoordinates(molecule, fragList);

                // don't call set molecule as it wipes x,y coordinates!
                // this looks like a self assignment but actually the fragment
                // method changes this.molecule
                this.molecule = rollback;
                atomPlacer.setMolecule(this.molecule);
                ringPlacer.setMolecule(this.molecule);
                macroPlacer = new MacroCycleLayout(this.molecule);
                return;
            }
        }

        // initial layout seeding either from a ring system of longest chain
        seedLayout();

        // Now, do the layout of the rest of the molecule
        int iter = 0;
        for (; !AtomPlacer.allPlaced(molecule) && iter < numAtoms; iter++) {
            logger.debug("*** Start of handling the rest of the molecule. ***");
            // layout for all acyclic parts of the molecule which are
            // connected to the parts which have already been laid out.
            layoutAcyclicParts();
            // layout cyclic parts of the molecule which
            // are connected to the parts which have already been laid out.
            layoutCyclicParts();
        }

        // display reasonable error on failed layout, otherwise we'll have a NPE somewhere
        if (iter == numAtoms && !AtomPlacer.allPlaced(molecule))
            throw new CDKException("Could not generate layout? If a set of 'fixed' atoms were provided"
                                       + " try removing these and regenerating the layout.");

        if (!isSubLayout) {
            // correct double-bond stereo, this changes the layout and in reality
            // should be done during the initial placement
            if (molecule.stereoElements().iterator().hasNext())
                CorrectGeometricConfiguration.correct(molecule);
        }

        refinePlacement(molecule);
        finalizeLayout(molecule);

        // stereo must be after refinement (due to flipping!)
        if (!isSubLayout)
            assignStereochem(molecule);

    }

    /**
     * Determine if any atoms in a connected molecule are fixed (i.e. already have coordinates/
     * have been placed).
     *
     * @param mol the moleucle to check
     * @return atoms are fixed
     */
    private boolean hasFixedPart(final IAtomContainer mol) {
        if (afix.isEmpty()) return false;
        for (IAtom atom : mol.atoms())
            if (afix.contains(atom))
                return true;
        return false;
    }

    private void seedLayout() throws CDKException {

        final int numAtoms = this.molecule.getAtomCount();
        final int numBonds = this.molecule.getBondCount();
        if (hasFixedPart(molecule)) {

            // no seeding needed as the molecule has atoms with coordinates, just calc rings if needed
            if (prepareRingSystems() > 0) {
                for (IRingSet rset : ringSystems) {
                    if (rset.getFlag(CDKConstants.ISPLACED)) {
                        ringPlacer.placeRingSubstituents(rset, bondLength);
                    } else {
                        List<IRing> placed = new ArrayList<>();
                        List<IRing> unplaced = new ArrayList<>();

                        for (IAtomContainer ring : rset.atomContainers()) {
                            if (ring.getFlag(CDKConstants.ISPLACED))
                                placed.add((IRing) ring);
                            else
                                unplaced.add((IRing) ring);
                        }

                        // partially laid out rings
                        if (placed.isEmpty()) {
                            for (IRing ring : unplaced) {
                                if (ringPlacer.completePartiallyPlacedRing(rset, ring, bondLength))
                                    placed.add(ring);
                            }
                            unplaced.removeAll(placed);
                        }

                        while (!unplaced.isEmpty() && !placed.isEmpty()) {

                            for (IAtomContainer ring : placed) {
                                ringPlacer.placeConnectedRings(rset, (IRing) ring, RingPlacer.FUSED, bondLength);
                                ringPlacer.placeConnectedRings(rset, (IRing) ring, RingPlacer.BRIDGED, bondLength);
                                ringPlacer.placeConnectedRings(rset, (IRing) ring, RingPlacer.SPIRO, bondLength);
                            }
                            Iterator<IRing> unplacedIter = unplaced.iterator();
                            placed.clear();
                            while (unplacedIter.hasNext()) {
                                IRing ring = unplacedIter.next();
                                if (ring.getFlag(CDKConstants.ISPLACED)) {
                                    unplacedIter.remove();
                                    placed.add(ring);
                                }
                            }
                        }

                        if (allPlaced(rset)) {
                            rset.setFlag(CDKConstants.ISPLACED, true);
                            ringPlacer.placeRingSubstituents(rset, bondLength);
                        }
                    }
                }
            }
        } else if (prepareRingSystems() > 0) {
            logger.debug("*** Start of handling rings. ***");
            prepareRingSystems();

            // We got our ring systems now choose the best one based on size and
            // number of heteroatoms
            RingPlacer.countHetero(ringSystems);
            Collections.sort(ringSystems, RingPlacer.RING_COMPARATOR);

            int respect = layoutRingSet(firstBondVector, ringSystems.get(0));

            // rotate monocyclic and when >= 4 polycyclic
            if (respect == 1) {
                if (ringSystems.get(0).getAtomContainerCount() == 1) {
                    respect = 0;
                } else if (ringSystems.size() >= 4) {
                    int numPoly = 0;
                    for (IRingSet rset : ringSystems)
                        if (rset.getAtomContainerCount() > 1)
                            numPoly++;
                    if (numPoly >= 4)
                        respect = 0;
                }
            }

            if (respect == 1 || respect == 2)
                selectOrientation = false;

            logger.debug("First RingSet placed");

            // place of all the directly connected atoms of this ring system
            ringPlacer.placeRingSubstituents(ringSystems.get(0), bondLength);
        } else {

            logger.debug("*** Start of handling purely aliphatic molecules. ***");

            // We are here because there are no rings in the molecule so we get the longest chain in the molecule
            // and placed in on a horizontal axis
            logger.debug("Searching initialLongestChain for this purely aliphatic molecule");
            IAtomContainer longestChain = AtomPlacer.getInitialLongestChain(molecule);
            logger.debug("Found linear chain of length " + longestChain.getAtomCount());
            logger.debug("Setting coordinated of first atom to 0,0");
            longestChain.getAtom(0).setPoint2d(new Point2d(0, 0));
            longestChain.getAtom(0).setFlag(CDKConstants.ISPLACED, true);

            // place the first bond such that the whole chain will be horizontally alligned on the x axis
            logger.debug("Attempting to place the first bond such that the whole chain will be horizontally alligned on the x axis");
            if (firstBondVector != null && firstBondVector != DEFAULT_BOND_VECTOR)
                atomPlacer.placeLinearChain(longestChain, firstBondVector, bondLength);
            else
                atomPlacer.placeLinearChain(longestChain, new Vector2d(Math.cos(RAD_30), Math.sin(RAD_30)), bondLength);
            logger.debug("Placed longest aliphatic chain");
        }
    }

    private int prepareRingSystems() {
        final int numRings = Cycles.markRingAtomsAndBonds(molecule);
        // compute SSSR/MCB
        if (numRings > 0) {
            sssr = Cycles.sssr(molecule).toRingSet();

            if (sssr.getAtomContainerCount() < 1)
                throw new IllegalStateException("Molecule expected to have rings, but had none?");

            // Give a handle of our molecule to the ringPlacer
            ringPlacer.checkAndMarkPlaced(sssr);

            // Partition the smallest set of smallest rings into disconnected
            // ring system. The RingPartioner returns a Vector containing
            // RingSets. Each of the RingSets contains rings that are connected
            // to each other either as bridged ringsystems, fused rings or via
            // spiro connections.
            ringSystems = RingPartitioner.partitionRings(sssr);

            // set the in-ring db stereo
            for (IStereoElement se : molecule.stereoElements()) {
                if (se.getConfigClass() == IStereoElement.CisTrans) {
                    IBond stereoBond    = (IBond) se.getFocus();
                    IBond firstCarrier  = (IBond) se.getCarriers().get(0);
                    IBond secondCarrier = (IBond) se.getCarriers().get(1);
                    for (IRingSet ringSet : ringSystems) {
                        for (IAtomContainer ring : ringSet.atomContainers()) {
                            if (ring.contains(stereoBond)) {
                                List<IBond> begBonds = ring.getConnectedBondsList(stereoBond.getBegin());
                                List<IBond> endBonds = ring.getConnectedBondsList(stereoBond.getEnd());
                                begBonds.remove(stereoBond);
                                endBonds.remove(stereoBond);
                                // something odd wrong, just skip it
                                if (begBonds.size() != 1 || endBonds.size() != 1)
                                    continue;
                                boolean flipped = begBonds.contains(firstCarrier) != endBonds.contains(secondCarrier);
                                int cfg = flipped ? se.getConfigOrder() ^ 0x3 : se.getConfigOrder();
                                ring.addStereoElement(new DoubleBondStereochemistry(stereoBond,
                                                                                    new IBond[]{begBonds.get(0), endBonds.get(0)},
                                                                                    cfg));
                            }
                        }
                    }

                }
            }
        } else {
            sssr = molecule.getBuilder().newInstance(IRingSet.class);
            ringSystems = new ArrayList<>();
        }
        return numRings;
    }

    private void assignStereochem(IAtomContainer molecule) {
        // XXX: can't check this unless we store 'unspecified' double bonds
        // if (!molecule.stereoElements().iterator().hasNext())
        //     return;

        // assign up/down labels, this doesn't not alter layout and could be
        // done on-demand (e.g. when writing a MDL Molfile)
        NonplanarBonds.assign(molecule);
    }

    private void refinePlacement(IAtomContainer molecule) {
        AtomPlacer.prioritise(molecule);

        // refine the layout by rotating, bending, and stretching bonds
        LayoutRefiner refiner = new LayoutRefiner(molecule, afix, bfix);
        refiner.refine();

        // check for attachment points, these override the direction which we rorate structures
        IAtom begAttach = null;
        for (IAtom atom : molecule.atoms()) {
            if (atom instanceof IPseudoAtom && ((IPseudoAtom) atom).getAttachPointNum() == 1) {
                begAttach = atom;
                selectOrientation = true;
                break;
            }
        }

        // choose the orientation in which to display the structure
        if (selectOrientation) {
            // no attachment point, rotate to maximise horizontal spread etc.
            if (begAttach == null) {
                selectOrientation(molecule, DEFAULT_BOND_LENGTH, 1);
            }
            // use attachment point bond to rotate
            else {
                final List<IBond> attachBonds = molecule.getConnectedBondsList(begAttach);
                if (attachBonds.size() == 1) {
                    IAtom end = attachBonds.get(0).getOther(begAttach);
                    Point2d xyBeg = begAttach.getPoint2d();
                    Point2d xyEnd = end.getPoint2d();

                    // snap to horizontal '*-(end)-{rest of molecule}'
                    GeometryUtil.rotate(molecule,
                                        GeometryUtil.get2DCenter(molecule),
                                        -Math.atan2(xyEnd.y - xyBeg.y, xyEnd.x - xyBeg.x));

                    // put the larger part of the structure is above the bond so fragments are drawn
                    // semi-consistently
                    double ylo = 0;
                    double yhi = 0;
                    for (IAtom atom : molecule.atoms()) {
                        double yDelta = xyBeg.y - atom.getPoint2d().y;
                        if (yDelta > 0 && yDelta > yhi) {
                            yhi = yDelta;
                        } else if (yDelta < 0 && yDelta < ylo) {
                            ylo = yDelta;
                        }
                    }

                    // mirror points if larger part is below
                    if (Math.abs(ylo) < yhi)
                        for (IAtom atom : molecule.atoms())
                            atom.getPoint2d().y = -atom.getPoint2d().y;

                    // rotate pointing downwards 30-degrees
                    GeometryUtil.rotate(molecule,
                                        GeometryUtil.get2DCenter(molecule),
                                        -Math.toRadians(30));
                }
            }
        }
    }

    /**
     * Finalize the molecule layout, primarily updating Sgroups.
     *
     * @param mol molecule being laid out
     */
    private void finalizeLayout(IAtomContainer mol) {
        placeMultipleGroups(mol);
        placePositionalVariation(mol);
        placeSgroupBrackets(mol);
    }

    /**
     * Calculates a histogram of bond directions, this allows us to select an
     * orientation that has bonds at nice angles (e.g. 60/120 deg). The limit
     * parameter is used to quantize the vectors within a range. For example
     * a limit of 60 will fill the histogram 0..59 and Bond's orientated at 0,
     * 60, 120 degrees will all be counted in the 0 bucket.
     *
     * @param mol molecule
     * @param counts the histogram is stored here, will be cleared
     * @param lim wrap angles to the (180 max)
     * @return number of aligned bonds
     */
    private static void calcDirectionHistogram(IAtomContainer mol,
                                               int[] counts,
                                               int lim) {
        if (lim > 180)
            throw new IllegalArgumentException("limit must be ≤ 180");
        Arrays.fill(counts, 0);
        for (IBond bond : mol.bonds()) {
            Point2d beg = bond.getBegin().getPoint2d();
            Point2d end = bond.getEnd().getPoint2d();
            Vector2d vec = new Vector2d(end.x - beg.x, end.y - beg.y);
            if (vec.x < 0)
                vec.negate();
            double angle = Math.PI/2 + Math.atan2(vec.y, vec.x);
            counts[(int)(Math.round(Math.toDegrees(angle))%lim)]++;
        }
    }

    /**
     * Select the global orientation of the layout. We click round at 30 degree increments
     * and select the orientation that a) is the widest or b) has the most bonds aligned to
     * +/- 30 degrees {@cdk.cite Clark06}.
     *
     * @param mol       molecule
     * @param widthDiff parameter at which to consider orientations equally good (wide select)
     * @param alignDiff parameter at which we consider orientations equally good (bond align select)
     */
    private void selectOrientation(IAtomContainer mol, double widthDiff, int alignDiff) {

        int[]    dirhist = new int[180];
        double[] minmax  = GeometryUtil.getMinMax(mol);
        Point2d pivot = new Point2d(minmax[0] + ((minmax[2] - minmax[0]) / 2),
                                    minmax[1] + ((minmax[3] - minmax[1]) / 2));

        // initial alignment to snapping bonds 60 degrees
        calcDirectionHistogram(mol, dirhist, 60);
        int max = 0;
        for (int i = 1; i < dirhist.length; i++)
            if (dirhist[i] > dirhist[max])
                max = i;
        // only apply if 50% of the bonds are pointing the same 'wrapped'
        // direction, max=0 means already aligned
        if (max != 0 && dirhist[max]/(double)mol.getBondCount() > 0.5)
            GeometryUtil.rotate(mol, pivot, Math.toRadians(60-max));

        double maxWidth = minmax[2] - minmax[0];
        double begWidth = maxWidth;
        calcDirectionHistogram(mol, dirhist, 180);
        int maxAligned = dirhist[60]+dirhist[120];

        Point2d[] coords = new Point2d[mol.getAtomCount()];
        for (int i = 0; i < mol.getAtomCount(); i++)
            coords[i] = new Point2d(mol.getAtom(i).getPoint2d());

        double step = Math.PI/3;
        double tau = 2*Math.PI;
        double total = 0;

        while (total < tau) {

            total += step;
            GeometryUtil.rotate(mol, pivot, step);
            minmax = GeometryUtil.getMinMax(mol);

            double width = minmax[2] - minmax[0];
            double delta = Math.abs(width - begWidth);

            // if this orientation is significantly wider than the
            // best so far select it
            if (delta >= widthDiff && width > maxWidth) {
                maxWidth = width;
                for (int j = 0; j < mol.getAtomCount(); j++)
                    coords[j] = new Point2d(mol.getAtom(j).getPoint2d());
            }
            // width is not significantly better or worse so check
            // the number of bonds aligned to 30 deg (aesthetics)
            else if (delta <= widthDiff) {
                calcDirectionHistogram(mol, dirhist, 180);
                int aligned = dirhist[60]+dirhist[120];
                int alignDelta = aligned - maxAligned;
                if (alignDelta > alignDiff || (alignDelta == 0 && width > maxWidth)) {
                    maxAligned = aligned;
                    maxWidth = width;
                    for (int j = 0; j < mol.getAtomCount(); j++)
                        coords[j] = new Point2d(mol.getAtom(j).getPoint2d());
                }
            }
        }

        // set the best coordinates we found
        for (int i = 0; i < mol.getAtomCount(); i++)
            mol.getAtom(i).setPoint2d(coords[i]);
    }

    private final double adjustForHydrogen(IAtom atom, IAtomContainer mol) {
        Integer hcnt = atom.getImplicitHydrogenCount();
        if (hcnt == null || hcnt == 0)
            return 0;
        List<IBond> bonds = mol.getConnectedBondsList(atom);

        int pos = 0; // right

        // isolated atoms, HCl vs NH4+ etc
        if (bonds.isEmpty()) {
            Elements elem = Elements.ofNumber(atom.getAtomicNumber());
            // see HydrogenPosition for canonical list
            switch (elem) {
                case Oxygen:
                case Sulfur:
                case Selenium:
                case Tellurium:
                case Fluorine:
                case Chlorine:
                case Bromine:
                case Iodine:
                    pos = -1; // left
                    break;
                default:
                    pos = +1; // right
                    break;
            }
        } else if (bonds.size() == 1) {
            IAtom  other  = bonds.get(0).getOther(atom);
            double deltaX = atom.getPoint2d().x - other.getPoint2d().x;
            if (Math.abs(deltaX) > 0.05)
                pos = (int) Math.signum(deltaX);
        }
        return pos * (bondLength/2);
    }

    /**
     * Similar to the method {@link GeometryUtil#getMinMax(IAtomContainer)} but considers
     * heteroatoms with hydrogens.
     *
     * @param mol molecule
     * @return the min/max x and y bounds
     */
    private final double[] getAprxBounds(IAtomContainer mol) {
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        IAtom[] boundedAtoms = new IAtom[4];
        for (int i = 0; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            if (atom.getPoint2d() != null) {
                if (atom.getPoint2d().x < minX) {
                    minX = atom.getPoint2d().x;
                    boundedAtoms[0] = atom;
                }
                if (atom.getPoint2d().y < minY) {
                    minY = atom.getPoint2d().y;
                    boundedAtoms[1] = atom;
                }
                if (atom.getPoint2d().x > maxX) {
                    maxX = atom.getPoint2d().x;
                    boundedAtoms[2] = atom;
                }
                if (atom.getPoint2d().y > maxY) {
                    maxY = atom.getPoint2d().y;
                    boundedAtoms[3] = atom;
                }
            }
        }
        double[] minmax = new double[4];
        minmax[0] = minX;
        minmax[1] = minY;
        minmax[2] = maxX;
        minmax[3] = maxY;
        double minXAdjust = adjustForHydrogen(boundedAtoms[0], mol);
        double maxXAdjust = adjustForHydrogen(boundedAtoms[1], mol);
        if (minXAdjust < 0) minmax[0] += minXAdjust;
        if (maxXAdjust > 0) minmax[1] += maxXAdjust;
        return minmax;
    }

    private void generateFragmentCoordinates(IAtomContainer mol, List<IAtomContainer> frags) throws CDKException {
        final List<IBond> ionicBonds = makeIonicBonds(frags);

        if (!ionicBonds.isEmpty()) {
            // add tmp bonds and re-fragment
            int rollback = mol.getBondCount();
            for (IBond bond : ionicBonds)
                mol.addBond(bond);
            frags = toList(ConnectivityChecker.partitionIntoMolecules(mol));

            // rollback temporary bonds
            int numBonds = mol.getBondCount();
            while (numBonds-- > rollback)
                mol.removeBond(numBonds);
        }

        List<double[]> limits = new ArrayList<>();
        final int numFragments = frags.size();

        // avoid overwriting our state
        Set<IAtom> afixbackup = new HashSet<>(afix);
        Set<IBond> bfixbackup = new HashSet<>(bfix);

        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);

        // generate the sub-layouts
        for (IAtomContainer fragment : frags) {
            setMolecule(fragment, false, afix, bfix);
            generateCoordinates(DEFAULT_BOND_VECTOR, true, true);
            lengthenIonicBonds(ionicBonds, fragment);
            double[] aprxBounds = getAprxBounds(fragment);

            if (sgroups != null && sgroups.size() > 0) {
                boolean hasBracket = false;
                for (Sgroup sgroup : sgroups) {
                    if (!hasBrackets(sgroup))
                        continue;
                    boolean contained = true;
                    Set<IAtom> aset = sgroup.getAtoms();
                    for (IAtom atom : sgroup.getAtoms()) {
                        if (!aset.contains(atom))
                            contained = false;
                    }
                    if (contained) {
                        hasBracket = true;
                        break;
                    }
                }

                if (hasBracket) {
                    // consider potential Sgroup brackets
                    aprxBounds[0] -= SGROUP_BRACKET_PADDING_FACTOR * bondLength;
                    aprxBounds[1] -= SGROUP_BRACKET_PADDING_FACTOR * bondLength;
                    aprxBounds[2] += SGROUP_BRACKET_PADDING_FACTOR * bondLength;
                    aprxBounds[3] += SGROUP_BRACKET_PADDING_FACTOR * bondLength;
                }
            }

            limits.add(aprxBounds);
        }

        // restore
        afix = afixbackup;
        bfix = bfixbackup;

        final int nRow = (int) Math.floor(Math.sqrt(numFragments));
        final int nCol = (int) Math.ceil(numFragments / (double) nRow);

        final double[] xOffsets = new double[nCol + 1];
        final double[] yOffsets = new double[nRow + 1];

        // calc the max widths/height of each row, we also add some
        // spacing
        double spacing = bondLength;
        for (int i = 0; i < numFragments; i++) {
            // +1 because first offset is always 0
            int col = 1 + i % nCol;
            int row = 1 + i / nCol;

            double[] minmax = limits.get(i);
            final double width = spacing + (minmax[2] - minmax[0]);
            final double height = spacing + (minmax[3] - minmax[1]);

            if (width > xOffsets[col])
                xOffsets[col] = width;
            if (height > yOffsets[row])
                yOffsets[row] = height;
        }

        // cumulative counts
        for (int i = 1; i < xOffsets.length; i++)
            xOffsets[i] += xOffsets[i - 1];
        for (int i = 1; i < yOffsets.length; i++)
            yOffsets[i] += yOffsets[i - 1];

        // translate the molecules, note need to flip y axis
        for (int i = 0; i < limits.size(); i++) {
            final int row = nRow - (i / nCol) - 1;
            final int col = i % nCol;
            Point2d dest = new Point2d((xOffsets[col] + xOffsets[col + 1]) / 2,
                                       (yOffsets[row] + yOffsets[row + 1]) / 2);
            double[] minmax = limits.get(i);
            Point2d curr = new Point2d((minmax[0] + minmax[2]) / 2, (minmax[1] + minmax[3]) / 2);
            GeometryUtil.translate2D(frags.get(i),
                                     dest.x - curr.x, dest.y - curr.y);
        }

        // correct double-bond stereo, this changes the layout and in reality
        // should be done during the initial placement
        if (mol.stereoElements().iterator().hasNext())
            CorrectGeometricConfiguration.correct(mol);

        // finalize
        assignStereochem(mol);
        finalizeLayout(mol);
    }

    private void lengthenIonicBonds(List<IBond> ionicBonds, IAtomContainer fragment) {

        final IChemObjectBuilder bldr = fragment.getBuilder();

        if (ionicBonds.isEmpty())
            return;

        IAtomContainer newfrag = bldr.newInstance(IAtomContainer.class);
        IAtom[] atoms = new IAtom[fragment.getAtomCount()];
        for (int i = 0; i < atoms.length; i++)
            atoms[i] = fragment.getAtom(i);
        newfrag.setAtoms(atoms);

        for (IBond bond : fragment.bonds()) {
            if (!ionicBonds.contains(bond)) {
                newfrag.addBond(bond);
            } else {
                Integer numBegIonic = bond.getBegin().getProperty("ionicDegree");
                Integer numEndIonic = bond.getEnd().getProperty("ionicDegree");
                if (numBegIonic == null) numBegIonic = 0;
                if (numEndIonic == null) numEndIonic = 0;
                numBegIonic++;
                numEndIonic++;
                bond.getBegin().setProperty("ionicDegree", numBegIonic);
                bond.getEnd().setProperty("ionicDegree", numEndIonic);
            }
        }

        if (newfrag.getBondCount() == fragment.getBondCount())
            return;

        IAtomContainerSet subfragments = ConnectivityChecker.partitionIntoMolecules(newfrag);
        Map<IAtom, IAtomContainer> atomToFrag = new HashMap<>();

        // index atom->fragment
        for (IAtomContainer subfragment : subfragments.atomContainers())
            for (IAtom atom : subfragment.atoms())
                atomToFrag.put(atom, subfragment);

        for (IBond bond : ionicBonds) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();

            // select which bond to stretch from
            Integer numBegIonic = bond.getBegin().getProperty("ionicDegree");
            Integer numEndIonic = bond.getEnd().getProperty("ionicDegree");
            if (numBegIonic == null || numEndIonic == null)
                continue;
            if (numBegIonic > numEndIonic) {
                IAtom tmp = beg;
                beg = end;
                end = tmp;
            } else if (numBegIonic.equals(numEndIonic) && numBegIonic > 1) {
                // can't stretch these
                continue;
            }

            IAtomContainer begFrag  = atomToFrag.get(beg);
            IAtomContainer endFrags = bldr.newInstance(IAtomContainer.class);
            if (begFrag == null)
                continue;
            for (IAtomContainer mol : subfragments.atomContainers()) {
                if (mol != begFrag)
                    endFrags.add(mol);
            }
            double dx = end.getPoint2d().x - beg.getPoint2d().x;
            double dy = end.getPoint2d().y - beg.getPoint2d().y;
            Vector2d bondVec = new Vector2d(dx, dy);
            bondVec.normalize();
            bondVec.scale(bondLength/2); // 1.5 bond length
            GeometryUtil.translate2D(endFrags, bondVec);
        }
    }

    /**
     * Property to cache the charge of a fragment.
     */
    private static final String FRAGMENT_CHARGE = "FragmentCharge";

    /**
     * Merge fragments with duplicate atomic ions (e.g. [Na+].[Na+].[Na+]) into
     * single fragments.
     *
     * @param frags input fragments (all connected)
     * @return the merge ions
     */
    private List<IAtomContainer> mergeAtomicIons(final List<IAtomContainer> frags) {
        final List<IAtomContainer> res = new ArrayList<>(frags.size());
        for (IAtomContainer frag : frags) {

            IChemObjectBuilder bldr = frag.getBuilder();

            if (frag.getBondCount() > 0 || res.isEmpty()) {
                res.add(bldr.newInstance(IAtomContainer.class, frag));
            } else {
                // try to find matching atomic ion
                int i = 0;
                while (i < res.size()) {
                    IAtom iAtm = frag.getAtom(0);
                    if (res.get(i).getBondCount() == 0) {
                        IAtom jAtm = res.get(i).getAtom(0);
                        if (nullAsZero(iAtm.getFormalCharge()) == nullAsZero(jAtm.getFormalCharge()) &&
                            nullAsZero(iAtm.getAtomicNumber()) == nullAsZero(jAtm.getAtomicNumber()) &&
                            nullAsZero(iAtm.getImplicitHydrogenCount()) == nullAsZero(jAtm.getImplicitHydrogenCount())) {
                            break;
                        }
                    }
                    i++;
                }

                if (i < res.size()) {
                    res.get(i).add(frag);
                } else {
                    res.add(bldr.newInstance(IAtomContainer.class, frag));
                }
            }
        }
        return res;
    }

    /**
     * Select ions from a charged fragment. Ions not in charge separated
     * bonds are favoured but select if needed. If an atom has lost or
     * gained more than one electron it is added mutliple times to the
     * output list
     *
     * @param frag charged fragment
     * @param sign the charge sign to select (+1 : cation, -1: anion)
     * @return the select atoms (includes duplicates)
     */
    private List<IAtom> selectIons(IAtomContainer frag, int sign) {
        int fragChg = frag.getProperty(FRAGMENT_CHARGE);
        assert Integer.signum(fragChg) == sign;
        final List<IAtom> atoms = new ArrayList<>();

        FIRST_PASS:
        for (IAtom atom : frag.atoms()) {
            if (fragChg == 0)
                break;
            int atmChg = nullAsZero(atom.getFormalCharge());
            if (Integer.signum(atmChg) == sign) {

                // skip in first pass if charge separated
                for (IBond bond : frag.getConnectedBondsList(atom)) {
                    if (Integer.signum(nullAsZero(bond.getOther(atom).getFormalCharge())) + sign == 0)
                        continue FIRST_PASS;
                }

                while (fragChg != 0 && atmChg != 0) {
                    atoms.add(atom);
                    atmChg -= sign;
                    fragChg -= sign;
                }
            }
        }

        if (fragChg == 0)
            return atoms;

        for (IAtom atom : frag.atoms()) {
            if (fragChg == 0)
                break;
            int atmChg = nullAsZero(atom.getFormalCharge());
            if (Math.signum(atmChg) == sign) {
                while (fragChg != 0 && atmChg != 0) {
                    atoms.add(atom);
                    atmChg -= sign;
                    fragChg -= sign;
                }
            }
        }

        return atoms;
    }

    /**
     * Alternative method name "Humpty Dumpty" (a la. R Sayle).
     * 
     * (Re)bonding of ionic fragments for improved layout. This method takes a list
     * of two or more fragments and creates zero or more bonds (return value) that
     * should be temporarily used for layout generation. In general this problem is
     * difficult but since molecules will be laid out in a grid by default - any
     * positioning is an improvement. Heuristics could be added if bad (re)bonds
     * are seen.
     *
     * @param frags connected fragments
     * @return ionic bonds to make
     */
    private List<IBond> makeIonicBonds(final List<IAtomContainer> frags) {
        assert frags.size() > 1;

        // merge duplicates together, e.g. [H-].[H-].[H-].[Na+].[Na+].[Na+]
        // would be two needsMerge fragments. We currently only do single
        // atoms but in theory could also do larger ones
        final List<IAtomContainer> mergedFrags = mergeAtomicIons(frags);
        final List<IAtomContainer> posFrags = new ArrayList<>();
        final List<IAtomContainer> negFrags = new ArrayList<>();

        int chgSum = 0;
        for (IAtomContainer frag : mergedFrags) {
            int chg = 0;
            for (final IAtom atom : frag.atoms())
                chg += nullAsZero(atom.getFormalCharge());
            chgSum += chg;
            frag.setProperty(FRAGMENT_CHARGE, chg);
            if (chg < 0)
                negFrags.add(frag);
            else if (chg > 0)
                posFrags.add(frag);
        }

        // non-neutral or we only have one needsMerge fragment?
        if (chgSum != 0 || mergedFrags.size() == 1)
            return Collections.emptyList();

        List<IAtom> cations = new ArrayList<>();
        List<IAtom> anions = new ArrayList<>();

        // trivial case
        if (posFrags.size() == 1 && negFrags.size() == 1) {
            cations.addAll(selectIons(posFrags.get(0), +1));
            anions.addAll(selectIons(negFrags.get(0), -1));
        } else {

            // sort hi->lo fragment charge, if same charge then we put smaller
            // fragments (bond count) before in cations and after in anions
            Comparator<IAtomContainer> comparator = new Comparator<IAtomContainer>() {
                @Override
                public int compare(IAtomContainer a, IAtomContainer b) {
                    int qA = a.getProperty(FRAGMENT_CHARGE);
                    int qB = b.getProperty(FRAGMENT_CHARGE);
                    int cmp = Integer.compare(Math.abs(qA), Math.abs(qB));
                    if (cmp != 0) return cmp;
                    int sign = Integer.signum(qA);
                    return Integer.compare(sign * a.getBondCount(), sign * b.getBondCount());
                }
            };

            // greedy selection
            Collections.sort(posFrags, comparator);
            Collections.sort(negFrags, comparator);

            for (IAtomContainer posFrag : posFrags)
                cations.addAll(selectIons(posFrag, +1));
            for (IAtomContainer negFrag : negFrags)
                anions.addAll(selectIons(negFrag, -1));
        }

        if (cations.size() != anions.size() && cations.isEmpty())
            return Collections.emptyList();

        final IChemObjectBuilder bldr = frags.get(0).getBuilder();

        // make the bonds
        final List<IBond> ionicBonds = new ArrayList<>(cations.size());
        for (int i = 0; i < cations.size(); i++) {
            final IAtom beg = cations.get(i);
            final IAtom end = anions.get(i);

            boolean unique = true;
            for (IBond bond : ionicBonds)
                if (bond.getBegin().equals(beg) && bond.getEnd().equals(end) ||
                    bond.getEnd().equals(beg) && bond.getBegin().equals(end))
                    unique = false;

            if (unique)
                ionicBonds.add(bldr.newInstance(IBond.class, beg, end));
        }

        // we could merge the fragments here using union-find structures
        // but it's much simpler (and probably more efficient) to return
        // the new bonds and re-fragment the molecule with these bonds added.

        return ionicBonds;
    }

    /**
     * Utility - safely access Object Integers as primitives, when we want the
     * default value of null to be zero.
     *
     * @param x number
     * @return the number primitive or zero if null
     */
    private static int nullAsZero(Integer x) {
        return x == null ? 0 : x;
    }

    /**
     * Utility - get the IAtomContainers as a list.
     *
     * @param frags connected fragments
     * @return list of fragments
     */
    private List<IAtomContainer> toList(IAtomContainerSet frags) {
        return new ArrayList<>(FluentIterable.from(frags.atomContainers()).toList());
    }

    /**
     * The main method of this StructurDiagramGenerator. Assign a molecule to the
     * StructurDiagramGenerator, call the generateCoordinates() method and get
     * your molecule back.
     *
     * @throws CDKException if an error occurs
     */
    public void generateCoordinates() throws CDKException {
        generateCoordinates(DEFAULT_BOND_VECTOR);
    }

    /**
     * Using a fast identity template library, lookup the the ring system and assign coordinates.
     * The method indicates whether a match was found and coordinates were assigned.
     *
     * @param rs       the ring set
     * @param molecule the rest of the compound
     * @param anon     check for anonmised templates
     * @return coordinates were assigned
     */
    private boolean lookupRingSystem(IRingSet rs, IAtomContainer molecule, boolean anon) {

        // identity templates are disabled
        if (!useIdentTemplates) return false;

        final IChemObjectBuilder bldr = molecule.getBuilder();

        final IAtomContainer ringSystem = bldr.newInstance(IAtomContainer.class);
        for (IAtomContainer container : rs.atomContainers())
            ringSystem.add(container);

        final Set<IAtom> ringAtoms = new HashSet<>();
        for (IAtom atom : ringSystem.atoms())
            ringAtoms.add(atom);

        // a temporary molecule of the ring system and 'stubs' of the attached substituents
        final IAtomContainer ringWithStubs = bldr.newInstance(IAtomContainer.class);
        ringWithStubs.add(ringSystem);
        for (IBond bond : molecule.bonds()) {
            IAtom atom1 = bond.getBegin();
            IAtom atom2 = bond.getEnd();
            if (isHydrogen(atom1) || isHydrogen(atom2)) continue;
            if (ringAtoms.contains(atom1) ^ ringAtoms.contains(atom2)) {
                ringWithStubs.addAtom(atom1);
                ringWithStubs.addAtom(atom2);
                ringWithStubs.addBond(bond);
            }
        }

        // Three levels of identity to check are as follows:
        //   Level 1 - check for a skeleton ring system and attached substituents
        //   Level 2 - check for a skeleton ring system
        //   Level 3 - check for an anonymous ring system
        // skeleton = all single bonds connecting different elements
        // anonymous = all single bonds connecting carbon
        final IAtomContainer skeletonStub = clearHydrogenCounts(AtomContainerManipulator.skeleton(ringWithStubs));
        final IAtomContainer skeleton = clearHydrogenCounts(AtomContainerManipulator.skeleton(ringSystem));
        final IAtomContainer anonymous = clearHydrogenCounts(AtomContainerManipulator.anonymise(ringSystem));

        for (IAtomContainer container : Arrays.asList(skeletonStub, skeleton, anonymous)) {

            if (!anon && container == anonymous)
                continue;

            // assign the atoms 0 to |ring|, the stubs are added at the end of the container
            // and are not placed here (since the index of each stub atom is > |ring|)
            if (identityLibrary.assignLayout(container)) {
                for (int i = 0; i < ringSystem.getAtomCount(); i++) {
                    IAtom atom = ringSystem.getAtom(i);
                    atom.setPoint2d(container.getAtom(i).getPoint2d());
                    atom.setFlag(CDKConstants.ISPLACED, true);
                }
                return true;
            }
        }

        return false;
    }

    /**
     * Is an atom a hydrogen atom.
     *
     * @param atom an atom
     * @return the atom is a hydrogen
     */
    private static boolean isHydrogen(IAtom atom) {
        if (atom.getAtomicNumber() != null) return atom.getAtomicNumber() == 1;
        return "H".equals(atom.getSymbol());
    }

    /**
     * Simple helper function that sets all hydrogen counts to 0.
     *
     * @param container a structure representation
     * @return the input container
     */
    private static IAtomContainer clearHydrogenCounts(IAtomContainer container) {
        for (IAtom atom : container.atoms())
            atom.setImplicitHydrogenCount(0);
        return container;
    }

    /**
     * Layout a set of connected rings (ring set/ring system). <br/>
     *
     * Current Scheme:
     *   1. Lookup the entire ring system for a known template.
     *   2. If first (most complex) ring is macrocycle,
     *      2a. Assign coordinates from macro cycle templates
     *   3. If first is not-macrocycle (or currently doesn't match out templates)
     *      3a. Layout as regular polygon
     *   4. Sequentially connected layout rings {@link RingPlacer}
     *
     * @param firstBondVector A vector giving the placement for the first bond
     * @param rs              The connected RingSet to layout
     */
    private int layoutRingSet(Vector2d firstBondVector, IRingSet rs) {

        // sort small -> large
        // Get the most complex ring in this RingSet (largest prioritized)
        RingSetManipulator.sort(rs);
        final IRing first = RingSetManipulator.getMostComplexRing(rs);

        final boolean macro         = isMacroCycle(first, rs);
        final boolean macroDbStereo = macro && first.stereoElements().iterator().hasNext();
        int result = 0;

        // Check for an exact match (identity) on the entire ring system
        if (!macroDbStereo) {
            if (lookupRingSystem(rs, molecule, rs.getAtomContainerCount() > 1)) {
                for (IAtomContainer container : rs.atomContainers())
                    container.setFlag(CDKConstants.ISPLACED, true);
                rs.setFlag(CDKConstants.ISPLACED, true);
                return macro ? 2 : 1;
            } else {
                // attempt ring peeling and retemplate
                final IRingSet core = getRingSetCore(rs);
                if (core.getAtomContainerCount() > 0 &&
                    core.getAtomContainerCount() < rs.getAtomContainerCount() &&
                    lookupRingSystem(core, molecule, !macro || rs.getAtomContainerCount() > 1)) {
                    for (IAtomContainer container : core.atomContainers())
                        container.setFlag(CDKConstants.ISPLACED, true);
                }
            }
        }

        // Place the most complex ring at the origin of the coordinate system
        if (!first.getFlag(CDKConstants.ISPLACED)) {
            IAtomContainer sharedAtoms = placeFirstBond(first.getBond(0), firstBondVector);
            if (!macro || !macroPlacer.layout(first, rs)) {
                // de novo layout of ring as a regular polygon
                Vector2d ringCenterVector = ringPlacer.getRingCenterOfFirstRing(first, firstBondVector, bondLength);
                ringPlacer.placeRing(first, sharedAtoms, GeometryUtil.get2DCenter(sharedAtoms), ringCenterVector, bondLength);
            } else {
                result = 2;
            }
            first.setFlag(CDKConstants.ISPLACED, true);
        }

        // hint to RingPlacer
        if (macro) {
            for (IAtomContainer ring : rs.atomContainers())
                ring.setProperty(RingPlacer.SNAP_HINT, true);
        }

        // Place all connected rings start with those connected to first
        int thisRing = 0;
        IRing ring = first;
        do {
            if (ring.getFlag(CDKConstants.ISPLACED)) {
                ringPlacer.placeConnectedRings(rs, ring, RingPlacer.FUSED, bondLength);
                ringPlacer.placeConnectedRings(rs, ring, RingPlacer.BRIDGED, bondLength);
                ringPlacer.placeConnectedRings(rs, ring, RingPlacer.SPIRO, bondLength);
            }
            thisRing++;
            if (thisRing == rs.getAtomContainerCount()) {
                thisRing = 0;
            }
            ring = (IRing) rs.getAtomContainer(thisRing);
        } while (!allPlaced(rs));

        return result;
    }

    /**
     * Peel back terminal rings to the complex 'core': {@cdk.cite Helson99}, {@cdk.cite Clark06}.
     *
     * @param rs ring set
     * @return the ring set core
     */
    private IRingSet getRingSetCore(IRingSet rs) {

        Multimap<IBond, IRing> ringlookup = HashMultimap.create();
        Set<IRing> ringsystem = new LinkedHashSet<>();

        for (IAtomContainer ring : rs.atomContainers()) {
            ringsystem.add((IRing) ring);
            for (IBond bond : ring.bonds())
                ringlookup.put(bond, (IRing) ring);
        }

        // iteratively reduce ring system by removing ring that only share one bond
        Set<IRing> toremove = new HashSet<>();
        do {
            toremove.clear();
            for (IRing ring : ringsystem) {
                int numAttach = 0;
                for (IBond bond : ring.bonds()) {
                    for (IRing attached : ringlookup.get(bond)) {
                        if (attached != ring && ringsystem.contains(attached)) {
                            numAttach++;
                            break;
                        }
                    }
                }
                if (numAttach <= 1)
                    toremove.add(ring);
            }
            ringsystem.removeAll(toremove);
        } while (!toremove.isEmpty());

        final IRingSet core = rs.getBuilder().newInstance(IRingSet.class);
        for (IRing ring : ringsystem)
            core.addAtomContainer(ring);

        return core;
    }

    /**
     * Check if a ring in a ring set is a macro cycle. We define this as a
     * ring with >= 10 atom and has at least one bond that isn't contained
     * in any other rings.
     *
     * @param ring ring to check
     * @param rs   rest of ring system
     * @return ring is a macro cycle
     */
    private boolean isMacroCycle(IRing ring, IRingSet rs) {
        if (ring.getAtomCount() < 8)
            return false;
        for (IBond bond : ring.bonds()) {
            boolean found = false;
            for (IAtomContainer other : rs.atomContainers()) {
                if (ring == other)
                    continue;
                if (other.contains(bond)) {
                    found = true;
                    break;
                }
            }
            if (!found)
                return true;
        }
        return false;
    }

    /**
     * Does a layout of all aliphatic parts connected to the parts of the molecule
     * that have already been laid out. Starts at the first bond with unplaced
     * neighbours and stops when a ring is encountered.
     *
     * @throws CDKException if an error occurs
     */
    private void layoutAcyclicParts() throws CDKException {
        logger.debug("Start of handleAliphatics");

        int safetyCounter = 0;
        IAtomContainer unplacedAtoms = null;
        IAtomContainer placedAtoms = null;
        IAtomContainer longestUnplacedChain = null;
        IAtom atom = null;

        Vector2d direction = null;
        Vector2d startVector = null;
        boolean done;
        do {
            safetyCounter++;
            done = false;
            atom = getNextAtomWithAliphaticUnplacedNeigbors();
            if (atom != null) {
                unplacedAtoms = getUnplacedAtoms(atom);
                placedAtoms = getPlacedAtoms(atom);

                longestUnplacedChain = atomPlacer.getLongestUnplacedChain(molecule, atom);

                logger.debug("---start of longest unplaced chain---");
                try {
                    logger.debug("Start at atom no. " + (molecule.indexOf(atom) + 1));
                    logger.debug(AtomPlacer.listNumbers(molecule, longestUnplacedChain));
                } catch (Exception exc) {
                    logger.debug(exc);
                }
                logger.debug("---end of longest unplaced chain---");

                if (longestUnplacedChain.getAtomCount() > 1) {

                    if (placedAtoms.getAtomCount() > 1) {
                        logger.debug("More than one atoms placed already");
                        logger.debug("trying to place neighbors of atom " + (molecule.indexOf(atom) + 1));
                        atomPlacer.distributePartners(atom, placedAtoms, GeometryUtil.get2DCenter(placedAtoms),
                                                      unplacedAtoms, bondLength);
                        direction = new Vector2d(longestUnplacedChain.getAtom(1).getPoint2d());
                        startVector = new Vector2d(atom.getPoint2d());
                        direction.sub(startVector);
                        logger.debug("Done placing neighbors of atom " + (molecule.indexOf(atom) + 1));
                    } else {
                        logger.debug("Less than or equal one atoms placed already");
                        logger.debug("Trying to get next bond vector.");
                        direction = atomPlacer.getNextBondVector(atom, placedAtoms.getAtom(0),
                                                                 GeometryUtil.get2DCenter(molecule), true);

                    }

                    for (int f = 1; f < longestUnplacedChain.getAtomCount(); f++) {
                        longestUnplacedChain.getAtom(f).setFlag(CDKConstants.ISPLACED, false);
                    }
                    atomPlacer.placeLinearChain(longestUnplacedChain, direction, bondLength);

                } else {
                    done = true;
                }
            } else {
                done = true;
            }
        } while (!done && safetyCounter <= molecule.getAtomCount());

        logger.debug("End of handleAliphatics");
    }

    /**
     * Does the layout for the next RingSystem that is connected to those parts of
     * the molecule that have already been laid out. Finds the next ring with an
     * unplaced ring atom and lays out this ring. Then lays out the ring substituents
     * of this ring. Then moves and rotates the laid out ring to match the position
     * of its attachment bond to the rest of the molecule.
     *
     * @throws CDKException if an error occurs
     */
    private void layoutCyclicParts() throws CDKException {
        logger.debug("Start of layoutNextRingSystem()");

        resetUnplacedRings();
        IAtomContainer placedAtoms = AtomPlacer.getPlacedAtoms(molecule);
        logger.debug("Finding attachment bond to already placed part...");
        IBond nextRingAttachmentBond = getNextBondWithUnplacedRingAtom();
        if (nextRingAttachmentBond != null) {
            logger.debug("...bond found.");

            /*
             * Get the chain and the ring atom that are connected to where we
             * are comming from. Both are connected by nextRingAttachmentBond.
             */
            IAtom ringAttachmentAtom = getRingAtom(nextRingAttachmentBond);
            IAtom chainAttachmentAtom = getOtherBondAtom(ringAttachmentAtom, nextRingAttachmentBond);

            // Get ring system which ringAttachmentAtom is part of
            IRingSet nextRingSystem = getRingSystemOfAtom(ringSystems, ringAttachmentAtom);

            // Get all rings of nextRingSytem as one IAtomContainer
            IAtomContainer ringSystem = RingSetManipulator.getAllInOneContainer(nextRingSystem);

            /*
             * Save coordinates of ringAttachmentAtom and chainAttachmentAtom
             */
            Point2d oldRingAttachmentAtomPoint  = ringAttachmentAtom.getPoint2d();
            Point2d oldChainAttachmentAtomPoint = chainAttachmentAtom.getPoint2d();

            /*
             * Do the layout of the next ring system
             */
            layoutRingSet(firstBondVector, nextRingSystem);

            /*
             * Place all the substituents of next ring system
             */
            AtomPlacer.markNotPlaced(placedAtoms);
            IAtomContainer placedRingSubstituents = ringPlacer.placeRingSubstituents(nextRingSystem, bondLength);
            ringSystem.add(placedRingSubstituents);
            AtomPlacer.markPlaced(placedAtoms);

            /*
             * Move and rotate the laid out ring system to match the geometry of
             * the attachment bond
             */
            logger.debug("Computing translation/rotation of new ringset to fit old attachment bond orientation...");

            // old placed ring atom coordinate
            Point2d oldPoint2 = oldRingAttachmentAtomPoint;
            // old placed substituent atom coordinate
            Point2d oldPoint1 = oldChainAttachmentAtomPoint;

            // new placed ring atom coordinate
            Point2d newPoint2 = ringAttachmentAtom.getPoint2d();
            // new placed substituent atom coordinate
            Point2d newPoint1 = chainAttachmentAtom.getPoint2d();

            logger.debug("oldPoint1: " + oldPoint1);
            logger.debug("oldPoint2: " + oldPoint2);
            logger.debug("newPoint1: " + newPoint1);
            logger.debug("newPoint2: " + newPoint2);

            double oldAngle = GeometryUtil.getAngle(oldPoint2.x - oldPoint1.x, oldPoint2.y - oldPoint1.y);
            double newAngle = GeometryUtil.getAngle(newPoint2.x - newPoint1.x, newPoint2.y - newPoint1.y);
            double angleDiff = oldAngle - newAngle;

            logger.debug("oldAngle: " + oldAngle + ", newAngle: " + newAngle + "; diff = " + angleDiff);

            Vector2d translationVector = new Vector2d(oldPoint1);
            translationVector.sub(new Vector2d(newPoint1));

            /*
             * Move to fit old attachment bond orientation
             */
            GeometryUtil.translate2D(ringSystem, translationVector);

            /*
             * Rotate to fit old attachment bond orientation
             */
            GeometryUtil.rotate(ringSystem, oldPoint1, angleDiff);

            logger.debug("...done translating/rotating new ringset to fit old attachment bond orientation.");
        } else {
            logger.debug("...no bond found");

            // partially laid out ring system
            if (ringSystems != null) {
                for (IRingSet ringset : ringSystems) {
                    for (IAtomContainer ring : ringset.atomContainers())
                        ringPlacer.completePartiallyPlacedRing(ringset, (IRing) ring, bondLength);
                    if (allPlaced(ringset))
                        ringPlacer.placeRingSubstituents(ringset, bondLength);
                }
            }
        }

        logger.debug("End of layoutNextRingSystem()");
    }

    /**
     * Returns an AtomContainer with all unplaced atoms connected to a given
     * atom
     *
     * @param atom The Atom whose unplaced bonding partners are to be returned
     * @return an AtomContainer with all unplaced atoms connected to a
     * given atom
     */
    private IAtomContainer getUnplacedAtoms(IAtom atom) {
        IAtomContainer unplacedAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
        List bonds = molecule.getConnectedBondsList(atom);
        IAtom connectedAtom;
        for (int f = 0; f < bonds.size(); f++) {
            connectedAtom = ((IBond) bonds.get(f)).getOther(atom);
            if (!connectedAtom.getFlag(CDKConstants.ISPLACED)) {
                unplacedAtoms.addAtom(connectedAtom);
            }
        }
        return unplacedAtoms;
    }

    /**
     * Returns an AtomContainer with all placed atoms connected to a given
     * atom
     *
     * @param atom The Atom whose placed bonding partners are to be returned
     * @return an AtomContainer with all placed atoms connected to a given
     * atom
     */
    private IAtomContainer getPlacedAtoms(IAtom atom) {
        IAtomContainer placedAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
        List bonds = molecule.getConnectedBondsList(atom);
        IAtom connectedAtom;
        for (int f = 0; f < bonds.size(); f++) {
            connectedAtom = ((IBond) bonds.get(f)).getOther(atom);
            if (connectedAtom.getFlag(CDKConstants.ISPLACED)) {
                placedAtoms.addAtom(connectedAtom);
            }
        }
        return placedAtoms;
    }

    /**
     * Returns the next atom with unplaced aliphatic neighbors
     *
     * @return the next atom with unplaced aliphatic neighbors
     */
    private IAtom getNextAtomWithAliphaticUnplacedNeigbors() {
        IBond bond;
        for (int f = 0; f < molecule.getBondCount(); f++) {
            bond = molecule.getBond(f);

            if (bond.getEnd().getFlag(CDKConstants.ISPLACED) && !bond.getBegin().getFlag(CDKConstants.ISPLACED)) {
                return bond.getEnd();
            }

            if (bond.getBegin().getFlag(CDKConstants.ISPLACED) && !bond.getEnd().getFlag(CDKConstants.ISPLACED)) {
                return bond.getBegin();
            }
        }
        return null;
    }

    /**
     * Returns the next bond with an unplaced ring atom
     *
     * @return the next bond with an unplaced ring atom
     */
    private IBond getNextBondWithUnplacedRingAtom() {
        for (IBond bond : molecule.bonds()) {
            IAtom beg = bond.getBegin();
            IAtom end = bond.getEnd();
            if (beg.getPoint2d() != null && end.getPoint2d() != null) {
                if (end.getFlag(CDKConstants.ISPLACED) && !beg.getFlag(CDKConstants.ISPLACED) && beg.isInRing()) {
                    return bond;
                }
                if (beg.getFlag(CDKConstants.ISPLACED) && !end.getFlag(CDKConstants.ISPLACED) && end.isInRing()) {
                    return bond;
                }
            }
        }
        return null;
    }

    /**
     * Places the first bond of the first ring such that one atom is at (0,0) and
     * the other one at the position given by bondVector
     *
     * @param bondVector A 2D vector to point to the position of the second bond
     *                   atom
     * @param bond       the bond to lay out
     * @return an IAtomContainer with the atoms of the bond and the bond itself
     */
    private IAtomContainer placeFirstBond(IBond bond, Vector2d bondVector) {
        IAtomContainer sharedAtoms = null;

        bondVector.normalize();
        logger.debug("placeFirstBondOfFirstRing->bondVector.length():" + bondVector.length());
        bondVector.scale(bondLength);
        logger.debug("placeFirstBondOfFirstRing->bondVector.length() after scaling:" + bondVector.length());
        IAtom atom;
        Point2d point = new Point2d(0, 0);
        atom = bond.getBegin();
        logger.debug("Atom 1 of first Bond: " + (molecule.indexOf(atom) + 1));
        atom.setPoint2d(point);
        atom.setFlag(CDKConstants.ISPLACED, true);
        point = new Point2d(0, 0);
        atom = bond.getEnd();
        logger.debug("Atom 2 of first Bond: " + (molecule.indexOf(atom) + 1));
        point.add(bondVector);
        atom.setPoint2d(point);
        atom.setFlag(CDKConstants.ISPLACED, true);
        /*
         * The new ring is layed out relativ to some shared atoms that have
         * already been placed. Usually this is another ring, that has
         * already been draw and to which the new ring is somehow connected,
         * or some other system of atoms in an aliphatic chain. In this
         * case, it's the first bond that we layout by hand.
         */
        sharedAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
        sharedAtoms.addAtom(bond.getBegin());
        sharedAtoms.addAtom(bond.getEnd());
        sharedAtoms.addBond(bond);
        return sharedAtoms;
    }

    /**
     * Are all rings in the Vector placed?
     *
     * @param rings The Vector to be checked
     * @return true if all rings are placed, false otherwise
     */
    private boolean allPlaced(IRingSet rings) {
        for (int f = 0; f < rings.getAtomContainerCount(); f++) {
            if (!((IRing) rings.getAtomContainer(f)).getFlag(CDKConstants.ISPLACED)) {
                logger.debug("allPlaced->Ring " + f + " not placed");
                return false;
            }
        }
        return true;
    }

    /**
     * Get the unplaced ring atom in this bond
     *
     * @param bond the bond to be search for the unplaced ring atom
     * @return the unplaced ring atom in this bond
     */
    private IAtom getRingAtom(IBond bond) {
        if (bond.getBegin().getFlag(CDKConstants.ISINRING) && !bond.getBegin().getFlag(CDKConstants.ISPLACED)) {
            return bond.getBegin();
        }
        if (bond.getEnd().getFlag(CDKConstants.ISINRING) && !bond.getEnd().getFlag(CDKConstants.ISPLACED)) {
            return bond.getEnd();
        }
        return null;
    }

    /**
     * Get the ring system of which the given atom is part of
     *
     * @param ringSystems a List of ring systems to be searched
     * @param ringAtom    the ring atom to be search in the ring system.
     * @return the ring system the given atom is part of
     */
    private IRingSet getRingSystemOfAtom(List ringSystems, IAtom ringAtom) {
        IRingSet ringSet = null;
        for (int f = 0; f < ringSystems.size(); f++) {
            ringSet = (IRingSet) ringSystems.get(f);
            if (ringSet.contains(ringAtom)) {
                return ringSet;
            }
        }
        return null;
    }

    /**
     * Set all the atoms in unplaced rings to be unplaced
     */
    private void resetUnplacedRings() {
        IRing ring = null;
        if (sssr == null) {
            return;
        }
        int unplacedCounter = 0;
        for (int f = 0; f < sssr.getAtomContainerCount(); f++) {
            ring = (IRing) sssr.getAtomContainer(f);
            if (!ring.getFlag(CDKConstants.ISPLACED)) {
                logger.debug("Ring with " + ring.getAtomCount() + " atoms is not placed.");
                unplacedCounter++;
                for (int g = 0; g < ring.getAtomCount(); g++) {
                    ring.getAtom(g).setFlag(CDKConstants.ISPLACED, false);
                }
            }
        }
        logger.debug("There are " + unplacedCounter + " unplaced Rings.");
    }

    /**
     * This method used to set the bond length used for laying out the molecule.
     * Since bond lengths in 2D are are arbitrary, the preferred way to do this
     * is with {@link GeometryUtil#scaleMolecule(IAtomContainer, double)}.
     *
     * <pre>
     * IAtomContainer mol = ...;
     * sdg.generateCoordinates(mol);
     * int targetBondLength = 28;
     * double factor = targetBondLength/GeometryUtil.getMedianBondLength(mol);
     * GeometryUtil.scaleMolecule(mol, factor);
     * </pre>
     *
     * @param bondLength The new bondLength value
     * @deprecated use {@link GeometryUtil#scaleMolecule(IAtomContainer, double)}
     * @throws UnsupportedOperationException not supported
     */
    @Deprecated
    public void setBondLength(double bondLength) {
        throw new UnsupportedOperationException(
            "Bond length should be adjusted post layout"
            + " with GeometryUtil.scaleMolecule();");
    }

    /**
     * Returns the bond length used for laying out the molecule.
     *
     * @return The current bond length
     */
    public double getBondLength() {
        return bondLength;
    }

    /**
     * Returns the other atom of the bond.
     * Expects bond to have only two atoms.
     * Returns null if the given atom is not part of the given bond.
     *
     * @param atom the atom we already have
     * @param bond the bond
     * @return the other atom of the bond
     */
    public IAtom getOtherBondAtom(IAtom atom, IBond bond) {
        if (!bond.contains(atom)) return null;
        if (bond.getBegin().equals(atom))
            return bond.getEnd();
        else
            return bond.getBegin();
    }

    /**
     * Multiple groups need special placement by overlaying the repeat part
     * coordinates.
     *
     * coordinates on each other.
     * @param mol molecule to place the multiple groups of
     */
    private void placeMultipleGroups(IAtomContainer mol) {
        final List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return;
        final List<Sgroup> multipleGroups = new ArrayList<>();
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() == SgroupType.CtabMultipleGroup)
                multipleGroups.add(sgroup);
        }
        if (multipleGroups.isEmpty())
            return;

        int[][] adjlist = GraphUtil.toAdjList(mol);
        Map<IAtom,Integer> idxs = new HashMap<>();
        for (IAtom atom : mol.atoms())
            idxs.put(atom, idxs.size());

        for (Sgroup sgroup : multipleGroups) {
            final int numCrossing = sgroup.getBonds().size();
            if (numCrossing != 0 && numCrossing != 2)
                continue;

            // extract substructure
            final IAtomContainer substructure = mol.getBuilder().newInstance(IAtomContainer.class);
            final Set<IAtom> visit = new HashSet<>();
            final Collection<IAtom> patoms = sgroup.getValue(SgroupKey.CtabParentAtomList);
            if (patoms == null)
                continue;
            for (IAtom atom : patoms) {
                substructure.addAtom(atom);
                visit.add(atom);
            }
            for (IBond bond : mol.bonds()) {
                IAtom beg = bond.getBegin();
                IAtom end = bond.getEnd();
                if (visit.contains(beg) && visit.contains(end))
                    substructure.addBond(bond);
            }

            // advanced API usage, we make a set that only includes the atoms we want to match
            // and use this in a custom AtomMatcher to skip matches we don't want and update as
            // we go
            visit.addAll(sgroup.getAtoms());

            Pattern ptrn = VentoFoggia.findSubstructure(substructure, new AtomMatcher() {
                @Override
                public boolean matches(IAtom a, IAtom b) {
                    if (!visit.contains(b))
                        return false;
                    final int aElem = safeUnbox(a.getAtomicNumber());
                    final int bElem = safeUnbox(b.getAtomicNumber());
                    if (aElem != bElem)
                        return false;
                    final int aChg = safeUnbox(a.getFormalCharge());
                    final int bChg = safeUnbox(b.getFormalCharge());
                    if (aChg != bChg)
                        return false;
                    final int aMass = safeUnbox(a.getMassNumber());
                    final int bMass = safeUnbox(b.getMassNumber());
                    if (aMass != bMass)
                        return false;
                    final int aHcnt = safeUnbox(a.getImplicitHydrogenCount());
                    final int bHcnt = safeUnbox(b.getImplicitHydrogenCount());
                    if (aHcnt != bHcnt)
                        return false;
                    return true;
                }
            }, BondMatcher.forOrder());

            Set<IAtom> sgroupAtoms = sgroup.getAtoms();

            // when there are crossing bonds, things are more tricky as
            // we need to translate connected parts
            List<Map.Entry<Point2d,Vector2d>> outgoing = new ArrayList<>();
            List<Map.Entry<IBond,Vector2d>>   xBondVec = new ArrayList<>();
            if (numCrossing == 2) {
                for (IBond bond : mol.bonds()) {
                    IAtom beg = bond.getBegin();
                    IAtom end = bond.getEnd();
                    if (patoms.contains(beg) == patoms.contains(end))
                        continue;
                    if (patoms.contains(beg)) {
                        outgoing.add(new SimpleImmutableEntry<>(beg.getPoint2d(),
                                                                new Vector2d(end.getPoint2d().x - beg.getPoint2d().x,
                                                                             end.getPoint2d().y - beg.getPoint2d().y)));
                    } else {
                        outgoing.add(new SimpleImmutableEntry<>(end.getPoint2d(),
                                                                new Vector2d(beg.getPoint2d().x - end.getPoint2d().x,
                                                                             beg.getPoint2d().y - end.getPoint2d().y)));
                    }
                }
                for (IBond bond : sgroup.getBonds()) {
                    IAtom beg = bond.getBegin();
                    IAtom end = bond.getEnd();
                    if (sgroupAtoms.contains(beg)) {
                        xBondVec.add(new SimpleImmutableEntry<>(bond,
                                                                new Vector2d(end.getPoint2d().x - beg.getPoint2d().x,
                                                                             end.getPoint2d().y - beg.getPoint2d().y)));
                    } else {
                        xBondVec.add(new SimpleImmutableEntry<>(bond,
                                                                new Vector2d(beg.getPoint2d().x - end.getPoint2d().x,
                                                                             beg.getPoint2d().y - end.getPoint2d().y)));
                    }
                }
            }

            // no crossing bonds is easy just map the repeat part and transfer coordinates
            visit.removeAll(patoms); // don't need to map parent
            for (Map<IAtom, IAtom> atoms : ptrn.matchAll(mol).uniqueAtoms().toAtomMap()) {
                for (Map.Entry<IAtom, IAtom> e : atoms.entrySet()) {
                    e.getValue().setPoint2d(new Point2d(e.getKey().getPoint2d()));
                }
                // search is lazy so can update the matcher before the next match
                // is found (implementation ninja)
                visit.removeAll(atoms.values());
            }

            // reposition
            assert xBondVec.size() == outgoing.size();
            for (Map.Entry<IBond,Vector2d> e : xBondVec) {
                IBond bond = e.getKey();

                // can't fix move ring bonds
                if (bond.isInRing())
                    continue;

                IAtom beg  = sgroupAtoms.contains(bond.getBegin()) ? bond.getBegin() : bond.getEnd();
                Map.Entry<Point2d,Vector2d> best = null;
                for (Map.Entry<Point2d,Vector2d> candidate : outgoing) {
                    if (best == null || candidate.getKey().distance(beg.getPoint2d()) < best.getKey().distance(beg.getPoint2d()))
                        best = candidate;
                }
                outgoing.remove(best);
                assert best != null;

                // visit rest of connected molecule
                Set<Integer> iVisit = new HashSet<>();
                iVisit.add(idxs.get(beg));
                visit(iVisit, adjlist, idxs.get(bond.getOther(beg)));
                iVisit.remove(idxs.get(beg));
                IAtomContainer frag = mol.getBuilder().newInstance(IAtomContainer.class);
                for (Integer idx : iVisit)
                    frag.addAtom(mol.getAtom(idx));

                Vector2d orgVec = e.getValue();
                Vector2d newVec = best.getValue();

                Point2d endP    = bond.getOther(beg).getPoint2d();
                Point2d newEndP = new Point2d(beg.getPoint2d());
                newEndP.add(newVec);

                // need perpendicular dot product to get signed angle
                double pDot  = orgVec.x * newVec.y - orgVec.y * newVec.x;
                double theta = Math.atan2(pDot, newVec.dot(orgVec));

                // position
                GeometryUtil.translate2D(frag, newEndP.x - endP.x, newEndP.y - endP.y);
                GeometryUtil.rotate(frag, new Point2d(bond.getOther(beg).getPoint2d()), theta);
            }
        }

    }

    private int safeUnbox(Integer x) {
        return x == null ? 0 : x;
    }

    private int getPositionalRingBondPref(IBond bond, IAtomContainer mol) {
        int begRingBonds = numRingBonds(mol, bond.getBegin());
        int endRingBonds = numRingBonds(mol, bond.getEnd());
        if (begRingBonds == 2 && endRingBonds == 2)
            return 0;
        if ((begRingBonds > 2 && endRingBonds == 2) ||
            (begRingBonds == 2 && endRingBonds > 2))
            return 1;
        return 2;
    }

    private void placePositionalVariation(final IAtomContainer mol) {

        final List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return;

        Multimap<Set<IAtom>, IAtom> mapping = aggregateMulticenterSgroups(sgroups);

        if (mapping.isEmpty())
            return;

        // helps with traversal
        GraphUtil.EdgeToBondMap bondMap = GraphUtil.EdgeToBondMap.withSpaceFor(mol);
        int[][] adjlist = GraphUtil.toAdjList(mol, bondMap);
        Map<IAtom,Integer> idxs = new HashMap<>();
        for (IAtom atom : mol.atoms())
            idxs.put(atom, idxs.size());

        for (Map.Entry<Set<IAtom>,Collection<IAtom>> e : mapping.asMap().entrySet()) {
            List<IBond> bonds = new ArrayList<>();

            IAtomContainer shared = mol.getBuilder().newInstance(IAtomContainer.class);
            for (IAtom atom : e.getKey())
                shared.addAtom(atom);
            Point2d center = GeometryUtil.get2DCenter(shared);

            for (IBond bond : mol.bonds()) {
                if (e.getKey().contains(bond.getBegin()) &&
                    e.getKey().contains(bond.getEnd())) {
                    bonds.add(bond);
                }
            }

            Collections.sort(bonds, new Comparator<IBond>() {
                @Override
                public int compare(IBond a, IBond b) {
                    int atype = getPositionalRingBondPref(a, mol);
                    int btype = getPositionalRingBondPref(b, mol);
                    if (atype != btype)
                        return Integer.compare(atype, btype);
                    int aord  = a.getOrder().numeric();
                    int bord  = b.getOrder().numeric();
                    if (aord > 0 && bord > 0) {
                        return Integer.compare(aord, bord);
                    }
                    return 0;
                }
            });

            if (bonds.size() >= e.getValue().size()) {

                Iterator<IAtom> begIter = e.getValue().iterator();
                Iterator<IBond> bndIter = bonds.iterator();

                while (begIter.hasNext() && bndIter.hasNext()) {

                    final IBond bond = bndIter.next();
                    final IAtom atom = begIter.next();

                    final Point2d newBegP = new Point2d(bond.getBegin().getPoint2d());
                    final Point2d newEndP = new Point2d(bond.getEnd().getPoint2d());

                    final Vector2d bndVec  = new Vector2d(newEndP.x-newBegP.x, newEndP.y-newBegP.y);
                    final Vector2d bndXVec = new Vector2d(-bndVec.y, bndVec.x);

                    // ensure vector is pointing out of rings
                    Vector2d centerVec = new Vector2d(center.x - ((newBegP.x + newEndP.x) / 2),
                                                      center.y - ((newBegP.y + newEndP.y) / 2));

                    double dot = bndXVec.dot(centerVec);
                    if (Math.abs(dot) < 0.01) {
                        // close to zero... grab adjacent bonds and use those as
                        // well to choose the side we point the bond
                        Set<IAtom> adj = new HashSet<>();
                        adj.addAll(mol.getConnectedAtomsList(bond.getBegin()));
                        adj.addAll(mol.getConnectedAtomsList(bond.getEnd()));
                        adj.remove(bond.getBegin());
                        adj.remove(bond.getEnd());
                        Point2d newCenter = GeometryUtil.get2DCenter(adj);
                        centerVec = new Vector2d(newCenter.x - ((newBegP.x + newEndP.x) / 2),
                                                 newCenter.y - ((newBegP.y + newEndP.y) / 2));
                        if (bndXVec.dot(centerVec) > 0.01)
                            bndXVec.negate();
                    } else if (dot > 0) {
                        bndXVec.negate();
                    }

                    bndVec.normalize();
                    bndXVec.normalize();

                    bndVec.scale(0.5 * bondLength); // crossing point

                    double bndStep = (bondLength) / 5;

                    newBegP.add(bndVec);
                    bndXVec.normalize();
                    bndXVec.scale(2*bndStep);
                    newBegP.sub(bndXVec);
                    newEndP.sub(bndVec);
                    bndXVec.normalize();
                    bndXVec.scale(4*bndStep);
                    newEndP.add(bndXVec);

                    int atomIdx = idxs.get(atom);
                    if (adjlist[atomIdx].length != 1)
                        continue;

                    // get all atoms connected to the part we will move
                    Set<Integer> visited = new HashSet<>();
                    visit(visited, adjlist, atomIdx);

                    // gather up other position group
                    Set<Integer> newvisit = new HashSet<>();
                    do {
                        newvisit.clear();
                        for (Integer idx : visited) {
                            IAtom visitedAtom = mol.getAtom(idx);
                            if (e.getKey().contains(visitedAtom) || e.getValue().contains(visitedAtom))
                                continue;
                            for (Map.Entry<Set<IAtom>, IAtom> e2 : mapping.entries()) {
                                if (e2.getKey().contains(visitedAtom)) {
                                    int other = idxs.get(e2.getValue());
                                    if (!visited.contains(other) && newvisit.add(other)) {
                                        visit(newvisit, adjlist, other);
                                    }
                                } else if (e2.getValue().equals(visitedAtom)) {
                                    int other = idxs.get(e2.getKey().iterator().next());
                                    if (!visited.contains(other) && newvisit.add(other)) {
                                        visit(newvisit, adjlist, other);
                                    }
                                }
                            }
                        }
                        visited.addAll(newvisit);
                    } while (!newvisit.isEmpty());

                    IAtomContainer frag = mol.getBuilder().newInstance(IAtomContainer.class);
                    for (Integer visit : visited)
                        frag.addAtom(mol.getAtom(visit));

                    final IBond attachBond = bondMap.get(atomIdx, adjlist[atomIdx][0]);
                    final Point2d begP = atom.getPoint2d();
                    final Point2d endP = attachBond.getOther(atom).getPoint2d();

                    Vector2d orgVec = new Vector2d(endP.x-begP.x, endP.y-begP.y);
                    Vector2d newVec = new Vector2d(newEndP.x-newBegP.x, newEndP.y-newBegP.y);

                    // need perpendicular dot product to get signed angle
                    double pDot = orgVec.x * newVec.y - orgVec.y * newVec.x;
                    double theta = Math.atan2(pDot, newVec.dot(orgVec));

                    // position
                    GeometryUtil.translate2D(frag, newBegP.x - begP.x, newBegP.y - begP.y);
                    GeometryUtil.rotate(frag, new Point2d(atom.getPoint2d()), theta);

                    // stretch bond
                    frag.removeAtomOnly(atom);
                    GeometryUtil.translate2D(frag, newEndP.x - endP.x, newEndP.y - endP.y);
                }
            } else {
                System.err.println("Positional variation not yet handled");
            }
        }
    }

    private static void visit(Set<Integer> visited, int[][] g, int v) {
        visited.add(v);
        for (int w : g[v]) {
            if (!visited.contains(w))
                visit(visited, g, w);
        }
    }

    private static Multimap<Set<IAtom>, IAtom> aggregateMulticenterSgroups(List<Sgroup> sgroups) {
        Multimap<Set<IAtom>,IAtom> mapping = HashMultimap.create();
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() != SgroupType.ExtMulticenter)
                continue;

            IAtom      beg  = null;
            Set<IAtom> ends = new HashSet<>();

            Set<IBond> bonds = sgroup.getBonds();
            if (bonds.size() != 1)
                continue;
            IBond bond = bonds.iterator().next();

            for (IAtom atom : sgroup.getAtoms()) {
                if (bond.contains(atom))
                    beg = atom;
                else
                    ends.add(atom);
            }

            if (beg == null || ends.isEmpty())
                continue;

            mapping.put(ends, beg);
        } return mapping;
    }


    private static int numRingBonds(IAtomContainer mol, IAtom atom) {
        int cnt = 0;
        for (IBond bond : mol.getConnectedBondsList(atom)) {
            if (bond.isInRing())
                cnt++;
        }
        return cnt;
    }

    private void updateMinMax(double[] minmax, Point2d p) {
        minmax[0] = Math.min(p.x, minmax[0]);
        minmax[1] = Math.min(p.y, minmax[1]);
        minmax[2] = Math.max(p.x, minmax[2]);
        minmax[3] = Math.max(p.y, minmax[3]);
    }

    /**
     * Place and update brackets for polymer Sgroups.
     *
     * @param mol molecule
     */
    private void placeSgroupBrackets(IAtomContainer mol) {
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null) return;

        // index all crossing bonds
        final Multimap<IBond,Sgroup> bondMap = HashMultimap.create();
        final Multimap<Sgroup,Sgroup> childMap = HashMultimap.create();
        final Map<IBond,Integer> counter = new HashMap<>();
        for (Sgroup sgroup : sgroups) {
            if (!hasBrackets(sgroup))
                continue;
            for (IBond bond : sgroup.getBonds()) {
                bondMap.put(bond, sgroup);
                counter.put(bond, 0);
            }
            for (Sgroup parent : sgroup.getParents())
                childMap.put(parent, sgroup);
        }
        sgroups = new ArrayList<>(sgroups);
        // place child sgroups first, or those with less total children
        Collections.sort(sgroups,
                         new Comparator<Sgroup>() {
                             @Override
                             public int compare(Sgroup o1, Sgroup o2) {
                                 return Integer.compare(childMap.get(o1).size(), childMap.get(o2).size());
                             }
                         });

        for (Sgroup sgroup : sgroups) {
            if (!hasBrackets(sgroup))
                continue;

            final Set<IAtom> atoms  = sgroup.getAtoms();
            final Set<IBond> xbonds = sgroup.getBonds();

            // clear all the existing brackets
            sgroup.putValue(SgroupKey.CtabBracket, null);

            // assign brackets to crossing bonds
            if (xbonds.size() >= 2) {

                // check for vertical alignment
                boolean vert = true;
                for (IBond bond : xbonds) {
                    final double theta = angle(bond);
                    if (Math.abs(Math.toDegrees(theta)) > 40 && Math.abs(Math.toDegrees(theta)) < 140) {
                        vert = false;
                        break;
                    }
                }

                for (IBond bond : xbonds)
                    sgroup.addBracket(newCrossingBracket(bond,
                                                         bondMap,
                                                         counter,
                                                         vert));
            }
            // <= 1 crossing bonds so simply wrap the entire fragment
            else {
                IAtomContainer tmp = mol.getBuilder().newInstance(IAtomContainer.class);
                for (IAtom atom : atoms)
                    tmp.addAtom(atom);
                double[] minmax = GeometryUtil.getMinMax(tmp);

                // if a child Sgroup also has brackets, account for that in our
                // bounds calculation
                for (Sgroup child : childMap.get(sgroup)) {
                    List<SgroupBracket> brackets = child.getValue(SgroupKey.CtabBracket);
                    if (brackets != null) {
                        for (SgroupBracket bracket : brackets) {
                            updateMinMax(minmax, bracket.getFirstPoint());
                            updateMinMax(minmax, bracket.getSecondPoint());
                        }
                    }
                }

                double padding  = SGROUP_BRACKET_PADDING_FACTOR * bondLength;
                sgroup.addBracket(new SgroupBracket(minmax[0] - padding, minmax[1] - padding,
                                                    minmax[0] - padding, minmax[3] + padding));
                sgroup.addBracket(new SgroupBracket(minmax[2] + padding, minmax[1] - padding,
                                                    minmax[2] + padding, minmax[3] + padding));
            }
        }

    }

    private static double angle(IBond bond) {
        Point2d end = bond.getBegin().getPoint2d();
        Point2d beg = bond.getEnd().getPoint2d();
        return Math.atan2(end.y - beg.y, end.x - beg.x);
    }

    /**
     * Generate a new bracket across the provided bond.
     *
     * @param bond bond
     * @param bonds bond map to Sgroups
     * @param counter count how many brackets this group has already
     * @param vert vertical align bonds
     * @return the new bracket
     */
    private SgroupBracket newCrossingBracket(IBond bond, Multimap<IBond,Sgroup> bonds, Map<IBond,Integer> counter, boolean vert) {
        final IAtom beg = bond.getBegin();
        final IAtom end = bond.getEnd();
        final Point2d begXy = beg.getPoint2d();
        final Point2d endXy = end.getPoint2d();
        final Vector2d lenOffset = new Vector2d(endXy.x-begXy.x, endXy.y-begXy.y);
        final Vector2d bndCrossVec = new Vector2d(-lenOffset.y, lenOffset.x);
        lenOffset.normalize();
        bndCrossVec.normalize();
        bndCrossVec.scale(((0.9 * bondLength)) / 2);

        final List<Sgroup> sgroups = new ArrayList<>(bonds.get(bond));

        // bond in sgroup, place it in the middle of the bond
        if (sgroups.size() == 1) {
            lenOffset.scale(0.5 * bondLength);
        }
        // two sgroups, place one near start and one near end
        else if (sgroups.size() == 2) {
            boolean flip = !sgroups.get(counter.get(bond)).getAtoms().contains(beg);
            if (counter.get(bond) == 0) {
                lenOffset.scale(flip ? 0.75 : 0.25 * bondLength); // 75 or 25% along
                counter.put(bond, 1);
            } else {
                lenOffset.scale(flip ? 0.25 : 0.75 * bondLength); // 25 or 75% along
            }
        }
        else {
            double step = bondLength / (1 + sgroups.size());
            int idx = counter.get(bond) + 1;
            counter.put(bond, idx);
            lenOffset.scale((idx * step) * bondLength);
        }

        // vertical bracket
        if (vert) {
            return new SgroupBracket(begXy.x + lenOffset.x, begXy.y + lenOffset.y + bndCrossVec.length(),
                                     begXy.x + lenOffset.x, begXy.y + lenOffset.y - bndCrossVec.length());
        } else {
            return new SgroupBracket(begXy.x + lenOffset.x + bndCrossVec.x, begXy.y + lenOffset.y + bndCrossVec.y,
                                     begXy.x + lenOffset.x - bndCrossVec.x, begXy.y + lenOffset.y - bndCrossVec.y);
        }
    }

    /**
     * Determine whether and Sgroup type has brackets to be placed.
     * @param sgroup the Sgroup
     * @return brackets need to be placed
     */
    private static boolean hasBrackets(Sgroup sgroup) {
        switch (sgroup.getType()) {
            case CtabStructureRepeatUnit:
            case CtabAnyPolymer:
            case CtabCrossLink:
            case CtabComponent:
            case CtabMixture:
            case CtabFormulation:
            case CtabGraft:
            case CtabModified:
            case CtabMonomer:
            case CtabCopolymer:
            case CtabMultipleGroup:
                return true;
            case CtabGeneric:
                List<SgroupBracket> brackets = sgroup.getValue(SgroupKey.CtabBracket);
                return brackets != null && !brackets.isEmpty();
            default:
                return false;
        }
    }

    private static final class IntTuple {
        private final int beg, end;

        public IntTuple(int beg, int end) {
            this.beg = beg;
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IntTuple that = (IntTuple) o;

            return (this.beg == that.beg && this.end == that.end) ||
                   (this.beg == that.end && this.end == that.beg);
        }

        @Override
        public int hashCode() {
            return beg ^ end;
        }
    }
}
