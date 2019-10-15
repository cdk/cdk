/*
 * Copyright (c) 2018 NextMove Software
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.openscience.cdk.isomorphism.matchers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ReactionRole;
import org.openscience.cdk.config.Elements;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.DfPattern;
import static org.openscience.cdk.isomorphism.matchers.Expr.Type.*;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Objects;

/**
 * A expression stores a predicate tree for checking properties of atoms
 * and bonds.
 * <pre>
 * Expr expr = new Expr(ELEMENT, 6);
 * if (expr.matches(atom)) {
 *   // expression matches if atom is a carbon!
 * }
 * </pre>
 * An expression is composed of an {@link Type}, an optional 'value', and
 * optionally one or more 'sub-expressions'. Each expr can either be a leaf or
 * an intermediate (logical) node. The simplest expression trees contain a
 * single leaf node:
 * <pre>
 * new Expr(IS_AROMATIC); // matches any aromatic atom
 * new Expr(ELEMENT, 6);  // matches any carbon atom (atomic num=6)
 * new Expr(VALENCE, 4);  // matches an atom with valence 4
 * new Expr(DEGREE, 1);   // matches a terminal atom, e.g. -OH, =O
 * new Expr(IS_IN_RING);  // matches any atom marked as in a ring
 * new Expr(IS_HETERO);   // matches anything other than carbon or nitrogen
 * new Expr(TRUE);        // any atom
 * </pre>
 * Logical internal nodes combine one or two sub-expressions with conjunction
 * (and), disjunction (or), and negation (not).
 * <br>
 * Consider the following expression tree, is matches fluorine, chlorine, or
 * bromine.
 * <pre>
 *     OR
 *    /  \
 *   F   OR
 *      /  \
 *     Cl   Br
 * </pre>
 * We can construct this tree as follows:
 * <pre>
 * Expr expr = new Expr(ELEMENT, 9) // F
 *                  .or(new Expr(ELEMENT, 17)) // Cl
 *                  .or(new Expr(ELEMENT, 35))  // Br</pre>
 * A more verbose construction could also be used:
 * <pre>
 * Expr leafF  = new Expr(ELEMENT, 9); // F
 * Expr leafCl = new Expr(ELEMENT, 17); // Cl
 * Expr leafBr = new Expr(ELEMENT, 35);  // Br
 * Expr node4  = new Expr(OR, leaf2, leaf3);
 * Expr node5  = new Expr(OR, leaf1, node4);
 * </pre>
 *
 * Expressions can be used to match bonds. Note some expressions apply to either
 * atoms or bonds.
 * <pre>
 * new Expr(TRUE);               // any bond
 * new Expr(IS_IN_RING);         // any ring bond
 * new Expr(ALIPHATIC_ORDER, 2); // double bond
 * </pre>
 * See the documentation for {@link Type}s for a detail explanation of
 * each type.
 */
public final class Expr {

    /** Sentinel value for indicating the stereochemistry configuration
     *  is not yet known. Since stereochemistry depends on the ordering
     *  of neighbors we can't check this until those neighbors are
     *  matched. */
    public static final int UNKNOWN_STEREO = -1;

    // the expression type
    private Type type;
    // used for primitive leaf types
    private int  value;
    // used for unary and binary types; not, and, or
    private Expr left, right;
    // user for recursive expression types
    private IAtomContainer query;
    private DfPattern      ptrn;

    /**
     * Creates an atom expression that will always match ({@link Type#TRUE}).
     */
    public Expr() {
        this(Type.TRUE);
    }

    /**
     * Creates an atom expression for the specified primitive.
     */
    public Expr(Type op) {
        setPrimitive(op);
    }

    /**
     * Creates an atom expression for the specified primitive and 'value'.
     */
    public Expr(Type op, int val) {
        setPrimitive(op, val);
    }

    /**
     * Creates a logical atom expression for the specified.
     */
    public Expr(Type op, Expr left, Expr right) {
        setLogical(op, left, right);
    }

    /**
     * Creates a recursive atom expression.
     */
    public Expr(Type op, IAtomContainer mol) {
        setRecursive(op, mol);
    }

    /**
     * Copy-constructor, creates a shallow copy of the provided expression.
     *
     * @param expr the expre
     */
    public Expr(final Expr expr) {
        set(expr);
    }

    private static boolean eq(Integer a, int b) {
        return a != null && a == b;
    }

    private static int unbox(Integer x) {
        return x != null ? x : 0;
    }

    private static boolean isInRingSize(IAtom atom, IBond prev, IAtom beg,
                                        int size, int req) {
        atom.setFlag(CDKConstants.VISITED, true);
        for (IBond bond : atom.bonds()) {
            if (bond == prev)
                continue;
            IAtom nbr = bond.getOther(atom);
            if (nbr.equals(beg))
                return size == req;
            else if (size < req &&
                     !nbr.getFlag(CDKConstants.VISITED) &&
                     isInRingSize(nbr, bond, beg, size + 1, req))
                return true;
        }
        atom.setFlag(CDKConstants.VISITED, false);
        return false;
    }

    private static boolean isInRingSize(IAtom atom, int size) {
        for (IAtom a : atom.getContainer().atoms())
            a.setFlag(CDKConstants.VISITED, false);
        return isInRingSize(atom, null, atom, 1, size);
    }

    private static boolean isInSmallRingSize(IAtom atom, int size) {
        IAtomContainer mol    = atom.getContainer();
        int[]          distTo = new int[mol.getAtomCount()];
        Arrays.fill(distTo, 1 + distTo.length);
        distTo[atom.getIndex()] = 0;
        Deque<IAtom> queue = new ArrayDeque<>();
        queue.push(atom);
        int smallest = 1 + distTo.length;
        while (!queue.isEmpty()) {
            IAtom a    = queue.poll();
            int   dist = 1 + distTo[a.getIndex()];
            for (IBond b : a.bonds()) {
                IAtom nbr = b.getOther(a);
                if (dist < distTo[nbr.getIndex()]) {
                    distTo[nbr.getIndex()] = dist;
                    queue.add(nbr);
                } else if (dist != 2 + distTo[nbr.getIndex()]) {
                    int tmp = dist + distTo[nbr.getIndex()];
                    if (tmp < smallest)
                        smallest = tmp;
                }
            }
            if (2 * dist > 1 + size)
                break;
        }
        return smallest == size;
    }

    /**
     * Internal match methods - does not null check.
     *
     * @param type the type
     * @param atom the atom
     * @return the expression matches
     */
    private boolean matches(Type type, IAtom atom, int stereo) {
        switch (type) {
            // predicates
            case TRUE:
                return true;
            case FALSE:
                return false;
            case IS_AROMATIC:
                return atom.isAromatic();
            case IS_ALIPHATIC:
                return !atom.isAromatic();
            case IS_IN_RING:
                return atom.isInRing();
            case IS_IN_CHAIN:
                return !atom.isInRing();
            case IS_HETERO:
                return !eq(atom.getAtomicNumber(), 6) &&
                       !eq(atom.getAtomicNumber(), 1);
            case HAS_IMPLICIT_HYDROGEN:
                return atom.getImplicitHydrogenCount() != null &&
                       atom.getImplicitHydrogenCount() > 0;
            case HAS_ISOTOPE:
                return atom.getMassNumber() != null;
            case HAS_UNSPEC_ISOTOPE:
                return atom.getMassNumber() == null;
            case UNSATURATED:
                for (IBond bond : atom.bonds())
                    if (bond.getOrder() == IBond.Order.DOUBLE)
                        return true;
                return false;
            // value primitives
            case ELEMENT:
                return eq(atom.getAtomicNumber(), value);
            case ALIPHATIC_ELEMENT:
                return !atom.isAromatic() &&
                       eq(atom.getAtomicNumber(), value);
            case AROMATIC_ELEMENT:
                return atom.isAromatic() &&
                       eq(atom.getAtomicNumber(), value);
            case IMPL_H_COUNT:
                return eq(atom.getImplicitHydrogenCount(), value);
            case TOTAL_H_COUNT:
                if (atom.getImplicitHydrogenCount() != null
                    && atom.getImplicitHydrogenCount() > value)
                    return false;
                return getTotalHCount(atom) == value;
            case DEGREE:
                return atom.getBondCount() == value;
            case HEAVY_DEGREE: // XXX: CDK quirk
                return atom.getBondCount() - (getTotalHCount(atom) - atom.getImplicitHydrogenCount()) == value;
            case TOTAL_DEGREE:
                int x = atom.getBondCount() + unbox(atom.getImplicitHydrogenCount());
                return x == value;
            case VALENCE:
                int v = unbox(atom.getImplicitHydrogenCount());
                if (v > value)
                    return false;
                for (IBond bond : atom.bonds()) {
                    if (bond.getOrder() != null)
                        v += bond.getOrder().numeric();
                }
                return v == value;
            case ISOTOPE:
                return eq(atom.getMassNumber(), value);
            case FORMAL_CHARGE:
                return eq(atom.getFormalCharge(), value);
            case RING_BOND_COUNT:
                if (!atom.isInRing() || atom.getBondCount() < value)
                    return false;
                int rbonds = 0;
                for (IBond bond : atom.bonds())
                    rbonds += bond.isInRing() ? 1 : 0;
                return rbonds == value;
            case RING_COUNT:
                return atom.isInRing() && getRingCount(atom) == value;
            case RING_SMALLEST:
                return atom.isInRing() && isInSmallRingSize(atom, value);
            case RING_SIZE:
                return atom.isInRing() && isInRingSize(atom, value);
            case HETERO_SUBSTITUENT_COUNT:
                if (atom.getBondCount() < value)
                    return false;
                int q = 0;
                for (IBond bond : atom.bonds())
                    q += matches(Type.IS_HETERO, bond.getOther(atom), stereo) ? 1 : 0;
                return q == value;
            case INSATURATION:
                int db = 0;
                for (IBond bond : atom.bonds())
                    if (bond.getOrder() == IBond.Order.DOUBLE)
                        db++;
                return db == value;
            case HYBRIDISATION_NUMBER:
                IAtomType.Hybridization hyb = atom.getHybridization();
                if (hyb == null)
                    return false;
                switch (value) {
                    case 1:
                        return hyb == IAtomType.Hybridization.SP1;
                    case 2:
                        return hyb == IAtomType.Hybridization.SP2;
                    case 3:
                        return hyb == IAtomType.Hybridization.SP3;
                    case 4:
                        return hyb == IAtomType.Hybridization.SP3D1;
                    case 5:
                        return hyb == IAtomType.Hybridization.SP3D2;
                    case 6:
                        return hyb == IAtomType.Hybridization.SP3D3;
                    case 7:
                        return hyb == IAtomType.Hybridization.SP3D4;
                    case 8:
                        return hyb == IAtomType.Hybridization.SP3D5;
                    default:
                        return false;
                }
            case PERIODIC_GROUP:
                return atom.getAtomicNumber() != null &&
                       Elements.ofNumber(atom.getAtomicNumber()).group() == value;
            case STEREOCHEMISTRY:
                return stereo == UNKNOWN_STEREO || stereo == value;
            case REACTION_ROLE:
                ReactionRole role = atom.getProperty(CDKConstants.REACTION_ROLE);
                return role != null && role.ordinal() == value;
            case AND:
                return left.matches(left.type, atom, stereo) &&
                       right.matches(right.type, atom, stereo);
            case OR:
                return left.matches(left.type, atom, stereo) ||
                       right.matches(right.type, atom, stereo);
            case NOT:
                return !left.matches(left.type, atom, stereo) ||
                       // XXX: ugly but needed, when matching stereo
                       (stereo == UNKNOWN_STEREO &&
                        (left.type == STEREOCHEMISTRY ||
                         left.type == OR && left.left.type == STEREOCHEMISTRY));
            case RECURSIVE:
                if (ptrn == null)
                    ptrn = DfPattern.findSubstructure(query);
                return ptrn.matchesRoot(atom);
            default:
                throw new IllegalArgumentException("Cannot match AtomExpr, type=" + type);
        }
    }

    public boolean matches(IBond bond, int stereo) {
        switch (type) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case ALIPHATIC_ORDER:
                return !bond.isAromatic() &&
                       bond.getOrder() != null &&
                       bond.getOrder().numeric() == value;
            case ORDER:
                return bond.getOrder() != null &&
                       bond.getOrder().numeric() == value;
            case IS_AROMATIC:
                return bond.isAromatic();
            case IS_ALIPHATIC:
                return !bond.isAromatic();
            case IS_IN_RING:
                return bond.isInRing();
            case IS_IN_CHAIN:
                return !bond.isInRing();
            case SINGLE_OR_AROMATIC:
                return bond.isAromatic() ||
                       IBond.Order.SINGLE.equals(bond.getOrder());
            case DOUBLE_OR_AROMATIC:
                return bond.isAromatic() ||
                       IBond.Order.DOUBLE.equals(bond.getOrder());
            case SINGLE_OR_DOUBLE:
                return IBond.Order.SINGLE.equals(bond.getOrder()) ||
                       IBond.Order.DOUBLE.equals(bond.getOrder());
            case STEREOCHEMISTRY:
                return stereo == UNKNOWN_STEREO || value == stereo;
            case AND:
                return left.matches(bond, stereo) && right.matches(bond, stereo);
            case OR:
                return left.matches(bond, stereo) || right.matches(bond, stereo);
            case NOT:
                return !left.matches(bond, stereo) ||
                       // XXX: ugly but needed, when matching stereo
                       (stereo == UNKNOWN_STEREO &&
                        (left.type == STEREOCHEMISTRY ||
                         left.type == OR && left.left.type == STEREOCHEMISTRY));
            default:
                throw new IllegalArgumentException("Cannot match BondExpr, type=" + type);
        }
    }

    public boolean matches(IBond bond) {
        return matches(bond, UNKNOWN_STEREO);
    }

    private static int getTotalHCount(IAtom atom) {
        int h = unbox(atom.getImplicitHydrogenCount());
        for (IBond bond : atom.bonds())
            if (eq(bond.getOther(atom).getAtomicNumber(), 1))
                h++;
        return h;
    }

    /**
     * Test whether this expression matches an atom instance.
     *
     * @param atom an atom (nullable)
     * @return the atom matches
     */
    public boolean matches(final IAtom atom) {
        return atom != null && matches(type, atom, UNKNOWN_STEREO);
    }

    public boolean matches(final IAtom atom, final int stereo) {
        return atom != null && matches(type, atom, stereo);
    }

    /**
     * Utility, combine this expression with another, using conjunction.
     * The expression will only match if both conditions are met.
     *
     * @param expr the other expression
     * @return self for chaining
     */
    public Expr and(Expr expr) {
        if (type == Type.TRUE) {
            set(expr);
        } else if (expr.type != Type.TRUE) {
            if (type.isLogical() && !expr.type.isLogical()) {
                if (type == AND)
                    right.and(expr);
                else if (type != NOT)
                    setLogical(Type.AND, expr, new Expr(this));
                else
                    setLogical(Type.AND, expr, new Expr(this));
            } else {
                setLogical(Type.AND, new Expr(this), expr);
            }
        }
        return this;
    }

    /**
     * Utility, combine this expression with another, using disjunction.
     * The expression will match if either conditions is met.
     * @param expr the other expression
     * @return self for chaining
     */
    public Expr or(Expr expr) {
        if (type == Type.TRUE ||
            type == Type.FALSE ||
            type == NONE) {
            set(expr);
        } else if (expr.type != Type.TRUE &&
                   expr.type != Type.FALSE &&
                   expr.type != Type.NONE) {
            if (type.isLogical() && !expr.type.isLogical()) {
                if (type == OR)
                    right.or(expr);
                else if (type != NOT)
                    setLogical(Type.OR, expr, new Expr(this));
                else
                    setLogical(Type.OR, new Expr(this), expr);
            }
            else
                setLogical(Type.OR, new Expr(this), expr);
        }
        return this;
    }

    /**
     * Negate the expression, the expression will not return true only if
     * the condition is not met. Some expressions have explicit types
     * that are more efficient to use, for example:
     * <code>IS_IN_RING =&gt; NOT(IS_IN_RING) =&gt; IS_IN_CHAIN</code>. This
     * negation method will use the more efficient type where possible.
     * <pre>{@code
     * Expr e = new Expr(ELEMENT, 8); // SMARTS: [#8]
     * e.negate(); // SMARTS: [!#8]
     * }</pre>
     * @return self for chaining
     */
    public Expr negate() {
        switch (type) {
            case TRUE:
                type = Type.FALSE;
                break;
            case FALSE:
                type = Type.TRUE;
                break;
            case HAS_ISOTOPE:
                type = Type.HAS_UNSPEC_ISOTOPE;
                break;
            case HAS_UNSPEC_ISOTOPE:
                type = Type.HAS_ISOTOPE;
                break;
            case IS_AROMATIC:
                type = Type.IS_ALIPHATIC;
                break;
            case IS_ALIPHATIC:
                type = Type.IS_AROMATIC;
                break;
            case IS_IN_RING:
                type = Type.IS_IN_CHAIN;
                break;
            case IS_IN_CHAIN:
                type = Type.IS_IN_RING;
                break;
            case NOT:
                set(this.left);
                break;
            default:
                setLogical(Type.NOT, new Expr(this), null);
                break;
        }
        return this;
    }

    /**
     * Set the primitive value of this atom expression.
     *
     * @param type the type of expression
     * @param val  the value to check
     */
    public void setPrimitive(Type type, int val) {
        if (type.hasValue()) {
            this.type = type;
            this.value = val;
            this.left = null;
            this.right = null;
            this.query = null;
        } else {
            throw new IllegalArgumentException("Value provided for non-value "
                                               + "expression type!");
        }
    }

    /**
     * Set the primitive value of this atom expression.
     *
     * @param type the type of expression
     */
    public void setPrimitive(Type type) {
        if (!type.hasValue() && !type.isLogical()) {
            this.type = type;
            this.value = -1;
            this.left = null;
            this.right = null;
            this.query = null;
        } else {
            throw new IllegalArgumentException("Expression type requires a value!");
        }
    }

    /**
     * Set the logical value of this atom expression.
     *
     * @param type  the type of expression
     * @param left  the left sub-expression
     * @param right the right sub-expression
     */
    public void setLogical(Type type, Expr left, Expr right) {
        switch (type) {
            case AND:
            case OR:
                this.type = type;
                this.value = 0;
                this.left = left;
                this.right = right;
                this.query = null;
                break;
            case NOT:
                this.type = type;
                if (left != null && right == null)
                    this.left = left;
                else if (left == null && right != null)
                    this.left = right;
                else if (left != null)
                    throw new IllegalArgumentException("Only one sub-expression"
                                                       + " should be provided"
                                                       + " for NOT expressions!");
                this.query = null;
                this.value = 0;
                break;
            default:
                throw new IllegalArgumentException("Left/Right sub expressions "
                                                   + "supplied for "
                                                   + " non-logical operator!");
        }
    }

    /**
     * Set the recursive value of this atom expression.
     *
     * @param type the type of expression
     * @param mol  the recursive pattern
     */
    private void setRecursive(Type type, IAtomContainer mol) {
        switch (type) {
            case RECURSIVE:
                this.type = type;
                this.value = 0;
                this.left = null;
                this.right = null;
                this.query = mol;
                this.ptrn  = null;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Set this expression to another (shallow copy).
     *
     * @param expr the other expression
     */
    public void set(Expr expr) {
        this.type = expr.type;
        this.value = expr.value;
        this.left = expr.left;
        this.right = expr.right;
        this.query = expr.query;
    }

    /**
     * Access the type of the atom expression.
     *
     * @return the expression type
     */
    public Type type() {
        return type;
    }

    /**
     * Access the value of this atom expression being
     * tested.
     *
     * @return the expression value
     */
    public int value() {
        return value;
    }

    /**
     * Access the left sub-expression of this atom expression being
     * tested.
     *
     * @return the expression value
     */
    public Expr left() {
        return left;
    }

    /**
     * Access the right sub-expression of this atom expression being
     * tested.
     *
     * @return the expression value
     */
    public Expr right() {
        return right;
    }

    /**
     * Access the sub-query, only applicable to recursive types.
     * @return the sub-query
     * @see Type#RECURSIVE
     */
    public IAtomContainer subquery() {
        return query;
    }

    /* Property Caches */
    private static LoadingCache<IAtomContainer, int[]> cacheRCounts;

    private static int[] getRingCounts(IAtomContainer mol) {
        int[] rcounts = new int[mol.getAtomCount()];
        for (int[] path : Cycles.mcb(mol).paths()) {
            for (int i = 1; i < path.length; i++) {
                rcounts[path[i]]++;
            }
        }
        return rcounts;
    }

    private static int getRingCount(IAtom atom) {
        final IAtomContainer mol = atom.getContainer();
        if (cacheRCounts == null) {
            cacheRCounts = CacheBuilder.newBuilder()
                                       .maximumWeight(1000) // 4KB
                                       .weigher(new Weigher<IAtomContainer, int[]>() {
                                           @Override
                                           public int weigh(IAtomContainer key,
                                                            int[] value) {
                                               return value.length;
                                           }
                                       })
                                       .build(new CacheLoader<IAtomContainer, int[]>() {
                                           @Override
                                           public int[] load(IAtomContainer key) throws Exception {
                                               return getRingCounts(key);
                                           }
                                       });
        }
        return cacheRCounts.getUnchecked(mol)[atom.getIndex()];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expr atomExpr = (Expr) o;
        return type == atomExpr.type &&
               value == atomExpr.value &&
               Objects.equals(left, atomExpr.left) && Objects.equals(right, atomExpr.right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(type, value, left, right);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);
        if (type.isLogical()) {
            switch (type) {
                case NOT:
                    sb.append('(').append(left).append(')');
                    break;
                case OR:
                case AND:
                    sb.append('(').append(left).append(',').append(right).append(')');
                    break;
            }
        } else if (type.hasValue()) {
            sb.append('=').append(value);
        } else if (type == Type.RECURSIVE) {
            sb.append("(...)");
        }
        return sb.toString();
    }

    /**
     * Types of expression, for use in the {@link Expr} tree object.
     */
    public enum Type {
        /** Always returns true. */
        TRUE,
        /** Always returns false. */
        FALSE,
        /** Return true if {@link IAtom#isAromatic} or {@link IBond#isAromatic} is
         *  true. */
        IS_AROMATIC,
        /** Return true if {@link IAtom#isAromatic} or {@link IBond#isAromatic} is
         *  flase. */
        IS_ALIPHATIC,
        /** Return true if {@link IAtom#isInRing()} or {@link IBond#isInRing} is
         *  true. */
        IS_IN_RING,
        /** Return true if {@link IAtom#isInRing()} or {@link IBond#isInRing} is
         *  false. */
        IS_IN_CHAIN,
        /** Return true if {@link IAtom#getAtomicNumber()} is neither 6 (carbon)
         *  nor 1 (hydrogen). */
        IS_HETERO,
        /** Return true if {@link IAtom#getAtomicNumber()} is neither 6 (carbon)
         *  nor 1 (hydrogen) and the atom is aliphatic. */
        IS_ALIPHATIC_HETERO,
        /** True if the hydrogen count ({@link IAtom#getImplicitHydrogenCount()})
         *  is &gt; 0. */
        HAS_IMPLICIT_HYDROGEN,
        /** True if the atom mass ({@link IAtom#getMassNumber()}) is non-null. */
        HAS_ISOTOPE,
        /** True if the atom mass ({@link IAtom#getMassNumber()}) is null
         *  (unspecified). */
        HAS_UNSPEC_ISOTOPE,
        /** True if the atom is adjacent to a hetero atom. */
        HAS_HETERO_SUBSTITUENT,
        /** True if the atom is adjacent to an aliphatic hetero atom. */
        HAS_ALIPHATIC_HETERO_SUBSTITUENT,
        /** True if the atom is unsaturated.
         *  TODO: check if CACTVS if double bond to non-carbons are counted. */
        UNSATURATED,
        /** True if the bond order ({@link IBond#getOrder()}) is single or double. */
        SINGLE_OR_DOUBLE,
        /** True if the bond order ({@link IBond#getOrder()}) is single or the bond
         *  is marked as aromatic ({@link IBond#isAromatic()}). */
        SINGLE_OR_AROMATIC,
        /** True if the bond order ({@link IBond#getOrder()}) is double or the bond
         *  is marked as aromatic ({@link IBond#isAromatic()}). */
        DOUBLE_OR_AROMATIC,

        /* Expressions that take values */

        /** True if the atomic number ({@link IAtom#getAtomicNumber()} ()})
         *  of an atom equals the specified 'value'. */
        ELEMENT,
        /** True if the atomic number ({@link IAtom#getAtomicNumber()} ()})
         *  of an atom equals the specified 'value' and {@link IAtom#isAromatic()}
         *  is false. */
        ALIPHATIC_ELEMENT,
        /** True if the atomic number ({@link IAtom#getAtomicNumber()} ()})
         *  of an atom equals the specified 'value' and {@link IAtom#isAromatic()}
         *  is true. */
        AROMATIC_ELEMENT,
        /** True if the hydrogen count ({@link IAtom#getImplicitHydrogenCount()})
         *  of an atom equals the specified 'value'. */
        IMPL_H_COUNT,
        /** True if the total hydrogen count of an atom equals the specified
         * 'value'. */
        TOTAL_H_COUNT,
        /** True if the degree ({@link IAtom#getBondCount()}) of an atom
         *  equals the specified 'value'. */
        DEGREE,
        /** True if the total degree ({@link IAtom#getBondCount()} +
         *  {@link IAtom#getImplicitHydrogenCount()}) of an atom equals the
         *  specified 'value'. */
        TOTAL_DEGREE,
        /** True if the degree ({@link IAtom#getBondCount()}) - any hydrogen atoms
         x*  equals the specified 'value'. */
        HEAVY_DEGREE,
        /** True if the valence of an atom equals the specified 'value'. */
        VALENCE,
        /** True if the mass ({@link IAtom#getMassNumber()}) of an atom equals the
         *  specified 'value'. */
        ISOTOPE,
        /** True if the formal charge ({@link IAtom#getFormalCharge()}) of an atom
         *  equals the specified 'value'. */
        FORMAL_CHARGE,
        /** True if the ring bond count of an atom equals the specified 'value'. */
        RING_BOND_COUNT,
        /** True if the number of rings this atom belongs to matches the specified
         *  'value'. Here a ring means a member of the Minimum Cycle Basis (MCB)
         *  (aka Smallest Set of Smallest Rings). Since the MCB is non-unique the
         *  numbers often don't make sense of bicyclo systems. */
        RING_COUNT,
        /** True if the smallest ring this atom belongs to equals the specified
         *  'value' */
        RING_SMALLEST,
        /** True if the this atom belongs to a ring equal to the specified
         * 'value' */
        RING_SIZE,
        /** True if the this atom hybridisation ({@link IAtom#getHybridization()})
         *  is equal to the specified 'value'. SP1=1, SP2=2, SP3=3, SP3D1=4,
         *  SP3D2=5, SP3D3=6, SP3D4=7, SP3D5=8. */
        HYBRIDISATION_NUMBER,
        /** True if the number hetero atoms (see {@link #IS_HETERO}) this atom is
         *  next to is equal to the specified value. */
        HETERO_SUBSTITUENT_COUNT,
        /** True if the number hetero atoms (see {@link #IS_ALIPHATIC_HETERO}) this atom is
         *  next to is equal to the specified value. */
        ALIPHATIC_HETERO_SUBSTITUENT_COUNT,
        /** True if the periodic table group of this atom is equal to the specified
         *  value. For example halogens are Group '17'.*/
        PERIODIC_GROUP,
        /** True if the number of double bonds equals the specified value.
         *  TODO: check if CACTVS if double bond to non-carbons are counted. */
        INSATURATION,
        /** True if an atom has the specified reaction role. */
        REACTION_ROLE,
        /** True if an atom or bond has the specified stereochemistry value, see
         *  ({@link org.openscience.cdk.interfaces.IStereoElement}) for a list of
         *  values.*/
        STEREOCHEMISTRY,
        /** True if the bond order {@link IBond#getOrder()} equals the specified
         *  value and the bond is not marked as aromatic
         *  ({@link IAtom#isAromatic()}). */
        ALIPHATIC_ORDER,
        /** True if the bond order {@link IBond#getOrder()} equals the specified
         *  value and the bond, aromaticity is not check. */
        ORDER,

        /* Binary/unary internal nodes */

        /** True if both the subexpressions are true. */
        AND,
        /** True if both either subexpressions are true. */
        OR,
        /** True if the subexpression is not true. */
        NOT,

        /** Recursive query. */
        RECURSIVE,

        /** Undefined expression type. */
        NONE;

        boolean isLogical() {
            switch (this) {
                case OR:
                case NOT:
                case AND:
                    return true;
                default:
                    return false;
            }
        }

        boolean hasValue() {
            switch (this) {
                case OR:
                case NOT:
                case AND:
                case TRUE:
                case FALSE:
                case NONE:
                case IS_AROMATIC:
                case IS_ALIPHATIC:
                case IS_IN_RING:
                case IS_IN_CHAIN:
                case IS_HETERO:
                case IS_ALIPHATIC_HETERO:
                case HAS_IMPLICIT_HYDROGEN:
                case HAS_ISOTOPE:
                case HAS_UNSPEC_ISOTOPE:
                case HAS_ALIPHATIC_HETERO_SUBSTITUENT:
                case HAS_HETERO_SUBSTITUENT:
                case UNSATURATED:
                case SINGLE_OR_AROMATIC:
                case SINGLE_OR_DOUBLE:
                case DOUBLE_OR_AROMATIC:
                case RECURSIVE:
                    return false;
                default:
                    return true;
            }
        }
    }
}
