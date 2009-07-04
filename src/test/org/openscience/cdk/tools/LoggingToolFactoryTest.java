/* Copyright (C) 2009  Egon Willighagen <egonw@users.sf.net>
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
import org.junit.Test;

/**
 * @cdk.module test-core
 */
public class LoggingToolFactoryTest {

    @Test public void testSetGetLoggingToolClass() {
        Class<? extends ILoggingTool> logger = LoggingTool.class;
        LoggingToolFactory.setLoggingToolClass(logger);
        Assert.assertEquals(
            LoggingTool.class.getName(),
            LoggingToolFactory.getLoggingToolClass().getName()
        );
    }
    
    @Test public void testCreateLoggingTool() {
        ILoggingTool logger = LoggingToolFactory.createLoggingTool(
            LoggingToolFactoryTest.class
        );
        Assert.assertNotNull(logger);
    }

    @Test public void testCreateLog4jLoggingTool() {
        Class<? extends ILoggingTool> logger = LoggingTool.class;
        LoggingToolFactory.setLoggingToolClass(logger);
        ILoggingTool instance = LoggingToolFactory.createLoggingTool(
            LoggingToolFactoryTest.class
        );
        Assert.assertTrue(instance instanceof LoggingTool);
    }
}
