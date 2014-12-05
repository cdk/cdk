/* Copyright (C) 2011  Jonathan Alvarsson <jonalv@users.sf.net>
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
package org.openscience.cdk.fingerprint;

import java.io.Serializable;

/**
 * Interface for count fingerprint representations. The fingerprint is
 * regarded as a list of hashes and a list of counts where the the list of
 * counts keeps track of how many times the corresponding hash is found in
 * the fingerprint. So index refers to position in the list. The list must
 * be sorted in natural order (ascending).
 *
 * @author jonalv
 * @cdk.module     core
 * @cdk.githash
 */
public interface ICountFingerprint extends Serializable {

    /**
     * Returns the number of bits of this fingerprint.
     *
     * @return the size of the fingerprint.
     */
    public long size();

    /**
     * Returns the number of bins that are populated. This number is typically smaller
     * then the total number of bins.
     *
     * @return the number of populated bins
     * @see    #size()
     */
    public int numOfPopulatedbins();

    /**
     * Returns the count value for the bin with the given index.
     *
     * @param index the index of the bin to return the number of hits for.
     * @return the count for the bin with given index.
     */
    public int getCount(int index);

    /**
     * Returns the hash corresponding to the given index in the fingerprint.
     *
     * @param index the index of the bin to return the hash for.
     * @return the hash for the bin with the given index.
     */
    public int getHash(int index);

    /**
     * Merge all from <code>fp</code> into the current fingerprint.
     *
     * @param fp to be merged
     */
    public void merge(ICountFingerprint fp);

    /**
     * Changes behaviour, if true is given the count fingerprint will
     * behave as a bit fingerprint and return 0 or 1 for counts.
     *
     * @param behaveAsBitFingerprint
     */
    public void setBehaveAsBitFingerprint(boolean behaveAsBitFingerprint);

    /**
     * Whether the fingerprint contains the given hash.
     *
     * @param hash
     * @return true if the fingerprint contains the given hash, otherwise false.
     */
    public boolean hasHash(int hash);

    /**
     * Get the number of times a certain hash exists in the fingerprint.
     *
     * @param hash
     * @return the number associated with the given hash
     */
    public int getCountForHash(int hash);
}
