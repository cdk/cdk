/* Copyright (C) 2009  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * The base class for permutors of atom containers, with a single abstract
 * method <code>containerFromPermutation</code> that should be implemented in
 * concrete derived classes.
 *
 * @author maclean
 * @cdk.githash
 * @cdk.created    2009-09-09
 * @cdk.keyword    permutation
 * @cdk.module     standard
 */
public abstract class AtomContainerPermutor extends Permutor implements Iterator<IAtomContainer> {

    /**
     * The atom container that is permuted at each step.
     */
    protected IAtomContainer atomContainer;

    /**
     * Start the permutor off with an initial atom container, and the size of
     * the permutation.
     *
     * @param atomContainer
     */
    public AtomContainerPermutor(int size, IAtomContainer atomContainer) {
        super(size);
        this.atomContainer = atomContainer;
    }

    /**
     * Convert a permutation (expressed as a list of numbers) into a permuted
     * atom container. This will differ depending on the desired effect of the
     * permutation (atoms or bonds, for example).
     *
     * @return the atom container corresponding to this permutation
     */
    public abstract IAtomContainer containerFromPermutation(int[] permutation);

    /**
     * Get a new container, but randomly skip forwards in the list of possible
     * permutations to generate it.
     *
     * @return a random next permuted atom container
     */
    public IAtomContainer randomNext() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            return this.containerFromPermutation(this.getRandomNextPermutation());
        }
    }

    /*
     * (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public IAtomContainer next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        } else {
            return this.containerFromPermutation(this.getNextPermutation());
        }
    }

    @Override
    public void remove() {
        // stupid method. not implemented.
    }

}
