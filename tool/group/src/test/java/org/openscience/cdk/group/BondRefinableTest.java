/* Copyright (C) 2017  Gilleain Torrance <gilleain.torrance@gmail.com>
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
package org.openscience.cdk.group;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * @author maclean
 * @cdk.module group
 */
class BondRefinableTest {
    
    private static final IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    void getVertexCount() {
        String acpString = "C0C1C2C3 0:1(1),1:2(1),2:3(1)";
        BondRefinable bondRefinable = refinable(acpString);
        Assertions.assertEquals(3, bondRefinable.getVertexCount());
    }
    
    @Test
    void getConnectivity() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        BondRefinable bondRefinable = refinable(acpString);
        Assertions.assertEquals(1, bondRefinable.getConnectivity(0, 1));
        Assertions.assertEquals(1, bondRefinable.getConnectivity(0, 2));
        Assertions.assertEquals(1, bondRefinable.getConnectivity(1, 3));
        Assertions.assertEquals(1, bondRefinable.getConnectivity(2, 3));
    }
    
    @Test
    void neighboursInBlock() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        BondRefinable bondRefinable = refinable(acpString);
        Set<Integer> block = new HashSet<>();
        block.add(1);
        block.add(3);
        Assertions.assertEquals(new IntegerInvariant(1), bondRefinable.neighboursInBlock(block, 0));
        Assertions.assertEquals(new IntegerInvariant(1), bondRefinable.neighboursInBlock(block, 2));
    }
    
    @Test
    void getBondPartitionTest() {
        String acpString = "C0C1C2C3O4 0:1(2),0:4(1),1:2(1),2:3(2),3:4(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        BondRefinable refinable = new BondRefinable(ac);
        Partition bondPartition = refinable.getInitialPartition();
        Partition expected = Partition.fromString("0,3|1,4|2");
        Assertions.assertEquals(expected, bondPartition);
    }
    
    private BondRefinable refinable(String acpString) {
        return new BondRefinable(AtomContainerPrinter.fromString(acpString, builder));
    }

}
