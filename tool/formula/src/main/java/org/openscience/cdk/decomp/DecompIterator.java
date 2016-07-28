/*
 *  This file is part of the SIRIUS library for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2015 Kai Dührkop
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with SIRIUS.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openscience.cdk.decomp;

/**
 * This class allows to iterate over decompositions instead of keeping all decompositions in memory. It's slightly
 * slower than generating all decompositions within a single loop, but it offers more flexibility (i.e. find a single
 * decomposition that satisfies some rule).
 * @param <T> type of the characters of the alphabet
 */
public interface DecompIterator<T> {

    /**
     * moves the iterator one step.
     * @return true, if a new decomposition is found. false, if the iterator reached the end.
     */
    public boolean next();

    /**
     * Give access to the current compomere. Please note that this array is only valid during the current iteration step
     * and might be changed afterwards. Furthermore, it is absolutely forbidden to write anything into this array.
     * However, you are free to clone the array and do anything with its copy.
     *
     * @return the compomere (a tuple (a_1,...,a_n) with a_i is the amount of the i-th character in the ordered alphabet
     */
    public int[] getCurrentCompomere();

    /**
     * @return the underlying (possibly unordered) alphabet
     */
    public Alphabet<T> getAlphabet();

    /**
     * The order of characters in the compomere might be different to the order of characters in the alphabet (i.e.
     * the characters in the compomere are always ordered by mass). This array maps the i-th character in the compomere
     * to it's appropiate index in the alphabet
     * @return mapping of positions in compomere to character indizes in alphabet
     */
    public int[] getAlphabetOrder();

    /**
     * Returns the character on the given position in the compomere
     * @param index index in compomere
     * @return corresponding character in alphabet
     */
    public T getCharacterAt(int index);

}
