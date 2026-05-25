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
import org.openscience.cdk.aromaticity.Aromaticity;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IElement;
import org.openscience.cdk.isomorphism.Mappings;
import org.openscience.cdk.isomorphism.Pattern;
import org.openscience.cdk.isomorphism.matchers.Expr;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.smarts.Smarts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TautSubSearch1 extends Pattern  {

    private IAtomContainer query;
    private Pattern recheck;
    private List<MatchOp> plan = new ArrayList<>();
    private IAtom[] amap;
    private IBond[] bmap;
    private boolean fail;
    private boolean[] avisit;
    private int flags;
    private static final int RING_REQUIRED = 0x1;
    private static final int AROM_REQUIRED = 0x3;

    public TautSubSearch1(IChemObjectBuilder builder, String smarts) {
        IAtomContainer query = builder.newAtomContainer();
        if (!Smarts.parse(query, smarts))
            throw new IllegalArgumentException("Invalid SMARTS\n" + Smarts.parseToResult(query, smarts)
                                                                          .displayErrorLocation());

        IAtomContainer queryRelaxed = builder.newAtomContainer();
        boolean changed = false;
        for (IAtom atom : query.atoms()) {
            QueryAtom qatom = new QueryAtom(builder);
            if (updateAtomExpr(atom, qatom))
                changed = true;
            queryRelaxed.addAtom(qatom);
        }
        for (IBond bond : query.bonds()) {
            QueryBond qbond = new QueryBond(builder);
            if (updateBondExpr(bond, qbond))
                changed = true;
            qbond.setAtoms(new IAtom[]{
                    queryRelaxed.getAtom(bond.getBegin().getIndex()),
                    queryRelaxed.getAtom(bond.getEnd().getIndex()),
            });
            queryRelaxed.addBond(qbond);
        }

        flags = getRequiredPrep(query);

        this.query = queryRelaxed;
        this.amap = new IAtom[query.getAtomCount()];
        this.avisit = new boolean[Math.max(query.getAtomCount(), 128)];
        plan.add(new MatchOp(MatchOpType.Init));
        for (IAtom atom : queryRelaxed.atoms()) {
            if (!avisit[atom.getIndex()])
                buildPlan(atom, null);
        }
        if (changed) {
            plan.add(new MatchOp(MatchOpType.TautCheck));
            recheck = new SubSearch(builder, smarts);
        }
        plan.add(new MatchOp(MatchOpType.Done));


        if (System.getProperty("DEBUG") != null) {
            System.err.println("QUERY 1: " + Smarts.generate(queryRelaxed));
            if (changed)
                System.err.println("QUERY 2: " + Smarts.generate(query));
        }
    }

    private static boolean updateBondExpr(IBond bond, QueryBond qbond) {
        Expr expr = ((QueryBond) BondRef.deref(bond)).getExpression();
        Expr relaxed = tautBondExpr(expr);
        qbond.setExpression(relaxed);
        return !expr.equals(relaxed);
    }

    private static boolean updateAtomExpr(IAtom atom, QueryAtom qatom) {
        Expr expr = ((QueryAtom) AtomRef.deref(atom)).getExpression();
        Expr relaxed = tautAtomExpr(expr);
        qatom.setExpression(relaxed);
        return !expr.equals(relaxed);
    }

    private static int getRequiredPrep(IAtomContainer query) {
        int flags = 0;
        for (IAtom atom : query.atoms())
            if (atom instanceof IQueryAtom)
                flags |= getRequiredPrep(((QueryAtom) AtomRef.deref(atom)).getExpression());
        for (IBond bond : query.bonds()) {
            if (bond instanceof IQueryBond)
                flags |= getRequiredPrep(((QueryBond) BondRef.deref(bond)).getExpression());
        }
        return flags;
    }

    private static int getRequiredPrep(Expr expr) {
        switch (expr.type()) {
            case RING_COUNT:
            case RING_BOND_COUNT:
            case RING_SMALLEST:
            case RING_SIZE:
            case IS_IN_RING:
            case IS_IN_CHAIN:
                return RING_REQUIRED;
            case ALIPHATIC_ELEMENT:
                // Chlorine is always aliphatic... and the parser knows this
                // but an expression may have been manually constructed
                switch (expr.value()) {
                    case IElement.Wildcard: // ??
                    case IElement.B: // not Daylight but prep still
                    case IElement.C:
                    case IElement.N:
                    case IElement.O:
                    case IElement.P:
                    case IElement.S:
                    case IElement.Si:
                    case IElement.Se:
                    case IElement.As:
                    case IElement.Te: // not Daylight but prep still
                        return AROM_REQUIRED;
                    default:
                        return 0;
                }
            case IS_ALIPHATIC:
            case IS_AROMATIC:
            case AROMATIC_ELEMENT:
            case ALIPHATIC_ORDER:
            case SINGLE_OR_AROMATIC:
            case DOUBLE_OR_AROMATIC:
            case SINGLE_OR_DOUBLE:
            case HAS_ALIPHATIC_HETERO_SUBSTITUENT:
            case HAS_HETERO_SUBSTITUENT:
            case IS_ALIPHATIC_HETERO:
            case ALIPHATIC_HETERO_SUBSTITUENT_COUNT:
                return AROM_REQUIRED;
            case RECURSIVE:
                return getRequiredPrep(expr.subquery());
            case AND:
            case OR:
                return getRequiredPrep(expr.left()) |
                        getRequiredPrep(expr.right());
            case NOT:
                return getRequiredPrep(expr.left());
            case TRUE:
            case FALSE:
            case IS_HETERO:
            case HAS_IMPLICIT_HYDROGEN:
            case HAS_ISOTOPE:
            case HAS_UNSPEC_ISOTOPE:
            case UNSATURATED:
            case ELEMENT:
            case IMPL_H_COUNT:
            case TOTAL_H_COUNT:
            case DEGREE:
            case TOTAL_DEGREE:
            case HEAVY_DEGREE:
            case VALENCE:
            case ISOTOPE:
            case FORMAL_CHARGE:
            case HYBRIDISATION_NUMBER:
            case HETERO_SUBSTITUENT_COUNT:
            case PERIODIC_GROUP:
            case INSATURATION:
            case REACTION_ROLE:
            case STEREOCHEMISTRY:
            case ORDER:
                return 0;
            default:
                throw new IllegalStateException("SmartPattern needs updating to know if: " + expr + " needs ring/arom flags?");
        }
    }

    private static Expr tautAtomExpr(Expr expr) {
        switch (expr.type()) {
            case OR:
                return tautAtomExpr(expr.left()).or(tautAtomExpr(expr.right()));
            case AND:
                return tautAtomExpr(expr.left()).and(tautAtomExpr(expr.right()));
            case NOT:
                Expr subexpr = tautAtomExpr(expr.left());
                return subexpr.type() != Expr.Type.TRUE ? subexpr.negate() : new Expr();
            case AROMATIC_ELEMENT:
            case ALIPHATIC_ELEMENT:
                switch (expr.value()) {
                    case IAtom.C:
                    case IAtom.N:
                    case IAtom.O:
                    case IAtom.S:
                    case IAtom.Te:
                        return new Expr(Expr.Type.ELEMENT, expr.value());
                    default:
                        return expr;
                }
            case TOTAL_DEGREE:
            case TOTAL_H_COUNT:
            case IMPL_H_COUNT:
            case HAS_IMPLICIT_HYDROGEN:
            case IS_ALIPHATIC:
            case IS_AROMATIC:
            case UNSATURATED:
            case HAS_ALIPHATIC_HETERO_SUBSTITUENT:
            case INSATURATION:
                return new Expr();
            default:
                return new Expr(expr);
        }
    }

    private static Expr tautBondExpr(Expr expr) {
        switch (expr.type()) {
            case OR:
                return tautBondExpr(expr.left()).or(tautBondExpr(expr.right()));
            case AND:
                return tautBondExpr(expr.left()).and(tautBondExpr(expr.right()));
            case NOT:
                Expr subexpr = tautBondExpr(expr.left());
                return subexpr.type() != Expr.Type.TRUE ? subexpr.negate() : new Expr();
            case ALIPHATIC_ORDER:
            case ORDER:
                if (expr.value() < 3)
                    return new Expr();
                return new Expr(expr);
            case IS_ALIPHATIC:
            case IS_AROMATIC:
            case SINGLE_OR_AROMATIC:
            case SINGLE_OR_DOUBLE:
            case DOUBLE_OR_AROMATIC:
                return new Expr(Expr.Type.TRUE);
            default:
                return new Expr(expr);
        }
    }

    private static int tautBondOrder(Expr expr) {
        int lft, rgt;
        switch (expr.type()) {
            case OR:
                lft = tautBondOrder(expr.left());
                rgt = tautBondOrder(expr.right());
                if (lft == rgt)
                    return rgt;
                return -2;
            case AND:
                lft = tautBondOrder(expr.left());
                rgt = tautBondOrder(expr.right());
                if (lft == -1 || lft == rgt)
                    return rgt;
                if (rgt == -1)
                    return lft;
                return -2;
            case NOT:
                return -2;
            case ALIPHATIC_ORDER:
            case ORDER:
                return expr.value();
            case IS_ALIPHATIC:
            case IS_IN_RING:
            case IS_IN_CHAIN:
            case IS_AROMATIC:
                return -1;
            case SINGLE_OR_AROMATIC:
            case SINGLE_OR_DOUBLE:
            case DOUBLE_OR_AROMATIC:
            default:
                return -2;
        }
    }

    private void buildPlan(IAtom atom, IBond prev) {
        avisit[atom.getIndex()] = true;
        if (prev == null) {
            plan.add(new MatchOp(plan.size() <= 1 ? MatchOpType.FirstAtom : MatchOpType.NewComponent,
                                 atom.getIndex(), ((QueryAtom) AtomRef.deref(atom)).getExpression(),
                                 atom.getBondCount()));
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
                                     null, -1));
            } else {
                plan.add(new MatchOp(MatchOpType.GrowBond,
                                     atom.getIndex(),
                                     nbor.getIndex(),
                                     ((QueryBond) BondRef.deref(bond)).getExpression(),
                                     ((QueryAtom) AtomRef.deref(nbor)).getExpression(),
                                     nbor.getBondCount()));
                buildPlan(nbor, bond);
            }
        }
    }

    private boolean matches(IAtomContainer mol, int idx) {
        MatchOp op = plan.get(idx);
        switch (op.type) {
            case Init:
                // clear tautomer state
                fail = false;
                return matches(mol, idx + 1);
            case FirstAtom:
                boolean init = false;
                for (IAtom atom : mol.atoms()) {
                    if (atom.getBondCount() < op.deg || !op.aexpr.matches(atom))
                        continue;
                    if (!init) {
                        clearVisit(mol);
                    }
                    init = true;
                    avisit[atom.getIndex()] = true;
                    amap[op.beg] = atom;
                    if (matches(mol, idx + 1))
                        return true;
                    avisit[atom.getIndex()] = false;
                    if (fail)
                        return false;
                }
                break;
            case NewComponent:
                for (IAtom atom : mol.atoms()) {
                    if (avisit[atom.getIndex()] || !op.aexpr.matches(atom))
                        continue;
                    avisit[atom.getIndex()] = true;
                    amap[op.beg] = atom;
                    if (matches(mol, idx + 1))
                        return true;
                    avisit[atom.getIndex()] = false;
                    if (fail)
                        return false;
                }
                break;
            case GrowBond:
                for (IBond bond : amap[op.beg].bonds()) {
                    if (!op.bexpr.matches(bond))
                        continue;
                    IAtom nbor = bond.getOther(amap[op.beg]);
                    if (avisit[nbor.getIndex()])
                        continue;
                    if (nbor.getBondCount() < op.deg || !op.aexpr.matches(nbor))
                        continue;
                    avisit[nbor.getIndex()] = true;
                    amap[op.end] = nbor;
                    if (matches(mol, idx + 1))
                        return true;
                    avisit[nbor.getIndex()] = false;
                    if (fail)
                        return false;
                }
                break;
            case CloseRing:
                IBond bond = amap[op.beg].getBond(amap[op.end]);
                if (bond == null || !op.bexpr.matches(bond))
                    return false;
                return matches(mol, idx + 1);
            case TautCheck:
                if (fail)
                    return false;
                try {
                    // if RING_REQUIRED == 0 (first query) we need to do it now
                    // so aromaticity works correctly
                    if ((flags&RING_REQUIRED) == 0)
                        Cycles.markRingAtomsAndBonds(mol);
                    for (IAtomContainer taut : Tautomers.hetero(mol)) {
                        Aromaticity.apply(Aromaticity.Model.Daylight, taut);
                        if (recheck.matches(taut))
                            return true;
                    }
                    fail = true;
                } catch (Exception ex) {
                    System.err.println("Fatal: " + ex.getMessage());
                }
                return false;
            case Done:
                return true;
        }

        return false;
    }

    private void clearVisit(IAtomContainer mol) {
        if (mol.getAtomCount() > avisit.length)
            avisit = new boolean[mol.getAtomCount()];
        else
            Arrays.fill(avisit, 0, mol.getAtomCount(), false);
    }


    public enum MatchOpType {
        Init,
        FirstAtom,
        NewComponent,
        GrowBond,
        CloseRing,
        TautCheck,
        Done
    }

    private static final class MatchOp {
        private final MatchOpType type;
        private final Expr bexpr;
        private final Expr aexpr;
        private final int beg;
        private final int end;
        private final int deg;
        private IBond.Order order;

        private MatchOp(MatchOpType type) {
            this.type = type;
            this.beg = -1;
            this.end = -1;
            this.bexpr = null;
            this.aexpr = null;
            this.deg = -1;
        }

        private MatchOp(MatchOpType type, int beg, Expr expr, int deg) {
            this.type = type;
            this.beg = beg;
            this.end = -1;
            this.bexpr = null;
            this.aexpr = expr;
            this.deg = deg;
        }

        private MatchOp(MatchOpType type, int beg, int eng, Expr bexpr, Expr aexpr, int deg) {
            this.type = type;
            this.beg = beg;
            this.end = eng;
            this.bexpr = bexpr;
            this.aexpr = aexpr;
            this.deg = deg;
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
                default:
                    return type.toString();
            }
        }
    }

    @Override
    public boolean matches(IAtomContainer mol) {
        if ((flags&RING_REQUIRED) != 0)
            Cycles.markRingAtomsAndBonds(mol);
        return matches(mol, 0);
    }

    @Override
    public int[] match(IAtomContainer target) {
        if (!matches(target))
            return new int[0];
        int[] match = new int[this.amap.length];
        for (int i=0; i<match.length; i++)
            match[i] = this.amap[i].getIndex();
        return match;
    }

    @Override
    public Mappings matchAll(IAtomContainer target) {
        return null;
    }
}
