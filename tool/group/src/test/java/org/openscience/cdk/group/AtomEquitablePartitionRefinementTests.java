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

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Test equitable refinement over atom refinables.
 * 
 * @author maclean
 *
 */
public class AtomEquitablePartitionRefinementTests {
    
    public static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void cube2eneWithoutBonds() {
        AtomRefinable ar = make(cubene(), true);
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(ar);
        Partition finer = refiner.refine(Partition.unit(8));
        Partition expected = Partition.unit(8);
        Assert.assertEquals(expected, finer);
    }
    
    @Test
    public void cube2eneWithBonds() {
        AtomRefinable ar = make(cubene(), false);
        EquitablePartitionRefiner refiner = new EquitablePartitionRefiner(ar);
        Partition finer = refiner.refine(Partition.unit(8));
        Partition expected = Partition.fromString("0,2,5,7|1,3,4,6");
        Assert.assertEquals(expected, finer);
    }
    
    private IAtomContainer cubene() {
        String acpString = "C0C1C2C3C4C5C6C7 0:1(1),0:2(2),0:4(1),1:3(1),"
                                          + "1:5(1),2:3(1),2:6(1),3:7(1),"
                                          + "4:5(1),4:6(1),5:7(2),6:7(1)";
        return AtomContainerPrinter.fromString(acpString, builder);
    }
    
    private AtomRefinable make(IAtomContainer atomContainer, boolean ignoreBonds) {
        return new AtomRefinable(atomContainer, false, ignoreBonds);
    }

}
