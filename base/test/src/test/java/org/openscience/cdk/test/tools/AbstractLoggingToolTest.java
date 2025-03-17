/*
 * =====================================
 *  Copyright (c) 2022 NextMove Software
 * =====================================
 */
package org.openscience.cdk.test.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.ILoggingTool;

/**
 */
public abstract class AbstractLoggingToolTest extends CDKTestCase {

    public abstract ILoggingTool getLoggingTool();

    @Test
    public void testLoggingTool_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        Assertions.assertNotNull(logger);
    }

    @Test
    public void testDebug_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, this);
    }

    @Test
    public void testDebug_Object_int() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, 1);
    }

    @Test
    public void testDebug_Object_double() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, 1.0);
    }

    @Test
    public void testDebug_Object_boolean() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, true);
    }

    @Test
    public void testDebug_Object_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, this, this, this, this);
    }

    @Test
    public void testDebug_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, this, this, this);
    }

    @Test
    public void testDebug_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.debug(this, this, this);
    }

    @Test
    public void testDebug_ExceptionWithNullMessage() throws Exception {
        ILoggingTool logger = getLoggingTool();
        Exception exc = new Exception((String) null);
        logger.debug(exc);
    }

    @Test
    public void testError_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this);
    }

    @Test
    public void testError_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, this);
    }

    @Test
    public void testError_Object_int() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, 1);
    }

    @Test
    public void testError_Object_double() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, 1.0);
    }

    @Test
    public void testError_Object_boolean() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, true);
    }

    @Test
    public void testError_Object_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, this, this, this, this);
    }

    @Test
    public void testError_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, this, this, this);
    }

    @Test
    public void testError_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.error(this, this, this);
    }

    @Test
    public void testWarn_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this);
    }

    @Test
    public void testWarn_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, this);
    }

    @Test
    public void testWarn_Object_int() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, 1);
    }

    @Test
    public void testWarn_Object_double() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, 1.0);
    }

    @Test
    public void testWarn_Object_boolean() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, true);
    }

    @Test
    public void testWarn_Object_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, this, this, this, this);
    }

    @Test
    public void testWarn_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, this, this, this);
    }

    @Test
    public void testWarn_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.warn(this, this, this);
    }

    @Test
    public void testInfo_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this);
    }

    @Test
    public void testInfo_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, this);
    }

    @Test
    public void testInfo_Object_int() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, 1);
    }

    @Test
    public void testInfo_Object_double() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, 1.0);
    }

    @Test
    public void testInfo_Object_boolean() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, true);
    }

    @Test
    public void testInfo_Object_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, this, this, this, this);
    }

    @Test
    public void testInfo_Object_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, this, this, this);
    }

    @Test
    public void testInfo_Object_Object_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.info(this, this, this);
    }

    @Test
    public void testFatal_Object() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.fatal(this);
    }

    @Test
    public void testSetStackLength_int() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.setStackLength(20);
    }

    @Test
    public void testDumpClasspath() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.dumpClasspath();
    }

    @Test
    public void testDumpSystemProperties() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.dumpSystemProperties();
    }

    @Test
    public void testIsDebugEnabled() throws Exception {
        ILoggingTool logger = getLoggingTool();
        logger.isDebugEnabled();
    }

}
