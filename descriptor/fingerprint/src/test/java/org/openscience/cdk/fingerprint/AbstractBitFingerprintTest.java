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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.openscience.cdk.CDKTestCase;

import org.junit.Test;

public abstract class AbstractBitFingerprintTest extends CDKTestCase {

    protected IBitFingerprint                bitsetFP;
    private Class<? extends IBitFingerprint> C;

    public AbstractBitFingerprintTest(Class<? extends IBitFingerprint> C) throws Exception {
        this.C = C;
        bitsetFP = C.newInstance();
    }

    @Test
    public void testCreate() {
        assertFalse(bitsetFP.get(0));
    }

    @Test
    public void testGetAndSet() {
        testCreate();
        bitsetFP.set(1, true);
        assertTrue(bitsetFP.get(1));
        assertFalse(bitsetFP.get(2));
        bitsetFP.set(3, true);
        assertTrue(bitsetFP.get(3));
    }

    private IBitFingerprint createFP2() throws Exception {
        IBitFingerprint fp = C.newInstance();
        fp.set(2, true);
        fp.set(3, true);
        return fp;
    }

    @Test
    public void testAnd() throws Exception {
        testGetAndSet();
        bitsetFP.and(createFP2());
        assertFalse(bitsetFP.get(0));
        assertFalse(bitsetFP.get(1));
        assertFalse(bitsetFP.get(2));
        assertTrue(bitsetFP.get(3));
    }

    @Test
    public void testOr() throws Exception {
        testGetAndSet();
        bitsetFP.or(createFP2());
        assertFalse(bitsetFP.get(0));
        assertTrue(bitsetFP.get(1));
        assertTrue(bitsetFP.get(2));
        assertTrue(bitsetFP.get(3));
    }

    @Test
    public void testEquals() throws Exception {
        IBitFingerprint fp1 = C.newInstance();
        IBitFingerprint fp2 = C.newInstance();

        for (IBitFingerprint fp : new IBitFingerprint[]{fp1, fp2}) {
            fp.set(0, true);
            fp.set(1, false);
            fp.set(2, true);
        }
        assertTrue("identical fingerprints should be equal", fp1.equals(fp2));
        assertFalse("different fingerprints should not be equal", bitsetFP.equals(fp1));
        assertTrue("equal fingerprints must have same hashcode", fp1.hashCode() == fp2.hashCode());
    }
}
