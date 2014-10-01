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

    @Test
    public void testSetGetLoggingToolClass() {
        Class<? extends ILoggingTool> logger = LoggingTool.class;
        LoggingToolFactory.setLoggingToolClass(logger);
        Assert.assertEquals(LoggingTool.class.getName(), LoggingToolFactory.getLoggingToolClass().getName());
    }

    @Test
    public void testCreateLoggingTool() {
        ILoggingTool logger = LoggingToolFactory.createLoggingTool(LoggingToolFactoryTest.class);
        Assert.assertNotNull(logger);
    }

    @Test
    public void testCreateLog4jLoggingTool() {
        Class<? extends ILoggingTool> logger = LoggingTool.class;
        LoggingToolFactory.setLoggingToolClass(logger);
        ILoggingTool instance = LoggingToolFactory.createLoggingTool(LoggingToolFactoryTest.class);
        Assert.assertTrue(instance instanceof LoggingTool);
    }

    @Test
    public void testCustomLogger() {
        LoggingToolFactory.setLoggingToolClass(CustomLogger.class);
        ILoggingTool instance = LoggingToolFactory.createLoggingTool(LoggingToolFactoryTest.class);
        Assert.assertTrue(instance instanceof CustomLogger);
    }

    /**
     * Custom dummy logger used in the
     * {@link LoggingToolFactoryTest#testCustomLogger()} test to see if
     * the custom {@link ILoggingTool} is really being used. It does
     * not really implement any method, as the test uses a mere
     * <code>instanceof</code> call.
     */
    private static class CustomLogger implements ILoggingTool {

        private CustomLogger(Class<?> sourceClass) {}

        public static ILoggingTool create(Class<?> sourceClass) {
            return new CustomLogger(sourceClass);
        }

        @Override
        public void debug(Object object) {}

        @Override
        public void debug(Object object, Object... objects) {}

        @Override
        public void dumpClasspath() {}

        @Override
        public void dumpSystemProperties() {}

        @Override
        public void error(Object object) {}

        @Override
        public void error(Object object, Object... objects) {}

        @Override
        public void fatal(Object object) {}

        @Override
        public void info(Object object) {}

        @Override
        public void info(Object object, Object... objects) {}

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public void setStackLength(int length) {}

        @Override
        public void warn(Object object) {}

        @Override
        public void warn(Object object, Object... objects) {}
    }
}
