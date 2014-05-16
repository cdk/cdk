/*
 * Copyright (c) 2014 European Bioinformatics Institute (EMBL-EBI)
 *                    John May <jwmay@users.sf.net>
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

import org.openscience.cdk.graph.GraphUtil;
import org.openscience.cdk.interfaces.IAtomContainer;

import static org.openscience.cdk.tautomer.Role.Acceptor;
import static org.openscience.cdk.tautomer.Role.Donor;

/**
 * Lazily generates a stream of tautomers using the Sayle-Delany algorithm. 
 * 
 * @author John May
 */
public final class SDTautomerStream {

    private final SDTautomerState state;
    private final IntStack      stack;
    
    SDTautomerStream(SDTautomerState state) {
        this.state = state;
        this.stack = new IntStack(state.candidates.length + 1);
    }

    public IAtomContainer next() {
        while (moveToNextState()) ;
        return state.complete() ? state.container() : null;
    }

    boolean moveToNextState() {
        int v = state.select();

        if (state.add(v, Donor) || state.add(v, Acceptor)) {
            stack.push(v);
            return !state.complete();
        }
        else {
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
    
    public static SDTautomerStream create(IAtomContainer container) {
        GraphUtil.EdgeToBondMap bonds = GraphUtil.EdgeToBondMap.withSpaceFor(container);
        int[][]                 graph = GraphUtil.toAdjList(container, bonds);
        Role[]                  roles = BasicRoleTyper.assignRoles(container, graph, bonds, false);
        return new SDTautomerStream(new SDTautomerState(container, graph, bonds, roles, Integer.MAX_VALUE));
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
