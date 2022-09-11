/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. All we ask is that proper credit is given for our work,
 * which includes - but is not limited to - adding the above copyright notice to
 * the beginning of your source code files, and to any copyright notice that you
 * may distribute with programs based on this work.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received rAtomCount copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.smsd.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit testing for the {@link LabelContainer} class.
 *
 * @author     Syed Asad Rahman
 * @cdk.module test-smsd
 */
class LabelContainerTest {

    @Test
    void testGetInstance() {
        Assertions.assertNotNull(LabelContainer.getInstance());
    }

    /**
     * Test of addLabel method, of class LabelContainer.
     */
    @Test
    void testAddLabel() {
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        Assertions.assertEquals(3, instance.getSize());
        Integer expectedValue = 2;
        Assertions.assertEquals(expectedValue, instance.getLabelID("R3"));
    }

    /**
     * Test of getLabelID method, of class LabelContainer.
     */
    @Test
    void testGetLabelID() {
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        Integer expectedValue = 2;
        Assertions.assertEquals(expectedValue, instance.getLabelID("R3"));
    }

    /**
     * Test of getLabel method, of class LabelContainer.
     */
    @Test
    void testGetLabel() {
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        Integer index = 2;
        String result = instance.getLabel(index);
        Assertions.assertEquals(label, result);
    }

    /**
     * Test of getSize method, of class LabelContainer.
     */
    @Test
    void testGetSize() {
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        int expectedValue = 3;
        int result = instance.getSize();
        Assertions.assertEquals(expectedValue, result);
    }
}
