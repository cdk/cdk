/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005-2006  The Chemistry Development Kit (CDK) project
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

	public static Test suite() {
		return new TestSuite(LoggingToolTest.class);
	}

	public void testLoggingTool_Object() {
        LoggingTool logger = new LoggingTool(this);
        assertNotNull(logger);
    }
	
	public void testLoggingTool() {
        LoggingTool logger = new LoggingTool();
        assertNotNull(logger);
    }
	
	public void testLoggingTool_Class() {
        LoggingTool logger = new LoggingTool(this.getClass());
        assertNotNull(logger);
    }
	
	public void testClass$_String() {
        // no idea why the Coverage test requires this test
    }

    public void testConfigureLog4j() {
        try {
            LoggingTool.configureLog4j();
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
	
	public void testDebug_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
	
	public void testDebug_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, this);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testDebug_Object_int() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, 1);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testDebug_Object_double() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, 1.0);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testDebug_Object_boolean() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, true);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testDebug_Object_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testDebug_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testDebug_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.debug(this, this, this);
        } catch (Exception exception) {
            fail("Exception during debug: " + exception.getMessage());
        }
    }
    
	public void testError_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
	
	public void testError_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, this);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
    
	public void testError_Object_int() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, 1);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
    
	public void testError_Object_double() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, 1.0);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
    
	public void testError_Object_boolean() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, true);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
    
	public void testError_Object_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
    
	public void testError_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }
    
	public void testError_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.error(this, this, this);
        } catch (Exception exception) {
            fail("Exception during error: " + exception.getMessage());
        }
    }

	public void testWarn_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
	
	public void testWarn_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, this);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
    
	public void testWarn_Object_int() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, 1);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
    
	public void testWarn_Object_double() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, 1.0);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
    
	public void testWarn_Object_boolean() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, true);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
    
	public void testWarn_Object_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
    
	public void testWarn_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }
    
	public void testWarn_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.warn(this, this, this);
        } catch (Exception exception) {
            fail("Exception during warn: " + exception.getMessage());
        }
    }

	public void testInfo_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
	
	public void testInfo_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, this);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
    
	public void testInfo_Object_int() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, 1);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
    
	public void testInfo_Object_double() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, 1.0);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
    
	public void testInfo_Object_boolean() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, true);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
    
	public void testInfo_Object_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
    
	public void testInfo_Object_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, this, this, this);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }
    
	public void testInfo_Object_Object_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.info(this, this, this);
        } catch (Exception exception) {
            fail("Exception during info: " + exception.getMessage());
        }
    }

	public void testFatal_Object() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.fatal(this);
        } catch (Exception exception) {
            fail("Exception during fatal: " + exception.getMessage());
        }
    }
	
	public void testSetStackLength_int() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.setStackLength(20);
        } catch (Exception exception) {
            fail("Exception during test: " + exception.getMessage());
        }
    }
	
	public void testDumpClasspath() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.dumpClasspath();
        } catch (Exception exception) {
            fail("Exception during test: " + exception.getMessage());
        }
    }
	
	public void testDumpSystemProperties() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.dumpSystemProperties();
        } catch (Exception exception) {
            fail("Exception during test: " + exception.getMessage());
        }
    }
	
	public void testIsDebugEnabled() {
        LoggingTool logger = new LoggingTool(this);
        try {
            logger.isDebugEnabled();
        } catch (Exception exception) {
            fail("Exception during test: " + exception.getMessage());
        }
    }
	
}

