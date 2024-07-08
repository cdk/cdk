/*
 * Copyright (c) 2024 John Mayfield NextMove Software
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

package org.openscience.cdk.tautomer;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.graph.Cycles;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import static org.openscience.cdk.tautomer.Tautomers.Role.Acceptor;
import static org.openscience.cdk.tautomer.Tautomers.Role.Donor;

/**
 * Efficient generate tautomers using the Sayle-Delany algorithm <a href="https://www.daylight.com/meetings/emug99/Delany/taut_html/index.htm">Sayle 96,
 * Canonicalization and Enumeration of Tautomers</a>. The algorithm uses
 * simple rules to identify atoms which can either accept, donate or transfer
 * a proton. These atoms are then partitioned into a different components/zones
 * and the hydrogens/bond orders stripped. The hydrogens are then placed using
 * backtracking algorithm failing whenever an invalid Kekulé state is found.
 * <br/>
 * This algorithm is very efficient and can process hundreds of thousands of
 * molecules per second. Typically, it is quicker to generate the tautomers then
 * store and read them back in but it's up to the caller to decide how to use it.
 *
 * <h1>Prerequisites/Limitations:</h1>
 * <ul>
 *     <li>Implicit hydrogens should be assigned and suppressed, explicit
 *     hydrogens (and their isotopes) can not be shifted.</li>
 *     <li>Ring membership should be assigned ({@link Cycles#markRingAtomsAndBonds}), this is mainly required
 *     for keto-enol tautomers where protons try to avoid carbon's in rings</li>
 * </ul>
 * <h1>Side-effects:</h1>
 * <ul>
 *     <li>The input molecule is modified in place, aromaticity flags are
 *         cleared and the hydrogen count/bond orders will change. The generator
 *         returns the same molecule on each iteration and so if copies need to be
 *         kept a {@link IAtomContainer#clone()}
 *         should be used on input and output.</li>
 * </ul>
 *
 * @author John Mayfield
 */
public final class Tautomers {

    public enum Type {
        /**
         * Keto-Enol tautomerism. Sp3 carbons are allowed to donate a hydrogen
         * if they are within two bonds (1,3-shift) from a hetero atom
         * donor/acceptor. Due to the nature of the algorithm 1,5-shifts and
         * larger may still be found if there is a larger tautomeric group but
         * this is not guaranteed.
         * {@code -C=C-OH <=> -C-C=O }
         */
        CARBON_SHIFTS,
    }

    /**
     * Tautomer atom role.
     *
     * @author John Mayfield
     */
    enum Role {
        /** The atom has no role. */
        None,
        /** A conjugated Sp2 hybridised atom, should be adjacent to a pi bond. */
        Conjugated,
        /** A donor atom that can donate a proton. */
        Donor,
        /** An acceptor atom that can accept a proton, it will be adjacent to
         *  a pi bond. */
        Acceptor
    }

    /**
     * The order in which to generate tautomers.
     */
    public enum Order {
        /* Generated tautomers sequentially based on input order and
           electronegativity. */
        SEQUENTIAL,
        /* Generated tautomers based on canonical order and electronegativity.
         * Note the atom order does not change only that the first tautomer
         * generated will always be the same. */
        CANONICAL,
        /* Tautomers will be generated randomly. This is mainly useful for
         * method testings.
         */
        RANDOM
    }

    private final SayleDelanyState state;
    private final IntStack stack;

    Tautomers(SayleDelanyState state) {
        this.state = state;
        this.stack = new IntStack(state.candidates.length + 1);
        SayleDelanyState.clearTypeInfo(state.container());
    }

    public IAtomContainer next() {
        while (moveToNextState()) ;
        return state.complete() ? state.container() : null;
    }

    private boolean moveToNextState() {
        int v = state.select();

        if (state.add(v, Donor) || state.add(v, Acceptor)) {
            stack.push(v);
            return !state.complete();
        } else {
            while (true) {

                while (!stack.empty() && state.roleOf(stack.peek()) == Acceptor) {
                    state.remove(stack.pop());
                }

                if (stack.empty())
                    return false;

                state.remove(v = stack.pop());
                if (state.add(v, Acceptor)) {
                    stack.push(v);
                    return !state.complete();
                }
            }
        }
    }



    public static Iterable<IAtomContainer> generate(IAtomContainer mol,
                                                    Set<Type> types,
                                                    Order mode) {
        // this is a little complex since we modify the input container,
        // calling this function multiple times is undefined but should work
        return () -> new Iterator<IAtomContainer>() {
            final Role[] roles = AtomTypeMatcher.assignRoles(mol, types);
            final SayleDelanyState state = new SayleDelanyState(mol,
                                                                roles,
                                                                mol.getAtomCount(),
                                                                mode);
            final Tautomers tautomers = new Tautomers(state);
            IAtomContainer next;

            IAtomContainer doNext() {
                return next != null ? next : (next = tautomers.next());
            }

            @Override
            public boolean hasNext() {
                return doNext() != null;
            }

            @Override
            public IAtomContainer next() {
                IAtomContainer container = doNext();
                next = null;
                return container;
            }
        };
    }

    public static Iterable<IAtomContainer> generate(IAtomContainer mol, Order mode) {
        return generate(mol, EnumSet.noneOf(Type.class), mode);
    }

    public static Iterable<IAtomContainer> generate(IAtomContainer mol) {
        return generate(mol, Order.SEQUENTIAL);
    }

    private static final class IntStack {
        int[] vs;
        int n = 0;

        private IntStack(int n) {
            this.vs = new int[n];
        }

        void push(int v) {
            assert n < vs.length;
            vs[n++] = v;
        }

        boolean empty() {
            return n == 0;
        }

        int pop() {
            assert n > 0;
            return vs[--n];
        }

        int peek() {
            assert n > 0;
            return vs[n - 1];
        }
    }
}
