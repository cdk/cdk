/*
 * Copyright (C) 2010  Mark Rijnbeek <mark_rynbeek@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may
 * distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.isomorphism.matchers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.exception.CDKException;

/**
 * Checks the functionality of the {@link org.openscience.cdk.isomorphism.matchers.RGroupList},
 * in particular setting valid 'occurrence' strings.
 *
 * @cdk.module test-isomorphism
 */
public class RGroupListTest extends CDKTestCase {

    @BeforeAll
    public static void setUp() {}

    @Test
    public void testOccurrenceCorrect() throws CDKException {
        RGroupList rgrLst = new RGroupList(1);
        rgrLst.setOccurrence("1, 3-7, 9, >11");
        Assertions.assertEquals(rgrLst.getOccurrence(), "1,3-7,9,>11");
    }

    @Test
    public void testOccurrenceNull() throws CDKException {
        RGroupList rgrLst = new RGroupList(1);
        rgrLst.setOccurrence(null);
        Assertions.assertEquals(rgrLst.getOccurrence(), RGroupList.DEFAULT_OCCURRENCE);
    }

    @Test
    public void testOccurrenceNumericValues() throws CDKException {
        RGroupList rgrLst = new RGroupList(1);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    rgrLst.setOccurrence("a,3,10");
                                });
    }

    @Test
    public void testOccurrenceNoNegativeNumber() throws CDKException {
        RGroupList rgrLst = new RGroupList(1);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    rgrLst.setOccurrence("-10");
                                });
    }

    @Test
    public void testOccurrenceNotSmallerThanZero() throws CDKException {
        RGroupList rgrLst = new RGroupList(1);
        Assertions.assertThrows(CDKException.class,
                                () -> {
                                    rgrLst.setOccurrence("<0");
                                });
    }

}
