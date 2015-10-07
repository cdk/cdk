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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import com.google.common.collect.FluentIterable;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryUtil;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.AtomContainerSetManipulator;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;

/**
 * Generates 2D coordinates for a molecule for which only connectivity is known
 * or the coordinates have been discarded for some reason. Usage: Create an
 * instance of this class, thereby assigning a molecule, call
 * generateCoordinates() and get your molecule back:
 * <pre>
 * StructureDiagramGenerator sdg = new StructureDiagramGenerator();
 * sdg.setMolecule(someMolecule);
 * sdg.generateCoordinates();
 * Molecule layedOutMol = sdg.getMolecule();
 * </pre>
 *
 * <p>The method will fail if the molecule is disconnected. The
 * partitionIntoMolecules(AtomContainer) can help here.
 *
 * @author      steinbeck
 * @cdk.created 2004-02-02
 * @see         org.openscience.cdk.graph.ConnectivityChecker#partitionIntoMolecules(IAtomContainer)
 * @cdk.keyword Layout
 * @cdk.keyword Structure Diagram Generation (SDG)
 * @cdk.keyword 2D-coordinates
 * @cdk.keyword Coordinate generation, 2D
 * @cdk.dictref blue-obelisk:layoutMolecule
 * @cdk.module  sdg
 * @cdk.githash
 * @cdk.bug     1536561
 * @cdk.bug     1788686
 */
public class StructureDiagramGenerator {

    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(StructureDiagramGenerator.class);
    public static final double DEFAULT_BOND_LENGTH = 1.5;
    
    private IAtomContainer          molecule;
    private IRingSet                sssr;
    private double                  bondLength               = DEFAULT_BOND_LENGTH;
    private Vector2d                firstBondVector;
    private RingPlacer              ringPlacer               = new RingPlacer();
    private AtomPlacer              atomPlacer               = new AtomPlacer();
    private List<IRingSet>          ringSystems              = null;
    private final String            disconnectedMessage      = "Molecule not connected. Use ConnectivityChecker.partitionIntoMolecules() and do the layout for every single component.";
    private TemplateHandler         templateHandler          = null;
    private boolean                 useTemplates             = true;
    private boolean                 useIdentTemplates        = true;
    
    /** Atoms of the molecule that mapped a template */
    private IAtomContainerSet       mappedSubstructures;

    /** Identity templates - for laying out primary ring system. */
    private IdentityTemplateLibrary identityLibrary;

    public  static Vector2d                DEFAULT_BOND_VECTOR      = new Vector2d(0, 1);
    private static TemplateHandler         DEFAULT_TEMPLATE_HANDLER = null;
    private static IdentityTemplateLibrary DEFAULT_IDENTITY_LIBRARY = IdentityTemplateLibrary.loadFromResource("chebi-ring-templates.smi");

    /**
     *  The empty constructor.
     */
    public StructureDiagramGenerator() {
        this(DEFAULT_IDENTITY_LIBRARY);
    }

    private StructureDiagramGenerator(IdentityTemplateLibrary identityLibrary) {
        this.identityLibrary = identityLibrary;
    }

    /**
     *  Creates an instance of this class while assigning a molecule to be layed
     *  out.
     *
     *  @param  molecule  The molecule to be layed out.
     */
    public StructureDiagramGenerator(IAtomContainer molecule) {
        this();
        setMolecule(molecule, false);
        templateHandler = new TemplateHandler(molecule.getBuilder());
    }

    /**
     *  Assings a molecule to be layed out. Call generateCoordinates() to do the
     *  actual layout.
     *
     *  @param  mol    the molecule for which coordinates are to be generated.
     *  @param  clone  Should the whole process be performed with a cloned copy?
     */
    public void setMolecule(IAtomContainer mol, boolean clone) {
        if (useTemplates && templateHandler == null)
            templateHandler = new TemplateHandler(mol.getBuilder());
        IAtom atom = null;
        if (clone) {
            try {
                this.molecule = (IAtomContainer) mol.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("Should clone, but exception occured: ", e.getMessage());
                logger.debug(e);
            }
        }
        else {
            this.molecule = mol;
        }
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            atom = molecule.getAtom(f);
            atom.setPoint2d(null);
            atom.setFlag(CDKConstants.ISPLACED, false);
            atom.setFlag(CDKConstants.VISITED, false);
            atom.setFlag(CDKConstants.ISINRING, false);
            atom.setFlag(CDKConstants.ISALIPHATIC, false);
        }
        atomPlacer.setMolecule(this.molecule);
        ringPlacer.setMolecule(this.molecule);
        ringPlacer.setAtomPlacer(this.atomPlacer);
    }

    /**
     *  Sets whether to use templates or not. Some complicated ring systems
     *  like adamantane are only nicely layouted when using templates. This
     *  option is by default set true.
     *
     *@param  useTemplates  set true to use templates, false otherwise
     */
    public void setUseTemplates(boolean useTemplates) {
        this.useTemplates = useTemplates;
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
     *  Returns whether the use of templates is enabled or disabled.
     *
     *  @return true, when the use of templates is enables, false otherwise
     */
    public boolean getUseTemplates() {
        return useTemplates;
    }

    /**
     *  Sets the templateHandler attribute of the StructureDiagramGenerator object
     *
     *  @param  templateHandler  The new templateHandler value
     */
    public void setTemplateHandler(TemplateHandler templateHandler) {
        this.templateHandler = templateHandler;
    }

    /**
     *  Gets the templateHandler attribute of the StructureDiagramGenerator object
     *
     *  @return The templateHandler value
     */
    public TemplateHandler getTemplateHandler() {
        if (templateHandler == null) {
            return DEFAULT_TEMPLATE_HANDLER;
        } else {
            return templateHandler;
        }
    }

    /**
     *  Assings a molecule to be layed out. Call generateCoordinates() to do the
     *  actual layout.
     *
     *  @param  molecule  the molecule for which coordinates are to be generated.
     */
    public void setMolecule(IAtomContainer molecule) {
        setMolecule(molecule, true);
    }

    /**
     *  Returns the molecule, usually used after a call of generateCoordinates()
     *
     *  @return    The molecule with new coordinates (if generateCoordinates() had
     *             been called)
     */
    public IAtomContainer getMolecule() {
        return molecule;
    }

    /**
     *  This method uses generateCoordinates, but it removes the hydrogens first,
     *  lays out the structuren and then adds them again.
     *
     *  @throws  CDKException  if an error occurs
     *  @see     #generateCoordinates
     */
    public void generateExperimentalCoordinates() throws CDKException {
        generateExperimentalCoordinates(DEFAULT_BOND_VECTOR);
    }

    /**
     * Generates 2D coordinates on the non-hydrogen skeleton, after which
     * coordinates for the hydrogens are calculated.
     *
     * @param firstBondVector the vector of the first bond to lay out
     * @throws CDKException if an error occurs
     */
    public void generateExperimentalCoordinates(Vector2d firstBondVector) throws CDKException {
        // first make a shallow copy: Atom/Bond references are kept
        IAtomContainer original = molecule;
        IAtomContainer shallowCopy = molecule.getBuilder().newInstance(IAtomContainer.class, molecule);
        // delete single-bonded H's from
        //IAtom[] atoms = shallowCopy.getAtoms();
        for (IAtom curAtom : shallowCopy.atoms()) {
            if (curAtom.getSymbol().equals("H")) {
                if (shallowCopy.getConnectedBondsCount(curAtom) < 2) {
                    shallowCopy.removeAtomAndConnectedElectronContainers(curAtom);
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
     *  The main method of this StructurDiagramGenerator. Assign a molecule to the
     *  StructurDiagramGenerator, call the generateCoordinates() method and get
     *  your molecule back.
     *
     *  @param  firstBondVector          The vector of the first bond to lay out
     *  @throws CDKException             if an error occurs
     */
    public void generateCoordinates(Vector2d firstBondVector) throws CDKException {
        generateCoordinates(firstBondVector, false);
    }

    /**
     *  The main method of this StructureDiagramGenerator. Assign a molecule to the
     *  StructureDiagramGenerator, call the generateCoordinates() method and get
     *  your molecule back.
     *
     *  @param  firstBondVector the vector of the first bond to lay out
     *  @param  isConnected the 'molecule' attribute is guaranteed to be connected (we have checked)
     *  @throws CDKException problem occurred during layout
     */
    private void generateCoordinates(Vector2d firstBondVector, boolean isConnected) throws CDKException {

        int safetyCounter = 0;
        /*
         * if molecule contains only one Atom, don't fail, simply set
         * coordinates to simplest: 0,0. See bug #780545
         */
        logger.debug("Entry point of generateCoordinates()");
        logger.debug("We have a molecules with " + molecule.getAtomCount() + " atoms.");
        if (molecule.getAtomCount() == 1) {
            molecule.getAtom(0).setPoint2d(new Point2d(0, 0));
            return;
        } else if (molecule.getBondCount() == 1) {
            molecule.getAtom(0).setPoint2d(new Point2d(0, 0));
            molecule.getAtom(1).setPoint2d(new Point2d(bondLength, 0));
            return;
        }

        // intercept fragment molecules and lay them out in a grid
        if (!isConnected) {
            final IAtomContainerSet frags = ConnectivityChecker.partitionIntoMolecules(molecule);
            if (frags.getAtomContainerCount() > 1) {
                generateFragmentCoordinates(molecule, toList(frags));
                // don't call set molecule as it wipes x,y coordinates!
                // this looks like a self assignment but actually the fragment
                // method changes this.molecule
                this.molecule = molecule;
                return;
            }
        }

        /*
         * compute the minimum number of rings as given by Frerejacque, Bull.
         * Soc. Chim. Fr., 5, 1008 (1939)
         */
        int nrOfEdges = molecule.getBondCount();
        //Vector2d ringSystemVector = null;
        //Vector2d newRingSystemVector = null;
        this.firstBondVector = firstBondVector;
        double angle;

        /*
         * First we check if we can map any templates with predefined
         * coordinates Those are stored as CML in
         * <i>org/openscience/cdk/layout/templates</i>.
         */
        if (useTemplates && !System.getProperty("java.version").contains("1.3.")) {
            logger.debug("Initializing TemplateHandler");
            logger.debug("TemplateHander initialized");
            logger.debug("Now starting Template Detection in Molecule...");
            mappedSubstructures = getTemplateHandler().getMappedSubstructures(molecule);
            logger.debug("Template Detection finished");
            logger.debug("Number of found templates: " + mappedSubstructures.getAtomContainerCount());
        }

        int expectedRingCount = nrOfEdges - molecule.getAtomCount() + 1;
        if (expectedRingCount > 0) {
            logger.debug("*** Start of handling rings. ***");
            /*
             * Get the smallest set of smallest rings on this molecule
             */

            sssr = Cycles.sssr(molecule).toRingSet();
            if (sssr.getAtomContainerCount() < 1) {
                return;
            }

            /*
             * Order the rings because SSSRFinder.findSSSR() returns rings in an
             * undeterministic order.
             */
            AtomContainerSetManipulator.sort(sssr);

            /*
             * Mark all the atoms from the ring system as "ISINRING"
             */
            markRingAtoms(sssr);
            /*
             * Give a handle of our molecule to the ringPlacer
             */
            ringPlacer.setMolecule(molecule);
            ringPlacer.checkAndMarkPlaced(sssr);
            /*
             * Partition the smallest set of smallest rings into disconnected
             * ring system. The RingPartioner returns a Vector containing
             * RingSets. Each of the RingSets contains rings that are connected
             * to each other either as bridged ringsystems, fused rings or via
             * spiro connections.
             */
            ringSystems = RingPartitioner.partitionRings(sssr);

            /*
             * We got our ring systems now
             */

            /*
             * Do the layout for the first connected ring system ...
             */
            int largest = 0;
            int largestSize = ((IRingSet) ringSystems.get(0)).getAtomContainerCount();
            logger.debug("We have " + ringSystems.size() + " ring system(s).");
            for (int f = 0; f < ringSystems.size(); f++) {
                logger.debug("RingSet " + f + " has size " + ((IRingSet) ringSystems.get(f)).getAtomContainerCount());
                if (((IRingSet) ringSystems.get(f)).getAtomContainerCount() > largestSize) {
                    largestSize = ((IRingSet) ringSystems.get(f)).getAtomContainerCount();
                    largest = f;
                }
            }
            logger.debug("Largest RingSystem is at RingSet collection's position " + largest);
            logger.debug("Size of Largest RingSystem: " + largestSize);

            IAtomContainer ringSystem = molecule.getBuilder().newInstance(IAtomContainer.class);
            for (IAtomContainer container : ringSystems.get(largest).atomContainers())
                ringSystem.add(container);

            // This is the primary ring system of the molecule, we lookup an identity template
            // that helps us orientate in de factor conformation.
            if (lookupRingSystem(ringSystem, molecule)) {
                for (IAtomContainer container : ringSystems.get(largest).atomContainers())
                    container.setFlag(CDKConstants.ISPLACED, true);
                ringSystems.get(largest).setFlag(CDKConstants.ISPLACED, true);
            } else {
                layoutRingSet(firstBondVector, (IRingSet) ringSystems.get(largest));
            }
            logger.debug("First RingSet placed");
            /*
             * and do the placement of all the directly connected atoms of this
             * ringsystem
             */
            ringPlacer.placeRingSubstituents((IRingSet) ringSystems.get(largest), bondLength);

        } else {

            logger.debug("*** Start of handling purely aliphatic molecules. ***");
            /*
             * We are here because there are no rings in the molecule so we get
             * the longest chain in the molecule and placed in on a horizontal
             * axis
             */
            logger.debug("Searching initialLongestChain for this purely aliphatic molecule");
            IAtomContainer longestChain = atomPlacer.getInitialLongestChain(molecule);
            logger.debug("Found linear chain of length " + longestChain.getAtomCount());
            logger.debug("Setting coordinated of first atom to 0,0");
            longestChain.getAtom(0).setPoint2d(new Point2d(0, 0));
            longestChain.getAtom(0).setFlag(CDKConstants.ISPLACED, true);

            /*
             * place the first bond such that the whole chain will be
             * horizontally alligned on the x axis
             */
            angle = Math.toRadians(-30);
            logger.debug("Attempting to place the first bond such that the whole chain will be horizontally alligned on the x axis");
            if (firstBondVector != null && firstBondVector != DEFAULT_BOND_VECTOR)
                atomPlacer.placeLinearChain(longestChain, firstBondVector, bondLength);
            else
                atomPlacer.placeLinearChain(longestChain, new Vector2d(Math.cos(angle), Math.sin(angle)), bondLength);
            logger.debug("Placed longest aliphatic chain");
        }

        /*
         * Now, do the layout of the rest of the molecule
         */
        do {
            safetyCounter++;
            logger.debug("*** Start of handling the rest of the molecule. ***");
            /*
             * do layout for all aliphatic parts of the molecule which are
             * connected to the parts which have already been laid out.
             */
            handleAliphatics();
            /*
             * do layout for the next ring aliphatic parts of the molecule which
             * are connected to the parts which have already been laid out.
             */
            layoutNextRingSystem();
        } while (!atomPlacer.allPlaced(molecule) && safetyCounter <= molecule.getAtomCount());

        fixRest();

        // correct double-bond stereo, this changes the layout and in reality
        // should be done during the initial placement
        CorrectGeometricConfiguration.correct(molecule);

        // assign up/down labels, this doesn't not alter layout and could be
        // done on-demand (e.g. when writing a MDL Molfile)
        NonplanarBonds.assign(molecule);

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

        // generate the sub-layouts
        for (IAtomContainer fragment : frags) {
            setMolecule(fragment, false);
            generateCoordinates(DEFAULT_BOND_VECTOR, true);
            limits.add(GeometryUtil.getMinMax(fragment));
        }

        final int nRow = (int) Math.floor(Math.sqrt(numFragments));
        final int nCol = (int) Math.ceil(numFragments / (double) nRow);

        final double[] xOffsets = new double[nCol+1];
        final double[] yOffsets = new double[nRow+1];

        // calc the max widths/height of each row, we also add some
        // spacing
        double spacing = 1.5 * bondLength;
        for (int i = 0; i < numFragments; i++) {
            // +1 because first offset is always 0
            int col = 1 + i % nCol;
            int row = 1 + i / nCol;

            double[] minmax = limits.get(i);
            final double width  = spacing + (minmax[2] - minmax[0]);
            final double height = spacing + (minmax[3] - minmax[1]);

            if (width > xOffsets[col])
                xOffsets[col] = width;
            if (height > yOffsets[row])
                yOffsets[row] = height;
        }

        // cumulative counts
        for (int i = 1; i < xOffsets.length; i++)
            xOffsets[i] += xOffsets[i-1];
        for (int i = 1; i < yOffsets.length; i++)
            yOffsets[i] += yOffsets[i-1];

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
                    if (Integer.signum(nullAsZero(bond.getConnectedAtom(atom).getFormalCharge())) + sign == 0)
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
     * <p/>
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
        final Set<IAtomContainer> remove = new HashSet<>();

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
        Map<IAtom, IAtomContainer> atmMap = new HashMap<>();

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
        final List<IBond> ionicBonds = new ArrayList<>();
        for (int i = 0; i < cations.size(); i++) {
            final IAtom beg = cations.get(i);
            final IAtom end = anions.get(i);
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
     * The method indicates whether a match was found.
     *
     * @param ringSystem the ring system (may be fused, bridged, etc.)
     * @param molecule the rest of the compound
     * @return coordinates were assigned
     */
    private boolean lookupRingSystem(IAtomContainer ringSystem, IAtomContainer molecule) {

        // identity templates are disabled
        if (!useIdentTemplates) return false;

        final IChemObjectBuilder bldr = molecule.getBuilder();

        final Set<IAtom> ringAtoms = new HashSet<IAtom>();
        for (IAtom atom : ringSystem.atoms())
            ringAtoms.add(atom);

        // a temporary molecule of the ring system and 'stubs' of the attached substituents
        final IAtomContainer ringWithStubs = bldr.newInstance(IAtomContainer.class);
        ringWithStubs.add(ringSystem);
        for (IBond bond : molecule.bonds()) {
            IAtom atom1 = bond.getAtom(0);
            IAtom atom2 = bond.getAtom(1);
            if (isHydrogen(atom1) || isHydrogen(atom2)) continue;
            if (ringAtoms.contains(atom1) ^ ringAtoms.contains(atom2)) {
                ringWithStubs.addBond(bond);
                ringWithStubs.addAtom(atom1);
                ringWithStubs.addAtom(atom2);
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
     *  Does a layout of all the rings in a given connected RingSet. Uses a TemplateHandler
     *  to treat templated mapped substructures differently if <code>useTemplates</code> is
     *  set true.
     *
     * @param firstBondVector A vector giving the placement for the first bond
     * @param rs The connected RingSet for which the layout is to be
     *           done
     * @throws CDKException if an error occurs
     */
    private void layoutRingSet(Vector2d firstBondVector, IRingSet rs) throws CDKException {
        IAtomContainer sharedAtoms;
        Vector2d ringCenterVector;
        int thisRing;
        logger.debug("Start of layoutRingSet");

        /*
         * First we check if we can map any templates with predifined
         * coordinates. All mapped substructures are saved in:
         * this.mappedSubstructures
         */
        if (useTemplates &&
            mappedSubstructures.getAtomContainerCount() > 0 &&
            !System.getProperty("java.version").contains("1.3.")) {

            for (IAtomContainer substructure : mappedSubstructures.atomContainers()) {
                boolean substructureMapped = false;

                for (IAtomContainer ring : rs.atomContainers()) {
                    for (IAtom atom : ring.atoms()) {
                        if (substructure.contains(atom)) {
                            substructureMapped = true;
                            break;
                        }
                    }
                    if (substructureMapped)
                        break;
                }

                // FIXME: we've already determine the atom-atom mapping to template but is redone here
                if (substructureMapped) {
                    if (getTemplateHandler().mapTemplateExact(substructure)) {
                        // Mark rings of substrucure as CDKConstants.ISPLACED
                        ringPlacer.checkAndMarkPlaced(rs);
                    } else {
                        logger.warn("A supposedly matched substructure failed to match.");
                    }
                }
            }
        }

        /*
         * Now layout the rest of this ring system
         */

        /*
         * Get the most complex ring in this RingSet
         */
        IRing ring = RingSetManipulator.getMostComplexRing(rs);
        int i = 0;

        /*
         * Place the most complex ring at the origin of the coordinate system
         */
        if (!ring.getFlag(CDKConstants.ISPLACED)) {
            sharedAtoms = placeFirstBond(ring.getBond(i), firstBondVector);
            /*
             * Call the method which lays out the new ring.
             */
            ringCenterVector = ringPlacer.getRingCenterOfFirstRing(ring, firstBondVector, bondLength);
            ringPlacer
                    .placeRing(ring, sharedAtoms, GeometryUtil.get2DCenter(sharedAtoms), ringCenterVector, bondLength);
            /*
             * Mark the ring as placed
             */
            ring.setFlag(CDKConstants.ISPLACED, true);
        }
        /*
         * Place all other rings in this ringsystem.
         */
        thisRing = 0;
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
        logger.debug("End of layoutRingSet");
    }

    /**
     * Does a layout of all aliphatic parts connected to the parts of the molecule
     * that have already been laid out. Starts at the first bond with unplaced
     * neighbours and stops when a ring is encountered.
     *
     * @throws CDKException if an error occurs
     */
    private void handleAliphatics() throws CDKException {
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
                    logger.debug("Start at atom no. " + (molecule.getAtomNumber(atom) + 1));
                    logger.debug(AtomPlacer.listNumbers(molecule, longestUnplacedChain));
                } catch (Exception exc) {
                    logger.debug(exc);
                }
                logger.debug("---end of longest unplaced chain---");

                if (longestUnplacedChain.getAtomCount() > 1) {

                    if (placedAtoms.getAtomCount() > 1) {
                        logger.debug("More than one atoms placed already");
                        logger.debug("trying to place neighbors of atom " + (molecule.getAtomNumber(atom) + 1));
                        atomPlacer.distributePartners(atom, placedAtoms, GeometryUtil.get2DCenter(placedAtoms),
                                                      unplacedAtoms, bondLength);
                        direction = new Vector2d(longestUnplacedChain.getAtom(1).getPoint2d());
                        startVector = new Vector2d(atom.getPoint2d());
                        direction.sub(startVector);
                        logger.debug("Done placing neighbors of atom " + (molecule.getAtomNumber(atom) + 1));
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
     *  Does the layout for the next RingSystem that is connected to those parts of
     *  the molecule that have already been laid out. Finds the next ring with an
     *  unplaced ring atom and lays out this ring. Then lays out the ring substituents
     *  of this ring. Then moves and rotates the laid out ring to match the position
     *  of its attachment bond to the rest of the molecule.
     *
     *  @throws CDKException if an error occurs
     */
    private void layoutNextRingSystem() throws CDKException {
        logger.debug("Start of layoutNextRingSystem()");

        resetUnplacedRings();
        IAtomContainer tempAc = atomPlacer.getPlacedAtoms(molecule);
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

            /*
             * Get ring system which ringAttachmentAtom is part of
             */
            IRingSet nextRingSystem = getRingSystemOfAtom(ringSystems, ringAttachmentAtom);

            /*
             * Get all rings of nextRingSytem as one IAtomContainer
             */
            IAtomContainer ringSystem = tempAc.getBuilder().newInstance(IAtomContainer.class);
            for (Iterator containers = RingSetManipulator.getAllAtomContainers(nextRingSystem).iterator(); containers
                    .hasNext(); )
                ringSystem.add((IAtomContainer) containers.next());

            /*
             * Save coordinates of ringAttachmentAtom and chainAttachmentAtom
             */
            Point2d oldRingAttachmentAtomPoint = ringAttachmentAtom.getPoint2d();
            Point2d oldChainAttachmentAtomPoint = chainAttachmentAtom.getPoint2d();

            /*
             * Do the layout of the next ring system
             */
            layoutRingSet(firstBondVector, nextRingSystem);

            /*
             * Place all the substituents of next ring system
             */
            atomPlacer.markNotPlaced(tempAc);
            IAtomContainer placedRingSubstituents = ringPlacer.placeRingSubstituents(nextRingSystem, bondLength);
            ringSystem.add(placedRingSubstituents);
            atomPlacer.markPlaced(tempAc);

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
        } else
            logger.debug("...no bond found");

        logger.debug("End of layoutNextRingSystem()");
    }

    /**
     *  Returns an AtomContainer with all unplaced atoms connected to a given
     *  atom
     *
     *  @param  atom  The Atom whose unplaced bonding partners are to be returned
     *  @return       an AtomContainer with all unplaced atoms connected to a
     *                given atom
     */
    private IAtomContainer getUnplacedAtoms(IAtom atom) {
        IAtomContainer unplacedAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
        List bonds = molecule.getConnectedBondsList(atom);
        IAtom connectedAtom;
        for (int f = 0; f < bonds.size(); f++) {
            connectedAtom = ((IBond) bonds.get(f)).getConnectedAtom(atom);
            if (!connectedAtom.getFlag(CDKConstants.ISPLACED)) {
                unplacedAtoms.addAtom(connectedAtom);
            }
        }
        return unplacedAtoms;
    }

    /**
     *  Returns an AtomContainer with all placed atoms connected to a given
     *  atom
     *
     *  @param  atom  The Atom whose placed bonding partners are to be returned
     *  @return       an AtomContainer with all placed atoms connected to a given
     *                atom
     */
    private IAtomContainer getPlacedAtoms(IAtom atom) {
        IAtomContainer placedAtoms = atom.getBuilder().newInstance(IAtomContainer.class);
        List bonds = molecule.getConnectedBondsList(atom);
        IAtom connectedAtom;
        for (int f = 0; f < bonds.size(); f++) {
            connectedAtom = ((IBond) bonds.get(f)).getConnectedAtom(atom);
            if (connectedAtom.getFlag(CDKConstants.ISPLACED)) {
                placedAtoms.addAtom(connectedAtom);
            }
        }
        return placedAtoms;
    }

    /**
     *  Returns the next atom with unplaced aliphatic neighbors
     *
     *  @return    the next atom with unplaced aliphatic neighbors
     */
    private IAtom getNextAtomWithAliphaticUnplacedNeigbors() {
        IBond bond;
        for (int f = 0; f < molecule.getBondCount(); f++) {
            bond = molecule.getBond(f);

            if (bond.getAtom(1).getFlag(CDKConstants.ISPLACED) && !bond.getAtom(0).getFlag(CDKConstants.ISPLACED)) {
                return bond.getAtom(1);
            }

            if (bond.getAtom(0).getFlag(CDKConstants.ISPLACED) && !bond.getAtom(1).getFlag(CDKConstants.ISPLACED)) {
                return bond.getAtom(0);
            }
        }
        return null;
    }

    /**
     *  Returns the next bond with an unplaced ring atom
     *
     *  @return    the next bond with an unplaced ring atom
     */
    private IBond getNextBondWithUnplacedRingAtom() {
        Iterator bonds = molecule.bonds().iterator();
        while (bonds.hasNext()) {
            IBond bond = (IBond) bonds.next();

            if (bond.getAtom(0).getPoint2d() != null && bond.getAtom(1).getPoint2d() != null) {
                if (bond.getAtom(1).getFlag(CDKConstants.ISPLACED) && !bond.getAtom(0).getFlag(CDKConstants.ISPLACED)
                        && bond.getAtom(0).getFlag(CDKConstants.ISINRING)) {
                    return bond;
                }

                if (bond.getAtom(0).getFlag(CDKConstants.ISPLACED) && !bond.getAtom(1).getFlag(CDKConstants.ISPLACED)
                        && bond.getAtom(1).getFlag(CDKConstants.ISINRING)) {
                    return bond;
                }
            }
        }
        return null;
    }

    /**
     *  Places the first bond of the first ring such that one atom is at (0,0) and
     *  the other one at the position given by bondVector
     *
     *  @param  bondVector  A 2D vector to point to the position of the second bond
     *                      atom
     *  @param  bond        the bond to lay out
     *  @return             an IAtomContainer with the atoms of the bond and the bond itself
     */
    private IAtomContainer placeFirstBond(IBond bond, Vector2d bondVector) {
        IAtomContainer sharedAtoms = null;
        try {
            bondVector.normalize();
            logger.debug("placeFirstBondOfFirstRing->bondVector.length():" + bondVector.length());
            bondVector.scale(bondLength);
            logger.debug("placeFirstBondOfFirstRing->bondVector.length() after scaling:" + bondVector.length());
            IAtom atom;
            Point2d point = new Point2d(0, 0);
            atom = bond.getAtom(0);
            logger.debug("Atom 1 of first Bond: " + (molecule.getAtomNumber(atom) + 1));
            atom.setPoint2d(point);
            atom.setFlag(CDKConstants.ISPLACED, true);
            point = new Point2d(0, 0);
            atom = bond.getAtom(1);
            logger.debug("Atom 2 of first Bond: " + (molecule.getAtomNumber(atom) + 1));
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
            sharedAtoms.addBond(bond);
            sharedAtoms.addAtom(bond.getAtom(0));
            sharedAtoms.addAtom(bond.getAtom(1));
        } catch (Exception exc) {
            logger.debug(exc);
        }
        return sharedAtoms;
    }

    /**
     *  This method will go as soon as the rest works. It just assignes Point2d's
     *  of position (0,0) so that the molecule can be drawn.
     */
    private void fixRest() {
        IAtom atom = null;
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            atom = molecule.getAtom(f);
            if (atom.getPoint2d() == null) {
                atom.setPoint2d(new Point2d(0, 0));
            }
        }
    }

    /**
     *  This method will go as soon as the rest works. It just assignes Point2d's
     *  of position (0,0) so that the molecule can be drawn.
     *  @param molecule the molecule to fix
     *  @return the fixed molecule
     */
    private IAtomContainer fixMol(IAtomContainer molecule) {
        IAtom atom = null;
        for (int f = 0; f < molecule.getAtomCount(); f++) {
            atom = molecule.getAtom(f);
            if (atom.getPoint2d() == null) {
                atom.setPoint2d(new Point2d(0, 0));
            }
        }
        return molecule;
    }

    /**
     *  Initializes all rings in RingSet rs as not placed
     *
     *  @param  rs  The RingSet to be initialized
     */
    //	private void markNotPlaced(IRingSet rs)
    //	{
    //		for (int f = 0; f < rs.size(); f++)
    //		{
    //			((IRing) rs.get(f)).setFlag(CDKConstants.ISPLACED, false);
    //		}
    //	}

    /**
     *  Are all rings in the Vector placed?
     *
     *  @param  rings  The Vector to be checked
     *  @return        true if all rings are placed, false otherwise
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
     *  Mark all atoms in the molecule as being part of a ring
     *
     *  @param  rings  an IRingSet with the rings to process
     */
    private void markRingAtoms(IRingSet rings) {
        IRing ring = null;
        for (int i = 0; i < rings.getAtomContainerCount(); i++) {
            ring = (IRing) rings.getAtomContainer(i);
            for (int j = 0; j < ring.getAtomCount(); j++) {
                ring.getAtom(j).setFlag(CDKConstants.ISINRING, true);
            }
        }
    }

    /**
     *  Get the unplaced ring atom in this bond
     *
     *  @param  bond  the bond to be search for the unplaced ring atom
     *  @return       the unplaced ring atom in this bond
     */
    private IAtom getRingAtom(IBond bond) {
        if (bond.getAtom(0).getFlag(CDKConstants.ISINRING) && !bond.getAtom(0).getFlag(CDKConstants.ISPLACED)) {
            return bond.getAtom(0);
        }
        if (bond.getAtom(1).getFlag(CDKConstants.ISINRING) && !bond.getAtom(1).getFlag(CDKConstants.ISPLACED)) {
            return bond.getAtom(1);
        }
        return null;
    }

    /**
     *  Get the ring system of which the given atom is part of
     *
     *  @param  ringSystems  a List of ring systems to be searched
     *  @param  ringAtom     the ring atom to be search in the ring system.
     *  @return              the ring system the given atom is part of
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
     *  Set all the atoms in unplaced rings to be unplaced
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
     *  Set the bond length used for laying out the molecule.
     *  The default value is 1.5.
     *
     *  @param  bondLength  The new bondLength value
     */
    public void setBondLength(double bondLength) {
        this.bondLength = bondLength;
    }

    /**
     *  Returns the bond length used for laying out the molecule.
     *
     *  @return The current bond length
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
        if (bond.getAtom(0).equals(atom))
            return bond.getAtom(1);
        else
            return bond.getAtom(0);
    }

}
