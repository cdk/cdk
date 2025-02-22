package uk.ac.ebi.beam;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** @author John May */
public class IntSetTest {

    @Test public void universe() throws Exception {
        IntSet universe = IntSet.universe();
        Random rnd = new Random();
        for (int i = 0; i < 1000; i++) {
            assertTrue(universe.contains(rnd.nextInt()));
        }
    }
    
    @Test public void empty() throws Exception {
        IntSet empty = IntSet.empty();
        Random rnd = new Random();
        for (int i = 0; i < 1000; i++) {
            assertFalse(empty.contains(rnd.nextInt()));
        }
    }
    
    @Test public void singleton() throws Exception {
        IntSet one = IntSet.allOf(1);
        assertFalse(one.contains(0));
        assertTrue(one.contains(1));
        assertFalse(one.contains(2));
        assertFalse(one.contains(3));
        assertFalse(one.contains(4));
        assertFalse(one.contains(5));
    }
    
    @Test public void allOf() throws Exception {
        IntSet one = IntSet.allOf(4, 2);
        assertFalse(one.contains(0));
        assertFalse(one.contains(1));
        assertTrue(one.contains(2));
        assertFalse(one.contains(3));
        assertTrue(one.contains(4));
        assertFalse(one.contains(5));
    }
    
    @Test public void noneOf() throws Exception {
        IntSet one = IntSet.noneOf(0, 1, 3, 5);
        assertFalse(one.contains(0));
        assertFalse(one.contains(1));
        assertTrue(one.contains(2));
        assertFalse(one.contains(3));
        assertTrue(one.contains(4));
        assertFalse(one.contains(5));
        assertTrue(one.contains(6));
    }
}
