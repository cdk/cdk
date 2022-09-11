/* Copyright (C) 1997-2007  The Chemistry Development Kit (CKD) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All I ask is that proper credit is given for my work, which includes
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
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test-fingerprint
 */
class StandardSubstructureSetsTest extends CDKTestCase {

    @Test
    void testGetFunctionalGroupSubstructureSet() throws Exception {
        String[] smarts;
        smarts = StandardSubstructureSets.getFunctionalGroupSMARTS();
        Assertions.assertNotNull(smarts);
        Assertions.assertEquals(307, smarts.length);
    }

    @Test
    void testGetCountableMACCSSMARTSSubstructureSet() throws Exception {
        String[] smarts;
        smarts = StandardSubstructureSets.getCountableMACCSSMARTS();
        Assertions.assertNotNull(smarts);
        Assertions.assertEquals(142, smarts.length); // currently fragment pattern is ignored
    }

}
