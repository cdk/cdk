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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * @cdk.module test-core
 */
public class SystemOutLoggingToolTest extends AbstractLoggingToolTest {

    @Test
    public void testLoggingTool_Class() throws Exception {
        ILoggingTool logger = new SystemOutLoggingTool(this.getClass());
        Assert.assertNotNull(logger);
    }

    @Test
    public void testDebug_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this, this);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_int() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this, 1);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_double() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this, 1.0);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1.0"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_boolean() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this, true);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("true"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this, this, this, this);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDebug_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.debug(this, this, this);
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, this);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_int() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, 1);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_double() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, 1.0);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1.0"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_boolean() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, true);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("true"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, this, this, this, this);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, this, this, this);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testError_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.error(this, this, this);
        Assert.assertTrue(out.toString().contains("ERROR"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, this);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_int() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, 1);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_double() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, 1.0);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1.0"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_boolean() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, true);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("true"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, this, this, this, this);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, this, this, this);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testWarn_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.warn(this, this, this);
        Assert.assertTrue(out.toString().contains("WARN"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, this);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_int() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, 1);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_double() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, 1.0);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("1.0"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_boolean() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, true);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("true"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, this, this, this, this);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, this, this, this);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testInfo_Object_Object_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.info(this, this, this);
        Assert.assertTrue(out.toString().contains("INFO"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testFatal_Object() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.fatal(this);
        Assert.assertTrue(out.toString().contains("FATAL"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testSetStackLength_int() throws Exception {
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setStackLength(20);
    }

    @Test
    @Override
    public void testDumpClasspath() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.dumpClasspath();
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("java.class.path"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testDumpSystemProperties() throws Exception {
        // set up things such that we can test the actual output
        PrintStream stdout = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        // do the testing
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        logger.setDebugEnabled(true);
        logger.dumpSystemProperties();
        Assert.assertTrue(out.toString().contains("DEBUG"));
        Assert.assertTrue(out.toString().contains(this.getClass().getName()));
        Assert.assertTrue(out.toString().contains("os.name"));
        Assert.assertTrue(out.toString().contains("os.version"));
        Assert.assertTrue(out.toString().contains("os.arch"));
        Assert.assertTrue(out.toString().contains("java.version"));
        Assert.assertTrue(out.toString().contains("java.vendor"));

        // reset the STDOUT
        System.setOut(stdout);
    }

    @Test
    @Override
    public void testIsDebugEnabled() throws Exception {
        SystemOutLoggingTool logger = new SystemOutLoggingTool(this.getClass());
        // the default must be not the debug
        Assert.assertFalse(logger.isDebugEnabled());
        // but we can overwrite it here...
        logger.setDebugEnabled(true);
        Assert.assertTrue(logger.isDebugEnabled());
        logger.setDebugEnabled(false);
        Assert.assertFalse(logger.isDebugEnabled());
    }

    @Test
    public void testCreate() throws Exception {
        ILoggingTool logger = SystemOutLoggingTool.create(this.getClass());
        Assert.assertNotNull(logger);
    }

    @Override
    public ILoggingTool getLoggingTool() {
        return new SystemOutLoggingTool(this.getClass());
    }
}
