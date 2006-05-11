/* $RCSfile$
 * $Author: egonw $
 * $Date: 2006-03-29 10:27:08 +0200 (Wed, 29 Mar 2006) $
 * $Revision: 5855 $
 *
 * Copyright (C) 2001-2003  Jmol Project
 * Copyright (C) 2003-2006  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.test.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Tests wether all Reader and Writer classes can be instantiated.
 *
 * @cdk.module test-io
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 */
public class ChemObjectIOInstantionTests extends CDKTestCase {
    
    private final static String IO_FORMATS_LIST = "io-formats.set";

    private LoggingTool logger;

    private static List formats = null;

    /**
     * Constructs a ChemObjectIOInstantionTests.
     */
    public ChemObjectIOInstantionTests(String name) {
    	super(name);
    	logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(ChemObjectIOInstantionTests.class);
    }
    
    private void loadFormats() {
        if (formats == null) {
            formats = new ArrayList();
            try {
                logger.debug("Starting loading Formats...");
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(IO_FORMATS_LIST)
                ));
                int formatCount = 0;
                while (reader.ready()) {
                    // load them one by one
                    String formatName = reader.readLine();
                    formatCount++;
                    try {
                        IResourceFormat format = (IResourceFormat)this.getClass().getClassLoader().
                            loadClass(formatName).newInstance();
                        if (format instanceof IChemFormat) {
                        	formats.add(format);
                        	logger.info("Loaded IChemFormat: " + format.getClass().getName());
                        }
                    } catch (ClassNotFoundException exception) {
                        logger.error("Could not find this IResourceFormat: ", formatName);
                        logger.debug(exception);
                    } catch (Exception exception) {
                        logger.error("Could not load this IResourceFormat: ", formatName);
                        logger.debug(exception);
                    }
                }
                logger.info("Number of loaded formats used in detection: ", formatCount);
            } catch (Exception exception) {
                logger.error("Could not load this io format list: ", IO_FORMATS_LIST);
                logger.debug(exception);
            }
        }
    }

    public void testInstantion() {
    	loadFormats();
    	
    	IChemFormat format = null;
    	Iterator formatIter = formats.iterator();
    	while (formatIter.hasNext()) {
            format = (IChemFormat)formatIter.next();
            if (format.getReaderClassName() != null) {
            	tryToInstantiate(format.getReaderClassName());
            }
            if (format.getWriterClassName() != null) {
            	tryToInstantiate(format.getWriterClassName());
            }
        }
    }
    
    private void tryToInstantiate(String className) {
    	try {
            // make a new instance of this class
            Object instance = this.getClass().getClassLoader().loadClass(className).newInstance();
            assertNotNull(instance);
            assertEquals(className, instance.getClass().getName());
        } catch (ClassNotFoundException exception) {
            logger.debug("Could not find this class: " + className);
            // but that's not error, it can mean that it is a Jmol based IO class, and no Jmol is in the classpath
        } catch (Exception exception) {
        	logger.debug(exception);
            fail("Could not instantiate this class: " + className);
        }
    }

}
