/* Copyright (C) 2009,2025  Egon Willighagen <egonw@users.sf.net>
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

/**
 */
class LoggingToolFactoryTest {

    @Test
    void testSetGetLoggingToolClass() {
        Class<? extends ILoggingTool> logger = StdErrLogger.class;
        LoggingToolFactory.setLoggingToolClass(logger);
        Assertions.assertEquals(StdErrLogger.class.getName(), LoggingToolFactory.getLoggingToolClass().getName());
    }

    @Test
    void testCreateLoggingTool() {
        ILoggingTool logger = LoggingToolFactory.createLoggingTool(LoggingToolFactoryTest.class);
        Assertions.assertNotNull(logger);
    }

    @Test
    void testCreateLog4jLoggingTool() {
        Class<? extends ILoggingTool> logger = StdErrLogger.class;
        LoggingToolFactory.setLoggingToolClass(logger);
        ILoggingTool instance = LoggingToolFactory.createLoggingTool(LoggingToolFactoryTest.class);
        Assertions.assertTrue(instance instanceof StdErrLogger);
    }

    @Test
    void testCustomLogger() {
        LoggingToolFactory.setLoggingToolClass(CustomLogger.class);
        ILoggingTool instance = LoggingToolFactory.createLoggingTool(LoggingToolFactoryTest.class);
        Assertions.assertTrue(instance instanceof CustomLogger);
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
        public void debug(Object object) { // no implemented because not used in the testing
        }

        @Override
        public void debug(Object object, Object... objects) { // no implemented because not used in the testing
        }

        @Override
        public void dumpClasspath() { // no implemented because not used in the testing
        }

        @Override
        public void dumpSystemProperties() { // no implemented because not used in the testing
        }

        @Override
        public void error(Object object) { // no implemented because not used in the testing
        }

        @Override
        public void error(Object object, Object... objects) { // no implemented because not used in the testing
        }

        @Override
        public void fatal(Object object) { // no implemented because not used in the testing
        }

        @Override
        public void info(Object object) { // no implemented because not used in the testing
        }

        @Override
        public void info(Object object, Object... objects) { // no implemented because not used in the testing
        }

        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        @Override
        public void setStackLength(int length) { // no implemented because not used in the testing
        }

        @Override
        public void warn(Object object) { // no implemented because not used in the testing
        }

        @Override
        public void warn(Object object, Object... objects) { // no implemented because not used in the testing
        }

        @Override
        public void setLevel(int level) {
        	 // no implemented because not used in the testing
        }

        @Override
        public int getLevel() {
            return 0;
        }
    }
}
