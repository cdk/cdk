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


/**
 * This enum lists the search algorithms that SMSD supports. {@link #DEFAULT},
 * {@link #MCSPlus} and {@link #VFLibMCS} run the same VF based MCS search,
 * {@link #CDKMCS} runs the CDK UIT MCS search, and {@link #SubStructure} and
 * {@link #TurboSubStructure} run a substructure search.
 *
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public enum Algorithm {

    /**
     * Default SMSD algorithm; runs the same search as {@link #VFLibMCS}.
     */
    DEFAULT(0, "Default SMSD algorithm"),
    /**
     * Runs the same VF based search as {@link #VFLibMCS}, not the clique based
     * {@link org.openscience.cdk.smsd.algorithm.mcsplus.MCSPlus} search.
     */
    MCSPlus(1, "MCS Plus algorithm"),
    /**
     * VF Lib based MCS algorithm.
     */
    VFLibMCS(2, "VF Lib based MCS algorithm"),
    /**
     * CDK UIT MCS.
     */
    CDKMCS(3, "CDK UIT MCS"),
    /**
     * Substructure search will return all maps.
     */
    SubStructure(4, "Substructure search"),
    /**
     * Substructure search will return first map.
     */
    TurboSubStructure(5, "Turbo Mode- Substructure search");

    private final int    type;
    private final String description;

    Algorithm(int aStatus, String desc) {
        this.type = aStatus;
        this.description = desc;
    }

    /**
     * Returns type of algorithm.
     * @return type of algorithm
     */
    public int type() {
        return this.type;
    }

    /**
     * Returns short description of the algorithm.
     * @return description of the algorithm
     */
    public String description() {
        return this.description;
    }
}
