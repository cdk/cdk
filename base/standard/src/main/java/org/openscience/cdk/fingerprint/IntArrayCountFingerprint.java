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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author jonalv
 * @cdk.module     standard
 * @cdk.githash
 */
public class IntArrayCountFingerprint implements ICountFingerprint {

    /**
     *
     */
    private static final long serialVersionUID = 8656072054328822795L;
    int[]                     hitHashes;
    int[]                     numOfHits;
    private boolean           behaveAsBitFingerprint;

    public IntArrayCountFingerprint() {
        hitHashes = new int[0];
        numOfHits = new int[0];
        behaveAsBitFingerprint = false;
    }

    public IntArrayCountFingerprint(Map<String, Integer> rawFingerprint) {
        Map<Integer, Integer> hashedFP = new HashMap<Integer, Integer>();
        for (String key : rawFingerprint.keySet()) {
            Integer hashedKey = key.hashCode();
            Integer count = hashedFP.get(hashedKey);
            if (count == null) {
                count = 0;
            }
            hashedFP.put(hashedKey, count + rawFingerprint.get(key));
        }
        List<Integer> keys = new ArrayList<Integer>(hashedFP.keySet());
        Collections.sort(keys);
        hitHashes = new int[keys.size()];
        numOfHits = new int[keys.size()];
        int i = 0;
        for (int key : keys) {
            hitHashes[i] = key;
            numOfHits[i] = hashedFP.get(key);
            i++;
        }
    }

    /**
     * Create an <code>IntArrayCountFingerprint</code> from a rawFingerprint
     * and if <code>behaveAsBitFingerprint</code> make it only return 0 or 1
     * as count thus behaving like a bit finger print.
     *
     * @param rawFingerprint
     * @param behaveAsBitFingerprint
     */
    public IntArrayCountFingerprint(Map<String, Integer> rawFingerprint, boolean behaveAsBitFingerprint) {
        this(rawFingerprint);
        this.behaveAsBitFingerprint = behaveAsBitFingerprint;
    }

    @Override
    public long size() {
        return 4294967296l;
    }

    @Override
    public int getCount(int index) {
        if (behaveAsBitFingerprint) {
            return numOfHits[index] == 0 ? 0 : 1;
        }
        return numOfHits[index];
    }

    @Override
    public int getHash(int index) {
        return hitHashes[index];
    }

    @Override
    public int numOfPopulatedbins() {
        return hitHashes.length;
    }

    @Override
    public void merge(ICountFingerprint fp) {
        Map<Integer, Integer> newFp = new HashMap<Integer, Integer>();
        for (int i = 0; i < hitHashes.length; i++) {
            newFp.put(hitHashes[i], numOfHits[i]);
        }
        for (int i = 0; i < fp.numOfPopulatedbins(); i++) {
            Integer count = newFp.get(fp.getHash(i));
            if (count == null) {
                count = 0;
            }
            newFp.put(fp.getHash(i), count + fp.getCount(i));
        }
        List<Integer> keys = new ArrayList<Integer>(newFp.keySet());
        Collections.sort(keys);
        hitHashes = new int[keys.size()];
        numOfHits = new int[keys.size()];
        int i = 0;
        for (Integer key : keys) {
            hitHashes[i] = key;
            numOfHits[i++] = newFp.get(key);
        }
    }

    @Override
    public void setBehaveAsBitFingerprint(boolean behaveAsBitFingerprint) {
        this.behaveAsBitFingerprint = behaveAsBitFingerprint;
    }

    @Override
    public boolean hasHash(int hash) {
        return Arrays.binarySearch(hitHashes, hash) >= 0;
    }

    @Override
    public int getCountForHash(int hash) {

        int index = Arrays.binarySearch(hitHashes, hash);
        if (index >= 0) {
            return numOfHits[index];
        }
        return 0;
    }
}
