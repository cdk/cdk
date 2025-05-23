/* Copyright (C) 2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.manipulator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IRing;

/**
 */
class RingSizeComparatorTest extends CDKTestCase {

    RingSizeComparatorTest() {
        super();
    }

    @Test
    void testRingSizeComparator_int() {
        RingSizeComparator comp = new RingSizeComparator(RingSizeComparator.LARGE_FIRST);
        Assertions.assertNotNull(comp);
    }

    @Test
    void testCompare() {
        IChemObjectBuilder builder = DefaultChemObjectBuilder.getInstance();
        IRing cycloPentane = builder.newInstance(IRing.class, 5, "C");
        IRing cycloHexane = builder.newInstance(IRing.class, 6, "C");
        IRing cycloHexane2 = builder.newInstance(IRing.class, 6, "C");

        RingSizeComparator ringSizeComparator = new RingSizeComparator(RingSizeComparator.LARGE_FIRST);
        Assertions.assertTrue(ringSizeComparator.compare(cycloHexane, cycloPentane) == -1);
        Assertions.assertTrue(ringSizeComparator.compare(cycloPentane, cycloHexane) == 1);
        Assertions.assertTrue(ringSizeComparator.compare(cycloHexane, cycloHexane2) == 0);

        ringSizeComparator = new RingSizeComparator(RingSizeComparator.SMALL_FIRST);
        Assertions.assertTrue(ringSizeComparator.compare(cycloHexane, cycloPentane) == 1);
        Assertions.assertTrue(ringSizeComparator.compare(cycloPentane, cycloHexane) == -1);
        Assertions.assertTrue(ringSizeComparator.compare(cycloHexane, cycloHexane2) == 0);
    }

}
