/* Copyright (C) 2011 Mark Rijnbeek <markr@ebi.ac.uk>
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.tautomers;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.InChINumbersTools;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.AtomMatcher;
import org.openscience.cdk.isomorphism.BondMatcher;
import org.openscience.cdk.smiles.SmiFlavor;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Creates tautomers for a given input molecule, based on the mobile H atoms listed in the InChI.
 * Algorithm described in {@cdk.cite Thalheim2010}.
 * <br>
 * <b>Provide your input molecules in Kekule form, and make sure atom type are perceived.</b><br>
 * When creating an input molecule by reading an MDL file, make sure to set implicit hydrogens. See the
 * InChITautomerGeneratorTest test case.
 *
 * @author Mark Rijnbeek
 * @cdk.module tautomer
 * @cdk.githash
 */
public final class InChITautomerGenerator {

    private final static ILoggingTool LOGGER = LoggingToolFactory.createLoggingTool(InChITautomerGenerator.class);

    private static final SmilesGenerator CANSMI = new SmilesGenerator(SmiFlavor.Canonical);

    /** Generate InChI with -KET (keto-enol tautomers) option. */
    public static final int KETO_ENOL      = 0x1;

    /** Generate InChI with -15T (1,5-shift tautomers) option. */
    public static final int ONE_FIVE_SHIFT = 0x2;

    private final int flags;

    /**
     * Create a tautomer generator specifygin whether to enable, keto-enol (-KET) and 1,5-shifts (-15T).
     *
     * <pre>{@code
     * // enabled -KET option
     * InChITautomerGenerator tautgen = new InChITautomerGenerator(InChITautomerGenerator.KETO_ENOL);
     * // enabled both -KET and -15T
     * InChITautomerGenerator tautgen = new InChITautomerGenerator(InChITautomerGenerator.KETO_ENOL | InChITautomerGenerator.ONE_FIVE_SHIFT);
     * }</pre>
     *
     * @param flags the options
     */
    public InChITautomerGenerator(int flags) {
        this.flags = flags;
    }

    /**
     * Create a tautomer generator, keto-enol (-KET) and 1,5-shifts (-15T) are disabled.
     */
    public InChITautomerGenerator() {
        this(0);
    }

    /**
     * Public method to get tautomers for an input molecule, based on the InChI which will be calculated by JNI-InChI.
     * @param mol molecule for which to generate tautomers
     * @return a list of tautomers, if any
     * @throws CDKException
     * @throws CloneNotSupportedException
     */
    public List<IAtomContainer> getTautomers(IAtomContainer mol) throws CDKException, CloneNotSupportedException {

        String opt = "";
        if ((flags & KETO_ENOL) != 0)
            opt += " -KET";
        if ((flags & ONE_FIVE_SHIFT) != 0)
            opt += " -15T";

        InChIGenerator gen   = InChIGeneratorFactory.getInstance().getInChIGenerator(mol, opt);
        String         inchi = gen.getInchi();
        String         aux   = gen.getAuxInfo();

        long[] amap = new long[mol.getAtomCount()];
        InChINumbersTools.parseAuxInfo(aux, amap);

        if (inchi == null)
            throw new CDKException(InChIGenerator.class
                    + " failed to create an InChI for the provided molecule, InChI -> null.");
        return getTautomers(mol, inchi, amap);
    }

    /**
     * This method is slower than recalculating the InChI with {@link #getTautomers(IAtomContainer)} as the mapping
     * between the two can be found more efficiently.
     *
     * @param mol
     * @param inchi
     * @return
     * @throws CDKException
     * @throws CloneNotSupportedException
     * @deprecated use {@link #getTautomers(IAtomContainer)} directly
     */
    @Deprecated
    public List<IAtomContainer> getTautomers(IAtomContainer mol, String inchi) throws CDKException, CloneNotSupportedException {
        return getTautomers(mol, inchi, null);
    }

    /**
     * Overloaded {@link #getTautomers(IAtomContainer)} to get tautomers for an input molecule with the InChI already
     * provided as input argument.
     *
     * @param mol   and input molecule for which to generate tautomers
     * @param inchi InChI for the input molecule
     * @param amap  ordering of the molecules atoms in the InChI
     * @return a list of tautomers
     * @throws CDKException
     * @throws CloneNotSupportedException no longer thrown
     */
    private List<IAtomContainer> getTautomers(IAtomContainer mol, String inchi, long[] amap) throws CDKException, CloneNotSupportedException {

        //Initial checks
        if (mol == null || inchi == null)
            throw new CDKException("Please provide a valid input molecule and its corresponding InChI value.");

        // shallow copy since we will suppress hydrogens
        mol = mol.getBuilder().newInstance(IAtomContainer.class, mol);
        for (IAtom atom : mol.atoms()) {
            if (atom.getImplicitHydrogenCount() == null)
                atom.setImplicitHydrogenCount(0);
        }

        List<IAtomContainer> tautomers = new ArrayList<IAtomContainer>();
        if (!inchi.contains("(H")) { //No mobile H atoms according to InChI, so bail out.
            tautomers.add(mol);
            return tautomers;
        }

        //Preparation: translate the InChi
        Map<Integer, IAtom> inchiAtomsByPosition = getElementsByPosition(inchi, mol);
        IAtomContainer inchiMolGraph = connectAtoms(inchi, mol, inchiAtomsByPosition);

        if (amap != null && amap.length == mol.getAtomCount()) {
            for (int i = 0; i < amap.length; i++) {
                mol.getAtom(i)
                   .setID(Long.toString(amap[i]));
            }
            mol = AtomContainerManipulator.suppressHydrogens(mol);
        } else {
            mol = AtomContainerManipulator.suppressHydrogens(mol);
            mapInputMoleculeToInchiMolgraph(inchiMolGraph, mol);
        }

        List<Integer> mobHydrAttachPositions = new ArrayList<Integer>();
        int totalMobHydrCount = parseMobileHydrogens(mobHydrAttachPositions, inchi);

        tautomers = constructTautomers(mol, mobHydrAttachPositions, totalMobHydrCount);
        //Remove duplicates
        return removeDuplicates(tautomers);
    }

    /**
     * Parses the InChI's formula (ignoring hydrogen) and returns a map
     * with with a position for each atom, increasing in the order
     * of the elements as listed in the formula.
     * @param inputInchi user input InChI
     * @param inputMolecule user input molecule
     * @return <Integer,IAtom> map indicating position and atom
     */
    private Map<Integer, IAtom> getElementsByPosition(String inputInchi, IAtomContainer inputMolecule)
            throws CDKException {
        Map<Integer, IAtom> inchiAtomsByPosition = new HashMap<Integer, IAtom>();
        int position = 0;
        String inchi = inputInchi;

        inchi = inchi.substring(inchi.indexOf('/') + 1);
        String formula = inchi.substring(0, inchi.indexOf('/'));

        /*
         * Test for dots in the formula. For now, bail out when encountered; it
         * would require more sophisticated InChI connection table parsing.
         * Example: what happened to the platinum connectivity below?
         * N.N.O=C1O[Pt]OC(=O)C12CCC2<br>
         * InChI=1S/C6H8O4.2H3N.Pt/c7-4(8)6(5(9)10
         * )2-1-3-6;;;/h1-3H2,(H,7,8)(H,9,10);2*1H3;/q;;;+2/p-2
         */
        if (formula.contains("."))
            throw new CDKException("Cannot parse InChI, formula contains dot (unsupported feature). Input formula="
                    + formula);

        Pattern formulaPattern = Pattern.compile("\\.?[0-9]*[A-Z]{1}[a-z]?[0-9]*");
        Matcher match = formulaPattern.matcher(formula);
        while (match.find()) {
            String symbolAndCount = match.group();
            String elementSymbol = (symbolAndCount.split("[0-9]"))[0];
            if (!elementSymbol.equals("H")) {
                int elementCnt = 1;
                if (!(elementSymbol.length() == symbolAndCount.length())) {
                    elementCnt = Integer.valueOf(symbolAndCount.substring(elementSymbol.length()));
                }

                for (int i = 0; i < elementCnt; i++) {
                    position++;
                    IAtom atom = inputMolecule.getBuilder().newInstance(IAtom.class, elementSymbol);
                    /*
                     * This class uses the atom's ID attribute to keep track of
                     * atom positions defined in the InChi. So if for example
                     * atom.ID=14, it means this atom has position 14 in the
                     * InChI connection table.
                     */
                    atom.setID(position + "");
                    inchiAtomsByPosition.put(position, atom);
                }
            }
        }
        return inchiAtomsByPosition;
    }

    /**
     * Pops and pushes its ways through the InChI connection table to build up a simple molecule.
     * @param inputInchi user input InChI
     * @param inputMolecule user input molecule
     * @param inchiAtomsByPosition
     * @return molecule with single bonds and no hydrogens.
     */
    private IAtomContainer connectAtoms(String inputInchi, IAtomContainer inputMolecule,
            Map<Integer, IAtom> inchiAtomsByPosition) throws CDKException {
        String inchi = inputInchi;
        inchi = inchi.substring(inchi.indexOf('/') + 1);
        inchi = inchi.substring(inchi.indexOf('/') + 1);
        String connections = inchi.substring(1, inchi.indexOf('/'));
        Pattern connectionPattern = Pattern.compile("(-|\\(|\\)|,|([0-9])*)");
        Matcher match = connectionPattern.matcher(connections);
        Stack<IAtom> atomStack = new Stack<IAtom>();
        IAtomContainer inchiMolGraph = inputMolecule.getBuilder().newInstance(IAtomContainer.class);
        boolean pop = false;
        boolean push = true;
        while (match.find()) {
            String group = match.group();
            push = true;
            if (!group.isEmpty()) {
                if (group.matches("[0-9]*")) {
                    IAtom atom = inchiAtomsByPosition.get(Integer.valueOf(group));
                    if (!inchiMolGraph.contains(atom)) inchiMolGraph.addAtom(atom);
                    IAtom prevAtom = null;
                    if (atomStack.size() != 0) {
                        if (pop) {
                            prevAtom = atomStack.pop();
                        } else {
                            prevAtom = atomStack.get(atomStack.size() - 1);
                        }
                        IBond bond = inputMolecule.getBuilder().newInstance(IBond.class, prevAtom, atom,
                                IBond.Order.SINGLE);
                        inchiMolGraph.addBond(bond);
                    }
                    if (push) {
                        atomStack.push(atom);
                    }
                } else if (group.equals("-")) {
                    pop = true;
                    push = true;
                } else if (group.equals(",")) {
                    atomStack.pop();
                    pop = false;
                    push = false;
                } else if (group.equals("(")) {
                    pop = false;
                    push = true;
                } else if (group.equals(")")) {
                    atomStack.pop();
                    pop = true;
                    push = true;
                } else {
                    throw new CDKException("Unexpected token " + group + " in connection table encountered.");
                }
            }
        }
        //put any unconnected atoms in the output as well
        for (IAtom at : inchiAtomsByPosition.values()) {
            if (!inchiMolGraph.contains(at)) inchiMolGraph.addAtom(at);
        }
        return inchiMolGraph;
    }

    /**
     * Atom-atom mapping of the input molecule to the bare container constructed from the InChI connection table.
     * This makes it possible to map the positions of the mobile hydrogens in the InChI back to the input molecule.
     * @param inchiMolGraph molecule (bare) as defined in InChI
     * @param mol user input molecule
     * @throws CDKException
     */
    private void mapInputMoleculeToInchiMolgraph(IAtomContainer inchiMolGraph, IAtomContainer mol) throws CDKException {
        Iterator<Map<IAtom, IAtom>> iter = org.openscience.cdk.isomorphism.VentoFoggia.findIdentical(inchiMolGraph,
                                                                                                     AtomMatcher.forElement(),
                                                                                                     BondMatcher.forAny())
                                                                                  .matchAll(mol)
                                                                                  .limit(1)
                                                                                  .toAtomMap()
                                                                                  .iterator();
        if (iter.hasNext()) {
            for (Map.Entry<IAtom,IAtom> e : iter.next().entrySet()) {
                IAtom src = e.getKey();
                IAtom dst = e.getValue();
                String position = src.getID();
                dst.setID(position);
                LOGGER.debug("Mapped InChI ", src.getSymbol(), " ", src.getID(), " to ", dst.getSymbol(),
                             " " + dst.getID());
            }
        } else {
            throw new IllegalArgumentException(CANSMI.create(inchiMolGraph) + " " + CANSMI.create(mol));
        }
    }

    /**
     * Parses mobile H group(s) in an InChI String.
     * <p>
     * Multiple InChI sequences of mobile hydrogens are joined into a single sequence (list),
     * see step 1 of algorithm in paper.
     * <br>
     * Mobile H group has syntax (H[n][-[m]],a1,a2[,a3[,a4...]])
     * Brackets [ ] surround optional terms.
     * <ul>
     *  <li>Term H[n] stands for 1 or, if the number n (n>1) is present, n mobile hydrogen atoms.</li>
     *  <li>Term [-[m]], if present, stands for 1 or, if the number m (m>1) is present, m mobile negative charges.</li>
     *  <li>a1,a2[,a3[,a4...]] are canonical numbers of atoms in the mobile H group.</li>
     *  <li>no two mobile H groups may have an atom (a canonical number) in common.</li>
     * </ul>
     * @param mobHydrAttachPositions list of positions where mobile H can attach
     * @param inputInchi InChI input
     * @return overall count of hydrogens to be dispersed over the positions
     */
    private int parseMobileHydrogens(List<Integer> mobHydrAttachPositions, String inputInchi) {

        int totalMobHydrCount = 0;
        String hydrogens = "";
        String inchi = inputInchi;
        if (inchi.indexOf("/h") != -1) {
            hydrogens = inchi.substring(inchi.indexOf("/h") + 2);
            if (hydrogens.indexOf('/') != -1) {
                hydrogens = hydrogens.substring(0, hydrogens.indexOf('/'));
            }
            String mobileHydrogens = hydrogens.substring(hydrogens.indexOf('('));
            Pattern mobileHydrPattern = Pattern.compile("\\((.)*?\\)");
            Matcher match = mobileHydrPattern.matcher(mobileHydrogens);
            while (match.find()) {
                String mobileHGroup = match.group();
                int mobHCount = 0;
                String head = mobileHGroup.substring(0, mobileHGroup.indexOf(',') + 1);
                if (head.contains("H,")) head = head.replace("H,", "H1,");
                if (head.contains("-,")) head = head.replace("-,", "-1,");
                head = head.substring(2);
                Pattern subPattern = Pattern.compile("[0-9]*");
                Matcher subMatch = subPattern.matcher(head);
                /*
                 * Pragmatically, also add any delocalised neg charge to the
                 * mobile H count. Based on examples like:
                 * C[N+](C)(C)CCCCC\C=C(/NC(=O)C1CC1(Cl)Cl)\C(=O)O ->
                 * ...(H-,18,20,21,22)
                 * COc1cc(N)c(Cl)cc1C(=O)NC2C[N+]3(CCl)CCC2CC3 ->
                 * ...(H2-,19,20,22)
                 */
                while (subMatch.find()) {
                    if (!subMatch.group().equals("")) {
                        mobHCount += Integer.valueOf(subMatch.group());
                    }
                }
                totalMobHydrCount += mobHCount;
                mobileHGroup = mobileHGroup.substring(mobileHGroup.indexOf(',') + 1).replace(")", "");
                StringTokenizer tokenizer = new StringTokenizer(mobileHGroup, ",");
                while (tokenizer.hasMoreTokens()) {
                    Integer position = Integer.valueOf(tokenizer.nextToken());
                    mobHydrAttachPositions.add(position);
                }
            }
        }
        LOGGER.debug("#total mobile hydrogens: ", totalMobHydrCount);
        return totalMobHydrCount;
    }

    /**
     * Constructs tautomers following (most) steps of the algorithm in {@cdk.cite Thalheim2010}.
     * @param inputMolecule input molecule
     * @param mobHydrAttachPositions mobile H positions
     * @param totalMobHydrCount count of mobile hydrogens in molecule
     * @return tautomers
     * @throws CloneNotSupportedException
     */
    private List<IAtomContainer> constructTautomers(IAtomContainer inputMolecule, List<Integer> mobHydrAttachPositions,
            int totalMobHydrCount) throws CloneNotSupportedException {
        List<IAtomContainer> tautomers = new ArrayList<IAtomContainer>();

        //Tautomeric skeleton generation
        IAtomContainer skeleton = (IAtomContainer) inputMolecule.clone();

        boolean atomsToRemove = true;
        List<IAtom> removedAtoms = new ArrayList<IAtom>();
        boolean atomRemoved = false;
        while (atomsToRemove) {
            ATOMS: for (IAtom atom : skeleton.atoms()) {
                atomRemoved = false;
                int position = Integer.valueOf(atom.getID());
                if (!mobHydrAttachPositions.contains(position)
                        && atom.getHybridization().equals(IAtomType.Hybridization.SP3)) {
                    skeleton.removeAtomOnly(atom);
                    removedAtoms.add(atom);
                    atomRemoved = true;
                    break ATOMS;
                } else {
                    for (IBond bond : skeleton.bonds()) {
                        if (bond.contains(atom) && bond.getOrder().equals(IBond.Order.TRIPLE)) {
                            skeleton.removeAtomOnly(atom);
                            removedAtoms.add(atom);
                            atomRemoved = true;
                            break ATOMS;
                        }
                    }
                }
            }
            if (!atomRemoved) atomsToRemove = false;

        }
        boolean bondsToRemove = true;
        boolean bondRemoved = false;
        while (bondsToRemove) {
            BONDS: for (IBond bond : skeleton.bonds()) {
                bondRemoved = false;
                for (IAtom removedAtom : removedAtoms) {
                    if (bond.contains(removedAtom)) {
                        IAtom other = bond.getOther(removedAtom);
                        int decValence = 0;
                        switch (bond.getOrder()) {
                            case SINGLE:
                                decValence = 1;
                                break;
                            case DOUBLE:
                                decValence = 2;
                                break;
                            case TRIPLE:
                                decValence = 3;
                                break;
                            case QUADRUPLE:
                                decValence = 4;
                                break;
                        }
                        other.setValency(other.getValency() - decValence);
                        skeleton.removeBond(bond);
                        bondRemoved = true;
                        break BONDS;
                    }
                }
            }
            if (!bondRemoved) bondsToRemove = false;

        }
        int doubleBondCount = 0;
        for (IBond bond : skeleton.bonds()) {
            if (bond.getOrder().equals(IBond.Order.DOUBLE)) {
            	bond.setOrder(IBond.Order.SINGLE);
                doubleBondCount++;
            }
        }

        for (int hPosition : mobHydrAttachPositions) {
            IAtom atom = findAtomByPosition(skeleton, hPosition);
            atom.setImplicitHydrogenCount(0);
        }

       

        // Make combinations for mobile Hydrogen attachments
        List<List<Integer>> combinations = new ArrayList<List<Integer>>();
        combineHydrogenPositions(new ArrayList<Integer>(), combinations, skeleton, totalMobHydrCount,
                mobHydrAttachPositions);

        Stack<Object> solutions = new Stack<Object>();
        for (List<Integer> hPositions : combinations) {
            IAtomContainer tautomerSkeleton = (IAtomContainer) skeleton.clone();
            for (Integer hPos : hPositions) {
                IAtom atom = findAtomByPosition(tautomerSkeleton, hPos);
                atom.setImplicitHydrogenCount(atom.getImplicitHydrogenCount() + 1);
            }
            List<IAtom> atomsInNeedOfFix = new ArrayList<IAtom>();
            for (IAtom atom : tautomerSkeleton.atoms()) {
                if (atom.getValency() - atom.getFormalCharge() != atom.getImplicitHydrogenCount()
                        + getConnectivity(atom, tautomerSkeleton)) atomsInNeedOfFix.add(atom);
            }
            List<Integer> dblBondPositions = tryDoubleBondCombinations(tautomerSkeleton, 0, 0, doubleBondCount,
                    atomsInNeedOfFix);
            if (dblBondPositions != null) {
                //Found a valid double bond combination for this mobile hydrogen configuration..
                solutions.push(dblBondPositions);
                solutions.push(tautomerSkeleton);
            }
        }
        LOGGER.debug("#possible solutions : ", solutions.size());
        if (solutions.size() == 0) {
            LOGGER.error("Could not generate any tautomers for the input. Is input in Kekule form? ");
            tautomers.add(inputMolecule);
        } else {

            while (solutions.size() != 0) {
                IAtomContainer tautomerSkeleton = (IAtomContainer) solutions.pop();
                List<Integer> dblBondPositions = (List<Integer>) solutions.pop();
                IAtomContainer tautomer = (IAtomContainer) inputMolecule.clone();
                for (IAtom skAtom1 : tautomerSkeleton.atoms()) {
                    for (IAtom atom1 : tautomer.atoms()) {
                        if (atom1.getID().equals(skAtom1.getID())) {
                            atom1.setImplicitHydrogenCount(skAtom1.getImplicitHydrogenCount());
                            for (int bondIdx = 0; bondIdx < tautomerSkeleton.getBondCount(); bondIdx++) {
                                IBond skBond = tautomerSkeleton.getBond(bondIdx);
                                if (skBond.contains(skAtom1)) {
                                    IAtom skAtom2 = skBond.getOther(skAtom1);
                                    for (IAtom atom2 : tautomer.atoms()) {
                                        if (atom2.getID().equals(skAtom2.getID())) {
                                            IBond tautBond = tautomer.getBond(atom1, atom2);
                                            if (dblBondPositions.contains(bondIdx))
                                                tautBond.setOrder(IBond.Order.DOUBLE);
                                            else
                                                tautBond.setOrder(IBond.Order.SINGLE);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (IAtom atom : tautomer.atoms()) {
                    atom.setFlag(CDKConstants.ISAROMATIC, false);
                    atom.setValency(null);
                }
                for (IBond bond : tautomer.bonds())
                    bond.setFlag(CDKConstants.ISAROMATIC, false);
                tautomers.add(tautomer);
            }
        }
        LOGGER.debug("# initial tautomers generated : ", tautomers.size());
        return tautomers;
    }

    /**
     * Removes duplicates from a molecule set. Uses canonical SMILES to detect identical molecules.
     * An example of pruning can be a case where double bonds are placed in different positions in
     * an aromatic (Kekule) ring, which all amounts to one same aromatic ring.
     *
     * @param tautomers molecule set of tautomers with possible duplicates
     * @return tautomers same set with duplicates removed
     * @throws CDKException unable to calculate canonical SMILES
     */
    private List<IAtomContainer> removeDuplicates(List<IAtomContainer> tautomers) throws CDKException {
        Set<String>          cansmis = new HashSet<>();
        List<IAtomContainer> result  = new ArrayList<>();
        for (IAtomContainer tautomer : tautomers) {
            if (cansmis.add(CANSMI.create(tautomer)))
                result.add(tautomer);
        }
        LOGGER.debug("# tautomers after clean up : ", tautomers.size());
        return result;
    }

    /**
     * Makes combinations recursively of all possible mobile Hydrogen positions.
     * @param taken positions taken by hydrogen
     * @param combinations combinations made so far
     * @param skeleton container to work on
     * @param totalMobHydrCount
     * @param mobHydrAttachPositions
     */
    private void combineHydrogenPositions(List<Integer> taken, List<List<Integer>> combinations,
            IAtomContainer skeleton, int totalMobHydrCount, List<Integer> mobHydrAttachPositions) {
        if (taken.size() != totalMobHydrCount) {
            for (int i = 0; i < mobHydrAttachPositions.size(); i++) {
                int pos = mobHydrAttachPositions.get(i);
                IAtom atom = findAtomByPosition(skeleton, pos);
                int conn = getConnectivity(atom, skeleton);
                int hCnt = 0;
                for (int t : taken)
                    if (t == pos) hCnt++;
                if (atom.getValency() - atom.getFormalCharge() > (hCnt + conn)) {
                    taken.add(pos);
                    combineHydrogenPositions(taken, combinations, skeleton, totalMobHydrCount, mobHydrAttachPositions);
                    taken.remove(taken.size() - 1);
                }
            }
        } else {
            List<Integer> addList = new ArrayList<Integer>(taken.size());
            addList.addAll(taken);
            Collections.sort(addList);
            if (!combinations.contains(addList)) {
                combinations.add(addList);
            }
        }
    }

    /**
     * Helper method that locates an atom based on its InChI atom table
     * position, which has been set as ID.
     * @param container input container
     * @param position InChI atom table position
     * @return atom on the position
     */
    private IAtom findAtomByPosition(IAtomContainer container, int position) {
        String pos = String.valueOf(position);
        for (IAtom atom : container.atoms()) {
            if (atom.getID().equals(pos)) return atom;
        }
        return null;
    }

    /**
     * Tries double bond combinations for a certain input container of which the double bonds have been stripped
     * around the mobile hydrogen positions. Recursively.
     *
     * @param container
     * @param dblBondsAdded counts double bonds added so far
     * @param bondOffSet offset for next double bond position to consider
     * @param doubleBondMax maximum number of double bonds to add
     * @param atomsInNeedOfFix atoms that require more bonds
     * @return a list of double bond positions (index) that make a valid combination, null if none found
     */
    private List<Integer> tryDoubleBondCombinations(IAtomContainer container, int dblBondsAdded, int bondOffSet,
            int doubleBondMax, List<IAtom> atomsInNeedOfFix) {

        int offSet = bondOffSet;
        List<Integer> dblBondPositions = null;

        while (offSet < container.getBondCount() && dblBondPositions == null) {
            IBond bond = container.getBond(offSet);
            if (atomsInNeedOfFix.contains(bond.getBegin()) && atomsInNeedOfFix.contains(bond.getEnd())) {
                bond.setOrder(IBond.Order.DOUBLE);
                dblBondsAdded = dblBondsAdded + 1;
                if (dblBondsAdded == doubleBondMax) {
                    boolean validDoubleBondConfig = true;
                    CHECK: for (IAtom atom : container.atoms()) {
                        if (atom.getValency() != atom.getImplicitHydrogenCount() + getConnectivity(atom, container)) {
                            validDoubleBondConfig = false;
                            break CHECK;
                        }
                    }
                    if (validDoubleBondConfig) {
                        dblBondPositions = new ArrayList<Integer>();
                        for (int idx = 0; idx < container.getBondCount(); idx++) {
                            if (container.getBond(idx).getOrder().equals(IBond.Order.DOUBLE))
                                dblBondPositions.add(idx);
                        }
                        return dblBondPositions;
                    }
                } else {
                    dblBondPositions = tryDoubleBondCombinations(container, dblBondsAdded, offSet + 1, doubleBondMax,
                            atomsInNeedOfFix);
                }

                bond.setOrder(IBond.Order.SINGLE);
                dblBondsAdded = dblBondsAdded - 1;
            }
            offSet++;
        }
        return dblBondPositions;
    }

    /**
     * Sums the number of bonds (counting order) an atom is hooked up with.
     * @param atom an atom in the container
     * @param container the container
     * @return valence (bond order sum) of the atom
     */
    private int getConnectivity(IAtom atom, IAtomContainer container) {
        int connectivity = 0;
        for (IBond bond : container.bonds()) {
            if (bond.contains(atom)) {
                switch (bond.getOrder()) {
                    case SINGLE:
                        connectivity++;
                        break;
                    case DOUBLE:
                        connectivity += 2;
                        break;
                    case TRIPLE:
                        connectivity += 3;
                        break;
                    case QUADRUPLE:
                        connectivity += 4;
                        break;
                    default:
                        connectivity += 10;
                }
            }
        }
        return connectivity;
    }
}
