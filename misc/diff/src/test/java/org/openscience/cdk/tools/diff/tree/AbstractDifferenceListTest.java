/* Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.tools.diff.tree;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @cdk.module test-diff
 */
public class AbstractDifferenceListTest extends CDKTestCase {

    @Test
    public void testConstructor() {
        DifferenceClass diffClass = new DifferenceClass();
        Assert.assertNotNull(diffClass);
    }

    @Test
    public void testAddChild() {
        DifferenceClass diffClass = new DifferenceClass();
        diffClass.addChild(StringDifference.construct("Foo", "Bar1", "Bar2"));
        Assert.assertEquals(1, diffClass.childCount());

        diffClass.addChild(null);
        Assert.assertEquals(1, diffClass.childCount());
    }

    @Test
    public void testChildDiffs() {
        DifferenceClass diffClass = new DifferenceClass();
        List<IDifference> diffs = new ArrayList<IDifference>();
        diffs.add(StringDifference.construct("Foo", "Bar1", "Bar2"));
        diffs.add(IntegerDifference.construct("Foo", 1, 2));
        diffClass.addChildren(diffs);
        Assert.assertEquals(2, diffClass.childCount());
        Iterator<IDifference> diffs2 = diffClass.getChildren().iterator();
        int count = 0;
        while (diffs2.hasNext()) {
            diffs2.next();
            count++;
        }
        Assert.assertEquals(2, count);
    }

    private class DifferenceClass extends AbstractDifferenceList {

    }

}
