/* Copyright (C) 2006-2010 Syed Asad Rahman <asad@ebi.ac.uk>
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
package org.openscience.cdk.smsd.algorithm.mcgregor;

import java.util.Collections;
import java.util.List;

/**
 * Helper Class for McGregor algorithm.
 *
 * The second part of the program extents the mapping by the McGregor algorithm in case,
 * that not all atoms of molecule A and molecule B are mapped by the clique approach.
 * @cdk.module smsd
 * @cdk.githash
 * @author Syed Asad Rahman &lt;asad@ebi.ac.uk&gt;
 * @deprecated SMSD has been deprecated from the CDK with a newer, more recent
 *             version of SMSD is available at <a href="http://github.com/asad/smsd">http://github.com/asad/smsd</a>.
 */
@Deprecated
public class McgregorHelper {

    private final List<String>  cBondSetA;
    private final List<String>  cBondSetB;
    private final boolean       mappingCheckFlag;
    private final int           mappedAtomCount;
    private final List<Integer> mappedAtomsOrg;
    private final int           neighborBondNumA;
    private final int           neighborBondNumB;
    private final List<Integer> iBondNeighborAtomsA;
    private final List<Integer> iBondNeighborAtomsB;
    private final List<String>  cBondNeighborsA;
    private final List<String>  cBondNeighborsB;
    private final int           setNumA;
    private final int           setNumB;
    private final List<Integer> iBondSetA;
    private final List<Integer> iBondSetB;

    /**
     * Stores the variables
     * @param mappingCheckFlag
     * @param mappedAtomCount
     * @param mappedAtomsOrg
     * @param neighborBondNumA
     * @param neighborBondNumB
     * @param iBondNeighborAtomsA
     * @param iBondNeighborAtomsB
     * @param cBondNeighborsA
     * @param cBondNeighborsB
     * @param setNumA
     * @param setNumB
     * @param iBondSetA
     * @param iBondSetB
     * @param cBondSetA
     * @param cBondSetB
     */
    protected McgregorHelper(boolean mappingCheckFlag, int mappedAtomCount, List<Integer> mappedAtomsOrg,
                             int neighborBondNumA, int neighborBondNumB, List<Integer> iBondNeighborAtomsA,
                             List<Integer> iBondNeighborAtomsB, List<String> cBondNeighborsA, List<String> cBondNeighborsB, int setNumA,
                             int setNumB, List<Integer> iBondSetA, List<Integer> iBondSetB, List<String> cBondSetA,
                             List<String> cBondSetB) {
        this.cBondSetA = cBondSetA;
        this.cBondSetB = cBondSetB;
        this.mappingCheckFlag = mappingCheckFlag;
        this.mappedAtomCount = mappedAtomCount;
        this.mappedAtomsOrg = mappedAtomsOrg;
        this.neighborBondNumA = neighborBondNumA;
        this.neighborBondNumB = neighborBondNumB;
        this.iBondNeighborAtomsA = iBondNeighborAtomsA;
        this.iBondNeighborAtomsB = iBondNeighborAtomsB;
        this.cBondNeighborsA = cBondNeighborsA;
        this.cBondNeighborsB = cBondNeighborsB;
        this.setNumA = setNumA;
        this.setNumB = setNumB;
        this.iBondSetA = iBondSetA;
        this.iBondSetB = iBondSetB;

    }

    /**
     * @return the cBondSetA
     */
    protected List<String> getCBondSetA() {
        return Collections.unmodifiableList(cBondSetA);
    }

    /**
     * @return the cBondSetB
     */
    protected List<String> getCBondSetB() {
        return Collections.unmodifiableList(cBondSetB);
    }

    /**
     * @return the mappingCheckFlag
     */
    protected boolean isMappingCheckFlag() {
        return mappingCheckFlag;
    }

    /**
     * @return the mappedAtomCount
     */
    protected int getMappedAtomCount() {
        return mappedAtomCount;
    }

    /**
     * @return the mappedAtomsOrg
     */
    protected List<Integer> getMappedAtomsOrg() {
        return Collections.unmodifiableList(mappedAtomsOrg);
    }

    /**
     * @return the neighborBondNumA
     */
    protected int getNeighborBondNumA() {
        return neighborBondNumA;
    }

    /**
     * @return the neighborBondNumB
     */
    protected int getNeighborBondNumB() {
        return neighborBondNumB;
    }

    /**
     * @return the iBondNeighborAtomsA
     */
    protected List<Integer> getiBondNeighborAtomsA() {
        return Collections.unmodifiableList(iBondNeighborAtomsA);
    }

    /**
     * @return the iBondNeighborAtomsB
     */
    protected List<Integer> getiBondNeighborAtomsB() {
        return Collections.unmodifiableList(iBondNeighborAtomsB);
    }

    /**
     * @return the cBondNeighborsA
     */
    protected List<String> getcBondNeighborsA() {
        return Collections.unmodifiableList(cBondNeighborsA);
    }

    /**
     * @return the cBondNeighborsB
     */
    protected List<String> getcBondNeighborsB() {
        return Collections.unmodifiableList(cBondNeighborsB);
    }

    /**
     * @return the setNumA
     */
    protected int getSetNumA() {
        return setNumA;
    }

    /**
     * @return the iBondSetA
     */
    protected List<Integer> getIBondSetA() {
        return Collections.unmodifiableList(iBondSetA);
    }

    /**
     * @return the iBondSetB
     */
    protected List<Integer> getIBondSetB() {
        return Collections.unmodifiableList(iBondSetB);
    }

    int getsetNumB() {
        return setNumB;
    }
}
