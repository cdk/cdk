package org.openscience.cdk.group;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * @author maclean
 * @cdk.module test-group
 */
public class BondEquitablePartitionRefinerTest extends CDKTestCase {
    
    public Map<Integer, List<Integer>> makeExampleTable() {
        Map<Integer, List<Integer>> table = new HashMap<Integer, List<Integer>>();
        table.put(0, Arrays.asList(1, 2));
        table.put(1, Arrays.asList(0, 3));
        table.put(2, Arrays.asList(0, 3));
        table.put(3, Arrays.asList(1, 2));
        return table;
    }
    
    @Test
    public void constructorTest() {
        BondEquitablePartitionRefiner refiner = 
                new BondEquitablePartitionRefiner(makeExampleTable());
        Assert.assertNotNull(refiner);
    }
    
    @Test
    public void getVertexCountTest() {
        BondEquitablePartitionRefiner refiner = 
                new BondEquitablePartitionRefiner(makeExampleTable());
        Assert.assertEquals(4, refiner.getVertexCount());
    }
    
    @Test
    public void neighboursInBlockTest() {
        BondEquitablePartitionRefiner refiner = 
                new BondEquitablePartitionRefiner(makeExampleTable());
        Set<Integer> block = new HashSet<Integer>();
        block.add(1);
        block.add(2);
        block.add(3);
        Assert.assertEquals(2, refiner.neighboursInBlock(block, 0));
    }
    
    @Test
    public void refineTest() {
        BondEquitablePartitionRefiner refiner = 
                new BondEquitablePartitionRefiner(makeExampleTable());
        Partition coarser = Partition.fromString("[0|1,2,3]");
        Partition finer = refiner.refine(coarser);
        Partition expected = Partition.fromString("[0|1,2|3]");
        Assert.assertEquals(expected, finer);
    }

}
