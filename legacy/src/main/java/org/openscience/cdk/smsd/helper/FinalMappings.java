/**
 *
 * Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute iterator and/or
 * modify iterator under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 *
 * This program is distributed in the hope that iterator will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.openscience.cdk.smsd.interfaces.IFinalMapping;

/**
 * Stores legacy raw index mappings for algorithms on the calling thread.
 *
 * <p>Every input mapping is copied. Getters return independent, mutable copies;
 * editing a returned map, list or iterator cannot alter the stored results.
 * This differs from the read-only snapshots returned by the MCS handlers.
 * The store does not validate chemical compatibility, index bounds or
 * injectivity; algorithm entry points are responsible for those checks.</p>
 *
 * <p>The thread-local instance isolates independent worker threads, but nested
 * algorithms on the same thread share this legacy store.</p>
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class FinalMappings implements IFinalMapping {

    private final List<Map<Integer, Integer>> mappings = new ArrayList<>();
    private static final ThreadLocal<FinalMappings> INSTANCE = ThreadLocal.withInitial(FinalMappings::new);

    /** Creates an empty mapping store. */
    protected FinalMappings() {

    }

    /**
     * Returns the mapping store belonging to the calling thread.
     *
     * @return the calling thread's store
     */
    public static FinalMappings getInstance() {
        return INSTANCE.get();
    }

    /**
     * Adds an independent copy of one index mapping.
     *
     * @param mapping source-to-target index mapping
     * @throws NullPointerException if the mapping is null
     */
    @Override
    synchronized public void add(Map<Integer, Integer> mapping) {
        mappings.add(new HashMap<>(Objects.requireNonNull(mapping, "mapping")));
    }

    /**
     * Replaces the stored mappings with independent copies.
     * All entries are copied before any stored result is cleared.
     *
     * @param list mappings to store, in the supplied order
     * @throws NullPointerException if the list or any mapping is null;
     *         existing results remain unchanged
     */
    @Override
    synchronized public final void set(List<Map<Integer, Integer>> list) {
        // Copy before clearing: callers may pass this store's own list or a view of it.
        List<Map<Integer, Integer>> copy = copyMappings(Objects.requireNonNull(list, "mappings"));
        this.clear();
        mappings.addAll(copy);
    }

    /**
     * Returns an iterator over independent copies of the stored mappings.
     *
     * @return a snapshot iterator; removal affects only that snapshot
     */
    @Override
    synchronized public Iterator<Map<Integer, Integer>> getIterator() {
        return copyMappings(mappings).iterator();
    }

    /**
     * Removes all stored mappings from this instance.
     *
     */
    @Override
    synchronized public void clear() {
        mappings.clear();
    }

    /**
     * Returns independent copies of the stored mappings.
     *
     * @return a mutable snapshot in insertion order
     */
    @Override
    synchronized public List<Map<Integer, Integer>> getFinalMapping() {
        return copyMappings(mappings);
    }

    private static List<Map<Integer, Integer>> copyMappings(List<Map<Integer, Integer>> source) {
        List<Map<Integer, Integer>> copy = new ArrayList<>(source.size());
        for (Map<Integer, Integer> mapping : source) {
            copy.add(new HashMap<>(Objects.requireNonNull(mapping, "mapping")));
        }
        return copy;
    }

    /**
     * Returns the number of stored mappings.
     *
     * @return mapping count
     */
    @Override
    synchronized public int getSize() {
        return mappings.size();
    }
}
