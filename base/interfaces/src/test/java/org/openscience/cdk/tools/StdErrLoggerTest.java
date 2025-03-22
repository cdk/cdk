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

}
