/*
 * Copyright (c) 2015 John May <jwmay@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version. All we ask is that proper credit is given
 * for our work, which includes - but is not limited to - adding the above
 * copyright notice to the beginning of your source code files, and to any
 * copyright notice that you may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 U
 */

package org.openscience.cdk.depict;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.graph.GraphUtil.EdgeToBondMap;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomContainerSet;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IPseudoAtom;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for abbreviating (sub)structures. Using either self assigned structural
 * motifs or pre-loading a common set a structure depiction can be made more concise with
 * the use of abbreviations (sometimes called superatoms).
 * <p>
 * Basic usage:
 * <pre>{@code
 * Abbreviations abrv = new Abbreviations();
 *
 * // add some abbreviations, when overlapping (e.g. Me,Et,tBu) first one wins
 * abrv.add("[Na+].[H-] NaH");
 * abrv.add("*c1ccccc1 Ph");
 * abrv.add("*C(C)(C)C tBu");
 * abrv.add("*CC Et");
 * abrv.add("*C Me");
 *
 * // maybe we don't want 'Me' in the depiction
 * abrv.setEnabled("Me", false);
 *
 * // assign abbreviations with some filters
 * int numAdded = abrv.apply(mol);
 *
 * // generate all but don't assign, need to be added manually
 * // set/update the CDKConstants.CTAB_SGROUPS property of mol
 * List<Sgroup> sgroups = abrv.generate(mol);
 * }</pre>
 * <p>
 * Predefined sets of abbreviations can be loaded, the following are
 * on the classpath.
 * <p>
 * <pre>{@code
 * // https://www.github.com/openbabel/superatoms
 * abrv.loadFromFile("obabel_superatoms.smi");
 * }</pre>
 *
 * @cdk.keyword abbreviate
 * @cdk.keyword depict
 * @cdk.keyword superatom
 * @see CDKConstants#CTAB_SGROUPS
 * @see Sgroup
 */
public class Abbreviations implements Iterable<String> {

    public enum Option {
        /**
         * Allow a structured to be collapsed to a single label.
         */
        ALLOW_SINGLETON,
        /**
         * Automatically contract on hetero atoms, e.g. -NMe3
         */
        AUTO_CONTRACT_HETERO,
        /**
         * Automatically contract on terminal atoms, e.g. -CMe3.
         * This will also allow contraction of symmetric abbreviations around
         * a bond, e.g. Ph-Ph => Ph2
         */
        AUTO_CONTRACT_TERMINAL,
        /**
         * On auto-contract to linker, e.g. -S(=O)(=O)- => -S(O2)-
         */
        AUTO_CONTRACT_LINKERS,
    }

    private static final int MAX_FRAG = 50;

    /**
     * Symbol for joining disconnected fragments.
     */
    private static final String INTERPUNCT = "·";

    private final Map<String, String> connectedAbbreviations    = new LinkedHashMap<>();
    private final Map<String, String> disconnectedAbbreviations = new LinkedHashMap<>();
    private final Set<String>         labels                    = new LinkedHashSet<>();
    private final Set<String>         disabled                  = new HashSet<>();
    private final SmilesGenerator     usmigen                   = SmilesGenerator.unique();

    private final SmilesParser smipar = new SmilesParser(SilentChemObjectBuilder.getInstance());
    private final Set<Option> options = EnumSet.of(Option.AUTO_CONTRACT_HETERO);

    private static final class AdjacentGroup implements Comparable<AdjacentGroup> {

        private final List<Sgroup> sgroups = new ArrayList<>();

        private final String symbol;

        /** The abbreviation is only composed of carbons */
        private final boolean allCarbon;

        /**
         * The abbreviation is trivial, 1st level contraction and can be
         * reversing in a sketch would be identical. e.g. -tBu and tBu-, is
         * trivial. -OMe and MeO- are not.
         */
        private final boolean isTrivial;

        /** Number of times the group occurs. */
        private int count = 0;

        private AdjacentGroup(Sgroup sgroup) {
            this.symbol = sgroup.getSubscript();
            this.allCarbon = sgroup.getAtoms()
                                   .stream()
                                   .noneMatch(AdjacentGroup::isNonCarbon);
            this.isTrivial = isTrivial(sgroup.getSubscript());
        }

        private AdjacentGroup(String symbol, IAtom nbr) {
            this.symbol    = symbol;
            this.allCarbon = !isNonCarbon(nbr);
            this.isTrivial = isTrivial(symbol);
        }

        private static boolean isNonCarbon(IAtom a) {
            return a.getAtomicNumber() != IAtom.C;
        }

        private void add() {
            count++;
        }

        private void add(Sgroup sgroup) {
            this.sgroups.add(sgroup);
            this.count++;
        }

        @Override
        public int compareTo(AdjacentGroup o) {
            int cmp;
            cmp = -Boolean.compare(allCarbon, o.allCarbon);
            if (cmp != 0)
                return cmp;
            cmp = -Boolean.compare(isTrivial, o.isTrivial);
            if (cmp != 0)
                return cmp;
            cmp = -Integer.compare(count, o.count);
            if (cmp != 0)
                return cmp;
            cmp = Integer.compare(symbol.length(), o.symbol.length());
            if (cmp != 0)
                return cmp;
            return symbol.compareTo(o.symbol);
        }
    }

    public Abbreviations() {
    }

    /**
     * Iterate over loaded abbreviations. Both enabled and disabled abbreviations are listed.
     *
     * @return the abbreviations labels (e.g. Ph, Et, Me, OAc, etc.)
     */
    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableSet(labels).iterator();
    }

    /**
     * Check whether an abbreviation is enabled.
     *
     * @param label is enabled
     * @return the label is enabled
     */
    public boolean isEnabled(final String label) {
        return labels.contains(label) && !disabled.contains(label);
    }

    /**
     * Set whether an abbreviation is enabled or disabled.
     *
     * @param label   the label (e.g. Ph, Et, Me, OAc, etc.)
     * @param enabled flag the label as enabled or disabled
     * @return the label state was modified
     */
    public boolean setEnabled(String label, boolean enabled) {
        return enabled ? labels.contains(label) && disabled.remove(label)
                       : labels.contains(label) && disabled.add(label);
    }

    /**
     * Convenience method to enable an option.
     *
     * @param option the option to enable.
     * @return self, for chaining
     */
    public Abbreviations with(Option option) {
        options.add(option);
        return this;
    }

    /**
     * Convenience method to disable an option.
     *
     * @param option the option to enable.
     * @return self, for chaining
     */
    public Abbreviations without(Option option) {
        options.remove(option);
        return this;
    }

    /**
     * Set whether abbreviations should be further contracted when they are connected
     * to a heteroatom, for example -NH-Boc becomes -NHBoc. By default this option
     * is enabled.
     *
     * @param val on/off
     */
    public void setContractOnHetero(boolean val) {
        if (val)
            options.add(Option.AUTO_CONTRACT_HETERO);
        else
            options.remove(Option.AUTO_CONTRACT_HETERO);
    }

    public void setContractToSingleLabel(boolean val) {
        if (val)
            options.add(Option.ALLOW_SINGLETON);
        else
            options.remove(Option.ALLOW_SINGLETON);
    }

    private static Set<IBond> findCutBonds(IAtomContainer mol, EdgeToBondMap bmap, int[][] adjlist) {
        Set<IBond> cuts = new HashSet<>();
        int numAtoms = mol.getAtomCount();
        for (int i = 0; i < numAtoms; i++) {
            final IAtom atom = mol.getAtom(i);
            int deg  = adjlist[i].length;
            int elem = atom.getAtomicNumber();

            if (elem == 6 && deg <= 2)
                continue;

            for (int w : adjlist[i]) {
                IBond bond = bmap.get(i, w);
                if (adjlist[w].length >= 2 && !bond.isInRing()) {
                    cuts.add(bond);
                }
            }
        }
        return cuts;
    }

    private static final String CUT_BOND = "cutbond";

    private static List<IAtomContainer> makeCut(IBond cut, IAtomContainer mol, Map<IAtom, Integer> idx,
                                                int[][] adjlist) {

        IAtom beg = cut.getBegin();
        IAtom end = cut.getEnd();

        Set<IAtom> bvisit = new LinkedHashSet<>();
        Set<IAtom> evisit = new LinkedHashSet<>();
        Deque<IAtom> queue = new ArrayDeque<>();

        bvisit.add(beg);
        evisit.add(end);

        queue.add(beg);
        bvisit.add(end); // stop visits
        while (!queue.isEmpty()) {
            IAtom atom = queue.poll();
            bvisit.add(atom);
            for (int w : adjlist[idx.get(atom)]) {
                IAtom nbr = mol.getAtom(w);
                if (!bvisit.contains(nbr))
                    queue.add(nbr);
            }
        }
        bvisit.remove(end);

        queue.add(end);
        evisit.add(beg); // stop visits
        while (!queue.isEmpty()) {
            IAtom atom = queue.poll();
            evisit.add(atom);
            for (int w : adjlist[idx.get(atom)]) {
                IAtom nbr = mol.getAtom(w);
                if (!evisit.contains(nbr))
                    queue.add(nbr);
            }
        }
        evisit.remove(beg);

        IChemObjectBuilder bldr = mol.getBuilder();
        IAtomContainer bfrag = bldr.newInstance(IAtomContainer.class);
        IAtomContainer efrag = bldr.newInstance(IAtomContainer.class);

        final int diff = bvisit.size() - evisit.size();

        if (diff < -10)
            evisit.clear();
        else if (diff > 10)
            bvisit.clear();

        if (!bvisit.isEmpty()) {
            bfrag.addAtom(bldr.newInstance(IPseudoAtom.class));
            for (IAtom atom : bvisit)
                bfrag.addAtom(atom);
            bfrag.addBond(0, 1, cut.getOrder());
            bfrag.getBond(0).setProperty(CUT_BOND, cut);
        }

        if (!evisit.isEmpty()) {
            efrag.addAtom(bldr.newInstance(IPseudoAtom.class));
            for (IAtom atom : evisit)
                efrag.addAtom(atom);
            efrag.addBond(0, 1, cut.getOrder());
            efrag.getBond(0).setProperty(CUT_BOND, cut);
        }

        for (IBond bond : mol.bonds()) {
            IAtom a1 = bond.getBegin();
            IAtom a2 = bond.getEnd();
            if (bvisit.contains(a1) && bvisit.contains(a2))
                bfrag.addBond(bond);
            else if (evisit.contains(a1) && evisit.contains(a2))
                efrag.addBond(bond);
        }

        List<IAtomContainer> res = new ArrayList<>();
        if (bfrag.getAtomCount() > 1)
            res.add(bfrag);
        if (efrag.getAtomCount() > 1)
            res.add(efrag);
        return res;
    }

    private static List<IAtomContainer> generateFragments(IAtomContainer mol) {

        final EdgeToBondMap bmap = EdgeToBondMap.withSpaceFor(mol);
        final int[][] adjlist = GraphUtil.toAdjList(mol, bmap);

        Cycles.markRingAtomsAndBonds(mol, adjlist, bmap);

        Set<IBond> cuts = findCutBonds(mol, bmap, adjlist);

        Map<IAtom, Integer> atmidx = new HashMap<>();
        for (IAtom atom : mol.atoms())
            atmidx.put(atom, atmidx.size());

        // frags are ordered by biggest to smallest
        List<IAtomContainer> frags = new ArrayList<>();

        for (IBond cut : cuts) {
            if (frags.size() >= MAX_FRAG)
                break;
            frags.addAll(makeCut(cut, mol, atmidx, adjlist));
        }

        frags.sort(new Comparator<IAtomContainer>() {
            @Override
            public int compare(IAtomContainer a, IAtomContainer b) {
                return -Integer.compare(a.getBondCount(), b.getBondCount());
            }
        });
        return frags;
    }

    private Map<IAtom,List<Sgroup>> getSgroupAdjacency(List<Sgroup> sgroups) {
        Map<IAtom,List<Sgroup>> sgroupAdjs = new HashMap<>();
        for (Sgroup sgroup : sgroups) {
            if (nonTerminal(sgroup))
                continue;
            IBond attachBond = sgroup.getBonds().iterator().next();
            Set<IAtom> atoms = sgroup.getAtoms();
            final IAtom attachAtom;
            if (!atoms.contains(attachBond.getBegin()) &&
                 atoms.contains(attachBond.getEnd()))
                attachAtom = attachBond.getBegin();
            else if (atoms.contains(attachBond.getBegin()) &&
                    !atoms.contains(attachBond.getEnd()))
                attachAtom = attachBond.getEnd();
            else
                continue; // corrupted?

            sgroupAdjs.computeIfAbsent(attachAtom, k -> new ArrayList<>())
                      .add(sgroup);
        }
        return sgroupAdjs;
    }

    private Set<IBond> getCrossingBonds(List<Sgroup> sgroups) {
        Set<IBond> xbonds = new HashSet<>();
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getBonds().size() == 1)
                xbonds.addAll(sgroup.getBonds());
        }
        return xbonds;
    }

    /**
     * Find all enabled abbreviations in the provided molecule. They are not
     * added to the existing Sgroups and may need filtering.
     *
     * @param mol molecule
     * @return list of new abbreviation Sgroups
     */
    public List<Sgroup> generate(final IAtomContainer mol) {

        // mark which atoms have already been abbreviated or are
        // part of an existing Sgroup
        Set<IAtom> usedAtoms = new HashSet<>();
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups != null) {
            for (Sgroup sgroup : sgroups)
                usedAtoms.addAll(sgroup.getAtoms());
        }

        final List<Sgroup> newSgroups = new ArrayList<>();

        // disconnected abbreviations, salts, common reagents, large compounds
        if (usedAtoms.isEmpty()) {
            try {
                IAtomContainer copy = AtomContainerManipulator.copyAndSuppressedHydrogens(mol);
                String cansmi = usmigen.create(copy);
                String label = disconnectedAbbreviations.get(cansmi);

                if (label != null && !disabled.contains(label) && options.contains(Option.ALLOW_SINGLETON)) {
                    Sgroup sgroup = new Sgroup();
                    sgroup.setType(SgroupType.CtabAbbreviation);
                    sgroup.setSubscript(label);
                    for (IAtom atom : mol.atoms())
                        sgroup.addAtom(atom);
                    return Collections.singletonList(sgroup);
                } else if (cansmi.contains(".")) {
                    IAtomContainerSet parts = ConnectivityChecker.partitionIntoMolecules(mol);


                    // leave one out
                    Sgroup best = null;
                    for (int i = 0; i < parts.getAtomContainerCount(); i++) {
                        IAtomContainer a = parts.getAtomContainer(i);
                        IAtomContainer b = a.getBuilder().newAtomContainer();
                        for (int j = 0; j < parts.getAtomContainerCount(); j++)
                            if (j != i)
                                b.add(parts.getAtomContainer(j));
                        Sgroup sgroup1 = getAbbr(a);
                        Sgroup sgroup2 = getAbbr(b);
                        if (sgroup1 != null && sgroup2 != null && options.contains(Option.ALLOW_SINGLETON)) {
                            Sgroup combined = new Sgroup();
                            label = null;
                            for (IAtom atom : sgroup1.getAtoms())
                                combined.addAtom(atom);
                            for (IAtom atom : sgroup2.getAtoms())
                                combined.addAtom(atom);
                            if (sgroup1.getSubscript().length() > sgroup2.getSubscript().length())
                                combined.setSubscript(sgroup1.getSubscript() + INTERPUNCT + sgroup2.getSubscript());
                            else
                                combined.setSubscript(sgroup2.getSubscript() + INTERPUNCT + sgroup1.getSubscript());
                            combined.setType(SgroupType.CtabAbbreviation);
                            return Collections.singletonList(combined);
                        }
                        if (sgroup1 != null && (best == null || sgroup1.getAtoms().size() > best.getAtoms().size()))
                            best = sgroup1;
                        if (sgroup2 != null && (best == null || sgroup2.getAtoms().size() < best.getAtoms().size()))
                            best = sgroup2;
                    }

                    if (best != null) {
                        newSgroups.add(best);
                        usedAtoms.addAll(best.getAtoms());
                    }
                }

            } catch (CDKException ignored) {
            }
        }

        List<IAtomContainer> fragments = generateFragments(mol);

        for (IAtomContainer frag : fragments) {
            try {
                final String smi = usmigen.create(AtomContainerManipulator.copyAndSuppressedHydrogens(frag));
                final String label = connectedAbbreviations.get(smi);

                if (label == null || disabled.contains(label))
                    continue;

                boolean overlap = false;

                // note: first atom is '*'
                int numAtoms = frag.getAtomCount();
                int numBonds = frag.getBondCount();
                for (int i = 1; i < numAtoms; i++) {
                    if (usedAtoms.contains(frag.getAtom(i))) {
                        overlap = true;
                        break;
                    }
                }

                // overlaps with previous assignment
                if (overlap)
                    continue;

                // create new abbreviation SGroup
                Sgroup sgroup = new Sgroup();
                sgroup.setType(SgroupType.CtabAbbreviation);
                sgroup.setSubscript(label);

                IBond attachBond = frag.getBond(0).getProperty(CUT_BOND, IBond.class);
                sgroup.addBond(attachBond);
                for (int i = 1; i < numAtoms; i++) {
                    IAtom atom = frag.getAtom(i);
                    usedAtoms.add(atom);
                    sgroup.addAtom(atom);
                }

                newSgroups.add(sgroup);

             } catch (CDKException e) {
                // ignore
            }
        }

        if (!options.contains(Option.AUTO_CONTRACT_HETERO) &&
            !options.contains(Option.AUTO_CONTRACT_TERMINAL))
            return newSgroups;

        // collect adjacency info and terminal crossing bonds
        Map<IAtom, List<Sgroup>> sgroupAdjs = getSgroupAdjacency(newSgroups);
        Set<IBond> allCrossingBonds = getCrossingBonds(newSgroups);

        // now collapse
        collapse:
        for (IAtom attach : mol.atoms()) {
            if (usedAtoms.contains(attach))
                continue;

            // skip charged or isotopic labelled, R, *, H, He
            if ((attach.getFormalCharge() != null && attach.getFormalCharge() != 0)
                || attach.getMassNumber() != null
                || attach.getAtomicNumber() <= IAtom.He)
                continue;

            boolean okay = false;
            if (attach.getAtomicNumber() != IAtom.C &&
                    options.contains(Option.AUTO_CONTRACT_HETERO))
                okay = true;
            else if (effectiveDegree(attach, allCrossingBonds) <= 1 &&
                     options.contains(Option.AUTO_CONTRACT_TERMINAL))
                okay = true;
            if (!okay)
                continue;

            int hcount = attach.getImplicitHydrogenCount();
            Set<IAtom> xatoms   = new HashSet<>();
            Set<IBond> xbonds   = new HashSet<>();
            Set<IBond> newbonds = new HashSet<>();
            xatoms.add(attach);

            Map<String,AdjacentGroup> adjGroupMap = new LinkedHashMap<>();

            for (final Sgroup sgroup : sgroupAdjs.getOrDefault(attach, Collections.emptyList())) {
                if (containsChargeChar(sgroup.getSubscript()))
                    continue;
                if (nonTerminal(sgroup))
                    continue;
                IBond xbond = sgroup.getBonds().iterator().next();
                xbonds.add(xbond);
                xatoms.addAll(sgroup.getAtoms());
                if (attach.getSymbol().length() == 1 &&
                    Character.isLowerCase(sgroup.getSubscript().charAt(0))) {
                    if (Elements.ofString(attach.getSymbol() + sgroup.getSubscript().charAt(0)) != Elements.Unknown)
                        continue collapse;
                }
                adjGroupMap.computeIfAbsent(sgroup.getSubscript(),
                                            k -> new AdjacentGroup(sgroup))
                         .add(sgroup);
            }


            for (IBond bond : mol.getConnectedBondsList(attach)) {
                if (!xbonds.contains(bond)) {
                    IAtom nbr = bond.getOther(attach);
                    // can only contract terminal bonds
                    if (mol.getConnectedBondsCount(nbr) == 1) {
                        if (nbr.getMassNumber() != null ||
                            (nbr.getFormalCharge() != null && nbr.getFormalCharge() != 0) ||
                             isNonMethylTerminalCarbon(nbr)) {
                            newbonds.add(bond);
                        } else if (nbr.getAtomicNumber() == 1) {
                            hcount++;
                            xatoms.add(nbr);
                        } else if (nbr.getAtomicNumber() > 0){
                            String symbol = newSymbol(nbr.getAtomicNumber(), nbr.getImplicitHydrogenCount(), false);
                            adjGroupMap.computeIfAbsent(symbol, k -> new AdjacentGroup(k, nbr)).add();
                            xatoms.add(nbr);
                        }
                    } else {
                        newbonds.add(bond);
                    }
                }
            }

            // too much contraction? keep the group that was repeated the most,
            // e.g. Ph-Sn(-Me)(-Me)(-Me) => Ph-SnMe3
            if (newbonds.size() < 1 && adjGroupMap.size() > 1 && !options.contains(Option.ALLOW_SINGLETON)) {
                AdjacentGroup bestMultiGroup = null;
                for (AdjacentGroup group : adjGroupMap.values()) {
                    if (group.count > 1 && (bestMultiGroup == null || group.count > bestMultiGroup.count))
                        bestMultiGroup = group;
                }
                if (bestMultiGroup != null) {
                    xatoms.clear();
                    xbonds.clear();
                    xatoms.add(attach);
                    for (Sgroup sgroup : bestMultiGroup.sgroups) {
                        xatoms.addAll(sgroup.getAtoms());
                        xbonds.addAll(sgroup.getBonds());
                    }
                    for (IBond bond : mol.getConnectedBondsList(attach))
                        if (!xbonds.contains(bond))
                            newbonds.add(bond);
                    adjGroupMap.clear();
                    adjGroupMap.put(bestMultiGroup.symbol, bestMultiGroup);
                }
            }

            // reject if no symbols
            // reject if no bonds (<1), except if all symbols are identical... (HashSet.size==1)
            // reject if more than 2 bonds
            if (adjGroupMap.isEmpty() ||
                newbonds.size() < 1 && !options.contains(Option.ALLOW_SINGLETON) ||
                newbonds.size() > 1 && !options.contains(Option.AUTO_CONTRACT_LINKERS) ||
                newbonds.size() > 2)
                continue;

            if (isCC(attach, xbonds, adjGroupMap))
                continue;

            // avoid contracting completely unless requested to
            if (newbonds.size() == 0 && !options.contains(Option.ALLOW_SINGLETON))
                continue;

            // create the symbol
            StringBuilder sb = new StringBuilder();
            String prev  = "{!no_match!}";

            List<AdjacentGroup> adjGroups = new ArrayList<>(adjGroupMap.values());
            Collections.sort(adjGroups);

            int first = 0;
            if (newbonds.size() == 0 && adjGroups.get(first).allCarbon) {
                AdjacentGroup group = adjGroups.get(first);
                appendGroup(sb, group.symbol, group.count, false);
                first++;
            }

            sb.append(newSymbol(attach.getAtomicNumber(), hcount,
                                newbonds.size() == 0 && first == 0));
            for (int i = first; i < adjGroups.size(); i++) {
                AdjacentGroup group = adjGroups.get(i);
                boolean useParen =
                        (group.count > 1 && !group.isTrivial) ||
                                !group.isTrivial ||
                                group.symbol.startsWith(prev) ||
                                !hasStandardValence(attach);
                boolean isLast = i + 1 == adjGroups.size();
                appendGroup(sb, group.symbol, group.count, useParen && !isLast);
                prev = group.symbol;
            }

            // remove existing
            for (AdjacentGroup group : adjGroups)
                newSgroups.removeAll(group.sgroups);

            // create new
            Sgroup newSgroup = new Sgroup();
            newSgroup.setType(SgroupType.CtabAbbreviation);
            newSgroup.setSubscript(sb.toString());
            for (IBond bond : newbonds)
                newSgroup.addBond(bond);
            for (IAtom atom : xatoms)
                newSgroup.addAtom(atom);

            newSgroups.add(newSgroup);
            usedAtoms.addAll(xatoms);
        }

        if (options.contains(Option.ALLOW_SINGLETON) &&
            options.contains(Option.AUTO_CONTRACT_TERMINAL)) {

            // recompute the adjacency and crossing bond info
            sgroupAdjs = getSgroupAdjacency(newSgroups);
            allCrossingBonds = getCrossingBonds(newSgroups);

            for (IBond bond : allCrossingBonds) {
                List<Sgroup> begAbbrs = sgroupAdjs.get(bond.getBegin());
                List<Sgroup> endAbbrs = sgroupAdjs.get(bond.getEnd());
                if (symmetricSgroups(begAbbrs, endAbbrs)) {
                    // Ph-Ph -> Ph2
                    String label = begAbbrs.get(0).getSubscript();
                    if (isTrivial(label))
                        begAbbrs.get(0).setSubscript(label + "2");
                    else
                        begAbbrs.get(0).setSubscript("(" + label + ")2");
                    begAbbrs.get(0).removeBond(bond);
                    for (IAtom atom : endAbbrs.get(0).getAtoms())
                        begAbbrs.get(0).addAtom(atom);
                    newSgroups.remove(endAbbrs.get(0));
                } else if (hasTrivial(begAbbrs, endAbbrs)) {
                    // e.g. Ph-MgCl => PhMgCl
                    // 'trivial' label goes in-front to avoid need to reverse
                    String begLabel = begAbbrs.get(0).getSubscript();
                    String endLabel = endAbbrs.get(0).getSubscript();
                    if (isTrivial(begLabel))
                        begAbbrs.get(0).setSubscript(begLabel + endLabel);
                    else if (isTrivial(endLabel))
                        begAbbrs.get(0).setSubscript(endLabel + begLabel);
                    else
                        throw new IllegalStateException();
                    begAbbrs.get(0).removeBond(bond);
                    for (IAtom atom : endAbbrs.get(0).getAtoms())
                        begAbbrs.get(0).addAtom(atom);
                    newSgroups.remove(endAbbrs.get(0));
                }
            }
        }

        return newSgroups;
    }

    private static boolean isNonMethylTerminalCarbon(IAtom nbr) {
        return nbr.getAtomicNumber() == IAtom.C && nbr.getImplicitHydrogenCount() != 3;
    }

    private static boolean hasStandardValence(IAtom attach) {
        switch (attach.getAtomicNumber()) {
            case IAtom.B:
            case IAtom.C:
            case IAtom.N:
            case IAtom.O:
            case IAtom.P:
            case IAtom.S:
            case IAtom.F:
            case IAtom.Cl:
            case IAtom.Br:
            case IAtom.I:
                return true;
            default:
                return false;
        }
    }

    private static boolean nonTerminal(Sgroup sgroup) {
        return sgroup.getBonds().size() != 1;
    }

    private static boolean isTrivial(String label) {
        int numCaps = 0;
        for (int i = 0; i < label.length(); i++) {
            if (Character.isUpperCase(label.charAt(i)))
                numCaps++;
            else if (Character.isDigit(label.charAt(i)))
                return false;
            else if (label.charAt(i) == '(')
                return false;
        }
        return numCaps == 1;
    }

    private static boolean symmetricSgroups(List<Sgroup> begAbbr, List<Sgroup> endAbbr) {
        return begAbbr != null && endAbbr != null &&
                begAbbr.size() == 1 && endAbbr.size() == 1 &&
                begAbbr.get(0) != endAbbr.get(0) &&
                begAbbr.get(0).getSubscript().equalsIgnoreCase(endAbbr.get(0).getSubscript());
    }

    private boolean hasTrivial(List<Sgroup> begAbbr, List<Sgroup> endAbbr) {
        return begAbbr != null && endAbbr != null &&
                begAbbr.size() == 1 && endAbbr.size() == 1 &&
                begAbbr.get(0) != endAbbr.get(0) &&
                (isTrivial(begAbbr.get(0).getSubscript()) || isTrivial(endAbbr.get(0).getSubscript()));
    }


    // Avoid CH2-Me => CH2Me and -C#CH -CCH
    private static boolean isCC(IAtom attach, Set<IBond> xbonds, Map<String, AdjacentGroup> nbrSymbols) {
        return attach.getAtomicNumber() == IAtom.C &&
                nbrSymbols.size() == 1 &&
                (nbrSymbols.values().iterator().next().symbol.equals("Me") ||
                 nbrSymbols.values().iterator().next().symbol.equals("CH"));
    }

    private int effectiveDegree(IAtom attach, Set<IBond> xbonds) {
        int degree = 0;
        for (IBond bond : attach.bonds()) {
            IAtom nbor = bond.getOther(attach);
            if (nbor.getBondCount() != 1 &&
                !xbonds.contains(bond))
                degree++;
        }
        return degree;
    }

    private Sgroup getAbbr(IAtomContainer part) throws CDKException {
        String label;
        String cansmi;
        if (part.getAtomCount() == 1) {
            IAtom atom = part.getAtom(0);
            label = getBasicElementSymbol(atom);
            if (label != null) {
                Sgroup sgroup = new Sgroup();
                sgroup.setType(SgroupType.CtabAbbreviation);
                sgroup.setSubscript(label);
                sgroup.addAtom(atom);
                return sgroup;
            }
        } else {
            cansmi = usmigen.create(part);
            label  = disconnectedAbbreviations.get(cansmi);
            if (label != null && !disabled.contains(label)) {
                Sgroup sgroup = new Sgroup();
                sgroup.setType(SgroupType.CtabAbbreviation);
                sgroup.setSubscript(label);
                for (IAtom atom : part.atoms())
                    sgroup.addAtom(atom);
                return sgroup;
            }
        }
        return null;
    }


    private boolean containsChargeChar(String str) {
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);
            if (c == '-' || c == '+')
                return true;
        }
        return false;
    }

    /**
     * Check if last char is a digit.
     */
    private boolean digitAtEnd(String str) {
        return Character.isDigit(str.charAt(str.length()-1));
    }

    private String newSymbol(int atomnum, int hcount, boolean prefix) {
        StringBuilder sb = new StringBuilder();
        Elements elem = Elements.ofNumber(atomnum);
        if (elem == Elements.Carbon && hcount == 3)
            return "Me";
        if (prefix && elem != Elements.Carbon) {
            if (hcount > 0) {
                sb.append('H');
                if (hcount > 1)
                    sb.append(hcount);
            }
            sb.append(elem.symbol());
        } else {
            sb.append(elem.symbol());
            if (hcount > 0) {
                sb.append('H');
                if (hcount > 1)
                    sb.append(hcount);
            }
        }
        return sb.toString();
    }

    private void appendGroup(StringBuilder sb, String group, int coef, boolean useParen) {
        if (coef <= 0 || group == null || group.isEmpty()) return;
        if (!useParen)
            useParen = coef > 1 && (!isTrivial(group) || digitAtEnd(group));
        if (useParen)
            sb.append('(');
        sb.append(group);
        if (useParen)
            sb.append(')');
        if (coef > 1)
            sb.append(coef);
    }

    /**
     * Generates and assigns abbreviations to a molecule. Abbreviations are first
     * generated with {@link #generate} and then applied to the molecule if it
     * is reasonable to do so. Currently, we count the number of ring/chain atoms
     * in/out of the contraction. If there are more atoms contracted than not
     * it is not applied.
     *
     * @param mol molecule
     * @return number of new abbreviations
     * @see #generate(IAtomContainer)
     */
    public int apply(final IAtomContainer mol) {
        List<Sgroup> newSgroups = generate(mol);
        List<Sgroup> sgroups = mol.getProperty(CDKConstants.CTAB_SGROUPS);

        if (sgroups == null)
            sgroups = new ArrayList<>();
        else
            sgroups = new ArrayList<>(sgroups);

        int numAtoms = mol.getAtomCount();
        int numRingAtoms = countRingAtoms(mol);
        int prev = sgroups.size();
        for (Sgroup sgroup : newSgroups) {
            if (shouldContract(sgroup, numAtoms, numRingAtoms))
                sgroups.add(sgroup);
        }
        mol.setProperty(CDKConstants.CTAB_SGROUPS, Collections.unmodifiableList(sgroups));
        return sgroups.size() - prev;
    }

    private static int countRingAtoms(IAtomContainer mol) {
        int numRingAtoms = 0;
        for (IAtom atom : mol.atoms())
            if (atom.isInRing())
                numRingAtoms++;
        return numRingAtoms;
    }

    private boolean shouldContract(Sgroup sgroup, int nAtoms, int nRingAtoms) {
        if (sgroup.getBonds().isEmpty())
            return true; // no crossing bonds, normally an agent etc
        int nAbbrRingAtoms  = 0;
        for (IAtom atom : sgroup.getAtoms()) {
            if (atom.isInRing())
                nAbbrRingAtoms++;;
        }
        int nOtherRingAtoms = nRingAtoms - nAbbrRingAtoms;
        if (nAbbrRingAtoms != 0)
            return nOtherRingAtoms > nAbbrRingAtoms;
        int nOtherAtoms = nAtoms - sgroup.getAtoms().size();
        return nOtherAtoms > sgroup.getAtoms().size();
    }

    /**
     * Make a query atom that matches atomic number, h count, valence, and
     * connectivity. This effectively provides an exact match for that atom
     * type.
     *
     * @param mol  molecule
     * @param atom atom of molecule
     * @return the query atom (null if attachment point)
     */
    private IQueryAtom matchExact(final IAtomContainer mol, final IAtom atom) {
        final IChemObjectBuilder bldr = atom.getBuilder();

        int elem = atom.getAtomicNumber();

        // attach atom skipped
        if (elem == 0)
            return null;

        int hcnt = atom.getImplicitHydrogenCount();
        int val = hcnt;
        int con = hcnt;

        for (IBond bond : mol.getConnectedBondsList(atom)) {
            val += bond.getOrder().numeric();
            con++;
            if (bond.getOther(atom).getAtomicNumber() == 1)
                hcnt++;
        }

        Expr expr = new Expr(Expr.Type.ELEMENT, elem)
                .and(new Expr(Expr.Type.TOTAL_DEGREE, con))
                .and(new Expr(Expr.Type.TOTAL_H_COUNT, hcnt))
                .and(new Expr(Expr.Type.VALENCE, val));
        return new QueryAtom(expr);
    }

    /**
     * Internal - create a query atom container that exactly matches the molecule provided.
     * Similar to {@link org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator}
     * but we can't access SMARTS query classes from that module (cdk-isomorphism).
     *
     * @param mol molecule
     * @return query container
     * @see org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator
     */
    private IQueryAtomContainer matchExact(IAtomContainer mol) {
        final IChemObjectBuilder bldr = mol.getBuilder();
        final IQueryAtomContainer qry = new QueryAtomContainer(mol.getBuilder());
        final Map<IAtom, IAtom> atmmap = new HashMap<>();

        for (IAtom atom : mol.atoms()) {
            IAtom qatom = matchExact(mol, atom);
            if (qatom != null) {
                atmmap.put(atom, qatom);
                qry.addAtom(qatom);
            }
        }

        for (IBond bond : mol.bonds()) {
            final IAtom beg = atmmap.get(bond.getBegin());
            final IAtom end = atmmap.get(bond.getEnd());

            // attach bond skipped
            if (beg == null || end == null)
                continue;

            IQueryBond qbond = new QueryBond(beg, end, Expr.Type.TRUE);
            qry.addBond(qbond);
        }

        return qry;
    }

    private boolean addDisconnectedAbbreviation(IAtomContainer mol, String label) {
        try {
            String cansmi = SmilesGenerator.unique().create(mol);
            disconnectedAbbreviations.put(cansmi, label);
            labels.add(label);
            return true;
        } catch (CDKException e) {
            return false;
        }
    }

    private boolean addConnectedAbbreviation(IAtomContainer mol, String label) {
        try {
            connectedAbbreviations.put(usmigen.create(mol),
                                       label);
            labels.add(label);
            return true;
        } catch (CDKException e) {
            return false;
        }
    }

    /**
     * Convenience method to add an abbreviation from a SMILES string.
     *
     * @param line the smiles to add with a title (the label)
     * @return the abbreviation was added, will be false if no title supplied
     * @throws InvalidSmilesException the SMILES was not valid
     */
    public boolean add(String line) throws InvalidSmilesException {
        return add(smipar.parseSmiles(line), getSmilesSuffix(line));
    }

    /**
     * Add an abbreviation to the factory. Abbreviations can be of various flavour based
     * on the number of attachments:
     * <p>
     *
     * <b>Detached</b> - zero attachments, the abbreviation covers the whole structure (e.g. THF)
     * <b>Terminal</b> - one attachment, covers substituents (e.g. Ph for Phenyl)
     * <b>Linker</b> - [NOT SUPPORTED YET] two attachments, covers long repeated chains (e.g. PEG4)
     * <p>
     * Attachment points (if present) must be specified with zero element atoms.
     * <pre>
     * *c1ccccc1 Ph
     * *OC(=O)C OAc
     * </pre>
     *
     * @param mol   the fragment to abbreviate
     * @param label the label of the fragment
     * @return the abbreviation was added
     */
    public boolean add(IAtomContainer mol, String label) {

        if (label == null || label.isEmpty())
            return false;

        // required invariants and check for number of attachment points
        int numAttach = 0;
        for (IAtom atom : mol.atoms()) {
            if (atom.getImplicitHydrogenCount() == null || atom.getAtomicNumber() == null)
                throw new IllegalArgumentException("Implicit hydrogen count or atomic number is null");
            if (atom.getAtomicNumber() == 0)
                numAttach++;
        }

        switch (numAttach) {
            case 0:
                return addDisconnectedAbbreviation(mol, label);
            case 1:
                return addConnectedAbbreviation(mol, label);
            default:
                // not-supported yet - update JavaDoc if added
                return false;
        }
    }

    private static String getSmilesSuffix(String line) {
        final int last = line.length() - 1;
        for (int i = 0; i < last; i++)
            if (line.charAt(i) == ' ' || line.charAt(i) == '\t')
                return line.substring(i + 1).trim();
        return "";
    }

    private static String getBasicElementSymbol(IAtom atom) {
        if (atom.getFormalCharge() != null && atom.getFormalCharge() != 0)
            return null;
        if (atom.getMassNumber() != null && atom.getMassNumber() != 0)
            return null;
        if (atom.getAtomicNumber() == null || atom.getAtomicNumber() < 1)
            return null;
        Integer hcnt = atom.getImplicitHydrogenCount();
        if (hcnt == null) return null;
        Elements elem = Elements.ofNumber(atom.getAtomicNumber());
        final String hsym = (hcnt > 0) ? ((hcnt > 1) ? ("H" + hcnt) : "H") : "";
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
                return hsym + elem.symbol();
            default:
                return elem.symbol() + hsym;
        }
    }

    private int loadSmiles(final InputStream in) throws IOException {
        int count = 0;
        try (BufferedReader brdr = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = brdr.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#')
                    continue;
                try {
                    if (add(line))
                        count++;
                } catch (InvalidSmilesException e) {
                    LoggingToolFactory.createLoggingTool(Abbreviations.class)
                                      .warn("Ignored Invalid SMILES", e);
                }
            }
        }
        return count;
    }

    /**
     * Load a set of abbreviations from a classpath resource or file in SMILES format. The title is seperated
     * by a space.
     *
     * <pre>
     * *c1ccccc1 Ph
     * *c1ccccc1 OAc
     * </pre>
     * <p>
     * Available:
     * <dl>
     * <dt>obabel_superatoms.smi</dt>
     * <dd><a href="https://www.github.com/openbabel/superatoms"><code>https://www.github.com/openbabel/superatoms</code></a></dd>
     * </dl>
     *
     * @param path classpath or filesystem path to a SMILES file
     * @return the number of loaded abbreviation
     * @throws IOException
     */
    public int loadFromFile(final String path) throws IOException {
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(path);
            if (in != null)
                return loadSmiles(in);
            File file = new File(path);
            if (file.exists() && file.canRead())
                return loadSmiles(new FileInputStream(file));
        } finally {
            if (in != null)
                in.close();
        }
        return 0;
    }
}
