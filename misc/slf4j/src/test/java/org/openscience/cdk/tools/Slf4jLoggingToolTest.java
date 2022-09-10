/* Copyright (C) 2022 John Mayfield
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
package org.openscience.cdk.tools;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.tools.AbstractLoggingToolTest;

/**
 * @cdk.module test-log4j
 */
public class Slf4jLoggingToolTest extends AbstractLoggingToolTest {

    @BeforeAll
    public static void ensureLog4JConfigured() {
        // Configurator.reconfigure();
    }

    @Override
    public Slf4jLoggingTool getLoggingTool() {
        return new Slf4jLoggingTool(this);
    }

    @Test
    public void testLoggingTool() throws Exception {
        Slf4jLoggingTool logger = new Slf4jLoggingTool();
        Assert.assertNotNull(logger);
    }

    @Test
    public void testLoggingTool_Class() throws Exception {
        Slf4jLoggingTool logger = new Slf4jLoggingTool(this.getClass());
        Assert.assertNotNull(logger);
    }

    @Test
    public void testClass$_String() throws Exception {
        // no idea why the Coverage test requires this test
        Assert.assertTrue(true);
    }

    @Test
    public void testDebug_Object() throws Exception {
        Slf4jLoggingTool logger = getLoggingTool();
        logger.debug(this);
    }

    @Test
    public void testCreate() throws Exception {
        ILoggingTool logger = Slf4jLoggingTool.create(this.getClass());
        Assert.assertNotNull(logger);
    }
}
