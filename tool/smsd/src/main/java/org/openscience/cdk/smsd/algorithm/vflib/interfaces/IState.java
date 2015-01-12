/*
 * MX Cheminformatics Tools for Java
 *
 * Copyright (c) 2007-2009 Metamolecular, LLC
 *
 * http://metamolecular.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.vflib.interfaces;

import java.util.Map;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.smsd.algorithm.vflib.map.Match;

/**
 * Interface for the storing the states of the mapping in the VF algorithm.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public interface IState {

    /**
     * Returns the current mapping of query atoms onto target atoms.
     * This map is shared among all states obtained through nextState.
     *
     * @return the current mapping of query atoms onto target atoms
     */
    public Map<INode, IAtom> getMap();

    /**
     * Returns true if another candidate match can be found or
     * false otherwise.
     *
     * @return true if another candidate mapping can be found or
     * false otherwise.
     */
    public boolean hasNextCandidate();

    /**
     * Returns the next candidate match.
     *
     * @return the next candidate match.
     */
    public Match nextCandidate();

    /**
     * Returns true if the given match will work with the current
     * map, or false otherwise.
     *
     * @param match the match to consider
     * @return true if the given match will work with the current
     * map, or false otherwise.
     */
    public boolean isMatchFeasible(Match match);

    /**
     * Returns true if all atoms in the query molecule have been
     * mapped.
     *
     * @return true if all atoms in the query molecule have been
     * mapped.
     */
    public boolean isGoal();

    /**
     * Returns true if no match will come from this IState.
     *
     * @return true if no match will come from this IState
     */
    public boolean isDead();

    /**
     * Returns a state in which the atoms in match have been
     * added to the current mapping.
     *
     * @param match the match to consider.
     * @return  a state in which the atoms in match have been
     * added to the current mapping.
     */
    public IState nextState(Match match);

    /**
     * Returns this IState's atom map to its original condition.
     */
    public void backTrack();
}
