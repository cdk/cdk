/* Copyright (C) 1997-2007  Christoph Steinbeck <steinbeck@users.sf.net>
 *                    2008  Egon Willighagen <egonw@users.sf.net>
 *                    2014  Mark B Vine (orcid:0000-0002-7794-0426)
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.tools;

import static java.util.Map.entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.config.Isotopes;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.invariant.CanonicalLabeler;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IIsotope;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.smiles.InvPair;

/**
 * Generates HOSE codes {@cdk.cite BRE78}.
 * IMPORTANT: Your molecule must contain implicit or explicit hydrogens
 * for this method to work properly.
 *
 * @author     steinbeck
 * @cdk.githash
 * @cdk.keyword    HOSE code, spherical atom search
 * @cdk.created    2002-05-10
 * @cdk.module     standard
 */
public class HOSECodeGenerator implements java.io.Serializable {

    private static final ILoggingTool logger               = LoggingToolFactory.createLoggingTool(HOSECodeGenerator.class);

    private static final long   serialVersionUID     = -4353471818831864513L;

    /**
     *  Container for the nodes in a sphere.
     */
    protected List<TreeNode>    sphereNodes;
    private final List<IAtom>       sphereNodesWithAtoms;

    /**
     *  Container for the node in the next sphere Assembled in a recursive method
     *  and then passed to the next recursion to become "sphereNodes".
     */
    private List<TreeNode>    nextSphereNodes;
    /**
     *  Counter for the sphere in which we currently work.
     */
    protected int               sphere               = 0;
    /**
     *  How many spheres are we supposed inspect.
     */
    private int               maxSphere            = 0;

    /**
     *  Here we store the spheres that we assemble, in order to parse them into a
     *  code later.
     */
    protected List<TreeNode>[]  spheres              = null;
    private List<IAtom>[]     spheresWithAtoms     = null;

    /**
     *  The HOSECode string that we assemble
     */
    protected StringBuffer      HOSECode;

    /**
     *  The molecular structure on which we work
     */
    protected IAtomContainer    atomContainer;

    /**
     *  Delimiters used to separate spheres in the output string. Bremser uses the
     *  sequence"(//)" for the first four spheres.
     */
    private final String[]          sphereDelimiters     = {"(", "/", "/", ")", "/", "/", "/", "/", "/", "/", "/", "/"};
    /**
     *  The bond symbols used for bond orders "single", "double", "triple" and
     *  "aromatic"
     */
    protected String[] bondSymbols = {"", "", "=", "%", "*"};

    protected String            centerCode           = null;

    boolean                     debug                = false;

    private final IAtomContainer      acold                = null;
    private IRingSet            soar                 = null;

    /**
     *  The ranks for the given element and other symbols.
     */
    private static final long RING_RANK = 1100l;
    private Map<String, Long> rankedSymbols = Map.ofEntries(
    	entry("C",9000l),entry("O",8900l),entry("N",8800l),entry("S",8700l),entry("P",8600l),entry("Si",8500l),entry("B",8400l),entry("F",8300l),
    	entry("Cl",8200l),entry("Br",8100l),entry(";",8000l),entry("I",7900l),entry("#",1200l),entry("&",RING_RANK),entry(",",1000l)
    );

    /**
     * The bond rankings to be used for the four bond order possibilities.
     */

    private static final int[] bondRankings = {0, 0, 200000, 300000, 100000};

    /** Configure with no special treatment (default). */
    private static final int DEFAULT_MODE = 0;

    /** Ignored parent ordering be considered when sorting spheres (legacy mode)
     *  for compatibility with existing ML/AI models. */
    public static final int LEGACY_MODE  = 1;

    private final int flags;

    /**
     * Constructor for the HOSECodeGenerator.
     *
     * <h1>Important!</h1>
     * A critical bug was discovered in
     * the implementation (see <a href="https://github.com/cdk/cdk/pull/828">PR
     * 828</a>) which gave the wrong nesting in "some" cases. Fixing this
     * behaviour invalidates any ML/AI models trained on the incorrect values.
     * If you have a model built with the old algorithm that can not be
     * retrained set {@code legacyMode=true}.
     *
     * @param flags (default: false)
     * @see <a href="https://github.com/cdk/cdk/pull/828">PR 828</a>
     */
    public HOSECodeGenerator(int flags) {
        this.flags = flags;
        sphereNodes = new ArrayList<>();
        sphereNodesWithAtoms = new ArrayList<>();
        nextSphereNodes = new ArrayList<>();
        HOSECode = new StringBuffer();
    }

    /**
     * Constructor for the HOSECodeGenerator.
     *
     * <h1>Important!</h1>
     * A critical bug was discovered in
     * the implementation (see <a href="https://github.com/cdk/cdk/pull/828">PR
     * 828</a>) which gave the wrong nesting in "some" cases. Fixing this
     * behaviour invalidates any ML/AI models trained on the incorrect values.
     * If you have a model built with the old algorithm that can not be
     * retrained set {@cdoe new HOSECodeGenerator(HOSECodeGenerator.LEGACY_MODE)}.
     *
     * @see <a href="https://github.com/cdk/cdk/pull/828">PR 828</a>
     */
    public HOSECodeGenerator() {
        this(DEFAULT_MODE);
    }

    private IsotopeFactory isotopeFac = null;

    private void ensureIsotopeFactory() throws CDKException {
        if (isotopeFac == null) {
            try {
                isotopeFac = Isotopes.getInstance();
            } catch (IOException e) {
                throw new CDKException("Could not instantiate the IsotopeFactory: " + e.getMessage(), e);
            }
        }
    }

    /**
     *  This method is intended to be used to get the atoms around an atom in spheres. It is not used in this class, but is provided for other classes to use.
     *  It also creates the HOSE code in HOSECode as a side-effect.
     *
     *@param  ac  The {@link IAtomContainer} with the molecular skeleton in which the root atom resides.
     *@param  root The root atom for which to produce the spheres.
     *@param  noOfSpheres  The number of spheres to look at.
     *@param  ringsize  Shall the center code have the ring size in it? Only use if you want to have the hose code later, else say false.
     *@return An array of {@link List}. The list at i-1 contains the atoms at sphere i as TreeNodes.
     **/
    public List<IAtom>[] getSpheres(IAtomContainer ac, IAtom root, int noOfSpheres, boolean ringsize)
            throws CDKException {
        ensureIsotopeFactory();
        centerCode = "";
        this.atomContainer = ac;
        maxSphere = noOfSpheres;
        spheres = new List[noOfSpheres + 1];
        spheresWithAtoms = new List[noOfSpheres + 1];
        for (int i = 0; i < ac.getAtomCount(); i++) {
            ac.getAtom(i).setFlag(IChemObject.VISITED, false);
        }
        root.setFlag(IChemObject.VISITED, true);
        /*
         * All we need to observe is how the ranking of substituents in the
         * subsequent spheres of the root nodes influences the ranking of the
         * first sphere, since the order of a node in a sphere depends on the
         * order the preceding node in its branch
         */
        HOSECode = new StringBuffer();
        createCenterCode(root, ac, ringsize);
        breadthFirstSearch(root, false);
        createCode();
        fillUpSphereDelimiters();
        logger.debug("HOSECodeGenerator -> HOSECode: " + HOSECode.toString());
        return spheresWithAtoms;
    }

    /**
     * Produces a HOSE code for Atom <code>root</code> in the {@link IAtomContainer} <code>ac</code>. The HOSE
     * code is produced for the number of spheres given by <code>noOfSpheres</code>.
     * IMPORTANT: if you want aromaticity to be included in the code, you need
     * to run the IAtomContainer <code>ac</code> to the {@link org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector} prior to
     * using <code>getHOSECode()</code>. This method only gives proper results if the molecule is
     * fully saturated (if not, the order of the HOSE code might depend on atoms in higher spheres).
     * This method is known to fail for protons sometimes.
     * IMPORTANT: Your molecule must contain implicit or explicit hydrogens
     * for this method to work properly.
     *
     * @param  ac  The {@link IAtomContainer} with the molecular skeleton in which the root atom resides
     * @param  root The root atom for which to produce the HOSE code
     * @param  noOfSpheres  The number of spheres to look at
     * @return The HOSECode value
     * @exception  org.openscience.cdk.exception.CDKException  Thrown if something is wrong
     */
    public String getHOSECode(IAtomContainer ac, IAtom root, int noOfSpheres) throws CDKException {
        return getHOSECode(ac, root, noOfSpheres, false);
    }

    /**
    * Produces a HOSE code for Atom <code>root</code> in the {@link IAtomContainer} <code>ac</code>. The HOSE
    * code is produced for the number of spheres given by <code>noOfSpheres</code>.
    * IMPORTANT: if you want aromaticity to be included in the code, you need
    * to run the IAtomContainer <code>ac</code> to the {@link org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector} prior to
    * using <code>getHOSECode()</code>. This method only gives proper results if the molecule is
    * fully saturated (if not, the order of the HOSE code might depend on atoms in higher spheres).
    * This method is known to fail for protons sometimes.
    * IMPORTANT: Your molecule must contain implicit or explicit hydrogens
    * for this method to work properly.
     *
     * @param  ac  The IAtomContainer with the molecular skeleton in which the root atom resides
     * @param  root The root atom for which to produce the HOSE code
     * @param  noOfSpheres  The number of spheres to look at
     * @param  ringsize  The size of the ring(s) it is in is included in center atom code
     * @return The HOSECode value
     * @exception  org.openscience.cdk.exception.CDKException  Thrown if something is wrong
     */
    public String getHOSECode(IAtomContainer ac, IAtom root, int noOfSpheres, boolean ringsize) throws CDKException {
        ensureIsotopeFactory();
        CanonicalLabeler canLabler = new CanonicalLabeler();
        canLabler.canonLabel(ac);
        centerCode = "";
        this.atomContainer = ac;
        maxSphere = noOfSpheres;
        spheres = new List[noOfSpheres + 1];
        for (int i = 0; i < ac.getAtomCount(); i++) {
            ac.getAtom(i).setFlag(IChemObject.VISITED, false);
        }
        root.setFlag(IChemObject.VISITED, true);
        /*
         * All we need to observe is how the ranking of substituents in the
         * subsequent spheres of the root nodes influences the ranking of the
         * first sphere, since the order of a node in a sphere depends on the
         * order the preceding node in its branch
         */
        HOSECode = new StringBuffer();
        createCenterCode(root, ac, ringsize);
        breadthFirstSearch(root, true);
        createCode();
        fillUpSphereDelimiters();
        logger.debug("HOSECodeGenerator -> HOSECode: ", HOSECode);
        return HOSECode.toString();
    }

    private void createCenterCode(IAtom root, IAtomContainer ac, boolean ringsize) {
        int partnerCount;
        partnerCount = atomContainer.getConnectedBondsCount(root)
                + (root.getImplicitHydrogenCount() == CDKConstants.UNSET ? 0 : root.getImplicitHydrogenCount());
        centerCode = root.getSymbol() + "-" + partnerCount + createChargeCode(root)
                + (ringsize ? getRingcode(root, ac) : "") + ";";
    }

    private String getRingcode(IAtom root, IAtomContainer ac) {
        if (ac != acold) {
            soar = Cycles.sssr(ac).toRingSet();
        }
        boolean[] bool = new boolean[1000];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < soar.getRings(root).getAtomContainerCount(); i++) {
            if (soar.getRings(root).getAtomContainer(i).getAtomCount() < bool.length)
                bool[soar.getRings(root).getAtomContainer(i).getAtomCount()] = true;
        }
        for (int i = 0; i < bool.length; i++) {
            if (bool[i]) sb.append(i);
        }
        if (sb.toString().isEmpty())
            return "";
        else
            return "-" + sb;
    }

    private String createChargeCode(IAtom atom) {
        StringBuilder tempCode = new StringBuilder();

        if (atom != null) {

            Integer formalCharge = atom.getFormalCharge();
            if (formalCharge == CDKConstants.UNSET) formalCharge = 0;

            if (formalCharge != 0) {

                if (Math.abs(formalCharge) == 1) {
                    if (formalCharge < 0)
                        tempCode.append('-');
                    else
                        tempCode.append('+');
                } else {
                    tempCode.append('\'');
                    if (formalCharge > 0) tempCode.append('+');
                    tempCode.append(formalCharge).append('\'');
                }
            }
        }
        return (tempCode.toString());
    }

    /**
     *  Prepares for a breadth first search within the {@link IAtomContainer}. The actual
     *  recursion is done in <code>nextSphere()</code>.
     *
     *@param  root  The atom at which we start the search
     *@exception  org.openscience.cdk.exception.CDKException  If something goes wrong.
     */
    private void breadthFirstSearch(IAtom root, boolean addTreeNode) throws CDKException {
        sphere = 0;
        TreeNode tempNode;
        List<IAtom> conAtoms = atomContainer.getConnectedAtomsList(root);
        IAtom atom;
        IBond bond;
        sphereNodes.clear();
        sphereNodesWithAtoms.clear();
        for (IAtom conAtom : conAtoms) {
            try {
                atom = conAtom;
                if (atom.getSymbol().equals("H")) continue;
                bond = atomContainer.getBond(root, atom);
                /*
                 * In the first sphere the atoms are labeled with their own atom
                 * atom as source
                 */
                if (bond.getFlag(IChemObject.AROMATIC)) {
                    tempNode = new TreeNode(atom.getSymbol(), new TreeNode(root.getSymbol(), null, root, 0, 0,
                                                                           0), atom, 4, atomContainer.getConnectedBondsCount(atom), 0);
                } else {
                    tempNode = new TreeNode(atom.getSymbol(), new TreeNode(root.getSymbol(), null, root, 0, 0,
                                                                           0), atom, bond.getOrder()
                                                                                         .numeric(), atomContainer.getConnectedBondsCount(atom), 0);
                }

                sphereNodes.add(tempNode);
                if (!addTreeNode) sphereNodesWithAtoms.add(atom);

                //		        rootNode.childs.addElement(tempNode);
                atom.setFlag(IChemObject.VISITED, true);
            } catch (Exception exc) {
                throw new CDKException("Error in HOSECodeGenerator->breadthFirstSearch.", exc);
            }
        }
        sphereNodes.sort(new TreeNodeComparator());
        nextSphere(sphereNodes);
    }

    /**
     *  The actual recursion method for our breadth first search. Each node in
     *  sphereNodes is inspected for its descendants which are then stored in
     *  <code>nextSphereNodes</code>, which again is passed to the next recursion level of
     *  <code>nextSphere()</code>.
     *
     *@param  sphereNodes The sphereNodes to be inspected
     *@exception  org.openscience.cdk.exception.CDKException  If something goes wrong
     */
    private void nextSphere(List<TreeNode> sphereNodes) throws CDKException {
        spheres[sphere] = sphereNodes;
        if (spheresWithAtoms != null) spheresWithAtoms[sphere] = sphereNodesWithAtoms;
        /*
         * From here we start assembling the next sphere
         */
        IAtom node;
        IAtom toNode;
        List<IAtom> conAtoms;
        TreeNode treeNode;
        nextSphereNodes = new ArrayList<>();
        IBond bond;
        for (TreeNode sphereNode : sphereNodes) {
            treeNode = sphereNode;
            if (!("&;#:,".contains(treeNode.symbol))) {
                node = treeNode.atom;
                if (node.getSymbol().equals("H")) continue;

                conAtoms = atomContainer.getConnectedAtomsList(node);
                if (conAtoms.size() == 1) {
                    nextSphereNodes.add(new TreeNode(",", treeNode, null, 0, 0, treeNode.score));
                } else {
                    for (IAtom conAtom : conAtoms) {
                        toNode = conAtom;
                        if (!toNode.equals(treeNode.source.atom)) {
                            bond = atomContainer.getBond(node, toNode);
                            if (bond.getFlag(IChemObject.AROMATIC)) {
                                nextSphereNodes.add(new TreeNode(toNode.getSymbol(), treeNode, toNode, 4, atomContainer
                                        .getConnectedBondsCount(toNode), treeNode.score));
                            } else {
                                nextSphereNodes.add(new TreeNode(toNode.getSymbol(), treeNode, toNode, bond.getOrder()
                                                                                                           .numeric(), atomContainer.getConnectedBondsCount(toNode), treeNode.score));
                            }
                        }
                    }
                }
            }
        }
        nextSphereNodes.sort(new TreeNodeComparator());
        if (sphere < maxSphere) {
            sphere++;
            nextSphere(nextSphereNodes);
        }
    }

    public String makeBremserCompliant(String code) {
        int sepIndex = code.indexOf(';');
        if (sepIndex >= 0) {
            code = code.substring(sepIndex + 1);
        }
        return code;
    }

    /**
     *  After recursively having established the spheres and assigning each node an
     *  appropriate score, we now generate the complete HOSE code.
     *
     *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong
     */
    private void createCode() throws CDKException {
        for (int f = 0; f < atomContainer.getAtomCount(); f++) {
            atomContainer.getAtom(f).setFlag(IChemObject.VISITED, false);
        }
        for (int f = 0; f < maxSphere; f++) {
            for (TreeNode tn : spheres[maxSphere - f]) {
                if (tn.source != null) {
                    tn.source.ranking += tn.degree;
                }
            }
        }

        if ((flags&LEGACY_MODE) != 0) {
            for (int f = 0; f < maxSphere; f++)
                calculateNodeScores(spheres[f]);
            for (int f = 0; f < maxSphere; f++)
                for (TreeNode tn : spheres[f])
                    tn.score += tn.ranking;
            for (int f = 0; f < maxSphere; f++) {
                for (TreeNode tn : spheres[f]) {
                    StringBuilder localscore = new StringBuilder(tn.score + "");
                    while (localscore.length() < 6) {
                        localscore.insert(0, "0");
                    }
                    tn.stringscore = tn.source.stringscore + "" + localscore;
                }
                sortNodesByScore(spheres[f]);
            }
        }
        else {
            for (int f = 0; f < maxSphere; f++) {
                calculateNodeScores(spheres[f]);
                for (TreeNode tn : spheres[f])
                    tn.score += tn.ranking;
                sortNodesByScore(spheres[f]);
            }
        }
        for (int i = 0; i < atomContainer.getAtomCount(); i++) {
            atomContainer.getAtom(i).setFlag(IChemObject.VISITED, false);
        }
        
        HOSECode.append(centerCode);
        for (int f = 0; f < maxSphere; f++) {
            sphere = f + 1;
            String s = getSphereCode(spheres[f]);
            HOSECode.append(s);
        }
    }

    /**
     *  Generates the string code for a given sphere.
     *
     *@param  sphereNodes A vector of TreeNodes for which a string code is to be generated
     *@return The SphereCode value
     *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong
     */
    private String getSphereCode(List<TreeNode> sphereNodes) throws CDKException {
        if (sphereNodes == null || sphereNodes.size() < 1) {
            return sphereDelimiters[sphere - 1];
        }
        TreeNode treeNode;
        StringBuilder code = new StringBuilder();
        /*
         * append the tree node code to the HOSECode in their now determined
         * order, using commas to separate nodes from different branches
         */
        IAtom branch = sphereNodes.get(0).source.atom;
        StringBuilder tempCode;
        for (TreeNode sphereNode : sphereNodes) {
            treeNode = sphereNode;
            tempCode = new StringBuilder();
            if (!treeNode.source.stopper && !treeNode.source.atom.equals(branch)) {
                branch = treeNode.source.atom;
                code.append(',');
            }

            if (!treeNode.source.stopper && treeNode.source.atom.equals(branch)) {
                if (treeNode.bondType <= 4) {
                    tempCode.append(bondSymbols[(int) treeNode.bondType]);
                } else {
                    throw new CDKException("Unknown bond type");
                }
                if (treeNode.atom != null && !treeNode.atom.getFlag(IChemObject.VISITED)) {
                    tempCode.append(getElementSymbol(treeNode.symbol));
                } else if (treeNode.atom != null && treeNode.atom.getFlag(IChemObject.VISITED)) {
                    tempCode.append('&');
                    treeNode.stopper = true;
                }
                code.append(tempCode).append(createChargeCode(treeNode.atom));
                treeNode.hSymbol = tempCode.toString();
            }
            if (treeNode.atom != null) treeNode.atom.setFlag(IChemObject.VISITED, true);
            if (treeNode.source.stopper) treeNode.stopper = true;
        }
        code.append(sphereDelimiters[sphere - 1]);
        return code.toString();
    }

    /**
     *  Gets the element rank for a given element symbol as given in Bremser's
     *  publication.
     *
     *@param  symbol  The element symbol for which the rank is to be determined
     *@return         The element rank
     */
    private long getElementRank(String symbol) {
        if(rankedSymbols.containsKey(symbol))
        	return rankedSymbols.get(symbol);
        IIsotope isotope = isotopeFac.getMajorIsotope(symbol);
        if (isotope.getMassNumber() != null) {
            return ((long) 800000 - isotope.getMassNumber());
        }
        return 800000;
    }

    /**
     *  Returns the Bremser-compatible symbols for a given element. Silicon, for
     *  example, is actually "Q". :-)
     *
     *@param  sym  The element symbol to be converted
     *@return      The converted symbol
     */
    private String getElementSymbol(String sym) {
        if (sym.equals("Si")) {
            return "Q";
        }
        if (sym.equals("Cl")) {
            return "X";
        }
        if (sym.equals("Br")) {
            return "Y";
        }
        if (sym.equals(",")) {
            return "";
        }
        return sym;
    }

	/**
     *  Determines the ranking score for each node, allowing for a sorting of nodes
     *  within one sphere.
     *
     *@param  sphereNodes The nodes for which the score is to be calculated.
     *@exception  org.openscience.cdk.exception.CDKException  Thrown if something goes wrong.
     */
    private void calculateNodeScores(List<TreeNode> sphereNodes) throws CDKException {
        TreeNode treeNode;
        List<TreeNode> visitedTreeNodes=new ArrayList<>();
        for (TreeNode sphereNode : sphereNodes) {
            treeNode = sphereNode;
            if ((flags&LEGACY_MODE) == 0 && treeNode.atom != null && treeNode.atom.getFlag(IChemObject.VISITED)) {
            	treeNode.score += RING_RANK;
            }else {
            	treeNode.score += getElementRank(treeNode.symbol);
            }
            if (treeNode.bondType <= 4) {
                treeNode.score += bondRankings[(int) treeNode.bondType];
            } else {
                throw new CDKException("Unknown bond type encountered in HOSECodeGenerator");
            }
            if (treeNode.atom != null) visitedTreeNodes.add(treeNode);
        }
        for(TreeNode treeNode2 : visitedTreeNodes) {
        	treeNode2.atom.setFlag(IChemObject.VISITED, true);
        }
    }

    /**
     *  Sorts the nodes (atoms) in the sphereNode vector according to their score.
     *  This is used for the essential ranking of nodes in HOSE code sphere.
     *
     *@param  sphereNodes  A vector with sphere nodes to be sorted.
     */
    private void sortNodesByScore(List<TreeNode> sphereNodes) {

        if ((flags&LEGACY_MODE) != 0) {
            sphereNodes.sort((a, b) -> b.stringscore.compareTo(a.stringscore));
        } else {
            sphereNodes.sort((a, b) -> {
                // compare the parent node (source) first then the child string score
                int cmp = Integer.compare(b.source.sortOrder, a.source.sortOrder);
                if (cmp != 0) return cmp;
                return Long.compare(b.score, a.score);
            });
        }

        /* Having sorted a sphere, we label the nodes with their sort order */
        for (int i = 0; i < sphereNodes.size(); i++) {
            sphereNodes.get(i).sortOrder = sphereNodes.size() - i;
        }
    }

    /**
     *  If we use less than four sphere, this fills up the code with the missing
     *  delimiters such that we are compatible with Bremser's HOSE code table.
     */
    private void fillUpSphereDelimiters() {
        logger.debug("Sphere: " + sphere);
        for (int f = sphere; f < 4; f++) {
            HOSECode.append(sphereDelimiters[f]);
        }
    }

    class TreeNodeComparator implements Comparator<TreeNode> {

        /**
         *The compare method, compares by canonical label of atoms
         *
         * @param  a  The first TreeNode
         * @param  b  The second TreeNode
         * @return       -1,0,1
         */
        @Override
        public int compare(TreeNode a, TreeNode b) {
            return label(a).compareTo(label(b));
        }

        /**
         * Access the canonical label for the given tree node's atom. If any component is null
         * then {@link Long#MIN_VALUE} is return thus sorting that object in lower order.
         * @param node a tree node to get the label from
         * @return canonical label value
         */
        private Long label(TreeNode node) {
            if (node == null) return Long.MIN_VALUE;
            IAtom atom = node.getAtom();
            if (atom == null) return Long.MIN_VALUE;
            // cast can be removed in master
            Long label = atom.getProperty(InvPair.CANONICAL_LABEL);
            if (label == null) return Long.MIN_VALUE;
            return label;
        }

    }

    /**
     *  Helper class for storing the properties of a node in our breadth first
     *  search.
     *
     * @author     steinbeck
     * @cdk.created    2002-11-16
     */
    class TreeNode {

        String         symbol;
        TreeNode       source;
        IAtom          atom;
        double         bondType;
        int            degree;
        long           score;
        int            ranking;
        int            sortOrder;
        List<TreeNode> childs;
        String         hSymbol     = null;
        boolean        stopper     = false;
        String         stringscore = "";

        /**
         *  Constructor for the TreeNode object.
         *
         *@param  symbol    The Element symbol of the node
         *@param  source    The preceding node for this node
         *@param  atom      The IAtom object belonging to this node
         *@param  bondType  The bond type by which this node was connect to its
         *      predecessor
         *@param  score     The score used to rank this node within its sphere.
         *@param  degree    Description of the Parameter
         */
        TreeNode(String symbol, TreeNode source, IAtom atom, double bondType, int degree, long score) {
            this.symbol = symbol;
            this.source = source;
            this.atom = atom;
            this.degree = degree;
            this.score = score;
            this.bondType = bondType;
            ranking = 0;
            sortOrder = 1;
            childs = new ArrayList<>();
        }

        public IAtom getAtom() {
            return atom;
        }

        /**
         *  A TreeNode is equal to another TreeNode if it
         *  stands for the same atom object.
         *
         *@param  o  The object that we compare this TreeNode to
         *@return    True, if the this TreeNode's atom object equals the one of the other TreeNode
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TreeNode))
                return false;
            return this.atom.equals(((TreeNode) o).atom);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(atom);
        }

        @Override
        public String toString() {
            String s = "";
            try {
                s += (atomContainer.indexOf(atom) + 1);
                s += " " + hSymbol;
                s += "; s=" + score;
                s += "; r=" + ranking;
                s += "; d = " + degree;
            } catch (Exception exc) {
                return exc.toString();
            }
            return s;
        }
    }

    public List<IAtom> getNodesInSphere(int sphereNumber) {
        sphereNodes = spheres[sphereNumber - 1];
        List<IAtom> atoms = new ArrayList<>();
        for (TreeNode sphereNode : sphereNodes) {
            atoms.add(sphereNode.atom);
        }
        return (atoms);
    }
}
