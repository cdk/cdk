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

import org.junit.jupiter.api.Assertions;
import org.openscience.cdk.test.CDKTestCase;

import org.junit.jupiter.api.Test;

abstract class AbstractBitFingerprintTest extends CDKTestCase {

    private IBitFingerprint                bitsetFP;
    private final Class<? extends IBitFingerprint> C;

    AbstractBitFingerprintTest(Class<? extends IBitFingerprint> C) throws Exception {
        this.C = C;
        bitsetFP = C.newInstance();
    }

    @Test
    void testCreate() {
        Assertions.assertFalse(bitsetFP.get(0));
    }

    @Test
    void testGetAndSet() {
        testCreate();
        bitsetFP.set(1, true);
        Assertions.assertTrue(bitsetFP.get(1));
        Assertions.assertFalse(bitsetFP.get(2));
        bitsetFP.set(3, true);
        Assertions.assertTrue(bitsetFP.get(3));
    }

    private IBitFingerprint createFP2() throws Exception {
        IBitFingerprint fp = C.newInstance();
        fp.set(2, true);
        fp.set(3, true);
        return fp;
    }

    @Test
    void testAnd() throws Exception {
        testGetAndSet();
        bitsetFP.and(createFP2());
        Assertions.assertFalse(bitsetFP.get(0));
        Assertions.assertFalse(bitsetFP.get(1));
        Assertions.assertFalse(bitsetFP.get(2));
        Assertions.assertTrue(bitsetFP.get(3));
    }

    @Test
    void testOr() throws Exception {
        testGetAndSet();
        bitsetFP.or(createFP2());
        Assertions.assertFalse(bitsetFP.get(0));
        Assertions.assertTrue(bitsetFP.get(1));
        Assertions.assertTrue(bitsetFP.get(2));
        Assertions.assertTrue(bitsetFP.get(3));
    }

    @Test
    void testEquals() throws Exception {
        IBitFingerprint fp1 = C.newInstance();
        IBitFingerprint fp2 = C.newInstance();

        for (IBitFingerprint fp : new IBitFingerprint[]{fp1, fp2}) {
            fp.set(0, true);
            fp.set(1, false);
            fp.set(2, true);
        }
        Assertions.assertTrue(fp1.equals(fp2), "identical fingerprints should be equal");
        Assertions.assertFalse(bitsetFP.equals(fp1), "different fingerprints should not be equal");
        Assertions.assertTrue(fp1.hashCode() == fp2.hashCode(), "equal fingerprints must have same hashcode");
    }
}
