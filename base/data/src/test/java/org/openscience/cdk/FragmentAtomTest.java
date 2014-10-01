/* Copyright (C) 2002-2007  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.AbstractFragmentAtomTest;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IFragmentAtom;
import org.openscience.cdk.interfaces.ITestObjectBuilder;

/**
 * Checks the functionality of the FragmentAtom.
 *
 * @cdk.module test-data
 */
public class FragmentAtomTest extends AbstractFragmentAtomTest {

    @BeforeClass
    public static void setUp() {
        setTestObjectBuilder(new ITestObjectBuilder() {

            @Override
            public IChemObject newTestObject() {
                return new FragmentAtom();
            }
        });
    }

    @Test
    public void testFragmentAtom() {
        IFragmentAtom a = new FragmentAtom();
        Assert.assertNotNull(a);
    }

}
