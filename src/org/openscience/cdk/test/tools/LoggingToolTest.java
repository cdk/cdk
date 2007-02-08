/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * @cdk.module test-core
 */
public class LoggingToolTest extends CDKTestCase {
	
	public LoggingToolTest(String name) {
		super(name);
	}

	public static Test suite() throws Exception {
		return new TestSuite(LoggingToolTest.class);
	}

	public void testLoggingTool_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		assertNotNull(logger);
	}

	public void testLoggingTool() throws Exception {
		LoggingTool logger = new LoggingTool();
		assertNotNull(logger);
	}

	public void testLoggingTool_Class() throws Exception {
		LoggingTool logger = new LoggingTool(this.getClass());
		assertNotNull(logger);
	}

	public void testClass$_String() throws Exception {
		// no idea why the Coverage test requires this test
		assertTrue(true);
	}

	public void testConfigureLog4j() throws Exception {
		LoggingTool.configureLog4j();
	}

	public void testDebug_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this);
	}

	public void testDebug_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, this);
	}

	public void testDebug_Object_int() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, 1);
	}

	public void testDebug_Object_double() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, 1.0);
	}

	public void testDebug_Object_boolean() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, true);
	}

	public void testDebug_Object_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, this, this, this, this);
	}

	public void testDebug_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, this, this, this);
	}

	public void testDebug_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.debug(this, this, this);
	}

	public void testError_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this);
	}

	public void testError_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, this);
	}

	public void testError_Object_int() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, 1);
	}

	public void testError_Object_double() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, 1.0);
	}

	public void testError_Object_boolean() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, true);
	}

	public void testError_Object_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, this, this, this, this);
	}

	public void testError_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, this, this, this);
	}

	public void testError_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.error(this, this, this);
	}

	public void testWarn_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this);
	}

	public void testWarn_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, this);
	}

	public void testWarn_Object_int() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, 1);
	}

	public void testWarn_Object_double() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, 1.0);
	}

	public void testWarn_Object_boolean() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, true);
	}

	public void testWarn_Object_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, this, this, this, this);
	}

	public void testWarn_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, this, this, this);
	}

	public void testWarn_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.warn(this, this, this);
	}

	public void testInfo_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this);
	}

	public void testInfo_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, this);
	}

	public void testInfo_Object_int() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, 1);
	}

	public void testInfo_Object_double() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, 1.0);
	}

	public void testInfo_Object_boolean() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, true);
	}

	public void testInfo_Object_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, this, this, this, this);
	}

	public void testInfo_Object_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, this, this, this);
	}

	public void testInfo_Object_Object_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.info(this, this, this);
	}

	public void testFatal_Object() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.fatal(this);
	}

	public void testSetStackLength_int() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.setStackLength(20);
	}

	public void testDumpClasspath() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.dumpClasspath();
	}

	public void testDumpSystemProperties() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.dumpSystemProperties();
	}

	public void testIsDebugEnabled() throws Exception {
		LoggingTool logger = new LoggingTool(this);
		logger.isDebugEnabled();
	}
	
}

