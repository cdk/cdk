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

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit testing for the {@link LabelContainer} class.
 * 
 * @author     Syed Asad Rahman
 * @cdk.module test-smsd
 */
public class LabelContainerTest {

    @Test
    public void testGetInstance() {
        Assert.assertNotNull(LabelContainer.getInstance());
    }

    /**
     * Test of addLabel method, of class LabelContainer.
     */
    @Test
    public void testAddLabel() {
        System.out.println("addLabel");
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        assertEquals(3, instance.getSize());
        Integer expectedValue = 2;
        assertEquals(expectedValue, instance.getLabelID("R3"));
    }

    /**
     * Test of getLabelID method, of class LabelContainer.
     */
    @Test
    public void testGetLabelID() {
        System.out.println("getLabelID");
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        Integer expectedValue = 2;
        assertEquals(expectedValue, instance.getLabelID("R3"));
    }

    /**
     * Test of getLabel method, of class LabelContainer.
     */
    @Test
    public void testGetLabel() {
        System.out.println("getLabel");
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        Integer index = 2;
        String result = instance.getLabel(index);
        assertEquals(label, result);
    }

    /**
     * Test of getSize method, of class LabelContainer.
     */
    @Test
    public void testGetSize() {
        System.out.println("getSize");
        String label = "R3";
        LabelContainer instance = new LabelContainer();
        instance.addLabel(label);
        int expectedValue = 3;
        int result = instance.getSize();
        assertEquals(expectedValue, result);
    }
}
