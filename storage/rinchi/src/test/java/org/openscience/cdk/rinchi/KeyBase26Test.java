/* Copyright (C) 2024 Beilstein-Institute
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
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
package org.openscience.cdk.rinchi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KeyBase26Test {

    @Test
    void getBase26Triplet_2703_DZZ() {
        assertEquals("DZZ", KeyBase26.getBase26Triplet(2703));
    }

    @Test
    void getBase26Triplet_2704_FAA() {
        assertEquals("FAA", KeyBase26.getBase26Triplet(2704));
    }
    
    @Test
    void getBase26Triplet_2704_ZZZ() {
        assertEquals("ZZZ", KeyBase26.getBase26Triplet(16383));
    }

    @Test
    void base26Triplet_701_BAZ_Test() {
        assertEquals("BAZ", KeyBase26.getBase26Triplet(701));
    }

    @Test
    void base26Triplet_676_BAA_Test() {
        assertEquals("BAA",  KeyBase26.getBase26Triplet(676));
    }

    @Test
    void base26Triplet_0_AAA_Test() {
        assertEquals("AAA", KeyBase26.getBase26Triplet(0));
    }

    @Test
    void base26Doublet_675_ZZ_Test() {
        assertEquals("ZZ", KeyBase26.getBase26Doublet(675));
    }

    @Test
    void base26Doublet_256_JW_Test() {
        assertEquals("JW", KeyBase26.getBase26Doublet(256));
    }
}