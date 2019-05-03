/* Copyright (C) 2006-2010  Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.interfaces;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Interface for mappings.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public interface IFinalMapping {

    /**
     * Adds mapping to the mapping list
     * @param mapping List of all MCS mapping between a given
     * reactant and product
     */
    public void add(Map<Integer, Integer> mapping);

    /**
     * Sets mapping list
     * @param list List of all MCS mapping between a given
     * reactant and product
     */
    public void set(List<Map<Integer, Integer>> list);

    /**
     * Returns a mapping Iterator
     * @return Iterator of mappings
     */
    public Iterator<Map<Integer, Integer>> getIterator();

    /**
     * clear the mapping
     */
    public void clear();

    /**
     * Returns the stored mappings
     * @return get of MCS mapping List
     */
    public List<Map<Integer, Integer>> getFinalMapping();

    /**
     * Returns number of stored mappings
     * @return size of the mapping
     */
    public int getSize();
}
