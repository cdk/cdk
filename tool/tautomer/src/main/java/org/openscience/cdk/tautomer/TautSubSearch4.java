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
import org.openscience.cdk.interfaces.IChemObject;
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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TautSubSearch4 extends Pattern {

    private IAtomContainer query;
    private List<MatchOp> plan = new ArrayList<>();
    private IAtom[] amap;
    private IBond[] bmap;
    private boolean[] avisit;
    private int flags;
    private static final int RING_REQUIRED = 0x1;
    private static final int AROM_REQUIRED = 0x3;
    private boolean first = false;
    private Map<Integer,IBond.Order> bondOrder = new HashMap<>();
    private Map<Map<Integer,IBond.Order>,List<TautStore>> cache = new HashMap<>();
    private TautStore baseTaut;
    private Tautomers.Role[] roles;

    public TautSubSearch4(IChemObjectBuilder builder, String smarts) {
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
            ExprBondOrder bord = ExprBondOrder.get(bond);
            if (bord == ExprBondOrder.Single) {
                bondOrder.put(bond.getIndex(), IBond.Order.SINGLE);
            } else if (bord == ExprBondOrder.Double) {
                bondOrder.put(bond.getIndex(), IBond.Order.DOUBLE);
            }
        }

        flags = getRequiredPrep(query);

        this.query = query;
        this.amap = new IAtom[query.getAtomCount()];
        this.bmap = new IBond[query.getBondCount()];
        this.avisit = new boolean[Math.max(query.getAtomCount(), 128)];
        plan.add(new MatchOp(MatchOpType.Init));
        for (IAtom atom : queryRelaxed.atoms()) {
            if (!avisit[atom.getIndex()])
                buildPlan(atom, null);
        }
        if (changed) {
            plan.add(new MatchOp(MatchOpType.TautCheck));
        }
        plan.add(new MatchOp(MatchOpType.Done));

        if (System.getProperty("DEBUG") != null) {
            for (MatchOp op : plan)
                System.err.println(op);
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
                                     null, -1, bond.getIndex()));
            } else {
                plan.add(new MatchOp(MatchOpType.GrowBond,
                                     atom.getIndex(),
                                     nbor.getIndex(),
                                     ((QueryBond) BondRef.deref(bond)).getExpression(),
                                     ((QueryAtom) AtomRef.deref(nbor)).getExpression(),
                                     nbor.getBondCount(),
                                     bond.getIndex()));
                buildPlan(nbor, bond);
            }
        }
    }

    private int matches(IAtomContainer mol, int idx) {
        MatchOp op = plan.get(idx);
        int ret;
        switch (op.type) {
            case Init:
                // clear tautomer state
                first = true;
                roles = null;
                cache.clear();
                baseTaut = null;
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
                    if ((ret = matches(mol, idx + 1)) > 0)
                        return ret;
                    avisit[atom.getIndex()] = false;
                }
                break;
            case NewComponent:
                for (IAtom atom : mol.atoms()) {
                    if (avisit[atom.getIndex()] || !op.aexpr.matches(atom))
                        continue;
                    avisit[atom.getIndex()] = true;
                    amap[op.beg] = atom;
                    if ((ret = matches(mol, idx + 1)) > 0)
                        return ret;
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
                    if (nbor.getBondCount() < op.deg || !op.aexpr.matches(nbor))
                        continue;
                    avisit[nbor.getIndex()] = true;
                    amap[op.end] = nbor;
                    bmap[op.bidx] = bond;
                    if ((ret = matches(mol, idx + 1)) > 0)
                        return ret;
                    avisit[nbor.getIndex()] = false;
                    if (ret < 0 && -ret < idx)
                        return ret;
                }
                break;
            case CloseRing:
                IBond bond = amap[op.beg].getBond(amap[op.end]);
                if (bond == null || !op.bexpr.matches(bond))
                    return 0;
                bmap[op.bidx] = bond;
                return matches(mol, idx + 1);
            case TautCheck:

                // recheck the atom/bonds expressions as is, we might get lucky
                if (first) {
                    if (recheck())
                        return 1;
                    roles = TautTypeMatcher.assignRoles(mol, EnumSet.noneOf(Tautomers.Type.class));
                    first = false;
                    // if RING_REQUIRED == 0 (first query) we need to do it now
                    // so aromaticity works correctly
                    if ((flags & RING_REQUIRED) == 0)
                        Cycles.markRingAtomsAndBonds(mol);
                    if ((ret = checkRoles()) < 0)
                        return ret;
                    Aromaticity.apply(Aromaticity.Model.Daylight, mol);
                } else {
                    if ((ret = checkRoles()) < 0)
                        return ret;
                }

                if ((ret = recheck2()) > 1)
                    return 1;
                int tmp;


                Map<Integer,IBond.Order> fixedBonds = new HashMap<>();
                for (Map.Entry<Integer,IBond.Order> e : bondOrder.entrySet()) {
                    fixedBonds.put(bmap[e.getKey()].getIndex(), e.getValue());
                }

                List<TautStore> tautomers = cache.get(fixedBonds);
                if (tautomers == null) {

                    if (baseTaut == null)
                        baseTaut = new TautStore(mol);
                    else
                        baseTaut.apply(mol);

                    tautomers = new ArrayList<>();
                    try {
                        for (IAtomContainer taut : Tautomers.generateNoIdentity(mol, roles, Tautomers.Order.SEQUENTIAL, fixedBonds)) {
                            Aromaticity.apply(Aromaticity.Model.Daylight, taut);
                            if ((tmp = recheck2()) > 0) return 1;
                            tautomers.add(new TautStore(mol));
                            ret = Math.min(tmp, ret);
                        }
                    } catch (Exception ex) {
                        System.err.println(mol.getTitle());
                        System.exit(1);
                    }
                    cache.put(fixedBonds, tautomers);
                } else if (!tautomers.isEmpty()) {
                    for (TautStore store : tautomers) {
                        store.apply(mol);
                        if ((tmp = recheck2()) > 0) return 1;
                        ret = Math.min(tmp,ret);
                    }
                }

                return ret;
            case Done:
                return 1;
        }

        return 0;
    }

    private String getMapping(IAtom[] amap) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<amap.length; i++) {
            sb.append(i+1).append("=>").append(amap[i].getIndex()+1).append(", ");
        }
        return sb.toString();
    }

    private boolean recheck() {
        for (int i = 0; i < amap.length; i++)
            if (!((IQueryAtom) query.getAtom(i)).matches(amap[i]))
                return false;
        for (int i = 0; i < bmap.length; i++)
            if (!((IQueryBond) query.getBond(i)).matches(bmap[i]))
                return false;
        return true;
    }

    private int checkRoles() {
        IAtom atom;
        for (int i=0; i<plan.size(); i++) {
            MatchOp op = plan.get(i);
            switch (op.type) {
                case FirstAtom:
                case NewComponent:
                    atom = amap[op.beg];
                    if (!((IQueryAtom)query.getAtom(op.beg)).matches(atom) &&
                            roles[atom.getIndex()] == Tautomers.Role.X &&
                            (!atom.isInRing() || atom.getAtomicNumber() == IAtom.C)) {
                        return -i;
                    }
                    break;
                case GrowBond:
                    atom = amap[op.end];
                    if (!((IQueryAtom)query.getAtom(op.end)).matches(atom) &&
                            roles[atom.getIndex()] == Tautomers.Role.X &&
                            (!atom.isInRing() || atom.getAtomicNumber() == IAtom.C)) {
                        return -i;
                    }
                    break;
            }
        }
        return 0;
    }

    private int recheck2() {
        IAtom atom; IBond bond;
        for (int i=0; i<plan.size(); i++) {
            MatchOp op = plan.get(i);
            switch (op.type) {
                case FirstAtom:
                case NewComponent:
                    atom = amap[op.beg];
                    if (!((IQueryAtom)query.getAtom(op.beg)).matches(atom))
                        return -i;
                    break;
                case GrowBond:
                    atom = amap[op.end];
                    bond = bmap[op.bidx];
                    if (!((IQueryAtom)query.getAtom(op.end)).matches(atom) ||
                        !((IQueryBond)query.getBond(op.bidx)).matches(bond)) {
                        return -i;
                    }
                    break;
                case CloseRing:
                    if (!((IQueryBond)query.getBond(op.bidx)).matches(bmap[op.bidx])) {
                        return -i;
                    }
                    break;
            }
        }
        return 1;
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
        private final int bidx;

        private MatchOp(MatchOpType type) {
            this.type = type;
            this.beg = -1;
            this.end = -1;
            this.bexpr = null;
            this.aexpr = null;
            this.deg = -1;
            this.bidx = -1;
        }

        private MatchOp(MatchOpType type, int beg, Expr expr, int deg) {
            this.type = type;
            this.beg = beg;
            this.end = -1;
            this.bexpr = null;
            this.aexpr = expr;
            this.deg = deg;
            this.bidx = -1;
        }

        private MatchOp(MatchOpType type, int beg, int eng, Expr bexpr, Expr aexpr, int deg, int bidx) {
            this.type = type;
            this.beg = beg;
            this.end = eng;
            this.bexpr = bexpr;
            this.aexpr = aexpr;
            this.deg = deg;
            this.bidx = bidx;
        }

        @Override
        public String toString() {
            switch (type) {
                case FirstAtom:
                case NewComponent:
                    return type + " " + beg + " " + aexpr;
                case GrowBond:
                    return type + " " + beg + "-" + end + " bexpr:" + bexpr + " aexpr:" + aexpr + " bidx: " + bidx;
                case CloseRing:
                    return type + " " + beg + "-" + end + " bexpr:" + bexpr + " bidx: " + bidx;
                default:
                    return type.toString();
            }
        }
    }

    @Override
    public boolean matches(IAtomContainer mol) {
        if ((flags & RING_REQUIRED) != 0)
            Cycles.markRingAtomsAndBonds(mol);
        return matches(mol, 0) > 0;
    }

    @Override
    public int[] match(IAtomContainer target) {
        if (!matches(target))
            return new int[0];
        int[] match = new int[this.amap.length];
        for (int i = 0; i < match.length; i++)
            match[i] = this.amap[i].getIndex();
        return match;
    }

    @Override
    public Mappings matchAll(IAtomContainer target) {
        return null;
    }

    static class TautStore {
        private final int AROM_MASK = 0xf0;
        private final int[] atype;
        private final int[] btype;

        TautStore(IAtomContainer mol) {
            this.atype = new int[mol.getAtomCount()];
            this.btype = new int[mol.getBondCount()];
            for (int i = 0; i < atype.length; i++) {
                atype[i] = mol.getAtom(i).getImplicitHydrogenCount();
                atype[i] |= mol.getAtom(i).isAromatic() ? AROM_MASK : 0;
            }
            for (int i = 0; i < btype.length; i++) {
                IBond bond = mol.getBond(i);
                btype[i] = (byte) (bond.getOrder().numeric() & 0xf);
                if (bond.isAromatic())
                    btype[i] |= AROM_MASK;
            }
        }

        void apply(IAtomContainer mol) {
            for (int i = 0; i < atype.length; i++) {
                IAtom atom = mol.getAtom(i);
                atom.setImplicitHydrogenCount(atype[i] & 0x0f);
                if ((atype[i] & AROM_MASK) != 0)
                    atom.set(IChemObject.AROMATIC);
                else
                    atom.clear(IChemObject.AROMATIC);
            }
            for (int i = 0; i < btype.length; i++) {
                IBond bond = mol.getBond(i);
                switch (btype[i]) {
                    case 0x01:
                        bond.setOrder(IBond.Order.SINGLE);
                        bond.clear(IChemObject.AROMATIC);
                        break;
                    case 0x02:
                        bond.setOrder(IBond.Order.DOUBLE);
                        bond.clear(IChemObject.AROMATIC);
                        break;
                    case 0x03:
                        bond.setOrder(IBond.Order.TRIPLE);
                        break;
                    case 0x04:
                        bond.setOrder(IBond.Order.QUADRUPLE);
                        break;
                    case 0xf1:
                        bond.setOrder(IBond.Order.SINGLE);
                        bond.set(IChemObject.AROMATIC);
                        break;
                    case 0xf2:
                        bond.setOrder(IBond.Order.DOUBLE);
                        bond.set(IChemObject.AROMATIC);
                        break;
                }
            }
        }
    }
}
