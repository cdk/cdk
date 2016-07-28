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
 * The alphabet for which a given weight is decomposed. An alphabet is a vector c_1..c_k of k characters of Type T.
 * It maps each character to a weight. It supports access by an index as well as by the character itself.
 *
 * @param <T> type of a single character in the alphabet
 */
interface Alphabet<T> {

    /**
     * @return size of the alphabet. Indizes of characters are 0..{@literal <} size
     */
    public int size();

    /**
     * @param i index of the character
     * @return weight of character c_i
     */
    public double weightOf(int i);

    /**
     * @param i index of the character
     * @return character c_i
     */
    public T get(int i);

    /**
     * Maps the character to its index. This operation should be fast, because internally a modified ordered
     * alphabet is used which have to be mapped back to the original alphabet
     * @param character
     * @return the index of the character
     */
    public int indexOf(T character);

}
