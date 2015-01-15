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
 *
 * This class represents various algorithm type supported by SMSD.
 * Presently SMSD supports 5 different kinds of algorithms:
 *
 * <OL>
 * <lI>0: default,
 * <lI>1: MCSPlus,
 * <lI>2: VFLibMCS,
 * <lI>3: CDKMCS,
 * <lI>4: SubStructure
 * <lI>5: TurboSubStructure
 * </OL>
 *
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman <asad@ebi.ac.uk>
 */
public enum Algorithm {

    /**
     * Default SMSD algorithm.
     */
    DEFAULT(0, "Default SMSD algorithm"),
    /**
     * MCS Plus algorithm.
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

    /**
     * Compares algorithm types.
     * @param <status>
     * @param obj
     * @return status
     */
    public <status> int compareTo(Algorithm obj) {
        return 0;
    }
}
