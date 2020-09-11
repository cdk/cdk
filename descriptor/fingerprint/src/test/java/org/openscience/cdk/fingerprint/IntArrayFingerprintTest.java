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

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class IntArrayFingerprintTest extends AbstractBitFingerprintTest {

    public IntArrayFingerprintTest() throws Exception {
        super(IntArrayFingerprint.class);
    }

    @Test
    public void testSetBit() {
        IntArrayFingerprint fp = new IntArrayFingerprint();
        fp.set(1, true);
        fp.set(55, true);
        fp.set(219, true);
        fp.set(3, true);
        fp.set(24, true);
        org.hamcrest.MatcherAssert.assertThat(new int[]{1, 3, 24, 55, 219},
                          is(fp.getSetbits()));
        fp.set(24, false);
        org.hamcrest.MatcherAssert.assertThat(new int[]{1, 3, 55, 219},
                          is(fp.getSetbits()));
        fp.set(26, true);
        org.hamcrest.MatcherAssert.assertThat(new int[]{1, 3, 26, 55, 219},
                          is(fp.getSetbits()));
    }

}
