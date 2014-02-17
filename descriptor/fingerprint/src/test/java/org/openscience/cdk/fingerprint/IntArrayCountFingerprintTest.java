package org.openscience.cdk.fingerprint;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class IntArrayCountFingerprintTest {
	
	@Test
	public void testMerge() {
		IntArrayCountFingerprint fp1 = new IntArrayCountFingerprint(
                                           new HashMap<String, Integer>() {{
                                        	      put("A", 1);
                                        	      put("B", 2);
                                        	      put("C", 3);
                                           }}
                                       );
		IntArrayCountFingerprint fp2 = new IntArrayCountFingerprint(
                                           new HashMap<String, Integer>() {{
                                        	      put("A", 1);
                                        	      put("E", 2);
                                        	      put("F", 3);
                                           }}
                                       );
		
		Map<Integer, Integer> hashCounts = new HashMap<Integer, Integer>(); 
		for ( int i = 0 ; i < fp1.numOfPopulatedbins() ; i++ ) {
			hashCounts.put( fp1.getHash(i), fp1.getCount(i) );
		}
		for ( int i = 0 ; i < fp2.numOfPopulatedbins() ; i++ ) { 
			int hash = fp2.getHash(i);
			Integer count = hashCounts.get(hash);
			if ( count == null ) {
				count = 0;
			}
			hashCounts.put(hash, count + fp2.getCount(i));
		}
		
		fp1.merge(fp2);
		
		assertEquals(fp1.numOfPopulatedbins(), hashCounts.size());
		
		for ( int i = 0 ; i < fp1.numOfPopulatedbins() ; i++ ) {
			Integer hash = fp1.getHash(i);
			Integer count = fp1.getCount(i);
			assertTrue( hashCounts.containsKey(hash) );
			assertEquals( count, hashCounts.get(hash) );
		}
		
		int Aindex = Arrays.binarySearch(fp1.hitHashes, "A".hashCode());
		assertTrue("A should be in the fingerprint", Aindex >= 0);
		assertEquals( fp1.numOfHits[Aindex], 2);
		int Cindex = Arrays.binarySearch(fp1.hitHashes, "C".hashCode());
		assertTrue("C should be in the fingerprint", Cindex >= 0);
		assertEquals( fp1.numOfHits[Cindex], 3);
	} 

}
