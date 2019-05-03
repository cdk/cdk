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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.openscience.cdk.smsd.interfaces.IFinalMapping;

/**
 * Class that stores raw mapping(s) after each algorithm is executed.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class FinalMappings implements IFinalMapping {

    private static List<Map<Integer, Integer>> mappings = null;
    private static FinalMappings               instance = null;

    protected FinalMappings() {
        mappings = new ArrayList<Map<Integer, Integer>>();
    }

    /**
     * Stores mapping solutions
     * @return instance of this object
     */
    synchronized public static FinalMappings getInstance() {
        if (instance == null) {
            instance = new FinalMappings();
        }
        return instance;
    }

    /**
     *  {@inheritDoc}
     *
     */
    @Override
    synchronized public void add(Map<Integer, Integer> mapping) {
        mappings.add(mapping);
    }

    /**
     * {@inheritDoc}
     * @param list list of mappings
     */
    @Override
    synchronized public final void set(List<Map<Integer, Integer>> list) {
        this.clear();
        mappings.addAll(list);
    }

    /**
     *  {@inheritDoc}
     *
     */
    @Override
    synchronized public Iterator<Map<Integer, Integer>> getIterator() {
        Iterator<Map<Integer, Integer>> iterator = mappings.iterator();
        return iterator;
    }

    /**
     *  {@inheritDoc}
     *
     */
    @Override
    synchronized public void clear() {
        FinalMappings.mappings.clear();
    }

    /**
     *  {@inheritDoc}
     *
     */
    @Override
    synchronized public List<Map<Integer, Integer>> getFinalMapping() {
        return mappings;
    }

    /**
     *  {@inheritDoc}
     *
     */
    @Override
    synchronized public int getSize() {
        return mappings.size();
    }
}
