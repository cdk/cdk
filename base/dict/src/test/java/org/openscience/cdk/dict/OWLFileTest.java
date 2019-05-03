/* Copyright (C) 2012-2013  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.dict;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

/**
 * @cdk.module test-dict
 */
public class OWLFileTest extends AbstractDictionaryTest {

    @Before
    public void setTestClass() {
        super.setTestClass(new OWLFile());
    }

    @After
    public void testTestClass() {
        Assert.assertTrue(getTestClass().getClass().getName().endsWith(".OWLFile"));
    }

}
