/* Copyright (C) 2005-2009  Egon Willighagen <egonw@users.sf.net>
 *                    2007  Rajarshi Guha <rajarshi@users.sf.net>
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

import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.test.tools.AbstractLoggingToolTest;

/**
 * @cdk.module test-log4j
 */
public class Log4jLoggingToolTest extends AbstractLoggingToolTest {

    @BeforeClass
    public static void ensureLog4JConfigured() {
        Configurator.reconfigure();
    }

    @Override
    public Log4jLoggingTool getLoggingTool() {
        String originalValue = System.getProperty("cdk.debugging");
        System.setProperty("cdk.debugging", "false");
        Log4jLoggingTool logger = new Log4jLoggingTool(this);
        if (originalValue != null) System.setProperty("cdk.debugging", originalValue);
        return logger;
    }

    @Test
    public void testLoggingTool() throws Exception {
        Log4jLoggingTool logger = new Log4jLoggingTool();
        Assert.assertNotNull(logger);
    }

    @Test
    public void testLoggingTool_Class() throws Exception {
        Log4jLoggingTool logger = new Log4jLoggingTool(this.getClass());
        Assert.assertNotNull(logger);
    }

    @Test
    public void testClass$_String() throws Exception {
        // no idea why the Coverage test requires this test
        Assert.assertTrue(true);
    }

    @Test
    public void testConfigureLog4j() throws Exception {
        Log4jLoggingTool.configureLog4j();
    }

    @Test
    public void testDebug_Object() throws Exception {
        Log4jLoggingTool logger = getLoggingTool();
        logger.debug(this);
    }

    @Test
    public void testCreate() throws Exception {
        ILoggingTool logger = Log4jLoggingTool.create(this.getClass());
        Assert.assertNotNull(logger);
    }
}
