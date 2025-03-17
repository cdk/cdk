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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 */
class AbstractDifferenceListTest {

    @Test
    void testConstructor() {
        DifferenceClass diffClass = new DifferenceClass();
        Assertions.assertNotNull(diffClass);
    }

    @Test
    void testAddChild() {
        DifferenceClass diffClass = new DifferenceClass();
        diffClass.addChild(StringDifference.construct("Foo", "Bar1", "Bar2"));
        Assertions.assertEquals(1, diffClass.childCount());

        diffClass.addChild(null);
        Assertions.assertEquals(1, diffClass.childCount());
    }

    @Test
    void testChildDiffs() {
        DifferenceClass diffClass = new DifferenceClass();
        List<IDifference> diffs = new ArrayList<>();
        diffs.add(StringDifference.construct("Foo", "Bar1", "Bar2"));
        diffs.add(IntegerDifference.construct("Foo", 1, 2));
        diffClass.addChildren(diffs);
        Assertions.assertEquals(2, diffClass.childCount());
        Iterator<IDifference> diffs2 = diffClass.getChildren().iterator();
        int count = 0;
        while (diffs2.hasNext()) {
            diffs2.next();
            count++;
        }
        Assertions.assertEquals(2, count);
    }

    private class DifferenceClass extends AbstractDifferenceList {

    }

}
