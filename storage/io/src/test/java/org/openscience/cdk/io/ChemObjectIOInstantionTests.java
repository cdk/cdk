/* Copyright (C) 2001-2003  Jmol Project
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * Tests whether all Reader and Writer classes can be instantiated.
 *
 * @cdk.module test-io
 *
 * @author Egon Willighagen &lt;egonw@sci.kun.nl&gt;
 */
public class ChemObjectIOInstantionTests extends CDKTestCase {

    private final static String      IO_FORMATS_LIST = "io-formats.set";

    private static ILoggingTool      logger          = LoggingToolFactory
                                                             .createLoggingTool(ChemObjectIOInstantionTests.class);

    private static List<IChemFormat> formats         = null;

    private void loadFormats() {
        if (formats == null) {
            formats = new ArrayList<IChemFormat>();
            try {
                logger.debug("Starting loading Formats...");
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
                        .getResourceAsStream(IO_FORMATS_LIST)));
                int formatCount = 0;
                while (reader.ready()) {
                    // load them one by one
                    String formatName = reader.readLine();
                    formatCount++;
                    try {
                        IResourceFormat format = (IResourceFormat) this.getClass().getClassLoader()
                                .loadClass(formatName).newInstance();
                        if (format instanceof IChemFormat) {
                            formats.add((IChemFormat) format);
                            logger.info("Loaded IChemFormat: " + format.getClass().getName());
                        }
                    } catch (ClassNotFoundException exception) {
                        logger.error("Could not find this IResourceFormat: ", formatName);
                        logger.debug(exception);
                    } catch (InstantiationException | IllegalAccessException exception) {
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

    @Test
    public void testInstantion() {
        loadFormats();

        IChemFormat format = null;
        Iterator<IChemFormat> formatIter = formats.iterator();
        while (formatIter.hasNext()) {
            format = (IChemFormat) formatIter.next();
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
            Assert.assertNotNull(instance);
            Assert.assertEquals(className, instance.getClass().getName());
        } catch (ClassNotFoundException exception) {
            logger.debug("Could not find this class: " + className);
            // but that's not error, it can mean that it is a Jmol based IO class, and no Jmol is in the classpath
        } catch (InstantiationException | IllegalAccessException exception) {
            logger.debug(exception);
            Assert.fail("Could not instantiate this class: " + className);
        }
    }

}
