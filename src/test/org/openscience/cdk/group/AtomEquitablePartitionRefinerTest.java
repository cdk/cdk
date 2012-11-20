package org.openscience.cdk.group;

import java.util.ArrayList;
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
public class AtomEquitablePartitionRefinerTest extends CDKTestCase {
    
    public Map<Integer, Integer> makeMap(int... keyValPairs) {
        assert keyValPairs.length % 2 == 0;
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < keyValPairs.length; i += 2) {
            map.put(keyValPairs[i], keyValPairs[i + 1]);
        }
        return map;
    }
    
    public List<Map<Integer, Integer>> makeExampleTable() {
        List<Map<Integer, Integer>> table = new ArrayList<Map<Integer, Integer>>();
        table.add(makeMap(1, 2, 2, 1));
        table.add(makeMap(0, 2, 3, 1));
        table.add(makeMap(0, 1, 3, 1));
        table.add(makeMap(1, 1, 2, 1));
        return table;
    }
    
    @Test
    public void constructorTest() {
        AtomEquitablePartitionRefiner refiner = 
                new AtomEquitablePartitionRefiner(makeExampleTable());
        Assert.assertNotNull(refiner);
    }
    
    @Test
    public void getVertexCountTest() {
        AtomEquitablePartitionRefiner refiner = 
                new AtomEquitablePartitionRefiner(makeExampleTable());
        Assert.assertEquals(4, refiner.getVertexCount());
    }
    
    @Test
    public void neighboursInBlockTest() {
        AtomEquitablePartitionRefiner refiner = 
                new AtomEquitablePartitionRefiner(makeExampleTable());
        Set<Integer> block = new HashSet<Integer>();
        block.add(1);
        block.add(2);
        block.add(3);
        Assert.assertEquals(2, refiner.neighboursInBlock(block, 0));
    }
    
    @Test
    public void refineTest() {
        AtomEquitablePartitionRefiner refiner = 
                new AtomEquitablePartitionRefiner(makeExampleTable());
        Partition coarser = Partition.fromString("[0|1,2,3]");
        Partition finer = refiner.refine(coarser);
        Partition expected = Partition.fromString("[0|1,2|3]");
        Assert.assertEquals(expected, finer);
    }

}
