/*
 * Copyright (C) 2024 John Mayfield
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */

package org.openscience.cdk.tautomer;

import org.openscience.cdk.AtomRef;
import org.openscience.cdk.BondRef;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.smarts.Smarts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class SubSearch extends Pattern {

    private IAtomContainer query;
    private TautState state;
    private List<MatchOp> plan = new ArrayList<>();
    private IAtom[] amap;
    private boolean[] avisit;

    public SubSearch(IChemObjectBuilder builder, String smarts) {
        this.query = builder.newAtomContainer();
        if (!Smarts.parse(query, smarts))
            throw new IllegalArgumentException("Invalid SMARTS!");
        amap = new IAtom[query.getAtomCount()];
        avisit = new boolean[Math.max(query.getAtomCount(),128)];
        for (IAtom atom : query.atoms()) {
            if (!avisit[atom.getIndex()])
                buildPlan(atom, null);
        }
//        for (MatchOp op : plan)
//            System.err.println(op);
    }


    private void buildPlan(IAtom atom, IBond prev) {
        avisit[atom.getIndex()] = true;
        if (prev == null) {
            plan.add(new MatchOp(plan.isEmpty() ? MatchOpType.FirstAtom : MatchOpType.NewComponent,
                                 atom.getIndex(), ((QueryAtom) AtomRef.deref(atom)).getExpression()));
        }
        for (IBond bond : atom.bonds()) {
            if (prev == bond)
                continue;
            IAtom nbor = bond.getOther(atom);
            if (avisit[nbor.getIndex()]) {
                if (atom.getIndex() < nbor.getIndex())
                    continue;
                plan.add(new MatchOp(MatchOpType.CloseRing,
                                     atom.getIndex(),
                                     nbor.getIndex(),
                                     ((QueryBond) BondRef.deref(bond)).getExpression(),
                                     null));
            } else {
                plan.add(new MatchOp(MatchOpType.GrowBond,
                                     atom.getIndex(),
                                     nbor.getIndex(),
                                     ((QueryBond) BondRef.deref(bond)).getExpression(),
                                     ((QueryAtom) AtomRef.deref(nbor)).getExpression()));
                buildPlan(nbor, bond);
            }
        }
    }

    public boolean matches(IAtomContainer mol) {
        return matches(mol, 0);
    }

    private boolean matches(IAtomContainer mol, int idx) {
        if (idx == plan.size())
            return true;
        MatchOp op = plan.get(idx);
        switch (op.type) {
            case FirstAtom:
                boolean init = false;
                for (IAtom atom : mol.atoms()) {
                    if (!op.aexpr.matches(atom))
                        continue;
                    if (!init) {
                        clearVisit(mol);
                    }
                    init = true;
                    avisit[atom.getIndex()] = true;
                    amap[op.beg] = atom;
                    if (matches(mol, idx+1))
                        return true;
                    avisit[atom.getIndex()] = false;
                }
                break;
            case NewComponent:
                for (IAtom atom : mol.atoms()) {
                    if (avisit[atom.getIndex()] || !op.aexpr.matches(atom))
                        continue;
                    avisit[atom.getIndex()] = true;
                    amap[op.beg] = atom;
                    if (matches(mol, idx+1))
                        return true;
                    avisit[atom.getIndex()] = false;
                }
                break;
            case GrowBond:
                for (IBond bond : amap[op.beg].bonds()) {
                    if (!op.bexpr.matches(bond))
                        continue;
                    IAtom nbor = bond.getOther(amap[op.beg]);
                    if (avisit[nbor.getIndex()])
                        continue;
                    if (!op.aexpr.matches(nbor))
                        continue;
                    avisit[nbor.getIndex()] = true;
                    amap[op.end] = nbor;
                    if (matches(mol, idx+1))
                        return true;
                    avisit[nbor.getIndex()] = false;
                }
                break;
            case CloseRing:
                IBond bond = amap[op.beg].getBond(amap[op.end]);
                if (bond == null || !op.bexpr.matches(bond))
                    return false;
                return matches(mol, idx+1);
        }

        return false;
    }

    private void clearVisit(IAtomContainer mol) {
        if (mol.getAtomCount() > avisit.length)
            avisit = new boolean[mol.getAtomCount()];
        else
            Arrays.fill(avisit, 0, mol.getAtomCount(), false);
    }

    @Override
    public int[] match(IAtomContainer target) {
        int[] match = new int[this.amap.length];
        for (int i=0; i<match.length; i++)
            match[i] = this.amap[i].getIndex();
        return match;
    }

    @Override
    public Mappings matchAll(IAtomContainer target) {
        return null;
    }

    public enum MatchOpType {
        FirstAtom,
        NewComponent,
        GrowBond,
        CloseRing
    }

    private final class MatchOp {
        private final MatchOpType type;
        private final Expr bexpr;
        private final Expr aexpr;
        private final int beg;
        private final int end;

        private MatchOp(MatchOpType type, int beg, Expr expr) {
            this.type = type;
            this.beg = beg;
            this.end = -1;
            this.bexpr = null;
            this.aexpr = expr;
        }

        private MatchOp(MatchOpType type, int beg, int eng, Expr bexpr, Expr aexpr) {
            this.type = type;
            this.beg = beg;
            this.end = eng;
            this.bexpr = bexpr;
            this.aexpr = aexpr;
        }

        @Override
        public String toString() {
            switch (type) {
                case FirstAtom:
                case NewComponent:
                    return type + " " + beg + " " + aexpr;
                case GrowBond:
                    return type + " " + beg + "-" + end + " bexpr:" + bexpr + " aexpr:" + aexpr;
                case CloseRing:
                    return type + " " + beg + "-" + end + " bexpr:" + bexpr;
            }
            return null;
        }
    }
}
