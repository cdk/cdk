/*
 * Copyright (c) 2021 Mehmet Aziz Yirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
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

package org.openscience.cdk.structgen.maygen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * Unit test class for the BundaryConditions class of MAYGEN. 
 * </p>
 *
 * @author MehmetAzizYirik <mehmetazizyirik@outlook.com> <0000-0001-7520-7215@orcid.org>
 * 
 * @cdk.module structgen
 *
 */
class BoundaryConditionsTest {

    @Test
    void detectTripleBonds() {
        Assertions.assertFalse(BoundaryConditions.detectTripleBonds(new int[][] {}));
    }

    @Test
    void detectAdjacentDoubleBonds() {
        Assertions.assertFalse(BoundaryConditions.detectAdjacentDoubleBonds(new int[][] {}));
    }

    @Test
    void detectAllenes() {
        Assertions.assertFalse(BoundaryConditions.detectAllenes(new int[][] {}, new String[] {}));
    }

    @Test
    void boundaryConditionCheck() {
        Assertions.assertTrue(BoundaryConditions.boundaryConditionCheck(new int[][] {}, new String[] {}));
    }
}
