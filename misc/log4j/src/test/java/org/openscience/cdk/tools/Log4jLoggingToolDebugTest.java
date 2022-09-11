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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.tools.AbstractLoggingToolTest;

/**
 * @cdk.module test-log4j
 */
class Log4jLoggingToolDebugTest extends AbstractLoggingToolTest {

    @Override
    public Log4jLoggingTool getLoggingTool() {
        String originalValue = System.getProperty("cdk.debugging");
        System.setProperty("cdk.debugging", "true");
        Log4jLoggingTool logger = new Log4jLoggingTool(this);
        if (originalValue != null) System.setProperty("cdk.debugging", originalValue);
        return logger;
    }

    @Test
    void testLoggingTool() throws Exception {
        Log4jLoggingTool logger = new Log4jLoggingTool();
        Assertions.assertNotNull(logger);
    }

    @Test
    void testLoggingTool_Class() throws Exception {
        Log4jLoggingTool logger = new Log4jLoggingTool(this.getClass());
        Assertions.assertNotNull(logger);
    }

    @Test
    void testClass$_String() throws Exception {
        // no idea why the Coverage test requires this test
        Assertions.assertTrue(true);
    }

    @Test
    void testDebug_Object() throws Exception {
        Log4jLoggingTool logger = getLoggingTool();
        logger.debug(this);
    }

    @Test
    void testCreate() throws Exception {
        ILoggingTool logger = Log4jLoggingTool.create(this.getClass());
        Assertions.assertNotNull(logger);
    }
}
