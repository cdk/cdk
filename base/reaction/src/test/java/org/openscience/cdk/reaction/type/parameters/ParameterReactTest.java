/* Copyright (C) 2008  Miguel Rojas <miguelrojasch@yahoo.es>
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
package org.openscience.cdk.reaction.type.parameters;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;

/**
 * Tests for ParameterReact implementations.
 *
 * @cdk.module test-reaction
 */
public class ParameterReactTest extends CDKTestCase {

    /**
     *  Constructor for the ParameterReactTest object.
     */
    public ParameterReactTest() {
        super();
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    public void testParameterReact() {
        IParameterReact paramSet = new ParameterReact();
        Assert.assertNotNull(paramSet);
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    public void testSetParameter_boolean() {
        IParameterReact paramSet = new ParameterReact();

        paramSet.setParameter(Boolean.TRUE);
        Assert.assertTrue(paramSet.isSetParameter());

    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    public void testIsSetParameter() {
        IParameterReact paramSet = new ParameterReact();
        Assert.assertFalse(paramSet.isSetParameter());
    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    public void testSetValue_object() {
        IParameterReact paramSet = new ParameterReact();
        paramSet.setValue(null);
        Assert.assertNull(paramSet.getValue());

    }

    /**
     * Junit test.
     *
     * @throws Exception
     */
    @Test
    public void testGetValue() {
        IParameterReact paramSet = new ParameterReact();
        paramSet.setValue(new Object());
        Assert.assertNotNull(paramSet.getValue());
    }
}
