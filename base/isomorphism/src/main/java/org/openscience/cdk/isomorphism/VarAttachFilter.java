/*
 * Copyright (c) 2025 NextMove Software Ltd
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
package org.openscience.cdk.isomorphism;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.sgroup.Sgroup;
import org.openscience.cdk.sgroup.SgroupType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This filter is used to filter and post-process mappings from substructure
 * searches, it checks variable attachment (aka positional variation) bonds.
 * <br/>
 * We match the subgraph skipping any '*' variable attachment atoms, these will
 * be left as "UNMAPPED (-1)" in our atom mapping bijection. This class then
 * finds solutions for the variable bonds and updates the mapping. This means
 * downstream filters for stereochemistry/parts etc work correctly as the '*'
 * atom is used as a placeholder for where it attaches.
 *
 * @author John Mayfield
 */
final class VarAttachFilter implements Predicate<int[]> {

    private static final class VarBond {
        private final Set<Integer> begIdxs;
        private final Set<Integer> endIdxs;
        private final IQueryBond qbond;
        private final int begAttachIdx;
        private final int endAttachIdx;

        public VarBond(Set<Integer> begIdxs,
                       Set<Integer> endAtoms,
                       IQueryBond qbond,
                       int begAttachIdx, int endAttachIdx) {
            this.begIdxs = begIdxs;
            this.endIdxs = endAtoms;
            this.qbond = qbond;
            this.begAttachIdx = begAttachIdx;
            this.endAttachIdx = endAttachIdx;
        }

        public VarBond(Integer begAtom,
                       Set<Integer> endAtoms,
                       IQueryBond qbond,
                       int endAttachIdx) {
            this(Collections.singleton(begAtom), endAtoms,
                 qbond, begAtom, endAttachIdx);
        }

        @Override
        public String toString() {
            return "VarBond{" +
                   "begAtoms=" + begIdxs + "@" + begAttachIdx +
                   ", endAtoms=" + endIdxs + "@" + endAttachIdx +
                   '}';
        }
    }

    private final IAtomContainer query;
    private final IAtomContainer mol;
    private List<VarBond> vbonds = null;

    VarAttachFilter(IAtomContainer query, IAtomContainer mol) {
        this.query = query;
        this.mol = mol;
    }

    /**
     * This functions scans a molecule and generate the {@link VarBond} entries
     * which need to be tested.
     *
     * @param query the query molecule
     * @return the var bonds to check
     */
    private static List<VarBond> indexVarBonds(IAtomContainer query) {
        List<Sgroup> sgroups = query.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return Collections.emptyList();

        Map<IAtom, Set<Integer>> map = new HashMap<>();
        Set<IBond> bonds = new HashSet<>();

        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() != SgroupType.ExtMulticenter)
                continue;

            // the Sgroups has the varIdxs to attach to AND the '*' atom, we know
            // which is which as one is in the 'bond' (should only be one bond)
            if (sgroup.getBonds().size() != 1)
                continue;

            IAtom attach = null;
            IBond bond = sgroup.getBonds().iterator().next();
            Set<Integer> varIdxs = new HashSet<>();

            if (sgroup.getAtoms().contains(bond.getBegin())) {
                attach = bond.getBegin();
            } else if (sgroup.getAtoms().contains(bond.getEnd())) {
                attach = bond.getEnd();
            }
            for (IAtom atom : sgroup.getAtoms()) {
                if (!atom.equals(attach))
                    varIdxs.add(query.indexOf(atom));
            }

            map.put(attach, varIdxs);
            bonds.add(bond);
        }

        List<VarBond> result = new ArrayList<>();
        for (IBond bond : bonds) {
            if (map.containsKey(bond.getBegin())) {
                if (map.containsKey(bond.getEnd())) {
                    result.add(new VarBond(map.get(bond.getBegin()),
                                           map.get(bond.getEnd()),
                                           makeQueryBond(bond),
                                           query.indexOf(bond.getBegin()),
                                           query.indexOf(bond.getEnd())));
                } else {
                    result.add(new VarBond(query.indexOf(bond.getEnd()),
                                           map.get(bond.getBegin()),
                                           makeQueryBond(bond),
                                           query.indexOf(bond.getBegin())));
                }
            } else if (map.containsKey(bond.getEnd())) {
                result.add(new VarBond(query.indexOf(bond.getBegin()),
                                       map.get(bond.getEnd()),
                                       makeQueryBond(bond),
                                       query.indexOf(bond.getEnd())));
            }
        }

        return result;
    }

    private static IQueryBond makeQueryBond(IBond bond) {
        // we expect it to be a query bond, but in future
        // may be useful to convert non query bonds to a
        // query equivalent
        return (IQueryBond)bond;
    }

    @Override
    public boolean test(int[] mapping) {
        if (vbonds == null)
            vbonds = indexVarBonds(query);

        // we sometimes need to check a bond visit in case multiple var-bonds
        // overlap, we assume the input query doesn't use a varbond to atoms
        // which are already bonded
        Set<IBond> bondVisit = new HashSet<>();

        // the predicate is valid if all the VarBonds can be mapped
        // successfully
        for (VarBond vbond : vbonds) {
            boolean found = false;

            // for each begAtom we see if it has a bond to any of the endAtoms!
            outer:
            for (Integer begIdx : vbond.begIdxs) {
                IAtom beg = mol.getAtom(mapping[begIdx]);

                for (IBond bond : mol.getConnectedBondsList(beg)) {
                    if (!vbond.qbond.matches(bond))
                        continue;
                    IAtom end = bond.getOther(beg);
                    for (Integer endIdx : vbond.endIdxs) {
                        if (mapping[endIdx] == mol.indexOf(end) && bondVisit.add(bond)) {
                            mapping[vbond.begAttachIdx] = mol.indexOf(beg);
                            mapping[vbond.endAttachIdx] = mol.indexOf(end);
                            found = true;
                            break outer;
                        }
                    }
                }
            }

            if (!found)
                return false;
        }

        return true;
    }

    /**
     * Utility to check if there is a variable attachment in a molecule, stored
     * as a Sgroup.
     *
     * @param query the query molecule to check
     * @return it has multicenter/variable attachments
     */
    static boolean hasVariableAttachment(IAtomContainer query) {
        List<Sgroup> sgroups = query.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return false;
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() == SgroupType.ExtMulticenter)
                return true;
        }
        return false;
    }

    /**
     * Utility to get the variable attachment atom end points.
     *
     * @param query the query molecule to check
     * @return it has multicenter/variable attachments
     */
    static Set<IAtom> getAttachAtoms(IAtomContainer query) {
        List<Sgroup> sgroups = query.getProperty(CDKConstants.CTAB_SGROUPS);
        if (sgroups == null)
            return Collections.emptySet();
        Set<IAtom> attach = new HashSet<>();
        for (Sgroup sgroup : sgroups) {
            if (sgroup.getType() != SgroupType.ExtMulticenter)
                continue;

            // the Sgroups has the atoms to attach to AND the '*' atom, we know
            // which is which as one is in the 'bond' (should only be one bond)
            for (IBond bond : sgroup.getBonds()) {
                if (sgroup.getAtoms().contains(bond.getBegin())) {
                    attach.add(bond.getBegin());
                } else if (sgroup.getAtoms().contains(bond.getEnd())) {
                    attach.add(bond.getEnd());
                }
            }
        }
        return attach;
    }
}
