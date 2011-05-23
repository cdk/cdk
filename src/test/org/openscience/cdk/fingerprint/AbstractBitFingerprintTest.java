/* Copyright (C) 2011  Jonathan Alvarsson <jonalv@users.sf.net>
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
package org.openscience.cdk.fingerprint;

import org.
junit.Test;
import org.openscience.cdk.CDKTestCase;

import static org.junit.Assert.*;

public abstract class AbstractBitFingerprintTest extends CDKTestCase {

	protected IBitFingerprint bitsetFP;
	
	public AbstractBitFingerprintTest(Class<? extends IBitFingerprint> C) 
	       throws Exception {
		bitsetFP = C.newInstance();
	}
	
	@Test
	public void testCreate() {
		assertFalse( bitsetFP.get(0) );
	}
	
	@Test
	public void testGetAndSet() {
		testCreate();
		bitsetFP.set(1, true);
		assertTrue(  bitsetFP.get(1) );
		assertFalse( bitsetFP.get(2) );
		bitsetFP.set(3, true);
		assertTrue( bitsetFP.get(3) );
	}
	
	private IBitFingerprint createFP2() {
		IBitFingerprint fp = new BitSetFingerprint();
		fp.set(2, true);
		fp.set(3, true);
		return fp;
	}
	
	@Test
	public void testAnd() {
		testGetAndSet();
		bitsetFP.and(createFP2());
		assertFalse( bitsetFP.get(0) );
		assertFalse( bitsetFP.get(1) );
		assertFalse( bitsetFP.get(2) );
		assertTrue(  bitsetFP.get(3) );
	}
	
	@Test
	public void testOr() {
		testGetAndSet();
		bitsetFP.or(createFP2());
		assertFalse( bitsetFP.get(0) );
		assertTrue(  bitsetFP.get(1) );
		assertTrue(  bitsetFP.get(2) );
		assertTrue(  bitsetFP.get(3) );
	}
	
	@Test
	public void testEquals() {
		fail("Not yet implemented");
	}
}
