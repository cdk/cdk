/* Copyright (C) 2025  Egon Willighagen <egon.willighagen@maastrichtuniversity.nl>
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
import java.io.IOException;
import java.io.PrintStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.exception.CDKException;

public class StdErrLoggerTest {

	private static PrintStream originalErr = System.err;

	@BeforeAll
	public static void setUpStreams() {
		originalErr = System.err;
	}

	@AfterAll
	public static void restoreStreams() {
	    System.setErr(originalErr);
	}
	
    @Test
    public void testSetLevel() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	Assertions.assertFalse(logger.isDebugEnabled());
    	logger.setLevel(ILoggingTool.DEBUG);
    	Assertions.assertTrue(logger.isDebugEnabled());
    	Assertions.assertEquals(ILoggingTool.DEBUG, logger.getLevel());
    }

    @Test
    public void testDumpSystemProperties() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.dumpSystemProperties();
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(logger.isDebugEnabled());
    	Assertions.assertTrue(output.contains("os.arch"));
    }

    @Test
    public void testDumpClasspath() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.dumpClasspath();
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(logger.isDebugEnabled());
    	Assertions.assertTrue(output.contains("java.class.path"));
    }

    @Test
    public void testDebug_Objects() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.debug("test", "foo", "bar");
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("DEBUG: testfoobar"));
    }

    @Test
    public void testInfo() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.INFO);
    	logger.info("test");
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("INFO: test"));
    }

    @Test
    public void testInfo_Objects() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.INFO);
    	logger.info("test", "foo", "bar");
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("INFO: testfoobar"));
    }

    @Test
    public void testWarn() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.INFO);
    	logger.warn("test");
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("WARN: test"));
    }

    @Test
    public void testWarn_Objects() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.INFO);
    	logger.warn("test", "foo", "bar");
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("WARN: testfoobar"));
    }

    @Test
    public void testThrowable() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.debug(new CDKException("oh no"));
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("DEBUG: Exception: oh no"));
    }

    @Test
    public void testThrowables() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.debug(new CDKException("oh no", new CDKException("this is bad")));
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("DEBUG: Exception: oh no"));
    	Assertions.assertTrue(output.contains("DEBUG: Caused by"));
    	Assertions.assertTrue(output.contains("DEBUG: Exception: this is bad"));
    }

    @Test
    public void testThrowables_Length() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.setStackLength(5);
    	try {
    		new Integer("no integer");
    	} catch (Exception e) {
    		logger.debug(new CDKException("oh no", e));
		}
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("DEBUG: Exception: oh no"));
    	Assertions.assertTrue(output.contains("DEBUG: Caused by"));
    	Assertions.assertTrue(output.contains("DEBUG: Exception: For input string: \"no integer\""));
    	Assertions.assertTrue(output.contains("java.lang.Integer.parseInt"));
    }

    @Test
    public void testThrowables_Length_Limited() throws IOException {
    	ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    	System.setErr(new PrintStream(errContent));
    	ILoggingTool logger = new StdErrLogger(this.getClass());
    	logger.setLevel(ILoggingTool.DEBUG);
    	logger.setStackLength(1);
    	try {
    		new Integer("no integer");
    	} catch (Exception e) {
    		logger.debug(new CDKException("oh no", e));
		}
    	errContent.flush();
    	String output = new String(errContent.toByteArray());
    	Assertions.assertTrue(output.contains("DEBUG: Exception: oh no"));
    	Assertions.assertTrue(output.contains("DEBUG: Caused by"));
    	Assertions.assertTrue(output.contains("DEBUG: Exception: For input string: \"no integer\""));
    	Assertions.assertFalse(output.contains("java.lang.Integer.parseInt"));
    }

    @Test
    public void testCreate() throws IOException {
    	ILoggingTool logger = StdErrLogger.create(this.getClass());
    	Assertions.assertNotNull(logger);
    }

}
