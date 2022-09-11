/* Copyright (C) 2010  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.debug;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.AbstractChemObjectBuilderTest;
import org.openscience.cdk.interfaces.IChemObjectBuilder;

/**
 * Checks the functionality of the {@link IChemObjectBuilder} {@link DebugChemObjectBuilder} implementation.
 *
 * @cdk.module test-datadebug
 */
public class DebugChemObjectBuilderTest extends AbstractChemObjectBuilderTest {

    @BeforeAll
    public static void setUp() {
        setRootObject(new DebugChemObject());
    }

    @Test
    public void testGetInstance() {
        Object builder = DebugChemObjectBuilder.getInstance();
        Assert.assertNotNull(builder);
        Assert.assertTrue(builder instanceof IChemObjectBuilder);
        Assert.assertTrue(builder.getClass().getName().contains("DebugChemObjectBuilder"));
    }

}
