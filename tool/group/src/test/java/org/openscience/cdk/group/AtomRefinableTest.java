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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.SilentChemObjectBuilder;

/**
 * Test the refinable wrapper around atom containers.
 * 
 * @author maclean
 * @cdk.module group
 */
public class AtomRefinableTest {
    
    public static IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
    
    @Test
    public void getVertexCount() {
        IAtomContainer ac = makeAtomContainer("CCCC");
        AtomRefinable refinable = new AtomRefinable(ac);
        assertEquals(ac.getAtomCount(), refinable.getVertexCount());
    }
    
    @Test
    public void getConnectivity() {
        String acpString = "C0C1C2C3 0:1(1),1:2(2),2:3(3)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomRefinable refinable = new AtomRefinable(ac);
        assertEquals(1, refinable.getConnectivity(0, 1));
        assertEquals(2, refinable.getConnectivity(1, 2));
        assertEquals(3, refinable.getConnectivity(2, 3));
    }
    
    @Test
    public void neighboursInBlockForSingleBonds() {
        String acpString = "C0C1C2C3 0:1(1),0:3(1),1:2(1),2:3(1)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomRefinable refinable = new AtomRefinable(ac);
        
        Invariant invariant = refinable.neighboursInBlock(set(0, 2), 1);
        assertTrue(invariant instanceof IntegerInvariant);
        assertEquals(new IntegerInvariant(2), invariant);
    }
    
    @Test
    public void neighboursInBlockForMultipleBonds() {
        String acpString = "C0C1C2C3C4 0:1(1),0:2(2),0:3(1),1:4(1),2:4(1),3:4(2)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomRefinable refinable = new AtomRefinable(ac);
        
        Invariant invariant = refinable.neighboursInBlock(set(1, 2), 0);
        assertTrue(invariant instanceof IntegerListInvariant);
        assertEquals(new IntegerListInvariant(new int[] {1, 1}), invariant);
    }
    
    @Test
    public void neighboursInBlockForMultipleBondsIgnoringBondOrders() {
        String acpString = "C0C1C2C3C4 0:1(1),0:2(2),0:3(1),1:4(1),2:4(1),3:4(2)";
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomRefinable refinable = new AtomRefinable(ac, false, true);
        
        Invariant invariant = refinable.neighboursInBlock(set(1, 2), 0);
        assertTrue(invariant instanceof IntegerInvariant);
        assertEquals(new IntegerInvariant(2), invariant);
    }
    
    private Set<Integer> set(int... elements) {
        Set<Integer> block = new HashSet<Integer>();
        for (int element : elements) {
            block.add(element);
        }
        return block;
    }
    
    @Test
    public void getElementPartitionTest() {
        String acpString = "C0N1C2P3C4N5";
        Partition expected = Partition.fromString("0,2,4|1,5|3");
        
        IAtomContainer ac = AtomContainerPrinter.fromString(acpString, builder);
        AtomRefinable refinable = new AtomRefinable(ac);
        
        Partition elPartition = refinable.getInitialPartition();
        Assert.assertEquals(expected, elPartition);
    }
    
    @Test
    public void oddEvenElementPartitionTest() {
        IAtomContainer ac = makeAtomContainer("CNCNCN");
        Partition expected = Partition.fromString("0,2,4|1,3,5");
        
        AtomRefinable refinable = new AtomRefinable(ac);
        
        Partition elPartition = refinable.getInitialPartition();
        Assert.assertEquals(expected, elPartition);
    }
    
    @Test
    public void orderedElementPartitionTest() {
        IAtomContainer ac = makeAtomContainer("CCCCNNNNOOOO");
        Partition expected = Partition.fromString("0,1,2,3|4,5,6,7|8,9,10,11");
        
        AtomRefinable refinable = new AtomRefinable(ac);
        
        Partition elPartition = refinable.getInitialPartition();
        Assert.assertEquals(expected, elPartition);
    }
    
    @Test
    public void disorderedElementPartitionTest() {
        IAtomContainer ac = makeAtomContainer("NNNNCCCCOOOO");
        Partition expected = Partition.fromString("4,5,6,7|0,1,2,3|8,9,10,11");
        
        AtomRefinable refinable = new AtomRefinable(ac);
        
        Partition elPartition = refinable.getInitialPartition();
        Assert.assertEquals(expected, elPartition);
    }
    
    private IAtomContainer makeAtomContainer(String elements) {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < elements.length(); i++) {
            String element = String.valueOf(elements.charAt(i));
            ac.addAtom(builder.newInstance(IAtom.class, element));
        }
        return ac;
    }

}
